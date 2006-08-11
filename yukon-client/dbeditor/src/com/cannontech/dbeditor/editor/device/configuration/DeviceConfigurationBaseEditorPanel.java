package com.cannontech.dbeditor.editor.device.configuration;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;

import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.gui.util.TitleBorder;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.data.device.configuration.DeviceConfiguration;

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
            JButton addDeviceButton = new JButton("Assign Devices");
            addDeviceButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    JInternalFrame frame = new JInternalFrame("Device Assignment",
                                                              true,
                                                              true,
                                                              true,
                                                              true);

                    JPanel panel = new DeviceSelectionPanel(config);

                    frame.setContentPane(panel);
                    frame.setSize(400, 400);
                    frame.setVisible(true);

                    JDesktopPane pane = CtiUtilities.getDesktopPane(((JButton) e.getSource()));
                    pane.add(frame);

                    try {
                        frame.setSelected(true);
                    } catch (PropertyVetoException e1) {
                        e1.printStackTrace();
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

    public void caretUpdate(CaretEvent e) {
        fireInputUpdate();
    }

    public void actionPerformed(ActionEvent e) {
        fireInputUpdate();
    }

    @Override
    public boolean isInputValid() {
        return !("".equals(this.name.getText()));
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
