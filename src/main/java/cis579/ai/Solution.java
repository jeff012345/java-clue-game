package cis579.ai;

import org.antinori.game.Card;

public class Solution {

	public Card suspect;
	public Card room;
	public Card weapon;

	@Override
	public String toString() {
		return suspect + " in the " + room + " with the " + weapon;
	}
}
