package co.charbox.client.quartz;

import org.quartz.JobBuilder;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import co.charbox.client.quartz.jobs.HeartbeatJob;
import co.charbox.client.quartz.jobs.PingJob;
import co.charbox.client.quartz.jobs.SstJob;
import co.charbox.client.quartz.jobs.UpgradeJob;
import co.charbox.client.quartz.triggers.AbstractTriggerProvider;
import co.charbox.client.quartz.triggers.HeartbeatTriggerProvider;
import co.charbox.client.quartz.triggers.PingTriggerProvider;
import co.charbox.client.quartz.triggers.SstTriggerProvider;
import co.charbox.client.quartz.triggers.UpgradeTriggerProvider;

@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class ScheduleManager {

	private Scheduler scheduler;
	
	@Autowired private HeartbeatTriggerProvider heartbeatTriggerProvider;
	@Autowired private PingTriggerProvider pingTriggerProvider;
	@Autowired private SstTriggerProvider sstTriggerProvider;
	@Autowired private UpgradeTriggerProvider upgradeTriggerProvider;
	
	
	public ScheduleManager() {
		try {
			scheduler = new StdSchedulerFactory().getScheduler();
			scheduler.start();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}
	
	public void init() {
		try {
			scheduler.scheduleJob(JobBuilder.newJob(HeartbeatJob.class).build(), heartbeatTriggerProvider.get());
			scheduler.scheduleJob(JobBuilder.newJob(PingJob.class).build(), pingTriggerProvider.get());
			scheduler.scheduleJob(JobBuilder.newJob(SstJob.class).build(), sstTriggerProvider.get());
			scheduler.scheduleJob(JobBuilder.newJob(UpgradeJob.class).build(), upgradeTriggerProvider.get());
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}
	
	public boolean updateHeartbeatSchedule() {
		return updateSchedule(heartbeatTriggerProvider);
	}
	
	public boolean updatePingSchedule() {
		return updateSchedule(pingTriggerProvider);
	}
	
	public boolean updateSstSchedule() {
		return updateSchedule(sstTriggerProvider);
	}
	
	public boolean updateUpgradeSchedule() {
		return updateSchedule(upgradeTriggerProvider);
	}
	
	protected boolean updateSchedule(AbstractTriggerProvider provider) {
		try {
			Trigger trigger = provider.get();
			scheduler.rescheduleJob(trigger.getKey(), trigger);
			return true;
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
		return false;
	}
}
