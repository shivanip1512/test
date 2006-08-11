package com.cannontech.dbeditor.editor.device.configuration;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.cannontech.common.editor.PropertyPanelEvent;
import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.gui.util.DataInputPanelListener;
import com.cannontech.database.data.device.configuration.Category;
import com.cannontech.database.data.device.configuration.DeviceConfiguration;
import com.cannontech.database.data.device.configuration.Type;

/**
 * Panel class which shows all category types of a given category group. Each
 * category type has a CategoryEditorBasePanel to represent it.
 */
public class CategoryGroupEditorPanel extends DataInputPanel implements DataInputPanelListener {

    private List<CategoryEditorPanelBase> categoryPanelList = null;
    private Type categoryType = null;
    private boolean editable;

    public CategoryGroupEditorPanel(boolean editable) {
        this.editable = editable;
        this.initialize();
    }

    @Override
    public Object getValue(Object o) {
        DeviceConfiguration config = (DeviceConfiguration) o;

        CategoryEditorPanelBase panel = null;
        Iterator<CategoryEditorPanelBase> panelIter = this.categoryPanelList.iterator();
        while (panelIter.hasNext()) {
            panel = panelIter.next();
            Category category = (Category) panel.getValue(null);
            config.addCategory(category);
        }

        return config;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setValue(Object o) {

        this.removeAll();

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints(1,
                                                                1,
                                                                1,
                                                                1,
                                                                1.0,
                                                                0.0,
                                                                GridBagConstraints.NORTHWEST,
                                                                GridBagConstraints.HORIZONTAL,
                                                                new Insets(0, 5, 5, 5),
                                                                0,
                                                                0);

        List<Category> categoryList = null;
        Iterator<List<Category>> categoryListIter = ((List<List<Category>>) o).iterator();
        while (categoryListIter.hasNext()) {
            categoryList = categoryListIter.next();

            if (categoryList.size() > 0 && this.categoryType == null) {
                this.categoryType = categoryList.get(0).getType();
            }

            CategoryEditorPanelBase panel = null;

            if ("TOU Schedule".equals(this.categoryType.getName())) {
                panel = new TOUCategoryEditorPanel(this.editable);
            } else {
                panel = new CategoryEditorPanelBase(this.editable);
            }

            panel.setValue(categoryList);
            panel.addDataInputPanelListener(this);
            this.categoryPanelList.add(panel);

            if (!categoryListIter.hasNext()) {
                constraints.weighty = 1.0;
            }

            mainPanel.add(panel, constraints);

            constraints.gridy += 1;

        }

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        this.add(scrollPane, BorderLayout.CENTER);

    }

    /**
     * Method to get the type of this editor panel (only useful when there is
     * one category type on this panel)
     * @return Type
     */
    public Type getType() {
        return this.categoryType;
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
        this.categoryPanelList = new ArrayList<CategoryEditorPanelBase>();
    }

}
