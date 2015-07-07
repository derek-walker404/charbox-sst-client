package co.charbox.client.ping;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.extern.slf4j.Slf4j;

import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

import co.charbox.domain.model.DeviceModel;
import co.charbox.domain.model.PingResults;

@Slf4j
@Component
public class PingCliParser {
	
	public PingResults parse(BufferedReader in, String uri, Integer deviceId) throws IOException {
		Pattern headerPattern = Pattern.compile("^---.+");
		String s = null;
		while ((s = in.readLine()) != null) {
		    if (headerPattern.matcher(s).find()) {
		    	PingResults results = PingResults.builder()
		    			.device(DeviceModel.builder()
		    					.id(deviceId)
		    					.build())
		    			.uri(uri)
		    			.startTime(new DateTime())
		    			.build();
		    	String row = in.readLine();
		    	Number[] packetLossVals = parsePacketLossRow(row);
		    	if (packetLossVals != null) {
		    		results.setPacketLoss(packetLossVals[1].doubleValue());
		    	} else {
		    		log.error("Could not parse packet loss row! " + row);
		    	}
		    	row = in.readLine();
		    	Number[] latenctRowVals = parseLatenctRow(row);
		    	if (latenctRowVals != null) {
		    		results.setMinLatency(latenctRowVals[0].doubleValue());
		    		results.setAvgLatency(latenctRowVals[1].doubleValue());
		    		results.setMaxLatency(latenctRowVals[2].doubleValue());
		    	} else {
		    		log.error("Could not parse latenct row! " + row);
		    	}
		    	return results;
		    }
		}
		return null;
	}
	
	private static final Pattern packetLossPattern = Pattern.compile("(\\d+) .+? (\\d+) .+? ([\\d.]+)%");
	private static final Pattern latencyPattern = Pattern.compile("([\\d.]+)/([\\d.]+)/([\\d.]+)/([\\d.]+) ms");

	private Number[] parsePacketLossRow(String row) {
		Matcher m = packetLossPattern.matcher(row);
		return m.find()
				? new Number[] { Integer.parseInt(m.group(1)), Double.parseDouble(m.group(3)) }
				: null;
	}
	
	private Number[] parseLatenctRow(String row) {
		Matcher m = latencyPattern.matcher(row);
		return m.find()
				? new Number[] { Double.parseDouble(m.group(1)),
						Double.parseDouble(m.group(2)),
						Double.parseDouble(m.group(3)),
						Double.parseDouble(m.group(4)) 
					}
				: null;
	}
}
