package co.charbox.client.quartz.jobs;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import co.charbox.client.hb.HeartbeatMain;
import co.charbox.client.ping.PingMain;
import co.charbox.client.sst.SstMain;

import com.tpofof.core.App;

public class JobUtils {

	private static final HeartbeatMain heartbeatMain = App.getContext().getBean(HeartbeatMain.class);
	private static final PingMain pingMain = App.getContext().getBean(PingMain.class);
	private static final SstMain sstMain = App.getContext().getBean(SstMain.class);
	private static final ThreadPoolExecutor clientJobExecutor = (ThreadPoolExecutor) Executors.newSingleThreadExecutor();
	
	public static HeartbeatMain getHeartbeatMain() {
		return heartbeatMain;
	}
	
	public static PingMain getPingMain() {
		return pingMain;
	}
	
	public static SstMain getSstMain() {
		return sstMain;
	}
	
	public static ThreadPoolExecutor getClientJobExecutor() {
		return clientJobExecutor;
	}
	
	public static int getQueueSize() {
		return clientJobExecutor.getQueue().size();
	}
}
