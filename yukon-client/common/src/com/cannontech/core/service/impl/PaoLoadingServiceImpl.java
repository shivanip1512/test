package com.cannontech.core.service.impl;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.common.device.model.DeviceCollectionReportDevice;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.service.PaoLoadingService;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.ImmutableList.Builder;

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
public class PaoLoadingServiceImpl implements PaoLoadingService, InitializingBean {
    private MeterDao meterDao;
    private DeviceDao deviceDao;
    private PaoDao paoDao;
    
    private List<PaoLoader<DisplayablePao>> displayablePaoloaders;
    private List<PaoLoader<DeviceCollectionReportDevice>> deviceCollectionReportDeviceLoaders;
    
    @Override
    public void afterPropertiesSet() throws Exception {
        Builder<PaoLoader<DisplayablePao>> builder1 = ImmutableList.builder();
        builder1.add(meterDao.getDisplayableDeviceLoader());
        builder1.add(paoDao.getDisplayablePaoLoader());
        displayablePaoloaders = builder1.build();

        Builder<PaoLoader<DeviceCollectionReportDevice>> builder2 = ImmutableList.builder();
        builder2.add(meterDao.getDeviceCollectionReportDeviceLoader());
        builder2.add(deviceDao.getDeviceCollectionReportDeviceLoader());
        deviceCollectionReportDeviceLoaders = builder2.build();
    }
    
    public List<DisplayablePao> getDisplayableDevices(Iterable<? extends YukonPao> paos) {
        
        List<DisplayablePao> result = loadDevices(paos, displayablePaoloaders);
        
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
        List<DeviceCollectionReportDevice> result = loadDevices(paos, deviceCollectionReportDeviceLoaders);
        return result;
    }
    
    public static <T> List<T> loadDevices(Iterable<? extends YukonPao> paos, Iterable<PaoLoader<T>> loaders) {

        Set<YukonPao> unloadedDevices = Sets.newHashSet(paos);
        int requestedDevices = unloadedDevices.size(); // count the "unloaded" because paos is an iterable
        List<T> result = Lists.newArrayListWithExpectedSize(requestedDevices);
        
        for (PaoLoader<T> loader : loaders) {
            if (unloadedDevices.isEmpty()) break;
            Map<YukonPao, T> namesForYukonDevices = loader.getForPaos(unloadedDevices);
            result.addAll(namesForYukonDevices.values());
            unloadedDevices.removeAll(namesForYukonDevices.keySet());
        }
        
        if (!unloadedDevices.isEmpty()) {
            throw new RuntimeException("Unable to load " + unloadedDevices.size() + " of the " + requestedDevices + " requested devices");
        }
        
        return result;
    }
    
    @Autowired
    public void setDeviceDao(DeviceDao deviceDao) {
        this.deviceDao = deviceDao;
    }
    
    @Autowired
    public void setMeterDao(MeterDao meterDao) {
        this.meterDao = meterDao;
    }
    
    @Autowired
    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }
}
