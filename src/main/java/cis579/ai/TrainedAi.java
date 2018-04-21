package cis579.ai;

import static org.antinori.game.Card.NUM_ROOMS;
import static org.antinori.game.Card.NUM_SUSPECTS;
import static org.antinori.game.Card.NUM_WEAPONS;
import static org.antinori.game.Card.TYPE_ROOM;
import static org.antinori.game.Card.TYPE_SUSPECT;
import static org.antinori.game.Card.TYPE_WEAPON;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;

import org.antinori.astar.Location;
import org.antinori.game.Card;
import org.antinori.game.Player;

public class TrainedAi extends AiPlayer {

	private static final Set<Card> DECK = new TreeSet<>();

	public static final double[] THETAS = new double[] { 1.2771127255668693, 3.228139469123818, 1.3852325544391302, 9.24851017909113 };

	static {
		// make deck of cards
		for (int i = 0; i < NUM_ROOMS; i++) {
			DECK.add(Card.getInstance(TYPE_ROOM, i));
		}
		for (int i = 0; i < NUM_SUSPECTS; i++) {
			DECK.add(Card.getInstance(TYPE_SUSPECT, i));
		}
		for (int i = 0; i < NUM_WEAPONS; i++) {
			DECK.add(Card.getInstance(TYPE_WEAPON, i));
		}
	}

	// =======================================================================================================================================
	// =======================================================================================================================================
	// =======================================================================================================================================

	private HashMap<Card, AtomicInteger> guessedButNotShown;

	public TrainedAi(final Player player) {
		super(player);

		this.guessedButNotShown = new HashMap<>();

		DECK.stream().forEach(card -> {
			this.guessedButNotShown.put(card, new AtomicInteger(0));
		});
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
		if(cards.size() == 1)
			return cards.stream().findFirst().get();

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

		return best;
	}

	@Override
	public void onGameOver() {
		// no nothing
	}

	private double evaluateCard(final Card c) {
		final double[] signals = this.calculateSignals(c);
		return signals == null ? 0 : Evaluator.evaluate(THETAS, signals);
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
