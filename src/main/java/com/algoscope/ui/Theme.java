package com.algoscope.ui;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicSpinnerUI;
import java.awt.*;

final class Theme {
    static final Color BG = new Color(10, 17, 29);
    static final Color CARD = new Color(20, 31, 49);
    static final Color FIELD = new Color(28, 43, 65);
    static final Color TEXT = new Color(237, 244, 255);
    static final Color MUTED = new Color(155, 177, 202);
    static final Color BLUE = new Color(105, 177, 251);
    static final Color GREEN = new Color(64, 213, 177);
    static final Color YELLOW = new Color(255, 197, 94);
    static final Color PINK = new Color(244, 115, 168);
    static final Color GRID = new Color(48, 66, 90);
    static final Font FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 14);
    static final Font FONT_SMALL = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
    static final Font FONT_TITLE = new Font(Font.SANS_SERIF, Font.BOLD, 33);
    static final Font FONT_HEADER = new Font(Font.SANS_SERIF, Font.BOLD, 17);
    static final Border PAD = BorderFactory.createEmptyBorder(18, 22, 18, 22);

    static JLabel label(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(TEXT);
        label.setFont(FONT);
        return label;
    }

    static JButton button(String text, boolean primary) {
        JButton button = new JButton(text);
        button.setFont(FONT.deriveFont(Font.BOLD));
        button.setForeground(primary ? BG : TEXT);
        button.setBackground(primary ? BLUE : FIELD);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(primary ? BLUE : GRID), BorderFactory.createEmptyBorder(9, 15, 9, 15)));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    static JPanel card(LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        panel.setBackground(CARD);
        panel.setBorder(PAD);
        return panel;
    }

    static void configureComponent(JComponent comp) {
        comp.setFont(FONT);
        comp.setBackground(FIELD);
        comp.setForeground(TEXT);
    }

    static void styleCombo(JComboBox<?> combo) {
        configureComponent(combo);
        combo.setFocusable(false);
        combo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GRID), BorderFactory.createEmptyBorder(2, 9, 2, 4)));
        combo.setUI(new BasicComboBoxUI() {
            @Override public void paintCurrentValue(Graphics graphics, Rectangle bounds, boolean focused) {
                graphics.setColor(FIELD);
                graphics.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
                Object current = combo.getSelectedItem();
                if (current != null) {
                    graphics.setColor(TEXT);
                    graphics.setFont(FONT);
                    FontMetrics fm = graphics.getFontMetrics();
                    String value = current.toString();
                    int maxWidth = Math.max(0, bounds.width - 20);
                    while (value.length() > 1 && fm.stringWidth(value) > maxWidth)
                        value = value.substring(0, value.length() - 2) + "…";
                    graphics.drawString(value, bounds.x + 11, bounds.y + (bounds.height - fm.getHeight()) / 2 + fm.getAscent());
                }
            }
            @Override protected JButton createArrowButton() {
                JButton arrow = new JButton("▾");
                arrow.setFont(FONT.deriveFont(Font.BOLD, 16f));
                arrow.setForeground(BLUE);
                arrow.setBackground(FIELD);
                arrow.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                arrow.setFocusPainted(false);
                arrow.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                return arrow;
            }
        });
        combo.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                                     boolean selected, boolean focus) {
                JLabel item = (JLabel) super.getListCellRendererComponent(list, value, index, selected, focus);
                item.setOpaque(true);
                item.setFont(FONT);
                item.setBackground(selected ? new Color(38, 82, 130) : FIELD);
                item.setForeground(TEXT);
                item.setBorder(BorderFactory.createEmptyBorder(7, 10, 7, 10));
                list.setSelectionBackground(new Color(38, 82, 130));
                list.setBackground(FIELD);
                list.setForeground(TEXT);
                return item;
            }
        });
    }

    static void styleSpinner(JSpinner spinner) {
        configureComponent(spinner);
        spinner.setBorder(BorderFactory.createLineBorder(GRID));
        spinner.setUI(new BasicSpinnerUI() {
            private JButton control(String symbol) {
                JButton button = new JButton(symbol);
                button.setForeground(BLUE);
                button.setBackground(FIELD);
                button.setFont(FONT_SMALL.deriveFont(Font.BOLD));
                button.setBorder(BorderFactory.createEmptyBorder(1, 7, 1, 7));
                button.setFocusPainted(false);
                return button;
            }
            @Override protected Component createNextButton() {
                JButton button = control("▴");
                button.addActionListener(e -> {
                    Object next = spinner.getNextValue();
                    if (next != null) spinner.setValue(next);
                });
                return button;
            }
            @Override protected Component createPreviousButton() {
                JButton button = control("▾");
                button.addActionListener(e -> {
                    Object previous = spinner.getPreviousValue();
                    if (previous != null) spinner.setValue(previous);
                });
                return button;
            }
        });
        if (spinner.getEditor() instanceof JSpinner.DefaultEditor editor) {
            JTextField field = editor.getTextField();
            field.setBackground(FIELD);
            field.setForeground(TEXT);
            field.setCaretColor(BLUE);
            field.setSelectionColor(new Color(38, 82, 130));
            field.setBorder(BorderFactory.createEmptyBorder(7, 11, 7, 6));
            field.setFont(FONT);
        }
    }

    private Theme() { }
}
