package co.charbox.client.sst;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import co.charbox.client.utils.ClientChartbotApiClient;
import co.charbox.domain.model.auth.TokenAuthModel;
import co.charbox.sst.SSTProperties;
import co.charbox.sst.utils.DataReceiver;
import co.charbox.sst.utils.DataSender;
import co.charbox.sst.utils.MyIOHAndler;

import com.tpofof.core.App;
import com.tpofof.core.utils.Config;

@Slf4j
@Setter
@Component
public class SstMain implements Runnable {
	
	private String host;
	private int port;
	private final Integer deviceId;
	private final String deviceKey;
	private boolean generateDeviceToken = true; // used for local host tests to evaluate the speed of the network stack on the device.
	@Autowired private ClientChartbotApiClient client;

	@Autowired
	public SstMain(Config config) {
		host = config.getString("sst.server.ip", "127.0.0.1");
		port = config.getInt("sst.server.port", 31415);
		deviceId = config.getInt("device.id", -1);
		deviceKey = config.getString("device.api.key", "asdf123");
	}

	public void run() {
		Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
		try {
			Socket sock = new Socket(host, port);
			MyIOHAndler io = new MyIOHAndler(sock, 4096);
			
			TokenAuthModel deviceToken = generateDeviceToken 
					? client.generateDeviceToken(deviceId, deviceKey, "sst") 
					: TokenAuthModel.builder()
							.authAssetId(deviceId)
							.token("asdf123")
							.serviceName("sst")
							.build();
			initConnection(io, deviceToken);
			
			log.trace("Initialized connection");
			
			instructionLoop:
			while (true) {
				if (sock.isClosed()) {
					return;
				}
				char inst = io.read(true).charAt(0);
				switch (inst) {
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

	private void initConnection(MyIOHAndler io, TokenAuthModel token) {
		if (token == null) {
			throw new RuntimeException("Could not authenticate with data api.");
		}
		log.debug(">>>Init Connection");
		try {
			io.write(deviceId + ":" + token.getToken(), true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void executeDownloadTest(MyIOHAndler io) throws IOException {
		long size = io.readLong(true);
		log.debug(">>>Download Test: " + size);
		DataReceiver dr = new DataReceiver(io, size);
		dr.run();
		io.write(dr.getDuration(), true);
	}
	
	private void executeUploadTest(MyIOHAndler io) throws IOException {
		long size = io.readLong(true);
		log.debug(">>>Upload Test: " + size);
		new DataSender(io, SSTProperties.getDefaultDataChunk(), size).run();
	}
	
	private void executePingTest(MyIOHAndler io) throws IOException {
		log.debug(">>>Ping Test...");
		long startTime = System.currentTimeMillis();
		io.write("ping", false);
		io.read(false);
		io.write((int)(System.currentTimeMillis() - startTime), true);
	}
	
	public static void main(String[] args) {
		try {
			App.getContext().getBean(SstMain.class).run();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.exit(0);
	}
}
