package com.cannontech.dbeditor.wizard.point;

/**
 * This type was created in VisualAge.
 */
import com.cannontech.common.gui.util.DataInputPanel;

public class PointTypePanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener {
	private int pointType = com.cannontech.database.data.point.PointTypes.ANALOG_POINT;
	private javax.swing.JRadioButton ivjAnalogRadioButton = null;
	private javax.swing.ButtonGroup ivjButtonGroup = null;
	private javax.swing.JRadioButton ivjCalculatedRadioButton = null;
	private javax.swing.JLabel ivjSelectTypeLabel = null;
	private javax.swing.JRadioButton ivjStatusRadioButton = null;
	private javax.swing.JRadioButton ivjAccumulatorRadioButton = null;
public PointTypePanel() {
	super();
	initialize();
}
/**
 * Comment
 */
public void accumulatorRadioButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	pointType = com.cannontech.database.data.point.PointTypes.PULSE_ACCUMULATOR_POINT;

	return;
}
/**
 * Method to handle events for the ActionListener interface.
 * @param e java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void actionPerformed(java.awt.event.ActionEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getAnalogRadioButton()) 
		connEtoC1(e);
	if (e.getSource() == getStatusRadioButton()) 
		connEtoC2(e);
	if (e.getSource() == getAccumulatorRadioButton()) 
		connEtoC3(e);
	if (e.getSource() == getCalculatedRadioButton()) 
		connEtoC4(e);
	// user code begin {2}
	// user code end
}
/**
 * Comment
 */
public void analogRadioButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	pointType = com.cannontech.database.data.point.PointTypes.ANALOG_POINT;

	return;
}
/**
 * Comment
 */
public void calculatedRadioButton_ActionPerformed(java.awt.event.ActionEvent actionEvent)
{
	pointType = com.cannontech.database.data.point.PointTypes.CALCULATED_POINT;

	return;
}
/**
 * connEtoC1:  (AnalogRadioButton.action.actionPerformed(java.awt.event.ActionEvent) --> PointTypePanel.analogRadioButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.analogRadioButton_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (StatusRadioButton.action.actionPerformed(java.awt.event.ActionEvent) --> PointTypePanel.statusRadioButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.statusRadioButton_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (AccumulatorRadioButton.action.actionPerformed(java.awt.event.ActionEvent) --> PointTypePanel.accumulatorRadioButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.accumulatorRadioButton_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC4:  (CalculatedRadioButton.action.actionPerformed(java.awt.event.ActionEvent) --> PointTypePanel.calculatedRadioButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.calculatedRadioButton_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM1:  (PointTypePanel.initialize() --> ButtonGroup.add(Ljavax.swing.AbstractButton;)V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1() {
	try {
		// user code begin {1}
		// user code end
		getButtonGroup().add(getAccumulatorRadioButton());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM2:  (PointTypePanel.initialize() --> ButtonGroup.add(Ljavax.swing.AbstractButton;)V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM2() {
	try {
		// user code begin {1}
		// user code end
		getButtonGroup().add(getStatusRadioButton());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM3:  (PointTypePanel.initialize() --> ButtonGroup.add(Ljavax.swing.AbstractButton;)V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM3() {
	try {
		// user code begin {1}
		// user code end
		getButtonGroup().add(getCalculatedRadioButton());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM4:  (PointTypePanel.initialize() --> ButtonGroup.add(Ljavax.swing.AbstractButton;)V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM4() {
	try {
		// user code begin {1}
		// user code end
		getButtonGroup().add(getAnalogRadioButton());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Return the JRadioButton3 property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getAccumulatorRadioButton() {
	if (ivjAccumulatorRadioButton == null) {
		try {
			ivjAccumulatorRadioButton = new javax.swing.JRadioButton();
			ivjAccumulatorRadioButton.setName("AccumulatorRadioButton");
			ivjAccumulatorRadioButton.setFont(new java.awt.Font("dialog", 0, 14));
			ivjAccumulatorRadioButton.setText("Accumulator");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAccumulatorRadioButton;
}
/**
 * Return the AnalogRadioButton property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getAnalogRadioButton() {
	if (ivjAnalogRadioButton == null) {
		try {
			ivjAnalogRadioButton = new javax.swing.JRadioButton();
			ivjAnalogRadioButton.setName("AnalogRadioButton");
			ivjAnalogRadioButton.setSelected(true);
			ivjAnalogRadioButton.setFont(new java.awt.Font("dialog", 0, 14));
			ivjAnalogRadioButton.setText("Analog");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAnalogRadioButton;
}
/**
 * Return the ButtonGroup property value.
 * @return javax.swing.ButtonGroup
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.ButtonGroup getButtonGroup() {
	if (ivjButtonGroup == null) {
		try {
			ivjButtonGroup = new javax.swing.ButtonGroup();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjButtonGroup;
}
/**
 * Return the CalculatedRadioButton property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getCalculatedRadioButton() {
	if (ivjCalculatedRadioButton == null) {
		try {
			ivjCalculatedRadioButton = new javax.swing.JRadioButton();
			ivjCalculatedRadioButton.setName("CalculatedRadioButton");
			ivjCalculatedRadioButton.setFont(new java.awt.Font("dialog", 0, 14));
			ivjCalculatedRadioButton.setText("Calculated");
			ivjCalculatedRadioButton.setEnabled(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCalculatedRadioButton;
}
/**
 * Insert the method's description here.
 * Creation date: (4/30/2001 3:23:30 PM)
 * @return int
 */
public int getPointType()
{
	return pointType;
}
/**
 * Return the SelectTypeLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getSelectTypeLabel() {
	if (ivjSelectTypeLabel == null) {
		try {
			ivjSelectTypeLabel = new javax.swing.JLabel();
			ivjSelectTypeLabel.setName("SelectTypeLabel");
			ivjSelectTypeLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjSelectTypeLabel.setText("Select the type of point:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSelectTypeLabel;
}
/**
 * Return the StatusRadioButton property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getStatusRadioButton() {
	if (ivjStatusRadioButton == null) {
		try {
			ivjStatusRadioButton = new javax.swing.JRadioButton();
			ivjStatusRadioButton.setName("StatusRadioButton");
			ivjStatusRadioButton.setFont(new java.awt.Font("dialog", 0, 14));
			ivjStatusRadioButton.setText("Status");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStatusRadioButton;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param val java.lang.Object
 */
public Object getValue(Object val) {
	//Ignore val - we're determing the type of the DBPersistent
	//Figure out the type, create it, and then return it

	int type = getPointType();
	
	com.cannontech.database.data.point.PointBase newPoint = com.cannontech.database.data.point.PointFactory.createPoint( type );

	newPoint.getPoint().setAlarmInhibit( com.cannontech.common.util.CtiUtilities.getFalseCharacter() );
	newPoint.getPoint().setServiceFlag( com.cannontech.common.util.CtiUtilities.getFalseCharacter() );
	newPoint.getPoint().setArchiveType("None");
	newPoint.getPoint().setArchiveInterval(new Integer(0));

	return newPoint;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	// System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	// exception.printStackTrace(System.out);
}
/**
 * Initializes connections
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getAnalogRadioButton().addActionListener(this);
	getStatusRadioButton().addActionListener(this);
	getAccumulatorRadioButton().addActionListener(this);
	getCalculatedRadioButton().addActionListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("PointTypePanel");
		setPreferredSize(new java.awt.Dimension(350, 200));
		setLayout(new java.awt.GridBagLayout());
		setSize(350, 200);

		java.awt.GridBagConstraints constraintsSelectTypeLabel = new java.awt.GridBagConstraints();
		constraintsSelectTypeLabel.gridx = 0; constraintsSelectTypeLabel.gridy = 0;
		constraintsSelectTypeLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsSelectTypeLabel.anchor = java.awt.GridBagConstraints.WEST;
		add(getSelectTypeLabel(), constraintsSelectTypeLabel);

		java.awt.GridBagConstraints constraintsAnalogRadioButton = new java.awt.GridBagConstraints();
		constraintsAnalogRadioButton.gridx = 0; constraintsAnalogRadioButton.gridy = 1;
		constraintsAnalogRadioButton.anchor = java.awt.GridBagConstraints.WEST;
		constraintsAnalogRadioButton.insets = new java.awt.Insets(0, 20, 0, 0);
		add(getAnalogRadioButton(), constraintsAnalogRadioButton);

		java.awt.GridBagConstraints constraintsStatusRadioButton = new java.awt.GridBagConstraints();
		constraintsStatusRadioButton.gridx = 0; constraintsStatusRadioButton.gridy = 2;
		constraintsStatusRadioButton.anchor = java.awt.GridBagConstraints.WEST;
		constraintsStatusRadioButton.insets = new java.awt.Insets(0, 20, 0, 0);
		add(getStatusRadioButton(), constraintsStatusRadioButton);

		java.awt.GridBagConstraints constraintsAccumulatorRadioButton = new java.awt.GridBagConstraints();
		constraintsAccumulatorRadioButton.gridx = 0; constraintsAccumulatorRadioButton.gridy = 3;
		constraintsAccumulatorRadioButton.anchor = java.awt.GridBagConstraints.SOUTHWEST;
		constraintsAccumulatorRadioButton.insets = new java.awt.Insets(0, 20, 0, 0);
		add(getAccumulatorRadioButton(), constraintsAccumulatorRadioButton);

		java.awt.GridBagConstraints constraintsCalculatedRadioButton = new java.awt.GridBagConstraints();
		constraintsCalculatedRadioButton.gridx = 0; constraintsCalculatedRadioButton.gridy = 4;
		constraintsCalculatedRadioButton.anchor = java.awt.GridBagConstraints.WEST;
		constraintsCalculatedRadioButton.insets = new java.awt.Insets(0, 20, 0, 0);
		add(getCalculatedRadioButton(), constraintsCalculatedRadioButton);
		initConnections();
		connEtoM1();
		connEtoM2();
		connEtoM3();
		connEtoM4();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		java.awt.Frame frame = new java.awt.Frame();
		PointTypePanel aPointTypePanel;
		aPointTypePanel = new PointTypePanel();
		frame.add("Center", aPointTypePanel);
		frame.setSize(aPointTypePanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of com.ibm.uvm.abt.edit.DeletedClassView");
		exception.printStackTrace(System.out);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (4/30/2001 3:23:30 PM)
 * @return int
 */
public void setPointType( int ptType )
{
	if( com.cannontech.database.data.point.PointTypes.isValidPointType(ptType) )
	{
		pointType = ptType;
 	}
 	else
 		throw new Error(getClass() + "::setPointType() - Invalid point type specified (" + ptType +")" );
}
/**
 * This method was created in VisualAge.
 * @param val java.lang.Object
 */
public void setValue(Object val) {
}
/**
 * Comment
 */
public void statusRadioButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
 	pointType = com.cannontech.database.data.point.PointTypes.STATUS_POINT;

	return;
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GD4F954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E155BB8BF494D516A061A31FB1E05000611C0851099AD7F4B33BB9AB3387334C0A9AC766AC0E38131D4D4CF2F6F00C4A2C4C0A4A6ACB678B84C8C2142000C1BE89EBB69FBE839323E3C24084B2B3D9095011A82924AB698A1D2ECEF5B5696EC458FB5F2BD7DD5D29CECB4E19F35C1CF3F9DD775DF7FF6F3EDB6F5DFA10527763F333AC2504E465926D6FC496A1DDFFA06427EFA47FA747CD92D3730861EF9F
	C0B1D9694D01B69A7AF292D2730A485A5682F8CE006738C529F9BB61778E72E2EB66897891C64EF268EBFA74055A716554DB391CFC7A9CD7D3886DB600C440E13BDEC47949B5E59ABECD43F348329032328FE91E2E592D61CE42F3BB40EC0078BEC61F826DA6AAF9EA7DC90D2EEBEEBCD97FC405E5AB72B17064D0564F46B6E8EDB9F9B0EBCDBF4A5AC07544B1AABCA78120715964C72751600676AB7D1F744A03553D0253A9BBD53157DEBDAC798545D66D12A527DADF5F0ECD074FA5368BCE5151EDC71E1F5696
	D09E04D442F3A6455D62C5B98970FBA940AA9D9F65C3FC2D141A578250ADF15BBE2EE850F4370EBCC8121FF5E6EF8FE31BD7E2B645CB815BE2BC2F676F2273606FC4FF27C39F99C84DAB85B889E08570823CC767FF32753F21ED6CD1CF49030332335165EAF25814BF4AB651813F6B6B21C743FDAE764A0A0D90B647EF56D41968B39E30F10535B7BA0E79A471A81F6F244F6FA169FFFD4E12EF62137444DACB74G73C562C040A7CC76693032D78C8447E3306C0B44A8BBE11A1C1DEBA2BB79194316F40D4FBA03
	6C83D7F02E3369DC9741EF9C076399FE9B455B8DF8961B73E94C6073A874E5DC6131E15D46F5E94BD9C80A6A5ACC7D901B5AE8E9F6B09D3A9C213AB47B11778C871339CB57251362179B70CC1719D4C7FCA60329F9E9GAC3E26DC42359A6BCC4DCB86C883D88310A3276615839C13F90CBDE12D7916B156AEA8EA13C7F258C4053031D3BA5F01D6742A0A506AF42B0233D73CA0A8125063905DC423195D09BE68899AB71746FEAF4878CCF40A0A20CA3293388B0322AAAAEEC633303398E9941176F6C803A28898
	F4914EF7F1678EE89D02DB7D42E593D4B1C0417A6F6B44B939AADCG0E40C073D1B1D952057E6AG7B9DG233A9F8EFE097C4E098AE40B7A7AB6D93E62F1F520A5A4CDC6BFF7515C918D3F93E56E63242F90D764CA4D2BF4F1BEDDDF169878B4CB3D283E2078789A5B6443B18DC0EFF57138F9494A6326F96B5D246A193326F10360F231F819618ADDBF2514AF6A364445F37D9DDE6B8D2E67ED1AEEB43F394C57DDF04EC81E100CEC2D4D0DC0477C7C6EE51C331520CFA6C0619077756DDF163358ED925CA20F56
	BFAB90E822AD60F92667D9FF2D6653ECCDAE62517FDBG707D341D52A1DFC25E8658295BF8190EBDGF25BGD400B80065G19G25CAEADE83008DE0B6C0B2C03A4267A05A37EC82F2EF541FFFE9E0367C833565C66C58759D50BD94D86C4C39047A0AD7C5275AAC3AC4278DFE38353E18CBE81716577AE4FD4CC20AF7C19EBC8509472D6363AF215D022AAAD20FC7954F0B2AAAB97B837D09D4165B53638A24D0AE6B41CB68CBF9987220CE1FFC89F3C82F6CF40AB40875A1DA7F5F509CA6BA44C1D07A146CF12A
	8C3F348B77FAD7C41F16C317F56106702A6070E0BEE67B27A5824E17CB1186E1E9E0CAA7F6B766B7F7ED1941C7B90043FDE083346DEE1E27067773BCF540FFA7D9F67920456CDD9BFBF733E5091B650E656EC01EE2BABD7415396EFFCBF32906F7B3DDFFD700E303F7F2AC7EF61099B5E8C713C7D5E567E70A6CF1E939626BEE643F8664EEC03BD41EAB0EFE0D7DBCAFEBC962146CF0B097C3DF070AFE307ACA8D36D42A4C8F0138A93FDC04F3A98EF9C4F85D618B621C6E6F0A4BA5A1FD97B45F337E1A4B283343
	70EE59C8E55E61677B6AB8153DDF12D5164753D4A61B51FDB14269DABC6351ED9A65F45D6352CD394E693CE16856FA106E8461F4519E167B9221CD81D88DD02D5365E6F13AE3D7D92EEC03366BEAA85D53BA5DA874E1DE0D01B6E18C5D828B27DBFE15655DF4E8B38DF4CB06112EB71B53958EB33D8E8C33F9338DB3BB98DDC58E275BB84C7462F425DEA35D4FF43AEEAF530B53F90368EE4965F4E9DE269727DB235131B84E3F0CFBDB33B3CB3FC72A7628926E948495120D4B239A4EAB7FC2779C18E70F797839
	64CEB74637831E23G62FCFCED7EE4ABBF1744DC09A799F73D9B764C156CE33A2D768556E67994FEAEC994D0E6199F76B4FE3EF66E3BEC5C9BB53A5DF22FC437E5DADC5B3A0DEB1761246E4D5076C90ADBED05646A65EBC06EC6FDF15F2621B59F7DD7759E6E2381612A602DF68FC38E2D2FB72EF03E677F660C5156255B0FD836043135412F6D337D81DB2B608C46764E4B691E9A73C99C742F82E03965439144CBCEC91584076497AB8FB39DEB1D9FC0AB58EC15ED869D2B9BFB5C702668D5192A9F9D3E4075FC
	E06F27BCBE7EEAA1A9906AADE6E745594F6F36940EB07D0E0D84741C3D6A317CF8G266B35912E6BA850A483B07C11D10E4F845C01D10ECF3A4E71DD000B51715D046353G17738D3779B1026F8EC1692FE3F9F045B9E39EEC780665873BCE7FB225D76D121B59BA5A49ED1D1416C44AF6B4195ACA577CB74C4604EF823672BE26E3F59657F1BD50D4DD63B267E7856B78630BE6BA5A2EF1DBB768FC3C00DB227339EC8966F37F10991F35BA1FD48B673391F0472EF3BE07330379BC6FB56363384E794C4E666746
	C900CB51793C12FD84795097D1B5EB5AC525CFD686C5DBA5C6DA35B02CD6533DCAF5A36DBE0D3F8F733593D30E3E971C02C36E7FDC30C9B20B444B0DA12C5A42730228E57338F807B1E69B2E6FB01D47995B77D856DDE773D7B6A9B00F238F67E5E7FE7D0696336DB95CDE9B5044CD62B51C262FD1861C56D40FFB627AB299B939DC46AA60DFB8194B282CBCCDFD5A6B9974B884D5D6A6AEC80F4B94DDD6B3489955E5BD5D558CED2F6068C5D122ED622258BE7B17FAADA4F9B2736D0A490133D87A643167B2D6A3
	F8D2C57D56CDE6B502ADBA7EB90AAF3AA900E776FC1F3ECB7039967AE2EE62675A8A9B6E85B740F36994D8FFG4DGB60028A97C1D70EA77D19221EE508A6FB8850272FC5046D7FB8FBCA0E06C43DED5FBFEC8C0DF523DAA61673E79A22E27F8103994E095C0A6C0E1945761E4CFC49DCEFBCDF5E0FEFE429F295ED595956CEBB6EEF544F16DA16318BDAF7461FE418EFDD1GF1GA9G69G9BGF6CF65F6CD6B79A822DD98B09DB20B1ED321A785CD4F28110959B7AF62380629E663CE0ECECC5E190863BACC65
	BDFCFDE27216C59C67B01557C7A6A6CF0DB8AEAAC49E4BAB6FB46DA5E6FB2233068483FBD43AAFFC6B9C6E41B00747CD656B780A9D63ED95BC2F83A818867B830096G9B400CE9BC5ED677B4C50AB73DBC87B1474F39E343CE3377D856447C5494F1DC7C3460F14C76DCAB5FA727CC63F517B7A728C321A55238F526BA7CF2086BD0B01D6B30A2A2AFF39DFE9AF15C27534DF4A8777198306ABA4C4B8E65959DB256DC0778B1637606BCA7CDB74BF19992465CF2684BG58GD0BAA3B56FA4C08700F7860F398FFB
	BF0E94F3DA0EBB853896B8B31B041B266B051C0959E89BB3AE54465899E663B667CECCDE5918F1217216184A7B7B894ACB0BA8EFC508BCD66BFE0D1EBBB320EFA3G434DF551DA774D10C3EE66B827023896F86E3E19475BCFD4FCE7C741F3BC40B2008C001C193C7FDFE9FF65CCF6CE6F524E7DF8F6E67D8F38F9BD9947F13D22EE9E3B6761003C634769C7D82675B3997F284B58E420499EE7BC6E11A2552A73351645F72DADFCFD71044D3C3E78314D3C3EF84AD64C704043789506771F330DDFFFBCEF8BDFFF
	DCA40657B3C1B45773BE51DC4FA56278FA260861745CC37D7FAB956B46E36A1EFF02B78F61F53F2C11CD9A4DE9D57EE31D4E7B37FD616D5858E7EE07259FEBA4EE5923740A213AEE6AAF537B821927D59587D19DC6D3C2472BF0G9447583A0B0EE7FD6146FF509F4E9F4C56C702ECBD226365D1A37E280EFF7E3A91FFCC47AF22710B7876BEC59EEC2F5356C1CE9B66071619DA0DC4EBDDDA9BB5F36C1AE2FAFB6C61FDBDEAB77775CD76703E3E599E5957736C61FD3D409E59570B6D68039E49A6A9DA45177966
	CAD630EF0E709CEFB5664ED84D97C9DA3B4C44B70C5FF1CBB03F239A3FDF8D99792D52462F555A0DE1793D95426F18466F204F482FE8969BFFC0EBCFC25BB6AB18A7DBE30F3F331D2ED137DBF45397AC969EB5D90FE79B6267D4E0ED7DDA8ABF5F3F114C677BD5A97CFC3FA9C51E6F7C0170733DEDA072FC6F9C98FFED4D49B15A9AD8DB0D39C6FCE0ED7DB6889FD8DBC5F52827CB917BC4C591ED67A5BF662A58D978FEBCDC076F47EE786D1845EBBD09AE7CA66390FC32C70535B79B69768C71EFAF69C0379EE0
	B760EBE7337A91BB8BBCB40CF150873BF0B0633CCB6CA587666058298A9F9B8D7489G4B8156G644C61B2DFBA0273F7DE44EF9D78A125CD68919D1A7E8FF7211F9AC3EBC73AFD5DB2761F8F2D55687D3B1B708EC023E91D05535C67443CFE4A2CBEC278F717D20F31F6661843EC0F1E43779A6F35517BD1701C2263EC9417864F69BAEE5D9E442D03E772BD0E7B5F3DFC1FD2286376D97958AE9D17FC080FF5693837AB7858D19D7766909F1B266352AF723199BA6EE3AB9F1B29639AFCFCEC43AD9CF7DA9F5B2E
	636E394847F66938E3DE44A54173B200D53760FD2F370708BE9753DC6550BA4561F23D4854AC8D0ACE3CGD1F974F8EDD579115A5A432C4E97BFEC3C23F5F2738953EFF988E551AC0FD4E96D52697B357A5169A1FEC74B86FDF151BCD66B3A718C201FDBD1117A7A4F944956A474375148E76715B9540FB8FED5B47F3E718865D9B8977619G8DF379778D6FA1DE73CF3AE7A1E91BF44E747BC6C64791CB47DC2627F7EE20AE1968F9BD1F493CDF61B21321BFF3AE2D0FA94658C3DF4E8502EAE8D97F9637319F57
	43E1E26C7FB8289F757D214558AF8775679A46B35B1EBC48ED5B78E4A2F17C6077E13F5D9C1B275D4D505A768B7B490119EF64B33F3F52D7C6CC6B85F441B37B4FF978B9B5FA9E4F8FC768BAF2537CA05AD86EEAED63F765D668F457BCD86F6E131DAA2319C3E9D6C37F9A1DE61D1FFE970097820B4F6318F2C94B2DC8EF5F5B2C59D1FA6B58779D3FA3412192E89076EBFD8D37327AA58213FF162E2FE840475D4A5703D58D482B2A60725242484B9DC7DE4698F995B4F6B2813FC117B743931057E16572AAE34C
	65651B485B476D0B8915A752DC5182780E98AE6F7E2B81F9BF3C4865C5051137E99C7BE28D72585C1D7322FE2293BFCD5BB49DFA3CBC8E92E278DAFC7A12B1F794AD5CE761FCC2F5C85674DA9693D80B2CCE30E898792E87FC51FCDE9378F268C3E8AF8883DAB56034CD02978AAD87G32DD11DD22227A9816FED95776C26706A255CB4C4EEA4524F30263665D70B8D6072FD1681D5279813FB37CC90AEFB060D9FEFDD44F2FAD5017B41F5775FE6C45F5DEF4DBEADE95C0B3C09740A8C06CED5C17F78D611D25F1
	6BAB9D322B8DB6F58E43D10D5937C41958BD50C773F99ECB7E3B45A4E55AEBD9613E4BA65D4662A1CDEB57F66C27F8E61F60C77BB6C2DF596D708EG68G3083CC3A1D5B77821D832C92C1B8B42A2A50EB27DF1F39F197A4F1B800468D93332D5DE334AD66F66DBE2A56AE3DBD5CBE3C3B0D66B545A332F5381FDA4C7A566C416F42C16F5DB310A4ABBF30C9382D7A202A9CDE3DF5478E73BA5D823A9F415C3392643DCF581D29829278E3B24E206BB45F7E22107BF6777DF71066E7DF4B626BB8D45F02D86D9D91
	9BD8F3EB87B666B31E55B9813D938F5D185EDFD5F03D23E3F9BEF2648774AD589C603D72620D711EEA653CD7683CFF37A540FB5E56G6F5F5FA06F17AEF25EE481677D03ED81FEBEAF1FDB96FFBD38DD564E9AAFDFBDC84264A4083233A07F4FBD83382F967A75B10B071139FE76E029CA6B3BF79877827A5D82FA0D62BC90BA7BD9FC9E47E30E16E7BEB17CFEECE8A277ACEFAC2FE57CC5E5B07B1F7506BFCB7E5CEBFE16DC63B52FB77D5ABBFE5D6BBF3C616BDE2FF90D776E583C4BC5B8B7A7A1C6BAG3CG33
	816297701CB35D07F107B72247661B5336FEBC4B62ADE8154B9F700737F5489F796E345F0F8B2AC7060352A0726778C9FE1CAFD5F6D1A443CD75230F94295F2EB2D28DBF4B0F316F907BA81A1DA37ABD7C8C32F481BFDBBC6767E71015BA6E1391FE86C9DF406B335B4B0D7933EAE1EA5E278B472F3DB25FBED50CFB158E20F58144GA481AC83D88D1089C06EC84DAB852882E8863082B8GA681C483A4G245C4167E2D65661F06B92105AE103CE08A2C8B8A5DA0C77D49875CF83FE99F7FC1BFB4EB3036AC16B
	E1CCC15C3799774AC563385DB026B642B8E65FF0D1785839D694B9F6269751BBD9B067F862576E6297E16DC16E6BF30BFA0C4EADAA52F12D6EF659ADE1C6E2FB3F989AA74D20EFFB9C4F1B77622BCA5BFFFEB142771F13CCECB26EADC3771F3876E262827B9D96EF074AD1DEBC60DF56F19FD2DC8A600E6B3E79C5F1F85F2CAD0E6C1B5F941B2F2BD70B472E2B0D45666B4AD2E2DCD7DA4D2A846D6A0758336AF405BA2ED3479517EC52F097A51BEA5771BBCBB6EB78969185F27C1E9254EB18622C9A6E83832ED3
	43D514607C0249F6D961E32BCA6819CDF2B8346F9214C6F242F46B7EB8CEF514DC02C16FFA9D57294F052FE26C7CDC2BB04E8F435DE99DCBF70FF5AC5DE6933A6DC1F4AC66EE42A9555E55CF79EF6C5DBFED043F7BD769B1FCB7DECD574EFE42A143DDBFBA9D8C3F5B3BB5006753212D69D9C177A38A97256615AF9AFFCDB35B960FF0FBFBF55C77C7383DF88EE47F6FEF03279334CCFA11643A95D2B2361653783D671ECD3A5CDCEAA116D612E7274A35125CE1545E0B57A4D7402A7ADAA5F92C2C87BFD87DCEA5
	DB8C05BA15EC8E1464C05056A0077E684E637F1E76738B6EEC1257CAF661197F3C28306B10B6AC99EB7F2F668C59C47F439D56BDDBC90E1BB639EE5567905BC8E95F67F8975A8D7C75CB8320445B748ED48D5EFB2DE9915CF6959767C7EDA44709D78CC8896BF6884E7E9A36B37A089A1555580D53C67DC9C05F9DD4DF64D22DF359714876037B4E2EC949A616B3E433C396ECE704DE303515ECD26D0A68364B8E1B577507CFA92382FA8445BDE6B630FBCFB79F0D2D6E009422ABFD453BE8765635DB4FD88871AA
	2406D2E20923DA27301D328BF887D4D4DAD57C9FCD32F2C06F1B3CD7BD7454E7BB3BACA4CBB3F208DCA385DE97CCCC1FB0A8B9FC7003052F9BFB8AD550D6911CEE3CB2861A1507E8463BF4815537BEDEBB7F5E573F248282C3D572BEF3A58C2D699765411A463EBE49A96A8C402F40FE0F4E9EE92AC3E8CA3CF35FBE7B4CDBBD90874DA4FB3036167EDBC77FBD027F56D1CC9D45549D81672EA3637E0576FB394DB4F8C01F6DD49F1C7E6AB3B0036ABF3F7878621FB52C4B846B4E10DCFAC38CA621A8C075A74FE0
	0F28CCCB1A3FE7CBCC46AC1CA397060A977996F9F50EE8E1AB4DFB99D70F56E77F5BDD599612836B073D87A0F8B119C370AAECB9E543FB00469D7E15F2ED2932F9E32291311B470695223769130BCF4514F4877D771D9BF6950EAA23E35F55AD5C479C38673B2FE9657F9BCDEB42E82A12DD6CABCA39EA974BD558D8154BFD656CFF9CFF772CB891EC05710F55948EDFB75671B6CCAD8B7B8D20A5C17BC61C90DA47FB0C56A8E3811F1C407766BBFD7FC3421F194F8F8A8E07AAB82EB4BAEDF8FEEE7229E2604C16
	66C37EEECE23F5323EFF72215E8306B17F87D0CB8788389CF0CD4895GG04BFGGD0CB818294G94G88G88GD4F954AC389CF0CD4895GG04BFGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG8296GGGG
**end of data**/
}
}
