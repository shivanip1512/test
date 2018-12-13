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
                descriptionLabel.setMaximumSize(new Dimension(1000, 190));
                descriptionLabel.setPreferredSize(new Dimension(1000, 190));
                descriptionLabel.setFont(new Font("dialog", 0, 12));
                descriptionLabel.setMinimumSize(new Dimension(1000, 200));
                String desc;
                desc = "<html><p><b>Nest Critical Cycle Gear</b><br>"
                        + "The Nest critical cycle gear operates in the following manner.<br>"
                        + "In the hours leading up to the event, the thermostat will continue<br>"
                        + "to operate as programmed. Maximum load reduction occurs at the start of<br>"
                        + "the event and gradually decreases as the event progresses. At event stop<br>"
                        + "time, the thermostat returns to their programmed behaviors. </p></html>";
                    descriptionLabel.setText(desc);
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

            getJLabelDescription.insets = new Insets(20, 20, 5, 5);
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
