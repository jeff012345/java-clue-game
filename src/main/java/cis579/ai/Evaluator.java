package cis579.ai;

import java.util.Arrays;

import org.antinori.game.ClueMain;
import org.antinori.game.Player;

public class Evaluator {

	private static final double ALPHA = 0.05;
	private static final double GAMMA = 0.99;
	private static final double LAMBDA = 1.0;

	// T0 = 2.9331381501643117; T1 = 4.568891700207492; T2 = 1.4004818852400833; T3 = 8.629261570017391
	// long running = 2.954352817910267	4.8783863956041005	1.4084074171327663	9.248173642315347
	// longer running 4/17 T0 = 1.2771164328633122; T1 = 3.2279713197674047; T2 = 1.3851975715541371; T3 = 6.4047055970723
	//                     T0 = 1.2774181852249895; T1 = 3.231393428925488;  T2 = 1.3872025915598771; T3 = 6.41156013454332
	//                     T0 = 1.276329489519604;  T1 = 3.223074676022708;  T2 = 1.3826722030238239; T3 = 6.394899078549686
	//                     T0 = 1.2775570116028898; T1 = 3.2318134203344986; T2 = 1.3874236489385927; T3 = 6.412401070582613
	//  not theta4         T0 = 1.4730081388341267; T1 = 13.60910612977283;  T2 = 1.8048995747096395; T3 = 27.12903401138953
	//private static double[] THETA = new double[] { 2.954352817910267, 4.8783863956041005, 1.4084074171327663, 9.248173642315347};

	//private static double[] BEST_THETA = new double[] { 1.2771164328633122, 3.2279713197674047, 1.3851975715541371, 9.248173642315347};
	private static double[] BEST_THETA = new double[] { 0.5, .1, .25, .8 };

	public static void updateQ(final double[] theta, final double reward, final double[] previousSignals, final double[] maxSignals) {
		if(previousSignals == null)
			return;

		final double maxQ = maxSignals == null ? 1.0 : evaluate(theta, maxSignals);
		final double q = evaluate(theta, previousSignals);
		final double z = ALPHA * (reward + (GAMMA * maxQ) - q);


		final double dt = z * q * (1 - q);
		theta[0] = theta[0] + dt * previousSignals[0];
		theta[1] = theta[1] + dt * previousSignals[1];
		theta[2] = theta[2] + dt * previousSignals[2];
		theta[3] = theta[3] + dt;
	}

	public static double reward(final Player player) {
		final Player winner = ClueMain.clue.getWinner();
		if(winner == null) {
			return 0d;
		} else {
			return player == winner || player.getSuspectNumber() == winner.getSuspectNumber() ? 1d : -1d;
		}
	}

	public static double evaluate(final double[] theta, final double[] signals) {
		final double sum = (theta[0] * signals[0]) + (theta[1] * signals[1]) + (theta[2] * signals[2]) + LAMBDA * theta[3];
		final double denominator = 1.0 + Math.exp(-1.0 * sum);
		return (2.0 / denominator) - 1.0;
	}

	public static void printBestTheta() {
		System.out.println("T0 = " + BEST_THETA[0] + "; T1 = " + BEST_THETA[1] + "; T2 = " + BEST_THETA[2] + "; T3 = " + BEST_THETA[3]);
	}

	public static double[] copyBestThetas() {
		return Arrays.copyOf(BEST_THETA, BEST_THETA.length);
	}

	public static void updateBestThetas(final double[] newBest) {
		BEST_THETA = Arrays.copyOf(newBest, newBest.length);
	}
}
