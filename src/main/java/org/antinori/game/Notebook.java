package org.antinori.game;

import static org.antinori.game.Card.NUM_ROOMS;
import static org.antinori.game.Card.NUM_SUSPECTS;
import static org.antinori.game.Card.NUM_WEAPONS;
import static org.antinori.game.Card.TOTAL;
import static org.antinori.game.Card.TYPE_ROOM;
import static org.antinori.game.Card.TYPE_SUSPECT;
import static org.antinori.game.Card.TYPE_WEAPON;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Random;

import org.antinori.astar.Location;

import com.smartfoxserver.v2.protocol.serialization.SerializableSFSType;

import cis579.ai.AiPlayerManager;
import cis579.ai.Solution;

public class Notebook implements SerializableSFSType {

	private Player player;
	private LinkedHashMap<Card, Entry> entries = new LinkedHashMap<>();

	public Notebook(final Player player) {
		this.setPlayer(player);

		final ArrayList<Card> deck = new ArrayList<>(TOTAL);
		for (int i = 0; i < NUM_SUSPECTS; i++) {
			deck.add(Card.getInstance(TYPE_SUSPECT, i));
		}
		for (int i = 0; i < NUM_WEAPONS; i++) {
			deck.add(Card.getInstance(TYPE_WEAPON, i));
		}
		for (int i = 0; i < NUM_ROOMS; i++) {
			deck.add(Card.getInstance(TYPE_ROOM, i));
		}

		for (final Card card : deck) {
			this.entries.put(card, new Entry(card));
		}

		//set cards in hand
		for (final Card card : player.getCardsInHand()) {
			final Entry entry = this.entries.get(card);
			entry.setInHand(true);
		}
	}

	public void setToggled(final Card card) {
		final Entry entry = this.entries.get(card);
		entry.setToggled(!entry.getToggled());
	}

	public boolean isCardInHand(final Card card) {
		final Entry entry = this.entries.get(card);
		return entry.inHand();
	}

	public boolean isCardToggled(final Card card) {
		final Entry entry = this.entries.get(card);
		return entry.getToggled();
	}

	public boolean isLocationCardInHandOrToggled(final Location location) {
		final Card roomCard = location.getRoomCard();
		return this.isLocationCardInHandOrToggled(roomCard);
	}

	public boolean isLocationCardInHandOrToggled(final Card card) {
		if (this.isCardInHand(card) || this.isCardToggled(card)) {
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		String text = this.getPlayer().toString() + "'s Notebook :";
		for (final Entry entry : this.entries.values()) {
			text += entry.toString();
		}
		return text;
	}

	public ArrayList<Card> canMakeAccusation() {

		ArrayList<Card> accusation = new ArrayList<>();

		// check if AI player can make an accusation
		if(this.player.isComputerPlayer()) {
			final Solution s = AiPlayerManager.canMakeAccusation(this.player);
			if(s == null) {
				return null;
			}

			accusation.add(s.suspect);
			accusation.add(s.room);
			accusation.add(s.weapon);

			return accusation;
		}

		int count = 0;
		for (int i = 0; i < NUM_SUSPECTS; i++) {
			final Card card = Card.getInstance(TYPE_SUSPECT, i);
			if (!this.isCardInHand(card) && !this.isCardToggled(card)) {
				count++;
			}
		}

		if (count == 1) {
			for (int i = 0; i < NUM_SUSPECTS; i++) {
				final Card card = Card.getInstance(TYPE_SUSPECT, i);
				if (!this.isCardInHand(card) && !this.isCardToggled(card)) {
					accusation.add(card);
				}
			}
		}

		count = 0;
		for (int i = 0; i < NUM_WEAPONS; i++) {
			final Card card = Card.getInstance(TYPE_WEAPON, i);
			if (!this.isCardInHand(card) && !this.isCardToggled(card)) {
				count++;
			}
		}

		if (count == 1) {
			for (int i = 0; i < NUM_WEAPONS; i++) {
				final Card card = Card.getInstance(TYPE_WEAPON, i);
				if (!this.isCardInHand(card) && !this.isCardToggled(card)) {
					accusation.add(card);
				}
			}
		}

		count = 0;
		for (int i = 0; i < NUM_ROOMS; i++) {
			final Card card = Card.getInstance(TYPE_ROOM, i);
			if (!this.isCardInHand(card) && !this.isCardToggled(card)) {
				count++;
			}
		}

		if (count == 1) {
			for (int i = 0; i < NUM_ROOMS; i++) {
				final Card card = Card.getInstance(TYPE_ROOM, i);
				if (!this.isCardInHand(card) && !this.isCardToggled(card)) {
					accusation.add(card);
				}
			}
		}

		if (accusation.size() != 3) {
			accusation = null;
		}

		return accusation;
	}

	public Card randomlyPickCardOfType(final int type) {

		//select a card of indicated type and check the cards in your hand
		Card picked_card = null;
		final ArrayList<Card> picks = new ArrayList<>();

		int total = 0;
		if (type == TYPE_SUSPECT) {
			total = NUM_SUSPECTS;
		}
		if (type == TYPE_ROOM) {
			total = NUM_ROOMS;
		}
		if (type == TYPE_WEAPON) {
			total = NUM_WEAPONS;
		}

		for (int i = 0; i < total; i++) {
			final Card card = Card.getInstance(type, i);
			if (this.isCardInHand(card) || this.isCardToggled(card)) {
				continue;
			}
			picks.add(card);
		}

		if (picks.size() > 1) {
			final int r = new Random().nextInt(picks.size());
			picked_card = picks.get(r);
		} else if (picks.size() == 1) {
			picked_card = picks.get(0);
		} else {
			//just return a random card of this type
			final int r = new Random().nextInt(total);
			picked_card = Card.getInstance(type, r);
		}

		return picked_card;
	}

	public Player getPlayer() {
		return this.player;
	}

	public void setPlayer(final Player player) {
		this.player = player;
	}

	public static class Entry implements SerializableSFSType {

		Card value;
		boolean inHand = false;
		boolean toggled = false;

		Entry(final Card value) {
			this.value = value;
		}

		boolean inHand() {
			return this.inHand;
		}

		void setInHand(final boolean inHand) {
			this.inHand = inHand;
		}

		boolean getToggled() {
			return this.toggled;
		}

		void setToggled(final boolean toggled) {
			this.toggled = toggled;
		}

		String getValue() {
			return this.value.toString();
		}

		@Override
		public String toString() {
			return "[" + this.value.toString() + " inHand:" + this.inHand + " toggled:" + this.toggled + "] ";
		}

	}

}
