import java.util.*;

public class No {
	private static final int LINHAS = 3;
	private static final int COLUNAS = 3;
	private static final int MATRIZ = LINHAS * COLUNAS;
	private Estado estado;
	private int fn, gn, hn; //Custos do A*
	private No pai; //Referência de onde o nó atual foi expandido

	public No(Estado estado){
		this.estado = estado;
		gn = 0;
		fn = 0;
		pai = null;
	}
	public No(Estado estado, int custoDoCaminho){
		this.estado = estado;
		gn = custoDoCaminho;
		fn = 0;
		pai = null;
	}

	public String converteParaString(){
		String stringEstado = "";
		int[] estadoAtual = estado.getEstado();

		for(int i = 0; i < MATRIZ; i++){
			if((i+1)%3==0)
				stringEstado+=estadoAtual[i]+"\n";
			else
				stringEstado+=estadoAtual[i]+" ";
		}
		
		return stringEstado;
	}
	public int getDistanciaHamming(Estado estadoObjetivo){
		hn = 0;
		int estadoAtual[] = estado.getEstado();
		int objetivo[] = estadoObjetivo.getEstado();

		for(int i = 0; i< MATRIZ; i++){
			if(estadoAtual[i] != objetivo[i]){
				hn++;
			}
		}
		return hn;
	}
	public int getDistanciaManhattan(Estado estadoObjetivo){
		hn=0;
		int atual[] = estado.getEstado();
		int objetivo[] = estadoObjetivo.getEstado();
		int atual2D[][];
		int objetivo2D[][];
		int pos = 0;

		//Converte estado atual e objetivo em matriz para facilitar nos cálculos
		atual2D = converteArrayEmMatriz(atual);
		objetivo2D = converteArrayEmMatriz(objetivo);

		for(int i = 0; i< LINHAS; i++){
			for(int j = 0; j< COLUNAS; j++){
				int val = atual2D[i][j];
				//val coord
				int x1 = i;
				int y1 = j;
				//goal coord
				int x2=0;
				int y2=0;

				// Encontra as coordenadas desejadas
				for(int l = 0; l< LINHAS; l++){
					for(int m = 0; m< COLUNAS; m++){
						int goalVal = objetivo2D[l][m];
						if(val==goalVal){
							x2=l;
							y2=m;
							break;
						}
					}
				}
				// Calcula o valor atual da distância de Manhattan
				hn +=  Math.abs(x1-x2) + Math.abs(y1-y2);
			}
		}

		return hn;
	}
	public static int[][] converteArrayEmMatriz(int[] arrayInteiro){
		int pos = 0;
		int[][] matrizResultado = new int[LINHAS][COLUNAS];
		for(int i = 0; i< LINHAS; i++){
			for(int j = 0; j< COLUNAS; j++){
				matrizResultado[i][j]=arrayInteiro[pos];
				pos++;
			}
		}
		return matrizResultado;
	}

	public List<No> expandeNos(Heuristica heuristica, int custo, Estado estadoObjetivo){
		List<No> estados = new ArrayList<>();
		Estado novoEstado;

		int[] atual = estado.getEstado();
		int[][] atual2D = new int[LINHAS][COLUNAS];

		//Converte array em matriz para facilitar nos cálculos
		atual2D = converteArrayEmMatriz(atual);

		for(int i = 0; i< LINHAS; i++){
			for(int j = 0; j< COLUNAS; j++){
				int val = atual2D[i][j];
				if(val==0){
					int x=i;
					int y=j;
					// Movimenta para esquerda
					if(y-1 >= 0){
						novoEstado = movimentaPeca(x, y, x, y-1, atual2D);
						
						estados.add(new No(novoEstado, custo));
					}
					// Movimenta para direita
					if(y+1 < COLUNAS){
						novoEstado = movimentaPeca(x, y, x, y+1, atual2D);
						
						estados.add(new No(novoEstado, custo));
					}
					// Movimenta para cima
					if(x-1>=0){
						novoEstado = movimentaPeca(x,y,x-1,y, atual2D);
						
						estados.add(new No(novoEstado, custo));
					}
					// Movimenta para baixo
					if(x+1 < LINHAS){
						novoEstado = movimentaPeca(x,y,x+1,y,atual2D);
						
						estados.add(new No(novoEstado, custo));
					}
				}
			}			
		}
		
		//Calcula o F(n) de cada estado
		//set the parent node of each neighbour to this
		for(int i = 0; i < estados.size(); i++){
			if(heuristica== Heuristica.DISTANCIA_HAMMING){
				estados.get(i).getDistanciaHamming(estadoObjetivo);
				estados.get(i).setCustoFn();
				estados.get(i).setNoPai(this);
			}
			if(heuristica == Heuristica.DISTANCIA_MANHATTAN){
				estados.get(i).getDistanciaManhattan(estadoObjetivo);
				estados.get(i).setCustoFn();
				estados.get(i).setNoPai(this);
			}
		}
		
		estados = mergeSort(estados); // Ordena os nós vizinhos por prioridade através do merge sort
		return estados;
	}

	public static List<No> mergeSort(List<No> lista){
		if(lista.size()<=1){
			return lista;
		}
		List<No> primeiraMetade = new ArrayList<>();
		List<No> segundaMetade = new ArrayList<>();

		for(int i=0; i < lista.size()/2; i++){
			primeiraMetade.add(lista.get(i));
		}
		
		for(int i=lista.size()/2; i<lista.size(); i++){
			segundaMetade.add(lista.get(i));
		}
		
		return merge(mergeSort(primeiraMetade), mergeSort(segundaMetade));
	}

	public static List<No> merge(List<No> l1, List<No> l2){
		if(l1.size()==0){
			return l2;
		}
		
		if(l2.size()==0){
			return l1;
		}
		
		ArrayList<No> resultado = new ArrayList<>();
		No proximoElemento;

		if(l1.get(0).getCustoFn() > l2.get(0).getCustoFn()){
			proximoElemento = l2.get(0);
			l2.remove(0);
		}else{
			proximoElemento = l1.get(0);
			l1.remove(0);
		}
		
		resultado.add(proximoElemento);
		resultado.addAll(merge(l1, l2));
		
		return resultado;
	}

	public Estado movimentaPeca(int x1, int y1, int x2, int y2, int[][] atual2D){

		Estado novoEstado;
		int pos=0;
		int[][] configuracao = new int[LINHAS][COLUNAS];
		for(int l = 0; l< LINHAS; l++){
			for(int m = 0; m< COLUNAS; m++){
				configuracao[l][m] = atual2D[l][m];
			}
		}

		// Troca posição das peças
		int temp = configuracao[x1][y1];
		configuracao[x1][y1] = configuracao[x2][y2];
		configuracao[x2][y2] = temp;


		int[] config = new int[MATRIZ];

		// Converte Matriz para Array
		for(int linha = 0; linha < LINHAS; linha++){
			for(int coluna = 0; coluna < COLUNAS; coluna++){
				config[pos] = configuracao[linha][coluna];
				pos++;
			}
		}

		novoEstado = new Estado(config);

		return novoEstado;
	}

	public int getGn(){
		return gn;
	}

	public void setCustoFn(){
		fn = gn+hn;
	}


	public int getCustoFn(){
		return fn;
	}

	public Estado getEstado(){
		return estado;
	}

	public void setNoPai(No noPai){
		pai = noPai;
	}

	public No getNoPai(){
		return pai;
	}

	public String toString(){
		return estado.toString();
	}
}
