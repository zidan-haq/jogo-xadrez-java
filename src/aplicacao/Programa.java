package aplicacao;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import xadrez.PartidaXadrez;
import xadrez.PecaXadrez;
import xadrez.PosicaoXadrez;
import xadrez.XadrezException;

public class Programa {

	public static void main(String[] args) {

		Scanner sc = new Scanner(System.in);
		PartidaXadrez partidaXadrez = new PartidaXadrez();
		List<PecaXadrez> capturada = new ArrayList<>();

		while (!partidaXadrez.getXequeMate()) {
			try {
				UI.clearScreen();
				UI.printPartida(partidaXadrez, capturada);
				System.out.println();
				System.out.print("Origem: ");
				PosicaoXadrez origem = UI.lerPosicaoXadrez(sc);

				boolean[][]movimentosPossiveis = partidaXadrez.movimentosPossiveis(origem);
				UI.clearScreen();
				UI.printTabuleiro(partidaXadrez.getPecas(), movimentosPossiveis);
				
				System.out.println();
				System.out.print("Destino: ");
				PosicaoXadrez destino = UI.lerPosicaoXadrez(sc);

				PecaXadrez capturaPeca = partidaXadrez.executarMovimentoXadrez(origem, destino);
				
				if(capturaPeca != null) {
					capturada.add(capturaPeca);
				}
				
				if(partidaXadrez.getPromocao() != null) {
					System.out.print("O escolha o tipo para a peça a ser promovida (B/C/T/D)");
					String tipo = sc.next().substring(0).toUpperCase();
					partidaXadrez.substituirPecaPromovida(tipo);
				}
			} catch (XadrezException e) {
				System.out.println(e.getMessage());
				sc.nextLine();
			} catch (InputMismatchException e) {
				System.out.println(e.getMessage());
				sc.nextLine();
			}
		}
		
		UI.clearScreen();
		UI.printPartida(partidaXadrez, capturada);
	}
}
