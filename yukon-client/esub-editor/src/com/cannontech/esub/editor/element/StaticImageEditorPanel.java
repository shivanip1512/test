package com.cannontech.esub.editor.element;

import java.awt.Frame;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.editor.PropertyPanelEvent;
import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.gui.util.DataInputPanelListener;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.Transaction;
import com.cannontech.database.data.lite.LiteYukonImage;
import com.cannontech.database.db.CTIDbChange;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.dbeditor.wizard.state.YukonImagePanel;
import com.cannontech.esub.element.StaticImage;
import com.cannontech.esub.util.Util;
import com.cannontech.message.dispatch.message.DBChangeMsg;

/**
 * Creation date: (1/22/2002 10:23:18 AM)
 * @author: alauinger
 */
public class StaticImageEditorPanel extends DataInputPanel {
	private javax.swing.JLabel ivjImageLabel = null;
	private javax.swing.JLabel ivjImageNameLabel = null;
	private StaticImage staticImage;
	private LinkToPanel ivjLinkToPanel = null;
	private javax.swing.JPanel ivjJPanel1 = null;
	/**
	 * StaticImageEditorPanel constructor comment.
	 */
	public StaticImageEditorPanel() {
		super();
		initialize();
	}
	/**
	 * 
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private static void getBuilderData() {
		/*V1.1
		**start of data**
			D0CB838494G88G88G9803B6ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135DA8DF0D45595E6461FC95B0CE20D951141CC09EDEC198E56E851B2A3D6C6D129C6C5C5A656586A8C63784314CCA79DB36D5D4DAE0961AFC04428E1447293886BA6A19B88118A01A084929213CDC8A0103CE4DFF6DF327B5EE67765878453F36EFD776D4B66ED90AC4C9C5E3B5F7DBB675CF36F796E5B10024F936D96EB81A196BB517E6D3390123513100C4A65DFF1AC75F8CE9EB17C7B82E4BDC92B30
			41B39D6AB202B9F979645562DCA8E7C0799014BF06770D64AF45070A61A546BC05D017F4EE7FEB134FB3F3BB1FA707166F712D0667DA10CC400A6AF272FCF5B87F9FFC059AFE884AEB35F7940B151055A730CD0A3D50004F565AE5EA4FE9344D8B3EA22D5E8DE5EC630767149311B1BF245A5DD4DD2515BBEFCBA099CF2F322E41398C63F3C9577A5ACE326796F2BBF5C4B6B994BE884F83CD835E2FFB150033D710E5C5D645DE1FD38C8F75B8C50F24AAA127689783222CD6D7372802AA755685842FF814D6B589
			3268A76AC954FB29BD170E8963C11969787C1A831A8E898389A4FD563FAC31F41C2B6916E650F1499F5F36465763589FD01FE33FA2A83BC1581CC7A4BEE7AAE0CD8D083DEB433639703E85242A016B725FE22ECB7BF733C8E60F533AAE06AE898DCC07F98D91DD9C9BBBEC1F5075AFA946B54C043A22D3B9F9E5A0EE9095A48164B39AF7EFEDAF01E7CD0FFAC489849439A698BC6C77049A940F6807776AEA285130E631D789F988E131753A5DE1220F9EAF274647D5A496CE939E8B331B2793D55DE6351B581672
			529A6BFCED0C4527A236B13B1E5809F6E5G1E5F181337936490C8B74814C6EED7690E0AAB59D58771931285FF4BD0CFD088290707A43FC78CE976D924496CB39356EF5DD577E3FEC92EB07AA56D01D2EB0E09DF52128B2CB39A19BFD29AA3FEE1F39F0FB9776246096BC177F3A34621E047B8DC866F96ADC699BEC07183CD911C45788A9A37D82E073AB9CDBCEE5B57F29B6AED37107C72B2D39B267DE93B3560DB260F635B68357DE5891EA96D002BA071A0C9A08BC052C1B23E656BFBF1C7698F0C5BA6435A32
			3E95451B60A90E2AA121CE8E2B025CAB3689A1C968710BE12235B9D00C3A770C6BD7C377D3AF4CF1CC1445901CB00A8C238B819182A94C5A1CA8DE0FEDC2A22DF5CB8191A68884899F3739F8A3BC7DC2D8ED8DFA84D50C34E075E70A511743C29B0C888340FBEDB37A7C9F744CAA0077B290662B67F6A396876544E66E17170AB74073A48436682F2E2E15FAD18DA1F4014F7F5CCEBCCB44E1B8A5EB4520A8FB60A52C553D3C734308DF7A94DD67B40A639EB9820A07F57C2FBB57B39C46B0FA19573FCD6B85D58D
			C9BDC32A58A22A2AA4FBA37D7FCEF5890F5BFEE1226DF18746D88A583588A4CD376F3BDD9B8D7655AB4A40D0500DAB2779290B1E334B61BD370567C12F9D732223054769FB6DBCCEEB575CC59CAFB9CD639425290545272FA5FA2F8D52B1A3F37C5356EE6867E8B15FEF57AAEC0E8DF6639C597F4771D1185F77BB516FD3C17F59A00BDA385F9F5A35056D3743C2D864BB2CB184ABA6FAA2D1465A4EAE099DE38F1418475843A566B176E84964B174E409D98C313AE7CBB037A8A332A1A6DFAC59C07730AC0BD4E9
			3D8BE1BC63BDB77AC212A5753464710AEA0BF4D184CE644658DA3392E36BCD70C9269ECBA3DBF9ACED3BF887195F7521E9BE1BF66FCEEB131B4504479D09A52653625D68F30D3B9CD106E41561CB697E56F06DFCD964B0EF7B028363F1C03C2035FB59311260B9629F92F55B66882B5047A1A9GCB059BD663FA4312116B5D887AA1AF1C874FD49056B735834F4C9834CA0B1DD0A94DA350A78B24201547CE73171F98762C96B4C7943F1FAD013EB6871D79381662601088C7B2EE6A133ABD5FB8B7C45535E93631
			7AD3CE344DEFB88F9C2DE85B36151CF7E13EE058D794FB9FB6551DB61EFF9B720D9C55937E42F49DD1022DEC7DEEEC0D4EDD0F166138C900276836BF65B41ED7B5613052AB5154202DC9CE9766ECB3DE6B9D121CC32A0427382042E6898E29545B0CCF2653F38E47D8584A79646AG7A1F34656495021435F19DC6CBA7465165D23CC074C921B070A00FB84A7DF8C3A97A83731386F35E3647681F79699F45744F71B666174E36E87F5877209E2300476B3AEDAADBEF708FF0CCC847DED8BD2E4F0232EC9657DAF9
			E1191D47B5616762DB93F8CFE2DB647CCDE91BF896B3FEB3057AEBE19B63B7F69D7F9945538CB863BD9BA80E6565D067BE4D737087542E39D0DEF81A5BF52EFC0D412EA6C512D55D263CBD38B715106415E40E2D2398B0ECFAD831DC16C557F8890C798E8843DE9DC0AC3F9DF8EFBB4FBDB787902B0732C747EE2358A814E768D8E1201D44BE7F31AF0E7163F2D47924F358490F4BDB4CD7248257E5D4AF5BE87902B676C63231FFA50D618B4A90B85D5101FEAB6967E75CFC30F18988813C2003718426577DA3F8
			067481210394539294FBC96E996C3BFC0077D521FD9CC8B248C210E5A0EC4E46866CDBAF4983EE059D0A8CDF95C23F1C20D039F65EBE9EB01E37CDE7188ED3611984425ADCA646B67334B669704CGE171585C4F6F1A2B4FC2FC1C65F614F7A30726E7CA3FB0ACB8059138919F8BC91E4302371EFA051DA1537BF3357E174E72B3E4A19DB3854A0BC05EBC4B735C688EFEA71D7D2BDBC87D14D253BBFC3AFB13B56B2C366FBBA37B3F906E24EC4ECDFEBEE7A754A7F4A2D66BAFB00C01FEDB816284E17543636A71
			3C785CE22CCF9ED70F7A5EE1B556AF9DD70FBEAA5746E736BD331D5B16754C8C62FFF0F34C6F93E91ACD649CFB66BBBFA5EF0EF4E9FEF788AEF2955FA88C4142FC73C100672A2AF3FCBD1E9E437588C2B98EA4719CDF63953B8A0DEBBC6A8C0FC050D6D71FB00C77A061B9E22EBE5E4C8A9CEFB914F34F6764EDB94F4F06767EEB3B6F76DDE33FFB2E3A9FBB1B278F207ED5675959EC5171D9942FB560EC0FBC2B6F11A628CBB94F4F6C07DC683F422E1CBC97C8BD088764C61044AEFE067F24220CDC610E69D602
			75C0F47D066B8F336FB37F35FD87F8AC0747E24AA33FA0F3EFFE4D32AAC6AC26F4E95FC734E73263D30AB37B6A7622FD36EED8E310DA10CE10A0C8DCB7376F798A87338F39DC0D2A8A3DBE4A9038F1ED12B8920171C0E43691F9B25BE6F4474A37DF93DC0BB95D51EBF70C62733A2357AEF50C2F5DC228F388BCD69FA15173CF95A2F3A68931665F604271668A5179FEB34597895179BE2902677BF4282B6D6173A78E90BD0E6FB33CAF6E1FAC777DD8E1F33CFBDD67506EDC2E587737A7DC667737C5AE737B5B8B
			2E496FEFCBDD316FEFEF380C77B79657376E4333BC9956E28148B210AC107CDE9E53CA65FFD8CC1B4473D138509DD168979FD57F9E33B7362D9DFB2F7C1D28E7AF72689EC5D515G0E4FF15FDEDCAFD589D210E1013D68A320FDBE15B5557070DEBC277DE29F05994F7BD866BC4F514B632E16C4E22D41707E7258750F3B772F6B9C4C6667E93E3A8476CE6D65FCF725157BA1C577C3218BB11BA7A72F4843312D942B02F23B0E7D26E2B21D31EF2CBA5BA4F5637D727824F358A7297BA1BC1845471D7BE247684C
			FDD70E51DF6F2377BF08AD77052028FD2359C76FA0FDFDE1D15F8B776F4B57313AF01392167022BA5F036BD0BEDC2B69B4EA629BFEEEF3FE9F8FED92BD6C37ADAC27F8A2E7BB63454B48C40E7E6FF11C97B9FA2E88EBAC32BB8C53E333156B6192A76A119DA54721CD279851A3A8C66B11EA457DCB3FE8548BBD78AD0361B565EBB9FECA88703A9491F5E8F5997964A5CD3FC4F8327A822BD9FD1648FF8B6A99663CAC3FAFA76FF3BA9F21314F7EADBFB89183DB7A70B73F473E46F746955ED84D39C251EFEFA535
			2F3CE659CA4C39C2FC1F761BD5DF0413936FFE0D13EF37F0CEDE6065BC314502392F9E4A9E10CBDE2E7BA9AB57BD41FB353AAF3FCC7498D95197033FD2CF333962DDFC2EC5DEFE8F397C0D71F73AF96BB6D9E371A31B0F593A4D9731B97E77EFDB5958A7299D3807BDBEFEDEEC9EB1720A99004F71CDFE8EB01D1EBBEB7CB612BF5DEE354450E901B6DE2AAF7A5B08B206F1ED13002F015402F40284C162C0E6004C81D9G12861281125B8FFB83248C6490089B44574F73604A033B6059A674EB2C15FD2E231990
			C06DB2C813E1C8E05FA6188FCAAC57464B1F2471F229BF1A5B25D2BC2EBF1A5BF5D8B837CB003A4CFE3E86ADD6637C1D836003014957C07B5E61B2537BC62D6FB413B1185F0BAC38D7E7C37DC210F410CC10C2BF77E35577D77C9D716868A43FA3B6D94D742A72B3FD6A35E737BF229F4BFB2E910CCF957037AC9C5BCF31F840CE6B7AFFE54588E43ACE79FEFB2CB587B5CF40B6EB6C6997FBD53760557B5CB096BB377CF46C4A396576B1F37EF35758C47EB3F34C1C7FA40D99798F436E9B43FD6F05B8ED575B7D
			CE47BAF46C81BAA6E227A50F6A53710729DE089F97F1C21D5BD23DC6A8564E3FB19A308E8DFBEA8C7393186CD3C23C6F33E3780D3DCF727BB5EECB5BC8B224CB5D9F2FD09D2520E050BBCD47BA74F87DB306BE67EF0677AE4B7567F22357F58E962B7DD69E3F969DD32CBCFEA3D8B2BD6313811B6B671CEFCDA567FC8B74FD384D92BBCEF7DB4C6FA4659673BB49BE4B643FDB9E34443A33B07D3CD62EF3C1006B6C34F11D3768D89C4502D01E9A605FED049D2B897BFDC9927CB4E499DEB43AA602731055384823
			AE0CBB958CE4F974EE048963C35BAAB99FCA8A4466E51C8F4D05B60BC0B8CFC88FC44EE5B676B3364858C7EDFC6CF7CC464E0E9A1B48EC6C0F884B5985F2B4573A47063CGB614F4D111D541DF63173CB25E50C83D0C73BDCC7D05EB36C566FC233512F3002932995FA0A4B65F28406D297D4D4F30CD7F3D44F41C6587D91FB65CA21C9B5A364053C0CF1CAD78BF3FC2061D4DF5470E3B9987C89CB1F2002A3C4FE27EBEC294664B826DF9296BD3EDDF1E29B4660AFA286FD6FEC86E7B4EE66C27C21F38AB74E339
			63C4AE5ABF835A4ED578BE3BBBE7B7094D9B1AC2CAD08C299798CAFF01537644E065447DBEDCB9F13F7F5CB6315D1DB6E3BB26C7E3AE6A296FC1B615D657160BB9DB5BAB52C57AF3DA8BB4143D6CEF6986775C8B4F596F92FBB8C45671CF604EBAFAD0278FD7D4E77FEDD63615586A48CA16386A088D33CF9D315364D4C732A1F10D629FED580C929EA45F53459E072AE4A53DA6D42B445E6E74CAFD664D4CD142B9642EB1E37CE45E14E50995BFD5039AF7980C6610AF5233B48E7024C13E1EDBDDDF46DACF6084
			AD81416F071DB8D0A3FB109F9C3E200A11F3F2098B478F73B6DAA52BFB1DFE6F69B774799FD0CB8788500E1552538EGGA4A9GGD0CB818294G94G88G88G9803B6AC500E1552538EGGA4A9GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG8D8FGGGG
		**end of data**/
	}
	/**
	 * Return the ImageLabel property value.
	 * @return javax.swing.JLabel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JLabel getImageLabel() {
		if (ivjImageLabel == null) {
			try {
				ivjImageLabel = new javax.swing.JLabel();
				ivjImageLabel.setName("ImageLabel");
				ivjImageLabel.setText("Image:");
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjImageLabel;
	}
	/**
	 * Return the ImageNameLabel property value.
	 * @return javax.swing.JLabel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JLabel getImageNameLabel() {
		if (ivjImageNameLabel == null) {
			try {
				ivjImageNameLabel = new javax.swing.JLabel();
				ivjImageNameLabel.setName("ImageNameLabel");
				ivjImageNameLabel.setText("X.gif");
				ivjImageNameLabel.setHorizontalAlignment(
					javax.swing.SwingConstants.RIGHT);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjImageNameLabel;
	}
	/**
	 * Return the JPanel1 property value.
	 * @return javax.swing.JPanel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JPanel getJPanel1() {
		if (ivjJPanel1 == null) {
			try {
				ivjJPanel1 = new javax.swing.JPanel();
				ivjJPanel1.setName("JPanel1");
				ivjJPanel1.setLayout(new java.awt.GridBagLayout());

				java.awt.GridBagConstraints constraintsImageLabel =
					new java.awt.GridBagConstraints();
				constraintsImageLabel.gridx = 0;
				constraintsImageLabel.gridy = 0;
				constraintsImageLabel.anchor = java.awt.GridBagConstraints.WEST;
				constraintsImageLabel.insets = new java.awt.Insets(4, 4, 4, 4);
				getJPanel1().add(getImageLabel(), constraintsImageLabel);

				java.awt.GridBagConstraints constraintsImageNameLabel =
					new java.awt.GridBagConstraints();
				constraintsImageNameLabel.gridx = 1;
				constraintsImageNameLabel.gridy = 0;
				constraintsImageNameLabel.fill =
					java.awt.GridBagConstraints.HORIZONTAL;
				constraintsImageNameLabel.weightx = 1.0;
				constraintsImageNameLabel.insets =
					new java.awt.Insets(4, 4, 4, 4);
				getJPanel1().add(
					getImageNameLabel(),
					constraintsImageNameLabel);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjJPanel1;
	}
	/**
	 * Return the LinkToPanel property value.
	 * @return com.cannontech.esub.editor.element.LinkToPanel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private LinkToPanel getLinkToPanel() {
		if (ivjLinkToPanel == null) {
			try {
				ivjLinkToPanel =
					new com.cannontech.esub.editor.element.LinkToPanel();
				ivjLinkToPanel.setName("LinkToPanel");
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjLinkToPanel;
	}
	/**
	 * This method was created in VisualAge.
	 * @return java.lang.Object
	 * @param o java.lang.Object
	 */
	public Object getValue(Object o) {
		String link = getLinkToPanel().getLinkTo();
		if(link.length() > 0) {
			staticImage.setLinkTo(link);
		}
		
		return staticImage;
	}
	/**
	 * Called whenever the part throws an exception.
	 * @param exception java.lang.Throwable
	 */
	private void handleException(java.lang.Throwable exception) {

		/* Uncomment the following lines to print uncaught exceptions to stdout */
		// CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
		// exception.printStackTrace(System.out);
	}
	/**
	 * Initialize the class.
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void initialize() {
		try {
			// user code begin {1}
			// user code end
			setName("StaticImageEditorPanel");
			setLayout(new java.awt.GridBagLayout());
			setSize(330, 121);

			java.awt.GridBagConstraints constraintsLinkToPanel =
				new java.awt.GridBagConstraints();
			constraintsLinkToPanel.gridx = 0;
			constraintsLinkToPanel.gridy = 0;
			constraintsLinkToPanel.gridwidth = 2;
			constraintsLinkToPanel.fill = java.awt.GridBagConstraints.BOTH;
			constraintsLinkToPanel.anchor = java.awt.GridBagConstraints.NORTH;
			constraintsLinkToPanel.weightx = 1.0;
			constraintsLinkToPanel.insets = new java.awt.Insets(4, 4, 4, 4);
			add(getLinkToPanel(), constraintsLinkToPanel);

			java.awt.GridBagConstraints constraintsJPanel1 =
				new java.awt.GridBagConstraints();
			constraintsJPanel1.gridx = 1;
			constraintsJPanel1.gridy = 1;
			constraintsJPanel1.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJPanel1.anchor = java.awt.GridBagConstraints.NORTH;
			constraintsJPanel1.weightx = 1.0;
			constraintsJPanel1.insets = new java.awt.Insets(4, 4, 4, 4);
			add(getJPanel1(), constraintsJPanel1);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
		// user code begin {2}

		getImageNameLabel().addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				Frame parent =
					CtiUtilities.getParentFrame(StaticImageEditorPanel.this);
				final javax.swing.JDialog d = new javax.swing.JDialog(parent);

				com.cannontech.dbeditor.wizard.state.YukonImagePanel yPanel =
					new com.cannontech.dbeditor.wizard.state.YukonImagePanel(
						null) {
					public void disposePanel() {
						d.setVisible(false);
					}
				};
				
				yPanel.addDataInputPanelListener( new DataInputPanelListener() {
					public void inputUpdate(PropertyPanelEvent event) {
						try {
							if(event.getID() == PropertyPanelEvent.EVENT_DB_INSERT) {							
								Transaction t = Transaction.createTransaction(Transaction.INSERT, (DBPersistent) event.getDataChanged());
								DBPersistent img = t.execute();
								if(img instanceof CTIDbChange) {
									com.cannontech.message.dispatch.message.DBChangeMsg[] dbChange = 
										com.cannontech.database.cache.DefaultDatabaseCache.getInstance().createDBChangeMessages(
												(com.cannontech.database.db.CTIDbChange)img, DBChangeMsg.CHANGE_TYPE_ADD);
											  
									for( int i = 0; i < dbChange.length; i++ )
									{
										//handle the DBChangeMsg locally
										com.cannontech.database.data.lite.LiteBase lBase = 
											com.cannontech.database.cache.DefaultDatabaseCache.getInstance().handleDBChangeMessage(dbChange[i]);
										Util.getConnToDispatch().write(dbChange[i]);
									}
								}	
							}
							else 
							if(event.getID() == PropertyPanelEvent.EVENT_DB_DELETE) {
								Transaction t = Transaction.createTransaction(Transaction.DELETE, (DBPersistent) event.getDataChanged());
								DBPersistent img = t.execute();

								if(img instanceof CTIDbChange) {
									com.cannontech.message.dispatch.message.DBChangeMsg[] dbChange = 
										com.cannontech.database.cache.DefaultDatabaseCache.getInstance().createDBChangeMessages(
												(com.cannontech.database.db.CTIDbChange)img, DBChangeMsg.CHANGE_TYPE_DELETE);
								 			  
									for( int i = 0; i < dbChange.length; i++ )
									{
										//handle the DBChangeMsg locally
										com.cannontech.database.data.lite.LiteBase lBase = 
											com.cannontech.database.cache.DefaultDatabaseCache.getInstance().handleDBChangeMessage(dbChange[i]);
										Util.getConnToDispatch().write(dbChange[i]);
									}
								}	
							}
						}
						catch(com.cannontech.database.TransactionException te) {
							CTILogger.error("Couldn't insert image", te);
						}
					}
				});
				
				d.setModal(true);
				d.getContentPane().add(yPanel);
				d.setSize(650, 500);

				d.setLocationRelativeTo(parent);

				d.show();
				
				if( yPanel.getReturnResult() == YukonImagePanel.OK_OPTION ) {		
					LiteYukonImage img = yPanel.getSelectedLiteImage();
					if(img != null) {
						staticImage.setYukonImage(img);
						getImageNameLabel().setText(img.getImageName());
						getImageNameLabel().setIcon(new javax.swing.ImageIcon(img.getImageValue()));
					}
				}		
			}
		});
		// user code end
	}
	/**
	 * main entrypoint - starts the part when it is run as an application
	 * @param args java.lang.String[]
	 */
	public static void main(java.lang.String[] args) {
		try {
			javax.swing.JFrame frame = new javax.swing.JFrame();
			StaticImageEditorPanel aStaticImageEditorPanel;
			aStaticImageEditorPanel = new StaticImageEditorPanel();
			frame.setContentPane(aStaticImageEditorPanel);
			frame.setSize(aStaticImageEditorPanel.getSize());
			frame.addWindowListener(new java.awt.event.WindowAdapter() {
				public void windowClosing(java.awt.event.WindowEvent e) {
					System.exit(0);
				};
			});
			frame.show();
			java.awt.Insets insets = frame.getInsets();
			frame.setSize(
				frame.getWidth() + insets.left + insets.right,
				frame.getHeight() + insets.top + insets.bottom);
			frame.setVisible(true);
		} catch (Throwable exception) {
			System.err.println(
				"Exception occurred in main() of com.cannontech.common.gui.util.DataInputPanel");
			exception.printStackTrace(System.out);
		}
	}
	/**
	 * This method was created in VisualAge.
	 * @param o java.lang.Object
	 */
	public void setValue(Object o) {
		staticImage = (StaticImage) o;

		getLinkToPanel().setLinkTo(staticImage.getLinkTo());
				
		getImageNameLabel().setText(staticImage.getYukonImage().getImageName());
		getImageNameLabel().setIcon(new javax.swing.ImageIcon(staticImage.getYukonImage().getImageValue()));
				
	}
}
