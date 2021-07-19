package ie.game.fred.model;

import java.io.Serializable;
import java.util.Objects;

public class Player implements Serializable {

	private static final long serialVersionUID = -564037031391014217L;

	private String name;
	private C5Token token;

	public Player() {

	}

	public Player(String name, C5Token token) {
		this.name = name;
		this.token = token;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if ((obj == null) || (getClass() != obj.getClass()))
			return false;
		Player other = (Player) obj;
		return Objects.equals(name, other.name) && token == other.token;
	}

	public C5Token getToken() {
		return token;
	}

	public String getName() {
		return name;
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, token);
	}

	public void setToken(C5Token token) {
		this.token = token;
	}

	public void setName(String name) {
		this.name = name;
	}

}
