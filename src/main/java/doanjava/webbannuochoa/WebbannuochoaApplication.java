package doanjava.webbannuochoa;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import vn.payos.PayOS;

@SpringBootApplication
public class WebbannuochoaApplication {

	// Đọc cấu hình từ application.properties
	@Value("${payos.clientId}")
	private String clientId;

	@Value("${payos.apiKey}")
	private String apiKey;

	@Value("${payos.checksumKey}")
	private String checksumKey;

	public static void main(String[] args) {
		SpringApplication.run(WebbannuochoaApplication.class, args);
	}

	// Định nghĩa Bean PayOS
	@Bean
	public PayOS payOS() {
		return new PayOS(clientId, apiKey, checksumKey);
	}
}
