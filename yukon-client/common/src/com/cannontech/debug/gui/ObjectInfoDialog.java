package com.cannontech.debug.gui;

import java.util.Vector;

/**
 * Insert the type's description here.
 * Creation date: (1/5/2001 4:56:13 PM)
 * @author: 
 */
public class ObjectInfoDialog extends javax.swing.JDialog implements com.cannontech.common.gui.util.OkPanelListener 
{
	private javax.swing.JPanel ivjJDialogContentPane = null;
	private javax.swing.JList ivjJListInfo = null;
	private javax.swing.JScrollPane ivjJScrollPaneInfo = null;
	private com.cannontech.common.gui.util.OkPanel ivjOkPanel = null;
/**
 * StrategyInfoDialog constructor comment.
 */
public ObjectInfoDialog() {
	super();
	initialize();
}
/**
 * StrategyInfoDialog constructor comment.
 * @param owner java.awt.Dialog
 */
public ObjectInfoDialog(java.awt.Dialog owner) {
	super(owner);
}
/**
 * StrategyInfoDialog constructor comment.
 * @param owner java.awt.Dialog
 * @param title java.lang.String
 */
public ObjectInfoDialog(java.awt.Dialog owner, String title) {
	super(owner, title);
}
/**
 * StrategyInfoDialog constructor comment.
 * @param owner java.awt.Dialog
 * @param title java.lang.String
 * @param modal boolean
 */
public ObjectInfoDialog(java.awt.Dialog owner, String title, boolean modal) {
	super(owner, title, modal);
}
/**
 * StrategyInfoDialog constructor comment.
 * @param owner java.awt.Dialog
 * @param modal boolean
 */
public ObjectInfoDialog(java.awt.Dialog owner, boolean modal) {
	super(owner, modal);
}
/**
 * StrategyInfoDialog constructor comment.
 * @param owner java.awt.Frame
 */
public ObjectInfoDialog(java.awt.Frame owner) {
	super(owner);

	initialize();
}
/**
 * StrategyInfoDialog constructor comment.
 * @param owner java.awt.Frame
 * @param title java.lang.String
 */
public ObjectInfoDialog(java.awt.Frame owner, String title) {
	super(owner, title);
}
/**
 * StrategyInfoDialog constructor comment.
 * @param owner java.awt.Frame
 * @param title java.lang.String
 * @param modal boolean
 */
public ObjectInfoDialog(java.awt.Frame owner, String title, boolean modal) {
	super(owner, title, modal);
}
/**
 * StrategyInfoDialog constructor comment.
 * @param owner java.awt.Frame
 * @param modal boolean
 */
public ObjectInfoDialog(java.awt.Frame owner, boolean modal) {
	super(owner, modal);
}
/**
 * connEtoC1:  (OkPanel.okPanel.JButtonOkAction_actionPerformed(java.util.EventObject) --> StrategyInfoDialog.okPanel_JButtonOkAction_actionPerformed(Ljava.util.EventObject;)V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.util.EventObject arg1) {
	try {
		// user code begin {1}
		// user code end
		this.okPanel_JButtonOkAction_actionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (1/3/2002 10:15:07 AM)
 * @return java.lang.Object
 * @param value java.lang.Object
 */
private Object formatSpecialObjects(Object value) 
{
	if( value instanceof java.util.GregorianCalendar )
	{
		value = new com.cannontech.clientutils.commonutils.ModifiedDate(
				((java.util.GregorianCalendar)value).getTime().getTime() );
	}
	else if( value instanceof java.util.Date )
	{
		value = new com.cannontech.clientutils.commonutils.ModifiedDate(
				((java.util.Date)value).getTime() );
	}
	else if( value instanceof Long )  //show Longs in HEX and Base10
	{
		value = value + "  (0x" + Long.toHexString( ((Long)value).longValue() ) +")";
	}
	else if( value instanceof Object[] )  //try our best to print the array
	{
		Object[] arr = (Object[])value;
		String s = "";
		for( int i = 0; i < arr.length; i++ )
			s += formatSpecialObjects(arr[i]) + ",";

		value = s;	
	}


	return value;
}
/**
 * Return the JDialogContentPane property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJDialogContentPane() {
	if (ivjJDialogContentPane == null) {
		try {
			ivjJDialogContentPane = new javax.swing.JPanel();
			ivjJDialogContentPane.setName("JDialogContentPane");
			ivjJDialogContentPane.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJScrollPaneInfo = new java.awt.GridBagConstraints();
			constraintsJScrollPaneInfo.gridx = 1; constraintsJScrollPaneInfo.gridy = 1;
			constraintsJScrollPaneInfo.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJScrollPaneInfo.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJScrollPaneInfo.weightx = 1.0;
			constraintsJScrollPaneInfo.weighty = 1.0;
			constraintsJScrollPaneInfo.ipady = 360;
			constraintsJScrollPaneInfo.insets = new java.awt.Insets(16, 16, 3, 15);
			getJDialogContentPane().add(getJScrollPaneInfo(), constraintsJScrollPaneInfo);

			java.awt.GridBagConstraints constraintsOkPanel = new java.awt.GridBagConstraints();
			constraintsOkPanel.gridx = 1; constraintsOkPanel.gridy = 2;
			constraintsOkPanel.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsOkPanel.anchor = java.awt.GridBagConstraints.WEST;
//			constraintsOkPanel.weightx = 1.0;
//			constraintsOkPanel.weighty = 1.0;
			constraintsOkPanel.ipadx = 15;
			constraintsOkPanel.insets = new java.awt.Insets(4, 16, 3, 15);
			getJDialogContentPane().add(getOkPanel(), constraintsOkPanel);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJDialogContentPane;
}
/**
 * Return the JList1 property value.
 * @return javax.swing.JList
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JList getJListInfo() {
	if (ivjJListInfo == null) {
		try {
			ivjJListInfo = new javax.swing.JList();
			ivjJListInfo.setName("JListInfo");
			ivjJListInfo.setBounds(0, 0, 160, 120);
			// user code begin {1}

			ivjJListInfo.setFont( new java.awt.Font("monospaced", java.awt.Font.BOLD, 12 ) );
			ivjJListInfo.setFixedCellHeight( 14 );

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJListInfo;
}
/**
 * Return the JScrollPane1 property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPaneInfo() {
	if (ivjJScrollPaneInfo == null) {
		try {
			ivjJScrollPaneInfo = new javax.swing.JScrollPane();
			ivjJScrollPaneInfo.setName("JScrollPaneInfo");
			getJScrollPaneInfo().setViewportView(getJListInfo());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPaneInfo;
}
/**
 * Return the OkPanel property value.
 * @return com.cannontech.common.gui.util.OkPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.common.gui.util.OkPanel getOkPanel() {
	if (ivjOkPanel == null) {
		try {
			ivjOkPanel = new com.cannontech.common.gui.util.OkPanel();
			ivjOkPanel.setName("OkPanel");
			ivjOkPanel.setLayout(new java.awt.FlowLayout());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOkPanel;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

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
	getOkPanel().addOkPanelListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("StrategyInfoDialog");
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setSize(457, 469);
		setTitle("Data Information");
		setContentPane(getJDialogContentPane());
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the OkPanelListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void JButtonOkAction_actionPerformed(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	if (newEvent.getSource() == getOkPanel()) 
		connEtoC1(newEvent);
	// user code begin {2}
	// user code end
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		ObjectInfoDialog aObjectInfoDialog;
		aObjectInfoDialog = new ObjectInfoDialog();
		aObjectInfoDialog.setModal(true);
		aObjectInfoDialog.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		aObjectInfoDialog.show();
		java.awt.Insets insets = aObjectInfoDialog.getInsets();
		aObjectInfoDialog.setSize(aObjectInfoDialog.getWidth() + insets.left + insets.right, aObjectInfoDialog.getHeight() + insets.top + insets.bottom);
		aObjectInfoDialog.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JDialog");
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
	}
}
/**
 * Comment
 */
public void okPanel_JButtonOkAction_actionPerformed(java.util.EventObject newEvent) 
{
	this.dispose();
	return;
}

/**
 * @param obj
 *
 * This method will use reflection to get all the "getter" mehods
 *  and print all their values and names in the JList
 */
private Vector getListData( Object obj )
{
	Vector data = new Vector(
			obj == null ? 0 : obj.getClass().getMethods().length );

	if( obj != null )
	{
		for( int i = 0; i < obj.getClass().getMethods().length; i++ )
		{
			//get all the getters
			if( obj.getClass().getMethods()[i].getName().startsWith("get")
				&& obj.getClass().getMethods()[i].getParameterTypes().length == 0 )
			{
				try
				{
					Object value = obj.getClass().getMethods()[i].invoke( obj, null );

					//format all objects that have an unreadable toString() output
					value = formatSpecialObjects( value );							
						
					data.addElement( obj.getClass().getMethods()[i].getName().substring(3) +
						" : " + (value == null ? "(null)" : value.toString()) );
				}
				catch( IllegalAccessException ie )
				{
					data.addElement("  <<IllegalAccessException for method : " + obj.getClass().getMethods()[i].getName() + ">>");
				}
				catch( java.lang.reflect.InvocationTargetException iv )
				{
					data.addElement("  <<InvocationTargetException for method : " + obj.getClass().getMethods()[i].getName() + ">>");
				}

			}

		}

		//give some order to our data
		java.util.Collections.sort( data );
	}
	else
		data.addElement("(null) object");

	return data;
}

/**
 * Shows multiple objects data. Adds a seperator for each object.
 */
public void showDialog( final Object[] objects )
{
	Vector totalData = new Vector(64);
	for( int i = 0; i < objects.length; i++ )
	{
		totalData.addAll( getListData(objects[i]) );
		totalData.add("");  //add a blank line
	}

	getJListInfo().setListData( totalData );
	show();
}

/**
 * Shows a single objects data
 */
public void showDialog( final Object obj )
{
	getJListInfo().setListData( getListData(obj) );
	show();
}

/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GF1F954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135DA8BF0D45599868C10722652B10369B42D0FE6C6C651EA25160E185216D6E65499D4E61A99E34BD8C63193ABBAA84C74E413EC9208C98829A6A10F0D848D0A8145D69A73CEC082D1B2A4E2FC121B5D1B4DCDEE76AEF76F6681817A7F67F177E6F3179490E6FE6EBD7F39671C7F7C7F77BF4E59100272387494C781A1A969047F7BA80510287D047C6A5E3C5B85EF1A1C60A416FF95C0BB49424EB4F8AE
	023E7D0B931CF9643E2319503E955A37AECAF03E816F3B4883C7B73F83AF9156D98BFD53D77D68054B2FC31CE21D8C5A7E3AB09B1EB9C071404367E699578F9496F27EDA4E971462A06451BE7CE6C1E189679D06F68750BC2038BE767D8AF8E65115E7569C665FFD33B80EEC7E535B0E57F09E4B1C028A3DECECADFF3A48DDA99B3C38567DD4CE9CE3C0FB9990EEBE15BC7851817C3EE350DB6F56062A5D124F27798C595DDF6D11FB035EEAEFD02929B959BBA03B0DB6DF1F562AC82A66A5BD7DB867BF8BB3699C
	04D4C1FB93653DD40F6B5C8A6FAB0156197C158D48BF28A4B80F8175A8E2EFF5EF57F2593B4F5FC53EF952171A9BE1EF238A5BD31C925A5B12602B99C5548E0D99286FC468A383894E822043C05DC023C03B297DBFF122791BFA0D96EDE8C873B5797D4D2AC77FD47348AA3C5754C08F671D125D1A6EA1045978B7057BED64399C31F18FDF71B8261378C3425E4BCE5DCE92FFF14211E123134478030EC583CC977183A11D3035530AA22D3DE6E0A29EA72E7D85312E5DBECBCBCD37D97B5E4777B99279BC491635
	DD03E8EB055ABA8F5ED3B8BE98BFC0799E8B1FE1F3AB458C368D68DBBFA830511DA3E4E9CFDBCA725EEF3755C3CCC21323D6E5B2F42961329CEC44392729613254D07EA2B5DC160486A1CB9C74258DA5B8993E5A3250D77B219D85948BF4AF50BA20E420B41F4058A267017F93E39D12EEB4879555A36B040DED4BF89D1E722821CBED3E00A1795C72E9C9D724DED58E907E4D298C54C16F04F1DFD06C3BE10DCFE41F2CCB062279E0F6E9C8B6E4BD403E694958095F68B26D6DD206E4D8E048CF443C7D993B6029
	CA8163F33FC7B26450972C5F1F01B69916CE430CB0813CB33D8CEFC7FDD5427EEB015ACDBDBCC767BBA16BBED92D29E9573441203F8BF7C2E2FD28675DC598BBFA60FD54A7F42C6FC0DE92343718733C1919EF1927D5F1237812BEA6FCCCEB40F81C2D894C7C3DDBE02675355B487E470F5BE286E93F4630522A057B4E981D9365E26D14066E883E142D597B5215470A6208EB24DE637CB9262D123251D6AA489C8D942F899DFF1B69E218ED168232C069C99DGA6FBC29AE7F27E27B10D6B13359BE9FB15A6F435
	066E43CE5F5F8731B51E26EBFC9FFA31A326134EAC4CC372306CB3DAE53F6C7340CB0077FD1505D863FE5A2719E3FAA85F8FFE5D820E94B079FDD968FB12E168CAEF5010BBE543D0FC5ED03F0F2E9588767AC3A1C12C954C42782B0D00DF1B5F1F4BC21FF0EBBE1FCC0DE38E617D6354A7E5D59E8221DB34204FE07C995B51D60372980F893336E301B3AC29C10CAF2C9ED8AE6D40BD684A90189CC3942975233FFED0D4E85151C630B756B5F950D769973EB732C778DE65391F12D5E776BA6CF2C74CED2D0EA8BF
	731B98FF487718CCA3BB6CE53FC0E3836787182C37982807CEB003A1FB47AC658947FBD18E4E358256D88B3441AF707E4A8E6C97B105833DC5D3D526CED34F4FE46721DD6433C199C2B1862F93266E1E4F4E8F6BBB4D75487ADF4A5EC1FDAD9477561DC5BD1EED94B55FB61027GF8074FB2DDC61DE53AE0FBBBF596E524F55900B4AC0DD687C6GB3B5B5474596F98D96CB63A87A05CB9735163384775D8DEDBFD034AE6C7358EB2256DAB298C712EEA90ED8C74669CC16553AA51F5EA0EAADF7AE2E1996G7983
	C2277E572C713EA990505C8ACDB5DC9F65B9D67BB1DEE54EF6F876A9FAGEACB0FBCAAF477CE8E4A0B390833F9EEDECAF3B66A278A566D81E2585C5BE045E68C70D783ED845A8614CA67CCE92AC7BF9A6C10C0406A634DC14350FCA7871B28454FC87451A16BFD1ABEA4FB2AD06555C1C3D12B0FE084E0756FBEE11B54CF1BC2F31D19EA2EF65B49BE5EF71A081A6BE73B9A396CEB6FDCCAEAAF6DF25855405139250E82035777C648AE6377B8D3B7FD37B50345461F523AAF5A304FCB31E17C6550E67550B90A19
	55C6F81D94C5796B0C70BAA93BC454C99B206FD8D06470FF6421AD16C1FB8DD09250962042E1407930404ABDB9074894B5D29B1CC1F41F24F6CE880A22D649CDE5F10C37555CF49147084891F10D6175EF5770443D7B435AEC5C1D134615053517040DE31858F4260CD84FD10602C7A7EAF688B18CAA044752DF17D8713AEC1855C00F42F38390736BEF9BB2795C5B0605DFB753BA794391283300BCA3C2679D7998D346219D83349CE8ADD0B2D076E802736028507B4B39F5D3691DA234CBEB815EB108DDAD61B9
	86B1C46B3795B46EF4405C8690636596A3AF9A5AF1A6EF41EE642D06767AD1D1A3CCAB41BC3441EC4F21ED74D9564EAF09D48BDDAD61DA936D7C6E773E06359EE17A5E1A973906F06C342FA1B2F75A5790B9BBF7B2BE4CE1BDB9087E57F7DE3E46A85BE9D7E314D2397F78FAAE1DBB90108394G2D12A1713E3F997FA2B63546E7F03860FB5EC51EEC4C625F9CB1341677CD1FF8F1DFA1314DE1549B984EDFAC91B8CF9B93B89FAEC71C57C23B8BC89D933868ED88774D283110DDE346A647BC9643FEC97DE65998
	0BE1A9A67FC14ADFE1613358F62CDE5C152C063E2AF342FF3E4A0FECCFA95F5E1E5EFC4C3181AD283B65F01B79728B4D3E1027359972902A1757C3F43CA169DEF9124DAF52712CAF5278597911F041EC77C8915A57A377C9C195A027856413FEFE04447D1FC3FDBCDBA4ECB46F1C38B73AFB846B1BBEB8E442F41DFE59CDE24F6337EF950B5816896D9220DA20EF00464F0BDCFC5BEE1C43D08C95B13461BC5E330DE2A863B2ADE9445A0AD7BAADF89D0518774942F73714046320EABCE47B5A71C8B8F816622AE3
	1C59BB5D643FC4799E8B1F61E03CDC549B235017B8AEE25A980D4D0D97G1F97842F3DD9443E518B822FAB316460B27F4E727ED97D779DFBA667B16FF5DD0357D445E8870BEBD7FF2178BC5A88188CEFAF0CC0F5EBDBE747494EA3E7CEFEF626259DDED83D93B7E23D4FBC3C7DA35BF382521A8B4C16097C39F2769EDE773653DC03E38AAEC2BE3C083C754585E1B25E817FD5431375EF5FED6DC73F00124E52DFBB219FE53DF142F85F047EF44BF8362F07770A7AFA5B4371C43D6F3F916B6BA597799DAFFFF61C
	5EC3AA67EC4DE073BC7235D5BFF9CBAB9CA99166C9666337DD8C5503BC971722FE8EDECAF036DE9298EDAD97986D3FA4B01ACCF973209DEF7236D25E4ACB6CFC42F8B34A4371045F0B77F9172E6D9E042DF17324B552436CF96D18DD5400E7DED59A53028658F59A6A65676679A18976318548857C8F2731BD2D215F381BDB2718BB1DBF11164018F820BD1C3F925E4B398DD94DB66B8D54F15E7484677E69A23F7C2668DDF81E1686F8E5F64423981ACECBB3E0F6689AC4E6E30CF169E9136B6D2E622B3BE7FEA8
	C360755E87FEC6D64CFAA5E5C7841C35CFE73AD079F3E3579E4AE7FBC9263FF544C25F8A20C4204DC019B3C00FB3445E52280CD8E10278CD06A1397B69C9D0941E2795F9A444460442665ED50C1232B90E976F0D249F1861B844A1F6366A123C263D5FAA1DEC6F3AD2E16F8FC13EF6A083A8FA86BBB7464D40EF3EAEAF345143C4AC7D410F754071CE372E29AAA6A81A79D95FD32750B62270E7F57FFB2E89F7A8FC3D029B2046GE2B9675FCD2276E8845E688DA25F9E7AE042FDC035B4E254549CD53511F63EB5
	9E97AFB40938387C86D1CF5DC1675C88ED9285E731A8F19FB07A16307F325B1702EECE58462B242E8F9C8723787DFAD4A84E4487DF4DA827FB05DCCF443F6486BB9E4B71CEBC26F06CBD1DAD56AE3963E6527A64ABA9FBA2E0EF91DFB34932764601ED998C93F35FC01ED95FB03760F879619B982F951FE2C0A5211C23C5CDA7FC6873B2ECD515E2FF61CC70F520EEA095E85ACC0159578B5FE41835416B910FD796F9CE2C77CAE1647A1094CDFD371FD50477A13D1AE1E8C3B83F606796A1168C4DCF190CD7D004
	FA5495EF3F41BE657CE2DA2B29F29FE5B3CC6DAE93180A053D2D1BA9B075FECF49E4CCFD22AB1EE6494BE045B065AE93184A1EA5B035184E598A6DEF000CD982D38303D63B222DA26540F93318BD174F8A59D5447CD462BDD2CAEC4F8EA1EFB3B1AF3B446F104926FC4F5622FCD533931C0DC05D33C59E7B38696AFE4BEB3B4AF1B32EF89C2BC94B1BD1FE7F6C70B3CB95650F4F8EBF335CED460DE8685BB4DB5C8D7C3E8CE3CEC7B4E09BA88AA896E8A5507AE801731B0A8F12A96E853AB4FFBBGDB359CBD597C
	6F3AB0AFEE0239B2FFG398C2895289B488F94859483348CE8B5D0A2D0B2D05A9C08C3C0D5C035C09DF304ACA7CA30260A1483B9B829636912022E47F1FF30B4326FFDD4BA356FB552FAC9861F699A73733353911ABB343E3E00EC7A787152BC1357966850828AC695E66B0A322B4348012B9C57FF456398065259F9E7CEB836F2A85FBFA79CDBB71971E39C7A126688EC7D21823155BAB74159839485948B349CE84DDCE14F27CA8FDD461E27B40D1E6C7010876614F50BE5599E87DC175BE3625CC8E73D6F6AD0
	6664396177197D14CF66055FE7A6570A7B4CBC68B36609F8313719187A2E363C5F5FF47D4FF00FDE57B518FD9EA90B6CB7CF14595FE9A416595FD17D256C72F7D04F16C57E1D6B0532493FF3B538A24BF654B535CF1FF45967534FDD137369D7AE7BFC5A633266D34613DDA82B97E2D137795D00497B5264E9F4CE64FD21F80CFE139F24F2A17F180C8B8A7EF9AA578865F5F35EB48B6FCB4E1B69423884DB6E57F4B1F60E8B6FD07B94D5E56D85749B4587E14A54470DD4C645AFD9640EB1F9DF1A385BD19E42F9
	01657D47E5579F733F3C2EEB30BDBFDA9199D7CFD6CC0D2B248AFBDCBDD3B199D74FD558636A058AAB2E5879FC3ACB1C4F571B7147D307F62D1AAF5ABE5AEEB45BAF57CE25AF9CFBEDB6492D1BEA0DEBB91B3373F50CEB2A733515FAE0CF46FCF63E46F674FC964F9FF30833597BEF235E0F37AB8103472E3573F11D1F54D96FF016G8F6FB9D64CE767737F7FAC5303CB73DA7561FD787705B635AA2E4EEB54A76AC40DDA30C0544A970EDAFF0FDD197B01A36279FB81BFFFAF88554A7B617C456C73673A4B6548
	E58BA65B1F656F38CA5445AA68CF845A8C143D90EA3805A2F7476F1972F73AA323363F53B1BC7F23CE6039BDB4E7E5E45F3B2B52BE5F5CD7E91FEFFEDDF9795FC4FEDB99A99F313B6A2CFAF19E7703FCD1C037822D865A30D07C8EF720DE1CFD529609FB46EF6B436F99710FC62B0938A7DCD89FFE4F18B2217F217A70FB462694EB7F0B7561770CE2BC33770775D7DA77313FFFBDF06ABC6261F912EE10BC7C0D4705EA56076841B82013DCF18A2CEE23F9A3E978D0CD6ADFEFC9F51034B662E4712E0D24E150EA
	A369B4263511D408F723FE2E872C0D513EE9013364A2C989636671337F3ECB567B4DADB38BA35E7F7A97B36CC6AD8E611E5F1FF521DDD681BFF1314056931D6F114877C31DC3122A9A12BA58647360DDD17318A10730B4378B678F08EFF8A73FC768C23987ACE37E87D0CB878818BF2A925F8FGG2CACGGD0CB818294G94G88G88GF1F954AC18BF2A925F8FGG2CACGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG9990GGG
	G
**end of data**/
}
}
