package model;

import interfaces.ILavavel;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe base abstrata para itens que podem ser lavados.
 * Elimina duplicação de código entre Camisa, Calca e RoupaIntima.
 */
public abstract class ItemLavavel extends ItemEmprestavel implements ILavavel {
    protected List<LocalDate> lavagens;

    public ItemLavavel(String id, String nome, String cor, String tamanho,
                       String lojaOrigem, Conservacao conservacao, TipoItem tipo) {
        super(id, nome, cor, tamanho, lojaOrigem, conservacao, tipo);
        this.lavagens = new ArrayList<>();
    }

    @Override
    public void registrarLavagem(LocalDate dataLavagem) {
        lavagens.add(dataLavagem);
    }

    @Override
    public int getQuantidadeLavagens() {
        return lavagens.size();
    }

    @Override
    public LocalDate getUltimaLavagem() {
        return lavagens.isEmpty() ? null : lavagens.get(lavagens.size() - 1);
    }
}
