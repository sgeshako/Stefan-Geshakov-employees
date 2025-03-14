package org.sgeshako;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public final class Gui {

    private Gui() {}

    public static String openDialog() {
        FileDialog dialog = new FileDialog((Frame)null, "Select File to Open");
        dialog.setMode(FileDialog.LOAD);
        dialog.setVisible(true);
        String file = dialog.getFile();
        dialog.dispose();
        return new File(dialog.getDirectory(), file).getAbsolutePath();
    }

    public static void showDialog(String[] columnNames, String[][] rowData) {
        JTable table = new JTable(rowData, columnNames);
        JScrollPane scrollPane = new JScrollPane(table); // Makes table scrollable

        // Show table inside a dialog
        JOptionPane.showMessageDialog(null, scrollPane, "Output CSV Data", JOptionPane.INFORMATION_MESSAGE);
    }
}
