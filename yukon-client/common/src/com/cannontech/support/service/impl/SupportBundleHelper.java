package com.cannontech.support.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.model.LocationData;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.metadatamulti.RfnMetadataMulti;
import com.cannontech.common.rfn.message.metadatamulti.RfnMetadataMultiQueryResult;
import com.cannontech.common.rfn.message.metadatamulti.RfnMetadataMultiResponse;
import com.cannontech.common.rfn.message.node.NodeData;
import com.cannontech.common.rfn.model.RfnGatewayData;
import com.cannontech.common.rfn.service.RfnGatewayDataCache;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.FileUtil;
import com.google.common.collect.Lists;

public class SupportBundleHelper {

    private static Logger log = YukonLogManager.getLogger(SupportBundleHelper.class);

    /**
     * Write location data to the destination directory in passed fileName.
     */
    public static void buildAndWriteLocationDataToDir(RfnMetadataMultiResponse response, List<LocationData> locationData, String destDir, String fileName, int coloumNum) throws IOException {
        List<String[]> dataRows = Lists.newArrayList();
        for (LocationData loc : locationData) {
            int index = 0;
            String[] dataRow = new String[coloumNum];
            RfnIdentifier rfnIdentifier = loc.getRfnIdentifier();
            RfnMetadataMultiQueryResult metaData = response.getQueryResults().get(rfnIdentifier);
            NodeData nodeData = null;
            if (metaData.isValidResultForMulti(RfnMetadataMulti.NODE_DATA)) {
                boolean isRelay = PaoType.RFN_RELAY == loc.getPaoIdentifier().getPaoType();
                nodeData = (NodeData) metaData.getMetadatas().get(RfnMetadataMulti.NODE_DATA);
                dataRow[index++] = nodeData != null ? nodeData.getMacAddress() : StringUtils.EMPTY;
                if (!isRelay) {
                    dataRow[index++] = rfnIdentifier != null ? rfnIdentifier.getSensorSerialNumber() : StringUtils.EMPTY;
                }
                dataRow[index++] = nodeData != null ? nodeData.getNodeSerialNumber() : StringUtils.EMPTY;
                dataRow[index++] = nodeData != null ? nodeData.getHardwareVersion() : StringUtils.EMPTY;
                dataRow[index++] = nodeData != null ? nodeData.getFirmwareVersion() : StringUtils.EMPTY;
                dataRow[index++] = nodeData != null ? "1" : StringUtils.EMPTY; // Set hard coded value.
                dataRow[index++] = nodeData != null ? String.valueOf(nodeData.getInNetworkTimestamp()) : StringUtils.EMPTY;
                dataRow[index++] = nodeData != null ? nodeData.getProductNumber() : StringUtils.EMPTY;
                dataRow[index++] = loc != null ? String.valueOf(loc.getLatitude()) : StringUtils.EMPTY;
                dataRow[index++] = loc != null ? String.valueOf(loc.getLongitude()) : StringUtils.EMPTY;
                if (!isRelay) {
                    dataRow[index++] = loc.getPaoIdentifier().getPaoType().toString();
                }
                dataRows.add(dataRow);
            } else {
                log.warn("Invalid location data entry found.");
            }
        }
        File temp = new File(CtiUtilities.getYukonBase() + destDir);
        if(!temp.exists()) {
            temp.mkdir();
        }
        FileUtil.writeToCSV(temp.getPath(), fileName, dataRows);
    }

    /**
     * Write gateway location data to the destination directory in passed fileName.
     */
    public static void buildAndWriteGatewayLocationDataToDir(List<LocationData> dataList, String destDir, String fileName, RfnGatewayDataCache dataCache) {
        List<String[]> dataRows = Lists.newArrayList();
        for(LocationData locData : dataList) {
            PaoIdentifier paoIdentifier = locData.getPaoIdentifier();
            RfnGatewayData gatewatData = dataCache.getIfPresent(paoIdentifier);
            String[] dataRow = new String[11];
            dataRow[0] = gatewatData != null ? gatewatData.getName() : StringUtils.EMPTY;
            RfnIdentifier rfnIdentifier = locData.getRfnIdentifier();
            dataRow[1] = rfnIdentifier != null ? rfnIdentifier.getSensorSerialNumber() : StringUtils.EMPTY;
            dataRow[2] = gatewatData != null ? gatewatData.getMacAddress() : StringUtils.EMPTY;
            dataRow[3] = gatewatData != null ? gatewatData.getSoftwareVersion() : StringUtils.EMPTY;
            dataRow[4] = gatewatData != null ? String.valueOf(gatewatData.getRouteColor()) : StringUtils.EMPTY;
            dataRow[5] = gatewatData != null ? String.valueOf(gatewatData.getControlRate()) : StringUtils.EMPTY;
            dataRow[6] = gatewatData != null ? String.valueOf(gatewatData.getGwTotalNodes()) : StringUtils.EMPTY;
            dataRow[7] = gatewatData != null ? String.valueOf(gatewatData.getGwTotalReadyNodes()) : StringUtils.EMPTY;
            dataRow[8] = gatewatData != null ? String.valueOf(gatewatData.getGwTotalNotReadyNodes()) : StringUtils.EMPTY;
            dataRow[9] = String.valueOf(locData.getLatitude());
            dataRow[10] = String.valueOf(locData.getLongitude());
            dataRows.add(dataRow);
        }
        File temp = new File(CtiUtilities.getYukonBase() + destDir);
        if(!temp.exists()) {
            temp.mkdir();
        }
  
        try {
            FileUtil.writeToCSV(temp.getPath(), fileName, dataRows);
        } catch (IOException ex) {
            log.error("Error found while builing location data for " + fileName, ex);
        }
    }
}
