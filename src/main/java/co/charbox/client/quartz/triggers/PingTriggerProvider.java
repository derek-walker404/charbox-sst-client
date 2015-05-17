package co.charbox.client.quartz.triggers;

import java.text.ParseException;

import org.quartz.CronExpression;
import org.springframework.stereotype.Component;

@Component
public class PingTriggerProvider extends AbstractTriggerProvider {

	@Override
	protected CronExpression getCron() {
		String rawCron = getConfig().getString("job.ping_results.interval", "0 */15 * * * ?");
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
