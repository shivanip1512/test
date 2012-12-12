package com.cannontech.amr.device.search.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LiteDeviceMeterNumber;
import com.cannontech.database.data.lite.LiteYukonPAObject;

@Component
public class DeviceSearchFilterByGenerator {
    
    public static List<DeviceSearchFilterBy> getFilterForFields(final List<DeviceSearchField> fields, final PaoDao paoDao, final DeviceDao deviceDao) {
        List<DeviceSearchFilterBy> filterByList = new ArrayList<DeviceSearchFilterBy>();
        
        for(DeviceSearchField field : fields) {
            switch(field) {
                case NAME:
                    filterByList.add(new DeviceSearchFilterBy(field, "name", new DeviceSearchFilterBy.Validator() {
                        @Override
                        public boolean isValid(LiteYukonPAObject lPao, String value) {
                            return lPao.getPaoName().startsWith(value);
                        }
                    }));
                    break;
                case TYPE:
                    filterByList.add(new DeviceSearchFilterBy(field, "type", new DeviceSearchFilterBy.Validator() {
                        @Override
                        public boolean isValid(LiteYukonPAObject lPao, String value) {
                            return lPao.getPaoType().getDbString().startsWith(value);
                        }
                    }));
                    break;
                case ADDRESS:
                    filterByList.add(new DeviceSearchFilterBy(field, "address", new DeviceSearchFilterBy.Validator() {
                        @Override
                        public boolean isValid(LiteYukonPAObject lPao, String value) {
                            return Integer.toString(lPao.getAddress()).startsWith(value);
                        }
                    }));
                    break;
                case METERNUMBER:
                    filterByList.add(new DeviceSearchFilterBy(field, "meterNumber", new DeviceSearchFilterBy.Validator() {
                        @Override
                        public boolean isValid(LiteYukonPAObject lPao, String value) {
                            LiteDeviceMeterNumber deviceMeterNumber = deviceDao.getLiteDeviceMeterNumber(lPao.getPaoIdentifier().getPaoId());

                            if(deviceMeterNumber != null) {
                                return deviceMeterNumber.getMeterNumber().startsWith(value);
                            }
                            
                            return false;
                        }
                    }));
                    break;
                case ROUTE:
                    filterByList.add(new DeviceSearchFilterBy(field, "route", new DeviceSearchFilterBy.Validator() {
                        @Override
                        public boolean isValid(LiteYukonPAObject lPao, String value) {
                            LiteYukonPAObject route = paoDao.getLiteYukonPAO(lPao.getRouteID());
                            
                            if(route != null) {
                                return route.getPaoName().startsWith(value);
                            }
                            
                            return false;
                        }
                    }));
                    break;
                case COMM_CHANNEL:
                    filterByList.add(new DeviceSearchFilterBy(field, "commChannel", new DeviceSearchFilterBy.Validator() {
                        @Override
                        public boolean isValid(LiteYukonPAObject lPao, String value) {
                            LiteYukonPAObject commChannel = paoDao.getLiteYukonPAO(lPao.getPortID());
                            
                            if(commChannel != null) {
                                return commChannel.getPaoName().startsWith(value);
                            }
                            
                            return false;
                        }
                    }));
                    break;
                case LOAD_GROUP:
                    filterByList.add(new DeviceSearchFilterBy(field, "loadGroup", new DeviceSearchFilterBy.Validator() {
                        @Override
                        public boolean isValid(LiteYukonPAObject lPao, String value) {
                            return lPao.getPaoName().startsWith(value);
                        }
                    }));
                    break;
                case LMGROUP_TYPE:
                    filterByList.add(new DeviceSearchFilterBy(field, "loadGroup", new DeviceSearchFilterBy.Validator() {
                        @Override
                        public boolean isValid(LiteYukonPAObject lPao, String value) {
                            return lPao.getPaoType().getDbString().startsWith(value);
                        }
                    }));
                    break;
                default:
                    filterByList.add(new DeviceSearchFilterBy(field, field.name(), null));
            }
        }
        
        return filterByList;
    }
}
