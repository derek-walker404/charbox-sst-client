package co.charbox.client.controller;

import lombok.extern.slf4j.Slf4j;
import co.charbox.client.ClientMain;
import co.charbox.client.hb.HeartbeatMain;
import co.charbox.client.ping.PingMain;
import co.charbox.client.sst.SstMain;
import co.charbox.client.sst.SstSelfServer;
import co.charbox.client.upgrade.UpgradeMain;

@Slf4j
public class ControllerMain {

	public static void main(String[] args) throws Exception {
		if (args.length != 1) {
			throw new IllegalArgumentException("Expected single argument. [client, sst, ping, hb]");
		}
		if (args[0].equals("client")) {
			log.info("Starting Client");
			ClientMain.main(args);
		} else if (args[0].equals("sst")) {
			log.info("Starting SST");
			SstMain.main(args);
			log.info("SST Complete, exiting client.");
			System.exit(0);
		} else if (args[0].equals("self-sst")) {
			log.info("Starting Self-SST");
			SstSelfServer.main(args);
			log.info("Self-SST Complete, exiting client.");
			System.exit(0);
		} else if (args[0].equals("ping")) {
			log.info("Starting Ping");
			PingMain.main(args);
			log.info("Ping Complete, exiting client.");
			System.exit(0);
		} else if (args[0].equals("hb")) {
			log.info("Starting Heartbeat");
			HeartbeatMain.main(args);
			log.info("Heartbeat Complete, exiting client.");
			System.exit(0);
		} else if (args[0].equals("upgrade")) {
			log.info("Starting Upgrade");
			UpgradeMain.main(args);
			log.info("Upgrade Complete, exiting client.");
			System.exit(0);
		} else {
			throw new IllegalArgumentException("Expected valid command. [client, sst, ping, hb]");
		}
	}
}
