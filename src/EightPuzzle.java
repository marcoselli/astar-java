public class EightPuzzle {
  public static void main(String[] args){
	  System.out.println("Manhattan distance:");
	  AStar search = new AStar(1);//manhattan distance
	  search.AStarSearch();
	  System.out.println(search.getPathCost());

	 System.out.println("Hamming distance:");
	 AStar search2 = new AStar(0);
	 search2.AStarSearch();
	 System.out.println(search2.getPathCost());
  }
}
