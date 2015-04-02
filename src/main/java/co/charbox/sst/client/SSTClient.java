package co.charbox.sst.client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import co.charbox.core.utils.Config;
import co.charbox.sst.SSTProperties;
import co.charbox.sst.utils.DataReceiver;
import co.charbox.sst.utils.DataSender;
import co.charbox.sst.utils.MyIOHAndler;

public class SSTClient implements Runnable {

	private final String host;
	private final int port;
	private final String deviceId;
	private final String deviceKey;

	public SSTClient(String host, int port, String deviceId, String deviceKey) {
		this.host = host;
		this.port = port;
		this.deviceId = deviceId;
		this.deviceKey = deviceKey;
	}

	public void run() {
		try {
			Socket sock = new Socket(host, port);
			MyIOHAndler io = new MyIOHAndler(sock);
			
			initConnection(io);
			
			instructionLoop:
			while (true) {
				switch (io.read().charAt(0)) {
				case 'D': {
					executeDownloadTest(io);
					break;
				}
				case 'U': {
					executeUploadTest(io);
					break;
				}
				case 'P': {
					executePingTest(io);
					break;
				}
				case 'F': {
					break instructionLoop;
				}
				}
				
			}
			
			io.close();
			sock.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void initConnection(MyIOHAndler io) {
		System.out.println(">>>Init Connection");
		try {
			io.write(deviceId);
			io.write(deviceKey);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void executeDownloadTest(MyIOHAndler io) throws IOException {
		int size = io.readInt();
		System.out.println(">>>Download Test: " + size);
		DataReceiver dr = new DataReceiver(io, size);
		dr.run();
		io.write(dr.getDuration());
	}
	
	private void executeUploadTest(MyIOHAndler io) throws IOException {
		int size = io.readInt();
		System.out.println(">>>Upload Test: " + size);
		new DataSender(io, SSTProperties.getDefaultDataChunk(), size).run();
	}
	
	private void executePingTest(MyIOHAndler io) throws IOException {
		System.out.println(">>>Ping Test...");
		long startTime = System.currentTimeMillis();
		io.write("ping");
		io.read();
		io.write((int)(System.currentTimeMillis() - startTime));
	}
	
	public static void main(String[] args) {
//		String host = "52.4.205.170";
		String host = Config.get().getString("sst.server.ip", "127.0.0.1");
		int port = Config.get().getInt("sst.server.port", 31415);
		String deviceId = Config.get().getString("device.id", "abcde");
		String deviceKey = Config.get().getString("device.key", "ignore");
		new SSTClient(host, port, deviceId, deviceKey).run();
	}
}
