package com.cannontech.web.bulk.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.common.bulk.model.Analysis;
import com.cannontech.common.bulk.model.ArchiveData;
import com.cannontech.common.bulk.model.DeviceArchiveData;
import com.cannontech.common.bulk.model.PixelData;
import com.cannontech.common.bulk.model.ReadType;
import com.cannontech.common.search.SearchResult;
import com.cannontech.web.bulk.model.ArchiveAnalysisResult;
import com.cannontech.web.bulk.model.DeviceCollectionCreationException;
import com.cannontech.web.bulk.model.DeviceCollectionFactory;
import com.google.common.collect.Lists;

public class AdaResultsHelper {

    private DeviceCollectionFactory deviceCollectionFactory;
    
    public ArchiveAnalysisResult buildResults(Analysis analysis, int barWidth, List<DeviceArchiveData> data, HttpServletRequest request) throws ServletRequestBindingException, DeviceCollectionCreationException {
        ArchiveAnalysisResult result = new ArchiveAnalysisResult(analysis);
//        DeviceCollection collection = deviceCollectionFactory.createDeviceCollection(request);
//        result.setDeviceCollection(collection);
        
        long analysisStartLong = analysis.getDateTimeRange().getStartMillis();
        long analysisEndLong = analysis.getDateTimeRange().getEndMillis();
        long analysisLength = analysisEndLong - analysisStartLong;
        long pxRange = analysisLength / barWidth;  // Each pixel represents <pxRange> milliseconds
        
        for (DeviceArchiveData device : data) {
            
            List<PixelData> pixels = Lists.newArrayList();
            for (int i = 0; i < barWidth; i++) {
                PixelData pixel = new PixelData();
                pixels.add(pixel);
            }
            List<ArchiveData> intervals = device.getArchiveData();
            
            boolean done = false;
            int pixelIndex = 0;
            int intervalIndex = 0;
            while (!done) {
                
                ArchiveData archiveData = intervals.get(intervalIndex);
                ReadType intervalReadType = archiveData.getReadType();
                long intervalEnd = archiveData.getArchiveRange().getEndMillis();
                long pixelStart = analysisStartLong + (pixelIndex * pxRange);
                long pixelEnd = analysisStartLong + ((pixelIndex + 1) * pxRange);
                PixelData currentPixel = pixels.get(pixelIndex);
                currentPixel.setStart(pixelStart);
                currentPixel.setEnd(pixelEnd);
                
                if (intervalEnd <= pixelEnd) {
                    // Interval ends inside or on this pixel's end
                    ReadType currentPixelReadType = currentPixel.getReadType();
                    if (currentPixelReadType != null && currentPixelReadType != intervalReadType) {
                        currentPixel.setHasTransition(true);
                    }
                    currentPixel.setReadType(intervalReadType);
                    
                    intervalIndex++;
                    if (intervalEnd == pixelEnd) { // rare
                        // We are done with this pixel, decide what color it is
                        setPixelColor(pixels, pixelIndex, currentPixel);
                        pixelIndex++;
                    }
                } else {
                    // Interval ended after pixel end
                    if (pixelIndex + 1 == pixels.size()) {
                        // We are in the last the last pixel, just include it here
                        ReadType currentPixelReadType = currentPixel.getReadType();
                        if (currentPixelReadType != null && currentPixelReadType != intervalReadType) {
                            currentPixel.setHasTransition(true);
                        }
                        currentPixel.setReadType(intervalReadType);
                        adjustForTransition(pixels, pixelIndex, currentPixel);
                    } else {
                        ReadType currentPixelReadType = currentPixel.getReadType();
                        if (currentPixelReadType != null && currentPixelReadType != intervalReadType) {
                            currentPixel.setHasTransition(true);
                        }
                        currentPixel.setReadType(intervalReadType);
                        // We are done with this pixel, decide what color it is
                        setPixelColor(pixels, pixelIndex, currentPixel);
                    }
                    pixelIndex++;
                }
                
                if (pixelIndex == pixels.size() || intervalIndex == intervals.size()) {
                    done = true;
                }
            }

            device.setTimeline(pixels);
        }
        
        int itemsPerPage = ServletRequestUtils.getIntParameter(request, "itemsPerPage", 25);
        int currentPage = ServletRequestUtils.getIntParameter(request, "page", 1);
        int startIndex = (currentPage - 1) * itemsPerPage;
        int toIndex = startIndex + itemsPerPage;
        int numberOfResults = data.size();
        
        if (numberOfResults < toIndex) toIndex = numberOfResults;
        data = data.subList(startIndex, toIndex);
        
        SearchResult<DeviceArchiveData> searchResult = new SearchResult<DeviceArchiveData>();
        searchResult.setResultList(data);
        searchResult.setBounds(startIndex, itemsPerPage, numberOfResults);
        result.setSearchResult(searchResult);
        
        return result;
    }
    
    private void setPixelColor(List<PixelData> pixels, int pixelIndex, PixelData currentPixel) {
        if (pixelIndex > 0) {
            adjustForTransition(pixels, pixelIndex, currentPixel);
        } else {
            // First pixel, if there were any holes, color it 'missing'
            if (currentPixel.isHasTransition() || currentPixel.getReadType() == ReadType.DATA_MISSING) {
                currentPixel.setReadType(ReadType.DATA_MISSING);
            }
        }
    }

    private void adjustForTransition(List<PixelData> pixels, int pixelIndex, PixelData currentPixel) {
        if (currentPixel.isHasTransition()) {
            // There was a transition, use the opposite read type of the previous pixel.
            PixelData lastPixel = pixels.get(pixelIndex - 1);
            if (lastPixel.getReadType() == ReadType.DATA_MISSING) {
                currentPixel.setReadType(ReadType.DATA_PRESENT);
            } else {
                currentPixel.setReadType(ReadType.DATA_MISSING);
            }
        }
    }
    
}