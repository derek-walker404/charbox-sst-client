package co.charbox.client.sst;

import java.io.IOException;
import java.net.Socket;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import org.joda.time.DateTime;

import co.charbox.client.sst.results.SstResultsHandler;
import co.charbox.client.sst.utils.DataReceiver;
import co.charbox.client.sst.utils.DataSender;
import co.charbox.client.sst.utils.MyIOHAndler;
import co.charbox.core.utils.SpeedUtils;
import co.charbox.domain.model.MyLocation;
import co.charbox.domain.model.SstResults;
import co.charbox.domain.model.mm.ConnectionInfoModel;
import co.charbox.domain.model.mm.MyCharboxConnection;

@Slf4j
@Builder
@AllArgsConstructor
public class SelfServerTestRunner implements Runnable {

	@NonNull private final Socket client;
	@NonNull private Integer initialSize;
	@NonNull private Integer minSendTime;
	@NonNull private List<SstResultsHandler> handlers;
	private SstResults results;

	public void run() {
		Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
		try {
			log.trace("Executing SelfServerSpeedTest...");
			MyIOHAndler io = new MyIOHAndler(client);
			initResults(io);
			log.trace("Initialized...");
			
			calculateDownloadSpeed(io);
			log.trace("Download Complete...");
			calculateUploadSpeed(io);
			log.trace("Upload Complete...");
			calculatePingSpeed(io);
			log.trace("Ping Complete...");
			
			io.write("F");
			log.trace("Test Complete...");
			
			for (SstResultsHandler handler : handlers) {
				handler.handle(results, client);
			}
			io.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidDeviceTokenException e) {
			e.printStackTrace();
		} finally {
			try {
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void initResults(MyIOHAndler io) throws InvalidDeviceTokenException {
		String deviceId = io.read();
		log.debug("Read deviceId " + deviceId);
		String deviceToken = io.read();
		log.debug("Read deviceToken " + deviceToken);
		this.results = SstResults.builder()
			.deviceId(deviceId)
			.deviceToken(deviceToken)
			.testStartTime(new DateTime())
			.deviceInfo(ConnectionInfoModel.builder()
					.connection(MyCharboxConnection.builder()
							.ip(io.getRemoteIp())
							.build())
					.build())
			.serverLocation(MyLocation.builder()
					.ip("") // TODO
					.build())
			.build();
	}
	
	private void calculateDownloadSpeed(MyIOHAndler io) throws IOException {
		// TODO: cache initial size??
		long currSize = this.initialSize;
		long totalDownloadSize = 0;
		while (this.results.getDownloadDuration() < this.minSendTime) {
			executeDownloadTest(currSize, io);
			totalDownloadSize += currSize;
			if (this.results.getDownloadDuration() >= this.minSendTime) {
				double speed = this.results.getDownloadSpeed();
				int duration = this.results.getDownloadDuration();
				executeDownloadTest(currSize, io);
				this.results.setDownloadSpeed(avg(this.results.getDownloadSpeed(), speed));
				this.results.setDownloadDuration((int)avg(this.results.getDownloadDuration(), duration));
				break;
			} else {
				currSize *= 2;
			}
		}
		log.debug("Total Download: " + totalDownloadSize/1024/1024 + "Mbs");
	}
	
	private void executeDownloadTest(long size, MyIOHAndler io) throws IOException {
		log.trace("Download Test...");
		this.results.setDownloadSize(size);
		io.write("D");
		io.write(size);
		new DataSender(io, SSTProperties.getDefaultDataChunk(), size).run();
		this.results.setDownloadDuration(io.readInt());
		this.results.setDownloadSpeed(SpeedUtils.calcSpeed(results.getDownloadDuration(), size));
	}
	
	private void calculateUploadSpeed(MyIOHAndler io) throws IOException {
		// TODO: cache initial size??
		int currSize = this.initialSize;
		long totalUploadSize = 0;
		while (this.results.getUploadDuration() < this.minSendTime) {
			executeUploadTest(currSize, io);
			totalUploadSize += currSize;
			if (this.results.getUploadDuration() >= this.minSendTime) {
				double speed = this.results.getDownloadSpeed();
				int duration = this.results.getDownloadDuration();
				executeUploadTest(currSize, io);
				this.results.setUploadSpeed(avg(this.results.getUploadSpeed(), speed));
				this.results.setUploadDuration((int)avg(this.results.getUploadDuration(), duration));
				break;
			} else {
				currSize *= 2;
			}
		}
		log.debug("Total Upload: " + totalUploadSize/1024/1024 + "Mbs");
	}
	
	private void executeUploadTest(long size, MyIOHAndler io) throws IOException {
//		System.out.println("Upload Test...");
		this.results.setUploadSize(size);
		io.write("U");
		io.write(size);
		DataReceiver dr = new DataReceiver(io, size);
		dr.run();
		this.results.setUploadDuration(dr.getDuration());
		this.results.setUploadSpeed(SpeedUtils.calcSpeed(results.getUploadDuration(), size));
	}
	
	private void calculatePingSpeed(MyIOHAndler io) throws IOException {
		executePingTest(io);
		int duration = this.results.getPingDuration();
		executePingTest(io);
		this.results.setPingDuration((int)avg(this.results.getPingDuration(), duration));
	}
	
	private void executePingTest(MyIOHAndler io) throws IOException {
		log.trace("Ping Test...");
		io.write("P");
		io.write(io.read());
		this.results.setPingDuration(io.readInt());
	}
	
	private double avg(double a, double b) {
		return (double)(a + b) / 2.0;
	}
}
