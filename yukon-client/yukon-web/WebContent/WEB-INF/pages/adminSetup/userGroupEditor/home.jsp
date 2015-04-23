<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="adminSetup" page="userEditor">
<cti:includeScript link="/JavaScript/yukon.admin.users.home.js"/>

<div id="page-buttons">
    <cm:dropdown icon="icon-plus-green" key="yukon.common.create" type="button" menuClasses="no-icons">
        <cm:dropdownOption key="yukon.common.user" data-popup=".js-new-user-dialog"/>
        <cm:dropdownOption key="yukon.common.user.group"/>
        <cm:dropdownOption key="yukon.common.role.group"/>
    </cm:dropdown>
</div>

<div class="dn js-new-user-dialog"
    data-dialog
    data-width="580"
    data-title="<cti:msg2 key=".user.new"/>"
    data-event="yukon:admin:user:create"
    data-ok-text="<cti:msg2 key="yukon.common.save"/>"
    data-url="<cti:url value="/adminSetup/new-user-dialog"/>"></div>

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

<cti:toJson id="password-types" object="${passwordTypes}"/>

</cti:standardPage>