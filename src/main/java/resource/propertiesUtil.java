package resource;

import java.io.IOException;
import java.util.Properties;

public class propertiesUtil {
	static Properties ps=new Properties();
	static {
		try {
			ps.load(propertiesUtil.class.getResourceAsStream("application.properties"));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
	public static String getvalue(String property){
		return ps.getProperty(property);
	}
}
