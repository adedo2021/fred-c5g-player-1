package ie.game.fred.model;

import java.util.List;
import java.util.Stack;

public class ServerResponse {

	private List<Stack<C5Token>> board;
	private boolean winningConditionMet;
	private Player currentPlayer;
	private Player nextPlayer;
	private List<Player> players;
	private boolean myTurn;
	private boolean gameStarted;
	private boolean drawConditionMet;

	public ServerResponse() {
		super();
	}

	public List<Stack<C5Token>> getBoard() {
		return board;
	}

	public Player getCurrentPlayer() {
		return currentPlayer;
	}

	public Player getNextPlayer() {
		return nextPlayer;
	}

	public List<Player> getPlayers() {
		return players;
	}

	public boolean isDrawConditionMet() {
		return drawConditionMet;
	}

	public boolean isGameStarted() {
		return gameStarted;
	}

	public boolean isMyTurn() {
		return myTurn;
	}

	public boolean isWinningConditionMet() {
		return winningConditionMet;
	}

	public void setBoard(List<Stack<C5Token>> board) {
		this.board = board;
	}

	public void setCurrentPlayer(Player currentPlayer) {
		this.currentPlayer = currentPlayer;
	}

	public void setDrawConditionMet(boolean drawConditionMet) {
		this.drawConditionMet = drawConditionMet;
	}

	public void setGameStarted(boolean gameStarted) {
		this.gameStarted = gameStarted;
	}

	public void setMyTurn(boolean myTurn) {
		this.myTurn = myTurn;
	}

	public void setNextPlayer(Player nextPlayer) {
		this.nextPlayer = nextPlayer;
	}

	public void setPlayers(List<Player> players) {
		this.players = players;
	}

	public void setWinningConditionMet(boolean winningConditionMet) {
		this.winningConditionMet = winningConditionMet;
	}

}
