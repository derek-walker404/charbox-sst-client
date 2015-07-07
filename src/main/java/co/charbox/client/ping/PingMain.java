package co.charbox.client.ping;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

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
	@Autowired private PingCliParser parser;
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
			
			PingResults pingResults = parser.parse(in, uri, config.getInt("device.id"));
			
			for (IResultsHandlers<PingResults> h : pingHandlers) {
				h.handle(pingResults);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		App.getContext().getBean(PingMain.class).run();
	}
}
