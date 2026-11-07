package com.algoscope;

import com.algoscope.ui.MainWindow;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/** Desktop entry point. Run this class from IntelliJ IDEA. */
public final class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); }
            catch (Exception ignored) { /* Default look and feel is still usable. */ }
            new MainWindow().setVisible(true);
        });
    }

    private Main() { }
}
