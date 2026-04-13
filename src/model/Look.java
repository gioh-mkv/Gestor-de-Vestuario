package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Look {
    private String id;
    private String nome;
    private Map<Item.TipoItem, Item> itens;
    private List<UtilizacaoLook> utilizacoes;

    public static class UtilizacaoLook {
        private LocalDate data;
        private String periodo;
        private String ocasiao;

        public UtilizacaoLook(LocalDate data, String periodo, String ocasiao) {
            this.data = data;
            this.periodo = periodo;
            this.ocasiao = ocasiao;
        }

        public LocalDate getData() { return data; }
        public String getPeriodo() { return periodo; }
        public String getOcasiao() { return ocasiao; }

        @Override
        public String toString() {
            return data + " - " + periodo + " - " + ocasiao;
        }
    }

    public Look(String id, String nome) {
        this.id = id;
        this.nome = nome;
        this.itens = new HashMap<>();
        this.utilizacoes = new ArrayList<>();
    }

    public void adicionarItem(Item item) {
        itens.put(item.getTipo(), item);
    }

    public void removerItem(Item.TipoItem tipo) {
        itens.remove(tipo);
    }

    public void modificarLook(Item item) {
        itens.put(item.getTipo(), item);
    }

    public void modificarLook(Item item1, Item item2) {
        itens.put(item1.getTipo(), item1);
        itens.put(item2.getTipo(), item2);
    }

    public void registrarUtilizacao(LocalDate data, String periodo, String ocasiao) {
        utilizacoes.add(new UtilizacaoLook(data, periodo, ocasiao));
        for (Item item : itens.values()) {
            item.registrarUtilizacao(data);
        }
    }

    public int getQuantidadeUtilizacoes() { return utilizacoes.size(); }

    public String getId() { return id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public Map<Item.TipoItem, Item> getItens() { return itens; }
    public List<UtilizacaoLook> getUtilizacoes() { return utilizacoes; }

    @Override
    public String toString() {
        return nome + " (" + itens.size() + " itens)";
    }
}
