package com.cannontech.common.gui.util;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Ellipse2D;
import java.awt.Shape;
import java.awt.FlowLayout;
/**
 * @author snebben
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class RoundButton extends JButton
{
	/**
	 * Constructor for RoundButton.
	 */
	public RoundButton()
	{
		super();
	}

	/**
	 * Constructor for RoundButton.
	 * @param icon
	 */
	public RoundButton(Icon icon)
	{
		super(icon);
	}

	/**
	 * Constructor for RoundButton.
	 * @param text
	 */
	public RoundButton(String text)
	{
		super(text);
	     // These statements enlarge the button so that it
	     // becomes a circle rather than an oval.
	     Dimension size = getPreferredSize();
	     size.width = size.height = Math.max(size.width,
	         size.height);
	     setPreferredSize(size);
	
	     // This call causes the JButton not to paint
	         // the background.
	     // This allows us to paint a round background.
	     setContentAreaFilled(false);
		
	}

	/**
	 * Constructor for RoundButton.
	 * @param a
	 */
	public RoundButton(Action a)
	{
		super(a);
	}

	/**
	 * Constructor for RoundButton.
	 * @param text
	 * @param icon
	 */
	public RoundButton(String text, Icon icon)
	{
		super(text, icon);
	}

 
	// Paint the round background and label.
	protected void paintComponent(Graphics g)
	{
//		if (getModel().isArmed())
//		{
//			// You might want to make the highlight color
//			// a property of the RoundButton class.
//			g.setColor(Color.magenta);
//		}
//		else
//		{
			g.setColor(getBackground());
//		}
		
		g.fillOval(0, 0, getSize().width-1, getSize().height-1);
		
		// This call will paint the label and the focus rectangle.
		super.paintComponent(g);
	}

	// Paint the border of the button using a simple stroke.
	protected void paintBorder(Graphics g)
	{
		g.setColor(getForeground());
		g.drawOval(0, 0, getSize().width-1, getSize().height-1);
	}

	// Hit detection.
	Shape shape;
	public boolean contains(int x, int y)
	{
		// If the button has changed size, make a new shape object.
		if (shape == null || !shape.getBounds().equals(getBounds()))
		{
			shape = new Ellipse2D.Float(0, 0, getWidth(), getHeight());
		}
		return shape.contains(x, y);
	}

	// Test routine.
	public static void main(String[] args)
	{
		// Create a button with the label "Jackpot".
		JButton button = new RoundButton("Jackpot");
		button.setBackground(Color.green);

		// Create a frame in which to show the button.
		javax.swing.JFrame frame = new javax.swing.JFrame();
		frame.getContentPane().setBackground(Color.yellow);

		frame.getContentPane().add(button);
		frame.getContentPane().setLayout(new FlowLayout());

		frame.setSize(150, 150);
		frame.setVisible(true);
	}
}
