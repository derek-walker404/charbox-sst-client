package co.charbox.client.utils;

import lombok.AllArgsConstructor;

import com.tpofof.core.utils.json.JsonUtils;

@AllArgsConstructor
public class ConsoleResultHandler<ModelT> implements IResultsHandlers<ModelT> {
	
	private JsonUtils json;
	
	public boolean handle(ModelT model) {
		System.out.println(json.toJson(model));
		return true;
	}

}
