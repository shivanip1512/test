package com.cannontech.common.gui.util;

import java.awt.Component;

import javax.swing.*;
import javax.swing.table.*;

public class JComponentCellRenderer implements TableCellRenderer
	{
			public Component getTableCellRendererComponent(JTable table, Object value,
					boolean isSelected, boolean hasFocus, int row, int column) {
			return (JComponent)value;
		}
}
