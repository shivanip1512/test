package com.cannontech.common.gui.util;

/*
 * Pulled straight from:
 * http://forum.java.sun.com/thread.jsp?forum=57&thread=294121&message=1159947
 * 
 */
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/* A Panel that can be blocked. <br> 
 * Just set an instance of this class as the glassPane 
 * of your JFrame an call <code>block()</code> as needed. 
 **/
public class BlockingGlassPane extends JPanel {
	private int blockCount = 0;
	private BlockMouse blockMouse = new BlockMouse();
	private BlockKeys blockKeys = new BlockKeys(); 
	
	/*   
	 * Constructor.   
	 **/
	public BlockingGlassPane() {
		setVisible(false);
		setOpaque(false);
		addMouseListener(blockMouse);
	} 
	
	/*   
	 *  Start or end blocking.      
	 *  @param block   should blocking be started or ended  
	 **/
	public void block(boolean block) {
		if (block) {
			if (blockCount == 0) {
				setVisible(true);
				setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				FocusManager.getCurrentManager().addKeyEventDispatcher(
					blockKeys);
			}
			blockCount++;
		} else {
			blockCount--;
			if (blockCount == 0) {
				FocusManager.getCurrentManager().removeKeyEventDispatcher(
					blockKeys);
				setCursor(Cursor.getDefaultCursor());
				setVisible(false);
			}
		}
	} 
	/*   
	 * Test if this glasspane is blocked.    
	 *  @return    <code>true</code> if currently blocked   
	 **/
	public boolean isBlocked() {
		return blockCount > 0;
	} /**   * The key dispatcher to block the keys.   */
	private class BlockKeys implements KeyEventDispatcher {
		public boolean dispatchKeyEvent(KeyEvent ev) {
			Component source = ev.getComponent();
			if (source != null
				&& SwingUtilities.isDescendingFrom(source, getParent())) {
				Toolkit.getDefaultToolkit().beep();
				ev.consume();
				return true;
			}
			return false;
		}
	} 
	/*
	 * The mouse listener used to block the mouse.   
	 **/
	private class BlockMouse extends MouseAdapter {
		public void mouseClicked(MouseEvent ev) {
			Toolkit.getDefaultToolkit().beep();
		}
	}
}