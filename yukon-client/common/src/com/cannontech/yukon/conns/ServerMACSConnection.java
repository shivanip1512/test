package com.cannontech.yukon.conns;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.messaging.message.BaseMessage;
import com.cannontech.messaging.message.ConnStateChangeMessage;
import com.cannontech.messaging.message.macs.CategoryChangeEvent;
import com.cannontech.messaging.message.macs.DeleteScheduleMessage;
import com.cannontech.messaging.message.macs.InfoMessage;
import com.cannontech.messaging.message.macs.OverrideRequestMessage;
import com.cannontech.messaging.message.macs.RetrieveScheduleMessage;
import com.cannontech.messaging.message.macs.ScheduleMessage;
import com.cannontech.messaging.message.macs.ScriptFileMessage;
import com.cannontech.messaging.util.ClientConnection;
import com.cannontech.yukon.IMACSConnection;

public class ServerMACSConnection extends ClientConnection implements IMACSConnection
{
    private java.util.Vector schedules = null;

    // This hastTable should contain String keys and ArrayList values
    private java.util.Hashtable categoryNames = null;

    public ServerMACSConnection() {
        super("MACS");
    }

    private void addCategory(ScheduleMessage sched) {
        if (sched == null)
            return;

        Object o = getCategoryNames().get(sched.getCategoryName());

        if (o != null) {
            // we found this category name to exist, lets just append the new Schedule to the
            // arrayList associated with the categoryName if it does not exist, else
            // we set the preexisting schedule to the new schedule
            try {
                java.util.ArrayList list = (java.util.ArrayList) o;
                boolean add = false;
                int i = 0;
                for (i = 0; i < list.size(); i++) {
                    if (((ScheduleMessage) list.get(i)).equals(sched)) {
                        add = false;
                        break; // found the schedule, lets get out of here
                    }
                    else {
                        add = true;
                        continue; // havent found the schedule, lets keep searching
                    }
                }

                if (add) // the schedule was not found
                    list.add(sched);
                else
                    // the schedule is already present, update it
                    list.set(i, sched);

            }
            catch (ClassCastException e) {
                throw new ClassCastException("CategoryName hashTable contained values that are not of type ArrayList");
            }

        }
        else {
            java.util.ArrayList list = new java.util.ArrayList(5);
            list.add(sched);

            // store the list
            getCategoryNames().put(sched.getCategoryName(), list);
        }

        // always tell our listeners we may have added a new category
        setChanged();
        notifyObservers(new CategoryChangeEvent(this, CategoryChangeEvent.INSERT, sched.getCategoryName()));

    }

    /**
     * This method was created in VisualAge.
     * @return com.cannontech.macs.Schedule[]
     */
    public ScheduleMessage[] getCategories(String category) {
        java.util.ArrayList list = (java.util.ArrayList) getCategoryNames().get(category);
        ScheduleMessage[] sched = new ScheduleMessage[list.size()];
        list.toArray(sched);

        return sched;
    }

    /**
     * Insert the method's description here. Creation date: (2/23/2001 10:03:31 AM)
     * @return java.util.Hashtable
     */
    public java.util.Hashtable getCategoryNames() {
        if (categoryNames == null)
            categoryNames = new java.util.Hashtable(40, (float) 0.6);

        return categoryNames;
    }

    /**
     * Insert the method's description here. Creation date: (2/21/2001 6:17:59 PM)
     * @return java.util.Vector
     */
    private java.util.Vector getSchedules() {
        if (schedules == null)
            schedules = new java.util.Vector(10);

        return schedules;
    }

    /**
     * Insert the method's description here. Creation date: (2/23/2001 10:37:45 AM)
     * @param multi com.cannontech.message.dispatch.message.Multi
     */
    private void handleDeleteSchedule(com.cannontech.messaging.message.macs.DeleteScheduleMessage deleted) {
        for (int j = 0; j < getSchedules().size(); j++) {
            if (((ScheduleMessage) getSchedules().get(j)).getId() == deleted.getScheduleId()) {
                removeCategory((ScheduleMessage) getSchedules().get(j));
                getSchedules().remove(j);
                break;
            }
        }
    }

    /**
     * Insert the method's description here. Creation date: (2/21/2001 6:03:46 PM)
     * @param sched com.cannontech.macs.Info
     */
    private void handleInfo(InfoMessage info) {
        com.cannontech.clientutils.CTILogger.info("Received an Info msg : " + info.getInfo());
    }

    /**
     * Insert the method's description here. Creation date: (2/21/2001 6:03:46 PM)
     * @param sched com.cannontech.macs.Schedule
     */
    private void handleSchedule(ScheduleMessage sched) {
        com.cannontech.clientutils.CTILogger.debug("Received a schedule named " + sched.getScheduleName() + "/" +
                                                   sched.getCategoryName());

        boolean found = false;
        for (int j = 0; j < getSchedules().size(); j++) {
            if (((ScheduleMessage) getSchedules().get(j)).equals(sched)) {
                // remove the previous category entry for the old version of the schedule
                // if the category name has changed
                if (!((ScheduleMessage) getSchedules().get(j)).getCategoryName().equals(sched.getCategoryName()))
                    removeCategory((ScheduleMessage) getSchedules().get(j));

                // the schedule already exists, so just assign it to the new value
                getSchedules().set(j, sched);
                found = true;
                break;
            }
        }

        addCategory(sched);

        if (!found)
            getSchedules().add(sched);
    }

    /**
     * Insert the method's description here. Creation date: (2/21/2001 6:03:46 PM)
     * @param sched com.cannontech.macs.Info
     */
    private void handleScriptFile(ScriptFileMessage file) {
        com.cannontech.clientutils.CTILogger.info("Received a ScriptFile " + file.getFileName());

        boolean found = false;
        for (int j = 0; j < getSchedules().size(); j++) {
            if (((ScheduleMessage) getSchedules().get(j)).getScriptFileName().equalsIgnoreCase(file.getFileName())) {
                // if this schedule uses the newly received script file name, update its values
                ((ScheduleMessage) getSchedules().get(j)).getNonPersistantData().setScript(file);
            }
        }

    }

    public boolean isScheduleNameExists(String scheduleName, int scheduleId) {
        boolean found = false;
        for (int j = 0; j < getSchedules().size(); j++) {
            ScheduleMessage sched = (ScheduleMessage) getSchedules().get(j);
            if (sched.getScheduleName().equalsIgnoreCase(scheduleName) && sched.getId() != scheduleId) {
                found = true;
                break;
            }
        }
        return found;
    }

    public boolean isScriptFileNameExists(String scriptFileName, int scheduleId) {
        boolean found = false;
        for (int j = 0; j < getSchedules().size(); j++) {
            ScheduleMessage sched = (ScheduleMessage) getSchedules().get(j);
            if (sched.getScriptFileName().equalsIgnoreCase(scriptFileName) && sched.getId() != scheduleId) {
                found = true;
                break;
            }
        }
        return found;
    }

    /**
     * Insert the method's description here. Creation date: (2/23/2001 10:08:10 AM)
     * @param catName java.lang.String
     */
    private void removeCategory(ScheduleMessage sched) {
        if (sched == null)
            return;

        Object o = getCategoryNames().get(sched.getCategoryName());

        if (o != null) {
            // we found this category name to exist, lets just remove the
            // schedule in the arrayList associated with the categoryName
            try {
                java.util.ArrayList list = (java.util.ArrayList) o;
                list.remove(sched);

                // if there are no more schedules that have this category name, lets delete this category
                if (list.size() <= 0) {
                    getCategoryNames().remove(sched.getCategoryName());

                    // tell our listeners we have added a new category
                    setChanged();
                    notifyObservers(new CategoryChangeEvent(this, CategoryChangeEvent.DELETE, sched.getCategoryName()));
                }

            }
            catch (ClassCastException e) {
                throw new ClassCastException("CategoryName hashTable contained values that are not of type ArrayList");
            }

        }

    }

    public ScheduleMessage[] retrieveSchedules() {
        try {
            ScheduleMessage[] scheds = new ScheduleMessage[getSchedules().size()];
            getSchedules().toArray(scheds);
            return scheds;
        }
        catch (ArrayStoreException e) {
            throw e;
        }

    }

    public void sendCreateSchedule(ScheduleMessage sched) throws java.io.IOException {

        if (!(isValid()))
            throw new java.io.IOException("Not connected to MACSServer.");

        com.cannontech.messaging.message.macs.AddScheduleMessage newSchedule =
            new com.cannontech.messaging.message.macs.AddScheduleMessage();
        newSchedule.setUserName(com.cannontech.common.util.CtiUtilities.getUserName());
        newSchedule.setSchedule(sched);
        newSchedule.setScript(sched.getNonPersistantData().getScript().getFileContents());

        write(newSchedule);

    }

    public void sendDeleteSchedule(int scheduleID) throws java.io.IOException {
        if (!(isValid()))
            throw new java.io.IOException("Not connected to MACSServer.");

        com.cannontech.messaging.message.macs.DeleteScheduleMessage sched =
            new com.cannontech.messaging.message.macs.DeleteScheduleMessage();
        sched.setUserName(com.cannontech.common.util.CtiUtilities.getUserName());
        sched.setScheduleId(scheduleID);

        write(sched);
    }

    public void sendEnableDisableSchedule(ScheduleMessage sched) throws java.io.IOException {
        if (!(isValid()))
            throw new java.io.IOException("Not connected to MACSServer.");

        com.cannontech.messaging.message.macs.OverrideRequestMessage request =
            new com.cannontech.messaging.message.macs.OverrideRequestMessage();
        request.setUserName(com.cannontech.common.util.CtiUtilities.getUserName());
        request.setSchedId(sched.getId());

        if (sched.getCurrentState().equalsIgnoreCase(ScheduleMessage.STATE_DISABLED)) {
            request.setAction(com.cannontech.messaging.message.macs.OverrideRequestMessage.OVERRIDE_ENABLE);
            request.setStart(new java.util.Date()); // current time
        }
        else {
            request.setAction(com.cannontech.messaging.message.macs.OverrideRequestMessage.OVERRIDE_DISABLE);
            request.setStop(new java.util.Date()); // current time
        }

        write(request);
    }

    /**
     * Sends a RetrieveSchedule message.
     */
    public void sendRetrieveAllSchedules() throws java.io.IOException {
        RetrieveScheduleMessage retrieveAllSchedulesMsg = getRetrieveAllSchedulesMsg();

        write(retrieveAllSchedulesMsg);
    }

    /**
     * Builds a RetrieveSchedule message that will retrieve all schedules.
     * @return a RetrieveSchedule message.
     */
    public RetrieveScheduleMessage getRetrieveAllSchedulesMsg() {
        RetrieveScheduleMessage newSchedules = new RetrieveScheduleMessage();
        newSchedules.setUserName(CtiUtilities.getUserName());
        newSchedules.setScheduleId(RetrieveScheduleMessage.ALL_SCHEDULES);
        return newSchedules;
    }

    public void sendRetrieveOneSchedule(int schedId) throws java.io.IOException {
        if (!(isValid()))
            throw new java.io.IOException("Not connected to MACSServer.");

        com.cannontech.messaging.message.macs.RetrieveScheduleMessage schedule =
            new com.cannontech.messaging.message.macs.RetrieveScheduleMessage();
        schedule.setUserName(com.cannontech.common.util.CtiUtilities.getUserName());
        schedule.setScheduleId(schedId);

        write(schedule);
    }

    public void sendRetrieveScriptText(String scriptFileName) throws java.io.IOException {
        if (!(isValid()))
            throw new java.io.IOException("Not connected to MACSServer.");

        com.cannontech.messaging.message.macs.RetrieveScriptMessage request =
            new com.cannontech.messaging.message.macs.RetrieveScriptMessage();
        request.setUserName(com.cannontech.common.util.CtiUtilities.getUserName());
        request.setScriptName(scriptFileName);

        write(request);
    }

    public void sendScriptFile(com.cannontech.messaging.message.macs.ScriptFileMessage file) throws java.io.IOException {
        if (!(isValid()))
            throw new java.io.IOException("Not connected to MACSServer.");

        com.cannontech.messaging.message.macs.ScriptFileMessage script =
            new com.cannontech.messaging.message.macs.ScriptFileMessage();
        script.setUserName(com.cannontech.common.util.CtiUtilities.getUserName());
        script.setFileName(file.getFileName());
        script.setFileContents(file.getFileContents());

        write(script);
    }

    public void sendStartStopSchedule(ScheduleMessage sched, java.util.Date startTime, java.util.Date stopTime,
                                      int command) throws java.io.IOException {
        if (!(isValid()))
            throw new java.io.IOException("Not connected to MACSServer.");

        /* Strange behavior here. If we have to send a start AND stop time, */
        /* we must send two seperate messages to the server? Server fix later. */

        // send the first message
        OverrideRequestMessage request = new OverrideRequestMessage();
        request.setSchedId(sched.getId());
        request.setAction(command);

        if (startTime != null)
            request.setStart(startTime);

        if (stopTime != null)
            request.setStop(stopTime);

        write(request);

        // send the second message if needed
        if (stopTime != null &&
            (request.getAction() == OverrideRequestMessage.OVERRIDE_START || request.getAction() == OverrideRequestMessage.OVERRIDE_START_NOW)) {
            OverrideRequestMessage secondRequest = new OverrideRequestMessage();
            secondRequest.setSchedId(request.getSchedId());

            secondRequest.setStart(request.getStart());
            secondRequest.setStop(request.getStop());
            secondRequest.setAction(OverrideRequestMessage.OVERRIDE_STOP);

            write(secondRequest);
        }

    }

    public void sendUpdateSchedule(ScheduleMessage sched) throws java.io.IOException {

        if (!(isValid()))
            throw new java.io.IOException("Not connected to MACSServer.");

        com.cannontech.messaging.message.macs.UpdateScheduleMessage modifiedSchedule =
            new com.cannontech.messaging.message.macs.UpdateScheduleMessage();
        modifiedSchedule.setTimeStamp(new java.util.Date());
        modifiedSchedule.setUserName(com.cannontech.common.util.CtiUtilities.getUserName());
        modifiedSchedule.setSchedule(sched);
        modifiedSchedule.setScript(sched.getNonPersistantData().getScript().getFileContents());

        write(modifiedSchedule);
    }

    public void writeMsg(BaseMessage msg) throws java.io.IOException {
        write(msg);
    }

    /**
     * Send a MessageEvent to all of this connections MessageListeners
     * @param msg
     */
    @Override
    protected void fireMessageEvent(BaseMessage msg) {
        if (msg instanceof ScheduleMessage) {
            handleSchedule((ScheduleMessage) msg);
        }
        else if (msg instanceof DeleteScheduleMessage) {
            handleDeleteSchedule((DeleteScheduleMessage) msg);
        }
        else if (msg instanceof InfoMessage) {
            handleInfo((InfoMessage) msg);
        }
        else if (msg instanceof ScriptFileMessage) {
            handleScriptFile((ScriptFileMessage) msg);
        }
        else if (msg instanceof ConnStateChangeMessage) {
            // nothing to do locally
        }
        else
            throw new RuntimeException("Recieved a message of an unknown type: " + msg.getClass());

        super.fireMessageEvent(msg);
    }
}
