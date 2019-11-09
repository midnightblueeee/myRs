package resource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.ValueOperations;

public class TieZi implements Resource, Serializable {
	private static final long serialVersionUID = 3911239764851790108L;
	String url;
	transient String title;
	transient String fit;
	static  final Logger logger = LoggerFactory.getLogger(TieZi.class);
	// 已解析信息
	static class TieZiYjxInfo implements Serializable {
		private static final long serialVersionUID = 3911265664851790108L;
		int dqys=1;
		int dqls;
		Date rq;
		public  byte[] serialize() {
		ObjectOutputStream oos = null;
	    ByteArrayOutputStream bos = null;
	    try {
	        bos = new ByteArrayOutputStream();
	        oos = new ObjectOutputStream(bos);
	        oos.writeObject(this);
	        byte[] b = bos.toByteArray();
	        return b;
	    } catch (IOException e) {
	    	logger.error("序列化失败 Exception:" + e.toString());
	        return null;
	    } finally {
	        try {
	            if (oos != null) {
	                oos.close();
	            }
	            if (bos != null) {
	                bos.close();
	            }
	        } catch (IOException ex) {
	            System.out.println("io could not close:" + ex.toString());
	        }
	    }
	}
		public static  TieZiYjxInfo deserialize(byte[] bytes) {
		    ByteArrayInputStream bais = null;
		    try {
		        // 反序列化
		        bais = new ByteArrayInputStream(bytes);
		        ObjectInputStream ois = new ObjectInputStream(bais);
		        TieZiYjxInfo rs= (TieZiYjxInfo) ois.readObject();
		        rs.flag=new AtomicBoolean(false); 
		        return rs;
		    } catch (IOException | ClassNotFoundException e) {
		        System.out.println("bytes Could not deserialize:" + e.toString());
		        return null;
		    } finally {
		        try {
		            if (bais != null) {
		                bais.close();
		            }
		        } catch (IOException ex) {
		            System.out.println("LogManage Could not serialize:" + ex.toString());
		        }
		    }
		}
	
		@Override
		public String toString() {
			return "已解析页数" + dqys;
		}

		// 已被使用true，否则else
		AtomicBoolean flag = new AtomicBoolean(false);

		public AtomicBoolean getFlag() {
			return flag;
		}

		public int getDqys() {
			return dqys;
		}

		public void setDqys(int dqys) {
			this.dqys = dqys;
		}

		public int getDqls() {
			return dqls;
		}

		public void setDqls(int dqls) {
			this.dqls = dqls;
		}

		public Date getRq() {
			return rq;
		}

		public void setRq(Date rq) {
			this.rq = rq;
		}

	}

	TieZi(String url, String title, String fit) {
		this.url = url;
		this.title = title;
		this.fit = fit;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj.getClass() != TieZi.class)
			return false;
		return ((TieZi) obj).url.equals(this.url);
	}

	@Override
	public int hashCode() {
		return url.hashCode();
	}

	// 记录每个帖子的当前解析页数
	static ConcurrentHashMap<TieZi, TieZiYjxInfo> dqysmap = new ConcurrentHashMap<>();

	static TieZiYjxInfo getYjxInfo(TieZi tz) {
		dqysmap.computeIfAbsent(tz, (x) -> {
			// 若map里没有值，则尝试从Redis里获取，获取失败则设置Redis初始化值
			@SuppressWarnings("unchecked")
			ValueOperations<TieZi, TieZiYjxInfo> op = TieBa.redisTemplate.opsForValue();
			TieZiYjxInfo rs = op.get(x);
			if (rs != null) {
				TieBa.logger.debug("redis获取缓存值" + x.title + "	" + rs);
				return rs;
			}
			rs = new TieZiYjxInfo();
			op.set(x, rs);
			return rs;
		});
		return dqysmap.get(tz);
	}

	static void updateYjxInfo(TieZi tz, TieZiYjxInfo inf) {
		@SuppressWarnings("unchecked")
		ValueOperations<TieZi, TieZiYjxInfo> op = TieBa.redisTemplate.opsForValue();
		op.set(tz, inf);
	}

	private Lou getLastLou(String url) {
		Document tzdoc = null;
		for (int i = 1; i < 5; i++) {
			try {
				tzdoc = Jsoup.connect(url).get();
				Elements lzs = tzdoc.select(".l_post");
				Element et = lzs.get(lzs.size() - 1);
				return (Lou) new Lou(et).parse();

			} catch (IOException e) {
				e.printStackTrace();
				continue;
			}

		}
		return null;

	}

	@Override
	public Object parse() {
		TieZiYjxInfo yjxInfo = getYjxInfo(this);
		AtomicBoolean fg = yjxInfo.getFlag();
		boolean rs = fg.compareAndSet(false, true);
		if (!rs)
			return null;
		Document tzdoc;
		try {
			tzdoc = Jsoup.connect("https://tieba.baidu.com/" + url).get();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		}
		logger.debug("解析帖子" + "https://tieba.baidu.com/" + url);
		// 最新页数
		int ys = Integer.parseInt(tzdoc.select("span.red").get(1).text());
		// 最后一楼
		Lou lastLou = getLastLou("https://tieba.baidu.com/" + url + "?pn=" + ys);
		//已解析楼数
		int resolvedLsNum=yjxInfo.getDqls();
		//最新楼数
		int newLs=lastLou.getLs();
		if(newLs<resolvedLsNum) {
			logger.info("最新楼数"+lastLou.getLs()+" 已解析"+yjxInfo.getDqls());
			return null;
		}
		
		yjxInfo.setDqls(newLs);
		int dqys = yjxInfo.getDqys();
		for (; dqys <= ys; dqys++) {
			try {
				tzdoc = Jsoup.connect("https://tieba.baidu.com/" + url + "?pn=" + dqys).get();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				continue;
			}
			logger.debug("当前页数 " + dqys + "总页数" + ys);
			Elements lzs = tzdoc.select(".l_post");
			for (int i = 0; i <= lzs.size() - 1; i++) {
				if (lzs.get(i).select(".post-tail-wrap").size() < 1)
					continue;
				Lou lz = new Lou(lzs.get(i));
				lz.parse();
				//当前楼小于已解析楼数
				if(lz.ls<resolvedLsNum)
					continue;
				int finaldqys=dqys;
				lz.fitter(fit, x->{
					String s = title + "   " + "https://tieba.baidu.com/" + url + "?pn=" + finaldqys + "   " + x.getLouzhu()
							+"	"+x.getLounr()+"	"+ x.getLs()+"	" +x.rq+ "\r\n";
					System.err.println(Thread.currentThread() + s);
					FileU.p(s);
				});			
				TieBa.a.incrementAndGet();
			}
		}
		yjxInfo.setRq(new Date());
		yjxInfo.setDqys(--dqys);
		updateYjxInfo(this, yjxInfo);
		if (!fg.compareAndSet(true, false))
			throw new RuntimeException("使用标记状态异常");
		return null;

	}
}
