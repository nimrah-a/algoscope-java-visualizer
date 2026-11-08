package com.algoscope.ui;

import com.algoscope.core.*;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

/** Interactive Swing UI: replayable trace, progress controls and reproducible benchmarks. */
public final class MainWindow extends JFrame {
    private final JComboBox<Algorithm> algorithmBox = new JComboBox<>(Algorithm.values());
    private final JComboBox<Datasets.Shape> shapeBox = new JComboBox<>(Datasets.Shape.values());
    private final JSpinner sizeInput = new JSpinner(new SpinnerNumberModel(32, 5, 100, 1));
    private final JSpinner targetInput = new JSpinner(new SpinnerNumberModel(50, -1000, 1000, 1));
    private final JSlider speedSlider = new JSlider(1, 100, 72);
    private final BarPanel bars = new BarPanel();
    private final JLabel title = Theme.label("AlgoScope");
    private final JLabel description = Theme.label("");
    private final JLabel countLabel = Theme.label("—");
    private final JLabel writeLabel = Theme.label("—");
    private final JLabel stepLabel = Theme.label("—");
    private final JLabel message = Theme.label("Choose an algorithm and press Play or Step.");
    private final JLabel progressLabel = Theme.label("0 / 0");
    private final JLabel complexityLabel = Theme.label("");
    private final JButton playButton = Theme.button("▶ Play", true);
    private final JButton nextButton = Theme.button("Step →", false);
    private final JProgressBar progress = new JProgressBar();
    private final Timer timer;
    private int[] input;
    private RunResult trace;
    private int step;
    private long seed = 14;

    public MainWindow() {
        super("AlgoScope — Algorithm Visualizer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1000, 720));
        setSize(1180, 860);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Theme.BG);
        setLayout(new BorderLayout(0, 16));
        getRootPane().setBorder(BorderFactory.createEmptyBorder(20, 23, 20, 23));

        timer = new Timer(delay(), e -> advance());
        add(makeHeader(), BorderLayout.NORTH);
        add(makeCenter(), BorderLayout.CENTER);
        add(makeFooter(), BorderLayout.SOUTH);

        algorithmBox.addActionListener(e -> { stop(); refreshForAlgorithm(); regenerate(false); });
        shapeBox.addActionListener(e -> regenerate(false));
        sizeInput.addChangeListener(e -> regenerate(false));
        targetInput.addChangeListener(e -> { if (currentAlgorithm().isSearch()) prepareTrace(); });
        speedSlider.addChangeListener(e -> timer.setDelay(delay()));
        playButton.addActionListener(e -> togglePlay());
        nextButton.addActionListener(e -> { stop(); advance(); });
        refreshForAlgorithm();
        regenerate(false);
    }

    private JPanel makeHeader() {
        JPanel header = new JPanel(new BorderLayout(0, 14));
        header.setOpaque(false);
        // Keep the heading and descriptive sentence away from the window edges.
        header.setBorder(BorderFactory.createEmptyBorder(18, 22, 20, 22));
        title.setFont(Theme.FONT_TITLE);
        title.setForeground(Theme.TEXT);
        header.add(title, BorderLayout.NORTH);
        description.setForeground(Theme.MUTED);
        header.add(description, BorderLayout.SOUTH);
        return header;
    }

    private JPanel makeCenter() {
        JPanel root = new JPanel(new BorderLayout(0, 12));
        root.setOpaque(false);
        JPanel options = Theme.card(new GridLayout(2, 1, 0, 12));
        JPanel firstLine = horizontal();
        firstLine.add(labeledControl("Algorithm", algorithmBox));
        firstLine.add(Box.createHorizontalStrut(28));
        firstLine.add(labeledControl("Dataset", shapeBox));
        firstLine.add(Box.createHorizontalStrut(28));
        firstLine.add(labeledControl("Size", sizeInput));
        firstLine.add(Box.createHorizontalGlue());
        options.add(firstLine);

        JPanel secondLine = horizontal();
        JButton shuffle = Theme.button("↻ New dataset", false);
        shuffle.addActionListener(e -> regenerate(true));
        secondLine.add(shuffle);
        secondLine.add(Box.createHorizontalStrut(28));
        secondLine.add(labeledControl("Search target", targetInput));
        secondLine.add(Box.createHorizontalStrut(28));
        secondLine.add(labeledControl("Animation speed", speedSlider));
        secondLine.add(Box.createHorizontalGlue());
        options.add(secondLine);
        for (JComponent comp : List.of(algorithmBox, shapeBox, sizeInput, targetInput, speedSlider)) Theme.configureComponent(comp);
        speedSlider.setPreferredSize(new Dimension(125, 29));
        secondLine.add(Box.createHorizontalGlue());
        options.add(secondLine);
        Theme.styleCombo(algorithmBox);
        Theme.styleCombo(shapeBox);
        Theme.styleSpinner(sizeInput);
        Theme.styleSpinner(targetInput);
        algorithmBox.setPreferredSize(new Dimension(190, 38));
        shapeBox.setPreferredSize(new Dimension(170, 38));
        sizeInput.setPreferredSize(new Dimension(80, 38));
        targetInput.setPreferredSize(new Dimension(110, 38));
        speedSlider.setOpaque(false);
        speedSlider.setForeground(Theme.BLUE);
        root.add(options, BorderLayout.NORTH);

        JPanel chartCard = Theme.card(new BorderLayout(0, 11));
        JPanel chartHead = horizontal();
        JLabel chartTitle = Theme.label("LIVE ARRAY VIEW");
        chartTitle.setForeground(Theme.MUTED);
        chartHead.add(chartTitle);
        chartHead.add(Box.createHorizontalGlue());
        chartHead.add(coloredKey(Theme.BLUE, "Default"));
        chartHead.add(coloredKey(Theme.YELLOW, "Active"));
        chartHead.add(coloredKey(Theme.PINK, "Compared"));
        chartHead.add(coloredKey(Theme.GREEN, "Complete"));
        chartCard.add(chartHead, BorderLayout.NORTH);
        chartCard.add(bars, BorderLayout.CENTER);
        root.add(chartCard, BorderLayout.CENTER);
        return root;
    }

    /** Pair a control with its label using consistent spacing across both rows. */
    private static JPanel labeledControl(String caption, JComponent control) {
        JPanel group = horizontal();
        JLabel label = Theme.label(caption);
        group.add(label);
        group.add(Box.createHorizontalStrut(14));
        group.add(control);
        return group;
    }

    private JComponent coloredKey(Color color, String text) {
        JLabel l = Theme.label("● " + text);
        l.setForeground(color);
        l.setFont(Theme.FONT_SMALL);
        l.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        return l;
    }

    private JPanel makeFooter() {
        JPanel root = new JPanel(new BorderLayout(0, 12));
        root.setOpaque(false);
        JPanel stats = new JPanel(new GridLayout(1, 3, 12, 0));
        stats.setOpaque(false);
        stats.add(statCard("COMPARISONS", countLabel));
        stats.add(statCard("ARRAY WRITES", writeLabel));
        stats.add(statCard("CURRENT STEP", stepLabel));
        root.add(stats, BorderLayout.NORTH);

        JPanel lower = Theme.card(new BorderLayout(0, 11));
        JPanel actions = horizontal();
        JButton reset = Theme.button("Reset", false);
        reset.addActionListener(e -> prepareTrace());
        JButton compare = Theme.button("Compare all sorts", false);
        compare.addActionListener(e -> showBenchmark());
        actions.add(playButton);
        actions.add(nextButton);
        actions.add(reset);
        actions.add(Box.createHorizontalStrut(10));
        actions.add(compare);
        actions.add(Box.createHorizontalGlue());
        actions.add(progressLabel);
        lower.add(actions, BorderLayout.NORTH);
        progress.setForeground(Theme.BLUE);
        progress.setBackground(Theme.FIELD);
        progress.setBorderPainted(false);
        lower.add(progress, BorderLayout.CENTER);
        JPanel details = new JPanel(new GridLayout(2, 1, 0, 6));
        details.setOpaque(false);
        message.setForeground(Theme.YELLOW);
        complexityLabel.setForeground(Theme.MUTED);
        details.add(message);
        details.add(complexityLabel);
        lower.add(details, BorderLayout.SOUTH);
        root.add(lower, BorderLayout.CENTER);
        return root;
    }

    private JPanel statCard(String name, JLabel value) {
        JPanel panel = Theme.card(new GridLayout(2, 1, 0, 5));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.GRID),
                BorderFactory.createEmptyBorder(12, 17, 12, 17)));
        JLabel caption = Theme.label(name);
        caption.setForeground(Theme.MUTED);
        caption.setFont(Theme.FONT_SMALL);
        value.setFont(Theme.FONT_HEADER.deriveFont(24f));
        value.setForeground(Theme.TEXT);
        panel.add(caption);
        panel.add(value);
        return panel;
    }

    private static JPanel horizontal() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        return panel;
    }

    private int delay() { return 15 + (100 - speedSlider.getValue()) * 6; }
    private Algorithm currentAlgorithm() { return (Algorithm) algorithmBox.getSelectedItem(); }

    private void refreshForAlgorithm() {
        Algorithm selected = currentAlgorithm();
        if (selected == null) return;
        targetInput.setEnabled(selected.isSearch());
        description.setText("Explore real execution steps, operations, and complexity — no external dependencies.");
        complexityLabel.setText("BEST " + selected.best() + "     WORST " + selected.worst() + "     SPACE " + selected.space() + "     |     " + selected.summary());
    }

    private void regenerate(boolean freshSeed) {
        if (freshSeed) seed++;
        input = Datasets.create((Integer) sizeInput.getValue(), (Datasets.Shape) shapeBox.getSelectedItem(), seed);
        if (currentAlgorithm().isSearch()) Arrays.sort(input);
        prepareTrace();
    }

    private void prepareTrace() {
        stop();
        if (input == null) return;
        try {
            trace = new Engine().run(currentAlgorithm(), input, (Integer) targetInput.getValue(), true);
            step = 0;
            progress.setMinimum(0);
            progress.setMaximum(Math.max(1, trace.frames().size() - 1));
            render();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "AlgoScope error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void togglePlay() {
        if (timer.isRunning()) { stop(); return; }
        if (trace == null) return;
        if (step >= trace.frames().size() - 1) step = 0;
        timer.setDelay(delay());
        timer.start();
        playButton.setText("❚❚ Pause");
    }

    private void stop() {
        timer.stop();
        playButton.setText("▶ Play");
    }

    private void advance() {
        if (trace == null) return;
        if (step < trace.frames().size() - 1) step++;
        render();
        if (step == trace.frames().size() - 1) stop();
    }

    private void render() {
        com.algoscope.core.Frame current = trace.frames().get(step);
        boolean complete = step == trace.frames().size() - 1;
        bars.display(current, complete && !trace.algorithm().isSearch());
        countLabel.setText(String.format("%,d", current.comparisons()));
        writeLabel.setText(String.format("%,d", current.writes()));
        stepLabel.setText(String.valueOf(step));
        progress.setValue(step);
        progressLabel.setText(step + " / " + (trace.frames().size() - 1));
        message.setText(current.message());
    }

    private void showBenchmark() {
        stop();
        int[] baseline = input.clone();
        List<Benchmark.Entry> result = Benchmark.compare(baseline);
        DefaultTableModel model = new DefaultTableModel(new Object[] {"Algorithm", "Comparisons", "Writes", "Time (ms)"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        for (Benchmark.Entry e : result) {
            model.addRow(new Object[] {e.algorithm().label(), e.comparisons(), e.writes(), String.format("%.3f", e.nanos() / 1_000_000.0)});
        }
        JTable table = new JTable(model);
        table.setFont(Theme.FONT);
        table.setRowHeight(33);
        table.setBackground(Theme.CARD);
        table.setForeground(Theme.TEXT);
        table.setGridColor(Theme.GRID);
        table.setSelectionBackground(Theme.FIELD);
        table.getTableHeader().setBackground(Theme.FIELD);
        table.getTableHeader().setForeground(Theme.TEXT);
        table.getTableHeader().setFont(Theme.FONT.deriveFont(Font.BOLD));
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setHorizontalAlignment(SwingConstants.RIGHT);
        for (int col = 1; col < table.getColumnCount(); col++) table.getColumnModel().getColumn(col).setCellRenderer(renderer);
        JPanel pane = Theme.card(new BorderLayout(0, 15));
        JLabel caption = Theme.label("All algorithms sorted the same " + baseline.length + " values. Time is a single run (JIT/CPU noise applies).");
        caption.setForeground(Theme.MUTED);
        pane.add(caption, BorderLayout.NORTH);
        pane.add(new JScrollPane(table), BorderLayout.CENTER);
        JLabel note = Theme.label("Comparisons and array writes are deterministic for the input; timing is illustrative, not a formal benchmark.");
        note.setForeground(Theme.MUTED);
        pane.add(note, BorderLayout.SOUTH);
        pane.setPreferredSize(new Dimension(760, 325));
        JOptionPane.showMessageDialog(this, pane, "Compare sorting algorithms", JOptionPane.PLAIN_MESSAGE);
    }
}
