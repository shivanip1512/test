package com.cannontech.dbeditor.editor.route;

import javax.swing.JOptionPane;

import com.cannontech.database.Transaction;
import com.cannontech.database.data.route.CCURoute;
import com.cannontech.database.db.*;
import com.cannontech.dbeditor.editor.regenerate.RegenerateRoute;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.util.MessageEvent;

/**
 * This type was created in VisualAge.
 */
public class RepeaterSetupEditorPanel extends com.cannontech.common.gui.util.DataInputPanel implements com.cannontech.common.gui.util.AddRemovePanelListener, java.awt.event.ActionListener {
   private javax.swing.JButton ivjAdvancedSetupButton = null;
   private AdvancedRepeaterSetupEditorPanel advancedRepeaterSetupEditorPanel = null;
   private Object objectToEdit = null;
   private int rightListItemIndex = getRepeatersAddRemovePanel().rightListGetSelectedIndex();
   private boolean rightListDragging = false;
   private com.cannontech.common.gui.util.AddRemovePanel ivjRepeatersAddRemovePanel = null;
   private boolean addOrRemoveHasBeenDone = false;
   private boolean changeUpdated = true;
   private boolean dbRegenerate = false;

/*
  * Constructor
 WARNING: THIS METHOD WILL BE REGENERATED.
  */
public RepeaterSetupEditorPanel() {
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
   if (e.getSource() == getAdvancedSetupButton()) 
      connEtoC3(e);
   // user code begin {2}
   // user code end
}
/**
 * This method was created by Cannon Technologies Inc.
 * @param newEvent java.util.EventObject
 */
public void addButtonAction_actionPerformed(java.util.EventObject newEvent) {
   // user code begin {1}
   // user code end
   if (newEvent.getSource() == getRepeatersAddRemovePanel()) 
      connEtoC4(newEvent);
   // user code begin {2}
   
   // user code end
}
/**
 * Comment
 */
public void advancedSetupButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {

  

   java.awt.Frame owner = com.cannontech.common.util.CtiUtilities.getParentFrame(this);
   
   //This makes sure that the user applies their changes before bringing up the advanced setup dialogue
   
   StringBuffer message = new StringBuffer("Advanced Setup may not accurately reflect current status \n" + 
   											"unless your latest changes are applied.  Do you want to \n" + 
   											"permanently apply your changes now?");
   if(addOrRemoveHasBeenDone && !changeUpdated)
   	{
   		int optional = javax.swing.JOptionPane.showInternalConfirmDialog(this, message, 
                                            "Changes not applied.",
                                            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        addOrRemoveHasBeenDone = false;
        if(optional == JOptionPane.YES_OPTION)
        {
			/*start of the chain that leads eventually out to the DatabaseEditor class
			 * in order to simulate an apply button click so that the db is updated
			 */
		
			fireInputDataPanelEvent( new com.cannontech.common.gui.util.DataInputPanelEvent(this, com.cannontech.common.gui.util.DataInputPanelEvent.EVENT_FORCE_APPLY));
        }
       else {
       	dbRegenerate = false;
       }
           	       	
   	}
   
   	
   getValue(this.objectToEdit);
   getAdvancedRepeaterSetupEditorPanel().setValue(this.objectToEdit);
   	
   
   com.cannontech.common.gui.util.BooleanDialog b = new com.cannontech.common.gui.util.BooleanDialog(getAdvancedRepeaterSetupEditorPanel(), owner);
   b.yesButtonSetText("Ok");
   b.noButtonSetText("Cancel");
   b.setTitle("Advanced Repeater Setup");
   b.setLocationRelativeTo(this);
   b.setSize( new java.awt.Dimension(445, 485) );

   if ( b.getValue() )
   {
      getAdvancedRepeaterSetupEditorPanel().getValue(this.objectToEdit);
      fireInputUpdate();
      setValue(this.objectToEdit);
   }
   
}
/**
 * connEtoC1:  (RepeatersAddRemovePanel.addRemovePanel.rightListMouse_mousePressed(java.util.EventObject) --> RepeaterSetupEditorPanel.repeatersAddRemovePanel_RightListMouse_mousePressed(Ljava.util.EventObject;)V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.util.EventObject arg1) {
   try {
      // user code begin {1}
      // user code end
      this.repeatersAddRemovePanel_RightListMouse_mousePressed(arg1);
      // user code begin {2}
      // user code end
   } catch (java.lang.Throwable ivjExc) {
      // user code begin {3}
      // user code end
      handleException(ivjExc);
   }
}
/**
 * connEtoC2:  (RepeatersAddRemovePanel.addRemovePanel.rightListMouse_mouseReleased(java.util.EventObject) --> RepeaterSetupEditorPanel.repeatersAddRemovePanel_RightListMouse_mouseReleased(Ljava.util.EventObject;)V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.util.EventObject arg1) {
   try {
      // user code begin {1}
      // user code end
      this.repeatersAddRemovePanel_RightListMouse_mouseReleased(arg1);
      // user code begin {2}
      // user code end
   } catch (java.lang.Throwable ivjExc) {
      // user code begin {3}
      // user code end
      handleException(ivjExc);
   }
}
/**
 * connEtoC3:  (AdvancedSetupButton.action.actionPerformed(java.awt.event.ActionEvent) --> RepeaterSetupEditorPanel.advancedSetupButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
   try {
      // user code begin {1}
      // user code end
      this.advancedSetupButton_ActionPerformed(arg1);
      // user code begin {2}
      // user code end
   } catch (java.lang.Throwable ivjExc) {
      // user code begin {3}
      // user code end
      handleException(ivjExc);
   }
}
/**
 * connEtoC4:  (AddRemovePanel.addRemovePanel.addButtonAction_actionPerformed(java.util.EventObject) --> RepeaterSetupEditorPanel.fireInputUpdate()V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.util.EventObject arg1) {
   try {
      // user code begin {1}
      // user code end
      this.fireInputUpdate();
      // user code begin {2}
      if( getRepeatersAddRemovePanel().rightListGetModel().getSize() > 0 )
         getAdvancedSetupButton().setEnabled(true);
      addOrRemoveHasBeenDone = true;
      changeUpdated = false;
      dbRegenerate = true;
   
      // user code end
   } catch (java.lang.Throwable ivjExc) {
      // user code begin {3}
      // user code end
      handleException(ivjExc);
   }
}
/**
 * connEtoC5:  (AddRemovePanel.addRemovePanel.removeButtonAction_actionPerformed(java.util.EventObject) --> RepeaterSetupEditorPanel.fireInputUpdate()V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.util.EventObject arg1) {
   try {
      // user code begin {1}
      // user code end
      this.fireInputUpdate();
      // user code begin {2}
      if( getRepeatersAddRemovePanel().rightListGetModel().getSize() == 0 )
         getAdvancedSetupButton().setEnabled(false);
      /*
     	This forces the Advanced Repeater Setup panel to renew  
     	so it is sure to exclude the newly removed repeaters.
      */
      else
      	setAdvancedRepeaterSetupEditorPanel(null);
      	
      addOrRemoveHasBeenDone = true;
      changeUpdated = false;
      dbRegenerate = true;
     
      // user code end
   } catch (java.lang.Throwable ivjExc) {
      // user code begin {3}
      // user code end
      handleException(ivjExc);
   }
}
/**
 * connEtoC6:  (RepeatersAddRemovePanel.addRemovePanel.rightListMouse_mouseExited(java.util.EventObject) --> RepeaterSetupEditorPanel.repeatersAddRemovePanel_RightListMouse_mouseExited(Ljava.util.EventObject;)V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(java.util.EventObject arg1) {
   try {
      // user code begin {1}
      // user code end
      this.repeatersAddRemovePanel_RightListMouse_mouseExited(arg1);
      // user code begin {2}
      // user code end
   } catch (java.lang.Throwable ivjExc) {
      // user code begin {3}
      // user code end
      handleException(ivjExc);
   }
}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.database.db.setup.gui.route.AdvancedRepeaterSetupEditorPanel
 */
protected AdvancedRepeaterSetupEditorPanel getAdvancedRepeaterSetupEditorPanel() {
   if( advancedRepeaterSetupEditorPanel == null )
      advancedRepeaterSetupEditorPanel = new AdvancedRepeaterSetupEditorPanel();
      
   return advancedRepeaterSetupEditorPanel;
}
/**
 * Return the AdvancedSetupButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getAdvancedSetupButton() {
   if (ivjAdvancedSetupButton == null) {
      try {
         ivjAdvancedSetupButton = new javax.swing.JButton();
         ivjAdvancedSetupButton.setName("AdvancedSetupButton");
         ivjAdvancedSetupButton.setText("Advanced Setup...");
         ivjAdvancedSetupButton.setMaximumSize(new java.awt.Dimension(159, 27));
         ivjAdvancedSetupButton.setActionCommand("Advanced Setup >>");
         ivjAdvancedSetupButton.setPreferredSize(new java.awt.Dimension(159, 27));
         ivjAdvancedSetupButton.setFont(new java.awt.Font("dialog", 0, 14));
         ivjAdvancedSetupButton.setMinimumSize(new java.awt.Dimension(159, 27));
         ivjAdvancedSetupButton.setEnabled(false);
         // user code begin {1}
         // user code end
      } catch (java.lang.Throwable ivjExc) {
         // user code begin {2}
         // user code end
         handleException(ivjExc);
      }
   }
   return ivjAdvancedSetupButton;
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
   D0CB838494G88G88G03CB5BACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E155BB8BF4D4C5166C9E464111E1F1C5E5B93843A82E4C8E6BB0A34E30A31EC507F5D118115538B2BBB89385B4E8543862009831525DC9BA64CFC292641308C192931B040F04CE8889E910E85088998CB32C61A57D1234F477EBDE3F10C6D1765E2AD755AF1D57796068B91BF3EE5E2BDB376AFE6A5E2ADBD52FC9FE713464A4CBBEA1C949C47F5B1FC4485486C2BE3EA771990E0B7DC3130D987E5EG48A5
   FF5A064884281BDE6C3265104B95F6A85BE21BECB11BDD36C2F84FA3E3AB665742CBB4BED0A75FFF63EF0766D37D956713CA4BAF78B2601985E087DE15GA1009C1266AF52712DD04E525F9112AC04D4BD0EB42D75C5867CDC1D4E86F5C5GDB1EC01AD27F963DBE843809GCBG629F57E901A60DCA779DE70DCEF7665AC9E455836916CC64E5681F037B8956F61C7EACA157D1C3D8C9EC4781F856351E6B296FD2FC0EAE4D63F0F7BA02325697F07474F99C2A522749CE6789B9A0CB1A2C1E440AA3EE0F26282D
   12DF76C25BB5D4668B75F65A9FA17320BCF699621E2BC7BB5541FB9B00F799177BBFB36BF439A61D1BC4E2EFD91794CD6E854B183C714B42F2DF6AF94DBAF5B976FF5C1F21372B04329B0069346E20F300CE125F2F78B5392B57A1D34907D70A69B44D4FF50ADD4EF55AD403BC0BD6B4592AG0E2D6078D80ABF8B6590404495DC5703BB392E6DDF5CC256F4BAAC59D1F41D31026938F8C5D857529D632D1B284F5DFD997DA663C9608B608268859887B083E0B3757550D7EF433331D3EBD2FCBE455F98889C763A
   55630ADB76423B5389B5BA6E045C2528EEC218BFFFD1DFE9A28F07F9CF8E74E584265BA7047B5F4C93D3082615DA12CDF41B7DC826A5C66FA36E4930EEB5CF215D3EF7886DE60777A42A83A18CBF1562AF9970EC4CD7D11F4272CC28BB96476D5C1E45E5E931CDA0B955D516D493D9A65F3B4332BC0E491098971605597846A434F159CA30AF4099G8DE092406C155C4677FE35E7B89BB7032F29FE49FB322FB3A0295A61BE0F57AD2B3A2DCF7A0732359930F652285BB13B4C58E39C2325F76CB2354B52997916
   45AB75B1DA99368B63FDEBD45E7627877BC61698436F53185872B49BC32B405FC8711586BC9B5BE20A47F29D54CD83E0746D8790EFFB0651E70AFEFAA83E4C00E77DBCC97B41F28B544D81E0633B4F0A73EAE9FC132D81209DA0GB091E0A6403CF8BE461917F70DB00EDA8D634B5A5EF739G1EF2C8D325E6FFD0137CDD72A9C975C81DDEB9C8F41A87AF23DDBB87340B21715D85BC5E137D32AAE99E458F3DCBBE991CA948E89E3B1C0BB42ACCEB5BBCBE9998788204773B62B24605D78AEAFF8A38E1E28BD330
   7AE7AF23CFDC10CEC10F50813C67BC0B767AFBBAF747027E09715CD61DB64435C13D77D9EE17DE32811E9F00F34BDE27536569C2B1A475A277510907CA61B98668EB1EEB32A5G0CFB3E49D61250E4DB83307885D8277F87E26BC59E334F35F35FF4E57E8329FC44E16A1B884BDF54E37645C85F3C1972ACDD857D8238814682CC87188F309C4076D2132D74A53EF62F2D1FE86897E5C4D9DDAF067D98E50E644FF5C079C79AFF282F999E6DE39E63EF9C1A9801CBA6EC2EB346F4E45BF45D57F3C7A897C2F6943C
   5584477CE1F1B246148B6CF896E04CCB5CA7EE01312031F2D88A4ABCBABED24131E5F758C3986D34A464ABDF107D1A8B16DE3F9BDE023AEF5E1614960E1BEEC578743FD0BC4E23CD6058C1013FA3A91761218FE394727A3BE83D24E92A279396FBD862B50F3FA75C7ED7D4166000A9BA286B7AD08A6ABA99F41C85B0CF685AE249B378FF0B221C6B8B34E1E46BF1B417668BB1C09FAF7C6D3A9A4C9DF37E484BB751F238FEC7CD3B094D11FEB4F37D50EBC73DA98F33313EF29E1F9EA8527DB5D577D33459D65DBF
   990D0F526C855D4EG0C851889908310A86C93DFF3565037313F74E164607CC6AEF36A81A3AF2B01DF66B06DC7A20FD12EBCF2489A5D770FDA4DFD3F45EA6E7BA72CC37B761F2DE63E4D6AA4AB6A28747B8D3152ED45F93DCB717BE59A302289E17974F3ED2807476F51BE7138FBE46D2467F3995656556873BF22EBC7A93C572C6673FA7F1BFCDEAF7D7CFAB22FA34DB4AF1CFCEB19E562EA666753D70767F5A6D30F8DE3D0764ABEB0D413526757985EC757A59D9F34D2DC6F36FF4636E50D6E8B385E3AE9BEFF
   38CF5394FF25C4B5F230C72B2CF6AB2ACFF617FDA6DD109CD23F6620A362E82455C7703D3C641DF76ACB22ED8DAA2541BCBA9AA3BAEF095EFBED792972AA66EBF9445E707FCA6E5DDC6E85B95F5365F61F10FD4A851933B0969D2A2727D7EB7184E1747C1A6481076B005DD8C071C3F75012AB5327F93C8E4A63234E4F407F0651235B234A4D7EC01F1E621401DCFB38DCCD99570CC4AE7A4ECC40B46CB035725797EC2FAED70273FEAA97EF9B9C21610EABFDC13943077FDBD5B9983CC249D4F3369DA7066053E2
   4A883D20FA8F61767EF96B422F2B578908E4695BD0CCB09AC2B3BEDE6FB65C77F575BA9272E85F02D6BA1B68BA6D63E31518F36DE8F402623791A1E1DF2A7EE85228EC7E6D85B14B89136D389E4D07F5EA89C042EA1E932E247BA51E936A09EF1362753225946A5A5660BA793BC37906354F3E061D9BE6097A5486337A2970E4750BBA4C6AE7097A63E74D6A670B7A5F1856AF91759FC9E6757122FE31E9BDF91957FF5CE9D61FAF6A9FB22D8F67C77F151C03B90BFC3ECF06E58A136A8F640BFC5F1B3CA1226E14
   646D8B6F27D7A4E3AE66B564638C3FA445BC0778FD8A666067C47FCF2424905C9733BED94EB3CB42BE832A47875B975CE06867378F7B0D6737E5209F1E751E052776B2BFFFCCB902B4511C2CEDAD76F34B81FE46BB9E5ACD87182F77E5DB2B7B85256BF1F23AE9D01E8330F4AD232BFA45C8D7D54B69DADFE1F43D2F18513DAC68666AF4B1BADDE922116E1EBACEB7A61151CDCEE4F4F183682EBE4869DC2FB23AB32FB23A992FB2BA328E6966097361DD8277A845BD8F0311E96567D547F30CE75A6E609B2639A3
   65370E755F3EAE729C4F19067D06GBFEE9D0F53B329463DDBE3B028F4F968B10A6EAB69F6E3DE49F0B976757004B9A128B5C3429E627ED6E0C7D9711CC5C76BBE73630A03E47039919BFA3EB5EE6A2733D11B52243865E36032CDA26DBE65117B89374317C70DE764F3338BAC69D16CB0D557FF2E418EE7F70C3732B36C3F6D60F6CF04FA06331E65383D2FF15CD801738ADC3244F153856EAA01DBAAF0291D9C17CFDCBAEE1C4035G0E59A735834F914D4E6571268127EA2753A5E9921D01E92C303E8EAAF65D
   46E321AFF65E6ED1B13667C2B986A09E001D7DAD22E702C8EBCBF2596AG587C5318E21CFFB440639EFCAABC597A33478969AD99FD564D477E295A43587EEBE759FC7CEF4EEB50E5DDF47B84D2F7F9F512593D41386CA44BAC50815F1768CF9C7F38167156C4GE61309CEEE13D20B4BF686004DE5A5C163DCB64942EC32881E3196EE17554EA3D42F3FC2F6A062E4FE798696D791EBADBF3FBDDD4B65DD0332DA39AC73EA0F8D16650AB38146EBFB9D67558BFC2689DED757B51A709AF5E6AEF4FE297EB8BC7F2A
   29B86176FADA683084644F31F19DBEBEF8F8E4BA8C1305F39D56F6FFB8CA9D064F3A398EBFBB48F5B886720F91BAEC2AEF98198EC3E65CCCFE656D16D14ABFDCFE2D5F036DB62ED973E2B7C6DD336658D80CAE3239A2562CED69B8474581BE91002DB3575AF38DEB5689E0A979FBA03B91E7CAE9E8A348E374F2D39A7225F98C616736FF4940F9AFA759E52B84F0811C8138144CF9BC10B66CFD5611509077D95F6D984D3D54D4604B5A594F0E265DAC516E6AD1351BAF5A2D17C653EE09E8F74D285A450BF6991D
   23E9C7D2F83B6F0F2ADD3EE8F7D809ECD7166292E748F506F78E6C8E6DB13A0E36C2BD7AEB324047D13C5B00E7F12A28BCCEC3D037D470FF68C0A47F065401BC4FC414D93B58C16D36C51447C434E3311CFD927D1B6692981CA1C7305F636FF1BA5FE7AB8161F7782194BBE1F7A313219F9693BF4D441CB5914AC5F6D8DFGDAG82G6381E659F9ECECDAEF164F8D0C0DE849DC78BE0269F903734A6CB3FDD0BBC7C4F9315D4CBE27EC4367DA83A7C0E62F76A04F5B626CBC571AC06FFECA52DC369A0056B4BE66
   9D35D72613EFD03BC81DFA534C5A1555DD993FF787359B64E3267C922E105F3AE179CDB665B7676015713BEFD03BC8FE33A27831B8F8A18B47FA8154ADC963BEFFE1BD5EFD70B527D57178B542571BB753F11F2C406A6B71F3DC9945F5A9AA3DD0E0382E0C68F79D1FE5185FF584B24C6FBAAEE48CFD4F77D5C6343B90F6A7770FF47F93877A5956C3EEC4F145E7B92EC8602689DC25403DA1F1DC03405DACF0ED8277E6A747758A5CF4013BA4F02769BEE6A214278B5C3B676998C0B9F6BD0F2BE5AA7D0EAD5DE5
   ABCB673867A88E7757F8D640F0FD2A593D5ED0007D8DC7E353E9980F89236611BC829AFE7738153E5FDF10CD4798525DA01DD0F1932857BD25EDA4A6F7D39FCABE19703343B4E71ACEF3C4D31A7688593FCCB1137D182E5F997D99083049D8A8CF0E404D0472BC009883DE3FE74E0C6E6B77E61A7B7A02CC0C31205227F649117E7CE0E611288B2FA64D1A6CC31575F3AD5ADE13549EF9D0BC7C0136E7F5515A47E5C60B99E64FDF8F3067DEE14F7F8D8E151B441978964BA5AC749CAEB11D6592C9821FCE71760C
   B01E6598131279F7FD79D0974A60FB019BEA0DF26D9372F654997155823F7320913FDB601F9F00FFD7586E0FD948B3A44AEBE979A261EB2A5C18A5ECFB446F6613A3BD4B3A134EB3DD96C2EE32F05C3BF4CE395AEABC737A854561D7266F88DC8D1DD31A0049B9017BD70AEB865CC201F352B92596F039C99CF797455D81BC4A05FE818567E42FF4D16983790EE56237315FD578DEFFA958F395409640D7E532BDBF536F277DF846542DCABEF0071381390B14E6E15B6367F95B09C0BF9DE0BE40F2GDB969F1B6B
   8B70FB5BA827773ADC8CCCEEBB75397A87B4D773C21F1732787CE8CBC55CB8A8CF9338ED742CEEB614978B5CAFA8EEB914C9B6475DCA5737FCA8178ADCF7811FEFEB846E43CA4A834A53846EE77BA98FA8AF90B8AD485BAE4D967B98B4359E9F390677A949231DB7C7830C479D5FA88F960BBFD144B736B9AE1BBB071F65D51F459CDA645FF81E65F43E27FA5C07251E966AF9AC1FDCCD57BC9A5BB9BC1F5C9140BEE3209C8FE04BF5695F8D045E62E71EB37FE982E999334B74FB665836824B16DCB657B4640677
   51B9624CB3F81E733C847533F35107FFF85EF87E077E38434272CED67F7000FA1C9FF17D8957A72B46FA147755816D778F28C79BD5686D19EE8BF7F05D9297CEA55EBB37C67DD6BB116B3401BDA7363CCE485DF3AC7A1DAB3D3B50E0D1E907FAC2BA70F3C3BD3EB627E27C7A2410475767239FE944ECC0597650981A8A6FB3B670F85DE7C77E6C248C37B7125F0DE3C269BF2431D41487F1134769FFC369832A5CAD2BAA84AF7D84C427FF0A523B0036D5504F4DC2B93BE14F41E89E2231F8896A47814467A16E4B
   82172E731C3C41EB877FB61443A220C1582C57456605578D46D32E247AFB7C917AFFC963FBEFBE64F179DC3E187D98C3329F2F0B5CBA5DB37B395FF873390C933E60B20E49B71751A867A420198130D1AF4F4D8F4B48E2764EC17B7411457A2F2E30DD62285B3135F9B50D51857911EBB6B9CFE3B7BFF24D2E8870F9A2966ADC9B79DE7BF0A12E9DB320BC9FE0A940AA007C82D8FB8A781E63F2F69599E61F5D2684DAA049729A36GCC3F5079AB7BAE7A1ED49E0F33FFF98319F355E3C9516ED7DC85EC5C5D7A33
   6C0357A91E6997DB047ACD023AD9GB1G71G2490769605DC3F7F482EE47A61D5D02326C9DD3D741609AB0797BFE1B4AE3CCC46877FE2BCD74C1912ECC90AA2E3E5A1132D26B0BC9F32790126B2FAFC64590B6169177BCB4495044B83AC71E360326AFD87AACBF66FAF576DB32D5F483BE67D6628E71ADE1D67987D1978656B04717EECCB1877CF6C3876EF670BFF89DD7DCB6A6B376B34D33606E927E40DC4CE56AE634DF03BD5795BA35B9D058928EC3B5BA3F9151E6D15A5D515A555FB4B7959506D857CDEE9
   8E48DECCD8FEE2A761BF4663A8FEE22C5B65718C6EB7DBFE7CF7447568E3C9EFC631CB02EE0F062270182C6A6A3130BEE76D884BFD5B7E11684BE8ADB5A8EF37648D627C4B747837AF389E21A2F3BD226B7670DB784EE26955C078BD65FC783DBA704D67B26FFF23BCD89CFED098FD2FF62250FC2F56DEE8FEAE71E961505FE0F697C67F8653D7E87C86134DA3376F447CE1960C5FA20084G5BA618C3B671B96415C274ED7C6541607963083B87770A78EB830D73DFD994DD57042261FF2370F2915EFDF7AA101E
   7B30FF0EFF2D8847CBD3829449F036CDE8A3FAA943C8F57C7ACDB41F173BA91A6939300C087AA91BB088CEC91F195E143F8F3140AEDA5A249EE127BB4B69DA8EF6E99536B93239A2DABF00ECD515002CEA979916FEB942736782635A91007E46EE9A7AFCC4BFB7FB9BF30249C0BB9BE089C082006DF558D381D48138G5A8182G63G26824C82188F908390871078BA177F1A5272A1645713EABA402A44361CCC7E4FAF99654F598C795A662165E77CFE3EB33AEF5C3DF3F85F38EFA77DD68246346DE2C0BFD3D8
   38131E8DF6F787E561030BF76688DCF330D589FAB05BE4F53FAC4FB2468F9B963D5DBD65988B783DB14C5B1E4F69E79AA701505F4376E18F50F93BE6B34BC1986EC10AEB55F12C1F640A68FAE6D48C2F67468A739858DCB1B886CAAA4CE3602D8AE38CB0DCC58546FF8F0CEB3B202B92385382370F760938CFBCEE2DD760EB29DC08BFA6A3C30EEF24F275D3DCBB7F365C00BB4D67418A9CABD039D791E7362D95B4DF77F83D2C5CC6E9BCFE98BAE10F53D4C6CFC0B2487D29401D96E3F16DA5BEBE7B2C9CF743A5
   BEBE3596FE1EA23D15919EE72AA243EF89E50671DCC542FE4FDB3465DE68F36633A4D9A339833389074391D4490641EBE863045FBEBA334315EFA116E61252CBBBECA6497D48B144FFBE7503C17DC1F692BCCF3EA25C21FEG3DE78E48AC7E7D077B1EDC747BD3C1ABC9E9A61BF033F3D2D6294F4AEEBC4354FFE8F5142485702C8837A44D4496240F6420F651AB37107C6E9378E1DBD0135D0DBC9A029A59C86F1CF761C54D2EE3D230D7C34F2CEDA1B6BF7EA40CECE055DE495F330B05C6AD55A42971AC4E2FB4
   5889481B973E39963D645D153D630D77174F31924BD1325E2BC86E23308FD454E61226752AF230D7713AC301C38D34A3FBBF0E9B6646D668EE33D09F15F534C17C88214F05EE9A1F9917F99406B72412DD149277298EC161EE6A15D4E0A52B4D9A7E44CDD1CBC55D5FEC535E7A75FB05E7ACA4C9D792CD1F9A8AE84CE040BC1BF609AABB0E02545AFFBFFB7F4D3F2BCB0807A6C7C9B23D768646B9E12A8F7BFC1D32FA554C6B3726CF0A3D86658A20F9682026EBA425DB72F93C97610565079C1F86F302BF085F0E
   G5312880DF915905CF15D7DD5577FE86D1F296061269AA9E6C384CDF775480AEFD7E3F73747AF0B8EE03C207B2D22FB24F1C450EC88DD3B717DC7DE6B847FF2912B2F2A0A7E2F267FF7637FEA0A2926186A5DB0E889E4483F487AD088F54E8F896B22A44DB468E3AF6CF1DA175DE235909B848D1B8441F9F1A68367D5D98CD9E19224FEE77CDB9F91B5D85AB2D89616CF17DFB66EB356FCA7B1A95ABEA32ED85FDF94C7FEBB71C9B11DEFCADC3636923E265E368D4FD7236596A7FD12572BC95EF30DFEB7669907
   AFEAF2B8F7BBD30C7D87B90DDE295FED95235C1F995A7C9FD0CB8788CEB0FAAD4E95GG44C0GGD0CB818294G94G88G88G03CB5BACCEB0FAAD4E95GG44C0GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG8896GGGG
**end of data**/
}
/**
 * Return the AddRemovePanel property value.
 * @return com.cannontech.common.gui.util.AddRemovePanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.common.gui.util.AddRemovePanel getRepeatersAddRemovePanel() {
   if (ivjRepeatersAddRemovePanel == null) {
      try {
         ivjRepeatersAddRemovePanel = new com.cannontech.common.gui.util.AddRemovePanel();
         ivjRepeatersAddRemovePanel.setName("RepeatersAddRemovePanel");
         // user code begin {1}
         ivjRepeatersAddRemovePanel.setMode(com.cannontech.common.gui.util.AddRemovePanel.TRANSFER_MODE);
         ivjRepeatersAddRemovePanel.setRightListMax( new Integer(7) );
         // user code end
      } catch (java.lang.Throwable ivjExc) {
         // user code begin {2}
         // user code end
         handleException(ivjExc);
      }
   }
   return ivjRepeatersAddRemovePanel;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param val java.lang.Object
 */
public Object getValue(Object val) {

   com.cannontech.database.data.route.CCURoute route = (com.cannontech.database.data.route.CCURoute) val;
   
   //Build up an assigned repeaterRoute Vector
   java.util.Vector repeaterRoute = new java.util.Vector( getRepeatersAddRemovePanel().rightListGetModel().getSize() );
   Integer deviceID = null;
   for( int i = 0; i < getRepeatersAddRemovePanel().rightListGetModel().getSize(); i++ )
   {
      deviceID = new Integer(((com.cannontech.database.data.lite.LiteYukonPAObject)getRepeatersAddRemovePanel().rightListGetModel().getElementAt(i)).getYukonID());

      com.cannontech.database.db.route.RepeaterRoute rr = new com.cannontech.database.db.route.RepeaterRoute(
                                                                           route.getRouteID(),
                                                                           deviceID,
                                                                           new Integer(7),
                                                                           new Integer(i+1) );
      
      repeaterRoute.addElement(rr);
   }

   if ( !route.getRepeaterVector().isEmpty() )
   {
      for( int i = 0; i < repeaterRoute.size(); i++ )
      {
         for ( int j = 0; j < route.getRepeaterVector().size(); j++ )
         {
            if ( ((com.cannontech.database.db.route.RepeaterRoute)route.getRepeaterVector().elementAt(j)).getDeviceID().
               equals(((com.cannontech.database.db.route.RepeaterRoute)repeaterRoute.elementAt(i)).getDeviceID()) )
            {
               ((com.cannontech.database.db.route.RepeaterRoute)repeaterRoute.elementAt(i)).setVariableBits(
                     ((com.cannontech.database.db.route.RepeaterRoute)route.getRepeaterVector().elementAt(j)).getVariableBits() );
               break;
            }
         }
      }
   }

	
   
   route.setRepeaterVector(repeaterRoute);
   if (dbRegenerate) {
  String userLocked = route.getCarrierRoute().getUserLocked();
   if (userLocked.equalsIgnoreCase("N")) {
   	
   	java.util.Vector routes = RegenerateRoute.resetRptSettings(RegenerateRoute.getAllCarrierRoutes(), false, route, true);
	val = (CCURoute)routes.elementAt(0);
	this.objectToEdit = val;
   }
   dbRegenerate = false;
   }
   
   
	
	changeUpdated = true;
	return val;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) {

   /* Uncomment the following lines to print uncaught exceptions to stdout */
   // com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
   // exception.printStackTrace(System.out);
}
/**
 * Initializes connections
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
   // user code begin {1}
   // user code end
   getAdvancedSetupButton().addActionListener(this);
   getRepeatersAddRemovePanel().addAddRemovePanelListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
   try {
      // user code begin {1}
      // user code end
      setName("RepeaterSetupEditorPanel");
      setLayout(new java.awt.GridBagLayout());
      setSize(382, 274);

      java.awt.GridBagConstraints constraintsRepeatersAddRemovePanel = new java.awt.GridBagConstraints();
      constraintsRepeatersAddRemovePanel.gridx = 0; constraintsRepeatersAddRemovePanel.gridy = 0;
      constraintsRepeatersAddRemovePanel.fill = java.awt.GridBagConstraints.BOTH;
      add(getRepeatersAddRemovePanel(), constraintsRepeatersAddRemovePanel);

      java.awt.GridBagConstraints constraintsAdvancedSetupButton = new java.awt.GridBagConstraints();
      constraintsAdvancedSetupButton.gridx = 0; constraintsAdvancedSetupButton.gridy = 1;
      constraintsAdvancedSetupButton.anchor = java.awt.GridBagConstraints.EAST;
      constraintsAdvancedSetupButton.insets = new java.awt.Insets(10, 0, 0, 0);
      add(getAdvancedSetupButton(), constraintsAdvancedSetupButton);
      initConnections();
   } catch (java.lang.Throwable ivjExc) {
      handleException(ivjExc);
   }
   // user code begin {2}
   // user code end
}
/**
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void leftListListSelection_valueChanged(java.util.EventObject newEvent) {
   // user code begin {1}
   // user code end
   // user code begin {2}
   // user code end
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
   try {
      java.awt.Frame frame;
      try {
         Class aFrameClass = Class.forName("com.ibm.uvm.abt.edit.TestFrame");
         frame = (java.awt.Frame)aFrameClass.newInstance();
      } catch (java.lang.Throwable ivjExc) {
         frame = new java.awt.Frame();
      }
      RepeaterSetupEditorPanel aRepeaterSetupEditorPanel;
      aRepeaterSetupEditorPanel = new RepeaterSetupEditorPanel();
      frame.add("Center", aRepeaterSetupEditorPanel);
      frame.setSize(aRepeaterSetupEditorPanel.getSize());
      frame.setVisible(true);
   } catch (Throwable exception) {
      System.err.println("Exception occurred in main() of com.cannontech.common.gui.util.DataInputPanel");
      exception.printStackTrace(System.out);
   }
}
/**
 * This method was created by Cannon Technologies Inc.
 * @param newEvent java.util.EventObject
 */
public void removeButtonAction_actionPerformed(java.util.EventObject newEvent) {
   // user code begin {1}
   // user code end
   if (newEvent.getSource() == getRepeatersAddRemovePanel()) 
      connEtoC5(newEvent);
   // user code begin {2}
   // user code end
}
/**
 * Comment
 */
public void repeatersAddRemovePanel_RightListMouse_mouseExited(java.util.EventObject newEvent) {
   rightListItemIndex = -1;
   rightListDragging = false;

   return;
}
/**
 * Comment
 */
public void repeatersAddRemovePanel_RightListMouse_mousePressed(java.util.EventObject newEvent) {
   rightListItemIndex = getRepeatersAddRemovePanel().rightListGetSelectedIndex();
   rightListDragging = true;

   return;
}
/**
 * Comment
 */
public void repeatersAddRemovePanel_RightListMouse_mouseReleased(java.util.EventObject newEvent) {
   int indexSelected = getRepeatersAddRemovePanel().rightListGetSelectedIndex();

   if ( rightListDragging &&  indexSelected != -1 && indexSelected != rightListItemIndex )
   {

      Object itemSelected = new Object();
      java.util.Vector destItems = new java.util.Vector( getRepeatersAddRemovePanel().rightListGetModel().getSize() + 1 );

      for( int i = 0; i < getRepeatersAddRemovePanel().rightListGetModel().getSize(); i++ )
         destItems.addElement( getRepeatersAddRemovePanel().rightListGetModel().getElementAt(i) );

      itemSelected = destItems.elementAt( rightListItemIndex );
      destItems.removeElementAt( rightListItemIndex );
      destItems.insertElementAt( itemSelected, indexSelected );
      getRepeatersAddRemovePanel().rightListSetListData(destItems);

      getRepeatersAddRemovePanel().revalidate();
      getRepeatersAddRemovePanel().repaint();

      // reset the values
      rightListItemIndex = -1;
      fireInputUpdate();
   }

   rightListDragging = false;

   return;
}
/**
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void rightListListSelection_valueChanged(java.util.EventObject newEvent) {
   // user code begin {1}
   // user code end
   // user code begin {2}
   // user code end
}
/**
 * This method was created by Cannon Technologies Inc.
 * @param newEvent java.util.EventObject
 */
public void rightListMouse_mouseClicked(java.util.EventObject newEvent) {
   // user code begin {1}
   // user code end
   // user code begin {2}
   // user code end
}
/**
 * This method was created by Cannon Technologies Inc.
 * @param newEvent java.util.EventObject
 */
public void rightListMouse_mouseEntered(java.util.EventObject newEvent) {
   // user code begin {1}
   // user code end
   // user code begin {2}
   // user code end
}
/**
 * This method was created by Cannon Technologies Inc.
 * @param newEvent java.util.EventObject
 */
public void rightListMouse_mouseExited(java.util.EventObject newEvent) {
   // user code begin {1}
   // user code end
   if (newEvent.getSource() == getRepeatersAddRemovePanel()) 
      connEtoC6(newEvent);
   // user code begin {2}
   // user code end
}
/**
 * This method was created by Cannon Technologies Inc.
 * @param newEvent java.util.EventObject
 */
public void rightListMouse_mousePressed(java.util.EventObject newEvent) {
   // user code begin {1}
   // user code end
   if (newEvent.getSource() == getRepeatersAddRemovePanel()) 
      connEtoC1(newEvent);
   // user code begin {2}
   // user code end
}
/**
 * This method was created by Cannon Technologies Inc.
 * @param newEvent java.util.EventObject
 */
public void rightListMouse_mouseReleased(java.util.EventObject newEvent) {
   // user code begin {1}
   // user code end
   if (newEvent.getSource() == getRepeatersAddRemovePanel()) 
      connEtoC2(newEvent);
   // user code begin {2}
   // user code end
}
/**
 * This method was created by Cannon Technologies Inc.
 * @param newEvent java.util.EventObject
 */
public void rightListMouseMotion_mouseDragged(java.util.EventObject newEvent) {
   // user code begin {1}
   // user code end
   // user code begin {2}
   // user code end
}
/**
 * This method was created in VisualAge.
 * @param newValue com.cannontech.database.db.setup.gui.route.AdvancedRepeaterSetupEditorPanel
 */
protected void setAdvancedRepeaterSetupEditorPanel(AdvancedRepeaterSetupEditorPanel newValue) {
   this.advancedRepeaterSetupEditorPanel = newValue;
}
/**
 * This method was created in VisualAge.
 * @param val java.lang.Object
 */
public void setValue(Object val) {
   this.objectToEdit = val;
   
   com.cannontech.database.data.route.CCURoute route = (com.cannontech.database.data.route.CCURoute) val;

   java.util.Vector repeaterRoutes = route.getRepeaterVector();
   java.util.Vector assignedRepeaters = new java.util.Vector();
   java.util.Vector availableRepeaters = new java.util.Vector();
   com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
   synchronized(cache)
   {
      java.util.List devices = cache.getAllDevices();
      com.cannontech.database.data.lite.LiteYukonPAObject liteDevice = null;
      int repeaterRouteDeviceID;
      for(int i=0;i<repeaterRoutes.size();i++)
      {
         for(int j=0;j<devices.size();j++)
         {
            liteDevice = (com.cannontech.database.data.lite.LiteYukonPAObject)devices.get(j);
            if( com.cannontech.database.data.device.DeviceTypesFuncs.isRepeater(liteDevice.getType()) )
            {
               repeaterRouteDeviceID = ((com.cannontech.database.db.route.RepeaterRoute)repeaterRoutes.get(i)).getDeviceID().intValue();
               if( repeaterRouteDeviceID == liteDevice.getYukonID() )
               {
                  assignedRepeaters.addElement(liteDevice);
                  break;
               }
            }
         }
      }
      boolean alreadyAssigned = false;
      for(int i=0;i<devices.size();i++)
      {
         alreadyAssigned = false;
         liteDevice = (com.cannontech.database.data.lite.LiteYukonPAObject)devices.get(i);
         if( com.cannontech.database.data.device.DeviceTypesFuncs.isRepeater(liteDevice.getType()) )
         {
            for(int j=0;j<assignedRepeaters.size();j++)
            {
               if( ((com.cannontech.database.data.lite.LiteYukonPAObject)assignedRepeaters.get(j)).getYukonID() ==
                     liteDevice.getYukonID() )
                  alreadyAssigned = true;
            }
            if( !alreadyAssigned )
            {
               availableRepeaters.addElement(liteDevice);
            }
         }
      }
   }

   getRepeatersAddRemovePanel().rightListSetListData( assignedRepeaters );
   getRepeatersAddRemovePanel().leftListSetListData( availableRepeaters );
   
   if( assignedRepeaters.size() > 0 )
      getAdvancedSetupButton().setEnabled(true);
}
}