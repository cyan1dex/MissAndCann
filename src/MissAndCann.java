import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.Random;

public class MissAndCann {

	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
		// Depth-First (use Stack via recursion)
		System.out.println("DEPTH-FIRST SEARCH");
		State startState = State.makeStartState();
		startState.output(0,0);
		solveDepthFirst(startState);
		showSummary("Depth-First");
		
/*		// Breadth-First (use Queue)
		Queue<State> q = new ArrayBlockingQueue<State>(20); 
		startState = State.makeStartState();
		q.add(startState);
		
		System.out.println("BREADTH-FIRST SEARCH");
		startState.output(0,0);
		solveBreadthFirst(q);
		showSummary("Breadth-First");

		// Randomized
		Random generator = new Random();
		
		System.out.println("RANDOM SEARCH");
		startState = State.makeStartState();
		startState.output(0,0);
		solveRandom(startState, generator);
		showSummary("Random");*/

	}
	
	private static void solveDepthFirst(State s)
	{
		if(s == null || State.isSolved()) return;
		State next = null;
		
		for(State.BoatOccupants occ : SEATING)
		{
			next = s.move(occ, true);
			if(State.isSolved()) return;
			if(next != null) 
			{
				solveDepthFirst(next);
				if(State.isSolved()) return;
			}
		}
	}
	
	private static void solveBreadthFirst(Queue<State> q)
	{	
		while(!q.isEmpty() && !State.isSolved())
		{
			State s = q.remove(), next = null;
			
			for(State.BoatOccupants occ : SEATING)
			{
				next = s.move(occ, true);
				if(State.isSolved()) break;
				if(next != null) q.add(next);
			}
		}
			
	}
	
	private static void solveRandom(State s, Random generator)
	{	
		if(State.askNumTrips() >= 500) return;
		while(!State.isSolved())
		{
			int guess = generator.nextInt(SEATING.length);
						
			State next = s.move(SEATING[guess], false);
			if(State.isSolved()) return;
			if(next != null) solveRandom(next, generator);
			if(State.isSolved()) return;
		}
	}
	
	private static void showSummary(String text)
	{
		System.out.print(text + "Search Number of trips: " + State.askNumTrips());
		System.out.println(", Number of generated states: " + State.askNumGeneratedStates() + "\n");
	}
	
	private static final State.BoatOccupants[] SEATING = State.BoatOccupants.values();
							
}
