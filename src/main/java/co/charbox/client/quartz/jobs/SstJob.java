package co.charbox.client.quartz.jobs;

import java.util.concurrent.Future;

import lombok.extern.slf4j.Slf4j;

import org.joda.time.DateTime;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.BeansException;
import org.springframework.stereotype.Component;

import co.charbox.client.sst.SstMain;

import com.tpofof.core.App;

@Slf4j
@Component
public class SstJob implements Job {

	public void execute(JobExecutionContext context) throws JobExecutionException {
		log.info(getClass().getSimpleName());
		final SstMain sstMain = JobUtils.getSstMain();
		final Future<?> promise = JobUtils.getClientJobExecutor().submit(sstMain);
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					if (sstMain.isDone()) {
						log.debug("Finished job. Killing timeout monitor thread.");
						return;
					}
					DateTime exp = sstMain.getExpiration();
					if (exp != null && exp.compareTo(new DateTime()) <= 0) {
						log.error("Sst Job timeout. Cancelling job.");
						promise.cancel(true);
					}
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	public static void main(String[] args) throws BeansException, JobExecutionException {
		App.getContext().getBean(SstJob.class).execute(null);
	}
}
