package co.charbox.sst;


public abstract class SSTProperties {

	public static final String DATA_CHUNK_100 = "0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";
	public static final String DATA_CHUNK_1000 = DATA_CHUNK_100 + DATA_CHUNK_100 + DATA_CHUNK_100 + DATA_CHUNK_100 + DATA_CHUNK_100 + DATA_CHUNK_100
			 + DATA_CHUNK_100 + DATA_CHUNK_100 + DATA_CHUNK_100 + DATA_CHUNK_100;
	public static final String DATA_CHUNK_2000 = DATA_CHUNK_1000 + DATA_CHUNK_1000;
	
	public static String getDefaultDataChunk() {
		return DATA_CHUNK_2000;
	}
}
