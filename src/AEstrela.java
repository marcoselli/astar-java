/**
 * @file AStar.java
 * @author Natasha Squires <nsquires@upei.ca>
 * Implements the A* algorithm 
 */
import java.util.*;

public class AEstrela {
	private final static int MATRIZ = 9;
	private int[] estadoObjetivo = {0,1,2,3,4,5,6,7,8};
	private Estado objetivo;
	private int[] estadoInicialArray;
	private Estado estadoInicial;
	private List<No> abertos;
	private List<No> fechados;
	private Map<String, String> explorados;
	private Heuristica escolhaHeuristica;

	private int numNoExpandido;
	private int custoCaminho;

	public AEstrela(Heuristica escolhaHeuristica){
		this.abertos = new ArrayList<>(); //empty open set
		this.fechados = new ArrayList<>(); //empty closed set
		this.explorados = new HashMap<>(); //empty explored set
		this.objetivo = new Estado(this.estadoObjetivo);
		this.estadoInicialArray = new int[MATRIZ];
		criaTabuleiroRandomico();
		this.escolhaHeuristica = escolhaHeuristica;
		this.custoCaminho =0;
	}

	public AEstrela(Heuristica escolhaHeuristica, int [] estadoInicialArray){
		this.abertos = new ArrayList<>();
		this.fechados = new ArrayList<>();
		this.explorados = new HashMap<>();
		this.objetivo = new Estado(this.estadoObjetivo);
		this.estadoInicialArray = estadoInicialArray;
		this.estadoInicial = new Estado(estadoInicialArray);
		this.escolhaHeuristica = escolhaHeuristica;
		this.custoCaminho =0;
	}

	public void criaTabuleiroRandomico(){
		Random gen = new Random();
		for(int i = 0; i< MATRIZ; i++){
			estadoInicialArray[i]= estadoObjetivo[i];
		}

		for(int i = MATRIZ - 1; i >= 1; i--){
			int j = gen.nextInt(i);
			
			int temp = estadoInicialArray[j];
			estadoInicialArray[j]= estadoInicialArray[i];
			estadoInicialArray[i] = temp;
		}
		
		//Cria o estado inicial aqui
		estadoInicial = new Estado(estadoInicialArray);
	}
	

	public void AStarSearch(){
		if(isResolvivel(estadoInicialArray) == false){
			System.out.println("Esse quebra-cabeça não tem solução. Deseja continuar mesmo assim? (Y/n)");
			Scanner ler = new Scanner(System.in);
			String opcao = ler.nextLine();

			if(opcao.equals("n"))
				return;
		}

		No noInicial = new No(estadoInicial);
		System.out.println("Start: \n" + noInicial);
		
		//Adiciona o nó na lista de abertos
		abertos.add(noInicial);
		
		//Custo estamatido total a partir do estado inicial
		//Estado inicial começa com G(n) = 0
		heuristica(noInicial);
		noInicial.setCustoFn();

		this.numNoExpandido = 0;
		System.out.println("Número de nós expandidos: ");
		while(!this.abertos.isEmpty()){
			//Encontra o nó com o menor custo de F(n)
			No atual;
			int pos=0;
			int menor = this.abertos.get(pos).getCustoFn();
			for(int i = 0; i< this.abertos.size(); i++){
				if (this.abertos.get(i).getCustoFn() < menor){
					menor = this.abertos.get(i).getCustoFn();
					pos = i;
				}
			}
			//this is the node we found it
			atual = abertos.get(pos);
			
			//is this node's state the same as the GOAL state?!?!
			if(atual.getEstado().equals(this.objetivo)){
				//solved!
				this.explorados.put(atual.getNoPai().converteParaString(), atual.converteParaString());
				String path = printaCaminho(this.explorados, atual);
				//print out the solution
				System.out.println("\nCaminho: \n" + path);
				System.out.println(atual.converteParaString());

				System.out.println("Custo caminho:" + this.custoCaminho);

				break;
			}
			
			//remove current from the frontier
			this.abertos.remove(atual);
			//add current to closed set
			this.fechados.add(atual);
			
			//increase pathCost since we have to expand the path
			//note: the distance from current to a neighbour will just
			//      be 1 in the case of the 8 puzzle
			this.custoCaminho = atual.getGn()+1;
			//get current's state expansion. This will return a list of prioritised neighbours to explore
			List<No> listaNoVizinho = expande(atual, custoCaminho);
			this.numNoExpandido++;
			System.out.print(numNoExpandido + ", ");
			
			//for neighbour in neighbourNodes
			for(int i = 0; i < listaNoVizinho.size(); i++){
				No noVizinho = listaNoVizinho.get(i);
				//System.out.println(neighbour);
				boolean isPresentAbertos = false;
				//check to see if it's in the closed set so we don't re-explore it
				for(int j = 0; j< this.fechados.size(); j++){
					if(noVizinho.getEstado().equals(this.fechados.get(j).getEstado()))
						isPresentAbertos=true;
				}
				//check to see if it's the open set
				boolean isPresentFechados = false;
				for(int j = 0; j< this.abertos.size(); j++){
					if(noVizinho.getEstado().equals(this.abertos.get(j).getEstado()))
						isPresentFechados=true;
				}
				//can explore the node
				if(!isPresentAbertos){
					//if it's not in the open set
					if(!isPresentFechados){
						//convert current and neighbour states to strings
						String currentString = atual.converteParaString();
						String neighbourString = noVizinho.converteParaString();
						this.explorados.put(currentString, neighbourString);
						
						//add to frontier
						this.abertos.add(noVizinho);
					}
				}
			}
		}
		if(abertos.isEmpty())
			System.out.println("Sem solução! Todos os nós foram expandidos...");
	}
	private int getNumeroInversao(int[][] arr){
		int contadorInversao = 0;
		for (int i = 0; i < 3 - 1; i++)
			for (int j = i + 1; j < 3; j++)
				if (arr[j][i] > 0 &&
						arr[j][i] > arr[i][j])
					contadorInversao++;
		return contadorInversao;
	}
	public boolean isResolvivel(int[] arrayInicial)
	{
		int[][] matrizInicial = No.converteArrayEmMatriz(arrayInicial);

		// Conta o número de inversões do quebra-cabeça
		int invCount = getNumeroInversao(matrizInicial);

		// O quebra-cabeça é resolvível se a quantidade de inversões for par
		return (invCount % 2 == 0);
	}
	public String printaCaminho(Map<String, String> historicoCaminho, No noAtual){
		String result = "";
	
		//beyond the graph at this point: done
		if(noAtual.getNoPai()==null){
			return result;
		}
		else{
			//find parent in explored map
			String parent="";
			if(historicoCaminho.containsKey(noAtual.getNoPai().converteParaString()))
				parent+=noAtual.getNoPai().converteParaString();
			//move up to parent node	
			result += printaCaminho(historicoCaminho, noAtual.getNoPai()) + "\n" + parent;
		}
		
		return result;
	}

	public List<No> expande(No no, int custo){
		List<No> resultado;

		//Encontra os caminhos
		resultado = no.expandeNos(this.escolhaHeuristica, custo, this.objetivo);
		return resultado;
	}

	public int heuristica(No no){
		int resultado = 0;
		if(this.escolhaHeuristica == Heuristica.DISTANCIA_HAMMING)
			resultado = no.getDistanciaHamming(this.objetivo);
		else if(this.escolhaHeuristica == Heuristica.DISTANCIA_MANHATTAN)
			resultado = no.getDistanciaManhattan(this.objetivo);
		
		return resultado;
	}
}
