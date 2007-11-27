package com.cannontech.cbc.dao;

import java.util.List;

import com.cannontech.cbc.model.CapbankComment;
import com.cannontech.core.dao.NotFoundException;


public interface CapbankCommentDao {
    
    public boolean add( CapbankComment bank );
    
    public boolean remove( CapbankComment bank );
    
    public boolean update( CapbankComment bank );
    
    public CapbankComment getById( int id ) throws NotFoundException;
    
    public List<String> getLastFiveByPaoId( int paoId ) throws NotFoundException;
    
    public List<CapbankComment> getAllCommentsByPao( int paoId ) throws NotFoundException;

}
