package co.charbox.client.controller;

import co.charbox.client.ClientMain;
import co.charbox.client.hb.HeartbeatMain;
import co.charbox.client.ping.PingMain;
import co.charbox.client.sst.SstMain;

public class ControllerMain {

	public static void main(String[] args) {
		if (args.length != 1) {
			throw new IllegalArgumentException("Expected single argument. [client, sst, ping, hb]");
		}
		if (args[0].equals("client")) {
			System.out.println("Starting Client");
			ClientMain.main(args);
		} else if (args[0].equals("sst")) {
			System.out.println("Starting SST");
			SstMain.main(args);
		} else if (args[0].equals("ping")) {
			System.out.println("Starting Ping");
			PingMain.main(args);
		} else if (args[0].equals("hb")) {
			System.out.println("Starting Heartbeat");
			HeartbeatMain.main(args);
		} else {
			throw new IllegalArgumentException("Expected valid command. [client, sst, ping, hb]");
		}
	}
}
