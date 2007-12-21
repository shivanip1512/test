package com.cannontech.cbc.dao;

import java.util.List;

import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import com.cannontech.cbc.model.CapControlComment;

public interface CapControlCommentDao {
    
    public boolean add(CapControlComment bank);
    
    public boolean remove(CapControlComment bank);
    
    public boolean update(CapControlComment bank);
    
    public CapControlComment getById(int id) throws IncorrectResultSizeDataAccessException;
    
    public CapControlComment getDisabledByPaoId(int paoId) throws IncorrectResultSizeDataAccessException;
    
    public CapControlComment getDisabledOVUVByPaoId(int paoId, int type) throws DataRetrievalFailureException;
    
    public CapControlComment getStandaloneReasonByPaoId(int paoId) throws IncorrectResultSizeDataAccessException;
    
    public List<CapControlComment> getUserCommentsByPaoId(int paoId, int num);
    
    public List<CapControlComment> getAllCommentsByPao(int paoId);
    
}
