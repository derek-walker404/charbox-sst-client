package co.charbox.client.quartz.triggers;

import java.text.ParseException;

import org.quartz.CronExpression;
import org.springframework.stereotype.Component;

@Component
public class SstTriggerProvider extends AbstractTriggerProvider {

	@Override
	protected CronExpression getCron() {
		String rawCron = getConfig().getString("job.sst.interval", "0 0 */3 * * ?");
		try {
			return new CronExpression(rawCron);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String getTriggerName() {
		return "sst";
	}

	@Override
	public String getTriggerGroup() {
		return "client";
	}

}
