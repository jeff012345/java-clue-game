package cis579.ai;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.antinori.game.Card;
import org.antinori.game.Player;

public class ResultLogger {

	private static final long START_TIME = System.currentTimeMillis();
	
	private static final int MAX_RUNS = 500;
	private static int runs = 0;
	private static int turns = 0;
	
	private static final HashMap<String, Integer> WINS_PER_SUSPECT = new HashMap<String, Integer>();
	private static final HashMap<Integer, Integer> TURNS_PER_GAME = new HashMap<Integer, Integer>();
	
	static {
		WINS_PER_SUSPECT.put(Card.SCARLET_NAME, 0);
		WINS_PER_SUSPECT.put(Card.WHITE_NAME, 0);
		WINS_PER_SUSPECT.put(Card.PLUM_NAME, 0);
		WINS_PER_SUSPECT.put(Card.MUSTARD_NAME, 0);
		WINS_PER_SUSPECT.put(Card.PEACOCK_NAME, 0);
		WINS_PER_SUSPECT.put(Card.GREEN_NAME, 0);
	}
	
	public static void nextTurn() {
		turns++;
	}
	
	public static boolean runAgain() {
		return runs < MAX_RUNS;
	}
	
	public static void logResult(Player player, ArrayList<Card> accusation) {
		runs++;
		
		WINS_PER_SUSPECT.put(player.getSuspectName(), WINS_PER_SUSPECT.get(player.getSuspectName()) + 1);
		TURNS_PER_GAME.put(runs, turns);

		CardTracker.reset();
		
		turns = 0;
		
		double minutesElasped = (System.currentTimeMillis() - START_TIME) / 60000D;
		double gamesPerMinute = runs < 5 ? -1 : runs / minutesElasped;
		double remainingMinutes = gamesPerMinute == -1 ? -1 : (MAX_RUNS - runs) / gamesPerMinute; 
		
		System.out.println("Game " + runs + " of " + MAX_RUNS
				+ ". Games per minute = " + Math.round(gamesPerMinute)
				+ ". Remainging time = " + Math.round(remainingMinutes * 100D) / 100D + " min" );
	}
	
	public static int currentTurn() {
		return turns;
	}
	
	public static void wrongAccusation(Player player) {
		System.out.println(player.getSuspectName() + " made an invalid accusation");
	}
	
	public static void printResults() {
		System.out.println("=====================================================================================\n");
		
		for(Entry<String, Integer> entry : WINS_PER_SUSPECT.entrySet()) {
			System.out.println(entry.getKey() + " wins " + entry.getValue());
		}
		
		int sum = 0;
		Integer minRound = null;
		Integer maxRound = null;
		int min = Integer.MAX_VALUE;
		int max = Integer.MIN_VALUE;
		
		for(Entry<Integer, Integer> entry : TURNS_PER_GAME.entrySet()) {
			int value = entry.getValue();
			sum += value;
			
			if(value < min) {
				min = value;
				minRound = entry.getKey();
			}
			
			if(value > max) {
				max = value;
				maxRound = entry.getKey();
			}
		}
		
		System.out.println("Average Turns = " + Math.round(sum / TURNS_PER_GAME.size()));
		System.out.println("Max Turns = " + max + " in round " + maxRound);
		System.out.println("Min Turns = " + min + " in round " + minRound);
		
		System.out.println("\n=====================================================================================");
	}
	
	
}
