package com.cannontech.dbeditor.editor.device.configuration.category;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import com.cannontech.common.device.configuration.model.Category;
import com.cannontech.common.device.configuration.model.Type;
import com.cannontech.common.editor.PropertyPanelEvent;
import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.gui.util.DataInputPanelListener;
import com.cannontech.dbeditor.editor.device.configuration.DeviceConfigurationPropertyPanel;

/**
 * Input panel for category.
 */
abstract public class CategoryInputPanel extends DataInputPanel implements DataInputPanelListener {

    private Type type = null;
    private Category category = new Category();

    private JPanel categoryPanel = null;
    private JTextField categoryName = null;
    private JLabel typeLabel = null;
    private DataInputPanel itemPanel = null;

    public CategoryInputPanel() {
        this.initialize();
    }

    /**
     * Method to create a DataInputPanel for the category's items.
     * @return - A DataInputPanel for the category's items
     */
    abstract protected DataInputPanel createItemPanel();

    @Override
    public Object getValue(Object o) {

        o = this.category;

        this.category.setName(this.categoryName.getText());
        this.category.setType(this.type);

        this.itemPanel.getValue(this.category);

        return this.category;
    }

    @Override
    public void setValue(Object o) {

        this.category = (Category) o;

        this.type = this.category.getType();

        this.typeLabel.setText(this.category.getType().getDisplayName());
        this.typeLabel.setToolTipText(this.category.getType().getDescription());
        this.categoryName.setText(this.category.getName());

        this.getItemPanel().addDataInputPanelListener(this);
        this.getItemPanel().setValue(this.category);

    }

    @Override
    /**
     * This method checks all of the required items and the category name to
     * make sure each has a value.
     * @return True if values are present
     */
    public boolean isInputValid() {

        if (this.itemPanel == null) {
            return false;
        } else if (this.categoryName != null && this.categoryName.getText().equals("")) {
            this.setErrorString("Category Name is a required field.");
            return false;
        } else if (this.itemPanel != null && !this.itemPanel.isInputValid()) {
            this.setErrorString(this.itemPanel.getErrorString());
            return false;
        }

        return true;
    }

    /**
     * Implemented for DataInputPanelListener
     */
    public void inputUpdate(PropertyPanelEvent event) {
        fireInputUpdate();
    }

    /**
     * Helper method to initialize this panel
     */
    private void initialize() {
        this.setLayout(new BorderLayout());

        this.categoryPanel = new JPanel(new GridBagLayout());

        // Add Category Type
        this.type = this.category.getType();
        JLabel typeLabel = new JLabel("Category Type:");
        typeLabel.setFont(DeviceConfigurationPropertyPanel.FIELD_FONT);
        this.categoryPanel.add(typeLabel, new GridBagConstraints(1,
                                                                 1,
                                                                 1,
                                                                 1,
                                                                 0.0,
                                                                 0.0,
                                                                 GridBagConstraints.WEST,
                                                                 GridBagConstraints.NONE,
                                                                 new Insets(5, 10, 5, 0),
                                                                 5,
                                                                 5));
        this.typeLabel = new JLabel();
        this.typeLabel.setFont(DeviceConfigurationPropertyPanel.FIELD_FONT_BOLD);
        this.categoryPanel.add(this.typeLabel, new GridBagConstraints(2,
                                                                      1,
                                                                      1,
                                                                      1,
                                                                      0.0,
                                                                      0.0,
                                                                      GridBagConstraints.WEST,
                                                                      GridBagConstraints.NONE,
                                                                      new Insets(0, 5, 5, 0),
                                                                      5,
                                                                      5));

        // Add Category Name
        JLabel nameLabel = new JLabel("Category Name:");
        nameLabel.setFont(DeviceConfigurationPropertyPanel.FIELD_FONT);
        this.categoryPanel.add(nameLabel, new GridBagConstraints(1,
                                                                 2,
                                                                 1,
                                                                 1,
                                                                 0.0,
                                                                 0.0,
                                                                 GridBagConstraints.WEST,
                                                                 GridBagConstraints.BOTH,
                                                                 new Insets(0, 10, 5, 0),
                                                                 5,
                                                                 5));

        this.categoryName = new JTextField(20);
        this.categoryName.addCaretListener(new CaretListener() {
            public void caretUpdate(CaretEvent e) {
                fireInputUpdate();
            }
        });
        this.categoryName.setFont(DeviceConfigurationPropertyPanel.FIELD_FONT);
        this.categoryPanel.add(this.categoryName, new GridBagConstraints(2,
                                                                         2,
                                                                         1,
                                                                         1,
                                                                         0.0,
                                                                         0.0,
                                                                         GridBagConstraints.WEST,
                                                                         GridBagConstraints.NONE,
                                                                         new Insets(0, 5, 5, 0),
                                                                         5,
                                                                         5));

        // Add item panel
        this.categoryPanel.add(this.getItemPanel(),
                               new GridBagConstraints(1,
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

        JScrollPane categoryScroll = new JScrollPane(categoryPanel);
        categoryScroll.getVerticalScrollBar().setUnitIncrement(10);

        this.add(categoryScroll, BorderLayout.CENTER);
    }

    /**
     * Helper method to get the itemPanel, creating one if one hasn't already
     * been created
     * @return The itemPanel
     */
    private DataInputPanel getItemPanel() {

        if (this.itemPanel == null) {
            this.itemPanel = this.createItemPanel();
        }

        return this.itemPanel;
    }

}
