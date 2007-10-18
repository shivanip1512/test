package com.cannontech.tools.bulk;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.cannontech.common.bulk.service.BulkMeterDeleterService;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.spring.YukonSpringHook;

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
    
    private List<LiteYukonPAObject> runArgs(String[] args, BulkMeterDeleterService bulkMeterDeleterService){
        
        List<LiteYukonPAObject> liteYukonPAOBjects = new ArrayList<LiteYukonPAObject>();
        for (int i = 0; i < args.length; i += 2) {

            // Takes care of the address bulk delete case
            if (args[i].equalsIgnoreCase("-address")) {
                try {

                    if (args[i + 1].equalsIgnoreCase("-range")) {
                        int addressValueA = Integer.parseInt(args[i + 2]);
                        int addressValueB = Integer.parseInt(args[i + 3]);
                        liteYukonPAOBjects.addAll(bulkMeterDeleterService.showPAObjectsAddress(addressValueA,
                                                                            addressValueB));

                        i += 2;

                    } else if (args[i + 1].equalsIgnoreCase("-inputFile")) {
                        List<String> fileLines = getFileLines(args[i + 2]);

                        for (String line : fileLines) {
                            int address = Integer.parseInt(line);
                            liteYukonPAOBjects.addAll(bulkMeterDeleterService.showPAObjectsAddress(address));
                        }

                    } else {
                        liteYukonPAOBjects.addAll(bulkMeterDeleterService.showPAObjectsAddress(Integer.parseInt(args[i + 1])));
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
                        liteYukonPAOBjects.addAll(bulkMeterDeleterService.showPAObjectsPaoName(line));
                    }
                } else {
                    liteYukonPAOBjects.addAll(bulkMeterDeleterService.showPAObjectsPaoName(args[i + 1]));
                }
            }

            // Takes care of the meter number bulk delete case
            if (args[i].equalsIgnoreCase("-meterNumber")) {
                if (args[i + 1].equalsIgnoreCase("-inputFile")) {
                    List<String> fileLines = getFileLines(args[i + 2]);

                    for (String line : fileLines) {
                        liteYukonPAOBjects.addAll(bulkMeterDeleterService.showPAObjectsMeterNumber(line));
                    }

                    i++;

                } else {
                    liteYukonPAOBjects.addAll(bulkMeterDeleterService.showPAObjectsMeterNumber(args[i + 1]));
                }
            }
        }
        
        return liteYukonPAOBjects;
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
            List<LiteYukonPAObject> liteYukonPAOBjects = new ArrayList<LiteYukonPAObject>();
            
            liteYukonPAOBjects = deleter.runArgs(args, bulkMeterDeleterService);
            
            deleter.displayRemovableItems(liteYukonPAOBjects);

            if (bulkMeterDeleterService.getErrors().length() > 0) {
                System.out.println("** Errors were found during your scan.  Please resolve these errors and try again. **");
                System.out.println(bulkMeterDeleterService.getErrors());
            }else{
            // Removes all the yukonPAObjects after confirmed by the user
            if (deleter.confirmationCommandLine()) {
                bulkMeterDeleterService.remove(liteYukonPAOBjects);
            }
            
                System.out.println("-- Process Complete --");
            }
            
            
        } else {
            System.out.println(misUseError);
        }
    }
}