package com.cannontech.cbc.db;

import org.apache.commons.lang.Validate;

import com.cannontech.cbc.exceptions.CapControlCreatedObjectRetrievalException;
import com.cannontech.cbc.exceptions.PAODoesntHaveNameException;
import com.cannontech.cbc.model.CBCCreationModel;
import com.cannontech.cbc.point.CBCPointFactory;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.TransactionException;
import com.cannontech.database.data.capcontrol.CCYukonPAOFactory;
import com.cannontech.database.data.capcontrol.CapBank;
import com.cannontech.database.data.capcontrol.CapBankController;
import com.cannontech.database.data.capcontrol.CapBankController702x;
import com.cannontech.database.data.capcontrol.ICapBankController;
import com.cannontech.database.data.device.DeviceBase;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.multi.MultiDBPersistent;
import com.cannontech.database.data.multi.SmartMultiDBPersistent;
import com.cannontech.database.data.pao.CapControlTypes;
import com.cannontech.database.data.pao.DBEditorTypes;
import com.cannontech.database.data.pao.PAOFactory;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.data.pao.YukonPAObject;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.data.point.PointFactory;
import com.cannontech.database.data.point.PointUtil;
import com.cannontech.database.data.point.StatusPoint;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.capcontrol.CapBankAdditional;
import com.cannontech.database.db.pao.PAOSchedule;
import com.cannontech.yukon.cbc.CBCUtils;

public class CapControlFactory {

    private static SmartMultiDBPersistent createParentItems(
            CBCCreationModel model, int paoType) {

        // store the objects we add to the DB
        SmartMultiDBPersistent retSmart = new SmartMultiDBPersistent();

        if (paoType == PAOGroups.CAPBANK) {

            // create the CBC for this CapBank if need be
            if (model.isCreateNested()) {

                int cbcType = model.getNestedWizard().getSelectedType();
                DBPersistent cbcObj = CCYukonPAOFactory.createCapControlPAO(cbcType);

                // set some of the standard fields
                ((YukonPAObject) cbcObj).setDisabled(model.getNestedWizard()
                                                          .getDisabled()
                                                          .booleanValue());
                ((YukonPAObject) cbcObj).setPAOName(model.getNestedWizard()
                                                         .getName());

                // for CBCs that have a portID with it
                if (DeviceTypesFuncs.cbcHasPort(cbcType))
                    ((ICapBankController) cbcObj).setCommID(model.getNestedWizard()
                                                                 .getPortID());

                // recursivly call this monkey to do the CBC support item
                // creations
                SmartMultiDBPersistent childSmart = createChildItems(cbcType,
                                                                     null);

                // add the new children and the owner for this SmartMulti
                PaoDao paoDao = DaoFactory.getPaoDao();
                Integer nextDevID = paoDao.getNextPaoId();
                ((DeviceBase) cbcObj).setDeviceID(nextDevID);
                retSmart.addOwnerDBPersistent(cbcObj);

                for (int i = 0; i < childSmart.size(); i++) {

                    DBPersistent childDB = childSmart.getDBPersistent(i);
                    if (childDB instanceof PointBase)
                        ((PointBase) childDB).getPoint().setPaoID(nextDevID);

                    retSmart.addDBPersistent(childDB);
                }
            }

        }

        return retSmart;
    }

    private static SmartMultiDBPersistent createChildItems(int paoType,
            Integer parentID) {

        // store the objects we add to the DB
        SmartMultiDBPersistent retSmart = new SmartMultiDBPersistent();
        boolean isTwoWay = CBCUtils.isTwoWay(paoType);
        if (DeviceTypesFuncs.isCapBankController(paoType) && !isTwoWay) {
            // create the Status Point for this CBC
            PointBase statusPt = CapBankController.createStatusControlPoint(parentID);

            retSmart.addDBPersistent(statusPt);
        }

        if (paoType == PAOGroups.CAPBANK) {
            // create the Status Point for this CapBank
            PointBase statusPt = PointFactory.createBankStatusPt(parentID);

            // create the Analog Point for this CapBank used to track Op Counts
            PointBase anaPt = PointFactory.createBankOpCntPoint(parentID);

            retSmart.addDBPersistent(statusPt);
            retSmart.addDBPersistent(anaPt);
        }

        return retSmart;
    }

    private static void createPreItems(int paoType, DBPersistent dbObj,
            CBCCreationModel model) {

        // store the objects we add to the DB
        SmartMultiDBPersistent smartMulti = new SmartMultiDBPersistent();
        // work around for a when pao is a sub bus. need when creating points
        if (paoType != PAOGroups.INVALID) {
            smartMulti = CapControlFactory.createParentItems(model, paoType);
        }
        // make sure we are inserting the right object
        try {
            Validate.notNull(smartMulti.getOwnerDBPersistent());
            errorCheckOnCreate(smartMulti.getOwnerDBPersistent(),
                               model.getNestedWizard());
        } catch (IllegalArgumentException nullArg) {
            // don't do anything since we just want to avoid exception thrown to
            // the user

        } catch (PAODoesntHaveNameException e) {
            CTILogger.error(e);
        }

        try {
            DBPersistentUtils.addDBObject(smartMulti);
        } catch (TransactionException e) {
            CTILogger.error(e);
        }

        if (dbObj instanceof CapBank) {
            // set the parent to use the newly created supporting items
            if (model.isCreateNested()) {
                // set the CapBanks ControlDeviceID to be the ID of the CBC we
                // just
                // created
                YukonPAObject pao = ((YukonPAObject) smartMulti.getOwnerDBPersistent());
                ((CapBank) dbObj).getCapBank()
                                 .setControlDeviceID(pao.getPAObjectID());
                StatusPoint statusPt;
                if (pao instanceof CapBankController702x) {
                    MultiDBPersistent pointVector = CBCPointFactory.createPointsForCBCDevice(pao);
                    try {
                        PointUtil.insertIntoDB(pointVector);
                    } catch (TransactionException e) {
                        CTILogger.error(e);
                    }
                    statusPt = (StatusPoint) MultiDBPersistent.getFirstObjectOfType(StatusPoint.class,
                                                                                    pointVector);
                }
                // create addtional info
                // find the first status point in our CBC and assign its ID to
                // our
                // CapBank
                // for control purposes
                else {
                    statusPt = (StatusPoint) SmartMultiDBPersistent.getFirstObjectOfType(StatusPoint.class,
                                                                                         smartMulti);
                }
                ((CapBank) dbObj).getCapBank()
                                 .setControlPointID(statusPt.getPoint()
                                                            .getPointID());

            }

        }
    }

    private static void errorCheckOnCreate(DBPersistent dbObj,
            CBCCreationModel model) throws PAODoesntHaveNameException {
        if (!model.getName().equalsIgnoreCase("")) {
            ((YukonPAObject) dbObj).setPAOName(model.getName());
        } else {

            throw new PAODoesntHaveNameException();
        }
    }

    private static void createPostItems(int paoType, int parentID,
            DBPersistent dbObj) throws TransactionException {
        SmartMultiDBPersistent smartMulti = new SmartMultiDBPersistent();
        // store the objects we add to the DB

        if (dbObj instanceof CapBank) {
            createCapBankAdditional(dbObj);
        }
        if (paoType == CapControlTypes.CAP_CONTROL_FEEDER || paoType == CapControlTypes.CAP_CONTROL_SUBBUS) {
            smartMulti = CBCPointFactory.createPointsForPAO(dbObj);
        } else {
            smartMulti = createChildItems(paoType, new Integer(parentID));

        }

        DBPersistentUtils.addDBObject(smartMulti);

    }

    public static void createCapBankAdditional(DBPersistent dbObj)
            throws TransactionException {
        CapBankAdditional additionalInfo = new CapBankAdditional();
        additionalInfo.setDeviceID(((CapBank) dbObj).getDevice().getDeviceID());
        DBPersistentUtils.addDBObject(additionalInfo);
    }

    public static int createCapControlObject(CBCCreationModel model) {

        int itemID = -1;

        try {

            // if there is a secondaryType set, use that value to creat the PAO
            int paoType = model.getSelectedType();
            DBPersistent dbObj = null;
            int editorType = -1;

            // todo: do this in a better way later
            if (paoType == DBEditorTypes.PAO_SCHEDULE) {

                dbObj = new PAOSchedule();
                ((PAOSchedule) dbObj).setDisabled(model.getDisabled()
                                                       .booleanValue());
                ((PAOSchedule) dbObj).setScheduleName(model.getName());

                DBPersistentUtils.addDBObject(dbObj);
                itemID = ((PAOSchedule) dbObj).getScheduleID().intValue();
                editorType = DBEditorTypes.EDITOR_SCHEDULE;
            } else {
                dbObj = CCYukonPAOFactory.createCapControlPAO(paoType);

                ((YukonPAObject) dbObj).setDisabled(model.getDisabled()
                                                         .booleanValue());

                // for CBCs that have a portID with it
                if (DeviceTypesFuncs.cbcHasPort(paoType))
                    ((ICapBankController) dbObj).setCommID((model).getPortID());
                createPreItems(paoType, dbObj, model);

                // make sure we configured the object correctly
                // before we insert it into DB
                errorCheckOnCreate(dbObj, model);

                DBPersistentUtils.addDBObject(dbObj);
                itemID = ((YukonPAObject) dbObj).getPAObjectID().intValue();
                editorType = DBEditorTypes.EDITOR_CAPCONTROL;

            }

            // creates any extra db objects if need be
            createPostItems(paoType, itemID, dbObj);

            // init this form with the newly created DB object wich should be in
            // the cache
            initItem(itemID, editorType);
            // create points for the CBC702x device
            if (dbObj instanceof CapBankController702x) {
                DBPersistent pointVector = CBCPointFactory.createPointsForCBCDevice((YukonPAObject) dbObj);
                PointUtil.insertIntoDB(pointVector);
            }

            // redirect to this form as the editor for this new DB object

        } catch (TransactionException te) {} catch (PAODoesntHaveNameException e) {
            CTILogger.error(e);
        } catch (CapControlCreatedObjectRetrievalException e) {
            CTILogger.error(e);
        }
        return itemID; // go nowhere since this action failed

    }

    private static void initItem(int id, int type)
            throws CapControlCreatedObjectRetrievalException {

        DBPersistent dbObj = null;

        switch (type) {

        case DBEditorTypes.EDITOR_CAPCONTROL:
            dbObj = PAOFactory.createPAObject(id);
            break;

        case DBEditorTypes.EDITOR_SCHEDULE:
            dbObj = new PAOSchedule();
            ((PAOSchedule) dbObj).setScheduleID(new Integer(id));
            break;

        // case DBEditorTypes.EDITOR_POINT:
        // dbObj = PointFactory.createPoint(
        // PointFuncs.getLitePoint(id).getPointType() );
        // break;
        }

        initItem(id, dbObj);
    }

    private static void initItem(int itemID, DBPersistent dbObj)
            throws CapControlCreatedObjectRetrievalException {
        if (DBPersistentUtils.retrieveDBPersistent(dbObj) == null)
            throw new CapControlCreatedObjectRetrievalException("Object could not be created");
    }
}
