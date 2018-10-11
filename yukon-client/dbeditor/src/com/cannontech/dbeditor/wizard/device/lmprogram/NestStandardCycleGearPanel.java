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
import com.cannontech.dr.nest.model.LoadShapingPeak;
import com.cannontech.dr.nest.model.LoadShapingPost;
import com.cannontech.dr.nest.model.LoadShapingPreparation;
import com.klg.jclass.util.value.JCValueEvent;

public class NestStandardCycleGearPanel extends GenericGearPanel {

    private static final long serialVersionUID = 1L;

    private JComboBox<LoadShapingPreparation> ivjJComboBoxLoadShapingPreparation = null;
    private JComboBox<LoadShapingPeak> ivjJComboBoxLoadShapingPeak = null;
    private JComboBox<LoadShapingPost> ivjJComboBoxLoadShapingPost = null;
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
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return helpPostButton;
    }

    private JLabel getJLabelLoadShapingPreparation() {
        if (loadShapingPreparationLabel == null) {
            try {
                loadShapingPreparationLabel = new JLabel();
                loadShapingPreparationLabel.setName("JLabelLoadShapingPreparation");
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

    private JLabel getJLabelLoadShapingPeak() {
        if (loadShapingPeakLabel == null) {
            try {
                loadShapingPeakLabel = new JLabel();
                loadShapingPeakLabel.setName("JLabelLoadShapingPeak");
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

    private JLabel getJLabelLoadShapingPost() {
        if (loadShapingPostLabel == null) {
            try {
                loadShapingPostLabel = new JLabel();
                loadShapingPostLabel.setName("JLabelLoadShapingPost");
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

    private JComboBox<LoadShapingPreparation> getJComboBoxLoadShapingPreparation() {
        if (ivjJComboBoxLoadShapingPreparation == null) {
            try {
                ivjJComboBoxLoadShapingPreparation = new JComboBox<LoadShapingPreparation>();
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

    private JComboBox<LoadShapingPeak> getJComboBoxLoadShapingPeak() {
        if (ivjJComboBoxLoadShapingPeak == null) {
            try {
                ivjJComboBoxLoadShapingPeak = new JComboBox<LoadShapingPeak>();
                ivjJComboBoxLoadShapingPeak.setName("JComboBoxLoadShapingPeak");
                ivjJComboBoxLoadShapingPeak.setPreferredSize(new java.awt.Dimension(50, 23));
                ivjJComboBoxLoadShapingPeak.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
                ivjJComboBoxLoadShapingPeak.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
                ivjJComboBoxLoadShapingPeak.addItem(LoadShapingPeak.UNIFORM);
                ivjJComboBoxLoadShapingPeak.addItem(LoadShapingPeak.STANDARD);
                ivjJComboBoxLoadShapingPeak.addItem(LoadShapingPeak.SYMMETRIC);
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJComboBoxLoadShapingPeak;
    }

    private JComboBox<LoadShapingPost> getJComboBoxLoadShapingPost() {
        if (ivjJComboBoxLoadShapingPost == null) {
            try {
                ivjJComboBoxLoadShapingPost = new JComboBox<LoadShapingPost>();
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
        if (loadShapingDescLabel == null) {
            try {
                loadShapingDescLabel = new JLabel();
                loadShapingDescLabel.setName("JLabelLoadShapingPreparationDesc");
                loadShapingDescLabel.setAlignmentY(TOP_ALIGNMENT);
                loadShapingDescLabel.setMaximumSize(new Dimension(1000, 190));
                loadShapingDescLabel.setPreferredSize(new Dimension(1000, 190));
                loadShapingDescLabel.setFont(new Font("dialog", 0, 12));
                loadShapingDescLabel.setAlignmentX(LEFT_ALIGNMENT);
                loadShapingDescLabel.setMinimumSize(new Dimension(1000, 200));
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return loadShapingDescLabel;
    }

    @Override
    public Object getValue(Object gearObj) {
        NestStandardCycleGear gear = (NestStandardCycleGear) gearObj;
        NestStandardCycleGear sGear = (NestStandardCycleGear) gear;
        sGear.setLoadShapingPreparation(
            (LoadShapingPreparation) getJComboBoxLoadShapingPreparation().getSelectedItem());
        sGear.setLoadShapingPeak((LoadShapingPeak) getJComboBoxLoadShapingPeak().getSelectedItem());
        sGear.setLoadShapingPost((LoadShapingPost) getJComboBoxLoadShapingPost().getSelectedItem());
        return sGear;
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
        getJButtonHelpPreparation().addActionListener(this);
        getJButtonHelpPeak().addActionListener(this);
        getJButtonHelpPost().addActionListener(this);
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
            
            GridBagConstraints constraintJButtonHelpPreparation = new GridBagConstraints();
            GridBagConstraints constraintJButtonHelpPeak = new GridBagConstraints();
            GridBagConstraints constraintJButtonHelpPost = new GridBagConstraints();

            constraintJLabelLoadShapingPreparation.insets = new Insets(20, 20, 5, 5);
            constraintJLabelLoadShapingPreparation.ipady = -3;
            constraintJLabelLoadShapingPreparation.ipadx = 200;
            constraintJLabelLoadShapingPreparation.gridwidth = 3;
            constraintJLabelLoadShapingPreparation.weightx = 5.0;
            constraintJLabelLoadShapingPreparation.anchor = GridBagConstraints.WEST;
            constraintJLabelLoadShapingPreparation.gridy = 1;
            constraintJLabelLoadShapingPreparation.gridx = 1;

            constraintJComboBoxLoadShapingPreparation.insets = new Insets(20, 20, 5, 5);
            constraintJComboBoxLoadShapingPreparation.ipady = -3;
            constraintJComboBoxLoadShapingPreparation.ipadx = 50;
            constraintJComboBoxLoadShapingPreparation.gridwidth = 3;
            constraintJComboBoxLoadShapingPreparation.anchor = GridBagConstraints.WEST;
            constraintJComboBoxLoadShapingPreparation.gridy = 1;
            constraintJComboBoxLoadShapingPreparation.gridx = 2;
            
            constraintJButtonHelpPreparation.insets = new Insets(20, 20, 5, 5);
            constraintJButtonHelpPreparation.ipady = -2;
            constraintJButtonHelpPreparation.ipadx = 20;
            constraintJButtonHelpPreparation.gridwidth = 3;
            constraintJButtonHelpPreparation.anchor = GridBagConstraints.WEST;
            constraintJButtonHelpPreparation.gridy = 1;
            constraintJButtonHelpPreparation.gridx = 3;

            constraintJLabelLoadShapingPeakLabel.insets = new Insets(0, 20, 5, 5);
            constraintJLabelLoadShapingPreparation.gridwidth = 3;
            constraintJLabelLoadShapingPeakLabel.anchor = GridBagConstraints.WEST;
            constraintJLabelLoadShapingPeakLabel.gridy = 2;
            constraintJLabelLoadShapingPeakLabel.gridx = 1;

            constraintJComboBoxLoadShapingPeak.insets = new Insets(0, 20, 5, 5);
            constraintJComboBoxLoadShapingPeak.ipady = -3;
            constraintJComboBoxLoadShapingPeak.ipadx = 50;
            constraintJLabelLoadShapingPreparation.gridwidth = 3;
            constraintJComboBoxLoadShapingPeak.anchor = GridBagConstraints.WEST;
            constraintJComboBoxLoadShapingPeak.gridy = 2;
            constraintJComboBoxLoadShapingPeak.gridx = 2;
            
            constraintJButtonHelpPeak.insets = new Insets(0, 20, 5, 5);
            constraintJButtonHelpPeak.ipady = -2;
            constraintJButtonHelpPeak.ipadx = 20;
            constraintJButtonHelpPeak.gridwidth = 3;
            constraintJButtonHelpPeak.anchor = GridBagConstraints.WEST;
            constraintJButtonHelpPeak.gridy = 2;
            constraintJButtonHelpPeak.gridx = 3;

            constraintJLabelLoadShapingPostLabel.insets = new Insets(0, 20, 5, 5);
            constraintJLabelLoadShapingPreparation.gridwidth = 3;
            constraintJLabelLoadShapingPostLabel.anchor = GridBagConstraints.WEST;
            constraintJLabelLoadShapingPostLabel.gridy = 3;
            constraintJLabelLoadShapingPostLabel.gridx = 1;

            constraintJComboBoxLoadShapingPost.insets = new Insets(0, 20, 5, 5);
            constraintJComboBoxLoadShapingPost.ipady = -3;
            constraintJComboBoxLoadShapingPost.ipadx = 50;
            constraintJComboBoxLoadShapingPost.gridwidth = 3;
            constraintJComboBoxLoadShapingPost.anchor = GridBagConstraints.WEST;
            constraintJComboBoxLoadShapingPost.gridy = 3;
            constraintJComboBoxLoadShapingPost.gridx = 2;
            
            
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
            this.add(getJLabelLoadShapingPreparation(), constraintJLabelLoadShapingPreparation);
            this.add(getJComboBoxLoadShapingPreparation(), constraintJComboBoxLoadShapingPreparation);

            this.add(getJLabelLoadShapingPeak(), constraintJLabelLoadShapingPeakLabel);
            this.add(getJComboBoxLoadShapingPeak(), constraintJComboBoxLoadShapingPeak);

            this.add(getJLabelLoadShapingPost(), constraintJLabelLoadShapingPostLabel);
            this.add(getJComboBoxLoadShapingPost(), constraintJComboBoxLoadShapingPost);

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

        NestStandardCycleGear hGear = (NestStandardCycleGear) gear;
        getJComboBoxLoadShapingPreparation().setSelectedItem(hGear.getLoadShapingPreparation());
        getJComboBoxLoadShapingPeak().setSelectedItem(hGear.getLoadShapingPeak());
        getJComboBoxLoadShapingPost().setSelectedItem(hGear.getLoadShapingPost());
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
                + "<b>Standard</b> - devices begin preconditioning according to the home’s thermal model (default)<br>"
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
        } else if (e.getSource() == getJComboBoxLoadShapingPeak()) {
            jComboBoxWhenChange_ActionPerformed(e);
        } else if (e.getSource() == getJComboBoxLoadShapingPost()) {
            jComboBoxWhenChange_ActionPerformed(e);
        }

        fireInputUpdate();
        return;
    }

    @Override
    public void jComboBoxWhenChange_ActionPerformed(ActionEvent actionEvent) {
        if (getJComboBoxLoadShapingPeak().getSelectedItem() == LoadShapingPeak.UNIFORM
            && getJComboBoxLoadShapingPost().getSelectedItem() == LoadShapingPost.RAMPING) {
            JOptionPane.showMessageDialog(this, "Peak as Uniform and Post as Ramping is not valid combination",
                "Incorrect Settings", JOptionPane.ERROR_MESSAGE);
        }
        return;
    }

    @Override
    public boolean isInputValid() {
        if (getJComboBoxLoadShapingPeak().getSelectedItem() == LoadShapingPeak.UNIFORM
            && getJComboBoxLoadShapingPost().getSelectedItem() == LoadShapingPost.RAMPING) {
            return false;
        }
        return true;
    }
}
