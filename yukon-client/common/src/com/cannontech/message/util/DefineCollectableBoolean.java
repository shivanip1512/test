package com.cannontech.message.util;

import java.io.IOException;

import com.roguewave.tools.v2_0.Comparator;
import com.roguewave.vsj.CollectableStreamer;
import com.roguewave.vsj.DefineCollectable;
import com.roguewave.vsj.VirtualInputStream;
import com.roguewave.vsj.VirtualOutputStream;

public class DefineCollectableBoolean implements DefineCollectable {
    public DefineCollectableBoolean() {
        super();
    }

    public Object create(VirtualInputStream vstr) throws java.io.IOException {
        return new CollectableBoolean();
    }

    public Comparator getComparator() {
        return new Comparator() {
            public int compare(Object x, Object y) {
                if (x == y) {
                    return 0;
                } else {
                    return -1;
                }
            }
        };
    }

    public int getCxxClassId() {
        return DefineCollectable.NO_CLASSID;
    }

    public String getCxxStringId() {
        return DefineCollectable.NO_STRINGID;
    }

    public Class getJavaClass() {
        return CollectableBoolean.class;
    }

    public void restoreGuts(Object obj, VirtualInputStream vstr,
                            CollectableStreamer polystr)
        throws IOException {

        CollectableBoolean bool = (CollectableBoolean) obj;
        bool.setValue(vstr.extractChar() == 'T');
    }

    public void saveGuts(Object obj, VirtualOutputStream vstr,
                         CollectableStreamer polystr) throws IOException {

        CollectableBoolean bool = (CollectableBoolean) obj;
        vstr.insertChar(bool.getValue() ? 'T' : 'F');
    }
}
