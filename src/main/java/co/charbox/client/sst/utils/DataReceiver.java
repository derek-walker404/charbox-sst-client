package co.charbox.client.sst.utils;

import java.io.IOException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
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
		long loopCount = 0;
		while (currSize < size) {
			if (loopCount++ >= 1000000) {
				loopCount = 0;
				log.warn("Wheels Spinning! currSize: " + currSize + " expectedSize: " + size);
				if (System.currentTimeMillis() - startTime > 5000) {
					this.duration = -1;
					return;
				}
			}
			if (currSize < 0) {
				throw new RuntimeException("Something went wrong. Size is less than 0.");
			}
			try {
				int bytesReceived = io.readAndForget();
				currSize += bytesReceived;
				if (bytesReceived > 0) {
					loopCount = 0;
				}
			} catch (IOException e) {
				log.error(e.getMessage());
				return;
			}
		}
		this.duration = (int)(System.currentTimeMillis() - startTime);
	}
}
