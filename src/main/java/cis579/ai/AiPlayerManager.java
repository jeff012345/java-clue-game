package cis579.ai;

import static org.antinori.game.Card.green;
import static org.antinori.game.Card.mustard;
import static org.antinori.game.Card.plum;
import static org.antinori.game.Card.scarlet;
import static org.antinori.game.Player.COLOR_WHITE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.antinori.astar.Location;
import org.antinori.game.Card;
import org.antinori.game.ClueMain;
import org.antinori.game.Player;

public class AiPlayerManager {

	private static final Map<Player, AiPlayer> aiPlayers = new HashMap<>();

	public enum PlayerType {
		HEURISTIC, RANDOM, ONE_UNKNOWN
	};

	private static final HashMap<Card, PlayerType> PLAYERS = new HashMap<>();

	static {
		reset();
	}

	public static void reset() {
		PLAYERS.clear();

		PLAYERS.put(scarlet, PlayerType.HEURISTIC);
		PLAYERS.put(plum, PlayerType.HEURISTIC);
		PLAYERS.put(green, PlayerType.HEURISTIC);
		PLAYERS.put(mustard, PlayerType.HEURISTIC);

		aiPlayers.clear();
	}

	public static PlayerType getPlayerType(final String name) {
		final AiPlayer aiPlayer = getPlayer(name);

		if(aiPlayer == null) {
			return null;
		}

		if(aiPlayer instanceof RandomPlayer)
			return PlayerType.RANDOM;

		if(aiPlayer instanceof HeuristicPlayer)
			return PlayerType.HEURISTIC;

		if(aiPlayer instanceof OneUnknownPlayer)
			return PlayerType.ONE_UNKNOWN;

		throw new RuntimeException("Player type not initialized: " + aiPlayer.getClass().getSimpleName());
	}

	public static AiPlayer getPlayer(final String name) {
		final Optional<Player> player = aiPlayers.keySet().stream().filter(p -> p.getSuspectName().equals(name)).findFirst();

		if(!player.isPresent()) {
			return null;
		}

		return aiPlayers.get(player.get());
	}

	public static void createAiPlayers() {
		for(final Card playerCard : PLAYERS.keySet()) {
			ClueMain.clue.addPlayer(playerCard, "", COLOR_WHITE, true);
		}
	}

	public static void addPlayer(final Player player) {
		if(!player.isComputerPlayer())
			return;

		final PlayerType type = PLAYERS.get(player.getPlayerCard());

		AiPlayer newAi = null;
		switch(type) {
		case HEURISTIC:
			newAi = new HeuristicPlayer(player);
			break;
		case ONE_UNKNOWN:
			newAi = new OneUnknownPlayer(player);
			break;
		case RANDOM:
			newAi = new RandomPlayer(player);
			break;
		default:
			throw new RuntimeException("Player type not implemened: " + type.toString());
		}

		aiPlayers.put(player, newAi);
	}

	/**
	 * @return suggestion for the turn
	 */
	public static Solution getSuggestion(final Player player) {
		final Solution guess = aiPlayers.get(player).getSuggestion();
		//System.out.println("Room = " + guess.room.toString() + "; Weapon = " + guess.weapon.toString() + "; Suspect = " + guess.suspect.toString());
		return guess;
	}

	/**
	 * 	@return returns a solution if it knows; otherwise, null.
	 */
	public static Solution canMakeAccusation(final Player player){
		return aiPlayers.get(player).canMakeAccusation();
	}

	/**
	 * 	@return returns a solution if it knows; otherwise, null.
	 */
	public static boolean shouldRoll(final Player player){
		return aiPlayers.get(player).shouldRoll();
	}

	/**
	 * @param choices
	 * @return Picks the best location based on the possible choices
	 */
	public static Location decideLocation(final Player player, final Collection<Location> choices) {
		return aiPlayers.get(player).decideLocation(choices);
	}

	/**
	 * Opponent does not have any cards in the suggestion
	 * @param player
	 * @param showingPlayer
	 * @param suggestion
	 */
	public static void onPlayerNoCardsToShow(final Player player, final Player showingPlayer, final ArrayList<Card> suggestion) {
		aiPlayers.get(player).onPlayerNoCardsToShow(showingPlayer, suggestion);
	}

	/**
	 * Opponent shows a card
	 * @param player
	 * @param showingPlayer
	 * @param suggestion
	 * @param shown
	 */
	public static void onShowCard(final Player player, final Player showingPlayer, final ArrayList<Card> suggestion, final Card shown) {
		aiPlayers.get(player).onShownCard(showingPlayer, suggestion, shown);
	}

	public static void allPlayerNoCardsToShow(final Player player, final ArrayList<Card> cards) {
		final Solution suggestion = new Solution();
		for(final Card c : cards) {
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
