package com.cannontech.cbc.dao;

import java.util.List;
import com.cannontech.cbc.model.CapControlComment;

public interface CapControlCommentDao {
    
    public boolean add( CapControlComment bank );
    
    public boolean remove( CapControlComment bank );
    
    public boolean update( CapControlComment bank );
    
    public CapControlComment getById( int id );
    
    public List<String> getLastFiveByPaoId( int paoId );
    
    public List<CapControlComment> getAllCommentsByPao( int paoId );

}
