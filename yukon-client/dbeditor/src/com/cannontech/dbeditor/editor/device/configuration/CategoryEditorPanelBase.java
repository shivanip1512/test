package com.cannontech.dbeditor.editor.device.configuration;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.wizard.WizardPanel;
import com.cannontech.common.wizard.WizardPanelEvent;
import com.cannontech.common.wizard.WizardPanelListener;
import com.cannontech.database.cache.functions.DeviceConfigurationFuncsImpl;
import com.cannontech.database.data.device.configuration.Category;
import com.cannontech.database.data.device.configuration.Item;
import com.cannontech.database.data.device.configuration.Type;
import com.cannontech.dbeditor.DatabaseEditor;

/**
 * Panel class which shows a category. This panel has a combo box with all the
 * existing categories of the type and an item panel with all the items for the
 * selected category. There is also a create new button which allows the user to
 * create a new category of the given type and a copy button which allows the
 * user to copy an existing category.
 */
public class CategoryEditorPanelBase extends DataInputPanel {

    protected JCheckBox configuredCheck = null;
    protected JComboBox categoryCombo = new JComboBox();
    protected JButton createButton = new JButton();
    protected JButton copyButton = new JButton();
    protected DataInputPanel itemPanel = null;

    protected boolean editable = false;

    public CategoryEditorPanelBase(boolean editable) {
        this.editable = editable;
        this.initialize();
    }

    @Override
    public Object getValue(Object o) {

        if (this.categoryCombo.isEnabled() && this.categoryCombo.getSelectedItem() != null) {
            return this.categoryCombo.getSelectedItem();
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setValue(Object o) {

        List<Category> categoryList = (List<Category>) o;

        if (categoryList != null) {
            this.add(this.createCategoryPanel(categoryList), BorderLayout.CENTER);
        }

    }

    /**
     * Method to enable or disable the category combobox, create button and copy
     * button
     * @param enabled - True for enable, false for disable
     */
    public void enablePanel(boolean enabled) {

        if (this.categoryCombo != null) {
            this.categoryCombo.setEnabled(enabled);
        }
        if (this.createButton != null) {
            this.createButton.setEnabled(enabled);
        }
        if (this.copyButton != null) {
            this.copyButton.setEnabled(enabled);
        }

    }

    /**
     * Method to initialize this panel
     */
    protected void initialize() {
        this.setLayout(new BorderLayout());
    }

    /**
     * Method to get an action listener for the create new button.
     * @param combo - Category combo box to be updated with newly created object
     * @param type - Type of categories in the category combo
     * @return - Create button action listener
     */
    protected ActionListener getCreateNewButtonListener(JComboBox combo, Type type) {
        return new CategoryButtonListener(combo, type, false);
    }

    /**
     * Method to get an action listener for the copy button.
     * @param combo - Category combo box to be updated with newly created object
     * @param type - Type of categories in the category combo
     * @return - Copy button action listener
     */
    protected ActionListener getCopyButtonListener(JComboBox combo, Type type) {
        return new CategoryButtonListener(combo, type, true);
    }

    /**
     * Method to create a DataInputPanel for the object's items.
     * @param category - Object to create an item panel for
     * @return - A DataInputPanel for the object's items
     */
    protected DataInputPanel createItemPanel(Object o) {
        DataInputPanel panel = new ItemPanel(this.editable);
        panel.setValue(o);

        panel.setVisible(this.configuredCheck.isSelected());
        return panel;
    }

    /**
     * Method to get a category combo box with a list of categories for items.
     * @param categoryList - List of categories to be added to the combo box
     * @param selectedCategory - Category to be selected once combo box is
     *            initilized
     * @return Initialized combo box
     */
    protected JComboBox getCategoryComboBox(List<Category> categoryList, Category selectedCategory) {

        List<Category> categoryComboList = new ArrayList<Category>();
        Category category = null;
        Iterator<Category> categoryIter = categoryList.iterator();
        while (categoryIter.hasNext()) {
            category = categoryIter.next();
            if (!(category.getName() == null || "".equals(category.getName()))) {
                // Don't add categories with no name
                categoryComboList.add(category);
            }
        }

        // Initialize the combo box
        this.categoryCombo = new JComboBox(categoryComboList.toArray());
        this.categoryCombo.setSelectedItem(selectedCategory);
        this.categoryCombo.setFont(DeviceConfigurationPropertyPanel.FIELD_FONT);
        this.categoryCombo.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (itemPanel != null) {
                    itemPanel.setVisible(true);
                }
                fireInputUpdate();
            }
        });

        return this.categoryCombo;

    }

    /**
     * Helper method to get a selected category from a category list. If there
     * is no selected category, a new category of the correct type will be
     * returned.
     * @param categoryList - List to search for a selected category
     * @return The selected category or new category if none selected
     */
    private Category getSelectedCategory(List<Category> categoryList) {

        Category category = null;
        Type type = null;
        Iterator<Category> categoryListIter = categoryList.iterator();
        while (categoryListIter.hasNext()) {
            category = categoryListIter.next();
            if (type == null) {
                type = category.getType();
            }
            if (category.isSelected()) {
                return category;
            }
        }

        category = new Category();
        category.setType(type);

        return category;
    }

    /**
     * Helper method to create the main category panel
     * @param categoryList - List of categories for the combo box
     * @return An initialized category panel
     */
    private JPanel createCategoryPanel(List<Category> categoryList) {

        Category selectedCategory = this.getSelectedCategory(categoryList);
        // This panel will be enabled if there is a selected category with a
        // name
        boolean enabled = selectedCategory.getName() != null
                && !("".equals(selectedCategory.getName()));

        JPanel categoryPanel = new JPanel(new GridBagLayout());
        categoryPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        GridBagConstraints constraints = new GridBagConstraints(1,
                                                                1,
                                                                1,
                                                                1,
                                                                0.0,
                                                                0.0,
                                                                GridBagConstraints.WEST,
                                                                GridBagConstraints.NONE,
                                                                new Insets(5, 10, 0, 0),
                                                                5,
                                                                5);

        // Add Category Type label
        JLabel typeLabel = new JLabel("Category Type:");
        typeLabel.setFont(DeviceConfigurationPropertyPanel.FIELD_FONT);
        categoryPanel.add(typeLabel, constraints);

        // Add Category Type Name label
        JLabel type = new JLabel(categoryList.get(0).getType().getName());
        if (categoryList.get(0).getType().getDescription() != null) {
            type.setToolTipText(categoryList.get(0).getType().getDescription());
        }
        type.setFont(DeviceConfigurationPropertyPanel.FIELD_FONT_BOLD);
        constraints.gridx = 2;
        categoryPanel.add(type, constraints);

        // Add Configured check box
        this.configuredCheck = new JCheckBox("Configured");
        this.configuredCheck.setFont(DeviceConfigurationPropertyPanel.FIELD_FONT);
        this.configuredCheck.setSelected(enabled);
        this.configuredCheck.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                configuredCheckChanged();

            }
        });
        constraints.gridx = 3;
        constraints.weightx = 1.0;
        categoryPanel.add(this.configuredCheck, constraints);

        // Add Category Name label
        JLabel nameLabel = new JLabel("Category Name:");
        nameLabel.setFont(DeviceConfigurationPropertyPanel.FIELD_FONT);
        constraints.gridwidth = 1;
        constraints.gridx = 1;
        constraints.gridy = 2;
        constraints.weightx = 0.0;
        constraints.insets = new Insets(5, 10, 10, 0);
        categoryPanel.add(nameLabel, constraints);

        // Add Category Name combo box
        this.categoryCombo = this.getCategoryComboBox(categoryList, selectedCategory);
        this.categoryCombo.setEnabled(enabled);
        this.categoryCombo.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                categoryComboChanged();
            }
        });
        constraints.gridx = 2;
        categoryPanel.add(categoryCombo, constraints);

        // Add button panel
        JPanel buttonPanel = new JPanel(new GridLayout(0, 1));

        // Add Create New button
        this.createButton = new JButton("Create New");
        this.createButton.setFont(DeviceConfigurationPropertyPanel.FIELD_FONT);
        this.createButton.setEnabled(enabled);
        this.createButton.addActionListener(this.getCreateNewButtonListener(categoryCombo,
                                                                            selectedCategory.getType()));

        buttonPanel.add(this.createButton);

        // Add Create New button
        this.copyButton = new JButton("Copy");
        this.copyButton.setFont(DeviceConfigurationPropertyPanel.FIELD_FONT);
        this.copyButton.setEnabled(enabled);
        this.copyButton.addActionListener(this.getCopyButtonListener(categoryCombo,
                                                                     selectedCategory.getType()));
        buttonPanel.add(this.copyButton);

        constraints.gridx = 3;
        constraints.weightx = 1.0;
        categoryPanel.add(buttonPanel, constraints);

        // Create and Add Item Panel
        this.itemPanel = this.createItemPanel(this.categoryCombo.getSelectedItem());

        categoryPanel.add(this.itemPanel, new GridBagConstraints(1,
                                                                 3,
                                                                 3,
                                                                 1,
                                                                 1.0,
                                                                 1.0,
                                                                 GridBagConstraints.WEST,
                                                                 GridBagConstraints.BOTH,
                                                                 new Insets(0, 10, 10, 10),
                                                                 5,
                                                                 5));

        return categoryPanel;
    }

    /**
     * Helper method to enable/disable the components and reset the itemPanel
     * when the configured check box is changed
     */
    private void configuredCheckChanged() {

        boolean selected = this.configuredCheck.isSelected();
        this.categoryCombo.setEnabled(selected);
        this.createButton.setEnabled(selected);

        if (this.categoryCombo.getItemCount() != 0) {
            this.copyButton.setEnabled(selected);
        }

        if (selected && this.categoryCombo.getSelectedItem() != null) {
            this.itemPanel.setValue(this.categoryCombo.getSelectedItem());
            this.itemPanel.setVisible(true);
            fireInputUpdate();
        } else if (!selected) {
            this.itemPanel.setVisible(false);
            fireInputUpdate();
        }

        // Refresh the internal frame that contains this panel to refresh the UI
        CtiUtilities.getParentInternalFrame(this).validate();
    }

    /**
     * Helper method to reset the itemPanel when the category combo box is
     * changed
     */
    private void categoryComboChanged() {
        this.itemPanel.setValue(this.categoryCombo.getSelectedItem());
        this.validate();
    }

    /**
     * Inner class to listen for a button to be clicked. Depending on the
     * button, either a new category is created or a new copy of a category is
     * created. When the user finishes creating the new category and closes the
     * frame, the new category is added to and selected in the combo box.
     */
    private class CategoryButtonListener implements ActionListener, WizardPanelListener {

        private WizardPanel panel = null;
        private JComboBox combo = null;
        private Type type = null;
        private boolean copy = false;

        public CategoryButtonListener(JComboBox combo, Type type, boolean copy) {
            this.combo = combo;
            this.type = type;
            this.copy = copy;
        }

        public void actionPerformed(ActionEvent e) {

            this.panel = new CategoryWizardPanel();
            if (this.copy) {
                // Copy button was clicked
                Category category = (Category) this.combo.getSelectedItem();

                Category newCategory = new Category();
                newCategory.setName(category.getName());
                newCategory.setType(category.getType());

                // Copy each of the items in the item list into the new category
                Iterator<Item> itemIter = category.getItemList().iterator();
                while (itemIter.hasNext()) {
                    Item item = itemIter.next();

                    Item newItem = new Item();
                    newItem.setId(item.getId());
                    newItem.setName(item.getName());
                    newItem.setMaxValue(item.getMaxValue());
                    newItem.setMinValue(item.getMinValue());
                    newItem.setRequired(item.isRequired());
                    newItem.setValue(item.getValue());
                    newItem.setValueType(item.getValueType().toString());

                    newCategory.addItem(newItem);
                }

                this.panel.setValue(newCategory);

            } else {
                // New button was clicked
                Category category = new Category();
                category.setType(this.type);
                category.setItemList(new DeviceConfigurationFuncsImpl().getItemsForCategoryType(this.type.getId()));
                this.panel.setValue(category);
            }

            this.panel.addWizardPanelListener(DatabaseEditor.getInstance());
            // Add this as a listener so we know when the frame is closed
            this.panel.addWizardPanelListener(this);

            // Create and initialize the internal frame
            JInternalFrame frame = new JInternalFrame("Category editor", true, true, true, true);
            frame.setContentPane(this.panel);
            frame.setSize(DatabaseEditor.EDITOR_FRAME_SIZE);
            frame.setVisible(true);

            JDesktopPane pane = CtiUtilities.getDesktopPane(((JButton) e.getSource()));
            pane.add(frame);

            try {
                frame.setSelected(true);
            } catch (PropertyVetoException e1) {
                e1.printStackTrace();
            }

        }

        public void selectionPerformed(WizardPanelEvent e) {

            // If the user clicked the finish button, update the combo box
            if (e.getID() == WizardPanelEvent.FINISH_SELECTION) {
                Category category = (Category) this.panel.getValue(null);

                this.combo.addItem(category);
                this.combo.setSelectedItem(category);
                this.combo.validate();
                
                copyButton.setEnabled(true);
                
            }
        }
    }
}
