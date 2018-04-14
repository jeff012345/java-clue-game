package org.antinori.game;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.antinori.astar.Location;

import com.smartfoxserver.v2.protocol.serialization.SerializableSFSType;

public class Player implements SerializableSFSType {

	private int suspectNumber = 0;
	private String suspectName = null;
	private String playerName = "";
	private Card playerCard = null;

	private ArrayList<Card> cardsInHand = new ArrayList<>();
	private ArrayList<Card> weapons = new ArrayList<>();
	private ArrayList<Card> rooms = new ArrayList<>();
	private ArrayList<Card> suspects = new ArrayList<>();

	private boolean computerPlayer = false;
	private Location playerLocation = null;
	private Notebook notebook = null;
	private Color playerColor = Color.gray;
	private boolean hasMadeFalseAccusation = false;

	public static final Color COLOR_SCARLET = Color.red;
	public static final Color COLOR_GREEN = Color.green;
	public static final Color COLOR_MUSTARD = Color.yellow;
	public static final Color COLOR_PLUM = Color.magenta;
	public static final Color COLOR_WHITE = Color.black;
	public static final Color COLOR_PEACOCK = Color.blue;

	public Player() {

	}

	public Player(final Card pick, final String name, final Color color, final boolean computer) {
		this.setPlayerName(name);
		this.setPlayerCard(pick);
		this.setSuspectNumber(pick.getValue());
		this.setSuspectName(this.getPlayerCard().toString());
		this.setComputerPlayer(computer);
		this.setPlayerColor(color);
	}

	public void setLocation(final Location location) {
		this.playerLocation = location;
	}

	public Location getLocation() {
		return this.playerLocation;
	}

	public void setNotebook(final Notebook notebook) {
		this.notebook = notebook;
	}

	public Notebook getNotebook() {
		return this.notebook;
	}

	public void addCard(final Card card) {
		this.cardsInHand.add(card);

		switch(card.getType()) {
		case Card.TYPE_ROOM:
			this.rooms.add(card);
			break;
		case Card.TYPE_WEAPON:
			this.weapons.add(card);
			break;
		case Card.TYPE_SUSPECT:
			this.suspects.add(card);
			break;
		}
	}

	public ArrayList<Card> getCardsInHand() {
		return this.cardsInHand;
	}

	public List<Card> getRooms(){
		return this.rooms;
	}

	public List<Card> getWeapons(){
		return this.rooms;
	}

	public List<Card> getSuspects(){
		return this.rooms;
	}

	public boolean isCardInHand(final Card card) {
		return this.cardsInHand.contains(card);
	}

	public boolean isCardInHand(final int type, final int id) {
		final Card card = Card.getInstance(type, id);
		return this.cardsInHand.contains(card);
	}

	public boolean isHoldingCardInSuggestion(final ArrayList<Card> suggestion) {
		boolean hasCards = false;
		for (final Card card : this.cardsInHand) {
			if (suggestion.contains(card)) {
				hasCards = true;
			}
		}
		return hasCards;
	}

	@Override
	public String toString() {
		return this.getPlayerCard().toString();
	}

	public String toLongString() {
		final String location = (this.playerLocation != null && this.playerLocation.getRoomId() != -1 ? "in the " + this.playerLocation.getRoomCard().toString() : "outside of a room");
		return this.getPlayerCard().toString() + ", played by " + (this.isComputerPlayer() ? "computer" : this.getPlayerName()) + " is currently " + location + ".";
	}

	public String getSuspectName() {
		return this.suspectName;
	}

	public void setSuspectName(final String suspectName) {
		this.suspectName = suspectName;
	}

	public Card getPlayerCard() {
		return this.playerCard;
	}

	public void setPlayerCard(final Card playerCard) {
		this.playerCard = playerCard;
	}

	public Color getPlayerColor() {
		return this.playerColor;
	}

	public void setPlayerColor(final Color playerColor) {
		this.playerColor = playerColor;
	}

	public String getPlayerName() {
		return this.playerName;
	}

	public void setPlayerName(final String playerName) {
		this.playerName = playerName;
	}

	public int getSuspectNumber() {
		return this.suspectNumber;
	}

	public void setSuspectNumber(final int suspectNumber) {
		this.suspectNumber = suspectNumber;
	}

	public boolean isComputerPlayer() {
		return this.computerPlayer;
	}

	public void setComputerPlayer(final boolean computerPlayer) {
		this.computerPlayer = computerPlayer;
	}

	public boolean hasMadeFalseAccusation() {
		return this.hasMadeFalseAccusation;
	}

	public void setHasMadeFalseAccusation() {
		this.hasMadeFalseAccusation = true;
	}

	public boolean isInARoom() {
		return this.playerLocation.getRoomId() != -1;
	}

	/*@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cardsInHand == null) ? 0 : cardsInHand.hashCode());
		result = prime * result + (computerPlayer ? 1231 : 1237);
		result = prime * result + (hasMadeFalseAccusation ? 1231 : 1237);
		result = prime * result + ((notebook == null) ? 0 : notebook.hashCode());
		result = prime * result + ((playerCard == null) ? 0 : playerCard.hashCode());
		result = prime * result + ((playerColor == null) ? 0 : playerColor.hashCode());
		result = prime * result + ((playerLocation == null) ? 0 : playerLocation.hashCode());
		result = prime * result + ((playerName == null) ? 0 : playerName.hashCode());
		result = prime * result + ((rooms == null) ? 0 : rooms.hashCode());
		result = prime * result + ((suspectName == null) ? 0 : suspectName.hashCode());
		result = prime * result + suspectNumber;
		result = prime * result + ((suspects == null) ? 0 : suspects.hashCode());
		result = prime * result + ((weapons == null) ? 0 : weapons.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Player other = (Player) obj;

		return this.suspectName.equals(other.suspectName);
	}*/

}
