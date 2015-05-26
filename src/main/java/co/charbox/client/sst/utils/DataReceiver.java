package co.charbox.client.sst.utils;


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
		int currSize = 0;
		long startTime = System.currentTimeMillis();
		while (currSize < size) {
			currSize += io.read().length();
		}
		this.duration = (int)(System.currentTimeMillis() - startTime);
	}
}
