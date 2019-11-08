package resource;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import org.apache.el.parser.AstInteger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;

import resource.TieZi.TieZiYjxInfo;

public class TieBa implements Resource {
	static AtomicInteger a= new AtomicInteger(0);
	static Random rd=new Random(System.currentTimeMillis());
	static ClassPathXmlApplicationContext context=new ClassPathXmlApplicationContext("resource/spring.xml");
	@SuppressWarnings("rawtypes")
	static org.springframework.data.redis.core.RedisTemplate redisTemplate=(RedisTemplate) context.getBean("redisTemplate");
	class AddTieZiRun implements Runnable {
		void tiebaTozt(Document doc) {
			Elements eles = doc.select(".threadlist_title a");
			eles.forEach(x -> {
				try {
					lq.put(new TieZi(x.attr("href"), x.attr("title"), fit));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			});
		}

		@Override
		public void run() {
			
			int r=propertiesUtil.getvalue("pageRang")==null?50:Integer.valueOf(propertiesUtil.getvalue("pageRang"));
			for (;;) {
				Document doc = null;
				try {
					String tieba=url+"&pn="+rd.nextInt(r);
					System.out.println(Thread.currentThread()+"获取贴吧 "+tieba);
					doc = Jsoup.connect(tieba).get();
				} catch (IOException e) {
					e.printStackTrace();
					continue;
				}
				tiebaTozt(doc);
			}
		}
	}

	class parseTieZi implements Runnable {
		@Override
		public void run() {
			for(;;) {
				TieZi tz=null;
			try {
				tz= lq.take();
			} catch (InterruptedException e) {
				e.printStackTrace();
				continue;
			}
			tz.parse();
		}
		}

	}
	public static ExecutorService ec = Executors.newCachedThreadPool();
	
	public static LinkedBlockingQueue<TieZi> lq = new LinkedBlockingQueue<>();
	private String tieBaName;
	private String url;
	private String fit;
	TieBa(String name, String url, String fit) {
		this.tieBaName = name;
		this.url = url;
		this.fit = fit;
	}

	TieBa(String url, String fit) {
		this.url = url;
		this.fit = fit;
	}

	public String getTbName() {
		return tieBaName;
	};

	public String getUrl() {
		return url;
	}

	@Override
	public Object parse() {
		String tbn = propertiesUtil.getvalue("TieBaToTzThreadNum");
		IntStream.rangeClosed(1, tbn == null ? 1 : Integer.valueOf(tbn)).forEach((x) -> ec.execute(new AddTieZiRun()));
		String tzi = propertiesUtil.getvalue("ParseThreadNum");
		long start=System.currentTimeMillis();
		IntStream.rangeClosed(1, tzi == null ? 1 : Integer.valueOf(tzi)).forEach((x) -> ec.execute(new parseTieZi()));
		ec.submit(()->{
			for(;;) {
				Thread.sleep(5000);
				TieZi.dqysmap.clear();
				long now=System.currentTimeMillis();
				System.err.println((now-start)/1000+"秒	"+a.get());
			}
		});
		return null;
	};
	public static void main(String[] args) throws UnsupportedEncodingException {
		context.getBean("redisTemplate");
		new TieBa("https://tieba.baidu.com/f?kw="+URLEncoder.encode("剑网三", "utf-8"),".*持烟.*").parse();
	}
}