package co.charbox.client.ping;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tpofof.core.utils.Config;

import co.charbox.client.utils.ClientChartbotApiClient;
import co.charbox.client.utils.IResultsHandlers;
import co.charbox.domain.model.PingResults;

@Component
public class CharbotApiPingResultsHanlder implements IResultsHandlers<PingResults> {

	@Autowired ClientChartbotApiClient client;
	@Autowired Config config;
	
	public boolean handle(PingResults model) {
		return client.postPingResults(model, config.getString("device.id"), config.getString("device.api.key"));
	}

}
