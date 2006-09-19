package com.cannontech.dbeditor.editor.device.configuration.category.tou;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JLabel;

import com.cannontech.common.device.configuration.model.Category;
import com.cannontech.common.device.configuration.model.Item;
import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.gui.util.TitleBorder;
import com.cannontech.dbeditor.editor.device.configuration.DeviceConfigurationPropertyPanel;

/**
 * This panel contains a list of items for a tou schedule category
 */
public class TOUScheduleItemPanel extends DataInputPanel {

    // Array of all possible days for scheduling
    private static final String[] DAYS = new String[] { "Monday", "Tuesday", "Wednesday",
            "Thursday", "Friday", "Saturday", "Sunday", "Holiday" };

    private JComboBox defaultRateCombo = null;
    private Map<String, JComboBox> dayComboMap = null;

    private boolean enabled;

    private Category category = null;

    public TOUScheduleItemPanel(boolean enabled) {
        this.enabled = enabled;
        this.initialize();
    }

    @Override
    public Object getValue(Object o) {
        o = this.category;

        // Get the default rate
        this.category.getItemByName("Default Rate")
                     .setValue((String) this.defaultRateCombo.getSelectedItem());

        // Loop through each of the days and get the value from the matching
        // combo box
        for (int dayIndex = 0; dayIndex < DAYS.length; dayIndex++) {

            String dayText = DAYS[dayIndex];
            this.category.getItemByName(dayText)
                         .setValue(((RateScheduleData) this.dayComboMap.get(dayText)
                                                                       .getSelectedItem()).getValue()
                                                                                          .toString());
        }

        return this.category;
    }

    @Override
    public void setValue(Object o) {

        this.category = (Category) o;

        if (this.category != null) {

            // Set the default rate combo box value on the panel
            Item rateItem = this.category.getItemByName("Default Rate");
            if (rateItem != null && this.defaultRateCombo != null) {
                for (int i = 0; i < this.defaultRateCombo.getItemCount(); i++) {
                    String rateStr = (String) this.defaultRateCombo.getItemAt(i);
                    if (rateStr.equalsIgnoreCase(rateItem.getValue())) {
                        this.defaultRateCombo.setSelectedIndex(i);
                        this.defaultRateCombo.validate();
                        break;
                    }
                }
            }

            // Loop through each of the days and set the combo box values on the
            // panel
            for (int dayIndex = 0; dayIndex < DAYS.length; dayIndex++) {

                String dayText = DAYS[dayIndex];
                Item dayItem = this.category.getItemByName(dayText);
                JComboBox dayCombo = this.dayComboMap.get(dayText);

                if (dayItem != null && dayCombo != null && dayItem.getValue() != null) {

                    dayCombo.setSelectedIndex(Integer.parseInt(dayItem.getValue()) - 1);

                }
            }
        }
    }

    /**
     * Helper method to initialize this panel
     */
    private void initialize() {

        this.dayComboMap = new HashMap<String, JComboBox>();

        this.setLayout(new GridBagLayout());
        TitleBorder border = new TitleBorder("TOU Rate Schedule");
        border.setTitleFont(DeviceConfigurationPropertyPanel.TITLE_FONT);
        this.setBorder(border);

        // Add the default rate label and combo box
        this.add(new JLabel("Default Rate:"), new GridBagConstraints(1,
                                                                     1,
                                                                     1,
                                                                     1,
                                                                     0.0,
                                                                     0.0,
                                                                     GridBagConstraints.WEST,
                                                                     GridBagConstraints.NONE,
                                                                     new Insets(0, 5, 0, 0),
                                                                     0,
                                                                     0));

        this.defaultRateCombo = new JComboBox(RateScheduleItemPanel.RATES);
        this.defaultRateCombo.setEnabled(this.enabled);
        this.defaultRateCombo.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                fireInputUpdate();
            }
        });
        this.add(this.defaultRateCombo, new GridBagConstraints(2,
                                                               1,
                                                               1,
                                                               1,
                                                               1.0,
                                                               0.0,
                                                               GridBagConstraints.WEST,
                                                               GridBagConstraints.NONE,
                                                               new Insets(0, 5, 0, 0),
                                                               0,
                                                               0));

        // Loop through all of the days and add a label and combo box for each
        for (int dayIndex = 0; dayIndex < DAYS.length; dayIndex++) {

            String dayText = DAYS[dayIndex];

            this.add(new JLabel(dayText), new GridBagConstraints(1,
                                                                 dayIndex + 2,
                                                                 1,
                                                                 1,
                                                                 0.0,
                                                                 0.0,
                                                                 GridBagConstraints.WEST,
                                                                 GridBagConstraints.NONE,
                                                                 new Insets(0, 5, 0, 0),
                                                                 0,
                                                                 0));
            JComboBox combo = new JComboBox(this.createRateScheduleDataArray());
            combo.setEnabled(this.enabled);
            combo.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    fireInputUpdate();
                }
            });
            this.dayComboMap.put(dayText, combo);
            this.add(combo, new GridBagConstraints(2,
                                                   dayIndex + 2,
                                                   1,
                                                   1,
                                                   1.0,
                                                   0.0,
                                                   GridBagConstraints.WEST,
                                                   GridBagConstraints.NONE,
                                                   new Insets(0, 5, 0, 0),
                                                   0,
                                                   0));

        }
    }

    /**
     * Helper method to create a RateScheduleData array based on the
     * NUMBER_OF_RATE_SCHEDULES
     * @return An array of RateScheduleData
     */
    private Object[] createRateScheduleDataArray() {

        List<RateScheduleData> rateSchedules = new ArrayList<RateScheduleData>();
        for (int index = 1; index <= RateScheduleItemPanel.NUMBER_OF_RATE_SCHEDULES; index++) {
            rateSchedules.add(new RateScheduleData("Daily Rate Schedule " + index, index));
        }

        return rateSchedules.toArray();
    }

    /**
     * Helper class to hold rate schedule data
     */
    private class RateScheduleData {

        private String name = null;
        private Integer value = -1;

        public RateScheduleData(String name, int value) {
            this.name = name;
            this.value = value;
        }

        public String toString() {
            return this.name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getValue() {
            return value;
        }

        public void setValue(Integer value) {
            this.value = value;
        }
    }

}
