package resource;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import resource.TieZi.TieZiYjxInfo;

public class TizZiInfoSer implements RedisSerializer<TieZiYjxInfo>{

	@Override
	public byte[] serialize(TieZiYjxInfo t) throws SerializationException {
		// TODO Auto-generated method stub
		return t.serialize();
	}

	@Override
	public TieZiYjxInfo deserialize(byte[] bytes) throws SerializationException {
		return TieZiYjxInfo.deserialize(bytes);
		 
	}

}
