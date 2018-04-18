package cis579.ai;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.antinori.astar.Location;
import org.antinori.game.Card;
import org.antinori.game.Player;

public class OneUnknownPlayer extends AiPlayer {

	private List<Card> knownWeapons;
	private List<Card> knownRooms;
	private List<Card> knownSuspects;

	private List<Card> weaponsInHand;
	private List<Card> roomsInHand;
	private List<Card> suspectsInHand;

	public OneUnknownPlayer(final Player player) {
		super(player);
	}

	@Override
	protected void init() {
		this.knownWeapons = new ArrayList<>(Card.NUM_WEAPONS);
		this.knownSuspects = new ArrayList<>(Card.NUM_SUSPECTS);
		this.knownRooms = new ArrayList<>(Card.NUM_ROOMS);

		this.weaponsInHand = new ArrayList<>(Card.NUM_WEAPONS);
		this.roomsInHand = new ArrayList<>(Card.NUM_SUSPECTS);
		this.suspectsInHand = new ArrayList<>(Card.NUM_ROOMS);

		super.init();
	}

	@Override
	public Solution getSuggestion() {
		final Solution solution = this.suggestionForCurrentRoom();

		if(this.unknownWeapons.size() > 1) {
			// choose first unknown weapon
			solution.suspect = this.suspectsInHand.isEmpty() ? this.unknownSuspects.stream().findFirst().get() : this.suspectsInHand.get(0);
			solution.weapon = this.unknownWeapons.stream().findFirst().get();
		} else if (this.unknownSuspects.size() > 1){
			// choose first unknown suspect
			solution.suspect = this.unknownSuspects.stream().findFirst().get();
			solution.weapon = this.weaponsInHand.isEmpty() ? this.unknownWeapons.stream().findFirst().get() : this.weaponsInHand.get(0);
		} else {
			// only unknown room
			solution.suspect = this.unknownSuspects.stream().findFirst().get();
			solution.weapon = this.unknownWeapons.stream().findFirst().get();
		}

		return solution;
	}

	@Override
	public void addCardToHand(final Card card) {
		super.addCardToHand(card);

		switch(card.getType()) {
		case Card.TYPE_ROOM:
			this.roomsInHand.add(card);
			break;
		case Card.TYPE_WEAPON:
			this.weaponsInHand.add(card);
			break;
		case Card.TYPE_SUSPECT:
			this.suspectsInHand.add(card);
			break;
		}
	}

	@Override
	public Solution canMakeAccusation() {
		if(this.unknownRooms.size() == 1 && this.unknownWeapons.size() == 1 && this.unknownSuspects.size() == 1) {
			final Solution solution = new Solution();
			solution.room = this.unknownRooms.stream().findFirst().get();
			solution.weapon = this.unknownWeapons.stream().findFirst().get();
			solution.suspect = this.unknownSuspects.stream().findFirst().get();
			return solution;
		}
		return null;
	}

	@Override
	public void onGameOver() {
	}

	@Override
	public void onShownCard(final Player showingPlayer, final List<Card> suggestion, final Card card) {
		super.onShownCard(showingPlayer, suggestion, card);

		switch(card.getType()) {
		case Card.TYPE_ROOM:
			this.knownRooms.add(card);
			break;
		case Card.TYPE_WEAPON:
			this.knownWeapons.add(card);
			break;
		case Card.TYPE_SUSPECT:
			this.knownSuspects.add(card);
			break;
		}
	}

	@Override
	public boolean shouldRoll() {
		if(this.player.isInARoom()) {
			return this.currentRoomIsKnown(); // if current room is known, then roll
		}
		return true;
	}

	@Override
	public void onAllPlayersNoCardsToShow(final Solution suggestion) {
		if(!this.knownRooms.contains(suggestion.room)) {
			this.unknownRooms.clear();
			this.unknownRooms.add(suggestion.room);
		}

		if(!this.knownSuspects.contains(suggestion.suspect)) {
			this.unknownSuspects.clear();
			this.unknownSuspects.add(suggestion.suspect);
		}

		if(!this.knownWeapons.contains(suggestion.weapon)) {
			this.unknownWeapons.clear();
			this.unknownWeapons.add(suggestion.weapon);
		}
	}

	@Override
	public void onPlayerNoCardsToShow(final Player showingPlayer, final List<Card> suggestion) {
		// ignore
	}

	@Override
	public Location decideLocation(final Collection<Location> choices) {
		final FilteredLocationChoices filteredChoices = this.filterChoices(choices);

		// check the secret passages and take it if the card is not known
		final Location shortcut = this.canTakeShortcut();
		if(shortcut != null) {
			final Card roomCard = shortcut.getRoomCard();
			if(!this.isCardKnown(roomCard)) {
				return shortcut;
			}
		}

		if(!filteredChoices.roomChoices.isEmpty()) {
			// going into a room is possible, so pick one if it's not already known
			for(final Location choice : filteredChoices.roomChoices) {
				if(!this.isCardKnown(choice.getRoomCard())) {
					// pick the first room that the player doesn't know
					return choice;
				}
			}

			// all possible rooms are known, so move somewhere else
		}

		return this.findClosestLocationToAnUnknownRoom(filteredChoices.otherChoices);
	}

}
