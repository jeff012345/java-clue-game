package cis579.ai;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;

import org.antinori.game.Card;

public class CardTracker {

	private static final HashMap<Card, AtomicInteger> TIMES_GUESSED = new HashMap<Card, AtomicInteger>();
	private static final Set<Card> NO_SHOW = new TreeSet<Card>();
	private static final List<Card> ALL_CARDS = new ArrayList<Card>();
	
	static {
		getAllCards();
		reset();
	}
	
	public static void reset() {
		TIMES_GUESSED.clear();
		NO_SHOW.clear();
		
		for(Card c : ALL_CARDS) {
			TIMES_GUESSED.put(c, new AtomicInteger(0));
		}
	}
	
	public static void suggestionMade(ArrayList<Card> suggestion) {
		for(Card c : suggestion) {
			TIMES_GUESSED.get(c).incrementAndGet();
		}
	}
	
	public static void noCardsToShow(ArrayList<Card> suggestion) {
		for(Card c : suggestion) {
			NO_SHOW.add(c);
		}
	}
	
	public static int timesGuessed(Card c) {
		return TIMES_GUESSED.get(c).intValue();
	}
	
	public static boolean isNoShow(Card c) {
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
	
}
