package cis579.ai;

import org.antinori.game.ClueMain;
import org.antinori.game.Player;

public class Evaluator {

	private static final double ALPHA = 0.05;
	private static final double GAMMA = 0.99;
	private static final double LAMBDA = 1.0;

	private static double[] THETA = new double[] { 0.2, 0.5, 0.2, 0.5 };

	public static void updateQ(final Player player, final double[] previousSignals, final double[] maxSignals) {
		if(previousSignals == null || maxSignals == null)
			return;

		final double reward = reward(player);
		
		double maxQ = evaluate(maxSignals);
		double q = evaluate(previousSignals);
		double z = ALPHA * (reward + (GAMMA * maxQ) - q);
		double dt = z * q * (1 - q);
		
		THETA[0] = THETA[0] + dt * previousSignals[0];
		THETA[1] = THETA[1] + dt * previousSignals[1];
		THETA[2] = THETA[2] + dt * previousSignals[2];
		THETA[3] = THETA[3] + dt;
	}

	public static double reward(final Player player) {
		final Player winner = ClueMain.clue.getWinner();
		if(winner == null) {
			return 0;
		}

		if(player.equals(winner)) {
			return 1;
		}

		return -1;
	}

	public static double evaluate(final double[] signals) {
		final double sum = (THETA[0] * signals[0]) + (THETA[1] * signals[1]) + (THETA[2] * signals[2]) + LAMBDA * THETA[3];
		return 1 / (1 + Math.exp(-1 * sum));
	}

	public static void printTheta() {
		System.out.println("T0 = " + THETA[0] + "; T1 = " + THETA[1] + "; T2 = " + THETA[2] + "; T3 = " + THETA[3]);
	}
}
