package com.cannontech.dbeditor.wizard.device.lmprogram;
/**
 * Insert the type's description here.
 * Creation date: (8/1/2002 4:22:55 PM)
 * @author: 
 */

import com.cannontech.database.db.device.lm.LMProgramDirectGear;

public class LatchingGearPanel extends GenericGearPanel {
    private javax.swing.JComboBox ivjJComboBoxControlStartState = null;
    private com.klg.jclass.field.JCSpinField ivjJCSpinFieldPercentReduction = null;
    private javax.swing.JLabel ivjJLabelControlStartState = null;
    private javax.swing.JLabel ivjJLabelPercentReduction = null;

    /**
     * LatchingGearPanel constructor comment.
     */
    public LatchingGearPanel() {
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
        if (e.getSource() == getJComboBoxControlStartState()) 
            connEtoC9(e);
        // user code end

        // user code begin {2}
        // user code end
    }

    /**
     * connEtoC9:  (JComboBoxLatchCommand.action.actionPerformed(java.awt.event.ActionEvent) --> DirectModifyGearPanel.fireInputUpdate()V)
     * @param arg1 java.awt.event.ActionEvent
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private void connEtoC9(java.awt.event.ActionEvent arg1) {
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
     * 
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private static void getBuilderData() {
        /*V1.1
         **start of data**
	D0CB838494G88G88G4CD485ADGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BA8BF49457F5CA8FF14809EA638447F28E2929A1BE44A18EEDC9CAD2B5258ECE418803C1E0BE829403ED618843E9F14B696114372B1510B0A0814286A3434AFC02BE56874936AA14C48BC2C8D8989DA40330790D34333B03E6E7563333524A9128773EB7EFF6345A95B8ED771C3BB36F3E7B6E3B3FF75FFDEF17949E1E18ED339792E24BA64667A39BA10D6F9372E862371E62389333DDF94472A98658
	CDAE5FF640338E7A46EDF26595101AEB3950EE0376448D2E3C3761FDAF293B5672BBF809B30F937A1EF8EC4D7B234F536F6573EC236D320BBB60398B209DF0BA404C59B87FA797779B78A4E86FB25E91ECF6C264B9C8F36A66FE8B5EB10751F5994F4D14C63AD8E474CF07B65224G244F0970E4C2FE3D3E4EE8DFFBF8BC59745CF17BCE1C4B421FC363F3EC2C5BF8BA49535490D924AC779CBC9B5AFB3DFF68D17D95BD0222280AAE7678AA5C5D22DB52D52D22DF7AD2505C95EE31CF6A91ABE4FFC0D33D1A602F
	2FEF93749E1F24F84F090256AEA822CCD21FC379051B3914B7A193204DE4FDE1E703A14B785E71A4FD520138326E1F4BE4EC189B11F5F0114F1EB997F9FF1B5A9E478DC2FBBAG1BF379C5BEE7D50AABEFDA8A62749BC86B037784002494AE4B6FFF47E5697A53A41258D035ADBF0EAC1BD2188CC573A232A4FFD6396DG0D03AC89FD5985FD03G63G26832482EC82B8C8636F175ED2F83A3A75B32A5F2FAA2EC060346C56AE28EED106777AFA68B1F017449ED5F39342E24CF333BC06BC9CF6BCBFBC3EA2B1F1
	05701818FA69FBA479278F5933E368B6FD71893B5360D17BFCC4B726572DDE542B9370810031G9381E68224BF4F754A75555CCB2FD608A3CD91640ED0F7C0507453A1C9F60B1A21DF524551740B85EC5C13DFF99C334B149A2BDD527E6E3CFDDB8C3B24CD2934EFB16CD1B8BFE297B6776A38F3574E9F698F9ADF73B18EB3E89C36403B4D08D1067FD70A1FE04133987F126231BD95699670386D5A45F5E8F3BCC88A2ABF0E29C352B30DF6FD8113A7F1C134EF9B7C18DBE6GBE95A093407102AB6FA8C0A3C067
	8B5C3F9D3E4A7B0C5BF60BEF59589D52DB7094433AA634AAC1DDD0FA444B02A6895D3298A4864DBE89E56F9EB66E9D3A1EFAE00EF322A2EA02AE298AF0977CA284D210519C11F6A30DA6525ECE49AF42847EG61FC4B243D701405207EE940AD68E204027557C8E84BBE61B2F0848670BE798534799D1A33F4F89F8740ECA5A9084B00B6D9486D723434871E9FC3E00BF2FDFD135403E2885AG1FDF6A45DCA276090A5EA486C4458DAFC123AF501B97310BC7B5E56E23F8DCA3E7C07020099F6C5D4D704043EAE5
	5EBF06768B3A2EC95DA1DD6C90F59D32F1E4FCA215A5B8EC7985093146D50C31C2502B9C204154EF1F3C5722DF1B2A7606821D68B943CE7FCE73ECBB507B96727D70759B38BFA6AE64F17AFA970F53261D0F1172452DB163142E4505ACBEE7AD0CDEEB1BA94F489C89B73A3E623A0F0644053157E8A437BC763F1EA3EB14BEE677C58134FBA648123B4815F7E29137FB3B6CE46B6D349094798A3B280147C4F7A44A98ED33BFFE0CDD70470E318EFF6C983B6A9FBD0684FF2C98E2FDBEBF6EADEA3FE209C93FFF8F
	DD430AA2D2214DA104559B2FF722ADA4C5522FC8EE2F28F7C8DF0A6067C598DB0F53B558886F1D0BF8AC759F6131F4744BC7484C1BC7E36EE7C9BFB8E31F3A0879F76622C8ACB1199485EDAE4A229F8CF5C68DA9BA436BF4FD9BF8A33FF8545834320A7158AB8E98F481B507E09E11C322295BB461CD3431A67941D538E00D1AEFE73B3566CB877930BE2CCDF56535G3031FF7A947DB0322262F133C3C339EE813D8E10184A63A6D3BDE0D92FC6401CD1E519195F744B32C0817AD17CA2A4C2BA46857D3129CBFA
	E0CFD45FE5C3AF561F91C03DE4EBAEC8C53DFEE656F938D7B05CBC0A5BG8BEA0E036F3D978A2CF56A7E7C0118BEC448C8E53E5B129A3DEF39024837E8B1642B45DC771EAF2C394A958C2ABD925D968CFF1C8AB713BF373625BB1054E3F1842BB37B5BD18E542BF1B12FB3575DC55F0421BD8EE00AA95F9EEDE4FC3D2DED0727C75202D09F3945B037713B9A7244FD4BC073FA6E2455F6B3532B625AEE66E2E633544551367B060EF2EC843CE38917ADC95BED319D541E30CDFB4133DC1E66E0960BF723FDBE184D
	631D70FCB9584E6B217DCB581C65CBA2B936915EBB178C4F392C6E39BEC46B72A52C6E49B37137A9FE0C854F6A218D543E581E88FD0E97797EFCA704FA79209DFE116B35BA385322D73BAAA93A29D3220EEBD655A42F24F05C43948715B7CDE2EC0F7B8E7571D860B99901620E16A0EE8634D3CC5C8FCA29DD211D3B1463FEDD0638F2E8B7AD65FBD74F902E3DAEE8A7A09D40EC33AFC07BB6C23BE019AB2F8A20FD996FFB632EF5BF427171761659D1FD89F1E8A7DB70EC0EDDC356B9C65B3F72225A59F16836D9
	5E190D7FA2047E8C1B6D87E8FB4018EFAFF92A9DFD9650C40F28C176E2DB8219328C6D79AB6A0BG3C8F82B07AD3A5B9F42D8C28A1F0EE63F2241BD6E24D3119C0DB8878934B193EAC9F7CCDBF66B18F2CEFC8319D813107942D40314925FC6CD8201FG108C108630E5B91FF3E5990E050112BF642792B2795E207EF6831F408ACE5B306B9DF81E074A39DBBD2D064FC05251D439C3079D813EF4116B1D1C03F794675B04EED19EC944E88683EF19B46D225683196412688E99491D51BC9AB8C8676B88C8CA33A4
	4A6E910429D44F49C305969BA52DE0B55CCCF8327EAD79567E22B4565F18467B9FAA0D555FE9769F2E33767B0C7E89701C160677AADF8C92536787CA1C70D444FE67ED21CF28907A750AA6A85595AC4ACF54D61DACF75656D415309CB8315F1A836B36FF98B787EECAE37E4EDD491E7287D70DFB0E6ADBF86E21FB811F90123CA639CF8B5EB69AC9AC3FAF216B9CF9B72E64795DD1CA73BB344781CCD949EB1370F1FE0FB0754987C9DB427918F79A691D55761906BCEBD7C6F273024FAA3731B9DFAA65F3BAD739
	723AD6A12E204CEACB3459F5782A40BCC07BCF8F6B5FED0C0F746B437AD15EE34346BFB92CBF5BB21E69B6779857ED4B5C89C47EE9DD5C7B1A092B0C9A5EF89247D5DB66A1494EFCBE61EE4467736E964577F9D539332A04794AAB9F347AAADC916C0785DBDFEF2EA8E3AFDEB1447762AD2B38CF6EB4DB6F1912732B6DEF46113BF6B51337F3F544A7896093A6433FF9764716012EC4B6FFCD814E0F7C472F6677D03F263E441AB9E5B52F6D6EFEF81CD86A10DE59DBF13B872B708A8F2ED6D44CDC3A3C4E38DC47
	F52BCA673A4D7F5C3A8E8A3E67345B626866CBE7BA6969513540F2EA33A9001F154E774C1F8D16417332F05BB8511F253591BD04G32DDD38322268FB0AC2D8C0D3DE3C549D73D0BE17BFC182E273569EC1F3719782FD17CC68B1E6D7F5BCB791D5F96683B759B3E7F3FE5C39F4D02F6BA40E6000217E08F85E8FB096B36F5300A5C639E23D38D3441E1CA369C3119FE2F167EF9F7CD7F3C0D476074DFFC174CF8E033ADDE8C5EFA0979A741F8A6592F5A7049747B219D751B86FDA9G9B81F25738720AGEA57F0
	7D4E5DA9E77A21FBDC3AAE7478E83549153BAC097D91B4EEFACC3774A12EDB8B703A8690D893298D462C99D9BFB0198EB9B0EFA4C17FF400940035G0E35DC1E35F76FF9AFF8B6BC4A3DE0DD015566C701EF9DC09B40ADGFD6DC8395838392551E77731C0BB41201F96E39C4DD900DF9A551789ED7272F09C1B6373BAAEDB614BA3791DGDC83C03B250F596C7774B787E61F7221E371569A1866BC241A0B5D37219A6F943C661855C3484BBC2333A5E8743DC277F4BCCFCB028C07995C6EBB00D071B2196F6275
	33A17FB7AC6F85A36C75FF89EC0E127F57B9187E07ED716FC20E5BE25F05145BE25F051C340DFE97D2EF0BFF976232D96FC2185FBF4DC15F38A19E92GA6834CGC8F9192F1579367758DA0931EE4F3A3DA25404F8FB2A73797F569EDF571F5B6FFD677A0C9D4F1E5D2A2E2BFE644F7133697D1C2E86A81261665951C6F0D472690C5440AF32637EA10B9E0AE6E71E7F986267250C1779B9E8A5BD1BED01F661AB9CD75907389350EEB4F189FD7C8C55798A374BF1C7FCBD5FF74476E90DA336CF7F5BB17A9D2A4B
	914F67CC3E0BF40F1F8C32CDB7E51E6061FA4CB6F12FD2DC9A34B3CDDC1B175BE03309CB72F2FDF73C4AF16BE94DF08D5A3A097B83BDD70C05F612091BC4F113211DEC627E1E5E37AC00F62609F39DC19C4900778C0E7B2E4C67BDEA622A693CBA3493CD1CC66B0E0950CEB6F1E373795885A66E17579167DC8779F99D47F9F6535FDF216DDE47FDF9BDA73EAF5DB9770ED9FF8E3DC301D86B9C88088C276550F33C4793944D35511FD3E062DA036DEAD042CC484EE8CE02BC42A05318F57C0C36A904EB81D226D7
	41F557454F1BBBE8BCCE833AD9A6EDF35FB05A2B1C7686255D84F4392F41D967B544BD5A57B4CA4EE275E064D98BE3DAGF0CF407A609A3CDFB55E595C1E823CF7744069104DB9C863F19A50A583B05C339EAEC766EBDC667F7260B89D0C4B6A708CDC62065CA1AF57F17FEF395CBF710EA6F7D69434C4495D82FCEA06496DA80BED33A29A431DB86FEF392C1F168D1775BAAD23D85FAF5839DB88C71D3B97D2BE630147C4134F73B7635FA33CCA57C98A50AEB5693B0E8C372DC2570DA3935EB339DDFEAC5F4BAE
	5996FFC65B2591787492369F32B9DBCAE35FA35C22EB318D683BB239FC5F2C0B2F4F93F41D0E815AF1262C0F670FA66BB60B1C31E4CD86BE82BC1F5D3AF5AB1BFF71755836DFCF577EEC20CFB5E53D33BB36DE0F501CC056C33EDB4F6F819236C77C3BB0F41844BD93D6FF84274213B5A564B025A5F49D738F9BFF248F7F23E10E052AB320AA90F095EDE71BBBEF3A5ADA4FFDF2616CA71DC0931FC7284F991BC7674576880B1163B77EB196CF2663A4B407F19E784D8EFE9EA87A51C32469454D36A3A476F920EE
	BD2B9D472E0F1C5B1A143FB44E52053BA3B2170A6FC55B2D99827944FBEE2883D56FFBF01AAEF7D61FAC6174E91E7B511151A6FA6F5726CC2E0F706703D16CDBD3F6BFF3B3DE0F6F0D0C1BFAF3345828BE898A561698B2BCD398997761117B9F279C080C9B126F5726EC6C436FC47C7DCF87A3FC6CF577BF7F7A224838BB7907620F2B0539BF28B267BEF8A8B2F76B61880F4FF75F034707919E74A7C7E35C159B77B3B72BEBBFAB46BCB393E2B48D003C8E7B88C0B9C093C097008C10G1084B08DE09640D200CC
	00ADG859B208E5940EB040CB701F55C7B8763120E96431AE03D4FD70A6357962162FB5796F70BE35743E30AC756435FAC0EDD8FBFD4EC2D0799EEFCB166D9AF485DE552BDE6622E1A383F22BC91F7C5F26BBE93BF194A05789691A764782728DC7D9457E560FEE241DDB5F0530B31BE8215FD2A46477E3C186EF512AC3376BFD29AC901F2A9F2BE20B2CA8141A2773F18B8141B7D27AE4D3B8618CDDDCF32039A59B3B2DEDC8FAEFBF16A4D26C2BB3137129C9F152D15E477231261GE43E27813E9D7C025C0DE4
	8A1D949AD760CEFA876E24176075BAB9F8CED3C3816799A1A074C87A00533C54F69E2BB7D920DCCF01B07926B02DD433743E1A7A2CB513326C448112B0CF3792873A8B845218E0D960E9940BB88C3882108F5CF68ECB834F7E75872F242C3A9C4CA2B92D64GD68D9D22C68F5D223BC95079BFCA1AC9DEGFFF542BD90268A52C7F6D09F1045B6D26839043F3286F5516D62C70E20CE763561CC15A1DD12ABDB0420CF47B0BD55C69C8A7E77056CE15D3220F8AB59597E94155766EA245A636D9881F977D2F911CB
	0549E56FBF649FABBE3FF6C69631B7136D32AA381B059ED8DD2DA4CF77E9E250274A6EF0600F0CD1EEBF3F504C82F687CD75D1590ACED8CC26503D616FA76E4C5859EC073ADAA31514928B388A1342FD46A7E8B01528356A78DF9ED5BBEA76FD6D10FEFC5E39372F59094DD0929D39BD9C50C90EC770CB72G3CB45124CBDD3CCB87E9B5C189FAD44D8F12B923A463DD6684955F19D375488F7F73B3BAC1E428CE8EB3D34250CA2F287AABDD9E0F2408A68330AB30FF57E40FB495D1B4FB428F6FBB3FF8EBB744C1
	934972D7D5516FEA7AFD923F2BA9261AE22ACF02F1B712D1BF517DE12EB38D9EB32A517D954D60C1FD797AB9CF2CE85818895AB513EC7A1BACB821A0C275C9485FADEA8FCCFD645DB74727FF8BFD9440D089A35FBD0CBD2EFE046DC64C971AEB896B0EC00CD66E0808C36C0E11210368825301585ACBC977516F7DDF599CB86A9DBA364854622829054DEFE80139B4A70E96A781CE18A3E4E3847EA269B0E99ACC9AF304D13F158CD96F5DB7FF7D6D383FBF25EEE2F5DB462668FB778B3DE81F23EF38729A5F607B
	5E77EF639F0F635D85F67885D956853957253871DE707400AEC66EB5AE75A27FA027B1BA0D7F74507F135D360C799FD0CB8788F86F30137D91GGE8ADGGD0CB818294G94G88G88G4CD485ADF86F30137D91GGE8ADGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGB792GGGG
         **end of data**/
    }

    /**
     * Return the JComboBoxControlStartState property value.
     * @return javax.swing.JComboBox
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JComboBox getJComboBoxControlStartState() {
        if (ivjJComboBoxControlStartState == null) {
            try {
                ivjJComboBoxControlStartState = new javax.swing.JComboBox();
                ivjJComboBoxControlStartState.setName("JComboBoxControlStartState");
                ivjJComboBoxControlStartState.setPreferredSize(new java.awt.Dimension(170, 23));
                ivjJComboBoxControlStartState.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
                ivjJComboBoxControlStartState.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
                // user code begin {1}
                ivjJComboBoxControlStartState.addItem("OPEN  (RawState: 0)");
                ivjJComboBoxControlStartState.addItem("CLOSE (RawState: 1)");
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjJComboBoxControlStartState;
    }

    /**
     * Return the JCSpinFieldPercentReduction property value.
     * @return com.klg.jclass.field.JCSpinField
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private com.klg.jclass.field.JCSpinField getJCSpinFieldPercentReduction() {
        if (ivjJCSpinFieldPercentReduction == null) {
            try {
                ivjJCSpinFieldPercentReduction = new com.klg.jclass.field.JCSpinField();
                ivjJCSpinFieldPercentReduction.setName("JCSpinFieldPercentReduction");
                ivjJCSpinFieldPercentReduction.setPreferredSize(new java.awt.Dimension(40, 50));
                ivjJCSpinFieldPercentReduction.setMaximumSize(new java.awt.Dimension(40, 60));
                ivjJCSpinFieldPercentReduction.setMinimumSize(new java.awt.Dimension(40, 50));
                // user code begin {1}
                ivjJCSpinFieldPercentReduction.setDataProperties(
                                                                 new com.klg.jclass.field.DataProperties(
                                                                                                         new com.klg.jclass.field.validate.JCIntegerValidator(
                                                                                                                                                              null, new Integer(0), new Integer(100), null, true, 
                                                                                                                                                              null, new Integer(1), "#,##0.###;-#,##0.###", false, 
                                                                                                                                                              false, false, null, new Integer(100)), 
                                                                                                                                                              new com.klg.jclass.util.value.MutableValueModel(java.lang.Integer.class, 
                                                                                                                                                                                                              new Integer(0)), new com.klg.jclass.field.JCInvalidInfo(true, 2, 
                                                                                                                                                                                                                                                                      new java.awt.Color(0, 0, 0, 255), new java.awt.Color(255, 255, 255, 255))));

                ivjJCSpinFieldPercentReduction.setValue( new Integer(100) );

                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjJCSpinFieldPercentReduction;
    }


    /**
     * Return the JLabelControlStartState property value.
     * @return javax.swing.JLabel
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JLabel getJLabelControlStartState() {
        if (ivjJLabelControlStartState == null) {
            try {
                ivjJLabelControlStartState = new javax.swing.JLabel();
                ivjJLabelControlStartState.setName("JLabelControlStartState");
                ivjJLabelControlStartState.setFont(new java.awt.Font("dialog", 0, 12));
                ivjJLabelControlStartState.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
                ivjJLabelControlStartState.setText("Control Start State:");
                ivjJLabelControlStartState.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjJLabelControlStartState;
    }


    /**
     * Return the JLabelPercentReduction property value.
     * @return javax.swing.JLabel
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JLabel getJLabelPercentReduction() {
        if (ivjJLabelPercentReduction == null) {
            try {
                ivjJLabelPercentReduction = new javax.swing.JLabel();
                ivjJLabelPercentReduction.setName("JLabelPercentReduction");
                ivjJLabelPercentReduction.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
                ivjJLabelPercentReduction.setText("Group Capacity Reduction %:");
                ivjJLabelPercentReduction.setMaximumSize(new java.awt.Dimension(112, 14));
                ivjJLabelPercentReduction.setPreferredSize(new java.awt.Dimension(112, 14));
                ivjJLabelPercentReduction.setFont(new java.awt.Font("dialog", 0, 12));
                ivjJLabelPercentReduction.setMinimumSize(new java.awt.Dimension(112, 14));
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjJLabelPercentReduction;
    }


    /**
     * getValue method comment.
     */
    public Object getValue(Object o) 
    {
        LMProgramDirectGear gear = null;

        gear = (LMProgramDirectGear)o;

        gear.setPercentReduction( new Integer( ((Number)getJCSpinFieldPercentReduction().getValue()).intValue() ) );

        com.cannontech.database.data.device.lm.LatchingGear l = (com.cannontech.database.data.device.lm.LatchingGear)gear;

        l.setStartControlState( new Integer(getJComboBoxControlStartState().getSelectedIndex()) );

        return l;
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

        getJCSpinFieldPercentReduction().addValueListener(this);
        getJComboBoxControlStartState().addActionListener(this);

        // user code end

    }

    /**
     * Initialize the class.
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private void initialize() {
        try {
            // user code begin {1}
            // user code end
            setName("LatchingGearPanel");
            setPreferredSize(new java.awt.Dimension(402, 430));
            setLayout(new java.awt.GridBagLayout());
            setSize(402, 430);
            setMinimumSize(new java.awt.Dimension(0, 0));

            java.awt.GridBagConstraints constraintsJComboBoxControlStartState = new java.awt.GridBagConstraints();
            constraintsJComboBoxControlStartState.gridx = 2; constraintsJComboBoxControlStartState.gridy = 1;
            constraintsJComboBoxControlStartState.gridwidth = 2;
            constraintsJComboBoxControlStartState.anchor = java.awt.GridBagConstraints.NORTHWEST;
            constraintsJComboBoxControlStartState.weightx = 1.0;
            constraintsJComboBoxControlStartState.ipadx = 44;
            constraintsJComboBoxControlStartState.insets = new java.awt.Insets(15, 4, 4, 87);
            add(getJComboBoxControlStartState(), constraintsJComboBoxControlStartState);

            java.awt.GridBagConstraints constraintsJLabelControlStartState = new java.awt.GridBagConstraints();
            constraintsJLabelControlStartState.gridx = 1; constraintsJLabelControlStartState.gridy = 1;
            constraintsJLabelControlStartState.anchor = java.awt.GridBagConstraints.NORTHWEST;
            constraintsJLabelControlStartState.ipadx = 25;
            constraintsJLabelControlStartState.insets = new java.awt.Insets(20, 10, 6, 4);
            add(getJLabelControlStartState(), constraintsJLabelControlStartState);

            java.awt.GridBagConstraints constraintsJLabelPercentReduction = new java.awt.GridBagConstraints();
            constraintsJLabelPercentReduction.gridx = 1; constraintsJLabelPercentReduction.gridy = 2;
            constraintsJLabelPercentReduction.gridwidth = 2;
            constraintsJLabelPercentReduction.anchor = java.awt.GridBagConstraints.NORTHWEST;
            constraintsJLabelPercentReduction.ipadx = 53;
            constraintsJLabelPercentReduction.ipady = 3;
            constraintsJLabelPercentReduction.insets = new java.awt.Insets(6, 10, 365, 9);
            add(getJLabelPercentReduction(), constraintsJLabelPercentReduction);

            java.awt.GridBagConstraints constraintsJCSpinFieldPercentReduction = new java.awt.GridBagConstraints();
            constraintsJCSpinFieldPercentReduction.gridx = 3; constraintsJCSpinFieldPercentReduction.gridy = 2;
            constraintsJCSpinFieldPercentReduction.anchor = java.awt.GridBagConstraints.NORTHWEST;
            constraintsJCSpinFieldPercentReduction.ipadx = 48;
            constraintsJCSpinFieldPercentReduction.ipady = -30;
            constraintsJCSpinFieldPercentReduction.insets = new java.awt.Insets(4, 10, 364, 120);
            add(getJCSpinFieldPercentReduction(), constraintsJCSpinFieldPercentReduction);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
        // user code begin {2}
        try
        {
            initConnections();
        }
        catch(Exception e)	{ }
        // user code end
    }

    /**
     * main entrypoint - starts the part when it is run as an application
     * @param args java.lang.String[]
     */
    public static void main(java.lang.String[] args) {
        try {
            javax.swing.JFrame frame = new javax.swing.JFrame();
            LatchingGearPanel aLatchingGearPanel;
            aLatchingGearPanel = new LatchingGearPanel();
            frame.setContentPane(aLatchingGearPanel);
            frame.setSize(aLatchingGearPanel.getSize());
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
            System.err.println("Exception occurred in main() of com.cannontech.dbeditor.wizard.device.lmprogram.GenericGearPanel");
            exception.printStackTrace(System.out);
        }
    }


    /**
     * setValue method comment.
     */
    public void setValue(Object o) 
    {
        LMProgramDirectGear gear = null;

        if( o == null )
        {
            return;
        }
        else
            gear = (LMProgramDirectGear)o;

        getJCSpinFieldPercentReduction().setValue( gear.getPercentReduction() );

        com.cannontech.database.data.device.lm.LatchingGear l = (com.cannontech.database.data.device.lm.LatchingGear)gear;

        getJComboBoxControlStartState().setSelectedIndex( l.getStartControlState().intValue() );

    }
    public void valueChanged(com.klg.jclass.util.value.JCValueEvent arg1) 
    {
        //fire this event for all JCSpinFields!!
        this.fireInputUpdate();
    }
}