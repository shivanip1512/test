package com.cannontech.common.gui.util;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.Timer;

/**
 * @author rneuharth
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class JDialogWait extends JDialog
{
	private JLabel jLabelMessage = null;
	private JProgressBar jProgressBar = null;
	

	/**
	 * Constructor for JDialogWait.
	 */
	public JDialogWait()
	{
		this( null );
	}

	/**
	 * Constructor for JDialogWait.
	 */
	public JDialogWait( Frame owner_ )
	{
		super( owner_ );
		initialize();
	}


	private void initialize()
	{
		setSize( new Dimension(300, 100) );
		setModal( false );
		setResizable( false );
		setDefaultCloseOperation( JDialog.DISPOSE_ON_CLOSE );

		getContentPane().setLayout(null);
		//getContentPane().setLayout( new BorderLayout() );		
		getContentPane().add( getJProgressBar() );//, BorderLayout.CENTER );
	}
	
	private JProgressBar getJProgressBar()
	{
		if( jProgressBar == null )
		{
			jProgressBar = new JProgressBar();
			jProgressBar.setFont( new Font(jProgressBar.getFont().getName(), Font.BOLD, 14) );
			jProgressBar.setString("Refreshing Data...");
			jProgressBar.setIndeterminate( true );
			jProgressBar.setStringPainted( true );
			
			jProgressBar.setLocation( 
					(int)(getWidth() * .15), 
					(int)(getHeight() * .15) ); 

			jProgressBar.setSize( new Dimension(200,25) );
		}
		
		return jProgressBar;
	}

	public static void main(String[] args)
	{
		JDialogWait d = new JDialogWait();
		
		d.show();
		
		try
		{
			
			Thread.currentThread().sleep( 3000 );
			System.out.println("..Done");
			
			d.dispose();
		}
		catch( Exception e ){}
	}
	
}
