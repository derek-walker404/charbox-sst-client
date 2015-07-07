package co.charbox.client.utils;

import java.io.IOException;
import java.io.Serializable;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import co.charbox.domain.model.DeviceVersionModel;
import co.charbox.domain.model.PingResults;
import co.charbox.domain.model.auth.TokenAuthModel;

import com.tpofof.core.utils.AuthorizationHeader;
import com.tpofof.core.utils.Config;
import com.tpofof.core.utils.HttpClientProvider;
import com.tpofof.core.utils.json.JsonUtils;

@Component
public class ClientChartbotApiClient {

	@Autowired private Config config;
	@Autowired private HttpClientProvider httpClientProvider;
	@Autowired private JsonUtils json;
	
	public TokenAuthModel generateDeviceToken(Serializable deviceId, String deviceApiKey, String serviceId) {
		PostMethod pm = new PostMethod(config.getString("charbot.api.uri", "http://localhost:8080") + "/tokenauth/" + serviceId + "/new");
		pm.addRequestHeader(new AuthorizationHeader(deviceId, deviceApiKey));
		try {
			if (200 == httpClientProvider.get().executeMethod(pm)) {
				return json.fromJsonResponse(pm.getResponseBodyAsString(), TokenAuthModel.class);
			}
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			pm.releaseConnection();
		}
		return null;
	}

	public boolean heartbeat(String deviceId, String deviceApiKey) {
		PostMethod pm = new PostMethod(config.getString("charbot.api.uri", "http://localhost:8080") + "/devices/id/" + deviceId + "/hb");
		pm.addRequestHeader(new AuthorizationHeader(deviceId, deviceApiKey));
		try {
			return 200 == httpClientProvider.get().executeMethod(pm);
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			pm.releaseConnection();
		}
		return false;
	}

	public boolean postPingResults(PingResults model, String deviceId, String deviceApiKey) {
		PostMethod pm = new PostMethod(config.getString("charbot.api.uri", "http://localhost:8080") + "/pingres");
		pm.addRequestHeader(new AuthorizationHeader(deviceId, deviceApiKey));
		try {
			pm.setRequestEntity(new StringRequestEntity(json.toJson(model), "application/json", "UTF-8"));
			return 200 == httpClientProvider.get().executeMethod(pm);
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			pm.releaseConnection();
		}
		return false;
	}

	public DeviceVersionModel queryUpgrade(String deviceId, String deviceApiKey, String version) {
		GetMethod pm = new GetMethod(config.getString("charbot.api.uri", "http://localhost:8080") + "/versions/upgrade/" + version);
		pm.addRequestHeader(new AuthorizationHeader(deviceId, deviceApiKey));
		try {
			if (200 == httpClientProvider.get().executeMethod(pm)) {
				return json.fromJsonResponse(pm.getResponseBodyAsString(), DeviceVersionModel.class);
			}
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			pm.releaseConnection();
		}
		return null;
	}
}
