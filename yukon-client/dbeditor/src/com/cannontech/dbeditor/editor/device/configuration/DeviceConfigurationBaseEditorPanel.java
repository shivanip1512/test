package com.cannontech.dbeditor.editor.device.configuration;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;

import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.device.configuration.model.DeviceConfiguration;
import com.cannontech.common.editor.PropertyPanel;
import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.gui.util.TitleBorder;
import com.cannontech.dbeditor.DatabaseEditor;

/**
 * Panel which contains basic information about a device configuration
 */
public class DeviceConfigurationBaseEditorPanel extends DataInputPanel implements CaretListener,
        ActionListener {

    private JTextField name = null;
    private JLabel type = null;

    public DeviceConfigurationBaseEditorPanel() {
        super();
        this.initialize();

    }

    @Override
    public Object getValue(Object o) {

        ((DeviceConfiguration) o).setName(name.getText());

        return o;
    }

    @Override
    public void setValue(Object o) {

        final DeviceConfiguration config = (DeviceConfiguration) o;

        // Add Identification panel
        this.add(this.buildIdentificationPanel(),
                 new GridBagConstraints(1,
                                        1,
                                        1,
                                        1,
                                        1.0,
                                        0.0,
                                        GridBagConstraints.NORTHWEST,
                                        GridBagConstraints.HORIZONTAL,
                                        new Insets(5, 5, 0, 5),
                                        5,
                                        5));

        if (config.getId() != null) {

            // Add assign devices button
            JButton addDeviceButton = new JButton("Assign Devices");
            addDeviceButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    JInternalFrame frame = DatabaseEditor.getInstance().getAvailableEditorFrame();

                    PropertyPanel panel = new DeviceSelectionPropertyPanel();
                    panel.setValue(config);
                    panel.addPropertyPanelListener(DatabaseEditor.getInstance());

                    frame.setContentPane(panel);
                    frame.setSize(DatabaseEditor.EDITOR_FRAME_SIZE);
                    frame.setVisible(true);

                    try {
                        frame.setSelected(true);
                    } catch (PropertyVetoException e1) {
                        CTILogger.error(e1.getMessage(), e1);
                    }

                }
            });
            this.add(addDeviceButton, new GridBagConstraints(1,
                                                             2,
                                                             1,
                                                             1,
                                                             1.0,
                                                             1.0,
                                                             GridBagConstraints.NORTHWEST,
                                                             GridBagConstraints.NONE,
                                                             new Insets(5, 5, 0, 5),
                                                             5,
                                                             5));
        }

        this.name.setText(config.getName());
        this.type.setText(config.getType().getName());
        if (config.getType().getDescription() != null) {
            this.type.setToolTipText(config.getType().getDescription());
        }
    }

    @Override
    public boolean isInputValid() {
        return !("".equals(this.name.getText()));
    }

    public void caretUpdate(CaretEvent e) {
        fireInputUpdate();
    }

    public void actionPerformed(ActionEvent e) {
        fireInputUpdate();
    }

    /**
     * Helper method to initialize this panel
     */
    private void initialize() {
        this.setLayout(new GridBagLayout());
        this.setPreferredSize(new Dimension(410, 480));
    }

    /**
     * Helper method to build and populate the Identification panel
     * @return Identification panel
     */
    private JPanel buildIdentificationPanel() {

        JPanel identificationPanel = new JPanel();
        identificationPanel.setLayout(new GridBagLayout());
        TitleBorder identificationBorder = new TitleBorder("Identification");
        identificationBorder.setTitleFont(DeviceConfigurationPropertyPanel.TITLE_FONT);
        identificationPanel.setBorder(identificationBorder);

        // Add config name label and config name
        JLabel nameLabel = new JLabel("Config Name:");
        nameLabel.setFont(DeviceConfigurationPropertyPanel.FIELD_FONT);
        this.name = new JTextField(20);
        this.name.setFont(DeviceConfigurationPropertyPanel.FIELD_FONT);
        this.name.addCaretListener(this);
        identificationPanel.add(nameLabel, new GridBagConstraints(1,
                                                                  1,
                                                                  1,
                                                                  1,
                                                                  0.0,
                                                                  0.0,
                                                                  GridBagConstraints.WEST,
                                                                  GridBagConstraints.NONE,
                                                                  new Insets(0, 0, 0, 0),
                                                                  5,
                                                                  5));
        identificationPanel.add(name, new GridBagConstraints(2,
                                                             1,
                                                             1,
                                                             1,
                                                             1.0,
                                                             0.0,
                                                             GridBagConstraints.WEST,
                                                             GridBagConstraints.NONE,
                                                             new Insets(0, 0, 0, 0),
                                                             5,
                                                             5));

        // Add config type label and config type
        JLabel typeLabel = new JLabel("Config Type:");
        typeLabel.setFont(DeviceConfigurationPropertyPanel.FIELD_FONT);
        this.type = new JLabel();
        this.type.setFont(DeviceConfigurationPropertyPanel.FIELD_FONT_BOLD);
        identificationPanel.add(typeLabel, new GridBagConstraints(1,
                                                                  2,
                                                                  1,
                                                                  1,
                                                                  0.0,
                                                                  1.0,
                                                                  GridBagConstraints.WEST,
                                                                  GridBagConstraints.NONE,
                                                                  new Insets(0, 0, 0, 0),
                                                                  5,
                                                                  5));
        identificationPanel.add(type, new GridBagConstraints(2,
                                                             2,
                                                             1,
                                                             1,
                                                             1.0,
                                                             1.0,
                                                             GridBagConstraints.WEST,
                                                             GridBagConstraints.NONE,
                                                             new Insets(0, 0, 0, 0),
                                                             5,
                                                             5));

        return identificationPanel;
    }

}
