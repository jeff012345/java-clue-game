package cis579.ai;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.antinori.astar.Location;
import org.antinori.game.Card;
import org.antinori.game.Player;

public class RandomPlayer extends AiPlayer {

	public RandomPlayer(Player player) {
		super(player);
	}

	@Override
	public Solution getSuggestion() {
		Solution guess = this.suggestionForCurrentRoom();
		
		guess.weapon = pickCardRandomly(this.unknownWeapons);
		guess.suspect = pickCardRandomly(this.unknownSuspects);
		
		//this.printUnknowns();
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
	public Location decideLocation(ArrayList<Location> choices) {
		
		//printUnknowns();
		
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
					//System.out.println("doesn't know about " + choice.getRoomCard().toString() + " so move to it");
					return choice;
				}
			}
			
			// all possible rooms are known, so move somewhere else
		}
		
		return this.findClosestLocationToAnUnknownRoom(filteredChoices.otherChoices);
	}
	
	@Override
	public void onAllPlayersNoCardsToShow(Solution suggestion) {
		this.unknownRooms.clear();
		this.unknownRooms.add(suggestion.room);
		
		this.unknownSuspects.clear();
		this.unknownSuspects.add(suggestion.suspect);
		
		this.unknownWeapons.clear();
		this.unknownWeapons.add(suggestion.weapon);
		
		//printUnknowns();
	}
	
	private Card pickCardRandomly(Set<Card> cards) {
		int randomIndex = (int) Math.floor(Math.random() * cards.size());
		
		Iterator<Card> iter = cards.iterator();
		int i = 0;
		while(iter.hasNext()) {
			Card c = iter.next();
			if(i == randomIndex) {
				return c;
			}
			i++;
		}
		
		return null;
	}

	@Override
	public void onPlayerNoCardsToShow(Player showingPlayer, List<Card> suggestion) {
	}

}

