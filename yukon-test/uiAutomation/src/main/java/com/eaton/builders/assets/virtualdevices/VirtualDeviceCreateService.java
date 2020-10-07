package com.eaton.builders.assets.virtualdevices;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import org.javatuples.Pair;
import org.json.JSONObject;

import com.eaton.builders.tools.points.PointCreateService;

public class VirtualDeviceCreateService {

    public static Pair<JSONObject, JSONObject> buildAndCreateVirtualDeviceOnlyRequiredFields() {
        return new VirtualDeviceCreateBuilder.Builder(Optional.empty())
                .create();
    }    

    public static Map<String, Pair<JSONObject, JSONObject>> buildAndCreateVirtualDeviceWithAnalogPoint() {
        HashMap<String, Pair<JSONObject, JSONObject>> hmap = new HashMap<>();

        Pair<JSONObject, JSONObject> virtualDevicePair = buildAndCreateVirtualDeviceOnlyRequiredFields();

        JSONObject response = virtualDevicePair.getValue1();
        Integer paoId = response.getInt("id");

        Pair<JSONObject, JSONObject> pointPair = PointCreateService.buildAndCreateAnalogPointOnlyRequiredFields(paoId, Optional.empty());

        hmap.put("VirtualDevice", virtualDevicePair);
        hmap.put("AnalogPoint", pointPair);

        return hmap;
    }

    public static Map<String, Pair<JSONObject, JSONObject>> buildAndCreateVirtualDeviceWithStatusPoint() {
        HashMap<String, Pair<JSONObject, JSONObject>> hmap = new HashMap<>();

        Pair<JSONObject, JSONObject> virtualDevicePair = buildAndCreateVirtualDeviceOnlyRequiredFields();

        JSONObject response = virtualDevicePair.getValue1();
        Integer paoId = response.getInt("id");

        Pair<JSONObject, JSONObject> pointPair = PointCreateService.buildAndCreateStatusPointOnlyRequiredFields(paoId, Optional.empty());

        hmap.put("VirtualDevice", virtualDevicePair);
        hmap.put("StatusPoint", pointPair);

        return hmap;
    }

    public static Map<String, Pair<JSONObject, JSONObject>> buildAndCreateVirtualDeviceWithCalcAnalogPoint() {
        HashMap<String, Pair<JSONObject, JSONObject>> hmap = new HashMap<>();

        Pair<JSONObject, JSONObject> virtualDevicePair = buildAndCreateVirtualDeviceOnlyRequiredFields();

        JSONObject response = virtualDevicePair.getValue1();
        Integer paoId = response.getInt("id");

        Pair<JSONObject, JSONObject> pointPair = PointCreateService.buildAndCreateCalcAnalogPointOnlyRequiredFields(paoId, Optional.empty());

        hmap.put("VirtualDevice", virtualDevicePair);
        hmap.put("CalcAnalogPoint", pointPair);

        return hmap;
    }

    public static Map<String, Pair<JSONObject, JSONObject>> buildAndCreateVirtualDeviceWithCalcStatusPoint() {
        HashMap<String, Pair<JSONObject, JSONObject>> hmap = new HashMap<>();

        Pair<JSONObject, JSONObject> virtualDevicePair = buildAndCreateVirtualDeviceOnlyRequiredFields();

        JSONObject response = virtualDevicePair.getValue1();
        Integer paoId = response.getInt("id");

        Pair<JSONObject, JSONObject> pointPair = PointCreateService.buildAndCreateCalcStatusPointOnlyRequiredFields(paoId, Optional.empty());

        hmap.put("VirtualDevice", virtualDevicePair);
        hmap.put("CalcStatusPoint", pointPair);

        return hmap;
    }

    public static Map<String, Pair<JSONObject, JSONObject>> buildAndCreateVirtualDeviceWithPulseAccumulatorPoint() {
        HashMap<String, Pair<JSONObject, JSONObject>> hmap = new HashMap<>();

        Pair<JSONObject, JSONObject> virtualDevicePair = buildAndCreateVirtualDeviceOnlyRequiredFields();

        JSONObject response = virtualDevicePair.getValue1();
        Integer paoId = response.getInt("id");

        Pair<JSONObject, JSONObject> pointPair = PointCreateService.buildAndCreatePulseAccumulatorPointOnlyRequiredFields(paoId, Optional.empty());

        hmap.put("VirtualDevice", virtualDevicePair);
        hmap.put("PulseAccumulatorPoint", pointPair);

        return hmap;
    }

    public static Map<String, Pair<JSONObject, JSONObject>> buildAndCreateVirtualDeviceWithDemandAccumulatorPoint() {
        HashMap<String, Pair<JSONObject, JSONObject>> hmap = new HashMap<>();

        Pair<JSONObject, JSONObject> virtualDevicePair = buildAndCreateVirtualDeviceOnlyRequiredFields();

        JSONObject response = virtualDevicePair.getValue1();
        Integer paoId = response.getInt("id");

        Pair<JSONObject, JSONObject> pointPair = PointCreateService.buildAndCreateDemandAccumulatorPointOnlyRequiredFields(paoId, Optional.empty());

        hmap.put("VirtualDevice", virtualDevicePair);
        hmap.put("DemandAccumulatorPoint", pointPair);

        return hmap;
    }

    public static Map<String, Pair<JSONObject, JSONObject>> buildAndCreateVirtualDeviceWithAllPoints(Optional<String> pointName) {
        HashMap<String, Pair<JSONObject, JSONObject>> hmap = new HashMap<>();

        Pair<JSONObject, JSONObject> virtualDevicePair = buildAndCreateVirtualDeviceOnlyRequiredFields();

        JSONObject response = virtualDevicePair.getValue1();
        Integer paoId = response.getInt("id");

        Pair<JSONObject, JSONObject> analogPointPair = PointCreateService.buildAndCreateAnalogPointOnlyRequiredFields(paoId, Optional.empty());
        Pair<JSONObject, JSONObject> statusPointPair = PointCreateService.buildAndCreateStatusPointOnlyRequiredFields(paoId, Optional.empty());
        Pair<JSONObject, JSONObject> calcAnalogPointPair = PointCreateService.buildAndCreateCalcAnalogPointOnlyRequiredFields(paoId, Optional.empty());
        Pair<JSONObject, JSONObject> calcStatusPointPair = PointCreateService.buildAndCreateCalcStatusPointOnlyRequiredFields(paoId, Optional.empty());
        Pair<JSONObject, JSONObject> pulseAccumulatorPointPair = PointCreateService.buildAndCreatePulseAccumulatorPointOnlyRequiredFields(paoId, Optional.empty());
        Pair<JSONObject, JSONObject> demandAccumulatorPointPair = PointCreateService.buildAndCreateDemandAccumulatorPointOnlyRequiredFields(paoId, Optional.empty());

        hmap.put("VirtualDevice", virtualDevicePair);
        hmap.put("AnalogPoint", analogPointPair);
        hmap.put("StatusPoint", statusPointPair);
        hmap.put("CalcAnalogPoint", calcAnalogPointPair);
        hmap.put("CalcStatusPoint", calcStatusPointPair);
        hmap.put("PulseAccumulatorPoint", pulseAccumulatorPointPair);
        hmap.put("DemandAccumulatorPoint", demandAccumulatorPointPair);

        return hmap;
    }
    
    /**
     * This method is used to create many virtual devices for manual testing
     * @param count = number of virtual devices to be created
     * @return
     */
    public static Map<String, Pair<JSONObject, JSONObject>> buildAndCreateMultipleVirtualDeviceRequiredFields(Integer count) {        
        HashMap<String, Pair<JSONObject, JSONObject>> map = new HashMap<>();
             
        String deviceName;
        for (int i = 1; i < count; i++) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        
            Random random = new Random();
            String num;
            if (i < 10) {
                num = "000" + i;                
            } else if (i < 100) {
                num = "00" + i;
            }else if (i < 1000) {
                num = "0" + i;
            }else {
                num = "" + i;
            }
                
            deviceName = "Virtual Device-" + num;
            Pair<JSONObject, JSONObject> virtualDevicePair = new VirtualDeviceCreateBuilder.Builder(Optional.of(deviceName))
                    .withEnable(Optional.of(random.nextBoolean()))
                    .create();
            map.put(deviceName, virtualDevicePair);
        }
        
        return map;
    }
    
    public static Map<String, Pair<JSONObject, JSONObject>> buildAndCreateVirtualDeviceRequiredFieldsWithMultiplePoints(Integer count) {        
        HashMap<String, Pair<JSONObject, JSONObject>> map = new HashMap<>();
             
        String u = UUID.randomUUID().toString().replace("-", "");
        String deviceName = "Vir Dev Multi Points " + u;
        String pointNameA;
        String pointNameB;
        String pointNameC;
        String pointNameD;
        String pointNameE;
        String pointNameF;
        
        Pair<JSONObject, JSONObject> virtualDevicePair = new VirtualDeviceCreateBuilder.Builder(Optional.of(deviceName))
                .withEnable(Optional.empty())
                .create();
        
        map.put("VirtualDevice", virtualDevicePair);
        
        JSONObject response = virtualDevicePair.getValue1();
        Integer paoId = response.getInt("id");
        
        for (int i = 1; i < count; i++) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        
            String num;
            if (i < 10) {
                num = "000" + i;                
            } else if (i < 100) {
                num = "00" + i;
            }else if (i < 1000) {
                num = "0" + i;
            }else {
                num = "" + i;
            }
                
            pointNameA = "Point-" + num + "a";
            pointNameB = "Point-" + num + "b";
            pointNameC = "Point-" + num + "c";
            pointNameD = "Point-" + num + "d";
            pointNameE = "Point-" + num + "e";
            pointNameF = "Point-" + num + "f";
            
            Pair<JSONObject, JSONObject> analogPointPair = PointCreateService.buildAndCreateAnalogPointOnlyRequiredFields(paoId, Optional.of(pointNameA));
            Pair<JSONObject, JSONObject> statusPointPair = PointCreateService.buildAndCreateStatusPointOnlyRequiredFields(paoId, Optional.of(pointNameB));
            Pair<JSONObject, JSONObject> calcAnalogPointPair = PointCreateService.buildAndCreateCalcAnalogPointOnlyRequiredFields(paoId, Optional.of(pointNameC));
            Pair<JSONObject, JSONObject> calcStatusPointPair = PointCreateService.buildAndCreateCalcStatusPointOnlyRequiredFields(paoId, Optional.of(pointNameD));
            Pair<JSONObject, JSONObject> pulseAccumulatorPointPair = PointCreateService.buildAndCreatePulseAccumulatorPointOnlyRequiredFields(paoId, Optional.of(pointNameE));
            Pair<JSONObject, JSONObject> demandAccumulatorPointPair = PointCreateService.buildAndCreateDemandAccumulatorPointOnlyRequiredFields(paoId, Optional.of(pointNameF));
            
            
            map.put(pointNameA, analogPointPair);
            map.put(pointNameB, statusPointPair);
            map.put(pointNameC, calcAnalogPointPair);
            map.put(pointNameD, calcStatusPointPair);
            map.put(pointNameE, pulseAccumulatorPointPair);
            map.put(pointNameF, demandAccumulatorPointPair);
        }
        
        return map;
    }
}
