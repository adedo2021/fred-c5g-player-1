package ie.game.fred.service;

public class ConsoleMenuService {

	public static void drawResultMenu() {
		System.out.println(" The game ends in a draw");
	}

	public static void gameCanStartMenu() {
		System.out.println("Now both players in the game. Let us start playing.");
	}

	public static void gameOverMenu() {
		System.out.println("Game over");
	}

	public static void gameWinnerMenu(String winnerName) {
		System.out.println(winnerName + " wins the game");
	}

	public static void resetClientDataOnServerMenu() {
		System.out.println("Reset client data on server");
	}

	public static void makeAMoveMenu(String playerName) {
		System.out.println();
		System.out.println(playerName + " , Please enter a position between 1-9 to take a turn. ");
		System.out.println("Remember only 9 token per rows and 6 per column: ");
	}

	public static void playerDisconnectedMenu() {
		System.out.println(" A player has disconnected, game is now over.");
	}

	public static void waitForTheOtherPlayerMoveMenu(String otherPlayerName) {
		System.out.println(" Please wait while your partner " + otherPlayerName + " takes a turn");
	}

	public static void waitingForSecondPlayerMenu() {
		System.out.println("Waiting for another player to join.");
	}

	public static void welcomeMenu() {
		System.out.println("Welcome to Fred Game of connect-5");
		System.out.println("Please enter your user name : ");
	}

	private ConsoleMenuService() {

	}
}
