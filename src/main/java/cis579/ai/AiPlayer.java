package cis579.ai;

import static org.antinori.game.Card.NUM_ROOMS;
import static org.antinori.game.Card.NUM_SUSPECTS;
import static org.antinori.game.Card.NUM_WEAPONS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.antinori.astar.Location;
import org.antinori.game.Card;
import org.antinori.game.ClueMain;
import org.antinori.game.Notebook;
import org.antinori.game.Player;

public abstract class AiPlayer implements ShownCardListener {

	protected Player player;
	
	protected Set<Card> unknownWeapons;
	protected Set<Card> unknownRooms;
	protected Set<Card> unknownSuspects;
	
	public AiPlayer(Player player) {
		this.player = player;
		
		this.player.getNotebook().setShownCardListener(this);
		
		this.unknownWeapons = new TreeSet<Card>();
		this.unknownRooms = new TreeSet<Card>();
		this.unknownSuspects = new TreeSet<Card>();
		
		determineUnknowns();
		
		// initialize cards
		for(Card card : player.getCardsInHand()) {
			this.onShownCard(card);
		}
	}
	
	/**
	 * @return suggestion for the turn
	 */
	public abstract Solution getSuggestion();
	
	/**
	 * 	@return returns a solution if it knows; otherwise, null.
	 */
	public abstract Solution canMakeAccusation();
	
	/**
	 * @return should the player roll or not
	 */
	public abstract boolean shouldRoll();
	
	/**
	 * event is triggered when the suggestion has no cards to show
	 */
	public abstract void onNoCardsToShow(Solution suggestion);
	
	/**
	 * Assumes that the player has already decided to roll
	 * 
	 * @param choices The possible locations that the player can go
	 * @return After rolling, picks the best location based on the possible choices
	 */
	public abstract Location decideLocation(ArrayList<Location> choices);
	
	private void determineUnknowns() {
		Notebook notebook = this.player.getNotebook();
		
		for (int i = 0; i < NUM_ROOMS; i++) {
			Card c = new Card(Card.TYPE_ROOM, i);
            if(!notebook.isCardToggled(c)) {
            	unknownRooms.add(c);
            }
        }
		
        for (int i = 0; i < NUM_SUSPECTS; i++) {
        	Card c = new Card(Card.TYPE_SUSPECT, i);
        	if(!notebook.isCardToggled(c)) {
            	unknownSuspects.add(c);
            }
        }
        
        for (int i = 0; i < NUM_WEAPONS; i++) {
        	Card c = new Card(Card.TYPE_WEAPON, i);
        	if(!notebook.isCardToggled(c)) {
            	unknownWeapons.add(c);
            }
        }
	}
	
	public void onShownCard(Card card) {
		System.out.println(this.player.getSuspectName() + " shown " + card.toString());
		switch(card.getType()) {
        case Card.TYPE_ROOM:
        	System.out.println("Shown room. Count = " + unknownRooms.size());
        	unknownRooms.remove(card);
        	System.out.println("Remove count = " + unknownRooms.size());
        	break;
        case Card.TYPE_WEAPON:
        	System.out.println("Shown weapon. Count = " + unknownWeapons.size());
        	unknownWeapons.remove(card);
        	System.out.println("Remove count = " + unknownWeapons.size());
        	break;
        case Card.TYPE_SUSPECT:
        	System.out.println("Shown suspect. Count = " + unknownSuspects.size());
        	unknownSuspects.remove(card);
        	System.out.println("Remove count = " + unknownSuspects.size());
        	break;
        }
		printUnknowns();
	}
	
	protected void printUnknowns() {
		System.out.println("Player: " + this.player.getSuspectName());
		
		System.out.print("Unknown Suspects: ");
		for(Card c : unknownSuspects) {
			System.out.print(c.toString() + ", ");
		}
		System.out.println("");
		
		System.out.print("Unknown Weapons: ");
		for(Card c : unknownWeapons) {
			System.out.print(c.toString() + ", ");
		}
		System.out.println("");
		
		System.out.print("Unknown rooms: ");
		for(Card c : unknownRooms) {
			System.out.print(c.toString() + ", ");
		}
		System.out.println("");
	}
	
	/**
	 * @return Location of the room that can take the shortcut to; otherwise, null 
	 */
	protected Location canTakeShortcut() {
		// TODO just return the card and not the location?
		int currentRoomId = this.player.getLocation().getRoomId();
		switch(currentRoomId) {
			case Card.ROOM_LOUNGE:
				return ClueMain.map.getRoomLocation(Card.ROOM_CONSERVATORY);
			case Card.ROOM_KITCHEN:
				return ClueMain.map.getRoomLocation(Card.ROOM_STUDY);
			case Card.ROOM_CONSERVATORY:
				return ClueMain.map.getRoomLocation(Card.ROOM_CONSERVATORY);
			case Card.ROOM_STUDY:
				return ClueMain.map.getRoomLocation(Card.ROOM_KITCHEN);
		}
		return null;
	}
	
	protected Location findClosestLocationToAnUnknownRoom(List<Location> choices) {
		// if only one choice, take it
		if(choices.size() == 1) {
			return choices.get(0);
		}
		
		TreeSet<Integer> unknownRoomIds = new TreeSet<Integer>(); 
		for(Card c : this.unknownRooms) {
			unknownRoomIds.add(c.getValue());
		}
		
		// get all other rooms except the one they are in and the ones they already know are false
        ArrayList<Location> rooms = ClueMain.map.getAllRoomLocations(unknownRoomIds);
        
        int closest = 100;
		// find a room location which is closest to them which is not in their hand or toggled
        Location newLocation = null;
        for (Location choice : choices) {
            for (Location room : rooms) {
                List<Location> path = ClueMain.pathfinder.findPath(ClueMain.map.getLocations(), choice, Collections.singleton(room));
                if (path.size() < closest) {
                    closest = path.size();
                    newLocation = choice;
                }
            }
        }
        
        return newLocation;
	}
	
	public void addCardToHand(Card card) {
		this.onShownCard(card);
	}
	
	public boolean isCardKnown(Card card) {
		switch(card.getType()) {
        case Card.TYPE_ROOM:
        	return !unknownRooms.contains(card);
        case Card.TYPE_WEAPON:
        	return !unknownWeapons.contains(card);
        case Card.TYPE_SUSPECT:
        	return !unknownSuspects.contains(card);
        }
		throw new RuntimeException("wat?");
	}
	
	protected boolean currentRoomIsKnown() {
		 return this.isCardKnown(this.player.getLocation().getRoomCard());
	}
	
	protected FilteredLocationChoices filterChoices(ArrayList<Location> choices){
		FilteredLocationChoices filtered = new FilteredLocationChoices(choices.size());
		
		// find any rooms in the possible choices
		for (Location choice : choices) {
			if(choice.getRoomId() != -1) {
				filtered.roomChoices.add(choice);
			} else {
				filtered.otherChoices.add(choice);
			}
		}
		
		return filtered;
	}
	
	/**
	 * @return new solution object with the room already initialized to the current room
	 */
	protected Solution suggestionForCurrentRoom() {
		Solution guess = new Solution();
		guess.room = this.player.getLocation().getRoomCard();
		
		return guess;
	}
	
	/*=================================================================================================================
	 * 
	 * Inner classes
	 * 
	 ================================================================================================================*/
	public static class FilteredLocationChoices {
		ArrayList<Location> roomChoices;
		ArrayList<Location> otherChoices;
		
		public FilteredLocationChoices(int size) {
			roomChoices = new ArrayList<Location>(size);
			otherChoices = new ArrayList<Location>(size);
		}
	}
	
}