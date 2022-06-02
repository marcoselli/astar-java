import java.util.Scanner;

public class EightPuzzle {


  public static void main(String[] args){

      System.out.println("*** QUEBRA-CABEÇA 8 PEÇAS UTILIZANDO ALGORITMO A ESTRELA ***");
      EightPuzzle.menuPrincipal();

  }
  public static void menuPrincipal(){
    Scanner ler = new Scanner(System.in);
    int escolhaPrincipal;
    int escolhaTabuleiro = -1;
    Heuristica escolhaHeuristica = null;
    int[] arrayInicial = new int[9];

    do{

      System.out.println("\n1 - Formulação do Tabuleiro");
      System.out.println("2 - Escolha Heurística");
      System.out.println("3 - Rodar programa");
      System.out.println("0 - SAIR");

      escolhaPrincipal = ler.nextInt();

      switch (escolhaPrincipal){
        case 1:
          escolhaTabuleiro = menuTabuleiro(ler);
          if(escolhaTabuleiro == 2){
            System.out.println("\nDigite os número de 0-8 na ordem que preferir:");
            for(int i = 0; i < 9; i++){
              arrayInicial[i] = ler.nextInt();
            }
          }
          break;
        case 2:
          escolhaHeuristica = menuHeuristica(ler);
          break;

        case 3:
          if(escolhaTabuleiro == -1 || escolhaHeuristica == null){
            System.out.println("Defina as opções 1 e 2 antes de rodar o programa.");
          }
          else {
            if (!EightPuzzle.isArrayVazio(arrayInicial)) {
              AEstrela busca = new AEstrela(escolhaHeuristica, arrayInicial);
              busca.AStarSearch();
            } else {
              AEstrela busca = new AEstrela(escolhaHeuristica);
              busca.AStarSearch();
            }
          }
          break;

        default:
          System.out.println("Por favor selecione uma opção válida!");
      }

    } while (escolhaPrincipal != 0);
  }

  public static int menuTabuleiro(Scanner ler) {
    System.out.println("\nVocê deseja utilizar qual formulação do tabuleiro?");
    System.out.println("1 - Randômica");
    System.out.println("2 - Escolher a ordem das peças");

    int escolhaTabuleiro = ler.nextInt();

    return escolhaTabuleiro;
  }

  public static Heuristica menuHeuristica(Scanner ler){
    System.out.println("\nVocê deseja utilizar qual heurística?");
    System.out.println("1 - Distância Manhattan");
    System.out.println("2 - Distância Hamming");

    int escolhaHeuristica = ler.nextInt();

    if(escolhaHeuristica == 1)
      return Heuristica.DISTANCIA_MANHATTAN;

    return  Heuristica.DISTANCIA_HAMMING;
  }

  public static boolean isArrayVazio(int[] array){
    int contador = 0;

    for(int i = 0; i < array.length; i++){
      if(contador > 1)
        return true;
      if(array[i] == 0){
        contador++;
      }
    }
    return false;
  }

}
