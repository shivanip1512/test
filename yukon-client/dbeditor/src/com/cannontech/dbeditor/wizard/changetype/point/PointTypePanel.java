package com.cannontech.dbeditor.wizard.changetype.point;

/**
 * This type was created in VisualAge.
 */

public class PointTypePanel extends com.cannontech.common.gui.util.DataInputPanel {
	private int pointType = com.cannontech.database.data.point.PointTypes.INVALID_POINT;
	private javax.swing.JRadioButton ivjAnalogRadioButton = null;
	private javax.swing.JRadioButton ivjCalculatedRadioButton = null;
	private javax.swing.JLabel ivjSelectTypeLabel = null;
	private javax.swing.JRadioButton ivjStatusRadioButton = null;
	private javax.swing.JRadioButton ivjAccumulatorRadioButton = null;
	private javax.swing.JRadioButton ivjDemandAccumulatorRadioButton = null;
	private javax.swing.ButtonGroup ivjButtonGroup = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JRadioButton ivjJRadioButtonAnalogOutput = null;
	private javax.swing.JRadioButton ivjJRadioButtonStatusOutput = null;

class IvjEventHandler implements java.awt.event.ActionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == PointTypePanel.this.getJRadioButtonAnalogOutput()) 
				connEtoC1(e);
			if (e.getSource() == PointTypePanel.this.getJRadioButtonStatusOutput()) 
				connEtoC2(e);
			if (e.getSource() == PointTypePanel.this.getAnalogRadioButton()) 
				connEtoC3(e);
			if (e.getSource() == PointTypePanel.this.getStatusRadioButton()) 
				connEtoC4(e);
			if (e.getSource() == PointTypePanel.this.getAccumulatorRadioButton()) 
				connEtoC5(e);
			if (e.getSource() == PointTypePanel.this.getDemandAccumulatorRadioButton()) 
				connEtoC6(e);
			if (e.getSource() == PointTypePanel.this.getCalculatedRadioButton()) 
				connEtoC7(e);
		};
	};
public PointTypePanel() {
	super();
	initialize();
}
/**
 * Comment
 */
public void accumulatorRadioButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	
	getJRadioButtonAnalogOutput().setVisible(false);
	getJRadioButtonStatusOutput().setVisible(false);
	
	pointType = com.cannontech.database.data.point.PointTypes.PULSE_ACCUMULATOR_POINT;

	return;
}
/**
 * Comment
 */
public void analogRadioButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	
	getJRadioButtonAnalogOutput().setVisible(false);
	getJRadioButtonStatusOutput().setVisible(false);
	
	pointType = com.cannontech.database.data.point.PointTypes.ANALOG_POINT;
	
	return;
}
/**
 * Comment
 */
public void calculatedRadioButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	
	pointType = com.cannontech.database.data.point.PointTypes.CALCULATED_POINT;
	
	getJRadioButtonAnalogOutput().setVisible(true);
	getJRadioButtonAnalogOutput().setSelected(true);
	getJRadioButtonStatusOutput().setVisible(true);
	getJRadioButtonStatusOutput().setSelected(false);
		
	return;
}
/**
 * connEtoC1:  (JRadioButtonAnalogOutput.action.actionPerformed(java.awt.event.ActionEvent) --> PointTypePanel.jRadioButtonAnalogOutput_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jRadioButtonAnalogOutput_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (JRadioButtonStatusOutput.action.actionPerformed(java.awt.event.ActionEvent) --> PointTypePanel.jRadioButtonStatusOutput_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jRadioButtonStatusOutput_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (AnalogRadioButton.action.actionPerformed(java.awt.event.ActionEvent) --> PointTypePanel.analogRadioButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
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
 * connEtoC4:  (StatusRadioButton.action.actionPerformed(java.awt.event.ActionEvent) --> PointTypePanel.statusRadioButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.awt.event.ActionEvent arg1) {
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
 * connEtoC5:  (AccumulatorRadioButton.action.actionPerformed(java.awt.event.ActionEvent) --> PointTypePanel.accumulatorRadioButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.awt.event.ActionEvent arg1) {
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
 * connEtoC6:  (DemandAccumulatorRadioButton.action.actionPerformed(java.awt.event.ActionEvent) --> PointTypePanel.demandAccumulatorRadioButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.demandAccumulatorRadioButton_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC7:  (CalculatedRadioButton.action.actionPerformed(java.awt.event.ActionEvent) --> PointTypePanel.calculatedRadioButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC7(java.awt.event.ActionEvent arg1) {
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
 * connEtoM4:  (PointTypePanel.initialize() --> ButtonGroup.add(Ljavax.swing.AbstractButton;)V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM5() {
	try {
		// user code begin {1}
		// user code end
		getButtonGroup().add(getDemandAccumulatorRadioButton());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Comment
 */
public void demandAccumulatorRadioButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	
	getJRadioButtonAnalogOutput().setVisible(false);
	getJRadioButtonStatusOutput().setVisible(false);
	
	pointType = com.cannontech.database.data.point.PointTypes.DEMAND_ACCUMULATOR_POINT;

	return;
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
			ivjAccumulatorRadioButton.setText("Pulse Accumulator");
			ivjAccumulatorRadioButton.setMaximumSize(new java.awt.Dimension(103, 27));
			ivjAccumulatorRadioButton.setActionCommand("Accumulator");
			ivjAccumulatorRadioButton.setFont(new java.awt.Font("dialog", 0, 14));
			ivjAccumulatorRadioButton.setMinimumSize(new java.awt.Dimension(103, 27));
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
			ivjAnalogRadioButton.setText("Analog");
			ivjAnalogRadioButton.setMaximumSize(new java.awt.Dimension(69, 27));
			ivjAnalogRadioButton.setActionCommand("Analog");
			ivjAnalogRadioButton.setSelected(true);
			ivjAnalogRadioButton.setFont(new java.awt.Font("dialog", 0, 14));
			ivjAnalogRadioButton.setMinimumSize(new java.awt.Dimension(69, 27));
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
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G990FA4B2GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DDC8DF4D4553526FD340DADD57A0C2B2125EFA1269AB536287415D65A97ADF5E1CADBEA317294DBFC4D6B43BE2C384475D2CB65CE82A1C00004840890E192C218C088137F908266E792888409C9C414A8391939C986664F199B9290716DFD7E6E1D19BB89C1DD4BAC37F74EF77659E76FFD4F4F3E7B5C0B1433EB5AEAD3CA0EA419D6CB6C2F46A4C96967A46954AEF7AE474E1A93CD126EAF9FA8CB7AC6
	53AA388EC2DD5415C4D3263428B69D4AD721DC79E122E99B7C4E161E5C9B5F81BFA274B3916AE67D7105B323777362E65E4F9AD23EEBD386DCB782CDGAC96E819997B7F0FCD398C1F8B650D6CB712A9C512E664A30F3BBCD707D766D33E09F00D816AAEC01E27B665317AE44010A7937042824ADB8F5735C43B2F5455B23E1EEF45C94B93EFCE5900FD6964F31A4A5AA6302BD95A1A020EC815B6365AD0E647450123F62F3B54AEFBBCDE0F2A5887CB9DFD0A4329FA7D25434EAB325FD1EA9F14BD830AFA592714
	7A3CCE0FDAD7570117EEGBAE40F62326EC65DEFADCFA7F2C1F768EBB64156D7203FD478BD89A8F6B7577FF79B6A19FE5197632445F7FCA9227ED2A1553B30D053BF69A7ABD7AFA8C4792919992C1D834AD30128AEA7B339AE39FB38AE2B0B1037834A03C093847E8A41E320BC83E86E9E2E63113DDC47339F7DD8CA6E3BBFE5D3849D976F213A2597E9BA764CDD107A9A99B39F6760FD37C15DA050D520E9C089C00B01F61031DA1B3D8F2E0DFDEA33576D76FA9AFD3EA61743FF4A6BD0DC703B2E8EEA98562958
	3DFE07A4517198D3DEE2208F276422603108C4EDFBC76263A73E7387122A5EC546C428EDB31F181C1231175934D7334D35977D76DDF2EF9B60370968A2C9940FA7788C9DCE6FD59641313C886A86AD5C4FE7B7F2DD5AD7CD17B24B9ECCD9E320CB4CA3F1A94BADD4070CE2CD976A63F736220FEB816F86F281CD840A851AD34CFD7C656C0A31FC5C86635F6F11DDDDC3FDBE592FB68DB9DD8E454FFC3DB6F3B4DF9B91ED37ED5C6D28DF62AA7477E851038F987AE5D15C37D396B3FFA46B7CC27B6E0858F76E3E70
	3131D15C434BE4CE146C2377F03540A7923CC10753FB3B1E60D86E063AD9C0147F5ADE447376D37ECDC24E4D843769F0AAC7A2B861033AA5C0747E3E3C8D57C5C7894CDB20982099C073011682C98778BD7EF576416B1CC79D3A7BCB5BFEB5E7ABDC159155AF37F9822A6C31ABE7E43FD36EF3A98109715C1203FE6D8BEA97CD66379D7AB826F8943F2CBA3D9E10AE3B9598CC814A733D9C9C77F63FC2EA3B1DEE85BAF07BA4AE777BB959E8339CD05F76B9E4D551B8E87DFDB9B8A6AE49E7C0A288005F9D8750DF
	BFA4EB2E99FE579F603EBA709AE251D00E97FE31EC598C5713B03895D7DD1D4DE9C7B5E47FE5BEC6EF2FB043B58178C7CAE18DBD18E8BA8E348CA8461AE8EAAFCBB4E51473797A3733FCDC5AB65CA715BCF13F6138C41ADDCE4758027250F179G69EF8560F99520BBD08F50D52029C0330196D570BDF7DB450488E398F55517D1FF54D70F21AD283FD17B8C26975ABA5AFCC4BFD0DD6AA26A32BA240CFB4F0690EC8D9B5BC66D33A4ED6E0F264BFAA0FA1F3F3B9D674A8A70D3EEE5222936125FEB7B96B31D83CD
	F2C061237E349F862C62506EBC656D5D0A7AA817940FEAD3FC0A4781BF82EC4C8DECDD2B4D07FE2F982BEE02637A588C83B6A0704056AC0A038C7D6C62759F11FAD9D57D4E3EA1D569D2D4556999505A7F8B51A590347486182D5F5BC176EE30F190E8C4581A31ADDBB72E5B3D5E0BC33EEE1C31ECFE8C10B8E0927047D672FB78C991E2F3211CA4302F938CF9D688AC2B686C98774208D0AEDE13AAC79FD3340FFDB754C7EA04B1E75C47443D38F61AD83B558CDF4B4652864197CD78722A92CDB6A097D0B450EC
	20A4200CEA6E17343D3EB1640D571E6BA15A779E52F724FEA34D2DCFCBF42C2D5C96F92E24659A4F150CDC633932B9F7743930BD57E8AE503A7CDC345DBB6C514D2D22DCDC5F6DDE0FC7A193DCB411E82C7F37EE344369F12A6FB89D904AF7B92FA8706CD00DF364D13207D4426F6355FC0D9F5E4D577842ABF7CB8967A7994607B15F3FB7E5DAB59D67B32B35B51E6AD47E9A4ED945253841D14D5EA10F4AE2DD32BFB1BC10CA30B36B6F413616931D324369ED9AD2D5583CA190738E1C9ED2FDC3EA09CC4CAA25
	178E455F6F753B95076502FCC9AE150755D2F2DBCA9BC9F58B7EAEB69FB8F0549C7C1CD2F2A10278730DA1F25BA38BBEDCFC2658CA47E0367461EB61FAF72932BA94785C74560B9F2F5EE5DCEFDBAF4E99AA70185FBB64E3F8416A29449E6A9CDD2F1F11A1F2285C71DAD0CE753C5BCB75244E786C758C044A9D2F1E95D44F274CF7D2FF5A6DC36EA1178C8F551F03D38D050FD763CABEB6263C75G51592638E50F63F3555CB1CA9763553F0A6B3F7FF4AC513FD9F659D12A62786C95379B499E2F4655B436191F
	076B6482D8BF17GC9B5BC3619799A5673580685F04DDE170BAE71D0B7D2036B77DFABF2F4EBF2EE8D4DCBB960CA6BAFD69A55A7087A25C1758BD87D2ADADE5FD4EB54FE23685FE3D81FAB5A3FE0D8DFA26AFD070C6A9BC47D0B06755A3EFC7B76CC5CAB158F0794F8E443606F24F219BDF745ED5F9CD2F7C6F68DE94FF3B33663BA6852450D943FE307715EF5778E4C87DE94726F5B11A661F39915C9775A99B24A74791DEE8833B190E6793FEB197A7CDFB758073942E838468151365569D897BC58A22F5954BF
	3FA9527BA70149455F338EE17D2F76721C62D228DB85D861A14A63B824673B454279D65541F3D91D46D7DF2767EBAE66FC13810F831ADBC779528F6B79566C63FC5D003B8EEBFC1302789E92FC8B81DF26634B2C5773F977F3BE8760D76BB53E29C1FCF717F13E6500E79C0131F60472390ED03E59C7106F99112FBCA8306584FB9A824B1BD671BC4C29CCFD2E559108B50C05102423D4FE4E51507C546A1DA446825CF6144F6F3DF97AE71746C040EBF71274G9B03FF485347C994FBBAEF9DDC7B1D7E005A8681
	68889F474F66212E18BFE0B09B0B776EBFA20567C3DAC8F617BF9AB68F1395295B5B6CF5A847E1AAB40BB0720CD3990676B4271ADF497DF576A80F93BFEE5567F1E7EF1A1232BE02EFA6B31F4C507946BCF7C1AA15BDDD48DEF6144F056736960C29FB1B9D97C2AAE3F7954A40E7824B9BBC8F7C7DDAAE37D7E0C507B896A3303B84B67F8D5E7F14BA4C979A65476DC4215201A1E769106A44040A2CC25C8F81991D30D46692726C0932529BF82EB9A707644F20FC964857407B7AE7696B3CAF5CB3C6CA834370BC
	D0D7271F6DBCC736FCAF4F35C5C17B04863ECEFE5D22DF87129B68B3EFE1E322092E9F67AC65F09D7F7E4747521E46E3F07D0CC2E8B63726E7B7B0FD677DE03AD47F4995439CFB542609A9350DF45C74B6EA63C77D53AFD2230028DFE6D938DF6201A729117B252B4848AFF9CD5C771527C26D7A94A1B63BC72EE22E0B957A99EC623A842E9969B22D095F235F97FF3AFB94EDC6FFFE5AA81B5AE05D47ED18897AAD95B614D6990E33E66E4F268FD007CF9BC1D39D12048E35A05F574CF5384DEF24C31C5041E2E9
	A1FE7C8C82CBB16FE276F3DDE6C3BF4B05AE0F9A4E3B929B57253F6CA4DCBF43D0176973F8995727817A9A3109F84BEE244F8C9B9FE3074ABF5D987BED8F1E9BFE9691B0BB0BAA576F2F890BE3A26E2FF3ECF46EAF34056E2F4F6D4235F6B96069ADFCFF6DF99DF3ACFCFF6D049DCD768CC00447774CF5BB519F904B3172461D58AF0965A41EEFAA2A9C4F59CDDE8B4F41976563BD30C2399D4881F495E82A506D361DE31EC5350C0CF296F55738741A5D42F3F7162A50F673DB34BCE5126EF7B27BCD5B5DD3BB1E
	7EB2DAF9FF7B8F0D271DD9340B9FD73BCA516E7930732E632D1ACDBD2D61B9D9FA9E95CB76E5DFEB68D9660F88BE35B574ACF3FB8EBF4B1C8BF55D47F87F2F6C8D6DFF713160BED70414E93BF4CBE83B1D213EB99E5C0E4E452DCD48979EB76873D1BCBEFA584263235A63FC1CDEAD40D8598765C9C031C089C0C9C0D29B2C69EDFC3CA66DB40AA30347EB24A0D2BB8720367EBC4C566B73513CB1FDE4EB8BB97BA1F6535CBE4A68E9637749DFFCE3BACC88EB972AC334218E77DCC39DB0360997BAD46F3BB19DEC
	E16DC2F548BCE12403C262E34C13DBCEF09D7EFA03BAA40F2943A4C39D6679398E31C20742329B5321BC2CDD288E69A70DF47827056BE0BE49F5D8F803BA7CE5CC9DA69A6AD06460BACCBD4967626C5D08C96DD05F4E67DD63AE6461FB98498BC8FC7F7A5BAE4CBDF87D4E812707E3AF914C6E75134381FA86F604BCDB19C1E63D10DB1A9F79AC23AA5F78AC63F03E71D9C6E37E6867FEED79114EBA28FEBF977AF5A0916C8DB2D69DD01EA830B3B50845C1B9C1E0596479AE894A4982FB38164B4BBC453126C39C
	AB9458AF84D6AB30D5D90835C339D7E04FEE667DCEBC45474BBDB9E64420BC87A889E8551B09A68BD0BB108A9473A667FD08704EFE1366CADCAC4701F9894CE1E0BE86F3AD14571CB3A1E47CF0C279C6B892570117D1AFB582FFFB889E434A969DCEF5E90E284BE8E7547CDC0E17B1460AF4AE97FAD6484F40576B3057090C474859308F061D92A09B8DBEF1333A7FD137C886E7DFEF4AEEC5623933B24BDA465322FA1B4B040D0F866DA90B188F16316B2A5361BEE46F0A94C41EBF5FACB01EBF3796203D816F10
	5F2E044E116F94640ABAED67ECD395B71A41F212243DAA7B871430B9F68FE9CF6BA235FF30A052BC24BEFAB948C76542C7379469FD144BFCD2422E8D86BEE2E7B445FAF995C25E8F2F991DF9F7B3B9036CFAD5A7F7B27B9D9B5257ACA84F8B41E85F6EFD7A3EABC55F972A0C7ADE4CE4ACE757740EF01B72C2B0AB14ED9DC6FD676F5777DDA57A7E1D5F286F33CC060BDDA798749D9D0245C1F936E15F674A74FDD70B3ED30A0D7A1E4FE4A431EB32C15F99EF85E3E6A8D73EE5547726F2FD5FB5226F7D8E233E0F
	B399BD6C6AFBAB3C6F09A1D88C1463C3B0BA369F5E0D717908A87F1214AFCBBCCED69AB70A795162F1300D1466FC3B497E3389987FAEF16C8902DDDCA5C93FB0F1995EDEBCCB98704B3EC127BDE0D382F63F53878F7290F7F6127D298A67E082683784A897305ACECDD7AA637EE1DC5B7A7D329B26DE17CF31CB0BC9DBF78DBF9B18445ACCE35704CE1EC778E185F98FCF6E43A3816673FD3521E782D47F05E48F7CBFE8D0A3ECFA176CF79590A0FCD7E0CF916CD93053E962B2F7E42511FEAEFB0740C751DD48D7
	1E457533C03981E89068EA974DADD09B12B7F31EC500AF43F65D09A6ABD0C7B74FBD503D20CB41B3ACBC60E8A75670BADC1F42CE1E052D34BE6C44D7D70F6F6F99E7D8B49E5CAB8CCF86051CC9F6FCA69A6D7413797E925A9B690D854A539246234FAD52FC4DCBB97AFC0D2F1B7AB43A1B479F4F10F8B58E4A3384D6530058BCA8AF925854869E27AC91581DC53CED6D5B9CD3C55BF601BDA15A1E95586F48B3CB9C14E789ACD1A2ED211CA430E69217A5C3B9678C47EEA718854A75827BAB41BA205CAB30F3EBF8
	3F2A40643D084D0172A201CD2A623AAC93580EEA2E4B2AF7B8F631186B12AB30C70AF93FA582BB1C467BED90587E8D3C5F4982FB494F7B0D95D8F70077BBC3E025293C5FB9827BCA9A6FF721409EDF4B7BDDAA301B06F93F75E7B9D6C8F506F22F40EEA2188B4A938576EABEE2B1D01EA9305B8A487BEDD0DEA8B05F89512F40D289DF7D3B09266EF7B9F64A416D9D9458B38EEE6FD501FD7BB037F73240DEB84C6D0D95D83368F716407694F0FB535F636B4207D5788C24CBE1146A5634B25C47125F43365F2CAA
	14683799C8EB99613A108775F1C0A4A7A3649E2A4139F5C96E9BF07687FA9CCA3FEF68CAD9F5A914E8FD97AE0F6CAC625CBAFE16B5E951DDD2CEECEF44EF926A4F313308F3DA9E526A5E144E3E03A0639C5B45C07D7CF3641924C6BF47D1C7A7D0A9902DFFA9289E577967D3E8BD556DD7FB38EEABFEB5C3F27DE8DAC45DF2FB5837A26C1AB94BE7DAF45B9CF6C672DE967FDEC2057A789E3E371475621AA772C818312D2BBB66F7BA1A6401F6329330F33AACEEDBD28FBF531ACFE4663D8FF181D07B7B7CECF024
	086B9CFF67F429FD428306BAAF6E1E1262F81F6A9A753E66CF1FB89B5C194D7B1C73BE77E7FB365EDFF8AFFFE6527B73DAF6283F0B037CBDA3289E75BD15222F7F2F6C507B415BD3FF3D541FAB99665D4806C6755E378575467DF9457BFCBCAE37607B837DDE0F6A7BGF95611B9DD893F9BBE60BCEFB6A00F8A8F90B3BF40EF12FA7723B7F34FA37F038DB603FB2E7743A6F6A55F61C0FB245D4CFF9967E99E9309767FDB32BF74826E82227D4514E87DAD9D333F2CD17A1B2A6B0F5A36649A370D767FAA5DE330
	6F735C7ECBDD68B337BC62F48F39490B0A1477A532774C853E8502773D9A9CD3749083D31330E58B5B82B5DC36544B79E791ACC0628F4541FCD0434FFEF3845F7BDB026F934DB394C23D95087A463FE6AC5F2C9E45B73D3D213E49BACA6CF6FAC26C7EBA598FDD40BF82C4319F117DB0964AB384E6A37B619CA84F97D8F4B167DBA1307FA5D8261CE84A1339AD1DE9E35932E694DB5A6550F155CD76523300BBE4BE2E9EBDA0097A0F499EBA996A264ADC2FF7489EBA974A8B8476833207AE03728A216BF3EB4752
	F56DA83A1674A51ACE87697AG59FFEB813775717BFF38B8F88EEE44D7C458FD1AD6ECBCBEE7913F7AC046D5A1E7CBC16471D9CA7641B8601DA978638B02475DA304E79E54AF6C6376FF6D44D8762F9F457EF4FB22692620F1373D40F85C15133EF3005FEC6777639209855A21FCD6E0E98A319B4AD78516F018734589AC379E3184A84F33F3DB5E9B53168D235832429EBA6E5EA4BA27BBA096F5706FC06F2D1AG572F767743FF03D176A913A7CC1932B52A2FA9BDAA35B9AD4A5E1C9E1552149655E7B3CB154D
	609EDBD3392DE92D392FE5F85B07B61B55FE6CF2E0B827525CF7ECF8F8F8F31F555E3AED7843AEA92D2F692D2CADCDE903F65B1E378BC1CEDFB97E25C549B8D0242DD150D9BF76477F129C285FE2F1F65EGE50CE166B95833B709EFAED76B639BBE1611AC8A44480A76BCC97758DD9B74E7272E5F28A95912F1DC904D5A4EE1572589BE16F75EEF453DAC997074FED89300AA010E83B97AF93E78F8A16EFF0D3E7ECE05A629CE499EF9003C19D4D787F01B9BCA7AC7E27EFDE24D0DFD7B76709A9EBB4C7C69BD52
	2C2F76C6FC2FE8C2BFFBDEE65738FF7711AB356DA7E5E85BFC28DB8A14B910E832G5583F58FF05BEEA9C49D71844B61D49BD5D5368F12B72E7841963E8425417850CEED6B4D3A316F81BDB7586E2EF13723E71853B7E3FCB6B8204D950A5FCBF0D5075398718FA2C61CGF5C9837CFCE2F3857A7278A00C8B202BC051C0B301668FF2DFAEAC34CAE31C93F6FBFD6D4AA54525BBAA20766D4E3EB13F9C5C22EF17B44866933BE6B8E17D822AF4EAF3072E6931B8F578BB438D782E19C799B60B9796DAE1913694B8
	1C78C8DFD0E23616184B4A0B79781CB62C1FF7356B7EAD62BB8B47D93FBD6CFA75B99F0B0FFFFBCEAFA373BBD1A92688B222D95BE9CEADBE5EA551FC86B9B3E2FF542E73F5781B7671EC861FC7F9775EA1591E6CCD599D210F85CCF661852D0F0C1F2DDCCDE5363F2179EA703AFDC55BFA9BB45D7E35D113F37E93543D00A6CD8AE87D4BCDDAFDED4D4E50FEECCE1802788124257AD009393C261873CE2D514B2258DFEA489E6E9FD2286C37DAB4592757DC0F8D15428635D533E17AB1CDCED6557568C85B7D7238
	56EEAB09558AE8B208593E24CD2B6FAD9ECDEED5106DBFAB8E1775E236A66BE05A78EC4D592259FAF22BA6E744FFBDBA51F61737E96DF6276E0A5C2E4CDAC19B52F62F64EA6DE625EEA077D4CB4195883D1E5E2171BD3AF6FC763D1A2759173FD31373387D7A75346D525A3D14BF0ADF8E15877945EF565A5DD2006BEC01160CE4776EB7859A0F634478ECFBF137E6DB4E9ECD0E5BF27DBA5694E96D1EF4986B785EDE0DE752616B11CDF91FBBECAC6F21831A3C1AF15A7C47D24DE613D513F377F559CCE344D7C8
	0C287F1BGC082791324BB6FCCCCDC36CC12D62E7C47BF70BA8D7EE2E311877FA87F4A159A7FCA72A70D7B0F6E3F7555FC7F6D0135B87A824F27A8D779FEE1FDF71AE41DF2BEE2CCB40F2D61CBAFE8319E35735B558DF07DB8D9127E398276C8C6ABDF257A079162ABB47AA4196AC9E3595955BC164D3DC873C8159793F5315773D60CE55B816B85D201220026814D821A8B3488A879A20F9DBA8EDA60CA8307E36C14252E2EF94026DCF25A95FC2F1D9D3412E013362934629A6F91E71C35D67DBBFC94BBE245F7
	667DACF0EDF3F03C590A71034365A2A745AC4E35621A637484F018284ECB041D551D36E6865711CFC9282DD565680B9CD722498A548DB488F45545ED7B13153D4BEE90DF36B886704C977F4581157775C8D9647365474A467EF78A9EAFC31B7B3C2A6AF523FC0EAFACC31BD52F0F0094FB061C997A1D8303AAE5E5781F4BC85EC169A7B00D356270880C45DD776BFEAF4A3A116F184797673D7C3976C1ED5ED191596F859546677A168A6377E2CAABC67F46373AA272B73EC7AA745F78523CF96B961EDF0FF171DC
	7A6CCE446221BCDBE0673BF83EFE3E40DEB942F36EC9823BA317676613DDBC466F2CC06C389BE2FCB79F3B4F55E282720CFCA17C8B8CGBB7CDE1F62D7AFD314CC89B67FCF6131BB33676021BD11E4408CB8816B73693E8BF8A6A88F0875E3248AF38393C10FE9C0F3009681AD83CA7740F3AA1095488654832482C581CD851A8114G34GE809075B91DBD7BC0A9DAC59CDA622DF266FD951F157D4FDE34FA59D63EEC76DB61133009520F72697EC84EAGBA6B956F04D94D1251BAC93E7CD19C9D328BC61AC257
	7BBFD4631A457F783EC4771995E21F71016CB8A17F6FE48D9DF6BA54C1F17FDE3562197020020B8447D24B108FD6E59DDFFA996EC9813B6CF1B9BDCA076C606FD613F7557BAE2BCA20C37137C39D45F34AF07DF6482ACC4B39E4FD72D193D85B574B508638A91EGFB494CB92238E8DDE1C81D7D72A9B9F0114D217248F359D6BE769AFA0AFC8B26405A07A755946BACA76F5C757787946107F74AB3855696687062666075309CB419330B4137CB3D629B0F4E609C78F3E48E677992CD969F4F155DCC66F08714FB
	84D6FC9431A8A8C77BF87E6C2739E3654F74941ABFCBGB9EF305F4C160A0D7AB50BCEE7D67781794E947F8D8108431CD74827EADD4068992075132AA27B7B562A317DBD354AF84F1ADE953EE75DD5E53CE75DD7255F33A876C0950E5381185FE7855F0F85762E40E69319083D93B4769FA1FAA1FEBCE87C4FA5FA8D936CAC43FE2D435E65FBF1950E99B0F99093E394FB320A64F91DAEB60E1FA6BC98DB28429F0B090ECE1F2C537B3F8526693D36064CCD5963F0891FED2C594C66DB2762035087EE857D341055
	EFA54F4681679566236DB5465F411AEBF05EFA831D471ADABCF02F15GFB1FE85CF9BD5AAEF35C6DE8DCFF0C64B4687C1951B93E67038B073536EFF721DD8574859433BA281871DF35B3FB7B4D749F36ABE0ED3ED92F35F96148E84F05157010DFF608BF974EB822350B493D7EF63FBF0A6D681A1DFC74988C44786725552A3401E6B44C74FD91A85327FCD14E6A98F28594336E99D915B653D7D774E040AFED88F256AFEF2F7A4B3CBF1E8924CAE9ED52EBF85E5025784934D69C78EAA57BF726DA253544AFF8AA
	50A62D8A104B6A00FA592534CBB97D1D78DDF5G1E629A790291D025AD641B1F0378E1644163F2E0D04561F438DDDA05D1BAE8C72BDD32E760A0DDBD8E0F10FFCF263181DDA061F813C05FEC22AFCAA995D2329F5A34A77FC4522CD4A925D5DA67724A0ED6598E6635C9EB55C1986503DE17E34477869514BE4C0FDFD3C15C8EE1BE9ADB5A8DCB11D07A624877A6EDD83221B5C512C67C52C142094BF32960F0B48F4AFE68CA71372978AFFCF97D05226ECBBB5522DF9F5B5613A21918119FCA572434911FAA2575
	4BEE276BB27C30112199401AF5AAE80B9B96FEA2851A19C3B463D5220352DB9F335EF677AB6F118E34262A340B3A921A9E9CD03C6E030D7D7D30318A8160D7907F3A900FBC25A1BC1BC73E35654493AB7BE09C5824D43755CA7EDFC67EDF0E7FAFA3C899C14A4A41394B24D17FC26BC7384DE47000BE1B08BEF87BCBDB618E2ACFBD7F5874276B17AD856BDA255564498DEEC226467566103BCF71FFB57E365757C7AF7E865EA39F8E1591143BF1C4C8C48B5B487A3978D2C5DD6A1F6FC8CD11D635C9E9F4BF0141
	0B1B828CDE32E734C929301F10F1C71484A249B9226C2A305108E8FA7830C2B8C35CDCACEDA22CD9647FB963F695365ACA5A668A8BF7D187622A3516E90ADF93CA50ADDD9A40811B435717461B7F73497873361C94A985FCC063B6B01E84F5A37C1F1DEB51195BCBD458F8BD66BABE87F3FBD939D8B3977FCE81C58B1D76F03DC1893A8E86410DDFBCF3AA424D0989B327B128B420CA6B75AB77974E26A361B61D8C33E9A0285423CA6B34145F974F24E361A69D8AB3494088D17545B3692DF013BE8EB7690B2777
	3BA32A74DA4470CCE80F479B379914383D07982DE767E0451F684F5A123F72C944733AF9D7E88EF3711550EF0C3F5CCC1E53BECAB455FE441F159FEF4244F5243CC617DBF639D459F5315163409CC793BE7FCABC867FDAB34A8FF09ED6C96BEEEEC63DAF685A7CBFD0CB878824A0F4EC6E9DGGB0DBGGD0CB818294G94G88G88G990FA4B224A0F4EC6E9DGGB0DBGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGA89EGGGG
	
**end of data**/
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
public javax.swing.JRadioButton getCalculatedRadioButton() {
	if (ivjCalculatedRadioButton == null) {
		try {
			ivjCalculatedRadioButton = new javax.swing.JRadioButton();
			ivjCalculatedRadioButton.setName("CalculatedRadioButton");
			ivjCalculatedRadioButton.setText("Calculated");
			ivjCalculatedRadioButton.setMaximumSize(new java.awt.Dimension(92, 27));
			ivjCalculatedRadioButton.setActionCommand("Calculated");
			ivjCalculatedRadioButton.setFont(new java.awt.Font("dialog", 0, 14));
			ivjCalculatedRadioButton.setMinimumSize(new java.awt.Dimension(92, 27));
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
 * Return the DemandAccumulatorRadioButton property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getDemandAccumulatorRadioButton() {
	if (ivjDemandAccumulatorRadioButton == null) {
		try {
			ivjDemandAccumulatorRadioButton = new javax.swing.JRadioButton();
			ivjDemandAccumulatorRadioButton.setName("DemandAccumulatorRadioButton");
			ivjDemandAccumulatorRadioButton.setText("Demand Accumulator");
			ivjDemandAccumulatorRadioButton.setMaximumSize(new java.awt.Dimension(92, 27));
			ivjDemandAccumulatorRadioButton.setActionCommand("Calculated");
			ivjDemandAccumulatorRadioButton.setFont(new java.awt.Font("dialog", 0, 14));
			ivjDemandAccumulatorRadioButton.setMinimumSize(new java.awt.Dimension(92, 27));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDemandAccumulatorRadioButton;
}
/**
 * Return the JRadioButtonAnalogOutput property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getJRadioButtonAnalogOutput() {
	if (ivjJRadioButtonAnalogOutput == null) {
		try {
			ivjJRadioButtonAnalogOutput = new javax.swing.JRadioButton();
			ivjJRadioButtonAnalogOutput.setName("JRadioButtonAnalogOutput");
			ivjJRadioButtonAnalogOutput.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJRadioButtonAnalogOutput.setText("Analog Output");
			// user code begin {1}
			ivjJRadioButtonAnalogOutput.setVisible(false);
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJRadioButtonAnalogOutput;
}
/**
 * Return the JRadioButtonStatusOutput property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getJRadioButtonStatusOutput() {
	if (ivjJRadioButtonStatusOutput == null) {
		try {
			ivjJRadioButtonStatusOutput = new javax.swing.JRadioButton();
			ivjJRadioButtonStatusOutput.setName("JRadioButtonStatusOutput");
			ivjJRadioButtonStatusOutput.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJRadioButtonStatusOutput.setText("Status Output");
			// user code begin {1}
			ivjJRadioButtonStatusOutput.setVisible(false);
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJRadioButtonStatusOutput;
}
/**
 * Insert the method's description here.
 * Creation date: (4/30/2001 3:23:30 PM)
 * @return int
 */
public int getPointType()
{
    if (pointType == com.cannontech.database.data.point.PointTypes.INVALID_POINT)
    {
        if (getAnalogRadioButton().isSelected())
        {
            pointType = com.cannontech.database.data.point.PointTypes.ANALOG_POINT;
        }
        else if (getStatusRadioButton().isSelected())
        {
            pointType = com.cannontech.database.data.point.PointTypes.STATUS_POINT;
        }
        else if (getAccumulatorRadioButton().isSelected())
        {
            pointType = com.cannontech.database.data.point.PointTypes.PULSE_ACCUMULATOR_POINT;
        }
        else if (getDemandAccumulatorRadioButton().isSelected())
        {
            pointType = com.cannontech.database.data.point.PointTypes.DEMAND_ACCUMULATOR_POINT;
        }
		else if (getCalculatedRadioButton().isSelected() && getJRadioButtonAnalogOutput().isSelected())
		{
			return com.cannontech.database.data.point.PointTypes.CALCULATED_POINT;
		}
		else if (getCalculatedRadioButton().isSelected() && getJRadioButtonStatusOutput().isSelected())
		{
			return com.cannontech.database.data.point.PointTypes.CALCULATED_STATUS_POINT;
		}
        else
            throw new Error(getClass() + "::getSelectedType() - No radio button is selected");
    }

    return pointType;
}
/**
 * This method was created in VisualAge.
 * @return int
 */
private int getSelectedType()
{

    if (getAnalogRadioButton().isSelected())
    {
        return com.cannontech.database.data.point.PointTypes.ANALOG_POINT;
    }
    else if (getStatusRadioButton().isSelected())
    {
        return com.cannontech.database.data.point.PointTypes.STATUS_POINT;
    }
    else if (getAccumulatorRadioButton().isSelected())
    {
        return com.cannontech.database.data.point.PointTypes.PULSE_ACCUMULATOR_POINT;
    }
    else if (getDemandAccumulatorRadioButton().isSelected())
    {
        return com.cannontech.database.data.point.PointTypes.DEMAND_ACCUMULATOR_POINT;
    }
    else if (getCalculatedRadioButton().isSelected() && getJRadioButtonAnalogOutput().isSelected())
    {
        return com.cannontech.database.data.point.PointTypes.CALCULATED_POINT;
    }
	else if (getCalculatedRadioButton().isSelected() && getJRadioButtonStatusOutput().isSelected())
	{
		return com.cannontech.database.data.point.PointTypes.CALCULATED_STATUS_POINT;
	}
    else
        throw new Error(getClass() + "::getSelectedType() - No radio button is selected");
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
			ivjSelectTypeLabel.setMaximumSize(new java.awt.Dimension(149, 19));
			ivjSelectTypeLabel.setMinimumSize(new java.awt.Dimension(149, 19));
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
public javax.swing.JRadioButton getStatusRadioButton() {
	if (ivjStatusRadioButton == null) {
		try {
			ivjStatusRadioButton = new javax.swing.JRadioButton();
			ivjStatusRadioButton.setName("StatusRadioButton");
			ivjStatusRadioButton.setText("Status");
			ivjStatusRadioButton.setMaximumSize(new java.awt.Dimension(65, 27));
			ivjStatusRadioButton.setActionCommand("Status");
			ivjStatusRadioButton.setFont(new java.awt.Font("dialog", 0, 14));
			ivjStatusRadioButton.setMinimumSize(new java.awt.Dimension(65, 27));
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
public Object getValue(Object val)
{
	int type = getSelectedType();

	if (val == null)
		return new Integer(type);
	else
	{

		((com.cannontech.database.data.point.PointBase) val).getPoint().setPointType(com.cannontech.database.data.point.PointTypes.getType(type));
		com.cannontech.database.db.point.PointAlarming pointAlarming = ((com.cannontech.database.data.point.PointBase) val).getPointAlarming();
		com.cannontech.database.db.point.Point point = ((com.cannontech.database.data.point.PointBase) val).getPoint();

		try
		{
			com.cannontech.database.Transaction t =
				com.cannontech.database.Transaction.createTransaction(
					com.cannontech.database.Transaction.DELETE_PARTIAL,
					((com.cannontech.database.db.DBPersistent) val));

			val = t.execute();
		}
		catch (com.cannontech.database.TransactionException e)
		{
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		}

		val = com.cannontech.database.data.point.PointFactory.createPoint(type);

		((com.cannontech.database.data.point.PointBase) val).setPoint(point);
		((com.cannontech.database.data.point.PointBase) val).setPointAlarming(pointAlarming);
		((com.cannontech.database.data.point.PointBase) val).setPointID(((com.cannontech.database.data.point.PointBase) val).getPoint().getPointID());

		if (val instanceof com.cannontech.database.data.point.StatusPoint)
			 ((com.cannontech.database.data.point.PointBase) val).getPoint().setStateGroupID(new Integer(1));
		else if (val instanceof com.cannontech.database.data.point.AccumulatorPoint)
			((com.cannontech.database.data.point.PointBase) val).getPoint().setStateGroupID(
				new Integer(com.cannontech.database.db.state.StateGroupUtils.STATEGROUP_ACCUMULATOR));
		else if (val instanceof com.cannontech.database.data.point.CalculatedPoint)
			((com.cannontech.database.data.point.PointBase) val).getPoint().setStateGroupID(
				new Integer(com.cannontech.database.db.state.StateGroupUtils.STATEGROUP_CALCULATED));
		else if (val instanceof com.cannontech.database.data.point.AnalogPoint)
			((com.cannontech.database.data.point.PointBase) val).getPoint().setStateGroupID(
				new Integer(com.cannontech.database.db.state.StateGroupUtils.STATEGROUP_ANALOG));

		//resets the pointOffset to next available pointOffset for the new type
		java.util.Vector usedPointOffsetsVector = new java.util.Vector();

		//fill vector with points of same deviceID
		com.cannontech.database.cache.DefaultDatabaseCache cache =
			com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
		synchronized (cache)
	{
			java.util.List points = cache.getAllPoints();
			java.util.Collections.sort(points, com.cannontech.database.data.lite.LiteComparators.litePointDeviceIDComparator);
			com.cannontech.database.data.lite.LitePoint litePoint = null;
			for (int i = 0; i < points.size(); i++)
			{
				litePoint = ((com.cannontech.database.data.lite.LitePoint) points.get(i));
				if (((com.cannontech.database.data.point.PointBase) val).getPoint().getPaoID().intValue() == litePoint.getPaobjectID()
					&& type == litePoint.getPointType())
				{
					usedPointOffsetsVector.addElement(litePoint);
				}
				else if (litePoint.getPaobjectID() > ((com.cannontech.database.data.point.PointBase) val).getPoint().getPaoID().intValue())
				{
					break;
				}
			}
		}

		//search through vector to find next available pointOffset -- if vector is empty then pointoffset the same
		int pointOffset = 1;
		if (usedPointOffsetsVector.size() > 0)
		{
			for (int i = 0; i < usedPointOffsetsVector.size(); i++)
			{
				if (pointOffset == ((com.cannontech.database.data.lite.LitePoint) usedPointOffsetsVector.elementAt(i)).getPointOffset())
				{
					pointOffset = pointOffset + 1;
					i = -1;
				}

			}

		}
		((com.cannontech.database.data.point.PointBase) val).getPoint().setPointOffset(new Integer(pointOffset));
		try
		{
			com.cannontech.database.Transaction t2 =
				com.cannontech.database.Transaction.createTransaction(
					com.cannontech.database.Transaction.ADD_PARTIAL,
					((com.cannontech.database.db.DBPersistent) val));

			val = t2.execute();

		}
		catch (com.cannontech.database.TransactionException e)
		{
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		}

		return val;
	}
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
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getJRadioButtonAnalogOutput().addActionListener(ivjEventHandler);
	getJRadioButtonStatusOutput().addActionListener(ivjEventHandler);
	getAnalogRadioButton().addActionListener(ivjEventHandler);
	getStatusRadioButton().addActionListener(ivjEventHandler);
	getAccumulatorRadioButton().addActionListener(ivjEventHandler);
	getDemandAccumulatorRadioButton().addActionListener(ivjEventHandler);
	getCalculatedRadioButton().addActionListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		initConnections();
		connEtoM1();
		connEtoM2();
		connEtoM3();
		connEtoM4();
		connEtoM5();
		// user code end
		setName("PointTypePanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(294, 222);

		java.awt.GridBagConstraints constraintsSelectTypeLabel = new java.awt.GridBagConstraints();
		constraintsSelectTypeLabel.gridx = 1; constraintsSelectTypeLabel.gridy = 1;
		constraintsSelectTypeLabel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsSelectTypeLabel.ipadx = 31;
		constraintsSelectTypeLabel.insets = new java.awt.Insets(11, 57, 0, 57);
		add(getSelectTypeLabel(), constraintsSelectTypeLabel);

		java.awt.GridBagConstraints constraintsAnalogRadioButton = new java.awt.GridBagConstraints();
		constraintsAnalogRadioButton.gridx = 1; constraintsAnalogRadioButton.gridy = 2;
		constraintsAnalogRadioButton.anchor = java.awt.GridBagConstraints.WEST;
		constraintsAnalogRadioButton.insets = new java.awt.Insets(0, 77, 0, 148);
		add(getAnalogRadioButton(), constraintsAnalogRadioButton);

		java.awt.GridBagConstraints constraintsStatusRadioButton = new java.awt.GridBagConstraints();
		constraintsStatusRadioButton.gridx = 1; constraintsStatusRadioButton.gridy = 3;
		constraintsStatusRadioButton.anchor = java.awt.GridBagConstraints.WEST;
		constraintsStatusRadioButton.insets = new java.awt.Insets(0, 77, 0, 152);
		add(getStatusRadioButton(), constraintsStatusRadioButton);

		java.awt.GridBagConstraints constraintsAccumulatorRadioButton = new java.awt.GridBagConstraints();
		constraintsAccumulatorRadioButton.gridx = 1; constraintsAccumulatorRadioButton.gridy = 4;
		constraintsAccumulatorRadioButton.anchor = java.awt.GridBagConstraints.WEST;
		constraintsAccumulatorRadioButton.ipadx = 39;
		constraintsAccumulatorRadioButton.insets = new java.awt.Insets(0, 77, 0, 75);
		add(getAccumulatorRadioButton(), constraintsAccumulatorRadioButton);

		java.awt.GridBagConstraints constraintsCalculatedRadioButton = new java.awt.GridBagConstraints();
		constraintsCalculatedRadioButton.gridx = 1; constraintsCalculatedRadioButton.gridy = 6;
		constraintsCalculatedRadioButton.anchor = java.awt.GridBagConstraints.WEST;
		constraintsCalculatedRadioButton.insets = new java.awt.Insets(0, 77, 0, 125);
		add(getCalculatedRadioButton(), constraintsCalculatedRadioButton);

		java.awt.GridBagConstraints constraintsDemandAccumulatorRadioButton = new java.awt.GridBagConstraints();
		constraintsDemandAccumulatorRadioButton.gridx = 1; constraintsDemandAccumulatorRadioButton.gridy = 5;
		constraintsDemandAccumulatorRadioButton.anchor = java.awt.GridBagConstraints.WEST;
		constraintsDemandAccumulatorRadioButton.ipadx = 68;
		constraintsDemandAccumulatorRadioButton.insets = new java.awt.Insets(0, 77, 0, 57);
		add(getDemandAccumulatorRadioButton(), constraintsDemandAccumulatorRadioButton);

		java.awt.GridBagConstraints constraintsJRadioButtonAnalogOutput = new java.awt.GridBagConstraints();
		constraintsJRadioButtonAnalogOutput.gridx = 1; constraintsJRadioButtonAnalogOutput.gridy = 7;
		constraintsJRadioButtonAnalogOutput.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJRadioButtonAnalogOutput.insets = new java.awt.Insets(0, 120, 0, 72);
		add(getJRadioButtonAnalogOutput(), constraintsJRadioButtonAnalogOutput);

		java.awt.GridBagConstraints constraintsJRadioButtonStatusOutput = new java.awt.GridBagConstraints();
		constraintsJRadioButtonStatusOutput.gridx = 1; constraintsJRadioButtonStatusOutput.gridy = 8;
		constraintsJRadioButtonStatusOutput.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJRadioButtonStatusOutput.insets = new java.awt.Insets(0, 120, 9, 75);
		add(getJRadioButtonStatusOutput(), constraintsJRadioButtonStatusOutput);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	
		
	// user code end
}
/**
 * Comment
 */
public void jRadioButtonAnalogOutput_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	
	pointType = com.cannontech.database.data.point.PointTypes.CALCULATED_POINT;
	getJRadioButtonStatusOutput().setSelected(false);
	
	return;
}
/**
 * Comment
 */
public void jRadioButtonStatusOutput_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	
	pointType = com.cannontech.database.data.point.PointTypes.CALCULATED_STATUS_POINT;
	getJRadioButtonAnalogOutput().setSelected(false);
	
	return;
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
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
	}
}
/**
 * Insert the method's description here.
 * Creation date: (6/26/2001 3:49:16 PM)
 */
public void setButtons(Object val)
{
	//sets buttons visible or not depending if point is off LMGroup 
	int deviceID = ((com.cannontech.database.data.point.PointBase) val).getPoint().getPaoID().intValue();

	com.cannontech.database.cache.DefaultDatabaseCache cache =
		com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	synchronized (cache)
{
		java.util.List allDevices = cache.getAllYukonPAObjects();
		for (int i = 0; i < allDevices.size(); i++)
		{
			if (((com.cannontech.database.data.lite.LiteYukonPAObject) allDevices.get(i)).getYukonID() == deviceID)
			{
				if (((com.cannontech.database.data.lite.LiteYukonPAObject) allDevices.get(i)).getPaoClass() == com.cannontech.database.data.pao.DeviceClasses.GROUP)
				{
					getCalculatedRadioButton().setEnabled(false);
					getAccumulatorRadioButton().setEnabled(false);
					getCalculatedRadioButton().setVisible(false);
					getAccumulatorRadioButton().setVisible(false);
					getDemandAccumulatorRadioButton().setEnabled(false);
					getDemandAccumulatorRadioButton().setVisible(false);
				}

			}
		}
	}
}
/**
 * Insert the method's description here.
 * Creation date: (4/30/2001 3:23:30 PM)
 * @param newPointType int
 */
public void setPointType(int newPointType) {
	pointType = newPointType;
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
public void statusRadioButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	
	getJRadioButtonAnalogOutput().setVisible(false);
	getJRadioButtonStatusOutput().setVisible(false);
 	
	pointType = com.cannontech.database.data.point.PointTypes.STATUS_POINT;
	
	return;
}
}
