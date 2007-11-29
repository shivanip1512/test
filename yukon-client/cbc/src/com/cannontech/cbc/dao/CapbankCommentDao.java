package com.cannontech.cbc.dao;

import java.util.List;
import com.cannontech.cbc.model.CapbankComment;

public interface CapbankCommentDao {
    
    public boolean add( CapbankComment bank );
    
    public boolean remove( CapbankComment bank );
    
    public boolean update( CapbankComment bank );
    
    public CapbankComment getById( int id );
    
    public List<String> getLastFiveByPaoId( int paoId );
    
    public List<CapbankComment> getAllCommentsByPao( int paoId );

}
