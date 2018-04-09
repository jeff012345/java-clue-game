package cis579.ai;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.antinori.game.Card;
import org.antinori.game.Player;

import cis579.ai.AiPlayerManager.PlayerType;
import cis579.ai.de.ResultDE;

public class ResultLogger {

	private static final long START_TIME = System.currentTimeMillis();

	private static final int MAX_RUNS = Integer.MAX_VALUE;
	private static int runs = 1;
	private static int turns = 1;

	private static final HashMap<String, AtomicInteger> WINS_PER_SUSPECT = new HashMap<>();
	private static final HashMap<Integer, Integer> TURNS_PER_GAME = new HashMap<>();

	private static final Database database = Database.getInstance();

	public static final boolean SHUFFLE_DECK = false;

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
		WINS_PER_SUSPECT.put(Card.SCARLET_NAME, new AtomicInteger(0));
		WINS_PER_SUSPECT.put(Card.WHITE_NAME, new AtomicInteger(0));
		WINS_PER_SUSPECT.put(Card.PLUM_NAME, new AtomicInteger(0));
		WINS_PER_SUSPECT.put(Card.MUSTARD_NAME, new AtomicInteger(0));
		WINS_PER_SUSPECT.put(Card.PEACOCK_NAME, new AtomicInteger(0));
		WINS_PER_SUSPECT.put(Card.GREEN_NAME, new AtomicInteger(0));

		HeuristicPlayer.resetCoefficients();
	}

	public static void logResult(final Player player, final ArrayList<Card> accusation) {
		WINS_PER_SUSPECT.get(player.getSuspectName()).incrementAndGet();
		TURNS_PER_GAME.put(runs, turns);

		CardTracker.reset();

		final double minutesElasped = (System.currentTimeMillis() - START_TIME) / 60000D;
		final double gamesPerMinute = runs < 5 ? -1 : runs / minutesElasped;
		final double remainingMinutes = gamesPerMinute == -1 ? -1 : (MAX_RUNS - (runs % MAX_RUNS)) / gamesPerMinute;

		runs++;
		turns = 1;

		if(runs % 250 == 0) {
			System.out.println("Game " + (runs % MAX_RUNS) + " of " + MAX_RUNS
					+ ". Games per minute = " + Math.round(gamesPerMinute)
					+ ". Remainging time = " + Math.round(remainingMinutes * 100D) / 100D + " min" );

			System.out.println("Miss Scarlet win Percent = " + WINS_PER_SUSPECT.get(Card.SCARLET_NAME).get() / (double)runs);
			Evaluator.printTheta();
		}
	}

	public static int currentTurn() {
		return turns;
	}

	public static void wrongAccusation(final Player player) {
		System.out.println(player.getSuspectName() + " made an invalid accusation");
	}

	public static void printResults() {

		System.out.println("=====================================================================================\n");

		final String gameGuid = UUID.randomUUID().toString();

		for(final Entry<String, AtomicInteger> entry : WINS_PER_SUSPECT.entrySet()) {
			final String name = entry.getKey();

			if(AiPlayerManager.getPlayerType(name) != PlayerType.HEURISTIC) {
				continue;
			}

			final double wins = entry.getValue().get();
			//System.out.println(name + " wins " + wins);

			final HeuristicPlayer aiPlayer = (HeuristicPlayer) AiPlayerManager.getPlayer(name);
			if(aiPlayer == null)
				continue; // not playing

			final double[] heuristics = aiPlayer.getCoefficients();

			database.logCoefficientResult(new ResultDE(heuristics, wins / MAX_RUNS, gameGuid));
		}

		int sum = 0;
		Integer minRound = null;
		Integer maxRound = null;
		int min = Integer.MAX_VALUE;
		int max = Integer.MIN_VALUE;

		for(final Entry<Integer, Integer> entry : TURNS_PER_GAME.entrySet()) {
			final int value = entry.getValue();
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
