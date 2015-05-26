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
			long currCount = 0;
			data += "\n";
			while (currCount <= size) {
					io.write(data);
					currCount += data.length() - 1;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
