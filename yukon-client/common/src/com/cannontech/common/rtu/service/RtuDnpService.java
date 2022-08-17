package com.cannontech.common.rtu.service;

import java.util.List;

import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.rtu.dao.RtuDnpDao.SortBy;
import com.cannontech.common.rtu.model.RtuDnp;
import com.cannontech.common.rtu.model.RtuPointDetail;
import com.cannontech.common.rtu.model.RtuPointsFilter;
import com.cannontech.common.search.result.SearchResults;

public interface RtuDnpService {

    /**
     * Returns RTU
     */
    RtuDnp getRtuDnp(int id);

    /**
     * 
     * Returns the RTU Point detail.
     * 
     * @param rtuId - Route paobject Id
     * @param RtuPointsFilter - this object is used in filtering the RTU point details
     * @param paging - paging information, can't be null
     * @param sortBy - used by order by, can't be null
     * @param direction - direction (asc/desc) for the order by, can't be null
     */
    
    SearchResults<RtuPointDetail> getRtuPointDetail(int rtuId, RtuPointsFilter filter, Direction direction, SortBy sortBy, PagingParameters paging);
    
    /**
     * 
     * Returns the RTU Point detail.
     * 
     * @param rtuId - Id of pao
     */
    List<RtuPointDetail> getRtuPointDetail(int rtuId);

    /**
     * Returns list of devices that include parent and children.
     */
    List<Integer> getParentAndChildDevices(int rtuId);
    
    /**
     * This method inserts or updates the RTUDNP. If rtuDnp.id is null, it performs an insert. Else it will
     * perform an update.
     * 
     * @param rtuDnp - The RTU-DNP object to the saved.
     * @return pao id of the object saved.
     */
    int save(RtuDnp rtuDnp);
}
