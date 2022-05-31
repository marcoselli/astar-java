/**
 * @file State.java
 * @author Natasha Squires <nsquires@upei.ca>
 * Represents the current state of the board for a particular node
 */
public class State {
	//size of the board
	private final static int BOARD=9;
	//state of the board
	private int[] currentState = new int[BOARD];

	public State(int[] s){
		for(int i=0; i<BOARD; i++){
			currentState[i]=s[i];
		}
	}

	public int[] getState(){
		return currentState;
	}
	

	public boolean equals(State other){
		int[] otherState = other.getState();
		
		for(int i=0; i<BOARD; i++){
			if(otherState[i]!=currentState[i]){
				return false;
			}
		}
		
		return true;
	}

	public String toString(){
		String result = "";
		for(int i=0; i<BOARD; i++){
			if((i+1)%3==0)
				result += currentState[i] + "\n";
			else
				result += currentState[i] + " ";
		}
		
		return result;
	}
}
