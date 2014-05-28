package hotel;

import java.util.ArrayList;
import java.util.TreeMap;

/* class of the elevator */
public class Elevator {
	
	public static final boolean UP = true;
	public static final boolean DOWN = false;
	private static final int MAX_IDLE_TIME = 5;
	
	private static int nextElevatorId = 0;
	
	final int OPENING_DOOR_TIME;
	final int MAX_FLOOR;
	final int MIN_FLOOR;
	
	private int elevatorId;
	private int currentFloor;
	private boolean direction;
	private boolean moving;
	private int doorOpenedElapsedTime;
	private int idleTime;
	/* Tree containing the stops assigned to the elevator */
	private TreeMap<Integer,ArrayList<Request>> stops = new TreeMap<Integer, ArrayList<Request>>();
	
	public Elevator( int doorTime, int maxFloor, int minFloor) {
		super();
		currentFloor = 1;
		moving = false;
		OPENING_DOOR_TIME = doorTime;
		MAX_FLOOR = maxFloor;
		MIN_FLOOR = minFloor;
		elevatorId = nextElevatorId++;
		doorOpenedElapsedTime = 0;
		idleTime = 0;
	}

	/* make next move of the elevator */
	void moveNext(){
		/* if door is not opening make next move */
		if ( !isDoorOpening() ){
			if ( stops.isEmpty() ){
				/* no more stops assigned: set the elevator to idle */
				moving = false;
				//System.out.println("Elevator "+elevatorId+" stays idle");
				idleTime++;
				
				/* if the elevator is idle for too much, it moves to bottom or top, which one is closer */
				if (idleTime >= MAX_IDLE_TIME){
					if ( currentFloor != MAX_FLOOR && currentFloor != MIN_FLOOR){
						int distanceFromTop = MAX_FLOOR-currentFloor;
						int distanceFromBottom = currentFloor - MIN_FLOOR;
						
						if ( distanceFromTop > distanceFromBottom ){
							addStop(MAX_FLOOR, null);
							//System.out.println("No other requests assigned: Elevator "+elevatorId+" heading to floor "+MAX_FLOOR);
						}else{
							addStop(MIN_FLOOR, null);
							//System.out.println("No other requests assigned: Elevator "+elevatorId+" heading to floor "+MIN_FLOOR);
						}
						moving = true;
						idleTime=0;
					}
				}
			}else{
				/* elevator moves to the current direction until there is a stop in such direction
				 * Otherwise it changes direction, going towards remaining stops
				 */
				if ( isDirection() == UP ){
					if (currentFloor < MAX_FLOOR ){
						if ( currentFloor < stops.lastKey() ){
							currentFloor++;
						}else{
							direction = DOWN;
							//System.out.println("Elevator "+elevatorId+" goes DOWN");
						}
					}else{
						direction = DOWN;
						//System.out.println("Elevator "+elevatorId+" goes DOWN");
					}
				}else{
					if (currentFloor > MIN_FLOOR){
						if ( currentFloor > stops.firstKey() ){
							currentFloor--;
						}else{
							direction = UP;
							//System.out.println("Elevator "+elevatorId+" goes UP");
						}
					}else{
						direction = UP;
						//System.out.println("Elevator "+elevatorId+" goes UP");
					}
				}
			}
		}
	}
	
	/* add a stop to the elevator and the related request */
	void addStop(Integer stop, Request r) {
		ArrayList<Request> reqList = stops.get(stop);
		if (reqList!=null){
			reqList.add(r);
		}else{
			reqList = new ArrayList<Request>();
			reqList.add(r);
		}
		stops.put(stop, reqList);
	}

	/* set the direction of the elevator when a new request is assigned from idle */
	void setInitialDirection(int start, int destination) {
		if ( start != currentFloor ){
			if ( start > currentFloor ){
				direction = UP;
				//System.out.println("Elevator "+elevatorId+" is going UP");
			}else{
				direction = DOWN;
				//System.out.println("Elevator "+elevatorId+" is going DOWN");
			}
		}else{
			if ( destination > currentFloor ){
				direction = UP;
				//System.out.println("Elevator "+elevatorId+" is going UP");
			}else{
				direction = DOWN;
				//System.out.println("Elevator "+elevatorId+" is going DOWN");
			}
		}
		moving = true;
	}
	
	void openDoor(){
		doorOpenedElapsedTime = OPENING_DOOR_TIME;
		//System.out.println("Elevator "+elevatorId+" is opening the door");
	}
	
	boolean isDoorOpening(){
		if (doorOpenedElapsedTime > 0){
			return true;
		}else{
			return false;
		}
	}
	
	int getCurrentFloor() {
		return currentFloor;
	}

	boolean isDirection() {
		return direction;
	}

	void setDirection(boolean direction) {
		this.direction = direction;
	}
	
	boolean isMoving() {
		return moving;
	}

	public int getElevatorId() {
		return elevatorId;
	}

	void decrementDoorElapsedTime(){
		//System.out.println("Elevator "+elevatorId+": door will close in "+doorOpenedElapsedTime);
		doorOpenedElapsedTime--;
	}

	TreeMap<Integer, ArrayList<Request>> getStops() {
		return stops;
	}

	int getDoorOpenedElapsedTime() {
		return doorOpenedElapsedTime;
	}

	void setMoving(boolean moving) {
		this.moving = moving;
	}
	
}
