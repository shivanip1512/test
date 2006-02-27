package com.cannontech.cbc.point;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.database.data.multi.MultiDBPersistent;
import com.cannontech.database.data.multi.SmartMultiDBPersistent;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.data.pao.YukonPAObject;
import com.cannontech.database.data.point.AccumPointParams;
import com.cannontech.database.data.point.AccumulatorPoint;
import com.cannontech.database.data.point.AnalogPoint;
import com.cannontech.database.data.point.AnalogPointParams;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.data.point.PointFactory;
import com.cannontech.database.data.point.PointParams;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.data.point.PointUnits;
import com.cannontech.database.data.point.StatusPoint;
import com.cannontech.database.data.point.StatusPointParams;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.point.PointUnit;
import com.cannontech.database.db.state.StateGroupUtils;




public class CBCPointFactory{
    
    private static final PointParams[] POINT_PROTOTYPES = {
       new StatusPointParams(1,"Capacitor bank state"),
       new StatusPointParams(2,"Re-close Blocked"),
       new StatusPointParams(3,"Control Mode"),
       new StatusPointParams(4,"Auto Volt Control"), 
       new StatusPointParams(5,"Last Control - Local"),
       new StatusPointParams(6,"Last Control -Remote"),
       new StatusPointParams(7,"Last Control – OVUV"),
       new StatusPointParams(8,"Last Control - Neutral Fault"),
       new StatusPointParams(9,"Last Control – Scheduled"),
       new StatusPointParams(10,"Last Control - Digital"),
       new StatusPointParams(11,"Last Control - Analog"),
       new StatusPointParams(12,"Last Control - Temperature"),
       new StatusPointParams(13,"OV Condition"),
       new StatusPointParams(14,"UV Condition"),
       new StatusPointParams(15,"Op Failed - Neutral Current"),
       new StatusPointParams(16,"Neutral Current Fault"),
       new StatusPointParams(24,"Bad Relay"),
       new StatusPointParams(25,"Daily Max Ops"),
       new StatusPointParams(26,"Voltage Delta Abnormal"),
       new StatusPointParams(27,"Temp Alarm"),
       new StatusPointParams(28,"DST Active"),
       new StatusPointParams(29,"Neutral Lockout"),
        
        //analog
        new AnalogPointParams(0.1, 5,"Voltage"),
        new AnalogPointParams(0.1, 6,"High Voltage"),
        new AnalogPointParams(0.1, 7,"Low Voltage"),
        new AnalogPointParams(0.1, 8,"Delta Voltage"),
        new AnalogPointParams(1.0, 9, "Analog Input 1"),
        new AnalogPointParams(0.1, 10, "Temperature"),      
        //accumulator
        new AccumPointParams(1.0, 1, "Total op count"),
        new AccumPointParams(1.0, 2, "OV op count"),
        new AccumPointParams(1.0, 3, "UV op count")
      
   };

    public CBCPointFactory() {
        super();

    }
    
    public static MultiDBPersistent createPointsForCBCDevice(YukonPAObject deviceCBC) {

        
        MultiDBPersistent dbPersistentVector = new MultiDBPersistent();
        
        Integer paoId = deviceCBC.getPAObjectID();
             switch (PAOGroups.getDeviceType(((YukonPAObject) deviceCBC).getPAOType())) {

             case PAOGroups.CBC_7020:
             case PAOGroups.CBC_7022:
             case PAOGroups.CBC_7023:
             case PAOGroups.CBC_7024:
                                                             
                  
                 dbPersistentVector = (MultiDBPersistent) createPointsForCBCDevice(paoId);
             }   
            
        return dbPersistentVector;
}
    
    public static DBPersistent createPointsForCBCDevice (Integer paoId){
        MultiDBPersistent dbPersistentVector = new MultiDBPersistent();
        dbPersistentVector.getDBPersistentVector().addAll(createPoints(paoId));
        return dbPersistentVector;
    }
    
    private static List createPoints(Integer paoId) {

        List pointList = new ArrayList();
        for (int i = 0; i < POINT_PROTOTYPES.length; i++) {
            PointParams array_element = POINT_PROTOTYPES[i];
            PointBase point = PointFactory.createPoint(array_element.getType());
            switch (array_element.getType()) {
            case PointTypes.STATUS_POINT:
                point = (StatusPoint) PointFactory.createNewPoint(point.getPoint()
                                                                       .getPointID(),
                                                                  PointTypes.STATUS_POINT,
                                                                  array_element.getName(),
                                                                  paoId,
                                                                  array_element.getOffset());

                point.getPoint()
                     .setStateGroupID(new Integer(StateGroupUtils.STATEGROUP_TWO_STATE_STATUS));

                break;
            case PointTypes.ANALOG_POINT:
                point = (AnalogPoint) PointFactory.createPoint(PointTypes.ANALOG_POINT);
                point = (AnalogPoint) PointFactory.createAnalogPoint(array_element.getName(),
                                                                     paoId,
                                                                     point.getPoint()
                                                                          .getPointID(),
                                                                     array_element.getOffset(),
                                                                     PointUnits.UOMID_UNDEF,
                                                                     ((AnalogPointParams) array_element).getMult());
                break;

            case PointTypes.PULSE_ACCUMULATOR_POINT:

                point = (AccumulatorPoint) PointFactory.createPoint(PointTypes.PULSE_ACCUMULATOR_POINT);
                point = (AccumulatorPoint) PointFactory.createPulseAccumPoint(array_element.getName(),
                                                                              paoId,
                                                                              point.getPoint()
                                                                                   .getPointID(),
                                                                              array_element.getOffset(),
                                                                              PointUnits.UOMID_UNDEF,
                                                                              ((AccumPointParams) array_element).getMult());

                
                PointUnit punit = new PointUnit(point.getPoint().getPointID(),new Integer (PointUnits.UOMID_COUNTS), 
                                                new Integer(0),
                                                new Double(0.0), new Double(0.0));
                                                                                 
                                                                                 
                                                                                
                ((AccumulatorPoint)point).setPointUnit(punit);
                break;
            }
            pointList.add(point);
        }

        return pointList;

    }
    
}
