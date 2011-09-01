/*
 * Created on Jun 7, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cannontech.fdemulator.protocols;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import com.cannontech.fdemulator.common.FDEProtocol;
import com.cannontech.fdemulator.common.FDTestPanelNotifier;

/**
 * @author ASolberg
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class RdexManual extends JFrame implements ActionListener, Runnable
{
	private FDTestPanelNotifier notifier;
	private RdexProtocol proto;
	private JButton submittButton = new JButton("Submit");
	private JButton dismissButton = new JButton("Dismiss");
	private JTextField name = new JTextField("Input 1");
	private JComboBox type = new JComboBox(new String[] { "Value", "Status", "Control" });
	private JTextField value = new JTextField("11.11");
	private JComboBox quality = new JComboBox(new String[] { "Normal", "Manual", "Questionable" });
	private JTextField count = new JTextField("1");
	private JLabel nameLabel = new JLabel("Translation:");
	private JLabel typeLabel = new JLabel("Type:");
	private JLabel valueLabel = new JLabel("Value:");
	private JLabel qualityLabel = new JLabel("Quality:");
	private JLabel countLabel = new JLabel("Count:");
	private JRadioButton open = new JRadioButton("Open");
	private JRadioButton close = new JRadioButton("Close");
	private ButtonGroup group = new ButtonGroup();

	private Thread manualThread = null;

	public RdexManual(FDEProtocol protocol)
	{
		super("RDEX Manual Commands");
		proto = (RdexProtocol) protocol;
		try
		{
			init();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void init()
	{
		submittButton.setBounds(new Rectangle(80, 160, 70, 25));
		dismissButton.setBounds(new Rectangle(160, 160, 80, 25));
		nameLabel.setBounds(new Rectangle(10, 10, 90, 20));
		typeLabel.setBounds(new Rectangle(10, 40, 90, 20));
		valueLabel.setBounds(new Rectangle(10, 70, 90, 20));
		qualityLabel.setBounds(new Rectangle(10, 100, 90, 20));
		countLabel.setBounds(new Rectangle(10, 130, 90, 20));
		name.setBounds(new Rectangle(110, 10, 170, 20));
		type.setBounds(new Rectangle(110, 40, 170, 20));
		value.setBounds(new Rectangle(110, 70, 40, 20));
		open.setBounds(new Rectangle(160, 70, 60, 20));
		close.setBounds(new Rectangle(230, 70, 60, 20));
		quality.setBounds(new Rectangle(110, 100, 170, 20));
		count.setBounds(new Rectangle(110, 130, 40, 20));
		this.getContentPane().setLayout(null);
		this.setSize(300, 230);
		this.getContentPane().add(submittButton, null);
		this.getContentPane().add(dismissButton, null);
		this.getContentPane().add(name, null);
		this.getContentPane().add(type, null);
		this.getContentPane().add(value, null);
		this.getContentPane().add(open, null);
		this.getContentPane().add(close, null);
		this.getContentPane().add(quality, null);
		this.getContentPane().add(count, null);
		this.getContentPane().add(nameLabel, null);
		this.getContentPane().add(typeLabel, null);
		this.getContentPane().add(valueLabel, null);
		this.getContentPane().add(qualityLabel, null);
		this.getContentPane().add(countLabel, null);

		group.add(open);
		group.add(close);
		open.setEnabled(false);
		close.setEnabled(false);
		open.setVisible(false);
		close.setVisible(false);

		open.addActionListener(this);
		close.addActionListener(this);
		type.addActionListener(this);
		submittButton.addActionListener(this);
		dismissButton.addActionListener(this);
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == submittButton)
		{
			submitButton_actionPerformed(e);
		} else if (e.getSource() == dismissButton)
		{
			dismissButton_actionPerformed(e);
		} else if (e.getSource() == type)
		{
			if (type.getSelectedItem().equals("Status") || type.getSelectedItem().equals("Control"))
			{
				open.setEnabled(true);
				close.setEnabled(true);
				open.setVisible(true);
				close.setVisible(true);
				value.setText("0");
				group.setSelected(open.getModel(), true);
			} else if (type.getSelectedItem().equals("Value"))
			{
				open.setEnabled(false);
				close.setEnabled(false);
				open.setVisible(false);
				close.setVisible(false);
			}
		} else if (e.getSource() == open)
		{
			value.setText("0");
		} else if (e.getSource() == close)
		{
			value.setText("1");
		}
	}

	public void submitButton_actionPerformed(ActionEvent e)
	{
		manualThread = new Thread(this, "manual");
		manualThread.start();
	}

	public void dismissButton_actionPerformed(ActionEvent e)
	{
		setVisible(false);
		dispose();
	}

	public void run()
	{
		try
		{
			int sendcount = Integer.parseInt(count.getText());
			for (int i = 0; i < sendcount; i++)
			{
				synchronized (this)
				{
					String qual = "0";
					if (quality.getSelectedItem().equals("Normal"))
					{
						qual = "1";
					} else if (quality.getSelectedItem().equals("Manual"))
					{
						qual = "2";
					} else
						qual = "4";
					proto.sendManual(type.getSelectedItem().toString(), name.getText(), value.getText(), qual, Integer.parseInt(count.getText()));
				}
			}
		} catch (Exception e)
		{
			System.out.println("Error in run method while sending manual");
		}
	}
}