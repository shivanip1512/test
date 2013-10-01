package com.cannontech.tools.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.login.ClientStartupHelper;
import com.cannontech.common.util.BootstrapUtils;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.esub.element.DrawingMetaElement;
import com.google.common.collect.Lists;

public class EsubRoleIdUpdater {
    private final Logger log;

    private EsubRoleIdUpdater(){
        ClientStartupHelper clientStartupHelper = new ClientStartupHelper();
        clientStartupHelper.setAppName("EsubRoleIdUpdater");
        log = YukonLogManager.getLogger(EsubRoleIdUpdater.class);

        String esubBase = BootstrapUtils.getYukonBase(false) + "/Server/web/webapps/ROOT/esub";
        File esubDir = new File(esubBase);
        List<File> jlxFiles = listJlxFiles(esubDir);

        for (File jlxFile : jlxFiles) {
            updateFile(jlxFile);
        }

        log.info("Completed updating esub files");
    }

    public static void main(String[] args) {
        new EsubRoleIdUpdater();
    }

    private List<File> listJlxFiles(File root) {
        List<File> jlxFiles = Lists.newArrayList();
        if (root.isDirectory()) {
            for (File child : root.listFiles()) {
                jlxFiles.addAll(listJlxFiles(child));
            }
        }
        if (root.isFile()) {
            if (root.getName().endsWith("jlx")) {
                jlxFiles.add(root);
            }
        }
        return jlxFiles;
    }

    private void updateFile(File jlxFile) {
        final String filePath = jlxFile.getAbsolutePath();
        FileInputStream fis;
        try {
            fis = new FileInputStream(jlxFile);
        } catch (FileNotFoundException e) {
            log.error("Error opening file " + filePath);
            return;
        }

        String fileAsString = null;
        try (Scanner scanner = new Scanner(fis, "UTF-8")) {
            fileAsString = scanner.useDelimiter("\\A").next();
        }

        //Meta section starts with full class name
        int metaStart = fileAsString.indexOf("com.cannontech.esub.element.DrawingMetaElement");
        if(metaStart == -1){
            log.warn("No meta element in " + filePath);
            return;
        }
        String metaSection = fileAsString.substring(metaStart);

        //Meta section ends with 2 consecutive line breaks
        int metaLength = metaSection.indexOf("\n\n");
        if(metaLength == -1){
            //File may contain Windows-style new lines
            metaLength = metaSection.indexOf("\r\n\r\n");
        }
        if(metaLength == -1){
            //Could be the last part of the file
            metaLength = metaSection.length();
        }

        metaSection = metaSection.substring(0, metaLength);

        //Role id is the last element in the meta section. Elements are space delimited
        int relRoleIdStart = metaSection.lastIndexOf(" ") + 1;
        String sRoleId = metaSection.substring(relRoleIdStart);
        int roleId = 0;
        try{
            roleId= Integer.parseInt(sRoleId);
        } catch(NumberFormatException e){
            log.warn("Unable to find role id in " + filePath);
        }

        //Update role id if it is -1 (obsolete 'Yukon' role) to -206 ('Esubstation Drawings' role)
        if (roleId == -1) {
            String fileOut = fileAsString.substring(0, metaStart + relRoleIdStart);
            fileOut += Integer.toString(YukonRole.OPERATOR_ESUBSTATION_DRAWINGS.getRoleId());
            fileOut += fileAsString.substring(metaStart + metaLength);

            try (FileWriter fw = new FileWriter(filePath)) {
                fw.write(fileOut);
                log.info("Updated role id in " + filePath);
            } catch (IOException e) {
                log.error("Error writing to file " + filePath);
            }
        }
    }
}