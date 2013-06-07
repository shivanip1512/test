package com.cannontech.tools.bulk;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.cannontech.common.bulk.BulkDataContainer;
import com.cannontech.common.bulk.service.BulkMeterDeleterService;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.dispatch.DbChangeType;
import com.cannontech.messaging.message.dispatch.DBChangeMessage;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.yukon.IServerConnection;
import com.cannontech.yukon.conns.ConnPool;

public class BulkMeterDeleter {

    private List<String> getFileLines(String filePath) {
        List<String> fileLines = new ArrayList<String>();

        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            return null;
        } else {
            try {
                String inputLine;
                FileReader fr = new FileReader(file);
                BufferedReader br = new BufferedReader(fr);

                while ((inputLine = br.readLine()) != null) {
                    String[] parsedLines = inputLine.split(",");
                    fileLines.add(parsedLines[0]);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        return fileLines;
    }

    private boolean confirmationCommandLine() {

        System.out.println("Are you sure you want to remove the previous items? Y/N \n");
        String answer = "";

        InputStreamReader stdin = new InputStreamReader(System.in);
        BufferedReader console = new BufferedReader(stdin);

        try {
            answer = console.readLine();
        } catch (IOException ioex) {
            System.out.println(ioex);
        }

        if (answer.equalsIgnoreCase("Y") || answer.equalsIgnoreCase("Yes")) {
            return true;
        } else {
            System.out.println("Item deletion aborted \n");
            return false;
        }

    }

    private void displayRemovableItems(
            List<LiteYukonPAObject> yukonPAList) {

        for (LiteYukonPAObject liteYukonPAObject : yukonPAList) {
            System.out.println(" PaoID = " + liteYukonPAObject.getLiteID() + " Name = " + liteYukonPAObject.getPaoName() + " Address = " + liteYukonPAObject.getAddress() + " Type = " + liteYukonPAObject.getLiteType());
        }
        System.out.println(" -Number of elements to be deleted = "+yukonPAList.size()+"\n");
    }
    
    private void sendDBChangeMsg(BulkDataContainer bulkDataContainer){
    	
    	PointDao pointDao = YukonSpringHook.getBean("pointDao", PointDao.class);
    	IServerConnection connToDispatch = ConnPool.getInstance().getDefDispatchConn();
    	
    	for (LiteYukonPAObject pao : bulkDataContainer.getYukonPAObjects()) {
    		
    		int paoId = pao.getLiteID();
    		DBChangeMessage changeMsgPao = new DBChangeMessage(paoId, DBChangeMessage.CHANGE_PAO_DB, PAOGroups.STRING_CAT_DEVICE, DbChangeType.DELETE);
    		connToDispatch.write(changeMsgPao);
    		
    		List<LitePoint> paoPoints = pointDao.getLitePointsByPaObjectId(paoId);
    		for (LitePoint point : paoPoints) {
    			
    			int pointId = point.getPointID();
    			DBChangeMessage changeMsgPoints = new DBChangeMessage(pointId, DBChangeMessage.CHANGE_POINT_DB, DBChangeMessage.CAT_POINT, DbChangeType.DELETE);
                connToDispatch.write(changeMsgPoints);
    		}
    	}
    }
    
    private BulkDataContainer runArgs(String[] args, BulkMeterDeleterService bulkMeterDeleterService){
        
        BulkDataContainer bulkDataContainer = new BulkDataContainer();
        for (int i = 0; i < args.length; i += 2) {

            // Takes care of the address bulk delete case
            if (args[i].equalsIgnoreCase("-address")) {
                try {

                    if (args[i + 1].equalsIgnoreCase("-range")) {
                        int addressValueA = Integer.parseInt(args[i + 2]);
                        int addressValueB = Integer.parseInt(args[i + 3]);
                        bulkDataContainer = bulkMeterDeleterService.getPAObjectsByAddress(addressValueA,
                                                                                          addressValueB,
                                                                                          bulkDataContainer);
                        i += 2;

                    } else if (args[i + 1].equalsIgnoreCase("-inputFile")) {
                        List<String> fileLines = getFileLines(args[i + 2]);

                        for (String line : fileLines) {
                            int address = Integer.parseInt(line);
                            bulkDataContainer = bulkMeterDeleterService.getPAObjectsByAddress(address,
                                                                                              bulkDataContainer);
                        }

                    } else {
                        bulkDataContainer = bulkMeterDeleterService.getPAObjectsByAddress(Integer.parseInt(args[i + 1]),
                                                                                          bulkDataContainer);
                    }

                } catch (NumberFormatException e) {
                    System.out.println(e);
                }

            }

            // Takes care of the YukonPAObject bulk delete case
            if (args[i].equalsIgnoreCase("-name")) {
                if (args[i + 1].equalsIgnoreCase("-inputFile")) {
                    List<String> fileLines = getFileLines(args[i + 2]);

                    for (String line : fileLines) {
                        bulkDataContainer = bulkMeterDeleterService.getPAObjectsByPaoName(line,
                                                                                          bulkDataContainer);
                    }
                } else {
                    bulkDataContainer = bulkMeterDeleterService.getPAObjectsByPaoName(args[i + 1],
                                                                                      bulkDataContainer);
                }
            }

            // Takes care of the meter number bulk delete case
            if (args[i].equalsIgnoreCase("-meterNumber")) {
                if (args[i + 1].equalsIgnoreCase("-inputFile")) {
                    List<String> fileLines = getFileLines(args[i + 2]);

                    for (String line : fileLines) {
                        bulkDataContainer = bulkMeterDeleterService.getPAObjectsByMeterNumber(line,
                                                                                              bulkDataContainer);
                    }

                    i++;

                } else {
                    bulkDataContainer = bulkMeterDeleterService.getPAObjectsByMeterNumber(args[i + 1],
                                                                                          bulkDataContainer);
                }
            }
        }
        
        return bulkDataContainer;
    }
        
    public static void main(String[] args) {

        System.setProperty("cti.app.name", "BulkMeterDeleter");
        
        // get an instance of BMD
        // call some "main" method, pass args
        BulkMeterDeleter deleter = new BulkMeterDeleter();
        BulkMeterDeleterService bulkMeterDeleterService = YukonSpringHook.getBean("bulkMeterDeleterService", BulkMeterDeleterService.class);

        String methodCall = "bulkMeterDeleter.bat";
        String misUseError = "-- Please use on the of the following formats --" + "\n" + 
                             "  " + methodCall + " -address $value" + "\n" + 
                             "  " + methodCall + " -address -range $valueMin $valueMax" + "\n" + 
                             "  " + methodCall + " -name $value" + "\n" + 
                             "  " + methodCall + " -meterNumber $value" + "\n" + 
                             "  " + methodCall + " [-address | -name | -meterNumber] -inputFile $fileName" + "\n";
        
        
        if (args.length > 1) {
            BulkDataContainer bulkDataContainer = deleter.runArgs(args, bulkMeterDeleterService);
            
            deleter.displayRemovableItems(bulkDataContainer.getYukonPAObjects());

            if (!bulkDataContainer.getFails().isEmpty()) {
                System.out.println("** Errors were found during your scan.  Please resolve these errors and try again. **");
                Map<String, List<String>> failsMap = bulkDataContainer.getFails();
                for (String key : failsMap.keySet()) {
                    List<String> temp = failsMap.get(key);
                    for (String string : temp) {
                        System.out.println("  Error found with the "+key+" = "+string);
                    }
                }
            }else{
                // Removes all the yukonPAObjects after confirmed by the user
                if (deleter.confirmationCommandLine()) {
                    bulkMeterDeleterService.remove(bulkDataContainer);
                    deleter.sendDBChangeMsg(bulkDataContainer);
                }
            
                System.out.println("-- Process Complete --");
            }
            
            
        } else {
            System.out.println(misUseError);
        }
    }
}