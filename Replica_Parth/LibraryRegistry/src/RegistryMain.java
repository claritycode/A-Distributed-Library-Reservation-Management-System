
public class RegistryMain {

	public static void main ( String[] args ) {
		Registry reg = new Registry ( "first.ser", 8000 ) ;
		Thread t = new Thread (reg) ;
		t.start();
		System.out.println ( "Registry Started" ) ;
	}
}
