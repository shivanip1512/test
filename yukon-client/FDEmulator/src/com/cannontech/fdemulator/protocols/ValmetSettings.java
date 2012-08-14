/*
 * Created on Jun 16, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cannontech.fdemulator.protocols;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.RandomAccessFile;
import java.util.Observer;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import com.cannontech.fdemulator.common.FDTestPanelNotifier;

/**
 * @author ASolberg
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ValmetSettings extends JFrame implements ActionListener
{
	private FDTestPanelNotifier notifier;
	private ValmetProtocol proto;

	private JButton okButton = new JButton("OK");
	private JButton cancelButton = new JButton("Cancel");
	private JTextField pathField = new JTextField();
	private JTextField trafficField = new JTextField();
	private JLabel pathLabel = new JLabel("Point file path:");
	private JLabel trafficLabel = new JLabel("Traffic log path:");
	private JTextField serverField = new JTextField();
	private JLabel serverLabel = new JLabel("Server Name or IP:");
	private JTextField portField = new JTextField();
    private JLabel portLabel = new JLabel("Port:");
	private JLabel defaultServer = new JLabel("127.0.0.1");
	private JLabel defaultPointPath = new JLabel("resource/valmet_points.cfg");
	private JLabel defaultTrafficPath = new JLabel("resource/valmet_traffic_log.txt");
    private JLabel defaultPort = new JLabel("1666");
    private JLabel defaultExtendedName = new JLabel("Use Extended Name:");

    private JCheckBox extendedNameCheckBox = new JCheckBox("ExtendedName", false);
	private String name = null;
	private String path = null;
	private String trafficPath = null;
	private String outPort = null;
	private boolean extendedName = false;
	private boolean set = false;
	ImageIcon fde = new ImageIcon("resource/fdeicon.gif");
	private RandomAccessFile settingsFile;

	public ValmetSettings(String title, ValmetProtocol protocol)
	{
		super("Valmet Settings");
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
				if (var.equalsIgnoreCase("VALMET"))
				{
					String primaryserver = settingsFile.readLine().trim();
					String pointpath = settingsFile.readLine().trim();
					String trafficpath = settingsFile.readLine().trim();
					String port = settingsFile.readLine().trim();
	                String extendedNameSetting = settingsFile.readLine().trim();

					serverField.setText(primaryserver);
					pathField.setText(pointpath);
					trafficField.setText(trafficpath);
					portField.setText(port);
					extendedNameCheckBox.setSelected(extendedNameSetting.equalsIgnoreCase("Y"));
					
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
		String pointpath = pathField.getText();
		String trafficpath = trafficField.getText();
		String port = portField.getText();
		int exit = 0;
		while (exit != 1)
		{
			try
			{
				var = settingsFile.readLine();
				if (var.equalsIgnoreCase("VALMET"))
				{

					settingsFile.writeBytes(primaryserver);
					for (int i = 0; i < 15 - primaryserver.length(); i++)
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
					
					settingsFile.writeBytes(extendedNameCheckBox.isSelected() ? "Y": "N");
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
		this.setSize(450, 400);

		okButton.setBounds(new Rectangle(140, 290, 50, 25));
		cancelButton.setBounds(new Rectangle(200, 290, 80, 25));
		serverLabel.setBounds(new Rectangle(10, 30, 90, 20));
		pathLabel.setBounds(new Rectangle(10, 150, 90, 20));
		trafficLabel.setBounds(new Rectangle(10, 180, 90, 20));
        portLabel.setBounds(new Rectangle(10, 210, 90, 20));
		serverField.setBounds(new Rectangle(110, 30, 170, 20));
		defaultServer.setBounds(new Rectangle(290, 30, 90, 20));
		pathField.setBounds(new Rectangle(110, 150, 170, 20));
		defaultPointPath.setBounds(new Rectangle(290, 150, 140, 20));
		trafficField.setBounds(new Rectangle(110, 180, 170, 20));
		defaultTrafficPath.setBounds(new Rectangle(290, 180, 140, 20));
		portField.setBounds(new Rectangle(110, 210, 170, 20));
		defaultPort.setBounds(new Rectangle(290, 210, 90, 20));
	    defaultExtendedName.setBounds(new Rectangle(10, 265, 90, 20));
	    extendedNameCheckBox.setBounds(new Rectangle(110, 265, 20, 20));
		this.getContentPane().add(okButton, null);
		this.getContentPane().add(cancelButton, null);
		this.getContentPane().add(serverLabel, null);
		this.getContentPane().add(pathLabel, null);
		this.getContentPane().add(portLabel, null);
    	this.getContentPane().add(trafficLabel, null);
		this.getContentPane().add(serverField, null);
		this.getContentPane().add(pathField, null);
		this.getContentPane().add(portField, null);
		this.getContentPane().add(trafficField, null);
		this.getContentPane().add(defaultServer, null);
		this.getContentPane().add(defaultPointPath, null);
		this.getContentPane().add(defaultTrafficPath, null);
        this.getContentPane().add(defaultPort, null);       
        this.getContentPane().add(defaultExtendedName, null);
        this.getContentPane().add(extendedNameCheckBox, null);
        
		okButton.addActionListener(this);
		cancelButton.addActionListener(this);
		this.setIconImage(fde.getImage());

	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == okButton) {
			okButton_actionPerformed(e);
		} else if (e.getSource() == cancelButton) {
			cancelButton_actionPerformed(e);
		}
	}

	public void okButton_actionPerformed(ActionEvent e)
	{
		name = serverField.getText();
		path = pathField.getText();
		trafficPath = trafficField.getText();
		outPort = portField.getText();
	    extendedName = extendedNameCheckBox.isSelected();
		set = true;
		saveSettings();
		setVisible(false);
		dispose();
	}

	public void cancelButton_actionPerformed(ActionEvent e)
	{
		notifier.setActionCode(FDTestPanelNotifier.ACTION_STOP_TEST);
		setVisible(false);
		dispose();
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
	
	public boolean isExtendedName()
	{
	    return extendedName;
	}

	public String getServer()
	{
		return name;
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
		return outPort;
	}
	
	public void listenForActions(Observer o)
	{
		notifier.addObserver(o);
	}
}
