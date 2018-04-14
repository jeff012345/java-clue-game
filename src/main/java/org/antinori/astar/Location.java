package org.antinori.astar;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.antinori.game.Card;

public class Location implements Node<Location>, Serializable {

	private static final long serialVersionUID = -6775415845109915655L;

	private final int x;
	private final int y;
	private int height;
	private boolean blocked;
	private boolean highlight;
	private boolean isRoom;
	private int roomId = -1;
	private Color color = Color.gray;

	private long visited;

	private transient List<Location> neighbors;

	private ArrayList<Location> realNeighbors = null;

	public Location(final int x, final int y) {
		this.x = x;
		this.y = y;
		this.neighbors = new ArrayList<>();
	}

	public Location(final int x, final int y, final Color color) {
		this.x = x;
		this.y = y;
		this.color = color;
		this.neighbors = new ArrayList<>();
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	@Override
	public int hashCode() {
		return this.x * this.y;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof Location) {
			final Location loc = (Location) obj;
			return (loc.getX() == this.x && loc.getY() == this.y);
		} else {
			return false;
		}
	}

	public void setBlocked(final boolean blocked) {
		this.blocked = blocked;
	}

	public boolean getBlocked() {
		return this.blocked;
	}

	public void setHighlighted(final boolean highlight) {
		this.highlight = highlight;
	}

	public boolean getHighlighted() {
		return this.highlight;
	}

	public void setIsRoom(final boolean isRoom) {
		this.isRoom = isRoom;
	}

	public boolean isRoom() {
		return this.isRoom;
	}

	public void setRoomId(final int id) {
		this.roomId = id;
	}

	public int getRoomId() {
		return this.roomId;
	}

	public Card getRoomCard() {
		return (this.roomId != -1 ? Card.getInstance(Card.TYPE_ROOM, this.roomId) : null);
	}

	public Color getColor() {
		return this.color;
	}

	public void setColor(final Color color) {
		this.color = color;
	}

	public int getHeight() {
		return this.height;
	}

	public void setHeight(final int height) {
		this.height = height;
	}

	public double getDistance(final Location dest) {
		final double a = dest.x - this.x;
		final double b = dest.y - this.y;
		return Math.sqrt(a * a + b * b);
	}

	@Override
	public double pathCostEstimate(final Location goal) {
		return this.getDistance(goal) * 0.99;
	}

	@Override
	public double traverseCost(final Location target) {
		final double distance = this.getDistance(target);
		final double diff = target.getHeight() - this.getHeight();
		return Math.abs(diff) + distance;
	}

	/**
	 * @return returns neighbors that are not blocked
	 */
	 @Override
	 public Iterable<Location> neighbors() {
		if(this.realNeighbors != null) {
			return this.realNeighbors;
		}

		this.realNeighbors = new ArrayList<>();
		if (!this.blocked) {
			for (final Location loc : this.neighbors) {
				if (!loc.blocked) {
					this.realNeighbors.add(loc);
				}
			}
		}

		return this.realNeighbors;
	 }

	 public void addNeighbor(final Location l) {
		 this.neighbors.add(l);
	 }

	 public void removeNeighbor(final Location l) {
		 this.neighbors.remove(l);
	 }

	 @Override
	 public String toString() {
		 return "Location [" + this.x + "][" + this.y + "]; Room = " + (this.isRoom ? this.getRoomCard().toString() : this.roomId);
	 }

	 public boolean visit(final long vistorId) {
		 if(vistorId == this.visited) {
			 return true;
		 }

		 this.visited = vistorId;
		 return false;
	 }

}
