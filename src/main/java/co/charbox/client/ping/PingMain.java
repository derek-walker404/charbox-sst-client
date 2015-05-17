package co.charbox.client.ping;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import co.charbox.client.utils.ConsoleResultHandler;
import co.charbox.client.utils.IResultsHandlers;
import co.charbox.domain.model.PingResults;

import com.google.api.client.util.Lists;
import com.tpofof.core.App;
import com.tpofof.core.utils.Config;
import com.tpofof.core.utils.json.JsonUtils;

@Component
public class PingMain implements Runnable {

	@Autowired private Config config;
	private List<IResultsHandlers<PingResults>> pingHandlers;
	
	@Autowired
	public PingMain(CharbotApiPingResultsHanlder apiHandler, JsonUtils json) {
		pingHandlers = Lists.newArrayList();
		pingHandlers.add(apiHandler);
		pingHandlers.add(new ConsoleResultHandler<PingResults>(json));
	}
	
	public void run() {
		String packetCount = "-c" + config.getInt("ping.count", 10);
		String packetSize = "-s" + config.getInt("ping.packetSize", 24);
		String uri = config.getString("ping.uri", "localhost");
		ProcessBuilder pb = new ProcessBuilder("ping", packetCount, "-i0.2", packetSize, uri);
		
		try {
			Process proc = pb.start();
			BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			
			PingResults pingResults = parse(in, uri);
			
			for (IResultsHandlers<PingResults> h : pingHandlers) {
				h.handle(pingResults);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private PingResults parse(BufferedReader in, String uri) throws IOException {
		Pattern headerPattern = Pattern.compile("--- .*? ping statistics ---");
		Pattern packetLossPattern = Pattern.compile("(\\d+) packets transmitted, (\\d+) packets received, ([\\d.]+)% packet loss");
		Pattern latencyPattern = Pattern.compile("round-trip min/avg/max/stddev = ([\\d.]+)/([\\d.]+)/([\\d.]+)/([\\d.]+) ms");
		String s = null;
		while ((s = in.readLine()) != null) {
		    if (headerPattern.matcher(s).find()) {
		    	Matcher m = packetLossPattern.matcher(in.readLine());
		    	PingResults results = PingResults.builder()
		    			.deviceId(config.getString("device.id"))
		    			.uri(uri)
		    			.testStartTime(new DateTime())
		    			.build();
		    	if (m.find()) {
		    		results.setPacketCount(Integer.parseInt(m.group(1)));
		    		results.setPacketLoss(Double.parseDouble(m.group(3)));
		    	}
		    	m = latencyPattern.matcher(in.readLine());
		    	if (m.find()) {
		    		int i = 1;
		    		results.setMinLatency(Double.parseDouble(m.group(i++)));
		    		results.setAvgLatency(Double.parseDouble(m.group(i++)));
		    		results.setMaxLatency(Double.parseDouble(m.group(i++)));
		    		results.setLatencyStdDev(Double.parseDouble(m.group(i++)));
		    	}
		    	return results;
		    }
		}
		return null;
	}
	
	public static void main(String[] args) {
		App.getContext().getBean(PingMain.class).run();
	}
}
