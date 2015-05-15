package co.charbox.sst.client;

import java.io.IOException;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import co.charbox.domain.model.auth.TokenAuthModel;

import com.tpofof.core.utils.AuthorizationHeader;
import com.tpofof.core.utils.Config;
import com.tpofof.core.utils.HttpClientProvider;
import com.tpofof.core.utils.json.JsonUtils;

@Component
public class SstClientChartbotApiClient {

	@Autowired private Config config;
	@Autowired private HttpClientProvider httpClientProvider;
	@Autowired private JsonUtils json;
	
	public TokenAuthModel generateDeviceToken(String deviceId, String deviceApiKey, String serviceId) {
		PostMethod get = new PostMethod(config.getString("charbot.api.uri", "http://localhost:8080") + "/tokenauth/" + serviceId + "/new");
		get.addRequestHeader(new AuthorizationHeader(deviceId, deviceApiKey));
		try {
			if (200 == httpClientProvider.get().executeMethod(get)) {
				return json.fromJsonResponse(get.getResponseBodyAsString(), TokenAuthModel.class);
			}
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
