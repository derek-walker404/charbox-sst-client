package co.charbox.client.quartz.jobs;

import lombok.extern.slf4j.Slf4j;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

@Slf4j
public class HeartbeatJob implements Job {

	public void execute(JobExecutionContext context) throws JobExecutionException {
		log.info(getClass().getSimpleName());
		JobUtils.getClientJobExecutor().execute(JobUtils.getHeartbeatMain());
	}

}
