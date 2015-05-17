package co.charbox.client.utils;

import java.util.List;

import org.elasticsearch.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.tpofof.core.utils.json.JsonUtils;

import co.charbox.client.ping.CharbotApiPingResultsHanlder;
import co.charbox.domain.model.PingResults;
/**
 * TODO: GET THIS WORKING 
 *  
 * @author david
 *
 */
@Configuration
public class CharbotClientConfiguration {

	@Autowired private CharbotApiPingResultsHanlder charbotApiPingResultsHanlder;
	@Autowired private JsonUtils json;
	
	@Bean(name="pingResultsHandlers")
	public List<IResultsHandlers<PingResults>> pingResultsHandlers() {
		List<IResultsHandlers<PingResults>> handlers = Lists.newArrayList();
		handlers.add(new ConsoleResultHandler<PingResults>(json));
		handlers.add(charbotApiPingResultsHanlder);
		return handlers;
	}
}
