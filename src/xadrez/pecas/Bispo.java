package xadrez.pecas;

import tabuleiroJogo.Posicao;
import tabuleiroJogo.Tabuleiro;
import xadrez.Cor;
import xadrez.PecaXadrez;

public class Bispo extends PecaXadrez {

	public Bispo(Tabuleiro tabuleiro, Cor cor) {
		super(tabuleiro, cor);
	}
	@Override
	public String toString() {
		return "B";
	}
	
	@Override
	public boolean[][] movimentosPossiveis() {
		boolean[][]mat = new boolean[getTabuleiro().getLinhas()][getTabuleiro().getColunas()];
		
		Posicao p = new Posicao(0,0);
		
		//Noroeste
		p.setValor(posicao.getLinha()-1, posicao.getColuna() -1);
		while(getTabuleiro().posicaoExistente(p) && !getTabuleiro().existeUmaPeca(p)) {
			mat[p.getLinha()][p.getColuna()] = true;
			p.setValor(p.getLinha() -1, p.getColuna() -1);
	}
		if(getTabuleiro().posicaoExistente(p) && eUmaPecaOponente(p)) {
			mat[p.getLinha()][p.getColuna()] = true;
		}
		
		//Nordeste
		p.setValor(posicao.getLinha()-1, posicao.getColuna()+1);
		while(getTabuleiro().posicaoExistente(p) && !getTabuleiro().existeUmaPeca(p)) {
			mat[p.getLinha()][p.getColuna()] = true;
			p.setValor(p.getLinha()-1, p.getColuna()+1);
	}
		if(getTabuleiro().posicaoExistente(p) && eUmaPecaOponente(p)) {
			mat[p.getLinha()][p.getColuna()] = true;
		}
		
		//Sudeste
		p.setValor(posicao.getLinha()+1, posicao.getColuna()+1);
		while(getTabuleiro().posicaoExistente(p) && !getTabuleiro().existeUmaPeca(p)) {
			mat[p.getLinha()][p.getColuna()] = true;
			p.setValor(p.getLinha()+1, p.getColuna()+1);
	}
		if(getTabuleiro().posicaoExistente(p) && eUmaPecaOponente(p)) {
			mat[p.getLinha()][p.getColuna()] = true;
		}
		
		//Sudoeste
		p.setValor(posicao.getLinha()+1, posicao.getColuna()-1);
		while(getTabuleiro().posicaoExistente(p) && !getTabuleiro().existeUmaPeca(p)) {
			mat[p.getLinha()][p.getColuna()] = true;
			p.setValor(p.getLinha()+1, p.getColuna()-1);
	}
		if(getTabuleiro().posicaoExistente(p) && eUmaPecaOponente(p)) {
			mat[p.getLinha()][p.getColuna()] = true;
		}
		
		return mat;
	}
}
