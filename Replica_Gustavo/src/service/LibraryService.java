package service;

import entities.Book;
import entities.Student;

public interface LibraryService {
	
	String INVALID_USERNAME = "Unable to create account: invalid username [%s]. First letter of username should be in ranges "
			+ "[a-z] or [A-Z].";
	String USER_ALREADY_EXISTS = "Unable to create account: username = [%s] already exists in this server.";
	String ACCOUNT_CREATED = "Account created: %s";
	String BOOK_RESERVED_INIT = "Book successfully reserved: ";
	String BOOK_RESERVED = BOOK_RESERVED_INIT + "%s";
	String DOUBLE_REGISTRATION = "Cannot reserve a book twice for the same user: %s";
	String INEXISTENT_BOOK_INIT = "Unable to find book: ";
	String INEXISTENT_BOOK = INEXISTENT_BOOK_INIT + "%s";
	String NO_COPIES_INIT = "There are no copies of this book left: ";
	String NO_COPIES = NO_COPIES_INIT + "%s";
	String BOOK_AVAILABLE = "Book is available: [%s]";
	String INEXISTENT_STUDENT = "Unable to find student with username = [%s].";
	String INEXISTENT_RESERVATION = "Unable to find reservation for user [%s] on book [%s]";
	String UPDATED_RESERVATION = "Duration of reservation for user [%s] on book [%s] updated for [%d] days.";

	String createAccount(final Student student);
	String reserveBook (final Student student, final Book book);
	String reserveBookExternal(final String username, final Book book);
	String addExternalReservationToLocalUser(final String username, final Book book, final String libraryName);
	String removeFailedExternalReservation(final String username, final Book book);
	String getNonRetuners (final int numDays);
	String setDuration (final String username, final String bookName, final int numDays);
}
