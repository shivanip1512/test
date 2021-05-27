package com.cannontech.support.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

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

    public static void buildAndWriteMeterLocDataToDir(RfnMetadataMultiResponse response, List<LocationData> locationData, String dir, String fileName) throws IOException {
        List<String[]> dataRows = Lists.newArrayList();
        for (LocationData loc : locationData) {
            String[] dataRow = new String[11];
            RfnIdentifier rfnIdentifier = loc.getRfnIdentifier();
            RfnMetadataMultiQueryResult metaData = response.getQueryResults().get(rfnIdentifier);
            NodeData nodeData = null;
            if (metaData.isValidResultForMulti(RfnMetadataMulti.NODE_DATA)) {
                nodeData = (NodeData) metaData.getMetadatas().get(RfnMetadataMulti.NODE_DATA);
            }
            dataRow[0] = nodeData != null ? nodeData.getMacAddress() : StringUtils.EMPTY;
            dataRow[1] = rfnIdentifier != null ? rfnIdentifier.getSensorSerialNumber() : StringUtils.EMPTY;
            dataRow[2] = nodeData != null ? nodeData.getNodeSerialNumber() : StringUtils.EMPTY;
            dataRow[3] = nodeData != null ? nodeData.getHardwareVersion() : StringUtils.EMPTY;
            dataRow[4] = nodeData != null ? nodeData.getFirmwareVersion() : StringUtils.EMPTY;
            dataRow[5] = nodeData != null ? "1" : StringUtils.EMPTY; // Need to set hard coded value.
            dataRow[6] = nodeData != null ? String.valueOf(nodeData.getInNetworkTimestamp()) : StringUtils.EMPTY;
            dataRow[7] = nodeData != null ? nodeData.getProductNumber() : StringUtils.EMPTY;
            dataRow[8] = loc != null ? String.valueOf(loc.getLatitude()) : StringUtils.EMPTY;
            dataRow[9] = loc != null ? String.valueOf(loc.getLongitude()) : StringUtils.EMPTY;
            dataRow[10] = loc.getPaoType().toString();
            dataRows.add(dataRow);
        }
        File temp = new File(CtiUtilities.getYukonBase() + dir);
        if(!temp.exists()) {
            temp.mkdir();
        }
        FileUtil.writeToCSV(temp.getPath(), fileName, dataRows);
    }
}
