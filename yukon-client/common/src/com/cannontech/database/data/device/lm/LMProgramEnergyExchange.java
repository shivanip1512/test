package com.cannontech.database.data.device.lm;

import com.cannontech.common.pao.PaoType;
import com.cannontech.database.db.device.lm.LMEnergyExchangeCustomerList;


public class LMProgramEnergyExchange extends LMProgramBase {
    private com.cannontech.database.db.device.lm.LMProgramEnergyExchange energyExchangeProgram = null;

    public LMProgramEnergyExchange() {
        super(PaoType.LM_ENERGY_EXCHANGE_PROGRAM);
    }

    @Override
    public void add() throws java.sql.SQLException {
        super.add();
        getEnergyExchangeProgram().add();

        for (int i = 0; i < getLmProgramStorageVector().size(); i++)
            ((LMEnergyExchangeCustomerList) getLmProgramStorageVector().elementAt(i)).add();
    }

    @Override
    public void delete() throws java.sql.SQLException {
        // delete all of our energy exchange customers first
        delete(LMEnergyExchangeCustomerList.TABLE_NAME, LMEnergyExchangeCustomerList.CONSTRAINT_COLUMNS[0], getPAObjectID());

        // must obtain all offer ids from database because certain tables must
        // be deleted
        // and require offerid which can only be retrieved from the database

        String[] offID = { "Offerid" };
        String[] devID = { "DeviceID" };
        Object[] getDevID = { getPAObjectID() };
        Object[][] results = retrieve(offID, "LMEnergyExchangeProgramOffer", devID, getDevID, true);

        // these tables must be deleted before energy exchange program is
        // deleted
        if (results.length > 0) {
            for (int i = 0; i < results.length; i++) {
                delete("LMenergyExchangeHourlyOffer", offID, results[i]);
                delete("LMenergyExchangeHourlyCustomer", offID, results[i]);
                delete("LMenergyExchangeCustomerReply", offID, results[i]);
                delete("LMenergyExchangeOfferRevision", offID, results[i]);
            }

        }

        delete("LMenergyExchangeProgramOffer", "deviceID", getPAObjectID());
        getEnergyExchangeProgram().delete();
        super.delete();
    }

    public com.cannontech.database.db.device.lm.LMProgramEnergyExchange getEnergyExchangeProgram() {
        if (energyExchangeProgram == null)
            energyExchangeProgram = new com.cannontech.database.db.device.lm.LMProgramEnergyExchange();

        return energyExchangeProgram;
    }

    @Override
    public void retrieve() throws java.sql.SQLException {
        super.retrieve();
        getEnergyExchangeProgram().retrieve();

        LMEnergyExchangeCustomerList[] customers = com.cannontech.database.db.device.lm.LMProgramEnergyExchange.getAllCustomerList(getPAObjectID(),
                                                                                                                                   getDbConnection());

        for (int i = 0; i < customers.length; i++)
            getLmProgramStorageVector().add(customers[i]);
    }

    @Override
    public void setDbConnection(java.sql.Connection conn) {
        super.setDbConnection(conn);
        getEnergyExchangeProgram().setDbConnection(conn);

        for (int i = 0; i < getLmProgramStorageVector().size(); i++)
            ((LMEnergyExchangeCustomerList) getLmProgramStorageVector().elementAt(i)).setDbConnection(conn);
    }

    public void setEnergyExchangeProgram(com.cannontech.database.db.device.lm.LMProgramEnergyExchange newEnergyExchangeProgram) {
        energyExchangeProgram = newEnergyExchangeProgram;
    }

    @Override
    public void setPAObjectID(Integer paoID) {
        super.setPAObjectID(paoID);
        getEnergyExchangeProgram().setDeviceID(paoID);

        for (int i = 0; i < getLmProgramStorageVector().size(); i++)
            ((LMEnergyExchangeCustomerList) getLmProgramStorageVector().elementAt(i)).setDeviceID(paoID);
    }

    @Override
    public void update() throws java.sql.SQLException {
        super.update();
        getEnergyExchangeProgram().update();

        // delete all of our energy exchange customers first
        delete(LMEnergyExchangeCustomerList.TABLE_NAME, LMEnergyExchangeCustomerList.CONSTRAINT_COLUMNS[0], getPAObjectID());

        for (int i = 0; i < getLmProgramStorageVector().size(); i++) {
            ((LMEnergyExchangeCustomerList) getLmProgramStorageVector().elementAt(i)).setDeviceID(getEnergyExchangeProgram().getDeviceID());
            ((LMEnergyExchangeCustomerList) getLmProgramStorageVector().elementAt(i)).setCustomerOrder(new Integer(i + 1));
            ((LMEnergyExchangeCustomerList) getLmProgramStorageVector().elementAt(i)).add();
        }
    }
}
