package com.cannontech.web.bulk.service;

import java.util.List;

import org.joda.time.Instant;
import org.joda.time.Interval;

import com.cannontech.common.bulk.model.Analysis;
import com.cannontech.common.bulk.model.ArchiveData;
import com.cannontech.common.bulk.model.DeviceArchiveData;
import com.cannontech.common.bulk.model.PixelData;
import com.cannontech.common.bulk.model.ReadSequence;
import com.cannontech.common.bulk.model.ReadType;
import com.google.common.collect.Lists;

public class AdaResultsHelper {

    public static void buildBars(Analysis analysis, int barWidth, List<DeviceArchiveData> data) {
        // Build pixel data for the visible devices subset
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
                currentPixel.setStart(new Instant(pixelStart));
                currentPixel.setEnd(new Instant(pixelEnd));
                
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
                        // We are in the last pixel, just include it here
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
            
            List<ReadSequence> readData = compressPixelData(pixels);
            device.setTimeline(readData);
        }
        
    }
    
    private static List<ReadSequence> compressPixelData(List<PixelData> pixels) {
        Instant start = pixels.get(0).getStart();
        int colorWidth = 1; //px width of div
        List<ReadSequence> readData = Lists.newArrayList();
        
        for (int i = 0; i < pixels.size(); i++) {
            PixelData current = pixels.get(i);
            PixelData next = i + 1 < pixels.size() ? pixels.get(i + 1) : null;
            
            if (next == null || next.getReadType() != current.getReadType()) {
                ReadSequence sequence = new ReadSequence(colorWidth, current.getReadType().name(), new Interval(start, current.getEnd()));
                readData.add(sequence);
                if (next != null) {
                    start = next.getStart();
                    colorWidth = 1;
                }
            } else {
                colorWidth++;
            }
        }
        
        return readData;
    }

    private static void setPixelColor(List<PixelData> pixels, int pixelIndex, PixelData currentPixel) {
        if (pixelIndex > 0) {
            adjustForTransition(pixels, pixelIndex, currentPixel);
        } else {
            // First pixel, if there were any holes, color it 'missing'
            if (currentPixel.isHasTransition() || currentPixel.getReadType() == ReadType.DATA_MISSING) {
                currentPixel.setReadType(ReadType.DATA_MISSING);
            }
        }
    }

    private static void adjustForTransition(List<PixelData> pixels, int pixelIndex, PixelData currentPixel) {
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