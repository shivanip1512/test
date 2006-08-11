package com.cannontech.dbeditor.editor.device.configuration;

import java.awt.BorderLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.cannontech.common.editor.PropertyPanel;
import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.database.cache.functions.DeviceConfigurationFuncs;
import com.cannontech.database.cache.functions.DeviceConfigurationFuncsImpl;
import com.cannontech.database.data.device.configuration.Category;
import com.cannontech.database.data.device.configuration.DeviceConfiguration;
import com.cannontech.database.data.device.configuration.Type;

/**
 * Main panel for editing device configurations
 */
public class DeviceConfigurationPropertyPanel extends PropertyPanel {

    private List<DataInputPanel> inputPanels = null;
    private List<String> inputPanelTabNames = null;

    // Static fonts used in device configuration UI
    public static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 14);
    public static final Font FIELD_FONT_BOLD = new Font("Dialog", Font.BOLD, 14);
    public static final Font FIELD_FONT = new Font("Dialog", Font.PLAIN, 14);

    public DeviceConfigurationPropertyPanel() {
        this.initialize();
    }

    @Override
    public void setValue(Object val) {

        DeviceConfiguration config = (DeviceConfiguration) val;

        setOriginalObjectToEdit(config);

        this.createCategoryInputPanels(config);

        this.inputPanels.get(0).setValue(config);

        String[] tabNames = getTabNames();
        DataInputPanel[] inputPanels = getInputPanels();

        // only add the tabs if we have 0
        if (getTabbedPane().getTabRunCount() == 0) {
            for (int i = 0; i < tabNames.length; i++) {

                getTabbedPane().addTab(tabNames[i], inputPanels[i]);

                inputPanels[i].addDataInputPanelListener(this);
            }
        }

        getTabbedPane().setSelectedIndex(0);
        this.removeAll();
        this.setLayout(new java.awt.BorderLayout());
        add(getTabbedPane(), BorderLayout.CENTER);
        add(getPropertyButtonPanel(), BorderLayout.SOUTH);

        setChanged(false);

        return;
    }

    @Override
    public String toString() {
        return "Device Configuration Editor";
    }

    @Override
    protected DataInputPanel[] getInputPanels() {
        return inputPanels.toArray(new DataInputPanel[] {});
    }

    @Override
    protected String[] getTabNames() {
        return inputPanelTabNames.toArray(new String[] {});
    }

    /**
     * Helper method to initialize this panel.
     */
    private void initialize() {

        this.inputPanels = inputPanels = new ArrayList<DataInputPanel>();
        this.inputPanels.add(new DeviceConfigurationBaseEditorPanel());
        this.inputPanelTabNames = new ArrayList<String>();
        this.inputPanelTabNames.add("General");

    }

    /**
     * Helper method to create and populate the category panels for the tabbed
     * pane
     * @param config - DeviceConfiguration to get the categories from
     */
    private void createCategoryInputPanels(DeviceConfiguration config) {

        DeviceConfigurationFuncs funcs = new DeviceConfigurationFuncsImpl();

        List<Type> categoryTypeList = funcs.getCategoryTypesForConfigType(config.getTypeId());
        List<Category> configCategoryList = config.getCategoryList();

        // categoryMap is a map with key: category group (if available) or
        // category type name and value: A list of list of category in the
        // group. The outer list contains one list for each category type in the
        // group or one list if there is no group. The inner list contains all
        // of the existing categories of a given type
        Map<String, List<List<Category>>> categoryMap = new LinkedHashMap<String, List<List<Category>>>();

        // Iterate through all of the possible category types for this config
        Iterator<Type> categoryTypeIter = categoryTypeList.iterator();
        while (categoryTypeIter.hasNext()) {
            Type type = categoryTypeIter.next();

            // Determine the categoryKey - either the group or the type name.
            // This will be used for the tab name
            String categoryKey = type.getGroup();
            if (categoryKey == null) {
                categoryKey = type.getName();
            }
            // Get the current category list from the existing categoryMap if
            // found or add the category group / type to the map
            List<List<Category>> currCategoryList = categoryMap.get(categoryKey);
            if (currCategoryList == null) {
                currCategoryList = new ArrayList<List<Category>>();
                categoryMap.put(categoryKey, currCategoryList);
            }

            boolean categoryFound = false;
            Category category = null;

            // Iterate through all of the categories in the config's
            // categoryList. If a category of the current category type is
            // found, get all of the categories for the type and select the
            // category found.
            Iterator<Category> configCategoryListIter = configCategoryList.iterator();
            while (configCategoryListIter.hasNext()) {
                category = configCategoryListIter.next();

                if (category.getTypeId() == type.getId()) {

                    categoryFound = true;
                    List<Category> categoryList = funcs.getCategoriesForType(category.getTypeId());
                    Iterator<Category> iter = categoryList.iterator();
                    while (iter.hasNext()) {
                        Category currentCategory = iter.next();

                        if (currentCategory.getId().equals(category.getId())) {
                            currentCategory.setSelected(true);
                            break;
                        }

                    }

                    currCategoryList.add(categoryList);
                    break;
                }
            }

            // If the config has no categories for the given group / type,
            // create a category list and add it to the current category list
            if (!categoryFound) {

                List<Category> categoryList = new ArrayList<Category>();

                category = new Category();
                category.setType(type);
                category.setSelected(true);
                categoryList.add(category);

                categoryList.addAll(funcs.getCategoriesForType(category.getTypeId()));

                currCategoryList.add(categoryList);
            }
        }

        // Iterate through the categoryMap keys (category groups / types) and
        // create a editor panel for each - this will create one tab for each
        // category group or type
        Iterator<String> categoryGroupIter = categoryMap.keySet().iterator();
        while (categoryGroupIter.hasNext()) {

            String key = categoryGroupIter.next();

            CategoryGroupEditorPanel categoryPanel = new CategoryGroupEditorPanel(false);
            categoryPanel.setValue(categoryMap.get(key));
            this.inputPanels.add(categoryPanel);
            this.inputPanelTabNames.add(key);
        }
    }

}
