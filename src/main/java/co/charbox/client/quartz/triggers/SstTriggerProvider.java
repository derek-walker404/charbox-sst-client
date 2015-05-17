package co.charbox.client.quartz.triggers;

import java.text.ParseException;

import org.quartz.CronExpression;

public class SstTriggerProvider extends AbstractTriggerProvider {

	@Override
	protected CronExpression getCron() {
		String rawCron = getConfig().getString("job.sst.interval", "0 */3 * * * ?");
		try {
			return new CronExpression(rawCron);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String getTriggerName() {
		return "pingres";
	}

	@Override
	public String getTriggerGroup() {
		return "client";
	}

}
