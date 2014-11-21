package service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;

import entities.Book;
import entities.Reservation;
import entities.Student;

public class LibraryServiceImpl implements LibraryService {

	/**
	 * The accounts are placed in several lists that are stored in a hash table according to the first character of the username
	 * indicated in the account. For example, all the accounts with the username starting with an “A” will belong to the same list
	 * and will be stored in a hash table (acting as the database) using the key “A”.
	 */
	private final Hashtable<Character, LinkedHashMap<String, Student>> accounts;

	/**
	 * The server for each library must maintain a collection of books.
	 */
	private final Hashtable<String, Book> books;
	
	private final String libraryName;

	public LibraryServiceImpl(final String studentsCsv, final String booksCsv, final String libraryName) {
		this.accounts = new Hashtable<Character, LinkedHashMap<String, Student>>();
		this.books = new Hashtable<String, Book>();
		this.libraryName = libraryName;
		initAccounts(studentsCsv);
		initBooks(booksCsv);
	}

	private void initAccounts(final String studentsCsv) {
		synchronized (accounts) {
			for (char i = 'a'; i < 'z'; i++) {
				accounts.put(i, new LinkedHashMap<String, Student>());
			}
			for (char i = 'A'; i < 'Z'; i++) {
				accounts.put(i, new LinkedHashMap<String, Student>());
			}
			// read csv with columns: username,password,institution,firstName,lastName,email,phone
			List<String[]> lines = loadCsv(studentsCsv, 7);
			for (String[] tokens : lines) {
				String username = tokens[0];
				Student student = new Student(username, tokens[1], tokens[2], tokens[3], tokens[4], tokens[5], tokens[6]);
				char firstLetter = username.charAt(0);
				LinkedHashMap<String, Student> studentsList = accounts.get(firstLetter);
				if (studentsList != null) {
					studentsList.put(username, student);
				}
			}
		}
	}
	
	private void initBooks(final String booksCsv) {
		synchronized (books) {
			// read csv with columns: name, author, copies
			List<String[]> lines = loadCsv(booksCsv, 3);
			for (String[] tokens : lines) {
				String name = tokens[0];
				String author = tokens[1];
				int copies = Integer.parseInt(tokens[2]);
				books.put(name, new Book(name, author, copies));
			}
		}
	}
	
	private List<String[]> loadCsv(final String path, int fields) {
		List<String[]> lines = new ArrayList<String[]>();
		final String regex = ",";
		BufferedReader br = null;
		String line = "";
		try {
			InputStream is = this.getClass().getClassLoader().getResourceAsStream(path);
			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				String[] tokens = line.split(regex);
				if (tokens.length == fields) {
					lines.add(tokens);
				} else {
					throw new IllegalArgumentException("Each line in '"+path+"' should have '"+fields+"' tokens.");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return lines;
	}

	private LinkedHashMap<String, Student> getStudentList(final String username) {
		char firstLetter = username.charAt(0);
		LinkedHashMap<String, Student> studentsList = null;

		if (accounts.containsKey(new Character(firstLetter))) {
			// f.y.i.: no need to lock the "accounts" object here because the "studentsList" for each firstLetter will be created
			// only once, in the initAccounts() method, so the reference will never change.
			studentsList = this.accounts.get(firstLetter);
		}
		return studentsList;
	}

	@Override
	public String createAccount(final Student student) {
		// f.y.i.: no need to lock the "student" parameter because it is created per request, so each "student" can only belong to
		// one thread.

		String message = null;
		final String username = student.getUsername();
		final LinkedHashMap<String, Student> studentsList = getStudentList(username);
		if (studentsList != null) {
			// locking list to avoid internal change during search
			synchronized (studentsList) {
				if (studentsList.get(username) != null) {
					message = String.format(USER_ALREADY_EXISTS, username);
				} else {
					studentsList.put(username, student);
					message = String.format(ACCOUNT_CREATED, student);
				}
			}
		} else {
			message = String.format(INVALID_USERNAME, username);
		}
		return message;
	}

	@Override
	public String reserveBook(final Student student, final Book book) {
		// f.y.i.: no need to lock the "student" parameter because it is created per request, so each "student" can only belong to
		// one thread. Same for the book parameter.

		String message = null;
		final String username = student.getUsername();

		// use only book name as key instead of hash code (with name and author) because author is not passed on setDuration()
		final Book retrievedBook = books.get(book.getName());
		if (retrievedBook != null) {
			// lock saved book during search-change
			synchronized (retrievedBook) {
				int copies = retrievedBook.getCopies();
				if (copies > 0) {
					final LinkedHashMap<String, Student> studentsList = getStudentList(username);
					if (studentsList != null) {
						message = addReservationToStudent(username, retrievedBook, this.libraryName, studentsList);
					} else {
						message = String.format(INVALID_USERNAME, username);
					}
				} else {
					message = String.format(NO_COPIES, book);
				}
			}
		} else {
			message = String.format(INEXISTENT_BOOK, book);
		}
		return message;
	}

	/**
	 * Add reservation to student.<br/>
	 * Before calling this method lock 'retrievedBook', so no other thread can access it.
	 * @param username
	 * @param retrievedBook
	 * @param libraryName
	 * @param studentsList
	 * @return
	 */
	private String addReservationToStudent(final String username, final Book retrievedBook, final String libraryName, 
			final LinkedHashMap<String, Student> studentsList) {
		String message;
		// locking list to avoid internal change during search
		synchronized (studentsList) {
			Student retrievedStudent = studentsList.get(username);
			if (retrievedStudent != null) {
				Hashtable<String, Reservation> reservations = retrievedStudent.getReservations();
				final Reservation reservation = new Reservation(username, retrievedBook, libraryName);
				// check if student already has the same reservation
				if (reservations.get(retrievedBook.getName()) != null) {
					message = String.format(DOUBLE_REGISTRATION, reservation);
				} else {
					reservations.put(retrievedBook.getName(), reservation);
					int copies = retrievedBook.getCopies();
					retrievedBook.setCopies(copies - 1);
					message = String.format(BOOK_RESERVED, reservation);
				}
			} else {
				message = String.format(INEXISTENT_STUDENT, username);
			}
		}
		return message;
	}
	
	public String isBookAvailable(final Book book) {
		String message = null;
		
		// use only book name as key instead of hash code (with name and author) because author is not passed on setDuration()
		final Book retrievedBook = books.get(book.getName());
		if (retrievedBook != null) {
			// lock saved book during search-change
			synchronized (retrievedBook) {
				int copies = retrievedBook.getCopies();
				if (copies > 0) {
					message = String.format(BOOK_AVAILABLE, book);
				} else {
					message = String.format(NO_COPIES, book);
				}
			}
		} else {
			message = String.format(INEXISTENT_BOOK, book);
		}
		return message;
	}
	
	@Override
	public String reserveBookExternal(final String username, final Book book) {
		String message = null;

		// use only book name as key instead of hash code (with name and author) because author is not passed on setDuration()
		final Book retrievedBook = books.get(book.getName());
		if (retrievedBook != null) {
			// lock saved book during search-change
			synchronized (retrievedBook) {
				int copies = retrievedBook.getCopies();
				if (copies > 0) {
					retrievedBook.setCopies(copies - 1);
					final Reservation reservation = new Reservation(username, retrievedBook, this.libraryName);
					message = String.format(BOOK_RESERVED, reservation);
				} else {
					message = String.format(NO_COPIES, book);
				}
			}
		} else {
			message = String.format(INEXISTENT_BOOK, book);
		}
		return message;
	}
	
	@Override
	public String addExternalReservationToLocalUser(final String username, final Book book, final String libraryName) {
		String message = null;
		// confirm user exists
		final LinkedHashMap<String, Student> studentsList = getStudentList(username);
		if (studentsList != null) {
			message = addReservationToStudent(username, book, libraryName, studentsList);
		} else {
			message = String.format(INVALID_USERNAME, username);
		}
		return message;
	}
	
	@Override
	public String removeFailedExternalReservation(final String username, final Book book) {
		String message = null;
		
		// confirm user exists
		final LinkedHashMap<String, Student> studentsList = getStudentList(username);
		if (studentsList != null) {
			// locking list to avoid internal change during search
			synchronized (studentsList) {
				Student retrievedStudent = studentsList.get(username);
				if (retrievedStudent != null) {
					Hashtable<String, Reservation> reservations = retrievedStudent.getReservations();
					reservations.remove(book.getName());
				} else {
					message = String.format(INEXISTENT_STUDENT, username);
				}
			}
		} else {
			message = String.format(INVALID_USERNAME, username);
		}
		return message;
	}

	@Override
	public String getNonRetuners(final int numDays) {
		StringBuffer sb = new StringBuffer();
		for (LinkedHashMap<String, Student> studentsList : accounts.values()) {
			// locking list to avoid internal change during search
			synchronized (studentsList) {
				for (final Student student : studentsList.values()) {
					synchronized (student) {
						if (isNonReturner(student, numDays)) {
							sb.append(student.getFirstName() + " " + student.getLastName() + " " + student.getPhone() + "\n");
						}
					}
				}
			}
		}
		return sb.toString();
	}
	
	/**
	 * Student will be a non returner if any of its reservations has a duration to return smaller than numDays.
	 * e.g.: student has one book with -3 days (3 days past due date) and numDays is -5, he will not be a non-returner. If the 
	 * numDays is -2 instead, he will be a non-returner.
	 * @param student
	 * @param numDays
	 * @return
	 */
	private boolean isNonReturner(final Student student, final int numDays) {
		boolean isNonReturner = false;
		
		final Hashtable<String, Reservation> reservations = student.getReservations();
		synchronized (reservations) {
			for (Reservation reservation : reservations.values()) {
				synchronized (reservation) {
					if (reservation.getDuration() < numDays) {
						isNonReturner = true;
						break;
					}
				}
			}
		}
		return isNonReturner;
	}

	@Override
	public String setDuration(final String username, final String bookName, final int numDays) {
		String message = null;

		final LinkedHashMap<String, Student> studentsList = getStudentList(username);
		if (studentsList != null) {
			// locking list to avoid internal change during search
			synchronized (studentsList) {
				Student retrievedStudent = studentsList.get(username);
				if (retrievedStudent != null) {
					Reservation retrievedReservation = null;
					Hashtable<String, Reservation> reservations = retrievedStudent.getReservations();
					synchronized (reservations) {
						retrievedReservation = reservations.get(bookName);
					}
					if (retrievedReservation != null) {
						synchronized (retrievedReservation) {
							retrievedReservation.setDuration(numDays);
							message = String.format(UPDATED_RESERVATION, username, bookName, numDays);
						}
					} else {
						message = String.format(INEXISTENT_RESERVATION, username, bookName);
					}
				} else {
					message = String.format(INEXISTENT_STUDENT, username);
				}
			}
		} else {
			message = String.format(INVALID_USERNAME, username);
		}
		return message;
	}

}
