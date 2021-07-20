package ie.game.fred.service;

import static ie.game.fred.util.UrlConstants.ADD_PLAYER;
import static ie.game.fred.util.UrlConstants.EXIT_GAME;
import static ie.game.fred.util.UrlConstants.GAME_STATUS;
import static ie.game.fred.util.UrlConstants.HTTP_LOCALHOST;
import static ie.game.fred.util.UrlConstants.PLAY_GAME;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import ie.game.fred.model.C5Token;
import ie.game.fred.model.ServerResponse;

public final class ClientOperations {

	static final String ADD_PLAYER_URL = HTTP_LOCALHOST + ADD_PLAYER;
	static final String GET_GAME_STATUS_URL = HTTP_LOCALHOST + GAME_STATUS;
	static final String SEND_A_MOVE_URL = HTTP_LOCALHOST + PLAY_GAME;
	static final String EXIT_GAME_URL = HTTP_LOCALHOST + EXIT_GAME;
	static final Integer WAITING_PERIOD_FOR_PLAYER_TO_MOVE = 5000;

	public static void addPlayer(final CloseableHttpClient httpClient, String name) throws IOException {

		var addPlayerRequest = new HttpPost(ADD_PLAYER_URL);

		List<NameValuePair> requestParams = new ArrayList<>();
		requestParams.add(new BasicNameValuePair("name", name));
		addPlayerRequest.setEntity(new UrlEncodedFormEntity(requestParams));

		try (CloseableHttpResponse response = httpClient.execute(addPlayerRequest)) {
			System.out.println(EntityUtils.toString(response.getEntity()));
		}
	}

	static void checkingWithServerForGameStart(final CloseableHttpClient httpClient, HttpGet getRequestGameStatus)
			throws IOException, InterruptedException {
		ServerResponse serverResponse;
		do {

			try (CloseableHttpResponse response = httpClient.execute(getRequestGameStatus)) {
				var mapper = new ObjectMapper();
				serverResponse = mapper.readValue(response.getEntity().getContent(),
						new TypeReference<ServerResponse>() {
						});

				if (serverResponse.getPlayers().size() == 2) {
					ConsoleMenuService.gameCanStartMenu();
				} else {
					Thread.sleep(WAITING_PERIOD_FOR_PLAYER_TO_MOVE);
					ConsoleMenuService.waitingForSecondPlayerMenu();
				}

			}

		} while (!serverResponse.isGameStarted());
	}

	public static void displayUpdatedGameBoard(List<Stack<C5Token>> board) {

		System.out.println();
		System.out.println("latest board update");
		System.out.println();

		for (var j = 5; j >= 0; j--) {

			for (var i = 0; i < 9; i++) {

				if (board.get(i).size() > j)
					System.out.print("[" + board.get(i).get(j) + "]");
				else
					System.out.print("[ ]");
			}
			System.out.println();
		}

	}

	public static ServerResponse getGameStatus(CloseableHttpClient httpClient, String gameStatusUrl)
			throws IOException {

		var getRequestGameStatus = new HttpGet(gameStatusUrl);
		ServerResponse serverResponse;
		try (CloseableHttpResponse response = httpClient.execute(getRequestGameStatus)) {

			var mapper = new ObjectMapper();
			serverResponse = mapper.readValue(response.getEntity().getContent(), new TypeReference<ServerResponse>() {
			});

		}
		return serverResponse;
	}

	public static void handlePlayerDisconnection() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				ConsoleMenuService.playerDisconnectedMenu();
			}
		});
	}

	public static boolean hasServerProcessedPlayerMoveSuccessfully(String position, CloseableHttpClient httpClient,
			String takeTurnurl) throws IOException {

		var takeTurnRequest = new HttpPost(takeTurnurl);

		List<NameValuePair> params = new ArrayList<>();
		params.add(new BasicNameValuePair("position", position));

		takeTurnRequest.setEntity(new UrlEncodedFormEntity(params));

		try (CloseableHttpResponse response = httpClient.execute(takeTurnRequest)) {
			return response.getStatusLine().getStatusCode() == HttpStatus.SC_OK;
		}

	}

	public static boolean isPlayerMoveWithinBoardLimits(String position, boolean isPositionValid,
			List<Stack<C5Token>> board) {

		try {
			Integer pos = Integer.parseInt(position);
			if (pos >= 1 && pos <= 9 && board.get(pos - 1).size() < 6) {
				isPositionValid = true;
			}

		} catch (NumberFormatException ex) {
			isPositionValid = false;
		}
		return isPositionValid;
	}

	public static void playGame(final CloseableHttpClient httpClient) throws IOException, InterruptedException {
		try (var reader = new BufferedReader(new InputStreamReader(System.in))) {

			String name = readName(reader);

			ClientOperations.addPlayer(httpClient, name);

			var getGameStatus = new HttpGet(GET_GAME_STATUS_URL);

			try {
				checkingWithServerForGameStart(httpClient, getGameStatus);

				ServerResponse serverResponse = null;
				var isWinningConditionMet = false;
				var isDrawConditionMet = false;

				do {
					serverResponse = ClientOperations.getGameStatus(httpClient, GET_GAME_STATUS_URL);

					if (serverResponse.isWinningConditionMet()) {
						ClientOperations.displayUpdatedGameBoard(serverResponse.getBoard());
						break;
					}

					if (serverResponse.isMyTurn()) {
						ClientOperations.displayUpdatedGameBoard(serverResponse.getBoard());

						serverResponse = sendTurnAndUpdateBoard(httpClient, reader, serverResponse);

						isWinningConditionMet = serverResponse.isWinningConditionMet();
						isDrawConditionMet = serverResponse.isDrawConditionMet();

					} else {
						waitForOtherPlayer(serverResponse);
					}

				} while (!isWinningConditionMet || !isDrawConditionMet);

				setGameResult(serverResponse, isWinningConditionMet, isDrawConditionMet);

			} finally {
				var exitGameCleanly = new HttpGet(EXIT_GAME_URL);
				try (CloseableHttpResponse response = httpClient.execute(exitGameCleanly)) {
				} finally {
					ConsoleMenuService.gameOverMenu();
				}
			}
		}
	}

	static String readName(BufferedReader reader) throws IOException {

		String name = reader.readLine();
		while (name == null || name.isBlank()) {

			System.out.println("name cannot be null.. try again");
			name = reader.readLine();
			if (name != null && !name.isBlank()) {
				break;
			}

		}
		return name;
	}

	static String promptForUserMove(BufferedReader reader, ServerResponse serverResponse) throws IOException {
		String position;
		ConsoleMenuService.makeAMoveMenu(serverResponse.getCurrentPlayer().getName());
		position = reader.readLine();
		return position;
	}

	static void sendPlayerMove(final CloseableHttpClient httpClient, BufferedReader reader,
			ServerResponse serverResponse) throws IOException {
		var isPositionValid = false;
		String position;
		var hasServerProcessedMove = false;
		do {
			position = promptForUserMove(reader, serverResponse);
			isPositionValid = ClientOperations.isPlayerMoveWithinBoardLimits(position, isPositionValid,
					serverResponse.getBoard());

			if (isPositionValid) {

				hasServerProcessedMove = ClientOperations.hasServerProcessedPlayerMoveSuccessfully(position, httpClient,
						SEND_A_MOVE_URL);

			}
		} while (!hasServerProcessedMove);
	}

	static ServerResponse sendTurnAndUpdateBoard(final CloseableHttpClient httpClient, BufferedReader reader,
			ServerResponse serverResponse) throws IOException {
		sendPlayerMove(httpClient, reader, serverResponse);
		serverResponse = ClientOperations.getGameStatus(httpClient, GET_GAME_STATUS_URL);
		ClientOperations.displayUpdatedGameBoard(serverResponse.getBoard());
		return serverResponse;
	}

	static void setGameResult(ServerResponse serverResponse, boolean isWinningConditionMet,
			boolean isDrawConditionMet) {
		if (isDrawConditionMet) {
			ConsoleMenuService.drawResultMenu();
		} else {
			setGameWinner(serverResponse, isWinningConditionMet);
		}
	}

	private static void setGameWinner(ServerResponse serverResponse, boolean isWinningConditionMet) {
		if (isWinningConditionMet && serverResponse.isMyTurn()) {
			ConsoleMenuService.gameWinnerMenu(serverResponse.getCurrentPlayer().getName());
		} else {
			ConsoleMenuService.gameWinnerMenu(serverResponse.getNextPlayer().getName());
		}
	}

	static void waitForOtherPlayer(ServerResponse serverResponse) throws InterruptedException {
		Thread.sleep(WAITING_PERIOD_FOR_PLAYER_TO_MOVE);
		ConsoleMenuService.waitForTheOtherPlayerMoveMenu(serverResponse.getCurrentPlayer().getName());
	}

	ClientOperations() {

	}
}
