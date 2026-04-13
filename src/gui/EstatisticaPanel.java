package gui;

import interfaces.IEmprestavel;
import interfaces.ILavavel;
import model.*;
import service.GestorVestuario;
import javax.swing.*;
import java.awt.*;

public class EstatisticaPanel extends JPanel {

    private final GestorVestuario gestor;
    private JTextArea areaEstatisticas;

    public EstatisticaPanel(GestorVestuario gestor) {
        this.gestor = gestor;
        initializeComponents();
        setupLayout();
        atualizarEstatisticas();
    }

    private void initializeComponents() {
        areaEstatisticas = new JTextArea();
        areaEstatisticas.setEditable(false);
        areaEstatisticas.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        areaEstatisticas.setMargin(new Insets(8, 12, 8, 12));
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        JButton btnAtualizar = new JButton("Atualizar Estatísticas");
        btnAtualizar.addActionListener(e -> atualizarEstatisticas());

        JPanel topo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topo.add(btnAtualizar);

        add(topo, BorderLayout.NORTH);
        add(new JScrollPane(areaEstatisticas), BorderLayout.CENTER);
    }

    private void atualizarEstatisticas() {
        StringBuilder sb = new StringBuilder();
        sb.append("╔══════════════════════════════════════╗\n");
        sb.append("║    ESTATÍSTICAS DO VESTUÁRIO — GVP   ║\n");
        sb.append("╚══════════════════════════════════════╝\n\n");

        sb.append("📦 RESUMO GERAL\n");
        sb.append("   Total de itens    : ").append(gestor.listarItens().size()).append("\n");
        sb.append("   Total de looks    : ").append(gestor.listarLooks().size()).append("\n");
        sb.append("   Total de lavagens : ").append(gestor.listarLavagens().size()).append("\n");
        sb.append("   Itens emprestados : ").append(gestor.getItensEmprestados().size()).append("\n\n");

        appendLista(sb, "🏆 ITENS MAIS USADOS",
                gestor.getItensMaisUsados(5),
                i -> i.getNome() + " — " + i.getQuantidadeUtilizacoes() + " usos");

        appendLista(sb, "💤 ITENS MENOS USADOS",
                gestor.getItensMenosUsados(5),
                i -> i.getNome() + " — " + i.getQuantidadeUtilizacoes() + " usos");

        appendLista(sb, "👗 LOOKS MAIS USADOS",
                gestor.getLooksMaisUsados(5),
                l -> l.getNome() + " — " + l.getQuantidadeUtilizacoes() + " usos");

        appendLista(sb, "🧺 ITENS MAIS LAVADOS",
                gestor.getItensMaisLavados(5),
                i -> i.getNome() + " — " + ((ILavavel) i).getQuantidadeLavagens() + " lavagens");

        sb.append("🤝 ITENS EMPRESTADOS\n");
        java.util.List<Item> emp = gestor.getItensEmprestados();
        if (emp.isEmpty()) {
            sb.append("   Nenhum item emprestado no momento.\n");
        } else {
            emp.forEach(i -> {
                IEmprestavel ie = (IEmprestavel) i;
                sb.append("   • ").append(i.getNome())
                  .append(" → ").append(ie.getNomeEmprestado())
                  .append(" (há ").append(ie.quantidadeDeDiasDesdeOEmprestimo()).append(" dias)\n");
            });
        }

        areaEstatisticas.setText(sb.toString());
        areaEstatisticas.setCaretPosition(0);
    }

    private <T> void appendLista(StringBuilder sb, String titulo,
                                  java.util.List<T> lista,
                                  java.util.function.Function<T, String> formatter) {
        sb.append(titulo).append("\n");
        if (lista.isEmpty()) {
            sb.append("   (nenhum registro)\n");
        } else {
            for (int i = 0; i < lista.size(); i++) {
                sb.append("   ").append(i + 1).append(". ")
                  .append(formatter.apply(lista.get(i))).append("\n");
            }
        }
        sb.append("\n");
    }
}
