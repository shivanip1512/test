/*
 * Created on Jun 8, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cannontech.fdemulator.protocols;

import java.awt.Rectangle;
import java.awt.event.*;
import java.io.RandomAccessFile;
import java.util.Observer;

import javax.swing.*;

import com.cannontech.fdemulator.common.FDTestPanelNotifier;

/**
 * @author ASolberg
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ACSSettings extends JDialog implements ActionListener
{
	private FDTestPanelNotifier notifier;
	private ACSProtocol proto;

	private JButton okButton = new JButton("OK");
	private JButton cancelButton = new JButton("Cancel");
	private JTextField pathField = new JTextField();
	private JTextField trafficField = new JTextField();
	private JLabel pathLabel = new JLabel("Point file path:");
	private JLabel trafficLabel = new JLabel("Traffic log path:");
	private JTextField serverField = new JTextField();
	private JLabel serverLabel = new JLabel("Server Name or IP:");
	private JTextField serverBackup1 = new JTextField();
	private JLabel backup1 = new JLabel("Backup Server 1:");
	private JTextField serverBackup2 = new JTextField();
	private JLabel backup2 = new JLabel("Backup Server 2:");
	private JTextField serverBackup3 = new JTextField();
	private JLabel backup3 = new JLabel("Backup Server 3:");
	private JTextField portField = new JTextField();
	private JLabel portLabel = new JLabel("Port:");
	private JLabel defaultServer = new JLabel("127.0.0.1");
	private JLabel defaultBack1 = new JLabel("127.0.0.1");
	private JLabel defaultBack2 = new JLabel("127.0.0.1");
	private JLabel defaultBack3 = new JLabel("127.0.0.1");
	private JLabel defaultPointPath = new JLabel("resource/acs_points.cfg");
	private JLabel defaultTrafficPath = new JLabel("resource/acs_traffic_log.txt");
	private JLabel defaultPort = new JLabel("1668");
	private String name = null;
	private String back1 = null;
	private String back2 = null;
	private String back3 = null;
	private String path = null;
	private String trafficPath = null;
	private String port = null;
	private boolean set = false;
	ImageIcon fde = new ImageIcon("resource/fdeicon.gif");
	private RandomAccessFile settingsFile;

	public ACSSettings(String title, ACSProtocol protocol)
	{
		super(protocol.getTestPanel().getWindow(), title, true);
        
		proto = protocol;
		try
		{
			init();
			loadLastSettings();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void loadLastSettings()
	{

		// start at beginning of file
		try
		{
			settingsFile = new RandomAccessFile("resource/settings.cfg", "rw");
			settingsFile.seek(0);
		} catch (Exception e)
		{
			e.printStackTrace(System.out);
		}

		int exit = 0;

		String var = "";
		while (exit != 1)
		{
			try
			{
				var = settingsFile.readLine();
				if (var.equalsIgnoreCase("ACS"))
				{
					String primaryserver = settingsFile.readLine().trim();
					String backup1 = settingsFile.readLine().trim();
					String backup2 = settingsFile.readLine().trim();
					String backup3 = settingsFile.readLine().trim();
					String pointpath = settingsFile.readLine().trim();
					String trafficpath = settingsFile.readLine().trim();
					String port = settingsFile.readLine().trim();
					serverField.setText(primaryserver);
					serverBackup1.setText(backup1);
					serverBackup2.setText(backup2);
					serverBackup3.setText(backup3);
					pathField.setText(pointpath);
					trafficField.setText(trafficpath);
					portField.setText(port);
					exit = 1;
				}
			} catch (Exception e)
			{
				e.printStackTrace(System.out);
				exit = 1;
			}

		}
	}

	public void saveSettings()
	{
		// start at beginning of file
		try
		{
			settingsFile = new RandomAccessFile("resource/settings.cfg", "rw");
			settingsFile.seek(0);
		} catch (Exception e)
		{
			e.printStackTrace(System.out);
		}

		String var = "";
		String primaryserver = serverField.getText();
		String backup1 = serverBackup1.getText();
		String backup2 = serverBackup2.getText();
		String backup3 = serverBackup3.getText();
		String pointpath = pathField.getText();
		String trafficpath = trafficField.getText();
		String port = portField.getText();
		int exit = 0;
		while (exit != 1)
		{
			try
			{
				var = settingsFile.readLine();
				if (var.equalsIgnoreCase("ACS"))
				{

					settingsFile.writeBytes(primaryserver);
					for (int i = 0; i < 15 - primaryserver.length(); i++)
					{
						settingsFile.writeBytes(" ");
					}
					settingsFile.writeBytes("\n");

					settingsFile.writeBytes(backup1);
					for (int i = 0; i < 15 - backup1.length(); i++)
					{
						settingsFile.writeBytes(" ");
					}
					settingsFile.writeBytes("\n");

					settingsFile.writeBytes(backup2);
					for (int i = 0; i < 15 - backup2.length(); i++)
					{
						settingsFile.writeBytes(" ");
					}
					settingsFile.writeBytes("\n");

					settingsFile.writeBytes(backup3);
					for (int i = 0; i < 15 - backup3.length(); i++)
					{
						settingsFile.writeBytes(" ");
					}
					settingsFile.writeBytes("\n");

					settingsFile.writeBytes(pointpath);
					for (int i = 0; i < 100 - pointpath.length(); i++)
					{
						settingsFile.writeBytes(" ");
					}
					settingsFile.writeBytes("\n");

					settingsFile.writeBytes(trafficpath);
					for (int i = 0; i < 100 - trafficpath.length(); i++)
					{
						settingsFile.writeBytes(" ");
					}
					settingsFile.writeBytes("\n");

					settingsFile.writeBytes(port);
					for (int i = 0; i < 5 - port.length(); i++)
					{
						settingsFile.writeBytes(" ");
					}
					settingsFile.writeBytes("\n");

					exit = 1;
				}
			} catch (Exception e)
			{
				e.printStackTrace(System.out);
				exit = 1;
			}
		}
	}

	public void init() throws Exception
	{
		notifier = new FDTestPanelNotifier(proto.getTestPanel());

		this.getContentPane().setLayout(null);
		this.setSize(450, 300);

		okButton.setBounds(new Rectangle(140, 235, 50, 25));
		cancelButton.setBounds(new Rectangle(200, 235, 80, 25));
		serverLabel.setBounds(new Rectangle(10, 30, 90, 20));
		backup1.setBounds(new Rectangle(10, 60, 90, 20));
		backup2.setBounds(new Rectangle(10, 90, 90, 20));
		backup3.setBounds(new Rectangle(10, 120, 90, 20));
		pathLabel.setBounds(new Rectangle(10, 150, 90, 20));
		trafficLabel.setBounds(new Rectangle(10, 180, 90, 20));
		portLabel.setBounds(new Rectangle(10, 210, 90, 20));
		serverField.setBounds(new Rectangle(110, 30, 170, 20));
		defaultServer.setBounds(new Rectangle(290, 30, 90, 20));
		serverBackup1.setBounds(new Rectangle(110, 60, 170, 20));
		defaultBack1.setBounds(new Rectangle(290, 60, 90, 20));
		serverBackup2.setBounds(new Rectangle(110, 90, 170, 20));
		defaultBack2.setBounds(new Rectangle(290, 90, 90, 20));
		serverBackup3.setBounds(new Rectangle(110, 120, 170, 20));
		defaultBack3.setBounds(new Rectangle(290, 120, 90, 20));
		pathField.setBounds(new Rectangle(110, 150, 170, 20));
		defaultPointPath.setBounds(new Rectangle(290, 150, 140, 20));
		trafficField.setBounds(new Rectangle(110, 180, 170, 20));
		defaultTrafficPath.setBounds(new Rectangle(290, 180, 140, 20));
		portField.setBounds(new Rectangle(110, 210, 170, 20));
		defaultPort.setBounds(new Rectangle(290, 210, 90, 20));
		this.getContentPane().add(okButton, null);
		this.getContentPane().add(cancelButton, null);
		this.getContentPane().add(serverLabel, null);
		this.getContentPane().add(backup1, null);
		this.getContentPane().add(backup2, null);
		this.getContentPane().add(backup3, null);
		this.getContentPane().add(pathLabel, null);
		this.getContentPane().add(portLabel, null);
		this.getContentPane().add(trafficLabel, null);
		this.getContentPane().add(serverField, null);
		this.getContentPane().add(serverBackup1, null);
		this.getContentPane().add(serverBackup2, null);
		this.getContentPane().add(serverBackup3, null);
		this.getContentPane().add(pathField, null);
		this.getContentPane().add(portField, null);
		this.getContentPane().add(trafficField, null);
		this.getContentPane().add(defaultServer, null);
		this.getContentPane().add(defaultBack1, null);
		this.getContentPane().add(defaultBack2, null);
		this.getContentPane().add(defaultBack3, null);
		this.getContentPane().add(defaultPointPath, null);
		this.getContentPane().add(defaultTrafficPath, null);
		this.getContentPane().add(defaultPort, null);
		okButton.addActionListener(this);
		cancelButton.addActionListener(this);
		//this.setIconImage(fde.getImage());
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == okButton)
		{
			okButton_actionPerformed(e);
		} else if (e.getSource() == cancelButton)
		{
			cancelButton_actionPerformed(e);
		}
	}

	public void okButton_actionPerformed(ActionEvent e)
	{
		name = serverField.getText();
		back1 = serverBackup1.getText();
		back2 = serverBackup2.getText();
		back3 = serverBackup3.getText();
		path = pathField.getText();
		trafficPath = trafficField.getText();
		port = portField.getText();
		set = true;
		saveSettings();
		setVisible(false);
		//dispose();
	}

	public void cancelButton_actionPerformed(ActionEvent e)
	{
		notifier.setActionCode(FDTestPanelNotifier.ACTION_STOP_TEST);
		setVisible(false);
		//dispose();
	}

	protected void processWindowEvent(WindowEvent e)
	{
		super.processWindowEvent(e);
		if (e.getID() == WindowEvent.WINDOW_CLOSING)
		{
			cancelButton_actionPerformed(null);
		}
	}

	// Method to make sure settings are done b4 starting
	public boolean isSet()
	{
		return set;
	}

	public String getServer()
	{
		return name;
	}

	public String getBack1()
	{
		return back1;
	}

	public String getBack2()
	{
		return back2;
	}

	public String getBack3()
	{
		return back3;
	}

	public String getPath()
	{
		return path;
	}

	public String getTrafficPath()
	{
		return trafficPath;
	}

	public String getPort()
	{
		return port;
	}

	public void listenForActions(Observer o)
	{
		notifier.addObserver(o);
	}
}