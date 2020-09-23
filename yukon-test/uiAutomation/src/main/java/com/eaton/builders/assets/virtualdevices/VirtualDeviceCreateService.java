package com.eaton.builders.assets.virtualdevices;

import java.util.HashMap;
import java.util.Optional;

import org.javatuples.Pair;
import org.json.JSONObject;

import com.eaton.builders.tools.points.PoinCreateService;

public class VirtualDeviceCreateService {
	
	public static Pair<JSONObject, JSONObject> buildAndCreateVirtualDeviceOnlyRequiredFields() {
        return new VirtualDeviceCreateBuilder.Builder(Optional.empty())
        		.create();	
    }

	public static HashMap<String, Pair<JSONObject, JSONObject>> buildAndCreateVirtualDeviceWithAnalogPoint() {
		HashMap<String, Pair<JSONObject, JSONObject>> hmap = new HashMap<String, Pair<JSONObject, JSONObject>>();
		
		Pair<JSONObject, JSONObject> virtualDevicePair = buildAndCreateVirtualDeviceOnlyRequiredFields();
        
        JSONObject response = virtualDevicePair.getValue1();        
        Integer paoId = response.getInt("id");

        Pair<JSONObject, JSONObject> pointPair = PoinCreateService.buildAndCreateAnalogPointOnlyRequiredFields(paoId);
        
        hmap.put("VirtualDevice", virtualDevicePair);
        hmap.put("AnalogPoint", pointPair);
        
        return hmap;
    }
	
	public static HashMap<String, Pair<JSONObject, JSONObject>> buildAndCreateVirtualDeviceWithStatusPoint() {
		HashMap<String, Pair<JSONObject, JSONObject>> hmap = new HashMap<String, Pair<JSONObject, JSONObject>>();
		
		Pair<JSONObject, JSONObject> virtualDevicePair = buildAndCreateVirtualDeviceOnlyRequiredFields();
        
        JSONObject response = virtualDevicePair.getValue1();        
        Integer paoId = response.getInt("id");

        Pair<JSONObject, JSONObject> pointPair = PoinCreateService.buildAndCreateStatusPointOnlyRequiredFields(paoId);
        
        hmap.put("VirtualDevice", virtualDevicePair);
        hmap.put("StatusPoint", pointPair);
        
        return hmap;
	}
	
	public static HashMap<String, Pair<JSONObject, JSONObject>> buildAndCreateVirtualDeviceWithCalcAnalogPoint() {
		HashMap<String, Pair<JSONObject, JSONObject>> hmap = new HashMap<String, Pair<JSONObject, JSONObject>>();
		
		Pair<JSONObject, JSONObject> virtualDevicePair = buildAndCreateVirtualDeviceOnlyRequiredFields();
        
        JSONObject response = virtualDevicePair.getValue1();        
        Integer paoId = response.getInt("id");
        
        Pair<JSONObject, JSONObject> pointPair = PoinCreateService.buildAndCreateCalcAnalogPointOnlyRequiredFields(paoId);
        
        hmap.put("VirtualDevice", virtualDevicePair);
        hmap.put("CalcAnalogPoint", pointPair);
        
        return hmap;
    }
	
	public static HashMap<String, Pair<JSONObject, JSONObject>> buildAndCreateVirtualDeviceWithCalcStatusPoint() {
		HashMap<String, Pair<JSONObject, JSONObject>> hmap = new HashMap<String, Pair<JSONObject, JSONObject>>();
		
		Pair<JSONObject, JSONObject> virtualDevicePair = buildAndCreateVirtualDeviceOnlyRequiredFields();
        
        JSONObject response = virtualDevicePair.getValue1();        
        Integer paoId = response.getInt("id");
        
        Pair<JSONObject, JSONObject> pointPair = PoinCreateService.buildAndCreateCalcStatusPointOnlyRequiredFields(paoId);
        
        hmap.put("VirtualDevice", virtualDevicePair);
        hmap.put("CalcStatusPoint", pointPair);
        
        return hmap;
	}
	
	public static HashMap<String, Pair<JSONObject, JSONObject>> buildAndCreateVirtualDeviceWithPulseAccumulatorPoint() {
		HashMap<String, Pair<JSONObject, JSONObject>> hmap = new HashMap<String, Pair<JSONObject, JSONObject>>();
		
		Pair<JSONObject, JSONObject> virtualDevicePair = buildAndCreateVirtualDeviceOnlyRequiredFields();
        
        JSONObject response = virtualDevicePair.getValue1();        
        Integer paoId = response.getInt("id");
        
        Pair<JSONObject, JSONObject> pointPair = PoinCreateService.buildAndCreatePulseAccumulatorPointOnlyRequiredFields(paoId);
        
        hmap.put("VirtualDevice", virtualDevicePair);
        hmap.put("PulseAccumulatorPoint", pointPair);
        
        return hmap;
	}
	
	public static HashMap<String, Pair<JSONObject, JSONObject>> buildAndCreateVirtualDeviceWithDemandAccumulatorPoint() {
		HashMap<String, Pair<JSONObject, JSONObject>> hmap = new HashMap<String, Pair<JSONObject, JSONObject>>();
		
		Pair<JSONObject, JSONObject> virtualDevicePair = buildAndCreateVirtualDeviceOnlyRequiredFields();
        
        JSONObject response = virtualDevicePair.getValue1();        
        Integer paoId = response.getInt("id");
        
        Pair<JSONObject, JSONObject> pointPair = PoinCreateService.buildAndCreateDemandAccumulatorPointOnlyRequiredFields(paoId);
        
        hmap.put("VirtualDevice", virtualDevicePair);
        hmap.put("DemandAccumulatorPoint", pointPair);
        
        return hmap;
	}
	
	public static HashMap<String, Pair<JSONObject, JSONObject>> buildAndCreateVirtualDeviceWithAllPoints() {
		HashMap<String, Pair<JSONObject, JSONObject>> hmap = new HashMap<String, Pair<JSONObject, JSONObject>>();
		
		Pair<JSONObject, JSONObject> virtualDevicePair = buildAndCreateVirtualDeviceOnlyRequiredFields();
        
        JSONObject response = virtualDevicePair.getValue1();        
        Integer paoId = response.getInt("id");
        
        Pair<JSONObject, JSONObject> analogPointPair = PoinCreateService.buildAndCreateAnalogPointOnlyRequiredFields(paoId);
        Pair<JSONObject, JSONObject> statusPointPair = PoinCreateService.buildAndCreateStatusPointOnlyRequiredFields(paoId);
        Pair<JSONObject, JSONObject> calcAnalogPointPair = PoinCreateService.buildAndCreateCalcAnalogPointOnlyRequiredFields(paoId);
        Pair<JSONObject, JSONObject> calcStatusPointPair = PoinCreateService.buildAndCreateCalcStatusPointOnlyRequiredFields(paoId);
        Pair<JSONObject, JSONObject> pulseAccumulatorPointPair = PoinCreateService.buildAndCreatePulseAccumulatorPointOnlyRequiredFields(paoId);
        Pair<JSONObject, JSONObject> demandAccumulatorPointPair = PoinCreateService.buildAndCreateDemandAccumulatorPointOnlyRequiredFields(paoId);
        
        
        hmap.put("VirtualDevice", virtualDevicePair);
        hmap.put("AnalogPoint", analogPointPair);
        hmap.put("StatusPoint", statusPointPair);
        hmap.put("CalcAnalogPoint", calcAnalogPointPair);
        hmap.put("CalcStatusPoint", calcStatusPointPair);
        hmap.put("PulseAccumulatorPoint", pulseAccumulatorPointPair);
        hmap.put("DemandAccumulatorPoint", demandAccumulatorPointPair);
        
        return hmap;
	}
}
