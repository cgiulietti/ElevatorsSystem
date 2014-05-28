package hotel;

/* class of the request */
public class Request{

	private static int nextRequestId = 0;
	
	private int startFloor;
	private int destinationFloor;
	private boolean requestTaken;
	private boolean requestSatisfied;
	private int timeRequest;
	private int timePickedUp;
	private int timeArrival;
	private int requestId;
	
	public Request(int startFloor, int destinationFloor, int timeRequest) {
		super();
		this.startFloor = startFloor;
		this.destinationFloor = destinationFloor;
		this.setRequestSatisfied(false);
		this.timeRequest = timeRequest;
		this.requestId = nextRequestId++;
	}
	
	
	public int getTimeArrival() {
		return timeArrival;
	}
	public void setTimeArrival(int timeArrival) {
		this.timeArrival = timeArrival;
	}
	public int getTimeRequest() {
		return timeRequest;
	}
	
	public int getStart() {
		return startFloor;
	}
	public int getDestination() {
		return destinationFloor;
	}

	@Override
	public String toString() {
		return "Request "+requestId+" ("+startFloor+" to "+destinationFloor+")";
	}


	public int getRequestId() {
		return requestId;
	}


	public boolean isRequestSatisfied() {
		return requestSatisfied;
	}


	public void setRequestSatisfied(boolean requestSatisfied) {
		this.requestSatisfied = requestSatisfied;
	}


	public boolean isRequestTaken() {
		return requestTaken;
	}


	public void setRequestTaken(boolean requestTaken) {
		this.requestTaken = requestTaken;
	}


	public int getTimePickedUp() {
		return timePickedUp;
	}


	public void setTimePickedUp(int timePickedUp) {
		this.timePickedUp = timePickedUp;
	}
	
	
}
