package com.cannontech.common.gui.panel;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.event.*;

/**
 * @author
 *
 * A document that allows for auto completion in a JComboBox
 *
 * http://217-114-210-82.kunde.vdserver.de/JComboBox/
 * 
 */
public class AutoCompletionDoc extends PlainDocument
{
    private JComboBox comboBox;
    private ComboBoxModel model;
    private JTextComponent editor;
    private boolean hidePopupOnFocusLoss;
    private boolean hitBackspace;
    private boolean hitBackspaceOnSelection;
    
    public AutoCompletionDoc(JComboBox comboBox) 
    {
        this.comboBox = comboBox;
        comboBox.setEditable(true);
        model = comboBox.getModel();
        editor = (JTextComponent) comboBox.getEditor().getEditorComponent();
        editor.setDocument(this);
        // Bug 5100422 on Java 1.5: Editable JComboBox won't hide popup when tabbing out
        hidePopupOnFocusLoss=System.getProperty("java.version").startsWith("1.5");
        // Highlight whole text when focus gets lost
        editor.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                highlightCompletedText(0);
                // Workaround for Bug 5100422 - Hide Popup on focus loss
                if (hidePopupOnFocusLoss) AutoCompletionDoc.this.comboBox.setPopupVisible(false);
            }
        });
        // Highlight whole text when user hits enter
        // Register when user hits backspace
        editor.addKeyListener(new KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == e.VK_ENTER) {
                    highlightCompletedText(0);
                }
                // determine if the pressed key is backspace (needed by the remove method)
                if (e.getKeyCode() == e.VK_BACK_SPACE) {
                    hitBackspace=true;
                    hitBackspaceOnSelection=editor.getSelectionStart()!=editor.getSelectionEnd();
                } else {
                    hitBackspace=false;
                }
            }
        });
        
        // Handle initially selected object
        Object selected = comboBox.getSelectedItem();
        if (selected != null) editor.setText(selected.toString());
    }
    
    public void remove(int offs, int length) throws BadLocationException
    {
        // ignore no deletion
        if (length==0) return;
        // check positions
        if (offs < 0 || offs > getLength() || length < 0 || (offs+length)>getLength()) throw new BadLocationException("Invalid parameters.", offs);

        if (hitBackspace) {
            // user hit backspace => move the selection backwards
            // old item keeps being selected
            if (offs>0) {
                if (hitBackspaceOnSelection) offs--;
            } else {
                // User hit backspace with the cursor positioned on the start => beep
                comboBox.getToolkit().beep(); // when available use: UIManager.getLookAndFeel().provideErrorFeedback(comboBox);
            }
            highlightCompletedText(offs);
            // show popup when the user types
            comboBox.setPopupVisible(true);
        } else {
            super.remove(offs, length);
        }
    }
    
    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException
    {
        // ignore empty insert
        if (str==null || str.length()==0) return;
        // check offset position
        if (offs < 0 || offs > getLength()) throw new BadLocationException("Invalid offset - must be >= 0 and <= " + getLength(), offs);

        // construct the resulting string
        String currentText = getText(0, getLength());
        String beforeOffset = currentText.substring(0, offs);
        String afterOffset = currentText.substring(offs, currentText.length());
        String futureText = beforeOffset + str + afterOffset;
        
        // lookup and select a matching item
        Object item = lookupItem(futureText);
        if (item != null) {
            comboBox.setSelectedItem(item);
        } else {
            // keep old item selected if there is no match
            item = comboBox.getSelectedItem();
            // imitate no insert (later on offs will be incremented by str.length(): selection won't move forward)
            offs = offs-str.length();
            // provide feedback to the user that his input has been received but can not be accepted
            comboBox.getToolkit().beep(); // when available use: UIManager.getLookAndFeel().provideErrorFeedback(comboBox);
        }
        
        // display the completed string
        String itemString=item==null?"":item.toString();
        setText(itemString);
        
        // if the user selects an item via mouse the the whole string will be inserted.
        // highlight the entire text if this happens.
        if (itemString.equals(str) && offs==0) {
            highlightCompletedText(0);
        } else {
            highlightCompletedText(offs+str.length());
            // show popup when the user types
            comboBox.setPopupVisible(true);
        }
    }
    
    private void highlightCompletedText(int start)
    {
        editor.setCaretPosition(getLength());
        editor.moveCaretPosition(start);
    }
    
    /**
     * Performs a linear search. If sorted, we can use a better performing
     * algorithim.
     * 
     * @param pattern
     * @return
     */
    private Object lookupItem(String pattern)
    {
        Object selectedItem = model.getSelectedItem();
        // only search for a different item if the currently selected does not match
        if (selectedItem != null && startsWithIgnoreCase(selectedItem.toString(), pattern)) {
            return selectedItem;
        } else {
            // iterate over all items
            for (int i=0, n=model.getSize(); i < n; i++) {
                Object currentItem = model.getElementAt(i);
                // current item starts with the pattern?
                if (startsWithIgnoreCase(currentItem.toString(), pattern)) {
                    return currentItem;
                }
            }
        }
        // no item starts with the pattern => return null
        return null;
    }
    
    // checks if str1 starts with str2 - ignores case
    private boolean startsWithIgnoreCase(String str1, String str2)
    {
        return str1.toUpperCase().startsWith(str2.toUpperCase());
    }
    
    private void setText(String text) throws BadLocationException
    {
        // remove all text and insert the new text
        super.remove(0, getLength());
        super.insertString(0, text, null);
    }
    
    public static void enable(JComboBox comboBox)
    {
        new AutoCompletionDoc(comboBox);
    }
    
    private static void createAndShowGUI()
    {
        // the combo box (add/modify items if you like to)
        JComboBox comboBox = new JComboBox(new Object[] {"Ester", "Jordi", "Jordina", "Jorge", "Sergi"});
        AutoCompletionDoc.enable(comboBox);

        // create and show a window containing the combo box
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(3);
        frame.getContentPane().add(comboBox);
        frame.pack(); frame.setVisible(true);
    }
    
    public static void main(String[] args)
    {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

}
