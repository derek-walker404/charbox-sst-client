package co.charbox.sst.utils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

public class MyIOHAndler {

	private final BufferedWriter bw;
	private final Scanner scan;
	
	public MyIOHAndler(Socket sock) throws IOException {
		this.bw = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
		this.scan = new Scanner(sock.getInputStream());
	}
	
	public void write(int i) throws IOException {
		write(i + "");
	}
	
	public void write(String s) throws IOException {
		bw.write(s);
		bw.write('\n');
		bw.flush();
	}
	
	public String read() {
		String line = "";
		while (line.isEmpty()) {
			line = scan.nextLine();
		}
		return line;
	}
	
	public int readInt() {
		String val = read();
		try {
		return Integer.parseInt(val);
		} catch (Exception e) {
			System.err.println("val = \"" + val + "\"");
			e.printStackTrace();
		}
		return -1;
	}
	
	public void close() throws IOException {
		bw.close();
		scan.close();
	}
}
