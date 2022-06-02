public class Estado {
	private final static int MATRIZ = 9 ;
	private int[] estadoAtual = new int[MATRIZ];

	public Estado(int[] s){
		for(int i = 0; i < MATRIZ; i++){
			estadoAtual[i]= s[i];
		}
	}

	public int[] getEstado(){
		return estadoAtual;
	}
	public boolean equals(Estado estado){
		int[] outroEstrado = estado.getEstado();
		
		for(int i=0; i < MATRIZ; i++){
			if(outroEstrado[i] != estadoAtual[i]){
				return false;
			}
		}
		return true;
	}

	public String toString(){
		String resultado = "";

		for(int i = 0; i < MATRIZ; i++){
			if((i + 1) % 3 == 0)
				resultado += estadoAtual[i] + "\n";
			else
				resultado += estadoAtual[i] + " ";
		}
		return resultado;
	}
}
