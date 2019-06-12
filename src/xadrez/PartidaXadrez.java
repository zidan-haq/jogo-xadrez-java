package xadrez;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import tabuleiroJogo.Peca;
import tabuleiroJogo.Posicao;
import tabuleiroJogo.Tabuleiro;
import xadrez.pecas.Bispo;
import xadrez.pecas.Cavalo;
import xadrez.pecas.Dama;
import xadrez.pecas.Peao;
import xadrez.pecas.Rei;
import xadrez.pecas.Torre;

public class PartidaXadrez {
	private int turno;
	private Cor jogadorAtual;
	private Tabuleiro tabuleiro;
	private boolean xeque;
	private boolean xequeMate;
	private PecaXadrez enPassantVulneravel;
	private PecaXadrez promocao;

	private List<Peca> pecasNoTabuleiro = new ArrayList<>();
	private List<Peca> pecasCapturadas = new ArrayList<>();

	public PartidaXadrez() {
		tabuleiro = new Tabuleiro(8, 8);
		turno = 1;
		jogadorAtual = Cor.BRANCO;
		configInicial();
	}

	public int getTurno() {
		return turno;
	}

	public Cor getJogadorAtual() {
		return jogadorAtual;
	}

	public boolean getXeque() {
		return xeque;
	}

	public boolean getXequeMate() {
		return xequeMate;
	}
	
	public PecaXadrez getEnPassantVulneravel() {
		return enPassantVulneravel;
	}
	
	public PecaXadrez getPromocao() {
		return promocao;
	}

	public PecaXadrez[][] getPecas() {
		PecaXadrez[][] matriz = new PecaXadrez[tabuleiro.getLinhas()][tabuleiro.getColunas()];
		for (int i = 0; i < tabuleiro.getLinhas(); i++) {
			for (int j = 0; j < tabuleiro.getColunas(); j++) {
				matriz[i][j] = (PecaXadrez) tabuleiro.peca(i, j);
			}
		}
		return matriz;
	}

	public boolean[][] movimentosPossiveis(PosicaoXadrez posicaoOrigem) {
		Posicao posicao = posicaoOrigem.paraPosicao();
		validarPosicaoOrigem(posicao);
		return tabuleiro.peca(posicao).movimentosPossiveis();
	}

	public PecaXadrez executarMovimentoXadrez(PosicaoXadrez posicaoOrigem, PosicaoXadrez posicaoDestino) {
		Posicao origem = posicaoOrigem.paraPosicao();
		Posicao destino = posicaoDestino.paraPosicao();
		validarPosicaoOrigem(origem);
		validarPosicaoDestino(origem, destino);
		Peca capturaPeca = fazerMover(origem, destino);

		if (testeXeque(jogadorAtual)) {
			desfazerMovimento(origem, destino, capturaPeca);
			throw new XadrezException("Você não pode se colocar em xeque.");
		}
		
		PecaXadrez pecaMovida = (PecaXadrez)tabuleiro.peca(destino);
		
		// #Movimento especial Promoção
		promocao = null;
		if(pecaMovida instanceof Peao) {
			if(pecaMovida.getCor() == Cor.BRANCO && destino.getLinha() == 0 || pecaMovida.getCor() == Cor.PRETO && destino.getLinha() == 7) {
				promocao = (PecaXadrez)tabuleiro.peca(destino);
				promocao = substituirPecaPromovida("D");
			}
		}
		xeque = (testeXeque(oponente(jogadorAtual))) ? true : false;

		if (testeXequeMate(oponente(jogadorAtual))) {
			xequeMate = true;
		} else {
			proximoTurno();
		}
		
		// #Movimento especial en passant
		if(pecaMovida instanceof Peao && (destino.getLinha() == origem.getLinha() -2 || destino.getLinha() == origem.getLinha() +2)) {
			enPassantVulneravel = pecaMovida;
		} else {
			enPassantVulneravel = null;
		}
		
		return (PecaXadrez) capturaPeca;
	}
	
	public PecaXadrez substituirPecaPromovida(String tipo) {
		if(promocao == null) {
			throw new IllegalStateException("Está peça não pode ser promovida");
		}
		if(!tipo.equals("B") && !tipo.equals("C") && tipo.equals("T") && tipo.equals("D")) {
			throw new InvalidParameterException("Tipo inválido para promoção.");
		}
		Posicao pos = promocao.getPosicaoXadrez().paraPosicao();
		Peca p = tabuleiro.removePeca(pos);
		pecasNoTabuleiro.remove(p);
		
		PecaXadrez novaPeca = novaPeca(tipo, promocao.getCor());
		tabuleiro.lugarPeca(novaPeca, pos);
		pecasNoTabuleiro.add(novaPeca);
		
		return novaPeca;
	}
	
	private PecaXadrez novaPeca(String tipo, Cor cor) {
		if(tipo.equals("B")) return new Bispo(tabuleiro, cor);
		if(tipo.equals("C")) return new Cavalo(tabuleiro, cor);
		if(tipo.equals("T")) return new Torre(tabuleiro, cor);
		return new Dama(tabuleiro, cor);
	}

	private Peca fazerMover(Posicao origem, Posicao destino) {
		PecaXadrez p = (PecaXadrez) tabuleiro.removePeca(origem);
		p.incrementarContadorMovimentos();
		Peca capturaPeca = tabuleiro.removePeca(destino);
		tabuleiro.lugarPeca(p, destino);

		if (capturaPeca != null) {
			pecasNoTabuleiro.remove(capturaPeca);
			pecasCapturadas.add(capturaPeca);
		}
		
		// #Movimento especial Roque do lado do Rei
		if(p instanceof Rei && destino.getColuna() == origem.getColuna() + 2) {
			Posicao origemT = new Posicao(origem.getLinha(), origem.getColuna() + 3);
			Posicao destinoT = new Posicao(origem.getLinha(), origem.getColuna() + 1);
			PecaXadrez torre = (PecaXadrez)tabuleiro.removePeca(origemT);
			tabuleiro.lugarPeca(torre, destinoT);
			torre.incrementarContadorMovimentos();
		}
		
		// #Movimento especial Roque do lado da Rainha
		if(p instanceof Rei && destino.getColuna() == origem.getColuna() - 2) {
			Posicao origemT = new Posicao(origem.getLinha(), origem.getColuna() - 4);
			Posicao destinoT = new Posicao(origem.getLinha(), origem.getColuna() - 1);
			PecaXadrez torre = (PecaXadrez)tabuleiro.removePeca(origemT);
			tabuleiro.lugarPeca(torre, destinoT);
			torre.incrementarContadorMovimentos();
		}
		
		// #Movimento especial en passant
		if(p instanceof Peao) {
			if(origem.getColuna() != destino.getColuna() && capturaPeca == null){
				Posicao peaoPosicao;
				if(p.getCor() == Cor.BRANCO) {
					peaoPosicao = new Posicao(destino.getLinha() +1, destino.getColuna());
				} else {
					peaoPosicao = new Posicao(destino.getLinha() -1, destino.getColuna());
				}
				capturaPeca = tabuleiro.removePeca(peaoPosicao);
				pecasCapturadas.add(capturaPeca);
				pecasNoTabuleiro.remove(capturaPeca);
			}
		}		
		return capturaPeca;
	}

	private void desfazerMovimento(Posicao origem, Posicao destino, Peca pecaCapturada) {
		PecaXadrez p = (PecaXadrez)tabuleiro.removePeca(destino);
		p.decrementarContadorMovimentos();
		tabuleiro.lugarPeca(p, origem);

		if (pecaCapturada != null) {
			tabuleiro.lugarPeca(pecaCapturada, destino);
			pecasCapturadas.remove(pecaCapturada);
			pecasNoTabuleiro.add(pecaCapturada);
		}
		// #Movimento especial Roque do lado do Rei
		if(p instanceof Rei && destino.getColuna() == origem.getColuna() + 2) {
			Posicao origemT = new Posicao(origem.getLinha(), origem.getColuna() + 3);
			Posicao destinoT = new Posicao(origem.getLinha(), origem.getColuna() + 1);
			PecaXadrez torre = (PecaXadrez)tabuleiro.removePeca(destinoT);
			tabuleiro.lugarPeca(torre, origemT);
			torre.decrementarContadorMovimentos();
		}
		
		// #Movimento especial Roque do lado da Rainha
		if(p instanceof Rei && destino.getColuna() == origem.getColuna() - 2) {
			Posicao origemT = new Posicao(origem.getLinha(), origem.getColuna() - 4);
			Posicao destinoT = new Posicao(origem.getLinha(), origem.getColuna() - 1);
			PecaXadrez torre = (PecaXadrez)tabuleiro.removePeca(destinoT);
			tabuleiro.lugarPeca(torre, origemT);
			torre.decrementarContadorMovimentos();
		}
		
		// #Movimento especial en passant
		if(p instanceof Peao) {
			if(origem.getColuna() != destino.getColuna() && pecaCapturada == enPassantVulneravel){
				PecaXadrez peao = (PecaXadrez)tabuleiro.removePeca(destino);
				Posicao peaoPosicao;
				if(p.getCor() == Cor.BRANCO) {
					peaoPosicao = new Posicao(3 , destino.getColuna());
				} else {
					peaoPosicao = new Posicao(4, destino.getColuna());
				}
				tabuleiro.lugarPeca(peao, peaoPosicao);
			}
		}

	}

	private void validarPosicaoOrigem(Posicao posicao) {
		if (!tabuleiro.existeUmaPeca(posicao)) {
			throw new XadrezException("Não existe uma peça nesta posição");
		}
		if (jogadorAtual != ((PecaXadrez) tabuleiro.peca(posicao)).getCor()) {
			throw new XadrezException("A peça escolhida não é sua.");
		}
		if (!tabuleiro.peca(posicao).existeMovimentoPossivel()) {
			throw new XadrezException("Essa peça não pode ser movida");
		}
	}

	private void validarPosicaoDestino(Posicao origem, Posicao destino) {
		if (!tabuleiro.peca(origem).movimentoPossivel(destino)) {
			throw new XadrezException("A peça escolhida não pode se mover para a posição de destino.");
		}
	}

	private void proximoTurno() {
		turno++;
		jogadorAtual = jogadorAtual == Cor.BRANCO ? Cor.PRETO : Cor.BRANCO;
	}

	private Cor oponente(Cor cor) {
		return (cor == Cor.BRANCO) ? Cor.PRETO : Cor.BRANCO;
	}

	private PecaXadrez rei(Cor cor) {
		List<Peca> lista = pecasNoTabuleiro.stream().filter(x -> ((PecaXadrez) x).getCor() == cor)
				.collect(Collectors.toList());
		for (Peca p : lista) {
			if (p instanceof Rei) {
				return (PecaXadrez) p;
			}
		}
		throw new IllegalStateException("Não existe o rei da cor" + cor + "no tabuleiro");
	}

	private boolean testeXeque(Cor cor) {
		Posicao posicaoRei = rei(cor).getPosicaoXadrez().paraPosicao();
		List<Peca> pecasOponente = pecasNoTabuleiro.stream().filter(x -> ((PecaXadrez) x).getCor() == oponente(cor))
				.collect(Collectors.toList());
		for (Peca p : pecasOponente) {
			boolean[][] mat = p.movimentosPossiveis();
			if (mat[posicaoRei.getLinha()][posicaoRei.getColuna()]) {
				return true;
			}
		}
		return false;
	}

	private boolean testeXequeMate(Cor cor) {
		if (!testeXeque(cor)) {
			return false;
		}
		List<Peca> list = pecasNoTabuleiro.stream().filter(x -> ((PecaXadrez) x).getCor() == cor)
				.collect(Collectors.toList());
		for (Peca p : list) {
			boolean[][] mat = p.movimentosPossiveis();
			for (int i = 0; i < tabuleiro.getLinhas(); i++) {
				for (int j = 0; j < tabuleiro.getColunas(); j++) {
					if (mat[i][j]) {
						Posicao origem = ((PecaXadrez) p).getPosicaoXadrez().paraPosicao();
						Posicao destino = new Posicao(i, j);
						Peca capturaPeca = fazerMover(origem, destino);
						boolean testeXeque = testeXeque(cor);
						desfazerMovimento(origem, destino, capturaPeca);
						if (!testeXeque) {
							return false;
						}
					}
				}
			}
		}
		return true;
	}

	private void lugarNovaPeca(char coluna, int linha, PecaXadrez peca) {
		tabuleiro.lugarPeca(peca, new PosicaoXadrez(coluna, linha).paraPosicao());
		pecasNoTabuleiro.add(peca);
	}

	private void configInicial() {
		lugarNovaPeca('a', 1, new Torre(tabuleiro, Cor.BRANCO));
		lugarNovaPeca('b', 1, new Cavalo(tabuleiro, Cor.BRANCO));
		lugarNovaPeca('c', 1, new Bispo(tabuleiro, Cor.BRANCO));
		lugarNovaPeca('d', 1, new Dama(tabuleiro, Cor.BRANCO));
		lugarNovaPeca('e', 1, new Rei(tabuleiro, Cor.BRANCO, this));
		lugarNovaPeca('f', 1, new Bispo(tabuleiro, Cor.BRANCO));
		lugarNovaPeca('g', 1, new Cavalo(tabuleiro, Cor.BRANCO));
		lugarNovaPeca('h', 1, new Torre(tabuleiro, Cor.BRANCO));
		lugarNovaPeca('a', 2, new Peao(tabuleiro, Cor.BRANCO, this));
		lugarNovaPeca('b', 2, new Peao(tabuleiro, Cor.BRANCO, this));
		lugarNovaPeca('c', 2, new Peao(tabuleiro, Cor.BRANCO, this));
        lugarNovaPeca('d', 2, new Peao(tabuleiro, Cor.BRANCO, this));
        lugarNovaPeca('e', 2, new Peao(tabuleiro, Cor.BRANCO, this));
        lugarNovaPeca('f', 2, new Peao(tabuleiro, Cor.BRANCO, this));
        lugarNovaPeca('g', 2, new Peao(tabuleiro, Cor.BRANCO, this));
        lugarNovaPeca('h', 2, new Peao(tabuleiro, Cor.BRANCO, this));
        
        lugarNovaPeca('a', 8, new Torre(tabuleiro, Cor.PRETO));
        lugarNovaPeca('b', 8, new Cavalo(tabuleiro, Cor.PRETO));
        lugarNovaPeca('c', 8, new Bispo(tabuleiro, Cor.PRETO));
        lugarNovaPeca('d', 8, new Dama(tabuleiro, Cor.PRETO));
        lugarNovaPeca('e', 8, new Rei(tabuleiro, Cor.PRETO, this));
        lugarNovaPeca('f', 8, new Bispo(tabuleiro, Cor.PRETO));
        lugarNovaPeca('g', 8, new Cavalo(tabuleiro, Cor.PRETO));
        lugarNovaPeca('h', 8, new Torre(tabuleiro, Cor.PRETO));
        lugarNovaPeca('a', 7, new Peao(tabuleiro, Cor.PRETO, this));
        lugarNovaPeca('b', 7, new Peao(tabuleiro, Cor.PRETO, this));
        lugarNovaPeca('c', 7, new Peao(tabuleiro, Cor.PRETO, this));
        lugarNovaPeca('d', 7, new Peao(tabuleiro, Cor.PRETO, this));
        lugarNovaPeca('e', 7, new Peao(tabuleiro, Cor.PRETO, this));
        lugarNovaPeca('f', 7, new Peao(tabuleiro, Cor.PRETO, this));
        lugarNovaPeca('g', 7, new Peao(tabuleiro, Cor.PRETO, this));
        lugarNovaPeca('h', 7, new Peao(tabuleiro, Cor.PRETO, this));
	}
}