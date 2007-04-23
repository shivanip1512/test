package com.cannontech.core.authorization.service.impl;

import java.util.ArrayList;
import java.util.List;

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

    public List<LiteYukonPAObject> getPaos(T group, Permission permission) {

        List<PaoPermission> permList = permissionDao.getPermissions(group);

        List<LiteYukonPAObject> paos = new ArrayList<LiteYukonPAObject>();
        for (PaoPermission perm : permList) {
            if (permission.equals(perm.getPermission())) {
                paos.add(paoDao.getLiteYukonPAO(perm.getPaoId()));
            }
        }

        return paos;
    }

    public boolean savePermissions(T group, List<Integer> idList, Permission permission) {

        // Remove existing pao permissions
        permissionDao.removeAllPermissions(group);

        // Add new pao permissions
        for (Integer id : idList) {
            permissionDao.addPermission(group, new LiteYukonPAObject(id), permission);

        }

        return true;
    }

}
