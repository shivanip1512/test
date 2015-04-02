package com.cannontech.web.stars.dr.operator.inventory.service.impl;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

import org.apache.log4j.Logger;
import org.joda.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.collection.inventory.InventoryCollection;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.common.inventory.YukonInventory;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.definition.attribute.lookup.AttributeDefinition;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.Range;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.common.util.ResultExpiredException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dao.RawPointHistoryDao.Order;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.model.LiteLmHardware;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.stars.dr.operator.inventory.model.AbstractInventoryTask;
import com.cannontech.web.stars.dr.operator.inventory.model.AuditRow;
import com.cannontech.web.stars.dr.operator.inventory.model.AuditSettings;
import com.cannontech.web.stars.dr.operator.inventory.model.ControlAuditTask;
import com.cannontech.web.stars.dr.operator.inventory.model.collection.MemoryCollectionProducer;
import com.cannontech.web.stars.dr.operator.inventory.service.ControlAuditService;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class ControlAuditServiceImpl implements ControlAuditService {
    
    @Autowired private InventoryDao inventoryDao;
    @Autowired private PaoDao paoDao;
    @Autowired private RawPointHistoryDao rphDao;
    @Autowired private MemoryCollectionProducer collectionProducer;
    @Autowired private YukonUserContextMessageSourceResolver resolver;
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    @Autowired @Qualifier("inventoryTasks") private RecentResultsCache<AbstractInventoryTask> resultsCache;
    @Autowired @Qualifier("longRunning") private Executor executor;
    
    private static final Logger log = YukonLogManager.getLogger(ControlAuditServiceImpl.class);
    
    private final BuiltInAttribute r1 = BuiltInAttribute.RELAY_1_SHED_TIME_DATA_LOG;
    private final BuiltInAttribute r2 = BuiltInAttribute.RELAY_2_SHED_TIME_DATA_LOG;
    private final BuiltInAttribute r3 = BuiltInAttribute.RELAY_3_SHED_TIME_DATA_LOG;
    
    private ListMultimap<PaoIdentifier, PointValueQualityHolder> getPointData(Date start, Date end,
            ListMultimap<BuiltInAttribute, YukonPao> paosByAttribute) {
        
        Range<Date> dateRange = new Range<Date>(start, true, end, false);
        ListMultimap<PaoIdentifier, PointValueQualityHolder> r1Data =
            rphDao.getAttributeData(paosByAttribute.get(r1), r1, true,
                dateRange.translate(CtiUtilities.INSTANT_FROM_DATE), Order.FORWARD, null);
        ListMultimap<PaoIdentifier, PointValueQualityHolder> r2Data =
            rphDao.getAttributeData(paosByAttribute.get(r2), r2, true,
                dateRange.translate(CtiUtilities.INSTANT_FROM_DATE), Order.FORWARD, null);
        ListMultimap<PaoIdentifier, PointValueQualityHolder> r3Data =
            rphDao.getAttributeData(paosByAttribute.get(r3), r3, true,
                dateRange.translate(CtiUtilities.INSTANT_FROM_DATE), Order.FORWARD, null);
        
        ListMultimap<PaoIdentifier, PointValueQualityHolder> all = ArrayListMultimap.create();
        all.putAll(r1Data);
        all.putAll(r2Data);
        all.putAll(r3Data);
        
        return all;
    }
    
    private boolean doesPaoSupport(YukonPao pao, Attribute attr) {
        
        PaoType type = pao.getPaoIdentifier().getPaoType();
        Map<Attribute, AttributeDefinition> map = paoDefinitionDao.getPaoAttributeAttrDefinitionMap().get(type);
        
        return map.containsKey(attr);
    }
    
    @Override
    public String start(AuditSettings settings, InventoryCollection collection, YukonUserContext userContext) {
        
        log.info(userContext.getYukonUser() + " initiated a control audit task for " 
                 + collection.getCount() + " devices.");
        
        ControlAuditTask task = new ControlAuditTask(settings, collection, userContext);
        String taskId = resultsCache.addResult(task);
        task.setTaskId(taskId);
        
        ControlAuditProcessor processor = new ControlAuditProcessor(task);
        
        executor.execute(processor);
        
        return taskId;
    }
    
    @Override
    public ControlAuditTask getTask(String taskId) throws ResultExpiredException {
        return (ControlAuditTask) resultsCache.getResult(taskId);
    }
    
    @Override
    public List<ControlAuditTask> getAllTasks() {
        
        List<ControlAuditTask> allTasks = resultsCache.getAll(ControlAuditTask.class);
        Collections.sort(allTasks);
        
        return allTasks;
    }
    
    /**
     * This processor handles all the work for a completing an audit.
     * The results of this work are stored in the task.
     */
    final class ControlAuditProcessor implements Runnable {
        
        private final ControlAuditTask task;
        
        public ControlAuditProcessor(ControlAuditTask task) {
            this.task = task;
        }
        
        @Override
        public void run() {
            try {
                
                ListMultimap<BuiltInAttribute, YukonPao> paosByAttribute = ArrayListMultimap.create();
                Map<YukonPao, InventoryIdentifier> inventoryByPao = new HashMap<>();
                
                AuditSettings settings = task.getSettings();
                List<InventoryIdentifier> identifiers = task.getCollection().getList();
                for (List<InventoryIdentifier> identifierSublist : Lists.partition(identifiers, 1000)) {
                    
                    // Check to see if the task was cancelled.
                    if (task.isCanceled()) {
                        break;
                    }
                    
                    Iterable<Integer> inventoryIds = Iterables.transform(identifierSublist, YukonInventory.TO_INVENTORY_ID);
                    Map<Integer, Integer> deviceIdsByInventoryId = inventoryDao.getDeviceIds(inventoryIds);
                    
                    List<PaoIdentifier> paos = paoDao.getPaoIdentifiersForPaoIds(deviceIdsByInventoryId.values());
                    Map<Integer, PaoIdentifier> paosById = Maps.uniqueIndex(paos, YukonPao.TO_PAO_ID);
                    
                    paosByAttribute.clear();
                    inventoryByPao.clear();
                    for (InventoryIdentifier identifier : identifierSublist) {
                        
                        log.debug("Control audit task working on device " + identifier);
                        
                        int deviceId = deviceIdsByInventoryId.get(identifier.getInventoryId());
                        boolean isSupported = true;
                        
                        if (deviceId <= 0) {
                            isSupported = false;
                        } else {
                            YukonPao pao = paosById.get(deviceId);
                            inventoryByPao.put(pao, identifier);
                            
                            // Devices that support relay shed time will always at least support relay #1 shed time.
                            if (!doesPaoSupport(pao, r1)) {
                                isSupported = false;
                            } else {
                                paosByAttribute.put(r1, pao);
                                if (doesPaoSupport(pao, r2)) {
                                    paosByAttribute.put(r2, pao);
                                }
                                if (doesPaoSupport(pao, r3)) {
                                    paosByAttribute.put(r3, pao);
                                }
                            }
                        }
                        if (!isSupported) {
                            AuditRow row = new AuditRow();
                            LiteLmHardware hardware = inventoryDao.getLiteLmHardwareByInventory(identifier);
                            row.setHardware(hardware);
                            task.getUnsupported().add(row);
                            task.addUnsupported();
                        }
                    }
                    
                    Date from = settings.getFrom().toDate();
                    Date to = settings.getTo().toDate();
                    ListMultimap<PaoIdentifier, PointValueQualityHolder> allPointData = 
                            getPointData(from, to, paosByAttribute);
                    
                    List<LiteLmHardware> liteLmHardwareByPaos = 
                            inventoryDao.getLiteLmHardwareByPaos(paosByAttribute.get(r1));
                    
                    ImmutableMap<YukonInventory, LiteLmHardware> inventoryByIdentifier =
                        Maps.uniqueIndex(liteLmHardwareByPaos, LiteLmHardware.TO_INVENTORY);
                    
                    for (YukonPao yukonPao : paosByAttribute.get(r1)) {
                        
                        AuditRow row = new AuditRow();
                        PaoIdentifier pao = yukonPao.getPaoIdentifier();
                        InventoryIdentifier inventory = inventoryByPao.get(pao);
                        row.setHardware(inventoryByIdentifier.get(inventory));
                        
                        List<PointValueQualityHolder> pvl = allPointData.get(pao);
                        
                        if (pvl.isEmpty()) {
                            task.getUnknown().add(row);
                        } else {
                            long durationMinutes = 0;
                            for (PointValueQualityHolder pv : pvl) {
                                durationMinutes += (long) pv.getValue();
                            }
                            Duration d = Duration.standardMinutes(durationMinutes); // Duration of shed time
                            row.setControl(d);
                            if (d.isLongerThan(Duration.standardMinutes(1))) {
                                task.getControlled().add(row);
                            } else {
                                task.getUncontrolled().add(row);
                            }
                        }
                        task.addSuccess();
                    }
                }
                
            } catch (Exception e) {
                // In the face of disaster, die gracefully.
                task.errorOccurred(e);
                log.error("An unexpected error occurred.", e);
            }
        }
    }
    
}