package cis579.ai;

import static org.antinori.game.Card.NUM_ROOMS;
import static org.antinori.game.Card.NUM_SUSPECTS;
import static org.antinori.game.Card.NUM_WEAPONS;
import static org.antinori.game.Card.TYPE_ROOM;
import static org.antinori.game.Card.TYPE_SUSPECT;
import static org.antinori.game.Card.TYPE_WEAPON;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.antinori.astar.Location;
import org.antinori.game.Card;
import org.antinori.game.Player;

public class HeuristicPlayer extends AiPlayer {

	private static final Map<String, List<Double>> coefficientStore = new HashMap<>();

	private static final Set<Card> DECK = new TreeSet<>();

	static {
		coefficientStore.put(Card.SCARLET_NAME, new ArrayList<Double>(3));
		coefficientStore.put(Card.WHITE_NAME, new ArrayList<Double>(3));
		coefficientStore.put(Card.PLUM_NAME, new ArrayList<Double>(3));
		coefficientStore.put(Card.MUSTARD_NAME, new ArrayList<Double>(3));
		coefficientStore.put(Card.PEACOCK_NAME, new ArrayList<Double>(3));
		coefficientStore.put(Card.GREEN_NAME, new ArrayList<Double>(3));

		// make deck of cards
		for (int i = 0; i < NUM_ROOMS; i++) {
			DECK.add(new Card(TYPE_ROOM, i));
		}
		for (int i = 0; i < NUM_SUSPECTS; i++) {
			DECK.add(new Card(TYPE_SUSPECT, i));
		}
		for (int i = 0; i < NUM_WEAPONS; i++) {
			DECK.add(new Card(TYPE_WEAPON, i));
		}
	}

	public static void resetCoefficients() {
		for(final List<Double> coeffs : coefficientStore.values()) {
			coeffs.clear();
		}
	}

	// =======================================================================================================================================
	// =======================================================================================================================================
	// =======================================================================================================================================

	private double[] coefficients = new double[3];
	private double[] signals = null;

	private HashMap<Card, AtomicInteger> guessedButNotShown = new HashMap<>();

	public HeuristicPlayer(final Player player) {
		super(player);

		DECK.stream().forEach(card -> {
			this.guessedButNotShown.put(card, new AtomicInteger(0));
		});

		this.determineCoefficients();
	}

	private void determineCoefficients() {
		final List<Double> coeffs = coefficientStore.get(this.player.getSuspectName());
		if(coeffs.isEmpty()) {
			// no coefficients set for the suspect, so create them
			//List<ResultDE> results = Database.getInstance().getAllResults();

			final Random rand1 = new Random(UUID.randomUUID().getMostSignificantBits());
			final Random rand2 = new Random(UUID.randomUUID().getLeastSignificantBits());
			final Random rand3 = new Random(UUID.randomUUID().getMostSignificantBits());

			this.coefficients[0] = rand1.nextInt(100) / 100d;
			this.coefficients[1] = rand2.nextInt(100) / 100d;
			this.coefficients[2] = rand3.nextInt(100) / 100d;

			coeffs.add(this.coefficients[0]);
			coeffs.add(this.coefficients[1]);
			coeffs.add(this.coefficients[2]);

			System.out.println("Reset coeffs: " + this.coefficients[0] + "," + this.coefficients[1] + "," + this.coefficients[2]);
		} else {
			// load existing
			this.coefficients[0] = coeffs.get(0);
			this.coefficients[1] = coeffs.get(1);
			this.coefficients[2] = coeffs.get(2);
		}

		//System.out.println(this.player.getSuspectName() + "; a = " + coefficients[0] + "; b = " + coefficients[1] +"; c = " + coefficients[2]);
	}

	public double[] getCoefficients() {
		return new double[] { this.coefficients[0], this.coefficients[1], this.coefficients[2] };
	}

	@Override
	public Solution getSuggestion() {
		final Solution guess = this.suggestionForCurrentRoom();

		guess.weapon = this.pickWeapon();
		guess.suspect = this.pickSuspect();

		return guess;
	}

	@Override
	public Solution canMakeAccusation() {
		if(this.unknownRooms.size() == 1 && this.unknownWeapons.size() == 1 && this.unknownSuspects.size() == 1) {
			final Solution solution = new Solution();
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
	public void onAllPlayersNoCardsToShow(final Solution suggestion) {
		this.unknownRooms.clear();
		this.unknownRooms.add(suggestion.room);

		this.unknownSuspects.clear();
		this.unknownSuspects.add(suggestion.suspect);

		this.unknownWeapons.clear();
		this.unknownWeapons.add(suggestion.weapon);

		// TODO when another player guesses something and no one else shows a card
		//		we need to decide the most probable solution and make an accusation
		//		using that solution the next turn
		// 		have a confidence level to actually make the accusation
	}

	@Override
	public Location decideLocation(final Collection<Location> choices) {
		final FilteredLocationChoices filteredChoices = this.filterChoices(choices);

		// check the secret passages and take it if the card is not known
		final Location shortcut = this.canTakeShortcut();
		if(shortcut != null) {
			final Card roomCard = shortcut.getRoomCard();
			if(!this.isCardKnown(roomCard)) {
				return shortcut;
			}
		}

		if(!filteredChoices.roomChoices.isEmpty()) {
			// going into a room is possible, so pick one if it's not already known
			for(final Location choice : filteredChoices.roomChoices) {
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
	public void onShownCard(final Player showingPlayer, final List<Card> suggestion, final Card card) {
		super.onShownCard(showingPlayer, suggestion, card);

		if(showingPlayer == null)
			return;

		suggestion.stream().filter(c -> !c.equals(card)).forEach(c -> {
			final AtomicInteger cnt = this.guessedButNotShown.get(c);
			if (cnt == null) {
				this.guessedButNotShown.put(c, new AtomicInteger(1));
			} else {
				cnt.incrementAndGet();
			}
		});
	}

	@Override
	public void onPlayerNoCardsToShow(final Player showingPlayer, final List<Card> suggestion) {
		suggestion.stream().forEach(c -> {
			final AtomicInteger cnt = this.guessedButNotShown.get(c);
			if (cnt == null) {
				this.guessedButNotShown.put(c, new AtomicInteger(1));
			} else {
				cnt.incrementAndGet();
			}
		});
	}

	private Card pickWeapon() {
		return this.pickBestCard(this.unknownWeapons);
	}

	private Card pickSuspect() {
		return this.pickBestCard(this.unknownSuspects);
	}

	private Card pickBestCard(final Set<Card> cards) {
		Card best = null;
		double maxValue = -1;
		double value;

		for(final Card c : cards) {
			value = this.evaluateCard(c);

			if(value > maxValue) {
				maxValue = value;
				best = c;
			}
		}

		if(best == null) {
			best = cards.iterator().next();
		}

		// update learning values
		final double[] previousSignals = this.signals;
		this.storeSignalValues(best);

		Evaluator.updateQ(this.getPlayer(), previousSignals, this.signals);

		return best;
	}

	private void storeSignalValues(final Card card) {
		final double[] newSignals = this.calculateSignals(card);
		if(newSignals != null) {
			this.signals = newSignals;
		}
	}

	private double evaluateCard(final Card c) {
		final double[] signals = this.calculateSignals(c);
		return signals == null ? 0 : Evaluator.evaluate(signals);
	}

	private double[] calculateSignals(final Card card) {
		if(CardTracker.suggestionsMade() == 0) {
			return null;
		}

		final int timesGuessed = CardTracker.timesGuessed(card);
		final double isNoShow = CardTracker.isNoShow(card) ? 1 : 0.5;
		final int timesNotShown = this.guessedButNotShown.get(card).get();

		return new double[] {
				timesGuessed,
				isNoShow,
				timesNotShown
		};
	}
}
