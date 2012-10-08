package com.cannontech.esub.editor.element;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.data.lite.LiteYukonRole;
import com.cannontech.esub.editor.EditorPrefs;
import com.cannontech.esub.element.DrawingMetaElement;

/**
 * Creation date: (12/5/2002 4:16:03 PM)
 * @author: 
 */
public class DrawingMetaElementEditorPanel extends DataInputPanel {
    private JLabel ivjHeightLabel;
    private JLabel ivjHeightPixelLabel;
    private JLabel ivjJLabel1;
    private JPanel ivjJPanel1;
    private JPanel ivjJPanel2;
    private JPanel ivjJPanel3;
    private JLabel ivjRoleLabel;
    private JLabel ivjWidthLabel;
    private JLabel ivjWidthPixelLabel;
    private JComboBox ivjYukonRoleComboBox;

    private JSpinner widthSpinner;
    private JSpinner heightSpinner;
    private JButton colorButton;

    private DrawingMetaElement metaElement;

    /**
     * DrawingPropertiesPanel constructor comment.
     */
    public DrawingMetaElementEditorPanel() {
        super();
        initialize();
    }
    /**
     * Return the HeightLabel property value.
     * @return javax.swing.JLabel
     */
    private javax.swing.JLabel getHeightLabel() {
        if (ivjHeightLabel == null) {
            try {
                ivjHeightLabel = new JLabel();
                ivjHeightLabel.setName("HeightLabel");
                ivjHeightLabel.setText("Height:");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjHeightLabel;
    }
    /**
     * Return the HeightPixelLabel property value.
     * @return javax.swing.JLabel
     */
    private JLabel getHeightPixelLabel() {
        if (ivjHeightPixelLabel == null) {
            try {
                ivjHeightPixelLabel = new JLabel();
                ivjHeightPixelLabel.setName("HeightPixelLabel");
                ivjHeightPixelLabel.setText("pixels");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjHeightPixelLabel;
    }
    /**
     * Return the JLabel1 property value.
     * @return javax.swing.JLabel
     */
    private JLabel getJLabel1() {
        if (ivjJLabel1 == null) {
            try {
                ivjJLabel1 = new JLabel();
                ivjJLabel1.setName("JLabel1");
                ivjJLabel1.setText("");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJLabel1;
    }
    /**
     * Return the JPanel1 property value.
     * @return javax.swing.JPanel
     */
    private JPanel getJPanel1() {
        if (ivjJPanel1 == null) {
            try {
                com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
                ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
                ivjLocalBorder.setTitle("Size");
                ivjJPanel1 = new javax.swing.JPanel();
                ivjJPanel1.setName("JPanel1");
                ivjJPanel1.setBorder(ivjLocalBorder);
                ivjJPanel1.setLayout(new java.awt.GridBagLayout());

                GridBagConstraints constraintsWidthLabel = new GridBagConstraints();
                constraintsWidthLabel.gridx = 0; constraintsWidthLabel.gridy = 0;
                constraintsWidthLabel.anchor = GridBagConstraints.WEST;
                constraintsWidthLabel.insets = new Insets(4, 4, 4, 4);
                getJPanel1().add(getWidthLabel(), constraintsWidthLabel);

                java.awt.GridBagConstraints constraintsHeightLabel = new java.awt.GridBagConstraints();
                constraintsHeightLabel.gridx = 0; constraintsHeightLabel.gridy = 1;
                constraintsHeightLabel.anchor = java.awt.GridBagConstraints.WEST;
                constraintsHeightLabel.insets = new java.awt.Insets(4, 4, 4, 4);
                getJPanel1().add(getHeightLabel(), constraintsHeightLabel);

                java.awt.GridBagConstraints constraintsHeightTextField = new java.awt.GridBagConstraints();
                constraintsHeightTextField.gridx = 1; constraintsHeightTextField.gridy = 1;
                constraintsHeightTextField.anchor = java.awt.GridBagConstraints.WEST;
                constraintsHeightTextField.insets = new java.awt.Insets(4, 4, 4, 4);
                constraintsHeightTextField.fill = java.awt.GridBagConstraints.NONE;
                getJPanel1().add(getHeightSpinner(), constraintsHeightTextField);

                java.awt.GridBagConstraints constraintsHeightPixelLabel = new java.awt.GridBagConstraints();
                constraintsHeightPixelLabel.gridx = 2; constraintsHeightPixelLabel.gridy = 0;
                constraintsHeightPixelLabel.anchor = java.awt.GridBagConstraints.WEST;
                constraintsHeightPixelLabel.weightx = 1.0;
                constraintsHeightPixelLabel.insets = new java.awt.Insets(4, 4, 4, 4);
                getJPanel1().add(getHeightPixelLabel(), constraintsHeightPixelLabel);

                java.awt.GridBagConstraints constraintsWidthPixelLabel = new java.awt.GridBagConstraints();
                constraintsWidthPixelLabel.gridx = 2; constraintsWidthPixelLabel.gridy = 1;
                constraintsWidthPixelLabel.anchor = java.awt.GridBagConstraints.WEST;
                constraintsWidthPixelLabel.weightx = 1.0;
                constraintsWidthPixelLabel.insets = new java.awt.Insets(4, 4, 4, 4);
                getJPanel1().add(getWidthPixelLabel(), constraintsWidthPixelLabel);

                java.awt.GridBagConstraints constraintsWidthTextField = new java.awt.GridBagConstraints();
                constraintsWidthTextField.gridx = 1; constraintsWidthTextField.gridy = 0;
                constraintsWidthTextField.anchor = java.awt.GridBagConstraints.WEST;
                constraintsWidthTextField.insets = new java.awt.Insets(4, 4, 4, 4);
                constraintsWidthTextField.fill = java.awt.GridBagConstraints.NONE;
                getJPanel1().add(getWidthSpinner(), constraintsWidthTextField);
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJPanel1;
    }
    
    private JPanel getJPanel2() {
        if (ivjJPanel2 == null) {
            com.cannontech.common.gui.util.TitleBorder border = new com.cannontech.common.gui.util.TitleBorder(); 
            border.setTitle("Color");
            ivjJPanel2 = new JPanel();
            ivjJPanel2.setName("JPanel2");
            ivjJPanel2.setBorder(border);
            ivjJPanel2.setLayout(new GridBagLayout());
            
            GridBagConstraints constraintsLabel = new GridBagConstraints();
            constraintsLabel.gridx = 0; constraintsLabel.gridy = 0;
            constraintsLabel.anchor = GridBagConstraints.WEST;
            constraintsLabel.insets = new Insets(4, 4, 4, 4);
            ivjJPanel2.add(new JLabel("Background Color:"), constraintsLabel);
            
            constraintsLabel.gridx = 1; constraintsLabel.gridy = 0;
            ivjJPanel2.add(getColorButton(), constraintsLabel);
        }
        
        return ivjJPanel2;
    }
    
    private JButton getColorButton() {
        if (colorButton == null) {
            int defaultColorRGB = (getMetaElement() != null) ? 
                    getMetaElement().getDrawingRGBColor() : EditorPrefs.getPreferences().getDefaultDrawingRGBColor();
            Color defaultColor = new Color(defaultColorRGB);
            
            colorButton = new JButton();
            Dimension size = new Dimension(100, 25);
            colorButton.setBackground(defaultColor);
            colorButton.setPreferredSize(size);
            colorButton.setSize(size);
            colorButton.setMaximumSize(size);
            colorButton.setMinimumSize(size);
            colorButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Color color = JColorChooser.showDialog(ivjJPanel2, "Choose Background Color", Color.BLACK);
                    if (color !=null ) {
                        colorButton.setBackground(color);
                        colorButton.repaint();
                    }
                }
            });
        }
        return colorButton;
    }
    
    /**
     * Return the JPanel2 property value.
     * @return javax.swing.JPanel
     */
    private JPanel getJPanel3() {
        if (ivjJPanel3 == null) {
            try {
                com.cannontech.common.gui.util.TitleBorder ivjLocalBorder1;
                ivjLocalBorder1 = new com.cannontech.common.gui.util.TitleBorder();
                ivjLocalBorder1.setTitle("Security");
                ivjJPanel3 = new javax.swing.JPanel();
                ivjJPanel3.setName("JPanel3");
                ivjJPanel3.setBorder(ivjLocalBorder1);
                ivjJPanel3.setLayout(new java.awt.GridBagLayout());

                GridBagConstraints constraintsRoleLabel = new GridBagConstraints();
                constraintsRoleLabel.gridx = 0; constraintsRoleLabel.gridy = 0;
                constraintsRoleLabel.insets = new Insets(4, 4, 4, 4);
                getJPanel3().add(getRoleLabel(), constraintsRoleLabel);

                java.awt.GridBagConstraints constraintsYukonRoleComboBox = new java.awt.GridBagConstraints();
                constraintsYukonRoleComboBox.gridx = 1; constraintsYukonRoleComboBox.gridy = 0;
                constraintsYukonRoleComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
                constraintsYukonRoleComboBox.insets = new java.awt.Insets(4, 4, 4, 4);
                getJPanel3().add(getYukonRoleComboBox(), constraintsYukonRoleComboBox);

                java.awt.GridBagConstraints constraintsJLabel1 = new java.awt.GridBagConstraints();
                constraintsJLabel1.gridx = 2; constraintsJLabel1.gridy = 0;
                constraintsJLabel1.weightx = 1.0;
                constraintsJLabel1.insets = new java.awt.Insets(4, 4, 4, 4);
                getJPanel3().add(getJLabel1(), constraintsJLabel1);
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJPanel3;
    }
    /**
     * Return the RoleLabel property value.
     * @return javax.swing.JLabel
     */
    private javax.swing.JLabel getRoleLabel() {
        if (ivjRoleLabel == null) {
            try {
                ivjRoleLabel = new javax.swing.JLabel();
                ivjRoleLabel.setName("RoleLabel");
                ivjRoleLabel.setText("Yukon Role:");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjRoleLabel;
    }
    /**
     * Return the WidthLabel property value.
     * @return javax.swing.JLabel
     */
    private javax.swing.JLabel getWidthLabel() {
        if (ivjWidthLabel == null) {
            try {
                ivjWidthLabel = new javax.swing.JLabel();
                ivjWidthLabel.setName("WidthLabel");
                ivjWidthLabel.setText("Width:");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjWidthLabel;
    }
    /**
     * Return the WidthPixelLabel property value.
     * @return javax.swing.JLabel
     */
    private javax.swing.JLabel getWidthPixelLabel() {
        if (ivjWidthPixelLabel == null) {
            try {
                ivjWidthPixelLabel = new javax.swing.JLabel();
                ivjWidthPixelLabel.setName("WidthPixelLabel");
                ivjWidthPixelLabel.setText("pixels");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjWidthPixelLabel;
    }

    private JSpinner getWidthSpinner() {
        if (widthSpinner == null) {
            try {
                SpinnerNumberModel model = new SpinnerNumberModel(1000,1,Integer.MAX_VALUE,1);
                widthSpinner = new JSpinner(model);
                widthSpinner.setName("WidthSpinner");	
                widthSpinner.setPreferredSize(new Dimension(75,24));					
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return widthSpinner;
    }

    private JSpinner getHeightSpinner() {
        if (heightSpinner == null) {
            try {
                SpinnerNumberModel model = new SpinnerNumberModel(600,1,Integer.MAX_VALUE,1);
                heightSpinner = new JSpinner(model);
                heightSpinner.setName("WidthSpinner");		
                heightSpinner.setPreferredSize(new Dimension(75,24));				
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return heightSpinner;
    }

    /**
     * Return the YukonRoleComboBox property value.
     * @return javax.swing.JComboBox
     */
    private javax.swing.JComboBox getYukonRoleComboBox() {
        if (ivjYukonRoleComboBox == null) {
            try {
                ivjYukonRoleComboBox = new javax.swing.JComboBox();
                ivjYukonRoleComboBox.setName("YukonRoleComboBox");
                Iterator i = DaoFactory.getAuthDao().getRoles("eSubstation").iterator();
                while(i.hasNext()) {
                    LiteYukonRole r = (LiteYukonRole) i.next();
                    ivjYukonRoleComboBox.addItem(r);
                }
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjYukonRoleComboBox;
    }
    /**
     * Called whenever the part throws an exception.
     * @param exception java.lang.Throwable
     */
    private void handleException(java.lang.Throwable exception) {
        CTILogger.error(exception);
    }
    /**
     * Initialize the class.
     */
    private void initialize() {
        try {
            setName("DrawingPropertiesPanel");
            setLayout(new java.awt.GridBagLayout());
            setSize(276, 228);

            java.awt.GridBagConstraints constraintsJPanel1 = new java.awt.GridBagConstraints();
            constraintsJPanel1.gridx = 0; constraintsJPanel1.gridy = 0;
            constraintsJPanel1.gridwidth = 2;
            constraintsJPanel1.fill = java.awt.GridBagConstraints.BOTH;
            constraintsJPanel1.weightx = 1.0;
            constraintsJPanel1.weighty = 1.0;
            constraintsJPanel1.insets = new java.awt.Insets(4, 4, 4, 4);
            add(getJPanel1(), constraintsJPanel1);

            java.awt.GridBagConstraints constraintsJPanel2 = new java.awt.GridBagConstraints();
            constraintsJPanel2.gridx = 0; constraintsJPanel2.gridy = 1;
            constraintsJPanel2.gridwidth = 2;
            constraintsJPanel2.fill = java.awt.GridBagConstraints.BOTH;
            constraintsJPanel2.weightx = 1.0;
            constraintsJPanel2.weighty = 1.0;
            constraintsJPanel2.insets = new java.awt.Insets(4, 4, 4, 4);
            add(getJPanel2(), constraintsJPanel2);
            
            java.awt.GridBagConstraints constraintsJPanel3 = new java.awt.GridBagConstraints();
            constraintsJPanel3.gridx = 1; constraintsJPanel3.gridy = 2;
            constraintsJPanel3.fill = java.awt.GridBagConstraints.BOTH;
            constraintsJPanel3.weightx = 1.0;
            constraintsJPanel3.weighty = 1.0;
            constraintsJPanel3.insets = new java.awt.Insets(4, 4, 4, 4);
            add(getJPanel3(), constraintsJPanel3);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    /**
     * main entrypoint - starts the part when it is run as an application
     * @param args java.lang.String[]
     */ 
    public static void main(java.lang.String[] args) {
        try {
            CtiUtilities.setLaF();
            javax.swing.JFrame frame = new javax.swing.JFrame();
            DrawingMetaElementEditorPanel aDrawingPropertiesPanel;
            aDrawingPropertiesPanel = new DrawingMetaElementEditorPanel(); 
            frame.setContentPane(aDrawingPropertiesPanel);
            frame.setSize(aDrawingPropertiesPanel.getSize());
            frame.addWindowListener(new java.awt.event.WindowAdapter() {

                @Override
                public void windowClosing(java.awt.event.WindowEvent e) {
                    System.exit(0);
                };
            });
            java.awt.Insets insets = frame.getInsets();
            frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
            frame.setVisible(true);
        } catch (Throwable exception) {
            System.err.println("Exception occurred in main() of javax.swing.JPanel");
            exception.printStackTrace(System.out);
        }
    }
    /**
     * @see com.cannontech.common.gui.util.DataInputPanel#getValue(Object)
     */
    @Override
    public Object getValue(Object o) {
        DrawingMetaElement e = (DrawingMetaElement) o;
        if(e == null)
            e = getMetaElement();

        e.setDrawingWidth(((Integer)getWidthSpinner().getValue()).intValue());
        e.setDrawingHeight(((Integer)getHeightSpinner().getValue()).intValue());
        e.setDrawingRgbColor(getColorButton().getBackground().getRGB());
        
        Object selected = getYukonRoleComboBox().getSelectedItem();
        if(selected != null) {
            e.setRoleID(((LiteYukonRole)selected).getRoleID());
        }
        return e;			
    }

    /**
     * @see com.cannontech.common.gui.util.DataInputPanel#setValue(Object)
     */
    @Override
    public void setValue(Object o) {
        DrawingMetaElement e = (DrawingMetaElement) o;
        setMetaElement(e);
        getWidthSpinner().setValue(new Integer(e.getDrawingWidth()));
        getHeightSpinner().setValue(new Integer(e.getDrawingHeight()));
        getColorButton().setBackground(new Color(e.getDrawingRGBColor()));
        getYukonRoleComboBox().setSelectedItem(DaoFactory.getAuthDao().getRole(e.getRoleID()));		
    }

    /**
     * Returns the metaElement.
     * @return DrawingMetaElement
     */
    public DrawingMetaElement getMetaElement() {
        return metaElement;
    }

    /**
     * Sets the metaElement.
     * @param metaElement The metaElement to set
     */
    public void setMetaElement(DrawingMetaElement metaElement) {
        this.metaElement = metaElement;
    }

}
