package com.cannontech.dbeditor.menu;

/**
 * This type was created in VisualAge.
 */
import java.awt.Event;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;

import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;

import com.cannontech.common.gui.util.CTIKeyEventDispatcher;

public class EditMenu extends javax.swing.JMenu {
    public JMenuItem editMenuItem;
    public JMenuItem copyMenuItem;
    public JMenuItem changeTypeMenuItem;
    public JMenuItem deleteMenuItem;
    public JMenuItem searchMenuItem;

    private JSeparator separator1;

    public EditMenu() {
        super();
        initialize();
    }

    private void initialize() {
        java.awt.Font font = new java.awt.Font("dialog", 0, 14);

        editMenuItem = new JMenuItem("Edit...");
        editMenuItem.setFont(font);
        editMenuItem.setMnemonic('E');
        editMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, Event.CTRL_MASK));

        copyMenuItem = new JMenuItem("Copy...");
        copyMenuItem.setFont(font);
        copyMenuItem.setMnemonic('C');
        copyMenuItem.setEnabled(true);
        copyMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, Event.CTRL_MASK));

        changeTypeMenuItem = new JMenuItem("Change Type...");
        changeTypeMenuItem.setFont(font);
        changeTypeMenuItem.setMnemonic('T');
        changeTypeMenuItem.setEnabled(true);
        changeTypeMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, Event.CTRL_MASK));

        deleteMenuItem = new JMenuItem("Delete");
        deleteMenuItem.setFont(font);
        deleteMenuItem.setMnemonic('d');
        deleteMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, Event.CTRL_MASK));

        separator1 = new JSeparator();

        searchMenuItem = new JMenuItem("Find...");
        searchMenuItem.setFont(font);
        searchMenuItem.setMnemonic('f');
        searchMenuItem.setEnabled(true);
        searchMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, Event.CTRL_MASK));

        setText("Edit");
        setFont(font);
        setMnemonic('e');

        add(editMenuItem);
        add(copyMenuItem);
        add(changeTypeMenuItem);
        add(deleteMenuItem);
        add(separator1);
        add(searchMenuItem);

        /*
         * This way to handle accelerators was changed to work with JRE 1.4. The
         * accelerator event would always get consumed by the component focus
         * was in. This ensures that accelerator fires the correct event ONLY
         * (that is why true is returned on after each click). We keep the above
         * accelerators set for display purposes.
         */
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new CTIKeyEventDispatcher() {
            @Override
            public boolean handleKeyEvent(KeyEvent e) {
                // do the checks of the keystrokes here
                if (e.getKeyCode() == KeyEvent.VK_C && e.isControlDown()) {
                    if (copyMenuItem.isEnabled()) {
                        copyMenuItem.doClick();
                        return true;
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_E && e.isControlDown()) {
                    if (editMenuItem.isEnabled()) {
                        editMenuItem.doClick();
                        return true;
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_F && e.isControlDown()) {
                    if (searchMenuItem.isEnabled()) {
                        searchMenuItem.doClick();
                        return true;
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_DELETE && e.isControlDown()) {
                    if (deleteMenuItem.isEnabled()) {
                        deleteMenuItem.doClick();
                        return true;
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_T && e.isControlDown()) {
                    if (changeTypeMenuItem.isEnabled()) {
                        changeTypeMenuItem.doClick();
                        return true;
                    }
                }
                // its this the last handling of the KeyEvent in this KeyboardFocusManager?
                return false;
            }
        });
    }
}
