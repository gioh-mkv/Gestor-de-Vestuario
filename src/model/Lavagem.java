package model;

import interfaces.ILavavel;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Lavagem {
    private String id;
    private LocalDate data;
    private List<ILavavel> itensLavados;
    private String observacoes;

    public Lavagem(String id, LocalDate data) {
        this.id = id;
        this.data = data;
        this.itensLavados = new ArrayList<>();
    }

    public void adicionarItem(ILavavel item) {
        itensLavados.add(item);
        item.registrarLavagem(data);
    }

    public void removerItem(ILavavel item) {
        itensLavados.remove(item);
    }

    public String getId() { return id; }
    public LocalDate getData() { return data; }
    public List<ILavavel> getItensLavados() { return itensLavados; }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    @Override
    public String toString() {
        return "Lavagem " + data + " (" + itensLavados.size() + " itens)";
    }
}
