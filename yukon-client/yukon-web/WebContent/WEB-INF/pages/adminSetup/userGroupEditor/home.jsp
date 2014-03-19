<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:standardPage module="adminSetup" page="userEditor">

    <cti:includeScript link="JQUERY_COOKIE"/>
    
    <cti:url value="/adminSetup/user/view" var="userUrl"/>
    <cti:url value="/adminSetup/userGroup/view" var="userGroupUrl"/>
    <cti:url value="/adminSetup/roleGroup/view" var="roleGroupUrl"/>

    <script type="text/javascript">
        $(function() {
            window['userPicker'].show.call(window['userPicker']);
            window['userGroupPicker'].show.call(window['userGroupPicker']);
            window['roleGroupPicker'].show.call(window['roleGroupPicker']);
            $('#tabs').tabs();
        });
        function editUser () {
            window.location.href = '${userUrl}?userId=' + $('#userId').val();
        }
        function editUserGroup () {
            window.location.href = '${userGroupUrl}?userGroupId=' + $('#userGroupId').val();
        }
        function editRoleGroup () {
            window.location.href = '${roleGroupUrl}?roleGroupId=' + $('#roleGroupId').val();
        }
    </script>

    <div id="tabs" class="section">
        <ul>
            <!-- Panel IDs should match the pickerDialog IDs minus the word 'Picker' -->
            <li><a href="#user"><i:inline key=".users"/></a></li>
            <li><a href="#userGroup"><i:inline key=".userGroups"/></a></li>
            <li><a href="#roleGroup"><i:inline key=".roleGroups"/></a></li>
        </ul>

        <div id="user">
            <input type="hidden" id="userId" name="userId">
            <tags:sectionContainer2 nameKey="userPicker" id="userPickerContainer"/>
            <tags:pickerDialog type="userPicker" id="userPicker" linkType="none" containerDiv="userPickerContainer_content" 
                immediateSelectMode="true" destinationFieldId="userId" endAction="editUser"/>
        </div>

        <div id="userGroup">
            <input type="hidden" id="userGroupId" name="userGroupId">
            <tags:sectionContainer2 nameKey="userGroupPicker" id="userGroupPickerContainer"/>
            <tags:pickerDialog type="userGroupPicker" id="userGroupPicker" linkType="none" containerDiv="userGroupPickerContainer_content" 
                immediateSelectMode="true" destinationFieldId="userGroupId" endAction="editUserGroup"/>
        </div>

        <div id="roleGroup">
            <input type="hidden" id="roleGroupId" name="roleGroupId">
            <tags:sectionContainer2 nameKey="roleGroupPicker" id="roleGroupPickerContainer"/>
            <tags:pickerDialog type="loginGroupPicker" id="roleGroupPicker" linkType="none" containerDiv="roleGroupPickerContainer_content" 
                immediateSelectMode="true" destinationFieldId="roleGroupId" endAction="editRoleGroup"/>
        </div>
    </div>
</cti:standardPage>