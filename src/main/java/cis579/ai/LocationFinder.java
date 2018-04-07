package cis579.ai;

import java.util.Collection;
import java.util.LinkedList;
import java.util.UUID;

import org.antinori.astar.Location;

public class LocationFinder {
	
	public static Collection<Location> findChoices(Location start, int roll) {
		long vistorId = UUID.randomUUID().getMostSignificantBits();
		start.visit(vistorId);
		
		Collection<Location> choices = graphSearch(vistorId, start, roll);
		
		return choices;
	}
	
	private static Collection<Location> graphSearch(final long vistorId, final Location start, final int roll) {
		final Collection<Location> choices = new LinkedList<>();
		
		if(roll == 0) {
			choices.add(start);
			return choices;
		}
		
		start.neighbors().forEach(location -> {
			if(!location.visit(vistorId)) {
				// was not visited yet
				if(start.isRoom()) {
					choices.add(location);
				} else {
					choices.addAll(graphSearch(vistorId, location, roll - 1));
				}
			}
		});
		
		return choices;
	}
	
}
