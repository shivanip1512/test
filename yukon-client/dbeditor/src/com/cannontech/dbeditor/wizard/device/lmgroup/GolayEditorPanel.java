package com.cannontech.dbeditor.wizard.device.lmgroup;

import com.cannontech.database.data.device.lm.LMGroupGolay;
/**
 * Insert the type's description here.
 * Creation date: (2/19/2004 4:54:55 PM)
 * @author: jdayton
 */
public class GolayEditorPanel extends com.cannontech.common.gui.util.DataInputPanel {
	private javax.swing.JPanel ivjAddressPanel = null;
	private javax.swing.JPanel ivjTimeoutPanel = null;
	private javax.swing.JLabel ivjHyphen1 = null;
	private javax.swing.JLabel ivjHyphen11 = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JLabel ivjOpAddressJLabel = null;
	private javax.swing.JTextField ivjOpAddressJTextField1 = null;
	private javax.swing.JTextField ivjOpAddressJTextField2 = null;
	private javax.swing.JTextField ivjOpAddressJTextField3 = null;
	private javax.swing.JLabel ivjNominalTimeoutJLabel = null;
	private javax.swing.JComboBox ivjJComboBoxNominalTimeout = null;

class IvjEventHandler implements java.awt.event.ActionListener, javax.swing.event.CaretListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == GolayEditorPanel.this.getJComboBoxNominalTimeout()) 
				connEtoC5(e);
		};
		public void caretUpdate(javax.swing.event.CaretEvent e) {
			if (e.getSource() == GolayEditorPanel.this.getOpAddressJTextField1()) 
				connEtoC1(e);
			if (e.getSource() == GolayEditorPanel.this.getOpAddressJTextField2()) 
				connEtoC2();
			if (e.getSource() == GolayEditorPanel.this.getOpAddressJTextField3()) 
				connEtoC3(e);
		};
	};

/**
 * GolayEditorPanel constructor comment.
 */
public GolayEditorPanel() {
	super();
	initialize();
}
/**
 * connEtoC1:  (JTextField1.caret.caretUpdate(javax.swing.event.CaretEvent) --> GolayEditorPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(javax.swing.event.CaretEvent arg1) {
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
 * connEtoC2:  (JTextField11.caret. --> GolayEditorPanel.fireInputUpdate()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2() {
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
 * connEtoC3:  (JTextField12.caret.caretUpdate(javax.swing.event.CaretEvent) --> GolayEditorPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(javax.swing.event.CaretEvent arg1) {
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
 * connEtoC4:  (TimeoutTextField.action.actionPerformed(java.awt.event.ActionEvent) --> GolayEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.awt.event.ActionEvent arg1) {
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
 * connEtoC5:  (JCheckBoxRelay1.action.actionPerformed(java.awt.event.ActionEvent) --> GolayEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.awt.event.ActionEvent arg1) {
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
 * Return the AddressPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getAddressPanel() {
	if (ivjAddressPanel == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder.setTitle("Addressing");
			ivjAddressPanel = new javax.swing.JPanel();
			ivjAddressPanel.setName("AddressPanel");
			ivjAddressPanel.setPreferredSize(new java.awt.Dimension(346, 160));
			ivjAddressPanel.setBorder(ivjLocalBorder);
			ivjAddressPanel.setLayout(new java.awt.GridBagLayout());
			ivjAddressPanel.setMinimumSize(new java.awt.Dimension(346, 160));

			java.awt.GridBagConstraints constraintsOpAddressJLabel = new java.awt.GridBagConstraints();
			constraintsOpAddressJLabel.gridx = 1; constraintsOpAddressJLabel.gridy = 1;
			constraintsOpAddressJLabel.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsOpAddressJLabel.ipadx = 9;
			constraintsOpAddressJLabel.insets = new java.awt.Insets(85, 35, 63, 4);
			getAddressPanel().add(getOpAddressJLabel(), constraintsOpAddressJLabel);

			java.awt.GridBagConstraints constraintsOpAddressJTextField1 = new java.awt.GridBagConstraints();
			constraintsOpAddressJTextField1.gridx = 2; constraintsOpAddressJTextField1.gridy = 1;
			constraintsOpAddressJTextField1.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsOpAddressJTextField1.weightx = 1.0;
			constraintsOpAddressJTextField1.ipadx = 20;
			constraintsOpAddressJTextField1.insets = new java.awt.Insets(81, 5, 61, 1);
			getAddressPanel().add(getOpAddressJTextField1(), constraintsOpAddressJTextField1);

			java.awt.GridBagConstraints constraintsOpAddressJTextField2 = new java.awt.GridBagConstraints();
			constraintsOpAddressJTextField2.gridx = 4; constraintsOpAddressJTextField2.gridy = 1;
			constraintsOpAddressJTextField2.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsOpAddressJTextField2.weightx = 1.0;
			constraintsOpAddressJTextField2.ipadx = 20;
			constraintsOpAddressJTextField2.insets = new java.awt.Insets(81, 1, 61, 1);
			getAddressPanel().add(getOpAddressJTextField2(), constraintsOpAddressJTextField2);

			java.awt.GridBagConstraints constraintsOpAddressJTextField3 = new java.awt.GridBagConstraints();
			constraintsOpAddressJTextField3.gridx = 6; constraintsOpAddressJTextField3.gridy = 1;
			constraintsOpAddressJTextField3.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsOpAddressJTextField3.weightx = 1.0;
			constraintsOpAddressJTextField3.ipadx = 20;
			constraintsOpAddressJTextField3.insets = new java.awt.Insets(81, 1, 61, 82);
			getAddressPanel().add(getOpAddressJTextField3(), constraintsOpAddressJTextField3);

			java.awt.GridBagConstraints constraintsHyphen11 = new java.awt.GridBagConstraints();
			constraintsHyphen11.gridx = 5; constraintsHyphen11.gridy = 1;
			constraintsHyphen11.insets = new java.awt.Insets(82, 1, 66, 1);
			getAddressPanel().add(getHyphen11(), constraintsHyphen11);

			java.awt.GridBagConstraints constraintsHyphen1 = new java.awt.GridBagConstraints();
			constraintsHyphen1.gridx = 3; constraintsHyphen1.gridy = 1;
			constraintsHyphen1.insets = new java.awt.Insets(82, 1, 66, 1);
			getAddressPanel().add(getHyphen1(), constraintsHyphen1);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAddressPanel;
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G84E52EB0GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13DBC8DD894573534E2E2220DBF21A9C9C8EA7CD9B789AAD65A5AC6DB73EA3FE7DEE9232F26B1296D47EB7DDA5B5ABC7BE2DBDFBE7B3C3B40828A080272A3C908A18A89AC20A43584DB91D408A4213AB5A6312D83BB406A7EE5F69696C47B4E39F76E4C6CB28B42FB56AF0719FB4E7DB9671EF34FBD775C59104217E7E7D82C0504D8B208726F7796C2E2BA88D97C5BAFAD6038EDBE130D687E3D8230077C
	25BA9D1ED9FE13ED76E3A6DBBE09294E02F29914E33EE8329541FB8119DCFD71943CC499278EE81F1C7FDA6C68639C4C6263E452725A349CF8668114832E95E0069F477FD9DA31026F03F21E720EE0319252798956898D956B706B14FA95C0BB86301556C9CFAFD36871D046BAC970DC75892B3B811E59143B490D4714FA17E61A4956FFFB491A0BE3697A6750A431361715E7B9B15109C8A30BF736A23D73EA6F9F3BFD9EFB3760757A3C32585DE7F7F609CE176C136C832EA1C1F25A1DE23F2BDB343BBD3D12AF
	68EFECBC63F38B0327E815CE41AB3A57C8487B03C3D934DF609D4A1782085BFF8D672B905E2B81DA831C7F3539CD8AFFF1D74DA4F56E1E287C4F8EB03ED785B47E134F572649B276DF131E2334CB07F293G63A5184EF9D9A6F3DE2C7DD8F78B143302A6DBF910631F2078E3D06E84688BF29E0F9F66BCDE3C76E5322D2B593ABB8A0FB1C1461BB9287138215B13D6C2EDE6D1B66AFDA350327ACD36B20093G1781E2G8ED0DBF5E53D8E4F66AE39456771783C4DFE7FC937D3FA5767945D705E58889485F7CE6C
	76C9CEC218BD4E9D2AB66007C3FCFF38ADA2B059BEA05CFE924FADA2327CFBEB0601EC49CF15DB9315BED676EB32658C603C1928EED381EF213C904270DF26784E818D4FF4D5C671D89E86DAD6084F7345BC4ECBC77A9C12DF5FE24DB460A57EDF0ED963C20C074410468B1B63A93BF00E5783FEABC04E2049D681508A609C64F33CB46B68D8F35C8E76AFF9857779E017DF106413C1175BA9CA4ADC9FCD9FED2E0D0035EB9EF7BBB6AF6623FA9DEDD8FA52F0DEB6188FDBE38659FC448FEA7342469E0ABA763241
	113611276A301F2E0914C12643B4959FC3719BF4F8265BAC0A4772E620F58E018EE87D638308CF9EE2753355FE5E2178B5BABC6B67C95A8F16B7832D731A494674FBA8977DE2B21457GEC82A0433006811AGBA07390E7FA72B76A657D127CE3F2C6D17337743D38C4912506E8D480237DB3CA0C8AE214BAD8608D2E7E5B64EEBD7D83BEF52755D8DE31C913D22A448AE1F97FA97BCA298D3005579CEB65AFD37A4D22A436591E1G0F1F70FE3F17DDGCF3790107F62F78A322855E0749FE6631C748B9720C768
	G5EE78F63FCFD157ADCBF3CCF9B66F3F5BE8FF19B213CDD1D178FF76E0567D9B0EE515D58586A6AC6B684E9105BE86235F2F866DFB759E28156G345EB059D682B47D8376B8001C983EDE1F3B486D32B5F7A129FE2A5950AE91E644A8FEB1A652AED7507156GFE8BC07E27CC36BA00CEGBF408C0024CF71BD77603598253FD8052FBC05D76A17F4BC0EE70D0559FC8C13F96C35FCCA65A5C33153BC456E71398BG77315D9364A30FB07F18B9C69D266747F753B584733499A06753DC57F1BB4B599AB8A984C4
	EE756FCBE03022D353BC2BBBF39772AA760BDE39D5740BDEA73C84941B0B5F152D2D079E1FEA2B8FD2BC7A4796B058000A1F37EB8F43C39F7A5545690914AE4832646A8A4A62F9D116DD5EDE2DFDB265A59066FA830A2C5F48C7D9ABC046A6008ED55663B985BA3B6E70792E867D8EDC314A7A18C963004BD0BF74E9EECF437D9853A528655BE8D9236775DF9C274E302F716D890747BDC65AB86A32B1DA867289DB17E9BAFBE534CECADB86FC2782E4CD0275B7094B7F9465CD5F0E6FC9933569113CBDC747607D
	E77EBF0F41EC23AEB73AED7FBE5758360F679A5BF6CB6E6836FBB65748F6996DDCAE4A689B706A56428739680F3BFDDE2FC897245A0430587CB9874A61723A648FDC4EDED1BE6F9A92A14E1C04B63D167A7C0B706E1E44FD72402B5CA7D78CBDCCD67E3D4CB01E0B1F7F36F555A4E62B6BA7E9BE1971F4B18F57186896BDB0D1AD3E20D7E6783F517DC4418752A86E71BF77E25B4AF79CB00727DD225BD9DF5D8DABDF36533FEC932B3CA2748BA1FBE0G264ACE75E0EFC16AA9FC2DAA2F2979E3F964C9223A47A5
	096DDEFF10F7D1F5212A0E30B1FFB0702748B17920B75DD73D525715723E483E0E5CB2810E30B1735AFCF4CC5C5BFDC1D99D39DA20A6E0E70FCED16A71C99E51C987378B8332B2F2B3A50FF368230A3CC9B77CF46C777D4DCE27A486827F94453131ED074FC49B7B48C475B76B7D28729C3D55FA2C3C5CCF47FE4F67F141D9611F294E863E777FE68F2E55ED3006F3E2612C934B7743E773104E77C3E553EF71395D4C4DGEDCAAC7A10DFDFAB54790526D8164B48D3692F8C0FCEFFF78C7A60987479E350570EC1
	7F5D98742A6B5168EC1E7EF5B76EFF8171132088E1BE868CE745C19E2B6F5E9BC13BA038035A9960FB3BB14EF36BE28D06FFA25F58FFBE190F7145D5357F6F66E32229DF6913797BA4817B74CBAE8F04E698BCA9B9A3F33ABEE7348664407CD27EE450B9GEB7BC1AF4AB2A2A7B3995B478E70FCD293149DG72E456C752E4B69F2C5E19902FD7F61B4956G60384D289E9964757A009EF3DB347E1255FA33214EB20075917D955F0E75B629B923DA953703621E0120E0F2BABF8B3F1B2F4FF7B9838D067B9B255F
	4E7A775F9E19A3F823G7B1D817859377375726E9EFD7C589C88783ADD74082668F47B9E7D5E47F0163DBB61892BB3A037C3D09162F6113997F945B31C02D6F47B689B474948B3A9D3958F4FDB8628A7F078DA60985986265522068697DC62G6173F07D34BEEF36FCF715F5D714F9C8D664DF271B07595D1EB446533D0D188BB14A7DF5D3A66D3DC117BDA83B7030A84882F5C3542C884B2F6D4D48D264AB1C4273E8FF4CC27B6F003293E098001D69975233BE560D83DC8AGDB235FA07AB51AFE0749D68D5089
	407CECE188EDE21C4EDC1973D5D5FB190EA2BCA81F47B985A714F1D3964DA1CD7F58E9185B1B323B506A3C034D5F34BB34F9F41E3D149682E07279C3DC3E75D0A77FCEAE5FF048C83E36BB39FCCF07EC2AFCBC7FD1B5487B3A847562543E96D69A7515F2A71F639F286DD281D7BC15376B8D685BF582BE8410B0157350BAF4841E635F3154BC62917D3ADC19DA99F5DDA6CDD57238D3A357656EFDE8CB29005FBA152F4BCF6F5D23DB1767E0A5885EDE7034EAECDF007C03CFD54A8D85B8AE7529046F119D7BD176
	2CE960AB811AG9CG72B4BE46E385E36683CF05C6498796E5C466566226E967C0733411E7C2167B7A82DDA74B27315CD7060ADFC171EBF4F8265B56AC2E5B0DC0EB7B8CBF27AD9D089CBF71B361E32E0EA833F60F0FE8E70FA8EF0EE8475654EB15E55CE76816E0E32396FA31BD6B57D97ABDAB7DAEF6762E04E753DDBCBF7F8261FE5FF1974FDB4DA9427E27C1598C3092E083G19EE3215811C184E75363350481F066BAD1AB355F292ECAE7AC3931B4358916DA26770727470F6CC6E25A1AEF7E8BA57E36D60
	44F8B8BE22DDA48FC906BCEC2C64BC2CD6F9787184F9785518BC1099C636546BBEE0ECCB2053AE5FC9DFC809FF5AACC636D48CFDD64F603674E0004B53BA035B520F0A91B78C65E9B3385D7496E2EE0E7B14CE1F4BAB936ECF8A7721AF76C92EDE171763CAA82E5BA75103BB43B976C74FA9FC3C5FB827A06CB74EA9746E9FBDDF665B9FAD6740F2DBF63A87A700FCC9G8C37AB93F16B203CF986576D32AC44654F04B5B813633EC1F1380760FE40F06FE469F3D2D8DF2F374A19A37D59F8000D519DB6C6E4CEAB
	637F50BF420B344FA7280E7CB055E200BA820CE0945ACF65FD44A06F721E609195FE8A484F873215BA27E4DFCB3DEA334FD7959B5B2C6E8E34A69B6795E33293B3F9DC948A207F8EC1F98600D955418EB25A3D50CA0379D67228C551EDF0E911318DFE2588EDBF608BCA5DE2241D2DACAAD6E91A27EC17C58FCE0BF236216DE5C16A95C758699A5A1E512235FF26A81AAD33B9DF98524F799155DE160D7033FAD8E7B0C72C3F1EC1FDFFC7557EFED6B9DAFF1B2276D7BA246F6FCD42FDE90C056BFB3B2A6FA48975
	5DB04BE46BGF04E627A1E9B98ED6C61D951747D53E20C87C2EA799734BC2872F6AA837D941C7EFCC1F0E85BE7A18F17B2385F1C81E5B3409A000D33187FE4763CE4G796E11848F28623CDF6CA66B6F463687B3F95BD6A8DF8488814483AC3B1B470D877E0AB2AA3997EA7F8C7FE2856215148845B3590FE4F3594B6360FC8950962749FB895E7DF161723318E88BDDBB31F1ACA64AD571BBA85E2C4333D8698645E3B985E847BE4B7D62FE8B770B73D45C13A10E5B2A62C89547355E4375B65942F90F831C99E0
	79BD9A2FEB60FD63BDC63CD7D0DE365D93996755D2FC4E67A2633CAFC9BC4EAB87DA6C67B8CFDF8DF01EB6FD0E477B31997C3B07E371B0F771DC27751743E2B40C5E9B9B4FC8AE67C9213703DA88335B943AF7E0FBF3BC375B97E81F29D05E8610FFAF4FAD078E71B3E722E98E6908B9E9782DC62A234ADAF9AF1B070EFB3598FF8B1CB95918ED19FC4CA9C0DFF9AF3DE74E541FCF500656DAD98C416866B0BA7A905C5FB57A33E1F464F7C5D87B1CACBD9D6728DEE94FE47B56EBDC366D5FCAA26EAF56C57D8EA5
	6BBEA6D335722CDBF31024D87E1A46F454CE7D3E93E8B177F13D559DC2DC9C144DAA6E3E924425C0B9D545159C607ACD3F1F6326D37DB8206C3E1F5B61EF8238AEE3201CG309CE08340F6007C84DEA7C346BA9589AC57532064E8DA216CD46B4486AEAB7D68655B2074934DBFADD7460AC6CF385FA8BE7CBC9DABF21F4FD65668E831422ED16811F14232843A77DADA47E8132603D8E8938F701A226FC4F82FD25ED9FFBF890D56DFF6C4FF6401707E8A9F086C2F28EA347EF2A37AAB0F682FC1579F3311846A53
	CEG7E52835CEEDE116877GD00ED5F1EB2FF11BB32B383357394D25283867C35C664A9E643672BA75BDF5D0F62838B7A98EF3FA320ADBAE995DE7E23BE872BAC6219959439E72146198235D0B0EA71EE4BADA9B980F8D45018CE600FD8AEDB93CFFFB470E9D961567BBC76488EECE56788936DBBB6EF6ECCFCA31216FDF7DA05B13ACAA7EA90ADF2743337D0758787E138A34564F73F357832568FF4CD0DE853081E0ABC061EC132DEEB6BF0779CB6A489867F7074F5F81812EDBF7ACE2723D1BB931EF299E4F64
	FEBE792B0F006DEE31C44B1B364EE6F669D41E2B3FF3106219FCEF1721FCF1C0CB82D887309900BC84E7520738FC4BCA2A19FC1862ED16E5213B0FE60F39F01850555098F8315869F2B67AG3FA4760812A4BA5915AFF1BEC46FD3687ED28D6F479E62B15819C39877F579A49A0C33386FEF07F86CE606FAAB8136E37BB9ACDF485AADA99999AF2EAE616DAA6730F87184BC9DCABB36B66A8E0C36B652A2408DEDE3663035C1574A9C4D06989F268C5481841FAECF50C325E532FE0D6EFD3321FE629C4E7322F2BC
	73E837FD9DC21768D67A29AEAA8C23E9B71B0CBE7DE0943AF21F175C9905FE1451C35D48CF5B203FCF745673BE3BB3B51CF26774362C0FB556ABF337FD8E4F45A6DB0C6879F3B97D659BC6748A203FCC47A4B4E7467EED7953C89C4402F3719B66402BC4357B7F4861F6DF7668DC527A74964B2BD16CFEF8AE337714393AEFD8AF5426990D4D4679C18516225349A1226AFA29A53AED2750FDACFD9E3C4F637ABEA60D346D77A5EE23CD7318EDBB6059B70F65B0D81D972E71BAB173189D4743B3D129436C386B7A
	D8F62C77732B206D7AF91A9DEF06779D0A0FE77C178DA27F5BG1FBE1F737F43CB788D3A1A4D8A3FAAD666E43F87759DCEE2064D6CE14535C8FBC0996A660F579E0207711D794FFF37E96F5F573D5BEC637D9EE97CFB534B37F48C66FBCD2551F3A38BCB0DF3A3CBCB0D73F3DFA99D7D1B1F47CB23FF73333AD47F4D8F5BBBCE51BB1CD6505FE500D800F8004479FC5F38F060A0217B06411EF14A590B39997CAAD566633716C51775EC59585F32BA4AF652B5A64BBE8F764F719F15213EE41F1FA2996E6FE5B8C7
	122B37CFE6D5953CD806766696FBA81A595BFA8CD3959BFF6DD0B9BC3D62C039FA19548A166E450FD8AB0FB63CD5DDDE7FD6DD9573D5338774F7C947F65A235EA52D1E2F7C9EC2F9A600D7E3F149ED0778F74805A6132D43444F45ABF6E24CF1D3771D8E176C96CFD24F434E2D5FB9446FBAE3CD7C2C7C53E2FA078365F5G1BCD7CDC3E25C47DBD4282132DE9814FD19FAB65E7DFF9816F2327946BA6C2F995400685B73B2E39FFBD79861658BB7D9900B2679F87EEE64E151CE6CF141CA675C30CF7C96672EFD5
	65790505632A4D9CF7618647B94DBCF7706447FA1D665FD7EC65B1C024CE934C4A374266487B4105B5682FB7GFE9BC05943F02E85E883388CA083CC81C8GC886C881C885588A10758844E5G55GA79ED16333D751C0AF88D7C65E4483D2C9165045A6896C0E004DF1DE1DB6477B25B167788FEFD5B11B981038CDBA0007E90F70797A3A4467EB55A35CA6DE223F11588E6542C7016FC739DD9D9C66F6558A3861C7F99FD70639DDA5BF4A7B0827F5B7C3B9BD9166ABF13CF6F526DE332BABC7B4196F3EA91999
	DFDB691993ED20A1D17D5622B23A3FF2D40E6D2F7ECA6F2DC470B30EC13F12D76E2A2477B6BDBD81D1750B3D1579AA2EBD50698B38B09F4DEC2968A875D7405394152F97DFFFE394FB6814FCFED11215CFEB6817C04AFEB66D50D8E791F322D1CE643EB15B3D94D1E66D3ECB5B2DCA3C39F3737AC47E9B0C0F29CF5A1C98F936F3D27C364448335DAF0BD59F3690622B05EA7E29E4E2E75274520935BBBE6EF6CC2EE729DFCDD998A96F0FA9FE7D42C8F95D25DC5E0DC0EBDB44659D100D46CFDC247C6EE051C8BD
	30F6DBACC66D56A975B7C5ED77718D23F65B157A790BC736E3B63D37819E0A6DFF2D9A73C506368CD144BB30EE5F6F3AA2F64B8E21D7ED73F8CD74F5393AE66CF5393646B80EF83AE6E49C312146B80E78D10DBE0ED06EBDEA50B77602BF3E28565B2C62BED4F13F22FDA26E8317D36ED3713F25FCA13ECD4481B97EF7142F810A3B28602CF68D77A17F1E4B0E3E85C46E7329773FB9F67C162F4765F633F2BE2D6372022BD167E31F9DF9F479859D5F25AA6EC3D55F572509790F29E336B3769F2BE83B8A83BBB2
	02E30B397F18C577240E45116B69FE0A3F38B8F2BDD5DC636B498934550B79FA2A9C1E189FF8EB026DFC63EE47644A390E7CD7A7C54A3B0F620FA5C54A7B2036B7834D1C44657D6FEBC66367ACB1594A819A16B0BD34AD517441EC256CA83E2B678E3665A834D70F9692763BG1760F68D51CF954FC3C5EFAF3B5FDED910CB69EE17D7ECE13FC8A3BCDF7CF39A47DE027164A5BC67DBDF0B38A9D0CED8427996502DA8F3F8C5777EFC712DBFA366145C7AB1D2CBB519FE26FB17CAEF7D58D3EF6998CC27F751F85D
	8C7ADC266A79F919677BD72938CCAB628A3FE032D5FC01635CD6DE2FCE45C9B42E0F01728C9577748DDEAFC145FD1F46331B211C3E1463CC74DB5EE2A8D7AF657695FFCD1B7307F46F2FDF3B75735F72CF98E36FF5CD26833A77F94337FE6C9537F48C2653FCFA66B8867AECD3759C24B88714E5957744B03711A9AAEEC690F16B213CFDA94F15D98E535F7F62317B340F1E37D8DE2D64B07DEEA9994EB0493CEEDBED862FCB7899A585BF47D04EA873E55C1FE588D8982D850F1F4A5D4E4E60F87257719177039D
	40C38151672FDF32060F557387ED2CC95651465A95E52CFDCA7F9713A3F365F75C889F6B0EC6ED2C67EE0CB6D66698E3B9C70C75DF1231BE56D05D79217E302A0F95C3A375117836260F434361FCBF592451268DE3FE2D9F8ED72EEE415D6CF675FAB167250EE7D24F360949BCF71BBABC9E9D1AF577F3584F8668075F6FB39E9684B13E05G4FB584228AEE4C7D7393A367635BC14E47A6E84F64F838D0136335DA7CFF05041D0BF15EAA8FB6C3206CBE8847E2782F21CA33D7E4C7F97F4F7D5A7B5EE26D5DDB2A
	75FF5F504D762F68E4986B6B3F12BF0FFFF97AB0E0EFB9751E6354B9F52C19873431B686EF36AD7BFF2CCC2FAAC40D7F12E484A432DB652F1D8644297DC79B53FEBCB74DCA525B090D9D905AC9BAC6796DA4039E825AC99A9C90C278E3AE7C2817A77C7E9C78045CC02EA8569295FBAE8C0B213F95GC36CD3404F5E11F90A663B1EF9BA716F2D05D6E285167AC446C346G9E9CA8973321630F549EADCA0F88960A4D882BC313B706AD5242DA30BA92B90C49C7D717479E6C775805AE590E7F1F953BC3923C8137
	A063290A85F3047D33D8C946C7242087AF235E03831A44EB0A6DE48F451E9359F7FB189169A0C5E13813C12F53AD62B7F8B2595B01F3D16B963C3D35EC0837BBC8FED8753362A0321B5B218C48712CBAA551998297601749CE65368CC8B259773EDF797F9E886EF20550D8AE93CB8D831B4365219577A8379D654AF5C7E3B9CCCAEE18D9FF7321B7FF1272438B01B4E2EBA7A5BD00B9AFCAB458951D78A12772FFC0B8CD327D7885AF5EA401CD8568A3A3A08F3AE19E8AFB4E612F37823268EC66E1F4G58E41300
	E95E5AB6A150C7A79C66A15D8BF32489E8E93ECC454C23AA14C881658FDB5955D6850F6DFE6D15F7B6AECBA35653E4275BA7B8CF8B5DF052EEA759F29F085F67F3BBC37EBF1D209DE58D700F8453213B832A38A81C9DBD0E4A6455508353F2B7651E868B8AC9241656C467E9D7EBB8DB7A8489069225F699675CA7D52834CF3DA49F7A761922CBD6E28997CAE69324AED1B4837BE960DAFE7617CF4C79FE53164D5064B449203F1F00017335DA6F85BDDD22F4DB62E7DF5C9517BA9579726374D04BAE854D76889E
	17FB90DEDAC1C73EDE954F0E523478924E82A7E44BE8427883936026BCE2AAB8C91548FE7793F51FFD78050F28C4DAD3193C4CD489CDEBFBC51F2736392787CEBCEA87EF077C507D0BEA77D8479ED1E7EFE8663EF71E5A51850656CA52BCF5F574EFBD7DFB847F56D3CCBD45549F81EDEEA1237E0B2407C2A8732BA155B7604F17717A9537443F285813BA8FF7EE946C3E912609586211B60668B2B673D89DDFDED19504258A7CF3887F9C26552A685F6A0949572049F7952006B07FF716657F2B7E214F7FEF1B3C
	BB6A1D4E464758B9F85BE311797F8FE8EE28E1394956391C67EC97D463C05172DC67BD025B8D3B7955E62F93F3DEA787E5D13B373BD4037D87F89D0548E85DB417F4C55766FF81D0CB8788DACB121F6599GG28CCGGD0CB818294G94G88G88G84E52EB0DACB121F6599GG28CCGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG9F9AGGGG
**end of data**/
}
/**
 * Return the Hyphen1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getHyphen1() {
	if (ivjHyphen1 == null) {
		try {
			ivjHyphen1 = new javax.swing.JLabel();
			ivjHyphen1.setName("Hyphen1");
			ivjHyphen1.setText("-");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjHyphen1;
}
/**
 * Return the Hyphen11 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getHyphen11() {
	if (ivjHyphen11 == null) {
		try {
			ivjHyphen11 = new javax.swing.JLabel();
			ivjHyphen11.setName("Hyphen11");
			ivjHyphen11.setText("-");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjHyphen11;
}
/**
 * Return the NominalTimeoutTextField property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxNominalTimeout() {
	if (ivjJComboBoxNominalTimeout == null) {
		try {
			ivjJComboBoxNominalTimeout = new javax.swing.JComboBox();
			ivjJComboBoxNominalTimeout.setName("JComboBoxNominalTimeout");
			// user code begin {1}
			ivjJComboBoxNominalTimeout.addItem("7.5 minutes");
			ivjJComboBoxNominalTimeout.addItem("15 minutes");
			ivjJComboBoxNominalTimeout.addItem("30 minutes");
			ivjJComboBoxNominalTimeout.addItem("60 minutes");
			ivjJComboBoxNominalTimeout.addItem("2 hours");
			ivjJComboBoxNominalTimeout.addItem("4 hours");
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxNominalTimeout;
}
/**
 * Return the NominalTimeoutJComboBox property value.
 * @return javax.swing.JComboBox
 */

/**
 * Return the NominalTimeoutJLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getNominalTimeoutJLabel() {
	if (ivjNominalTimeoutJLabel == null) {
		try {
			ivjNominalTimeoutJLabel = new javax.swing.JLabel();
			ivjNominalTimeoutJLabel.setName("NominalTimeoutJLabel");
			ivjNominalTimeoutJLabel.setText("Nominal Timeout: ");
			ivjNominalTimeoutJLabel.setVerticalAlignment(javax.swing.SwingConstants.CENTER);
			ivjNominalTimeoutJLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjNominalTimeoutJLabel;
}
/**
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getOpAddressJLabel() {
	if (ivjOpAddressJLabel == null) {
		try {
			ivjOpAddressJLabel = new javax.swing.JLabel();
			ivjOpAddressJLabel.setName("OpAddressJLabel");
			ivjOpAddressJLabel.setText("Operational Address: ");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOpAddressJLabel;
}
/**
 * Return the JTextField1 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getOpAddressJTextField1() {
	if (ivjOpAddressJTextField1 == null) {
		try {
			ivjOpAddressJTextField1 = new javax.swing.JTextField();
			ivjOpAddressJTextField1.setName("OpAddressJTextField1");
			// user code begin {1}
			ivjOpAddressJTextField1.setDocument( new com.cannontech.common.gui.unchanging.DoubleRangeDocument(0, 99) );
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOpAddressJTextField1;
}
/**
 * Return the JTextField11 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getOpAddressJTextField2() {
	if (ivjOpAddressJTextField2 == null) {
		try {
			ivjOpAddressJTextField2 = new javax.swing.JTextField();
			ivjOpAddressJTextField2.setName("OpAddressJTextField2");
			// user code begin {1}
			ivjOpAddressJTextField2.setDocument( new com.cannontech.common.gui.unchanging.DoubleRangeDocument(0, 99) );
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOpAddressJTextField2;
}
/**
 * Return the JTextField12 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getOpAddressJTextField3() {
	if (ivjOpAddressJTextField3 == null) {
		try {
			ivjOpAddressJTextField3 = new javax.swing.JTextField();
			ivjOpAddressJTextField3.setName("OpAddressJTextField3");
			// user code begin {1}
			ivjOpAddressJTextField3.setDocument( new com.cannontech.common.gui.unchanging.DoubleRangeDocument(0, 99) );
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOpAddressJTextField3;
}
/**
 * Return the TimeoutPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getTimeoutPanel() {
	if (ivjTimeoutPanel == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder1;
			ivjLocalBorder1 = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder1.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder1.setTitle("Timing");
			ivjTimeoutPanel = new javax.swing.JPanel();
			ivjTimeoutPanel.setName("TimeoutPanel");
			ivjTimeoutPanel.setPreferredSize(new java.awt.Dimension(346, 196));
			ivjTimeoutPanel.setBorder(ivjLocalBorder1);
			ivjTimeoutPanel.setLayout(new java.awt.GridBagLayout());
			ivjTimeoutPanel.setMinimumSize(new java.awt.Dimension(346, 196));

			java.awt.GridBagConstraints constraintsJComboBoxNominalTimeout = new java.awt.GridBagConstraints();
			constraintsJComboBoxNominalTimeout.gridx = 2; constraintsJComboBoxNominalTimeout.gridy = 1;
			constraintsJComboBoxNominalTimeout.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBoxNominalTimeout.weightx = 1.0;
			constraintsJComboBoxNominalTimeout.ipadx = -26;
			constraintsJComboBoxNominalTimeout.insets = new java.awt.Insets(69, 2, 104, 89);
			getTimeoutPanel().add(getJComboBoxNominalTimeout(), constraintsJComboBoxNominalTimeout);

			java.awt.GridBagConstraints constraintsNominalTimeoutJLabel = new java.awt.GridBagConstraints();
			constraintsNominalTimeoutJLabel.gridx = 1; constraintsNominalTimeoutJLabel.gridy = 1;
			constraintsNominalTimeoutJLabel.ipadx = 6;
			constraintsNominalTimeoutJLabel.insets = new java.awt.Insets(75, 45, 107, 2);
			getTimeoutPanel().add(getNominalTimeoutJLabel(), constraintsNominalTimeoutJLabel);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTimeoutPanel;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param o java.lang.Object
 */
public Object getValue(Object o) {
	
	LMGroupGolay golay = null;
	
	if( o instanceof com.cannontech.database.data.multi.MultiDBPersistent )
	{
		golay = (LMGroupGolay)
				com.cannontech.database.data.multi.MultiDBPersistent.getFirstObjectOfType(
				LMGroupGolay.class,
				(com.cannontech.database.data.multi.MultiDBPersistent)o );
	}
	else if( o instanceof com.cannontech.database.data.multi.SmartMultiDBPersistent )
		golay = (LMGroupGolay)
				((com.cannontech.database.data.multi.SmartMultiDBPersistent)o).getOwnerDBPersistent();
	
	
	if( o instanceof LMGroupGolay || golay != null )
	{
		if( golay == null )
			golay = (LMGroupGolay) o;
		
		//some annoying checks to verify that the user hasn't messed up the six digit address
		StringBuffer opAddress = new StringBuffer();
		if(getOpAddressJTextField1().getText().length() < 2)
			opAddress.append( (getOpAddressJTextField1().getText().length()==0 ? "00" : "0") );
		opAddress.append(getOpAddressJTextField1().getText());

		if(getOpAddressJTextField2().getText().length() < 2)
			opAddress.append( (getOpAddressJTextField2().getText().length()==0 ? "00" : "0") );
		opAddress.append(getOpAddressJTextField2().getText());

		if(getOpAddressJTextField3().getText().length() < 2)
			opAddress.append( (getOpAddressJTextField3().getText().length()==0 ? "00" : "0") );
		opAddress.append(getOpAddressJTextField3().getText());

		golay.getLMGroupSASimple().setOperationalAddress(opAddress.toString());			
		golay.getLMGroupSASimple().setNominalTimeout(com.cannontech.common.util.CtiUtilities.getIntervalSecondsValueFromDecimal((String)getJComboBoxNominalTimeout().getSelectedItem()));
						
	}
	return o;
}

/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	// System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	// exception.printStackTrace(System.out);
}
/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getOpAddressJTextField1().addCaretListener(ivjEventHandler);
	getOpAddressJTextField2().addCaretListener(ivjEventHandler);
	getOpAddressJTextField3().addCaretListener(ivjEventHandler);
	getJComboBoxNominalTimeout().addActionListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("GolayEditorPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(350, 360);

		java.awt.GridBagConstraints constraintsAddressPanel = new java.awt.GridBagConstraints();
		constraintsAddressPanel.gridx = 1; constraintsAddressPanel.gridy = 1;
		constraintsAddressPanel.fill = java.awt.GridBagConstraints.NONE;
		constraintsAddressPanel.weightx = 1.0;
		constraintsAddressPanel.weighty = 1.0;
		constraintsAddressPanel.insets = new java.awt.Insets(1, 2, 1, 2);
		add(getAddressPanel(), constraintsAddressPanel);

		java.awt.GridBagConstraints constraintsTimeoutPanel = new java.awt.GridBagConstraints();
		constraintsTimeoutPanel.gridx = 1; constraintsTimeoutPanel.gridy = 2;
		constraintsTimeoutPanel.fill = java.awt.GridBagConstraints.NONE;
		constraintsTimeoutPanel.weightx = 1.0;
		constraintsTimeoutPanel.weighty = 1.0;
		constraintsTimeoutPanel.ipadx = -1;
		constraintsTimeoutPanel.insets = new java.awt.Insets(1, 2, 1, 3);
		add(getTimeoutPanel(), constraintsTimeoutPanel);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}
public boolean isInputValid() 
{
	return true;
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		GolayEditorPanel aGolayEditorPanel;
		aGolayEditorPanel = new GolayEditorPanel();
		frame.setContentPane(aGolayEditorPanel);
		frame.setSize(aGolayEditorPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.show();
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of com.cannontech.common.gui.util.DataInputPanel");
		exception.printStackTrace(System.out);
	}
}
/**
 * This method was created in VisualAge.
 * @param o java.lang.Object
 */
public void setValue(Object o) {
	
	if(o instanceof LMGroupGolay)
	{
		LMGroupGolay golay = (LMGroupGolay) o;
		
		StringBuffer address = new StringBuffer(golay.getLMGroupSASimple().getOperationalAddress());
		if(address.length() < 6)
		{
			address.reverse();
			for(int j = 6 - address.length(); j > 0; j--)
			{
				address.append("0");
			}
			address.reverse();
		}
		getOpAddressJTextField1().setText(address.substring(0,2));
		getOpAddressJTextField2().setText(address.substring(2,4));
		getOpAddressJTextField3().setText(address.substring(4,6));
		
		com.cannontech.common.util.CtiUtilities.setIntervalComboBoxSelectedItem( 
			getJComboBoxNominalTimeout(), golay.getLMGroupSASimple().getNominalTimeout().intValue() );
	
	
	}

}
}
