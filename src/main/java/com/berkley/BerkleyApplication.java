/**
 * Autores: Victor Krepsky e Bruna Schroeder
 */

package com.berkley;

import com.berkley.domain.Master;
import com.berkley.singleton.InstanceInfo;
import com.berkley.singleton.SchedulerState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Random;

@SpringBootApplication
@EnableScheduling
public class BerkleyApplication {

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private SchedulerState schedulerState;

	public static void main(String[] args) {
		SpringApplication.run(BerkleyApplication.class, args);
	}

	/**
	 * Método que será executado após o inicio da aplicação
	 * <p>
	 * Irá chamar um endpoint de alguma outra insância para verificar se já existe algum master,
	 * caso não há, a instância de ID 1 será o master
	 * <p>
	 * implementa um loop maximo de 5 retries
	 */
	@EventListener(ApplicationReadyEvent.class)
	public void afterInit() {
		Integer value = Integer.valueOf(System.getenv("ID"));
		InstanceInfo.getInstance().setInstanceId(value);
		Util.log("INICIADA INSTANCIA COM ID: " + value);

		int maxAttempts = 5;
		int attempt = 0;
		boolean success = false;

		ResponseEntity<Master> response = null;

		Random random = new Random();
		Boolean result = null;
		Integer valueToCall = null;
		int[] nodeSwitch;

		while (attempt < maxAttempts && !success) {
			try {
				nodeSwitch = Util.nodeSwitch();

				result = random.nextBoolean();

				// Randomiza qual instância será chamada ao bootar a aplicação
				// para evitar loops infinitos
				if (result) {
					valueToCall = nodeSwitch[0];
				} else {
					valueToCall = nodeSwitch[1];
				}

				String url = "http://app" + valueToCall + ":8080/master";

				Util.log("Tentativa " + (attempt + 1) + ": chamando " + url);

				response = restTemplate.getForEntity(url, Master.class);
				success = true;
			} catch (RestClientException e) {
				Util.log("Erro ao tentar conectar: " + e.getMessage());
				try {
					Thread.sleep(1000 * (long) Math.pow(2, attempt)); // Backoff exponencial
				} catch (InterruptedException ie) {
					Thread.currentThread().interrupt();
				}
				attempt++;
			}
		}

		if (success) {
			int masterInstanceId = response.getStatusCode() == HttpStatus.NO_CONTENT ? 1 : response.getBody().getId();
			InstanceInfo.getInstance().setMasterInstance(masterInstanceId);
			Util.log("Instancia : "+ masterInstanceId + " foi definida como mestre ao iniciar a aplicação");
		} else {
			Util.log(String.format("Falha após %d tentativas", maxAttempts));
			throw new RuntimeException("Falha ao tentar se conectar com outras instancias");
		}
		schedulerState.setInitialized(true);
	}
}
