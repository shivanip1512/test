package com.cannontech.loadcontrol.gui.manualentry;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.cannontech.dr.service.DemandResponseService;
import com.cannontech.spring.YukonSpringHook;

public class TargetCycleConfigPanel extends JPanel implements ActionListener{
    Date startTime = null;
    Date stopTime = null;
    private JTextField[] fields = null;
    private JLabel[] labels = null;
    private JButton okButton = null;
    private JButton cancelButton = null;
    private int timeSlots = -1;
    private JPanel okCancelPanel = null;
    private String additonalInfo = null;
    
    public TargetCycleConfigPanel(Date start_, Date stop_) {
        super();
        startTime = start_;
        stopTime = stop_;
        initPanel();
    }

    public TargetCycleConfigPanel() {
        super();
        
    }

    private void initPanel() {


        
        setName("TargetCycleConfigPanel");
        setLayout(new java.awt.GridBagLayout());
        setMaximumSize(new java.awt.Dimension(315, 260));
        setPreferredSize(new java.awt.Dimension(315, 260));
        setBounds(new java.awt.Rectangle(0, 0, 300, 234));
        setSize(577, 289);
        setMinimumSize(new java.awt.Dimension(315, 260));
        
     
        java.awt.GridBagConstraints constraintsJTextGearAdjustLabels = new java.awt.GridBagConstraints();
        constraintsJTextGearAdjustLabels.gridx = 1; constraintsJTextGearAdjustLabels.gridy = 2;
        constraintsJTextGearAdjustLabels.gridwidth = 3;
        constraintsJTextGearAdjustLabels.anchor = java.awt.GridBagConstraints.WEST;
        constraintsJTextGearAdjustLabels.ipadx = 3;
        constraintsJTextGearAdjustLabels.insets = new java.awt.Insets(2, 5, 4, 6);
        
        JLabel timeFrmHdr = createHdrLabel("Time Frame");            
        this.add (timeFrmHdr, constraintsJTextGearAdjustLabels);
        constraintsJTextGearAdjustLabels.gridy ++;

        
        JLabel[] labels = getLabels();
        for (int i = 0; i < labels.length; i++) {
            JLabel label = getLabels()[i];
            label.setVisible(true);            
            this.add (label, constraintsJTextGearAdjustLabels);    
            constraintsJTextGearAdjustLabels.gridy ++;
            
        }

                  
        java.awt.GridBagConstraints constraintsJTextGearAdjustFields = new java.awt.GridBagConstraints();
        constraintsJTextGearAdjustFields.gridx = 5; constraintsJTextGearAdjustFields.gridy = 2;
        constraintsJTextGearAdjustFields.weightx = 1.0;
        constraintsJTextGearAdjustFields.anchor = java.awt.GridBagConstraints.WEST;
        constraintsJTextGearAdjustFields.fill = java.awt.GridBagConstraints.HORIZONTAL;
        constraintsJTextGearAdjustFields.ipadx = 10;
        constraintsJTextGearAdjustFields.insets = new java.awt.Insets(2, 2, 3, 1);
        
        JLabel trgtAdjHdr = createHdrLabel ("Adjustment(0%-250%)");
        this.add (trgtAdjHdr, constraintsJTextGearAdjustFields);    
        constraintsJTextGearAdjustFields.gridy ++;
        
        JTextField[] fields = getInputFields();
        for (int i = 0; i < fields.length; i++) {
            JTextField field = fields[i];
            this.add(field, constraintsJTextGearAdjustFields);
            constraintsJTextGearAdjustFields.gridy ++;
        }
        
        java.awt.GridBagConstraints constraintsJPanelOkCancel = new java.awt.GridBagConstraints();
        constraintsJPanelOkCancel.gridx = 1; constraintsJPanelOkCancel.gridy = getTimeSlots() + 3;
        constraintsJPanelOkCancel.gridwidth = 2;
        constraintsJPanelOkCancel.fill = java.awt.GridBagConstraints.BOTH;
        constraintsJPanelOkCancel.weightx = 1.0;
        constraintsJPanelOkCancel.weighty = 1.0;
        constraintsJPanelOkCancel.ipadx = 0;
        constraintsJPanelOkCancel.insets = new java.awt.Insets(1, 0, 0, 0);
        add(getOkCancelPanel(), constraintsJPanelOkCancel);
        getCancelButton().addActionListener(this);
        getOkButton().addActionListener(this);
        

    }

    private JLabel createHdrLabel(String text) {
        JLabel timeFrmHdr = new JLabel();
        timeFrmHdr.setFont(new Font (null, Font.BOLD, 12));
        timeFrmHdr.setText(text);
        timeFrmHdr.setToolTipText(text);
        timeFrmHdr.setMaximumSize(new java.awt.Dimension(50, 22));
        timeFrmHdr.setMinimumSize(new java.awt.Dimension(50, 22));
        timeFrmHdr.setVisible(true);
        return timeFrmHdr;
    }

    private Component getOkCancelPanel() {
        if (okCancelPanel  == null) {
            okCancelPanel = new javax.swing.JPanel();
            okCancelPanel.setName("JPanelOkCancel");
            okCancelPanel.setLayout(getJPanelOkCancelFlowLayout());
          
            okCancelPanel.setMinimumSize(new java.awt.Dimension(161, 35));
            okCancelPanel.add(getOkButton(), getOkButton().getName());
            okCancelPanel.add(getCancelButton(), getCancelButton().getName());
            }
        return okCancelPanel;
    }

    public int getTimeSlots() {
        if (timeSlots < 0 ) {
            timeSlots = YukonSpringHook.getBean(DemandResponseService.class).getTimeSlotsForTargetCycle(stopTime, startTime);
        }
        return timeSlots;
    }
    
    private JTextField[] getInputFields() {
        if (fields  == null) {
            try {
                fields = new JTextField[getTimeSlots()];
                for (int i = 0; i < getTimeSlots(); i++) {
                    final JTextField adjustment = new JTextField();
                    adjustment.setName("JCheckBoxTargetAdjust_" + i);
                    adjustment.setToolTipText("Enter the number from 0 to 250");
                    adjustment.setMaximumSize(new java.awt.Dimension(10, 22));
                    adjustment.setActionCommand("Target Adjust");
                    adjustment.setMinimumSize(new java.awt.Dimension(10, 22));
                    adjustment.setText("100");
                    JTextFieldRangeListener from0to250 = new JTextFieldRangeListener (adjustment, 0, 250);
                    from0to250.setControl(getOkButton());
                    adjustment.addFocusListener(from0to250);
                    fields [i] = adjustment;
                }
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                
            }
        }
        return fields;
    }
    
    private JLabel[] getLabels() {
        if (labels   == null) {
            try {
                labels = new JLabel[getTimeSlots()];
                for (int i = 0; i < getTimeSlots(); i++) {
                    JLabel label = new JLabel();
                    label.setName("JCheckBoxTargetAdjust_" + i);
                    Calendar c = GregorianCalendar.getInstance();
                    c.setTime(startTime);
                    int hours = c.get(Calendar.HOUR_OF_DAY) + i;
                    String labelString = (hours)+ ":00-" +(hours + 1)+ ":00";
                    label.setText(labelString);
                    label.setToolTipText("Enter the number from 0 to 250");
                    label.setMaximumSize(new java.awt.Dimension(50, 22));
                    label.setMinimumSize(new java.awt.Dimension(50, 22));
                    labels [i] = label;
                }
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {

            }
        }
        return labels;
    }


    public void actionPerformed(ActionEvent e) {
        
        if (e.getSource() == getCancelButton()) 
        {
            exit();
            return;
        }
        if (e.getSource() == getOkButton()) 
        {
            int wrong = 0;
            String addInfo = "adjustments ";
            //check to see that input is valid
            JTextField[] f = getInputFields();
            for (int i = 0; i < f.length; i++) {
                JTextField field = f[i];
                String s = field.getText();
                if (!s.equalsIgnoreCase(""))
                {
                    addInfo += s + " ";
                }
                else
                {
                    field.setBackground(Color.RED);
                    wrong ++;
                }

            }
            if (wrong > 0) 
            {
                getOkButton().setEnabled(false);
                addInfo = null;
            }
            else 
            {
                additonalInfo = addInfo;
                exit();
                return;
            }
        }
    }

    public JButton getCancelButton() {
        if (cancelButton == null) {
            cancelButton = new JButton();
            cancelButton.setName("JButtonCancel");
            cancelButton.setMnemonic(67);
            cancelButton.setText("Cancel");
            cancelButton.setMaximumSize(new java.awt.Dimension(73, 25));
            cancelButton.setActionCommand("Cancel");
            cancelButton.setMinimumSize(new java.awt.Dimension(73, 25));
            
        }
        return cancelButton;
    }

    public JButton getOkButton() {
        if (okButton == null) {
            okButton = new javax.swing.JButton();
            okButton.setName("JButtonOk");
            okButton.setMnemonic(79);
            okButton.setMaximumSize(new java.awt.Dimension(73, 25));
            okButton.setPreferredSize(new java.awt.Dimension(73, 25));
            okButton.setMinimumSize(new java.awt.Dimension(73, 25));
            okButton.setMargin(new java.awt.Insets(2, 14, 2, 14));
            // user code begin {1}

            okButton.setText("Submit");
        }
        return okButton;
    }
    private java.awt.FlowLayout getJPanelOkCancelFlowLayout() {
        java.awt.FlowLayout layout = null;
    
            /* Create part */
            layout = new java.awt.FlowLayout();
            layout.setAlignment(java.awt.FlowLayout.CENTER);
            layout.setVgap(5);
            layout.setHgap(5);
     
        return layout;
    }
    
    public void exit() {}

    public String getAdditionalInfo() {
           return additonalInfo;
    }

    public void setAdditonalInfo(String additonalInfo) {
        this.additonalInfo = additonalInfo;
    }
}
