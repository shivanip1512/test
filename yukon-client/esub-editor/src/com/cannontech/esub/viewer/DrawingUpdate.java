package com.cannontech.esub.viewer;

import java.net.URL;
import java.net.URLConnection;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import com.loox.jloox.LxGraph;
import com.loox.jloox.LxComponent;

import com.cannontech.esub.util.ClientSession;

/**
 * Creation date: (1/7/2002 3:46:36 PM)
 * @author:  Aaron Lauinger
 */
public class DrawingUpdate implements ActionListener {
	private static final String pointDataURI = "/servlet/PointData";		
	private LxGraph lxGraph;

/**
 * DrawingUpdate constructor comment.
 */
public DrawingUpdate() {
	super();
}
/**
 * Creation date: (1/7/2002 4:43:55 PM)
 */
public void actionPerformed(ActionEvent e)
{

    try
        {
        System.out.println("Performing update");
        LxComponent[] components = lxGraph.getComponents();

        for (int i = 0; i < components.length; i++)
            {

            LxComponent c = components[i];
            if (c instanceof com.cannontech.esub.editor.element.DynamicText)
                {
                final com.cannontech.esub.editor.element.DynamicText t =
                    (com.cannontech.esub.editor.element.DynamicText) c;

                final double val = getPointValue(t.getPointID());
                System.out.println("Update pointid " + t.getPointID() + ":" + val);

                javax.swing.SwingUtilities.invokeLater(new Runnable()
                {
                    public void run()
                    {
                        t.setText(Double.toString(val));
                    }
                });
            }
            else
                if (c instanceof com.cannontech.esub.editor.element.StateImage)
                    {

                    final com.cannontech.esub.editor.element.StateImage si =
                        (com.cannontech.esub.editor.element.StateImage) c;

                    final String state = getPointState(si.getPointID());
                    System.out.println("Update pointid " + si.getPointID() + ":" + state);
                    javax.swing.SwingUtilities.invokeLater(new Runnable()
                    {
                        public void run()
                        {
                            si.setState(state);
                        }
                    });
                }
        }

        javax.swing.SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                for (int i = 0; i < lxGraph.getViewCount(); i++)
                    {
                    lxGraph.getView(i).repaint();
                }
            }
        });

    }
    catch (Exception ex)
        { //avoid the timer dying
        ex.printStackTrace();
    }
}
/**
 * Creation date: (1/7/2002 4:39:11 PM)
 * @return com.loox.jloox.LxGraph
 */
public com.loox.jloox.LxGraph getLxGraph() {
	return lxGraph;
}
/**
 * Creation date: (1/7/2002 4:57:13 PM)
 * @return double
 * @param id int
 */
String getPointState(int id) {
	double retVal = Double.NaN;
	
	String[] param = new String[] { "id" };
	String[] values = new String[] { Integer.toString(id) };
  	String valStr = ClientSession.getInstance().doPost("/servlet/PointData", param, values);
System.out.println("State received was: " + valStr);
	return valStr;
	
}
/**
 * Creation date: (1/7/2002 4:57:13 PM)
 * @return double
 * @param id int
 */
double getPointValue(int id) {
	double retVal = Double.NaN;
	
	String[] param = new String[] { "id" };
	String[] values = new String[] { Integer.toString(id) };
  	String valStr = ClientSession.getInstance().doPost("/servlet/PointData", param, values);

  	if( valStr != null ) {
	  	try {
			retVal = Double.parseDouble(valStr);
	  	}
 	 	catch(NumberFormatException ne) {
	 	 	ne.printStackTrace();
  		}
  	}

	return retVal;
	
}
/**
 * Creation date: (1/7/2002 4:39:11 PM)
 * @param newLxGraph com.loox.jloox.LxGraph
 */
public void setLxGraph(com.loox.jloox.LxGraph newLxGraph) {
	lxGraph = newLxGraph;
}
}
