package util;

import java.util.ArrayList;
import java.util.List;

import DRMSServices.lateStudent;
import DRMSServices.nonReturners;

public class NonReturnersParser {

	public final static String SEPARATOR = ":";

	/**
	 * Writes each non-returner in the format defined in the specs to be printed, i.e.:
	 * <p><i>
	 * Educational Institution 1 : FirstName1 LastName1 514xxxxxxx<br/>
	 * FirstName2 LastName2 514xxxxxxx<br/>
	 * FirstName3 LastName3 514xxxxxxx<br/>
	 * ........
	 * </i></p>
	 * So that it can be used for printing the response.
	 * @param nr
	 * @return The parsed string.
	 */
	public static String nonReturnersToString(nonReturners nr) {
		String result = null;
		if (nr.universityName != null) {
			result = nr.universityName + ": ";
			if (nr.studentList != null) {
				for (lateStudent ls : nr.studentList) {
					result += ls.firstName + " " + ls.lastName + " " + ls.phoneNumber + "\n";
				}
			} else {
				result += "\n";
			}
			result += "......";
		}
		return result;
	}

	/**
	 * Parses nonReturners object into a single String, e.g.:</br>
	 * For a university "univ" with 2 non-returners, you would have:</br>
	 * univ:firstName1:lastName1:phone1:firstName2:lastName2:phone2
	 * @param nr
	 * @return The parsed string.
	 */
	public static String nonReturnersToSingleLine(nonReturners nr) {
		String result = null;
		if (nr.universityName != null) {
			result = nr.universityName;
			if (nr.studentList != null) {
				for (lateStudent ls : nr.studentList) {
					result += SEPARATOR + ls.firstName + SEPARATOR + ls.lastName + SEPARATOR + ls.phoneNumber;
				}
			}
		}
		return result;
	}

	/**
	 * Gets a single string formatted by {@link #nonReturnersToSingleLine(nonReturners)} and parse it to a nonReturners object.
	 * @param line
	 * @return The nonReturners object.
	 */
	public static nonReturners singleLineToNonReturners(String line) {
		nonReturners nr = null;
		if (line != null && line.length() > 0) {
			nr = new nonReturners();
			String[] tokens = line.split(SEPARATOR);
			nr.universityName = tokens[0];
			// if it has students, it should have (1 + 3x(number of students)) tokens, as each student has 3 tokens 
			if ((tokens.length > 1) && (tokens.length % 3 == 1)) {
				List<lateStudent> lsList = new ArrayList<lateStudent>();
				for (int i = 1; i < tokens.length; i+=3) {
					lsList.add(new lateStudent(tokens[i], tokens[i+1], tokens[i+2]));
				}
				
				nr.studentList = lsList.toArray(new lateStudent[lsList.size()]);
			}
		}
		return nr;
	}

	public static void main(String[] args) {
		lateStudent ls1 = new lateStudent("1", "a", "x");
		lateStudent ls2 = new lateStudent("2", "b", "y");
		lateStudent ls3 = new lateStudent("3", "c", "z");
		lateStudent[] lsArray = new lateStudent[] {ls1, ls2, ls3};
		nonReturners nr = new nonReturners("univ", lsArray);
		
		System.out.println(nonReturnersToString(nr));
		String line = nonReturnersToSingleLine(nr);
		System.out.println(line);
		
		nonReturners nr2 = singleLineToNonReturners(line);
		System.out.println("universityName = " + nr2.universityName);
		lateStudent[] lsArray2 = nr2.studentList;
		System.out.println("student 1 = " + lsArray2[0].firstName);
		System.out.println("student 2 = " + lsArray2[1].firstName);
		System.out.println("student 3 = " + lsArray2[2].firstName);

	}

}
