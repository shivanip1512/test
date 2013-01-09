package com.cannontech.esub.editor.element;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.editor.PropertyPanelEvent;
import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.gui.util.DataInputPanelListener;
import com.cannontech.common.gui.util.TitleBorder;
import com.cannontech.common.util.SwingUtil;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.Transaction;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.data.lite.LiteYukonImage;
import com.cannontech.database.db.CTIDbChange;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.dbeditor.wizard.state.YukonImagePanel;
import com.cannontech.esub.element.FunctionElement;
import com.cannontech.esub.element.StateImage;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.yukon.conns.ConnPool;

/**
 * Creation date: (1/14/2002 3:37:58 PM)
 * @author: alauinger
 */
public class StateImageEditorPanel extends DataInputPanel implements ActionListener, TreeSelectionListener {
	private javax.swing.JPanel pointPanel = null;
	private PointSelectionPanel ivjPointSelectionPanel = null;
	private StateImage stateImage;
    private HashMap map = new HashMap();
	private LinkToPanel ivjLinkToPanel = null;
    private JLabel previewLabel;
    private JLabel rawStateLabel;
    private JLabel imageLabel;
    private JPanel previewPanel;
    private JButton editButton0;
    private JButton editButton1;
    private JButton editButton2;
    private JButton editButton3;
    private JButton editButton4;
    private JButton editButton5;
    private JButton editButton6;
    private JButton editButton7;
    private JButton editButton8;
    private JButton editButton9;
    private JButton editButton10;
    private JButton resetButton;
    private JLabel rawState0;
    private JLabel rawState1;
    private JLabel rawState2;
    private JLabel rawState3;
    private JLabel rawState4;
    private JLabel rawState5;
    private JLabel rawState6;
    private JLabel rawState7;
    private JLabel rawState8;
    private JLabel rawState9;
    private JLabel rawState10;
    private JLabel imageLabel0;
    private JLabel imageLabel1;
    private JLabel imageLabel2;
    private JLabel imageLabel3;
    private JLabel imageLabel4;
    private JLabel imageLabel5;
    private JLabel imageLabel6;
    private JLabel imageLabel7;
    private JLabel imageLabel8;
    private JLabel imageLabel9;
    private JLabel imageLabel10;
    private JLabel[] imageLabels = new JLabel[12];
    private JButton[] buttons = new JButton[12];
    private JLabel[] stateLabels = new JLabel[12];
    private static ImageIcon defaultIcon = null;
    private JCheckBox controlCheckBox;
    
    static
    {
        byte[] imagebuffer = null;
        try 
        {
            InputStream resource = null;

            resource = FunctionElement.class.getResourceAsStream("/X.gif");

            BufferedInputStream in = new BufferedInputStream(resource);
            ByteArrayOutputStream out = new ByteArrayOutputStream(1024);

            byte[] buffer = new byte[1024];
            int n;
            while ((n = in.read(buffer)) > 0) 
            {
                out.write(buffer, 0, n);
            }
            in.close();
            out.flush();
            imagebuffer = out.toByteArray();
        } catch (IOException ioe)
        {
            System.err.println(ioe.toString());
        }

        defaultIcon = new ImageIcon(imagebuffer);
    }
    
/**
 * StateImageEditorPanel constructor comment.
 */
public StateImageEditorPanel() {
	super();
	initialize();
}
/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getPointPanel() {
	if (pointPanel == null) {
		try {
			pointPanel = new javax.swing.JPanel();
			pointPanel.setName("JPanel1");
			pointPanel.setLayout(new java.awt.GridBagLayout());
			pointPanel.setBorder(new TitleBorder("Point Selection:"));
//            pointPanel.setPreferredSize(new Dimension(300  ,520));
//            pointPanel.setMinimumSize(new Dimension(300,520));
            
            java.awt.GridBagConstraints constraintsLinkToPanel = new java.awt.GridBagConstraints();
            constraintsLinkToPanel.gridx = 0; constraintsLinkToPanel.gridy = 0;
            constraintsLinkToPanel.gridwidth = 1;
            constraintsLinkToPanel.anchor = java.awt.GridBagConstraints.WEST;
            constraintsLinkToPanel.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsLinkToPanel.insets = new java.awt.Insets(4, 4, 4, 4);
             getPointPanel().add(getLinkToPanel(), constraintsLinkToPanel);
            
             java.awt.GridBagConstraints constraintsControlCheckBox = new java.awt.GridBagConstraints();
             constraintsControlCheckBox.gridx = 0; constraintsControlCheckBox.gridy = 1;
             constraintsControlCheckBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
//             constraintsControlCheckBox.weightx = 1.0;
//             constraintsControlCheckBox.weighty = 1.0;
             constraintsControlCheckBox.gridwidth = 1;
             constraintsControlCheckBox.anchor = java.awt.GridBagConstraints.WEST;
             constraintsControlCheckBox.insets = new java.awt.Insets(4, 4, 4, 4);
              getPointPanel().add(getControlCheckBox(), constraintsControlCheckBox);
             
            java.awt.GridBagConstraints constraintsPointSelectionPanel = new java.awt.GridBagConstraints();
			constraintsPointSelectionPanel.gridx = 0; constraintsPointSelectionPanel.gridy = 2;
			constraintsPointSelectionPanel.fill = java.awt.GridBagConstraints.BOTH;
			constraintsPointSelectionPanel.weightx = 1.0;
			constraintsPointSelectionPanel.weighty = 1.0;
            constraintsPointSelectionPanel.gridwidth = 1;
            constraintsPointSelectionPanel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsPointSelectionPanel.insets = new java.awt.Insets(4, 4, 4, 4);
             getPointPanel().add(getPointSelectionPanel(), constraintsPointSelectionPanel);

		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return pointPanel;
}

private javax.swing.JPanel getPreviewPanel() 
{
    if (previewPanel == null) 
    {
        try
        {
            previewPanel = new javax.swing.JPanel();
            previewPanel.setName("PreviewPanel");
            previewPanel.setLayout(null);
            
            previewPanel.setPreferredSize(new Dimension(200,520));
            previewPanel.setSize(new Dimension(200,520));
            previewPanel.setMinimumSize(new Dimension(200,520));
            previewPanel.setBorder(new TitleBorder("Preview"));
            previewPanel.setAlignmentX(javax.swing.SwingConstants.TOP);
            
            getPreviewLabel().setBounds(new Rectangle(20,20,100,20));
            previewPanel.add(getPreviewLabel());
            
            getRawStateLabel().setBounds(new Rectangle( 10, 50, 60, 20));
            previewPanel.add(getRawStateLabel());
            
            getImageLabel().setBounds(new Rectangle( 75, 50, 60, 20));
            getImageLabel().setHorizontalAlignment(SwingConstants.CENTER);
            previewPanel.add(getImageLabel());
            
            getImageLabel0().setBounds(new Rectangle( 85, 75, 60, 30));
            previewPanel.add(getImageLabel0());
            
            getImageLabel1().setBounds(new Rectangle( 85, 110, 60, 30));
            previewPanel.add(getImageLabel1());
            
            getImageLabel2().setBounds(new Rectangle( 85, 145, 60, 30));
            previewPanel.add(getImageLabel2());
            
            getImageLabel3().setBounds(new Rectangle( 85, 180, 60, 30));
            previewPanel.add(getImageLabel3());
            
            getImageLabel4().setBounds(new Rectangle( 85, 215, 60, 30));
            previewPanel.add(getImageLabel4());
            
            getImageLabel5().setBounds(new Rectangle( 85, 250, 60, 30));
            previewPanel.add(getImageLabel5());
            
            getImageLabel6().setBounds(new Rectangle( 85, 285, 60, 30));
            previewPanel.add(getImageLabel6());
            
            getImageLabel7().setBounds(new Rectangle( 85, 320, 60, 30));
            previewPanel.add(getImageLabel7());
            
            getImageLabel8().setBounds(new Rectangle( 85, 355, 60, 30));
            previewPanel.add(getImageLabel8());
            
            getImageLabel9().setBounds(new Rectangle( 85, 390, 60, 30));
            previewPanel.add(getImageLabel9());
            
            getImageLabel10().setBounds(new Rectangle( 85, 425, 60, 30));
            previewPanel.add(getImageLabel10());
            
            getRawState0().setBounds(new Rectangle( 10, 75, 90, 30));
            previewPanel.add(getRawState0());
            
            getRawState1().setBounds(new Rectangle( 10, 110, 90, 30));
            previewPanel.add(getRawState1());
            
            getRawState2().setBounds(new Rectangle( 10, 145, 90, 30));
            previewPanel.add(getRawState2());
            
            getRawState3().setBounds(new Rectangle( 10, 180, 90, 30));
            previewPanel.add(getRawState3());
            
            getRawState4().setBounds(new Rectangle( 10, 215, 90, 30));
            previewPanel.add(getRawState4());
            
            getRawState5().setBounds(new Rectangle( 10, 250, 85, 30));
            previewPanel.add(getRawState5());
            
            getRawState6().setBounds(new Rectangle( 10, 285, 90, 30));
            previewPanel.add(getRawState6());
            
            getRawState7().setBounds(new Rectangle( 10, 320, 90, 30));
            previewPanel.add(getRawState7());
            
            getRawState8().setBounds(new Rectangle( 10, 355, 90, 30));
            previewPanel.add(getRawState8());
            
            getRawState9().setBounds(new Rectangle( 10, 390, 90, 30));
            previewPanel.add(getRawState9());
            
            getRawState10().setBounds(new Rectangle( 10, 425, 90, 30));
            previewPanel.add(getRawState10());
            
            getEditButton0().setBounds(new Rectangle( 140, 75, 50, 25));
            previewPanel.add(getEditButton0());
            
            getEditButton1().setBounds(new Rectangle( 140, 110, 50, 25));
            previewPanel.add(getEditButton1());
            
            getEditButton2().setBounds(new Rectangle( 140, 145, 50, 25));
            previewPanel.add(getEditButton2());
            
            getEditButton3().setBounds(new Rectangle( 140, 180, 50, 25));
            previewPanel.add(getEditButton3());
            
            getEditButton4().setBounds(new Rectangle( 140, 215, 50, 25));
            previewPanel.add(getEditButton4());
            
            getEditButton5().setBounds(new Rectangle( 140, 250, 50, 25));
            previewPanel.add(getEditButton5());
            
            getEditButton6().setBounds(new Rectangle( 140, 285, 50, 25));
            previewPanel.add(getEditButton6());
            
            getEditButton7().setBounds(new Rectangle( 140, 320, 50, 25));
            previewPanel.add(getEditButton7());
            
            getEditButton8().setBounds(new Rectangle( 140, 355, 50, 25));
            previewPanel.add(getEditButton8());
            
            getEditButton9().setBounds(new Rectangle( 140, 390, 50, 25));
            previewPanel.add(getEditButton9());
            
            getEditButton10().setBounds(new Rectangle( 140, 425, 50, 25));
            previewPanel.add(getEditButton10());
            
            getResetButton().setBounds(new Rectangle( 60, 460, 80, 25));
            previewPanel.add(getResetButton());
                    
        } catch (java.lang.Throwable ivjExc) 
        {
            handleException(ivjExc);
        }
    }
    return previewPanel;
}

/**
 * Return the LinkToPanel property value.
 * @return com.cannontech.esub.editor.element.LinkToPanel
 */
private LinkToPanel getLinkToPanel() {
	if (ivjLinkToPanel == null) {
		try {
			ivjLinkToPanel = new com.cannontech.esub.editor.element.LinkToPanel();
			ivjLinkToPanel.setName("LinkToPanel");
		} catch (java.lang.Throwable ivjExc) {

			handleException(ivjExc);
		}
	}
	return ivjLinkToPanel;
}
/**
 * Return the PointSelectionPanel property value.
 * @return com.cannontech.esub.editor.element.PointSelectionPanel
 */
private PointSelectionPanel getPointSelectionPanel() {
	if (ivjPointSelectionPanel == null) {
		try {
			ivjPointSelectionPanel = com.cannontech.esub.editor.Util.getPointSelectionPanel();		
			ivjPointSelectionPanel.setName("PointSelectionPanel");
            ivjPointSelectionPanel.setPreferredSize(new Dimension(150,400));
            ivjPointSelectionPanel.setMinimumSize(new Dimension(150,400));
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjPointSelectionPanel;
}

private JCheckBox getControlCheckBox()
{
    if (controlCheckBox == null) {
        try {
            controlCheckBox = new JCheckBox("Enable Control");   
            controlCheckBox.setName("ControlCheckBox");
            controlCheckBox.setSelected(true);

        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return controlCheckBox;
}

/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param o java.lang.Object
 */
public Object getValue(Object o) {
	String link = getLinkToPanel().getLinkTo();
	if(link.length() > 0 ) {
		stateImage.setLinkTo(link);
	} else {
		stateImage.setLinkTo(null);
	}
	stateImage.setPoint(getPointSelectionPanel().getSelectedPoint());
    
    stateImage.setCustomImageMap(map);
	stateImage.setControlEnabled(getControlCheckBox().isSelected());
	return stateImage;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	 System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	 exception.printStackTrace(System.out);
}
/**
 * Initialize the class.
 */
private void initialize() {
	try {
		setName("StateImageEditorPanel");
        setLayout(new java.awt.GridBagLayout());

        defaultIcon.setImage(
                defaultIcon.getImage().getScaledInstance( 
                      30,
                      30,
                      java.awt.Image.SCALE_AREA_AVERAGING ) );
        
        
        java.awt.GridBagConstraints constraintsPointPanel = new java.awt.GridBagConstraints();
        constraintsPointPanel.gridx = 0; constraintsPointPanel.gridy = 0;
        constraintsPointPanel.gridwidth = 1;
        constraintsPointPanel.fill = java.awt.GridBagConstraints.BOTH;
        constraintsPointPanel.anchor = java.awt.GridBagConstraints.NORTH;
        constraintsPointPanel.insets = new java.awt.Insets(4, 4, 4, 4);
        constraintsPointPanel.weightx = .5;
        add(getPointPanel(), constraintsPointPanel);

        java.awt.GridBagConstraints constraintsPreviewPanel = new java.awt.GridBagConstraints();
        constraintsPreviewPanel.gridx = 1; constraintsPreviewPanel.gridy = 0;
        constraintsPreviewPanel.gridwidth = 1;
        constraintsPreviewPanel.fill = java.awt.GridBagConstraints.BOTH;
        constraintsPreviewPanel.weightx = .5;
        constraintsPreviewPanel.insets = new java.awt.Insets(4, 4, 4, 4);
        add(getPreviewPanel(), constraintsPreviewPanel);
        
         
         imageLabels[0] = getImageLabel0(); 
         imageLabels[1] = getImageLabel1(); 
         imageLabels[2] = getImageLabel2(); 
         imageLabels[3] = getImageLabel3(); 
         imageLabels[4] = getImageLabel4(); 
         imageLabels[5] = getImageLabel5(); 
         imageLabels[6] = getImageLabel6(); 
         imageLabels[7] = getImageLabel7(); 
         imageLabels[8] = getImageLabel8(); 
         imageLabels[9] = getImageLabel9(); 
         imageLabels[10] = getImageLabel10();

         
         buttons[0] = getEditButton0(); 
         buttons[1] = getEditButton1();
         buttons[2] = getEditButton2();
         buttons[3] = getEditButton3();
         buttons[4] = getEditButton4();
         buttons[5] = getEditButton5();
         buttons[6] = getEditButton6();
         buttons[7] = getEditButton7();
         buttons[8] = getEditButton8();
         buttons[9] = getEditButton9();
         buttons[10] = getEditButton10();

         
         getEditButton0().addActionListener(this);
         getEditButton1().addActionListener(this);
         getEditButton2().addActionListener(this);
         getEditButton3().addActionListener(this);
         getEditButton4().addActionListener(this);
         getEditButton5().addActionListener(this);
         getEditButton6().addActionListener(this);
         getEditButton7().addActionListener(this);
         getEditButton8().addActionListener(this);
         getEditButton9().addActionListener(this);
         getEditButton10().addActionListener(this);

         
         getResetButton().addActionListener(this);
         
         stateLabels[0] = getRawState0(); 
         stateLabels[1] = getRawState1();
         stateLabels[2] = getRawState2();
         stateLabels[3] = getRawState3();
         stateLabels[4] = getRawState4();
         stateLabels[5] = getRawState5();
         stateLabels[6] = getRawState6();
         stateLabels[7] = getRawState7();
         stateLabels[8] = getRawState8();
         stateLabels[9] = getRawState9();
         stateLabels[10] = getRawState10();

        
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	getPointSelectionPanel().getIvjDevicePointTree().addTreeSelectionListener(this);
}
/**
 * Creation date: (1/23/2002 11:12:06 AM)
 * @return boolean
 */
public boolean isInputValid() {
	LitePoint pt = getPointSelectionPanel().getSelectedPoint();
	return (pt != null);
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		StateImageEditorPanel aStateImageEditorPanel;
		aStateImageEditorPanel = new StateImageEditorPanel();
		frame.setContentPane(aStateImageEditorPanel);
		frame.setSize(aStateImageEditorPanel.getSize());
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
		System.err.println("Exception occurred in main() of com.cannontech.common.gui.util.DataInputPanel");
		exception.printStackTrace(System.out);
	}
}
/**
 * This method was created in VisualAge.
 * @param o java.lang.Object
 */
public void setValue(Object o) 
{
	stateImage = (StateImage) o;
	map = new HashMap( stateImage.getCustomImageMap());
	// Set link
	getLinkToPanel().setLinkTo(stateImage.getLinkTo());

	getPointSelectionPanel().refresh();

	// Set selected point 
	LitePoint lp = stateImage.getPoint();
	if( lp != null )
    {  // this is usually not null since the default pointid is 7 ?
		getPointSelectionPanel().selectPoint(lp);
	}
    
    boolean control = stateImage.getControlEnabled();
    getControlCheckBox().setSelected(control);
    
}
/**
 * Creation date: (12/18/2001 4:16:51 PM)
 * @param evt javax.swing.event.TreeSelectionEvent
 */
public void valueChanged(TreeSelectionEvent evt) 
{
	LitePoint p = getPointSelectionPanel().getSelectedPoint();	
    if(p != null)
    {
        setPreviewPanelImages(p);
    }
	fireInputUpdate();
}

public void resetPreviewPanelImages(LitePoint p)
{
    List images = new ArrayList(12);
    if(p != null)
    {
        LiteStateGroup lsg = DaoFactory.getStateDao().getLiteStateGroup(p.getStateGroupID());
        List states = lsg.getStatesList();
        
        for(int i = 0; i < states.size(); i++) 
        {
            int imgId = ((LiteState) states.get(i)).getImageID();
            LiteState state = (LiteState)states.get(i);
            int rawStateNumber = state.getStateRawState();
            
            LiteYukonImage lyi = DaoFactory.getYukonImageDao().getLiteYukonImage(imgId); 
            ImageIcon icon; 
            if(lyi == null)
            {
                icon = defaultIcon;
            }else
            {
                icon = new ImageIcon(lyi.getImageValue());
            }
            
            icon = scaleImage(icon);
            imageLabels[rawStateNumber].setIcon(icon);
            images.add(imageLabels[rawStateNumber]);
            
            stateLabels[i].setText(state.getStateText());
            
            buttons[i].setVisible(true);
            imageLabels[i].setVisible(true);
            stateLabels[i].setVisible(true);
        }
        
        for(int i = 0; i < 11; i++)
        {
            
           if(!images.contains(imageLabels[i]))
           {
               buttons[i].setVisible(false);
               imageLabels[i].setVisible(false);
               stateLabels[i].setVisible(false);
           }
        }
    }else
    {
        for(int i = 0; i < 11; i++)
        {
            
            buttons[i].setVisible(false);
            imageLabels[i].setIcon(scaleImage(defaultIcon));
            imageLabels[i].setVisible(false);
            stateLabels[i].setVisible(false);
        }
    }
}

/**
 * Returns a list of LiteYukonImages corresponding to each of the points raw states
 * @return List
 */
public void setPreviewPanelImages(LitePoint p) 
{
    List images = new ArrayList(12);
    LiteStateGroup lsg = DaoFactory.getStateDao().getLiteStateGroup(p.getStateGroupID());
    List states = lsg.getStatesList();
    
    for(int i = 0; i < states.size(); i++) 
    {
        
        int imgId = ((LiteState) states.get(i)).getImageID();
        LiteState state = (LiteState)states.get(i);
        int rawStateNumber = state.getStateRawState();
        
        if(!(map.get(rawStateNumber) == null))
        {
            imgId = new Integer((Integer)map.get(rawStateNumber)).intValue();
        }
        
        LiteYukonImage lyi = DaoFactory.getYukonImageDao().getLiteYukonImage(imgId); 
        ImageIcon icon; 
        if(lyi == null)
        {
            icon = defaultIcon;
        }else
        {
            icon = new ImageIcon(lyi.getImageValue());
        }
        icon = scaleImage(icon);
        imageLabels[rawStateNumber].setIcon(icon);
        images.add(imageLabels[rawStateNumber]);
        
        stateLabels[i].setText(state.getStateText());
        
        buttons[rawStateNumber].setVisible(true);
        imageLabels[rawStateNumber].setVisible(true);
        stateLabels[rawStateNumber].setVisible(true);

    }
    
    for(int i = 0; i < 11; i++)
    {
       if(!images.contains(imageLabels[i]))
       {
           buttons[i].setVisible(false);
           imageLabels[i].setVisible(false);
           stateLabels[i].setVisible(false);
       }
    }
}

private JLabel getPreviewLabel()
{
    if (previewLabel == null)
    {
        try
        {
            previewLabel = new JLabel();
            previewLabel.setName("PreviewLabel");
            previewLabel.setText("State Images:");
            previewLabel.setVerticalAlignment(javax.swing.SwingConstants.TOP);
            
        }catch( java.lang.Throwable ivjExc )
        {
            handleException(ivjExc);
        }
    }
    return previewLabel;
}

private JLabel getImageLabel()
{
    if (imageLabel == null)
    {
        try
        {
            imageLabel = new JLabel();
            imageLabel.setName("ImageLabel");
            imageLabel.setText("Image");
        }catch( java.lang.Throwable ivjExc )
        {
            handleException(ivjExc);
        }
    }
    return imageLabel;
}

private JLabel getRawStateLabel()
{
    if (rawStateLabel == null)
    {
        try
        {
            rawStateLabel = new JLabel();
            rawStateLabel.setName("RawStateLabel");
            rawStateLabel.setText("Raw State");
            rawStateLabel.setAlignmentX(javax.swing.SwingConstants.TOP);

        }catch( java.lang.Throwable ivjExc )
        {
            handleException(ivjExc);
        }
    }
    return rawStateLabel;
}

private javax.swing.JLabel getRawState0() {
    if (rawState0 == null) {
        try {
            rawState0 = new javax.swing.JLabel();
            rawState0.setName("RawState0");
            rawState0.setText("0");
            rawState0.setHorizontalAlignment(SwingConstants.CENTER);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return rawState0;
}

private javax.swing.JLabel getRawState1() {
    if (rawState1 == null) {
        try {
            rawState1 = new javax.swing.JLabel();
            rawState1.setName("RawState1");
            rawState1.setText("1");
            rawState1.setHorizontalAlignment(SwingConstants.CENTER);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return rawState1;
}

private javax.swing.JLabel getRawState2() {
    if (rawState2 == null) {
        try {
            rawState2 = new javax.swing.JLabel();
            rawState2.setName("RawState2");
            rawState2.setText("2");
            rawState2.setHorizontalAlignment(SwingConstants.CENTER);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return rawState2;
}

private javax.swing.JLabel getRawState3() {
    if (rawState3 == null) {
        try {
            rawState3 = new javax.swing.JLabel();
            rawState3.setName("RawState3");
            rawState3.setText("3");
            rawState3.setHorizontalAlignment(SwingConstants.CENTER);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return rawState3;
}

private javax.swing.JLabel getRawState4() 
{
    if (rawState4 == null) {
        try {
            rawState4 = new javax.swing.JLabel();
            rawState4.setName("RawState4");
            rawState4.setText("4");
            rawState4.setHorizontalAlignment(SwingConstants.CENTER);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return rawState4;
}

private javax.swing.JLabel getRawState5() {
    if (rawState5 == null) {
        try {
            rawState5 = new javax.swing.JLabel();
            rawState5.setName("RawState5");
            rawState5.setText("5");
            rawState5.setHorizontalAlignment(SwingConstants.CENTER);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return rawState5;
}

private javax.swing.JLabel getRawState6() {
    if (rawState6 == null) {
        try {
            rawState6 = new javax.swing.JLabel();
            rawState6.setName("RawState6");
            rawState6.setText("6");
            rawState6.setHorizontalAlignment(SwingConstants.CENTER);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return rawState6;
}

private javax.swing.JLabel getRawState7() {
    if (rawState7 == null) {
        try {
            rawState7 = new javax.swing.JLabel();
            rawState7.setName("RawState7");
            rawState7.setText("7");
            rawState7.setHorizontalAlignment(SwingConstants.CENTER);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return rawState7;
}

private javax.swing.JLabel getRawState8() {
    if (rawState8 == null) {
        try {
            rawState8 = new javax.swing.JLabel();
            rawState8.setName("RawState8");
            rawState8.setText("8");
            rawState8.setHorizontalAlignment(SwingConstants.CENTER);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return rawState8;
}

private javax.swing.JLabel getRawState9() {
    if (rawState9 == null) {
        try {
            rawState9 = new javax.swing.JLabel();
            rawState9.setName("RawState9");
            rawState9.setText("9");
            rawState9.setHorizontalAlignment(SwingConstants.CENTER);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return rawState9;
}

private javax.swing.JLabel getRawState10() {
    if (rawState10 == null) {
        try {
            rawState10 = new javax.swing.JLabel();
            rawState10.setName("RawState10");
            rawState10.setText("10");
            rawState10.setHorizontalAlignment(SwingConstants.CENTER);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return rawState10;
}

private JButton getEditButton0()
{
    if (editButton0 == null) {
        try {
            editButton0 = new JButton();
            editButton0.setName("EditButton0");
            editButton0.setText("Edit");
            editButton0.setHorizontalTextPosition(SwingConstants.CENTER);
            editButton0.setVerticalTextPosition(SwingConstants.CENTER);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return editButton0;
}

private JButton getEditButton1()
{
    if (editButton1 == null) {
        try {
            editButton1 = new JButton();
            editButton1.setName("EditButton1");
            editButton1.setText("Edit");
            editButton1.setHorizontalTextPosition(SwingConstants.CENTER);
            editButton1.setVerticalTextPosition(SwingConstants.CENTER);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return editButton1;
}

private JButton getEditButton2()
{
    if (editButton2 == null) {
        try {
            editButton2 = new JButton();
            editButton2.setName("EditButton2");
            editButton2.setText("Edit");
            editButton2.setHorizontalTextPosition(SwingConstants.CENTER);
            editButton2.setVerticalTextPosition(SwingConstants.CENTER);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return editButton2;
}

private JButton getEditButton3()
{
    if (editButton3 == null) {
        try {
            editButton3 = new JButton();
            editButton3.setName("EditButton3");
            editButton3.setText("Edit");
            editButton3.setHorizontalTextPosition(SwingConstants.CENTER);
            editButton3.setVerticalTextPosition(SwingConstants.CENTER);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return editButton3;
}

private JButton getEditButton4()
{
    if (editButton4 == null) {
        try {
            editButton4 = new JButton();
            editButton4.setName("EditButton0");
            editButton4.setText("Edit");
            editButton4.setHorizontalTextPosition(SwingConstants.CENTER);
            editButton4.setVerticalTextPosition(SwingConstants.CENTER);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return editButton4;
}

private JButton getEditButton5()
{
    if (editButton5 == null) {
        try {
            editButton5 = new JButton();
            editButton5.setName("EditButton5");
            editButton5.setText("Edit");
            editButton5.setHorizontalTextPosition(SwingConstants.CENTER);
            editButton5.setVerticalTextPosition(SwingConstants.CENTER);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return editButton5;
}

private JButton getEditButton6()
{
    if (editButton6 == null) {
        try {
            editButton6 = new JButton();
            editButton6.setName("EditButton6");
            editButton6.setText("Edit");
            editButton6.setHorizontalTextPosition(SwingConstants.CENTER);
            editButton6.setVerticalTextPosition(SwingConstants.CENTER);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return editButton6;
}

private JButton getEditButton7()
{
    if (editButton7 == null) {
        try {
            editButton7 = new JButton();
            editButton7.setName("EditButton7");
            editButton7.setText("Edit");
            editButton7.setHorizontalTextPosition(SwingConstants.CENTER);
            editButton7.setVerticalTextPosition(SwingConstants.CENTER);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return editButton7;
}

private JButton getEditButton8()
{
    if (editButton8 == null) {
        try {
            editButton8 = new JButton();
            editButton8.setName("EditButton8");
            editButton8.setText("Edit");
            editButton8.setHorizontalTextPosition(SwingConstants.CENTER);
            editButton8.setVerticalTextPosition(SwingConstants.CENTER);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return editButton8;
}

private JButton getEditButton9(){
    if (editButton9 == null) {
        try {
            editButton9 = new JButton();
            editButton9.setName("EditButton9");
            editButton9.setText("Edit");
            editButton9.setHorizontalTextPosition(SwingConstants.CENTER);
            editButton9.setVerticalTextPosition(SwingConstants.CENTER);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return editButton9;
}

private JButton getEditButton10(){
    if (editButton10 == null) {
        try {
            editButton10 = new JButton();
            editButton10.setName("EditButton10");
            editButton10.setText("Edit");
            editButton10.setHorizontalTextPosition(SwingConstants.CENTER);
            editButton10.setVerticalTextPosition(SwingConstants.CENTER);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return editButton10;
}

private JLabel getImageLabel0()
{
    if (imageLabel0== null) {
        try {
            imageLabel0 = new JLabel();
            imageLabel0.setName("ImageLabel0");
            imageLabel0.setIcon(defaultIcon);
            imageLabel0.setHorizontalAlignment(SwingConstants.CENTER);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return imageLabel0;
}

private JLabel getImageLabel1()
{
    if (imageLabel1== null) {
        try {
            imageLabel1 = new JLabel();
            imageLabel1.setName("ImageLabel1");
            imageLabel1.setIcon(defaultIcon);
            imageLabel1.setHorizontalAlignment(SwingConstants.CENTER);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return imageLabel1;
}

private JLabel getImageLabel2()
{
    if (imageLabel2== null) {
        try {
            imageLabel2 = new JLabel();
            imageLabel2.setName("ImageLabel2");
            imageLabel2.setIcon(defaultIcon);
            imageLabel2.setHorizontalAlignment(SwingConstants.CENTER);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return imageLabel2;
}

private JLabel getImageLabel3()
{
    if (imageLabel3== null) {
        try {
            imageLabel3 = new JLabel();
            imageLabel3.setName("ImageLabel0");
            imageLabel3.setIcon(defaultIcon);
            imageLabel3.setHorizontalAlignment(SwingConstants.CENTER);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return imageLabel3;
}

private JLabel getImageLabel4()
{
    if (imageLabel4== null) {
        try {
            imageLabel4 = new JLabel();
            imageLabel4.setName("ImageLabel4");
            imageLabel4.setIcon(defaultIcon);
            imageLabel4.setHorizontalAlignment(SwingConstants.CENTER);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return imageLabel4;
}

private JLabel getImageLabel5()
{
    if (imageLabel5== null) {
        try {
            imageLabel5 = new JLabel();
            imageLabel5.setName("ImageLabel5");
            imageLabel5.setIcon(defaultIcon);
            imageLabel5.setHorizontalAlignment(SwingConstants.CENTER);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return imageLabel5;
}

private JLabel getImageLabel6()
{
    if (imageLabel6== null) {
        try {
            imageLabel6 = new JLabel();
            imageLabel6.setName("ImageLabel6");
            imageLabel6.setIcon(defaultIcon);
            imageLabel6.setHorizontalAlignment(SwingConstants.CENTER);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return imageLabel6;
}

private JLabel getImageLabel7()
{
    if (imageLabel7== null) {
        try {
            imageLabel7 = new JLabel();
            imageLabel7.setName("ImageLabel7");
            imageLabel7.setIcon(defaultIcon);
            imageLabel7.setHorizontalAlignment(SwingConstants.CENTER);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return imageLabel7;
}

private JLabel getImageLabel8()
{
    if (imageLabel8== null) {
        try {
            imageLabel8 = new JLabel();
            imageLabel8.setName("ImageLabel8");
            imageLabel8.setIcon(defaultIcon);
            imageLabel8.setHorizontalAlignment(SwingConstants.CENTER);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return imageLabel8;
}

private JLabel getImageLabel9()
{
    if (imageLabel9== null) {
        try {
            imageLabel9 = new JLabel();
            imageLabel9.setName("ImageLabel9");
            imageLabel9.setIcon(defaultIcon);
            imageLabel9.setHorizontalAlignment(SwingConstants.CENTER);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return imageLabel9;
}

private JLabel getImageLabel10()
{
    if (imageLabel10== null) {
        try {
            imageLabel10 = new JLabel();
            imageLabel10.setName("ImageLabel10");
            imageLabel10.setIcon(defaultIcon);
            imageLabel10.setHorizontalAlignment(SwingConstants.CENTER);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return imageLabel10;
}

private JButton getResetButton()
{
    if (resetButton == null) {
        try {
            resetButton = new JButton();
            resetButton.setName("resetButton");
            resetButton.setText("Reset");

        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return resetButton;
}

private ImageIcon scaleImage(ImageIcon img)
{
    return new ImageIcon(img.getImage().getScaledInstance(30,30, java.awt.Image.SCALE_AREA_AVERAGING ));
}
public void actionPerformed(ActionEvent e) 
{
    if(e.getSource() == getResetButton())
    {
        map.clear();
        LitePoint p = getPointSelectionPanel().getSelectedPoint();
        resetPreviewPanelImages(p);
    }else
    {
    JButton button = (JButton)e.getSource();
    
    Frame parent = SwingUtil.getParentFrame(StateImageEditorPanel.this);
    final javax.swing.JDialog d = new javax.swing.JDialog(parent);

    YukonImagePanel yPanel = new YukonImagePanel(null)
    {
        public void disposePanel()
        {
            d.setVisible(false);
        }
    };
    
    yPanel.addDataInputPanelListener( new DataInputPanelListener() {
        public void inputUpdate(PropertyPanelEvent event) {
            try {
                if(event.getID() == PropertyPanelEvent.EVENT_DB_INSERT) {                           
                    Transaction t = Transaction.createTransaction(Transaction.INSERT, (DBPersistent) event.getDataChanged());
                    DBPersistent img = t.execute();
                    if(img instanceof CTIDbChange) {
                        DBChangeMsg[] dbChange = DefaultDatabaseCache.getInstance().createDBChangeMessages(
                                    (CTIDbChange)img, DbChangeType.ADD);
                                  
                        for( int i = 0; i < dbChange.length; i++ )
                        {
                            //handle the DBChangeMsg locally
                            LiteBase lBase = DefaultDatabaseCache.getInstance().handleDBChangeMessage(dbChange[i]);
                            ConnPool.getInstance().getDefDispatchConn().write(dbChange[i]);
                        }
                    }   
                }
                else 
                if(event.getID() == PropertyPanelEvent.EVENT_DB_DELETE) {
                    Transaction t = Transaction.createTransaction(Transaction.DELETE, (DBPersistent) event.getDataChanged());
                    DBPersistent img = t.execute();

                    if(img instanceof CTIDbChange) {
                        DBChangeMsg[] dbChange = DefaultDatabaseCache.getInstance().createDBChangeMessages(
                                    (CTIDbChange)img, DbChangeType.DELETE);
                                  
                        for( int i = 0; i < dbChange.length; i++ )
                        {
                            //handle the DBChangeMsg locally
                            LiteBase lBase = DefaultDatabaseCache.getInstance().handleDBChangeMessage(dbChange[i]);
                            ConnPool.getInstance().getDefDispatchConn().write(dbChange[i]);
                        }
                    }   
                }
            }
            catch(com.cannontech.database.TransactionException te) 
            {
                CTILogger.error("Couldn't insert image", te);
            }
        }
    });
    
    d.setModal(true);
    d.getContentPane().add(yPanel);
    d.setSize(650, 500);

    d.setLocationRelativeTo(parent);

    d.show();
    
    if( yPanel.getReturnResult() == YukonImagePanel.OK_OPTION )
    {       
        LiteYukonImage img = yPanel.getSelectedLiteImage();
        if(img != null) 
        {
            ImageIcon icon = new javax.swing.ImageIcon(img.getImageValue());
            
            int state = 0;
            for (int i = 0; i < 11; i++)
            {
                if(button == buttons[i])
                {
                    state = i;
                    imageLabels[state].setIcon(scaleImage(icon));
                    map.put(state, img.getImageID());
                    break;
                }
            }
        }
    }
    }
}
}