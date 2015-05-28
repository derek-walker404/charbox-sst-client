package co.charbox.client.sst.utils;

import java.io.IOException;

public class DataSender implements Runnable {

	private MyIOHAndler io;
	private String data;
	private long size;
	
	public DataSender(MyIOHAndler io, String data, long size) {
		super();
		this.io = io;
		this.data = data;
		this.size = size;
	}

	public void run() {
		try {
			long currSize = 0;
			byte[] rawData = data.getBytes();
			int length = rawData.length;
			while (currSize < size) {
					io.write(rawData, length);
					currSize += length;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
