package cis579.ai;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;

import org.antinori.game.Card;
import org.antinori.game.Player;

import cis579.ai.AiPlayerManager.PlayerType;
import cis579.ai.de.ResultDE;

public class ResultLogger {

	private static final long START_TIME = System.currentTimeMillis();
	
	private static final int MAX_RUNS = 500;
	private static int runs = 1;
	private static int turns = 1;
	
	private static final HashMap<String, Integer> WINS_PER_SUSPECT = new HashMap<String, Integer>();
	private static final HashMap<Integer, Integer> TURNS_PER_GAME = new HashMap<Integer, Integer>();
	
	private static final Database database = Database.getInstance();
	
	static {
		reset();
	}
	
	public static void nextTurn() {
		turns++;
		
	}
	
	public static boolean runAgain() {
		//return runs < MAX_RUNS)
		
		if(runs % MAX_RUNS == 0) {
			printResults();
			reset();
		}
		
		AiPlayerManager.reset();
		
		return true;
	}
	
	public static void reset() {
		WINS_PER_SUSPECT.put(Card.SCARLET_NAME, 0);
		WINS_PER_SUSPECT.put(Card.WHITE_NAME, 0);
		WINS_PER_SUSPECT.put(Card.PLUM_NAME, 0);
		WINS_PER_SUSPECT.put(Card.MUSTARD_NAME, 0);
		WINS_PER_SUSPECT.put(Card.PEACOCK_NAME, 0);
		WINS_PER_SUSPECT.put(Card.GREEN_NAME, 0);
		
		HeuristicPlayer.resetCoefficients();
	}
	
	public static void logResult(Player player, ArrayList<Card> accusation) {
		WINS_PER_SUSPECT.put(player.getSuspectName(), WINS_PER_SUSPECT.get(player.getSuspectName()) + 1);
		TURNS_PER_GAME.put(runs, turns);

		CardTracker.reset();
		
		double minutesElasped = (System.currentTimeMillis() - START_TIME) / 60000D;
		double gamesPerMinute = runs < 5 ? -1 : runs / minutesElasped;
		double remainingMinutes = gamesPerMinute == -1 ? -1 : (MAX_RUNS - (runs % MAX_RUNS)) / gamesPerMinute; 
		
		System.out.println("Game " + (runs % MAX_RUNS) + " of " + MAX_RUNS
				+ ". Games per minute = " + Math.round(gamesPerMinute)
				+ ". Remainging time = " + Math.round(remainingMinutes * 100D) / 100D + " min" );
		
		runs++;
		turns = 1;
	}
	
	public static int currentTurn() {
		return turns;
	}
	
	public static void wrongAccusation(Player player) {
		System.out.println(player.getSuspectName() + " made an invalid accusation");
	}
	
	public static void printResults() {
		
		System.out.println("=====================================================================================\n");
		
		String gameGuid = UUID.randomUUID().toString();
		
		for(Entry<String, Integer> entry : WINS_PER_SUSPECT.entrySet()) {
			String name = entry.getKey();
			double wins = entry.getValue().intValue();
			
			System.out.println(name + " wins " + wins);
			
			if(AiPlayerManager.getPlayerType(name) != PlayerType.HEURISTIC) {
				continue;
			}
			
			HeuristicPlayer aiPlayer = (HeuristicPlayer) AiPlayerManager.getPlayer(name);
			if(aiPlayer == null)
				continue; // not playing
			
			double[] heuristics = aiPlayer.getCoefficients();
			
			database.logCoefficientResult(new ResultDE(heuristics, wins / MAX_RUNS, gameGuid));
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
