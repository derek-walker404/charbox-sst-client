package co.charbox.sst.utils;

import java.io.IOException;

public class DataSender implements Runnable {

	private MyIOHAndler io;
	private String data;
	private int size;
	
	public DataSender(MyIOHAndler io, String data, int size) {
		super();
		this.io = io;
		this.data = data;
		this.size = size;
	}

	public void run() {
		try {
			int iters = size/data.length();
			data += "\n";
			for (int i=0;i<iters;i++) {
					io.write(data);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
