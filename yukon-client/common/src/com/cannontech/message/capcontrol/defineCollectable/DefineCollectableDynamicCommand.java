package com.cannontech.message.capcontrol.defineCollectable;

import java.io.IOException;
import java.util.Map;

import com.cannontech.message.capcontrol.model.DynamicCommand;
import com.cannontech.message.capcontrol.model.DynamicCommand.DynamicCommandType;
import com.cannontech.message.capcontrol.model.DynamicCommand.Parameter;
import com.cannontech.message.util.CollectionInserter;
import com.roguewave.tools.v2_0.Comparator;
import com.roguewave.vsj.CollectableStreamer;
import com.roguewave.vsj.DefineCollectable;
import com.roguewave.vsj.VirtualInputStream;
import com.roguewave.vsj.VirtualOutputStream;

public class DefineCollectableDynamicCommand  extends DefineCollectableCapControlCommand {

    private static int DYNAMICCOMMAND_ID = 529;
    
    public DefineCollectableDynamicCommand() { 
        super();
    }
    
    @Override
    public Object create(VirtualInputStream arg0) {
        return new DynamicCommand(DynamicCommandType.UNDEFINED);
    }

    @Override
    public Comparator getComparator() {
        return new Comparator() {
            public int compare(Object x, Object y) {
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
    public void restoreGuts(Object arg0, VirtualInputStream arg1, CollectableStreamer polystr) {
        throw new UnsupportedOperationException("RestoreGuts invalid for " + this.getClass().getName());
    }

    @Override
    public void saveGuts(Object obj, VirtualOutputStream vstr, CollectableStreamer polystr) throws IOException {

        super.saveGuts(obj,vstr,polystr);

        DynamicCommand command = (DynamicCommand) obj;
        
        DynamicCommandType dynamicCommandType = command.getCommandType();
        Map<Parameter, Integer> intMap = command.getIntParameters();
        Map<Parameter, Double> doubleMap = command.getDoubleParameters();
        
        vstr.insertUnsignedInt(dynamicCommandType.getSerializeId());
        CollectionInserter.insertIntegerMap(intMap, vstr, polystr);
        CollectionInserter.insertDoubleMap(doubleMap, vstr, polystr);
    }

}