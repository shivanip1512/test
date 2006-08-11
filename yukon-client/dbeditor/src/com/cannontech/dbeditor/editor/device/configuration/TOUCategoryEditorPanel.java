package com.cannontech.dbeditor.editor.device.configuration;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;

import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.gui.util.TitleBorder;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.wizard.WizardPanel;
import com.cannontech.common.wizard.WizardPanelEvent;
import com.cannontech.common.wizard.WizardPanelListener;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.cache.functions.DeviceConfigurationFuncs;
import com.cannontech.database.cache.functions.DeviceConfigurationFuncsImpl;
import com.cannontech.database.data.device.configuration.Category;
import com.cannontech.database.data.device.configuration.Item;
import com.cannontech.database.data.device.configuration.Type;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LiteTOUSchedule;
import com.cannontech.database.data.tou.TOUSchedule;
import com.cannontech.dbeditor.DatabaseEditor;
import com.cannontech.dbeditor.wizard.tou.TOUScheduleBasePanel;
import com.cannontech.dbeditor.wizard.tou.TOUScheduleWizardPanel;
import com.cannontech.yukon.IDatabaseCache;

/**
 * Panel class which displays TOU Schedule information for TOU Schedule
 * categories
 */
public class TOUCategoryEditorPanel extends CategoryEditorPanelBase {

    private Category category = null;

    public TOUCategoryEditorPanel(boolean editable) {
        super(editable);
    }

    @Override
    public Object getValue(Object o) {

        if (this.categoryCombo.isEnabled() && this.category != null) {

            DeviceConfigurationFuncs funcs = new DeviceConfigurationFuncsImpl();
            List<Item> itemList = funcs.getItemsForCategoryType(this.category.getTypeId());

            TOUSchedule schedule = (TOUSchedule) this.categoryCombo.getSelectedItem();

            this.category.setName("TOU Schedule");
            this.category.setId(null);
            Item item = itemList.get(0);
            item.setValue(schedule.getScheduleID().toString());
            this.category.setItemList(itemList);

            return this.category;
        }

        return null;
    }

    @Override
    protected DataInputPanel createItemPanel(Object o) {

        this.copyButton.setVisible(false);

        DataInputPanel panel = new TOUScheduleBasePanel();
        panel.setValue(o);
        TitleBorder border = new TitleBorder("Schedule");
        border.setTitleFont(DeviceConfigurationPropertyPanel.TITLE_FONT);
        panel.setBorder(border);

        if (!this.editable) {
            this.disablePanel(panel);
        }

        panel.setVisible(this.configuredCheck.isSelected());

        return panel;

    }

    @Override
    protected JComboBox getCategoryComboBox(List<Category> categoryList, Category selectedCategory) {

        List<TOUSchedule> touScheduleList = this.getTouScheduleList();
        this.categoryCombo = new JComboBox(touScheduleList.toArray());
        this.categoryCombo.setFont(DeviceConfigurationPropertyPanel.FIELD_FONT);
        this.categoryCombo.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (itemPanel != null) {
                    itemPanel.setVisible(true);
                }
                fireInputUpdate();
            }
        });

        this.category = selectedCategory;

        if (selectedCategory.getItemList().size() == 1) {
            Item item = selectedCategory.getItemList().get(0);
            Integer touId = Integer.valueOf(item.getValue());

            Iterator<TOUSchedule> touScheduleListIter = touScheduleList.iterator();
            while (touScheduleListIter.hasNext()) {

                TOUSchedule schedule = touScheduleListIter.next();
                if (schedule.getScheduleID().equals(touId)) {
                    this.categoryCombo.setSelectedItem(schedule);
                    break;
                }
            }
        }

        return this.categoryCombo;
    }

    @Override
    protected ActionListener getCreateNewButtonListener(JComboBox combo, Type type) {
        return new NewScheduleButtonListener(combo);
    }

    /**
     * Helper method to get a list of all TOUSchedules from the cache
     * @return A list of TOUSchedules
     */
    private List<TOUSchedule> getTouScheduleList() {

        List<TOUSchedule> persistantList = new ArrayList<TOUSchedule>();

        IDatabaseCache cache = DefaultDatabaseCache.getInstance();
        List<LiteTOUSchedule> touScheduleList = cache.getAllTOUSchedules();
        Iterator<LiteTOUSchedule> scheduleIter = touScheduleList.iterator();
        while (scheduleIter.hasNext()) {

            LiteTOUSchedule schedule = scheduleIter.next();

            TOUSchedule persistantSchedule = (TOUSchedule) LiteFactory.convertLiteToDBPersAndRetrieve(schedule);
            persistantList.add(persistantSchedule);

        }

        return persistantList;

    }

    /**
     * Helper method to disable all of the components on a JPanel and all of its
     * subpanels
     * @param panel - Panel to disable
     */
    private void disablePanel(JPanel panel) {

        Component[] components = panel.getComponents();
        for (int i = 0; i < components.length; i++) {

            JComponent component = (JComponent) components[i];

            if (component instanceof JPanel) {
                this.disablePanel((JPanel) component);
            } else {
                component.setEnabled(false);
            }
        }

    }

    /**
     * Inner class to listen for the new category button to be clicked. When
     * clicked, a new category wizard frame is opened. When the user finishes
     * creating a new category and closes the frame, the new category is added
     * to and selected in the combo box.
     */
    private class NewScheduleButtonListener implements ActionListener, WizardPanelListener {

        private WizardPanel panel = null;
        private JComboBox combo = null;

        public NewScheduleButtonListener(JComboBox combo) {
            this.combo = combo;
        }

        public void actionPerformed(ActionEvent e) {
            JInternalFrame frame = new JInternalFrame("TOU editor", true, true, true, true);

            this.panel = new TOUScheduleWizardPanel();
            this.panel.addWizardPanelListener(DatabaseEditor.getInstance());
            this.panel.addWizardPanelListener(this);

            TOUSchedule schedule = new TOUSchedule();
            this.panel.setValue(schedule);

            frame.setContentPane(this.panel);
            frame.pack();
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
            if (e.getID() == WizardPanelEvent.FINISH_SELECTION) {
                TOUSchedule schedule = (TOUSchedule) this.panel.getValue(null);

                this.combo.addItem(schedule);
                this.combo.setSelectedItem(schedule);
                this.combo.validate();
            }
        }
    }

}
