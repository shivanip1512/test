package com.cannontech.web.stars.dr.operator.inventoryOperations.model.collection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.common.bulk.collection.inventory.YukonCollection;
import com.cannontech.common.bulk.collection.inventory.InventoryCollectionType;
import com.cannontech.common.bulk.collection.inventory.ListBasedInventoryCollection;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.common.collection.CollectionProducer;

public class InventoryIdListCollectionProducer  implements CollectionProducer<InventoryCollectionType, YukonCollection>{

    private InventoryDao inventoryDao;
    
    public InventoryCollectionType getSupportedType() {
        return InventoryCollectionType.idList;
    }

    public YukonCollection createCollection(HttpServletRequest request) throws ServletRequestBindingException {

        final String ids = ServletRequestUtils.getStringParameter(request, getSupportedType().getParameterName("ids"));
        
        final List<Integer> idList = ServletUtil.getIntegerListFromString(ids);
        
        return new ListBasedInventoryCollection() {

            public Map<String, String> getCollectionParameters() {

                Map<String, String> paramMap = new HashMap<String, String>();

                paramMap.put("collectionType", getSupportedType().name());
                paramMap.put(getSupportedType().getParameterName("ids"), ids);

                return paramMap;
            }

            public List<InventoryIdentifier> getList() {

                List<InventoryIdentifier> inventoryList = new ArrayList<InventoryIdentifier>();

                for (int id : idList) {
                    InventoryIdentifier inventory = inventoryDao.getYukonInventory(id);
                    inventoryList.add(inventory);
                }

                return inventoryList;
            }
            
            @Override
            public long getCount() {
                return idList.size();
            }

            @Override
            public MessageSourceResolvable getDescription() {
                return new YukonMessageSourceResolvable("yukon.common.collection.inventory.idList");
            }

        };
    }
    
    @Autowired
    public void setInventoryDao(InventoryDao inventoryDao) {
        this.inventoryDao = inventoryDao;
    }
    
}