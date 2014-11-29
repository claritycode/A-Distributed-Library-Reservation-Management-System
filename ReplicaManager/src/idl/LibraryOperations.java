package idl;


/**
* idl/LibraryOperations.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from idl/ReplicaManager.idl
* Wednesday, November 26, 2014 10:37:55 PM EST
*/

public interface LibraryOperations 
{
  String createAccount (String firstName, String lastName, String emailAddress, String phoneNumber, String username, String password, String educationalInstitution);
  String reserveBook (String username, String password, String bookName, String authorName);
  String reserveInterLibrary (String username, String password, String bookName, String authorName);
  String getNonRetuners (String adminUsername, String adminPassword, String educationalInstitution, int numDays);
  String setDuration (String username, String bookName, int num_of_days);
} // interface LibraryOperations