package cis579.ai;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.antinori.astar.Location;
import org.antinori.game.Card;
import org.antinori.game.Player;

public class AiPlayerManager {
	
	private static final Map<Player, AiPlayer> aiPlayers = new HashMap<Player, AiPlayer>();
	
	public static void addPlayer(Player player) {
		if(!player.isComputerPlayer())
			return;
		
		System.out.println("Adding new AI player");
		aiPlayers.put(player, new RandomPlayer(player));
	}
	
	/**
	 * @return suggestion for the turn
	 */
	public static Solution getSuggestion(Player player) {
		Solution guess = aiPlayers.get(player).getSuggestion();
		System.out.println("Room = " + guess.room.toString() + "; Weapon = " + guess.weapon.toString() + "; Suspect = " + guess.suspect.toString());
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
	
	public static void noCardsToShow(Player player, ArrayList<Card> cards) {
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
		
		aiPlayers.get(player).onNoCardsToShow(suggestion);
	}
}
