package xadrez.pecas;

import tabuleiroJogo.Posicao;
import tabuleiroJogo.Tabuleiro;
import xadrez.Cor;
import xadrez.PartidaXadrez;
import xadrez.PecaXadrez;

public class Peao extends PecaXadrez {
	
	private PartidaXadrez partidaXadrez;

	public Peao(Tabuleiro tabuleiro, Cor cor, PartidaXadrez partidaXadrez) {
		super(tabuleiro, cor);
		this.partidaXadrez = partidaXadrez;
	}

	@Override
	public boolean[][] movimentosPossiveis() {
		boolean[][] mat = new boolean [getTabuleiro().getLinhas()][getTabuleiro().getColunas()];
		
		Posicao p = new Posicao(0,0);
		if(getCor() == Cor.BRANCO) {
			p.setValor(posicao.getLinha() -1 , posicao.getColuna());
			if(getTabuleiro().posicaoExistente(p) && !getTabuleiro().existeUmaPeca(p)) {
				mat[p.getLinha()][p.getColuna()] = true;
			}
			p.setValor(posicao.getLinha() -2 , posicao.getColuna());
			Posicao p2 = new Posicao(posicao.getLinha() - 1, posicao.getColuna());
			if(getTabuleiro().posicaoExistente(p) && !getTabuleiro().existeUmaPeca(p) && getTabuleiro().posicaoExistente(p2) && !getTabuleiro().existeUmaPeca(p2) && getContadorMovimentos() == 0) {
				mat[p.getLinha()][p.getColuna()] = true;
			}
			p.setValor(posicao.getLinha() -1 , posicao.getColuna()-1);
			if(getTabuleiro().posicaoExistente(p) && eUmaPecaOponente(p)) {
				mat[p.getLinha()][p.getColuna()] = true;
			}
			p.setValor(posicao.getLinha() -1 , posicao.getColuna()+1);
			if(getTabuleiro().posicaoExistente(p) && eUmaPecaOponente(p)) {
				mat[p.getLinha()][p.getColuna()] = true;
			}
			
			// #Movimento especial en passant peças brancas
			if(posicao.getLinha() == 3) {
				Posicao esquerda = new Posicao(posicao.getLinha(), posicao.getColuna() -1);
				if (getTabuleiro().posicaoExistente(esquerda) && eUmaPecaOponente(esquerda) && getTabuleiro().peca(esquerda) == partidaXadrez.getEnPassantVulneravel()) {
					mat[esquerda.getLinha() -1][esquerda.getColuna()] = true;
				}
				Posicao direita = new Posicao(posicao.getLinha(), posicao.getColuna() +1);
				if (getTabuleiro().posicaoExistente(direita) && eUmaPecaOponente(direita) && getTabuleiro().peca(direita) == partidaXadrez.getEnPassantVulneravel()) {
					mat[direita.getLinha() -1][direita.getColuna()] = true;
				}
			}
		} else {
			p.setValor(posicao.getLinha() +1 , posicao.getColuna());
			if(getTabuleiro().posicaoExistente(p) && !getTabuleiro().existeUmaPeca(p)) {
				mat[p.getLinha()][p.getColuna()] = true;
			}
			p.setValor(posicao.getLinha() +2 , posicao.getColuna());
			Posicao p2 = new Posicao(posicao.getLinha() + 1, posicao.getColuna());
			if(getTabuleiro().posicaoExistente(p) && !getTabuleiro().existeUmaPeca(p) && getTabuleiro().posicaoExistente(p2) && !getTabuleiro().existeUmaPeca(p2) && getContadorMovimentos() == 0) {
				mat[p.getLinha()][p.getColuna()] = true;
			}
			p.setValor(posicao.getLinha() +1 , posicao.getColuna()-1);
			if(getTabuleiro().posicaoExistente(p) && eUmaPecaOponente(p)) {
				mat[p.getLinha()][p.getColuna()] = true;
			}
			p.setValor(posicao.getLinha() +1 , posicao.getColuna()+1);
			if(getTabuleiro().posicaoExistente(p) && eUmaPecaOponente(p)) {
				mat[p.getLinha()][p.getColuna()] = true;
			}
			
			// #Movimento especial en passant peças pretas
			if(posicao.getLinha() == 4) {
				Posicao esquerda = new Posicao(posicao.getLinha(), posicao.getColuna() -1);
				if (getTabuleiro().posicaoExistente(esquerda) && eUmaPecaOponente(esquerda) && getTabuleiro().peca(esquerda) == partidaXadrez.getEnPassantVulneravel()) {
					mat[esquerda.getLinha() +1][esquerda.getColuna()] = true;
				}
				Posicao direita = new Posicao(posicao.getLinha(), posicao.getColuna() +1);
				if (getTabuleiro().posicaoExistente(direita) && eUmaPecaOponente(direita) && getTabuleiro().peca(direita) == partidaXadrez.getEnPassantVulneravel()) {
					mat[direita.getLinha() +1][direita.getColuna()] = true;
				}
			}
		}
		return mat;
	}
	@Override
	public String toString() {
		return "P";
	}
}
