package co.charbox.client.quartz.jobs;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class UpgradeJob implements Job {

	public void execute(JobExecutionContext context) throws JobExecutionException {
		System.out.println(getClass().getSimpleName());
		JobUtils.getClientJobExecutor().execute(JobUtils.getUpgradeMain());
	}

}
