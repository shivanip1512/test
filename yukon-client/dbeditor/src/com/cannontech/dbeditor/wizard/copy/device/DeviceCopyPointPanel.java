package com.cannontech.dbeditor.wizard.copy.device;

import java.util.List;

import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.lite.LitePoint;
import com.google.common.collect.Lists;

/**
 * This type was created in VisualAge.
 */

public class DeviceCopyPointPanel extends com.cannontech.common.gui.util.DataInputPanel implements com.klg.jclass.util.value.JCValueListener, java.awt.event.KeyListener{
	private int numberOfDevicePoints = 0;
	private java.util.Vector devicePoints = null;
	private javax.swing.JLabel ivjEndingPointNumberField = null;
	private javax.swing.JLabel ivjEndingPointNumberLabel = null;
	private javax.swing.JLabel ivjStartingPointNumberLabel = null;
	private com.klg.jclass.field.JCSpinField ivjStartingPointNumberSpinner = null;
	private javax.swing.JLabel ivjUsedPointNumberLabel = null;
public DeviceCopyPointPanel() {
	super();
	initialize();
}
/**
 * connEtoC2:  (NameTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> PointSettingsPanel.nameTextField_CaretUpdate(Ljavax.swing.event.CaretEvent;)V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(javax.swing.event.CaretEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.nameTextField_CaretUpdate(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (PointNumberSpinner.value.valueChanged(com.klg.jclass.util.value.JCValueEvent) --> DeviceCopyPointPanel.pointNumberSpinner_ValueChanged(Lcom.klg.jclass.util.value.JCValueEvent;)V)
 * @param arg1 com.klg.jclass.util.value.JCValueEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(com.klg.jclass.util.value.JCValueEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.pointNumberSpinner_ValueChanged(arg1);
		// user code begin {2}
		
		fireInputUpdate();
		
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Return the EndingPointNumberField property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getEndingPointNumberField() {
	if (ivjEndingPointNumberField == null) {
		try {
			ivjEndingPointNumberField = new javax.swing.JLabel();
			ivjEndingPointNumberField.setName("EndingPointNumberField");
			ivjEndingPointNumberField.setFont(new java.awt.Font("dialog", 0, 14));
			ivjEndingPointNumberField.setText("");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjEndingPointNumberField;
}
/**
 * Return the EndingPointNumberLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getEndingPointNumberLabel() {
	if (ivjEndingPointNumberLabel == null) {
		try {
			ivjEndingPointNumberLabel = new javax.swing.JLabel();
			ivjEndingPointNumberLabel.setName("EndingPointNumberLabel");
			ivjEndingPointNumberLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjEndingPointNumberLabel.setText("Ending Point #:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjEndingPointNumberLabel;
}
/**
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getStartingPointNumberLabel() {
	if (ivjStartingPointNumberLabel == null) {
		try {
			ivjStartingPointNumberLabel = new javax.swing.JLabel();
			ivjStartingPointNumberLabel.setName("StartingPointNumberLabel");
			ivjStartingPointNumberLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjStartingPointNumberLabel.setText("Starting Point #:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStartingPointNumberLabel;
}
/**
 * Return the PointNumberSpinner property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getStartingPointNumberSpinner() {
	if (ivjStartingPointNumberSpinner == null) {
		try {
			ivjStartingPointNumberSpinner = new com.klg.jclass.field.JCSpinField();
			ivjStartingPointNumberSpinner.setName("StartingPointNumberSpinner");
			ivjStartingPointNumberSpinner.setPreferredSize(new java.awt.Dimension(60, 22));
			ivjStartingPointNumberSpinner.setFont(new java.awt.Font("dialog", 0, 14));
			ivjStartingPointNumberSpinner.setBackground(java.awt.Color.white);
			ivjStartingPointNumberSpinner.setMinimumSize(new java.awt.Dimension(60, 22));
			// user code begin {1}
			ivjStartingPointNumberSpinner.setDataProperties(new com.klg.jclass.field.DataProperties(new com.klg.jclass.field.validate.JCIntegerValidator(null, new Integer(1), new Integer(1000000), null, true, null, new Integer(1), "#,##0.###;-#,##0.###", false, false, false, null, new Integer(0)), new com.klg.jclass.util.value.MutableValueModel(java.lang.Integer.class, new Integer(1)), new com.klg.jclass.field.JCInvalidInfo(true, 2, new java.awt.Color(0, 0, 0, 255), new java.awt.Color(255, 255, 255, 255))));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStartingPointNumberSpinner;
}
/**
 * Return the InvalidPointOffsetLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getUsedPointNumberLabel() {
	if (ivjUsedPointNumberLabel == null) {
		try {
			ivjUsedPointNumberLabel = new javax.swing.JLabel();
			ivjUsedPointNumberLabel.setName("UsedPointNumberLabel");
			ivjUsedPointNumberLabel.setText("IDs Already Assigned in Range");
			ivjUsedPointNumberLabel.setMaximumSize(new java.awt.Dimension(250, 20));
			ivjUsedPointNumberLabel.setPreferredSize(new java.awt.Dimension(220, 20));
			ivjUsedPointNumberLabel.setFont(new java.awt.Font("dialog.bold", 1, 14));
			ivjUsedPointNumberLabel.setMinimumSize(new java.awt.Dimension(220, 20));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjUsedPointNumberLabel;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param val java.lang.Object
 */
@Override
public Object getValue(Object val) 
{
	com.cannontech.database.data.multi.MultiDBPersistent objectsToAdd = (com.cannontech.database.data.multi.MultiDBPersistent)val;
	com.cannontech.database.data.device.DeviceBase device = null;

	// find the DeviceBase element (shuould always have one)
	for( int i = 0; i < objectsToAdd.getDBPersistentVector().size(); i++ )
	{
		if( objectsToAdd.getDBPersistentVector().elementAt(i) instanceof com.cannontech.database.data.device.DeviceBase )
		{
			device = (com.cannontech.database.data.device.DeviceBase)objectsToAdd.getDBPersistentVector().elementAt(i);
			break;
		}
	}
	
	if( getUsedPointNumberLabel().getText().equals("") )
	{
		Integer pointID = null;
		Object pointNumSpinVal = getStartingPointNumberSpinner().getValue();
		pointID = new Integer( ((Number)pointNumSpinVal).intValue() );

		for(int i = 0; i < objectsToAdd.getDBPersistentVector().size(); i++)
		{
			if( objectsToAdd.getDBPersistentVector().elementAt(i) instanceof com.cannontech.database.data.point.PointBase )
			{
				((com.cannontech.database.data.point.PointBase)objectsToAdd.getDBPersistentVector().elementAt(i)).setPointID(pointID);
				((com.cannontech.database.data.point.PointBase)objectsToAdd.getDBPersistentVector().elementAt(i)).getPoint().setPaoID(device.getDevice().getDeviceID());
				pointID = new Integer(pointID.intValue() + 1);
			}
			
			if( objectsToAdd.getDBPersistentVector().elementAt(i) instanceof com.cannontech.database.data.route.RouteBase )
			{
				((com.cannontech.database.data.route.RouteBase)objectsToAdd.getDBPersistentVector().elementAt(i)).setRouteName( 
							(device.getPAOName().length() > 50 ? device.getPAOName().substring(0,50) : device.getPAOName()) );
			}
			
		}
	}

	return objectsToAdd;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
	com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
}
/**
 * Initializes connections
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	
	getStartingPointNumberSpinner().addKeyListener(this);
	
	// user code end
	getStartingPointNumberSpinner().addValueListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("PointSettingsPanel");
		setPreferredSize(new java.awt.Dimension(350, 200));
		setLayout(new java.awt.GridBagLayout());
		setSize(350, 200);

		java.awt.GridBagConstraints constraintsStartingPointNumberLabel = new java.awt.GridBagConstraints();
		constraintsStartingPointNumberLabel.gridx = 0; constraintsStartingPointNumberLabel.gridy = 0;
		constraintsStartingPointNumberLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsStartingPointNumberLabel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsStartingPointNumberLabel.insets = new java.awt.Insets(5, 0, 5, 0);
		add(getStartingPointNumberLabel(), constraintsStartingPointNumberLabel);

		java.awt.GridBagConstraints constraintsStartingPointNumberSpinner = new java.awt.GridBagConstraints();
		constraintsStartingPointNumberSpinner.gridx = 1; constraintsStartingPointNumberSpinner.gridy = 0;
		constraintsStartingPointNumberSpinner.anchor = java.awt.GridBagConstraints.WEST;
		constraintsStartingPointNumberSpinner.insets = new java.awt.Insets(5, 10, 5, 0);
		add(getStartingPointNumberSpinner(), constraintsStartingPointNumberSpinner);

		java.awt.GridBagConstraints constraintsUsedPointNumberLabel = new java.awt.GridBagConstraints();
		constraintsUsedPointNumberLabel.gridx = 0; constraintsUsedPointNumberLabel.gridy = 2;
		constraintsUsedPointNumberLabel.gridwidth = 2;
		constraintsUsedPointNumberLabel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsUsedPointNumberLabel.insets = new java.awt.Insets(5, 0, 5, 0);
		add(getUsedPointNumberLabel(), constraintsUsedPointNumberLabel);

		java.awt.GridBagConstraints constraintsEndingPointNumberLabel = new java.awt.GridBagConstraints();
		constraintsEndingPointNumberLabel.gridx = 0; constraintsEndingPointNumberLabel.gridy = 1;
		constraintsEndingPointNumberLabel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsEndingPointNumberLabel.insets = new java.awt.Insets(5, 0, 5, 0);
		add(getEndingPointNumberLabel(), constraintsEndingPointNumberLabel);

		java.awt.GridBagConstraints constraintsEndingPointNumberField = new java.awt.GridBagConstraints();
		constraintsEndingPointNumberField.gridx = 1; constraintsEndingPointNumberField.gridy = 1;
		constraintsEndingPointNumberField.anchor = java.awt.GridBagConstraints.WEST;
		constraintsEndingPointNumberField.insets = new java.awt.Insets(5, 10, 5, 0);
		add(getEndingPointNumberField(), constraintsEndingPointNumberField);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}
/**
 * This method was created in VisualAge.
 * @return boolean
 */
@Override
public boolean isInputValid() 
{
	if( getUsedPointNumberLabel().getText().equalsIgnoreCase("IDs Already Assigned in Range") )
	{
		setErrorString("That PointID is already used");
		return false;
	}
	else
		return true;
}
/**
 * Insert the method's description here.
 * Creation date: (12/8/00 11:51:12 AM)
 * @param ev java.awt.event.KeyEvent
 */
@Override
public void keyPressed(java.awt.event.KeyEvent ev) 
{
}
/**
 * Insert the method's description here.
 * Creation date: (12/8/00 11:51:12 AM)
 * @param ev java.awt.event.KeyEvent
 */
@Override
public void keyReleased(java.awt.event.KeyEvent ev) 
{

	if( ev.getSource() == getStartingPointNumberSpinner() )
		pointNumberSpinner_ValueChanged(null);		
		
}
/**
 * Insert the method's description here.
 * Creation date: (12/8/00 11:51:12 AM)
 * @param ev java.awt.event.KeyEvent
 */
@Override
public void keyTyped(java.awt.event.KeyEvent ev) 
{
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		java.awt.Frame frame = new java.awt.Frame();
		DeviceCopyPointPanel aDeviceCopyPointPanel;
		aDeviceCopyPointPanel = new DeviceCopyPointPanel();
		frame.add("Center", aDeviceCopyPointPanel);
		frame.setSize(aDeviceCopyPointPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
            public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of com.ibm.uvm.abt.edit.DeletedClassView");
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
	}
}
/**
 * Comment
 */
public void nameTextField_CaretUpdate(javax.swing.event.CaretEvent caretEvent) {
	fireInputUpdate();
}
/**
 * Comment
 */
public void pointNumberSpinner_ValueChanged(com.klg.jclass.util.value.JCValueEvent arg1) {
	fireInputUpdate();
    getUsedPointNumberLabel().setText("");
    
    Object startingPointNumberSpinVal = getStartingPointNumberSpinner().getValue();
    Integer startingPointNumber = null;
    if( startingPointNumberSpinVal instanceof Long )
        startingPointNumber = new Integer( ((Long)startingPointNumberSpinVal).intValue() );
    else if( startingPointNumberSpinVal instanceof Integer )
        startingPointNumber = new Integer( ((Integer)startingPointNumberSpinVal).intValue() );

    Integer endingPointNumber = null;
    if( startingPointNumber != null)
        endingPointNumber = new Integer( startingPointNumber.intValue() + numberOfDevicePoints );
    getEndingPointNumberField().setText( endingPointNumber.toString() );
   		
    Integer[] ids = new Integer[endingPointNumber-startingPointNumber];
    for(int i = 0; i < ids.length; i++) {
        ids[i] = startingPointNumber+i; 
    }
    
    List<LitePoint> pointsInRange = DaoFactory.getPointDao().getLitePoints(Lists.newArrayList(ids));
    if(pointsInRange.size() > 0) {
        getUsedPointNumberLabel().setText("IDs Already Assigned in Range");
    }
    		
	revalidate();
	repaint();
	
	return;
}
/**
 * This method was created in VisualAge.
 * @param val java.lang.Object
 */
@Override
public void setValue(Object val) {

    PointDao pointDao = DaoFactory.getPointDao();
    int deviceId = ((com.cannontech.database.data.device.DeviceBase)val).getDevice().getDeviceID();
    List<LitePoint> points = pointDao.getLitePointsByPaObjectId(deviceId);
    numberOfDevicePoints = points.size();
    getStartingPointNumberSpinner().setValue(new Integer(pointDao.getNextPointId()));
}

/**
 * Method to handle events for the JCValueListener interface.
 * @param arg1 com.klg.jclass.util.value.JCValueEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
@Override
public void valueChanged(com.klg.jclass.util.value.JCValueEvent arg1) {
	// user code begin {1}
	// user code end
	if (arg1.getSource() == getStartingPointNumberSpinner()) 
		connEtoC3(arg1);
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the JCValueListener interface.
 * @param arg1 com.klg.jclass.util.value.JCValueEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
@Override
public void valueChanging(com.klg.jclass.util.value.JCValueEvent arg1) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GF7F954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBA8DF8D4D5F6F189BF2E002105AD32344B3AE0691A6E3AAEED796ACFE39695D7ECF597BFF14BE23A52EE3A45AE2D54C517561BC9B28990A01038C4810982EAB48DA120EB4300A4197C8C0EB81A9847158AEFB2AFB3133C19B73CF9134C04GBD675EF75FFBB3F993406A576663705EBD771EF34FBDFF775C7B06D46F1DD7DAE42BA6242814E8FF3FADA2E44591C29EF86B74F20E4B92736D4474779240
	F672505192F866C0DF568D7976CA327B0DF2E84F07F6410CFC7BCEF82FA2AF3D51D88FAF9966A9073E7BD6BC1EB77EBCBFEA66731451F6F3CB85BC3782AC849CBE5709B87F07ADB59AFE190667D0E4A3E4E5BF0E89376C56F0EFC35B83B09BA0371F0DDF824F0DF466094DEFEB63CE4F18C7567D70D55B9664E36249E1FF0051F6E94FDA725D2235811C6BEBD4CE24490276E2GDCFCB149B962002743B398B85EA7079B7A04C8C40E28E2DF30E1B8B4A2A87E06BEB91AEC700BC321BE313959C91FED0072482108
	6A91A222A485117F2A16F24A0F10A350DECFF1BFCA601C37407BD200829DFFC392710ED03E3D87C08A71F59EFB55212D43FB7E3B6474A31162ED9956199DE26B4B8D996B1C99FF266CF9EA139D8E54FDA174D58C645BEB811CGFE00EC00DD54971EBF7ABABCDBFCEA1B9C8E4B1116E834D572AB6F49FED1027766E66851F09F0AFD3262A70459FBEECB3D05BC6381237B76D553B11DACBC4CED1F7B61CDE465775F3515D968E46542C65B5C81260BC58306CE585C0FE71CFB79C02AEF264E7DB1B14F6D1EA49717
	DA4C3D6861BADB2146E703E96E7AC134F5873575EEF8AF527C0361BD949FB561191F2E20BE036DAC68DBBD48FD433B154B62AE19C3AA8F39AD75B0BB3F4556A5B1994ECA6932EC99C15ED325F4D9F6D27CDCA9DD16EFA439AC3950D7994E37B37F5AF59C63D605F68E40C200BC0082008DG15916EE33E23872F5047BC02223646C312DFD488235D65788DBC45042A882EC8CC95A2FDE22F2004841FA44608B6E63F83F560CB212B233E5F87F3F40A91D19154109C816EC2D8D4C5A54646B4B9366398C5243DBD21
	30889304230473EDF6D441D392E26AC7D13F200A4688565F6AC01B8C893D40919840BB53CB57BB282FC6D8FF9700D757439D145F3B228259223959AD4B0371E88F2E04AC0C201E1F2139C302776C88573173846256C03BC466FC56B9F61878B8C3FDA83E20A4F90C3913181BEBE46EB37F6C65BE635C320054BFFC5252E7E87E15192FF86474586925BCD1AE56DE1570EAB4B532F56C7CEF004D71ABFD0E62AF187FD65DD637527DA081EB188930C8577141E3354CE7DB0518483D74FD859CCC749B9AE7F2D60D20
	EECAF539775076B2196BEAC152EB1A3774CB51551DA9F3FC517AE2BAB9F09C77A1F1C80C28CEB1AAC67C7092537A6A0F232FE9F15AAF6BB4CD949F0538EE03C00A6978370FE36C892A2A04FCF1D56C96D5B59489987DA768DC3138AFEA2484BED767F15421BC8CF12D0FF79F47186813A391119AC7A7517ACF511894A5B18CC2374971084A70BD6F202D86452416933C6FE031B3A4C8F14CAF2CB638C5580CEBD0C2E1B0B926A8620DE23CEEEF2DB169E89D589BEB1CFA68BB916531B73C0F475E7E11EF123CB387
	ECD6FB476C85CE5B3C2836EFC50D58E3B239CED84B7EBE4D8D9ABE46E4FD768C6A01D6A5DC2734BA617E7ECF2D48EBB94CD188C04EF1FF3F7F8476731C22B9FA1BACC9CC1D3A1E9737D422DD44F3F191D2B1A62FF7F55D7DD54B0E343EDECD0F2C7F5E965423E44AFBEB4F219ECF0D707A2F865A0DG1EF3CC17334FB1DD3035151D417D5B2AC68B44C38DF1B50459D0D0418123F1F3CDF63E35DC0B930742EB2FC7CF229ED4E8CF85182FF0FBBD340557DEB387671102EF3D1031463CDDE13295A8063DEAA7705A
	6BCDA74ED99B037544380EDBDB4D793FA596137BC2F46B517473AF2DE6FBB25CD32D1B60599FD2E22A8B82AE41F539219565453DC9C3EBFB75B05D43D1DFCE18F794E0C38C7157C62BCD32AF0A31BD26901E45145F2A11E66E778D747F36209089087E57D05303D220E1208F6317EA198DE8B859C64D5B0E79200E5BA87A7AC90C95743FD37130CFD43A23A108C965CC2F19297B4A389E2D6B65B5580DD5A7B45917FDE78EF1DC2A32D95544D3366D31B9D5E68FC9B56CB2FA2B3DF84DE73FAEE339B22B1557010B
	35B14BD4E38FABD0476EE72CBEAAF623EE57292CBE5A2C6337D3FCF55C40B3DBBCC6FD8C5BC720EFF61C6F69CB5AB1EFEC00F64D907887C097C090E04A9077156C364364B2B5138BFCDF0988D2F7CA1264350B331865B52DFD5CD94A6DCB781EF1E1A43D9E1EBF143A763C34B6233BEE8C5D0BE96D55E9F44CB7AE3E750AB9FECD766F8F09123F3979E49BFACB87B640FEB4CE7322651A8EB78C71983D50E10E515B37B559B6E70851A343CC0E1EE1D38D2C1F0FA21D18F7E6C25FADGCB81D68314A7726D7B813A
	925C96CB1D47AEE78B6A2EBDF29B603AA03F35256FC3C04FEA3CBF20B19E845E59G8CF7DFA762E6C3BBD7475D64C25C0350AECC70BAE2239BE338A289BE836085C8G4CCD727E1D34BFAF49F2E881BC49881C7BC6F87F5F1F74DA6888F970778A537B5C6478F51DD713A355B77E43F20E0D26F7F3ED32F99C7E1C2739DEE276F820BDF39D323C5D3A8E79C93BF59DD2503E1D61C5BC469BD1447B8B5B472FD35636DB55A9FB68BA6F7B4DB64ABB9693E354C1F0F3527AFE2EBEC7AC6A15D3F0C051F4D4C5960DEC
	5446342BB24D87CC47F7C7F97C1DG5B760C98B688427BA8C04EC82A6D183F7FDB977A47FC684B83D88130962072BC7773C99D174DB96D09F1F2CE22B333DDAEF5DA5BE5E2A76E2FB1B92E7409693A1F5AD92377999165D245B02A090D19C569D5C18908E3ECB71752333ECC748BBAB359175960BB2D48BF2A087D228295D1F7E8846D93BC0FB1FAAF0D65C6F8F71C677BE8C1A7560F12101463AA54B323B86E23AEDEA7664238A5GEBG48A85B03D9EE7C5EB046C2BF9CD0E17A6E2858C756D15ABBDC1C76AC34
	D50019G0BG160F72B9875F44B15D30D0145DE36C3DEE41A7CAEC4CDF1FFE41FA0C36BF7375568D210FFC94937D56FC5A3662F93CBD623718097328FA51F20C1657D9BD62F61B6B914785260B3397F83DB26BBD2BFED56F1F9B376A1F2277CFCDDA754F3D40736BCA3A2F5C826DE5BA6E038E4495C0FB2D0EDB3095F1A597736D3BAFF25CDCA74F4D0DBA6ECF079017856D5997F9BC7DFBD766F8A8F1D94743A617F51E2AF40D1F27EADC197CB877A25E5FD6FB086E53F7F65642B3A28E578E88C3C203B02CB6B8
	C3D0F362A546EB075FEC2C2FEDEAEC2CE3FB6D3CE1731D6B5B1BFEEBF9FEC1D8FB11651C12CB6CF97B64FDDA9DF4210B5F39BA20CF3D44FD7E50D95CFFE9BD4085E9EE6ED4C27ED6A160269144EA003B3B4AB57A5CCB3CFEFF1272DC8D6D8DG55D93CB6C83C42EB035C1B6690F7563B1675FBC1CF132DBECB3B6B48B2EA0305D09B30B99FF771B927C27F12ACDA671DB47B946AF2868CE800A76BEFCB69C7FBC0D9EC6A0F2774233CF7584C7DFF1C520FBA5A275133355DFF002FED437D8B0974E77F1571ECD2F1
	8DDBD3237674746EA37B3F7A6BB22677A77BEB4CFACFB4440641D7207C22D14C56BF414D4FCCC16041F04B5EC39C4EB9CF477DB84E4715FC0563FE10544FDBDF61B96A49E866BCDAF18E472BB0B6CB9F9F6C40F3F9BF1C6F58180BB4B6F321FF31BE66762DF8B67289FD038185CE45FE4E2F399247964238F5GDD939077FD27D353CD750431F5CD312647E28DDE03B1887B385FCEE035BD9FD3C5168CE13E06E49D8A4743F4B5EC6E15B48F644078399338CF7E4D2766982A1C73120D73CA17E329A65B728906CF
	323DFB55874837BC9BEA53EC1EDFBE713C814FDEE1C05B295B7DA1D5D668D68DC80FA2C7C5C5CDB2ACBDD4E97543042E4FF7F7BF63F3527D702A695839AA604298F7E69B3AE778A845FBCCF816E71E56734CE9684B4B66672DED5D686F7BA7665BCFGF88122GB9G399339AEEF74B4124B54BEBDF2548D49DCB225DF363EC257677B26F1D7990F67C5FFF9A3D9BC696922CC67183C095ADD0376AC6C59E72A6BEE6C4175D5CE4A375783F8G02G23GB3A771757D42D34F56076E50222AC2DF105EB270457506
	44E1830D8588DB5B9437F9ED8BA7E939DDFBBEB8E9ECACB13A075E33BA872C56462F372023F9EBB21CD7A627769D01F657E42BF3680A38D936535A1828764C1E1CC93625C9B35DECEDDC2E764C3320E3FB6859DE349A0B7F0E8EBC73E658C35F42AD34290E535D7BBB036E062DAF2753F5C012FB6DE5FFC81064404B7535C0FB6808CE6B7A4C201D69447DC127EB13A5D9E9988E06D411655D5534AE419C3F9C56301752C04EA0469F63793DB3C477413F2B60BE387B662F9367A3CF97711CB746B6D318DECECFB1
	F2D3614006B246F317E04819F86FCAF443FCF7A925E3BE5A78418112A11F01071E84DE6F7B8620086F9182BA4D639F20ACFA5D43521C56770B8FD0DF1022D550EDE804DE35F54340C80079C158C5F41F98B23DBF5BE5653BDF9430B9EADCDF669CDACD581D397E5C5BEDDDFF9E68362EBF5F689E7F9CFC38BB73FDFDF337793E1E597DE19F5AE6A67852ADG4B818A81B6CC516FDC3AB1CE704B6058DC556E8F60F9903F862AFC7E1BFBB22F75561E4BFFC33C2D075692322A4AE1644F71F775203DD4B9CA118CF7
	CF8F6AC8898502AA9B2A611775506FF7E2BFC533F341B7E89DD5F3AD5493577233423DF644B9206D56F175E57CCC6155F1CF52FAEBA134976BB86FA944AD0176CA9D57CBEB3035502E78AA47659F26774F50EE54F149D1BE07D347BD5C0D3819504E55F13F247C72207D200EAB6DC55CEAE82F53F1833D1C9F390E639E23351F875AE7F55C73DEDA5FC1BBDB47DD74F25A1C6B787D54BFBA4DF939G706B2E9B7F8E0A7952E1BF56F665D321B6826882B88B2082CC8518873088E0A9C08140FA000AE9304FG9C81
	F082F88124E95C771EBAD317A98F81D2BB20D087D404903A20E67F1F7AB27B60EA5F65FD70899F3D7F875F69C9C6357B09FF7561FD025C5F9F93F5DF7F152FD24739E29EB996424C46F26F1F3BD36B66E5547F32E0EDD32769F77DF69C2302B0ACF73F508FCD426A6436B2DEA75FBEED2C6E4DB5F0FA1D0CB17E603474BAF9532954390EE16A567A64D334069692E9B574E46A63AB01D721AEF3B81E797CB0097AEB75F43069F4BE7E514329FA78A50D87B774FB2773B3C14EA8DF6B546956EBADA57C6E9A4EBCB0
	86016F0F0B26272FF5CD37751920046605BC983FCC174FBE14AA5FEEFAEED88F7DA55773B13BFBD3F5F7B6E858691ADEAE7B116B2DE5AFB6C1970CC160321F3EBEDD7627122972D40C50FBEA98276A72B4FAD36579C14810E716174BB35FC21E624B483358A48F4B8F241D66B98D2F65A00ADB296134EF417E4CB1F74AFF79187B58EF1D777F5BBFB66F7B7C56F9BF68B767FD068B7B51EF8210AB3C7AB8C547FD226306A9CF44FD9C722BC19DFF014A0578AE91A7647889D42EE10A73EA38A9A65CA79AEE1A9F73
	86ACB9A8AB1C76777C5446A1C9E26D2F51B121882491DD9FDF27B2062202C96EF9BA8E65E636FFDDB26278B67B1556342CCED54BF81D3A9EEC4978F943C467FDBEE270FEB73EB71D37D1CBB6313B2ABA0D6E0FE4036EAF8E0FC75714C27702E250C9232F0E3716861FAC79E1C107580258FA6E9A656BE94C992F6EBE96B35E6FD40DB9AF0D3B46D4D93FBDEC50BD7D5155697D0EDEAEE7F0DCB99FC8986FC74E9B73ED71DE5DFC77F879FC73F2381DBF9EA50467D461158A225767547D99FEF7E20B01676E2F554D
	49B2F30D3082782E49993FC6E0F4770C1A69480CFCFB750CAB214B6FB5533D86B40EAB223B5FEB2673G0D7FB2F4AC476514713CA76838D9E5BC6F3D2463FEBABAB6BF7EEC541C9F996E515E3163D6750E9D77B36F58F1BF771A47315F8E5F7C768CD8E06E9324B426109DE37D35E57A230F641EF1D65B084DC56CC1EABC97A99DC66BA67027F5CBA14BFF96BBC7AED20743846DGCEDBD2BC6A6EEF3E798F4BD675460A095DC51E47CD29DBD46899CE7463B7B46D87F79DE4A37D2181EE35AED292230F52181A14
	C4B7296E7F90FF0892D3C5FF8BBF3544D4721C9BE7BA08BFB8B858A54402AA6E86C75D24A402BF8DA4BBD837A4C4828759D171A8153A2865AC1517BA8F48DBC56525BFDB5039D45D366D40CBA7D7AFAEA6368E32C91285FF07508705230BECD4030A988BC26AC8C45FB9C199158F736F5A4540EE173EFCDCECC38F6CD93A500309BF1C3A25F0CB070D1004C28E52113887B768A37CEDC1C10129C4452562CF9DE5E53F5EF74D0B6AAB8FF46EBCEDA3C55AA2D175250928CA6C7DC2B8A4A5610545AFB54AC6957EA9
	C2A456AFABE1102CB6CDB25E25CF50707B77B54E7A13E7FEC7A7B0C8D532172992C88F86C4B9FC30253FBF9491F58620D7E03FC7E70FE39A52466CC84CF86E64434F7A408F1C24B85C58C87FBFC47FEF427F8FD14CA10AB954844ADDCB467DCB6FCF70B5D36781F936D1F9507C8D9DE0C175A7CF5CF7635FBA562E0155F510D27A7389B0C225B10AFD5A1A14BBEB4F66198557210D2268AA89643BA5819C37265E5794AD58B921727A43F19BA96FC08BC851206052B83B49E69F2DAEC485462A245250A02585B735
	75018F6FB270FD08E73D5DD1218FBDF923DFD086A98714E15727E4E33F92705189D4E27B3463D318163630FB0F1BECD203F81AC617D249CE0335874FB6DA878B8272AC7CC5691F2EAADC180BD69085C3071B0B9F7FD6310D14C064330A8A22924BA2D8204292C1B1D4D41497E67ACD1A95CAD35301CA36736F14357473E16D3EE6156CF1B9E335AD12A28A7EE4EDCBAC968AC4C4FFEDA8D27BA17E24C8A55B5877C6938521EC0B47C4AFE24B4606A122B76B41002D2DF4E8A57D3F6A2ADD8B291E2334BBF50F28E3
	8ED72BA9007F998A40CB11E7D2DAABADD7D114E195A5FC954E141036336210C903B4DF4E721A2EE4F9568B7ABFB54B3171647EFFA529B3D5D2BD02883F4F2D3BE47E5634FEE2CD466F35CBEEE075480A9B523FB57D58C14F53F3726D0DF3781DC80D63BFC9663B596E30A0C92AA08D36C47CF8CF5B1AD4C563EEF00583794778982D1375BDC6FFC3BFE022799FD0CB8788AB6DD4566B93GGGB5GGD0CB818294G94G88G88GF7F954ACAB6DD4566B93GGGB5GG8CGGGGGGGGGGGGGG
	GGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGA594GGGG
**end of data**/
}
}
