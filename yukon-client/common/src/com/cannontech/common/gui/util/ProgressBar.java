package com.cannontech.common.gui.util;

/**
	ProgressBar
*/
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

public class ProgressBar extends JDialog implements Runnable
{
	protected JLabel m_Label;
	protected JProgressBar m_Progress;
	protected JButton m_BtCancel;
	protected boolean m_bIsAborted = false;

	class MyWindowListener extends WindowAdapter
	{
		public void windowClosing(WindowEvent e) 
			{System.exit(0);}
	}
	private long timeToRun;
	private long increment;
	public ProgressBar()
	{
		super();
		setSize(300, 100);

		JPanel p = new JPanel();
		p.setBorder(new	EtchedBorder() );

		//This is half-assed
		m_Label = new JLabel("                                                   ");
		p.add(m_Label);

		JPanel p1 = new JPanel();
		m_Progress = new JProgressBar();
		p1.add(m_Progress);

		m_BtCancel = new JButton("Cancel");
		ActionListener al = new ActionListener() 
		{ 
			public void actionPerformed(ActionEvent e) 
				{ m_bIsAborted = true; }
		};
		m_BtCancel.addActionListener(al);
		p1.add(m_BtCancel);
		p.add(p1);

		getContentPane().add("Center", p);
		addWindowListener(new MyWindowListener());
		setVisible(true);
	}
	public ProgressBar(String message)
	{
		super();
		setSize(300, 100);

		JPanel p = new JPanel();
		p.setBorder(new	EtchedBorder() );

		//This is half-assed
		m_Label = new JLabel(message);
		m_Label.setFont(new java.awt.Font("dialog", 0, 14));
		p.add(m_Label);

		JPanel p1 = new JPanel();
		m_Progress = new JProgressBar();
		p1.add(m_Progress);

		m_BtCancel = new JButton("Cancel");
		m_BtCancel.setFont(new java.awt.Font("dialog", 0, 14));
		
		ActionListener al = new ActionListener() 
		{ 
			public void actionPerformed(ActionEvent e) 
				{ m_bIsAborted = true; }
		};
		m_BtCancel.addActionListener(al);
		p1.add(m_BtCancel);
		p.add(p1);

		getContentPane().add("Center", p);
		addWindowListener(new MyWindowListener());
		setVisible(true);
	}
/**
 * This method was created in VisualAge.
 * @return long
 */
protected long getIncrement() {
	return increment;
}
/**
 * This method was created in VisualAge.
 * @return long
 */
protected long getTimeToRun() {
	return timeToRun;
}
	public boolean isAborted()
	{
		return m_bIsAborted;
	}
	public static void main(String argv[]) 
	{
		showDumbProgressBar("Adding Port to the Database....", 2000, 250 );
		
		
		ProgressBar pBar = new ProgressBar();
		pBar.setMessage("Adding Port to the Database....");
		pBar.setMaximum(100);

		for(int k=0; k<=100; k++)
		{
			pBar.setValue(k);
			if (pBar.isAborted())
				break;
			long t = System.currentTimeMillis()+200;
			while (System.currentTimeMillis() < t);

		}
		System.exit(0);
	}
/**
 * This method was created in VisualAge.
 */
public void run() {
	
	setMaximum( (int)getTimeToRun());

	long baseLineMillis = System.currentTimeMillis();
	long currentMillis = baseLineMillis;
	
	try
	{
	
	while( currentMillis <= ( baseLineMillis + getTimeToRun() ) )
	{
		setValue( (int)(currentMillis - baseLineMillis ));
		
		while( System.currentTimeMillis() < ( currentMillis+increment))
			if( isAborted() )
			{
				dispose();
				return;
			}
			else
				Thread.sleep(200);
			
				
		currentMillis += increment;
	}
	
	}
	catch( InterruptedException ie ){}
	
	dispose();
}
/**
 * This method was created in VisualAge.
 * @param newValue long
 */
protected void setIncrement(long newValue) {
	this.increment = newValue;
}
	public void setMaximum(int n)
	{
		m_Progress.setMaximum(n);
	}
	public void setMessage(String str)
	{
		m_Label.setText(str);
	}
	public void setMinimum(int n)
	{
		m_Progress.setMinimum(n);
	}
/**
 * This method was created in VisualAge.
 * @param newValue long
 */
protected void setTimeToRun(long newValue) {
	this.timeToRun = newValue;
}
	public void setValue(int n)
	{
		n = Math.max(n, m_Progress.getMinimum());
		n = Math.min(n, m_Progress.getMaximum());
		m_Progress.setValue(n);
	}
/**
 * This method was created in VisualAge.
 * @param message java.lang.String
 * @param milliseconds long
 */
public static void showDumbProgressBar( String message, int milliseconds, int increment) 
{	
	ProgressBar pBar = new ProgressBar(message);

	pBar.setTimeToRun(milliseconds);
	pBar.setIncrement(increment);
	
	Thread runner = new Thread(pBar);
	runner.start();	
}
}
