package com.cannontech.common.gui.unchanging;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultMutableTreeNode;

import com.cannontech.common.gui.dnd.TransferableTreeNode;
import com.cannontech.database.data.lite.LiteYukonPAObject;

/**
 * @author rneuharth
 *
 * The class is an example of using a DnD (Drag and Drop) or clipboard transfer with user defined
 * property.  Most DnD is done using pre 1.4 methods (the hard way, but easy
 * once it has been implemented!) in a different package.
 *
 */
public class CTITransferHandler extends TransferHandler
{
	//the name of the property, needs a method for it defined in the JavaBean standard
	public static final String TRANS_PROP = "transferProp";


public class MyButton extends JButton
{
	public MyButton(String s )
	{
		super(s);
	};

	
	//acts as the call for DRAG
	public LiteYukonPAObject getTransferProp()
	{
		System.out.println("		--Called GET TransferProp() Button");
		return new LiteYukonPAObject(1111, "Testy");				
	};


	//acts as the call for DROP
	public void setTransferProp( LiteYukonPAObject trans_ )
	{
		System.out.println("		--Called SET TransferProp(VAL) Button");
						
		MyButton.this.setText( trans_.getPaoName() );
	};			
				
};


public class MyTable extends JTable
{
	public MyTable( Object[][] v, Object[] vc )
	{
		super( v, vc );		
	};

	//acts as the call for DRAG
	public LiteYukonPAObject getTransferProp()
	{
		
		System.out.println("		--Called GET TransferProp() Table");
		return new LiteYukonPAObject(1111, "Testy");				
	};


	//acts as the call for DROP
	public void setTransferProp( LiteYukonPAObject trans_ )
	{
		System.out.println("		--Called SET TransferProp(VAL) Table");
						
		MyTable.this.setToolTipText( trans_.getPaoName() );
	};			
				
};



	/**
	 * Constructor for CTITransferHandler.
	 * @param property
	 */
	public CTITransferHandler(String property)
	{
		super(property);
	}

	/**
	 * Constructor for CTITransferHandler.
	 */
	public CTITransferHandler()
	{
		super();
	}


	public static void main(String args[])
	{
		javax.swing.JFrame f = new javax.swing.JFrame();
		f.addWindowListener( new WindowAdapter()  { 
          public void windowClosing(WindowEvent e) 
          { System.exit(0); } 
      });

        
		Object[][] v =
		{ 
			{"a1", "a2", "a3"},
			{"b1", "b2", "b3"},
			{"c1", "c2", "c3"},
		};
		
		CTITransferHandler th0 = new CTITransferHandler(TRANS_PROP);
		MyTable t = th0.new MyTable( v, new Object[] { "First", "Sec", "Third" } );
		t.setTransferHandler( th0 );
		t.setSize( 100, 100 );

     // Mouse click used as a Drag gesture recognizer 
     MouseAdapter ml = new MouseAdapter() { 
       public void mousePressed(MouseEvent e) {
System.out.println("  mouse_press" ); 
             JComponent c = (JComponent)e.getSource(); 
             TransferHandler th = c.getTransferHandler(); 
             th.exportAsDrag(c, e, TransferHandler.COPY); 
         } 
     }; 
     t.addMouseListener(ml);
	
	


		//JButton b1 = new JButton("Button1")
		CTITransferHandler th1 = new CTITransferHandler(TRANS_PROP);
		MyButton b1 = th1.new MyButton("Button1");		
		b1.setTransferHandler( th1 );
		b1.setSize( 60, 25 );
		b1.addMouseListener(ml);


		//JButton b2 = new JButton("Button2")
		CTITransferHandler th2 = new CTITransferHandler(TRANS_PROP);
		MyButton b2 = th2.new MyButton("Button2");
		b2.setTransferHandler( th2 );
		b2.setSize( 60, 25 );
		b2.addMouseListener(ml);


		JPanel cp = new JPanel();
		cp.add( t );
		cp.add( b1 );
		cp.add( b2 );
		
		f.setContentPane( cp );
		f.setSize( 300, 300 );
		
		
		
		f.show();	
	}



	public static void printProps( JComponent comp )
	{
	
		Class k = comp.getClass();
		BeanInfo bi;
		try {
		    bi = Introspector.getBeanInfo(k);
		} catch (IntrospectionException ex) 
		{
			return;
		}
		
		PropertyDescriptor props[] = bi.getPropertyDescriptors();
		for (int i=0; i < props.length; i++) 
		{
			System.out.println("\t\t" + props[i].getName().toString() );		
		}
		
	}

}