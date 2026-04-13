package model;

/**
 * Representa um relógio ou acessório.
 * Pode ser emprestado, mas não é lavável.
 */
public class Relogio extends ItemEmprestavel {

    public Relogio(String id, String nome, String cor, String tamanho,
                   String lojaOrigem, Conservacao conservacao) {
        super(id, nome, cor, tamanho, lojaOrigem, conservacao, TipoItem.ACESSORIO);
    }
}
