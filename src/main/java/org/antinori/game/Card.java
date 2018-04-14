package org.antinori.game;


import com.smartfoxserver.v2.protocol.serialization.SerializableSFSType;

public class Card implements SerializableSFSType, Comparable<Card> {

	public Card() {
	}

	public int type = 0;
	public int value = 0;
	public String desc = null;

	public static final int NUM_SUSPECTS = 6;
	public static final int NUM_ROOMS = 9;
	public static final int NUM_WEAPONS = 6;
	public static final int TOTAL = NUM_ROOMS + NUM_SUSPECTS + NUM_WEAPONS;

	public static final int TYPE_SUSPECT = 0;
	public static final int TYPE_WEAPON = 1;
	public static final int TYPE_ROOM = 2;

	public static final int ROOM_HALL = 0;
	public static final int ROOM_LOUNGE = 1;
	public static final int ROOM_DINING = 2;
	public static final int ROOM_KITCHEN = 3;
	public static final int ROOM_BALLROOM = 4;
	public static final int ROOM_CONSERVATORY = 5;
	public static final int ROOM_BILLIARD = 6;
	public static final int ROOM_STUDY = 7;
	public static final int ROOM_LIBRARY = 8;

	public static final int SUSPECT_SCARLET = 0;
	public static final int SUSPECT_WHITE = 1;
	public static final int SUSPECT_PLUM = 2;
	public static final int SUSPECT_MUSTARD = 3;
	public static final int SUSPECT_GREEN = 4;
	public static final int SUSPECT_PEACOCK = 5;

	public static final int WEAPON_KNIFE = 0;
	public static final int WEAPON_ROPE = 1;
	public static final int WEAPON_REVOLVER = 2;
	public static final int WEAPON_WRENCH = 3;
	public static final int WEAPON_PIPE = 4;
	public static final int WEAPON_CANDLE = 5;

	public static final String mustard_desc = "Colonel Mustard: was an old friend of Mr. Boddy's uncle and a frequent guest at the Tudor Mansion.";
	public static final String plum_desc = "Professor Plum: A Professor in Middle Eastern history had many of his archaeological digs funded by Mr. Boddy's uncle.";
	public static final String scarlet_desc = "Miss Scarlett: An aspiring but not very talented actress. Has decided on a career change and is now setting her sights on rich old widowers, which is why she is at Mr.Boddy's dinner party.";
	public static final String green_desc = "Mr. Green: a trickster and conman has become acquainted with Mr. Boddy through his uncle, Sir Hugh Black.";
	public static final String peacock_desc = "Mrs. Peacock: is Miss. Scarlett's mother, a socialite and three time widow, she still dreams of a career on the stage.";
	public static final String white_desc = "Mrs. White: Mr. Boddy's long-term housekeeper and cook.";
	public static final String[] descriptions = {scarlet_desc, white_desc, plum_desc, mustard_desc, green_desc, peacock_desc};

	public static final Card scarlet = new Card(TYPE_SUSPECT, SUSPECT_SCARLET);
	public static final Card mustard = new Card(TYPE_SUSPECT, SUSPECT_MUSTARD);
	public static final Card green = new Card(TYPE_SUSPECT, SUSPECT_GREEN);
	public static final Card plum = new Card(TYPE_SUSPECT, SUSPECT_PLUM);
	public static final Card peacock = new Card(TYPE_SUSPECT, SUSPECT_PEACOCK);
	public static final Card white = new Card(TYPE_SUSPECT, SUSPECT_WHITE);

	public static final Card KITCHEN = new Card(TYPE_ROOM, ROOM_KITCHEN);
	public static final Card LOUNGE = new Card(TYPE_ROOM, ROOM_LOUNGE);
	public static final Card CONSERVATORY = new Card(TYPE_ROOM, ROOM_CONSERVATORY);
	public static final Card STUDY = new Card(TYPE_ROOM, ROOM_STUDY);
	public static final Card HALL = new Card(TYPE_ROOM, ROOM_HALL);
	public static final Card DINING = new Card(TYPE_ROOM, ROOM_DINING);
	public static final Card BALLROOM = new Card(TYPE_ROOM, ROOM_BALLROOM);
	public static final Card BILLIARD = new Card(TYPE_ROOM, ROOM_BILLIARD);
	public static final Card LIBRARY = new Card(TYPE_ROOM, ROOM_LIBRARY);

	public static final Card KNIFE = new Card(TYPE_WEAPON, WEAPON_KNIFE);
	public static final Card ROPE = new Card(TYPE_WEAPON, WEAPON_ROPE);
	public static final Card REVOLVER = new Card(TYPE_WEAPON, WEAPON_REVOLVER);
	public static final Card WRENCH = new Card(TYPE_WEAPON, WEAPON_WRENCH);
	public static final Card PIPE = new Card(TYPE_WEAPON, WEAPON_PIPE);
	public static final Card CANDLE = new Card(TYPE_WEAPON, WEAPON_CANDLE);

	public static final String PEACOCK_NAME = "Mrs. Peacock";
	public static final String SCARLET_NAME = "Miss Scarlet";
	public static final String PLUM_NAME = "Professor Plum";
	public static final String WHITE_NAME = "Mrs. White";
	public static final String MUSTARD_NAME = "Colonel Mustard";
	public static final String GREEN_NAME = "Mr. Green";

	private Card(final int type, final int value) {
		this.type = type;
		this.value = value;
		this.desc = this.toString();
	}

	public static Card getInstance(final int type, final int value) {
		switch (type) {
		case TYPE_SUSPECT:
			switch (value) {
			case SUSPECT_SCARLET:
				return scarlet;
			case SUSPECT_PLUM:
				return plum;
			case SUSPECT_WHITE:
				return white;
			case SUSPECT_MUSTARD:
				return mustard;
			case SUSPECT_GREEN:
				return green;
			case SUSPECT_PEACOCK:
				return peacock;
			}
			break;
		case TYPE_ROOM:
			switch (value) {
			case ROOM_HALL:
				return HALL;
			case ROOM_LOUNGE:
				return LOUNGE;
			case ROOM_DINING:
				return DINING;
			case ROOM_KITCHEN:
				return KITCHEN;
			case ROOM_BALLROOM:
				return BALLROOM;
			case ROOM_CONSERVATORY:
				return CONSERVATORY;
			case ROOM_BILLIARD:
				return BILLIARD;
			case ROOM_STUDY:
				return STUDY;
			case ROOM_LIBRARY:
				return LIBRARY;
			}
			break;
		case TYPE_WEAPON:
			switch (value) {
			case WEAPON_KNIFE:
				return KNIFE;
			case WEAPON_ROPE:
				return ROPE;
			case WEAPON_REVOLVER:
				return REVOLVER;
			case WEAPON_WRENCH:
				return WRENCH;
			case WEAPON_PIPE:
				return PIPE;
			case WEAPON_CANDLE:
				return CANDLE;
			}
			break;
		}

		return null;
	}

	public int getType() {
		return this.type;
	}

	public int getValue() {
		return this.value;
	}

	@Override
	public int hashCode() {
		return this.type + this.value;
	}

	@Override
	public boolean equals(final Object obj) {
		if(obj == this)
			return true;

		if (obj instanceof Card) {
			final Card c = (Card) obj;
			return (c.type == this.type && c.value == this.value);
		} else {
			return false;
		}
	}

	public String getDescription() {
		return descriptions[this.value];
	}

	@Override
	public String toString() {
		String desc = null;

		switch (this.type) {
		case TYPE_SUSPECT:
			switch (this.value) {
			case SUSPECT_SCARLET:
				desc = SCARLET_NAME;
				break;
			case SUSPECT_PLUM:
				desc = PLUM_NAME;
				break;
			case SUSPECT_WHITE:
				desc = WHITE_NAME;
				break;
			case SUSPECT_MUSTARD:
				desc = MUSTARD_NAME;
				break;
			case SUSPECT_GREEN:
				desc = GREEN_NAME;
				break;
			case SUSPECT_PEACOCK:
				desc = PEACOCK_NAME;
				break;
			}
			break;
		case TYPE_ROOM:
			switch (this.value) {
			case ROOM_HALL:
				desc = "Hall";
				break;
			case ROOM_LOUNGE:
				desc = "Lounge";
				break;
			case ROOM_DINING:
				desc = "Dining Room";
				break;
			case ROOM_KITCHEN:
				desc = "Kitchen";
				break;
			case ROOM_BALLROOM:
				desc = "Ballroom";
				break;
			case ROOM_CONSERVATORY:
				desc = "Conservatory";
				break;
			case ROOM_BILLIARD:
				desc = "Billiard Room";
				break;
			case ROOM_STUDY:
				desc = "Study";
				break;
			case ROOM_LIBRARY:
				desc = "Library";
				break;
			}
			break;
		case TYPE_WEAPON:
			switch (this.value) {
			case WEAPON_KNIFE:
				desc = "Knife";
				break;
			case WEAPON_ROPE:
				desc = "Rope";
				break;
			case WEAPON_REVOLVER:
				desc = "Revolver";
				break;
			case WEAPON_WRENCH:
				desc = "Wrench";
				break;
			case WEAPON_PIPE:
				desc = "Pipe";
				break;
			case WEAPON_CANDLE:
				desc = "Candlestick";
				break;
			}
			break;
		}

		return desc;

	}

	@Override
	public int compareTo(final Card arg0) {
		if(this.getType() < arg0.getType())
			return -1;

		if(this.getType() > arg0.getType())
			return 1;

		if(this.getValue() < arg0.getValue())
			return -1;

		if(this.getValue() > arg0.getValue())
			return 1;

		return 0;
	}

}
