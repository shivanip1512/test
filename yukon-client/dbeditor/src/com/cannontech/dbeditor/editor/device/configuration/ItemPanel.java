package com.cannontech.dbeditor.editor.device.configuration;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.ToolTipManager;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.gui.util.DateComboBox;
import com.cannontech.common.gui.util.TitleBorder;
import com.cannontech.database.data.device.configuration.Category;
import com.cannontech.database.data.device.configuration.Item;
import com.cannontech.database.data.device.configuration.ItemValueType;

/**
 * This panel contains a list of item names and values for a given category
 */
public class ItemPanel extends DataInputPanel {

    private boolean editable = false;
    private Map<Item, JComponent> itemValueMap;

    public ItemPanel(boolean editable) {
        this.editable = editable;
        this.initialize();
    }

    @Override
    public Object getValue(Object o) {

        Category category = (Category) o;

        List<Item> itemList = new ArrayList<Item>();
        Item item = null;
        Iterator<Item> itemIter = this.itemValueMap.keySet().iterator();
        while (itemIter.hasNext()) {

            item = itemIter.next();
            item.setValue(this.getFieldValue(item));
            itemList.add(item);

        }

        category.setItemList(itemList);

        return category;

    }

    @Override
    public void setValue(Object o) {

        this.removeAll();

        Category category = (Category) o;

        if (category != null) {
            Item item = null;
            int row = 1;
            List<Item> itemList = category.getItemList();
            Iterator<Item> itemListIter = itemList.iterator();
            while (itemListIter.hasNext()) {

                item = itemListIter.next();

                boolean required = item.isRequired();

                GridBagConstraints constraints = new GridBagConstraints(1,
                                                                        row,
                                                                        1,
                                                                        1,
                                                                        0.0,
                                                                        (!itemListIter.hasNext())
                                                                                ? 1.0 : 0.0,
                                                                        GridBagConstraints.NORTHWEST,
                                                                        GridBagConstraints.NONE,
                                                                        new Insets(0, 5, 5, 5),
                                                                        5,
                                                                        5);

                // Add Item Name label
                JLabel itemName = new JLabel(item.getName() + ":");
                if (item.getDescription() != null && !item.getDescription().equals("")) {
                    itemName.setToolTipText(item.getDescription());
                }
                itemName.setFont(DeviceConfigurationPropertyPanel.FIELD_FONT);
                this.add(itemName, constraints);

                // Add Item Value component
                constraints.gridx = 2;
                constraints.gridwidth = (required) ? 1 : 2;
                constraints.weightx = (required) ? 0.0 : 1.0;
                this.add(this.getItemValueComponent(item), constraints);

                // Add red * if item is required
                if (required) {
                    JLabel requiredLabel = new JLabel("*");
                    requiredLabel.setToolTipText("Required Item");
                    requiredLabel.setFont(DeviceConfigurationPropertyPanel.FIELD_FONT);
                    requiredLabel.setForeground(Color.RED);

                    constraints.gridx = 3;
                    constraints.gridwidth = 1;
                    constraints.weightx = 1.0;
                    this.add(requiredLabel, constraints);
                }

                row++;
            }

            this.repaint();
        }

    }

    @Override
    /**
     * This method checks all of the required items and the category name to
     * make sure each has a value.
     * @return True if values are present
     */
    public boolean isInputValid() {

        if (this.itemValueMap.size() == 0) {
            return false;
        }

        Iterator<Item> itemIter = this.itemValueMap.keySet().iterator();
        while (itemIter.hasNext()) {
            Item item = itemIter.next();

            if (item.isRequired() && (this.getFieldValue(item).equals(""))) {
                this.setErrorString(item.getName() + " is a required field.");
                return false;
            }
        }
        return true;
    }

    /**
     * This method removes all of the item data from the panel
     */
    public void clearPanel() {
        this.removeAll();
        this.itemValueMap.clear();
        this.repaint();
    }

    /**
     * Helper method to get the desired component for the item value. This
     * method should be overridden in sub classe to return desired components
     * @param item - Item to get the component for
     * @return The item value's component
     */
    protected JComponent getItemValueComponent(Item item) {

        JComponent component = null;

        if (this.editable) {

            if (item.getValueType() == ItemValueType.NUMERIC) {
                // Numeric types use a JSpinner
                SpinnerNumberModel model = new SpinnerNumberModel(item.getMinValue(),
                                                                  item.getMinValue(),
                                                                  item.getMaxValue(),
                                                                  1);
                JSpinner spinner = new JSpinner(model);
                component = spinner;
            } else if (item.getValueType() == ItemValueType.DATE) {
                // Date types use a DateComboBox
                component = new DateComboBox();
                this.itemValueMap.put(item, component);
            } else if (item.getValueType() == ItemValueType.BOOLEAN) {
                // Boolean types use a JCheckBox
                component = new JCheckBox();
                this.itemValueMap.put(item, component);

            } else {
                // The default input component is a JTextField
                JTextField field = new JTextField(20);

                field.setText(item.getValue());
                field.addCaretListener(new CaretListener() {
                    public void caretUpdate(CaretEvent e) {
                        fireInputUpdate();
                    }
                });

                component = field;
                this.itemValueMap.put(item, component);
            }

            component.setFont(DeviceConfigurationPropertyPanel.FIELD_FONT);

        } else {
            // JLabels are used for non-editable items
            component = new JLabel(item.getValue());
            component.setFont(DeviceConfigurationPropertyPanel.FIELD_FONT);
            component.setForeground(Color.BLUE);

        }

        this.itemValueMap.put(item, component);
        return component;
    }

    /**
     * Helper method to initialize this panel
     */
    private void initialize() {
        this.setLayout(new GridBagLayout());
        TitleBorder border = new TitleBorder("Items");
        border.setTitleFont(DeviceConfigurationPropertyPanel.TITLE_FONT);
        this.setBorder(border);
        this.itemValueMap = new HashMap<Item, JComponent>();

        // Set the the dismiss delay to 60000 so that tool tips will be
        // displayed for 60 seconds
        ToolTipManager manager = ToolTipManager.sharedInstance();
        manager.setDismissDelay(60000);
    }

    /**
     * Helper method to get the String value from an item's component
     * @param item - Item to get the value for
     * @return String value of input
     */
    private String getFieldValue(Item item) {

        JComponent component = this.itemValueMap.get(item);
        if (component == null) {
            return null;
        }

        if (component instanceof JTextField) {
            return ((JTextField) component).getText();
        } else if (component instanceof JCheckBox) {
            return String.valueOf(((JCheckBox) component).isSelected());
        } else if (component instanceof JSpinner) {
            return ((JSpinner) component).getValue().toString();
        } else if (component instanceof DateComboBox) {
            return ((DateComboBox) component).getSelectedDate().toString();
        }

        return null;

    }

}
