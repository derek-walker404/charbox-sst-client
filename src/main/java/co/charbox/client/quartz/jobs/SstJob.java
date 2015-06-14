package co.charbox.client.quartz.jobs;

import lombok.extern.slf4j.Slf4j;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SstJob implements Job {

	public void execute(JobExecutionContext context) throws JobExecutionException {
		log.info(getClass().getSimpleName());
		JobUtils.getClientJobExecutor().execute(JobUtils.getSstMain());
	}

}
