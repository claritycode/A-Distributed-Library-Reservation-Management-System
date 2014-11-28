package DRMSIDL;

import java.util.Comparator;


public class StudentArrayListSorter implements Comparator
{

	@Override
	public int compare(Object o1, Object o2) 
	{
		StudentCredentials s1=(StudentCredentials)o1;
		StudentCredentials s2=(StudentCredentials)o2;
		return(s1.username.compareTo(s2.username));
		
	}


}
