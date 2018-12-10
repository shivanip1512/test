package com.cannontech.dbeditor.editor.point;

/**
 * This type was created in VisualAge.
 */

import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.data.point.PointType;
import com.cannontech.database.data.point.PointTypes;

public class PointEditorPanel extends com.cannontech.common.editor.PropertyPanel 
{
	private DataInputPanel[] inputPanels;
	private String[] inputPanelTabNames;

	private static final int[][] EDITOR_TYPES =
	{
		{  //0 - PointBaseEditorPanel
			PointType.PulseAccumulator.getPointTypeId(), PointType.Analog.getPointTypeId(), PointType.CalcAnalog.getPointTypeId(), PointType.Status.getPointTypeId(), 
			PointType.DemandAccumulator.getPointTypeId(), PointType.CalcStatus.getPointTypeId(), PointType.System.getPointTypeId()
		},
		{  //1 - PointStatusPhysicalSettingsEditorPanel
			PointType.Status.getPointTypeId()
		},
		{  //2 - PointAccumulatorPhysicalSettingsEditorPanel
		 	PointType.PulseAccumulator.getPointTypeId(), PointType.DemandAccumulator.getPointTypeId()
		},
		{  //3 - PointAnalogPhysicalSettingsEditoraPanel
			PointType.Analog.getPointTypeId(), PointType.System.getPointTypeId()
		},
		{  //4 - 
			PointType.PulseAccumulator.getPointTypeId(), PointType.Analog.getPointTypeId(), PointType.CalcAnalog.getPointTypeId(), PointType.DemandAccumulator.getPointTypeId()
		},
		{  //5 - 
			PointType.Status.getPointTypeId(), PointType.CalcStatus.getPointTypeId()
		},
		{  //6 - PointDataOptionsEditorPanel
			PointType.PulseAccumulator.getPointTypeId(), PointType.Analog.getPointTypeId(), PointType.CalcStatus.getPointTypeId(), PointType.Status.getPointTypeId(), 
			PointType.DemandAccumulator.getPointTypeId(), PointType.CalcAnalog.getPointTypeId(), PointType.System.getPointTypeId()
		},
		{  //7 - PointAlarmOptionsEditorPanel
			PointType.PulseAccumulator.getPointTypeId(), PointType.Analog.getPointTypeId(), PointType.CalcAnalog.getPointTypeId(), PointType.Status.getPointTypeId(), 
			PointType.DemandAccumulator.getPointTypeId(), PointType.CalcStatus.getPointTypeId(), PointType.System.getPointTypeId()
		}	,
		{  //8 - PointCalcComponent
			PointType.CalcAnalog.getPointTypeId(), PointType.CalcStatus.getPointTypeId()
		}

	};
	private javax.swing.JTabbedPane ivjPointEditorTabbedPane = null;

/**
 *  This method should return an object array with 2 elements,
 *   Object[0] is a DataInputPanel
 *   Object[1] is a String (Tab Name)
 */
public Object[] createNewPanel(int panelIndex)
{
	Object[] objs = new Object[2];
	
	switch( panelIndex )
	{
		case 0: 
			objs[0] = new com.cannontech.dbeditor.editor.point.PointBaseEditorPanel();
			objs[1] = "General";
			break;

		case 1:
			objs[0] = new com.cannontech.dbeditor.editor.point.PointStatusPhysicalSettingsEditorPanel();
			objs[1] = "Physical Setup";
			break;

		case 2:
			objs[0] = new com.cannontech.dbeditor.editor.point.PointAccumulatorPhysicalSettingsEditorPanel();
			objs[1] = "Physical Setup";
			break;

		case 3:
			objs[0] = new com.cannontech.dbeditor.editor.point.PointAnalogPhysicalSettingsEditorPanel();
			objs[1] = "Physical Setup";
			break;

		case 4:
			objs[0] = new com.cannontech.dbeditor.editor.point.PointLimitEditorPanel();
			objs[1] = "Limits";
			break;

		case 5:
			objs[0] = new com.cannontech.dbeditor.editor.point.StatusPointLimitEditorPanel();
			objs[1] = "Limits";
			break;
		
		case 6:
			objs[0] = new com.cannontech.dbeditor.editor.point.PointForeignDataEditorPanel();
			objs[1] = "Foreign Data";
			break;
			
		case 7:
			objs[0] = new com.cannontech.dbeditor.editor.point.PointAlarmOptionsEditorPanel();
			objs[1] = "Alarms";
			break;
			
		case 8:
			objs[0] = new com.cannontech.dbeditor.editor.point.PointCalcComponentEditorPanel();
			objs[1] = "Calculation";
			break;
	}
		
	return objs;
}

@Override
public DataInputPanel[] getInputPanels() {
	return this.inputPanels;
}

private javax.swing.JTabbedPane getPointEditorTabbedPane() {
	if (ivjPointEditorTabbedPane == null) {
		try {
			ivjPointEditorTabbedPane = new javax.swing.JTabbedPane();
			ivjPointEditorTabbedPane.setName("PointEditorTabbedPane");
			ivjPointEditorTabbedPane.setFont(new java.awt.Font("dialog", 0, 14));
			ivjPointEditorTabbedPane.setBounds(0, 0, 400, 350);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPointEditorTabbedPane;
}

@Override
public java.awt.Dimension getPreferredSize() {
	return new java.awt.Dimension( 400, 465 );
}

@Override
public String[] getTabNames() {
	return this.inputPanelTabNames;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
	com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("PointEditorPanel");
		setLayout(null);
		setSize(400, 350);
		add(getPointEditorTabbedPane(), getPointEditorTabbedPane().getName());
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	super.setHasMultipleUpdates(true);
	// user code end
}

@Override
public void setValue(Object val) 
{
	//Vector to hold the panels temporarily
	java.util.Vector<DataInputPanel> panels = new java.util.Vector<DataInputPanel>();
	java.util.Vector<Object> tabs = new java.util.Vector<Object>();
	
	DataInputPanel tempPanel;

	//We must assume that val is an instance of PointBase	
	PointBase point =  (PointBase) val;
	int type = PointTypes.getType( point.getPoint().getPointType() );

 	for( int i = 0; i < EDITOR_TYPES.length; i++ )
 	{
	 	for( int j = 0; j < EDITOR_TYPES[i].length; j++ )
	 	{
		 	if( type == EDITOR_TYPES[i][j] )
			{
				Object[] panelTabs = createNewPanel(i);

				tempPanel = (DataInputPanel)panelTabs[0];
				panels.addElement( tempPanel );
				tabs.addElement( panelTabs[1] );
				break;				
			}
	 	}

 	}

	this.inputPanels = new DataInputPanel[panels.size()];
	panels.copyInto( this.inputPanels );

	this.inputPanelTabNames = new String[tabs.size()];
	tabs.copyInto( this.inputPanelTabNames );
	
	super.setValue(val);
	
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
@Override
public String toString() {
	return "Point Editor";
}
}