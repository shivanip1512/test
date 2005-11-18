package com.cannontech.web.editor.point;

import java.util.HashMap;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import com.cannontech.database.data.fdr.FDRInterface;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.db.point.fdr.FDRTranslation;
import com.cannontech.web.util.JSFParamUtil;

/**
 * @author ryan
 *
 */
public class PointFDREntry
{
	private PointBase pointBase = null;
	
	private FDRTranslationEntry fdrTranslationEntry = null;

	private SelectItem[] fdrInterfaceSel = null;
	
	//contains ( Key<String:InterfaceType>, Value<SelectItem[]:Directions> )
	private HashMap fdrDirectionsMap = null;

	//contains ( Key<String:InterfaceType>, Value<data.FDRInterface> )
	private HashMap fdrInterfacesMap = null;

	private int selectedRowNum = -1;
	private FDRInterface selectedInterface = null;
	private String selectedTranslation = null;

		
	/**
	 * 
	 */
	public PointFDREntry( PointBase pBase )
	{
		super();
		
		if( pBase == null )
			throw new IllegalArgumentException("PointFDREntry can not be created with a NULL PointBase reference");

		loadInterfaceData();		

		pointBase = pBase;
	}


	/**
	 * The instance of the underlying base object
	 */
	private PointBase getPointBase() {
		return pointBase;
	}

	/**
	 * Shows the current editor for the given translation string 
	 */
	public String showTranslation() {
		
		int rowNum = new Integer(JSFParamUtil.getJSFReqParam("rowNumber")).intValue();
		String interfaceName= JSFParamUtil.getJSFReqParam("fdrInterface");

		//set our data structures up
		setSelectedRowNum( rowNum );
		setSelectedInterface(
			(FDRInterface)getFdrInterfacesMap().get(interfaceName) );		

		fdrTranslationEntry = new FDRTranslationEntry(
				(FDRTranslation)getPointBase().getPointFDRList().get(getSelectedRowNum()),
				getSelectedInterface() );

		//keep us on our same page
		return null;
	}

	/**
	 * Shows the current editor for the given translation string 
	 */
	public void interfaceChange( ValueChangeEvent ev ) {

		Integer rowNum =
			(Integer)JSFParamUtil.getChildElemValue( ev.getComponent(), "rowNumber");
		String interfaceName = (String)ev.getNewValue();

		//set up some values as to edit this translation
		setSelectedRowNum( -1 );
		setSelectedInterface( null );
			//(FDRInterface)getFdrInterfacesMap().get(interfaceName) );		


		//update the db object with the change
		if( rowNum.intValue() >= 0 && rowNum.intValue() <= getPointBase().getPointFDRList().size() ) {

			getPointBase().getPointFDRList().set( rowNum.intValue(),
				FDRInterface.createDefaultTranslation(
						(FDRInterface)getFdrInterfacesMap().get(interfaceName),
						getPointBase().getPoint().getPointID(),
						getPointBase().getPoint().getPointType() ) );

//			//make the editor for this translation visible
//			fdrTranslationEntry = new FDRTranslationEntry(
//					(FDRTranslation)getPointBase().getPointFDRList().get(rowNum.intValue() ),
//					getSelectedInterface() );
		}


//		fdrTranslationEntry = new FDRTranslationEntry(
//				(FDRInterface)getFdrInterfacesMap().get(interfaceName),
//				getPointBase().getPoint().getPointID(),
//				getPointBase().getPoint().getPointType() );

	}

	/**
	 * Remove the selected translation from our table
	 */
	public String deleteTranslation() {
		
		int rowNum = new Integer(JSFParamUtil.getJSFReqParam("rowNumber")).intValue();

		//be sure we have a valid row number
		if( rowNum >= 0 && rowNum <= getPointBase().getPointFDRList().size() )
			getPointBase().getPointFDRList().remove( rowNum );

		//we must clear out any editing action
		setSelectedRowNum( -1 );
		setSelectedInterface( null );

		//keep us on our same page
		return null;
	}

	/**
	 * Add a new default translation entry to our table
	 */
	public String addTranslation() {
		
		FDRTranslation trans = FDRTranslation.createTranslation(
				getPointBase().getPoint().getPointID() );

		getPointBase().getPointFDRList().add( trans );

		//we must clear out any editing action
		setSelectedRowNum( -1 );
		setSelectedInterface( null );

		//keep us on our same page
		return null;
	}

	/**
	 * Loads all of our FDRInterface data structures into memory
	 */
	private void loadInterfaceData() {

		FDRInterface[] fdrInterfaces =
			com.cannontech.database.db.point.fdr.FDRInterface.getALLFDRInterfaces();

		fdrInterfaceSel = new SelectItem[ fdrInterfaces.length ];
		fdrDirectionsMap = new HashMap(8);
		fdrInterfacesMap = new HashMap(32);

		for( int i = 0; i < fdrInterfaces.length; i++ ) {
			fdrInterfaceSel[i] = new SelectItem(
				fdrInterfaces[i].getFdrInterface().getInterfaceName(),
				fdrInterfaces[i].getFdrInterface().getInterfaceName() );

			//mapping used to map InterfaceName to the FDRInterface instance 
			getFdrInterfacesMap().put(
				fdrInterfaces[i].getFdrInterface().getInterfaceName(),
				fdrInterfaces[i] );
				
			//build our directions mapping for this interface
			String[] dirsStrs = fdrInterfaces[i].getFdrInterface().getAllDirections();
			SelectItem[] dirItems = new SelectItem[ dirsStrs.length ];
			for( int j = 0; j < dirsStrs.length; j++ )	
				dirItems[j] = new SelectItem( dirsStrs[j], dirsStrs[j] );

			//map of InterfaceType to SelectItms[]
			getFDRDirectionsMap().put( fdrInterfaces[i].getFdrInterface().getInterfaceName(), dirItems );
		}

	}
	
	/**
	 * Returns the number of translations we have in our list
	 */
	public int getFDRTransSize() {
		return getPointBase().getPointFDRList().size();
	}

	/**
	 * Returns all FDR interfaces from the DB
	 */
	public SelectItem[] getFDRInterfaces() 
	{
		return fdrInterfaceSel;
	}

	/**
	 * Gets all the possible direction for a FDRInterface
	 */
	public HashMap getFDRDirectionsMap() {
		
		return fdrDirectionsMap;
	}

	/**
	 * @return
	 */
	public HashMap getFdrInterfacesMap() {
		return fdrInterfacesMap;
	}

	/**
	 * @return
	 */
	public int getSelectedRowNum() {
		return selectedRowNum;
	}

	/**
	 * @param i
	 */
	public void setSelectedRowNum(int i) {
		selectedRowNum = i;
	}

	/**
	 * @return
	 */
	private FDRInterface getSelectedInterface() {
		return selectedInterface;
	}

	/**
	 * @param interface1
	 */
	private void setSelectedInterface(FDRInterface interface1) {
		selectedInterface = interface1;
	}

	/**
	 * @return
	 */
	public FDRTranslationEntry getFdrTranslationEntry() {		
		return fdrTranslationEntry;
	}

	/**
	 * @return
	 */
	public String getSelectedTranslation() {
		return selectedTranslation;
	}

	/**
	 * @param string
	 */
	public void setSelectedTranslation(String string) {
		selectedTranslation = string;
	}

}
