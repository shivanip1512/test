package com.cannontech.dbeditor.editor.device.configuration;

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

import com.cannontech.common.editor.PropertyPanelEvent;
import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.gui.util.DataInputPanelListener;
import com.cannontech.database.data.device.configuration.Category;
import com.cannontech.database.data.device.configuration.Type;

/**
 * Input panel for category.
 */
public class CategoryInputPanel extends DataInputPanel implements DataInputPanelListener {

    private JTextField categoryName = null;
    private Type type = null;
    private Category category = new Category();

    private ItemPanel itemPanel = null;

    public CategoryInputPanel() {
        super();
        this.initialize();

    }

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

        JPanel categoryPanel = new JPanel(new GridBagLayout());

        // Add Category Type
        this.type = this.category.getType();
        JLabel typeLabel = new JLabel("Category Type:");
        typeLabel.setFont(DeviceConfigurationPropertyPanel.FIELD_FONT);
        categoryPanel.add(typeLabel, new GridBagConstraints(1,
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
        JLabel type = new JLabel(this.category.getType().getName());
        type.setFont(DeviceConfigurationPropertyPanel.FIELD_FONT_BOLD);
        categoryPanel.add(type, new GridBagConstraints(2,
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
        categoryPanel.add(nameLabel, new GridBagConstraints(1,
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
        this.categoryName.setText(this.category.getName());
        this.categoryName.addCaretListener(new CaretListener() {
            public void caretUpdate(CaretEvent e) {
                fireInputUpdate();
            }
        });
        this.categoryName.setFont(DeviceConfigurationPropertyPanel.FIELD_FONT);
        categoryPanel.add(this.categoryName, new GridBagConstraints(2,
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

        // Add Item Panel
        this.itemPanel = new ItemPanel(true);
        this.itemPanel.setValue(this.category);
        this.itemPanel.addDataInputPanelListener(this);
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

        JScrollPane categoryScroll = new JScrollPane(categoryPanel);

        this.add(categoryScroll, BorderLayout.CENTER);

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
            this.categoryName.requestFocus();
            return false;
        } else if (this.itemPanel != null && !this.itemPanel.isInputValid()) {
            this.setErrorString(this.itemPanel.getErrorString());
            return false;
        }

        return true;
    }

    /**
     * Helper method to initialize this panel
     */
    private void initialize() {
        this.setLayout(new BorderLayout());
    }

    /**
     * Implemented for DataInputPanelListener
     */
    public void inputUpdate(PropertyPanelEvent event) {
        fireInputUpdate();
    }

}
