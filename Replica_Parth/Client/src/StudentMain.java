
public class StudentMain {

	public static void main ( String[] args ) {
		StudentClient a = new StudentClient ( args ) ;
//		Thread accountCreation[] = new Thread[9] ;
//		for ( int i = 0 ; i < accountCreation.length ; i++ ) {
//			accountCreation[i] = new Thread(a) ;
//			accountCreation[i].start();
//		}
//		for ( int i = 0 ; i < accountCreation.length ; i++ ) {
//			try {
//				accountCreation[i].join();
//			} catch ( InterruptedException e ) {
//				continue ;
//			}
//		}
		a.getMenu();		
	}
	
}
