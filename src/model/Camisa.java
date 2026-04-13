package model;

/**
 * Representa uma camisa ou peça de roupa superior.
 * Pode ser emprestada e lavada.
 */
public class Camisa extends ItemLavavel {

    public Camisa(String id, String nome, String cor, String tamanho,
                  String lojaOrigem, Conservacao conservacao) {
        super(id, nome, cor, tamanho, lojaOrigem, conservacao, TipoItem.PARTE_SUPERIOR);
    }
}
