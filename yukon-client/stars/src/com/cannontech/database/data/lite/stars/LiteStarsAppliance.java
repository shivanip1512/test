package com.cannontech.database.data.lite.stars;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteTypes;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class LiteStarsAppliance extends LiteBase {
	
	public static class AirConditioner {
		private int tonnageID = CtiUtilities.NONE_ID;
		private int typeID = CtiUtilities.NONE_ID;
		
		/**
		 * @return
		 */
		public int getTonnageID() {
			return tonnageID;
		}

		/**
		 * @return
		 */
		public int getTypeID() {
			return typeID;
		}

		/**
		 * @param i
		 */
		public void setTonnageID(int i) {
			tonnageID = i;
		}

		/**
		 * @param i
		 */
		public void setTypeID(int i) {
			typeID = i;
		}
	}
	
	public static class WaterHeater {
		private int numberOfGallonsID = CtiUtilities.NONE_ID;
		private int energySourceID = CtiUtilities.NONE_ID;
		private int numberOfElements = 0;
		
		/**
		 * @return
		 */
		public int getEnergySourceID() {
			return energySourceID;
		}

		/**
		 * @return
		 */
		public int getNumberOfElements() {
			return numberOfElements;
		}

		/**
		 * @return
		 */
		public int getNumberOfGallonsID() {
			return numberOfGallonsID;
		}

		/**
		 * @param i
		 */
		public void setEnergySourceID(int i) {
			energySourceID = i;
		}

		/**
		 * @param i
		 */
		public void setNumberOfElements(int i) {
			numberOfElements = i;
		}

		/**
		 * @param i
		 */
		public void setNumberOfGallonsID(int i) {
			numberOfGallonsID = i;
		}
	}
	
	public static class DualFuel {
		private int switchOverTypeID = CtiUtilities.NONE_ID;
		private int secondaryKWCapacity = 0;
		private int secondaryEnergySourceID = CtiUtilities.NONE_ID;
		
		/**
		 * @return
		 */
		public int getSecondaryEnergySourceID() {
			return secondaryEnergySourceID;
		}

		/**
		 * @return
		 */
		public int getSecondaryKWCapacity() {
			return secondaryKWCapacity;
		}

		/**
		 * @return
		 */
		public int getSwitchOverTypeID() {
			return switchOverTypeID;
		}

		/**
		 * @param i
		 */
		public void setSecondaryEnergySourceID(int i) {
			secondaryEnergySourceID = i;
		}

		/**
		 * @param i
		 */
		public void setSecondaryKWCapacity(int i) {
			secondaryKWCapacity = i;
		}

		/**
		 * @param i
		 */
		public void setSwitchOverTypeID(int i) {
			switchOverTypeID = i;
		}
	}
	
	public static class Generator {
		private int transferSwitchTypeID = CtiUtilities.NONE_ID;
		private int transferSwitchMfgID = CtiUtilities.NONE_ID;
		private int peakKWCapacity = 0;
		private int fuelCapGallons = 0;
		private int startDelaySeconds = 0;
		
		/**
		 * @return
		 */
		public int getFuelCapGallons() {
			return fuelCapGallons;
		}

		/**
		 * @return
		 */
		public int getPeakKWCapacity() {
			return peakKWCapacity;
		}

		/**
		 * @return
		 */
		public int getStartDelaySeconds() {
			return startDelaySeconds;
		}

		/**
		 * @return
		 */
		public int getTransferSwitchMfgID() {
			return transferSwitchMfgID;
		}

		/**
		 * @return
		 */
		public int getTransferSwitchTypeID() {
			return transferSwitchTypeID;
		}

		/**
		 * @param i
		 */
		public void setFuelCapGallons(int i) {
			fuelCapGallons = i;
		}

		/**
		 * @param i
		 */
		public void setPeakKWCapacity(int i) {
			peakKWCapacity = i;
		}

		/**
		 * @param i
		 */
		public void setStartDelaySeconds(int i) {
			startDelaySeconds = i;
		}

		/**
		 * @param i
		 */
		public void setTransferSwitchMfgID(int i) {
			transferSwitchMfgID = i;
		}

		/**
		 * @param i
		 */
		public void setTransferSwitchTypeID(int i) {
			transferSwitchTypeID = i;
		}
	}
	
	public static class GrainDryer {
		private int dryerTypeID = CtiUtilities.NONE_ID;
		private int binSizeID = CtiUtilities.NONE_ID;
		private int blowerEnergySourceID = CtiUtilities.NONE_ID;
		private int blowerHorsePowerID = CtiUtilities.NONE_ID;
		private int blowerHeatSourceID = CtiUtilities.NONE_ID;
		
		/**
		 * @return
		 */
		public int getBinSizeID() {
			return binSizeID;
		}

		/**
		 * @return
		 */
		public int getBlowerEnergySourceID() {
			return blowerEnergySourceID;
		}

		/**
		 * @return
		 */
		public int getBlowerHeatSourceID() {
			return blowerHeatSourceID;
		}

		/**
		 * @return
		 */
		public int getBlowerHorsePowerID() {
			return blowerHorsePowerID;
		}

		/**
		 * @return
		 */
		public int getDryerTypeID() {
			return dryerTypeID;
		}

		/**
		 * @param i
		 */
		public void setBinSizeID(int i) {
			binSizeID = i;
		}

		/**
		 * @param i
		 */
		public void setBlowerEnergySourceID(int i) {
			blowerEnergySourceID = i;
		}

		/**
		 * @param i
		 */
		public void setBlowerHeatSourceID(int i) {
			blowerHeatSourceID = i;
		}

		/**
		 * @param i
		 */
		public void setBlowerHorsePowerID(int i) {
			blowerHorsePowerID = i;
		}

		/**
		 * @param i
		 */
		public void setDryerTypeID(int i) {
			dryerTypeID = i;
		}
	}
	
	public static class StorageHeat {
		private int storageTypeID = CtiUtilities.NONE_ID;
		private int peakKWCapacity = 0;
		private int hoursToRecharge = 0;
		
		/**
		 * @return
		 */
		public int getHoursToRecharge() {
			return hoursToRecharge;
		}

		/**
		 * @return
		 */
		public int getPeakKWCapacity() {
			return peakKWCapacity;
		}

		/**
		 * @return
		 */
		public int getStorageTypeID() {
			return storageTypeID;
		}

		/**
		 * @param i
		 */
		public void setHoursToRecharge(int i) {
			hoursToRecharge = i;
		}

		/**
		 * @param i
		 */
		public void setPeakKWCapacity(int i) {
			peakKWCapacity = i;
		}

		/**
		 * @param i
		 */
		public void setStorageTypeID(int i) {
			storageTypeID = i;
		}
	}
	
	public static class HeatPump {
		private int pumpTypeID = CtiUtilities.NONE_ID;
		private int pumpSizeID = CtiUtilities.NONE_ID;
		private int standbySourceID = CtiUtilities.NONE_ID;
		private int secondsDelayToRestart = 0;
		
		/**
		 * @return
		 */
		public int getPumpSizeID() {
			return pumpSizeID;
		}

		/**
		 * @return
		 */
		public int getPumpTypeID() {
			return pumpTypeID;
		}

		/**
		 * @return
		 */
		public int getSecondsDelayToRestart() {
			return secondsDelayToRestart;
		}

		/**
		 * @return
		 */
		public int getStandbySourceID() {
			return standbySourceID;
		}

		/**
		 * @param i
		 */
		public void setPumpSizeID(int i) {
			pumpSizeID = i;
		}

		/**
		 * @param i
		 */
		public void setPumpTypeID(int i) {
			pumpTypeID = i;
		}

		/**
		 * @param i
		 */
		public void setSecondsDelayToRestart(int i) {
			secondsDelayToRestart = i;
		}

		/**
		 * @param i
		 */
		public void setStandbySourceID(int i) {
			standbySourceID = i;
		}
	}
	
	public static class Irrigation {
		private int irrigationTypeID = CtiUtilities.NONE_ID;
		private int horsePowerID = CtiUtilities.NONE_ID;
		private int energySourceID = CtiUtilities.NONE_ID;
		private int soilTypeID = CtiUtilities.NONE_ID;
		private int meterLocationID = CtiUtilities.NONE_ID;
		private int meterVoltageID = CtiUtilities.NONE_ID;
		
		/**
		 * @return
		 */
		public int getEnergySourceID() {
			return energySourceID;
		}

		/**
		 * @return
		 */
		public int getHorsePowerID() {
			return horsePowerID;
		}

		/**
		 * @return
		 */
		public int getIrrigationTypeID() {
			return irrigationTypeID;
		}

		/**
		 * @return
		 */
		public int getMeterLocationID() {
			return meterLocationID;
		}

		/**
		 * @return
		 */
		public int getMeterVoltageID() {
			return meterVoltageID;
		}

		/**
		 * @return
		 */
		public int getSoilTypeID() {
			return soilTypeID;
		}

		/**
		 * @param i
		 */
		public void setEnergySourceID(int i) {
			energySourceID = i;
		}

		/**
		 * @param i
		 */
		public void setHorsePowerID(int i) {
			horsePowerID = i;
		}

		/**
		 * @param i
		 */
		public void setIrrigationTypeID(int i) {
			irrigationTypeID = i;
		}

		/**
		 * @param i
		 */
		public void setMeterLocationID(int i) {
			meterLocationID = i;
		}

		/**
		 * @param i
		 */
		public void setMeterVoltageID(int i) {
			meterVoltageID = i;
		}

		/**
		 * @param i
		 */
		public void setSoilTypeID(int i) {
			soilTypeID = i;
		}
	}
	
	private int accountID = com.cannontech.database.db.stars.customer.CustomerAccount.NONE_INT;
	private int applianceCategoryID = com.cannontech.database.db.stars.appliance.ApplianceCategory.NONE_INT;
	private int programID = 0;
	private int yearManufactured = 0;
	private int manufacturerID = CtiUtilities.NONE_ID;
	private int locationID = CtiUtilities.NONE_ID;
	private String notes = null;
	private String modelNumber = null;
	private int kwCapacity = 0;
	private int efficiencyRating = 0;
	
	private int inventoryID = CtiUtilities.NONE_ID;
	private int addressingGroupID = 0;
	private int loadNumber = 0;
	
	private AirConditioner airConditioner = null;
	private WaterHeater waterHeater = null;
	private Generator generator = null;
	private DualFuel dualFuel = null;
	private GrainDryer grainDryer = null;
	private StorageHeat storageHeat = null;
	private HeatPump heatPump = null;
	private Irrigation irrigation = null;
	
	public LiteStarsAppliance() {
		super();
		setLiteType( LiteTypes.STARS_APPLIANCE );
	}
	
	public LiteStarsAppliance(int appID) {
		super();
		setApplianceID( appID );
		setLiteType( LiteTypes.STARS_APPLIANCE );
	}
	
	public int getApplianceID() {
		return getLiteID();
	}
	
	public void setApplianceID(int appID) {
		setLiteID( appID );
	}

	/**
	 * Returns the accountID.
	 * @return int
	 */
	public int getAccountID() {
		return accountID;
	}

	/**
	 * Returns the applianceCategoryID.
	 * @return int
	 */
	public int getApplianceCategoryID() {
		return applianceCategoryID;
	}

	/**
	 * Returns the programID.
	 * @return int
	 */
	public int getProgramID() {
		return programID;
	}

	/**
	 * Returns the locationID.
	 * @return int
	 */
	public int getLocationID() {
		return locationID;
	}

	/**
	 * Returns the manufacturerID.
	 * @return int
	 */
	public int getManufacturerID() {
		return manufacturerID;
	}

	/**
	 * Returns the notes.
	 * @return String
	 */
	public String getNotes() {
		return notes;
	}

	/**
	 * Returns the yearManufactured.
	 * @return int
	 */
	public int getYearManufactured() {
		return yearManufactured;
	}

	/**
	 * Sets the accountID.
	 * @param accountID The accountID to set
	 */
	public void setAccountID(int accountID) {
		this.accountID = accountID;
	}

	/**
	 * Sets the applianceCategoryID.
	 * @param applianceCategoryID The applianceCategoryID to set
	 */
	public void setApplianceCategoryID(int applianceCategoryID) {
		this.applianceCategoryID = applianceCategoryID;
	}

	/**
	 * Sets the programID.
	 * @param programID The programID to set
	 */
	public void setProgramID(int programID) {
		this.programID = programID;
	}

	/**
	 * Sets the locationID.
	 * @param locationID The locationID to set
	 */
	public void setLocationID(int locationID) {
		this.locationID = locationID;
	}

	/**
	 * Sets the manufacturerID.
	 * @param manufacturerID The manufacturerID to set
	 */
	public void setManufacturerID(int manufacturerID) {
		this.manufacturerID = manufacturerID;
	}

	/**
	 * Sets the notes.
	 * @param notes The notes to set
	 */
	public void setNotes(String notes) {
		this.notes = notes;
	}

	/**
	 * Sets the yearManufactured.
	 * @param yearManufactured The yearManufactured to set
	 */
	public void setYearManufactured(int yearManufactured) {
		this.yearManufactured = yearManufactured;
	}

	/**
	 * Returns the addressingGroupID.
	 * @return int
	 */
	public int getAddressingGroupID() {
		return addressingGroupID;
	}

	/**
	 * Returns the inventoryID.
	 * @return int
	 */
	public int getInventoryID() {
		return inventoryID;
	}

	/**
	 * Sets the addressingGroupID.
	 * @param addressingGroupID The addressingGroupID to set
	 */
	public void setAddressingGroupID(int addressingGroupID) {
		this.addressingGroupID = addressingGroupID;
	}

	/**
	 * Sets the inventoryID.
	 * @param inventoryID The inventoryID to set
	 */
	public void setInventoryID(int inventoryID) {
		this.inventoryID = inventoryID;
	}

	/**
	 * Returns the modelNumber.
	 * @return String
	 */
	public String getModelNumber() {
		return modelNumber;
	}

	/**
	 * Sets the modelNumber.
	 * @param modelNumber The modelNumber to set
	 */
	public void setModelNumber(String modelNumber) {
		this.modelNumber = modelNumber;
	}

	/**
	 * Returns the efficiencyRating.
	 * @return int
	 */
	public int getEfficiencyRating() {
		return efficiencyRating;
	}

	/**
	 * Returns the kwCapacity.
	 * @return int
	 */
	public int getKWCapacity() {
		return kwCapacity;
	}

	/**
	 * Sets the efficiencyRating.
	 * @param efficiencyRating The efficiencyRating to set
	 */
	public void setEfficiencyRating(int efficiencyRating) {
		this.efficiencyRating = efficiencyRating;
	}

	/**
	 * Sets the kwCapacity.
	 * @param kwCapacity The kwCapacity to set
	 */
	public void setKWCapacity(int kwCapacity) {
		this.kwCapacity = kwCapacity;
	}

	/**
	 * @return
	 */
	public int getLoadNumber() {
		return loadNumber;
	}

	/**
	 * @param i
	 */
	public void setLoadNumber(int i) {
		loadNumber = i;
	}

	/**
	 * @return
	 */
	public AirConditioner getAirConditioner() {
		return airConditioner;
	}

	/**
	 * @return
	 */
	public DualFuel getDualFuel() {
		return dualFuel;
	}

	/**
	 * @return
	 */
	public Generator getGenerator() {
		return generator;
	}

	/**
	 * @return
	 */
	public GrainDryer getGrainDryer() {
		return grainDryer;
	}

	/**
	 * @return
	 */
	public HeatPump getHeatPump() {
		return heatPump;
	}

	/**
	 * @return
	 */
	public Irrigation getIrrigation() {
		return irrigation;
	}

	/**
	 * @return
	 */
	public StorageHeat getStorageHeat() {
		return storageHeat;
	}

	/**
	 * @return
	 */
	public WaterHeater getWaterHeater() {
		return waterHeater;
	}

	/**
	 * @param conditioner
	 */
	public void setAirConditioner(AirConditioner conditioner) {
		airConditioner = conditioner;
	}

	/**
	 * @param fuel
	 */
	public void setDualFuel(DualFuel fuel) {
		dualFuel = fuel;
	}

	/**
	 * @param generator
	 */
	public void setGenerator(Generator generator) {
		this.generator = generator;
	}

	/**
	 * @param dryer
	 */
	public void setGrainDryer(GrainDryer dryer) {
		grainDryer = dryer;
	}

	/**
	 * @param pump
	 */
	public void setHeatPump(HeatPump pump) {
		heatPump = pump;
	}

	/**
	 * @param irrigation
	 */
	public void setIrrigation(Irrigation irrigation) {
		this.irrigation = irrigation;
	}

	/**
	 * @param heat
	 */
	public void setStorageHeat(StorageHeat heat) {
		storageHeat = heat;
	}

	/**
	 * @param heater
	 */
	public void setWaterHeater(WaterHeater heater) {
		waterHeater = heater;
	}

}
