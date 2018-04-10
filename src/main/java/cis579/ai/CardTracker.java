package cis579.ai;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;

import org.antinori.game.Card;
import org.antinori.game.Player;

public class CardTracker {

	private static final HashMap<Card, AtomicInteger> TIMES_GUESSED = new HashMap<>();
	private static final Set<Card> NO_SHOW = new TreeSet<>();
	private static final List<Card> ALL_CARDS = new ArrayList<>();

	private static final HashMap<Player, Set<Card>> notInHand = new HashMap<>();

	private static int SUGGESTIONS_MADE = 0;

	static {
		getAllCards();
		reset();
	}

	public static void reset() {
		TIMES_GUESSED.clear();
		NO_SHOW.clear();
		notInHand.clear();

		for(final Card c : ALL_CARDS) {
			TIMES_GUESSED.put(c, new AtomicInteger(0));
		}

		SUGGESTIONS_MADE = 0;
	}

	public static void suggestionMade(final ArrayList<Card> suggestion) {
		for(final Card c : suggestion) {
			TIMES_GUESSED.get(c).incrementAndGet();
		}

		SUGGESTIONS_MADE++;
	}

	public static void playerHasNoCardsToShow(final Player player, final ArrayList<Card> suggestion) {
		Set<Card> cards = notInHand.get(player);

		if(cards == null) {
			cards = new TreeSet<>();
			notInHand.put(player, cards);
		}

		for(final Card c : suggestion) {
			cards.add(c);
		}
	}

	public static void playersHaveNoCardsToShow(final ArrayList<Card> suggestion) {
		for(final Card c : suggestion) {
			NO_SHOW.add(c);
		}
	}

	public static int timesGuessed(final Card c) {
		return TIMES_GUESSED.get(c).intValue();
	}

	public static boolean isNoShow(final Card c) {
		return NO_SHOW.contains(c);
	}

	private static void getAllCards(){
		for (int i = 0; i < Card.NUM_ROOMS; i++) {
			ALL_CARDS.add(new Card(Card.TYPE_ROOM, i));
		}

		for (int i = 0; i < Card.NUM_SUSPECTS; i++) {
			ALL_CARDS.add( new Card(Card.TYPE_SUSPECT, i));
		}

		for (int i = 0; i < Card.NUM_WEAPONS; i++) {
			ALL_CARDS.add(new Card(Card.TYPE_WEAPON, i));
		}
	}

	public static int suggestionsMade() {
		return SUGGESTIONS_MADE;
	}

	public static double averageTimesGuessed() {
		int sum = 0;
		for(final AtomicInteger cnt : TIMES_GUESSED.values()) {
			sum += cnt.get();
		}

		final int total = TIMES_GUESSED.size();
		return total == 0 ? 0 : sum / total; // TODO is zero a good value here?
	}

}
