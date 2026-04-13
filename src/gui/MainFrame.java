package gui;

import service.GestorVestuario;
import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private final GestorVestuario gestor;

    public MainFrame() {
        this.gestor = new GestorVestuario();
        setTitle("GVP — Gestor de Vestuário Pessoal");
        setSize(1050, 720);
        setMinimumSize(new Dimension(800, 550));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("👕 Itens",       new ItemPanel(gestor));
        tabs.addTab("👔 Looks",       new LookPanel(gestor));
        tabs.addTab("🤝 Empréstimos", new EmprestimoPanel(gestor));
        tabs.addTab("🧺 Lavagens",    new LavagemPanel(gestor));
        tabs.addTab("📊 Estatísticas", new EstatisticaPanel(gestor));

        setLayout(new BorderLayout());
        add(tabs, BorderLayout.CENTER);

        JLabel status = new JLabel("  GVP v1.1 — Gestor de Vestuário Pessoal");
        status.setBorder(BorderFactory.createLoweredBevelBorder());
        status.setFont(status.getFont().deriveFont(11f));
        add(status, BorderLayout.SOUTH);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            new MainFrame().setVisible(true);
        });
    }
}
