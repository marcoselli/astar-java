/**
 * @file AStar.java
 * @author Natasha Squires <nsquires@upei.ca>
 * Implements the A* algorithm 
 */
import java.util.*;

public class AStar {
	//board size
	private final static int BOARD=9;
	//represents the goal of the puzzle
	private int[] goalState = {0,1,2,3,4,5,6,7,8};
	private State goal;
	//The start state, which will be randomly shuffled and solved
	private int[] startState;
	private State start;
	//open list for the A* search
	private ArrayList<Node> open;
	//closed list for the A* search
	private ArrayList<Node> closed;
	//explored list for the A* search;
	private HashMap<String, String> explored;
	//heuristic choice specified by "user"
	private int heuristicChoice;
	//path cost for path finding
	private int pathCost;

	public AStar(int hc){
		open = new ArrayList<Node>(); //empty open set
		closed = new ArrayList<Node>(); //empty closed set
		explored = new HashMap<String, String>(); //empty explored set
		goal = new State(goalState);
		startState = new int[BOARD];
		shuffleBoard();
		heuristicChoice = hc;
		pathCost=0;
	}
	

	public void shuffleBoard(){
		Random gen = new Random();
		//temporarily feed in goal state values
		for(int i=0; i<BOARD; i++){
			startState[i]=goalState[i];
		}
		
		//the fisher-yates algorithm
		for(int i=BOARD-1; i>=1; i--){
			int j = gen.nextInt(i);
			
			int temp = startState[j];
			startState[j]=startState[i];
			startState[i] = temp;
		}
		
		//create the start state here
		start = new State(startState);
	}
	

	public void AStarSearch(){
		//creating a node containing the goal state
		//Node goalNode = new Node(goal);
		//creating a node containing the start state
		Node startNode = new Node(start);
		System.out.println("Start: \n" + startNode);
		
		//Putting the start node on the open list/frontier
		open.add(startNode);
		
		//Estimated total cost from the start 
		//note: start state will have a G(n) val of 0, naturally
		heuristic(startNode);
		startNode.setFCost();
		
		
		//while the frontier is not the empty set
		while(!open.isEmpty()){
			//find node with lowest F(n) cost 
			Node current;
			int pos=0;
			int lowest = open.get(pos).getFCost();
			for(int i=0; i<open.size(); i++){
				if (open.get(i).getFCost() < lowest){
					lowest = open.get(i).getFCost();
					pos = i;
				}
			}
			//this is the node we found it
			current = open.get(pos);
			
			//is this node's state the same as the GOAL state?!?!
			if(current.getState().equals(goal)){
				//solved!
				explored.put(current.getParentNode().convertToString(), current.convertToString());
				String path = printPath(explored, current);
				//print out the solution
				System.out.println("Path: \n" + path);
				System.out.println(current.convertToString());
				break;
			}
			
			//remove current from the frontier
			open.remove(current);
			//add current to closed set
			closed.add(current);
			
			//increase pathCost since we have to expand the path
			//note: the distance from current to a neighbour will just
			//      be 1 in the case of the 8 puzzle
			pathCost = current.getGn()+1;
			//get current's state expansion. This will return a list of prioritised neighbours to explore
			ArrayList<Node> neighbourNodes = expand(current, pathCost);
			
			//for neighbour in neighbourNodes
			for(int i=0; i<neighbourNodes.size(); i++){
				Node neighbour = neighbourNodes.get(i);
				//System.out.println(neighbour);
				boolean inClosedSet=false;
				//check to see if it's in the closed set so we don't re-explore it
				for(int j=0; j<closed.size(); j++){
					if(neighbour.getState().equals(closed.get(j).getState()))
						inClosedSet=true;
				}
				//check to see if it's the open set
				boolean inOpenSet=false;
				for(int j=0; j<open.size(); j++){
					if(neighbour.getState().equals(open.get(j).getState()))
						inOpenSet=true;
				}
				//can explore the node
				if(!inClosedSet){
					//if it's not in the open set
					if(!inOpenSet){
						//convert current and neighbour states to strings
						String currentString = current.convertToString();
						String neighbourString = neighbour.convertToString();
						explored.put(currentString, neighbourString);
						
						//add to frontier
						open.add(neighbour);
					}
				}
			}
		}	
	}

	public String printPath(HashMap<String, String> cameFrom, Node current){
		String result = "";
	
		//beyond the graph at this point: done
		if(current.getParentNode()==null){
			return result;
		}
		else{
			//find parent in explored map
			String parent="";
			if(cameFrom.containsKey(current.getParentNode().convertToString()))
				parent+=current.getParentNode().convertToString();
			//move up to parent node	
			result += printPath(cameFrom, current.getParentNode()) + "\n" + parent;
		}
		
		return result;
	}

	public ArrayList<Node> expand(Node node, int cost){
		ArrayList<Node> result=null;
		//find the paths
		result = node.expandNodes(heuristicChoice, cost, goal);
		return result;
	}

	public int heuristic(Node node){
		int result=0;
		if(heuristicChoice==0)
			result = node.hammingDistance(goal);
		else if(heuristicChoice==1)
			result = node.manhattanDistance(goal);
		
		return result;
	}
}
