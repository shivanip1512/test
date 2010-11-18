package com.cannontech.stars.dr.hardware.dao;

import java.util.List;

import com.cannontech.stars.dr.hardware.model.InventoryPagingSchedule;

public interface InventoryPagingScheduleDao {
    public InventoryPagingSchedule getById(int inventoryPagingScheduleId);

    public List<InventoryPagingSchedule> getAll();

    public List<InventoryPagingSchedule> getAllEnabled();

    public void save(InventoryPagingSchedule schedule);
}
