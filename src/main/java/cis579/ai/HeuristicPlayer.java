package cis579.ai;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.antinori.astar.Location;
import org.antinori.game.Card;
import org.antinori.game.Player;
import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;

import cis579.ai.de.ResultDE;

public class HeuristicPlayer extends AiPlayer {
	
	private static final Map<String, List<Double>> coefficientStore = new HashMap<>();
	
	static {
		coefficientStore.put(Card.SCARLET_NAME, new ArrayList<Double>(3));
		coefficientStore.put(Card.WHITE_NAME, new ArrayList<Double>(3));
		coefficientStore.put(Card.PLUM_NAME, new ArrayList<Double>(3));
		coefficientStore.put(Card.MUSTARD_NAME, new ArrayList<Double>(3));
		coefficientStore.put(Card.PEACOCK_NAME, new ArrayList<Double>(3));
		coefficientStore.put(Card.GREEN_NAME, new ArrayList<Double>(3));
	}
	
	public static void resetCoefficients() {
		for(List<Double> coeffs : coefficientStore.values()) {
			coeffs.clear();
		}
	}
	
	private static double fitCoefficient(List<ResultDE> results, int index) {
		if(results.size() < 200000000) {
			return Math.random() * 100;
		}
		
		// Collect data.
		final WeightedObservedPoints obs = new WeightedObservedPoints();
		
		for(ResultDE r : results) {
			obs.add(r.getCoefficients()[index], r.getSuccessRate());
		}
		
		// Instantiate a third-degree polynomial fitter.
		final PolynomialCurveFitter fitter = PolynomialCurveFitter.create(2);

		// Retrieve fitted parameters (coefficients of the polynomial function).
		final double[] coeff = fitter.fit(obs.toList());
		
		System.out.println("coeff length = " + coeff.length);
		
		// a^2x + bx + c
		// 2ax + b = 0
		// x = -b / 2a 

		return coeff[1] / (-2.0  * coeff[0]);
	}
	
	// =======================================================================================================================================
	// =======================================================================================================================================
	// =======================================================================================================================================
	
	private double[] coefficients = new double[3];
	
	private HashMap<Card, AtomicInteger> guessedButNotShown = new HashMap<>();
	
	public HeuristicPlayer(Player player) {
		super(player);
		
		determineCoefficients();
	}
	
	private void determineCoefficients() {
		List<Double> coeffs = coefficientStore.get(this.player.getSuspectName());
		if(coeffs.isEmpty()) {
			// no coefficients set for the suspect, so create them
			List<ResultDE> results = Database.getInstance().getAllResults();
			
			coefficients[0] = fitCoefficient(results, 0);
			coefficients[1] = fitCoefficient(results, 1);
			coefficients[2] = fitCoefficient(results, 2);
			
			coeffs.add(coefficients[0]);
			coeffs.add(coefficients[1]);
			coeffs.add(coefficients[2]);
			
		} else {
			// load existing
			coefficients[0] = coeffs.get(0);
			coefficients[1] = coeffs.get(1);
			coefficients[2] = coeffs.get(2);
		}
		
		//System.out.println(this.player.getSuspectName() + "; a = " + coefficients[0] + "; b = " + coefficients[1] +"; c = " + coefficients[2]);
	}
	
	public double[] getCoefficients() {
		return new double[] { coefficients[0], coefficients[1], coefficients[2] };
	}
	
	@Override
	public Solution getSuggestion() {
		Solution guess = this.suggestionForCurrentRoom();
		
		guess.weapon = pickWeapon();
		guess.suspect = pickSuspect();
		
		return guess;
	}

	@Override
	public Solution canMakeAccusation() {
		if(this.unknownRooms.size() == 1 && this.unknownWeapons.size() == 1 && this.unknownSuspects.size() == 1) {
			Solution solution = new Solution();
			solution.room = this.unknownRooms.stream().findFirst().get();
			solution.weapon = this.unknownWeapons.stream().findFirst().get();
			solution.suspect = this.unknownSuspects.stream().findFirst().get();
			return solution;
		}
		return null;
	}

	@Override
	public boolean shouldRoll() {
		if(this.player.isInARoom()) {
			return this.currentRoomIsKnown(); // if current room is known, then roll
		}
		return true;
	}

	@Override
	public void onAllPlayersNoCardsToShow(Solution suggestion) {
		this.unknownRooms.clear();
		this.unknownRooms.add(suggestion.room);
		
		this.unknownSuspects.clear();
		this.unknownSuspects.add(suggestion.suspect);
		
		this.unknownWeapons.clear();
		this.unknownWeapons.add(suggestion.weapon);
		
		// TODO put a hook for tracking all guesses
		// TODO track all player's guesses
		// TODO keep counts of how many times a card is guessed
		
		// TODO when another player guesses something and no one else shows a card
		//		we need to decide the most probable solution and make an accusation
		//		using that solution the next turn
		// 		have a confidence level to actually make the accusation
	}

	@Override
	public Location decideLocation(ArrayList<Location> choices) {
		FilteredLocationChoices filteredChoices = this.filterChoices(choices);
		
		// check the secret passages and take it if the card is not known
		Location shortcut = this.canTakeShortcut();
		if(shortcut != null) {
			Card roomCard = shortcut.getRoomCard();
			if(!this.isCardKnown(roomCard)) {
				return shortcut;
			}
		}
		
		if(!filteredChoices.roomChoices.isEmpty()) {
			// going into a room is possible, so pick one if it's not already known
			for(Location choice : filteredChoices.roomChoices) {
				if(!this.isCardKnown(choice.getRoomCard())) {
					// pick the first room that the player doesn't know 
					return choice;
				}
			}
			
			// all possible rooms are known, so move somewhere else
		}
		
		return this.findClosestLocationToAnUnknownRoom(filteredChoices.otherChoices);
	}
	
	@Override
	public void onShownCard(Player showingPlayer, List<Card> suggestion, Card card) {
		super.onShownCard(showingPlayer, suggestion, card);
		
		if(showingPlayer == null)
			return;
		
		suggestion.stream().filter(c -> !c.equals(card)).forEach(c -> {
			AtomicInteger cnt = guessedButNotShown.get(c);
			if (cnt == null) {
				guessedButNotShown.put(c, new AtomicInteger(1));
			} else {
				cnt.incrementAndGet();
			}
		});
	}
	
	@Override
	public void onPlayerNoCardsToShow(Player showingPlayer, List<Card> suggestion) {
		suggestion.stream().forEach(c -> {
			AtomicInteger cnt = guessedButNotShown.get(c);
			if (cnt == null) {
				guessedButNotShown.put(c, new AtomicInteger(1));
			} else {
				cnt.incrementAndGet();
			}
		});
	}
	
	private Card pickWeapon() {
		return pickBestCard(this.unknownWeapons);
	}
	
	private Card pickSuspect() {
		return pickBestCard(this.unknownSuspects);
	}
	
	private Card pickBestCard(Set<Card> cards) {
		Card best = null;
		double maxValue = -1;
		double value;
		
		for(Card c : cards) {
			value = evaluateCard(c);
			
			if(value > maxValue) {
				maxValue = value;
				best = c;
			}
		}
		
		return best;
	}
	
	private double evaluateCard(Card c) {
		int timesGuessed = CardTracker.timesGuessed(c);
		int isNoShow = CardTracker.isNoShow(c) ? 1 : 0;
		int timesNotShown = guessedButNotShown.containsKey(c) ? guessedButNotShown.get(c).get() : 0;
		
		return coefficients[0] * timesGuessed + coefficients[1] * isNoShow + coefficients[2] * timesNotShown;
	}

}
