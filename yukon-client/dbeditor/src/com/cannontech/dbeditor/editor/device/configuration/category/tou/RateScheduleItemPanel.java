package com.cannontech.dbeditor.editor.device.configuration.category.tou;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.cannontech.common.device.configuration.model.Category;
import com.cannontech.common.device.configuration.model.Item;
import com.cannontech.common.editor.PropertyPanelEvent;
import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.gui.util.DataInputPanelListener;
import com.cannontech.common.gui.util.TitleBorder;
import com.cannontech.dbeditor.editor.device.configuration.DeviceConfigurationPropertyPanel;

/**
 * This panel contains a list of items for a rate schedule category
 */
public class RateScheduleItemPanel extends DataInputPanel implements DataInputPanelListener {

    public static final String[] RATES = new String[] { "A", "B", "C", "D" };
    public static final int NUMBER_OF_RATE_SCHEDULES = 4;
    private static final int NUMBER_OF_RATE_CHANGES = 6;
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");

    private Category category = new Category();
    private List<DailyRateSchedule> dailyRateScheduleList = null;

    private final boolean enabled;

    public RateScheduleItemPanel(boolean enabled) {
        super();
        this.enabled = enabled;
        this.initialize();

    }

    @Override
    public Object getValue(Object o) {

        o = this.category;

        // Loop through each of the rate schedules and set all of the items in
        // the category
        for (int i = 1; i <= NUMBER_OF_RATE_SCHEDULES; i++) {

            DailyRateSchedule schedule = this.dailyRateScheduleList.get(i - 1);

            for (int j = 0; j < NUMBER_OF_RATE_CHANGES; j++) {

                String timeItemString = "Schedule" + i + "Time" + j;
                String rateItemString = "Schedule" + i + "Rate" + j;

                String time = schedule.getTime(j);
                String rate = schedule.getRate(j);

                this.category.getItemByName(timeItemString).setValue(time);
                this.category.getItemByName(rateItemString).setValue(rate);

            }
        }

        return this.category;
    }

    @Override
    public void setValue(Object o) {

        this.category = (Category) o;

        if (this.category != null) {

            // Loop through all of the rate schedules, for each, loop through
            // the rate changes and get each of the time spinner / rate combo
            // pairs and set the values to the matching category item values
            for (int rateSchedule = 1; rateSchedule <= NUMBER_OF_RATE_SCHEDULES; rateSchedule++) {

                DailyRateSchedule schedule = this.dailyRateScheduleList.get(rateSchedule - 1);

                for (int change = 0; change < NUMBER_OF_RATE_CHANGES; change++) {

                    String timeItemString = "Schedule" + rateSchedule + "Time" + change;
                    String rateItemString = "Schedule" + rateSchedule + "Rate" + change;

                    Item timeItem = this.category.getItemByName(timeItemString);
                    JSpinner spinner = schedule.getTimeSpinner(change);
                    if (timeItem != null) {
                        spinner.setToolTipText(timeItem.getDescription());
                        if (spinner != null && timeItem.getValue() != null) {
                            try {
                                if (!"".equals(timeItem.getValue())) {
                                    spinner.setValue(TIME_FORMAT.parse(timeItem.getValue()));
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    JComboBox combo = schedule.getRateCombo(change);
                    Item rateItem = this.category.getItemByName(rateItemString);
                    if (rateItem != null && combo != null) {
                        combo.setToolTipText(rateItem.getDescription());
                        for (int i = 0; i < combo.getItemCount(); i++) {
                            String rateStr = (String) combo.getItemAt(i);
                            if (rateStr.equalsIgnoreCase(rateItem.getValue())) {
                                combo.setSelectedIndex(i);
                                combo.validate();
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Helper method to create a panel with times and rates for a given rate
     * schedule
     * @param index - Index of rate schedule
     * @return Initilized panel
     */
    private JPanel createTimeRatePanel(int index) {

        JPanel panel = new JPanel(new GridBagLayout());
        TitleBorder border = new TitleBorder("Daily Rate Schedule " + (index + 1));
        border.setTitleFont(DeviceConfigurationPropertyPanel.TITLE_FONT);
        panel.setBorder(border);

        DailyRateSchedule schedule = new DailyRateSchedule();

        // Create a time / rate pair for each of the rate changes
        for (int i = 0; i < NUMBER_OF_RATE_CHANGES; i++) {

            // Add the time label and spinner
            panel.add(new JLabel("Time:"), new GridBagConstraints(1,
                                                                  i + 1,
                                                                  1,
                                                                  1,
                                                                  0.0,
                                                                  0.0,
                                                                  GridBagConstraints.WEST,
                                                                  GridBagConstraints.NONE,
                                                                  new Insets(0, 5, 0, 0),
                                                                  0,
                                                                  0));

            // Initialize the time spinner to 00:00
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.AM_PM, Calendar.AM);
            JSpinner spinner = new JSpinner(new SpinnerDateModel(calendar.getTime(),
                                                                 null,
                                                                 null,
                                                                 Calendar.MINUTE));
            // Only show hour and minute in the date spinner
            spinner.setEditor(new JSpinner.DateEditor(spinner, "HH:mm"));
            if (i == 0) {
                spinner.setEnabled(false);
            } else {
                spinner.setEnabled(this.enabled);
            }
            spinner.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    fireInputUpdate();
                }
            });
            panel.add(spinner, new GridBagConstraints(2,
                                                      i + 1,
                                                      1,
                                                      1,
                                                      0.0,
                                                      0.0,
                                                      GridBagConstraints.WEST,
                                                      GridBagConstraints.NONE,
                                                      new Insets(0, 5, 0, 0),
                                                      0,
                                                      0));

            // Add the rate label and combo box
            panel.add(new JLabel("Rate:"), new GridBagConstraints(3,
                                                                  i + 1,
                                                                  1,
                                                                  1,
                                                                  0.0,
                                                                  0.0,
                                                                  GridBagConstraints.WEST,
                                                                  GridBagConstraints.NONE,
                                                                  new Insets(0, 5, 0, 0),
                                                                  0,
                                                                  0));

            JComboBox combo = new JComboBox(RATES);
            combo.setEnabled(this.enabled);
            combo.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    fireInputUpdate();
                }
            });
            panel.add(combo, new GridBagConstraints(4,
                                                    i + 1,
                                                    1,
                                                    1,
                                                    1.0,
                                                    0.0,
                                                    GridBagConstraints.WEST,
                                                    GridBagConstraints.NONE,
                                                    new Insets(0, 5, 0, 0),
                                                    0,
                                                    0));

            schedule.add(spinner, combo);
        }

        this.dailyRateScheduleList.add(schedule);

        return panel;
    }

    /**
     * Helper method to initialize this panel
     */
    private void initialize() {
        this.setLayout(new BorderLayout());

        // Initialize the dailyRateSchedule list
        this.dailyRateScheduleList = new ArrayList<DailyRateSchedule>();

        JPanel categoryPanel = new JPanel(new GridBagLayout());

        // Create a panel for each of the rate schedules
        for (int i = 0; i < NUMBER_OF_RATE_SCHEDULES; i++) {

            categoryPanel.add(this.createTimeRatePanel(i),
                              new GridBagConstraints(1,
                                                     i + 1,
                                                     1,
                                                     1,
                                                     1.0,
                                                     0.0,
                                                     GridBagConstraints.WEST,
                                                     GridBagConstraints.BOTH,
                                                     new Insets(0, 10, 10, 10),
                                                     5,
                                                     5));
        }

        this.add(categoryPanel, BorderLayout.CENTER);
    }

    /**
     * Implemented for DataInputPanelListener
     */
    public void inputUpdate(PropertyPanelEvent event) {
        fireInputUpdate();
    }

    /**
     * Helper class which represents a daily rate schedule and all of it's time /
     * rate pairs
     */
    private class DailyRateSchedule {

        private List<JSpinner> timeList = null;
        private List<JComboBox> rateList = null;

        public DailyRateSchedule() {
            this.timeList = new ArrayList<JSpinner>();
            this.rateList = new ArrayList<JComboBox>();
        }

        /**
         * Method to add a time jspinner / rate combo box pair to the
         * DailyRateSchedule
         * @param timeSpinner - time spinner to add
         * @param rateCombo - rate combo to add
         */
        public void add(JSpinner timeSpinner, JComboBox rateCombo) {
            this.timeList.add(timeSpinner);
            this.rateList.add(rateCombo);
        }

        /**
         * Method to get the time from the timeList at the given position
         * @param position - Position to get the time for
         * @return A String representing the time
         */
        public String getTime(int position) {

            try {
                JSpinner spinner = timeList.get(position);

                return TIME_FORMAT.format(spinner.getValue());

            } catch (IndexOutOfBoundsException e) {
                return "";
            }
        }

        /**
         * Method to get the time spinner from the timeList at the given
         * position
         * @param position - Position to get the time for
         * @return The time spinner
         */
        public JSpinner getTimeSpinner(int position) {
            try {
                return timeList.get(position);
            } catch (IndexOutOfBoundsException e) {
                return null;
            }
        }

        /**
         * Method to get the rate from the rateList at the given position
         * @param position - Position to get the rate for
         * @return A String representing the rate
         */
        public String getRate(int position) {

            try {
                JComboBox combo = rateList.get(position);

                return (String) combo.getSelectedItem();

            } catch (IndexOutOfBoundsException e) {
                return "";
            }
        }

        /**
         * Method to get the rate combo box from the rateList at the given
         * position
         * @param position - Position to get the rate for
         * @return The rate combo box
         */
        public JComboBox getRateCombo(int position) {
            try {
                return rateList.get(position);
            } catch (IndexOutOfBoundsException e) {
                return null;
            }
        }
    }

}
