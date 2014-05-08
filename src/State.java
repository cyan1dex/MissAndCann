import java.util.HashSet;
import java.util.Set;
import java.lang.Integer;

public class State 
{
	public enum BoatPos { LEFT, RIGHT };
	public enum BoatOccupants { TWO_MISS, TWO_CANN, ONE_EACH, ONE_MISS, ONE_CANN };
	
	////////////////////////////// Static Methods /////////////////////////////////////////
	
	public static State makeStartState()
	{
		knownStates = new HashSet<Integer>();
		solved = false;
		numTrips = 0;
		numGeneratedStates = 0;
		
		State start = new State();
		start.checkSolved();
		start.markAsVisited();
		
		return start;
	}
	
	public static int askNumTrips()
	{
		return numTrips;
	}
	
	public static int askNumGeneratedStates()
	{
		return numGeneratedStates;
	}
	
	public static boolean isSolved()
	{
		return solved;
	}
	
////////////////////////////// Public Instance Methods //////////////////////////////////
	
	public State move(State.BoatOccupants occupants, boolean check_visited)
	{
		int numMissBoat = 0, numCannBoat = 0;
		
		switch(occupants)
		{
			case TWO_MISS:
			{
				numMissBoat = 2;
				numCannBoat = 0;
			}
			break;
					
			case TWO_CANN:
			{
				numMissBoat = 0;
				numCannBoat = 2;
			}
			break;
			
			case ONE_MISS:
			{
				numMissBoat = 1;
				numCannBoat = 0;
			}
			break;
			
			case ONE_CANN:
			{
				numMissBoat = 0;
				numCannBoat = 1;
			}
			break;
			
			case ONE_EACH:
			{
				numMissBoat = 1;
				numCannBoat = 1;
			}
			break;
			
			default:
				throw new IllegalArgumentException();
		}
		
		State oldState = new State(this);
		State newState = new State(this);
		
		if(newState._boatPos == State.BoatPos.LEFT)
		{
			// Move left to right
			newState._boatPos = State.BoatPos.RIGHT;
			newState._numMissLeft -= numMissBoat;
			newState._numMissRight += numMissBoat;
			newState._numCannLeft -= numCannBoat;
			newState._numCannRight += numCannBoat;		
		}
		else 
		{
			// Move right to left
			newState._boatPos = State.BoatPos.LEFT;
			newState._numMissLeft += numMissBoat;
			newState._numMissRight -= numMissBoat;
			newState._numCannLeft += numCannBoat;
			newState._numCannRight -= numCannBoat;	
		}			
		
		newState.setValidity();
		++numGeneratedStates;
		
		if( !newState._valid || newState.alreadyVisited() ) return null;
		else
		{
			oldState.output(numMissBoat, numCannBoat);
			newState.output(0,0);
			if(check_visited) newState.markAsVisited();
			newState.checkSolved();
			++numTrips;
						
		    return newState;
		}
	}
	
	public void output(int numMissBoat, int numCannBoat)
	{
		if(_boatPos == State.BoatPos.LEFT)
		{
			_numMissLeft -= numMissBoat;
			_numCannLeft -= numCannBoat;
		}
		else
		{
			_numMissRight -= numMissBoat;
			_numCannRight -= numCannBoat;
		}
		
		if(_numMissLeft == 0 && _numCannLeft == 0) System.out.print("   ");
		else
		{
			for(int ii = 0; ii < _numMissLeft; ++ii) System.out.print("M");
			for(int ii = 0; ii < _numCannLeft; ++ii) System.out.print("C");
		}
		
		if(_boatPos == State.BoatPos.LEFT) outputOccupants(numMissBoat, numCannBoat); 
		
		System.out.print("   ");
		
		if(_boatPos == State.BoatPos.RIGHT) outputOccupants(numMissBoat, numCannBoat); 
		
		for(int ii = 0; ii < _numMissRight; ++ii) System.out.print("M");
		for(int ii = 0; ii < _numCannRight; ++ii) System.out.print("C");
		
		System.out.println();
	}

	//////////////////////////////////////// Private Methods //////////////////////////////////
	private State()
	{
		this(3,3,0,0,State.BoatPos.LEFT);
	}
	
	private State(State s)
	{
		this(s._numMissLeft, s._numCannLeft, s._numMissRight, s._numCannRight, s._boatPos);
	}
	
	private State(int numMissLeft, int numCannLeft, int numMissRight, int numCannRight, State.BoatPos boatPos)
	{
		_numMissLeft = numMissLeft;
		_numCannLeft = numCannLeft;
		_numMissRight = numMissRight;
		_numCannRight = numCannRight;
		_boatPos = boatPos;
		_valid = true;
	}
	
	private void setValidity()
	{
		_valid = true;
		
		if(_numMissLeft < 0 || _numMissRight < 0 || _numCannLeft < 0 || _numCannRight < 0) _valid = false;
		if(_numMissLeft != 0 && _numMissLeft < _numCannLeft) _valid = false;
		if(_numMissRight != 0 && _numMissRight < _numCannRight) _valid = false;
	}
	
	private void checkSolved()
	{
		if( solved ) return;
		
		if(_numMissLeft == 0 && _numCannLeft == 0 &&
		   _numMissRight == 3 && _numCannRight == 3 ) solved = true;
		else solved = false;
	}
	
	private void outputOccupants(int numMissBoat, int numCannBoat)
	{
		int total = numMissBoat + numCannBoat;
		if(total > 0 && _boatPos == State.BoatPos.RIGHT) System.out.print("<--");
		System.out.print("[");
		
		for(int ii = 0; ii < numMissBoat; ++ii) System.out.print("M");
		for(int ii = 0; ii < numCannBoat; ++ii) System.out.print("C");
		
		System.out.print("]");
		if(total > 0 && _boatPos == State.BoatPos.LEFT) System.out.print("-->");
	}
		
	private int makeKey()
	{
		int bank;
		
		if(_boatPos == State.BoatPos.LEFT) bank = 0;
		else                               bank = 1;
		
		// Use radix 4 as greatest value in any column is 3
		return _numMissLeft * 256 + _numCannLeft * 64 + _numMissRight * 16 + _numCannRight * 4 + bank;
	}
	
	private boolean alreadyVisited()
	{
		int key = makeKey();
		
		return knownStates.contains(key);
	}
	
	private void markAsVisited()
	{
		int key = makeKey();
		
		knownStates.add(key);
	}
	
	private int _numMissLeft;
	private int _numCannLeft;
	private int _numMissRight;
	private int _numCannRight;
	private State.BoatPos _boatPos;
	private boolean _valid;
	
	private static Set<Integer> knownStates;
	private static int numTrips;
	private static boolean solved;
	private static int numGeneratedStates;
	
}
