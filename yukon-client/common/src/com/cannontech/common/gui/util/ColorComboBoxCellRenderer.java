package com.cannontech.common.gui.util;

import com.cannontech.common.YukonColorPallet;

/**
 * Insert the type's description here.
 * Creation date: (1/19/00 1:38:38 PM)
 * @author: 
 */
public class ColorComboBoxCellRenderer extends javax.swing.JPanel implements javax.swing.ListCellRenderer {
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
 * Insert the method's description here.
 * Creation date: (1/19/00 3:11:02 PM)
 * @return java.awt.Color
 * @param colorString java.lang.String
 */
    private java.awt.Color getColorFromString(String colorString) {
        java.awt.Color stringColor = null;
        switch (colorString) {
        case Colors.GREEN_STR_ID:
            stringColor = YukonColorPallet.GREEN.getAwtColor();
            break;
        case Colors.RED_STR_ID:
            stringColor = YukonColorPallet.WINE.getAwtColor();
            break;
        case Colors.WHITE_STR_ID:
            stringColor = YukonColorPallet.WHITE.getAwtColor();
            break;
        case Colors.YELLOW_STR_ID:
            stringColor = YukonColorPallet.YELLOW.getAwtColor();
            break;
        case Colors.BLUE_STR_ID:
            stringColor = YukonColorPallet.BLUE.getAwtColor();
            break;
        case Colors.TEAL_STR_ID:
            stringColor = YukonColorPallet.TEAL.getAwtColor();
            break;
        case Colors.BLACK_STR_ID:
            stringColor = YukonColorPallet.BLACK.getAwtColor();
            break;
        case Colors.ORANGE_STR_ID:
            stringColor = YukonColorPallet.ORANGE.getAwtColor();
            break;
        case Colors.LIGHT_GREEN_STR_ID:
            stringColor = YukonColorPallet.SAGE.getAwtColor();
            break;
        case Colors.GRAY_STR_ID:
            stringColor = YukonColorPallet.GRAY.getAwtColor();
            break;
        case Colors.PURPLE_STR_ID:
            stringColor = YukonColorPallet.PURPLE.getAwtColor();
            break;
        case Colors.SKY_STR_ID:
            stringColor = YukonColorPallet.SKY.getAwtColor();
            break;
        default:
            break;
        }
        return stringColor;
    }
/**
 * getListCellRendererComponent method comment.
 */
public java.awt.Component getListCellRendererComponent(javax.swing.JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
	iconColor = (getColorFromString((String)value));
	boxString = (String)value;
	return this;
}
/**
 * Insert the method's description here.
 * Creation date: (1/19/00 2:29:21 PM)
 * @param g java.awt.Graphics
 */
public void paint(java.awt.Graphics g) 
{
   //color
	g.setColor(iconColor);
	g.fillRect(0,0,((int)(.15*getWidth())), getHeight() - 1 );
	g.setColor(java.awt.Color.black);
	g.drawRect(0,0,(int)(.15*getWidth()),getHeight() - 1);
   
   //text
	g.drawString( boxString,
         ((int)(.15*getWidth())+5),
         ((int)(.5*getHeight() + .25*g.getFontMetrics().getHeight())));
}
}
