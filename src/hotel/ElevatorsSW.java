package hotel;

import java.util.ArrayList;
import java.util.HashMap;

/* class implementing the software managing the elevators */
public class ElevatorsSW {

	private static final int MAX_DELAY = Integer.MAX_VALUE;
	private Building hotel;
	private int time;
	
	/* ordered by time list containing the requests waiting to be assigned */
	private ArrayList<Request> waitingRequests = new ArrayList<Request>();
	/* Hash table containing the request still on going */
	private HashMap<Integer,Request> currentRequests = new HashMap<Integer, Request>();
	/* Hash table containing the request already satisfied */
	private HashMap<Integer,Request> satisfiedRequests = new HashMap<Integer, Request>();
	
	public ElevatorsSW( Building h ) {
		super();
		hotel = h;
		time = 0;
	}
	
	/* make next step in the simulation */
	public void nextSimulationStep(){
		//System.out.println("Time "+ time);
		/* make next move for each elevator */
		for (Elevator e : hotel.getElevators()) {
			//System.out.println("Elevator "+ e.getElevatorId()+" is at floor "+e.getCurrentFloor());
			nextAction(e);
		}
		/* try to assign the requests still waiting */
		assignWaitingRequests();
		/* increment the time */
		time++;
	}
	
	/* method defining next action will be taken by the elevator */
	private void nextAction(Elevator e){
		
		if (e.isMoving() == true){
			if ( !e.isDoorOpening() ){
				/* check if elevator has reached a destination floor */
				checkStop(e);
				
				/* if door is not opening go to next floor */
				e.moveNext();
				
			}else{
				/* if door is opening decrement the elapsed time */
				e.decrementDoorElapsedTime();
				
			}
		}
	}
	
	
	/* try to assign the request to an elevator
	 * return true if assigned, false otherwise
	 */
	public boolean assignElevator(Request r){
		int extraTime; 
		int elevatorNb = -1;
		int minTime = MAX_DELAY;
		int count = 0;
		Elevator elevator;
		
		currentRequests.put(r.getRequestId(),r);
		
		for (Elevator e : hotel.getElevators()) {
			/* calculate the global delay introduced by assigning the request to this elevator 
			 * if possible to assign */
			extraTime = calculateGlobalDelay(e, r.getStart(), r.getDestination());
			/* select the minimum delay */
			if (extraTime<minTime && extraTime>-1){
				minTime=extraTime;
				elevatorNb = count;
			}
			count++;
		}
		
		
		if ( elevatorNb >= 0 ){
			elevator = hotel.getElevators().get(elevatorNb);
			//System.out.println(r+" assigned to elevator "+elevator.getElevatorId()+". Extra time: "+minTime);
			
			/* add stop to the selected elevator */
			elevator.addStop(r.getStart(),r);
			
			/* if elevator is not moving set the initial direction */
			if ( !elevator.isMoving() )
				elevator.setInitialDirection(r.getStart(),r.getDestination());
			return true;
		}else{
			/* request cannot be satisfied right now: add it to the waiting list */
			if ( !waitingRequests.contains(r) ){
				//System.out.println(r+" cannot be satisfied at the moment");
				waitingRequests.add(r);
			}
			return false;
		}
		
	}
	
	/* try to assign waiting requests to an elevator 
	 * return the number of still unassigned requests
	 */
	private int assignWaitingRequests() {
		
		/* assign unsatisfied requests if possible */
		ArrayList<Request> copyOfWaitingRequest = new ArrayList<Request>(waitingRequests);
		
		for (Request req : copyOfWaitingRequest) {
			if ( assignElevator(req) ){
				waitingRequests.remove(req);
			}
		}
		
		return waitingRequests.size();
	}

	
	/* calculate the global delay introduced by the new request */
	private int calculateGlobalDelay ( Elevator e, int start, int dest ){
		int time = 0;
		
		/* calculate delay caused to the request if assigned to this elevator */
		time += calculateExtraTime ( e, start, dest );
		
		/* if elevator cannot be assigned, return */
		if ( time == -1 ) return -1;
		
		/* calculate time introduced assigning the request to this elevator */
		time += calculateExtraTimeAdded(e, start, dest);
	
		return time;
	}

	
	/* calculate extra time necessary to satisfy the request
	 * return -1 if elevator cannot satisfy the request */
	private int calculateExtraTime ( Elevator e, int start, int dest ){
		int time = 0;
		
		/* if the elevator is moving, calculate the delay introduced by the stop
		 * already planned */
		if (e.isMoving() == true){
			
			/* check if elevator can take the request: take only requests
			 * having the same direction */
			if ( e.isDirection() == Elevator.UP && dest>start ){
				if ( start < e.getCurrentFloor()){
					/* elevator already passed this floor, do not take the request */
					return -1;
				}else{
					/* add time spent keeping the door open when reaching a destination in between */
					for (Integer s : e.getStops().keySet()) {
						if ( s < dest && s > e.getCurrentFloor() ){ 
							/* do not consider the time of opening the door on the starting floor */
							if (!s.equals(start)) time += e.OPENING_DOOR_TIME;
						}
						/* if the request is not already taken, consider its destination (it is not already added in the stops) */
						for (Request r : e.getStops().get(s)) {
							if ( r != null ){
								if ( !r.isRequestTaken() ){
									/* if the destination will be reached before reaching the destination of the request */
									if ( r.getDestination() < dest && 
										 r.getDestination() > e.getCurrentFloor() && 
										 r.getDestination() > r.getStart()){
										time += e.OPENING_DOOR_TIME;
									}
								}
							}
						}
					}
				}
			}else if ( e.isDirection() == Elevator.DOWN && dest<start ){
				if ( start > e.getCurrentFloor() ){
					/* elevator already passed this floor */
					return -1;
				}else{
					/* add time spent keeping the door open when reaching a destination in between */
					for (Integer s : e.getStops().keySet()) {
						if ( s > dest && s < e.getCurrentFloor() ){ 
							/* do not consider the time of opening the door on the starting floor */
							if (!s.equals(start)) time += e.OPENING_DOOR_TIME;
						}
						/* if the request is not already taken, consider its destination (it is not already added in the stops) */
						for (Request r : e.getStops().get(s)) {
							if ( r != null ){
								if ( !r.isRequestTaken() ){
									/* if the destination will be reached before reaching the destination of the request */
									if ( r.getDestination() > dest && 
										 r.getDestination() < e.getCurrentFloor() && 
										 r.getDestination() < r.getStart()){
										time += e.OPENING_DOOR_TIME;
									}
								}
							}
						}
					}
				}
			}else{
				/* elevator cannot satisfy the request */
				return -1;
			}

		}
		
		/* add time waiting at current floor */
		time += e.getDoorOpenedElapsedTime();
		
		/* add time needed to reach starting floor */
		time += Math.abs(e.getCurrentFloor() - start);
	
		return time;
	}
	
	/* calculate extra time added to other stops by adding this stop */
	private int calculateExtraTimeAdded(Elevator e, int start, int dest) {
		int time = 0;
		
		/* if elevator is not moving there are no other stops */
		if (e.isMoving() == true){
			
			if ( e.isDirection() == Elevator.UP ){
				/* for each stop consider added delay */
				for (Integer s : e.getStops().keySet()) {
					/* if stop is after the destination and/or the start of the request, 
					 * add the delay of opening the door
					 */
					if ( s > dest && !e.getStops().containsKey(dest)){ 
						time += e.OPENING_DOOR_TIME;
					}
					if ( s > start  && !e.getStops().containsKey(start)){ 
						time += e.OPENING_DOOR_TIME;
					}
					
					/* consider the delay for the future stops scheduled, too */
					for (Request r : e.getStops().get(s)) {
						if ( r != null ){
							if ( !r.isRequestTaken() ){
								/* if the destination will be reached before reaching the destination of the request */
								if ( r.getDestination() < dest && 
									 r.getDestination() > e.getCurrentFloor() && 
									 r.getDestination() > r.getStart()){
									time += e.OPENING_DOOR_TIME;
								}
							}
						}
					}
					
				}
			}else{
				/* for each stop consider added delay */
				for (Integer s : e.getStops().keySet()) {
					/* if stop is after the destination and/or the start of the request, 
					 * add the delay of opening the door
					 */
					if ( s < dest && !e.getStops().containsKey(dest)){ 
						time += e.OPENING_DOOR_TIME;
					}
					if ( s < start  && !e.getStops().containsKey(start)){ 
						time += e.OPENING_DOOR_TIME;
					}
					
					/* consider the delay for the future stops scheduled, too */
					for (Request r : e.getStops().get(s)) {
						if ( r != null ){
							if ( !r.isRequestTaken() ){
								/* if the destination will be reached before reaching the destination of the request */
								if ( r.getDestination() > dest && 
									 r.getDestination() < e.getCurrentFloor() && 
									 r.getDestination() < r.getStart()){
									time += e.OPENING_DOOR_TIME;
								}
							}
						}
					}
				}
				
			}

		}
		
		return time;
	}
	
	/* check if the elevator reached a scheduled stop */
	private void checkStop(Elevator e) {
		
		if ( e.getStops().containsKey(e.getCurrentFloor()) ){
			//System.out.println("Elevator "+e.getElevatorId()+" stops at floor "+e.getCurrentFloor());
			
			/* check requests at this stop */
			for (Request r : e.getStops().get(e.getCurrentFloor())) {
				if ( r != null ){
					if ( r.getStart() == e.getCurrentFloor() ){
						/* elevator has reached the starting floor for the request: add the destination stop */
						r.setTimePickedUp(time);
						r.setRequestTaken(true);
						e.addStop(r.getDestination(), r);
					}
					if ( r.getDestination() == e.getCurrentFloor() && r.isRequestTaken() ){
						/* elevator has delivered the request */
						//System.out.println(r+" from floor "+r.getStart()+" to floor "+r.getDestination()+" satisfied in "+(time-r.getTimeRequest()));
						
						r.setRequestSatisfied(true);
						r.setTimeArrival(time);
						satisfiedRequests.put(r.getRequestId(), r);
						currentRequests.remove(r.getRequestId());
						
						/* collect statistics for the request */
						//int waitingTime = (r.getTimePickedUp()-r.getTimeRequest());
						//int delayAfterPickUp = (r.getTimeArrival()-r.getTimePickedUp()-(Math.abs(r.getDestination()-r.getStart())+1)-e.OPENING_DOOR_TIME);
						//int overallDelay = waitingTime + delayAfterPickUp;
						
						//System.out.println("Waiting time: "+waitingTime+", delay after pick up: "+delayAfterPickUp+", overall delay: "+overallDelay);
					}
				}
			}
			
			/* open the door and remove the stop */
			e.openDoor();
			e.getStops().remove(e.getCurrentFloor());
		}
		
	}
	
	
	public int getTime() {
		return time;
	}
	
	/* return if there are still requests to be satisfied */
	public boolean elaboratingRequests(){
		return !currentRequests.isEmpty();
	}

	/* print final statistics */
	public void printStatistics() {
		int totalTimeToBePicked = 0;
		int reqNumber = 0;
		int totalDelayAfterPick = 0;
		double[] waitTimeArray = new double[satisfiedRequests.size()];
		int waitTimeToBePicked = 0;
		int delayAfterPick = 0;
		
		for (Request r : satisfiedRequests.values()) {
			waitTimeToBePicked = r.getTimePickedUp()-r.getTimeRequest();
			delayAfterPick = (r.getTimeArrival()-r.getTimePickedUp()-(Math.abs(r.getDestination()-r.getStart())+1)-10);
			totalTimeToBePicked += waitTimeToBePicked;
			totalDelayAfterPick += delayAfterPick;
			waitTimeArray[reqNumber] = (double) (totalTimeToBePicked+totalDelayAfterPick);
			reqNumber++;
		}
		
		double averageTimeToBePicked = totalTimeToBePicked/reqNumber;
		double averageDelayAfterPick = totalDelayAfterPick/reqNumber;
				
		System.out.println("average time before being picked up: "+averageTimeToBePicked);
		System.out.println("average delay after being picked up: "+averageDelayAfterPick);
		System.out.println("average wait time: "+(averageTimeToBePicked+averageDelayAfterPick));
		System.out.println("standard deviation: "+(calculateStandardDeviation(waitTimeArray,(double) (averageTimeToBePicked+averageDelayAfterPick))));
		System.out.println("total requests: "+reqNumber);
	}

	private static double calculateStandardDeviation ( double[] data, double avg ) { 
		final int n = data.length; 

		double sum = 0; 
		
		for ( int i = 0; i < data.length; i++ ) { 
			sum += ( data[i] - avg ) * ( data[i] - avg ) ; 
		}  
		
		return Math.sqrt( sum / ( n ) ); 
	} 

}
