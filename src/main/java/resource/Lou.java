package resource;
import java.util.function.Consumer;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
public class Lou implements Resource {
	String louzhu;
	String lounr;
	// 所在楼数
	int ls;
	// 日期
	String rq;
	Element element;

	Lou(Element element) {
		this.element = element;
	}

	public int getLs() {
		return ls;
	}

	public void setLs(int ls) {
		this.ls = ls;
	}

	public String getRq() {
		return rq;
	}

	public void setRq(String rq) {
		this.rq = rq;
	}

	public String getLouzhu() {
		return louzhu;
	}

	public void setLouzhu(String louzhu) {
		this.louzhu = louzhu;
	}

	public String getLounr() {
		return lounr;
	}

	public void setLounr(String lounr) {
		this.lounr = lounr;
	}

	public boolean fitter(String regex,Consumer<Lou> consumer) {
		if (louzhu.matches((regex)) || louzhu.matches(regex)) {
			consumer.accept(this);
			return true;
		}
		return false;
	}

	@Override
	public Object parse() {
		if (element.select(".post-tail-wrap").size() < 1)
			return null;
		Element root = element;
		louzhu = root.select("[alog-group=p_author]").get(0).text();
		lounr = root.select("cc").get(0).select(".d_post_content").text();
		//楼信息root	
		Element lInfoRoot = root.select(".post-tail-wrap").get(0);
		int len=lInfoRoot.childNodeSize();
		String strls=lInfoRoot.child(len-2).text();
		ls = Integer.parseInt(strls.substring(0,strls.length()-1));
		rq = lInfoRoot.child(len-1).text();
		return this;
	}

}
