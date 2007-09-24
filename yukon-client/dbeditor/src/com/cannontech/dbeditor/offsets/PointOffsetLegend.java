package com.cannontech.dbeditor.offsets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.device.definition.dao.DeviceDefinitionDao;
import com.cannontech.common.device.definition.model.DeviceDefinition;
import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.spring.YukonSpringHook;

/**
 * Insert the type's description here.
 * Creation date: (10/24/2003 9:37:54 AM)
 * @author: 
 */
public class PointOffsetLegend extends DataInputPanel//javax.swing.JPanel 
{
	private javax.swing.JLabel ivjJLabelTitle = null;
    
/**
 * PointOffsetLegend constructor comment.
 */
public PointOffsetLegend() {
	super();
	initialize();
}

// complete the needed methods with NO-OPS
public Object getValue( Object o ) { return o; }
public void setValue( Object o ) {}

/**
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelTitle() {
	if (ivjJLabelTitle == null) {
		try {
			ivjJLabelTitle = new javax.swing.JLabel();
			ivjJLabelTitle.setName("JLabelTitle");
			ivjJLabelTitle.setFont(new java.awt.Font("dialog", 0, 18));
			ivjJLabelTitle.setText("Point Offset/Number Legend");
			ivjJLabelTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			ivjJLabelTitle.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelTitle;
}

private javax.swing.JTabbedPane newJTabbedPaneOffsets(List<JPanel> panelList) {
    JTabbedPane temp = new JTabbedPane();
    if (panelList != null){
        try {
            for (JPanel panel : panelList) {
                temp.insertTab(panel.getName(), null, panel, null, 0);
            }
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return temp;
}

private javax.swing.JTextPane newTextPane(String textPaneName){
    JTextPane temp_ = new JTextPane();
    if(textPaneName != null){
        try {
            temp_.setName(textPaneName);
            temp_.setFont(new java.awt.Font("monospaced", 0, 12));
            temp_.setBounds(0, 0, 583, 408);

            temp_.setEditable( false );
            
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return temp_;
}

public javax.swing.JScrollPane newScrollPane(JTextPane textPane) {
    JScrollPane temp_ = new JScrollPane();
    if (textPane != null) {
        try {
            temp_.setName(textPane.getName());
            temp_.setViewportView(textPane);
            // user code begin {1}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {2}
            // user code end
            handleException(ivjExc);
        }
    }
    return temp_;
}

private javax.swing.JPanel newPanel(String panelName, JScrollPane scrollPane) {
    JPanel temp_ = new JPanel();
    if (panelName != null) {
        try {
            temp_.setName(panelName);
            temp_.setLayout(new java.awt.BorderLayout());
            temp_.add(scrollPane, "Center");
            // user code begin {1}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {2}
            // user code end
            handleException(ivjExc);
        }
    }
    return temp_;
}


private List<JScrollPane> getScrollPanes(){
    List<JScrollPane> scrollPaneList = new ArrayList<JScrollPane>();
    List<String> groupTypes = getGroupTypesSet();
    DeviceDefinitionDao definitionDao = YukonSpringHook.getBean("deviceDefinitionDao",DeviceDefinitionDao.class);
    
    for (String groupType : groupTypes) {
        JTextPane textPane = newTextPane(groupType);
        
        String htmlSnippet = definitionDao.getPointLegendHtml(groupType);
        textPane.setContentType("text/html");
        textPane.setText(htmlSnippet);
        
        JScrollPane scrollPane = newScrollPane(textPane);
        scrollPaneList.add(scrollPane);
    }
    
    return scrollPaneList;
}

private List<JPanel> getPanes(List<JScrollPane> scrollPaneList){
    List<JPanel> panelList = new ArrayList<JPanel>();
    
    for (JScrollPane scrollPane : scrollPaneList) {
        JPanel panel = newPanel(scrollPane.getName(), scrollPane);
        panelList.add(panel);
    }    
    
    return panelList;
}

/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
	exception.printStackTrace(System.out);
}


/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
    List<JScrollPane> scrollPanes = null;
    try {
        // user code begin {1}
        // user code end
        setName("PointOffsetLegend");
        setLayout(new java.awt.GridBagLayout());
        setSize(593, 473);
        
        scrollPanes = getScrollPanes();
        List<JPanel> panelList = getPanes(scrollPanes);

        java.awt.GridBagConstraints constraintsJTabbedPaneOffsets = new java.awt.GridBagConstraints();
        constraintsJTabbedPaneOffsets.gridx = 1; constraintsJTabbedPaneOffsets.gridy = 2;
        constraintsJTabbedPaneOffsets.fill = java.awt.GridBagConstraints.BOTH;
        constraintsJTabbedPaneOffsets.anchor = java.awt.GridBagConstraints.WEST;
        constraintsJTabbedPaneOffsets.weightx = 1.0;
        constraintsJTabbedPaneOffsets.weighty = 1.0;
        constraintsJTabbedPaneOffsets.ipadx = 561;
        constraintsJTabbedPaneOffsets.ipady = 335;
        constraintsJTabbedPaneOffsets.insets = new java.awt.Insets(4, 2, 3, 3);
        add(newJTabbedPaneOffsets(panelList), constraintsJTabbedPaneOffsets);

        java.awt.GridBagConstraints constraintsJLabelTitle = new java.awt.GridBagConstraints();
        constraintsJLabelTitle.gridx = 1; constraintsJLabelTitle.gridy = 1;
        constraintsJLabelTitle.fill = java.awt.GridBagConstraints.HORIZONTAL;
        constraintsJLabelTitle.anchor = java.awt.GridBagConstraints.WEST;
        constraintsJLabelTitle.ipadx = 361;
        constraintsJLabelTitle.ipady = -1;
        constraintsJLabelTitle.insets = new java.awt.Insets(6, 2, 3, 3);
        add(getJLabelTitle(), constraintsJLabelTitle);
    } catch (java.lang.Throwable ivjExc) {
        handleException(ivjExc);
    }
    // user code begin {2}
    
    initPages(scrollPanes);
    
    // user code end
}

/**
 * Gets all the information for the deviceDefinitions.xml
 * 
 * @return
 */
private List<String> getGroupTypesSet(){
    
    DeviceDefinitionDao definitionDao = YukonSpringHook.getBean("deviceDefinitionDao",DeviceDefinitionDao.class);

    Map<String, List<DeviceDefinition>> deviceDefinitionsMap = definitionDao.getDeviceDisplayGroupMap();
    List<String> groupTypes = new ArrayList<String>(deviceDefinitionsMap.keySet());
    Collections.sort(groupTypes);
    Collections.reverse(groupTypes);

    return groupTypes;
}

private void initPages(final List<JScrollPane> scrollPanes)
{
    
    //we need to scroll up the scrollbars in a different Thread
    SwingUtilities.invokeLater( new Runnable()
    {
        public void run()
        {
            for (JScrollPane panel : scrollPanes){
                panel.getVerticalScrollBar().setValue(
                     panel.getVerticalScrollBar().getMinimum() );
            }
        }
    });

}

/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		PointOffsetLegend aPointOffsetLegend;
		aPointOffsetLegend = new PointOffsetLegend();
		frame.setContentPane(aPointOffsetLegend);
		frame.setSize(aPointOffsetLegend.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.show();
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}
}