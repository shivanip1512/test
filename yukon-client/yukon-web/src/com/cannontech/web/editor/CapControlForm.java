package com.cannontech.web.editor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.myfaces.custom.tree2.TreeNode;
import org.apache.myfaces.custom.tree2.TreeNodeBase;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.NativeIntVector;
import com.cannontech.database.PoolManager;

import com.cannontech.database.TransactionException;
import com.cannontech.database.cache.functions.PAOFuncs;
import com.cannontech.database.cache.functions.PointFuncs;
import com.cannontech.database.data.capcontrol.CCYukonPAOFactory;
import com.cannontech.database.data.capcontrol.CapBank;
import com.cannontech.database.data.capcontrol.CapBankController;
import com.cannontech.database.data.capcontrol.CapControlFeeder;
import com.cannontech.database.data.capcontrol.CapControlSubBus;
import com.cannontech.database.data.capcontrol.ICapBankController;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.lite.LiteComparators;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.PAOFactory;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.data.pao.YukonPAObject;
import com.cannontech.database.data.point.PointFactory;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.data.point.PointUnits;
import com.cannontech.database.db.capcontrol.CCFeederBankList;
import com.cannontech.database.db.capcontrol.CCFeederSubAssignment;
import com.cannontech.database.db.capcontrol.CapControlStrategy;
import com.cannontech.database.db.capcontrol.DeviceCBC;

/**
 * @author ryan
 *
 */
public class CapControlForm extends DBEditorForm
{
	private String paoDescLabel = "Description";
	private String childLabel = "Children";
	private boolean editingCBCStrategy = false;
	private boolean editingController = false;
	
	//contains <Integer(stratID), CapControlStrategy>
	private HashMap cbcStrategiesMap = null;

	//contains LiteYukonPAObject
	private List unassignedBanks = null;

	//contains LiteYukonPAObject
	private List unassignedFeeders = null;

	//contains <Integer(cbcID), DeviceCBC>
	private HashMap cbcDevicesMap = null;

	//possible selection types for every wizard panel
	private CBCWizardModel wizData = null;
	
	//possible editor for the CBC a CapBank belongs to
	private CBControllerEditor cbControllerEditor = null;


	
	//selectable items that appear in lists on the GUI
	private SelectItem[] kwkvarPaos = null;
	private SelectItem[] kwkvarPoints = null;
	private SelectItem[] cbcStrategies = null;


	/**
	 * default constructor
	 */
	public CapControlForm()
	{
		super();
	}

	/**
	 * Hold all the CBCStrategies in memory for quicker access.
	 */
	public SelectItem[] getCbcStrategies() {
		
		if( cbcStrategies == null ) {
			
			CapControlStrategy[] cbcDBStrats = CapControlStrategy.getAllCBCStrategies();
			cbcStrategies = new SelectItem[cbcDBStrats.length];

			for( int i = 0; i < cbcDBStrats.length; i++ ) {
				cbcStrategies[i] = new SelectItem( //value, label
					cbcDBStrats[i].getStrategyID(),
					cbcDBStrats[i].getStrategyName() );
					
				getCbcStrategiesMap().put( 
					cbcDBStrats[i].getStrategyID(),
					cbcDBStrats[i] );
			}
		}

		return cbcStrategies;
	}

	/**
	 * Hold a the CBCStrategies in memory for quicker access.
	 */
	public HashMap getCbcStrategiesMap() {
		
		if( cbcStrategiesMap == null ) {
			cbcStrategiesMap = new HashMap(32);
			
			//force us to init our map with the correct data
			getCbcStrategies();
		}
		
		return cbcStrategiesMap;
	}
	
	/**
	 * Hold a the CBCDevices in memory for quicker access.
	 */
	public HashMap getCbcDevicesMap() {
		
		if( cbcDevicesMap == null ) {
			
			DeviceCBC[] devCBCs = DeviceCBC.getAllDeviceCBCs();
			cbcDevicesMap = new HashMap( devCBCs.length );
			
			for( int i = 0; i < devCBCs.length; i++ )
				cbcDevicesMap.put( devCBCs[i].getDeviceID(), devCBCs[i] );			
		}
		
		return cbcDevicesMap;
	}

	/**
	 * Hold a the KwkvarPaos SelectableItems in memory for quicker access.
	 */
	public SelectItem[] getKwkvarPaos() {
		
		if( kwkvarPaos == null ) {
			
			PointLists pLists = new PointLists();
			LiteYukonPAObject[] lPaos =
				pLists.getPAOsByUofMPoints( PointUnits.CAP_CONTROL_VAR_UOMIDS );
			
			SelectItem[] temp = new SelectItem[lPaos.length];
			for( int i = 0; i < temp.length; i++ )
				temp[i] = new SelectItem( //value, label
					new Integer(lPaos[i].getLiteID()),
					lPaos[i].getPaoName() );

			//add the none PAObject to this list
			kwkvarPaos = new SelectItem[temp.length+1];
			kwkvarPaos[0] = new SelectItem( new Integer(LiteYukonPAObject.LITEPAOBJECT_NONE.getLiteID()), LiteYukonPAObject.LITEPAOBJECT_NONE.getPaoName() );
			System.arraycopy( temp, 0, kwkvarPaos, 1, temp.length );
		}

		return kwkvarPaos;
	}

	/**
	 * Hold a the KwkvarPoints SelectableItems in memory for quicker access.
	 */
	public SelectItem[] getKwkvarPoints() {
		
		if( kwkvarPoints == null ) {			
			kwkvarPoints = new SelectItem[1];
			kwkvarPoints[0] = new SelectItem( new Integer(LitePoint.NONE_LITE_PT.getLiteID()), LitePoint.NONE_LITE_PT.getPointName() );
		}

		return kwkvarPoints;
	}
	
	/**
	 * Returns the currently selected strategyID for the given Sub or Feeder
	 *
	 */
	public int getCurrentStrategyID() {

		int stratID = CtiUtilities.NONE_ZERO_ID;
		
		if( getPAOBase() instanceof CapControlFeeder )
			stratID = ((CapControlFeeder)getPAOBase()).getCapControlFeeder().getStrategyID().intValue();
		else if( getPAOBase() instanceof CapControlSubBus )
			stratID = ((CapControlSubBus)getPAOBase()).getCapControlSubstationBus().getStrategyID().intValue();

		return stratID;
	}
	
	/**
	 * Returns all available VAR points
	 *
	 */
	public TreeNode getVarTreeData() {
		
		TreeNode rootData = new TreeNodeBase("root", "VAR Points", true);
		
		PointLists pLists = new PointLists();
		LiteYukonPAObject[] lPaos =
			pLists.getPAOsByUofMPoints( PointUnits.CAP_CONTROL_VAR_UOMIDS );

		TreeNodeBase[] paos = new TreeNodeBase[lPaos.length];
		for( int i = 0; i < lPaos.length; i++ ) {

			paos[i] = new TreeNodeBase( //type, description, leaf
				"paos", lPaos[i].getPaoName(), String.valueOf(lPaos[i].getYukonID()), true);
			
			LitePoint[] lPoints = pLists.getPointsByUofMPAOs(lPaos[i].getYukonID(), PointUnits.CAP_CONTROL_VAR_UOMIDS);							
			for( int j = 0; j < lPoints.length; j++ ) {
				paos[i].getChildren().add(
					new TreeNodeBase("points", lPoints[j].getPointName(), 
						String.valueOf(lPoints[j].getPointID()), false) );
			}

			rootData.getChildren().add( paos[i] );
		}


		return rootData;
	}

	/**
	 * Returns all available WATT points
	 *
	 */
	public TreeNode getWattTreeData() {
		
		TreeNode rootData = new TreeNodeBase("root", "WATT Points", true);
		
		PointLists pLists = new PointLists();
		LiteYukonPAObject[] lPaos =
			pLists.getPAOsByUofMPoints( PointUnits.CAP_CONTROL_WATTS_UOMIDS );

		TreeNodeBase[] paos = new TreeNodeBase[lPaos.length];
		for( int i = 0; i < lPaos.length; i++ ) {

			paos[i] = new TreeNodeBase( //type, description, leaf
				"paos", lPaos[i].getPaoName(), String.valueOf(lPaos[i].getYukonID()), true);
			
			LitePoint[] lPoints = pLists.getPointsByUofMPAOs(lPaos[i].getYukonID(), PointUnits.CAP_CONTROL_WATTS_UOMIDS);							
			for( int j = 0; j < lPoints.length; j++ ) {
				paos[i].getChildren().add(
					new TreeNodeBase("points", lPoints[j].getPointName(), 
						String.valueOf(lPoints[j].getPointID()), false) );
			}

			rootData.getChildren().add( paos[i] );
		}


		return rootData;
	}

	/**
	 * Returns all available Volt points
	 *
	 */
	public TreeNode getVoltTreeData() {
		
		TreeNode rootData = new TreeNodeBase("root", "Volt Points", true);
		
		PointLists pLists = new PointLists();
		LiteYukonPAObject[] lPaos =
			pLists.getPAOsByUofMPoints( PointUnits.CAP_CONTROL_VOLTS_UOMIDS );

		TreeNodeBase[] paos = new TreeNodeBase[lPaos.length];
		for( int i = 0; i < lPaos.length; i++ ) {

			paos[i] = new TreeNodeBase( //type, description, leaf
				"paos", lPaos[i].getPaoName(), String.valueOf(lPaos[i].getYukonID()), true);
			
			LitePoint[] lPoints = pLists.getPointsByUofMPAOs(lPaos[i].getYukonID(), PointUnits.CAP_CONTROL_VOLTS_UOMIDS);							
			for( int j = 0; j < lPoints.length; j++ ) {
				paos[i].getChildren().add(
					new TreeNodeBase("points", lPoints[j].getPointName(), 
						String.valueOf(lPoints[j].getPointID()), false) );
			}

			rootData.getChildren().add( paos[i] );
		}


		return rootData;
	}

	/**
	 * Returns all status points that are available for a CapBank to use
	 * for its control point
	 */
	public TreeNode getCapBankPoints() {

		TreeNode rootNode = new TreeNodeBase("root", "Devices With Status Points", false);		
		if( !(getPAOBase() instanceof CapBank) ) return rootNode;


		LiteYukonPAObject[] lPaos = PAOFuncs.getAllUnusedCCPAOs(
					((CapBank)getPAOBase()).getCapBank().getControlDeviceID() );
		
		Vector typeList = new Vector(32);
		Arrays.sort( lPaos, LiteComparators.litePaoTypeComparator );


		int currType = Integer.MIN_VALUE;
		TreeNodeBase paoTypeNode = null;
		TreeNodeBase[] paoNodes = new TreeNodeBase[lPaos.length];

		for( int i = 0; i < lPaos.length; i++ ) {

			//only show CapControl speficic PAOs
			if( !PAOGroups.isCapControl(lPaos[i]) ) continue;


			if( currType != lPaos[i].getType() ) {
				paoTypeNode = new TreeNodeBase( //type, description, isLeaf
					"paoTypes", PAOGroups.getPAOTypeString(lPaos[i].getType()),
					PAOGroups.getPAOTypeString(lPaos[i].getType()), false);

				typeList.add( paoTypeNode );
			}

			paoNodes[i] = new TreeNodeBase( //type, description, isLeaf
				"paos", lPaos[i].getPaoName(), String.valueOf(lPaos[i].getYukonID()), false);
			
			LitePoint[] lPoints = PAOFuncs.getLitePointsForPAObject(lPaos[i].getYukonID());							
			for( int j = 0; j < lPoints.length; j++ ) {
								
				//status points are only allowed in this list
				if( lPoints[j].getPointType() == PointTypes.STATUS_POINT )
					paoNodes[i].getChildren().add(
						new TreeNodeBase("points", lPoints[j].getPointName(), 
							String.valueOf(lPoints[j].getPointID()), true) );
			}

			//only show PAObjects that have 1 or more points
			if( paoNodes[i].getChildren().size() > 0 )
				paoTypeNode.getChildren().add( paoNodes[i] );

			currType = lPaos[i].getType();
		}

		//this list will be a fixed size with a controlled max value
		//java.util.Collections.sort( typeList, DummyTreeNode.comparator);
		//only show types that have 1 or more points
		for( int i = 0; i < typeList.size(); i++ )
			if( ((TreeNodeBase)typeList.get(i)).getChildren().size() > 0 )
				rootNode.getChildren().add( typeList.get(i) );

		return rootNode;
	}

	/**
	 * Event fired when the Var Point selection has changed
	 *
	 */
	public void varPtTeeClick( ActionEvent ae ) {
		
		String val = (String)FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("ptID");
		if( val == null ) return;

		if( getPAOBase() instanceof CapControlFeeder )
			((CapControlFeeder)getPAOBase()).getCapControlFeeder().setCurrentVarLoadPointID( new Integer(val) );
		else if( getPAOBase() instanceof CapControlSubBus )
			((CapControlSubBus)getPAOBase()).getCapControlSubstationBus().setCurrentVarLoadPointID( new Integer(val) );


	}

	/**
	 * Event fired when the Watt Point selection has changed
	 */
	public void wattPtTeeClick( ActionEvent ae ) {
		
		String val = (String)FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("ptID");
		if( val == null ) return;

		if( getPAOBase() instanceof CapControlFeeder )
			((CapControlFeeder)getPAOBase()).getCapControlFeeder().setCurrentWattLoadPointID( new Integer(val) );
		else if( getPAOBase() instanceof CapControlSubBus )
			((CapControlSubBus)getPAOBase()).getCapControlSubstationBus().setCurrentWattLoadPointID( new Integer(val) );
	}

	/**
	 * Event fired when the the CapBank control point selection has changed
	 */
	public void capBankTeeClick( ActionEvent ae ) {
		
		String val = (String)FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("ptID");
		if( val == null ) return;

		if( getPAOBase() instanceof CapBank )
			((CapBank)getPAOBase()).getCapBank().setControlPointID( new Integer(val) );
	}


	/**
	 * Event fired when the Volt Point selection has changed
	 *
	 */
	public void voltPtTeeClick( ActionEvent ae ) {

		String val = (String)FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("ptID");
		if( val == null ) return;

		if( getPAOBase() instanceof CapControlFeeder )
			((CapControlFeeder)getPAOBase()).getCapControlFeeder().setCurrentVoltLoadPointID( new Integer(val) );
		else if( getPAOBase() instanceof CapControlSubBus )
			((CapControlSubBus)getPAOBase()).getCapControlSubstationBus().setCurrentVoltLoadPointID( new Integer(val) );
	}

	
	/**
	 * Restores the object from the database
	 */
	public void initItem( int id ) {

		YukonPAObject paoDB = PAOFactory.createPAObject( id );
		setDbPersistent( paoDB );
		initItem();
	}

	/**
	 * Inits the wizard for the creation of a particular object type
	 */
	public void initWizard( int paoType ) {

		//YukonPAObject paoDB = PAOFactory.createPAObject( id );
		//setDbPersistent( paoDB );
		//initItem();		
		getWizData().setWizPaoType( paoType );

		initPanels( paoType );
	}

	/**
	 * Initialize our current DBPersistent object from the databse
	 */
	protected void initItem() {

		Connection conn = null;
		if( getPAOBase() == null ) return;

		try {
			conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
			getPAOBase().setDbConnection( conn );
			getPAOBase().retrieve();
		}
		catch( SQLException sql ) {
			CTILogger.error("Unable to retrieve YukonPAObject", sql );
		}
		finally {
			getPAOBase().setDbConnection( null );
			
			try {
			if( conn != null ) conn.close();
			} catch( java.sql.SQLException e2 ) { }			
		}


		initPanels( PAOGroups.getPAOType(getPAOBase().getPAOCategory(), getPAOBase().getPAOType()) );
		initEditorPanels();
	}

	/**
	 * Reset any data structures and allow the parent to do its thing
	 * 
	 */
	public void resetForm() {
		
		resetStrategies();
		resetCBCs();

		editingCBCStrategy = false;
		unassignedBanks = null;
		unassignedFeeders = null;


		kwkvarPaos = null;
		kwkvarPoints = null;
		editingController = false;

		super.resetForm();
	}

	/**
	 * Reset CBC Strategy data, forcing them to be reInited
	 * 
	 */
	private void resetStrategies() {		
		cbcStrategiesMap = null;
		cbcStrategies = null;
	}

	/**
	 * Reset CBC Strategy data, forcing them to be reInited
	 * 
	 */
	private void resetCBCs() {		
		cbcDevicesMap = null;
		cbControllerEditor = null;
	}

	/**
	 * All possible panels for this editor go here. Set visible panels and labels
	 * based on type of object. DO NOT access the DB persitent object in
	 * this method since it may be null in the case of a Wizard.
	 */
	private void initPanels( int paoType ) {

		//all panels that are always displayed
		getVisibleTabs().put( "General", new Boolean(true) );

		//all type specifc panels
		getVisibleTabs().put( "BaseCapControl", new Boolean(false) );
		getVisibleTabs().put( "CBCSubstation", new Boolean(false) );
		getVisibleTabs().put( "CBCFeeder", new Boolean(false) );
		getVisibleTabs().put( "CBCCapBank", new Boolean(false) );
		getVisibleTabs().put( "CBCController", new Boolean(false) );


		switch( paoType ) {

			case PAOGroups.CAP_CONTROL_SUBBUS:
				setEditorTitle("Substation Bus");			
				setPaoDescLabel("Geographical Name");
				setChildLabel("Feeders");
				getVisibleTabs().put( "CBCSubstation", new Boolean(true) );				
				break;

			case PAOGroups.CAP_CONTROL_FEEDER:
				setEditorTitle("Feeder");			
				getVisibleTabs().put( "CBCFeeder", new Boolean(true) );
				setPaoDescLabel(null);
				setChildLabel("CapBanks");				
				break;
			
			case PAOGroups.CAPBANK:
				setEditorTitle("Capacitor Bank");			
				setPaoDescLabel( "Street Location" );
				getVisibleTabs().put( "CBCCapBank", new Boolean(true) );

				break;

			case PAOGroups.CAPBANKCONTROLLER:
			case PAOGroups.CBC_FP_2800:
			case PAOGroups.DNP_CBC_6510:
			case PAOGroups.CBC_EXPRESSCOM:
			case PAOGroups.CBC_7010:
			case PAOGroups.CBC_7020:
				setEditorTitle("CBC");
				setPaoDescLabel(null);
				getVisibleTabs().put( "CBCController", new Boolean(true) );
				break;

			//-------- todo ----------
			case PointTypes.ANALOG_POINT:
				break;



			default:
				throw new IllegalArgumentException("Unknown PAO type given, PAO type = " + paoType );
		}

	}
	
	/**
	 * The instance of the underlying base object
	 *
	 */
	public YukonPAObject getPAOBase() {
		return (YukonPAObject)getDbPersistent();
	}


	/**
	 * Fired when the kwkvarPaos component is changed
	 * @param ev
	 */
	public void kwkvarPaosChanged( ValueChangeEvent ev ) {

		if(ev == null || ev.getNewValue() == null) return;

		PointLists pLists = new PointLists();
		LitePoint[] lPoints = pLists.getPointsByUofMPAOs(
						((Integer)ev.getNewValue()).intValue(),
						PointUnits.CAP_CONTROL_VAR_UOMIDS);
		
		SelectItem[] temp = new SelectItem[ lPoints.length ];
		for( int i = 0; i < lPoints.length; i++ )
			temp[i] = new SelectItem(
						new Integer(lPoints[i].getLiteID()),
						lPoints[i].getPointName() );		

		//add the none LitePoint to this list
		kwkvarPoints = new SelectItem[temp.length+1];
		kwkvarPoints[0] = new SelectItem( new Integer(LitePoint.NONE_LITE_PT.getLiteID()), LitePoint.NONE_LITE_PT.getPointName() );
		System.arraycopy( temp, 0, kwkvarPoints, 1, temp.length );
	}
	
	/**
	 * Executes any last minute object updates before writting
	 * the data to the databse. The return value is where the requested
	 * value is redirected as defined in our faces-config.xml
	 * 
	 */
	public void update() {

		//this message will be filled in by the super class
		FacesMessage facesMsg = new FacesMessage();

		try {
			//update the CBCStrategy object if we are editing it
			if( isEditingCBCStrategy() ) {
			
				int stratID = CtiUtilities.NONE_ZERO_ID;
				if( getPAOBase() instanceof CapControlFeeder )
					stratID = ((CapControlFeeder)getPAOBase()).getCapControlFeeder().getStrategyID().intValue();
				else if( getPAOBase() instanceof CapControlSubBus )
					stratID = ((CapControlSubBus)getPAOBase()).getCapControlSubstationBus().getStrategyID().intValue();

				updateDBObject(
					(CapControlStrategy)getCbcStrategiesMap().get( new Integer(stratID) ), facesMsg );

				//clear out the memory of any list of Strategies
				resetStrategies();
				setEditingCBCStrategy( false );
			}

			//update the CBC object if we are editing it
			if( isEditingController() ) {
				updateDBObject(
					getCBContollerEditor().retrieveDB(), facesMsg );

				//clear out the memory of CBCs structures
				resetCBCs();
				setEditingController( false );
			}

			updateDBObject( getPAOBase(), facesMsg );
			
			facesMsg.setDetail( "Database update was SUCCESSFUL" );
		}
		catch( TransactionException te ) {
			//do nothing since the appropriate actions was taken in the super
		}
		finally {
			FacesContext.getCurrentInstance().addMessage("cti_db_update", facesMsg);		
		}
	}

	/**
	 * Creates extra points or any other supporting object for the given parent
	 * based ont he paoType
	 */
	private void createSupportItems( int paoType, int parentID, final FacesMessage facesMsg ) throws TransactionException {
		
		//a status point is automatically added to all capbank controllers
		if( DeviceTypesFuncs.isCapBankController(paoType) ) {
			addDBObject(
				CapBankController.createStatusControlPoint(parentID),
				facesMsg );
		}
		else if( paoType == PAOGroups.CAPBANK ) {
			addDBObject( 
				PointFactory.createBankStatusPt(parentID), facesMsg );

			addDBObject( 
				PointFactory.createBankOpCntPoint(parentID), facesMsg );
		}

	}

	/**
	 * Executes the creation of the current DB object. We stuff the current
	 * DB persistent object with the newlay created one so our jump to the 
	 * editor page will use the new created DB object.
	 */
	public String create() {

		//creates the DB object
		FacesMessage facesMsg = new FacesMessage();

		try {
			
			//if there is a secondaryType set, use that value to creat the PAO
			int paoType = getWizData().getSelectedType();

			YukonPAObject dbObj = 
				(YukonPAObject)CCYukonPAOFactory.createCapControlPAO(paoType);

			dbObj.setDisabled( getWizData().getDisabled().booleanValue() );
			dbObj.setPAOName( getWizData().getName() );
			

			//for CBCs that have a portID with it
			if( DeviceTypesFuncs.cbcHasPort(paoType) )
				((ICapBankController)dbObj).setCommID( getWizData().getPortID() );


			addDBObject( dbObj, facesMsg );
			
			//creates any extra db objects if need be
			createSupportItems( paoType, dbObj.getPAObjectID().intValue(), facesMsg );


			facesMsg.setDetail( "Database add was SUCCESSFUL" );
			
			//init this form with the newly created DB object
			initItem( dbObj.getPAObjectID().intValue() );

			//redirect to this form as the editor for this new DB object
			return "cbcEditor";
			
		}
		catch( TransactionException te ) {
			//do nothing since the appropriate actions was taken in the super
		}
		finally {
			FacesContext.getCurrentInstance().addMessage("cti_db_add", facesMsg);		
		}

		return ""; //go nowhere since this action failed
	}

	/**
	 * Puts our form into CBC editing mode 
	 */
	public void editController( ValueChangeEvent ev ) {
		
		if(ev == null || ev.getNewValue() == null) return;
		
		if( getCBContollerEditor()== null && isControllerCBC() ) {

			int devID = PointFuncs.getLitePoint( 
				((CapBank)getPAOBase()).getCapBank().getControlPointID().intValue() ).getPaobjectID();

			if( devID >= 0 )
				setCBControllerEditor(
					new CBControllerEditor( (DeviceCBC)getCbcDevicesMap().get(new Integer(devID)) ) );
			else
				setCBControllerEditor( null );
		}

	}

	/**
	 * Returns the editor object for the internal CBC editor
	 */
	private CBControllerEditor getCBContollerEditor() {
		return cbControllerEditor;
	}
	
	/**
	 * Sets the editor object for the internal CBC editor
	 */
	private void setCBControllerEditor( CBControllerEditor cbCntrlEditor) {
		cbControllerEditor = cbCntrlEditor;
	}
	
	/**
	 * Tells us if we are editing a CBC 
	 */
	public boolean isEditingController() {
		return editingController;
	}

	/**
	 * Tells us if we are editing a CBC 
	 */
	public void setEditingController( boolean val ) {
		editingController = val;
	}

	/**
	 * Creates a strategy 
	 *
	 */
	public void createStrategy() {

		CapControlStrategy ccStrat = CCYukonPAOFactory.createCapControlStrategy();		
		Integer newID = CapControlStrategy.getNextStrategyID();
		ccStrat.setStrategyID( newID );
		ccStrat.setStrategyName( "Strat #" + newID + " (New)" );


		//this message will be filled in by the super class
		FacesMessage facesMsg = new FacesMessage();
		try {
			addDBObject( ccStrat, facesMsg );
			

			//set the current object to use this new Strategy
			if( getPAOBase() instanceof CapControlFeeder )
				((CapControlFeeder)getPAOBase()).getCapControlFeeder().setStrategyID( newID );
			else if( getPAOBase() instanceof CapControlSubBus )
				((CapControlSubBus)getPAOBase()).getCapControlSubstationBus().setStrategyID( newID );
			
			//clear out the memory of the any list of Strategies
			resetStrategies();
			setEditingCBCStrategy( true );

			facesMsg.setDetail( "CapControl Strategy add was SUCCESSFUL" );
		}
		catch( TransactionException te ) {
			//do nothing since the appropriate actions was taken in the super
		}
		finally {

			FacesContext.getCurrentInstance().addMessage("cti_db_add", facesMsg);		
		}
	}

	/**
	 * Delete the selected Strategy 
	 *
	 */
	public void deleteStrategy() {

		//this message will be filled in by the super class
		FacesMessage facesMsg = new FacesMessage();

		try {			
			//cancel any editing of the Strategy we may have been doing
			setEditingCBCStrategy( false );

			int stratID = CtiUtilities.NONE_ZERO_ID;
			if( getPAOBase() instanceof CapControlFeeder ) {
				stratID = ((CapControlFeeder)getPAOBase()).getCapControlFeeder().getStrategyID().intValue();
				((CapControlFeeder)getPAOBase()).getCapControlFeeder().setStrategyID( new Integer(CtiUtilities.NONE_ZERO_ID) );
			}
			else if( getPAOBase() instanceof CapControlSubBus ) {
				stratID = ((CapControlSubBus)getPAOBase()).getCapControlSubstationBus().getStrategyID().intValue();
				((CapControlSubBus)getPAOBase()).getCapControlSubstationBus().setStrategyID( new Integer(CtiUtilities.NONE_ZERO_ID) );
			}


			//decide if we need to do any special handling of this transaction
			// based on what other PAOs use this Strategy
			int[] paos = CapControlStrategy.getAllPAOSUsingStrategy( stratID, getPAOBase().getPAObjectID().intValue() );			
			if( paos.length <= 0 ) {

				//update the current PAOBase object just in case it uses the strategy we are deleting
				updateDBObject( getPAOBase(), null );

				deleteDBObject(
					(CapControlStrategy)getCbcStrategiesMap().get( new Integer(stratID) ), facesMsg );
				

				//clear out the memory of the any list of Strategies
				resetStrategies();

				facesMsg.setDetail( "CapControl Strategy delete was SUCCESSFUL" );
			}
			else {
				StringBuffer items = new StringBuffer("");
				for( int i = 0; i < paos.length; i++ )
					items.append( PAOFuncs.getYukonPAOName(paos[i]) + "," );

				facesMsg.setDetail( "Unable to delete the Strategy since the following items use it: " +
					items.deleteCharAt(items.length()-1) );

				facesMsg.setSeverity( FacesMessage.SEVERITY_ERROR );
			}
			
		}
		catch( TransactionException te ) {
			facesMsg.setDetail( "Unable to delete the Strategy: " + te.getMessage() );
			facesMsg.setSeverity( FacesMessage.SEVERITY_ERROR );			
		}
		finally {

			FacesContext.getCurrentInstance().addMessage("cti_db_delete", facesMsg);		
		}
	}

	/**
	 * @return
	 */
	public String getPAODescLabel() {
		return paoDescLabel;
	}

	/**
	 * Returns the parent object to the current DB object
	 * @return
	 */
	public String getParent() {
				
		int parentID = CtiUtilities.NONE_ZERO_ID;		

		if( getPAOBase() instanceof CapControlFeeder )
			parentID = com.cannontech.database.db.capcontrol.CapControlFeeder.getParentSubBusID( getPAOBase().getPAObjectID().intValue() );
		else if( getPAOBase() instanceof CapBank )
			parentID = com.cannontech.database.db.capcontrol.CapBank.getParentFeederID( getPAOBase().getPAObjectID().intValue() );

		if( parentID == CtiUtilities.NONE_ZERO_ID )
			return CtiUtilities.STRING_NONE;
		else {		
			LiteYukonPAObject parentPAO = PAOFuncs.getLiteYukonPAO(parentID);
			return parentPAO.getPaoName() + 
					"   (" + PAOGroups.getPAOTypeString(parentPAO.getType()) +
					",  id: " + parentPAO.getLiteID() + ")";
		}

	}

	/**
	 * Adds an element from one table to another by the given id
	 * @param event
	 */
	public void treeSwapAddAction()
	{
		FacesContext context = FacesContext.getCurrentInstance();
		Map paramMap = context.getExternalContext().getRequestParameterMap();

		String swapType = (String)paramMap.get("swapType");
		int elemID = new Integer((String)paramMap.get("id")).intValue();

		if( "CapBank".equalsIgnoreCase(swapType) ) {
			//a table that swaps CapBanks, must be for a Feeder object			
			if( unassignedBanks != null ) {
				for( int i = 0; i < unassignedBanks.size(); i++ ) {
			
					if( elemID == ((LiteYukonPAObject)unassignedBanks.get(i)).getLiteID() ) {
						
						//Add the mapping for the given CapBank id to this Feeder
						CapControlFeeder currFdr = (CapControlFeeder)getPAOBase();
						currFdr.getChildList().add( new CCFeederBankList(
							getPAOBase().getPAObjectID(),
							new Integer(elemID),
							new Integer(currFdr.getChildList().size()+1)) );
						
						unassignedBanks.remove(i);
						break;
					}
				}				
			}

		}
		else if( "Feeder".equalsIgnoreCase(swapType) ) {
			//a table that swaps Feeders, must be for a SubBus object
			if( unassignedFeeders != null ) {
				for( int i = 0; i < unassignedFeeders.size(); i++ ) {
			
					if( elemID == ((LiteYukonPAObject)unassignedFeeders.get(i)).getLiteID() ) {
						//Add the mapping for the given Feeders id to this Sub
						CapControlSubBus currSub = (CapControlSubBus)getPAOBase();
						currSub.getChildList().add( new CCFeederSubAssignment(
							new Integer(elemID),
							getPAOBase().getPAObjectID(),
							new Integer(currSub.getChildList().size()+1)) );
						
						unassignedFeeders.remove(i);
						break;
					}
				}				
			}
		}

	}

	/**
	 * Removed an element from one table to another by the given id
	 * 
	 */
	public void treeSwapRemoveAction()
	{
		FacesContext context = FacesContext.getCurrentInstance();
		Map paramMap = context.getExternalContext().getRequestParameterMap();

		String swapType = (String)paramMap.get("swapType");
		int elemID = new Integer((String)paramMap.get("id")).intValue();

		if( "CapBank".equalsIgnoreCase(swapType) ) {
			//a table that swaps CapBanks, must be for a Feeder object			
			CapControlFeeder currFdr = (CapControlFeeder)getPAOBase();
			for( int i = 0; i < currFdr.getChildList().size(); i++ ) {
		
				CCFeederBankList listItem = (CCFeederBankList)currFdr.getChildList().get(i);
				if( elemID == listItem.getDeviceID().intValue() ) {

					//remove the mapping for the given CapBank id to this Feeder
					currFdr.getChildList().remove( i );
					
					unassignedBanks.add( PAOFuncs.getLiteYukonPAO(elemID) );
					//keep our order
					Collections.sort( unassignedBanks, LiteComparators.liteStringComparator );
					break;
				}
			}		

		}
		else if( "Feeder".equalsIgnoreCase(swapType) ) {
			//a table that swaps Feeders, must be for a SubBus object
			CapControlSubBus currSub = (CapControlSubBus)getPAOBase();

			for( int i = 0; i < currSub.getChildList().size(); i++ ) {		
				CCFeederSubAssignment listItem = (CCFeederSubAssignment)currSub.getChildList().get(i);
				if( elemID == listItem.getFeederID().intValue() ) {

					//remove the mapping for the given Feeder id to this SubBus
					currSub.getChildList().remove( i );

					unassignedFeeders.add( PAOFuncs.getLiteYukonPAO(elemID) );
					//keep our order
					Collections.sort( unassignedFeeders, LiteComparators.liteStringComparator );
					break;
				}
			}		
		}

	}


	/**
	 * Builds up the available CapBanks and unavailable CapBanks for assignment to
	 * a feeder. Also, inits panel state based on the editor object that is set.
	 */
	public void initEditorPanels() 
	{
		if( getPAOBase() instanceof CapControlFeeder ) {
			int fdrVarPtID = ((CapControlFeeder)getPAOBase()).getCapControlFeeder().getCurrentVarLoadPointID().intValue();
			kwkvarPaosChanged(
				new ValueChangeEvent(
					DUMMY_UI, null,
					new Integer(PointFuncs.getLitePoint(fdrVarPtID).getPaobjectID())) );
		}
		else if( getPAOBase() instanceof CapControlSubBus ) {
			int varPtID = ((CapControlSubBus)getPAOBase()).getCapControlSubstationBus().getCurrentVarLoadPointID().intValue();
			if( varPtID > CtiUtilities.NONE_ZERO_ID )				
				kwkvarPaosChanged(
					new ValueChangeEvent(
						DUMMY_UI, null,
						new Integer(PointFuncs.getLitePoint(varPtID).getPaobjectID())) );
		}
		
		unassignedBanks = new Vector(16);
		int[] unassignedBankIDs = 
			com.cannontech.database.db.capcontrol.CapBank.getUnassignedCapBankIDs();

		for( int i = 0; i < unassignedBankIDs.length; i++ ) {
			
			unassignedBanks.add( PAOFuncs.getLiteYukonPAO(unassignedBankIDs[i]) );
		}		
		Collections.sort( unassignedBanks, LiteComparators.liteStringComparator );



		unassignedFeeders = new Vector(16);
		int[] unassignedFeederIDs = 
			com.cannontech.database.db.capcontrol.CapControlFeeder.getUnassignedFeederIDs();

		for( int i = 0; i < unassignedFeederIDs.length; i++ ) {
			
			unassignedFeeders.add( PAOFuncs.getLiteYukonPAO(unassignedFeederIDs[i]) );
		}
		Collections.sort( unassignedFeeders, LiteComparators.liteStringComparator );
	}
	

	/**
	 * @param string
	 */
	public void setPaoDescLabel(String string) {
		paoDescLabel = string;
	}

	/**
	 * @return
	 */
	public boolean isEditingCBCStrategy() {
		return editingCBCStrategy;
	}

	/**
	 * @param b
	 */
	public void setEditingCBCStrategy(boolean b) {
		editingCBCStrategy = b;
	}
	
	/**
	 * @return
	 */
	public SelectItem[] getControlInterval() {
		
		SelectItem[] interval = new SelectItem[ timeInterval.length + 1 ];
		interval[0] = new SelectItem(new Integer(CtiUtilities.NONE_ZERO_ID), "(On New Data Only)");
		
		System.arraycopy( timeInterval, 0, interval, 1, timeInterval.length );
		return interval;
	}
		
	/**
	 * @return
	 */
	public SelectItem[] getDayTime() {
		
		SelectItem[] interval = new SelectItem[ timeInterval.length + 1 ];
		interval[0] = new SelectItem(new Integer(CtiUtilities.NONE_ZERO_ID), CtiUtilities.STRING_NONE);
		
		System.arraycopy( timeInterval, 0, interval, 1, timeInterval.length );
		return interval;
	}

	/**
	 * @return
	 */
	public List getUnassignedBanks() {
		return unassignedBanks;
	}

	/**
	 * @return
	 */
	public List getUnassignedFeeders() {
		return unassignedFeeders;
	}

	/**
	 * @return
	 */
	public String getChildLabel() {
		return childLabel;
	}

	/**
	 * @param string
	 */
	public void setChildLabel(String string) {
		childLabel = string;
	}
	

	/**
	 * Converter for allowing an array of Integers for our DaysofWeek string
	 */
	public void setStratDaysOfWeek(int[] newDaysOfWeek) {

		CapControlStrategy strat = (CapControlStrategy)
				getCbcStrategiesMap().get( new Integer(getCurrentStrategyID()) );

		if( strat == null || newDaysOfWeek == null ) return;
		
		StringBuffer buff = new StringBuffer("NNNNNNNN");
		for( int i = 0; i < newDaysOfWeek.length; i++ ) {
			buff.setCharAt( newDaysOfWeek[i], 'Y' );
		}

		strat.setDaysOfWeek( buff.toString() );
	}

	/**
	 * Converter for allowing an array of Integers for our DaysofWeek string
	 */
	public int[] getStratDaysOfWeek() {
		
		CapControlStrategy strat = (CapControlStrategy)
				getCbcStrategiesMap().get( new Integer(getCurrentStrategyID()) );

		if( strat == null ) return new int[0];

		NativeIntVector retList = new NativeIntVector(8);
		for( int i = 0; i < strat.getDaysOfWeek().length(); i++ ) {			
			if( strat.getDaysOfWeek().charAt(i) == 'Y' )
				retList.add( i );
		}

		return retList.toArray();
	}

	/**
	 * Tells us if our current CapBank uses a CBC for control or not
	 */
	public boolean isControllerCBC() {
		
		if( getPAOBase() instanceof CapBank ) {
			
			int paoID = PointFuncs.getLitePoint( 
					((CapBank)getPAOBase()).getCapBank().getControlPointID().intValue() ).getPaobjectID();
			
			if( paoID != CtiUtilities.NONE_ZERO_ID )
				return DeviceTypesFuncs.isCapBankController(
						PAOFuncs.getLiteYukonPAO(paoID).getType() );
		}		

		return false;
	}

	/**
	 * Returns true if the DB object is a CapBank and the OpState is FIXED
	 * @return
	 */
	public boolean isBankControlPtVisible() {

		if( getPAOBase() instanceof CapBank ) {			
			return !CapBank.FIXED_OPSTATE.equals( ((CapBank)getPAOBase()).getCapBank().getOperationalState() );
		}
		else
			return false;
	}

	/**
	 * @return
	 */
	public CBCWizardModel getWizData() {
		
		if( wizData == null )
			wizData = new CBCWizardModel();

		return wizData;
	}

}