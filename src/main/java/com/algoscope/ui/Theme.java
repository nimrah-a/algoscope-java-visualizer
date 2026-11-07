package com.algoscope.ui;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

final class Theme {
    static final Color BG = new Color(13, 20, 33);
    static final Color CARD = new Color(22, 32, 50);
    static final Color FIELD = new Color(30, 43, 64);
    static final Color TEXT = new Color(237, 244, 255);
    static final Color MUTED = new Color(155, 177, 202);
    static final Color BLUE = new Color(80, 151, 249);
    static final Color GREEN = new Color(68, 207, 155);
    static final Color YELLOW = new Color(255, 197, 94);
    static final Color PINK = new Color(244, 115, 168);
    static final Color GRID = new Color(45, 58, 80);
    static final Font FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 14);
    static final Font FONT_SMALL = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
    static final Font FONT_TITLE = new Font(Font.SANS_SERIF, Font.BOLD, 29);
    static final Font FONT_HEADER = new Font(Font.SANS_SERIF, Font.BOLD, 17);
    static final Border PAD = BorderFactory.createEmptyBorder(17, 20, 17, 20);

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
        button.setBackground(primary ? GREEN : FIELD);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(primary ? GREEN : GRID), BorderFactory.createEmptyBorder(9, 15, 9, 15)));
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

    private Theme() { }
}
