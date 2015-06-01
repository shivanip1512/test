package com.cannontech.dbeditor.wizard.changetype.device;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import com.cannontech.common.bulk.service.ChangeDeviceTypeService.ChangeDeviceTypeInfo;
import com.cannontech.common.editor.EditorInputValidationException;
import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.pao.PaoType;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.device.range.DlcAddressRangeService;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.yukon.IDatabaseCache;

public class MctOptionPanel extends DataInputPanel implements CaretListener {
    private JLabel addressLabel;
    private JTextField addressTextField;
    private JLabel routeLabel;
    private JComboBox<LiteYukonPAObject> routeComboBox;
    private Font textFont = new Font("dialog", 0, 14);
    private PaoType paoType;
    
    private final DlcAddressRangeService dlcAddressRangeService =
            YukonSpringHook.getBean("dlcAddressRangeService", DlcAddressRangeService.class);
    
    public MctOptionPanel() {
        super();
        initialize();
    }
    
    private void initialize() {
        setLayout(new GridBagLayout());
        setSize(351, 264);
        setPreferredSize(new Dimension(350, 260));
        setBorder(new TitledBorder("PLC Meter Settings"));

        GridBagConstraints addressLabelConstraint = new GridBagConstraints();
        addressLabelConstraint.gridx = 0; 
        addressLabelConstraint.gridy = 0;
        addressLabelConstraint.anchor = GridBagConstraints.WEST;
        addressLabelConstraint.insets = new Insets(3, 3, 3, 3);
        add(getAddressLabel(), addressLabelConstraint);
        
        GridBagConstraints addressTextFieldConstraint = new GridBagConstraints();
        addressTextFieldConstraint.gridx = 1; 
        addressTextFieldConstraint.gridy = 0;
        addressTextFieldConstraint.fill = GridBagConstraints.BOTH;
        addressTextFieldConstraint.anchor = GridBagConstraints.WEST;
        addressTextFieldConstraint.insets = new Insets(3, 3, 3, 3);
        add(getAddressTextField(), addressTextFieldConstraint);


        java.awt.GridBagConstraints constraintsRouteLabel = new java.awt.GridBagConstraints();
        constraintsRouteLabel.gridx = 0;
        constraintsRouteLabel.gridy = 1;
        constraintsRouteLabel.weightx = 1;
        constraintsRouteLabel.anchor = java.awt.GridBagConstraints.WEST;
        constraintsRouteLabel.insets = new java.awt.Insets(5, 5, 5, 5);
        add(getRouteLabel(), constraintsRouteLabel);

        java.awt.GridBagConstraints constraintsRouteComboBox = new java.awt.GridBagConstraints();
        constraintsRouteComboBox.gridx = 1;
        constraintsRouteComboBox.gridy = 1;
        constraintsRouteComboBox.weightx = 1;
        constraintsRouteComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
        constraintsRouteComboBox.anchor = java.awt.GridBagConstraints.WEST;
        constraintsRouteComboBox.insets = new java.awt.Insets(5, 5, 5, 5);
        add(getRouteComboBox(), constraintsRouteComboBox);
        
        init();
    }
    
    private JComboBox<LiteYukonPAObject> getRouteComboBox() {
        if (routeComboBox == null) {
            try {
                routeComboBox = new JComboBox<LiteYukonPAObject>();
                routeComboBox.setName("RouteComboBox");
            } catch (java.lang.Throwable e) {
                handleException(e);
            }
        }
        return routeComboBox;
    }

    private JLabel getRouteLabel() {
        if (routeLabel == null) {
            try {
                routeLabel = new JLabel();
                routeLabel.setName("RouteLabel");
                routeLabel.setFont(new java.awt.Font("dialog", 0, 14));
                routeLabel.setText("Communication Route:");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return routeLabel;
    }
    
    private void handleException(Throwable exception) {

        /* Uncomment the following lines to print uncaught exceptions to stdout */
        com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
        com.cannontech.clientutils.CTILogger.error(exception.getMessage(), exception);
    }

    
    private void init() {
        IDatabaseCache cache = DefaultDatabaseCache.getInstance();
        synchronized (cache) {
            List<LiteYukonPAObject> allRoutes = cache.getAllRoutes();
            if (getRouteComboBox().getModel().getSize() > 0)
                getRouteComboBox().removeAllItems();

            for (LiteYukonPAObject liteRoute : allRoutes) {
                if (liteRoute.getPaoType() == PaoType.ROUTE_CCU || liteRoute.getPaoType() == PaoType.ROUTE_MACRO) {
                    getRouteComboBox().addItem(liteRoute);
                }
            }
        }
    }
    
    @Override
    public void setFirstFocus() {
        /* Make sure that when its time to display this panel, the focus starts in the top component */
        SwingUtilities.invokeLater( new Runnable() {
            @Override
            public void run() {
                getAddressTextField().requestFocus();
            }
        });
    }

    @Override
    public void caretUpdate(CaretEvent e) {
        fireInputUpdate();
    }

    @Override
    public Object getValue(Object o) throws EditorInputValidationException {
        try {
            int address = Integer.parseInt(getAddressTextField().getText());
            if (!dlcAddressRangeService.isValidEnforcedAddress(paoType, address)) {
                
                String rangeString = dlcAddressRangeService.rangeStringEnforced(paoType);
                throw new EditorInputValidationException("Invalid address. Device address range: " + rangeString);
            }
        } catch (NumberFormatException e) {
            throw new EditorInputValidationException("Physical address must be an integer.");
        }
        int routeId = ((LiteYukonPAObject) getRouteComboBox().getSelectedItem()).getYukonID();
        Integer address = new Integer(getAddressTextField().getText());
        ChangeDeviceTypeInfo info = new ChangeDeviceTypeInfo(address, routeId);
        return info;
    }

    @Override
    public void setValue(Object o) {
    }

    public JTextField getAddressTextField() {
        if(addressTextField == null) {
            addressTextField = new JTextField();
            addressTextField.setName("addressTextField");
            addressTextField.setColumns(30);
        }
        return addressTextField;
    }

    public JLabel getAddressLabel() {
        if(addressLabel == null) {
            addressLabel = new JLabel();
            addressLabel.setFont(textFont);
            addressLabel.setText("Physical Address:");
        }
        return addressLabel;
    }
    
    public void setPaoType(PaoType paoType) {
        this.paoType = paoType;
    }

}