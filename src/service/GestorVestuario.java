package service;

import interfaces.IEmprestavel;
import interfaces.ILavavel;
import model.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Camada de serviço principal do GVP.
 * Gerencia itens, looks, lavagens e empréstimos, com persistência automática.
 *
 * Correção aplicada: lavagens agora são salvas e carregadas corretamente.
 */
public class GestorVestuario {

    private final Map<String, Item>    itens    = new LinkedHashMap<>();
    private final Map<String, Look>    looks    = new LinkedHashMap<>();
    private final Map<String, Lavagem> lavagens = new LinkedHashMap<>();
    private final PersistenciaService  persistencia = new PersistenciaService();

    public GestorVestuario() {
        carregarDados();
    }

    // -------------------------------------------------------------------------
    // Itens
    // -------------------------------------------------------------------------

    public void adicionarItem(Item item) {
        itens.put(item.getId(), item);
        salvarDados();
    }

    public void removerItem(String id) {
        itens.remove(id);
        salvarDados();
    }

    public void modificarItem(Item item) {
        itens.put(item.getId(), item);
        salvarDados();
    }

    public Item buscarItem(String id) { return itens.get(id); }

    public List<Item> listarItens() { return new ArrayList<>(itens.values()); }

    // -------------------------------------------------------------------------
    // Looks
    // -------------------------------------------------------------------------

    public void adicionarLook(Look look) {
        looks.put(look.getId(), look);
        salvarDados();
    }

    public void removerLook(String id) {
        looks.remove(id);
        salvarDados();
    }

    public void modificarLook(Look look) {
        looks.put(look.getId(), look);
        salvarDados();
    }

    public Look buscarLook(String id) { return looks.get(id); }

    public List<Look> listarLooks() { return new ArrayList<>(looks.values()); }

    // -------------------------------------------------------------------------
    // Empréstimos
    // -------------------------------------------------------------------------

    public void emprestarItem(String itemId, String nomeEmprestado, LocalDate data) {
        Item item = itens.get(itemId);
        if (item instanceof IEmprestavel) {
            ((IEmprestavel) item).registrarEmprestimo(nomeEmprestado, data);
            salvarDados();
        }
    }

    public void devolverItem(String itemId) {
        Item item = itens.get(itemId);
        if (item instanceof IEmprestavel) {
            ((IEmprestavel) item).registrarDevolucao();
            salvarDados();
        }
    }

    public List<Item> getItensEmprestados() {
        return itens.values().stream()
                .filter(i -> i instanceof IEmprestavel && ((IEmprestavel) i).estaEmprestado())
                .collect(Collectors.toList());
    }

    // -------------------------------------------------------------------------
    // Lavagens
    // -------------------------------------------------------------------------

    public void adicionarLavagem(Lavagem lavagem) {
        lavagens.put(lavagem.getId(), lavagem);
        salvarDados();
    }

    public List<Lavagem> listarLavagens() { return new ArrayList<>(lavagens.values()); }

    // -------------------------------------------------------------------------
    // Estatísticas
    // -------------------------------------------------------------------------

    public List<Item> getItensMaisUsados(int limite) {
        return itens.values().stream()
                .sorted(Comparator.comparingInt(Item::getQuantidadeUtilizacoes).reversed())
                .limit(limite)
                .collect(Collectors.toList());
    }

    public List<Item> getItensMenosUsados(int limite) {
        return itens.values().stream()
                .sorted(Comparator.comparingInt(Item::getQuantidadeUtilizacoes))
                .limit(limite)
                .collect(Collectors.toList());
    }

    public List<Look> getLooksMaisUsados(int limite) {
        return looks.values().stream()
                .sorted(Comparator.comparingInt(Look::getQuantidadeUtilizacoes).reversed())
                .limit(limite)
                .collect(Collectors.toList());
    }

    public List<Item> getItensMaisLavados(int limite) {
        return itens.values().stream()
                .filter(i -> i instanceof ILavavel)
                .sorted(Comparator.comparingInt(i -> -((ILavavel) i).getQuantidadeLavagens()))
                .limit(limite)
                .collect(Collectors.toList());
    }

    // -------------------------------------------------------------------------
    // Persistência
    // -------------------------------------------------------------------------

    private void salvarDados() {
        persistencia.salvarItens(new ArrayList<>(itens.values()));
        persistencia.salvarLooks(new ArrayList<>(looks.values()));

        // Converte Lavagem -> LavagemDto para salvar apenas IDs dos itens
        List<PersistenciaService.LavagemDto> dtos = new ArrayList<>();
        for (Lavagem lav : lavagens.values()) {
            PersistenciaService.LavagemDto dto = new PersistenciaService.LavagemDto();
            dto.id = lav.getId();
            dto.data = lav.getData();
            dto.observacoes = lav.getObservacoes();
            for (ILavavel il : lav.getItensLavados()) {
                if (il instanceof Item) dto.idsItens.add(((Item) il).getId());
            }
            dtos.add(dto);
        }
        persistencia.salvarLavagens(dtos);
    }

    private void carregarDados() {
        for (Item item : persistencia.carregarItens()) {
            itens.put(item.getId(), item);
        }
        for (Look look : persistencia.carregarLooks()) {
            looks.put(look.getId(), look);
        }
        // Reconstrói Lavagem a partir dos DTOs, re-vinculando objetos Item
        for (PersistenciaService.LavagemDto dto : persistencia.carregarLavagens()) {
            Lavagem lav = new Lavagem(dto.id, dto.data);
            lav.setObservacoes(dto.observacoes);
            for (String idItem : dto.idsItens) {
                Item item = itens.get(idItem);
                if (item instanceof ILavavel) {
                    // Não chama adicionarItem() para evitar duplicar registros de lavagem
                    lav.getItensLavados().add((ILavavel) item);
                }
            }
            lavagens.put(lav.getId(), lav);
        }
    }
}
