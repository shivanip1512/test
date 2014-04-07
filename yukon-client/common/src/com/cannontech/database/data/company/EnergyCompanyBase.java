package com.cannontech.database.data.company;

import java.sql.Connection;
import java.sql.SQLException;

import com.cannontech.common.editor.EditorPanel;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.data.customer.Contact;
import com.cannontech.database.db.CTIDbChange;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.company.EnergyCompany;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;

public class EnergyCompanyBase extends DBPersistent implements CTIDbChange, EditorPanel {
    private EnergyCompany energyCompany = null;

    public EnergyCompanyBase() {
        super();
    }

    @Override
    public void add() throws SQLException {
        getEnergyCompany().add();
    }

    @Override
    public void delete() throws SQLException {
        delete("EnergyCompanyCustomerList", "EnergyCompanyID", getEnergyCompany().getEnergyCompanyId());
        delete("EnergyCompanyOperatorLoginList", "EnergyCompanyID", getEnergyCompany().getEnergyCompanyId());
        getEnergyCompany().delete();

        if (getEnergyCompany().getPrimaryContactId() != null
            && getEnergyCompany().getPrimaryContactId().intValue() != CtiUtilities.NONE_ZERO_ID) {
            Contact contact = new Contact();
            contact.setContactID(getEnergyCompany().getPrimaryContactId());
            contact.setDbConnection(getDbConnection());
            contact.delete();
        }
    }

    @Override
    public DBChangeMsg[] getDBChangeMsgs(DbChangeType dbChangeType) {
        DBChangeMsg[] msgs =
            { new DBChangeMsg(getEnergyCompany().getEnergyCompanyId().intValue(), DBChangeMsg.CHANGE_ENERGY_COMPANY_DB,
                DBChangeMsg.CAT_ENERGY_COMPANY, dbChangeType) };

        return msgs;
    }

    public EnergyCompany getEnergyCompany() {
        if (energyCompany == null) {
            energyCompany = new EnergyCompany();
        }

        return energyCompany;
    }

    @Override
    public void retrieve() throws java.sql.SQLException {
        getEnergyCompany().retrieve();
    }

    @Override
    public void setDbConnection(Connection conn) {
        super.setDbConnection(conn);
        getEnergyCompany().setDbConnection(conn);
    }

    public void setEnergyCompany(EnergyCompany newEnergyCompany) {
        energyCompany = newEnergyCompany;
    }

    public void setEnergyCompanyID(Integer ecID) {
        getEnergyCompany().setEnergyCompanyId(ecID);
    }

    public void setName(String name) {
        getEnergyCompany().setName(name);
    }

    public String getName() {
        return getEnergyCompany().getName();
    }

    @Override
    public void update() throws java.sql.SQLException {
        getEnergyCompany().update();
    }
}
