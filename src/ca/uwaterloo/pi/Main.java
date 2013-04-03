package ca.uwaterloo.pi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {

	public Main() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws IOException, InterruptedException {
		Process tr = Runtime.getRuntime().exec( "opt -S -print-callgraph " + args[0] );
		BufferedReader rd = new BufferedReader( new InputStreamReader( tr.getErrorStream() ) );
		int exitcode = tr.waitFor();
		while(rd.ready()) {
			String s = rd.readLine();
			System.out.println( s );
		}
	}

}
