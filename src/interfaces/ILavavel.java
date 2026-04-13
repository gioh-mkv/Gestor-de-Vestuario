package interfaces;

import java.time.LocalDate;

public interface ILavavel {
    void registrarLavagem(LocalDate dataLavagem);
    int getQuantidadeLavagens();
    LocalDate getUltimaLavagem();
}
