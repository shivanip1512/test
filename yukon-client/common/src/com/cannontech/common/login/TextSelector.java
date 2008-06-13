package com.cannontech.common.login;

import java.awt.KeyEventPostProcessor;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.text.JTextComponent;

public class TextSelector {
    private static FocusHandler installedInstance;
    private static KeyEventPostProcessor keppInstance;
    private static boolean tabbed = false;
    private static boolean initial = true;
    
    /**
     * Install an PropertyChangeList listener to the default focus manager
     * and selects text when a text component is focused.       
     */
    public static void install() {
        //already installed
        if (installedInstance != null) {
            return;
        }

        installedInstance = new FocusHandler();

        KeyboardFocusManager kfm = KeyboardFocusManager.getCurrentKeyboardFocusManager();

        kfm.addPropertyChangeListener("focusOwner", installedInstance);
        
        keppInstance = new KeyEventHandler();
        kfm.addKeyEventPostProcessor(keppInstance);
    }

    public static void uninstall() {
        if (installedInstance != null) {
            KeyboardFocusManager kfm = KeyboardFocusManager.getCurrentKeyboardFocusManager();
            kfm.removePropertyChangeListener("focusOwner", installedInstance);
        }
        if (keppInstance != null) {
            KeyboardFocusManager kfm = KeyboardFocusManager.getCurrentKeyboardFocusManager();
            kfm.removeKeyEventPostProcessor(keppInstance);
        }
    }

    private static class FocusHandler implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getNewValue() instanceof JTextComponent) {
                JTextComponent text = (JTextComponent) evt.getNewValue();
                //select text if the component is editable
                //and the caret is at the end of the text
                if (text.isEditable() && (TextSelector.tabbed || TextSelector.initial)) {
                    text.selectAll();
                }
                TextSelector.tabbed = false;
                TextSelector.initial = false;
            }
        }

    }
    
    private static class KeyEventHandler implements KeyEventPostProcessor {

        @Override
        public boolean postProcessKeyEvent(KeyEvent e) {
            if (e.getID() == KeyEvent.KEY_PRESSED && e.getKeyChar() == '\t') {
                TextSelector.tabbed = true;
            }
            return false;
        }
        
    }
}
