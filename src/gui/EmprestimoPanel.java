package gui;

import interfaces.IEmprestavel;
import model.Item;
import service.GestorVestuario;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;

public class EmprestimoPanel extends JPanel {

    private final GestorVestuario gestor;
    private JTable tabelaEmprestimos;
    private DefaultTableModel modeloTabela;
    private JComboBox<Item> cbItensEmprestáveis;
    private JTextField txtNomeEmprestado;

    public EmprestimoPanel(GestorVestuario gestor) {
        this.gestor = gestor;
        initializeComponents();
        setupLayout();
        carregarDados();
    }

    private void initializeComponents() {
        String[] colunas = {"Item", "Emprestado para", "Data Empréstimo", "Dias Emprestado"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabelaEmprestimos  = new JTable(modeloTabela);
        cbItensEmprestáveis = new JComboBox<>();
        txtNomeEmprestado  = new JTextField(20);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        JPanel painelForm = new JPanel(new GridBagLayout());
        painelForm.setBorder(BorderFactory.createTitledBorder("Registrar Empréstimo"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 6, 5, 6);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0; painelForm.add(new JLabel("Item:"), gbc);
        gbc.gridx = 1;                painelForm.add(cbItensEmprestáveis, gbc);
        gbc.gridx = 0; gbc.gridy = 1; painelForm.add(new JLabel("Emprestado para:"), gbc);
        gbc.gridx = 1;                painelForm.add(txtNomeEmprestado, gbc);

        JButton btnEmprestar = new JButton("Emprestar");
        JButton btnDevolver  = new JButton("Devolver Selecionado");
        JButton btnAtualizar = new JButton("Atualizar");

        btnEmprestar.addActionListener(e -> registrarEmprestimo());
        btnDevolver.addActionListener(e -> registrarDevolucao());
        btnAtualizar.addActionListener(e -> carregarDados());

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelBotoes.add(btnEmprestar);
        painelBotoes.add(btnDevolver);
        painelBotoes.add(btnAtualizar);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        painelForm.add(painelBotoes, gbc);

        add(painelForm, BorderLayout.NORTH);
        add(new JScrollPane(tabelaEmprestimos), BorderLayout.CENTER);
    }

    private void registrarEmprestimo() {
        Item item = (Item) cbItensEmprestáveis.getSelectedItem();
        String nome = txtNomeEmprestado.getText().trim();

        if (item == null) { JOptionPane.showMessageDialog(this, "Selecione um item!"); return; }
        if (nome.isEmpty()) { JOptionPane.showMessageDialog(this, "Digite o nome da pessoa!"); return; }

        gestor.emprestarItem(item.getId(), nome, LocalDate.now());
        txtNomeEmprestado.setText("");
        carregarDados();
        JOptionPane.showMessageDialog(this, "Empréstimo registrado!");
    }

    private void registrarDevolucao() {
        int row = tabelaEmprestimos.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Selecione um empréstimo!"); return; }

        String nomeItem = (String) modeloTabela.getValueAt(row, 0);
        gestor.listarItens().stream()
                .filter(i -> i.toString().equals(nomeItem) && i instanceof IEmprestavel)
                .findFirst()
                .ifPresent(i -> {
                    gestor.devolverItem(i.getId());
                    carregarDados();
                    JOptionPane.showMessageDialog(this, "Devolução registrada!");
                });
    }

    private void carregarDados() {
        // Atualiza combobox com itens disponíveis para empréstimo
        cbItensEmprestáveis.removeAllItems();
        for (Item item : gestor.listarItens()) {
            if (item instanceof IEmprestavel && !((IEmprestavel) item).estaEmprestado()) {
                cbItensEmprestáveis.addItem(item);
            }
        }
        // Atualiza tabela com itens emprestados
        modeloTabela.setRowCount(0);
        for (Item item : gestor.getItensEmprestados()) {
            if (item instanceof IEmprestavel) {
                IEmprestavel ie = (IEmprestavel) item;
                modeloTabela.addRow(new Object[]{
                    item.toString(),
                    ie.getNomeEmprestado(),
                    ie.getDataEmprestimo(),
                    ie.quantidadeDeDiasDesdeOEmprestimo() + " dias"
                });
            }
        }
    }
}
