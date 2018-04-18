package cis579.ai;

import java.util.Arrays;

import org.antinori.game.ClueMain;
import org.antinori.game.Player;

public class Evaluator {

	private static final double ALPHA = 0.05;
	private static final double GAMMA = 0.99;
	private static final double LAMBDA = 0.0;

	// T0 = 2.9331381501643117; T1 = 4.568891700207492; T2 = 1.4004818852400833; T3 = 8.629261570017391
	// long running = 2.954352817910267	4.8783863956041005	1.4084074171327663	9.248173642315347
	//private static double[] THETA = new double[] { 2.954352817910267, 4.8783863956041005, 1.4084074171327663, 9.248173642315347};
	private static double[] THETA = new double[] { 0.2954352817910267, 0.48783863956041005, 0.14084074171327663, 0.9248173642315347};

	public static void updateQ(final double reward, final double[] previousSignals, final double[] maxSignals) {
		if(previousSignals == null)
			return;

		final double maxQ = maxSignals == null ? 1.0 : evaluate(maxSignals);
		final double q = evaluate(previousSignals);
		final double z = ALPHA * (reward + (GAMMA * maxQ) - q);


		final double dt = z * q * (1 - q);
		THETA[0] = THETA[0] + dt * previousSignals[0];
		THETA[1] = THETA[1] + dt * previousSignals[1];
		THETA[2] = THETA[2] + dt * previousSignals[2];
		THETA[3] = THETA[3] + dt;


		/*
		double temp = 1 / (1 + Math.exp(-1 * THETA[0] * previousSignals[0]));
		final double dt0 = temp * (1 - temp) * previousSignals[0];

		temp = 1 / (1 + Math.exp(-1 * THETA[1] * previousSignals[1]));
		final double dt1 = temp * (1 - temp) * previousSignals[1];

		temp = 1 / (1 + Math.exp(-1 * THETA[2] * previousSignals[2]));
		final double dt2 = temp * (1 - temp) * previousSignals[2];

		temp = 1 / (1 + Math.exp(-1 * THETA[3]));
		final double dt3 = temp * (1 - temp) ;

		THETA[0] = THETA[0] + z * dt0;
		THETA[1] = THETA[1] + z * dt1;
		THETA[2] = THETA[2] + z * dt2;
		THETA[3] = THETA[3] + z * dt3;
		 */
	}

	/*public static double reward(final Card card) {
		if(ClueMain.clue.getVictimSet().contains(card)) {
			return 0.9999999;
		}

		return 0;
	}*/

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

	public static double reward(final Player player) {
		final Player winner = ClueMain.clue.getWinner();
		if(winner == null) {
			return 0d;
		} else {
			return player.equals(winner) ? 1d : -1d;
		}
	}

	public static double evaluate(final double[] signals) {
		final double sum = (THETA[0] * signals[0]) + (THETA[1] * signals[1]) + (THETA[2] * signals[2]) + LAMBDA * THETA[3];
		final double denominator = 1.0 + Math.exp(-1.0 * sum);
		return (2.0 / denominator) - 1.0;
	}

	public static void printTheta() {
		System.out.println("T0 = " + THETA[0] + "; T1 = " + THETA[1] + "; T2 = " + THETA[2] + "; T3 = " + THETA[3]);
	}

	public static double[] getThetas() {
		return Arrays.copyOf(THETA, THETA.length);
	}
}
