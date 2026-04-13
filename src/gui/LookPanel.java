package gui;

import model.*;
import service.GestorVestuario;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.UUID;

public class LookPanel extends JPanel {

    private final GestorVestuario gestor;
    private JTable tabelaLooks;
    private DefaultTableModel modeloTabela;
    private JTextField txtNomeLook;
    private JList<Item> listaItensDisponiveis;
    private DefaultListModel<Item> modeloListaItens;
    private Look lookSelecionado;

    public LookPanel(GestorVestuario gestor) {
        this.gestor = gestor;
        initializeComponents();
        setupLayout();
        carregarDados();
    }

    private void initializeComponents() {
        String[] colunas = {"ID", "Nome", "Qtd Itens", "Utilizações"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabelaLooks = new JTable(modeloTabela);
        tabelaLooks.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // Oculta coluna ID
        tabelaLooks.getColumnModel().getColumn(0).setMinWidth(0);
        tabelaLooks.getColumnModel().getColumn(0).setMaxWidth(0);
        tabelaLooks.getColumnModel().getColumn(0).setWidth(0);
        tabelaLooks.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) selecionarLook();
        });

        txtNomeLook = new JTextField(20);
        modeloListaItens = new DefaultListModel<>();
        listaItensDisponiveis = new JList<>(modeloListaItens);
        listaItensDisponiveis.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Painel esquerdo — criação
        JPanel painelCriacao = new JPanel(new BorderLayout());
        painelCriacao.setBorder(BorderFactory.createTitledBorder("Criar/Editar Look"));

        JPanel painelNome = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelNome.add(new JLabel("Nome do Look:"));
        painelNome.add(txtNomeLook);

        JButton btnCriar    = new JButton("Criar Look");
        JButton btnAddItens = new JButton("Adicionar Itens ao Look");
        JButton btnUso      = new JButton("Registrar Uso");

        btnCriar.addActionListener(e -> criarLook());
        btnAddItens.addActionListener(e -> adicionarItensAoLook());
        btnUso.addActionListener(e -> registrarUsoLook());

        JPanel painelBotoesCriacao = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelBotoesCriacao.add(btnCriar);
        painelBotoesCriacao.add(btnAddItens);
        painelBotoesCriacao.add(btnUso);

        painelCriacao.add(painelNome, BorderLayout.NORTH);
        painelCriacao.add(new JScrollPane(listaItensDisponiveis), BorderLayout.CENTER);
        painelCriacao.add(painelBotoesCriacao, BorderLayout.SOUTH);

        // Painel direito — lista de looks
        JPanel painelLooks = new JPanel(new BorderLayout());
        painelLooks.setBorder(BorderFactory.createTitledBorder("Looks Criados"));
        painelLooks.add(new JScrollPane(tabelaLooks), BorderLayout.CENTER);

        JButton btnRemover    = new JButton("Remover Look");
        JButton btnDetalhes   = new JButton("Ver Detalhes");
        JButton btnAtualizar  = new JButton("Atualizar");

        btnRemover.addActionListener(e -> removerLook());
        btnDetalhes.addActionListener(e -> verDetalhesLook());
        btnAtualizar.addActionListener(e -> carregarDados());

        JPanel painelBotoesLook = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelBotoesLook.add(btnRemover);
        painelBotoesLook.add(btnDetalhes);
        painelBotoesLook.add(btnAtualizar);
        painelLooks.add(painelBotoesLook, BorderLayout.SOUTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, painelCriacao, painelLooks);
        split.setDividerLocation(400);
        add(split, BorderLayout.CENTER);
    }

    private void criarLook() {
        String nome = txtNomeLook.getText().trim();
        if (nome.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nome do look é obrigatório!");
            return;
        }
        gestor.adicionarLook(new Look(UUID.randomUUID().toString(), nome));
        txtNomeLook.setText("");
        carregarLooks();
        JOptionPane.showMessageDialog(this, "Look criado com sucesso!");
    }

    private void adicionarItensAoLook() {
        if (lookSelecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione um look primeiro!");
            return;
        }
        java.util.List<Item> selecionados = listaItensDisponiveis.getSelectedValuesList();
        if (selecionados.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecione pelo menos um item!");
            return;
        }
        selecionados.forEach(lookSelecionado::adicionarItem);
        gestor.modificarLook(lookSelecionado);
        carregarLooks();
        JOptionPane.showMessageDialog(this, "Itens adicionados ao look!");
    }

    private void registrarUsoLook() {
        if (lookSelecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione um look primeiro!");
            return;
        }
        JPanel panel = new JPanel(new GridLayout(3, 2, 4, 4));
        JTextField txtData  = new JTextField(LocalDate.now().toString());
        JComboBox<String> cbPeriodo = new JComboBox<>(new String[]{"Manhã", "Tarde", "Noite"});
        JTextField txtOcasiao = new JTextField();

        panel.add(new JLabel("Data (YYYY-MM-DD):")); panel.add(txtData);
        panel.add(new JLabel("Período:"));           panel.add(cbPeriodo);
        panel.add(new JLabel("Ocasião:"));           panel.add(txtOcasiao);

        if (JOptionPane.showConfirmDialog(this, panel, "Registrar Uso",
                JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                lookSelecionado.registrarUtilizacao(
                        LocalDate.parse(txtData.getText()),
                        (String) cbPeriodo.getSelectedItem(),
                        txtOcasiao.getText());
                gestor.modificarLook(lookSelecionado);
                carregarLooks();
                JOptionPane.showMessageDialog(this, "Uso registrado!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage());
            }
        }
    }

    private void selecionarLook() {
        int row = tabelaLooks.getSelectedRow();
        if (row >= 0) {
            String id = (String) modeloTabela.getValueAt(row, 0);
            lookSelecionado = gestor.buscarLook(id);
        }
    }

    private void removerLook() {
        int row = tabelaLooks.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Selecione um look."); return; }
        if (JOptionPane.showConfirmDialog(this, "Remover look selecionado?", "Confirmar",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            gestor.removerLook((String) modeloTabela.getValueAt(row, 0));
            lookSelecionado = null;
            carregarLooks();
        }
    }

    private void verDetalhesLook() {
        if (lookSelecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione um look.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Look: ").append(lookSelecionado.getNome()).append("\n\nItens:\n");
        lookSelecionado.getItens().values().forEach(i ->
                sb.append("  - ").append(i.getNome()).append(" (").append(i.getCor()).append(")\n"));
        sb.append("\nUtilizações:\n");
        lookSelecionado.getUtilizacoes().forEach(u ->
                sb.append("  - ").append(u).append("\n"));

        JTextArea ta = new JTextArea(sb.toString());
        ta.setEditable(false);
        JScrollPane sp = new JScrollPane(ta);
        sp.setPreferredSize(new Dimension(420, 300));
        JOptionPane.showMessageDialog(this, sp, "Detalhes do Look", JOptionPane.INFORMATION_MESSAGE);
    }

    private void carregarDados() {
        modeloListaItens.clear();
        gestor.listarItens().forEach(modeloListaItens::addElement);
        carregarLooks();
    }

    private void carregarLooks() {
        modeloTabela.setRowCount(0);
        gestor.listarLooks().forEach(l -> modeloTabela.addRow(new Object[]{
                l.getId(), l.getNome(), l.getItens().size(), l.getQuantidadeUtilizacoes()
        }));
    }
}
