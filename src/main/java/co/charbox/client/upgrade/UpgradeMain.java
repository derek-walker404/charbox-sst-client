package co.charbox.client.upgrade;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tpofof.core.App;
import com.tpofof.core.io.IO;
import com.tpofof.core.utils.Config;

import co.charbox.client.utils.ClientChartbotApiClient;
import co.charbox.domain.model.DeviceVersionModel;

@Slf4j
@Component
public class UpgradeMain implements Runnable {

	@Autowired private Config config;
	@Autowired private ClientChartbotApiClient client;
	@Autowired private IO io;
	

	public void run() {
		String deviceId = config.getString("device.id");
		String deviceApiKey = config.getString("device.api.key");
		String version = config.getString("device.version", "0.0.0");
		DeviceVersionModel newVersion = client.queryUpgrade(deviceId, deviceApiKey, version);
		if (newVersion != null) {
			try {
				URL installScriptUrl = new URL(newVersion.getInstallScriptUrl());
				String contents = io.getContents(installScriptUrl);
				io.toFile("upgrade.sh", contents);
				Process proc = new ProcessBuilder("/bin/bash","-c","echo \"odroid\"| sudo shutdown -r now").start();
				String line;
				@Cleanup
			    BufferedReader input = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			    while ((line = input.readLine()) != null) {
			        log.info(line);
			    }
			    input.close();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
		App.getContext().getBean(UpgradeMain.class).run();
	}
}
