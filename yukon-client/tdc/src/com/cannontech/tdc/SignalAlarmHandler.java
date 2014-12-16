package com.cannontech.tdc;

import java.text.SimpleDateFormat;
import java.util.Vector;

import javax.swing.JMenuItem;

import com.cannontech.clientutils.tags.TagUtils;
import com.cannontech.common.login.ClientSession;
import com.cannontech.core.dao.AlarmCatDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteAlarmCategory;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.message.dispatch.message.Signal;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.tdc.bookmark.BookMarkBase;
import com.cannontech.tdc.bookmark.BookMarkSelectionListener;
import com.cannontech.tdc.bookmark.SelectionHandler;
import com.cannontech.tdc.data.Display;
import com.cannontech.tdc.utils.TDCDefines;

class SignalAlarmHandler {
    private static final java.awt.Color ALARM_FG_COLOR = java.awt.Color.red.brighter();

    private static java.awt.Color bgColor = java.awt.SystemColor.control;
    private static java.awt.Color fgColor = java.awt.SystemColor.controlText;

    private Thread alrmBlinker = null;
    private javax.swing.JMenu alarmMenu = null;

    private Vector<JMenuItem> alarmVector = null;

    public static final int ALARMS_DISPLAYED = Integer.parseInt(ClientSession.getInstance()
                                                                             .getRolePropertyValue(YukonRoleProperty.TDC_ALARM_COUNT));

    private BookMarkSelectionListener bookmarkListener = null;

    /**
     * Constructor for SignalAlarmHandler.
     */
    public SignalAlarmHandler(javax.swing.JMenu menu_, BookMarkSelectionListener bookmarkListener_) {
        super();

        if (menu_ == null || bookmarkListener_ == null)
            throw new IllegalArgumentException("Constuctor must take a non null values for: " + getClass().getName());

        alarmMenu = menu_;
        bookmarkListener = bookmarkListener_;
        bgColor = alarmMenu.getBackground();
        fgColor = alarmMenu.getForeground();

        getJMenuAlarms().setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        alarmMenu.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                if (alrmBlinker != null)
                    alrmBlinker.interrupt();
            }
        });

    }

    private synchronized Vector<JMenuItem> getAlarmVector() {
        if (alarmVector == null) {
            alarmVector = new Vector<JMenuItem>(10);
        }
        return alarmVector;
    }

    private javax.swing.JMenu getJMenuAlarms() {
        return alarmMenu;
    }

    public synchronized void handleSignal(Signal sig) {

        boolean foundSig = false;
        int prevAlrmCnt = getAlarmVector().size();// alarmCount;
        boolean addAlarm = TagUtils.isAlarmUnacked(sig.getTags());

        for (int i = 0; i < getAlarmVector().size(); i++) {
            javax.swing.JMenuItem menuItem = getAlarmVector().get(i);

            Signal storedSig = (Signal) menuItem.getClientProperty(SignalAlarmHandler.class.getName());

            // we already have a JMenuItem for this signal
            if (storedSig != null) {
                if (storedSig.equals(sig)) {// update the sig value
                    if (addAlarm) { // update the underlying signal
                        menuItem.putClientProperty(SignalAlarmHandler.class.getName(), sig);

                        LitePoint lp = YukonSpringHook.getBean(PointDao.class).getLitePoint(sig.getPointID());

                        menuItem.setText("(" + YukonSpringHook.getBean(PaoDao.class).getYukonPAOName(lp.getPaobjectID()) + 
                                         " / " + lp.getPointName() + ") " + sig.getDescription() + 
                                         (TagUtils.isAlarmUnacked(sig.getTags()) ? "" : " (ACKED)"));

                        menuItem.setToolTipText(sig.getAction() + " @ " + new SimpleDateFormat("MM-dd-yyyy HH:mm:ss").format(sig.getTimeStamp()));

                    } else { // remove the JMenuItem
                        removeAlarm(i);
                    }

                    foundSig = true;
                    break;
                }
            }
        }

        // we may need to add this new signal to the list
        if (!foundSig && addAlarm) {
            addAlarm(sig);
        }

        // keep our list in order!
        java.util.Collections.sort(getAlarmVector(), new java.util.Comparator<JMenuItem>() {
            @Override
                public int compare(JMenuItem o1, JMenuItem o2) {
                long val1 = ((Signal) o1.getClientProperty(SignalAlarmHandler.class.getName())).getTimeStamp().getTime();
                long val2 = ((Signal) o2.getClientProperty(SignalAlarmHandler.class.getName())).getTimeStamp().getTime();

                return (val1 < val2 ? 1 : (val1 == val2 ? 0 : -1));
            }

        });

        // set the color of our item
        if (getAlarmVector().size() <= 0) {
            getJMenuAlarms().setForeground(fgColor);
            getJMenuAlarms().setBackground(bgColor);
        } else {
            getJMenuAlarms().setForeground(ALARM_FG_COLOR);

            // only start blinking if we added an alarm
            if (getAlarmVector().size() > prevAlrmCnt) {
                createMenuBlinker();
            }
        }

        // update the text of our menu
        getJMenuAlarms().setText("Unacked Alarms: " + getAlarmVector().size());

        updateAlarmMenu();
    }

    private void removeAlarm(int menuIndx) {
        getAlarmVector().remove(menuIndx);
    }

    private void updateAlarmMenu() {
        synchronized (getAlarmVector()) {
            getJMenuAlarms().removeAll();

            for (int i = 0; i < getAlarmVector().size(); i++) {
                javax.swing.JMenuItem item = getAlarmVector().get(i);

                if (i < ALARMS_DISPLAYED) {
                    getJMenuAlarms().add(item);
                }
            }
        }
    }

    private void addAlarm(Signal sig) {
        if (sig == null) {
            return;
        }

        LitePoint lp = YukonSpringHook.getBean(PointDao.class).getLitePoint(sig.getPointID());

        JMenuItem newItem = new JMenuItem("(" + YukonSpringHook.getBean(PaoDao.class).getYukonPAOName(lp.getPaobjectID()) + 
                                          " / " + lp.getPointName() + ") " + sig.getDescription() + 
                                          (TagUtils.isAlarmUnacked(sig.getTags()) ? "" : " (ACKED)"));

        newItem.setToolTipText(sig.getAction() + " @ " + new SimpleDateFormat("MM-dd-yyyy HH:mm:ss").format(sig.getTimeStamp()));

        newItem.putClientProperty(SignalAlarmHandler.class.getName(), sig);
        newItem.setBackground(java.awt.SystemColor.control);
        newItem.setForeground(java.awt.SystemColor.controlText);

        LiteAlarmCategory liteAlarmCategory = YukonSpringHook.getBean(AlarmCatDao.class).getAlarmCategoryFromCache((int) sig.getCategoryID());
        String categoryText = (liteAlarmCategory == null ? null : liteAlarmCategory.getCategoryName());

        newItem.putClientProperty(TDCMainPanel.PROP_BOOKMARK,
                                  Display.DISPLAY_TYPES[Display.ALARMS_AND_EVENTS_TYPE_INDEX] + 
                                  BookMarkBase.BOOKMARK_TOKEN + categoryText);

        newItem.addActionListener(new SelectionHandler(bookmarkListener));

        getAlarmVector().add(newItem);
    }

    private void createMenuBlinker() {
        if (alrmBlinker == null) {
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    try {
                        while (getAlarmVector().size() > 0) {
                            if (!TDCDefines.ICON_ALARM.equals(getJMenuAlarms().getIcon())) {
                                getJMenuAlarms().setIcon(TDCDefines.ICON_ALARM);
                            }

                            Thread.currentThread().sleep(1000);
                        }
                    } catch (Exception e) {} finally {
                        getJMenuAlarms().setIcon(null);
                        alrmBlinker = null;
                    }
                }
            };

            alrmBlinker = new Thread(r, "AlarmsCountThread");
            alrmBlinker.start();
        }
    }
}