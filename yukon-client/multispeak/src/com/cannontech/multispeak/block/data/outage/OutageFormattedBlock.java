package com.cannontech.multispeak.block.data.outage;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.multispeak.block.data.FormattedBlockBase;
import com.cannontech.multispeak.block.data.FormattedBlockValueListBase;

public class OutageFormattedBlock implements FormattedBlockBase {

    private List<OutageBlock> outageBlock;
    private OutageValList outageValList;
    public OutageFormattedBlock(List<OutageBlock> outageBlock) {
        super();
        this.outageBlock = outageBlock;
        outageValList = new OutageValList(this.outageBlock);
    }  
    
    public OutageFormattedBlock(OutageBlock outageBlock) {
        super();
        this.outageBlock = new ArrayList<OutageBlock>();
        this.outageBlock.add(outageBlock);
        outageValList = new OutageValList(this.outageBlock);
        
    }
    
    public OutageFormattedBlock() {
        // TODO Auto-generated constructor stub
    }
    
    public void setOutageBlock(List<OutageBlock> outageBlock) {
        this.outageBlock = outageBlock;
    }
    
    public List<OutageBlock> getOutageBlock() {
        return outageBlock;
    }
    
    public FormattedBlockValueListBase getValList() {
        return outageValList;
    }
}
