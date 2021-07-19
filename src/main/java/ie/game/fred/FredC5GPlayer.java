package ie.game.fred;

import java.io.IOException;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import ie.game.fred.service.ClientOperations;
import ie.game.fred.service.ConsoleMenuService;

public class FredC5GPlayer {

	public static void main(String[] args) throws IOException, InterruptedException {

		final CloseableHttpClient httpClient = HttpClients.createDefault();

		ClientOperations.handlePlayerDisconnection();
		ConsoleMenuService.welcomeMenu();
		ClientOperations.playGame(httpClient);

	}

}
