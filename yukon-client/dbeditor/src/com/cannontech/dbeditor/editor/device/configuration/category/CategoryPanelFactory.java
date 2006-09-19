package com.cannontech.dbeditor.editor.device.configuration.category;

import com.cannontech.common.device.configuration.model.Type;
import com.cannontech.dbeditor.editor.device.configuration.category.tou.RateScheduleEditorPanel;
import com.cannontech.dbeditor.editor.device.configuration.category.tou.RateScheduleInputPanel;
import com.cannontech.dbeditor.editor.device.configuration.category.tou.TOUScheduleEditorPanel;
import com.cannontech.dbeditor.editor.device.configuration.category.tou.TOUScheduleInputPanel;

/**
 * Factory class to get category panels for different category types
 */
public class CategoryPanelFactory {

    /**
     * Method to create a specific CategoryEditorPanel based on category type
     * @param categoryType - Type of category to get editor panel for
     * @return The CategoryEditorPanel for the category type
     */
    public static CategoryEditorPanel createEditorPanel(Type categoryType) {

        CategoryEditorPanel panel;
        if ("MCT Rate Schedule".equals(categoryType.getName())) {
            panel = new RateScheduleEditorPanel();
        } else if ("MCT TOU".equals(categoryType.getName())) {
            panel = new TOUScheduleEditorPanel();
        } else {
            panel = new CategoryEditorPanelBase();
        }

        return panel;
    }

    /**
     * Method to create a specific CategoryInputPanel based on category type
     * @param categoryType - Type of category to get input panel for
     * @return The CategoryInputPanel for the category type
     */
    public static CategoryInputPanel createInputPanel(Type cateogryType) {

        CategoryInputPanel panel;
        if ("MCT Rate Schedule".equals(cateogryType.getName())) {
            panel = new RateScheduleInputPanel();
        } else if ("MCT TOU".equals(cateogryType.getName())) {
            panel = new TOUScheduleInputPanel();
        } else {
            panel = new CategoryInputPanelBase();
        }

        return panel;
    }
}
