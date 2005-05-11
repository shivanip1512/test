package com.cannontech.dbeditor.wizard.device.lmprogram;

/**
 * This type was created in VisualAge.
 */

import com.cannontech.common.gui.unchanging.DoubleRangeDocument;
import com.cannontech.common.gui.util.TextFieldDocument;
import com.cannontech.database.data.device.lm.LMProgramBase;
import com.cannontech.common.editor.PropertyPanelEvent;
import com.cannontech.database.data.lite.LiteComparators;
import com.cannontech.database.data.device.lm.LMProgramDirect;
import java.util.Collections;
import com.cannontech.database.data.pao.DeviceTypes;

public class LMProgramBasePanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener, javax.swing.event.CaretListener {
	private javax.swing.JLabel ivjJLabelName = null;
	//This is for the timed operational state
	private boolean isAWizardOp = false;
	//Control Methods
	public static final String TIME_REFRESH_CONTROL = "TimeRefresh";
	public static final String SMART_CYCLE_CONTROL = "SmartCycle";
	public static final String MASTER_CYCLE_CONTROL = "MasterCycle";
	public static final String ROTATION_CONTROL = "Rotation";
	public static final String LATCHING_CONTROL = "Latching";
	// Stop Types
	public static final String RESTORE_STOP = "Restore";
	public static final String TIME_IN_STOP = "Time-In";
	private javax.swing.JComboBox ivjJComboBoxOperationalState = null;
	private javax.swing.JLabel ivjJLabelOperationalState = null;
	private javax.swing.JTextField ivjJTextFieldName = null;
	private javax.swing.JLabel ivjJLabelActualProgType = null;
	private javax.swing.JLabel ivjJLabelProgramType = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JComboBox ivjJComboBoxConstraint = null;
	private javax.swing.JLabel ivjJLabelConstraint = null;
	private javax.swing.JButton ivjActionPasser = null;
	private javax.swing.JPanel ivjJPanelTriggerThreshold = null;
	private javax.swing.JLabel ivjJLabelTriggerOffset = null;
	private javax.swing.JTextField ivjJTextFieldTriggerOffset = null;

	private javax.swing.JLabel jLabel = null;
	private javax.swing.JTextField jTextFieldOffset = null;
	private javax.swing.JPanel jPanel = null;
class IvjEventHandler implements java.awt.event.ActionListener, javax.swing.event.CaretListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == LMProgramBasePanel.this.getJComboBoxOperationalState()) 
				connEtoC1(e);
			if (e.getSource() == LMProgramBasePanel.this.getJComboBoxConstraint()) 
				connEtoC3(e);
			
		};
		
		public void caretUpdate(javax.swing.event.CaretEvent e) {
			if (e.getSource() == LMProgramBasePanel.this.getJTextFieldName()) 
				connEtoC2(e);
			if (e.getSource() == LMProgramBasePanel.this.getJTextFieldTriggerOffset()) 
				connEtoC4(e);
			if (e.getSource() == LMProgramBasePanel.this.getJTextFieldOffset()) 
				connEtoC4(e);
		};
	};
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public LMProgramBasePanel() {
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
	if (e.getSource() == getJComboBoxOperationalState()) 
		connEtoC1(e);
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the CaretListener interface.
 * @param e javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void caretUpdate(javax.swing.event.CaretEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getJTextFieldName()) 
		connEtoC2(e);
	// user code begin {2}
	// user code end
}
/**
 * connEtoC1:  (JComboBoxOperationalState.action.actionPerformed(java.awt.event.ActionEvent) --> LMProgramBasePanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		if(!isAWizardOp)
		{
			getActionPasser().doClick();
			
			/*java.awt.Frame owner = com.cannontech.common.util.CtiUtilities.getParentFrame(this);
   
			//This makes sure that the user applies the Timed state before going to the control window tab
			StringBuffer message = new StringBuffer("You have selected a Timed operational state.  Please click \n" + 
													 "the Apply button before specifying your control times or \n" + 
													 "the control window panel will not reflect the current operational state");
			int optional = 
						 javax.swing.JOptionPane.showConfirmDialog(
								 this, 
								 message,
							  "Changes should be applied.",
							  JOptionPane.OK_OPTION,
							  JOptionPane.WARNING_MESSAGE);*/
							  
				/*fireInputDataPanelEvent( new PropertyPanelEvent(
										this, 
										PropertyPanelEvent.EVENT_FORCE_APPLY));*/
		}
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
 * connEtoC2:  (JTextFieldName.caret.caretUpdate(javax.swing.event.CaretEvent) --> LMProgramBasePanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(javax.swing.event.CaretEvent arg1) {
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
 * connEtoC3:  (JComboBoxConstraint.action.actionPerformed(java.awt.event.ActionEvent) --> LMProgramBasePanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
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
 * connEtoC3:  (JCheckBoxSeasonChooser.JCheckBoxSeasonChooser.JCheckBoxFallAction_actionPerformed(java.util.EventObject) --> LMProgramBasePanel.fireInputUpdate()V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.util.EventObject arg1) {
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
 * connEtoC4:  (JTextField1.action.actionPerformed(java.awt.event.ActionEvent) --> LMProgramBasePanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(javax.swing.event.CaretEvent arg1) {
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
 * connEtoC4:  (JCheckBoxSeasonChooser.JCheckBoxSeasonChooser.JCheckBoxSpringAction_actionPerformed(java.util.EventObject) --> LMProgramBasePanel.fireInputUpdate()V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.util.EventObject arg1) {
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
 * connEtoC5:  (JCheckBoxSeasonChooser.JCheckBoxSeasonChooser.JCheckBoxSummerAction_actionPerformed(java.util.EventObject) --> LMProgramBasePanel.fireInputUpdate()V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.util.EventObject arg1) {
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
 * connEtoC6:  (JCheckBoxSeasonChooser.JCheckBoxSeasonChooser.JCheckBoxWinterAction_actionPerformed(java.util.EventObject) --> LMProgramBasePanel.fireInputUpdate()V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(java.util.EventObject arg1) {
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
 * connEtoC8:  (JComboBoxHoliday.action.actionPerformed(java.awt.event.ActionEvent) --> LMProgramBasePanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC8(java.awt.event.ActionEvent arg1) {
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
 * Return the ActionPasser property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JButton getActionPasser() {
	if (ivjActionPasser == null) {
		try {
			ivjActionPasser = new javax.swing.JButton();
			ivjActionPasser.setName("ActionPasser");
			ivjActionPasser.setText("");
			ivjActionPasser.setVisible(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjActionPasser;
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G85FE1AB2GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBC8DF8D54515F10B5D6C0A15D65AE2975BACE29B95AC343825165D2615EE355095ABA8D454E0A322C40D0092B54A646507C048FF402890227293B46687E21B0DB189A4440081A3049079115C645D64BDF2736E733D1B6485036CB9B3F7665E77F2DF82FCE53F667B8E774E19B9BFB3F3664C19F367C1F23706A5455BF28909CFA27A5F5F628919FC181047EFAB3A1E63E26E2DCEA6263F37G32483755
	C4AC4C2BCE1E3525A6B913546725C0B1974A6139B5492F41FB8E49EA8B2DC724351CF628BBF8474D0F0FAC27BE014BD9CB4B5DE7B740B39DA08FF0D5G21G19644259BC9D5F8E65F47D9DA15EC6C853FD58E6470EBC93FE2E5E2EG6A4A81E2E81B9F1C5D2C570F07B23619824F087BD85B0570DCC7353B3A32C2EFF76A5B61E4559F3EE5CBC3D9A67E9CAA7E44E8CF694F823249068311C096ECE904E7D5F3CFF7F5275ADB5CA939DC2ACB13BB9D45768E596E54D4CF7100731C6431975B65FEE727DC2C743ABD
	EA37C76A2D2CECBA544CDE77C9DE39D9F2494A5CBF217ECDBBD2A8EF509F4AED77A3EE768E9C33CCF8AF84283D1F77613E34AA5E571EF092F953D5C17BB071FE26FB447DC69FCA3FD813641E0F7C6F22F302F4844A95GCC17549E2E4B0C79DC171BEC58B69A4A098B2A13B7AF6078338908AF07F29300FD815771231DDC47362FFEC1E2BB265BB20268B8340069B6F9012163661B0EA63CCE6D666A13B87711D01778C0F5F29EC09540A900A100B7293DDE1370AEBC77F6E8F5EAEF2F6A5A6BF66FD36C1EC32ADD
	D6603D3292EAF45C9139D3755889E1B679590EA28BFDB80CFF405F9E91D85F0E93EEC3D30E5CCEB46DA754AE827BB6E341B8DB384EE35683C65F629E40F12B58096336985E11AE9500619BA83E76C1834F666A8F5496305C8EF5339F64635C164EF5E9CA1CC4B24BFEEEDBEB214B045F065B8A97B29DEA979A3A30B1BEF39A47D881FCA8C09840CC0005G318B7998AFC858B35A989F00B560F1C94A513E8E3764517675B9953B6C51477A1E33A30D3595B03A87AF190E0DCB789E739CAD7E794FAC47E5F17877ED
	390B58F894ADB24605494E6C89A63BE951F05BC897F3789ADD936D0B589CA6887CDB946FB661595C5EC2579616C320AE8A0035FF0436EF7CB3EB3FCE70D9CE71F6931E71F1D3BC16C320AE8A004D6FABDF20EFECFC8856A7409040F80099G73G229E92F3FC2264A257D133E9FE996D37CEEE02276C53BC528117D713DC1DF22B64F1CA9D0A6CA5FA1B6F1F44F16D70231BC857F7A74878D8F6499EC9F32AAE60AE754AE0CCDE56666613D95846A3535A96E72F8C82FA5D04731DFAB2876DD772EA475CF6C9130D
	962C7E0E13B8A67DD2ABF0848670DE3E98474BC1FDEEA23C97AC66E375EEBB62FCD09E37180FCB6929ECF8FE8246ADAB1515754ECED4C3728CF29B2D5E891BA4198C6D0B9E2ECE1E8C306D116A64090F70B57ACC9B37457A342924E841F4CBDB243677A8332B0AC783ED31094AE887FC88C098C084C06423FCFF1D3723CD6701322DEC9375B317392EF41F88C2633F961F94B25685E91790D0CE52E971BDF514F54B46FF0204B6CCA2E10F81280A64F350F32A0059A66E1B5C9ABF7500A149F6E3C6D8DB47E95CEB
	64FE5925554BEE59E507972FEE8B6A69F5061DF62942067AA89E7DD69D9812D7603FBA1D45704043EC753C7E9B34DE52B40F3323CF130F4A1A66F4F59B7457D0DD3CFEAE512B7775879D585796682382B0A47A1A7BC50E495E1AD435274F5D02ABC9375B1DF4FF9E876D43A3797CEF217156DCD15EC94BC67DCFEDFC6EAE85105F457B6A3BAFCBC6204D0CACA3AAA113303513E032AFD6779C2D0B861DF317006DG5817707E77A734D950DD2A2EA36BF69D15E1669FEC1DDC8EB05BD8FBA638ED271F31366D5CB3
	56367D461911ED776DB3D6364B6AF61D41BE2A83AE53DAA8BE03FE32D3F539E43AA08589E1F173B3AD588F274B299DF75A3BE56D28731C8CF15292FA8E203EF8AC3C07AD61FEF3609D6EB7371D3B05C41C99EB99E7CD3879B6DB6C92E6279B9EB37CA65329249D57182C483DB0D0F5EA1FCBE3788F281F57715E840AEB4CBB07340587E17F6BD077293ECF5D7A0E84C10D869BCB11C47BD54C9E4D3227CB75744A764233D23FD4AC8DE845F4DE0A77526A7D783E2B607D772B8B06877DC5DDCE0FFC40656E53372C
	42DD2D3BCA196D64101C9A05695182F3596014957BE1588F0BBA4189E94574DF1D88657A0A3D83B0EB3A68BA2C3DE449E5CCEE76BA4D3F7FF5AAEC659E4969522EEC4FF7B379EADBE03F4B2E2C5CBDCC6EB77679826436F81C5D5D3267532EAE2FFC057B3E076D85FF6CC4BBAD827B2D85E8FB0C6F85535B311E6F857A06D727AA8ADBE2D0B76BB1DCBFD75178002F8977E36C0C1DAE6A270ED23FE8147A2442116B4B025633FE7CD0C25F6C153F6C13A1B4444D6C93F91047F7D2F6C0DD2B247499F163D489E387
	45348FB2FCD807755A7EF1876EFDBD027F2D9D18206857F9B2DFB40D72F4FB1C3D90B660462E679A2AFB4C3906443FD4A7E3DE229E1EEDG4CDEEE9EC2FD07CFAFA1A9D14863BBDBF8AE428DB421G61BA1FE5FFE1E3425A356673F6ADD0D6G46C6D935FBFDABEFF7B714A3814863D66D9E966DEA213E8D409750EE4663586EFBA257D0A2F0D3A96EA158347EA5111F218EE51AF3A5F66FE4CB7F0B907B386EFF17861EADEDB272AD82FC55D2EE535BBA4D714DDE2FD76DF4D2B72B4F6B7D1DE65F4CF08BBB5743
	93560F974E19F65947ED6311CE5495E3FF9D2D4F6FED6FFDC4061FE576536C8C8F9F6B8668EAEDD16B6078518866D5A7362ED627BCC078B81CEFB067DBE6E55CE0CB8DB28E4DFA7F5D26F1081CF4B40169F4C3A51E212D72C61DD449626EBEE7F11F6644C30624C954D1B043E2F919F09A77A12FE9CBF97E456742B5308C4A64096A64BCGF6967C55168DFA5BD240B9G58BA5DF4483CCE431EE0B1645DCFF05BFEBBFFB7BCAF5D6371F11F3DAB1B4DD3001F63E3B9A927D617BD7776C9246A42B94B3CD0E84698
	5B62A75898A6AEB346327C4EFD890DG2C0F9772F99F1B21CD68B25E478FCE187B98310C75F159B25E472579E87717381D6AE76CFDDB394CB860D77EA41759FF41EADC15A7394CDB36DE6E38B2394985DCEE88701CA96426B8827B9A83F539CFF139AB37DFCEDF75BC57EE737A0F081C90F47D97BE4566286A2940753F348BED368D704AD3FC7DFFEE4FB22D7FA33062A4D7B7F8F591634A28BB78EF3D1CAA23DC6A3F89BFA73EC37B9D82BCA7824CG188B90ADE4FCC79EB5DF355FB7C23EEA6A305C4F066546D9
	28E07970F3914B4D9428B49F3A1C65E6B68A7C9E0AF71870ECDE3F266B974B93212EB11A1FD7FE33A5D07E2AE8FF1919CF7B17995D3D43680A834A6581F4EC5DF6ABEFF25FE4B20D4ACA918B6ABE6850A13EF7B4BD4DF3A603CE5CF746C0B98CE0A640FC00D8005CE72A13AB1E61F332C43672497EF3924CA19B67EE564F2B73AFEFFC2E9FC697B8BEED4FD80D4F76424D5663A3E2C6B6BE4A896E235D4F7039EC5CFAF93A368E238B54B5BCC0D72643548BA9BA0F882143F736DF1E8EE1436882F5088D500145D1
	57471B63284CE719BFAC07E763337CBBC30D036B597EAC3725DF1EC55C84EC7FAC371B6AAE7C3E417DC5338A679142FD4572AE0C6DD408571DAE0E0B2138CE55C38F278C771EA37839F937437A5CDC6130BEB7D7BBC64E89B5B8021D2BD97EE6B65D47E762B8GB05C938A62E2201C924367EC07DA884FA2A8578334830C811882B097A09AA065396A64C2005A67B84D7FD29A7BF3AC1E9C739C0BBFA743F3B6C054F3ACFECCDC417689C6B3469596B02748BF503FF160FA18F128C73076289F6F8347C50F40BF90
	303FC67FFEBACC573491E82DF2B0D9A33447381467EFB8AE63A275C4403890FD79964A671ECD9954CE3CDE59CB9D9BC6F5FA5D935AC6E211AB4103BE61F90058AD6B74B67BB5352E0C70B5B97370C85FC4362D98BE36FA2E5299FC8D7C2B53FA8D0CF36278FA55BECF279CE8675FF76609BA43D39F5064DE6C06FEC623741A646916072D131FD0FAD6970CFE1AB358DAE2E3B4AB5FBCC63B4598FDF982472842E2AC280F3240B3FE97361A796D917CFEF6010FF93B852D863850GBC63F74FF6B33FF2427713153B
	306F967B09691B701C78945D26A62E60717EE75D98AF4C07F2B4C062CA3EAE6A9DDC3F4DAB076BD78A387AGBC9B5F7FBA0B7108CF14FFCB4B03C27F07FA5017AA52205A87CEF74ACA54E1055F99398DF0A1G63F57E2CFF5387D06FAEB89EC3370E3A65CE923A8AE9DFD5B86DAAE81F8238C200C6G47AA9E339E9940335E41A629C3D65802E078C737A11DFFD60C57AD5F3AC95084266DF81B03253847844D6E09BE4B4DD90217FE46EF99F49B755764670AFA08557BA45A8A1B307AEB73B2C53D11C2E3F5514D
	B9E69D0CEA1D77776AF1FE59892099BD07C74FD1FDC87BC72DC34FCE35B8BC325721AAF61E03712A6633403CD5EC4FDD350A1F956A0ED95527BC4F6BE3FA2D6AB70B7A58FA2B7AD2D1DF16EED5DFAF6A2FD54C75ED4F337A1067799EF86FE13E870E9738D09ACFC5C1B9CE601EBDA1764A97B86E9E9A7334C1D99338FEA7EF97AAF02B339137904AB1AF08EFE771FC6DA42E06B3B4C051EAE32D5442FB4BEA7F3543E27A5F513568D84DE27AA4011FC771C3A6BC0B750FF773B35CB828DB351A6BF41246C1ED3120
	FBAC477D4F1050DD60EE7C0A6A8E6598G364E4A5A7D62D2BCC5D6D6FE6CF15A77C95DCDF455B2DF12C7638B54A771C56ECB9A9454278A4AADG4A0BBC6F6E5B41739DD3FEBC09B4057C4C72FEC9E44B8D36B1AF32F189FF51F43F64262389CC66BF75F219D1D03F79A544CD7333B1346D2C8496D3327A477C6AF19D62FEE954673A4C75286FA21B393E412F9E4768FD1D1E75ED4EF65E3738B95308F2C7D8503BB37617D81FC275E766CCF77CC4F71D1E4BD9599544FF53D54F7AFF2BAA627017F84CFB6E984745
	8A5CCE9747153E4CF1A57BB94EA1F00E74943D9FE1AF73794FB87A3A359E7B7AB40D6607996DEC0573DB883475F108DBFA385E225F6631DDC738BF077E834DB6000DBA2E825E6790631BDA8E79518974791A2AAAADCEB7FAE9B2B68E6F063D0EDBBEE1B213CF0EA4F3ADB13E1DA14FF981B223603D44E61699F981674F6D113BE40FC73633CFCA2470951475A27589B1C09397477BFC2DF3A47929BAA468203C62AFBF649536268D7969B9589EB6B9E7EFDFAF154EE4C7EF245F3360FD42ABA246277B79ECA8C78A
	5CF6FA1EDE856514D7B9AE3C1566CC21DCABF0F30EF3FFE097387ECD081B8165392FF2DFB6279BF111D0CEDC43F18FD09C660337895C696E4B79D6BB9220CC6AC3050C7E3F3B8C7E2D185BE48E71B8024F7FB5542F5603BE4DEB787CBFBEB4527CF33E9C06563079FFC71F73F1EB8267FF46D75673BF177A6A0950BEDC0C479665D26FB2317D64CE6A27671BE4B37CDD949FE94233FD26C961777A22316F24C6BFDB47F950E74D0072BC00E5GA43EA639G208A0045FF9F771412D1F2B4AD2A3B898E910A69684B
	7A774F3D17F7D76BB7EB39CF1EF1672DE466B75B036757C1CFFC3A75E778FF386371497AF71D067D8B033AD9G11G31G9BECB5495BEC3CFF57513C9EE61A6CCEED2F26C91D8E7A1501F78E937F869A83D77D5C291A7BD6E1E3721B74E73B7E8C74E5D96442B1B31D26378B551F93035295394CF45374F63375674220F4657B4DF451FABB12401E39894168FA534DF405FA3BAA7D598CCFFBG2DFE2F4F5226FDFA5B31708C8B205BCA58B9BB05703F181A61B8226F9DDF7593E1A7CBB6F0BB59FC5BCD247E41F6
	9BDF170172E768B29393EBC4DC52A62DC9E2FC6EBDE94EEBE77EA054969F04CF15CE5FE862E325BF5375DD1F217774C1BA6C34ED0F93E9DC72C001C87457BB413C70B2DA616E527222025DFBF84EBDEC402CDB457A9F864D398F69BA0D531FC3CF3A63036B7666D706EE55C306EEEE65E2F4E3EDDF0D8F6EEBDF33214C3BC11779G2CFD55A1BCBBF6289EFA68E7E747230778790FA455A4678134GB8GE2443712DF378EBFF3BE50BA6C4C89B46313D85F19EFFF6C78E83EBDCD37A514B385E8A3GB66A657970
	0E633CD70C4D351B2CFD7B4DF45F0D047651C93C2F3F78906F4E8739C120737B45EEFA76239122FFBDBB9F151C8FBCBF611864A557A433F96BBFEF4C5BDDC37941676DEF3BE15ACAF7D92FB5466B062F8DFB78538503EF76790B614B56FE84DE5790FE40F8BF227C3D777D61FE27670A4AE0FBC512B7F83EEC0357BADF166D354E9967F9C73EEBD5608DFE57EA2757FC570A6DF513885ACA9158C6A300829082B0BE196FF3F7F85FA6F41F335863765B3BE51AF810B0114E5ACF50027775C75A68F7FB43B54C59F4
	28F06868C57E9CFF3B06732529EE0AE4381FEBB8C630G9C9AEB2A63FF29211DAAF297C53398773F6999EA9A74AB8200619E72A1EEBE1423842EF6104747B182773DE31C36F6AD47EDC9C5DC8B146D8277CF291C56ADF0F574CC968165F982F70BC4F391D0DEA5F0699D1C1624F05CA77BB9ED3340F550585F8EE5CD60FE9F4FE9C384EE8CBD634D03F2244015D3DDD6C1B9F19D475D6062341982F7BD0D735B216C9338C8FA6E9B8B650982373F1753068B5C5F28AE31D05E301E470B44067E34G4AD5G6DGBE
	0031G1381E6824C850884088548CC05B88B208220896094009BE0CCAA374908FEF09D24D5BABB7CB686A0752405A9A346ED3D8BDD13EEA349835B0371G4BBE88BE6B530E33F22756A2F58B7A66C47ACD897418A1F4F9E1703D91F4E97628EE59238DB2AC3D0D247B4A5FA4993E720DC3A36C1DFB4AE16F7CEB853FB77055A1FE47FFBE6830E1C30DFEFE7F4F7598EFDF541D10962726487B68EE4A4E74F79D625F9A6B019F4B69BCFF0C661E20BC9EE08AGEB9B574AF3B133C55BF70FE35BB8A8672655A49781
	DCDCEC467710A87A3BAF763EB6459817726E0B999726D757F9A444FD39BE0D4F4D2DA438AF1ACEC677C5F7D2DDE570A11813E538DF937A1D10ED37FA3B59A4D3608EF81BD52F93332E2C8F8F535F49F541DC30BDFFA575C36120630CB43E672F70E19B8D145163BF9A4231D8647860E83108792C394D1467207932B476DB3C17572CD943647F1A7A32984047897965297E72DFD8EF483FA1F5B479665CCE207C22749AFAE68571EB182C93AE7F71F0D37FD3816D6A5339BE91123FBE27B7987AEC6F98CD1F35A368
	23018C1E17E172B56AE3C3GBFD6489F0C77179F12E648FFA4FE34DC57C872E7010C12F8A3F64CA17F166EBF9E53295F3E9B5A4D977AFC6272576776F4C31FE92E5146A31598F930C0FD92B358F82489FDEE6D26312A648B08D5FFCB7704CCE83FB9036B35DA71576B3E8CC32FD33D97E337EB743FC03D9AC1462FG3FDC584D61F86CC33F536B44FB1BCCA7B91E7B22168C3E66F3ED41577C1B3651577CB61BF57C712EEDF87CD1E2330EBFBE3019638F062B24672FEE581B5AC43B9A017BDC606AA9CF449DF75A
	B507409F24FAA13ED1C6019C5FCC759A2038B69DF74C047BDC471D30215F02AEBBD4F117610C0D5A1ED3D1D8391336F13A400D0971F0D09D1DEE4924F72F4099FA9FCB44F995F1A95B6A743AD30939045D57F7CA0A739C7DD6F6949A3A3AD98CFE55215172BD0E8C2B1C41F52352658714755F17D03AB19943F398D6B0815A31BC1933959D51140C1A007C190762E71A70EC2FFE79984F1F4D063A424C9ABDFF97FF7C7272F7BF247B6C584CC079BF217889190172CB0FF3791321AEA50B4B6FB9EFF566D13258F8
	04E4D90F4BF8400787544D02723CG9C7385B762146BBE65DE9F1EA17C767186F4F8EF5B419E546E370B8ACA0B8A4A4AF7F15AB9688674771DF9861F2C54CB63F366D3034F3D1F99FCEE14AE0D4F0AA3861F0FDA8DBE2D71EFD973598BCEC581B6E5BA9B1D367D384167771F9BFC8ADD17264FD3A78CBEE9278CBE1FDF98A1DEAA5BD346F2B00C2E7434C1B7C4FF185FA5A9DE74E16CDEC9C4CE9ADD330A53A55731DFD592FE6F68D19AAFC6413C47E571387FBC0DABB233A1F64F66F6265231E26FBE53FBFC6B15
	BF573F717FA0E372F123CFD3CD6FFFBDFE65E5FFFAC5E530B93D16465425B01F3559FC1ED7D0DC93146D8257A8736F4EBE01DBC64F51E321BCD160DA0E707362B401FB70BD7EAD1A6470386205C33887EAF8CC41F5C0F82C16CE3F6B67C23B8251F67C31A45E1670B5F1748BC238EDCFBE6F9F23741FB16A56FDED9D6B3C75B54AE9869927G986ECB5A47D0A80F9732578C7A47EE9B4F5173BA544F85E0F1D04F1111622004G60F1904FC74765303594AF460094210C14DCB8EB6572FC5E43125127AF0FDD04CF
	5AED78A452E950FEA40F64BFE88E37147B0FF76C865D430397CF373E5B20EBBCF2E9FE6FFD8C077477E62741671B360B114F7ECF12F4A5850CEB4A53A4C9A399F87127C06DAA50AF67F8BDA4C3F0B8C04320487EBD1589FF39A941C6928F10E496969EA009985B9DA0C9B474BBC092A0AC74614F48F9B80A663B5E7BA5791A16566B0B96EB5F81995943355C7B2DC58FCEB9D31FEBA3B6906310995F24818C81A9E7D4FE2E60480F8F7453BA45A67A3541926E3F0D0282AFAF58GBE23BA8CEF03A5B05478B6C1A1
	697CF10924783751C8920EDC25C632CDF71F8A6865A7C0266943D90027E6A82FAB6CF37538548157AE0D6472BAA3BDD3D9GAB9C06A54D4F88FE776F9FAC1D7BC82BB701A49FA02F639ABBAAFBE878A85B717A237E9B7C86324E0D77F8F175423CF869A3492B8DAAF293496DBA023F8573EA32FDAF8FCC3D9A59580412CAB099D252A8F99D9A9EGBEECA209AE7C5F82C8B62BD6A4D7F7890BD2BF24D3903F379DCD0E2EDB54B7076A0BDC0A85171CDFE5ECFF6BE0544C84E2EBA06B95D532B7C81D1A6AB9C056E9
	7C16154FDDD3CB99258C70EBF989406ECD51FD6CECB1FEG90CA7778EE9C1B36AC2D81E65067A1A534A53A34E25142DE6710BCA0CA769C50F0C6D44FB6D1F7D53E36630F9F3FF64AC66275CE62ECEE7239B5125CA575BA15C1F82927CB0E4E739BB0E5B0B5AEAF7E2A84B4AB88500CD7898145575FD37A5DDBDEBAC18598249A594A0692C8CB3AE53537E4EFD797049302810CAB305FA258E31B6200B6593EEFEFBC38E0CD8758C1BDC968ADAD257F1651FFF7633FE594D3C6B1E53BE1F0E34808FF0175BE5EE7EA
	BC4285607497B7408CEAFFFE7A1EC98FD545C4C36F9AC8927D7588CCC226516AF0DFEF076C7966146FEEC99D9FF98D4E119BCD4507FC73FDE2C1256B8BAAD9AC415CE1D605583C616601684DE212303415B6FD0B7E3B6D123B0CD4BBA8ED215034585014FF5D423F757FF81AFE6073F32D28E9CED0CD6303E81A682FA9B702E4F6EAE69211664AF4214A3F8B7C3F6879879C6C86C3D3FE891E6EBCFE3B4EB7C49B3FBF0D3C21FF1EAC90F7CC8B7897AB0DE472CA767132324054FB56D7FAFC3D38894452C778CF4D
	5FF79CEA7572E7BE3FDD53E1D17A071A374F0DF9433D3F19307D5FFF6FF730D53E6B02792EC06C5597025E95A0797A7D0FFC639E837BB6926A26B1B36015FC1E0BDB282268E05FD80E76CA0A22C9CA4FDE179D3F376C9B54E463BB6635EE646F65ED74CAD6B75E0DFA1FB5517C9FD0CB87886E2C6889549AGG4CCEGGD0CB818294G94G88G88G85FE1AB26E2C6889549AGG4CCEGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG
	8E9BGGGG
**end of data**/
}

public boolean getIsAWizardOp()
{
	return isAWizardOp;
}
/**
 * Return the JComboBox1 property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxConstraint() {
	if (ivjJComboBoxConstraint == null) {
		try {
			ivjJComboBoxConstraint = new javax.swing.JComboBox();
			ivjJComboBoxConstraint.setName("JComboBoxConstraint");
			ivjJComboBoxConstraint.setPreferredSize(new java.awt.Dimension(204, 23));
			ivjJComboBoxConstraint.setMinimumSize(new java.awt.Dimension(204, 23));
			// user code begin {1}
			com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
			java.util.List constraints = cache.getAllLMProgramConstraints();
			Collections.sort( constraints, LiteComparators.liteStringComparator );
			for( int i = 0; i < constraints.size(); i++ )
				ivjJComboBoxConstraint.addItem( constraints.get(i) );
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxConstraint;
}
/**
 * Return the JComboBoxControl property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxOperationalState() {
	if (ivjJComboBoxOperationalState == null) {
		try {
			ivjJComboBoxOperationalState = new javax.swing.JComboBox();
			ivjJComboBoxOperationalState.setName("JComboBoxOperationalState");
			// user code begin {1}
			ivjJComboBoxOperationalState.addItem( LMProgramBase.OPSTATE_AUTOMATIC );
			ivjJComboBoxOperationalState.addItem( LMProgramBase.OPSTATE_MANUALONLY );
			ivjJComboBoxOperationalState.addItem( LMProgramBase.OPSTATE_TIMED);
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxOperationalState;
}
/**
 * Return the JLabelActualProgType property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelActualProgType() {
	if (ivjJLabelActualProgType == null) {
		try {
			ivjJLabelActualProgType = new javax.swing.JLabel();
			ivjJLabelActualProgType.setName("JLabelActualProgType");
			ivjJLabelActualProgType.setFont(new java.awt.Font("Arial", 1, 14));
			ivjJLabelActualProgType.setText("(unknown)");
			// user code begin {1}

			ivjJLabelActualProgType.setVisible(true);
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelActualProgType;
}
/**
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelConstraint() {
	if (ivjJLabelConstraint == null) {
		try {
			ivjJLabelConstraint = new javax.swing.JLabel();
			ivjJLabelConstraint.setName("JLabelConstraint");
			ivjJLabelConstraint.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelConstraint.setText("Program Constraint: ");
			ivjJLabelConstraint.setMaximumSize(new java.awt.Dimension(131, 23));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelConstraint;
}
/**
 * Return the SelectLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelName() {
	if (ivjJLabelName == null) {
		try {
			ivjJLabelName = new javax.swing.JLabel();
			ivjJLabelName.setName("JLabelName");
			ivjJLabelName.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelName.setText("Name:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelName;
}
/**
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelOperationalState() {
	if (ivjJLabelOperationalState == null) {
		try {
			ivjJLabelOperationalState = new javax.swing.JLabel();
			ivjJLabelOperationalState.setName("JLabelOperationalState");
			ivjJLabelOperationalState.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelOperationalState.setText("Operational State:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelOperationalState;
}
/**
 * Return the JLabelProgramType property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelProgramType() {
	if (ivjJLabelProgramType == null) {
		try {
			ivjJLabelProgramType = new javax.swing.JLabel();
			ivjJLabelProgramType.setName("JLabelProgramType");
			ivjJLabelProgramType.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelProgramType.setText("Program Type:");
			// user code begin {1}

			ivjJLabelProgramType.setVisible(true);
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelProgramType;
}
/**
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelTriggerOffset() {
	if (ivjJLabelTriggerOffset == null) {
		try {
			ivjJLabelTriggerOffset = new javax.swing.JLabel();
			ivjJLabelTriggerOffset.setName("JLabelTriggerOffset");
			ivjJLabelTriggerOffset.setText("Trigger Offset: ");
			ivjJLabelTriggerOffset.setMaximumSize(new java.awt.Dimension(104, 20));
			ivjJLabelTriggerOffset.setPreferredSize(new java.awt.Dimension(104, 20));
			ivjJLabelTriggerOffset.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelTriggerOffset.setMinimumSize(new java.awt.Dimension(104, 20));
			// user code begin {1}
			ivjJLabelTriggerOffset.setToolTipText("Any postivie float value is valid");
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelTriggerOffset;
}
/**
 * Return the JPanelTriggerThreshold property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelTriggerThreshold() {
	if (ivjJPanelTriggerThreshold == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("Arial", 1, 12));
			ivjLocalBorder.setTitle("Trigger Threshold Settings");
			ivjJPanelTriggerThreshold = new javax.swing.JPanel();
			java.awt.GridBagConstraints consGridBagConstraints2 = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints consGridBagConstraints3 = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints consGridBagConstraints4 = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints consGridBagConstraints1 = new java.awt.GridBagConstraints();
			consGridBagConstraints2.insets = new java.awt.Insets(10,11,0,2);
			consGridBagConstraints2.ipadx = 3;
			consGridBagConstraints2.gridy = 0;
			consGridBagConstraints2.gridx = 0;
			consGridBagConstraints3.insets = new java.awt.Insets(10,11,10,2);
			consGridBagConstraints3.ipady = 1;
			consGridBagConstraints3.ipadx = 11;
			consGridBagConstraints3.gridy = 1;
			consGridBagConstraints3.gridx = 0;
			consGridBagConstraints4.insets = new java.awt.Insets(10,2,10,150);
			consGridBagConstraints4.ipadx = -10;
			consGridBagConstraints4.fill = java.awt.GridBagConstraints.HORIZONTAL;
			consGridBagConstraints4.weightx = 1.0;
			consGridBagConstraints4.gridy = 1;
			consGridBagConstraints4.gridx = 1;
			consGridBagConstraints4.anchor = java.awt.GridBagConstraints.NORTHWEST;
			consGridBagConstraints3.anchor = java.awt.GridBagConstraints.NORTHWEST;
			consGridBagConstraints2.anchor = java.awt.GridBagConstraints.NORTHWEST;
			consGridBagConstraints1.insets = new java.awt.Insets(10,2,0,150);
			consGridBagConstraints1.ipadx = -10;
			consGridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
			consGridBagConstraints1.weightx = 1.0;
			consGridBagConstraints1.gridy = 0;
			consGridBagConstraints1.gridx = 1;
			consGridBagConstraints1.anchor = java.awt.GridBagConstraints.NORTHWEST;
			ivjJPanelTriggerThreshold.setName("JPanelTriggerThreshold");
			ivjJPanelTriggerThreshold.setPreferredSize(new java.awt.Dimension(344,120));
			ivjJPanelTriggerThreshold.setBorder(ivjLocalBorder);
			ivjJPanelTriggerThreshold.setLayout(new java.awt.GridBagLayout());
			ivjJPanelTriggerThreshold.add(getJTextFieldTriggerOffset(), consGridBagConstraints1);
			ivjJPanelTriggerThreshold.add(getJLabelTriggerOffset(), consGridBagConstraints2);
			ivjJPanelTriggerThreshold.add(getJLabel(), consGridBagConstraints3);
			ivjJPanelTriggerThreshold.add(getJTextFieldOffset(), consGridBagConstraints4);
			ivjJPanelTriggerThreshold.setMinimumSize(new java.awt.Dimension(344, 68));

			getJPanelTriggerThreshold().setVisible(false);
			// user code end
			ivjJPanelTriggerThreshold.setEnabled(true);
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelTriggerThreshold;
}


public void setTriggerThresholdVisible(boolean visible)
{
	getJPanelTriggerThreshold().setVisible(visible);
}
/**
 * Return the JTextField1 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldName() {
	if (ivjJTextFieldName == null) {
		try {
			ivjJTextFieldName = new javax.swing.JTextField();
			ivjJTextFieldName.setName("JTextFieldName");
			ivjJTextFieldName.setToolTipText("Name of Program");
			// user code begin {1}

			ivjJTextFieldName.setDocument(
					new TextFieldDocument(
						TextFieldDocument.MAX_DEVICE_NAME_LENGTH,
						TextFieldDocument.INVALID_CHARS_PAO) );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldName;
}
/**
 * Return the JTextField1 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldTriggerOffset() {
	if (ivjJTextFieldTriggerOffset == null) {
		try {
			ivjJTextFieldTriggerOffset = new javax.swing.JTextField();
			ivjJTextFieldTriggerOffset.setName("JTextFieldTriggerOffset");
			ivjJTextFieldTriggerOffset.setPreferredSize(new java.awt.Dimension(72, 20));
			ivjJTextFieldTriggerOffset.setMinimumSize(new java.awt.Dimension(72, 20));
			// user code begin {1}
			
			ivjJTextFieldTriggerOffset.setDocument( new com.cannontech.common.gui.unchanging.DoubleRangeDocument( 0.0000, 99999.9999, 4 ) );
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldTriggerOffset;
}

/**
 * getValue method comment.
 */
public Object getValue(Object o) 
{
	LMProgramBase program = (LMProgramBase)o;

	program.setName( getJTextFieldName().getText() );
	program.getProgram().setControlType( getJComboBoxOperationalState().getSelectedItem().toString() );
	
	if( getJComboBoxConstraint().getSelectedItem() != null )
		program.getProgram().setConstraintID( new Integer(((com.cannontech.database.data.lite.LiteLMConstraint)getJComboBoxConstraint().getSelectedItem()).getConstraintID() ));

	if(program.getPAOType().compareTo(DeviceTypes.STRING_LM_DIRECT_PROGRAM[0]) == 0)
	{
		LMProgramDirect prog = (LMProgramDirect)o;
		if(getJTextFieldTriggerOffset().getText().length() > 0)
			prog.getDirectProgram().setTriggerOffset(new Double(getJTextFieldTriggerOffset().getText()));

		if(getJTextFieldOffset().getText().length() > 0)
			prog.getDirectProgram().setRestoreOffset(new Double(getJTextFieldOffset().getText()));
	}

	return o;
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
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getJComboBoxOperationalState().addActionListener(ivjEventHandler);
	getJTextFieldName().addCaretListener(ivjEventHandler);
	getJComboBoxConstraint().addActionListener(ivjEventHandler);
	getJTextFieldTriggerOffset().addCaretListener(ivjEventHandler);
	
	getJTextFieldOffset().addCaretListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("LMProgramBasePanel");
		java.awt.BorderLayout layBorderLayout37 = new java.awt.BorderLayout();
		layBorderLayout37.setHgap(0);
		layBorderLayout37.setVgap(50);
		this.setLayout(layBorderLayout37);
		this.add(getActionPasser(), java.awt.BorderLayout.NORTH);
		this.add(getJPanelTriggerThreshold(), java.awt.BorderLayout.SOUTH);
		this.add(getJPanel(), java.awt.BorderLayout.NORTH);
		setSize(364, 392);

		this.setComponentOrientation(java.awt.ComponentOrientation.UNKNOWN);
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
public boolean isInputValid() 
{
	if( getJTextFieldName().getText() == null || getJTextFieldName().getText().length() <= 0 )
	{
		setErrorString("The Name text field must be filled in");
		return false;
	}


	return true;
}
public boolean isTimedOperationalState()
{
	return getJComboBoxOperationalState().getSelectedItem().toString().compareTo("Timed") == 0;
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		LMProgramBasePanel aLMProgramBasePanel;
		aLMProgramBasePanel = new LMProgramBasePanel();
		frame.setContentPane(aLMProgramBasePanel);
		frame.setSize(aLMProgramBasePanel.getSize());
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
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
	}
}
public void setIsAWizardOp(boolean wizard)
{
	isAWizardOp = wizard;
}
/**
 * setValue method comment.
 */
public void setValue(Object o) 
{
	LMProgramBase program = (LMProgramBase)o;

	getJTextFieldName().setText( program.getPAOName() );
	getJComboBoxOperationalState().setSelectedItem( program.getProgram().getControlType().toString() );

	getJLabelProgramType().setVisible( true );
	getJLabelActualProgType().setVisible( true );
	getJLabelActualProgType().setText( program.getPAOType() );

	for( int i = 0; i < getJComboBoxConstraint().getItemCount(); i++ )
		if( ((com.cannontech.database.data.lite.LiteLMConstraint)getJComboBoxConstraint().getItemAt(i)).getConstraintID()
			== program.getProgram().getConstraintID().intValue() )
			{
				getJComboBoxConstraint().setSelectedIndex(i);
				break;
			}
			
	if(program.getPAOType().compareTo(DeviceTypes.STRING_LM_DIRECT_PROGRAM[0]) == 0)
	{
		getJPanelTriggerThreshold().setVisible(true);
		getJTextFieldTriggerOffset().setText(((LMProgramDirect)program).getDirectProgram().getTriggerOffset().toString());
		getJTextFieldOffset().setText(((LMProgramDirect)program).getDirectProgram().getRestoreOffset().toString());
	}

}
/**
 * Method to handle events for the JCValueListener interface.
 * @param arg1 com.klg.jclass.util.value.JCValueEvent
 */
public void valueChanged(com.klg.jclass.util.value.JCValueEvent arg1) 
{
	//fire this event for all JCSpinFields!!
	this.fireInputUpdate();
}
/**
 * Method to handle events for the JCValueListener interface.
 * @param arg1 com.klg.jclass.util.value.JCValueEvent
 */
public void valueChanging(com.klg.jclass.util.value.JCValueEvent arg1) 
{
}
	/**
	 * This method initializes jLabel
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJLabel() {
		if(jLabel == null) {
			jLabel = new javax.swing.JLabel();
			jLabel.setBounds(16, 57, 107, 20);
			jLabel.setText("Restore Offset:");
			jLabel.setFont(new java.awt.Font("Dialog", java.awt.Font.PLAIN, 14));
			jLabel.setName("JLabelRestoreOffset");
			jLabel.setToolTipText("Any postivie or negative float value is valid");
		}
		return jLabel;
	}
	/**
	 * This method initializes jTextField
	 * 
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JTextField getJTextFieldOffset() {
		if(jTextFieldOffset == null) {
			jTextFieldOffset = new javax.swing.JTextField();
			jTextFieldOffset.setPreferredSize(new java.awt.Dimension(72,20));
			jTextFieldOffset.setName("JTextFieldRestoreOffset");
			
			jTextFieldOffset.setDocument( 
				new DoubleRangeDocument( -9999.9999, 99999.9999, 4 ) );
			jTextFieldOffset.setActionCommand("");
		}
		return jTextFieldOffset;
	}
	/**
	 * This method initializes jPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getJPanel() {
		if(jPanel == null) {
			jPanel = new javax.swing.JPanel();
			java.awt.GridBagConstraints consGridBagConstraints26 = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints consGridBagConstraints27 = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints consGridBagConstraints28 = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints consGridBagConstraints30 = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints consGridBagConstraints31 = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints consGridBagConstraints32 = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints consGridBagConstraints29 = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints consGridBagConstraints33 = new java.awt.GridBagConstraints();
			consGridBagConstraints26.insets = new java.awt.Insets(5,2,19,3);
			consGridBagConstraints26.ipadx = 5;
			consGridBagConstraints26.gridwidth = 3;
			consGridBagConstraints26.gridy = 3;
			consGridBagConstraints26.gridx = 0;
			consGridBagConstraints27.insets = new java.awt.Insets(5,3,15,5);
			consGridBagConstraints27.fill = java.awt.GridBagConstraints.HORIZONTAL;
			consGridBagConstraints27.weightx = 1.0;
			consGridBagConstraints27.gridy = 3;
			consGridBagConstraints27.gridx = 3;
			consGridBagConstraints32.insets = new java.awt.Insets(5,7,8,4);
			consGridBagConstraints32.ipadx = 279;
			consGridBagConstraints32.fill = java.awt.GridBagConstraints.HORIZONTAL;
			consGridBagConstraints32.weightx = 1.0;
			consGridBagConstraints32.gridwidth = 3;
			consGridBagConstraints32.gridy = 0;
			consGridBagConstraints32.gridx = 1;
			consGridBagConstraints31.insets = new java.awt.Insets(5,1,9,7);
			consGridBagConstraints31.ipadx = 151;
			consGridBagConstraints31.gridwidth = 2;
			consGridBagConstraints31.gridy = 1;
			consGridBagConstraints31.gridx = 2;
			consGridBagConstraints28.insets = new java.awt.Insets(5,1,6,5);
			consGridBagConstraints28.ipadx = 196;
			consGridBagConstraints28.fill = java.awt.GridBagConstraints.HORIZONTAL;
			consGridBagConstraints28.weightx = 1.0;
			consGridBagConstraints28.gridwidth = 2;
			consGridBagConstraints28.gridy = 2;
			consGridBagConstraints28.gridx = 2;
			consGridBagConstraints28.anchor = java.awt.GridBagConstraints.NORTH;
			consGridBagConstraints30.insets = new java.awt.Insets(5,2,7,19);
			consGridBagConstraints30.ipadx = 5;
			consGridBagConstraints30.gridwidth = 2;
			consGridBagConstraints30.gridy = 1;
			consGridBagConstraints30.gridx = 0;
			consGridBagConstraints30.anchor = java.awt.GridBagConstraints.NORTH;
			consGridBagConstraints31.anchor = java.awt.GridBagConstraints.NORTH;
			consGridBagConstraints26.anchor = java.awt.GridBagConstraints.NORTH;
			consGridBagConstraints33.insets = new java.awt.Insets(5,2,9,6);
			consGridBagConstraints33.ipadx = 11;
			consGridBagConstraints33.gridy = 0;
			consGridBagConstraints33.gridx = 0;
			consGridBagConstraints33.anchor = java.awt.GridBagConstraints.NORTH;
			consGridBagConstraints32.anchor = java.awt.GridBagConstraints.NORTH;
			consGridBagConstraints27.anchor = java.awt.GridBagConstraints.NORTH;
			consGridBagConstraints29.insets = new java.awt.Insets(5,2,12,0);
			consGridBagConstraints29.ipadx = 3;
			consGridBagConstraints29.gridwidth = 2;
			consGridBagConstraints29.gridy = 2;
			consGridBagConstraints29.gridx = 0;
			consGridBagConstraints29.anchor = java.awt.GridBagConstraints.NORTH;
			jPanel.setLayout(new java.awt.GridBagLayout());
			jPanel.add(getJLabelConstraint(), consGridBagConstraints26);
			jPanel.add(getJComboBoxConstraint(), consGridBagConstraints27);
			jPanel.add(getJComboBoxOperationalState(), consGridBagConstraints28);
			jPanel.add(getJLabelOperationalState(), consGridBagConstraints29);
			jPanel.add(getJLabelProgramType(), consGridBagConstraints30);
			jPanel.add(getJLabelActualProgType(), consGridBagConstraints31);
			jPanel.add(getJTextFieldName(), consGridBagConstraints32);
			jPanel.add(getJLabelName(), consGridBagConstraints33);
			jPanel.setVisible(true);
			jPanel.setPreferredSize(new java.awt.Dimension(374,180));
			jPanel.setName("InfoPanel");
		}
		return jPanel;
	}
}
