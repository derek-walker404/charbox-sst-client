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
	private int currBufferLength;
	private String remoteIp;
	
	public MyIOHAndler(Socket sock, int readBufferSize) throws IOException {
		this.bos = new BufferedOutputStream(sock.getOutputStream());
		this.bis = new BufferedInputStream(sock.getInputStream());
		this.readBuffer = new byte[readBufferSize];
		this.currBufferLength = 0;
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
		String rawS = s + "\n";
		write(rawS.getBytes(), rawS.length());
		if (assertValue) {
			String val = read(false);
			if (!val.equals(s)) {
				throw new RuntimeException("Values not equal: " + s.replace("\n", "\\n") + " != " + val.replace("\n", "\\n"));
			}
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
		if (currBufferLength > 0 && readBuffer[currBufferLength - 1] == '\n') {
			int tempLength = currBufferLength;
			currBufferLength = 0;
			return new String(arr, 0, tempLength);
		}
		int length = currBufferLength;
		int lastLength = 0;
		int endIndex = currBufferLength;
		readLoop:
		while (length <= 0) {
			length += bis.read(arr, length, arr.length - length);
			for (int i=0;i<length;i++) {
				if (arr[i] == '\n') {
					endIndex = i;
					break readLoop;
				}
			}
			if (lastLength != length) {
				lastLength = length;
			}
			if (length == readBuffer.length) {
				cleanBuffer();
			}
		}
		String val = new String(arr, 0, endIndex);
		if (endIndex + 1 == length) {
			currBufferLength = 0;
		} else {
			for (int i=endIndex + 1, j=0;i<length;i++,j++) {
				readBuffer[j] = readBuffer[i];
			}
			currBufferLength = length - endIndex - 1;
		}
		cleanBuffer();
		return val;
	}
	
	private void cleanBuffer() {
		if (currBufferLength == 1 && readBuffer[0] == '\n') {
			currBufferLength = 0;
			return;
		}
		int firstNonZero = 0;
		for(int i=0;i<currBufferLength;i++) {
			if (readBuffer[i] != '0') {
				firstNonZero = i;
				break;
			}
		}
		if (firstNonZero > 0) {
			int length = 0;
			for (int i=firstNonZero;i<readBuffer.length;length++,i++) {
				readBuffer[length] = readBuffer[i];
			}
			currBufferLength = length;
		}
	}
	
	public int readAndForget() throws IOException {
		currBufferLength = 0;
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
