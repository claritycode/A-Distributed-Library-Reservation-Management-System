package Webserver;


/**
 * Interface definition: DRMS.
 * 
 * @author OpenORB Compiler
 */
public interface DRMSOperations
{
    /**
     * Operation createAccount
     */
    public int createAccount(String first_name, String last_name, String email, String username, String password, String institute, int phone_no);

    /**
     * Operation studentLogin
     */
    public boolean studentLogin(String username, String password);

    /**
     * Operation reserveBook
     */
    public boolean reserveBook(String usrename, String book_name);

    /**
     * Operation addBook
     */
    public boolean addBook(String bookName, String authorName);

    /**
     * Operation getNonReturners
     */
    public String getNonReturners(int duration);

    /**
     * Operation setDuration
     */
    public boolean setDuration(String student, int noOfDays);

    /**
     * Operation reserveInterLibrary
     */
    public boolean reserveInterLibrary(String username, String password, String bookName, String authorName);

}
