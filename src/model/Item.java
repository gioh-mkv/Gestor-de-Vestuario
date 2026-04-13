package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public abstract class Item {
    protected String id;
    protected String nome;
    protected String cor;
    protected String tamanho;
    protected String lojaOrigem;
    protected Conservacao conservacao;
    protected String caminhoImagem;
    protected LocalDate dataCompra;
    protected List<LocalDate> utilizacoes;
    protected TipoItem tipo;

    public enum Conservacao {
        EXCELENTE, BOA, REGULAR, RUIM
    }

    public enum TipoItem {
        PARTE_SUPERIOR, PARTE_INFERIOR, CALCADO, ACESSORIO, ROUPA_INTIMA
    }

    public Item(String id, String nome, String cor, String tamanho,
                String lojaOrigem, Conservacao conservacao, TipoItem tipo) {
        this.id = id;
        this.nome = nome;
        this.cor = cor;
        this.tamanho = tamanho;
        this.lojaOrigem = lojaOrigem;
        this.conservacao = conservacao;
        this.tipo = tipo;
        this.utilizacoes = new ArrayList<>();
        this.dataCompra = LocalDate.now();
    }

    public void registrarUtilizacao(LocalDate data) {
        utilizacoes.add(data);
    }

    public int getQuantidadeUtilizacoes() {
        return utilizacoes.size();
    }

    public String getId() { return id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCor() { return cor; }
    public void setCor(String cor) { this.cor = cor; }
    public String getTamanho() { return tamanho; }
    public void setTamanho(String tamanho) { this.tamanho = tamanho; }
    public String getLojaOrigem() { return lojaOrigem; }
    public void setLojaOrigem(String lojaOrigem) { this.lojaOrigem = lojaOrigem; }
    public Conservacao getConservacao() { return conservacao; }
    public void setConservacao(Conservacao conservacao) { this.conservacao = conservacao; }
    public String getCaminhoImagem() { return caminhoImagem; }
    public void setCaminhoImagem(String caminhoImagem) { this.caminhoImagem = caminhoImagem; }
    public TipoItem getTipo() { return tipo; }
    public List<LocalDate> getUtilizacoes() { return utilizacoes; }
    public LocalDate getDataCompra() { return dataCompra; }

    @Override
    public String toString() {
        return nome + " (" + cor + ")";
    }
}
