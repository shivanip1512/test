package com.cannontech.importer.point;

import java.io.IOException;

import com.cannontech.clientutils.CTILogger;

/**
 * Insert the type's description here.
 * Creation date: (11/19/2003 2:42:51 PM)
 * @author: jdayton
 */

public class PointImportUtilityPanel {
	private javax.swing.JButton ivjAnalogImportJButton = null;
	private javax.swing.JTextField ivjAnalogJTextField = null;
	private javax.swing.JLabel ivjAnalogLabel = null;
    private javax.swing.JButton ivjAcuumulatorImportJButton = null;
    private javax.swing.JTextField ivjAcuumulatorJTextField = null;
    private javax.swing.JLabel ivjAcuumulatorLabel = null;    
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JFrame ivjJFrame1 = null;
	private javax.swing.JPanel ivjJFrameContentPane = null;
	private javax.swing.JPanel ivjPointImportPanel = null;
	private javax.swing.JButton ivjStatusImportJButton = null;
	private javax.swing.JTextField ivjStatusJTextField = null;
	private javax.swing.JLabel ivjStatusLabel = null;
	private boolean analogSuccess;
	private boolean statusSuccess;
    private boolean accumulatorSuccess;

class IvjEventHandler implements java.awt.event.ActionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == PointImportUtilityPanel.this.getAnalogJTextField()) 
				connEtoC1(e);
            if (e.getSource() == PointImportUtilityPanel.this.getAccumulatorJTextField()) 
                connEtoC5(e);
			if (e.getSource() == PointImportUtilityPanel.this.getStatusJTextField()) 
				connEtoC2(e);
			if (e.getSource() == PointImportUtilityPanel.this.getAnalogImportJButton()) 
				connEtoC3(e);
            if (e.getSource() == PointImportUtilityPanel.this.getAccumulatorImportJButton()) 
                connEtoC6(e);
			if (e.getSource() == PointImportUtilityPanel.this.getStatusImportJButton()) 
				connEtoC4(e);
		};
	};
/**
 * PointImportUtilityPanel constructor comment.
 */
public PointImportUtilityPanel() {
	super();
	initialize();
}
/**
 * Comment
 */
public void analogImportJButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	String fileName = getAnalogJTextField().getText();
	try {
		analogSuccess = PointImportUtility.processAnalogPoints(fileName);
	}catch( IOException e ) {
		analogSuccess = false;
	}
	if(analogSuccess)
	{
		javax.swing.JOptionPane.showMessageDialog(
						this.getJFrame1(), 
						"Analog point file was processed and inserted successfully. \n" +
						"All analog points were added to the database" , "Import Successful",
						javax.swing.JOptionPane.INFORMATION_MESSAGE );
		System.exit(0);
	}
	else
	{
		javax.swing.JOptionPane.showMessageDialog(
					this.getJFrame1(), 
					"Analog point import could not be completed successfully. \n" 
					+ "Please verify that you are not trying to import duplicates." , "Import FAILED",
					javax.swing.JOptionPane.WARNING_MESSAGE );
			
			
	}
		
	return;
}
/**
 * Comment
 */
public void AnalogjTextField_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	return;
}

/**
 * connEtoC1:  (AnalogJTextField.action.actionPerformed(java.awt.event.ActionEvent) --> PointImportUtilityPanel.AnalogjTextField_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.AnalogjTextField_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC1:  (AccumulatorJTextField.action.actionPerformed(java.awt.event.ActionEvent) --> PointImportUtilityPanel.AnalogjTextField_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.awt.event.ActionEvent arg1) {
    try {
        // user code begin {1}
        // user code end
        this.AccumulatorjTextField_ActionPerformed(arg1);
        // user code begin {2}
        // user code end
    } catch (java.lang.Throwable ivjExc) {
        // user code begin {3}
        // user code end
        handleException(ivjExc);
    }
}
/**
 * connEtoC2:  (StatusJTextField.action.actionPerformed(java.awt.event.ActionEvent) --> PointImportUtilityPanel.StatusjTextField_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.StatusjTextField_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (AnalogImportJButton.action.actionPerformed(java.awt.event.ActionEvent) --> PointImportUtilityPanel.analogImportJButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.analogImportJButton_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(java.awt.event.ActionEvent arg1) {
    try {
        // user code begin {1}
        // user code end
        this.AccumulatorImportJButton_ActionPerformed(arg1);
        // user code begin {2}
        // user code end
    } catch (java.lang.Throwable ivjExc) {
        // user code begin {3}
        // user code end
        handleException(ivjExc);
    }
}

/**
 * connEtoC4:  (StatusImportJButton.action.actionPerformed(java.awt.event.ActionEvent) --> PointImportUtilityPanel.statusImportJButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.statusImportJButton_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Return the AnalogImportJButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getAnalogImportJButton() {
	if (ivjAnalogImportJButton == null) {
		try {
			ivjAnalogImportJButton = new javax.swing.JButton();
			ivjAnalogImportJButton.setName("AnalogImportJButton");
			ivjAnalogImportJButton.setText("ANALOG IMPORT");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAnalogImportJButton;
}
/**
 * Return the AccumulatorImportJButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getAccumulatorImportJButton() {
    if (ivjAcuumulatorImportJButton == null) {
        try {
            ivjAcuumulatorImportJButton = new javax.swing.JButton();
            ivjAcuumulatorImportJButton.setName("AccumulatorImportJButton");
            ivjAcuumulatorImportJButton.setText("ACCUMULATOR IMPORT");
            // user code begin {1}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {2}
            // user code end
            handleException(ivjExc);
        }
    }
    return ivjAcuumulatorImportJButton;
}

/**
 * Return the AnalogJTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getAnalogJTextField() {
	if (ivjAnalogJTextField == null) {
		try {
			ivjAnalogJTextField = new javax.swing.JTextField();
			ivjAnalogJTextField.setName("AnalogJTextField");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAnalogJTextField;
}

/**
 * Return the AnalogJTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getAccumulatorJTextField() {
    if (ivjAcuumulatorJTextField == null) {
        try {
            ivjAcuumulatorJTextField = new javax.swing.JTextField();
            ivjAcuumulatorJTextField.setName("AccumulatorJTextField");
            // user code begin {1}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {2}
            // user code end
            handleException(ivjExc);
        }
    }
    return ivjAcuumulatorJTextField;
}
/**
 * Return the AnalogLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getAnalogLabel() {
	if (ivjAnalogLabel == null) {
		try {
			ivjAnalogLabel = new javax.swing.JLabel();
			ivjAnalogLabel.setName("AnalogLabel");
			ivjAnalogLabel.setText("Analog points file (complete path)");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAnalogLabel;
}

/**
 * Return the AnalogLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getAccumulatorLabel() {
    if (ivjAcuumulatorLabel == null) {
        try {
            ivjAcuumulatorLabel = new javax.swing.JLabel();
            ivjAcuumulatorLabel.setName("AccumulatorLabel");
            ivjAcuumulatorLabel.setText("Accumulator points file (complete path)");
            // user code begin {1}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {2}
            // user code end
            handleException(ivjExc);
        }
    }
    return ivjAcuumulatorLabel;
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G78C2F4AFGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BB8BD0D4D716A6E592374619B2BBD60D13A133A41AA912B03359C4B3D662EE580C354B4C10514DA69337066C120A1BB533A64BEC1CA9B363CCBDE808C07395BFA8288DB48A89E2730912368579E8GD1DA9A91933361C1BF2035695769FE2D98D1771C7BF97D3AF98D22112AD36F5DF36EBD775CF34FB9775C538FE16B5E687414542D021012AE303F43A902F06128A034FCD870BD0E4BB0D90C02662F
	9420C0E86ACD03E7915066DF309873058D47B220DD8D6D124FAD469D70DEA83CFFEC2D95DEA24C3382E885637F7B76747314EE6773ECA66D02EB59704C8530G2E8FA05A0473D7DDABE27889E86732F70414D4C168AB45BE577797E970EBD93FCA2059G5612BE6D57CA98BD8AF12C4F52D27ACC00E7A6116EBEEBA36BF7614198E143AF76246660DC9A7E9C2A4B68D8BBFB1A049F91C59804FBDBBF06274DFEE93449AB3BBC0AEB5CAB7B140062F23B142BDE51A3392DD6BB92BA8961ACA55811G63624AD05E45
	7BB388AFC118876D6EF24495D7220E52603D8420211C4B7CAFB9B6A65342CBB1C252620208B2AFA8E76BAE8F4AFC612B87521DE664BFCE76G47EDGFA35194F7B844147C0BB8EE00D0A4FAEC5FCB234B3AAACC6D385176768FEAE4F40351F8A9B07DAD273A24853D8C165982C884A73D41EE458C9EC6247BBF0DF23009687708A40C60022FD96E3A3C0B13145316D9F4233E5C8E9134747E5CF0B575B6AF67ACE49CE498D6FD6ABD0982ED79A16FDCEC1207696333FDAC79E8E76FD213606C057F6CE6076915B7B
	93C1D18E27266B2CED692B26D4A763B131AF383638FD28373FA17B38885EF19C6A06627F166E6F7EA01E6ECBA941E33B9AE8B17B391E87F239AC5DE90F8A7935ED291BF5E4D974E2E32A39124AE02B8C4AC2F57C40CE547198606782C481AC85F885E0C3A55771535B6BE752F127C711FC9E516D888CF9C51F529AF0391D120F695AFFEDBADD6B819DF74F2C47D13D4454EB77A87119D6DD3DA4466CCF4D2FE23122AAFC0F76CC224EBB2A68DE9854BD2AA478818D1E6E51D344C730BD863484G5AFF8E71914B07
	34FF264A675B845F2D41D3BEDB889E5BE3C0CBG207BD4D10C714B7291789E0093E092A08AE0B9C042C7FC2F7E3C7D60AD7A03DD33CFF46C33BB3643D31AD0FCE227472F081EE129DF743944A1376497D81F97F620AE07C2462DA4FEBA8CF3FCAAF9A41F2838E48FF09747A5B08ABF6D73520E826C631388354FB5AE418463DE0173FDF5C7A1BC5D22DFB96BF50A0A946CC1692F6FC01DDC967B01A3B0007712EA54578BA4CE2E0775E7D4F3DD7516A0EE905A9355DCAF83C5DB60F9920C9423EF3BEB984590FDD7
	392DBDD6097A1D8F7D9B8ED80C09G8BEBAC4676036087G31966EF36F8EF05BEA4F7931D07DEA0B2EEDA1B8EB29CDCD5686ED0B4E350C4C358470AB8156822455D90CE600F6GE79DBF9377D7FA79DC870371886562B22936FCE0EA3C5283DCDFA4DA8363C167BE214EED8C73AFBCDB7242FC75D666B6CCC3CBE7CF3AFFF13B518766029E2281626A789EFE3748C4ED3BD574CB5C1AFBFCE00812B33823346F021DA823F4D972A86D12D772B861454FEC69FBBBB303F6BEA22BB678B041E37CEA83C3742B7845BB
	8BA89EF8E83D06531FA4F4D1D1FC2E2100A2B9A4C5F1F9C603631FA13278C3C2231F2D7545BD3856B53046E4G211E2F55362BD0E32F5D32FCA9606DC3CFE47BE31E447CE9EBBD37159A522ED45B4FDE9B3845FD61003CE6971FD74DFA0E69ECC0FF0E7BCB73856ADF86E6A71BD55AF7892D9B64D6G22GD62A6BAFAA9D5099B79BD9E7162D164C21651F798D4EC16D60E0F1E4BBBED42CEF4736E2FDBBEEAD1E5ECE3B0A756C14524E94639A65AB9E0D5D7797E3CC9D16BD9E09B81FBAC420F9703BFD388E1747
	251CF3B9C7A545617ADA82BBEB40387DEB9A37E13F04869EDB2F147358EA7E7AF1A16E4B925D5CEA51E3C7D2879A282D3A9B0231154AF42E847DC9F2CB632028B6B960D1A87E8BF2A6B03C5FC0F0BF9C78BE0E2D6C6A839D1EF0C9EEE7ED35C8D6D2C39FF649B7A27B46A5E765C57132D8A3DED1EA48CE54349072F1FC2FB29DB850E40A104ED7DF8C327E32A50CE7F7E426C72A7A2BAC4C368A053FEC7DC1381CF7D350D9CBDA4B64FC6E6093C44E96C88465D1BAC9D7EBC0D1E44FB7AEAF6486CA409FB247EC45
	2E677ACD98DE2C11BB2865EFDAE64ABF286959DB84153767G1557C1F4F07764257CEFDFDEEE976B36BF9E49AE3EE9114529D34CD66ABA556FFA3B9E5768792E4A7D4D18B34BA9FE3F97E360FC080D51G4B9BF8CE71EFA5C867B985CB1C5AE4371B06EF2015FC0C31F9FD65D6CD3CDD5DC06BB139AA3D28EAFAFA7B8CF46F8C7428996871B350B7C424D3BD7C53EE4C9B7C52D781892EB818D41D142E72FB4A6EADE134FE519D885EFFDE5D0D7930DB130FD1FC7C9E7DF367D7FBB0AF3B2472FFF94F87825EE3A8
	CFEAE7FBBE435C216B845E18EAD9CD6B4BC9EDCD4B896B427A57BCF8C6617BA16C734F253C263594F089G6BD93F6AC32C76C07AFD2B0C77DB896D44C341FE1BC27AB51773FE9D07AD46C100B907E93F38435AFE86B36F175FE8B1D6B6867BF5B452FE0B9A315FBBEA8D6B200ACB2BC45CEFE041FF15466F6B277235B5B7273FC1775CC7C8E47C139B436B989F16A25F7CCFACC653A75C5E3B77EAF368963FDF9EF6116BA75B33BF6D55668494A7183260B962727915CEC836A6783EFFE0C2D971FE4A50AC47F972
	232342547B76F127CB11FD7C0A52F605B8E91F5C86D7648EB01DB6B5E56AF7C9D7842E076BA734F53C95F9D5294688FAE878042E3F6F13209E14BC49C0E5FAFF975A8E29BFE248182871DF01BC4FEA2596C66B0BBF1F4CE073AE7C045773A6EF228DA7C0FBAD40A6GDA13C8A6B589643D558AFD81281F7DB1D76BE7F3819F8B309A005A7539B20C25330F14DC37C7BA1AD137F7F6EAB25DBEDAF80C49197013C7855B4DAC5D1A643C3C2D29EB2DD42759C7023A3594B98C6DGD49FCB4B38BE86204F02A3DC9F51
	3B7474918FF43A1E3A0B381EBBBCD5D91DE82C1C4B10887C375A388CAFA9DA993AEDD486454677E4D5791D6D491B632DB87E0E0FD9DE6F2AB673F54C83996354F51CAC56DB47C69B57E5348D653873E3D32D6B56E9E3D0DCD2E5449814FD14594551709814DB0EBEE5833C7DA80FC177188AB4B128973CDE740C4229215E6FCAF1DF60FCE06D7AD21C171C8F82BF4FCF1AD197CE60B987E09140D3G2B55B91EAB1D31BEFBFCE21A7A6C2FA6314E1AFCD47F7E1F56E4B196B585E955CD3C3619CC62453126709A69
	9F895E5E94DEA33D77A62F118E82EDE5933F2B7F21B43C4EDB599CAACBFBD81B0ECBAB8B9F37A72CBD96B60E5ACE85293F6944C755A769997C57C66D99BC87F8AD810887C88420F134B60F1FF19B1AF97D71118A64DFFD8C4EC300C100C900E80095GC947783EE516691D9D217B96696088562028AE1E1B220BDB53E17C0CBA94DAC24751F53F360B2F3B28056F23277C76E4181CB2AEDC86FB0B5EBE66B9F66A6FA30D842A2CE9018CB6E6CC15354EFCFB329E1BB2AEDC56E8DDFD5DDB4CE5D85E426D640DFD08
	CB0636504AEDE2248C6B2BBCDE106B0240E3C5E1B9660E324FB56A72F0DC91418D4BBED21821B807B9F24D68B333FE4DE8502CDFB39AB1CFDF7B7418A35514E8FD7EBFC91E118F6BEBG20B85BF544F5C0FBD045BDFF83F1F3213DC845255DC4DCAC3463DA55D8F193736984358D7189G7D1236675D1C29A609E32723CF87F40EC7E61CE3B6F54EF058CD467FFCFB9E513B5FAF79C990E8979511517ECB59A668545DCEC3F2A770BB450132CC566738A237552ABE73DED5045C105CC3E95C2B9542639E56155703
	5E04B69E77FEE16476DC544673474339A46EC3FB8CE0CE9B5F13287C69FE47DB54B6F5BFD8DD3DA2329DBFDD21EF474BAB50FF7CF240B7AC055B6A8B95C5AAAD98C9BB95E99CD5476EF1E43CA27AC625A9363E0A0C2734C86357D4C472873AAF3F8B5917BA552692F7CD2723D89D9DD1FE0F15EB79552B7CB283FA7C62981FD7A272F319357C9AD4FE8F946B71DB4778EC0A3807EF6D439CE0C2ED7F16342F2A73ED1AC4DBF6CAA3E2408D21C776CBBDDE7693A260BB5A512EF6DC6736165F4E733F3F3B02F72391
	385000FA9DDEE9D8B09D473E0E6B5CF667C35FE800B800A4G61B84F01DF3E01EB3A6C723B707ECE67F87B86BF4F4654FE45B7F1AE452510FCED62B8FE47E22B91D4594F7662DE53DB955803C2C224877BD29F591B4265DEF11C7B486BD968A39B201DFD42E2B41F60BE324A982EDF5B0920CE6DA7266A17FDB3C0FC4EF90266DAB9AA7E9F88FE1E86CFF33023840F6DD82095FD4AE36DB379BCA64FD5F16F6D6238D7D41C091C25668E08679D9CF7CF0077BB2662FEC84E3B85500ED6F106925EAF36036B3866BA
	2EDB930AEA4E39BCCE798A6893FF01D67CB56D1D8EFB0F637817BD273FEC6B06971A2FC7DF51666B0DD9B5917375CD9DD4F7791D7429980A597EDCA5F605E3BA00B62793F169B734778E14718D68001BCE6907C368387FBF9334F4D7889DE52DC85352FF90C2C719EF3271F4DD3F2C60B500CD3FFCCAF0AF33C47C16A71E2DE7BDFBE63C3CCBD8779B07016AD5A06724628BC8146F2A4F357A4AFF28A8B5A582DFF39765E7698A7F5D5A1C92EE234E2E20DDCEF4C53251B3C6D20F6C8A3FA7FCCE70D1DD617704B7
	3378BDA196E879A7397D3CC54E9C855A73D4DCDA0EEAD3A739AFAD4B4598309A5A6B81B23AADC693C0A340C0B76F331274411A995643A6G3F281B563C301EC57B34658E062D8747A34F70F5A27FC831FB3526FF8613C3CB27F37D5F1439F204606F62DA186EB7C6C398DFE398FDFDB7B93B735BE760114E1E884BE1CC8240B606CF027767D3286DD0FED945B3714B5470EBB8954A2F63D4109F355D0737915B05431D465CBA7CA90C4D35A8B05DDC69E132A7E8E4C77EFDB057A50DBC0542326250393E2E8D4E35
	2BE42639B22719EB906612D99F1A6F6F2FC3DBF583FE5EA935BED07391BC7B450BD3AB0700347BE4B88D152B94CBAA51BCDF38FEFB5FC67D63E69EDF16BE7704307C7E6414C8B54618D354CE6359B3FA75AE0227EBF9309E7DE7A350361E861B863083B881A6CF73354557200CF80B8571DB94C59C9EA315D6FE394562E7900D07B8653D94538E16AF649E2C08249F60508567C64F50C5C8447A44D1015FB90E2470B83160B40F93CFE671B83174B4776FF3C65C7BB5EADBA4ED2CB351765BD933793E81794CE6BF
	689C2946594CB15BFB854BD3B2710E68962F4A8138CCF67420BE723235B97E86549360ADBDAC6613DC2AB80777D9G78A5GAB815682245979791D160D3F25047FF2CAE9FFEEC5DA782F1414766B4DC6F5DC37B8A439192CF9B70C6A98ADFE759705EA7F10FA9F23275DA8D4470552E9BD31FE1B765CB55BE9BD31434E6B0D4F946851AF40F3AF618F31DB60FF494DD3F1BCBF9CB9A4283E76C6B6773512A7978B6D2FA52714C770B52F1D6ABD4E9EBCF367AA8F246B4DCD67D9FDB8B867FB0D41776D78E5B6731D
	6ABA64255EC5E87860F546BADCAFFE1B61925D2E2FC9426D000E1ED16A03EFD9113E916459FA867C9B209BE0708C776D1F5615894437F57C7A38F3D462799B1F6F217A48F739C76AE77E6E7009FA7CDDE6C806AD9DC77E9C7FD4BD7A11A2FB8912621655237EFD2E51B105F6E578676BC97C15C6881A7A01BB135B78A42CEDA1C0BCC062993A8F9B4EE01F9733343E226FE3CB9434F54D4F3B64BECD4F225A4110FAA356222D56CFFDAEE72B88360FDEC973510F33F9BEEA6B65770561EC4C0594E84F87D8524B3F
	15195847EDAB76C70F8A5DF3DAF57344243E2A54953DF4ADC93DC15BDA19A799681CADB9FC4E92874F01651C709C7835109C78111C709C987B84697F1A931E836F8E490153F343F360F4B61E55A8C6A254A8C8C020F2DF5C466FD6FD8EDE1378ED81474DF7709A5BF7ECF8062F04F692C0DA9F78BB0085208360820097E0AEC09440D200F800C400E4G61AC58BFC065D9EE77CF9E2A92A21F57ECD309717BC4ED6D6EDDEBE47B7F3DF5E67B7F0B157CD689F65BF7554BEA8D06A3249EB8B26217D4BF4BB8122F62
	BA7DF6596FC24F26F97542CC7EFD3F8D56231C65F76951328A2E6FE1F93C46B5B4DE9338BCDEA38EA9B592AC4FEABD9DF03B3B5971416ECE09195CEEE24FF2DB4DA2F97BDAE8EFC23D75F3DB3DF8C99B87517EA27ECFC5BF7B1F0A7E202D864FCAEB76ECF39EFA4FF80D785002FEFA4FC8D171C9849F2541D3BD3932391EE20016FD0E5BD220896D528B6D79GCBGD6GA482ECBC476D6361A3182CCEDB536F133D5DE088EECDB915FDF31BF3FB5F494B5B7446E58FB09D8ECC4DCB68387FA9509B57407AF7680C
	23EBFC549659169F374DEC4BCF5B74E379F25B54D87E77B67DD87EB31BB61633FC530676BC8ABEB82076FBC9451DD7F1AF931E08BB67F2AAE3AAFE8D118B719D92CE48717FCE643AC2F0838C772686F71E615659509FE149E332DA0BFF4706B965084B6D266D5F11BEAE8F38272A0FBF9099DDDED1A3771FD45CF9F53F9E4A3CBD7B08394DF1EF4CFA9C752B9BA48EDC988877378788FEECA05C5F7233383FA9C0DBB3407DEDD5B3B9874E433D9DE092E0A1C08CC05CF96EEF5FB90AB54FC87138D71649C10C55B9
	884712CF9319699ADF4729597BDB1AF7B9FB365FB64FDE3F736E6A9CD4BFB71AA27B6AFDCD7AB574EFB5697F9674E053745F8FFF3FA972774351CD5A6F07691D6C7EED3C16784AF9FECF7B6FED7C1E162462BE5802B84BE79663314FB86E52965E2F7BB3FEDF1B1B955C3FEFEB5E374F6A9EF7FBFBF9702E4E4162D3F3643D7C0F6619636E3AE67D387BEE7354387BDE33FE5CFD3FD99BF7691D63B1F26720B513546DB35502347525701AC9946CE53D202D856D5E92DA1F09BF921C6B5EADB34D35F91A39E2E02E
	436C1D72CE411F63593B39895F697F3BAE31BB21D36CBBC23AA29437099E0F6C219FF21AE82AEFE21FF2AAC2CE4B69166E1ECFCD1D276CBD3DFD50F67435741DF530365FA764299F23F412A3A069F23D557066E2C32A1056A9FCC03726D3C8C37DF68A69C47D1D028136E6827F9D872F910719D8E97E2F049BE4E1BD9A2CC14596B2CCFEC85F939A2CC191AC74C2EDA27F266AB741B1A519AA3DEEC974CBA6EFC0B139BCA648888121C0DBD4462AE0883DE35F7A90F5EE831373D9DC09AE3610660BD5B735356A0D
	7765C52C6D2F3EC0F3143597423F45795801F6EA7A42E234FD414F0A7BFA8F8811EBC90EF1516DD6C47725960F936BCA2DD795A9985B2C8E646F67FD981152DA9CA877C54D187F87D0CB878816C218A2EE94GG04BDGGD0CB818294G94G88G88G78C2F4AF16C218A2EE94GG04BDGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG2894GGGG
**end of data**/
}
/**
 * Return the JFrame1 property value.
 * @return javax.swing.JFrame
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JFrame getJFrame1() {
	if (ivjJFrame1 == null) {
		try {
			ivjJFrame1 = new javax.swing.JFrame();
			ivjJFrame1.setName("JFrame1");
			ivjJFrame1.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			ivjJFrame1.setBounds(63, 97, 574, 240);
			ivjJFrame1.setVisible(true);
			ivjJFrame1.setTitle("Cannon Point Import Utility");
			getJFrame1().setContentPane(getJFrameContentPane());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJFrame1;
}
/**
 * Return the JFrameContentPane property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJFrameContentPane() {
	if (ivjJFrameContentPane == null) {
		try {
			ivjJFrameContentPane = new javax.swing.JPanel();
			ivjJFrameContentPane.setName("JFrameContentPane");
			ivjJFrameContentPane.setLayout(null);
			getJFrameContentPane().add(getPointImportPanel(), getPointImportPanel().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJFrameContentPane;
}
/**
 * Return the PointImportPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getPointImportPanel() {
	if (ivjPointImportPanel == null) {
		try {
			ivjPointImportPanel = new javax.swing.JPanel();
			ivjPointImportPanel.setName("PointImportPanel");
			ivjPointImportPanel.setLayout(new java.awt.GridBagLayout());
			ivjPointImportPanel.setBounds(2, 1, 569, 235);

			java.awt.GridBagConstraints constraintsAnalogJTextField = new java.awt.GridBagConstraints();
			constraintsAnalogJTextField.gridx = 1; constraintsAnalogJTextField.gridy = 2;
			constraintsAnalogJTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsAnalogJTextField.weightx = 1.0;
			constraintsAnalogJTextField.ipadx = 264;
			constraintsAnalogJTextField.insets = new java.awt.Insets(4, 27, 10, 33);
			getPointImportPanel().add(getAnalogJTextField(), constraintsAnalogJTextField);

            java.awt.GridBagConstraints constraintsAccumulatorJTextField = new java.awt.GridBagConstraints();
            constraintsAccumulatorJTextField.gridx = 1; constraintsAccumulatorJTextField.gridy = 4;
            constraintsAccumulatorJTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsAccumulatorJTextField.weightx = 1.0;
            constraintsAccumulatorJTextField.ipadx = 264;
            constraintsAccumulatorJTextField.insets = new java.awt.Insets(4, 27, 10, 33);
            getPointImportPanel().add(getAccumulatorJTextField(), constraintsAccumulatorJTextField);            
            
			java.awt.GridBagConstraints constraintsStatusJTextField = new java.awt.GridBagConstraints();
			constraintsStatusJTextField.gridx = 1; constraintsStatusJTextField.gridy = 6;
			constraintsStatusJTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsStatusJTextField.weightx = 1.0;
			constraintsStatusJTextField.ipadx = 264;
			constraintsStatusJTextField.insets = new java.awt.Insets(4, 27, 30, 33);//3 27 72 33
			getPointImportPanel().add(getStatusJTextField(), constraintsStatusJTextField);

			java.awt.GridBagConstraints constraintsAnalogLabel = new java.awt.GridBagConstraints();
			constraintsAnalogLabel.gridx = 1; constraintsAnalogLabel.gridy = 1;
			constraintsAnalogLabel.ipadx = 25;
			//constraintsAnalogLabel.insets = new java.awt.Insets(34, 27, 4, 12);
			getPointImportPanel().add(getAnalogLabel(), constraintsAnalogLabel);

            java.awt.GridBagConstraints constraintsAccumulatorLabel = new java.awt.GridBagConstraints();
            constraintsAccumulatorLabel.gridx = 1; constraintsAccumulatorLabel.gridy = 3;
            constraintsAccumulatorLabel.ipadx = 25;
            //constraintsAccumulatorLabel.insets = new java.awt.Insets(19, 27, 3, 12);
            getPointImportPanel().add(getAccumulatorLabel(), constraintsAccumulatorLabel);            
            
            java.awt.GridBagConstraints constraintsStatusLabel = new java.awt.GridBagConstraints();
			constraintsStatusLabel.gridx = 1; constraintsStatusLabel.gridy = 5;
			constraintsStatusLabel.ipadx = 27;
			//constraintsStatusLabel.insets = new java.awt.Insets(19, 27, 2, 12);//19
			getPointImportPanel().add(getStatusLabel(), constraintsStatusLabel); 
            
			java.awt.GridBagConstraints constraintsAnalogImportJButton = new java.awt.GridBagConstraints();
			constraintsAnalogImportJButton.gridx = 2; constraintsAnalogImportJButton.gridy = 2;
			constraintsAnalogImportJButton.ipadx = 53;
			constraintsAnalogImportJButton.ipady = 4;
			constraintsAnalogImportJButton.insets = new java.awt.Insets(4, 13, 10, 85);
			getPointImportPanel().add(getAnalogImportJButton(), constraintsAnalogImportJButton);

            java.awt.GridBagConstraints constraintsAccumulatorImportJButton = new java.awt.GridBagConstraints();
            constraintsAccumulatorImportJButton.gridx = 2; constraintsAccumulatorImportJButton.gridy = 4;
            constraintsAccumulatorImportJButton.ipadx = 53;
            constraintsAccumulatorImportJButton.ipady = 4;
            constraintsAccumulatorImportJButton.insets = new java.awt.Insets(4, 13, 10, 44);
            getPointImportPanel().add(getAccumulatorImportJButton(), constraintsAccumulatorImportJButton);            
            
			java.awt.GridBagConstraints constraintsStatusImportJButton = new java.awt.GridBagConstraints();
			constraintsStatusImportJButton.gridx = 2; constraintsStatusImportJButton.gridy = 6;
			constraintsStatusImportJButton.ipadx = 55;
			constraintsStatusImportJButton.ipady = 4;
			constraintsStatusImportJButton.insets = new java.awt.Insets(3, 13, 30, 85);
			getPointImportPanel().add(getStatusImportJButton(), constraintsStatusImportJButton);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPointImportPanel;
}
/**
 * Return the StatusImportJButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getStatusImportJButton() {
	if (ivjStatusImportJButton == null) {
		try {
			ivjStatusImportJButton = new javax.swing.JButton();
			ivjStatusImportJButton.setName("StatusImportJButton");
			ivjStatusImportJButton.setText("STATUS IMPORT");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStatusImportJButton;
}
/**
 * Return the StatusJTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getStatusJTextField() {
	if (ivjStatusJTextField == null) {
		try {
			ivjStatusJTextField = new javax.swing.JTextField();
			ivjStatusJTextField.setName("StatusJTextField");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStatusJTextField;
}
/**
 * Return the StatusLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getStatusLabel() {
	if (ivjStatusLabel == null) {
		try {
			ivjStatusLabel = new javax.swing.JLabel();
			ivjStatusLabel.setName("StatusLabel");
			ivjStatusLabel.setText("Status points file (complete path)");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStatusLabel;
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
	getAnalogJTextField().addActionListener(ivjEventHandler);
    getAccumulatorJTextField().addActionListener(ivjEventHandler);
	getStatusJTextField().addActionListener(ivjEventHandler);
	getAnalogImportJButton().addActionListener(ivjEventHandler);
    getAccumulatorImportJButton().addActionListener(ivjEventHandler);
    getStatusImportJButton().addActionListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		ivjJFrame1 = new javax.swing.JFrame();
		ivjJFrame1.setName("JFrame1");
		ivjJFrame1.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		ivjJFrame1.setBounds(63, 97, 574, 240);
		ivjJFrame1.setVisible(true);
		ivjJFrame1.setTitle("Cannon Point Import Utility");
		getJFrame1().setContentPane(getJFrameContentPane());
		getJFrameContentPane().resize(574,240);
		// user code end
		initConnections();
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
	    String appName = "PointImportUtilityTool";
        System.setProperty("cti.app.name", appName);
        CTILogger.info(appName + " starting...");
		PointImportUtilityPanel aPointImportUtilityPanel;
		aPointImportUtilityPanel = new PointImportUtilityPanel();
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of java.lang.Object");
		exception.printStackTrace(System.out);
	}
}
/**
 * Comment
 */
public void statusImportJButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	String fileName = getStatusJTextField().getText();
	
	try {
		statusSuccess = PointImportUtility.processStatusPoints(fileName);
	}catch( IOException e ) {
		statusSuccess = false;
	}

	if(statusSuccess)
	{
	javax.swing.JOptionPane.showMessageDialog(
					this.getJFrame1(), 
					" Status point file was processed and inserted successfully. \n" +
					"All status points were added to the database" , "Import Successful",
					javax.swing.JOptionPane.INFORMATION_MESSAGE );
		System.exit(0);
	}
	else
	{
		javax.swing.JOptionPane.showMessageDialog(
					this.getJFrame1(), 
					" Status point import could not be completed successfully. \n" 
					+ "Please verify that you are not trying to import duplicates." , "Import FAILED",
					javax.swing.JOptionPane.WARNING_MESSAGE );
	}
	return;
}
/**
 * Comment
 */
public void StatusjTextField_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	return;
}
/**
 * Comment
 */
public void AccumulatorImportJButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
    String fileName = getAccumulatorJTextField().getText();
    
	try {
		accumulatorSuccess = PointImportUtility.processAccumulatorPoints(fileName);
	}catch( IOException e ) {
		accumulatorSuccess = false;
	}
	
    if(accumulatorSuccess)
    {
        javax.swing.JOptionPane.showMessageDialog(
                        this.getJFrame1(), 
                        "Accumulator point file was processed and inserted successfully. \n" +
                        "All analog points were added to the database" , "Import Successful",
                        javax.swing.JOptionPane.INFORMATION_MESSAGE );
        System.exit(0);
    }
    else
    {
        javax.swing.JOptionPane.showMessageDialog(
                    this.getJFrame1(), 
                    "Accumulator point import could not be completed successfully. \n" 
                    + "Please verify that you are not trying to import duplicates." , "Import FAILED",
                    javax.swing.JOptionPane.WARNING_MESSAGE );
            
            
    }
        
    return;
}
/**
 * Comment
 */
public void AccumulatorjTextField_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
    return;
}

}
