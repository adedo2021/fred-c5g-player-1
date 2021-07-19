package ie.game.fred.model;

public enum C5Token {

	X('X'), O('O');

	private final char asChar;

	C5Token(char asChar) {
		this.asChar = asChar;
	}

	public char asChar() {
		return asChar;
	}

}
