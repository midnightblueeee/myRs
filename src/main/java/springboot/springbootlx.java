package springboot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
@Controller
@SpringBootApplication
public class springbootlx {
	@Value("${myboot.name}")
	private String p;

@RequestMapping("/")
public String test() {
	return "test";
}
public static void main(String[] args) {
	SpringApplication.run(springbootlx.class, args);
}
}
