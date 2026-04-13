package model;

import interfaces.IEmprestavel;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Classe base abstrata para itens que podem ser emprestados.
 * Elimina duplicação de código entre Camisa, Calca e Relogio.
 */
public abstract class ItemEmprestavel extends Item implements IEmprestavel {
    protected boolean emprestado;
    protected String nomeEmprestado;
    protected LocalDate dataEmprestimo;

    public ItemEmprestavel(String id, String nome, String cor, String tamanho,
                           String lojaOrigem, Conservacao conservacao, TipoItem tipo) {
        super(id, nome, cor, tamanho, lojaOrigem, conservacao, tipo);
        this.emprestado = false;
    }

    @Override
    public void registrarEmprestimo(String nomeEmprestado, LocalDate dataEmprestimo) {
        this.emprestado = true;
        this.nomeEmprestado = nomeEmprestado;
        this.dataEmprestimo = dataEmprestimo;
    }

    @Override
    public long quantidadeDeDiasDesdeOEmprestimo() {
        if (!emprestado || dataEmprestimo == null) return 0;
        return ChronoUnit.DAYS.between(dataEmprestimo, LocalDate.now());
    }

    @Override
    public void registrarDevolucao() {
        this.emprestado = false;
        this.nomeEmprestado = null;
        this.dataEmprestimo = null;
    }

    @Override
    public boolean estaEmprestado() { return emprestado; }

    @Override
    public String getNomeEmprestado() { return nomeEmprestado; }

    @Override
    public LocalDate getDataEmprestimo() { return dataEmprestimo; }
}
