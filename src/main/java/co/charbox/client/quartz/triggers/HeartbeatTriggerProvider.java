package co.charbox.client.quartz.triggers;

import java.text.ParseException;

import org.quartz.CronExpression;

public class HeartbeatTriggerProvider extends AbstractTriggerProvider {

	@Override
	protected CronExpression getCron() {
		String rawCron = getConfig().getString("job.heartbeat.interval", "* * * * * ?");
		try {
			return new CronExpression(rawCron);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String getTriggerName() {
		return "heartbeat";
	}

	@Override
	public String getTriggerGroup() {
		return "client";
	}

}
