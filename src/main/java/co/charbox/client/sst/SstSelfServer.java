package co.charbox.client.sst;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import co.charbox.client.sst.results.ConsoleSstResultsHandler;
import co.charbox.client.sst.results.SstResultsHandler;

import com.google.api.client.util.Lists;
import com.tpofof.core.App;
import com.tpofof.core.utils.Config;

@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class SstSelfServer implements Runnable {

	private ServerSocket sock;
	private final int initialSize;
	private final int minSendTime;
	private final ThreadPoolExecutor es;
	private final List<SstResultsHandler> handlers;
	private final AtomicBoolean keepGoing = new AtomicBoolean(false);
	
	@Autowired
	public SstSelfServer(Config config, ConsoleSstResultsHandler consoleHandler) throws IOException {
		sock = new ServerSocket(31416);
		this.initialSize = config.getInt("sst.initialSize", 6000);
		this.minSendTime = config.getInt("sst.minSendTime", 3000);
		this.es = (ThreadPoolExecutor) Executors.newFixedThreadPool(config.getInt("sst.executor.threadCount", 1));
		this.handlers = Lists.newArrayList();
		this.handlers.add(consoleHandler);
	}
	
	public void kill() {
		this.keepGoing.set(false);
	}
	
	public boolean keepGoing() {
		return keepGoing.get();
	}
	
	@Override
	public String toString() {
		return "SSTServer [intialSize=" + initialSize + ", minSendTime=" + minSendTime + "]";
	}
	
	public void run() {
		keepGoing.set(true);
		while (keepGoing.get()) {
			try {
				Socket client = sock.accept();
				System.out.println("Just connected to "
		                  + client.getRemoteSocketAddress());
				
	            es.execute(SelfServerTestRunner.builder()
	            		.client(client)
	            		.initialSize(initialSize)
	            		.minSendTime(minSendTime)
	            		.handlers(handlers)
	            		.build());
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) throws IOException, InterruptedException {
		ApplicationContext context = App.getContext();
		SstSelfServer server = context.getBean(SstSelfServer.class);
		ThreadGroup group = new ThreadGroup("Server Speed Test");
		Thread serverThread = new Thread(group, server, "server");
		SstMain client = context.getBean(SstMain.class);
		client.setGenerateDeviceToken(false);
		client.setHost("127.0.0.1");
		client.setPort(31415);
		Thread clientThread = new Thread(group, client, "client");
		serverThread.start();
		while (server.keepGoing()) {
			Thread.sleep(200);
		}
		clientThread.start();
		clientThread.join(2 * 60 * 1000);
		Thread.sleep(10 * 1000);
		server.kill();
		System.exit(0);
	}
}
