package com.algoscope.ui;

import com.algoscope.core.Frame;
import javax.swing.*;
import java.awt.*;

/** Custom-painted bar chart; rendering doesn't affect algorithm measurements. */
final class BarPanel extends JPanel {
    private Frame frame;
    private boolean finished;

    BarPanel() {
        setOpaque(true);
        setBackground(Theme.CARD);
        setPreferredSize(new Dimension(760, 375));
    }

    void display(Frame next, boolean complete) {
        this.frame = next;
        this.finished = complete;
        repaint();
    }

    @Override protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics.create();
        try {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();
            int left = 25, top = 32, bottom = h - 36;
            g.setFont(Theme.FONT_SMALL);
            g.setColor(Theme.MUTED);
            g.drawString("VALUE", left, 19);
            g.setColor(Theme.GRID);
            for (int i = 0; i <= 4; i++) {
                int y = bottom - i * (bottom - top) / 4;
                g.drawLine(left, y, w - 22, y);
            }
            if (frame == null) return;
            int[] values = frame.values();
            if (values.length == 0) return;
            int max = 1;
            for (int v : values) max = Math.max(max, v);
            double available = Math.max(1, w - left - 24);
            double step = available / values.length;
            double barWidth = Math.max(1.0, step - Math.min(5.0, step * .23));
            for (int i = 0; i < values.length; i++) {
                int barH = (int) Math.round((bottom - top - 5) * Math.max(0, values[i]) / (double) max);
                int x = left + (int) Math.round(i * step + (step - barWidth) / 2.0);
                int y = bottom - barH;
                Color color = finished || frame.sortedFrom() >= 0 && i >= frame.sortedFrom() ? Theme.GREEN : Theme.BLUE;
                if (i == frame.first()) color = Theme.YELLOW;
                if (i == frame.second()) color = Theme.PINK;
                g.setColor(color);
                g.fillRoundRect(x, y, (int) Math.max(1, barWidth), Math.max(2, barH), 5, 5);
                if (values.length <= 22) {
                    g.setColor(Theme.TEXT);
                    String s = String.valueOf(values[i]);
                    FontMetrics fm = g.getFontMetrics();
                    g.drawString(s, (int) (x + barWidth / 2 - fm.stringWidth(s) / 2.0), Math.max(28, y - 7));
                }
            }
        } finally { g.dispose(); }
    }
}
