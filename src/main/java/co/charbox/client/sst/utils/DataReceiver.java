package co.charbox.client.sst.utils;

import java.io.IOException;


public class DataReceiver implements Runnable {

	private MyIOHAndler io;
	private long size;
	private int duration;
	
	public DataReceiver(MyIOHAndler io, long size) {
		super();
		this.io = io;
		this.size = size;
	}

	public int getDuration() {
		return duration;
	}

	public void run() {
		long currSize = 0;
		long startTime = System.currentTimeMillis();
		while (currSize < size) {
			try {
				currSize += io.readAndForget();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		this.duration = (int)(System.currentTimeMillis() - startTime);
	}
}
