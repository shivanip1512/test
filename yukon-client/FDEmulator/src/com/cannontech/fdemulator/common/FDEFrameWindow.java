/*
 * Created on May 19, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cannontech.fdemulator.common;
import java.awt.*;
import java.awt.event.*;
import java.util.Observable;
import java.util.Observer;

import javax.swing.*;
import javax.swing.border.*;

/**
 * @author ASolberg
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class FDEFrameWindow extends JFrame implements Observer, ActionListener
{

	// UI and components
	JPanel contentPane;
	JPanel panelFrame = new JPanel();

	JMenuBar menuBar1 = new JMenuBar();
	JMenu menuFile = new JMenu();
	JMenu menuTools = new JMenu();
	JMenu menuFDSystem = new JMenu();
	JMenu menuHelp = new JMenu();
	JMenuItem menuHelpAbout = new JMenuItem();
	JMenuItem menuFileExit = new JMenuItem();
	JMenuItem pointEditor = new JMenuItem();
	JMenuItem rdexProto = new JMenuItem();
	JMenuItem acsProto = new JMenuItem();
	JMenuItem valmetProto = new JMenuItem();

	ImageIcon fde = new ImageIcon("resource/fdeicon.gif");

	TitledBorder titledBorder;
	int numTests = 0;
	FDTestPanel testPanel;

	/**
	 * 
	 */
	public FDEFrameWindow()
	{
		enableEvents(AWTEvent.WINDOW_EVENT_MASK);

		try
		{
			init();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void init() throws Exception
	{
		contentPane = (JPanel) this.getContentPane();
        contentPane.setBackground(Color.gray);
		contentPane.setLayout(new BorderLayout());
		this.setSize(new Dimension(720, 700));
		this.setTitle("Foreign Data Emulator");

		BoxLayout boxLayout = new BoxLayout(panelFrame, BoxLayout.Y_AXIS);
		panelFrame.setLayout(boxLayout);
		//panelFrame.setPreferredSize(new Dimension(680, 2150));
		panelFrame.setBackground(Color.GRAY);
        JScrollPane scroller = new JScrollPane(panelFrame);
		contentPane.add(scroller, BorderLayout.CENTER);

		// initialized menu bar and menu items, as well as menu event listeners
		this.setJMenuBar(menuBar1);
		menuTools.setText("Tools");
		menuTools.setMnemonic(KeyEvent.VK_T);
		pointEditor.setText("Point Editor");
		menuFile.setText("File");
		menuFile.setMnemonic(KeyEvent.VK_F);
		menuFDSystem.setText("New FDE Test");
		menuHelp.setText("Help");
		menuHelp.setMnemonic(KeyEvent.VK_H);
		menuHelpAbout.setText("About");
		menuFileExit.setText("Exit");
		rdexProto.setText("RDEX");
		acsProto.setText("ACS");
		valmetProto.setText("VALMET");

		menuTools.add(pointEditor);
		menuHelp.add(menuHelpAbout);
		menuFile.add(menuFDSystem);
		menuFile.add(menuFileExit);
		menuFDSystem.add(rdexProto);
		menuFDSystem.add(acsProto);
		menuFDSystem.add(valmetProto);
		menuBar1.add(menuFile);
		menuBar1.add(menuTools);
		menuBar1.add(menuHelp);

		menuFileExit.addActionListener(this);
		menuHelpAbout.addActionListener(this);
		rdexProto.addActionListener(this);
		acsProto.addActionListener(this);
		valmetProto.addActionListener(this);
		pointEditor.addActionListener(this);
		this.setIconImage(fde.getImage());

	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == menuFileExit)
		{
			fileExit_actionPerformed(e);
		} else if (e.getSource() == menuHelpAbout)
		{
			helpAbout_actionPerformed(e);
		} else if (e.getSource() == rdexProto)
		{
			startRDEX_actionPerformed(e);
		} else if (e.getSource() == acsProto)
		{
			startACS_actionPerformed(e);
		} else if (e.getSource() == valmetProto)
		{
			startVALMET_actionPerformed(e);
		} else if (e.getSource() == pointEditor)
		{
			pointEditor_actionPerformed(e);
		}
	}

	public void pointEditor_actionPerformed(ActionEvent e)
	{
		PointEditor pe = new PointEditor();

		// Center the window
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = pe.getSize();
		if (frameSize.height > screenSize.height)
		{
			frameSize.height = screenSize.height;
		}
		if (frameSize.width > screenSize.width)
		{
			frameSize.width = screenSize.width;
		}
		pe.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
		pe.show();

	}

	public void helpAbout_actionPerformed(ActionEvent e)
	{
		AboutDialog about = new AboutDialog(this);
		about.show();
	}

	public void startRDEX_actionPerformed(ActionEvent e)
	{
		// Open dialog to setup emulated FDS

		testPanel = new FDTestPanel(1, this);
		// this adds us as a listener for actions
		testPanel.listenForActions(this);
		testPanel.setBorder(new EtchedBorder(EtchedBorder.RAISED));
		testPanel.setPreferredSize(new Dimension(680, 300));
		testPanel.setSize(680, 300);
		panelFrame.add(testPanel);
		testPanel.setVisible(true);
		testPanel.revalidate();
		//we change the content pane, let us revalidate
        panelFrame.revalidate();
        contentPane.revalidate();
		numTests++;
	}

	public void startACS_actionPerformed(ActionEvent e)
	{
		// Open dialog to setup emulated FDS
		testPanel = new FDTestPanel(2, this);
		// this adds us as a listener for actions
		testPanel.listenForActions(this);
		testPanel.setBorder(new EtchedBorder(EtchedBorder.RAISED));
		testPanel.setPreferredSize(new Dimension(680, 300));
		testPanel.setSize(680, 300);
		panelFrame.add(testPanel);
		testPanel.setVisible(true);
		testPanel.revalidate();
		//we change the content pane, let us revalidate
        panelFrame.revalidate();
        contentPane.revalidate();
		numTests++;
	}

	public void startVALMET_actionPerformed(ActionEvent e)
	{
		// Open dialog to setup emulated FDS
		testPanel = new FDTestPanel(3, this);
		// this adds us as a listener for actions
		testPanel.listenForActions(this);
		testPanel.setBorder(new EtchedBorder(EtchedBorder.RAISED));
		testPanel.setPreferredSize(new Dimension(680, 300));
		testPanel.setSize(680, 300);
		panelFrame.add(testPanel);
		testPanel.setVisible(true);
		testPanel.revalidate();
		//we change the content pane, let us revalidate
        panelFrame.revalidate();
        contentPane.revalidate();
		numTests++;
	}

	//	Exit from File mennu action performed
	public void fileExit_actionPerformed(ActionEvent e)
	{
		// exit the application
		System.exit(0);
	}

	/* (non-Javadoc)
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update(Observable o, Object arg)
	{
		FDTestPanelNotifier os = (FDTestPanelNotifier) o;

		if (os.getAction() == com.cannontech.fdemulator.common.FDTestPanelNotifier.ACTION_CLEAR_STATS)
		{
			os.getFDTestPanel().getLogPanel().clear();
		} else if (os.getAction() == com.cannontech.fdemulator.common.FDTestPanelNotifier.ACTION_STOP_TEST)
		{
			removeTestInProgress(os);
		}
	}

	private void removeTestInProgress(FDTestPanelNotifier notifier)
	{
		panelFrame.remove(notifier.getFDTestPanel());
		testPanel = null;
		numTests--;
		contentPane.doLayout();
		contentPane.invalidate();
		contentPane.repaint();
		this.invalidate();
		this.repaint();
		panelFrame.repaint();
		panelFrame.revalidate();
	}

	protected void processWindowEvent(WindowEvent e)
	{
		super.processWindowEvent(e);
		if (e.getID() == WindowEvent.WINDOW_CLOSING)
		{
			fileExit_actionPerformed(null);
		}
	}


	/* (non-Javadoc)
	 * @see java.awt.event.ComponentListener#componentMoved(java.awt.event.ComponentEvent)
	 */
	public void componentMoved(ComponentEvent e)
	{
		

	}

	/* (non-Javadoc)
	 * @see java.awt.event.ComponentListener#componentShown(java.awt.event.ComponentEvent)
	 */
	public void componentShown(ComponentEvent e)
	{
		

	}

	/* (non-Javadoc)
	 * @see java.awt.event.ComponentListener#componentHidden(java.awt.event.ComponentEvent)
	 */
	public void componentHidden(ComponentEvent e)
	{
		

	}
}
