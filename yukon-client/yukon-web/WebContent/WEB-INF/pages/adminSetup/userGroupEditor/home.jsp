<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:standardPage module="adminSetup" page="userEditor">

    <cti:includeScript link="JQUERY_COOKIE"/>
    
    <cti:url value="/spring/adminSetup/user/view" var="userUrl"/>
    <cti:url value="/spring/adminSetup/userGroup/view" var="userGroupUrl"/>
    <cti:url value="/spring/adminSetup/roleGroup/view" var="roleGroupUrl"/>

    <script type="text/javascript">
        jQuery(function() {
            jQuery('#tabs').tabs({'cookie' : {}});
        });

        function editUser() { window.location = '${userUrl}?userId=' + $F('userId'); }
        function editUserGroup() { window.location = '${userGroupUrl}?userGroupId=' + $F('userGroupId'); }
        function editRoleGroup() { window.location = '${roleGroupUrl}?roleGroupId=' + $F('roleGroupId'); }
    </script>

    <div id="tabs">
        <ul>
            <li><a href="#usersTab"><i:inline key=".users"/></a></li>
            <li><a href="#userGroupsTab"><i:inline key=".userGroups"/></a></li>
            <li><a href="#roleGroupsTab"><i:inline key=".roleGroups"/></a></li>
        </ul>

        <div id="usersTab">
            <input type="hidden" id="userId" name="userId">
            <tags:boxContainer2 nameKey="userPicker" id="userPickerContainer"/>
            <tags:pickerDialog type="userPicker" id="userPicker" linkType="none" containerDiv="userPickerContainer_content" 
                immediateSelectMode="true" destinationFieldId="userId" endAction="editUser"/>
        </div>

        <div id="userGroupsTab">
            <input type="hidden" id="userGroupId" name="userGroupId">
            <tags:boxContainer2 nameKey="userGroupPicker" id="userGroupPickerContainer"/>
            <tags:pickerDialog type="userGroupPicker" id="userGroupPicker" linkType="none" containerDiv="userGroupPickerContainer_content" 
                immediateSelectMode="true" destinationFieldId="userGroupId" endAction="editUserGroup"/>
        </div>

        <div id="roleGroupsTab">
            <input type="hidden" id="roleGroupId" name="roleGroupId">
            <tags:boxContainer2 nameKey="roleGroupPicker" id="roleGroupPickerContainer"/>
            <tags:pickerDialog type="loginGroupPicker" id="roleGroupPicker" linkType="none" containerDiv="roleGroupPickerContainer_content" 
                immediateSelectMode="true" destinationFieldId="roleGroupId" endAction="editRoleGroup"/>
        </div>
    </div>

    <script type="text/javascript">
        Event.observe(window, 'load', function() {
        	roleGroupPicker.show(true);
        });
        Event.observe(window, 'load', function() {
            userGroupPicker.show(true);
        });
        Event.observe(window, 'load', function() {
            userPicker.show();
        });
    </script>
</cti:standardPage>