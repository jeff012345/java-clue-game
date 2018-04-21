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
	private static int GAME_SPLIT = 2000;

	private static int runs = 1;
	private static int turns = 1;

	private static final HashMap<String, AtomicInteger> WINS_PER_SUSPECT = new HashMap<>();
	//private static final HashMap<Integer, Integer> TURNS_PER_GAME = new HashMap<>();

	private static final Database database = Database.getInstance();

	public static final boolean SHUFFLE_DECK = true;

	public static final boolean SELF_TRAINING = false;

	static {
		resetWins();
	}

	public static void nextTurn() {
		turns++;
	}

	public static boolean runAgain() {
		/*if(runs % MAX_RUNS == 0) {
			printResults();
			resetWins();
		}*/

		AiPlayerManager.reset();

		return true;
	}

	public static void resetWins() {
		WINS_PER_SUSPECT.clear();
		WINS_PER_SUSPECT.put(Card.SCARLET_NAME, new AtomicInteger(0));
		WINS_PER_SUSPECT.put(Card.WHITE_NAME, new AtomicInteger(0));
		WINS_PER_SUSPECT.put(Card.PLUM_NAME, new AtomicInteger(0));
		WINS_PER_SUSPECT.put(Card.MUSTARD_NAME, new AtomicInteger(0));
		WINS_PER_SUSPECT.put(Card.PEACOCK_NAME, new AtomicInteger(0));
		WINS_PER_SUSPECT.put(Card.GREEN_NAME, new AtomicInteger(0));
	}

	public static void logResult(final Player player, final ArrayList<Card> accusation) {
		WINS_PER_SUSPECT.get(player.getSuspectName()).incrementAndGet();

		CardTracker.reset();

		runs++;
		turns = 1;

		if(runs % GAME_SPLIT == 0) {
			final double minutesElasped = (System.currentTimeMillis() - START_TIME) / 60000d;
			final double gamesPerMinute = runs < 5 ? -1 : runs / minutesElasped;
			final double remainingMinutes = gamesPerMinute == -1 ? -1 : (MAX_RUNS - (runs % MAX_RUNS)) / gamesPerMinute;

			System.out.println("Game " + (runs % MAX_RUNS) + " of " + MAX_RUNS
					+ ". Games per minute = " + Math.round(gamesPerMinute)
					+ ". Remainging time = " + Math.round(remainingMinutes * 100D) / 100D + " min" );

			if(SELF_TRAINING) {
				logSelfTrainingResults();
			} else {
				logGenericResults();
			}

			resetWins();
		}
	}

	private static void logSelfTrainingResults() {
		String bestPlayer = null;
		int mostWins = -1;
		for(final Entry<String, AtomicInteger> entry : WINS_PER_SUSPECT.entrySet()) {
			if(entry.getValue().get() > mostWins) {
				mostWins = entry.getValue().get();
				bestPlayer = entry.getKey();
			}
		}

		final double winRatio = WINS_PER_SUSPECT.get(bestPlayer).get() / (double)GAME_SPLIT;

		System.out.println("Best Player " + bestPlayer + " with win Percent = " + winRatio);
		Evaluator.printBestTheta();

		final HeuristicPlayer aiPlayer = (HeuristicPlayer) AiPlayerManager.getPlayer(bestPlayer);
		Evaluator.updateBestThetas(aiPlayer.getThetas());

		database.logCoefficientResult(new ResultDE(Evaluator.copyBestThetas(), winRatio,
				"best player = " + bestPlayer + "; total runs " + runs, PlayerType.HEURISTIC));
	}

	private static void logGenericResults() {
		final String gameGuid = UUID.randomUUID().toString();

		final StringBuilder sb = new StringBuilder();

		for(final Entry<String, AtomicInteger> entry : WINS_PER_SUSPECT.entrySet()) {
			final String suspectName = entry.getKey();

			final PlayerType playerType = AiPlayerManager.getPlayerType(suspectName);
			if(playerType == null)
				continue; // not playing

			final double winRatio = entry.getValue().get() / (double)GAME_SPLIT;

			sb.append(suspectName + "(" + playerType.toString() + ") = " + (Math.round(winRatio * 100d) / 100d) + "; ");

			if(playerType == PlayerType.HEURISTIC) {
				final HeuristicPlayer aiPlayer = (HeuristicPlayer) AiPlayerManager.getPlayer(suspectName);
				final double[] heuristics = aiPlayer.getThetas();

				database.logCoefficientResult(new ResultDE(heuristics, winRatio, gameGuid, PlayerType.HEURISTIC));

			} else if(playerType == PlayerType.TRAINED_AI) {
				database.logCoefficientResult(new ResultDE(TrainedAi.THETAS, winRatio, gameGuid, PlayerType.TRAINED_AI));

			} else if(playerType == PlayerType.ONE_UNKNOWN) {
				database.logCoefficientResult(new ResultDE(new double[3], winRatio, gameGuid, PlayerType.ONE_UNKNOWN));
			}
		}

		System.out.println(sb.toString());
	}

	public static int currentTurn() {
		return turns;
	}

	public static void wrongAccusation(final Player player) {
		System.out.println(player.getSuspectName() + " made an invalid accusation");
	}

	/*
	public static void printResults() {

		System.out.println("=====================================================================================\n");

		final String gameGuid = UUID.randomUUID().toString();

		for(final Entry<String, AtomicInteger> entry : WINS_PER_SUSPECT.entrySet()) {
			final String name = entry.getKey();

			final double winRatio = entry.getValue().get() / (double)MAX_RUNS;

			final PlayerType playerType = AiPlayerManager.getPlayerType(name);

			if(playerType == PlayerType.HEURISTIC) {
				final HeuristicPlayer aiPlayer = (HeuristicPlayer) AiPlayerManager.getPlayer(name);
				if(aiPlayer == null)
					continue; // not playing

				final double[] heuristics = aiPlayer.getThetas();

				database.logCoefficientResult(new ResultDE(heuristics, winRatio, gameGuid, PlayerType.HEURISTIC));

			} else if(playerType == PlayerType.TRAINED_AI) {
				database.logCoefficientResult(new ResultDE(TrainedAi.THETAS, winRatio, gameGuid, PlayerType.TRAINED_AI));

			} else if(playerType == PlayerType.ONE_UNKNOWN) {
				database.logCoefficientResult(new ResultDE(new double[3], winRatio, gameGuid, PlayerType.ONE_UNKNOWN));
			}

		}

		System.out.println("\n=====================================================================================");
	}
	 */

}
