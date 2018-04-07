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

    private ArrayList<Player> players = new ArrayList<Player>(6);

    private ArrayList<Card> shuffled = new ArrayList<Card>(TOTAL);
    private ArrayList<Card> victimSet = new ArrayList<Card>(3);

    private DealRequestHandler multiplayerDealer = null;

    public Clue() {

    }

    public void createDeck() {

        ArrayList<Card> deck = new ArrayList<Card>(TOTAL);

        //create deck
        for (int i = 0; i < NUM_ROOMS; i++) {
            deck.add(new Card(TYPE_ROOM, i));
        }
        for (int i = 0; i < NUM_SUSPECTS; i++) {
            deck.add(new Card(TYPE_SUSPECT, i));
        }
        for (int i = 0; i < NUM_WEAPONS; i++) {
            deck.add(new Card(TYPE_WEAPON, i));
        }

        // shuffle it
        int w, r, s;
        
        if(ResultLogger.SHUFFLE_DECK) {
	        Random rand = new Random(System.currentTimeMillis() % 389);
	        
	        for (int i = 0; i < TOTAL; i++) {
	            int a = rand.nextInt(deck.size());
	            Card c = deck.remove(a);
	            shuffled.add(c);
	        }
	
	        //pull the victim set
	        w = rand.nextInt(NUM_WEAPONS);
	        r = rand.nextInt(NUM_ROOMS);
	        s = rand.nextInt(NUM_SUSPECTS);
        } else {
        	// 6 + 9 + 6 = 21 
        	int[] order = { 2,20,18,19,17,8,1,7,12,14,9,15,16,0,13,6,5,10,11,3,4 }; 
        	
        	for(int i = 0; i < order.length; i++) {
        		shuffled.add(deck.get(order[i]));
        	}
        	
        	if(shuffled.size() != deck.size()) {
        		throw new RuntimeException("you shuffled the deck wrong");
        	}
        	
        	w = 1;
        	r = 1;
        	s = 1;
        }

        Card weapon = new Card(TYPE_WEAPON, w);
        Card suspect = new Card(TYPE_SUSPECT, s);
        Card room = new Card(TYPE_ROOM, r);

        shuffled.remove(weapon);
        shuffled.remove(suspect);
        shuffled.remove(room);

        victimSet.add(weapon);
        victimSet.add(suspect);
        victimSet.add(room);

    }

    public Player addPlayer(Card p, String name, Color color, boolean computer) {
        Player player = new Player(p, name, color, computer);
        players.add(player);
        return player;
    }

    public int getCurrentPlayerCount() {
        return players.size();
    }

    public boolean containsSuspect(Card card) {
        return players.contains(card);
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public Player getPlayer(int id) {
        Player player = null;
        for (Player p : players) {
            if (p.getSuspectNumber() == id) {
                player = p;
            }
        }
        return player;
    }

    public Player getPlayer(String name) {
        Player player = null;
        for (Player p : players) {
            if (p.getPlayerName().equals(name)) {
                player = p;
            }
        }
        return player;
    }

    public void setMultiplayerHandler(DealRequestHandler multiplayerDealer) {
        this.multiplayerDealer = multiplayerDealer;
    }

    public String dealShuffledDeck() throws Exception {

        if (shuffled == null || shuffled.isEmpty()) {
            throw new RuntimeException("Shuffled Deck is null.");
        }

        if (players == null || players.isEmpty()) {
            throw new RuntimeException("Players is null.");
        }

        //deal the cards
        int player_index = 0;
        for (int i = 0; i < shuffled.size(); i++) {
            Card card = shuffled.get(i);
            if (player_index == players.size()) {
                player_index = 0;
            }
            Player player = players.get(player_index);

            player.addCard(card);

            if (multiplayerDealer != null) {
                multiplayerDealer.dealCard(card, player);
            }

            player_index++;
        }

        String msg = "Cards have been dealt, and the players are:\n";
        for (int j = 0; j < players.size(); j++) {
            msg += players.get(j).toLongString() + "\n";
        }

        if (multiplayerDealer != null) {
            multiplayerDealer.getSet(players.get(0));
            multiplayerDealer.startTurn(players.get(0));
        }

        return msg;
    }

    public String getAdjacentPlayerName(String name) {
        String adjPlayerName = null;
        for (int i = 0; i < players.size(); i++) {
            Player p = players.get(i);
            if (p.getPlayerName().equals(name)) {
                int next = i + 1;
                if (next == players.size()) {
                    next = 0;
                }
                adjPlayerName = players.get(next).getPlayerName();
                break;
            }
        }
        return adjPlayerName;
    }

    public List<Card> getShuffledDeck() {
        return shuffled;
    }

    public boolean matchesVictimSet(ArrayList<Card> accusation) {
        Card weapon = null, suspect = null, room = null;
        for (Card card : accusation) {
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
        return matchesVictimSet(weapon, suspect, room);
    }

    public boolean matchesVictimSet(Card weapon, Card suspect, Card room) {
        if (victimSet.contains(weapon) && victimSet.contains(suspect) && victimSet.contains(room)) {
            return true;
        }
        return false;
    }

    public boolean matchesVictimSet(int w, int s, int r) {
        Card suspect = new Card(TYPE_SUSPECT, s);
        Card weapon = new Card(TYPE_WEAPON, w);
        Card room = new Card(TYPE_ROOM, r);
        if (victimSet.contains(weapon) && victimSet.contains(suspect) && victimSet.contains(room)) {
            return true;
        }
        return false;
    }

}
