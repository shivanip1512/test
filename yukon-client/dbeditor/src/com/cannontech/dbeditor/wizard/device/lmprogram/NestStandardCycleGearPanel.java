package com.cannontech.dbeditor.wizard.device.lmprogram;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;

import com.cannontech.database.data.device.lm.NestStandardCycleGear;
import com.cannontech.dr.nest.model.LoadShapingPeak;
import com.cannontech.dr.nest.model.LoadShapingPost;
import com.cannontech.dr.nest.model.LoadShapingPreparation;
import com.klg.jclass.util.value.JCValueEvent;

public class NestStandardCycleGearPanel extends GenericGearPanel {

    private static final long serialVersionUID = 1L;

    private javax.swing.JComboBox<LoadShapingPreparation> ivjJComboBoxLoadShapingPreparation = null;
    private javax.swing.JComboBox<LoadShapingPeak> ivjJComboBoxLoadShapingPeak = null;
    private javax.swing.JComboBox<LoadShapingPost> ivjJComboBoxLoadShapingPost = null;
    private JLabel LoadShapingPreparationLabel;
    private JLabel LoadShapingPeakLabel;
    private JLabel LoadShapingPostLabel;
    private JLabel LoadShapingDescLabel;

    public NestStandardCycleGearPanel() {
        initialize();
    }

    private JLabel getJLabelLoadShapingPreparation() {
        if (LoadShapingPreparationLabel == null) {
            try {
                LoadShapingPreparationLabel = new JLabel();
                LoadShapingPreparationLabel.setName("JLabelLoadShapingPreparation");
                LoadShapingPreparationLabel.setAlignmentY(TOP_ALIGNMENT);
                LoadShapingPreparationLabel.setText("Preparation Load Shaping:");
                LoadShapingPreparationLabel.setMaximumSize(new Dimension(150, 18));
                LoadShapingPreparationLabel.setPreferredSize(new Dimension(150, 18));
                LoadShapingPreparationLabel.setFont(new Font("dialog", 0, 12));
                LoadShapingPreparationLabel.setAlignmentX(LEFT_ALIGNMENT);
                LoadShapingPreparationLabel.setMinimumSize(new Dimension(112, 14));
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return LoadShapingPreparationLabel;
    }

    private JLabel getJLabelLoadShapingPeak() {
        if (LoadShapingPeakLabel == null) {
            try {
                LoadShapingPeakLabel = new JLabel();
                LoadShapingPeakLabel.setName("JLabelLoadShapingPeak");
                LoadShapingPeakLabel.setAlignmentY(TOP_ALIGNMENT);
                LoadShapingPeakLabel.setText("Peak Load Shaping:");
                LoadShapingPeakLabel.setMaximumSize(new Dimension(150, 18));
                LoadShapingPeakLabel.setPreferredSize(new Dimension(150, 18));
                LoadShapingPeakLabel.setFont(new Font("dialog", 0, 12));
                LoadShapingPeakLabel.setAlignmentX(LEFT_ALIGNMENT);
                LoadShapingPeakLabel.setMinimumSize(new Dimension(112, 14));
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return LoadShapingPeakLabel;
    }

    private JLabel getJLabelLoadShapingPost() {
        if (LoadShapingPostLabel == null) {
            try {
                LoadShapingPostLabel = new JLabel();
                LoadShapingPostLabel.setName("JLabelLoadShapingPost");
                LoadShapingPostLabel.setAlignmentY(TOP_ALIGNMENT);
                LoadShapingPostLabel.setText("Post Peak Load Shaping:");
                LoadShapingPostLabel.setMaximumSize(new Dimension(150, 18));
                LoadShapingPostLabel.setPreferredSize(new Dimension(150, 18));
                LoadShapingPostLabel.setFont(new Font("dialog", 0, 12));
                LoadShapingPostLabel.setAlignmentX(LEFT_ALIGNMENT);
                LoadShapingPostLabel.setMinimumSize(new Dimension(112, 14));
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return LoadShapingPostLabel;
    }

    private javax.swing.JComboBox<LoadShapingPreparation> getJComboBoxLoadShapingPreparation() {
        if (ivjJComboBoxLoadShapingPreparation == null) {
            try {
                ivjJComboBoxLoadShapingPreparation = new javax.swing.JComboBox<LoadShapingPreparation>();
                ivjJComboBoxLoadShapingPreparation.setName("JComboBoxLoadShapingPreparation");
                ivjJComboBoxLoadShapingPreparation.setPreferredSize(new java.awt.Dimension(50, 23));
                ivjJComboBoxLoadShapingPreparation.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
                ivjJComboBoxLoadShapingPreparation.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
                ivjJComboBoxLoadShapingPreparation.addItem(LoadShapingPreparation.STANDARD);
                ivjJComboBoxLoadShapingPreparation.addItem(LoadShapingPreparation.RAMPING);
                ivjJComboBoxLoadShapingPreparation.addItem(LoadShapingPreparation.NONE);
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJComboBoxLoadShapingPreparation;
    }

    private javax.swing.JComboBox<LoadShapingPeak> getJComboBoxLoadShapingPeak() {
        if (ivjJComboBoxLoadShapingPeak == null) {
            try {
                ivjJComboBoxLoadShapingPeak = new javax.swing.JComboBox<LoadShapingPeak>();
                ivjJComboBoxLoadShapingPeak.setName("JComboBoxLoadShapingPeak");
                ivjJComboBoxLoadShapingPeak.setPreferredSize(new java.awt.Dimension(50, 23));
                ivjJComboBoxLoadShapingPeak.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
                ivjJComboBoxLoadShapingPeak.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
                ivjJComboBoxLoadShapingPeak.addItem(LoadShapingPeak.STANDARD);
                ivjJComboBoxLoadShapingPeak.addItem(LoadShapingPeak.SYMMETRIC);
                ivjJComboBoxLoadShapingPeak.addItem(LoadShapingPeak.UNIFORM);
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJComboBoxLoadShapingPeak;
    }

    private javax.swing.JComboBox<LoadShapingPost> getJComboBoxLoadShapingPost() {
        if (ivjJComboBoxLoadShapingPost == null) {
            try {
                ivjJComboBoxLoadShapingPost = new javax.swing.JComboBox<LoadShapingPost>();
                ivjJComboBoxLoadShapingPost.setName("JComboBoxLoadShapingPost");
                ivjJComboBoxLoadShapingPost.setPreferredSize(new java.awt.Dimension(50, 23));
                ivjJComboBoxLoadShapingPost.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
                ivjJComboBoxLoadShapingPost.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
                ivjJComboBoxLoadShapingPost.addItem(LoadShapingPost.STANDARD);
                ivjJComboBoxLoadShapingPost.addItem(LoadShapingPost.RAMPING);
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJComboBoxLoadShapingPost;
    }

    private JLabel getJLabelLoadShapingDesc() {
        if (LoadShapingDescLabel == null) {
            try {
                String prepDesc = "<html><p><b>Preconditioning Load Shaping</b><br>"
                    + "Reduces preconditioning cooling strength which might result in decreased load<br> "
                    + "reduction during the event. <br> You have the option to skip preconditioning when needed.<br>"
                    + "Select from these options:<br>"
                    + "<b>Standard</b> - devices begin preconditioning according to the home’s thermal model (default)<br>"
                    + "<b>Ramping</b> - gradually increases AC load, and is staggered over time<br>"
                    + "Choose this option to skip preconditioning<br>"
                    + "Useful when you need to begin an event quickly, with little notice<br>"
                    + "<b>None</b> - no preconditioning<br><br>" + "<b>Peak Load Shaping</b><br>"
                    + "Temp text!!!! <br><br>" + "<b>Post Peak Load Shaping</b><br>"
                    + "<b>Standard</b> - devices begin to cool immediately following the event,<br>"
                    + "per occupant schedules and comfort needs (default)<br>"
                    + "<b>Ramping</b> - devices begin cooling at different times,<br>"
                    + "per the occupant schedules and comfort needs, reducing AC snapback<br>"
                    + "Do not choose after event ramping if you selected Uniform load shaping for the event</p></html>";

                LoadShapingDescLabel = new JLabel();
                LoadShapingDescLabel.setName("JLabelLoadShapingPreparationDesc");
                LoadShapingDescLabel.setAlignmentY(TOP_ALIGNMENT);
                LoadShapingDescLabel.setText(prepDesc);
                LoadShapingDescLabel.setMaximumSize(new Dimension(2000, 350));
                LoadShapingDescLabel.setPreferredSize(new Dimension(2000, 350));
                LoadShapingDescLabel.setFont(new Font("dialog", 0, 12));
                LoadShapingDescLabel.setAlignmentX(LEFT_ALIGNMENT);
                LoadShapingDescLabel.setMinimumSize(new Dimension(2000, 350));
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return LoadShapingDescLabel;
    }

    @Override
    public Object getValue(Object gearObj) {
        NestStandardCycleGear gear = (NestStandardCycleGear) gearObj;

        com.cannontech.database.data.device.lm.NestStandardCycleGear sGear =
            (com.cannontech.database.data.device.lm.NestStandardCycleGear) gear;
        sGear.setLoadShapingPreparation(
            (LoadShapingPreparation) getJComboBoxLoadShapingPreparation().getSelectedItem());
        sGear.setLoadShapingPeak((LoadShapingPeak) getJComboBoxLoadShapingPeak().getSelectedItem());
        sGear.setLoadShapingPost((LoadShapingPost) getJComboBoxLoadShapingPost().getSelectedItem());
        return gear;
    }

    private void handleException(Throwable exception) {
        System.out.print(exception.getMessage());
        System.out.println("--------- UNCAUGHT EXCEPTION ---------");
        exception.printStackTrace(System.out);
    }

    private void initConnections() {
        getJComboBoxLoadShapingPreparation().addActionListener(this);
        getJComboBoxLoadShapingPeak().addActionListener(this);
        getJComboBoxLoadShapingPost().addActionListener(this);
    }

    private void initialize() {
        try {
            setName("NestStandardCycleGearPanel");

            GridBagConstraints constraintJLabelLoadShapingPreparation = new GridBagConstraints();
            GridBagConstraints constraintJComboBoxLoadShapingPreparation = new java.awt.GridBagConstraints();

            GridBagConstraints constraintJLabelLoadShapingPeakLabel = new GridBagConstraints();
            GridBagConstraints constraintJComboBoxLoadShapingPeak = new java.awt.GridBagConstraints();

            GridBagConstraints constraintJLabelLoadShapingPostLabel = new GridBagConstraints();
            GridBagConstraints constraintJComboBoxLoadShapingPost = new GridBagConstraints();

            GridBagConstraints constraintJLabelLoadShapingDesc = new GridBagConstraints();

            constraintJLabelLoadShapingPreparation.insets = new Insets(20, 20, 5, 5);
            constraintJLabelLoadShapingPreparation.ipady = -3;
            constraintJLabelLoadShapingPreparation.ipadx = 200;
            constraintJLabelLoadShapingPreparation.gridwidth = 2;
            constraintJLabelLoadShapingPreparation.weightx = 5.0;
            constraintJLabelLoadShapingPreparation.anchor = GridBagConstraints.WEST;
            constraintJLabelLoadShapingPreparation.gridy = 1;
            constraintJLabelLoadShapingPreparation.gridx = 1;

            constraintJComboBoxLoadShapingPreparation.insets = new Insets(20, 20, 5, 5);
            constraintJComboBoxLoadShapingPreparation.ipady = -3;
            constraintJComboBoxLoadShapingPreparation.ipadx = 50;
            constraintJComboBoxLoadShapingPreparation.gridwidth = 2;
            constraintJComboBoxLoadShapingPreparation.anchor = GridBagConstraints.WEST;
            constraintJComboBoxLoadShapingPreparation.gridy = 1;
            constraintJComboBoxLoadShapingPreparation.gridx = 2;

            constraintJLabelLoadShapingPeakLabel.insets = new Insets(0, 20, 5, 5);
            constraintJLabelLoadShapingPreparation.gridwidth = 2;
            constraintJLabelLoadShapingPeakLabel.anchor = GridBagConstraints.WEST;
            constraintJLabelLoadShapingPeakLabel.gridy = 2;
            constraintJLabelLoadShapingPeakLabel.gridx = 1;

            constraintJComboBoxLoadShapingPeak.insets = new Insets(0, 20, 5, 5);
            constraintJComboBoxLoadShapingPeak.ipady = -3;
            constraintJComboBoxLoadShapingPeak.ipadx = 50;
            constraintJLabelLoadShapingPreparation.gridwidth = 2;
            constraintJComboBoxLoadShapingPeak.anchor = GridBagConstraints.WEST;
            constraintJComboBoxLoadShapingPeak.gridy = 2;
            constraintJComboBoxLoadShapingPeak.gridx = 2;

            constraintJLabelLoadShapingPostLabel.insets = new Insets(0, 20, 5, 5);
            constraintJLabelLoadShapingPreparation.gridwidth = 2;
            constraintJLabelLoadShapingPostLabel.anchor = GridBagConstraints.WEST;
            constraintJLabelLoadShapingPostLabel.gridy = 3;
            constraintJLabelLoadShapingPostLabel.gridx = 1;

            constraintJComboBoxLoadShapingPost.insets = new Insets(0, 20, 5, 5);
            constraintJComboBoxLoadShapingPost.ipady = -3;
            constraintJComboBoxLoadShapingPost.ipadx = 50;
            constraintJComboBoxLoadShapingPost.gridwidth = 2;
            constraintJComboBoxLoadShapingPost.anchor = GridBagConstraints.WEST;
            constraintJComboBoxLoadShapingPost.gridy = 3;
            constraintJComboBoxLoadShapingPost.gridx = 2;

            constraintJLabelLoadShapingDesc.insets = new Insets(0, 20, 5, 5);
            constraintJLabelLoadShapingDesc.ipady = -3;
            constraintJLabelLoadShapingDesc.ipadx = 200;
            constraintJLabelLoadShapingDesc.gridwidth = 2;
            constraintJLabelLoadShapingDesc.anchor = GridBagConstraints.WEST;
            constraintJLabelLoadShapingDesc.gridy = 4;
            constraintJLabelLoadShapingDesc.gridx = 1;

            setLayout(new GridBagLayout());
            this.add(getJLabelLoadShapingPreparation(), constraintJLabelLoadShapingPreparation);
            this.add(getJComboBoxLoadShapingPreparation(), constraintJComboBoxLoadShapingPreparation);

            this.add(getJLabelLoadShapingPeak(), constraintJLabelLoadShapingPeakLabel);
            this.add(getJComboBoxLoadShapingPeak(), constraintJComboBoxLoadShapingPeak);

            this.add(getJLabelLoadShapingPost(), constraintJLabelLoadShapingPostLabel);
            this.add(getJComboBoxLoadShapingPost(), constraintJComboBoxLoadShapingPost);

            this.add(getJLabelLoadShapingDesc(), constraintJLabelLoadShapingDesc);

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

        com.cannontech.database.data.device.lm.NestStandardCycleGear hGear =
            (com.cannontech.database.data.device.lm.NestStandardCycleGear) gear;
        getJComboBoxLoadShapingPreparation().setSelectedItem(hGear.getLoadShapingPreparation());
        getJComboBoxLoadShapingPeak().setSelectedItem(hGear.getLoadShapingPeak());
        getJComboBoxLoadShapingPost().setSelectedItem(hGear.getLoadShapingPost());
    }

    @Override
    public void valueChanged(JCValueEvent valueEvent) {
        fireInputUpdate();
    }

}
