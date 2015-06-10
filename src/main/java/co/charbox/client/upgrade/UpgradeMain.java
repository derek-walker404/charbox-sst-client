package co.charbox.client.upgrade;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import co.charbox.client.utils.ClientChartbotApiClient;
import co.charbox.domain.model.DeviceVersionModel;

import com.tpofof.core.App;
import com.tpofof.core.io.IO;
import com.tpofof.core.utils.Config;
import com.tpofof.core.utils.json.JsonUtils;

/**
 * Reboot Hint: http://ubuntuforums.org/showthread.php?t=1035397
 * 
 * @author david
 */
@Slf4j
@Component
public class UpgradeMain implements Runnable {

	@Autowired private Config config;
	@Autowired private ClientChartbotApiClient client;
	@Autowired private IO io;
	@Autowired private JsonUtils json;
	

	public void run() {
		String deviceId = config.getString("device.id");
		String deviceApiKey = config.getString("device.api.key");
		String version = config.getString("device.version", "0.0.0");
		DeviceVersionModel newVersion = client.queryUpgrade(deviceId, deviceApiKey, version);
		if (newVersion != null) {
			log.info(json.toJson(newVersion));
			try {
				URL installScriptUrl = new URL(newVersion.getInstallScriptUrl());
				String[] path = installScriptUrl.getFile().split("/");
				String upgradeFileName = path[path.length - 1];
				String contents = io.getContents(installScriptUrl);
				io.toFile(upgradeFileName, contents);
				log.info("Saved upgrade script to " + upgradeFileName);
				
				new ProcessBuilder("chmod", "+x", upgradeFileName).start().waitFor();
				new ProcessBuilder("/bin/bash", "-c", "./" + upgradeFileName).start().waitFor();
				log.info("Upgraded to: " + newVersion.getVersion());
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
		App.getContext().getBean(UpgradeMain.class).run();
	}
}
