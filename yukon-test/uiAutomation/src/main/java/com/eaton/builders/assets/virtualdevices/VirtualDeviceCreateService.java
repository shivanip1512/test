package com.eaton.builders.assets.virtualdevices;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import org.javatuples.Pair;
import org.json.JSONObject;

import com.eaton.builders.admin.attributes.AttributeAsgmtTypes;
import com.eaton.builders.admin.attributes.AttributeService;
import com.eaton.builders.tools.points.PointCreateService;
import com.eaton.framework.SeleniumTestSetup;
import com.github.javafaker.Faker;

public class VirtualDeviceCreateService {

    public static Pair<JSONObject, JSONObject> createVirtualDeviceOnlyRequiredFields() {
        return new VirtualDeviceCreateBuilder.Builder(Optional.empty())
                .withEnable(Optional.empty())
                .create();
    }    

    public static Map<String, Pair<JSONObject, JSONObject>> createVirtualDeviceWithAnalogPoint() {
        HashMap<String, Pair<JSONObject, JSONObject>> hmap = new HashMap<>();

        Pair<JSONObject, JSONObject> virtualDevicePair = createVirtualDeviceOnlyRequiredFields();

        JSONObject response = virtualDevicePair.getValue1();
        Integer paoId = response.getInt("id");

        Pair<JSONObject, JSONObject> pointPair = PointCreateService.createAnalogPointOnlyRequiredFields(paoId, Optional.empty(), Optional.empty());

        hmap.put("VirtualDevice", virtualDevicePair);
        hmap.put("AnalogPoint", pointPair);

        return hmap;
    }

    public static Map<String, Pair<JSONObject, JSONObject>> createVirtualDeviceWithStatusPoint() {
        HashMap<String, Pair<JSONObject, JSONObject>> hmap = new HashMap<>();

        Pair<JSONObject, JSONObject> virtualDevicePair = createVirtualDeviceOnlyRequiredFields();

        JSONObject response = virtualDevicePair.getValue1();
        Integer paoId = response.getInt("id");

        Pair<JSONObject, JSONObject> pointPair = PointCreateService.createStatusPointOnlyRequiredFields(paoId, Optional.empty(), Optional.empty());

        hmap.put("VirtualDevice", virtualDevicePair);
        hmap.put("StatusPoint", pointPair);

        return hmap;
    }

    public static Map<String, Pair<JSONObject, JSONObject>> createVirtualDeviceWithCalcAnalogPoint() {
        HashMap<String, Pair<JSONObject, JSONObject>> hmap = new HashMap<>();

        Pair<JSONObject, JSONObject> virtualDevicePair = createVirtualDeviceOnlyRequiredFields();

        JSONObject response = virtualDevicePair.getValue1();
        Integer paoId = response.getInt("id");

        Pair<JSONObject, JSONObject> pointPair = PointCreateService.createCalcAnalogPointOnlyRequiredFields(paoId, Optional.empty(), Optional.empty());

        hmap.put("VirtualDevice", virtualDevicePair);
        hmap.put("CalcAnalogPoint", pointPair);

        return hmap;
    }

    public static Map<String, Pair<JSONObject, JSONObject>> createVirtualDeviceWithCalcStatusPoint() {
        HashMap<String, Pair<JSONObject, JSONObject>> hmap = new HashMap<>();

        Pair<JSONObject, JSONObject> virtualDevicePair = createVirtualDeviceOnlyRequiredFields();

        JSONObject response = virtualDevicePair.getValue1();
        Integer paoId = response.getInt("id");

        Pair<JSONObject, JSONObject> pointPair = PointCreateService.createCalcStatusPointOnlyRequiredFields(paoId, Optional.empty(), Optional.empty());

        hmap.put("VirtualDevice", virtualDevicePair);
        hmap.put("CalcStatusPoint", pointPair);

        return hmap;
    }

    public static Map<String, Pair<JSONObject, JSONObject>> createVirtualDeviceWithPulseAccumulatorPoint() {
        HashMap<String, Pair<JSONObject, JSONObject>> hmap = new HashMap<>();

        Pair<JSONObject, JSONObject> virtualDevicePair = createVirtualDeviceOnlyRequiredFields();

        JSONObject response = virtualDevicePair.getValue1();
        Integer paoId = response.getInt("id");

        Pair<JSONObject, JSONObject> pointPair = PointCreateService.createPulseAccumulatorPointOnlyRequiredFields(paoId, Optional.empty(), Optional.empty());

        hmap.put("VirtualDevice", virtualDevicePair);
        hmap.put("PulseAccumulatorPoint", pointPair);

        return hmap;
    }

    public static Map<String, Pair<JSONObject, JSONObject>> createVirtualDeviceWithDemandAccumulatorPoint() {
        HashMap<String, Pair<JSONObject, JSONObject>> hmap = new HashMap<>();

        Pair<JSONObject, JSONObject> virtualDevicePair = createVirtualDeviceOnlyRequiredFields();

        JSONObject response = virtualDevicePair.getValue1();
        Integer paoId = response.getInt("id");

        Pair<JSONObject, JSONObject> pointPair = PointCreateService.createDemandAccumulatorPointOnlyRequiredFields(paoId, Optional.empty(), Optional.empty());

        hmap.put("VirtualDevice", virtualDevicePair);
        hmap.put("DemandAccumulatorPoint", pointPair);

        return hmap;
    }

    public static Map<String, Pair<JSONObject, JSONObject>> createVirtualDeviceWithAllPoints(Optional<String> pointName) {
        HashMap<String, Pair<JSONObject, JSONObject>> hmap = new HashMap<>();

        Pair<JSONObject, JSONObject> virtualDevicePair = createVirtualDeviceOnlyRequiredFields();

        JSONObject response = virtualDevicePair.getValue1();
        Integer paoId = response.getInt("id");

        Pair<JSONObject, JSONObject> analogPointPair = PointCreateService.createAnalogPointOnlyRequiredFields(paoId, Optional.empty(), Optional.empty());
        Pair<JSONObject, JSONObject> statusPointPair = PointCreateService.createStatusPointOnlyRequiredFields(paoId, Optional.empty(), Optional.empty());
        Pair<JSONObject, JSONObject> calcAnalogPointPair = PointCreateService.createCalcAnalogPointOnlyRequiredFields(paoId, Optional.empty(), Optional.empty());
        Pair<JSONObject, JSONObject> calcStatusPointPair = PointCreateService.createCalcStatusPointOnlyRequiredFields(paoId, Optional.empty(), Optional.empty());
        Pair<JSONObject, JSONObject> pulseAccumulatorPointPair = PointCreateService.createPulseAccumulatorPointOnlyRequiredFields(paoId, Optional.empty(), Optional.empty());
        Pair<JSONObject, JSONObject> demandAccumulatorPointPair = PointCreateService.createDemandAccumulatorPointOnlyRequiredFields(paoId, Optional.empty(), Optional.empty());

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
    public static Map<String, Pair<JSONObject, JSONObject>> createMultipleVirtualDeviceRequiredFields(Integer count) {        
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
    
    public static Map<String, Pair<JSONObject, JSONObject>> createVirtualDeviceRequiredFieldsWithMultiplePoints(Integer count) {   
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
            
            Pair<JSONObject, JSONObject> analogPointPair = PointCreateService.createAnalogPointOnlyRequiredFields(paoId, Optional.of(pointNameA), Optional.empty());            
            Pair<JSONObject, JSONObject> statusPointPair = PointCreateService.createStatusPointOnlyRequiredFields(paoId, Optional.of(pointNameB), Optional.empty());
            Pair<JSONObject, JSONObject> calcAnalogPointPair = PointCreateService.createCalcAnalogPointOnlyRequiredFields(paoId, Optional.of(pointNameC), Optional.empty());
            Pair<JSONObject, JSONObject> calcStatusPointPair = PointCreateService.createCalcStatusPointOnlyRequiredFields(paoId, Optional.of(pointNameD), Optional.empty());
            Pair<JSONObject, JSONObject> pulseAccumulatorPointPair = PointCreateService.createPulseAccumulatorPointOnlyRequiredFields(paoId, Optional.of(pointNameE), Optional.empty());
            Pair<JSONObject, JSONObject> demandAccumulatorPointPair = PointCreateService.createDemandAccumulatorPointOnlyRequiredFields(paoId, Optional.of(pointNameF), Optional.empty());
            
            
            map.put(pointNameA, analogPointPair);
            map.put(pointNameB, statusPointPair);
            map.put(pointNameC, calcAnalogPointPair);
            map.put(pointNameD, calcStatusPointPair);
            map.put(pointNameE, pulseAccumulatorPointPair);
            map.put(pointNameF, demandAccumulatorPointPair);
        }
        
        return map;
    }
    
    public static void createVirtualDeviceWithMultiplePointsAndCustomAttributes(Integer count) {      
        Faker faker = SeleniumTestSetup.getFaker();
        String u = UUID.randomUUID().toString().replace("-", "");
        String deviceName = "Vir Dev Multi Points " + u;
        String point;
        String numA;
        String numB;
        String numC;
        String numD;
        String numE;
        String numF;
        
        Pair<JSONObject, JSONObject> virtualDevicePair = new VirtualDeviceCreateBuilder.Builder(Optional.of(deviceName))
                .withEnable(Optional.empty())
                .create();
        
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
                
            point = "Point-";
            numA = num + "a";
            numB = num + "b";
            numC = num + "c";
            numD = num + "d";
            numE = num + "e";
            numF = num + "f";
            
            Integer offSet = faker.number().numberBetween(0, 99999999);
            final AttributeAsgmtTypes.PaoTypes paoType = AttributeAsgmtTypes.PaoTypes.VIRTUAL_SYSTEM;
            
            PointCreateService.createAnalogPointOnlyRequiredFields(paoId, Optional.of(point + numA), Optional.of(offSet));
            AttributeService.createAttributeWithSpecificAssignment(paoType, AttributeAsgmtTypes.PointTypes.ANALOG, offSet, Optional.of("zttr-" + numA));
            
            PointCreateService.createStatusPointOnlyRequiredFields(paoId, Optional.of(point + numB), Optional.of(offSet));
            AttributeService.createAttributeWithSpecificAssignment(paoType, AttributeAsgmtTypes.PointTypes.STATUS, offSet, Optional.of("zttr-" + numB));
            
            PointCreateService.createCalcAnalogPointOnlyRequiredFields(paoId, Optional.of(point + numC), Optional.of(offSet));
            AttributeService.createAttributeWithSpecificAssignment(paoType, AttributeAsgmtTypes.PointTypes.CALCANALOG, offSet, Optional.of("zttr-" + numC));
            
            PointCreateService.createCalcStatusPointOnlyRequiredFields(paoId, Optional.of(point + numD), Optional.of(offSet));
            AttributeService.createAttributeWithSpecificAssignment(paoType, AttributeAsgmtTypes.PointTypes.CALCSTATUS, offSet, Optional.of("zttr-" + numD));
            
            PointCreateService.createPulseAccumulatorPointOnlyRequiredFields(paoId, Optional.of(point + numE), Optional.of(offSet));
            AttributeService.createAttributeWithSpecificAssignment(paoType, AttributeAsgmtTypes.PointTypes.PULSEACCUMULATOR, offSet, Optional.of("zttr-" + numE));
            
            PointCreateService.createDemandAccumulatorPointOnlyRequiredFields(paoId, Optional.of(point + numF), Optional.of(offSet));
            AttributeService.createAttributeWithSpecificAssignment(paoType, AttributeAsgmtTypes.PointTypes.DEMANDACCUMULATOR, offSet, Optional.of("zttr-" + numF));
        }
    }
}
