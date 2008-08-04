package com.cannontech.dbeditor;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.List;

import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class DatabaseEditorOptionPane {

    private DatabaseEditorOptionPane() {
        
    }
    
    public static int showAlreadyUsedConfirmDialog(Component parentComponent,
            String message, String title, List<String> deviceList) {
        
        return DatabaseEditorOptionPane.showAlreadyUsedConfirmDialog(parentComponent,
                                                                            message,
                                                                            title,
                                                                            deviceList.toArray(new String[deviceList.size()]));
    }
    
    public static int showAlreadyUsedConfirmDialog(Component parentComponent,
            String message, String title, String[] devices) {
        
        final JPanel panel = new JPanel();
        
        final JList list = new JList(devices);
        list.setVisibleRowCount(8);

        final JTextArea textArea = new JTextArea(message);
        textArea.setEditable(false);
        textArea.setBackground(panel.getBackground());
        
        panel.setLayout(new BorderLayout());
        panel.add(textArea, BorderLayout.NORTH);
        panel.add(new JScrollPane(list), BorderLayout.SOUTH);
        
        int res = JOptionPane.showConfirmDialog(parentComponent,
                                                panel,
                                                title,
                                                JOptionPane.YES_NO_OPTION,
                                                JOptionPane.WARNING_MESSAGE);
        return res;    
    }
    
}
