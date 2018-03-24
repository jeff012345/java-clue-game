package cis579.ai;

import static org.antinori.game.Card.green;
import static org.antinori.game.Card.mustard;
import static org.antinori.game.Card.peacock;
import static org.antinori.game.Card.plum;
import static org.antinori.game.Card.scarlet;
import static org.antinori.game.Card.white;
import static org.antinori.game.Player.COLOR_GREEN;
import static org.antinori.game.Player.COLOR_MUSTARD;
import static org.antinori.game.Player.COLOR_PEACOCK;
import static org.antinori.game.Player.COLOR_PLUM;
import static org.antinori.game.Player.COLOR_SCARLET;
import static org.antinori.game.Player.COLOR_WHITE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.antinori.astar.Location;
import org.antinori.game.Card;
import org.antinori.game.ClueMain;
import org.antinori.game.Player;

public class AiPlayerManager {
	
	private static final Map<Player, AiPlayer> aiPlayers = new HashMap<Player, AiPlayer>();
	
	public enum PlayerType {
		HEURISTIC, RANDOM
	};
	
	public static PlayerType getPlayerType(String name) {
		Optional<Player> player = aiPlayers.keySet().stream().filter(p -> p.getSuspectName().equals(name)).findFirst();
		
		if(!player.isPresent()) {
			return null;
		}
		
		AiPlayer aiPlayer = aiPlayers.get(player.get());
		
		if(aiPlayer instanceof RandomPlayer)
			return PlayerType.RANDOM;
		
		if(aiPlayer instanceof HeuristicPlayer)
			return PlayerType.HEURISTIC;
		
		return null;
	}
	
	public static void createAiPlayers() {
		int players = 4;
		
        ClueMain.clue.addPlayer(scarlet, "", COLOR_SCARLET, true);
        ClueMain.clue.addPlayer(green, "", COLOR_GREEN, true);
        ClueMain.clue.addPlayer(mustard, "", COLOR_MUSTARD, true);
        
        if(players > 3)
        	ClueMain.clue.addPlayer(plum, "", COLOR_PLUM, true);
        
        if(players > 4)
        	ClueMain.clue.addPlayer(peacock, "", COLOR_PEACOCK, true);
        
        if(players == 6)
        	ClueMain.clue.addPlayer(white, "", COLOR_WHITE, true);
	}
	
	public static void addPlayer(Player player) {
		if(!player.isComputerPlayer())
			return;

		if(player.getSuspectName().equals(Card.SCARLET_NAME) || player.getSuspectName().equals(Card.MUSTARD_NAME) 
				|| player.getSuspectName().equals(Card.PEACOCK_NAME)) {
			aiPlayers.put(player, new HeuristicPlayer(player));
		} else {
			aiPlayers.put(player, new RandomPlayer(player));
		}
	}
	
	/**
	 * @return suggestion for the turn
	 */
	public static Solution getSuggestion(Player player) {
		Solution guess = aiPlayers.get(player).getSuggestion();
		//System.out.println("Room = " + guess.room.toString() + "; Weapon = " + guess.weapon.toString() + "; Suspect = " + guess.suspect.toString());
		return guess;
	}
	
	/**
	 * 	@return returns a solution if it knows; otherwise, null.
	 */
	public static Solution canMakeAccusation(Player player){
		return aiPlayers.get(player).canMakeAccusation();
	}
	
	/**
	 * 	@return returns a solution if it knows; otherwise, null.
	 */
	public static boolean shouldRoll(Player player){
		return aiPlayers.get(player).shouldRoll();
	}

	/**
	 * @param choices
	 * @return Picks the best location based on the possible choices
	 */
	public static Location decideLocation(Player player, ArrayList<Location> choices) {
		return aiPlayers.get(player).decideLocation(choices);
	}
	
	/**
	 * Opponent does not have any cards in the suggestion
	 * @param player
	 * @param showingPlayer
	 * @param suggestion
	 */
	public static void onPlayerNoCardsToShow(Player player, Player showingPlayer, ArrayList<Card> suggestion) {
		aiPlayers.get(player).onPlayerNoCardsToShow(showingPlayer, suggestion);
	}
	
	/**
	 * Opponent shows a card
	 * @param player
	 * @param showingPlayer
	 * @param suggestion
	 * @param shown
	 */
	public static void onShowCard(Player player, Player showingPlayer, ArrayList<Card> suggestion, Card shown) {
		aiPlayers.get(player).onShownCard(showingPlayer, suggestion, shown);
	}
	
	public static void allPlayerNoCardsToShow(Player player, ArrayList<Card> cards) {
		Solution suggestion = new Solution();
		for(Card c : cards) {
			switch(c.getType()) {
	        case Card.TYPE_ROOM:
	        	suggestion.room = c;
	        	continue;
	        case Card.TYPE_WEAPON:
	        	suggestion.weapon = c;
	        	continue;
	        case Card.TYPE_SUSPECT:
	        	suggestion.suspect = c;
	        	continue;
	        }
		}
		
		aiPlayers.get(player).onAllPlayersNoCardsToShow(suggestion);
	}
}
