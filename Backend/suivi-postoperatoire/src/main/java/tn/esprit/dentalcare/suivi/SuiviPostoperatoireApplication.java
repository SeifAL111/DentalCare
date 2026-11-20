package tn.esprit.dentalcare.suivi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class SuiviPostoperatoireApplication {

	public static void main(String[] args) {
		SpringApplication.run(SuiviPostoperatoireApplication.class, args);
	}

}
