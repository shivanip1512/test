<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:standardPage module="adminSetup" page="userEditor">

    <cti:url value="/spring/adminSetup/user/view" var="userUrl"/>
    <cti:url value="/spring/adminSetup/userGroup/view" var="userGroupUrl"/>
    <cti:url value="/spring/adminSetup/roleGroup/view" var="roleGroupUrl"/>

    <script type="text/javascript">
        function editUser() { window.location = '${userUrl}?userId=' + $F('userId'); }
        function editUserGroup() { window.location = '${userGroupUrl}?userGroupId=' + $F('userGroupId'); }
        function editRoleGroup() { window.location = '${roleGroupUrl}?roleGroupId=' + $F('roleGroupId'); }
    </script>

    <cti:tabbedContentSelector>
        <cti:tabbedContentSelectorContent key=".users">
            <input type="hidden" id="userId" name="userId">
            <tags:boxContainer2 nameKey="userPicker" id="userPickerContainer"/>
            <tags:pickerDialog type="userPicker" id="userPicker" linkType="none" containerDiv="userPickerContainer_content" 
                immediateSelectMode="true" destinationFieldId="userId" endAction="editUser"/>
        </cti:tabbedContentSelectorContent>

        <cti:tabbedContentSelectorContent key=".userGroups">
            <input type="hidden" id="userGroupId" name="userGroupId">
            <tags:boxContainer2 nameKey="userGroupPicker" id="userGroupPickerContainer"/>
            <tags:pickerDialog type="userGroupPicker" id="userGroupPicker" linkType="none" containerDiv="userGroupPickerContainer_content" 
                immediateSelectMode="true" destinationFieldId="userGroupId" endAction="editUserGroup"/>
        </cti:tabbedContentSelectorContent>

        <cti:tabbedContentSelectorContent key=".roleGroups">
            <input type="hidden" id="roleGroupId" name="roleGroupId">
            <tags:boxContainer2 nameKey="roleGroupPicker" id="roleGroupPickerContainer"/>
            <tags:pickerDialog type="loginGroupPicker" id="roleGroupPicker" linkType="none" containerDiv="roleGroupPickerContainer_content" 
                immediateSelectMode="true" destinationFieldId="roleGroupId" endAction="editRoleGroup"/>
        </cti:tabbedContentSelectorContent>
    </cti:tabbedContentSelector>
    
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