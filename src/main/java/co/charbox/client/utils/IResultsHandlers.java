package co.charbox.client.utils;

public interface IResultsHandlers<ModelT> {

	boolean handle(ModelT model);
}
