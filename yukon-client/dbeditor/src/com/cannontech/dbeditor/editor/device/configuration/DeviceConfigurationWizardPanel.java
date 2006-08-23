package com.cannontech.dbeditor.editor.device.configuration;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.wizard.WizardPanel;
import com.cannontech.database.cache.functions.DeviceConfigurationFuncs;
import com.cannontech.database.cache.functions.DeviceConfigurationFuncsImpl;
import com.cannontech.database.data.device.configuration.Category;
import com.cannontech.database.data.device.configuration.DeviceConfiguration;
import com.cannontech.database.data.device.configuration.Type;

/**
 * Wizard panel for device configuration creation
 */
public class DeviceConfigurationWizardPanel extends WizardPanel {

    private DeviceConfiguration config = null;
    private List<List<Category>> categoryTypeList = new ArrayList<List<Category>>();
    private DeviceConfigurationWizardTypePanel typePanel = null;

    public DeviceConfigurationWizardPanel() {
        this.config = new DeviceConfiguration();
    }

    public DeviceConfigurationWizardPanel(DeviceConfiguration config) {
        this.config = config;
        this.config.setId(null);
    }

    @Override
    public Dimension getActualSize() {

        setPreferredSize(new java.awt.Dimension(410, 480));

        return getPreferredSize();
    }

    @Override
    public Object getValue(Object o) {
        return this.config;
    }

    @Override
    protected String getHeaderText() {
        return "Device Configuration Setup";
    }

    @Override
    protected DataInputPanel getNextInputPanel(DataInputPanel currentInputPanel) {
        if (currentInputPanel == null) {

            // First panel is the device configuration type panel
            return this.getTypePanel();

        } else if (currentInputPanel instanceof DeviceConfigurationWizardTypePanel) {

            // If we're on the device configuration type panel, the next panel
            // is the device configuration base editor panel

            // Initialize the device configuration and category list for
            // the selected type
            DeviceConfigurationFuncs funcs = new DeviceConfigurationFuncsImpl();
            Integer typeId = (Integer) currentInputPanel.getValue(null);

            if (this.config.getType() == null || !typeId.equals(this.config.getTypeId())) {
                this.config.setType(funcs.loadConfigType(typeId));
                this.initializeCategoryList(this.config.getTypeId());
            }

            DeviceConfigurationBaseEditorPanel panel = new DeviceConfigurationBaseEditorPanel();
            panel.setValue(this.config);

            return panel;

        } else {
            if (currentInputPanel instanceof DeviceConfigurationBaseEditorPanel) {

                // If we're on the device configuration base editor panel, the
                // next panel is the first category group editor panel

                // Get the values from the base typePanel
                this.config = (DeviceConfiguration) currentInputPanel.getValue(this.config);

                List<Category> currentCategoryList = this.categoryTypeList.get(0);
                CategoryGroupEditorPanel panel = new CategoryGroupEditorPanel(false);
                panel.setValue(this.wrapCategoryList(currentCategoryList));
                return panel;

            } else {
                // We're on the category group editor panel, we have to get the
                // next category group editor panel

                this.config = (DeviceConfiguration) ((CategoryGroupEditorPanel) currentInputPanel).getValue(this.config);

                Integer index = this.getNextCategoryListIndex(((CategoryGroupEditorPanel) currentInputPanel).getType());
                List<Category> categoryList = this.categoryTypeList.get(index);

                CategoryGroupEditorPanel panel = new CategoryGroupEditorPanel(false);
                panel.setValue(this.wrapCategoryList(categoryList));
                return panel;
            }
        }
    }

    @Override
    protected boolean isLastInputPanel(DataInputPanel currentPanel) {

        // If we're on the device configuration base editor panel and there are
        // no categories or if we are on the last category group editor panel,
        // return true
        if ((currentPanel instanceof DeviceConfigurationBaseEditorPanel && this.categoryTypeList.size() == 0)
                || (currentPanel instanceof CategoryGroupEditorPanel && this.getNextCategoryListIndex(((CategoryGroupEditorPanel) currentPanel).getType()) == this.categoryTypeList.size())) {

            if (currentPanel instanceof DeviceConfigurationBaseEditorPanel) {
                // Get the values from the base typePanel
                this.config = (DeviceConfiguration) currentPanel.getValue(this.config);
            } else if (currentPanel instanceof CategoryGroupEditorPanel) {
                // Get the selected category
                this.config = (DeviceConfiguration) ((CategoryGroupEditorPanel) currentPanel).getValue(this.config);
            }

            return true;
        } else {
            return false;
        }
    }

    /**
     * Helper method to get the DeviceConfigurationWizardTypePanel
     * @return DeviceConfigurationWizardTypePanel
     */
    private DeviceConfigurationWizardTypePanel getTypePanel() {
        if (this.typePanel == null) {
            this.typePanel = new DeviceConfigurationWizardTypePanel();
        }

        return this.typePanel;
    }

    /**
     * Helper method to get the index of the next category list in the category
     * type list based on the category type of the previous category list
     * @param categoryType - Category type of the previous category list
     * @return The index of the next category list
     */
    private Integer getNextCategoryListIndex(Type categoryType) {

        int index = 0;
        Iterator<List<Category>> categoryTypeListIter = this.categoryTypeList.iterator();
        while (categoryTypeListIter.hasNext()) {

            List<Category> list = categoryTypeListIter.next();

            if (list.get(0).getType().getId() == categoryType.getId()) {
                return index + 1;
            }
            index++;
        }
        return null;
    }

    /**
     * Helper method to populate the category data list based on the type of
     * config
     * @param configTypeId - Type of config
     */
    private void initializeCategoryList(Integer configTypeId) {
        DeviceConfigurationFuncs funcs = new DeviceConfigurationFuncsImpl();

        List<Type> categoryTypeList = funcs.getCategoryTypesForConfigType(configTypeId);
        Iterator<Type> categoryTypeIter = categoryTypeList.iterator();
        while (categoryTypeIter.hasNext()) {
            Type categoryType = categoryTypeIter.next();

            List<Category> categoryList = new ArrayList<Category>();
            // Only add categories that should be shown in the wizard
            if (categoryType.getLevel().showInWizard()) {

                Category category = this.config.getCategoryByType(categoryType.getId());
                if (category == null) {
                    category = new Category();
                    category.setType(categoryType);
                    category.setItemList(funcs.getItemsForCategoryType(categoryType.getId()));
                }
                category.setSelected(true);

                categoryList.add(category);
                List<Category> list = funcs.getCategoriesForType(category.getTypeId());
                // list.remove(category);
                categoryList.addAll(list);

                this.categoryTypeList.add(categoryList);
            }

        }
    }

    /**
     * Helper method to wrap a category list in a list
     * @param data - Category list to be wrapped
     * @return A list of category list
     */
    private List<List<Category>> wrapCategoryList(List<Category> categoryList) {
        List<List<Category>> dataList = new ArrayList<List<Category>>();
        dataList.add(categoryList);

        return dataList;
    }

}
