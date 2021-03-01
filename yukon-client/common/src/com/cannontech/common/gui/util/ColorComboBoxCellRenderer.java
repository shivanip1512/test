package com.cannontech.common.gui.util;

import java.awt.Component;

import javax.swing.JList;

import com.cannontech.common.YukonColorPalette;

public class ColorComboBoxCellRenderer extends javax.swing.JPanel implements javax.swing.ListCellRenderer<YukonColorPalette> {
    private java.awt.Color iconColor = null;
    private String boxString = null;

    /**
     * ForegroundColorCellRenderer constructor comment.
     */
    public ColorComboBoxCellRenderer() {
        super();
        setOpaque(true);
        setPreferredSize(new java.awt.Dimension(100, 18));
        setMinimumSize(new java.awt.Dimension(100, 18));
    }

    /**
     * getListCellRendererComponent method comment.
     */
    public Component getListCellRendererComponent(JList<? extends YukonColorPalette> list, YukonColorPalette value, int index,
            boolean isSelected, boolean cellHasFocus) {
        YukonColorPalette color = ((YukonColorPalette) value);
        iconColor = color.getAwtColor();
        boxString = color.toDefaultText();
        return this;
    }

    public void paint(java.awt.Graphics g) {
        // color
        g.setColor(iconColor);
        g.fillRect(0, 0, ((int) (.15 * getWidth())), getHeight() - 1);
        g.setColor(java.awt.Color.black);
        g.drawRect(0, 0, (int) (.15 * getWidth()), getHeight() - 1);

        // text
        g.drawString(boxString,
                     ((int) (.15 * getWidth()) + 5),
                     ((int) (.5 * getHeight() + .25 * g.getFontMetrics().getHeight())));
    }
}
