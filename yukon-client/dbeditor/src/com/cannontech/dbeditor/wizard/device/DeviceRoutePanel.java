package com.cannontech.dbeditor.wizard.device;

/**
 * This type was created in VisualAge.
 */
import java.awt.Dimension;
import java.util.List;
import java.util.Vector;

import com.cannontech.database.data.device.CarrierBase;
import com.cannontech.database.data.device.DeviceBase;
import com.cannontech.database.data.device.MCT310;
import com.cannontech.database.data.device.MCT310ID;
import com.cannontech.database.data.device.MCT310IDL;
import com.cannontech.database.data.device.MCT310IL;
import com.cannontech.database.data.device.MCT410_KWH_Only;
import com.cannontech.database.data.device.RepeaterBase;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.data.point.PointFactory;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.data.point.StatusPoint;
import com.cannontech.database.data.route.CCURoute;
import com.cannontech.database.data.route.MacroRoute;
import com.cannontech.database.data.route.RouteBase;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.point.PointStatus;
import com.cannontech.database.db.state.StateGroupUtils;
import com.cannontech.dbeditor.editor.regenerate.RegenerateRoute;

public class DeviceRoutePanel
	extends com.cannontech.common.gui.util.DataInputPanel {
	private javax.swing.JLabel ivjRouteLabel = null;
	private javax.swing.JComboBox ivjRouteComboBox = null;
	/**
	 * Constructor
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	public DeviceRoutePanel() {
		super();
		initialize();
	}
	/**
	 * Comment
	 */
	public void addRouteButton_ActionPerformed(
		java.awt.event.ActionEvent actionEvent)
		throws Throwable {
		/* THIS SHOULD BE UPDATED OR REMOVED!	
			try
			{		
				com.cannontech.database.db.setup.gui.route.RouteWizardController rwc = new com.cannontech.database.db.setup.gui.route.RouteWizardController();
		
				Component comp = this;
			    do
			    {
					comp = comp.getParent();
			    }
				while( !( comp instanceof java.awt.Frame ) );
			
				rwc.doModalSetup((java.awt.Frame) comp);
			}
			catch(Throwable t )
			{
				com.cannontech.clientutils.CTILogger.error( t.getMessage(), t );
				throw t;
			}
			*/
	}
	/**
	 * Comment
	 */
	public void editRouteButton_ActionPerformed(
		java.awt.event.ActionEvent actionEvent) {
		return;
	}
	/**
	 * This method was created in VisualAge.
	 * @return java.awt.Dimension
	 */
	public Dimension getMinimumSize() {
		return getPreferredSize();
	}
	/**
	 * This method was created in VisualAge.
	 * @return java.awt.Dimension
	 */
	public Dimension getPreferredSize() {
		return new Dimension(350, 200);
	}
	/**
	 * Return the RouteComboBox property value.
	 * @return javax.swing.JComboBox
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JComboBox getRouteComboBox() {
		if (ivjRouteComboBox == null) {
			try {
				ivjRouteComboBox = new javax.swing.JComboBox();
				ivjRouteComboBox.setName("RouteComboBox");
				// user code begin {1}

				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjRouteComboBox;
	}
	/**
	 * Return the RouteLabel property value.
	 * @return javax.swing.JLabel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JLabel getRouteLabel() {
		if (ivjRouteLabel == null) {
			try {
				ivjRouteLabel = new javax.swing.JLabel();
				ivjRouteLabel.setName("RouteLabel");
				ivjRouteLabel.setFont(new java.awt.Font("dialog", 0, 14));
				ivjRouteLabel.setText(
					"Select the route used with this device:");
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjRouteLabel;
	}
	/**
	 * This method was created in VisualAge.
	 * @return java.lang.Object
	 * @param val java.lang.Object
	 */
	public Object getValue(Object val) 
   {
		((CarrierBase) val).getDeviceRoutes().setRouteID(new Integer(((com.cannontech.database.data.lite.LiteYukonPAObject) getRouteComboBox().getSelectedItem()).getYukonID()));
		DBPersistent chosenRoute = LiteFactory.createDBPersistent((LiteBase) getRouteComboBox().getSelectedItem());

		try 
      {
         chosenRoute = com.cannontech.database.Transaction.createTransaction(
                           com.cannontech.database.Transaction.RETRIEVE,
                           chosenRoute).execute();

		} catch (com.cannontech.database.TransactionException t) 
      {
			com.cannontech.clientutils.CTILogger.error(t.getMessage(), t);
		}

		Integer deviceID = ((RouteBase) chosenRoute).getDeviceID();
		//special cases for some MCTs
		if( val instanceof MCT310 
			 || val instanceof MCT310IL 
			 || val instanceof MCT310ID
			 || val instanceof MCT310IDL
			 || val instanceof MCT410_KWH_Only ) {

			com.cannontech.database.data.multi.MultiDBPersistent newVal = new com.cannontech.database.data.multi.MultiDBPersistent();
			((DeviceBase) val).setDeviceID(com.cannontech.database.db.pao.YukonPAObject.getNextYukonPAObjectID());

			newVal.getDBPersistentVector().add(val);


			//accumulator point is automatically added
			com.cannontech.database.data.point.PointBase newPoint = com.cannontech.database.data.point.PointFactory.createPoint(com.cannontech.database.data.point.PointTypes.PULSE_ACCUMULATOR_POINT);

			int pointID = com.cannontech.database.cache.functions.PointFuncs.getMaxPointID();


			//always create the PulseAccum point
         newVal.getDBPersistentVector().add( 
            PointFactory.createPulseAccumPoint(
               "kWh",
               ((DeviceBase) val).getDevice().getDeviceID(),
               new Integer(++pointID),
               PointTypes.PT_OFFSET_TOTAL_KWH,
               com.cannontech.database.data.point.PointUnits.UOMID_KWH,
               .01) );

			//only certain devices get the DemandAccum point auto created
			if( val instanceof MCT310IL
				 || val instanceof MCT310IDL ) {
	
	         newVal.getDBPersistentVector().add( 
	            PointFactory.createDmdAccumPoint(
	               "kW-LP",
	               ((DeviceBase) val).getDevice().getDeviceID(),
	               new Integer(++pointID),
	               PointTypes.PT_OFFSET_LPROFILE_KW_DEMAND,
	               com.cannontech.database.data.point.PointUnits.UOMID_KW,
	               .01) );
				 }



			if (val instanceof MCT310ID
				 || val instanceof MCT310IDL ) 
			{
				 	
				//an automatic status point is created for certain devices
				//set default for point tables
				PointBase newPoint2 = PointFactory.createNewPoint(
	               new Integer(++pointID),
						PointTypes.STATUS_POINT,
						"DISCONNECT STATUS",
						((DeviceBase) val).getDevice().getDeviceID(),
						new Integer(PointTypes.PT_OFFSET_TOTAL_KWH));


				newPoint2.getPoint().setStateGroupID(
						new Integer(StateGroupUtils.STATEGROUP_THREE_STATE_STATUS) );

				((StatusPoint) newPoint2).setPointStatus(
								new PointStatus(newPoint2.getPoint().getPointID()) );

				newVal.getDBPersistentVector().add(newPoint2);
			}

			//returns newVal, a vector with MCT310 or MCT310IL & accumulator point & status point if of type MCTID
			return newVal;

		} else if (val instanceof RepeaterBase) {
			com.cannontech.database.data.multi.MultiDBPersistent newVal = new com.cannontech.database.data.multi.MultiDBPersistent();
			newVal.getDBPersistentVector().add(val);

			((DeviceBase) val).setDeviceID(com.cannontech.database.db.pao.YukonPAObject.getNextYukonPAObjectID());
			
			Integer pointID = 
				new Integer( com.cannontech.database.db.point.Point.getNextPointID() );
         
			//A status point is automatically added to each repeater
			com.cannontech.database.data.point.PointBase newPoint = PointFactory.createNewPoint(
				pointID,
				com.cannontech.database.data.point.PointTypes.STATUS_POINT,
				"COMM STATUS",
				((DeviceBase) val).getDevice().getDeviceID(),
					new Integer(PointTypes.PT_OFFSET_TRANS_STATUS) );
         
				newPoint.getPoint().setStateGroupID( 
					new Integer(com.cannontech.database.db.state.StateGroupUtils.STATEGROUP_TWO_STATE_STATUS) );
   
				((com.cannontech.database.data.point.StatusPoint) newPoint).setPointStatus(
					new com.cannontech.database.db.point.PointStatus(pointID));

			newVal.getDBPersistentVector().add(newPoint);

			//if the chosen route is a macro route then the generated route will be copied from
			//the first route in the macro 
			if (chosenRoute instanceof MacroRoute) {
				if (((MacroRoute) chosenRoute).getMacroRouteVector().size()> 0) {
					com.cannontech.database.db.route.MacroRoute firstRoute = (com.cannontech.database.db.route.MacroRoute) ((MacroRoute) chosenRoute).getMacroRouteVector().firstElement();

					com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
					synchronized (cache) {
						List routes = cache.getAllRoutes();
						DBPersistent rt = null;
						

						for (int i = 0; i < routes.size(); i++) {

							if (firstRoute.getSingleRouteID().intValue()== ((LiteBase) routes.get(i)).getLiteID()) {
								chosenRoute = LiteFactory.createDBPersistent((LiteBase) routes.get(i));
								break;
							}
						}

						try 
                  {
                     chosenRoute = com.cannontech.database.Transaction.createTransaction(
                                       com.cannontech.database.Transaction.RETRIEVE,
                                       chosenRoute).execute();

						} catch (
							com.cannontech.database.TransactionException t) {
							com.cannontech.clientutils.CTILogger.error(t.getMessage(),t);

						}

					}

				}

			}
			//create new route to be added - copy from the chosen route and add new repeater to it
			//A route is automatically added to each transmitter
			if (chosenRoute instanceof CCURoute) {
			com.cannontech.database.data.route.RouteBase route = com.cannontech.database.data.route.RouteFactory.createRoute(com.cannontech.database.data.pao.RouteTypes.STRING_CCU);
			Integer routeID = com.cannontech.database.db.pao.YukonPAObject.getNextYukonPAObjectID();
			route.setRouteName(((DeviceBase) val).getPAOName());

			//set default values for route tables possibly using same values in chosen route		
			route.setDeviceID(((RouteBase) chosenRoute).getDeviceID());
			((CCURoute) route).getCarrierRoute().setBusNumber(((CCURoute) chosenRoute).getCarrierRoute().getBusNumber());
			((CCURoute) route).setRepeaterVector(((CCURoute) chosenRoute).getRepeaterVector());

			//add the new repeater to this route
			com.cannontech.database.db.route.RepeaterRoute rr = new com.cannontech.database.db.route.RepeaterRoute(route.getRouteID(),((DeviceBase) val).getPAObjectID(),new Integer(7),new Integer(((CCURoute) chosenRoute).getRepeaterVector().size()+ 1));
			
			if (((CCURoute)route).getRepeaterVector().size() >= 7) 
				((CCURoute)route).setRepeaterVector(new Vector());
				
				((CCURoute) route).getRepeaterVector().addElement(rr);
				
			
			route.setDefaultRoute(com.cannontech.common.util.CtiUtilities.getTrueCharacter().toString());
			java.util.Vector regRoute =RegenerateRoute.resetRptSettings(RegenerateRoute.getAllCarrierRoutes(),false,route,false);
			newVal.getDBPersistentVector().add(regRoute.firstElement());

			return newVal;
		}
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
	 * Initialize the class.
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void initialize() {
		try {
			// user code begin {1}
			// user code end
			setName("DeviceRoutePanel");
			setLayout(new java.awt.GridBagLayout());
			setSize(350, 200);

			java.awt.GridBagConstraints constraintsRouteLabel =
				new java.awt.GridBagConstraints();
			constraintsRouteLabel.gridx = 1;
			constraintsRouteLabel.gridy = 1;
			constraintsRouteLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsRouteLabel.insets = new java.awt.Insets(5, 0, 5, 0);
			add(getRouteLabel(), constraintsRouteLabel);

			java.awt.GridBagConstraints constraintsRouteComboBox =
				new java.awt.GridBagConstraints();
			constraintsRouteComboBox.gridx = 1;
			constraintsRouteComboBox.gridy = 2;
			constraintsRouteComboBox.fill =
				java.awt.GridBagConstraints.HORIZONTAL;
			constraintsRouteComboBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsRouteComboBox.insets = new java.awt.Insets(5, 0, 5, 15);
			add(getRouteComboBox(), constraintsRouteComboBox);
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
			javax.swing.JFrame frame = new javax.swing.JFrame();
			DeviceRoutePanel aDeviceRoutePanel;
			aDeviceRoutePanel = new DeviceRoutePanel();
			frame.getContentPane().add("Center", aDeviceRoutePanel);
			frame.setSize(aDeviceRoutePanel.getSize());
			frame.setVisible(true);
		} catch (Throwable exception) {
			System.err.println(
				"Exception occurred in main() of com.cannontech.common.gui.util.DataInputPanel");
			com.cannontech.clientutils.CTILogger.error(
				exception.getMessage(),
				exception);
			;
		}
	}
	/**
	 * Comment
	 */
	public void routeList_ValueChanged(
		javax.swing.event.ListSelectionEvent listSelectionEvent) {
		fireInputUpdate();
	}
	/**
	 * This method was created in VisualAge.
	 * @param val java.lang.Object
	 */
	public void setValue(Object val) {
		com.cannontech.database.cache.DefaultDatabaseCache cache =
			com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
		synchronized (cache) {
			java.util.List allRoutes = cache.getAllRoutes();
			if (getRouteComboBox().getModel().getSize() > 0)
				getRouteComboBox().removeAllItems();

			int routeType = 0;
			for (int i = 0; i < allRoutes.size(); i++) {
				routeType =
					((com
						.cannontech
						.database
						.data
						.lite
						.LiteYukonPAObject) allRoutes
						.get(i))
						.getType();
				if (routeType
					== com.cannontech.database.data.pao.RouteTypes.ROUTE_CCU
					|| routeType
						== com
							.cannontech
							.database
							.data
							.pao
							.RouteTypes
							.ROUTE_MACRO) {
					getRouteComboBox().addItem(allRoutes.get(i));
				}

			}
		}
	}
	/**
	 * 
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private static void getBuilderData() {
		/*V1.1
		**start of data**
			D0CB838494G88G88GEBF954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135998BF0D455B588822D14410A53D46DCCC0F4D21B0ED0F1E494E7E8C6E62A26AD352034B2B5CE2D43F430E5865AC124634DEE3204CFA0FC02D122AEBF95CD171F1AE589ED92B6A11BACA4A49182E261ED76A5F9495BFD1B775EE6B79020675C7B6E5B17E4B728A3BBF3763EFB4E3D67F74FBD675E77487677B38A72EC5B8949ABA0462FA20F105C5D04DC18D6FD1E635244EC87317CF681ECA1FFA84807
			F68A50A6CE48F694132CAB05501F817D62F1590E3760F99B19F56572FB7010C20E9BE8ABDE29299BDD4E16EAAEE7AD6D3F7879C6E8B781E4828E5B15A8132C783C54406798F88EF9B6C296F66218CD95E5864E8D7DE600A9GB3BA597839502E2312EF77380DF1976F4CA0AB1F7950D604FCACBCB914F53139D5C66BA48F67AD6DC2D995959B0DB9F1684F84C0636D643152A350D6B677F67D27C38939BA04F0D8896BE2C737ABA6DD915420ABA876CB9D22476325ED0B925545E6A1AC4AA4580DFCEFD494D2BE04
			14C3FFB94575C7D1D6A6BC4F83D8EC620F77A37ED3A95B618788CA5C3E639FD69A7A37DFFD18DCFCA1EC5F1C423EC10959B5C3CA58B7B53AFA6D5BF4AD4E54204FF301165F136DA88528843888B088301346C0FB754750D6877493CAA824042BA3119AB9281ED202228C4F9E8FD08CDC0B582128C1C258BA9F2FA8CF224FE84066B5FE63F94CA719C7781AE735BCC816BC52E8DB1B44A7CBB28F5A2676B0DFE475A4FC42E40FCBA9BB27E7E8CC8E15FD0ED8E57B47AB7602A432E7AF5AEF4BB5782C34485E5B0BEB
			7DD79A835B61B94F088F06DFC9715D96BC0B4FC10A47FE9CE8CBFAF9EC34EF623A78736FA145077CC97D101EDDED2B12198E6D72F0DDE651389BA7B319F926AE0FD37C948B1E69F2221F6F157B00C6C2598E96DF973C38DF0350CF83C887188930GE099G8973981BD6FD60EB46D833206AB5D1C98E0AAAE1F3AF546C00D60C6B2A608BEB3A906E904F8A2AA484E4D1A346180E9A74C1E0483CEE9A7B9DA063249896D5C11714B0F097C222AE2A9A9B53D733854728A22536C9A1918404A204739D2859862DACE8
			7A971120200B09910CBE26865724DFB88B9C0181BCB33FCC28C5FFB941FEB7C03D6907B514DF2328C23A70F87C0A529B0D3421A5A4BD0CFE3E17660E0B702C07390F4756A1EE8974D71AFC0E1C58EA616315BAD0FDC19D60FBECF994F3F22142E366EF6DBCE63CC58F1072C58DC9E386E12F42E225D2993EF7D6D31E28976B5F96ED374CABD4126F1FEF8BCC463DC3E4583FC37E1B4C35DAF39257AA887A2781E4A85C476193CE9633B502A672A8EDD2A14044E04263CC4F477A092973BC4B332F3F7DBB56F9240F
			8237D48673C5500B75C76C97433AD70C0861A0BCE8862D570BB1E66C4FCE451C53C77191584FA7E083E9A6BE6645BDA7683AAA8520F4360A3AAE053B92749BD416968DC49229004B9A6F453C2B44E0BF1B636F7062DE68D042E111AE0AB945207F006EC5D196C3207489A59A56997E2EDA0C67DEF1404885BF2A4543CD3FA0C7B12F303340CCE1835A20CAA1D8EACCCD24AC027B74979525969F4D011841B34DF2E873A3FC4F457670BD37774AB4B26F52BEDB321A117E0057960C303D930FA4769C53E9E2DDF25D
			A7531CE060B5266B74F545508EBF1430987F4C07FC5201FF9640D3919E636BCFA21D6791A338CFA8324CDCE97A58D60F3CB531AFAAC27A450455E87AEDDD7D56E13433068F99FDCBBD7AD03664BA266F5ACBD80B131D333A2212AB2ACB1859849D02AA925529AD6CFCD566C65B11471288BFC7FDFE0C56563EECC7B900370F2F416F0A78B9EAEAEF86493D7F1D1467440BFD4C77E9FD09B5F00E6567289B8DA8F39E509674F15FCD2A376672EACDD3BAA4DAC68C3B0F792CEB44F0D53E7550F6CA2A267BE0934539
			0F6AFC28AF569983ED6C6DDD6E616715D77A92F9F6D55F481C4B6A78C3549F9BD5D6478BCC7C23946F3460D9FD2F24F86CFF8A34F415571E390D9867EB20DF2AE5BB8E825483F483CC54389FFE6343737228355D876B2A068539F54826B6EC9F57E0E77B5068CFECA8607B90E31656C5B7DD7399A0F38EG4359BD34AEC2FF2546EB524CE308AB55C1D71D631E20B8BF74E593D7F30C67CC9C07633F7B3C7C25A903457B2D484B739BD367651C4664F979394664F9F9F1639606879E56D3982767B60E1E3717B6A6
			4B5BF6CAEBF2BBE8FC8FC0DE023855F17DBA5DBC07CE027E8C009C00DCE3AD184FE645B0F7F442218DC435C6448E32B00AF35F717039D550EF83508126824C86E0B25FBE04FB0DE6C23F90006B1921CB45A7C59C8F919BD0EA945CEFED7D487735E35BADBEAE6BE76730AAE899FDF24CCA3F4F20BF85AD5BC7135D7C1C3C984747781D7250E5BCA753DC57A3748BAEA126FBBCA7D5A9D8A3F47929D7D8EEFB1E46B94E2F0F715C16FF94F926C1BF9DE0E60C67367887BC37E5BDF88F7127B5A64DED39ED07ED3F0A
			B11FAE0BA5F2DBA65C91194CAA8F1779D1BC5BF1B90E383047EAA77A6DCE9860021651B30650B1AE9691AB7D39A1F45477C91B151EBF444F6823B346FCE65B337B38EDEB1E4DA47223C7D3666D4CB833A947E81B4F6EA1FB3F7F46DA6677951D25D63F47DDDA8C824963E92091416C5FDBD9E86C597C816E5BEBF5563AB1F773E15B069472AB87185C0B83895F1603EF197CAEF9E7F279E664B19D664778BB037B86F80E1ABAA277FF3D3CB37DDB4EFB719B4FE3B565F99A1F8B87065FA5DF22785C0161F74975C7
			799ED9863466AB3C567C73942E55EC68AFGD88AC02EE6BB1CG15D7F92D3966BFC8EED2EB5A14089F9222ECC9E14C3EDA4F37FB6F704B35BC96E6BFBE1D4C997FFADE2AD8E83E4AE2A0E234CB5B76D0BC336FFBCDE8DF8650668264822C8258B8186D58BB486D1B6FAFE7765586A53DDA57050EEEFAFA60461D1544D8820D891159363F52EA1BFB1049779B6D654111750D6934229977218E74A9G19G73GF2CDFDB61F3AE9ED2F0DA72D6DCC2FE7E223791CDC9B29971B3764A8B11F7FE2F9AEBEFA6B6FD37B
			EF298C6657B7CF252E5B0E53496B764653496B765653235765F7CE273ECF6DBAED3DCF99F705960C096D30B687815AG3A81862F7118F87874EE9693C96233B65885C5153E2551397C9FB625367547CDB7FF37B33D896BF8C051F5A5047CB9BE338957CBD7A29449F0BFEFC29F29D2D7374E069A78C71AB03F4AE2A7C53373E3954D7B69E0D766B5FE269CD3023839504FB1F12F1670F366E25387EFB4273649F6A6797A951EC93EFE1B4E0CFE6E5AF1A6557AB27DDE0EF17DBE3A4E75BB5E12DABFEF4B4DFDFE2A
			055EDD40D7ED83911161DADA708C27F4F6EA22393667DA0ACD1CCFEBD6B489AFC06C1CF52E926F571DF0271BFD9D758CD1FF4761F95CF5FEC6CBAF216F14C019D557715B4223DF60DE0F5C40717FAA719AFB26724648BCE19F864DB086E18FE139B9F8A3D1F7983FD5B14EEFCE8AFE8596FEB996FE084F3570E33A6E38C088BFD3C6CBF0CF044518139F6FDCF5E078C73B0312A0ABDD3B4B1D874B1D07BE5D4FEBCAC64C7A7D423D3EA2693B8104E5062EA44D7886B2E10F416337DFDAF9945F334B161702075B18
			6BCDCB1CD158394335035F2563C0FB1F5AC2C8A1C97C182D133F42E7A677650D3C9E167D6CDE62FD61753CBDA9644EB46411B116EFAFBDEB0CF3677EB8176D9EB31A6CD57F43E7161B9E380474880C1FB406477A1797BE01762C50E3D4A7AC1D0ACAB392A01BD5A5A22A7AG4352B793862F0C4B4857F8BFFDE1DFAA9E10559AE0CD1B82BDE2075EA6F491DEA70E6C305636AC50E75E18516F11CC6E33824661C2983B9CE07BED590EF200AA00F6G99A08DE0AAC09640D3G0B811681103160B7003DG6E315C7E
			5FFF35FF947B0DCB86CD4A2AA0E1DAB674F7452D7A7B015F6531236B4F64BDF4891F4DFC44DCEA50E6DD425AA105A18D8832F4053E8EE9050161AE162B9AE83C69E359DEE238A60A1BE460981F170454796BD56166796B6FC2721A718FE1E44DD8ADA42F99A4E02D998CD79040F86D821F361B63561B3873A6EEF3E01D01BBA78575EE93DF92D8EF606BC5944871BB8328D70C625A8D1C5302BBEF607685B08703495D0A4A67FE9C20F9D612E556F751B1D298D232690FA3D4C7A9A2D874F61B387366DA5446C72E
			CFC35C3ABEACC78A9FECCC2CB3B511614B62C589BCB71130EF4DCF5AE5C816D92F11821DFC58AA62ABC6275EADBAD53C09BB231A98F446A43D9BF01266B43E31EAAAA99A12D167CF7B6C551CBF1D556C4461A3EFE3F1E995D59AEBE290DF969A9FE26A483A0882415E8AF89F4957E8D32069833268A75BBBDB70E525260B41EA9E451ACECA7CA869G3EF8BCD0AFE85DBAC654B1BF498F63A7A3321511E5A15CF500057431B8FEA84A2B26F9049DE8C15FEDD4DF6462B239ECFBE2733EDD8D2F4C3193DB9DD9AFAB
			C230CE6800CD6AA36B74EED5543A95B9980F7C370AB2AA24759C6F98F6E03753B49F0DF535C15C1BCA7746FFB229E8E9D19DDC2D63AAB9C0C7E2ADF519A302A73A8595C4092ACF47CFE00A3A57240DF9CF7F6057A75F3AE8A3F90611FD64BA3147A3BAF1F48AA1C99E0087D63FB4242C55C1DBD588EB1D0A9A824D1C43B463A4D3006B2E278F5E7D535597280044D41D3C4FDC89D38FF409CA68C0F5E7279496CD8660D7E07F2E499E4738060D599A3F3324E1511B810883AF31078E9E247F07687FE17CBFC4B107
			A86650E1F06EF2B26AEFB8BD4EED2641837AEC267A60723B6AE08575BF3E76747497AB17AF836B6AC881FDDD890BD01C98F5BA9A8A086A782C3B5F5DB0B5778EDC238806CA9C79EE081B9C51C29F5D2C397DC7BC76BF5FEF3711FC9FF1306485410B99880217A6A89F31C37222F1D7E0D13600AADBEA41E27AACE075FA7F8D6BB9E1556D25A94F9AB993D87E4F1D1028572CE6ADF0E0CC16CD043AB311579D27635FA4F54DEC8D89322C8BF2EFF5B8087533E6C09793E75EC5143F464798C4C6DBC25F4174D8667C
			9FD0CB8788E2F79937678DGG88A3GGD0CB818294G94G88G88GEBF954ACE2F79937678DGG88A3GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGA18EGGGG
		**end of data**/
	}
}
