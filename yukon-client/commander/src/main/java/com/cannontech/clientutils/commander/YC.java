package com.cannontech.clientutils.commander;

/**
 * Insert the type's description here.
 * Creation date: (2/25/2002 3:24:43 PM)
 * @author: 
 */
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.Timer;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.logging.log4j.Logger;

import com.cannontech.amr.errors.dao.DeviceErrorTranslatorDao;
import com.cannontech.amr.errors.model.DeviceErrorDescription;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.clientutils.commander.model.OutputMessage;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.groups.service.DeviceGroupTreeFactory;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.KeysAndValues;
import com.cannontech.common.util.KeysAndValuesFile;
import com.cannontech.core.authorization.exception.PaoAuthorizationException;
import com.cannontech.core.authorization.service.LMCommandAuthorizationService;
import com.cannontech.core.authorization.service.PaoCommandAuthorizationService;
import com.cannontech.core.dao.CommandDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.data.command.DeviceTypeCommand;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteCommand;
import com.cannontech.database.data.lite.LiteComparators;
import com.cannontech.database.data.lite.LiteDeviceMeterNumber;
import com.cannontech.database.data.lite.LiteDeviceTypeCommand;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.data.point.PointType;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.command.Command;
import com.cannontech.database.db.command.CommandCategory;
import com.cannontech.database.db.command.CommandCategoryUtil;
import com.cannontech.database.db.device.Device;
import com.cannontech.database.model.LiteBaseTreeModel;
import com.cannontech.database.model.NullDBTreeModel;
import com.cannontech.database.model.TreeModelEnum;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.message.dispatch.message.SystemLogHelper;
import com.cannontech.message.porter.message.Request;
import com.cannontech.message.porter.message.Return;
import com.cannontech.message.util.ClientConnection;
import com.cannontech.message.util.Message;
import com.cannontech.message.util.MessageEvent;
import com.cannontech.message.util.MessageListener;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.util.ColorUtil;
import com.cannontech.yukon.IDatabaseCache;
import com.cannontech.yukon.IServerConnection;
import com.cannontech.yukon.conns.ConnPool;

public class YC extends Observable implements MessageListener {
    
    protected static final IServerConnection connection = ConnPool.getInstance().getDefPorterConn();
    protected static final String commandsDir = CtiUtilities.getCommandsDirPath();
    protected static final Logger log = YukonLogManager.getLogger(YC.class);
    
    protected IDatabaseCache cache = YukonSpringHook.getBean(IDatabaseCache.class);
    protected PointDao pointDao = YukonSpringHook.getBean(PointDao.class);
    protected DeviceGroupService deviceGroupService = YukonSpringHook.getBean(DeviceGroupService.class);
    protected PaoCommandAuthorizationService paoCommandAuthService = YukonSpringHook.getBean(PaoCommandAuthorizationService.class);
    protected LMCommandAuthorizationService lmCommandAuthService = YukonSpringHook.getBean(LMCommandAuthorizationService.class);
    protected CommandDao commandDao = YukonSpringHook.getBean(CommandDao.class);
    protected DeviceErrorTranslatorDao deviceErrorTranslatorDao = YukonSpringHook.getBean(DeviceErrorTranslatorDao.class);

    private final SystemLogHelper systemLogHelper;
    private String logUserName;
    
    // HashSet of userMessageIds for this instance
    private Set<Long> requestMessageIDs = new HashSet<>(10);

    // HashSet of userMessageIds for this instance, we will remove from this list on ExpectMore flag 
    // which is no longer guaranteed
    // but very useful for refreshing the custom web interface in a timely fashion.
    private Set<Long> requestMessageIDs_executing = new HashSet<>(10);

    // Time in millis (yes I realize this is an int) to wait for command response messages. 
    private int timeOut = 0;
    
    // An action listener for the timer events.
    private ActionListener timerPerfomer;
    
    // A timer which fires an action event after a specified delay in millis.
    private Timer stopWatch;
    
    // Porter Return messages displayable text.
    private String resultText = "";
    
    // A string to hold error messages for the current command(s) sent.
    private String errorMsg = "";
    
    // Current command string to execute, the actual string entered from the command line.
    private String commandString = "";
    
    // The parsed vector of commands from the command line.
    private Vector<String> executeCmdsVector;
    
    // liteYukonPao or serialNumber will be used to send command to.
    // Selected liteYukonPAObject
    private LiteYukonPAObject liteYukonPao;
    
    // Selected serial number
    private String serialNumber;
    
    // Selected tree item object
    private Object treeItem;
    
    // Selected route id, used for serialNumber commands or for some loop commands.
    private int routeID = -1;
    
    // Selected tree model type. Refer to com.cannontech.database.model.* for valid models.
    private Class<? extends LiteBaseTreeModel> modelType = NullDBTreeModel.class;
    
    // Valid send command modes 
    public static int DEFAULT_MODE = 0; // Send commands with YC addt's (loop, queue, route..)
    public static int CGP_MODE = 1; // Send commands as they are, no parsing done on them.
    private int commandMode = DEFAULT_MODE;
    
    // Store last Porter request message, for use when need to send it again (loop).
    private Request porterRequest;
    
    // Singleton incrementor for messageIDs to send to porter connection.
    private static volatile long currentUserMessageID = 1;
    
    // Flag indicating more commands to send out (mainly loop commands).
    public volatile int sendMore = 0;

    // All LiteYukonPaobject of type route.
    private LiteYukonPAObject[] allRoutes;

    // Valid loop command types
    public static int NOLOOP = 0; // Loop not parsed
    public static int LOOP = 1; // Loop alone parsed
    public static int LOOPNUM = 2; // Loop for some num of times
    public static int LOOPLOCATE = 3; // Loop locate parsed
    public static int LOOPLOCATE_ROUTE = 4; // Loop locate route parsed
    private int loopType = NOLOOP;
    
    // Contains LiteDeviceTypeCommand for the deviceType selected.
    private List<LiteDeviceTypeCommand> liteDeviceTypeCommands = new ArrayList<>();
    
    // The device Type for the currently selected object in the tree model. Values found in DeviceTypes class.
    protected String deviceType = "";
    
    // Default YC properties
    private YCDefaults ycDefaults;
    
    // Fields used in the MessageReceived(..) method to track last printed data
    protected SimpleDateFormat displayFormat = new SimpleDateFormat("MMM d HH:mm:ss a z");
    
    // Keep track of the last userMessageID from the MessageEvents.
    private long prevUserID = -1;
    
    private LiteYukonUser user;
    
    public YC() {
        // Don't load defaults from file (mainly for web servlet)
        this(false);
    }
    
    /**
     * If loadDefaultsFromFile is true, use the saved properties file for class defaults. 
     * Gets a connection to porter and adds a message listener to this.
     */
    public YC(boolean loadDefaultsFromFile) {
        super();
        loadCustomCommandsFromDatabase();
        ycDefaults = new YCDefaults(loadDefaultsFromFile);
        systemLogHelper = new SystemLogHelper(PointTypes.SYS_PID_SYSTEM);
        connection.addMessageListener(this);
    }
    
    /**
     * Execute the command, based on commandMode, selected object type, and YC properties.
     */
    public void executeCommand() {
        
        if (getCommandMode() == CGP_MODE) {
            
            porterRequest = new Request(0, getExecuteCmdsVector().get(0), currentUserMessageID);
            porterRequest.setPriority(getCommandPriority());
            getExecuteCmdsVector().remove(0);    //remove the sent command from the list!
            writeNewRequestToPorter(porterRequest);
            
        } else if (getTreeItem() != null) {
            // Must setup the request to send.
            // Stops the requests from continuing (a.k.a. kills the "loop" command).
            sendMore = 0;

            // Device item selected (including other models)
            if (getTreeItem() instanceof LiteYukonPAObject) {
                LiteYukonPAObject liteYukonPao = (LiteYukonPAObject) getTreeItem();
                setLiteYukonPao(liteYukonPao);
                handleDevice();
            } else if (getTreeItem() instanceof LiteDeviceMeterNumber) {
                // Meter number item in tree selected.
                LiteDeviceMeterNumber ldmn = (LiteDeviceMeterNumber) getTreeItem();
                LiteYukonPAObject liteYukonPao = cache.getAllPaosMap().get(ldmn.getLiteID());
                setLiteYukonPao(liteYukonPao);
                handleDevice();
            } else if (TreeModelEnum.isEditableSerial(getModelType())) {
                // Serial Number item in tree selected.
                handleSerialNumber();
            } else if (getModelType() == DeviceGroupTreeFactory.LiteBaseModel.class) {
                // TestCollectionGroup is selected.
                synchronized (YC.this) {
                    
                    DeviceGroup deviceGroup = (DeviceGroup) getTreeItem();
                    
                    Set<Integer> deviceIds = deviceGroupService.getDeviceIds(Collections.singleton(deviceGroup));
                    Vector<String> savedVector = new Vector<>();
                    savedVector.addAll(getExecuteCmdsVector());
                    Vector<String> finishedVector = null;
                    
                    Iterator<Integer> deviceIter = deviceIds.iterator();
                    while (deviceIter.hasNext()) {
                        int deviceId = deviceIter.next();
                        LiteYukonPAObject liteYukonPao = cache.getAllPaosMap().get(deviceId);
                        setLiteYukonPao(liteYukonPao);
                        handleDevice();
                        // Clone the vector because handleDevice() removed the command but in truth, it
                        // shouldn't be removed until all of the devices have been looped through.
                        finishedVector = executeCmdsVector;
                        executeCmdsVector = new Vector<>();
                        executeCmdsVector.addAll(savedVector);
                    }
                    executeCmdsVector = finishedVector;
                }
            } else {
                log.info(getModelType() + " - New type needs to be handled");
            }
            
        } else {
            // Are we coming from the servlet and have no treeObject? (only deviceID or serial number)
            // Send the command out on deviceID/serialNumber.
            if (liteYukonPao != null) {
                handleDevice();
            } else if (!serialNumber.equalsIgnoreCase(PAOGroups.STRING_INVALID)) {
                handleSerialNumber();
            }
        }
    }
    
    public LiteYukonPAObject[] getAllRoutes() {
        if (allRoutes == null) {
            int size = cache.getAllRoutes().size();
            allRoutes = cache.getAllRoutes().toArray(new LiteYukonPAObject[size]);
        }
        return allRoutes;
    }
    
    public String getCommandString() {
        return commandString;
    }
    
    /** Vector of String commands, parsed from commandString */
    public Vector<String> getExecuteCmdsVector() {
        if (executeCmdsVector == null) {
            executeCmdsVector = new Vector<String>(2);
        }
        return executeCmdsVector;
    }
    
    /**
     * Retturns the YCDefaults commandPriority (values 1-14)
     * @return int commandPriority 
     */
    public int getCommandPriority() {
        return getYCDefaults().getCommandPriority();
    }
    
    /**
     * Returns the loop command type
     * Valid loop types are:
     * NOLOOP=0                - loop not parsed
     * LOOP=1                - loop alone parsed
     * LOOPNUM=2            - loop for some num of times
     * LOOPLOCATE=3            - loop locate parsed
     * LOOPLOCATE_ROUTE=4    - loop locate route parsed
     * @return int loopType
     */
    public int getLoopType() {
        return loopType;
    }
    
    /**
     * Return the model type.
     * Refer to com.cannontech.database.model.* for valid models.
     * @return int modelType
     */
    public Class<? extends LiteBaseTreeModel> getModelType() {
        return modelType;
    }
    
    /**
     * Returns the "queue" command string if queuing is turned on.
     */
    public String getQueueCommandString() {
        if (getYCDefaults().getQueueExecuteCommand()) {
            return "";
        } else {
            return " noqueue";
        }
    }
    
    /**
     * Return the routeID
     * TODO - RouteID is only useful for loop commands or those applied to serialNumber.
     */
    public int getRouteID() {
        return routeID;
    }
    
    public String getSerialNumber() {
        return serialNumber;
    }
    
    /**
     * Return the treeItem. (the current selected tree object)
     */
    public Object getTreeItem() {
        return treeItem;
    }
    
    /**
     * Return the default properties.
     */
    public YCDefaults getYCDefaults() {
        if (ycDefaults == null) {
            ycDefaults = new YCDefaults();
        }
        return ycDefaults;
    }
    
    /**
     * Gets the device ID(opt1) of the object, or selected serial number(opt2) if deviceID is < 0.
     * Checks for the command string "loop" to exist.
     * Creates the message.Request to send to porter.
     * Saves the message.Request if "loop" was found in the string so the Request can be resubmitted.
     * Write's the Request to the porter connection.
     */
    public void handleDevice() {
        
        if (liteYukonPao == null) {
            // No device selected
            logCommand(" *** Warning: Please select a Device (or Serial Number) ***");
            return;
        }
    
        setLoopType(parseLoopCommand());

        Vector<String> commandVec = getExecuteCmdsVector();
        for (int i = 0; i < commandVec.size(); i++) {
            String command = getExecuteCmdsVector().get(i);
            if (liteYukonPao.getPaoType().isPlc() || liteYukonPao.getPaoType().isRepeater()) {
                if (command.indexOf("noqueue") == -1) {
                    // Replace the old command with this one.
                    getExecuteCmdsVector().setElementAt(command + getQueueCommandString(), i);
                }
            }
        }
    
        // Send the first command from the vector out.
        porterRequest = new Request(liteYukonPao.getLiteID(), getExecuteCmdsVector().get(0), currentUserMessageID);
        porterRequest.setPriority(getCommandPriority());
        
        // Remove the sent command from the list.
        getExecuteCmdsVector().remove(0);
        
        if (getLoopType() == LOOPLOCATE) {
            
            if (getAllRoutes() != null && getAllRoutes()[sendMore] instanceof LiteYukonPAObject) {
                
                LiteYukonPAObject route = getAllRoutes()[sendMore];
                
                while (route.getPaoType() == PaoType.ROUTE_MACRO && sendMore > 0) {
                    sendMore--;
                    route = getAllRoutes()[sendMore];
                }
    
                if (route.getPaoType() == PaoType.ROUTE_MACRO) {
                    return;
                }
    
                porterRequest.setRouteID(route.getYukonID());
            }
        } else if (getLoopType() == LOOPLOCATE_ROUTE) {
            porterRequest.setRouteID(getRouteID());
        }
        
        writeNewRequestToPorter(porterRequest);
    }
    
    /**
     * Looks for the string "serial" in the command.
     * 
     * If the string is not found, tack it on to the string 
     * along with the currentSelectedSerialNumber.
     * 
     * Create a message.Request to send to porter.
     * Set the route selected for the message.Request.
     * Write Request to the connection to porter.
     */
    public void handleSerialNumber() {
        
        for (int i = 0; i < getExecuteCmdsVector().size(); i++) {
            
            String command = getExecuteCmdsVector().get(i);
            int index = command.indexOf("serial");
            
            if (index < 0) {
                getExecuteCmdsVector().setElementAt(command + " serial " + serialNumber , i);
            } else {
                // Set serial as in command string
                StringTokenizer st = new StringTokenizer(command.substring(index+6));
                if (st.hasMoreTokens()) {
                    /** TODO only one serial number can be entered with multiple commands at this time. */
                    String serialNumToken = st.nextToken();
                    if (i > 0) {
                        // More than one command
                        if (!getSerialNumber().equalsIgnoreCase(serialNumToken)) {
                            logCommand(" ** Warning: Different serial numbers are being used in this multiple command." 
                                    + " The first serial number will be used for all commands!");
                        }
                    } else {
                        setSerialNumber(serialNumToken);
                    }
                }
            }
        }
        
        if (getSerialNumber() == null) {
            // No serial Number Selected
            logCommand(" *** Warning: Please select a Serial Number (or Device) ***");
            return;
        }
    
        setLoopType(parseLoopCommand());
        
        porterRequest = new Request(Device.SYSTEM_DEVICE_ID, getExecuteCmdsVector().get(0), currentUserMessageID);
        porterRequest.setPriority(getCommandPriority());
        
        // Remove the sent command from the list.
        getExecuteCmdsVector().remove(0);

        // Get routeID / set it in the request.
        if (getRouteID() >= 0) {
            porterRequest.setRouteID(getRouteID());
        } else {
            log.info("Route cannot be determined. " + getRouteID());
        }
        
        writeNewRequestToPorter(porterRequest);
    }
    
    /**
     * Returns the send command mode.
     * Valid values are:
     * DEFAULT_MODE = 0; Send commands with YC addt's (loop, queue, route..).
     * CGP_MODE = 1; Send commands as they are, no parsing done on them.
     */
    public int getCommandMode() {
        return commandMode;
    }
    
    /**
     * Notifies the observers of new logging data.
     */
    public void logCommand(String command) {
        setChanged();
        this.notifyObservers(command);
    }
    
    /**
     * Search for the string "loop" in the command.
     * If loop is found, search for any value in the command (following "loop").
     * Set sendMore to the number found after loop (which tells how many times loop wishes to perform).
     */
    public int parseLoopCommand() {
        
        for (int i = 0; i < getExecuteCmdsVector().size(); i++) {
            
            String tempCommand = getExecuteCmdsVector().get(i).toLowerCase();
            String valueSubstring = null;
                
            int loopIndex = tempCommand.indexOf("loop");
        
            if (loopIndex >= 0) {
                // A loop exists
                for (int j = tempCommand.indexOf("loop") + 4; j < tempCommand.length(); j++) {
                    // Skip whitespaces
                    if (tempCommand.charAt(j) != ' ' && tempCommand.charAt(j) != '\t') {
                        valueSubstring = tempCommand.substring(j);
                        break;
                    }
                }
                
                if (valueSubstring != null) {
                    if (valueSubstring.startsWith("locater")) {
                        // Parse out locateroute
                        synchronized (YC.class) {
                            getExecuteCmdsVector().setElementAt("loop", i);
                        }
                        
                        return LOOPLOCATE_ROUTE;
                        
                    } else if (valueSubstring.startsWith("loc")) {
                        // Parse out locate
                        synchronized (YC.class) {
                            getExecuteCmdsVector().setElementAt("loop", i);
                            // Loop through each route
                            sendMore = getAllRoutes().length - 1;
                        }
                        
                        return LOOPLOCATE;
                        
                    } else {
                        
                        for (int j = 0; j < valueSubstring.length(); j++) {
                            
                            if (valueSubstring.charAt(j) == ' ' || valueSubstring.charAt(j) == '\t') {
                                // Skip whitespaces
                                valueSubstring = valueSubstring.substring(0, j);
                                break;
                            }
                        }
                        
                        int value;
                        try {
                            // Assume it is an integer.
                            value = Integer.parseInt(valueSubstring);
                        } catch(NumberFormatException nfe) {
                            value = 1;
                        }
                        
                        synchronized (YC.class) {
                            getExecuteCmdsVector().setElementAt("loop", i);
                            // Subtract one because 0 is the last occurance, not 1.  (Ex. 0-4 not 1-5)
                            sendMore = value - 1;
                        }
                        
                        return LOOPNUM;
                        
                    }
                }
                
                return LOOP;
            }
        }
        
        return NOLOOP;
    }
    
    public void setAllRoutes(LiteYukonPAObject[] allRoutes) {
        if (allRoutes == null) {
            this.allRoutes = null;
        } else {
            this.allRoutes = new LiteYukonPAObject[allRoutes.length];
            for (int i = 0; i < allRoutes.length; i++) {
                this.allRoutes[i] = allRoutes[i];
            }
        }
    }
    
    /**
     * Set the commmand string.
     * @throws PaoAuthorizationException 
     */
    public void setCommandString(String command) throws PaoAuthorizationException {
        commandString = command;
        setCommands(commandString);
        
        checkCommandAuthorization();
    }

    /**
     * Set the command string without checking command authorization.
     */
    public void setCommandStringWithoutPaoAuth(String command) {
        setCommands(command);
    }
    
    /**
     * Takes a command string, then parses the string for multiple commands
     * separated by the '&' character.
     */
    private void setCommands(String command) {
        // Remove everything from the vector, otherwise we could get in a big loop...
        getExecuteCmdsVector().removeAllElements();
        
        final char SEPARATOR = '&';
        String tempCommand = command;

        int begIndex = 0;
        int firstQuote = tempCommand.indexOf("'");
        int secondQuote = tempCommand.indexOf("'", firstQuote+1);
        int sepIndex = tempCommand.indexOf(SEPARATOR);
        if (sepIndex > firstQuote && sepIndex < secondQuote) {
            sepIndex = tempCommand.indexOf(SEPARATOR, secondQuote);
        }

        while(sepIndex > -1) {
            String begString = tempCommand.substring(0, sepIndex).trim();
            begIndex = sepIndex+1;
            String cmd = getCommandFromLabel(begString) + " update"; 
            getExecuteCmdsVector().add(cmd);
            tempCommand = tempCommand.substring(begIndex).trim();
            sepIndex = tempCommand.indexOf(SEPARATOR);
        }
        // Add the final (or only) command.
        getExecuteCmdsVector().add(getCommandFromLabel(tempCommand) + " update");
    }
    
    public void checkCommandAuthorization() throws PaoAuthorizationException{
        // Check authorization for each command. 
        for (String commandObj : getExecuteCmdsVector()) {
            if (!this.isAllowCommand(commandObj)) {
                throw new PaoAuthorizationException("Unauthorized command", commandObj);
            }
        }
    }
    
    public boolean isAllowCommand(String command) {
        return this.isAllowCommand(command, user);
    }
    
    public boolean isAllowCommand(String command, LiteYukonUser user) {
        
        if (liteYukonPao != null) {
            return this.isAllowCommand(command, user, liteYukonPao);
        } else if (getModelType() == DeviceGroupTreeFactory.LiteBaseModel.class) {
            return paoCommandAuthService.isAuthorized(user, command) || getCommandMode() == YC.CGP_MODE;
        } else if (!PAOGroups.STRING_INVALID.equalsIgnoreCase(serialNumber)) {
            return this.isAllowCommand(command, user, "lmdevice");
        }
        
        return false;
    }
        
    public boolean isAllowCommand(String command, LiteYukonUser user, Object object) {
        
        if (object instanceof LiteYukonPAObject) {
            boolean authorized = paoCommandAuthService.isAuthorized(user, command, (LiteYukonPAObject) object)
                    || getCommandMode() == YC.CGP_MODE;
            return authorized;
            
        } else if (object instanceof String) {
            
            boolean authorized = lmCommandAuthService.isAuthorized(user, command, (String) object)
                    || getCommandMode() == YC.CGP_MODE;
            return authorized;
        }
        
        return false;
    }
    
    public void setDeviceType(LiteBase liteBase) {
        
        if (liteBase instanceof LiteYukonPAObject) {
            // TreeModelEnum.DEVICE,MCTBROADCAST,LMGROUPS,CAPBANKCONTROLLER
            setDeviceType(((LiteYukonPAObject)liteBase).getPaoType().getDbString());  
        } else if (liteBase instanceof LiteDeviceMeterNumber) {
            // TreeModelEnum.DEVICE_METERNUMBER
            setDeviceType(((LiteDeviceMeterNumber)liteBase).getPaoType().getDbString());
        }  else {
            //*TODO - This is a really bad catch all...revise!*/
            log.error("Device Type undefined. Item instance of " + (liteBase == null ? null : liteBase.getClass()));
            setDeviceType("");
        }
    }
    
    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
        log.debug(" DEVICE TYPE for command lookup: " + this.deviceType);
        setLiteDeviceTypeCommands(commandDao.getAllDevTypeCommands(this.deviceType));
    }
    
    /**
     * Set the commandMode.
     * Valid values are:
     * DEFAULT_MODE = 0; Send commands with YC addt's (loop, queue, route..).
     * CGP_MODE = 1; Send commands as they are, no parsing done on them.
     */
    public void setCommandMode(int commandMode) {
        this.commandMode = commandMode;
    }
    
    /**
     * Set the loopType.
     * Valid loop types are:
     * NOLOOP=0 - Loop not parsed
     * LOOP=1 - Loop alone parsed
     * LOOPNUM=2 - Loop for some num of times
     * LOOPLOCATE=3 - Loop locate parsed
     * LOOPLOCATE_ROUTE=4 - Loop locate route parsed
     */
    public void setLoopType(int loopType){
        this.loopType = loopType;
    }
    
    /**
     * Set the modelType.
     * Refer to com.cannontech.database.model.* for valid models. 
     */
    public void setModelType(Class<? extends LiteBaseTreeModel> modelType) {
        this.modelType = modelType;
    }
    
    public void setRouteID(int routeID) {
        this.routeID = routeID;
    }
    
    /**
     * Set the serialNumber
     * The serialNumber for the LCR commands
     */
    public void setSerialNumber(String serialNumber) {
        if (serialNumber == null) {
            this.serialNumber = null;
        } else {
            this.serialNumber = serialNumber.trim();
        }
    }
    
    /**
     * Set the selected treeItem object
     */
    public void setTreeItem(Object treeItem) {
        this.treeItem = treeItem;

        if (this.treeItem instanceof LiteYukonPAObject) {
            // Set device id for tree item
            LiteYukonPAObject liteYukonPao = (LiteYukonPAObject) getTreeItem();
            setLiteYukonPao(liteYukonPao);
        } else if (this.treeItem instanceof LiteDeviceMeterNumber) {
            // Meter number item in tree selected.
            LiteDeviceMeterNumber ldmn = (LiteDeviceMeterNumber) getTreeItem();
            LiteYukonPAObject liteYukonPao = cache.getAllPaosMap().get(ldmn.getLiteID());
            setLiteYukonPao(liteYukonPao);
        } else {
            setLiteYukonPao(null);
        }
    }
    
    /**
     * Set the YCDefualts. The default properties for YC setup.
     */
    public void setYCDefaults(YCDefaults ycDefaults) {
        this.ycDefaults = ycDefaults;
    }
    
    /**
     * Reset the sendMore counter to -1.
     * Stops multiple command processing done by YC.
     */
    public void stop() {
        generateMessageID();
        sendMore = -1;
    }
    
    /**
     * Assumes command may be a "user-friendly" string instead of a porter accepted command string.
     * Attempt to substitute a porter accepted command for a "user-friendly" command:
     * Ex.  User type "Read My Meter" instead of "getvalue kwh"
     */
    public String getCommandFromLabel(String command) {
        
        if (getCommandMode() == DEFAULT_MODE) {
            // Need to do all checks and setup for DEFAULT_MODE
            if (getLiteDeviceTypeCommands() != null) {
                
                String friendlyCommand = command.trim();
                // Try to match the entered command string alias to a label in the database, return the actual command.
                // OR
                // Try to match the entered command string alias to the actual command in the database, return the actual command.
                for (int i = 0; i < getLiteDeviceTypeCommands().size(); i++) {
                    LiteDeviceTypeCommand ldtc = getLiteDeviceTypeCommands().get(i);
                    if (ldtc.isVisible()) {
                        LiteCommand liteCommand = commandDao.getCommand(ldtc.getCommandId());
                        if (liteCommand.getLabel().trim().equalsIgnoreCase(friendlyCommand) ||
                            liteCommand.getCommand().trim().equalsIgnoreCase(friendlyCommand)) {
                            return liteCommand.getCommand();
                        }
                    }
                }
            }
        }
        
        // Default, return whatever they typed in on the command line, didn't match with anything.
        return command; 
    }
    
    /**
     * Write Request message to porter.
     */
    public void writeNewRequestToPorter(Request request) {
        
        SimpleDateFormat format = new SimpleDateFormat("MMM d HH:mm:ss a z");
        long timer = (System.currentTimeMillis());

        String message = "";
        if (request.getDeviceID() > 0) {
            message = " Device \'" + cache.getAllPaosMap().get(request.getDeviceID()).getPaoName() + "\'";
        } else {
            message = " Serial # \'" + serialNumber + "\'";
        }

        if (connection.isValid()) {
            
            logCommand("[" + format.format(new java.util.Date(timer)) 
                        + "] - {"+ currentUserMessageID + "} Command Sent to" + message 
                        + " -  \'" + request.getCommandString() + "\'");
            startStopWatch(getTimeOut());
            addRequestMessage(currentUserMessageID);
            generateMessageID();
            connection.write(request);
            logSystemEvent(request.getCommandString(), request.getDeviceID());
            
        } else {
            
            ClientConnection clientConnection = (ClientConnection)connection;
            String porterError = "Command request not sent - Connection to Yukon Port Control, " 
                    + clientConnection.getConnectionUri().getRawAuthority() + ", is not valid.";
            String logOutput= "<BR>["+ displayFormat.format(new java.util.Date()) + "] - " + porterError;
            
            writeOutputMessage(OutputMessage.DEBUG_MESSAGE, logOutput, MessageType.ERROR);
            setErrorMsg(porterError);
            
            log.info("REQUEST NOT SENT: CONNECTION TO PORTER IS NOT VALID");
        }
        
    }
    
    public Request getPorterRequest() {
        return porterRequest;
    }
    
    /**
     * A unique count id of Request messages sent to Porter.
     */
    public long getCurrentUserMessageID() {
        return currentUserMessageID;
    }
    
    /**
     * Replaces the run() method and allows us to remove the Runnable interface 
     * by using the MessageListener available to us. Verifies the messages are 
     * of Return type and then uses these messages to create readable display/debug messages, OutputMessage.
     */
    @Override
    public void messageReceived(MessageEvent e) {
        
        Message in = e.getMessage();
        
        if (in instanceof Return) {
            
            Return returnMsg = (Return) in;
            
            synchronized(this) {
                
                log.debug("Message Received [ID:"+ returnMsg.getUserMessageID() + 
                          " DevID:" + returnMsg.getDeviceID() + 
                          " Command:" + returnMsg.getCommandString() +
                          " Result:" + returnMsg.getResultString() + 
                          " Status:" + returnMsg.getStatus() +
                          " More:" + returnMsg.getExpectMore()+"]");
                
                if (!getRequestMessageIDs().contains(returnMsg.getUserMessageID())) {
                    
                    log.debug("Unknown Message: "+ returnMsg.getUserMessageID() 
                            + " Command [" + returnMsg.getCommandString()+"]");
                    log.debug("Unknown Message: "+ returnMsg.getUserMessageID() 
                            + " Result [" + returnMsg.getResultString()+"]");
                    return;
                    
                } else {
                    // Remove the messageID from the set of Executing ids.
                    if (sendMore == 0 && returnMsg.getExpectMore() == 0) {
                        // Nothing more is coming, remove from list.
                        // Do not remove these from the "master" requestMessageIDs anymore, Per Corey 20060501
                        getRequestMessageIDs_Executing().remove(returnMsg.getUserMessageID());
                    }
                }
                
                log.debug("Total Messages: " + getRequestMessageIDs().size() 
                        + " | Commands Executing: " + getRequestMessageIDs_Executing().size());
                String debugOutput = "";
                String displayOutput = "";

                if (prevUserID != returnMsg.getUserMessageID()) {
                    // When new (one that is different from the previous) userMessageID occurs, 
                    // print datetime, command, etc info. 
                    String command = StringEscapeUtils.escapeHtml4(returnMsg.getCommandString());
                    debugOutput = "<BR>[" + displayFormat.format(returnMsg.getTimeStamp()) 
                            + "] - {" + returnMsg.getUserMessageID() 
                            + "} {Device: " +  cache.getAllPaosMap().get(returnMsg.getDeviceID()).getPaoName() 
                            + "} Return from '" + command + "'";
                    writeOutputMessage(OutputMessage.DEBUG_MESSAGE, debugOutput, MessageType.INFO);
                    debugOutput = "";
                    prevUserID = returnMsg.getUserMessageID();
                    
                }
                
                // Add all PointData.getStr() objects to the output.
                for (Object o : returnMsg.getMessages()) {
                    
                    if (o instanceof PointData) {
                        
                        PointData pd = (PointData) o;
                        if (pd.getStr().length() > 0) {
                            int tabCount = (60 - displayOutput.length()) / 24;
                            for (int x = 0; x <= tabCount; x++) {
                                displayOutput += "\t";
                            }
                            debugOutput += pd.getStr() + "<BR>";
                        }
                    }
                }
                
                MessageType messageType = MessageType.getMessageType(returnMsg.getStatus());
                
                if (returnMsg.getExpectMore() == 0) {
                    String routeName = null;
                    if (returnMsg.getRouteOffset() > 0) {
                        routeName = cache.getAllPaosMap().get(returnMsg.getRouteOffset()).getPaoName();
                    }
                    
                    if (routeName == null) {
                        routeName = cache.getAllPaosMap().get(returnMsg.getDeviceID()).getPaoName();
                    }

                    displayOutput = "Route:   " + routeName;
                    int tabCount = (60 - displayOutput.length()) / 24;
                    for (int i = 0; i <= tabCount; i++) {
                        displayOutput += "\t";
                    }
                    
                    if (getLoopType() != YC.NOLOOP) {
                        if (returnMsg.getStatus() != 0) {
                            if (returnMsg.getExpectMore() == 0) {
                                displayOutput += "Error  " + returnMsg.getStatus() 
                                        + "\t(" + returnMsg.getResultString()+ ")";
                            }
                        } else {
                            // Status == 0 == successfull
                            if (returnMsg.getExpectMore() == 0) {
                                displayOutput += "Valid";
                            }
                        }
                        writeOutputMessage(OutputMessage.DISPLAY_MESSAGE, displayOutput, messageType);
                    }
                }
                
                if (returnMsg.getResultString().length() > 0) {
                    debugOutput += returnMsg.getResultString();
                }

                if (returnMsg.getStatus() > 1) {
                    
                    DeviceErrorDescription deviceErrorDesc = 
                            deviceErrorTranslatorDao.translateErrorCode(returnMsg.getStatus());
                    writeOutputMessage(OutputMessage.DEBUG_MESSAGE, 
                            "<B>" + deviceErrorDesc.getCategory() + "</B> -- " 
                            + deviceErrorDesc.getDescription(), messageType);
                    
                }
                
                // 0=success; 1="Not Normal" YUK-10411/TSSL-1230 changed 1 to an "error",
                // but don't have a good error-code.xml entry for this so still excluding it.
                writeOutputMessage(OutputMessage.DEBUG_MESSAGE, debugOutput, messageType);

                synchronized (YukonCommander.class) {
                    // Only send next message when ret expects nothing more
                    if (returnMsg.getExpectMore() == 0) {
                        
                        //Break out of this outer loop.
                        doneSendMore:
                        if (sendMore == 0) {
                            // Command finished, see if there are more commands to send.
                            if (!getExecuteCmdsVector().isEmpty()) {
                                executeCommand();
                            }
                        } else if (sendMore > 0) {
                            // Decrement the number of messages to send
                            sendMore--;
                            
                            if (getLoopType() == YC.LOOPLOCATE) {
                                if (getAllRoutes()[sendMore] instanceof LiteYukonPAObject) {
                                    LiteYukonPAObject route = getAllRoutes()[sendMore];
                                    while(route.getPaoType() == PaoType.ROUTE_MACRO && sendMore > 0) {
                                        sendMore--;
                                        route = getAllRoutes()[sendMore];
                                    }
                                    // Have to check again because last one may be route_ macro.
                                    if (route.getPaoType() == PaoType.ROUTE_MACRO) {
                                        break doneSendMore;
                                    }
                                    
                                    getPorterRequest().setRouteID(route.getYukonID());
                                }
                            }
                            
                            startStopWatch(getTimeOut());
                            // Do the saved loop request
                            connection.write(getPorterRequest());
                            
                        } else {
                            debugOutput = "Command cancelled<BR>";
                            writeOutputMessage(OutputMessage.DEBUG_MESSAGE, debugOutput, MessageType.INFO);
                        }
                    }
                }
            }
        }
    }

    public void writeOutputMessage(int displayAreaType, String outputStr, MessageType messageType) {
        OutputMessage message = new OutputMessage(displayAreaType, outputStr, messageType);
        setChanged();
        this.notifyObservers(message);
        appendResultText(message);    
    }
    
    /**
     * Returns result string from Return porter messages.
     */
    public String getResultText() {
        return resultText;
    }
    
    public void setResultText(String string) {
        resultText = string;
    }
    
    public void appendResultText(OutputMessage message) {
        
        Color color = message.getMessageType().getColor();
        resultText = getResultText() + "<BR>" +
                    (color == null ? "" : "<span style='color:" + ColorUtil.getHTMLColor(color) + ";'>") 
                    + message.getText() + (color == null ? "" : "</span>");
    }
    
    public void clearResultText() {
        setResultText("");
    }
    
    /**
     * Write resultText to out.
     * @throws java.io.IOException
     */
    public void encodeResults(OutputStream out) throws IOException {
        StringBuffer buf = new StringBuffer("<table><tr><td>" + getResultText() + "</td></tr></table>");
        out.write(buf.toString().getBytes());
    }
    
    /**
     * Generate a unique mesageid, don't let it be negative.
     */
    private synchronized long generateMessageID() {
        if (++currentUserMessageID == Integer.MAX_VALUE) {
            currentUserMessageID = 1;
        }
        return currentUserMessageID;
    }
    
    public List<LiteDeviceTypeCommand> getLiteDeviceTypeCommands() {
        return liteDeviceTypeCommands;
    }
    
    public void setLiteDeviceTypeCommands(List<LiteDeviceTypeCommand> commands) {
        liteDeviceTypeCommands = commands;
        Collections.sort(liteDeviceTypeCommands, LiteComparators.liteDeviceTypeCommandComparator);
    }
    
    /**
     * Returns the currently selected object from the tree model's device type string.
     */
    public String getDeviceType() {
        return deviceType;
    }
    
    protected void addRequestMessage(long userMessageId) {
        getRequestMessageIDs().add(userMessageId);
        getRequestMessageIDs_Executing().add(userMessageId);
    }
    
    public void clearRequestMessage() {
        getRequestMessageIDs().clear();
        getRequestMessageIDs_Executing().clear();
    }
    
    /**
     * A set of request messageIDs that have been sent.
     * This set is NOT effected by the expectMore flag returning.
     */
    private Set<Long> getRequestMessageIDs() {
        return requestMessageIDs;
    }
    
    /**
     * A set of request messageIDS that are currently executing. 
     * This set IS effected by the expectMore flag returning
     */
    public Set<Long> getRequestMessageIDs_Executing() {
        return requestMessageIDs_executing;
    }

    public void startStopWatch(int timeOutInMillis) {
        if (stopWatch == null) {
            stopWatch = new Timer(timeOutInMillis, timerPerfomer);
        }
        stopWatch.setInitialDelay(timeOutInMillis);
        stopWatch.setRepeats(false);
        stopWatch.start();
    }
    
    /**
     * Returns true if the timer is running, otherwise return false (timer has stopped).
     */
    public boolean isWatchRunning() {
        if (stopWatch != null && stopWatch.isRunning()) {
            return true;
        }
        return false;
    }
    
    public int getTimeOut() {
        return timeOut;
    }
    
    public void setTimeOut(int timeOut) {
        this.timeOut = timeOut;
    }
    
    public ActionListener getTimerPerfomer() {
        if (timerPerfomer == null) {
            timerPerfomer = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    setTimeOut(0);
                    ((Timer) e.getSource()).stop();
                }
            };
        }
        return timerPerfomer;
    }
    
    /**
     * Load custom command files to be parsed for custom commands.
     */
    private void loadCustomCommandsFromDatabase() {
        
        File f = new File(commandsDir + "custom/");
        
        if (f.exists()) {
            
            String[] fileNames = f.list();
            
            {
                for (String fileName : fileNames) {
                    
                    log.info("Parsing custom file: " + fileName);
                    
                    int extIndex= fileName.lastIndexOf('.');
                    if (extIndex > 0) {
                        
                        fileName = fileName.substring(0, extIndex);
                        String category = null;
                        
                        if (PaoType.getPaoTypeId(fileName) != PaoType.INVALID) {
                            category = fileName;
                        } else if (fileName.equalsIgnoreCase("alpha-base")) {
                            category = CommandCategory.ALPHA_BASE.getDbString();
                        } else if (fileName.equalsIgnoreCase("cbc-base")) {
                            category = CommandCategory.CBC_BASE.getDbString();
                        } else if (fileName.equalsIgnoreCase("ccu-base")) {
                            category = CommandCategory.CCU_BASE.getDbString();
                        } else if (fileName.equalsIgnoreCase("iedbase")) {
                            category = CommandCategory.IED_BASE.getDbString();
                        } else if (fileName.equalsIgnoreCase("ion-base")) {
                            category = CommandCategory.ION_BASE.getDbString();
                        } else if (fileName.equalsIgnoreCase("lcu-base")) {
                            category = CommandCategory.LCU_BASE.getDbString();
                        } else if (fileName.equalsIgnoreCase("lsbase")) {
                            category = CommandCategory.LP_BASE.getDbString();
                        } else if (fileName.equalsIgnoreCase("loadgroup-base")) {
                            category = CommandCategory.LOAD_GROUP_BASE.getDbString();
                        } else if (fileName.equalsIgnoreCase("mct-base")) {
                            category = CommandCategory.MCT_BASE.getDbString();
                        } else if (fileName.equalsIgnoreCase("rtu-base")) {
                            category = CommandCategory.RTU_BASE.getDbString();
                        } else if (fileName.equalsIgnoreCase("repeater-base")) {
                            category = CommandCategory.REPEATER_BASE.getDbString();
                        } else if (fileName.equalsIgnoreCase("tcu-base")) {
                            category = CommandCategory.TCU_BASE.getDbString();
                        } else if (fileName.equalsIgnoreCase("lcrserial")) {
                            category = CommandCategory.SERIALNUMBER.getDbString();
                        } else {
                            log.info("Unknown filename: " + fileName);
                        }
                        
                        if (category != null) {
                            parseCommandFile(f.getAbsolutePath() + "/" + fileName, category);
                        }
                        
                        writeProcessedFile(fileName);
                    }
                }
            }
            //Force a reload of all commands and deviceTypeCommands!
            cache.releaseAllCommands();
            cache.releaseAllDeviceTypeCommands();
        }
    }
    
    /**
     * Parse command file for custom commands.
     */
    private void parseCommandFile(String dirFileName, String category) {
        
        KeysAndValuesFile kavFile = new KeysAndValuesFile(dirFileName);
        KeysAndValues keysAndValues = kavFile.getKeysAndValues();
        
        if (keysAndValues != null) {
            
            String labels[] = keysAndValues.getKeys();
            String commands[] = keysAndValues.getValues();

            Collection<LiteCommand> allCommands = cache.getAllCommands().values();

            for (int i = 0; i < labels.length; i++) {
                
                Command command = new Command();
                command.setLabel(labels[i].trim());
                command.setCommand(commands[i].trim());
                command.setCategory(category);
                boolean exists = false;
                
                for (LiteCommand liteCommand : allCommands) {
                    if ((liteCommand.getCommand().equalsIgnoreCase(command.getCommand()) 
                            && liteCommand.getLabel().equalsIgnoreCase(command.getLabel()))) {
                        exists = true;
                        break;
                    }
                }
                
                if (!exists) {
                    insertDBPersistent(command, Transaction.INSERT);
                    //Lazy man's cache reload! (...we don't have a dispatch connection)
                    cache.releaseAllCommands();
                    addDeviceTypeCommand(category, command);
                    log.info("Adding: " + command.toString() + " ID:" + command.getCommandID().toString());
                }
            }
        }
    }
    
    /**
     * Insert item into database.  No DBChange is sent out.
     */
    public void insertDBPersistent(DBPersistent item, int transType) {
        
        if (item != null) {
            try {
                Transaction t = Transaction.createTransaction(transType, item);
                item = t.execute();
            } catch(TransactionException | NullPointerException e) {
                log.error(e.getMessage(), e);
            }
        }
    }
    
    /**
     * Inserts a deviceTypeCommand into the database for the command and deviceType parameters.
     * If the deviceType is a category, then an entry for every deviceType in that category is inserted.
     */
    private void addDeviceTypeCommand(String deviceType, Command command) {
        
        int nextId = com.cannontech.database.db.command.DeviceTypeCommand.getNextID(CtiUtilities.getDatabaseAlias());
        
        if (CommandCategoryUtil.isCommandCategory(deviceType)) {
            // The deviceType is actually a category, not a deviceType from YukonPaobject.paoType column
            CommandCategory category = CommandCategory.getForDbString(deviceType);
            List<PaoType> paoTypes = CommandCategoryUtil.getAllTypesForCategory(category);
            DeviceTypeCommand deviceTypeCommand = null;
            for (PaoType paoType : paoTypes) {
                //Add to DeviceTypeCommand table, entries for all deviceTypes! yikes...I know
                deviceTypeCommand = new DeviceTypeCommand();
                deviceTypeCommand.getDeviceTypeCommand().setDeviceCommandID(nextId);
                deviceTypeCommand.getDeviceTypeCommand().setDeviceType(paoType.getDbString());
                
                // Hey, default it, we're going to update it in a bit anyway right?
                deviceTypeCommand.getDeviceTypeCommand().setDisplayOrder(new Integer(20));
                
                deviceTypeCommand.getDeviceTypeCommand().setVisibleFlag(new Character('Y'));
                deviceTypeCommand.setCommand(command);
                
                insertDBPersistent(deviceTypeCommand, Transaction.INSERT);
            }
            
        } else {
            //Add to DeviceTypeCommand table, entries for all deviceTypes! yikes...I know
            DeviceTypeCommand deviceTypeCommand = new DeviceTypeCommand();
            deviceTypeCommand.getDeviceTypeCommand().setDeviceCommandID(nextId);
            deviceTypeCommand.getDeviceTypeCommand().setDeviceType(deviceType);
            
            // Hey, default it, we're going to update it in a bit anyway right?
            deviceTypeCommand.getDeviceTypeCommand().setDisplayOrder(new Integer(20));
            
            deviceTypeCommand.getDeviceTypeCommand().setVisibleFlag(new Character('Y'));
            deviceTypeCommand.setCommand(command);
            
            insertDBPersistent(deviceTypeCommand, Transaction.INSERT);
        }
    }
    
    /**
     * Move the procFileName from /command/custom/ to /command/processed/.
     */
    public void writeProcessedFile(String procFileName) {
        
        File  file = new File(commandsDir + "custom/" + procFileName);
        File destFile = new File(commandsDir + "processed/");
        destFile.mkdirs();
        destFile = new File(commandsDir + "processed/" + procFileName);
        boolean success = file.renameTo(destFile);
        
        if (!success) {
            log.info("Could not rename file " + procFileName + ".  This file will be deleted.");
            file.delete();
        }
    }



    
    
    private void logSystemEvent(String command, int deviceId) {
        
        String commandStr = command.toLowerCase();
        String logDescr = "";
        
        if (commandStr.startsWith("control") 
                || commandStr.startsWith("putconfig") 
                || commandStr.startsWith("putstatus") 
                || commandStr.startsWith("putvalue")) {
            
            int pointId = PointTypes.SYS_PID_SYSTEM;
            LiteYukonPAObject liteYukonPAObject = cache.getAllPaosMap().get(deviceId);
            logDescr = liteYukonPAObject.getPaoType().getPaoClass() + ": " 
                    + liteYukonPAObject.getPaoName() + " (ID:" + liteYukonPAObject.getLiteID() + ")";
                        
            if (liteYukonPAObject.getPaoType().isCbc()) {
                // The Bank Status Point
                pointId = getLogPointID(PointType.Status, 1);
                
            } else if (liteYukonPAObject.getPaoType().isLoadGroup()) {
                // The Control Status (Pseudo) Point
                pointId = getLogPointID(PointType.Status, 0);
                
            } else if (liteYukonPAObject.getPaoType().isMct()) {
                if (commandStr.indexOf(" connect") > -1 || commandStr.indexOf(" disconnect") > -1) {
                    // The Disconnect Status Point
                    pointId = getLogPointID(PointType.Status, 1);
                    
                }
            } else if (liteYukonPAObject.getLiteID() == Device.SYSTEM_DEVICE_ID) {
                logDescr = "Serial: " + getSerialNumber();
            }
            
            systemLogHelper.log(pointId, "Manual: " + command, logDescr, logUserName);
        }
    }
    
    private int getLogPointID(PointType pointType, int pointOffset) {
        
        List<LitePoint> points = pointDao.getLitePointsByPaObjectId(liteYukonPao.getLiteID());
        for (LitePoint point : points) {
            if (point.getPointTypeEnum() == pointType && point.getPointOffset() == pointOffset) {
                return point.getPointID();
            }
        }
        
        return PointTypes.SYS_PID_SYSTEM;
    }
    
    public String getLogUserName() {
        return logUserName;
    }
    
    public void setLiteUser(LiteYukonUser user) {
        this.user  = user;
        logUserName = user.getUsername();
    }
    
    public String getErrorMsg() {
        return errorMsg;
    }
    
    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
    
    public void clearErrorMsg() {
        setErrorMsg("");
    }

    public void setLiteYukonPao(LiteYukonPAObject liteYukonPao) {
        this.liteYukonPao = liteYukonPao;
    }

    public LiteYukonPAObject getLiteYukonPao() {
        return liteYukonPao;
    }
    
}