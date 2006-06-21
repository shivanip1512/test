package com.cannontech.web.editor.point;

import java.util.HashMap;
import java.util.List;

import javax.faces.model.SelectItem;

import com.cannontech.database.data.fdr.FDRInterface;
import com.cannontech.database.db.point.fdr.FDRInterfaceOption;
import com.cannontech.database.db.point.fdr.FDRTranslation;

/**
 * Very advanced GUI to port to the web. Used to show the selectable options an FDR
 * interface gives for input
 */
public class FDRTranslationEntry {

	private FDRTranslation fdrTranslation = null;
	
	private FDRInterface currentInterface = null;
	
	//contains: Key:"String", Value:OptionData
	private HashMap optionDataMap = new HashMap(8);
	
	public static final int MAX_OPTIONS = 5;


	/**
	 * Create a default translation internally
	 */
	public FDRTranslationEntry( FDRInterface fdrInterface, Integer pointID, String pointType ) {
		this(
			FDRInterface.createDefaultTranslation(fdrInterface, pointID, pointType ),
			fdrInterface );
	}

	/**
	 * 
	 */
	public FDRTranslationEntry( FDRTranslation fdrTrans, FDRInterface fdrInterface ) {
		super();
		
		if( fdrTrans == null )
			throw new IllegalArgumentException("A null FDRTranslation reference is not a valid argument");

		if( fdrInterface == null )
			throw new IllegalArgumentException("A null FDRInterface reference is not a valid argument");

		currentInterface = fdrInterface;
		fdrTranslation = fdrTrans;

		loadData();
	}

	/**
	 * Stores a flattened instance of the FDROption
	 */
	class OptionData {

		private int optNum = 0;
		private String label = null;
		private FDRTranslation fdrTranslation = null;
		private SelectItem[] comboItems = null;

		public OptionData( int opNum, String disLabel, SelectItem[] cItems, FDRTranslation fdrTrans ) {
			super();
			optNum = opNum;
			comboItems = cItems;  //this will remain null if we are NOT a Combo type
			label = disLabel;
			fdrTranslation = fdrTrans; //current translation for this entry
		}

		public SelectItem[] getComboItems() {
			return comboItems;
		}

		public String getLabel() {
			return label;
		}

		public String getOptionName() {
			return "Option_" + optNum;
		}

		public String getTranslation() {
			return fdrTranslation.getTranslation();
		}

		public String getValue() {			
			return FDRTranslation.getTranslationValue( fdrTranslation.getTranslation(), getLabel() );
		}

		public void setValue( String val ) {			
			fdrTranslation.setTranslation(
				FDRTranslation.setTranslationValue(getTranslation(), val, getLabel()) );
		}

	}
	
	private void loadData() {

		getOptionDataMap().clear();
		List options = getCurrentInterface().getInterfaceOptionVector();

		if( options == null ) return;

		//set the rest of the data parameters
		for( int i = 0; i < MAX_OPTIONS; i++ ) {

			if( i < options.size() ) {
				FDRInterfaceOption option = (FDRInterfaceOption)options.get(i);
				SelectItem[] selItems = null;
				
				if( FDRInterfaceOption.OPTION_COMBO.equalsIgnoreCase(option.getOptionType()) ) {					
					//add all the possible choices for the ComboBox input
					String[] optValues = option.getAllOptionValues();
					selItems = new SelectItem[ optValues.length ];
					for( int j = 0; j < optValues.length; j++ )
						selItems[j] = new SelectItem( optValues[j], optValues[j] );
				}

				OptionData optData = new OptionData(
						i, option.getOptionLabel(), selItems,
						getFDRTranslation() );

				//store our data by the option name
				getOptionDataMap().put( optData.getOptionName(), optData );
			}
		}


			/////
			//
			// OPTION_QUERY is no longer used, so implementation is avoided for the time being
			//
			/////				
			/*				
			else if( option.getOptionType().equalsIgnoreCase( FDRInterfaceOption.OPTION_QUERY ) )
			{
				SqlStatement s = new SqlStatement(
							option.getOptionValues(), CtiUtilities.getDatabaseAlias() );
			
				try
				{
					s.execute();
			
					for( int j = 0; j < s.getRowCount(); j++ )
						((javax.swing.JComboBox)getDataOptions()[i][1]).addItem( 
					   s.getRow(j)[0].toString() );
				}
				catch( CommandExecutionException e )
				{
					handleException(e);
				}
			
			}
			*/		
	}


	public SelectItem[] getCombo0Items() {
		OptionData data = (OptionData)getOptionDataMap().get("Option_0");
		return data.getComboItems();
	}

	public SelectItem[] getCombo1Items() {
		OptionData data = (OptionData)getOptionDataMap().get("Option_1");
		return data.getComboItems();
	}

	public SelectItem[] getCombo2Items() {
		OptionData data = (OptionData)getOptionDataMap().get("Option_2");
		return data.getComboItems();
	}

	public SelectItem[] getCombo3Items() {
		OptionData data = (OptionData)getOptionDataMap().get("Option_3");
		return data.getComboItems();
	}

	public SelectItem[] getCombo4Items() {
		OptionData data = (OptionData)getOptionDataMap().get("Option_4");
		return data.getComboItems();
	}


	public String getLabel0() {
		OptionData data = (OptionData)getOptionDataMap().get("Option_0");
		return data == null ? "" : data.getLabel();
	}

	public String getLabel1() {
		OptionData data = (OptionData)getOptionDataMap().get("Option_1");
		return data == null ? "" : data.getLabel();
	}

	public String getLabel2() {
		OptionData data = (OptionData)getOptionDataMap().get("Option_2");
		return data == null ? "" : data.getLabel();
	}

	public String getLabel3() {
		OptionData data = (OptionData)getOptionDataMap().get("Option_3");
		return data == null ? "" : data.getLabel();
	}

	public String getLabel4() {
		OptionData data = (OptionData)getOptionDataMap().get("Option_4");
		return data == null ? "" : data.getLabel();
	}



	public String getValue0() {
		OptionData data = (OptionData)getOptionDataMap().get("Option_0");
		return data.getValue();
	}

	public String getValue1() {
		OptionData data = (OptionData)getOptionDataMap().get("Option_1");
		return data.getValue();
	}

	public String getValue2() {
		OptionData data = (OptionData)getOptionDataMap().get("Option_2");
		return data.getValue();
	}

	public String getValue3() {
		OptionData data = (OptionData)getOptionDataMap().get("Option_3");
		return data.getValue();
	}

	public String getValue4() {
		OptionData data = (OptionData)getOptionDataMap().get("Option_4");
		return data.getValue();
	}


	public void setValue0( String s ) {
		OptionData data = (OptionData)getOptionDataMap().get("Option_0");
		data.setValue( s );
		setPointTranslation( data );	
	}

	public void setValue1( String s ) {
		OptionData data = (OptionData)getOptionDataMap().get("Option_1");
		data.setValue( s );
		setPointTranslation( data );	
	}

	public void setValue2( String s ) {
		OptionData data = (OptionData)getOptionDataMap().get("Option_2");
		data.setValue( s );
		setPointTranslation( data );	
	}

	public void setValue3( String s ) {
		OptionData data = (OptionData)getOptionDataMap().get("Option_3");
		data.setValue( s );
		setPointTranslation( data );	
	}

	public void setValue4( String s ) {
		OptionData data = (OptionData)getOptionDataMap().get("Option_4");
		data.setValue( s );
		setPointTranslation( data );	
	}

	/**
	 * Updates the translation string to the user specified value
	 */
	private void setPointTranslation( OptionData data ) {
		getFDRTranslation().setTranslation( data.getTranslation() );
	}


	/**
	 * @return
	 */
	private FDRInterface getCurrentInterface() {
		return currentInterface;
	}

	/**
	 * @return
	 */
	public HashMap getOptionDataMap() {
		return optionDataMap;
	}

	/**
	 * @return
	 */
	private FDRTranslation getFDRTranslation() {
		
		if( fdrTranslation == null ) {
			fdrTranslation =
				FDRInterface.createDefaultTranslation(
					getCurrentInterface(), null, null );
		}

		return fdrTranslation;
	}

}