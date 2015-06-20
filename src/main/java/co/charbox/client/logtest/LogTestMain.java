package co.charbox.client.logtest;


import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import com.tpofof.core.App;

@Slf4j
@Component
public class LogTestMain implements Runnable {
	
	@Override
	public void run() {
		log.info("info");
		log.warn("warn");
		log.debug("debug");
		log.error("error");
		log.trace("trace");
		
		log.info(log.getName());
	}
	
	public static void main(String[] args) {
		App.getContext().getBean(LogTestMain.class).run();
		System.exit(0);
	}
}
