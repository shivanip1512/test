package com.cannontech.common.bulk.collection.device.service.impl;

import static com.cannontech.common.bulk.collection.device.model.CollectionActionOptionalLogEntry.DEVICE_ERROR_TEXT;
import static com.cannontech.common.bulk.collection.device.model.CollectionActionOptionalLogEntry.POINT_DATA;
import static com.cannontech.common.bulk.collection.device.model.CollectionActionOptionalLogEntry.TIMESTAMP;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.collection.device.model.CollectionActionDetail;
import com.cannontech.common.bulk.collection.device.model.CollectionActionLogDetail;
import com.cannontech.common.bulk.collection.device.model.CollectionActionResult;
import com.cannontech.common.bulk.collection.device.service.CollectionActionLogDetailService;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.core.service.PointFormattingService;
import com.cannontech.core.service.PointFormattingService.Format;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;

public class CollectionActionLogDetailServiceImpl implements CollectionActionLogDetailService {

    @Autowired private DateFormattingService dateFormattingService;
    @Autowired private PointFormattingService pointFormattingService;
    @Autowired private IDatabaseCache dbCache;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    
    private final Logger log = YukonLogManager.getLogger(CollectionActionLogDetailServiceImpl.class);
    
    private Cache<Integer, Set<CollectionActionLogDetail>> cache =
            CacheBuilder.newBuilder().expireAfterAccess(7, TimeUnit.DAYS).build();
    
    @Override
    public List<CollectionActionLogDetail> buildLogDetails(List<? extends YukonPao> paos,
            CollectionActionDetail detail) {
        return paos.stream()
                .map(pao -> new CollectionActionLogDetail(pao, detail))
                .collect(Collectors.toList());
    }
    
    @Override
    public void clearCache(int cacheKey) {
        cache.invalidate(cacheKey);
    }
    
    @Override
    public void appendToLog(CollectionActionResult result, CollectionActionLogDetail detail) {
        if(detail != null) {
            appendToLog(result, Lists.newArrayList(detail));
        }
    }

    @Override
    public void appendToLog(CollectionActionResult result, List<CollectionActionLogDetail> details) {
        if (cache.getIfPresent(result.getCacheKey()) == null) {
            cache.put(result.getCacheKey(), new HashSet<>());
        }
        if (CollectionUtils.isNotEmpty(details)) {
            MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(result.getContext());
            List<String> data = new ArrayList<>();
            for(CollectionActionLogDetail detail: details) {
                if(cache.getIfPresent(result.getCacheKey()).contains(detail)) {
                    continue;
                }
                cache.getIfPresent(result.getCacheKey()).add(detail);
                List<String> fields = new ArrayList<>();
                fields.add(detail.getPao() != null ? dbCache.getAllPaosMap().get(detail.getPao().getPaoIdentifier().getPaoId()).getPaoName() : "");
                fields.add(dateFormattingService.format(new Instant(), DateFormatEnum.BOTH, result.getContext()));
                fields.add(detail.getDetail() != null ? accessor.getMessage(detail.getDetail()) : "");

                if (result.getAction().contains(TIMESTAMP)) {
                    fields.add(detail.getTime() != null
                        ? dateFormattingService.format(detail.getTime(), DateFormatEnum.BOTH, result.getContext())
                        : "");
                }

                if (result.getAction().contains(DEVICE_ERROR_TEXT)) {
                    fields.add(StringUtils.isNotEmpty(detail.getDeviceErrorText()) ? detail.getDeviceErrorText() : "");
                }

                if (result.getAction().contains(POINT_DATA)) {
                    fields.add(detail.getValue() != null
                        ? pointFormattingService.getValueString(detail.getValue(), Format.FULL, result.getContext())
                        : "");
                }

                fields.add(StringUtils.defaultString(detail.getExecutionExceptionText()));
                data.add(String.join(",", fields));
            }
         
            try {
                FileUtils.writeLines(new File(CtiUtilities.getCollectionActionDirPath(), String.valueOf(result.getCacheKey())+".csv"),
                    data, true);
            } catch (IOException e) {
                log.error(log);
            }
        }
    }
    
    @Override
    public File getLog(int cacheKey) throws FileNotFoundException {
        String fileName = String.valueOf(cacheKey) + ".csv";
        File file = new File(CtiUtilities.getCollectionActionDirPath(), fileName);
        if (!file.exists()) {
            String error = fileName + " not found in " + CtiUtilities.getCollectionActionDirPath();
            throw new FileNotFoundException(error);
        }
        return file;
    }

    @Override
    public boolean hasLog(int cacheKey) {
        File file = new File(CtiUtilities.getCollectionActionDirPath(), String.valueOf(cacheKey) + ".csv");
        if (file.exists()) {
            return true;
        }
        return false;
    }
}
