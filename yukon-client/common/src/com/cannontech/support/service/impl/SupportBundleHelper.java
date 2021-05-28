package com.cannontech.support.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.model.LocationData;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.metadatamulti.RfnMetadataMulti;
import com.cannontech.common.rfn.message.metadatamulti.RfnMetadataMultiQueryResult;
import com.cannontech.common.rfn.message.metadatamulti.RfnMetadataMultiResponse;
import com.cannontech.common.rfn.message.node.NodeData;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.FileUtil;
import com.google.common.collect.Lists;

public class SupportBundleHelper {

    private static Logger log = YukonLogManager.getLogger(SupportBundleHelper.class);

    /**
     * Write location data to the destination directory in passed fileName.
     */
    public static void buildAndWriteLocationDataToDir(RfnMetadataMultiResponse response, List<LocationData> locationData, String dir, String fileName, int coloumNum) throws IOException {
        List<String[]> dataRows = Lists.newArrayList();
        for (LocationData loc : locationData) {
            int index = 0;
            String[] dataRow = new String[coloumNum];
            RfnIdentifier rfnIdentifier = loc.getRfnIdentifier();
            RfnMetadataMultiQueryResult metaData = response.getQueryResults().get(rfnIdentifier);
            NodeData nodeData = null;
            if (metaData.isValidResultForMulti(RfnMetadataMulti.NODE_DATA)) {
                boolean isRelay = PaoType.RFN_RELAY == loc.getPaoType();
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
                    dataRow[index++] = loc.getPaoType().toString();
                }
                dataRows.add(dataRow);
            } else {
                log.warn("Invalid location data entry found.");
            }
        }
        File temp = new File(CtiUtilities.getYukonBase() + dir);
        if(!temp.exists()) {
            temp.mkdir();
        }
        FileUtil.writeToCSV(temp.getPath(), fileName, dataRows);
    }
}
