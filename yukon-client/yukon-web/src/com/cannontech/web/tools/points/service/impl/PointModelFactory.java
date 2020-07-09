package com.cannontech.web.tools.points.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.point.ControlStateType;
import com.cannontech.database.data.point.PointArchiveInterval;
import com.cannontech.database.data.point.PointArchiveType;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.data.point.PointFactory;
import com.cannontech.database.data.point.PointType;
import com.cannontech.database.data.point.StatusControlType;
import com.cannontech.database.db.point.PointUnit;
import com.cannontech.database.db.state.StateGroupUtils;
import com.cannontech.web.tools.points.model.AccumulatorPointModel;
import com.cannontech.web.tools.points.model.AnalogPointModel;
import com.cannontech.web.tools.points.model.CalcAnalogPointModel;
import com.cannontech.web.tools.points.model.CalcStatusPointModel;
import com.cannontech.web.tools.points.model.PointBaseModel;
import com.cannontech.web.tools.points.model.ScalarPointModel;
import com.cannontech.web.tools.points.model.StatusPointModel;
import com.cannontech.yukon.IDatabaseCache;

/**
 * The purpose of this class is to create PointBaseModel and Populating default PointBase object based on mandatory
 * fields for different point type.
 */
public class PointModelFactory {
    
    private static IDatabaseCache cache;

    @Autowired
        public PointModelFactory(IDatabaseCache cache){
            PointModelFactory.cache = cache;
        }

    /**
     * Create PointBaseModel based on point Type.
     */
    public final static PointBaseModel<? extends PointBase> getModel(PointType pointType) {

        PointBaseModel<? extends PointBase> pointModel = null;
        switch (pointType) {
            case Analog:
                pointModel = new AnalogPointModel();
                break;
            case CalcAnalog:
                pointModel = new CalcAnalogPointModel();
                break;
            case CalcStatus:
                pointModel = new CalcStatusPointModel();
                break;
            case DemandAccumulator:
            case PulseAccumulator:
                pointModel = new AccumulatorPointModel();
                break;
            case Status:
                pointModel = new StatusPointModel<>();
                break;
            case System:
                break;
            default:
                break;

        }

        return pointModel;
    }

    /**
     * Populating default PointBase object based on mandatory fields present in PointBaseModel for different point
     * types.
     */
    public final static PointBase createPoint(PointBaseModel baseModel) {

        PointBase pointBase = null;
        ScalarPointModel<?> scalarPointModel = null;
        if (baseModel instanceof ScalarPointModel) {
            scalarPointModel = (ScalarPointModel<?>) baseModel;
        }

        switch (baseModel.getPointType()) {
            case Analog:
                pointBase = PointFactory.createAnalogPoint(baseModel.getPointName(),
                                                           baseModel.getPaoId(),
                                                           baseModel.getPointId(),
                                                           baseModel.getPointOffset(),
                                                           scalarPointModel.getPointUnit().getUomId(),
                                                           -1);
                break;
            case CalcAnalog:
                LiteYukonPAObject pao = cache.getAllPaosMap().get(baseModel.getPaoId());
                pointBase = PointFactory.createCalculatedPoint(pao.getPaoIdentifier(),
                                                              baseModel.getPointName(),
                                                              StateGroupUtils.STATEGROUP_ANALOG,
                                                              baseModel.getPointOffset());
                break;
            case CalcStatus:
                pointBase = PointFactory.createCalcStatusPoint(baseModel.getPaoId(),
                                                               baseModel.getPointName(),
                                                               StateGroupUtils.STATEGROUP_ANALOG,
                                                               baseModel.getPointOffset());
                break;
            case DemandAccumulator:
                pointBase =  PointFactory.createDmdAccumPoint(baseModel.getPointName(),
                                                              baseModel.getPaoId(),
                                                              baseModel.getPointId(),
                                                              baseModel.getPointOffset(),
                                                              scalarPointModel.getPointUnit().getUomId(),
                                                              0.1,
                                                              StateGroupUtils.STATEGROUP_ANALOG,
                                                              PointUnit.DEFAULT_DECIMAL_PLACES,
                                                              PointArchiveType.NONE,
                                                              PointArchiveInterval.ZERO);
                break;
            case PulseAccumulator:
                pointBase = PointFactory.createPulseAccumPoint(baseModel.getPointName(),
                                                               baseModel.getPaoId(),
                                                               baseModel.getPointId(),
                                                               baseModel.getPointOffset(),
                                                               scalarPointModel.getPointUnit().getUomId(),
                                                               1.0,
                                                               StateGroupUtils.STATEGROUP_ANALOG,
                                                               PointUnit.DEFAULT_DECIMAL_PLACES,
                                                               PointArchiveType.NONE,
                                                               PointArchiveInterval.ZERO);
                break;
            case Status:
                pointBase = PointFactory.createStatusPoint(baseModel.getPointName(),
                                                           baseModel.getPaoId(),
                                                           baseModel.getPointId(),
                                                           baseModel.getPointOffset(),
                                                           baseModel.getStateGroupId(),
                                                           0,
                                                           0,
                                                           StatusControlType.NONE,
                                                           ControlStateType.OPEN.getControlCommand(),
                                                           ControlStateType.CLOSE.getControlCommand(),
                                                           PointArchiveType.NONE,
                                                           PointArchiveInterval.ZERO);
                break;
            default:
                break;

        }

        return pointBase;
    }

}
