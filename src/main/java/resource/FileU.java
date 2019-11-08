package resource;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

public class FileU {
	static String fname = "D:\\tb.txt";
	static FileWriter fos;
	static {

		try {
			fos = new FileWriter(fname, true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
	}

	static synchronized void p(String s)  {
		try {
			fos.write(s);
			fos.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
