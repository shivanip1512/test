/*
 * Created on May 18, 2005
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.database.db;

import java.util.Comparator;
import java.util.Vector;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.TransactionType;
import com.cannontech.database.db.contact.ContactNotification;
import com.cannontech.database.db.device.DeviceVerification;
import com.cannontech.database.db.device.lm.LMControlAreaProgram;
import com.cannontech.database.db.device.lm.LMControlAreaTrigger;
import com.cannontech.database.db.device.lm.LMControlScenarioProgram;
import com.cannontech.database.db.device.lm.LMProgramDirectGear;
import com.cannontech.database.db.pao.PAOExclusion;

public final class NestedDBPersistentComparators {
    public static Comparator<LMControlAreaTrigger> lmControlAreaTriggerComparator =
        new Comparator<LMControlAreaTrigger>() {
            @Override
            public int compare(LMControlAreaTrigger trigger1, LMControlAreaTrigger trigger2) {
                // if the ID is null, then one can safely assume it is new
                Integer thisVal = trigger1.getTriggerID();
                if (thisVal == null) {
                    thisVal = -2;
                }
                Integer anotherVal = trigger2.getTriggerID();
                if (anotherVal == null) {
                    anotherVal = -3;
                }
                return (thisVal.intValue() < anotherVal.intValue() ? -1 : (thisVal.intValue() == anotherVal.intValue()
                    ? 0 : 1));
            }

            @Override
            public boolean equals(Object obj) {
                return false;
            }
        };

    public static Comparator<LMProgramDirectGear> lmDirectGearComparator = new Comparator<LMProgramDirectGear>() {
        @Override
        public int compare(LMProgramDirectGear gear1, LMProgramDirectGear gear2) {
            // if the ID is null, then one can safely assume it is new
            Integer thisVal = gear1.getGearID();
            if (thisVal == null) {
                thisVal = -2;
            }
            Integer anotherVal = gear2.getGearID();
            if (anotherVal == null) {
                anotherVal = -1;
            }
            return (thisVal.intValue() < anotherVal.intValue() ? -1 : (thisVal.intValue() == anotherVal.intValue() ? 0
                : 1));
        }

        @Override
        public boolean equals(Object obj) {
            return false;
        }
    };

    public static Comparator<LMControlAreaProgram> lmControlAreaProgramComparator =
        new Comparator<LMControlAreaProgram>() {
            @Override
            public int compare(LMControlAreaProgram program1, LMControlAreaProgram program2) {
                int thisVal = program1.getLmProgramDeviceID().intValue();
                int anotherVal = program2.getLmProgramDeviceID().intValue();
                return (thisVal < anotherVal ? -1 : (thisVal == anotherVal ? 0 : 1));
            }

            @Override
            public boolean equals(Object obj) {
                return false;
            }
        };

    public static Comparator<LMControlScenarioProgram> lmControlScenarioProgramComparator =
        new Comparator<LMControlScenarioProgram>() {
            @Override
            public int compare(LMControlScenarioProgram program1, LMControlScenarioProgram program2) {
                int thisVal = program1.getProgramID().intValue();
                int anotherVal = program2.getProgramID().intValue();
                return (thisVal < anotherVal ? -1 : (thisVal == anotherVal ? 0 : 1));
            }

            @Override
            public boolean equals(Object obj) {
                return false;
            }
        };

    public static Comparator<DeviceVerification> deviceVerificationComparator = new Comparator<DeviceVerification>() {
        @Override
        public int compare(DeviceVerification deviceVerification1, DeviceVerification deviceVerification2) {
            int thisTransID = deviceVerification1.getTransmitterID().intValue();
            int anotherTransID = deviceVerification2.getTransmitterID().intValue();
            int thisReceiverID = deviceVerification1.getReceiverID().intValue();
            int anotherReceiverID = deviceVerification2.getReceiverID().intValue();
            return (thisTransID == anotherTransID ? (thisReceiverID == anotherReceiverID ? 0 : 1) : -1);
        }

        @Override
        public boolean equals(Object obj) {
            return false;
        }
    };

    public static Comparator<PAOExclusion> paoExclusionComparator = new Comparator<PAOExclusion>() {
        @Override
        public int compare(PAOExclusion paoExclusion1, PAOExclusion paoExclusion2) {
            // if the ID is null, then one can safely assume it is new
            Integer thisVal = paoExclusion1.getExclusionID();
            if (thisVal == null) {
                thisVal = -1;
            }
            Integer anotherVal = paoExclusion2.getExclusionID();
            if (anotherVal == null) {
                anotherVal = -1;
            }
            return (thisVal.intValue() < anotherVal.intValue() ? -1 : (thisVal.intValue() == anotherVal.intValue() ? 0
                : 1));
        }

        @Override
        public boolean equals(Object obj) {
            return false;
        }
    };

    public static Comparator<ContactNotification> contactNotificationComparator = new Comparator<ContactNotification>() {
        @Override
        public int compare(ContactNotification notification1, ContactNotification notification2) {
            // if the ID is null, then one can safely assume it is new
            Integer thisVal = notification1.getContactNotifID();
            if (thisVal == null) {
                thisVal = -1;
            }
            Integer anotherVal = notification2.getContactNotifID();
            if (anotherVal == null) {
                anotherVal = -2;
            }
            return (thisVal.intValue() < anotherVal.intValue() ? -1 : (thisVal.intValue() == anotherVal.intValue() ? 0
                : 1));
        }

        @Override
        public boolean equals(Object obj) {
            return false;
        }
    };

    public static <T> boolean areNestedObjectsEqual(T obj1, T obj2, Comparator<? super T> comparator) {
        return comparator.compare(obj1, obj2) == 0;
    }

    public static <T extends NestedDBPersistent> Vector<T> NestedDBPersistentCompare(Vector<T> oldList,
            Vector<T> newList, Comparator<? super T> comparator) {
        if (comparator == null) {
            CTILogger.error("NestedDBPersistentCompare requires a valid nested db persistent Comparator.");
        }

        Vector<T> tempVect = new Vector<>();

        // checks for old or unused objects to update or delete
        for (int j = 0; j < oldList.size(); j++) {
            T oldNest = oldList.get(j);
            boolean fnd = false;

            for (int i = 0; i < newList.size(); i++) {
                T newNest = newList.get(i);

                if (areNestedObjectsEqual(oldNest, newNest, comparator)) {
                    // item in OLD list & NEW list, update
                    newNest.setOpCode(TransactionType.UPDATE);
                    tempVect.add(newNest);
                    fnd = true;
                    break;
                }
            }

            if (!fnd) {
                // item in OLD list only, delete
                oldNest.setOpCode(TransactionType.DELETE);
                tempVect.add(oldNest);
            }
        }

        // checks for brand new objects to add
        for (int x = 0; x < newList.size(); x++) {
            T newNest = newList.get(x);
            boolean inOld = false;

            for (int i = 0; i < oldList.size(); i++) {
                T oldNest = oldList.get(i);

                if (areNestedObjectsEqual(newNest, oldNest, comparator)) {
                    inOld = true;
                    break;
                }
            }
            if (!inOld) {
                // item in NEW list, add
                newNest.setOpCode(TransactionType.INSERT);
                tempVect.add(newNest);
            }
        }

        return tempVect;
    }
}
