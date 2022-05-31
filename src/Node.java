/**
 * @file Node.java
 * @author Natasha Squires <nsquires@upei.ca>
 * Represents a Node for the 8 puzzle, which is the board state
 */
import java.util.*;

public class Node {
	private static final int ROWS=3;
	private static final int COLUMNS=3;
	private static final int BOARD=ROWS*COLUMNS;
	private State state; 
	private int fn, gn, hn; //A* cost stuff
	private Node parent; //"parent" node. Where the current node comes from

	public Node(State s){
		state = s;
		gn = 0;
		fn = 0;
		parent = null;
	}
	public Node(State s, int pathcost){
		state = s;
		gn = pathcost;
		fn = 0;
		parent = null;
	}

	public String convertToString(){
		String stringState = "";
		int[] currentState = state.getState();
		for(int i=0; i < BOARD; i++){
			if((i+1)%3==0)
				stringState+=currentState[i]+"\n";
			else
				stringState+=currentState[i]+" ";
		}
		
		return stringState;
	}
	public int hammingDistance(State goalState){
		hn = 0;
		int current[] = state.getState();
		int goal[] = goalState.getState();
		
		for(int i=0; i<BOARD; i++){
			if(current[i]!=goal[i]){
				hn++;
			}
		}
		return hn;
	}

	public int manhattanDistance(State goalState){
		hn=0;
		int current[] = state.getState();
		int goal[] = goalState.getState();
		int current2D[][] = new int[ROWS][COLUMNS];
		int goal2D[][] = new int[ROWS][COLUMNS];
		int pos = 0;
		
		//convert current and goal into a 2D array for easier calculation
		for(int i=0; i<ROWS; i++){
			for(int j=0; j<COLUMNS; j++){
				current2D[i][j]=current[pos];
				goal2D[i][j] = goal[pos];
				pos++;
			}
	    }
		
		//the manhatten distance
		//int val: n
		//val coord: (x1,y1)
		//find goalVal
		//goal coord: (x2,y2)
		//distance: |x1-x2| + |y1-y2|
		for(int i=0; i<ROWS; i++){
			for(int j=0; j<COLUMNS; j++){
				int val = current2D[i][j];
				//val coord
				int x1 = i;
				int y1 = j;
				//goal coord
				int x2=0;
				int y2=0;
				
				//find the goal coordinates
				for(int l=0; l<ROWS; l++){
					for(int m=0; m<COLUMNS; m++){
						int goalVal = goal2D[l][m];
						if(val==goalVal){
							x2=l;
							y2=m;
							break;
						}
					}
				}
				//calculates current value's manhattan distance
				hn +=  Math.abs(x1-x2) + Math.abs(y1-y2);
			}
		}
		
		return hn;
	}

	public ArrayList<Node> expandNodes(int heuristic, int cost, State goalState){
		ArrayList<Node> states = new ArrayList<Node>();
		State newState;
		
		//convert board to 2D array for easier calculation
		int[] current = state.getState();
		int[][] current2D = new int[ROWS][COLUMNS];
		int pos=0;
		for(int i=0; i<ROWS; i++){
			for(int j=0; j<COLUMNS; j++){
				current2D[i][j] = current[pos];
				pos++;
			}
		}
		//figure out the possible paths based on the 0 element
		//find 0
		//0 coords: x, y
		//check left, right, up, down
		//left: (x,y-1)
		//right: (x,y+1)
		//up: (x-1, y)
		//down: (x+1,y)
		for(int i=0; i<ROWS; i++){
			for(int j=0; j<COLUMNS; j++){
				int val = current2D[i][j];
				if(val==0){
					int x=i;
					int y=j;
					//slide left
					if(y-1 >= 0){
						newState = slideTile(x, y, x, y-1, current2D);
						
						states.add(new Node(newState, cost));
					}
					//slide right
					if(y+1 < COLUMNS){
						newState = slideTile(x, y, x, y+1, current2D);
						
						states.add(new Node(newState, cost));
					}
					//slide up
					if(x-1>=0){
						newState = slideTile(x,y,x-1,y, current2D);
						
						states.add(new Node(newState, cost));
					}
					//slide down
					if(x+1 < ROWS){
						newState = slideTile(x,y,x+1,y,current2D);
						
						states.add(new Node(newState, cost));
					}
				}
			}			
		}
		
		//calculate and set each state's f(n)
		//set the parent node of each neighbour to this
		for(int i=0; i<states.size(); i++){
			//if heuristic is hamming
			if(heuristic==0){
				states.get(i).hammingDistance(goalState);
				states.get(i).setFCost();
				states.get(i).setParentNode(this);
			}
			//if heuristic is manhattan
			if(heuristic==1){
				states.get(i).manhattanDistance(goalState);
				states.get(i).setFCost();
				states.get(i).setParentNode(this);
			}
		}
		
		states = mergeSort(states); //sort neighbours for priority/convenience
		return states;
	}

	public static ArrayList<Node> mergeSort(ArrayList<Node> list){
		if(list.size()<=1){
			return list;
		}
		ArrayList<Node> firstHalf = new ArrayList<Node>();
		ArrayList<Node> secondHalf = new ArrayList<Node>();
		for(int i=0; i < list.size()/2; i++){
			firstHalf.add(list.get(i));
		}
		
		for(int i=list.size()/2; i<list.size(); i++){
			secondHalf.add(list.get(i));
		}
		
		return merge(mergeSort(firstHalf), mergeSort(secondHalf));
	}

	public static ArrayList<Node> merge(ArrayList<Node> l1, ArrayList<Node> l2){
		if(l1.size()==0){
			return l2;
		}
		
		if(l2.size()==0){
			return l1;
		}
		
		ArrayList<Node> result = new ArrayList<Node>();
		Node nextElement;
		if(l1.get(0).getFCost() > l2.get(0).getFCost()){
			nextElement = l2.get(0);
			l2.remove(0);
		}else{
			nextElement = l1.get(0);
			l1.remove(0);
		}
		
		result.add(nextElement);
		result.addAll(merge(l1, l2));
		
		return result;
	}

	public State slideTile(int x1, int y1, int x2, int y2, int[][] current2D){
		State newState;
		int pos=0;
		int[][] configuration = new int[ROWS][COLUMNS];
		for(int l=0; l<ROWS; l++){
			for(int m=0; m<COLUMNS; m++){
				configuration[l][m] = current2D[l][m];
			}
		}
		//switch tile positions
		int temp = configuration[x1][y1];
		configuration[x1][y1] = configuration[x2][y2];
		configuration[x2][y2] = temp;
		//now convert configuration into a 1D array (complicated, I know)
		//maybe this wasn't the best design in the world okay?!
		int[] config = new int[BOARD];

		for(int l=0; l<ROWS; l++){
			for(int m=0; m<COLUMNS; m++){
				config[pos] = configuration[l][m];
				pos++;
			}
		}
		newState = new State(config);
		return newState;
	}

	public int getHn(){
		return hn;
	}
	

	public void setGn(int g){
		gn = g;
	}
	

	public int getGn(){
		return gn;
	}

	public void setFCost(){
		fn = gn+hn;
	}

	public void setFCost(int fc){
		fn = fc;
	}

	public int getFCost(){
		return fn;
	}

	public State getState(){
		return state;
	}

	public void setParentNode(Node node){
		parent = node;
	}

	public Node getParentNode(){
		return parent;
	}

	public String toString(){
		return state.toString();
	}
}
