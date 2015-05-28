package co.charbox.client.sst.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MyIOHAndler {

	private final BufferedOutputStream bos;
	private final BufferedInputStream bis;
	private byte[] readBuffer;
	private String remoteIp;
	
	public MyIOHAndler(Socket sock, int readBufferSize) throws IOException {
		this.bos = new BufferedOutputStream(sock.getOutputStream());
		this.bis = new BufferedInputStream(sock.getInputStream());
		this.readBuffer = new byte[readBufferSize];
		this.remoteIp = sock.getRemoteSocketAddress().toString();
		if (remoteIp.charAt(0) == '/') {
			remoteIp = remoteIp.substring(1);
		}
		int portDeclaration = remoteIp.indexOf(':');
		if (portDeclaration > 0) {
			remoteIp = remoteIp.substring(0, portDeclaration);
		}
	}
	
	public void write(long i, boolean assertValue) throws IOException {
		write(i + "", assertValue);
	}
	
	public void write(String s, boolean assertValue) throws IOException {
		s += "\n";
		write(s.getBytes(), s.length());
		if (assertValue) {
			String val = read(false);
			assert(val.equals(s));
		}
	}
	
	public void write(byte[] arr, int length) throws IOException {
		bos.write(arr, 0, length);
		bos.flush();
	}
	
	public String read(boolean assertValue) {
		try {
			String val = readLine(readBuffer);
			if (assertValue) {
				write(val, false);
			}
			return val;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public String readLine(byte[] arr) throws IOException {
		int length = 0;
		while (length == 0 || arr[length-1] != '\n') {
			length += bis.read(arr, length, arr.length - length);
		}
		return new String(arr, 0, length-1);
	}
	
	public int readAndForget() throws IOException {
		return read(readBuffer);
	}
	
	public int read(byte[] arr) throws IOException {
		int length = 0;
		while (length == 0) {
			length = bis.read(arr, length, arr.length - length);
		}
		return length;
	}
	
	public int readInt(boolean assertValue) {
		String val = read(assertValue);
		try {
			return Integer.parseInt(val);
		} catch (Exception e) {
			log.error("Error reading int: " + val);
			e.printStackTrace();
		}
		return -1;
	}
	
	public long readLong(boolean assertValue) {
		String val = read(assertValue);
		try {
			return Long.parseLong(val);
		} catch (Exception e) {
			log.error("Error reading int: " + val);
			e.printStackTrace();
		}
		return -1;
	}
	
	public void close() throws IOException {
		bos.close();
		bis.close();
	}

	public String getRemoteIp() {
		return remoteIp;
	}
}
