package co.charbox.client.hb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tpofof.core.App;
import com.tpofof.core.utils.Config;

import co.charbox.client.utils.ClientChartbotApiClient;

@Component
public class HeartbeatMain implements Runnable {

	@Autowired private ClientChartbotApiClient client;
	@Autowired private Config config;
	
	public void run() {
		client.heartbeat(config.getString("device.id"), config.getString("device.api.key"));
	}
	
	public static void main(String[] args) {
		App.getContext().getBean(HeartbeatMain.class).run();
	}
}
