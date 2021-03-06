package xadrez.pecas;

import tabuleiroJogo.Posicao;
import tabuleiroJogo.Tabuleiro;
import xadrez.Cor;
import xadrez.PecaXadrez;

public class Torre extends PecaXadrez {

	public Torre(Tabuleiro tabuleiro, Cor cor) {
		super(tabuleiro, cor);
	}
	@Override
	public String toString() {
		return "T";
	}
	
	@Override
	public boolean[][] movimentosPossiveis() {
		boolean[][]mat = new boolean[getTabuleiro().getLinhas()][getTabuleiro().getColunas()];
		
		Posicao p = new Posicao(0,0);
		
		//acima
		p.setValor(posicao.getLinha()-1, posicao.getColuna());
		while(getTabuleiro().posicaoExistente(p) && !getTabuleiro().existeUmaPeca(p)) {
			mat[p.getLinha()][p.getColuna()] = true;
			p.setLinha(p.getLinha()-1);
	}
		if(getTabuleiro().posicaoExistente(p) && eUmaPecaOponente(p)) {
			mat[p.getLinha()][p.getColuna()] = true;
		}
		
		//à esquerda
		p.setValor(posicao.getLinha(), posicao.getColuna()-1);
		while(getTabuleiro().posicaoExistente(p) && !getTabuleiro().existeUmaPeca(p)) {
			mat[p.getLinha()][p.getColuna()] = true;
			p.setColuna(p.getColuna()-1);
	}
		if(getTabuleiro().posicaoExistente(p) && eUmaPecaOponente(p)) {
			mat[p.getLinha()][p.getColuna()] = true;
		}
		
		//à direita
		p.setValor(posicao.getLinha(), posicao.getColuna()+1);
		while(getTabuleiro().posicaoExistente(p) && !getTabuleiro().existeUmaPeca(p)) {
			mat[p.getLinha()][p.getColuna()] = true;
			p.setColuna(p.getColuna()+1);
	}
		if(getTabuleiro().posicaoExistente(p) && eUmaPecaOponente(p)) {
			mat[p.getLinha()][p.getColuna()] = true;
		}
		
		//abaixo
		p.setValor(posicao.getLinha()+1, posicao.getColuna());
		while(getTabuleiro().posicaoExistente(p) && !getTabuleiro().existeUmaPeca(p)) {
			mat[p.getLinha()][p.getColuna()] = true;
			p.setLinha(p.getLinha()+1);
	}
		if(getTabuleiro().posicaoExistente(p) && eUmaPecaOponente(p)) {
			mat[p.getLinha()][p.getColuna()] = true;
		}
		
		return mat;
	}
}
