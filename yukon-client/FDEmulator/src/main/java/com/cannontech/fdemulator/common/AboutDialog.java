/*
 * Created on May 19, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cannontech.fdemulator.common;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.border.EtchedBorder;

public class AboutDialog extends JDialog implements ActionListener
{

	private JLabel companyNamejLabel = new JLabel();
	private JLabel fDEjLabel = new JLabel();
	private JLabel copyrightjLabel = new JLabel();
	private JLabel versionjLabel = new JLabel();
	private JButton okButton = new JButton();
	ImageIcon cannon = new ImageIcon("resource/cannonlogo_small.gif");
	private JLabel logo = new JLabel(cannon);

	/** 
	 * constructs the AboutDialog box
	 * 
	 * @param JFrame of the owner window
	 */

	public AboutDialog(JFrame owner)
	{
		super(owner, "About", true);
		try
		{
			jbInit();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/** 
	 * common Initialization method
	 */

	private void jbInit() throws Exception
	{

		this.getContentPane().setLayout(null);
		this.setSize(250, 210);
		this.setLocation(getParent().getLocationOnScreen().x + 30, getParent().getLocationOnScreen().y + 30);

		companyNamejLabel.setText("Cannon Technologies");
		companyNamejLabel.setBounds(new Rectangle(10, 40, 134, 17));
		fDEjLabel.setText("Foreign Data Emulator");
		fDEjLabel.setBounds(new Rectangle(10, 65, 154, 17));
		copyrightjLabel.setText("Copyright 2004 Cannon Technologies");
		copyrightjLabel.setBounds(new Rectangle(10, 90, 218, 17));
		versionjLabel.setText("Version 1.1");
		versionjLabel.setBounds(new Rectangle(10, 115, 67, 17));
		okButton.setBounds(new Rectangle(80, 145, 79, 27));
		okButton.setText("OK");
		okButton.addActionListener(this);

		logo.setBounds(5, 5, 125, 30);
		logo.setBorder(new EtchedBorder(EtchedBorder.RAISED));
		this.getContentPane().add(logo, null);
		this.getContentPane().add(companyNamejLabel, null);
		this.getContentPane().add(fDEjLabel, null);
		this.getContentPane().add(copyrightjLabel, null);
		this.getContentPane().add(versionjLabel, null);
		this.getContentPane().add(okButton, null);

		this.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				actionPerformed(null);
			}
		});
	}

	/**
	 * Standard Event handler method
	 * 
	 * @param ActionEvent
	 */
	public void actionPerformed(ActionEvent e)
	{
		setVisible(false);
		dispose();
	}

}
