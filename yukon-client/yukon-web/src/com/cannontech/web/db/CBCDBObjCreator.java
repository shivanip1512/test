package com.cannontech.web.db;


import com.cannontech.cbc.model.EditorDataModel;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.TransactionException;
import com.cannontech.database.data.capcontrol.CCYukonPAOFactory;
import com.cannontech.database.data.capcontrol.CapBankController;
import com.cannontech.database.data.capcontrol.ICapBankController;
import com.cannontech.database.data.device.DeviceBase;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.multi.SmartMultiDBPersistent;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.data.pao.YukonPAObject;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.data.point.PointFactory;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.web.wizard.CBCWizardModel;
import com.cannontech.yukon.cbc.CBCUtils;

/**
 * @author ryan
 *
 */
public class CBCDBObjCreator {
	
	//the wizard model data to operate on
	private CBCWizardModel cbcWizardModel = null;


	/**
	 * Forces a WizModel for a new CBC object to be given
	 */
	public CBCDBObjCreator( EditorDataModel cbcWizModel ) {
		super();
		
		if( cbcWizModel == null )
			throw new IllegalArgumentException("A CBCWizardModel of NULL is not acceptable");

		setCbcWizardModel( cbcWizModel );
	}


	/**
	 * Creates extra points or any other supporting object for the given parent
	 * based ont he paoType
	 */
	public SmartMultiDBPersistent createChildItems( int paoType, Integer parentID ) throws TransactionException {
		
		//store the objects we add to the DB
		SmartMultiDBPersistent retSmart = new SmartMultiDBPersistent();
		boolean isTwoWay = CBCUtils.isTwoWay(paoType);
        if( DeviceTypesFuncs.isCapBankController(paoType) && !isTwoWay) {
			//create the Status Point for this CBC
			PointBase statusPt = CapBankController.createStatusControlPoint(parentID);
		
			retSmart.addDBPersistent( statusPt );
		}
		
		if( paoType == PAOGroups.CAPBANK ) {			
			//create the Status Point for this CapBank
			PointBase statusPt = PointFactory.createBankStatusPt(parentID);

			//create the Analog Point for this CapBank used to track Op Counts
			PointBase anaPt = PointFactory.createBankOpCntPoint(parentID);

			retSmart.addDBPersistent( statusPt );
			retSmart.addDBPersistent( anaPt );
		}

		return retSmart;
	}


	/**
	 * Creates extra supporting object(s) for the given parent
	 * based ont he paoType
	 */
	public SmartMultiDBPersistent createParentItems( int paoType ) throws TransactionException {
		
		//store the objects we add to the DB
		SmartMultiDBPersistent retSmart = new SmartMultiDBPersistent();

		if( paoType == PAOGroups.CAPBANK ) {

			//create the CBC for this CapBank if need be
			if( getCbcWizardModel().isCreateNested() ) {

				int cbcType = getCbcWizardModel().getNestedWizard().getSelectedType();
				DBPersistent cbcObj = CCYukonPAOFactory.createCapControlPAO( cbcType );

				//set some of the standard fields
				((YukonPAObject)cbcObj).setDisabled( getCbcWizardModel().getNestedWizard().getDisabled().booleanValue() );
				((YukonPAObject)cbcObj).setPAOName( getCbcWizardModel().getNestedWizard().getName() );
	
				//for CBCs that have a portID with it
				if( DeviceTypesFuncs.cbcHasPort(cbcType) )
					((ICapBankController)cbcObj).setCommID( getCbcWizardModel().getNestedWizard().getPortID() );
	
	
				//recursivly call this monkey to do the CBC support item creations
				SmartMultiDBPersistent childSmart = createChildItems( cbcType, null );
				

				//add the new children and the owner for this SmartMulti
                PaoDao paoDao = DaoFactory.getPaoDao();
				Integer nextDevID = paoDao.getNextPaoId();
				((DeviceBase)cbcObj).setDeviceID( nextDevID );
				retSmart.addOwnerDBPersistent( cbcObj );

				for( int i = 0; i < childSmart.size(); i++ ) {
					
					DBPersistent childDB = childSmart.getDBPersistent(i);
					if( childDB instanceof PointBase )
						((PointBase)childDB).getPoint().setPaoID( nextDevID );

					retSmart.addDBPersistent( childDB );
				}
			}

		}

		return retSmart;
	}

	/**
	 * @return
	 */
	public CBCWizardModel getCbcWizardModel() {
		return cbcWizardModel;
	}

	/**
	 * @param model
	 */
	public void setCbcWizardModel(EditorDataModel model) {
		cbcWizardModel = (CBCWizardModel) model;
	}

}
