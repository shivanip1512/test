package com.cannontech.core.service.impl;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.plc.impl.PlcBasicDaoImpl;
import com.cannontech.common.device.model.DeviceCollectionReportDevice;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.LMDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.service.PaoLoadingService;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * This class uses lists of PaoLoaders to load PAOs in a flexible manner
 * that allows different types of PAOs to be loaded separately. For instance,
 * to determine a PAOs display name, the name field is used unless the PAO is
 * a meter in which case a role property must be used to determine which
 * field to use. So the main "trick" that this class knows is to work through
 * a list of PaoLoaders offering each the opportunity to load the remaining PAOs
 * that the previous PaoLoaders were unable to load. No attempt is made to
 * examine the PAO's type or category, it is up to each PaoLoader to know
 * which identifiers it is capable of loading and to ignore the others.
 *
 */
public class PaoLoadingServiceImpl implements PaoLoadingService {
    
    @Autowired private MeterDao meterDao;
    @Autowired private DeviceDao deviceDao;
    @Autowired private PaoDao paoDao;
    @Autowired private PlcBasicDaoImpl plcBasicDao;
    @Autowired private LMDao lmDao;
    
    private List<PaoLoader<DisplayablePao>> displayablePaoloaders;
    private List<PaoLoader<DeviceCollectionReportDevice>> deviceCollectionReportDeviceLoaders;
    
    @PostConstruct
    public void init() throws Exception {
        Builder<PaoLoader<DisplayablePao>> builder1 = ImmutableList.builder();
        builder1.add(meterDao.getDisplayableDeviceLoader());
        builder1.add(paoDao.getDisplayablePaoLoader());
        displayablePaoloaders = builder1.build();

        Builder<PaoLoader<DeviceCollectionReportDevice>> builder2 = ImmutableList.builder();
        builder2.add(meterDao.getDeviceCollectionReportDeviceLoader());
        builder2.add(plcBasicDao.getDeviceCollectionReportDeviceLoader());
        builder2.add(deviceDao.getDeviceCollectionReportDeviceLoader());
        builder2.add(lmDao.getDeviceCollectionReportDeviceLoader());
        deviceCollectionReportDeviceLoaders = builder2.build();
    }
    
    @Override
    public Map<PaoIdentifier, DisplayablePao> getDisplayableDeviceLookup(Iterable<? extends YukonPao> paos) {
        return loadDevices(paos, displayablePaoloaders);
    }
    
    @Override
    public List<DisplayablePao> getDisplayableDevices(Iterable<? extends YukonPao> paos) {
        
        Map<PaoIdentifier, DisplayablePao> loadedDevices = loadDevices(paos, displayablePaoloaders);
        List<DisplayablePao> result = Lists.newArrayList(loadedDevices.values());
        
        Collections.sort(result, new Comparator<DisplayablePao> () {
            @Override
            public int compare(DisplayablePao o1, DisplayablePao o2) {
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
        });
        return result;
    }
    
    @Override
    public List<DeviceCollectionReportDevice> getDeviceCollectionReportDevices(Iterable<? extends YukonPao> paos) {
        Map<PaoIdentifier, DeviceCollectionReportDevice> loadedDevices = loadDevices(paos, deviceCollectionReportDeviceLoaders);
        List<DeviceCollectionReportDevice> result = Lists.newArrayList(loadedDevices.values());
        return result;
    }
    
    public static <T> Map<PaoIdentifier, T> loadDevices(Iterable<? extends YukonPao> paos, Iterable<PaoLoader<T>> loaders) {
        
        Set<PaoIdentifier> allIdentifiers = Sets.newHashSet(Iterables.transform(paos, YukonPao.TO_PAO_IDENTIFIER));

        Map<PaoIdentifier, T> result = Maps.newHashMapWithExpectedSize(allIdentifiers.size());
        
        for (PaoLoader<T> loader : loaders) {
            Set<PaoIdentifier> unloadedDevices = Sets.difference(allIdentifiers, result.keySet());
            if (unloadedDevices.isEmpty()) {
                break;
            }
            Map<PaoIdentifier, T> namesForYukonDevices = loader.getForPaos(unloadedDevices);
            result.putAll(namesForYukonDevices);
        }
        
        Set<PaoIdentifier> missingPaos = Sets.difference(allIdentifiers, result.keySet());
        if (!missingPaos.isEmpty()) {
            throw new RuntimeException("Unable to load " + missingPaos.size() +  " of the " + allIdentifiers.size() 
                + " requested devices: " + Joiner.on(", ").join(missingPaos));
        }
        
        return result;
    }
    
    @Override
    public DisplayablePao getDisplayablePao(YukonPao pao) {
        
        List<DisplayablePao> displayableDevices = getDisplayableDevices(Collections.singletonList(pao));
        return displayableDevices.get(0);
    }
    
}