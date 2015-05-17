package co.charbox.client.quartz.jobs;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

@Component
public class SstJob implements Job {

	public void execute(JobExecutionContext context) throws JobExecutionException {
		JobUtils.getClientJobExecutor().execute(JobUtils.getSstMain());
	}

}
