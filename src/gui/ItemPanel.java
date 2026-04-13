package gui;

import model.*;
import service.GestorVestuario;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.UUID;

/**
 * Painel de gerenciamento de itens do vestuário.
 * Correção: botões eram criados duas vezes na versão original
 * (uma vez em initializeComponents e novamente em setupLayout via
 * double-brace initialization). Agora são criados uma única vez em setupLayout.
 */
public class ItemPanel extends JPanel {

    private final GestorVestuario gestor;
    private JTable tabelaItens;
    private DefaultTableModel modeloTabela;
    private JTextField txtNome, txtCor, txtTamanho, txtLoja;
    private JComboBox<Item.Conservacao> cbConservacao;
    private JComboBox<String> cbTipoItem;

    public ItemPanel(GestorVestuario gestor) {
        this.gestor = gestor;
        initializeComponents();
        setupLayout();
        carregarItens();
    }

    private void initializeComponents() {
        String[] colunas = {"ID", "Nome", "Tipo", "Cor", "Tamanho", "Conservação", "Utilizações"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        tabelaItens = new JTable(modeloTabela);
        tabelaItens.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // Oculta coluna ID (índice 0) — presente apenas para referência interna
        tabelaItens.getColumnModel().getColumn(0).setMinWidth(0);
        tabelaItens.getColumnModel().getColumn(0).setMaxWidth(0);
        tabelaItens.getColumnModel().getColumn(0).setWidth(0);

        txtNome   = new JTextField(20);
        txtCor    = new JTextField(15);
        txtTamanho = new JTextField(10);
        txtLoja   = new JTextField(20);
        cbConservacao = new JComboBox<>(Item.Conservacao.values());
        cbTipoItem    = new JComboBox<>(new String[]{"Camisa", "Calça", "Relógio", "Roupa Íntima"});
    }

    private void setupLayout() {
        setLayout(new BorderLayout(0, 4));

        // Formulário
        JPanel painelFormulario = new JPanel(new GridBagLayout());
        painelFormulario.setBorder(BorderFactory.createTitledBorder("Adicionar Item"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.anchor = GridBagConstraints.WEST;

        addFormRow(painelFormulario, gbc, 0, 0, "Nome:",        txtNome);
        addFormRow(painelFormulario, gbc, 0, 1, "Tipo:",        cbTipoItem);
        addFormRow(painelFormulario, gbc, 0, 2, "Cor:",         txtCor);
        addFormRow(painelFormulario, gbc, 2, 0, "Tamanho:",     txtTamanho);
        addFormRow(painelFormulario, gbc, 2, 1, "Loja:",        txtLoja);
        addFormRow(painelFormulario, gbc, 2, 2, "Conservação:", cbConservacao);

        // Botões
        JButton btnAdicionar = new JButton("Adicionar");
        JButton btnRemover   = new JButton("Remover Selecionado");
        JButton btnAtualizar = new JButton("Atualizar Lista");

        btnAdicionar.addActionListener(e -> adicionarItem());
        btnRemover.addActionListener(e -> removerItem());
        btnAtualizar.addActionListener(e -> carregarItens());

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        painelBotoes.add(btnAdicionar);
        painelBotoes.add(btnRemover);
        painelBotoes.add(btnAtualizar);

        JPanel topo = new JPanel(new BorderLayout());
        topo.add(painelFormulario, BorderLayout.CENTER);
        topo.add(painelBotoes, BorderLayout.SOUTH);

        add(topo, BorderLayout.NORTH);
        add(new JScrollPane(tabelaItens), BorderLayout.CENTER);
    }

    private void addFormRow(JPanel p, GridBagConstraints gbc,
                            int col, int row, String label, JComponent field) {
        gbc.gridx = col; gbc.gridy = row;
        p.add(new JLabel(label), gbc);
        gbc.gridx = col + 1;
        p.add(field, gbc);
    }

    private void adicionarItem() {
        String nome      = txtNome.getText().trim();
        String cor       = txtCor.getText().trim();
        String tamanho   = txtTamanho.getText().trim();
        String loja      = txtLoja.getText().trim();
        Item.Conservacao conservacao = (Item.Conservacao) cbConservacao.getSelectedItem();
        String tipoItem  = (String) cbTipoItem.getSelectedItem();

        if (nome.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nome é obrigatório!", "Atenção",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String id = UUID.randomUUID().toString();
        Item item;
        switch (tipoItem) {
            case "Camisa":       item = new Camisa(id, nome, cor, tamanho, loja, conservacao); break;
            case "Calça":        item = new Calca(id, nome, cor, tamanho, loja, conservacao);  break;
            case "Relógio":      item = new Relogio(id, nome, cor, tamanho, loja, conservacao); break;
            case "Roupa Íntima": item = new RoupaIntima(id, nome, cor, tamanho, loja, conservacao); break;
            default: return;
        }

        gestor.adicionarItem(item);
        limparCampos();
        carregarItens();
        JOptionPane.showMessageDialog(this, "Item adicionado com sucesso!");
    }

    private void removerItem() {
        int row = tabelaItens.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um item para remover.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Confirmar remoção do item selecionado?", "Confirmar",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            String id = (String) modeloTabela.getValueAt(row, 0);
            gestor.removerItem(id);
            carregarItens();
            JOptionPane.showMessageDialog(this, "Item removido.");
        }
    }

    public void carregarItens() {
        modeloTabela.setRowCount(0);
        for (Item item : gestor.listarItens()) {
            modeloTabela.addRow(new Object[]{
                item.getId(),
                item.getNome(),
                item.getClass().getSimpleName(),
                item.getCor(),
                item.getTamanho(),
                item.getConservacao(),
                item.getQuantidadeUtilizacoes()
            });
        }
    }

    private void limparCampos() {
        txtNome.setText("");
        txtCor.setText("");
        txtTamanho.setText("");
        txtLoja.setText("");
        cbConservacao.setSelectedIndex(0);
        cbTipoItem.setSelectedIndex(0);
    }
}
