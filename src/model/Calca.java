package model;

/**
 * Representa uma calça ou peça de roupa inferior.
 * Pode ser emprestada e lavada.
 */
public class Calca extends ItemLavavel {

    public Calca(String id, String nome, String cor, String tamanho,
                 String lojaOrigem, Conservacao conservacao) {
        super(id, nome, cor, tamanho, lojaOrigem, conservacao, TipoItem.PARTE_INFERIOR);
    }
}
