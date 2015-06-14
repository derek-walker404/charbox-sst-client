package co.charbox.client.utils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.tpofof.core.utils.json.JsonUtils;

@Slf4j
@AllArgsConstructor
public class ConsoleResultHandler<ModelT> implements IResultsHandlers<ModelT> {
	
	private JsonUtils json;
	
	public boolean handle(ModelT model) {
		log.info(model.getClass().getSimpleName() + ":\t" + json.toJson(model));
		return true;
	}

}
