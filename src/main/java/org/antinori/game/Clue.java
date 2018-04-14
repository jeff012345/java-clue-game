package org.antinori.game;

import static org.antinori.game.Card.NUM_ROOMS;
import static org.antinori.game.Card.NUM_SUSPECTS;
import static org.antinori.game.Card.NUM_WEAPONS;
import static org.antinori.game.Card.TOTAL;
import static org.antinori.game.Card.TYPE_ROOM;
import static org.antinori.game.Card.TYPE_SUSPECT;
import static org.antinori.game.Card.TYPE_WEAPON;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.antinori.multiplayer.DealRequestHandler;

import com.smartfoxserver.v2.entities.data.SFSObject;

import cis579.ai.ResultLogger;

public class Clue extends SFSObject {

	private ArrayList<Player> players = new ArrayList<>(6);

	private ArrayList<Card> shuffled = new ArrayList<>(TOTAL);
	private ArrayList<Card> victimSet = new ArrayList<>(3);

	private Player winner = null;

	private DealRequestHandler multiplayerDealer = null;

	public Clue() {

	}

	public void createDeck() {

		final ArrayList<Card> deck = new ArrayList<>(TOTAL);

		//create deck
		for (int i = 0; i < NUM_ROOMS; i++) {
			deck.add(Card.getInstance(TYPE_ROOM, i));
		}
		for (int i = 0; i < NUM_SUSPECTS; i++) {
			deck.add(Card.getInstance(TYPE_SUSPECT, i));
		}
		for (int i = 0; i < NUM_WEAPONS; i++) {
			deck.add(Card.getInstance(TYPE_WEAPON, i));
		}

		// shuffle it
		int w, r, s;

		if(ResultLogger.SHUFFLE_DECK) {
			final Random rand = new Random(System.currentTimeMillis() % 389);

			for (int i = 0; i < TOTAL; i++) {
				final int a = rand.nextInt(deck.size());
				final Card c = deck.remove(a);
				this.shuffled.add(c);
			}

			//pull the victim set
			w = rand.nextInt(NUM_WEAPONS);
			r = rand.nextInt(NUM_ROOMS);
			s = rand.nextInt(NUM_SUSPECTS);
		} else {
			// 6 + 9 + 6 = 21
			final int[] order = { 2,20,18,19,17,8,1,7,12,14,9,15,16,0,13,6,5,10,11,3,4 };

			for(int i = 0; i < order.length; i++) {
				this.shuffled.add(deck.get(order[i]));
			}

			if(this.shuffled.size() != deck.size()) {
				throw new RuntimeException("you shuffled the deck wrong");
			}

			w = 1;
			r = 1;
			s = 1;
		}

		final Card weapon = Card.getInstance(TYPE_WEAPON, w);
		final Card suspect = Card.getInstance(TYPE_SUSPECT, s);
		final Card room = Card.getInstance(TYPE_ROOM, r);

		this.shuffled.remove(weapon);
		this.shuffled.remove(suspect);
		this.shuffled.remove(room);

		this.victimSet.add(weapon);
		this.victimSet.add(suspect);
		this.victimSet.add(room);

	}

	public Player addPlayer(final Card p, final String name, final Color color, final boolean computer) {
		final Player player = new Player(p, name, color, computer);
		this.players.add(player);
		return player;
	}

	public int getCurrentPlayerCount() {
		return this.players.size();
	}

	public boolean containsSuspect(final Card card) {
		return this.players.contains(card);
	}

	public ArrayList<Player> getPlayers() {
		return this.players;
	}

	public Player getPlayer(final int id) {
		Player player = null;
		for (final Player p : this.players) {
			if (p.getSuspectNumber() == id) {
				player = p;
			}
		}
		return player;
	}

	public Player getPlayer(final String name) {
		Player player = null;
		for (final Player p : this.players) {
			if (p.getPlayerName().equals(name)) {
				player = p;
			}
		}
		return player;
	}

	public void setMultiplayerHandler(final DealRequestHandler multiplayerDealer) {
		this.multiplayerDealer = multiplayerDealer;
	}

	public String dealShuffledDeck() throws Exception {

		if (this.shuffled == null || this.shuffled.isEmpty()) {
			throw new RuntimeException("Shuffled Deck is null.");
		}

		if (this.players == null || this.players.isEmpty()) {
			throw new RuntimeException("Players is null.");
		}

		//deal the cards
		int player_index = 0;
		for (int i = 0; i < this.shuffled.size(); i++) {
			final Card card = this.shuffled.get(i);
			if (player_index == this.players.size()) {
				player_index = 0;
			}
			final Player player = this.players.get(player_index);

			player.addCard(card);

			if (this.multiplayerDealer != null) {
				this.multiplayerDealer.dealCard(card, player);
			}

			player_index++;
		}

		String msg = "Cards have been dealt, and the players are:\n";
		for (int j = 0; j < this.players.size(); j++) {
			msg += this.players.get(j).toLongString() + "\n";
		}

		if (this.multiplayerDealer != null) {
			this.multiplayerDealer.getSet(this.players.get(0));
			this.multiplayerDealer.startTurn(this.players.get(0));
		}

		return msg;
	}

	public String getAdjacentPlayerName(final String name) {
		String adjPlayerName = null;
		for (int i = 0; i < this.players.size(); i++) {
			final Player p = this.players.get(i);
			if (p.getPlayerName().equals(name)) {
				int next = i + 1;
				if (next == this.players.size()) {
					next = 0;
				}
				adjPlayerName = this.players.get(next).getPlayerName();
				break;
			}
		}
		return adjPlayerName;
	}

	public List<Card> getShuffledDeck() {
		return this.shuffled;
	}

	public boolean matchesVictimSet(final ArrayList<Card> accusation) {
		Card weapon = null, suspect = null, room = null;
		for (final Card card : accusation) {
			if (card.getType() == TYPE_WEAPON) {
				weapon = card;
			}
			if (card.getType() == TYPE_ROOM) {
				room = card;
			}
			if (card.getType() == TYPE_SUSPECT) {
				suspect = card;
			}
		}
		return this.matchesVictimSet(weapon, suspect, room);
	}

	public boolean matchesVictimSet(final Card weapon, final Card suspect, final Card room) {
		if (this.victimSet.contains(weapon) && this.victimSet.contains(suspect) && this.victimSet.contains(room)) {
			return true;
		}
		return false;
	}

	public boolean matchesVictimSet(final int w, final int s, final int r) {
		final Card suspect = Card.getInstance(TYPE_SUSPECT, s);
		final Card weapon = Card.getInstance(TYPE_WEAPON, w);
		final Card room = Card.getInstance(TYPE_ROOM, r);
		if (this.victimSet.contains(weapon) && this.victimSet.contains(suspect) && this.victimSet.contains(room)) {
			return true;
		}
		return false;
	}

	public ArrayList<Card> getVictimSet(){
		return this.victimSet;
	}

	public Player getWinner() {
		return this.winner;
	}

	public void setWinner(final Player player) {
		this.winner = player;
	}
}
