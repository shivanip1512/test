package com.cannontech.common.util;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JInternalFrame;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import com.cannontech.clientutils.CTILogger;

public class SwingUtil {
    // prevent instantiation
    private SwingUtil() {
    }

    public final static Integer getIntervalComboBoxSecondsValue(JComboBox<String> comboBox) {
        return getIntervalSecondsValue((String) comboBox.getSelectedItem());
    }

    public final static Integer getIntervalSecondsValue(String selectedString) {
        Integer generic = null;
        Integer retVal = null;
        int multiplier = 1;

        if (selectedString == null) {
            retVal = new Integer(0); // we have no idea, just use zero
        } else if (selectedString.toLowerCase().compareTo("daily") == 0) {
            generic = new Integer(86400);
            return generic;
        } else if (selectedString.toLowerCase().compareTo("weekly") == 0) {
            generic = new Integer(604800);
            return generic;
        } else if (selectedString.toLowerCase().compareTo("monthly") == 0) {
            generic = new Integer(2592000);
            return generic;
        } else if (selectedString.toLowerCase().indexOf("second") != -1) {
            multiplier = 1;
        } else if (selectedString.toLowerCase().indexOf("minute") != -1) {
            multiplier = 60;
        } else if (selectedString.toLowerCase().indexOf("hour") != -1) {
            multiplier = 3600;
        } else if (selectedString.toLowerCase().indexOf("day") != -1) {
            multiplier = 86400;
        } else {
            multiplier = 0; // we have no idea, just use zero
        }

        try {
            int loc = selectedString.toLowerCase().indexOf(" ");

            retVal = new Integer(multiplier * Integer.parseInt(selectedString.toLowerCase().substring(0, loc)));
        } catch (Exception e) {
            CTILogger.error("Unable to parse combo box text string into seconds, using ZERO", e);
            retVal = new Integer(0);
        }

        return retVal;
    }

    public static void setCheckBoxState(JCheckBox cBox, Character state) {
        char c = Character.toLowerCase(state.charValue());

        if (c == 'y') {
            cBox.setSelected(true);
        } else if (c == 'n') {
            cBox.setSelected(false);
        }
    }

    public static final void setIntervalComboBoxSelectedItem(JComboBox<String> comboBox, double scanRateSecs) {
        String scanRateString = null;
        boolean found = false;

        // when we divide the scanRateSecs value, we must use a double formatted number
        // so we are returned a double value (Ex: numberFormatter.format(scanRateSecs/60.0)

        DecimalFormat numberFormatter = new DecimalFormat();
        if (scanRateSecs < 60) {
            scanRateString = numberFormatter.format(scanRateSecs) + " second";
        } else if (scanRateSecs < 3600) {
            scanRateString = numberFormatter.format(scanRateSecs / 60.0) + " minute";
        } else if (scanRateSecs < 86400) {
            scanRateString = numberFormatter.format(scanRateSecs / 3600.0) + " hour";
        } else {
            if (scanRateSecs == 86400) {
                scanRateString = "Daily";
            } else if (scanRateSecs == 604800) {
                scanRateString = "Weekly";
            } else if (scanRateSecs == 2592000) {
                scanRateString = "Monthly";
            } else {
                scanRateString = numberFormatter.format(scanRateSecs / 86400.0) + " day";
            }
        }

        for (int i = 0; i < comboBox.getModel().getSize(); i++) {
            if (comboBox.getItemAt(i).indexOf(scanRateString) != -1) {
                comboBox.setSelectedIndex(i);
                found = true;
                break;
            }
        }

        if (!found) {
            comboBox.addItem(scanRateString);
            comboBox.setSelectedItem(scanRateString);
        }
    }

    // this is mainly for gear refresh rates
    public static final void setIntervalComboBoxSelectedItem(JComboBox<String> comboBox, JComboBox<String> comboBox2,
            double scanRateSecs) {
        String scanRateString = null;
        String scanRateUnitString = null;

        // when we divide the scanRateSecs value, we must use a double formatted number
        // so we are returned a double value (Ex: numberFormatter.format(scanRateSecs/60.0)

        DecimalFormat numberFormatter = new DecimalFormat();
        if (scanRateSecs < 3600) {
            scanRateString = numberFormatter.format(scanRateSecs / 60.0);
            scanRateUnitString = "minutes";
        } else {
            scanRateString = numberFormatter.format(scanRateSecs / 3600.0);
            if (scanRateString.indexOf(".") != -1) {
                scanRateString = numberFormatter.format(scanRateSecs / 60.0);
                scanRateUnitString = "minutes";
            } else {
                scanRateUnitString = "hours";
            }
        }

        comboBox.setSelectedItem(scanRateString);
        comboBox2.setSelectedItem(scanRateUnitString);
    }

    public final static <T> void setSelectedInComboBox(JComboBox<T> comboBox, T val) {
        int items = comboBox.getItemCount();
        boolean foundIt = false;

        for (int i = 0; i < items; i++) {
            Object item = comboBox.getItemAt(i);

            if (item.equals(val)) {
                comboBox.setSelectedIndex(i);
                foundIt = true;
                break;
            }
        }

        // Add it if we didn't find it
        if (foundIt == false) {
            comboBox.addItem(val);
            comboBox.setSelectedItem(val);
        }
    }

    /**
     * This method will return the Dialog associated with a component
     * If no parent dialog is found null will be returned
     */
    public final static Dialog getParentDialog(Component comp) {
        while (comp != null && !(comp instanceof Dialog)) {
            comp = comp.getParent();
        }

        return (Dialog) comp;
    }

    /**
     * This method will return the Frame associated with a component
     * If no parent frame is found null will be returned
     */
    public final static Frame getParentFrame(Component comp) {
        while (comp != null && !(comp instanceof Frame)) {
            comp = comp.getParent();
        }

        return (Frame) comp;
    }

    public final static JInternalFrame getParentInternalFrame(Component comp) {
        while (comp != null && !(comp instanceof JInternalFrame)) {
            comp = comp.getParent();
        }

        return (JInternalFrame) comp;
    }

    public static void setLaF() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace(); /* Not much to do about */
        }
    }

    private static boolean findPath(Stack<DefaultMutableTreeNode> s, Object o) {
        DefaultMutableTreeNode node = s.peek();

        if (node.getUserObject().equals(o)) {
            return true;
        }

        for (int i = 0; i < node.getChildCount(); i++) {
            s.push((DefaultMutableTreeNode) node.getChildAt(i));

            if (findPath(s, o)) {
                return true;
            }

            s.pop();
        }

        return false;
    }

    public final static TreePath getTreePath(JTree tree, Object o) {
        DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) tree.getModel().getRoot();

        Stack<DefaultMutableTreeNode> s = new Stack<>();
        s.push(rootNode);

        if (findPath(s, o)) {
            List<DefaultMutableTreeNode> tmpForArray = new ArrayList<>();

            while (!s.isEmpty()) {
                tmpForArray.add(0, s.pop());
            }

            DefaultMutableTreeNode[] path = tmpForArray.toArray(new DefaultMutableTreeNode[tmpForArray.size()]);

            return new TreePath(path);
        }

        return null;
    }
}
