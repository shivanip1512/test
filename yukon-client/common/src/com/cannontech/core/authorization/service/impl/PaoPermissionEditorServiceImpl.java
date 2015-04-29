package com.cannontech.core.authorization.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.common.pao.YukonPao;
import com.cannontech.core.authorization.dao.PaoPermissionDao;
import com.cannontech.core.authorization.model.PaoPermission;
import com.cannontech.core.authorization.service.PaoPermissionEditorService;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;

public class PaoPermissionEditorServiceImpl<T> implements PaoPermissionEditorService<T> {

    private PaoDao paoDao = null;
    private PaoPermissionDao<T> permissionDao;
    
    public PaoDao getPaoDao() {
        return paoDao;
    }
    
    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }
    
    public void setPermissionDao(PaoPermissionDao<T> permissionDao) {
        this.permissionDao = permissionDao;
    }
    
    public List<LiteYukonPAObject> getPaos(T object, Permission permission) {
        
        List<PaoPermission> permList = permissionDao.getPermissions(object);
        
        List<LiteYukonPAObject> paos = new ArrayList<LiteYukonPAObject>();
        for (PaoPermission perm : permList) {
            if (permission.equals(perm.getPermission())) {
                paos.add(paoDao.getLiteYukonPAO(perm.getPaoId()));
            }
        }
        
        return paos;
    }
    
    public boolean savePermissions(T object, List<Integer> idList, Permission permission, boolean allow) {
        
        // Remove existing pao permissions
        permissionDao.removeAllPermissions(object, permission);
        
        // Add new pao permissions
        for (Integer id : idList) {
            permissionDao.addPermission(object, id, permission, allow);
        }
        
        return true;
    }
    
    public boolean addPermissions(T group, List<Integer> idList, Permission permission, boolean allow) {
        
        // Add new pao permissions
        for (Integer id : idList) {
            permissionDao.addPermission(group, id, permission, allow);
        }
        
        return true;
    }
    
    @Override
    public void removePermission(T object, YukonPao pao, Permission permission) {
        permissionDao.removePermission(object, pao, permission);
    }
    
}