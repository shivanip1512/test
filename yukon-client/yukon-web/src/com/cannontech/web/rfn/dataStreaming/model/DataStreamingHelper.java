package com.cannontech.web.rfn.dataStreaming.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.cannontech.common.device.streaming.model.Behavior;
import com.cannontech.common.device.streaming.model.BehaviorType;
import com.cannontech.common.device.streaming.model.BehaviorValue;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;

public class DataStreamingHelper {
        
        public static DataStreamingConfig convertBehaviorToConfig(Behavior behavior) {
            DataStreamingConfig config = new DataStreamingConfig();
            
            config.setId(behavior.getId());
            List<DataStreamingAttribute> atts = new ArrayList<>();
            List<BehaviorValue> values = behavior.getValues();
            for (BehaviorValue value : values) {
                String name = value.getName();
                if (name.contains("attribute")){
                    DataStreamingAttribute att = new DataStreamingAttribute();
                    att.setAttribute(BuiltInAttribute.valueOf(value.getValue()));
                    att.setAttributeOn(true);
                    String intervalSearch = name.substring(0, name.lastIndexOf("."));
                    BehaviorValue interval = (BehaviorValue) Arrays
                            .stream(values.toArray())
                            .filter(x -> ((BehaviorValue) x).getName().equals(intervalSearch + ".interval"))
                            .findFirst()
                            .orElse(null);
                    if (interval != null) {
                        att.setInterval(Integer.parseInt(interval.getValue()));
                    }
                    atts.add(att);
                }

            }
            
            config.setAttributes(atts);
            
            return config;
        }
        
        public static void addAllAttributesToConfig(DataStreamingConfig config) {
            //TODO: get this list from somewhere
            List<DataStreamingAttribute> attributes = new ArrayList<>();
            List<String> attributeStrings = new ArrayList<>();
            attributeStrings.add("KVAR");
            attributeStrings.add("DEMAND");
            attributeStrings.add("DELIVERED_KWH");
            attributeStrings.add("RECEIVED_KWH");
            for (String attString : attributeStrings) {
                DataStreamingAttribute attribute = new DataStreamingAttribute();
                attribute.setAttribute(BuiltInAttribute.valueOf(attString));
                attributes.add(attribute);
            }
            config.setAttributes(attributes);
        }
        
        public static List<Integer> getAllIntervals() {
            //TODO: get these from somewhere?
            List<Integer> intervals = new ArrayList<>();
            intervals.add(1);
            intervals.add(3);
            intervals.add(5);
            intervals.add(15);
            intervals.add(30);
            return intervals;
        }
        
        public static Behavior convertConfigToBehavior(DataStreamingConfig config) {
            Behavior behavior = new Behavior();
            behavior.setType(BehaviorType.DATA_STREAMING);
            int index = 0;
            for (DataStreamingAttribute att : config.getAttributes()) {
                if (att.isAttributeOn()) {
                    BehaviorValue value = new BehaviorValue();
                    value.setName("channels." + index + ".attribute");
                    value.setValue(att.getAttribute().getKey());
                    behavior.getValues().add(value);
                    BehaviorValue value2 = new BehaviorValue();
                    value2.setName("channels." + index + ".interval");
                    value2.setValue(Integer.toString(config.getSelectedInterval()));
                    behavior.getValues().add(value2);
                    index++;
                }
            }
            BehaviorValue channels = new BehaviorValue();
            channels.setName("channels");
            channels.setValue(Integer.toString(index));
            behavior.getValues().add(0, channels);
            return behavior;
        }

}
