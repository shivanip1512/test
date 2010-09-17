package com.cannontech.yukon.cbc;

import java.io.IOException;
import java.util.Map;

import com.cannontech.message.util.CollectionInserter;
import com.cannontech.yukon.cbc.DynamicCommand.CommandType;
import com.cannontech.yukon.cbc.DynamicCommand.Parameter;
import com.roguewave.tools.v2_0.Comparator;
import com.roguewave.vsj.CollectableStreamer;
import com.roguewave.vsj.DefineCollectable;
import com.roguewave.vsj.VirtualInputStream;
import com.roguewave.vsj.VirtualOutputStream;

public class DefineCollectableDynamicCommand  implements com.roguewave.vsj.DefineCollectable {

    private static int DYNAMICCOMMAND_ID = 529;
    
    public DefineCollectableDynamicCommand() { 
        super();
    }
    
    @Override
    public Object create(VirtualInputStream arg0) throws IOException {
        throw new IOException("Unable to create new DynamicCommand");
    }

    @Override
    public Comparator getComparator() {
        return new Comparator() 
        {
            public int compare(Object x, Object y) 
            {
                return ((DynamicCommand)x).getCommandType().compareTo(((DynamicCommand)y).getCommandType());
            }
        };
    }

    @Override
    public int getCxxClassId() {
        return DYNAMICCOMMAND_ID;
    }

    @Override
    public String getCxxStringId() {
        return DefineCollectable.NO_STRINGID;
    }

    @Override
    public Class<DynamicCommand> getJavaClass() {
        return DynamicCommand.class;
    }

    @Override
    public void restoreGuts(Object arg0, VirtualInputStream arg1, CollectableStreamer polystr) throws IOException {
        throw new IOException("Unable to restore DynamicCommand.");
    }

    @Override
    public void saveGuts(Object obj, VirtualOutputStream vstr, CollectableStreamer polystr) throws IOException {
        DynamicCommand command = (DynamicCommand) obj;
        
        CommandType commandType = command.getCommandType();
        Map<Parameter, Integer> intMap = command.getIntParameters();
        Map<Parameter, Double> doubleMap = command.getDoubleParameters();
        
        vstr.insertUnsignedInt(commandType.getSerializeId());
        CollectionInserter.insertIntegerMap(intMap, vstr, polystr);
        CollectionInserter.insertDoubleMap(doubleMap, vstr, polystr);
    }

}
