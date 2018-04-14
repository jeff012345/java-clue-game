package cis579.ai;

import java.util.Arrays;

import org.antinori.game.Card;
import org.antinori.game.ClueMain;

public class Evaluator {

	private static final double ALPHA = 0.05;
	private static final double GAMMA = 0.99;
	private static final double LAMBDA = 1.0;

	private static double[] THETA = new double[] { 2.8509083850878976, 3.4394153590656056, 1.3693469643784069, 6.370751479651988 };

	public static void updateQ(final double reward, final double[] previousSignals, final double[] maxSignals) {
		if(previousSignals == null || maxSignals == null)
			return;

		final double maxQ = evaluate(maxSignals);
		final double q = evaluate(previousSignals);
		final double z = ALPHA * (reward + (GAMMA * maxQ) - q);
		final double dt = z * q * (1 - q);

		THETA[0] = THETA[0] + dt * previousSignals[0];
		THETA[1] = THETA[1] + dt * previousSignals[1];
		THETA[2] = THETA[2] + dt * previousSignals[2];
		THETA[3] = THETA[3] + dt;
	}

	public static double reward(final Card card) {
		if(ClueMain.clue.getVictimSet().contains(card)) {
			return 0.9999999;
		}

		return 0;
	}

	/*
	public static double reward(final Solution solution) {
		final List<Card> victimSet = ClueMain.clue.getVictimSet();
		if(victimSet.contains(solution.room)
				&& victimSet.contains(solution.suspect)
				&& victimSet.contains(solution.weapon)) {
			return 0.9999999;
		}

		return 0;
	}
	 */

	public static double evaluate(final double[] signals) {
		final double sum = (THETA[0] * signals[0]) + (THETA[1] * signals[1]) + (THETA[2] * signals[2]) + LAMBDA * THETA[3];
		return 1 / (1 + Math.exp(-1 * sum));
	}

	public static void printTheta() {
		System.out.println("T0 = " + THETA[0] + "; T1 = " + THETA[1] + "; T2 = " + THETA[2] + "; T3 = " + THETA[3]);
	}

	public static double[] getThetas() {
		return Arrays.copyOf(THETA, THETA.length);
	}
}
