package hotel;

import java.util.ArrayList;

/* class of the hotel */
public class Building {
	
	final int MAX_FLOOR;
	final int MIN_FLOOR;
	
	/* elevators of the hotel */
	private ArrayList<Elevator> elevators = new ArrayList<Elevator>();

	public Building( int maxFloor, int minFloor, int nbElevators, int doorTime) {
		super();
		MAX_FLOOR = maxFloor;
		MIN_FLOOR = minFloor;
		for (int i = 0; i < nbElevators; i++) {
			elevators.add(new Elevator(doorTime, maxFloor, minFloor));
		}
	}

	public ArrayList<Elevator> getElevators() {
		return elevators;
	}
	
}
