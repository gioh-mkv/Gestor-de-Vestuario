package gui;

import interfaces.ILavavel;
import model.*;
import service.GestorVestuario;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.UUID;

public class LavagemPanel extends JPanel {

    private final GestorVestuario gestor;
    private JTable tabelaLavagens;
    private DefaultTableModel modeloTabela;
    private JList<Item> listaItensLavaveis;
    private DefaultListModel<Item> modeloListaItens;
    private JTextField txtObservacoes;

    public LavagemPanel(GestorVestuario gestor) {
        this.gestor = gestor;
        initializeComponents();
        setupLayout();
        carregarDados();
    }

    private void initializeComponents() {
        String[] colunas = {"Data", "Qtd Itens", "Observações"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabelaLavagens = new JTable(modeloTabela);

        modeloListaItens = new DefaultListModel<>();
        listaItensLavaveis = new JList<>(modeloListaItens);
        listaItensLavaveis.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        txtObservacoes = new JTextField(30);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        JPanel painelLavagem = new JPanel(new BorderLayout());
        painelLavagem.setBorder(BorderFactory.createTitledBorder("Registrar Lavagem"));

        JPanel painelObs = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelObs.add(new JLabel("Observações:"));
        painelObs.add(txtObservacoes);

        JButton btnRegistrar = new JButton("Registrar Lavagem");
        JButton btnAtualizar = new JButton("Atualizar");
        btnRegistrar.addActionListener(e -> registrarLavagem());
        btnAtualizar.addActionListener(e -> carregarDados());

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelBotoes.add(btnRegistrar);
        painelBotoes.add(btnAtualizar);

        painelLavagem.add(painelObs, BorderLayout.NORTH);
        painelLavagem.add(new JScrollPane(listaItensLavaveis), BorderLayout.CENTER);
        painelLavagem.add(painelBotoes, BorderLayout.SOUTH);

        JPanel painelHistorico = new JPanel(new BorderLayout());
        painelHistorico.setBorder(BorderFactory.createTitledBorder("Histórico de Lavagens"));
        painelHistorico.add(new JScrollPane(tabelaLavagens), BorderLayout.CENTER);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, painelLavagem, painelHistorico);
        split.setDividerLocation(280);
        add(split, BorderLayout.CENTER);
    }

    private void registrarLavagem() {
        java.util.List<Item> selecionados = listaItensLavaveis.getSelectedValuesList();
        if (selecionados.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecione pelo menos um item!");
            return;
        }
        Lavagem lavagem = new Lavagem(UUID.randomUUID().toString(), LocalDate.now());
        lavagem.setObservacoes(txtObservacoes.getText());
        selecionados.stream()
                .filter(i -> i instanceof ILavavel)
                .forEach(i -> lavagem.adicionarItem((ILavavel) i));

        gestor.adicionarLavagem(lavagem);
        txtObservacoes.setText("");
        carregarDados();
        JOptionPane.showMessageDialog(this, "Lavagem registrada com sucesso!");
    }

    private void carregarDados() {
        modeloListaItens.clear();
        gestor.listarItens().stream()
                .filter(i -> i instanceof ILavavel)
                .forEach(modeloListaItens::addElement);

        modeloTabela.setRowCount(0);
        gestor.listarLavagens().forEach(l -> modeloTabela.addRow(new Object[]{
                l.getData(),
                l.getItensLavados().size(),
                l.getObservacoes()
        }));
    }
}
