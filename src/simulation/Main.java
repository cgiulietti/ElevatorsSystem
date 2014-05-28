package simulation;

import hotel.Building;
import hotel.ElevatorsSW;
import hotel.Request;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

public class Main {

	public static void main(String[] args) {
		String fileName = args[0];
		String line = null;
		int time, startFloor, destFloor;

		/* initialize hotel: max floor 100
		 * 					 min floor 0
		 * 					 4 elevators
		 * 					 10 seconds to open the door  */
		Building DrumpGalactic = new Building(100, 0, 4, 10);
		ElevatorsSW sw = new ElevatorsSW(DrumpGalactic);
		
		try {
			
			BufferedReader input =  new BufferedReader(new FileReader(fileName));
			while (( line = input.readLine() ) != null){
				/* Read and parse input */
				StringTokenizer tok = new StringTokenizer(line,",");
				time = Integer.valueOf(tok.nextToken());
				startFloor = Integer.valueOf(tok.nextToken());
				destFloor = Integer.valueOf(tok.nextToken());
				
				/* if input moved to next time slice run next simulation step */
				while ( time > sw.getTime() ) sw.nextSimulationStep();
				
				Request request = new Request(startFloor, destFloor, time);
				//System.out.println("New request: "+request.getTimeRequest()+","+request.getStart()+","+request.getDestination());
				
				/* assign the request to an elevator if possible */
				sw.assignElevator(request);
			}
			input.close();
			
			/* keep running the simulation until all requests are satisfied */
			while ( sw.elaboratingRequests() ) sw.nextSimulationStep();
		
			/* print statistics in output */
			sw.printStatistics();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
