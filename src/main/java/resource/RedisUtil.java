package resource;

import java.util.Collections;

import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;

import redis.clients.jedis.Jedis;

public class RedisUtil {
	static RedisTemplate<String,?> redisTemplate=TieBa.redisTemplate;
	static public boolean deleteByKeyPattern(String keyPattern) {
		Long result = redisTemplate.execute(new RedisCallback<Long>() {
			public Long doInRedis(RedisConnection connection) {
				Jedis jedis = (Jedis) connection.getNativeConnection();
				StringBuffer script = new StringBuffer();
				script.append(" local ks = redis.call('KEYS', KEYS[1])");
				script.append(" for i = 1, #ks, 5000 do");
				script.append("     redis.call('del', unpack(ks, i, math.min(i + 4999, #ks)))");
				script.append(" end");
				script.append(" return 1");
				Object resultTemp = jedis.eval(script.toString(), Collections.singletonList(keyPattern),
						Collections.singletonList(""));
				return Long.valueOf(resultTemp.toString());
			}
		});
		if (1L == result.longValue()) {
			return true;
		}
		return false;
	}
public static void main(String[] args) {
	deleteByKeyPattern("/p/*");
}
}
