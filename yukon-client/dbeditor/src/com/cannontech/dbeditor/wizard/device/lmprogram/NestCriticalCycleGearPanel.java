package com.cannontech.dbeditor.wizard.device.lmprogram;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;

import com.cannontech.database.data.device.lm.NestCriticalCycleGear;

public class NestCriticalCycleGearPanel extends GenericGearPanel {

    private static final long serialVersionUID = 1L;

    private JLabel descriptionLabel;

    public NestCriticalCycleGearPanel() {
        initialize();
    }

    private JLabel getJLabelDescription() {
        if (descriptionLabel == null) {
            try {
                descriptionLabel = new JLabel();
                descriptionLabel.setName("JLabelDescription");
                descriptionLabel.setAlignmentY(TOP_ALIGNMENT);
                descriptionLabel.setText("This will describe what does critical cycle gear mean");
                descriptionLabel.setMaximumSize(new Dimension(103, 14));
                descriptionLabel.setPreferredSize(new Dimension(103, 14));
                descriptionLabel.setFont(new Font("dialog", 0, 12));
                descriptionLabel.setMinimumSize(new Dimension(103, 14));
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return descriptionLabel;
    }

    @Override
    public Object getValue(Object gearObj) {
        NestCriticalCycleGear gear = (NestCriticalCycleGear) gearObj;
        return gear;
    }

    private void handleException(Throwable exception) {
        System.out.print(exception.getMessage());
        System.out.println("--------- UNCAUGHT EXCEPTION ---------");
        exception.printStackTrace(System.out);
    }

    private void initialize() {
        try {
            setName("NestCriticalCycleGearPanel");

            GridBagConstraints getJLabelDescription = new GridBagConstraints();

            getJLabelDescription.insets = new Insets(0, 0, 5, 0);
            getJLabelDescription.ipady = -3;
            getJLabelDescription.ipadx = 200;
            getJLabelDescription.gridwidth = 3;
            getJLabelDescription.anchor = GridBagConstraints.WEST;
            getJLabelDescription.gridy = 1;
            getJLabelDescription.gridx = 1;

            setLayout(new GridBagLayout());
            this.add(getJLabelDescription(), getJLabelDescription);
        } catch (Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
}
