package co.charbox.client.sst.results;

import java.net.Socket;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import co.charbox.domain.model.SstResultsModel;

import com.tpofof.core.utils.json.JsonUtils;

@Slf4j
@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class ConsoleSstResultsHandler implements SstResultsHandler {

	@Autowired private JsonUtils json;
	
	public boolean handle(SstResultsModel results, Socket client) {
		log.info(json.toJson(results));
		log.info(client.getRemoteSocketAddress().toString());
		return true;
	}

}
