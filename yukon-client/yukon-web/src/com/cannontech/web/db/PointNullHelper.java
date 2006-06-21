package com.cannontech.web.db;

import org.springframework.jdbc.core.JdbcOperations;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.JdbcTemplateHelper;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.capcontrol.CapBank;
import com.cannontech.database.db.capcontrol.CapControlFeeder;
import com.cannontech.database.db.capcontrol.CapControlSubstationBus;

public class PointNullHelper {

	private String columnName = null;
	private String tableName = null;

	public LitePoint getLitePoint(int paoId, DBPersistent obj) {
		LitePoint litePoint = DaoFactory.getPointDao().getLitePoint(paoId);
		if (litePoint == null) {
			handlePointForPAO(obj, paoId);
		}
		return litePoint;
	}

	private void handlePointForPAO(DBPersistent obj, int controlPtId) {
		init(obj, controlPtId);
		if (controlPtId > 0 && tableName != null && columnName != null) {
			JdbcOperations yukonTemplate = JdbcTemplateHelper.getYukonTemplate();
			String sqlStmt = "UPDATE " + tableName + " SET ";
			sqlStmt += columnName + " = 0";
			sqlStmt += " WHERE " + columnName + " = ?";
			yukonTemplate.update(sqlStmt, new Integer[] { new Integer(
					controlPtId) });
		}
		else {
			CTILogger.warn("Invalid PAO object. Currently supported objects: SubBus, Feeder, Cap Bank - PointNullHelper");
		}
	}

	private void init(DBPersistent obj, int ctlPtId) {
		if (obj instanceof CapBank) {
			tableName = CapBank.TABLE_NAME;
			columnName = CapBank.SETTER_COLUMNS[3];
		}
		if (obj instanceof CapControlFeeder) {
			tableName = CapControlFeeder.TABLE_NAME;
			CapControlFeeder capControlFeeder = ((CapControlFeeder) obj);
			if (ctlPtId == capControlFeeder.getCurrentVarLoadPointID()
					.intValue())
				columnName = CapControlFeeder.SETTER_COLUMNS[0];
			if (ctlPtId == capControlFeeder.getCurrentWattLoadPointID()
					.intValue())
				columnName = CapControlFeeder.SETTER_COLUMNS[1];
			if (ctlPtId == capControlFeeder.getCurrentVoltLoadPointID()
					.intValue())
				columnName = CapControlFeeder.SETTER_COLUMNS[4];
		}
		if (obj instanceof CapControlSubstationBus) {
			CapControlSubstationBus subBus = (CapControlSubstationBus) obj;
			tableName = CapControlSubstationBus.TABLE_NAME;
			if (ctlPtId == subBus.getCurrentVarLoadPointID().intValue())
				columnName = CapControlSubstationBus.SETTER_COLUMNS[0];
			if (ctlPtId == subBus.getCurrentWattLoadPointID().intValue())
				columnName = CapControlSubstationBus.SETTER_COLUMNS[1];
			if (ctlPtId == subBus.getCurrentVoltLoadPointID().intValue())
				columnName = CapControlSubstationBus.SETTER_COLUMNS[4];
			if (ctlPtId == subBus.getSwitchPointID().intValue())
				columnName = CapControlSubstationBus.SETTER_COLUMNS[6];
					
		}
	}

}
