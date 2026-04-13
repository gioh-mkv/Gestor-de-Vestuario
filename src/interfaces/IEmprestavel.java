package interfaces;

import java.time.LocalDate;

public interface IEmprestavel {
    void registrarEmprestimo(String nomeEmprestado, LocalDate dataEmprestimo);
    long quantidadeDeDiasDesdeOEmprestimo();
    void registrarDevolucao();
    boolean estaEmprestado();
    String getNomeEmprestado();
    LocalDate getDataEmprestimo();
}
