package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;


public class StartOrbd {
	
	public static void printReader(BufferedReader reader, String readerName) throws IOException {
		System.out.println(readerName + ":");
		if (reader.ready()) {
			String line = reader.readLine();
			if (line != null) {
				while (line != null) {
					System.out.println(line);
					line = reader.readLine();
				}
			}
		} else {
			System.out.println("nothing in " + readerName);
		}
	}

	public static void main(String[] args) {
		Process orbd = null;
		try {
			StringBuffer sb = new StringBuffer("orbd ");
			if (args != null && args.length > 0) {
				for (String arg : args) {
					sb.append(arg + " ");
				}
				System.out.println("calling$ " + sb.toString());

				Runtime rt = Runtime.getRuntime();
				orbd = rt.exec(sb.toString());
				//orbd = rt.exec("ls");
				System.out.println("orbd triggered"); 
				
				BufferedReader stdInput = new BufferedReader(new InputStreamReader(orbd.getInputStream()));
				BufferedReader stdError = new BufferedReader(new InputStreamReader(orbd.getErrorStream()));
				
				//PrintWriter pw = new PrintWriter(System.out, true);
				
				System.out.println("Press any key to stop");
				Scanner keyboard = new Scanner(System.in);
				
				if (keyboard.next() != null) {
					printReader(stdInput, "standard output");
					printReader(stdError, "error output");
					System.out.println("exiting");
					orbd.destroy();
				}
				keyboard.close();
			} else {
				System.out.println("You should pass the orbd parameters as argments. e.g.: -ORBInitialPort 1050");
			}
		} catch (Exception e) {
			System.err.println("ERROR: " + e);
			e.printStackTrace();
			System.exit(1);
		} finally {
			if (orbd != null) {
				System.out.println("Destroying orbd process");
				orbd.destroy();
			}
			System.out.println("Exiting orbd");
		}
	}

}
