package co.charbox.client.quartz.jobs;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class HeartbeatJob implements Job {

	public void execute(JobExecutionContext context) throws JobExecutionException {
		JobUtils.getClientJobExecutor().execute(JobUtils.getHeartbeatMain());
	}

}
