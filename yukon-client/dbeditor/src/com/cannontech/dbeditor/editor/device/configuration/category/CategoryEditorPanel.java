package com.cannontech.dbeditor.editor.device.configuration.category;

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
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.device.configuration.model.Category;
import com.cannontech.common.device.configuration.model.Type;
import com.cannontech.common.device.configuration.service.DeviceConfigurationFuncsImpl;
import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.wizard.WizardPanel;
import com.cannontech.common.wizard.WizardPanelEvent;
import com.cannontech.common.wizard.WizardPanelListener;
import com.cannontech.dbeditor.DatabaseEditor;
import com.cannontech.dbeditor.editor.device.configuration.DeviceConfigurationPropertyPanel;

/**
 * Panel used to edit a category
 */
public abstract class CategoryEditorPanel extends DataInputPanel {

    private JCheckBox configuredCheck = null;
    private JLabel typeNameLabel = null;
    private JComboBox categoryCombo = null;
    private JButton createButton = null;
    private JButton copyButton = null;
    private DataInputPanel itemPanel = null;

    public CategoryEditorPanel() {
        this.initialize();
    }

    /**
     * Method to create a DataInputPanel for the category's items.
     * @return - A DataInputPanel for the category's items
     */
    abstract protected DataInputPanel createItemPanel();

    /**
     * Method to create a WizardPanel for the category
     * @return - A DataInputPanel for the category
     */
    abstract protected WizardPanel createWizardPanel();

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

            Category selectedCategory = this.getSelectedCategory(categoryList);

            this.typeNameLabel.setText(categoryList.get(0).getType().getDisplayName());
            if (categoryList.get(0).getType().getDescription() != null) {
                this.typeNameLabel.setToolTipText(categoryList.get(0).getType().getDescription());
            }

            // This panel will be enabled if there is a selected category with a
            // name
            boolean enabled = selectedCategory.getName() != null
                    && !("".equals(selectedCategory.getName()));
            this.configuredCheck.setSelected(enabled);

            // Populate the category combo box
            Category category = null;
            Iterator<Category> categoryIter = categoryList.iterator();
            while (categoryIter.hasNext()) {
                category = categoryIter.next();
                if (!(category.getName() == null || "".equals(category.getName()))) {
                    // Don't add categories with no name
                    this.categoryCombo.addItem(category);
                }
            }
            this.categoryCombo.setSelectedItem(selectedCategory);
            this.categoryCombo.setEnabled(enabled);

            this.createButton.setEnabled(enabled);
            this.createButton.addActionListener(new CategoryButtonListener(categoryCombo,
                                                                           selectedCategory.getType(),
                                                                           false));
            this.copyButton.setEnabled(enabled);
            this.copyButton.addActionListener(new CategoryButtonListener(categoryCombo,
                                                                         selectedCategory.getType(),
                                                                         true));

            this.itemPanel.setValue(this.categoryCombo.getSelectedItem());
            this.itemPanel.setVisible(enabled);
        }
    }

    @Override
    public boolean isInputValid() {

        if (this.configuredCheck != null && this.categoryCombo != null) {

            if (!(!this.configuredCheck.isSelected() || (this.configuredCheck.isSelected() && this.categoryCombo.getSelectedItem() != null))) {

                this.setErrorString("You must select a valid 'Category Name' or uncheck "
                        + "'Configured' for category: " + this.typeNameLabel.getText());
                return false;
            }
            return true;
        }

        return false;
    }

    /**
     * Method to initialize this panel
     */
    public void initialize() {

        this.setLayout(new BorderLayout());

        JPanel categoryPanel = new JPanel(new GridBagLayout());
        categoryPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

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
        this.typeNameLabel = new JLabel();
        this.typeNameLabel.setFont(DeviceConfigurationPropertyPanel.FIELD_FONT_BOLD);
        constraints.gridx = 2;
        categoryPanel.add(this.typeNameLabel, constraints);

        // Add Configured check box
        this.configuredCheck = new JCheckBox("Configured");
        this.configuredCheck.setFont(DeviceConfigurationPropertyPanel.FIELD_FONT);
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
        this.categoryCombo = new JComboBox();
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
        buttonPanel.add(this.createButton);

        // Add Create New button
        this.copyButton = new JButton("Copy");
        this.copyButton.setFont(DeviceConfigurationPropertyPanel.FIELD_FONT);
        buttonPanel.add(this.copyButton);

        constraints.gridx = 3;
        constraints.weightx = 1.0;
        categoryPanel.add(buttonPanel, constraints);

        // Create and Add Item Panel
        this.itemPanel = this.createItemPanel();
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

        this.add(categoryPanel, BorderLayout.CENTER);
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
        } else if (!selected) {
            this.itemPanel.setVisible(false);
        }
        fireInputUpdate();
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
     * frame, the new category is added to and selected in the category combo
     * box.
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

            this.panel = createWizardPanel();
            if (this.copy) {
                // Copy button was clicked
                Category category = (Category) this.combo.getSelectedItem();
                this.panel.setValue(category.clone());

            } else {
                // New button was clicked
                Category category = new Category();
                category.setType(this.type);
                category.setItemList(new DeviceConfigurationFuncsImpl().getItemsForCategoryType(this.type.getId()));
                category.restoreDefaultValues();
                this.panel.setValue(category);
            }

            // Add the DatabaseEditor as a listener for wizard functionality
            this.panel.addWizardPanelListener(DatabaseEditor.getInstance());
            // Add this as a listener so we know when the frame is closed
            this.panel.addWizardPanelListener(this);

            // Create and initialize the internal frame
            JInternalFrame frame = new JInternalFrame("Category editor", true, true, true, true);
            ImageIcon editorIcon = new ImageIcon(java.awt.Toolkit.getDefaultToolkit()
                                                                 .getImage(DatabaseEditor.DBEDITOR_IMG_16));
            frame.setFrameIcon(editorIcon);

            // Add the new internal frame to the existing desktop pane
            JDesktopPane pane = CtiUtilities.getDesktopPane(((JButton) e.getSource()));
            pane.add(frame);

            frame.setContentPane(this.panel);
            frame.setSize(DatabaseEditor.EDITOR_FRAME_SIZE);
            frame.setVisible(true);

            try {
                frame.setSelected(true);
            } catch (PropertyVetoException e1) {
                CTILogger.error(e1.getMessage(), e1);
            }

        }

        public void selectionPerformed(WizardPanelEvent e) {

            // If the user clicked the finish button, update the combo box.
            // Ignore other wizard events
            if (e.getID() == WizardPanelEvent.FINISH_SELECTION) {
                Category category = (Category) this.panel.getValue(null);

                this.combo.addItem(category);
                this.combo.setSelectedItem(category);
                this.combo.validate();

                copyButton.setEnabled(true);

                itemPanel.setVisible(true);
                fireInputUpdate();
            }
        }
    }
}
