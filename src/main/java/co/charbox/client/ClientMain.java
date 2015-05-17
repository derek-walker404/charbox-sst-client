package co.charbox.client;

import co.charbox.client.quartz.ScheduleManager;

import com.tpofof.core.App;

public class ClientMain {

	public static void main(String[] args) {
		App.getContext().getBean(ScheduleManager.class).init();
	}
}
