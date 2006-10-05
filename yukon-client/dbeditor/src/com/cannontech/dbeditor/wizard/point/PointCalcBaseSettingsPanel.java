package com.cannontech.dbeditor.wizard.point;

import java.util.List;

import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.data.lite.LiteUnitMeasure;
import com.cannontech.database.data.point.CalcStatusPoint;

/**
 * This type was created in VisualAge.
 */

public class PointCalcBaseSettingsPanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener {
	private javax.swing.JComboBox ivjPeriodicRateComboBox = null;
	private javax.swing.JLabel ivjPeriodicRateLabel = null;
	private javax.swing.JComboBox ivjUpdateTypeComboBox = null;
	private javax.swing.JLabel ivjUpdateTypeLabel = null;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public PointCalcBaseSettingsPanel() {
	super();
	initialize();
}
/**
 * Method to handle events for the ActionListener interface.
 * @param e java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void actionPerformed(java.awt.event.ActionEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getUpdateTypeComboBox()) 
		connEtoC1(e);
	if (e.getSource() == getPeriodicRateComboBox()) 
		connEtoC2(e);
	// user code begin {2}
	// user code end
}
/**
 * connEtoC1:  (UpdateTypeComboBox.action.actionPerformed(java.awt.event.ActionEvent) --> PointCalcBaseSettingsPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireInputUpdate();
		// user code begin {2}
		if( ((String)getUpdateTypeComboBox().getSelectedItem()).equalsIgnoreCase("On Timer") 
			|| ((String)getUpdateTypeComboBox().getSelectedItem()).equalsIgnoreCase("On Timer+Change"))
		{
			getPeriodicRateLabel().setEnabled(true);
			getPeriodicRateComboBox().setEnabled(true);
		}
		else
		{
			getPeriodicRateLabel().setEnabled(false);
			getPeriodicRateComboBox().setEnabled(false);
		}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (PeriodicRateComboBox.action.actionPerformed(java.awt.event.ActionEvent) --> PointCalcBaseSettingsPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireInputUpdate();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Return the PeriodicRateComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getPeriodicRateComboBox() {
	if (ivjPeriodicRateComboBox == null) {
		try {
			ivjPeriodicRateComboBox = new javax.swing.JComboBox();
			ivjPeriodicRateComboBox.setName("PeriodicRateComboBox");
			ivjPeriodicRateComboBox.setFont(new java.awt.Font("dialog", 0, 14));
			// user code begin {1}

			//Load the Periodic Rate combo box with default possible values
			ivjPeriodicRateComboBox.addItem("1 second");
			ivjPeriodicRateComboBox.addItem("2 second");
			ivjPeriodicRateComboBox.addItem("5 second");
			ivjPeriodicRateComboBox.addItem("10 second");
			ivjPeriodicRateComboBox.addItem("15 second");
			ivjPeriodicRateComboBox.addItem("30 second");
			ivjPeriodicRateComboBox.addItem("1 minute");
			ivjPeriodicRateComboBox.addItem("2 minute");
			ivjPeriodicRateComboBox.addItem("3 minute");
			ivjPeriodicRateComboBox.addItem("5 minute");
			ivjPeriodicRateComboBox.addItem("10 minute");
			ivjPeriodicRateComboBox.addItem("15 minute");
			ivjPeriodicRateComboBox.addItem("30 minute");
			ivjPeriodicRateComboBox.addItem("1 hour");
			ivjPeriodicRateComboBox.addItem("2 hour");
			ivjPeriodicRateComboBox.addItem("6 hour");
			ivjPeriodicRateComboBox.addItem("12 hour");
			ivjPeriodicRateComboBox.addItem("24 hour");
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPeriodicRateComboBox;
}
/**
 * Return the PeriodicRateLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getPeriodicRateLabel() {
	if (ivjPeriodicRateLabel == null) {
		try {
			ivjPeriodicRateLabel = new javax.swing.JLabel();
			ivjPeriodicRateLabel.setName("PeriodicRateLabel");
			ivjPeriodicRateLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjPeriodicRateLabel.setText("Periodic Rate:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPeriodicRateLabel;
}
/**
 * Return the UpdateTypeComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getUpdateTypeComboBox() {
	if (ivjUpdateTypeComboBox == null) {
		try {
			ivjUpdateTypeComboBox = new javax.swing.JComboBox();
			ivjUpdateTypeComboBox.setName("UpdateTypeComboBox");
			ivjUpdateTypeComboBox.setFont(new java.awt.Font("dialog", 0, 14));
			// user code begin {1}

			//Load the Update Type combo box with default possible values
			ivjUpdateTypeComboBox.addItem("On First Change");
			ivjUpdateTypeComboBox.addItem("On All Change");
			ivjUpdateTypeComboBox.addItem("On Timer");
			ivjUpdateTypeComboBox.addItem("On Timer+Change");
			ivjUpdateTypeComboBox.addItem("Constant");
			ivjUpdateTypeComboBox.addItem("Historical");
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjUpdateTypeComboBox;
}
/**
 * Return the UpdateTypeLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getUpdateTypeLabel() {
	if (ivjUpdateTypeLabel == null) {
		try {
			ivjUpdateTypeLabel = new javax.swing.JLabel();
			ivjUpdateTypeLabel.setName("UpdateTypeLabel");
			ivjUpdateTypeLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjUpdateTypeLabel.setText("Update Type:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjUpdateTypeLabel;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param val java.lang.Object
 */
public Object getValue(Object val) {
	
	if(val instanceof com.cannontech.database.data.point.CalcStatusPoint)
	{
		CalcStatusPoint point = (CalcStatusPoint)val;
		
		point.getCalcBase().setUpdateType((String)getUpdateTypeComboBox().getSelectedItem());
		point.getCalcBase().setPeriodicRate(com.cannontech.common.util.CtiUtilities.getIntervalComboBoxSecondsValue(getPeriodicRateComboBox()));
		//point.getPoint().setPseudoFlag(new Character('P'));
		point.getPoint().setPointOffset(new Integer(0));
        point.getPoint().setStateGroupID( new Integer(com.cannontech.database.db.state.StateGroupUtils.STATEGROUP_TWO_STATE_STATUS) );
	}
	
	else
	{
		com.cannontech.database.data.point.CalculatedPoint point = (com.cannontech.database.data.point.CalculatedPoint) val;
	
		point.getCalcBase().setUpdateType((String)getUpdateTypeComboBox().getSelectedItem());
		point.getCalcBase().setPeriodicRate(com.cannontech.common.util.CtiUtilities.getIntervalComboBoxSecondsValue(getPeriodicRateComboBox()));
		point.getPoint().setStateGroupID( new Integer(com.cannontech.database.db.state.StateGroupUtils.STATEGROUP_ANALOG) );
		//point.getPoint().setPseudoFlag(new Character('P'));
		point.getPoint().setPointOffset(new Integer(0));

        List<LiteUnitMeasure> unitMeasures = 
            DaoFactory.getUnitMeasureDao().getLiteUnitMeasures();
        //Better be at least 1!
        point.getPointUnit().setUomID(unitMeasures.get(0).getUomID());
		point.getPointUnit().setDecimalPlaces(new Integer(com.cannontech.dbeditor.DatabaseEditor.getDecimalPlaces()));
	}
	
	return val;
	
	
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	// com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
	// com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
}
/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getUpdateTypeComboBox().addActionListener(this);
	getPeriodicRateComboBox().addActionListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("PointCalcBaseSettingsPanel");
		setPreferredSize(new java.awt.Dimension(350, 200));
		setLayout(new java.awt.GridBagLayout());
		setSize(373, 225);

		java.awt.GridBagConstraints constraintsUpdateTypeLabel = new java.awt.GridBagConstraints();
		constraintsUpdateTypeLabel.gridx = 0; constraintsUpdateTypeLabel.gridy = 0;
		constraintsUpdateTypeLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsUpdateTypeLabel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsUpdateTypeLabel.insets = new java.awt.Insets(5, 0, 5, 0);
		add(getUpdateTypeLabel(), constraintsUpdateTypeLabel);

		java.awt.GridBagConstraints constraintsPeriodicRateLabel = new java.awt.GridBagConstraints();
		constraintsPeriodicRateLabel.gridx = 0; constraintsPeriodicRateLabel.gridy = 1;
		constraintsPeriodicRateLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsPeriodicRateLabel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsPeriodicRateLabel.insets = new java.awt.Insets(5, 0, 5, 0);
		add(getPeriodicRateLabel(), constraintsPeriodicRateLabel);

		java.awt.GridBagConstraints constraintsUpdateTypeComboBox = new java.awt.GridBagConstraints();
		constraintsUpdateTypeComboBox.gridx = 1; constraintsUpdateTypeComboBox.gridy = 0;
		constraintsUpdateTypeComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsUpdateTypeComboBox.anchor = java.awt.GridBagConstraints.WEST;
		constraintsUpdateTypeComboBox.insets = new java.awt.Insets(5, 10, 5, 0);
		add(getUpdateTypeComboBox(), constraintsUpdateTypeComboBox);

		java.awt.GridBagConstraints constraintsPeriodicRateComboBox = new java.awt.GridBagConstraints();
		constraintsPeriodicRateComboBox.gridx = 1; constraintsPeriodicRateComboBox.gridy = 1;
		constraintsPeriodicRateComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsPeriodicRateComboBox.anchor = java.awt.GridBagConstraints.WEST;
		constraintsPeriodicRateComboBox.insets = new java.awt.Insets(5, 10, 5, 0);
		add(getPeriodicRateComboBox(), constraintsPeriodicRateComboBox);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	getPeriodicRateLabel().setEnabled(false);
	getPeriodicRateComboBox().setEnabled(false);
	// user code end
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		PointCalcBaseSettingsPanel aPointCalcBaseSettingsPanel;
		aPointCalcBaseSettingsPanel = new PointCalcBaseSettingsPanel();
		frame.setContentPane(aPointCalcBaseSettingsPanel);
		frame.setSize(aPointCalcBaseSettingsPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of com.cannontech.common.gui.util.DataInputPanel");
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
	}
}
/**
 * This method was created in VisualAge.
 * @param val java.lang.Object
 */
public void setValue(Object val) {
}

public void setFirstFocus() 
{
    // Make sure that when its time to display this panel, the focus starts in the top component
    javax.swing.SwingUtilities.invokeLater( new Runnable() 
        { 
        public void run() 
            { 
            getUpdateTypeComboBox().requestFocus();
        } 
    });    
}

/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GB7F954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBA8DD4D45719C4D109FAF6EDE29A573ADB1B18141CB5EA9A3AF1F7CD0F2134EBDA3B1B6E5146ECEDEBDB1B651C1AEC762C3B47A41C5D4B702B0108CA9270A7A1C193D8898A12B882CD038CC4149AE088E2706F413C19F970E65E70660D8C7843FE5F7DF97398E630E65BF3BA67FC735E7D3EFB6F77FB3F7B5DBBC30A5FDA1C13E9ABA6A4B307704F71CCC2B63CCF48AE7D708901CB10D3F3096573B640
	AE726F473261B98FE889F72566961171C37950DE826DADF3D3F35F0077BDA471D063FB709207CFB150D2DE791DEFEABE6E8E41A70F36BB738A6079AAC0B2607039D5C67ECADE89472F65F88119B6C2B63A31CFE25EFE0E33C33B83E08140D2B76B1F864F6D14730CFABB6FF761CB0B49566F3DE7AB44F9ACF38AB868E1E3CF71E799D91E790287F9BDCC6544B18950DE89004AE711002B911E0D9DC31E0F86B4DF75006477EBFEC39E70D60FA8E3126E2A8EE80A5F282F6F40C733248E1C140232D3B68C456F89F6
	C8FED9D53DB87FFF6665537988290376B60ABB560CBCD7407B9A00CDA67EA50AEFD4D2F33B81D4C568791B779A399E3D57161314277DD9BB63681928B07D16AA91BD6707DE4AAB25BEF93093ED1F8E340241545CB2G87008BA091E09F0D0507BB7E971ECD7DC6336673E97E26C060246A52FF2739E4955E6B6B0142F1DD7200263B88E17EFEB42FAA06BCD3819B7744ED0FE3B6C9BEA6FC3F346BC1327151B3363C98B65918FC5436E81059A2E5B0E293467B25383C578DCE0C4D093C4F91AB6FA5B3352C1C983C
	D356D75A5279BC99965ED5C3686B2FD3DF6F0777CC9E9F8C7F884587ACF89627A7A89E5B89C05BBCA4E22377D5A1CBFB76C2D2D4539E538E8BD21BEC27D4A64395B5DA16D1874EBDD7E5BCF3CCD992A9FE31854FE4A1940F6D94201578D2F3D9FCADF7623A3D8E6D85GABG56G2483E47B211FDF4458FFF59C79BDE32CC3520D13A1C5F549BAE1E317F73E8ECFB9EC68D21BBFE8C87E8139C7529529DF15030477D95909B6681FB06EF19A7B8340639359AF6B1221E8FE18DD7249062C87D91F6FF66E42BE3ACC
	295D0ACF86863EG91737EE3679EF82AD25078AC60128CB9520351FF5409BE39AA75400CB0813CB33BEC6CC6FB59C17F8E00AB269DBA3BF03EB3328E59223E3EDD5306C201EE54042C70231DF765E36EB060FD2EDF58F843E7085B8A6D82CD4C1354355BB20FC399C071A5FDD42C31071BB1B717E9A2E61E6F95B163A8FC00D42DBF9DB3E6E83E54F84CE851EB67B1BAA74A455A3FED6E3D4DF59C8B4A34586B0E71687D0370480A03E776EDBD073ECA82B916G2419B67EF6D7990BD94C73A2CABF55A140E4D744
	62CC4EF1C7BE37E716A97BEC0A5B8873E587044DF29D3D16FE7FDF3DE25B6CAD4A83674F79A370E03679DBA76EC772D559EFB864006CF741CB1053D6BBB16678FAF5EB66186FD2FCG56F7B3AC282009DF6B44B5A899062E74078CF3C7B5696BA82FE028BF90C98D02570F1D2823B6826B5B6C3F4909EBE3C0737BE56AA4F388277F022ECDD915FDA0F43396729B8C1F5E0D71BDA40F725C30259B0B1E2B129A42BC43EA049552AB28032E784075182A483CG2E5B0A7C920B0D2AG07350E8B1E6100D803A365E2
	8D9E9C7B9AD9FD7910AD569E32608107EDC3002D09AD01489AE4B27D643358327F1266880E8FE6D15CDF146E05E77CEAC5447F6CDE1C33E0986A83G7B30087F4BBDC897B90687FE33262A4C2C263D5B4F9521FF6461108C299913599953064EF33B23E8BD5C1E0C5EF78E6D29DA72E07AB05AF315C354039D507682A48DB31B2E99E6B6E17D92DAC43FBABDB537DD1F5C0F79AD6FB26E7B31EABBCFC8298E998AE6D14900008D048CCBADF7974D47382ED4DD54ECDFBF0176DA826DD5G6BF4615F9F960A1AED7E
	50E232697E3DF1EB53ADBA132DA89871EF59F4D1332DBB0FBCBB00F6A5A8FC7123F356FD23A9985486943AE5F1BB4E6B357A1F616E695D81CF3722870DB6D820E1E1732F74223C3827F1B45F63FFE56E718670DD8AD0E6A03E76CC31C5764DC136B795G0D45D9FBCBAD2EDF9A94556C51A16BEECD77492E0AC1692AD4AD0D9855B4D7D4B7D1F28B3ED78A5F24D56EE6F24A547AEC232D286C913557FDFBBE663C57AEDBC89A477758E25542C9BB8F583C863367BCA3E2576B5F484D5AF26965BC2663C3ADC2470D
	5027B8A4F4DC6F0825E3FBC8646DFB73C55DF8A54466B6C211FDABA9B4F98FE375548F6B115F22902B27361B781FD27CCA8B1E1FCDE8ECE1FB9D506CD7C58DB0500779E59134D782BC85108E10BF121ADBB1A2E2247C7CB1F20B9A2B8DE2DE77CB2AF3C2B265F1714C79AC167FF87BA767F3C47EA3E2EF7935A33AFEF60CCC547DCAD41B0DFBF152386A28F6E254B8969B9EF51FD83798C9616A6088642F7A7A5320E33FF6D28B9336CE15B379FC4C2291319EEF34DA57632A1D3536D7622C47F51C775691CB1DEC
	1E218AAFE20E298BC3AD876085C884C886C883C88F8B7BFBBE7F4D2D6CCF833FDBEB865CA948F94D51FB14E92F65ADDF4C4E0FCF9A97ED677C510963187D8ADA047D4A004EEA51E5B47775C25BB0F177FE0838A4E8AFB2F1069DF12B213DE1D42C17276BF1CF4800F6499844B1C06F1820ED2634B034970C315C0EF91B1FA9CFE06DD2427BCFD50758EFC137C286571971D8CBF96470F5B655383C5FF37ED7C9241EE3312076452F0F02FD316B23513E587551F85FAE0607B92C27A3C11F55B7F57D7467FD316A27
	83D466A7DF5FC9678E8665A08DCE5C84B96DDF0D57C80CBA6AAC9C20383EFB48BBAD5BF91F96C3EB2EB1ED6CBFE30D4315D13EDA9B43F7EC2D4D3F04F9F89350B3G0A2F2566D68134DF93EB6C5005DB6638167094B96E0D0B717D72664558FEB9F89177712096528764E85B9F3ED8E252A22B394D10FDE8A656270E0EB7A45DA3CF72DD839D4FE87146BBAE4673AF73413393FCD0EB7AE05704DC20DE1BE87304EB13FD40667BCEBE4A9B50E5372CC34567D446505FEB2F51FD0B660479703E781A587F17FD08F5
	32AA0DEAA103106B5731DF79F1D18F97C13B82208340F51D6D2BAC37BFB20279588D87F2D047991087C86F8D9CBB47AE4626C17F8DG5BGCA00E63FA1F8BE5603FE64E77251005CAE75C3C14BE84F97223D2084D1B417B25085BD98D5687732FB77043166D6426973060B23460B9E2CD6A8E930568A0BEFB01D52EE08DA62034331689BEE08FCB9036ED5DB201DE1623277099CDAF4D3447A22CB71E3757ECB31E37521CB31F34872CBD3671047AE450BB1A6DFC50B10794ACDA1734B25C2E683F0E9B7710EB899
	C3055BF16E07E570744BA3E5E69168D020BE450B120AE39F9C2DAA2BBDFA34126D550BC72C773A769D47E31E0D687EB54E62F6BFFF2E1AD54EEB27B347453DEEB750660E0BB82B39027B37D9C320A075751F680A6B2464E927D14B76403B0F0BB3FC5A3828751F20F3E6C03BB881F2D10228AD426F0A5AE269038BC9FB421918357E266EDA1BA3014969CD08549649D0DBB01E0F58854FA5C05F1800381F592D7103367CF6A65B4799FD5789AB9D7D862534055EBC010E727E5AE62507A65051C6A778F8265B778F
	895DB23E1FCC54EF1E08FF47FE873FE3674F0E1EF2F2F0764BF94C6E7F61AEA1B1EBB73A9C197E1FB60873D5824C41F06AE101DBF907D86F539A70EC6E06B39B4BC34B689AFA816899G46B4446D5B6760F2D5CD0B1D5B443D8B424750279B201C5B40BB0D5562880CC7A70D79D500DFB3CD70583DD77058960707958A26CF64D1B6BD4203597A1F7A2CF1DF3470ED1B20C74FDDB51D7133CF0F44915BB3EFF8D0CE9570C953C56EF8CEBE8C4F9EE9106F10ADAE4550F43AC58232C3578232EE0CB2ACBD1173BD23
	7378973B533F7C8547AD386DF16C7C74019D57E532451E8C7F914527D870ACB77C03199BD6836DE022B8D7AD73E20CA6C1FB8940AA00F5GDB810AE688DBFE68BECAEED1F3F4EB01F6C8542AA535B27D7E467E45FE6BF8A2CF2C41143F3B0F2C1C79E2E63C334B4199AC9E9C7C195EDDEE29271A86E9FD8134F9GA9GEB81B681ECB3753B692EE27AE1B8B49906B460253788C2399EC59E0920F123E63AF5B7D8F5AB18496312BF6BE6CEDE9FEC5C70E16B38D33C5F857E8C44984776162F62B6477716EDFB5FA1
	D1FBCBABA4070AF7DC0A24EA1EF72A4AE0FB29292B94671B76FD62FC1388733FC567003D04C4BE31F00C6F7378F3G774B4F8B04DF767F75D708636997B34B6378E5955723E0D6E43D268FE66431B91705A23A7CDB696D69523CD768621895DB9746E363D5E246411DA3784E7C1D66BEC46264867076E91061537EC1A8C43BA50FB9E61A87F5B06B8316B2B86DCE8F6EA70ADFB1C0DEE50CDECBB921235F437C5D00A90E7B5EE1F97F4171BF445D7E54796559BFAA8FDEBBF96357E95F72462E53523C316B346FF9
	27BE4B7D33B77EDD78B3DE6BDDB8737B1743681B6B90A78BGD681BC8530F916F9E721E06C612FEF1357FD0B4B03E79A7C454D907CCF8F465735F370563F53FDBE08F7127D1AE1E8BE1CDF602F8C223F8CADC0118CA78F220DF44563B5D8D70E9F9A247B1D6C26E8D60B8EB5087A14A4097AB4218BF14550BEE862FE55A5EA5623A6AE09569B89501EE7623ED5C27FB3816D9493D7EE563B2B134C7FBC6C33663222BB01571DD35FCBB03FEC9D45BA428EFD3B8142G73G16G2C82F88AE0B340B6002259F0FE82
	E88468GF08198GC9G0BE68BBFFEB4DC99EFCD8312973B54193A2420BB797AB83557AA7F8A18AFED765472B3FEFF950E9F878F04EF9D874B42743E9A7C07A7B006FBB40C67C84D6D8E4AE63C2D8C97193836E00796D4B03B307AEC8175798610F773ECD1A75E5B05F522814CD98EFC6A9A41B62D5DB23AC46DD6BCE7320E22DE9035DBF7D4FDD8B1A7D2D3B0DEDFBDBC31A67DA60D23BA6877719CA14F7A1209727C777508BC7525C21EC09CF9322610A751A28F0BCFAF5D6366F1BC4379A8AE196318CD734762
	7BEE67582DFDD7B296FB8D3FB9B6F98D170F45DE4315E356B54CF055E3281787E235576CF74C441DB7F1F6BAA7624EA9AE43EB62BF22F2A17E140C8C855EC1659A2138DE0EEB33604EF35C59B10CBFD0592B69E22CF30C7AD8D1D556BEC77BA8FE88C7539E97280CCAC03248AD19387326AFAE6F1D6C9F57DE2BFFD80CAC9CA7E60C99DD37D79B3C6F9435C15A9CF1A6FF9C2763F317E6C5667EC6496D4DBDD4AA664EB067EEC473703BFA695D82E2568154B58C3FBFDC98418B57707CD36B34661F3A3929392766
	CE1DFF5838C029F55C8598933845B8E66FB667E49F1CF5DAFD40F033CAA7771BDBEA6D477EC7F736E6A6A83974B9129354496E49779CCDFF764C53CBAFBB0AED4456C6F23DD479B612B3025689E3DD3986D670A598DBB841875F795A874F2E7DE9CFB00B643611D2CCB1CED92755156C429BDA7EF713D6321D7EA78D2F685AC8F610BEF2024628AA3713E2F7977E6C9AB4E4D71328A70286F92D9DB99D411FBC0F1C1202DE8357F6C3BB4976639FE348EEC6D6A53F6788AB629A281C19CDD7E89821A989483B074A
	0B33D41B336C797B1D075EBE3DF9E5963135129D2AA639DA258158065A48F6432B4BC12F263A42015FFECCA74A9FC1EB6229A68B265BE72A0F4AD6F7C386B2059E8A7F655C427442D69BA1E11D9C21BDB123D61BBDDC4DDEC987D6325EE6609FFDB47D20C93B63CD635D9FFC7246859B4964CA8E131BA4BB9CB0C82EDB72A96AA83CB8E844871112EF00343A648F628F18A0D9D914E402E4B2283E7B492377BC74526714C1E428C15EE2260421C7BC3266BB5264F6ABFE591CG6C8A539FB02747BE55D1FDF6073F
	745A69757F538FF160A0D93E23C769F78D7D2E456F9A0A2921181ADAB06E8BE44ACFB4BDACF4264183726C2472207B2BDB4103463FBC7764FDBFEEFCE18BE857CAF2680F01600422C82F33A1DF3F2C4FDCFA4F01D766EF1A03BE8AE02804F15E0230B9A3EA58C65366262B476A33FEF9FF160DE44312E05B8884AF6E85903CBADBA1D9300D5038439432038B1BC30535CD0AC64466CE8EABC46FB01D0B2DC25AF5A77D5EFD5B2642D145F46C6B2606871881CB23A5B5C8813BE0AE437A265EA02F0A7B64B23CD02E
	7F5353645D091A084F1F204D5F0FA329B823D70EDB6F54364DA809FB171CF6975BB3B65C95FD2716DEC74F8EF743B961EED1E35B6B5EA7714F4DCE1F242A06248EB579DDF806BEB9EA4811F35BF3F5B8FFD07461C4C65BDA07F28FDA467C9FD0CB87888B228194E591GGGAEGGD0CB818294G94G88G88GB7F954AC8B228194E591GGGAEGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG1F91GGGG
**end of data**/
}
}
