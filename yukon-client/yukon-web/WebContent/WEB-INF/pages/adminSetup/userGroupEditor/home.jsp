<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="adminSetup" page="userEditor">
<cti:includeScript link="/JavaScript/yukon.admin.users.home.js"/>

<div id="tabs" class="section">
    <ul>
        <li><a href="#users-tab"><i:inline key=".users"/></a></li>
        <li><a href="#users-groups-tab"><i:inline key=".userGroups"/></a></li>
        <li><a href="#role-groups-tab"><i:inline key=".roleGroups"/></a></li>
    </ul>
    
    <div id="users-tab">
        <tags:pickerDialog type="userPicker" id="userPicker" linkType="none" container="users-tab" 
            immediateSelectMode="true" endEvent="yukon:admin:users-home:picker:complete"/>
    </div>
    
    <div id="users-groups-tab">
        <tags:pickerDialog type="userGroupPicker" id="userGroupPicker" linkType="none" container="users-groups-tab" 
            immediateSelectMode="true" endEvent="yukon:admin:users-home:picker:complete"/>
    </div>
    
    <div id="role-groups-tab">
        <tags:pickerDialog type="loginGroupPicker" id="roleGroupPicker" linkType="none" container="role-groups-tab" 
            immediateSelectMode="true" endEvent="yukon:admin:users-home:picker:complete"/>
    </div>
</div>

</cti:standardPage>