package co.charbox.client.quartz.triggers;

import org.quartz.CronExpression;
import org.quartz.CronScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tpofof.core.utils.Config;

@Component
public abstract class AbstractTriggerProvider {

	@Autowired private Config config;
	
	protected abstract CronExpression getCron();
	public abstract String getTriggerName();
	public abstract String getTriggerGroup();
	
	protected Config getConfig() {
		return config;
	}
	
	public Trigger get() {
		return TriggerBuilder
				.newTrigger()
				.withIdentity(getTriggerName(), getTriggerGroup())
				.withSchedule(CronScheduleBuilder.cronSchedule(getCron()))
				.build();
	}
}
