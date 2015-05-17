package co.charbox.client.quartz.jobs;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

@Component
public class PingJob implements Job {

	public void execute(JobExecutionContext context) throws JobExecutionException {
		System.out.println(getClass().getSimpleName());
		JobUtils.getClientJobExecutor().execute(JobUtils.getPingMain());
	}

}
