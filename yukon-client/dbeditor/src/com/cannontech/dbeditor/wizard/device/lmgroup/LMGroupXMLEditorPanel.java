package com.cannontech.dbeditor.wizard.device.lmgroup;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.*;
import java.util.List;

import javax.swing.*;

import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.gui.util.XmlParameterTableModel;
import com.cannontech.database.data.device.lm.LMGroupXML;
import com.cannontech.database.data.device.lm.LMxmlParameter;
import com.cannontech.database.data.device.lm.LMGroupXML.XmlType;

public class LMGroupXMLEditorPanel extends DataInputPanel implements ActionListener, MouseListener {
	
    //Drop down is for when multiple types is supported.
	private JComboBox xmlTypeDropDown;
	
	private JScrollPane parameterScrollPane = null;
	private JTable parameterTable = null;
	private JPanel operationPanel = null;
	private JLabel parameterNameLabel = null;
    private JLabel parameterValueLabel = null;
	private JTextField parameterNameTextField = null;
	private JTextField parameterValueTextField = null;
	private JButton addButton = null;
	private JButton removeButton = null;
	private JButton updateButton = null;
	
	private int groupId;
	private XmlType xmlType;
	
	public LMGroupXMLEditorPanel() {
		super();
		initialize();
	}
	
    public XmlType getXmlType() {
        return xmlType;
    }

    public void setXmlType(XmlType xmlType) {
        this.xmlType = xmlType;
    }    
	
	/**
	 * Initialize the class.
	 */
	private void initialize() {
        setName("LMGroupXMLEditorPanel");
        setLayout(new java.awt.GridBagLayout());
        
        GridBagConstraints constraintsComponentsScrollPane = new GridBagConstraints();
        constraintsComponentsScrollPane.gridx = 0; constraintsComponentsScrollPane.gridy = 0;
        constraintsComponentsScrollPane.fill = GridBagConstraints.BOTH;
        constraintsComponentsScrollPane.anchor = GridBagConstraints.WEST;
        constraintsComponentsScrollPane.weightx = 1.0;
        constraintsComponentsScrollPane.weighty = 5.0;
        constraintsComponentsScrollPane.ipadx = 218;
        constraintsComponentsScrollPane.insets = new Insets(11, 4, 8, 5);
        add(getParameterScrollPane(), constraintsComponentsScrollPane);
            
            
        getParameterTable().setModel(new XmlParameterTableModel());
        ((XmlParameterTableModel)getParameterTable().getModel()).makeTable();

        getParameterTable().getColumnModel().getColumn(1).setWidth(200);
        getParameterTable().getColumnModel().getColumn(1).setPreferredWidth(200);
        getParameterTable().revalidate();
        getParameterTable().repaint();
	    
        java.awt.GridBagConstraints constraintsJPanelOperations = new java.awt.GridBagConstraints();
        constraintsJPanelOperations.gridx = 0; constraintsJPanelOperations.gridy = 1;
        constraintsJPanelOperations.fill = java.awt.GridBagConstraints.HORIZONTAL;
        constraintsJPanelOperations.anchor = java.awt.GridBagConstraints.WEST;
        constraintsJPanelOperations.weightx = 1.0;
        constraintsJPanelOperations.weighty = 1.0;
        constraintsJPanelOperations.insets = new java.awt.Insets(8, 4, 15, 5);
        add(getOperationsPanel(), constraintsJPanelOperations);
        
        initConnections();
	}
	
	private JPanel getOperationsPanel() {
        if(operationPanel == null) {
            operationPanel = new JPanel();
            operationPanel.setName("JPanelOperations");
            operationPanel.setLayout(new java.awt.GridBagLayout());
            
//            GridBagConstraints xmlTypeDropDownConstraint = new GridBagConstraints();
//            xmlTypeDropDownConstraint.gridx = 0; xmlTypeDropDownConstraint.gridy = 1;
//            xmlTypeDropDownConstraint.fill = GridBagConstraints.HORIZONTAL;
//            xmlTypeDropDownConstraint.anchor = GridBagConstraints.WEST;
//            operationPanel.add(getXmlTypeDropDown(), xmlTypeDropDownConstraint);
            
            GridBagConstraints constraintsParameterNameLabel = new GridBagConstraints();
            constraintsParameterNameLabel.gridx = 1; constraintsParameterNameLabel.gridy = 0;
            constraintsParameterNameLabel.fill = GridBagConstraints.HORIZONTAL;
            constraintsParameterNameLabel.anchor = java.awt.GridBagConstraints.WEST;
            constraintsParameterNameLabel.ipadx = 50;
            constraintsParameterNameLabel.insets = new java.awt.Insets(0, 5, 0, 5);
            operationPanel.add(getParameterNameLabel(), constraintsParameterNameLabel);
            
            GridBagConstraints constraintsParameterValueLabel = new GridBagConstraints();
            constraintsParameterValueLabel.gridx = 2; constraintsParameterValueLabel.gridy = 0;
            constraintsParameterValueLabel.fill = GridBagConstraints.HORIZONTAL;
            constraintsParameterValueLabel.anchor = java.awt.GridBagConstraints.EAST;
            constraintsParameterValueLabel.ipadx = 74;
            constraintsParameterValueLabel.insets = new java.awt.Insets(0, 5, 0, 5);
            operationPanel.add(getParameterValueLabel(), constraintsParameterValueLabel);           
            
            GridBagConstraints constraintsParamNameTextField = new GridBagConstraints();
            constraintsParamNameTextField.gridx = 1; constraintsParamNameTextField.gridy = 1;
            constraintsParamNameTextField.fill = GridBagConstraints.HORIZONTAL;
            constraintsParamNameTextField.anchor = GridBagConstraints.WEST;
            constraintsParamNameTextField.weightx = 1.0;
            constraintsParamNameTextField.insets = new Insets(7, 5, 7, 5);
            operationPanel.add(getParameterNameTextField(), constraintsParamNameTextField);
            
            GridBagConstraints constraintsParamValueTextField = new GridBagConstraints();
            constraintsParamValueTextField.gridx = 2; constraintsParamValueTextField.gridy = 1;
            constraintsParamValueTextField.fill = GridBagConstraints.HORIZONTAL;
            constraintsParamValueTextField.anchor = GridBagConstraints.EAST;
            constraintsParamValueTextField.weightx = 1.0;
            constraintsParamValueTextField.insets = new Insets(7, 5, 7, 5);
            operationPanel.add(getParameterValueTextField(), constraintsParamValueTextField);
            
            GridBagConstraints constraintsAddButton = new GridBagConstraints();
            constraintsAddButton.gridx = 0; constraintsAddButton.gridy = 2;
            constraintsAddButton.anchor = java.awt.GridBagConstraints.EAST;
            constraintsAddButton.ipadx = 43;
            constraintsAddButton.insets = new java.awt.Insets(6, 5, 4, 3);
            operationPanel.add(getAddButton(), constraintsAddButton);
            
            GridBagConstraints constraintsUpdateButton = new GridBagConstraints();
            constraintsUpdateButton.gridx = 1; constraintsUpdateButton.gridy = 2;
            constraintsUpdateButton.anchor = java.awt.GridBagConstraints.CENTER;
            constraintsUpdateButton.ipadx = 43;
            constraintsUpdateButton.insets = new java.awt.Insets(6, 5, 4, 3);
            operationPanel.add(getUpdateButton(), constraintsUpdateButton);
            
            GridBagConstraints constraintsRemoveButton = new GridBagConstraints();
            constraintsRemoveButton.gridx = 2; constraintsRemoveButton.gridy = 2;
            constraintsRemoveButton.anchor = java.awt.GridBagConstraints.WEST;
            constraintsRemoveButton.ipadx = 43;
            constraintsRemoveButton.insets = new java.awt.Insets(6, 5, 4, 3);
            operationPanel.add(getRemoveButton(), constraintsRemoveButton);
        }
        
        return operationPanel;
	}
	
	public boolean isInputValid() {
		return true;
	}
	
	private void initConnections() {
		getXmlTypeDropDown().addActionListener(this);
		getParameterTable().addMouseListener(this);
		
		getAddButton().addActionListener(this);
		getUpdateButton().addActionListener(this);
		getRemoveButton().addActionListener(this);
	}

	public void setFirstFocus() {
	    // Make sure that when its time to display this panel, the focus starts in the top component
	    SwingUtilities.invokeLater( new Runnable() { 
	    	public void run() { 
	    		getXmlTypeDropDown().requestFocus(); 
	        } 
	    });    
	}

	//Get values from fields and put into the database.
	@Override
	public Object getValue(Object o) {
       if( o instanceof LMGroupXML )
        {    
           LMGroupXML group = (LMGroupXML)o;
           
           List<LMxmlParameter> list;
           list = ((XmlParameterTableModel)getParameterTable().getModel()).getRows();
           group.setParameterList(list);
        }
	    
		return o;
	}

	//Set the values in the fields from the database.
	@Override
	public void setValue(Object o) {
		if( o instanceof LMGroupXML )
		{
			LMGroupXML group = (LMGroupXML) o;

			XmlType xmlType = group.getXmlType();
			setXmlType(xmlType);
			//Set groupId so we know which group this panel is referring to.
			groupId = group.getPAObjectID();
			
			//Add parameter's from the database.
			((XmlParameterTableModel)getParameterTable().getModel()).addRows(group.getParameterList());
			
		}
	}

	public JComboBox getXmlTypeDropDown() {
		if(xmlTypeDropDown == null){
			xmlTypeDropDown = new JComboBox();
			// add stuff to it
			xmlTypeDropDown.addItem("Zigbee");
			xmlTypeDropDown.addItem("Other");
		}
		return xmlTypeDropDown;
	}

	private javax.swing.JScrollPane getParameterScrollPane() {
	    if (parameterScrollPane == null) {
            parameterScrollPane = new javax.swing.JScrollPane();
            parameterScrollPane.setName("ComponentsScrollPane");
            parameterScrollPane.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            parameterScrollPane.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            parameterScrollPane.setPreferredSize(new java.awt.Dimension(200, 180));
            parameterScrollPane.setFont(new java.awt.Font("dialog", 0, 14));
            parameterScrollPane.setMinimumSize(new java.awt.Dimension(200, 180));
            getParameterScrollPane().setViewportView(getParameterTable());
	    }
	    return parameterScrollPane;
	}
	
	private JTable getParameterTable() {
	    if (parameterTable == null) {

            parameterTable = new javax.swing.JTable();
            parameterTable.setName("ComponentsTable");
            getParameterScrollPane().setColumnHeaderView(parameterTable.getTableHeader());
            parameterTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_NEXT_COLUMN);
            parameterTable.setPreferredSize(new java.awt.Dimension(200, 8000));
            parameterTable.setBounds(0, 0, 396, 177);
            parameterTable.setMaximumSize(new java.awt.Dimension(32767, 32767));
            parameterTable.setPreferredScrollableViewportSize(new java.awt.Dimension(200, 8000));
            parameterTable.createDefaultColumnsFromModel();
	    }
	    return parameterTable;
	}

	private JTextField getParameterNameTextField() {
	    if (parameterNameTextField == null) {
            parameterNameTextField = new javax.swing.JTextField();
            parameterNameTextField.setName("ParamterNameTextField");
            parameterNameTextField.setMaximumSize(new java.awt.Dimension(130, 24));
            parameterNameTextField.setColumns(0);
            parameterNameTextField.setPreferredSize(new java.awt.Dimension(130, 24));
            parameterNameTextField.setFont(new java.awt.Font("dialog", 0, 14));
            parameterNameTextField.setMinimumSize(new java.awt.Dimension(130, 24));
	    }
	    return parameterNameTextField;
	}
	
	private javax.swing.JTextField getParameterValueTextField() {
	    if (parameterValueTextField == null) {

	        parameterValueTextField = new javax.swing.JTextField();
	        parameterValueTextField.setName("ParamterValueTextField");
	        parameterValueTextField.setMaximumSize(new java.awt.Dimension(130, 24));
	        parameterValueTextField.setColumns(0);
	        parameterValueTextField.setPreferredSize(new java.awt.Dimension(130, 24));
	        parameterValueTextField.setFont(new java.awt.Font("dialog", 0, 14));
	        parameterValueTextField.setMinimumSize(new java.awt.Dimension(130, 24));
	    }
	    return parameterValueTextField;
	}
	
	private JLabel getParameterNameLabel() {
	    if (parameterNameLabel == null) {
            parameterNameLabel = new JLabel();
            parameterNameLabel.setName("ParameterNameLabel");
            parameterNameLabel.setFont(new java.awt.Font("dialog", 0, 14));
            parameterNameLabel.setText("Name");
	    }
	    return parameterNameLabel;
	}
	
    private JLabel getParameterValueLabel() {
        if (parameterValueLabel == null) {
            parameterValueLabel = new JLabel();
            parameterValueLabel.setName("ParameterValueLabel");
            parameterValueLabel.setFont(new java.awt.Font("dialog", 0, 14));
            parameterValueLabel.setText("Value");
        }
        return parameterValueLabel;
    }
    
    private JButton getAddButton() {
        if (addButton == null) {
            addButton = new javax.swing.JButton();
            addButton.setName("AddParameterButton");
            addButton.setFont(new java.awt.Font("dialog", 0, 14));
            addButton.setText("Add");
            addButton.setContentAreaFilled(true);
        }
        return addButton;
    }
    
    private JButton getUpdateButton() {
        if (updateButton == null) {
            updateButton = new javax.swing.JButton();
            updateButton.setName("UpdateParameterButton");
            updateButton.setFont(new java.awt.Font("dialog", 0, 14));
            updateButton.setText("Update");
            updateButton.setContentAreaFilled(true);
        }
        return updateButton;
    }
    
    private JButton getRemoveButton() {
        if (removeButton == null) {
            removeButton = new javax.swing.JButton();
            removeButton.setName("RemoveParameterButton");
            removeButton.setFont(new java.awt.Font("dialog", 0, 14));
            removeButton.setText("Remove");
            removeButton.setContentAreaFilled(true);
        }
        return removeButton;
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        parameterSelected(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == getAddButton()) {
            addClicked(e);
        }
        else if (e.getSource() == getUpdateButton()) {
            updateClicked(e);
        }
        else if (e.getSource() == getRemoveButton()) {
            removeClicked(e);
        }
    }
    
    public void parameterSelected(java.awt.event.MouseEvent mouseEvent) {
        if( getParameterTable().getSelectedRowCount() == 1 )
        {
            int selectedRow = getParameterTable().getSelectedRow();
            
            String parameterName = (String)getParameterTable().getModel().getValueAt(selectedRow,0);
            String paramterValue = (String)getParameterTable().getModel().getValueAt(selectedRow,1);
            
            getParameterNameTextField().setText(parameterName);
            getParameterValueTextField().setText(paramterValue);
        }
    }
    
    public void addClicked(ActionEvent e) {
        String name = getParameterNameTextField().getText();
        String value = getParameterValueTextField().getText();
        
        //Validate name != ""
        if(name.compareTo("") == 0)
        {
            //Parameter name has no value. avoiding inserting a blank.
            return;
        }
        //Make the new parameter from the text fields and insert it into the table model.
        LMxmlParameter newParam = new LMxmlParameter();
        
        newParam.setLmGroupId(groupId);
        newParam.setParameterName(name);
        newParam.setParameterValue(value);
        
        ((XmlParameterTableModel)getParameterTable().getModel()).addRow(newParam);
        
        getParameterNameTextField().setText("");
        getParameterValueTextField().setText("");
        
        fireInputUpdate();
    }
    
    public void updateClicked(ActionEvent e) {
        int row = getParameterTable().getSelectedRow();
        //So row selected
        if (row < 0) {
            return;
        }
        
        //Make the new parameter from the text fields
        LMxmlParameter newParam = new LMxmlParameter();
        
        newParam.setLmGroupId(groupId);
        newParam.setParameterName(getParameterNameTextField().getText());
        newParam.setParameterValue(getParameterValueTextField().getText());
        
        
        //Replace existing row with this new value.
        ((XmlParameterTableModel)getParameterTable().getModel()).editRow(row,newParam);
        
        fireInputUpdate();
    }

    public void removeClicked(ActionEvent e) {
        int row = getParameterTable().getSelectedRow();
        
        if (row < 0) {
            //no row selected.
            return;
        }
        
        ((XmlParameterTableModel)getParameterTable().getModel()).removeRow(row);
        
        fireInputUpdate();
    }
}
