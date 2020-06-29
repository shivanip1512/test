package com.cannontech.web.tools.points.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import com.cannontech.common.util.LazyList;
import com.cannontech.database.data.point.AccumulatorPoint;
import com.cannontech.database.data.point.AnalogPoint;
import com.cannontech.database.data.point.CalcStatusPoint;
import com.cannontech.database.data.point.CalculatedPoint;
import com.cannontech.database.data.point.PointArchiveType;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.data.point.ScalarPoint;
import com.cannontech.database.data.point.StatusPoint;
import com.cannontech.database.db.point.PointLimit;
import com.cannontech.database.db.point.fdr.FDRTranslation;
import com.cannontech.web.editor.point.AlarmTableEntry;
import com.cannontech.web.editor.point.StaleData;
/**
 * @deprecated This is used by the original MVC point editor. Use the REST API point editor and associated objects instead.
 * @see PointBaseModel
 */
@Deprecated(since="7.5")
public class PointModel<T extends PointBase> {

    private T pointBase;
    private StaleData staleData;
    private List<AlarmTableEntry> alarmTableEntries = LazyList.ofInstance(AlarmTableEntry.class);

    public PointModel() {
        this(null, null, new ArrayList<>());
    }

    public PointModel (T base, StaleData staleData, List<AlarmTableEntry> alarmTableEntries) {
        this.pointBase = base;
        this.staleData = staleData;
        this.alarmTableEntries = alarmTableEntries;
    }

    public T getPointBase() {
        return pointBase;
    }

    public void setPointBase(T pointBase) {
        this.pointBase = pointBase;
    }

    public StaleData getStaleData() {
        return staleData;
    }

    public void setStaleData(StaleData staleData) {
        this.staleData = staleData;
    }

    public List<AlarmTableEntry> getAlarmTableEntries() {
        return alarmTableEntries;
    }

    public void setAlarmTableEntries(List<AlarmTableEntry> alarmTableEntries) {
        this.alarmTableEntries = alarmTableEntries;
    }

    public Integer getId() {
        return pointBase.getPoint().getPointID();
    }

    public void finishSetup() {

        Integer id = getId();
        PointBase base = getPointBase();
        
        /* Remove unused translations and fill in the point id on used ones */
        List<FDRTranslation> newFdrs = new ArrayList<>();

        for (FDRTranslation fdrTranslation : base.getPointFDRList()) {
            String translation = fdrTranslation.getTranslation();
            if (translation == null) continue;
            translation = translation.trim();

            if (!translation.isEmpty()) {
                fdrTranslation.setTranslation(translation);
                fdrTranslation.setPointID(id);
                if (StringUtils.containsIgnoreCase(translation, "destination")) {
                    fdrTranslation.setDestination(FDRTranslation.getDestinationField(fdrTranslation.getTranslation()));
                }
                newFdrs.add(fdrTranslation);
            }
        }

        base.setPointFDRTranslations(newFdrs);
        base.getPointAlarming().setPointID(getId());

        if (base.getPoint().getArchiveInterval() == null) {
            base.getPoint().setArchiveInterval(0);
        }

        if (base.getPoint().getArchiveType() == null) {
            base.getPoint().setArchiveType(PointArchiveType.NONE);
        }

        if (base instanceof AnalogPoint) {
            AnalogPoint analogPoint = (AnalogPoint) base;

            analogPoint.getPointAnalog().setPointID(id);
            analogPoint.getPointAnalogControl().setPointID(id);
        }

        if (base instanceof StatusPoint) {
            StatusPoint statusPoint = (StatusPoint) base;

            statusPoint.getPointStatus().setPointID(id);
            statusPoint.getPointStatusControl().setPointID(id);
        }

        if (base instanceof AccumulatorPoint) {
            AccumulatorPoint accumulatorPoint = (AccumulatorPoint) base;

            accumulatorPoint.getPointAccumulator().setPointID(id);
        }

        if (base instanceof ScalarPoint) {
            ScalarPoint scalar = (ScalarPoint) base;

            for (Entry<Integer, PointLimit> entry :scalar.getPointLimitsMap().entrySet()) {
                PointLimit limit = entry.getValue();
                limit.setPointID(id);
                limit.setLimitNumber(entry.getKey());
            }

            scalar.getPointUnit().setPointID(id);
        }

        if (base instanceof CalcStatusPoint) {

            CalcStatusPoint calcStatus = (CalcStatusPoint) base;

            calcStatus.getCalcBase().setPointID(id);
        }

        if (base instanceof CalculatedPoint) {

            CalculatedPoint calcPoint = (CalculatedPoint) base;

            calcPoint.getCalcBase().setPointID(id);
        }
    }

}
