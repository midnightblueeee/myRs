package resource;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

public class TieZiSer implements RedisSerializer<TieZi>{

	@Override
	public byte[] serialize(TieZi t) throws SerializationException {
		// TODO Auto-generated method stub
		return (t.url+"TZFIT"+t.fit).getBytes();
	}

	@Override
	public TieZi deserialize(byte[] bytes) throws SerializationException {
		String str=new String(bytes);
		int idx=str.indexOf("TZFIT");
		return idx==-1?new TieZi(str,null,null):new TieZi(str.substring(0,idx), null, str.substring(idx+5));	
	}

}
