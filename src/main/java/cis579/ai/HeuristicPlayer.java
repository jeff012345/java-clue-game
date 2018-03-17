package cis579.ai;

import java.util.ArrayList;

import org.antinori.astar.Location;
import org.antinori.game.Card;
import org.antinori.game.Player;

import cis579.ai.AiPlayer.FilteredLocationChoices;

public class HeuristicPlayer extends AiPlayer {
	
	public HeuristicPlayer(Player player) {
		super(player);
	}
	
	@Override
	public Solution getSuggestion() {
		Solution guess = this.suggestionForCurrentRoom();
		
		guess.weapon = pickWeapon();
		guess.suspect = pickSuspect();
		
		this.printUnknowns();
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
	public void onNoCardsToShow(Solution suggestion) {
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
		
		printUnknowns();
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
					System.out.println("doesn't know about " + choice.getRoomCard().toString() + " so move to it");
					return choice;
				}
			}
			
			// all possible rooms are known, so move somewhere else
		}
		
		return this.findClosestLocationToAnUnknownRoom(filteredChoices.otherChoices);
	}
	
	private Card pickWeapon() {
		return null;
	}
	
	private Card pickSuspect() {
		return null;
	}
	
	private double evaluateCard(Card card) {
		
		
		
		return 0;
	}
}
