package com.cannontech.common.events.loggers;

import com.cannontech.common.events.Arg;
import com.cannontech.common.events.YukonEventLog;
import com.cannontech.common.util.TransactionExecutor.ExecutorTransactionality;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.core.dao.impl.LoginStatusEnum;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface UsersEventLogService {

    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "system.usersAndGroups.user")
    public void userCreated(@Arg(ArgEnum.username) String userName, @Arg(ArgEnum.userGroup) String userGroup,
            @Arg(ArgEnum.energyCompanyName) String energyCompany, @Arg(ArgEnum.userStatus) LoginStatusEnum userStatus,
            @Arg(ArgEnum.changedByUser) LiteYukonUser user);

    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "system.usersAndGroups.user")
    public void userUpdated(@Arg(ArgEnum.username) String userName, @Arg(ArgEnum.userGroup) String userGroup,
            @Arg(ArgEnum.energyCompanyName) String energyCompany, @Arg(ArgEnum.userStatus) LoginStatusEnum userStatus,
            @Arg(ArgEnum.changedByUser) LiteYukonUser user);

    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "system.usersAndGroups.user")
    public void userDeleted(@Arg(ArgEnum.username) String userName, @Arg(ArgEnum.changedByUser) LiteYukonUser user);

    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "system.usersAndGroups.user")
    public void passwordUpdated(@Arg(ArgEnum.username) LiteYukonUser user);

    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "system.usersAndGroups.user")
    public void userUnlocked(@Arg(ArgEnum.username) String userName, @Arg(ArgEnum.changedByUser) LiteYukonUser user);

    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "system.usersAndGroups.user")
    public void permissionAdded(@Arg(ArgEnum.paoPermissionAllowDeny) String allowDeny,
            @Arg(ArgEnum.username) String userName, @Arg(ArgEnum.paoName) String paoName,
            @Arg(ArgEnum.paoPermission) Permission permission, @Arg(ArgEnum.changedByUser) LiteYukonUser user);

    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "system.usersAndGroups.user")
    public void permissionRemoved(@Arg(ArgEnum.paoPermissionAllowDeny) String allowDeny,
            @Arg(ArgEnum.username) String username, @Arg(ArgEnum.paoName) String paoName,
            @Arg(ArgEnum.paoPermission) Permission permission, @Arg(ArgEnum.changedByUser) LiteYukonUser user);

    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "system.usersAndGroups.userGroup")
    public void userGroupCreated(@Arg(ArgEnum.userGroup) String userGroup,
            @Arg(ArgEnum.changedByUser) LiteYukonUser user);

    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "system.usersAndGroups.userGroup")
    public void userGroupUpdated(@Arg(ArgEnum.userGroup) String userGroup,
            @Arg(ArgEnum.changedByUser) LiteYukonUser user);

    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "system.usersAndGroups.userGroup")
    public void userGroupDeleted(@Arg(ArgEnum.userGroup) String userGroup,
            @Arg(ArgEnum.changedByUser) LiteYukonUser user);

    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "system.usersAndGroups.userGroup")
    public void roleGroupAdded(@Arg(ArgEnum.userGroup) String userGroup, @Arg(ArgEnum.roleGroup) String roleGroup,
            @Arg(ArgEnum.changedByUser) LiteYukonUser user);

    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "system.usersAndGroups.userGroup")
    public void roleGroupRemoved(@Arg(ArgEnum.userGroup) String userGroup, @Arg(ArgEnum.roleGroup) String roleGroup,
            @Arg(ArgEnum.changedByUser) LiteYukonUser user);

    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "system.usersAndGroups.userGroup")
    public void userAdded(@Arg(ArgEnum.username) String userName, @Arg(ArgEnum.userGroup) String userGroup,
            @Arg(ArgEnum.changedByUser) LiteYukonUser user);

    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "system.usersAndGroups.userGroup")
    public void userRemoved(@Arg(ArgEnum.username) String userName, @Arg(ArgEnum.userGroup) String userGroup,
            @Arg(ArgEnum.changedByUser) LiteYukonUser user);

    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "system.usersAndGroups.userGroup")
    public void permissionAdded(@Arg(ArgEnum.changedByUser) LiteYukonUser user,
            @Arg(ArgEnum.paoPermissionAllowDeny) String allowDeny, @Arg(ArgEnum.userGroup) String userGroup,
            @Arg(ArgEnum.paoName) String paoName, @Arg(ArgEnum.paoPermission) Permission permission);

    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "system.usersAndGroups.userGroup")
    public void permissionRemoved(@Arg(ArgEnum.changedByUser) LiteYukonUser user,
            @Arg(ArgEnum.paoPermissionAllowDeny) String allowDeny, @Arg(ArgEnum.userGroup) String userGroup,
            @Arg(ArgEnum.paoName) String paoName, @Arg(ArgEnum.paoPermission) Permission permission);

    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "system.usersAndGroups.roleGroup")
    public void roleGroupCreated(@Arg(ArgEnum.roleGroup) String roleGroup,
            @Arg(ArgEnum.username) LiteYukonUser user);

    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "system.usersAndGroups.roleGroup")
    public void roleGroupUpdated(@Arg(ArgEnum.roleGroup) String roleGroup,
            @Arg(ArgEnum.username) LiteYukonUser user);

    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "system.usersAndGroups.roleGroup")
    public void roleGroupDeleted(@Arg(ArgEnum.roleGroup) String roleGroup,
            @Arg(ArgEnum.username) LiteYukonUser user);

    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "system.usersAndGroups.roleGroup")
    public void roleAdded(@Arg(ArgEnum.roleGroup) String roleGroup, @Arg(ArgEnum.role) YukonRole role,
            @Arg(ArgEnum.changedByUser) LiteYukonUser user);

    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "system.usersAndGroups.roleGroup")
    public void rolePropertyUpdated(@Arg(ArgEnum.roleGroup) String roleGroup, @Arg(ArgEnum.role) YukonRole role,
            @Arg(ArgEnum.roleProperty) YukonRoleProperty roleProperty, String oldValue, String newValue,
            @Arg(ArgEnum.changedByUser) LiteYukonUser user);

    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "system.usersAndGroups.roleGroup")
    public void roleRemoved(@Arg(ArgEnum.roleGroup) String roleGroup, @Arg(ArgEnum.role) YukonRole role,
            @Arg(ArgEnum.changedByUser) LiteYukonUser user);

    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "system.usersAndGroups.roleGroup")
    public void roleGroupExpirePasswords(@Arg(ArgEnum.roleGroup) String roleGroup,
            @Arg(ArgEnum.changedByUser) LiteYukonUser user);
}
