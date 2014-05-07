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
            public int compare(LMControlAreaTrigger o1, LMControlAreaTrigger o2) {
                // if the ID is null, then one can safely assume it is new
                Integer thisVal = o1.getTriggerID();
                if (thisVal == null) {
                    thisVal = new Integer(-2);
                }
                Integer anotherVal = o2.getTriggerID();
                if (anotherVal == null) {
                    anotherVal = new Integer(-3);
                }
                return (thisVal.intValue() < anotherVal.intValue() ? -1 : (thisVal.intValue() == anotherVal.intValue()
                    ? 0 : 1));
            }

            @Override
            public boolean equals(Object obj) {
                return false;
            }
        };

    public static Comparator lmDirectGearComparator = new Comparator() {
        @Override
        public int compare(Object o1, Object o2) {
            // if the ID is null, then one can safely assume it is new
            Integer thisVal = ((LMProgramDirectGear) o1).getGearID();
            if (thisVal == null) {
                thisVal = new Integer(-2);
            }
            Integer anotherVal = ((LMProgramDirectGear) o2).getGearID();
            if (anotherVal == null) {
                anotherVal = new Integer(-1);
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
            public int compare(LMControlAreaProgram o1, LMControlAreaProgram o2) {
                int thisVal = o1.getLmProgramDeviceID().intValue();
                int anotherVal = o2.getLmProgramDeviceID().intValue();
                return (thisVal < anotherVal ? -1 : (thisVal == anotherVal ? 0 : 1));
            }

            @Override
            public boolean equals(Object obj) {
                return false;
            }
        };

    public static Comparator lmControlScenarioProgramComparator = new Comparator() {
        @Override
        public int compare(Object o1, Object o2) {
            int thisVal = ((LMControlScenarioProgram) o1).getProgramID().intValue();
            int anotherVal = ((LMControlScenarioProgram) o2).getProgramID().intValue();
            return (thisVal < anotherVal ? -1 : (thisVal == anotherVal ? 0 : 1));
        }

        @Override
        public boolean equals(Object obj) {
            return false;
        }
    };

    public static Comparator deviceVerificationComparator = new Comparator() {
        @Override
        public int compare(Object o1, Object o2) {
            int thisTransID = ((DeviceVerification) o1).getTransmitterID().intValue();
            int anotherTransID = ((DeviceVerification) o2).getTransmitterID().intValue();
            int thisReceiverID = ((DeviceVerification) o1).getReceiverID().intValue();
            int anotherReceiverID = ((DeviceVerification) o2).getReceiverID().intValue();
            return (thisTransID == anotherTransID ? (thisReceiverID == anotherReceiverID ? 0 : 1) : -1);
        }

        @Override
        public boolean equals(Object obj) {
            return false;
        }
    };

    public static Comparator paoExclusionComparator = new Comparator() {
        @Override
        public int compare(Object o1, Object o2) {
            // if the ID is null, then one can safely assume it is new
            Integer thisVal = ((PAOExclusion) o1).getExclusionID();
            if (thisVal == null) {
                thisVal = new Integer(-1);
            }
            Integer anotherVal = ((PAOExclusion) o2).getExclusionID();
            if (anotherVal == null) {
                anotherVal = new Integer(-1);
            }
            return (thisVal.intValue() < anotherVal.intValue() ? -1 : (thisVal.intValue() == anotherVal.intValue() ? 0
                : 1));
        }

        @Override
        public boolean equals(Object obj) {
            return false;
        }
    };

    public static Comparator contactNotificationComparator = new Comparator() {
        @Override
        public int compare(Object o1, Object o2) {
            // if the ID is null, then one can safely assume it is new
            Integer thisVal = ((ContactNotification) o1).getContactNotifID();
            if (thisVal == null) {
                thisVal = new Integer(-1);
            }
            Integer anotherVal = ((ContactNotification) o2).getContactNotifID();
            if (anotherVal == null) {
                anotherVal = new Integer(-2);
            }
            return (thisVal.intValue() < anotherVal.intValue() ? -1 : (thisVal.intValue() == anotherVal.intValue() ? 0
                : 1));
        }

        @Override
        public boolean equals(Object obj) {
            return false;
        }
    };

    public static boolean areNestedObjectsEqual(Object obj1, Object obj2, Comparator comparator) {
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
