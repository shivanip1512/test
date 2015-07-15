package com.cannontech.database.data.point;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import com.cannontech.database.db.point.DynamicCalcHistorical;
import com.cannontech.database.db.point.calculation.CalcBase;
import com.cannontech.database.db.point.calculation.CalcComponent;
import com.cannontech.database.db.point.calculation.CalcPointBaseline;
import com.google.common.collect.Lists;

public class CalcStatusPoint extends StatusPoint {
    private CalcBase calcBase = null;
    private List<CalcComponent> calcComponents = null;
    private CalcPointBaseline calcBaselinePoint = null;
    private boolean baselineAssigned = false;

    public CalcStatusPoint() {
        super();
    }

    public void add() throws SQLException {
        super.add();

        getCalcBase().add();

        // add a DynamicClacHistorical row for this new calc point
        DynamicCalcHistorical d = new DynamicCalcHistorical();
        d.setPointID(getPoint().getPointID());

        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(new Date());
        gc.set(GregorianCalendar.DAY_OF_YEAR, (gc.get(GregorianCalendar.DAY_OF_YEAR) - 30));
        d.setLastUpdate(gc);

        d.setDbConnection(getDbConnection());
        d.add();

        for (CalcComponent calcComponent : getCalcComponents()) {
            calcComponent.add();
        }

        if (baselineAssigned) {
            getCalcBaselinePoint().setPointID(getPoint().getPointID());
            getCalcBaselinePoint().add();
        }
    }

    public void addPartial() throws SQLException {

        getCalcBaseDefaults().add();
        super.addPartial();

    }

    public void delete() throws SQLException {
        CalcComponent.deleteCalcComponents(getPoint().getPointID(), getDbConnection());
        if (!baselineAssigned)
            CalcPointBaseline.deleteCalcBaselinePoint(getPoint().getPointID(), getDbConnection());

        // a dynamic table used by the CalcHistorical application
        delete(DynamicCalcHistorical.TABLE_NAME, "PointID", getPoint().getPointID());

        getCalcBase().delete();
        super.delete();
    }

    public void deletePartial() throws SQLException {
        super.deletePartial();
    }

    public CalcBase getCalcBase() {
        if (this.calcBase == null)
            this.calcBase = new CalcBase();

        return this.calcBase;
    }

    public CalcBase getCalcBaseDefaults() {

        getCalcBase().setPeriodicRate(1);
        getCalcBase().setUpdateType("On First Change");
        return getCalcBase();
    }

    public List<CalcComponent> getCalcComponents() {

        if (calcComponents == null)
            calcComponents = Lists.newArrayList();

        return calcComponents;
    }

    public CalcPointBaseline getCalcBaselinePoint() {

        if (calcBaselinePoint == null)
            calcBaselinePoint = new CalcPointBaseline();

        return calcBaselinePoint;
    }

    public boolean getBaselineAssigned() {
        return baselineAssigned;
    }

    public void retrieve() throws SQLException {
        super.retrieve();

        getCalcBase().retrieve();

        calcComponents = CalcComponent.getCalcComponents(getPoint().getPointID());

        calcBaselinePoint = CalcPointBaseline.getCalcBaselinePoint(getPoint().getPointID());
    }

    public void setCalcBase(CalcBase newValue) {
        this.calcBase = newValue;
    }

    public void setCalcComponents(List<CalcComponent> newValue) {
        this.calcComponents = newValue;
    }

    public void setCalcBaselinePoint(CalcPointBaseline newValue) {
        this.calcBaselinePoint = newValue;
    }

    public void setBaselineAssigned(boolean truthValue) {
        baselineAssigned = truthValue;
    }

    public void setDbConnection(Connection conn) {
        super.setDbConnection(conn);

        getCalcBase().setDbConnection(conn);

        for (CalcComponent calcComponent : getCalcComponents()) {
            calcComponent.setDbConnection(conn);
        }

        getCalcBaselinePoint().setDbConnection(conn);
    }

    public void setPointID(Integer pointID) {
        super.setPointID(pointID);

        getCalcBase().setPointID(pointID);

        for (CalcComponent calcComponent : getCalcComponents()) {
            calcComponent.setPointID(pointID);
        }

        getCalcBaselinePoint().setPointID(pointID);
    }

    public void update() throws SQLException {

        super.update();

        getCalcBase().update();

        CalcComponent.deleteCalcComponents(getPoint().getPointID(), getDbConnection());
        for (CalcComponent calcComponent : getCalcComponents()) {
            calcComponent.add();
        }

        CalcPointBaseline.deleteCalcBaselinePoint(getPoint().getPointID(), getDbConnection());

        if (baselineAssigned) {
            getCalcBaselinePoint().setPointID(getPoint().getPointID());
            getCalcBaselinePoint().add();
        }

    }

    public static boolean inUseByPoint(Integer baselineID, String databaseAlias) {
        return CalcPointBaseline.inUseByPoint(baselineID, databaseAlias);
    }

}
