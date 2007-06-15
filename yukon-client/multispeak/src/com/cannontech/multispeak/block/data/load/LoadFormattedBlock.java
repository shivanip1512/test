package com.cannontech.multispeak.block.data.load;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.multispeak.block.data.FormattedBlockBase;
import com.cannontech.multispeak.block.data.FormattedBlockValueListBase;

public class LoadFormattedBlock implements FormattedBlockBase {

    private List<LoadBlock> loadBlock;
    private LoadValList loadValList;
    public LoadFormattedBlock(List<LoadBlock> loadBlock) {
        super();
        this.loadBlock = loadBlock;
        loadValList = new LoadValList(this.loadBlock);
    }  
    
    public LoadFormattedBlock(LoadBlock loadBlock) {
        super();
        this.loadBlock = new ArrayList<LoadBlock>();
        this.loadBlock.add(loadBlock);
        loadValList = new LoadValList(this.loadBlock);
    }
    
    public LoadFormattedBlock() {
        // TODO Auto-generated constructor stub
    }
    
    public List<LoadBlock> getLoadBlock() {
        return loadBlock;
    }
    
    public void setLoadBlock(List<LoadBlock> loadBlock) {
        this.loadBlock = loadBlock;
    }
    
    public FormattedBlockValueListBase getValList() {
        return loadValList;
    }
}
