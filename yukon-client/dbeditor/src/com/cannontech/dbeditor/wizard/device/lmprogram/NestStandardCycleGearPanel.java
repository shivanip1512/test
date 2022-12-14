package com.cannontech.dbeditor.wizard.device.lmprogram;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import com.cannontech.database.data.device.lm.NestStandardCycleGear;
import com.cannontech.dr.nest.model.v3.PeakLoadShape;
import com.cannontech.dr.nest.model.v3.PostLoadShape;
import com.cannontech.dr.nest.model.v3.PrepLoadShape;
import com.klg.jclass.util.value.JCValueEvent;

public class NestStandardCycleGearPanel extends GenericGearPanel {

    private static final long serialVersionUID = 1L;

    private JComboBox<String> ivjJComboBoxPrepLoadShape = null;
    private JComboBox<String> ivjJComboBoxPeakLoadShape = null;
    private JComboBox<String> ivjJComboBoxPostLoadShape = null;
    private JLabel loadShapingPreparationLabel;
    private JLabel loadShapingPeakLabel;
    private JLabel loadShapingPostLabel;
    private JLabel loadShapingDescLabel;
    private JButton helpPreparationButton;
    private JButton helpPeakButton;
    private JButton helpPostButton;

    public NestStandardCycleGearPanel() {
        initialize();
    }
    
    private JButton getJButtonHelpPreparation() {
        if (helpPreparationButton == null) {
            try {
                helpPreparationButton = new JButton();
                helpPreparationButton.setName("JButtonHelp");
                helpPreparationButton.setText("?");
                helpPreparationButton.setAlignmentY(TOP_ALIGNMENT);
                helpPreparationButton.setMaximumSize(new Dimension(20, 20));
                helpPreparationButton.setPreferredSize(new Dimension(20, 20));
                helpPreparationButton.setFont(new Font("dialog", 0, 12));
                helpPreparationButton.setAlignmentX(LEFT_ALIGNMENT);
                helpPreparationButton.setMinimumSize(new Dimension(20, 20));
                helpPreparationButton.setEnabled(false);
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return helpPreparationButton;
    }
    
    private JButton getJButtonHelpPeak() {
        if (helpPeakButton == null) {
            try {
                helpPeakButton = new JButton();
                helpPeakButton.setName("JButtonHelp");
                helpPeakButton.setText("?");
                helpPeakButton.setAlignmentY(TOP_ALIGNMENT);
                helpPeakButton.setMaximumSize(new Dimension(20, 20));
                helpPeakButton.setPreferredSize(new Dimension(20, 20));
                helpPeakButton.setFont(new Font("dialog", 0, 12));
                helpPeakButton.setAlignmentX(LEFT_ALIGNMENT);
                helpPeakButton.setMinimumSize(new Dimension(20, 20));
                helpPeakButton.setEnabled(false);
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return helpPeakButton;
    }
    
    private JButton getJButtonHelpPost() {
        if (helpPostButton == null) {
            try {
                helpPostButton = new JButton();
                helpPostButton.setName("JButtonHelp");
                helpPostButton.setText("?");
                helpPostButton.setAlignmentY(TOP_ALIGNMENT);
                helpPostButton.setMaximumSize(new Dimension(20, 20));
                helpPostButton.setPreferredSize(new Dimension(20, 20));
                helpPostButton.setFont(new Font("dialog", 0, 12));
                helpPostButton.setAlignmentX(LEFT_ALIGNMENT);
                helpPostButton.setMinimumSize(new Dimension(20, 20));
                helpPostButton.setEnabled(false);
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return helpPostButton;
    }

    private JLabel getJLabelPrepLoadShape() {
        if (loadShapingPreparationLabel == null) {
            try {
                loadShapingPreparationLabel = new JLabel();
                loadShapingPreparationLabel.setName("JLabelPrepLoadShape");
                loadShapingPreparationLabel.setAlignmentY(TOP_ALIGNMENT);
                loadShapingPreparationLabel.setText("Preparation Load Shaping:");
                loadShapingPreparationLabel.setMaximumSize(new Dimension(150, 18));
                loadShapingPreparationLabel.setPreferredSize(new Dimension(150, 18));
                loadShapingPreparationLabel.setFont(new Font("dialog", 0, 12));
                loadShapingPreparationLabel.setAlignmentX(LEFT_ALIGNMENT);
                loadShapingPreparationLabel.setMinimumSize(new Dimension(112, 14));
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return loadShapingPreparationLabel;
    }

    private JLabel getJLabelPeakLoadShape() {
        if (loadShapingPeakLabel == null) {
            try {
                loadShapingPeakLabel = new JLabel();
                loadShapingPeakLabel.setName("JLabelPeakLoadShape");
                loadShapingPeakLabel.setAlignmentY(TOP_ALIGNMENT);
                loadShapingPeakLabel.setText("Peak Load Shaping:");
                loadShapingPeakLabel.setMaximumSize(new Dimension(150, 18));
                loadShapingPeakLabel.setPreferredSize(new Dimension(150, 18));
                loadShapingPeakLabel.setFont(new Font("dialog", 0, 12));
                loadShapingPeakLabel.setAlignmentX(LEFT_ALIGNMENT);
                loadShapingPeakLabel.setMinimumSize(new Dimension(112, 14));
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return loadShapingPeakLabel;
    }

    private JLabel getJLabelPostLoadShape() {
        if (loadShapingPostLabel == null) {
            try {
                loadShapingPostLabel = new JLabel();
                loadShapingPostLabel.setName("JLabelPostLoadShape");
                loadShapingPostLabel.setAlignmentY(TOP_ALIGNMENT);
                loadShapingPostLabel.setText("Post Peak Load Shaping:");
                loadShapingPostLabel.setMaximumSize(new Dimension(150, 18));
                loadShapingPostLabel.setPreferredSize(new Dimension(150, 18));
                loadShapingPostLabel.setFont(new Font("dialog", 0, 12));
                loadShapingPostLabel.setAlignmentX(LEFT_ALIGNMENT);
                loadShapingPostLabel.setMinimumSize(new Dimension(112, 14));
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return loadShapingPostLabel;
    }

    private JComboBox<String> getJComboBoxPrepLoadShape() {
        if (ivjJComboBoxPrepLoadShape == null) {
            try {
                ivjJComboBoxPrepLoadShape = new JComboBox<String>();
                ivjJComboBoxPrepLoadShape.setName("JComboBoxPrepLoadShape");
                ivjJComboBoxPrepLoadShape.setPreferredSize(new java.awt.Dimension(50, 23));
                ivjJComboBoxPrepLoadShape.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
                ivjJComboBoxPrepLoadShape.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
                ivjJComboBoxPrepLoadShape.addItem(PrepLoadShape.PREP_STANDARD.getDisplayText());
                ivjJComboBoxPrepLoadShape.setEnabled(false);
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJComboBoxPrepLoadShape;
    }

    private JComboBox<String> getJComboBoxPeakLoadShape() {
        if (ivjJComboBoxPeakLoadShape == null) {
            try {
                ivjJComboBoxPeakLoadShape = new JComboBox<String>();
                ivjJComboBoxPeakLoadShape.setName("JComboBoxPeakLoadShape");
                ivjJComboBoxPeakLoadShape.setPreferredSize(new java.awt.Dimension(50, 23));
                ivjJComboBoxPeakLoadShape.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
                ivjJComboBoxPeakLoadShape.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
                ivjJComboBoxPeakLoadShape.addItem(PeakLoadShape.PEAK_STANDARD.getDisplayText());
                ivjJComboBoxPeakLoadShape.setEnabled(false);
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJComboBoxPeakLoadShape;
    }

    private JComboBox<String> getJComboBoxPostLoadShape() {
        if (ivjJComboBoxPostLoadShape == null) {
            try {
                ivjJComboBoxPostLoadShape = new JComboBox<String>();
                ivjJComboBoxPostLoadShape.setName("JComboBoxPostLoadShape");
                ivjJComboBoxPostLoadShape.setPreferredSize(new java.awt.Dimension(50, 23));
                ivjJComboBoxPostLoadShape.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
                ivjJComboBoxPostLoadShape.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
                ivjJComboBoxPostLoadShape.addItem(PostLoadShape.POST_STANDARD.getDisplayText());
                ivjJComboBoxPostLoadShape.setEnabled(false);
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJComboBoxPostLoadShape;
    }

    private JLabel getJLabelLoadShapingDesc() {
        if (loadShapingDescLabel == null) {
            try {
                loadShapingDescLabel = new JLabel();
                loadShapingDescLabel.setName("JLabelPrepLoadShapeDesc");
                loadShapingDescLabel.setAlignmentY(TOP_ALIGNMENT);
                loadShapingDescLabel.setMaximumSize(new Dimension(1000, 190));
                loadShapingDescLabel.setPreferredSize(new Dimension(1000, 190));
                loadShapingDescLabel.setFont(new Font("dialog", 0, 12));
                loadShapingDescLabel.setAlignmentX(LEFT_ALIGNMENT);
                loadShapingDescLabel.setMinimumSize(new Dimension(1000, 200));
                String desc;
                desc = "<html><p><b>Nest Standard Cycle Gear</b><br>"
                        + "The Nest standard cycle gear operates in the following manner.<br>"
                        + "In the hours leading up to the event, the thermostat will decrease<br>"
                        + "or increase the temperature set point in preparation for the requested<br>"
                        + "control event. Maximum load reduction occurs at the start of the event<br>"
                        + "and gradually decreases as the event progresses. At event stop time, the<br>"
                        + "thermostat returns to their programmed behaviors. </p></html>";
                loadShapingDescLabel.setText(desc);
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return loadShapingDescLabel;
    }

    @Override
    public Object getValue(Object gearObj) {
        NestStandardCycleGear gear = (NestStandardCycleGear) gearObj;
        String prepString = (String) getJComboBoxPrepLoadShape().getSelectedItem();
        String peakString = (String) getJComboBoxPeakLoadShape().getSelectedItem();
        String postString = (String) getJComboBoxPostLoadShape().getSelectedItem();
        gear.setLoadShapingOptions(PrepLoadShape.getFromNameMap(prepString), 
                                   PeakLoadShape.getFromNameMap(peakString),
                                   PostLoadShape.getFromNameMap(postString));
        return gear;
    }

    private void handleException(Throwable exception) {
        System.out.print(exception.getMessage());
        System.out.println("--------- UNCAUGHT EXCEPTION ---------");
        exception.printStackTrace(System.out);
    }

    private void initConnections() {
        getJComboBoxPrepLoadShape().addActionListener(this);
        getJComboBoxPeakLoadShape().addActionListener(this);
        getJComboBoxPostLoadShape().addActionListener(this);
        getJButtonHelpPreparation().addActionListener(this);
        getJButtonHelpPeak().addActionListener(this);
        getJButtonHelpPost().addActionListener(this);
    }

    private void initialize() {
        try {
            setName("NestStandardCycleGearPanel");

            GridBagConstraints constraintJLabelPrepLoadShape = new GridBagConstraints();
            GridBagConstraints constraintJComboBoxPrepLoadShape = new java.awt.GridBagConstraints();

            GridBagConstraints constraintJLabelPeakLoadShapeLabel = new GridBagConstraints();
            GridBagConstraints constraintJComboBoxPeakLoadShape = new java.awt.GridBagConstraints();

            GridBagConstraints constraintJLabelPostLoadShapeLabel = new GridBagConstraints();
            GridBagConstraints constraintJComboBoxPostLoadShape = new GridBagConstraints();

            GridBagConstraints constraintJLabelLoadShapingDesc = new GridBagConstraints();
            
            GridBagConstraints constraintJButtonHelpPreparation = new GridBagConstraints();
            GridBagConstraints constraintJButtonHelpPeak = new GridBagConstraints();
            GridBagConstraints constraintJButtonHelpPost = new GridBagConstraints();

            constraintJLabelPrepLoadShape.insets = new Insets(20, 20, 5, 5);
            constraintJLabelPrepLoadShape.ipady = -3;
            constraintJLabelPrepLoadShape.ipadx = 200;
            constraintJLabelPrepLoadShape.gridwidth = 3;
            constraintJLabelPrepLoadShape.weightx = 5.0;
            constraintJLabelPrepLoadShape.anchor = GridBagConstraints.WEST;
            constraintJLabelPrepLoadShape.gridy = 1;
            constraintJLabelPrepLoadShape.gridx = 1;

            constraintJComboBoxPrepLoadShape.insets = new Insets(20, 20, 5, 5);
            constraintJComboBoxPrepLoadShape.ipady = -3;
            constraintJComboBoxPrepLoadShape.ipadx = 50;
            constraintJComboBoxPrepLoadShape.gridwidth = 3;
            constraintJComboBoxPrepLoadShape.anchor = GridBagConstraints.WEST;
            constraintJComboBoxPrepLoadShape.gridy = 1;
            constraintJComboBoxPrepLoadShape.gridx = 2;
            
            constraintJButtonHelpPreparation.insets = new Insets(20, 20, 5, 5);
            constraintJButtonHelpPreparation.ipady = -2;
            constraintJButtonHelpPreparation.ipadx = 20;
            constraintJButtonHelpPreparation.gridwidth = 3;
            constraintJButtonHelpPreparation.anchor = GridBagConstraints.WEST;
            constraintJButtonHelpPreparation.gridy = 1;
            constraintJButtonHelpPreparation.gridx = 3;

            constraintJLabelPeakLoadShapeLabel.insets = new Insets(0, 20, 5, 5);
            constraintJLabelPrepLoadShape.gridwidth = 3;
            constraintJLabelPeakLoadShapeLabel.anchor = GridBagConstraints.WEST;
            constraintJLabelPeakLoadShapeLabel.gridy = 2;
            constraintJLabelPeakLoadShapeLabel.gridx = 1;

            constraintJComboBoxPeakLoadShape.insets = new Insets(0, 20, 5, 5);
            constraintJComboBoxPeakLoadShape.ipady = -3;
            constraintJComboBoxPeakLoadShape.ipadx = 50;
            constraintJLabelPrepLoadShape.gridwidth = 3;
            constraintJComboBoxPeakLoadShape.anchor = GridBagConstraints.WEST;
            constraintJComboBoxPeakLoadShape.gridy = 2;
            constraintJComboBoxPeakLoadShape.gridx = 2;
            
            constraintJButtonHelpPeak.insets = new Insets(0, 20, 5, 5);
            constraintJButtonHelpPeak.ipady = -2;
            constraintJButtonHelpPeak.ipadx = 20;
            constraintJButtonHelpPeak.gridwidth = 3;
            constraintJButtonHelpPeak.anchor = GridBagConstraints.WEST;
            constraintJButtonHelpPeak.gridy = 2;
            constraintJButtonHelpPeak.gridx = 3;

            constraintJLabelPostLoadShapeLabel.insets = new Insets(0, 20, 5, 5);
            constraintJLabelPrepLoadShape.gridwidth = 3;
            constraintJLabelPostLoadShapeLabel.anchor = GridBagConstraints.WEST;
            constraintJLabelPostLoadShapeLabel.gridy = 3;
            constraintJLabelPostLoadShapeLabel.gridx = 1;

            constraintJComboBoxPostLoadShape.insets = new Insets(0, 20, 5, 5);
            constraintJComboBoxPostLoadShape.ipady = -3;
            constraintJComboBoxPostLoadShape.ipadx = 50;
            constraintJComboBoxPostLoadShape.gridwidth = 3;
            constraintJComboBoxPostLoadShape.anchor = GridBagConstraints.WEST;
            constraintJComboBoxPostLoadShape.gridy = 3;
            constraintJComboBoxPostLoadShape.gridx = 2;
            
            
            constraintJButtonHelpPost.insets = new Insets(0, 20, 5, 5);
            constraintJButtonHelpPost.ipady = -2;
            constraintJButtonHelpPost.ipadx = 20;
            constraintJButtonHelpPost.gridwidth = 3;
            constraintJButtonHelpPost.anchor = GridBagConstraints.WEST;
            constraintJButtonHelpPost.gridy = 3;
            constraintJButtonHelpPost.gridx = 3;

            constraintJLabelLoadShapingDesc.insets = new Insets(0, 20, 5, 5);
            constraintJLabelLoadShapingDesc.ipady = -3;
            constraintJLabelLoadShapingDesc.ipadx = 50;
            constraintJLabelLoadShapingDesc.gridwidth = 3;
            constraintJLabelLoadShapingDesc.anchor = GridBagConstraints.WEST;
            constraintJLabelLoadShapingDesc.gridy = 4;
            constraintJLabelLoadShapingDesc.gridx = 1;

            setLayout(new GridBagLayout());
            this.add(getJLabelPrepLoadShape(), constraintJLabelPrepLoadShape);
            this.add(getJComboBoxPrepLoadShape(), constraintJComboBoxPrepLoadShape);

            this.add(getJLabelPeakLoadShape(), constraintJLabelPeakLoadShapeLabel);
            this.add(getJComboBoxPeakLoadShape(), constraintJComboBoxPeakLoadShape);

            this.add(getJLabelPostLoadShape(), constraintJLabelPostLoadShapeLabel);
            this.add(getJComboBoxPostLoadShape(), constraintJComboBoxPostLoadShape);

            this.add(getJLabelLoadShapingDesc(), constraintJLabelLoadShapingDesc);
            this.add(getJButtonHelpPreparation(), constraintJButtonHelpPreparation);
            this.add(getJButtonHelpPeak(), constraintJButtonHelpPeak);
            this.add(getJButtonHelpPost(), constraintJButtonHelpPost);
        } catch (Throwable ivjExc) {
            handleException(ivjExc);
        }
        initConnections();
    }

    @Override
    public void setValue(Object gearObj) {
        if (gearObj == null) {
            return;
        }
        NestStandardCycleGear gear = (NestStandardCycleGear) gearObj;
        getJComboBoxPrepLoadShape().setSelectedItem(gear.getPrepLoadShape());
        getJComboBoxPeakLoadShape().setSelectedItem(gear.getPeakLoadShape());
        getJComboBoxPostLoadShape().setSelectedItem(gear.getPostLoadShape());
    }

    @Override
    public void valueChanged(JCValueEvent valueEvent) {
        fireInputUpdate();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String desc = null;
        if (e.getSource() == getJButtonHelpPreparation()) {
            desc = "<html><p><b>Preconditioning Load Shaping</b><br>"
                + "Reduces preconditioning cooling strength which might result in decreased load<br> "
                + "reduction during the event. <br> You have the option to skip preconditioning when needed.<br>"
                + "Select from these options:<br>"
                + "<b>Standard</b> - devices begin preconditioning according to the home?s thermal model (default)<br>"
                + "<b>Ramping</b> - gradually increases AC load, and is staggered over time<br>"
                + "Choose this option to skip preconditioning<br>"
                + "Useful when you need to begin an event quickly, with little notice<br>"
                + "<b>None</b> - no preconditioning<br><br>";
            getJLabelLoadShapingDesc().setText(desc);
        } else if (e.getSource() == getJButtonHelpPeak()) {
            desc = "<html><p><b>Peak Load Shaping</b><br>" + "Temp text!!!! <br><br> ";
            getJLabelLoadShapingDesc().setText(desc);
        } else if (e.getSource() == getJButtonHelpPost()) {
            desc = "<html><p><b>Post Peak Load Shaping</b><br>"
                + "<b>Standard</b> - devices begin to cool immediately following the event,<br>"
                + "per occupant schedules and comfort needs (default)<br>"
                + "<b>Ramping</b> - devices begin cooling at different times,<br>"
                + "per the occupant schedules and comfort needs, reducing AC snapback<br>"
                + "Do not choose after event ramping if you selected Uniform load shaping for the event</p></html>";
            getJLabelLoadShapingDesc().setText(desc);
        } else if (e.getSource() == getJComboBoxPeakLoadShape()) {
            jComboBoxWhenChange_ActionPerformed(e);
        } else if (e.getSource() == getJComboBoxPostLoadShape()) {
            jComboBoxWhenChange_ActionPerformed(e);
        }

        fireInputUpdate();
        return;
    }

    @Override
    public void jComboBoxWhenChange_ActionPerformed(ActionEvent actionEvent) {
        if (getJComboBoxPeakLoadShape().getSelectedItem() == PeakLoadShape.PEAK_UNIFORM
            && getJComboBoxPostLoadShape().getSelectedItem() == PostLoadShape.POST_RAMPING) {
            JOptionPane.showMessageDialog(this, "Peak as Uniform and Post as Ramping is not valid combination",
                "Incorrect Settings", JOptionPane.ERROR_MESSAGE);
        }
        return;
    }

    @Override
    public boolean isInputValid() {
        if (getJComboBoxPeakLoadShape().getSelectedItem() == PeakLoadShape.PEAK_UNIFORM
            && getJComboBoxPostLoadShape().getSelectedItem() == PostLoadShape.POST_RAMPING) {
            return false;
        }
        return true;
    }
}
