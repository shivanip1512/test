<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:standardPage module="adminSetup" page="userGroupEditor">

    <script type="text/javascript">
    
    function editGroup() {
        window.location = "edit?typeToEdit=group&groupId=" + $F('groupId');
    }
    
    function editUser() {
        window.location = "edit?typeToEdit=user&userId=" + $F('userId');
    }
    
    </script>
    
    <cti:dataGrid cols="2" tableClasses="twoColumnLayout userGroupHomeLayout">
        <cti:dataGridCell>
            <input type="hidden" id="groupId" name="groupId">
            <tags:boxContainer2 nameKey="groupPicker" id="groupPickerContainer"/>
            <tags:pickerDialog type="loginGroupPicker" id="groupPicker" linkType="none" containerDiv="groupPickerContainer_content" 
                immediateSelectMode="true" destinationFieldId="groupId" endAction="editGroup"/>
        </cti:dataGridCell>
        
        <cti:dataGridCell>
            <input type="hidden" id="userId" name="userId">
            <tags:boxContainer2 nameKey="userPicker" id="userPickerContainer"/>
            <tags:pickerDialog type="userPicker" id="userPicker" linkType="none" containerDiv="userPickerContainer_content" 
                immediateSelectMode="true" destinationFieldId="userId" endAction="editUser"/>
        </cti:dataGridCell>
    </cti:dataGrid>
    
    
    <script type="text/javascript">
    Event.observe(window, 'load', function() {
        groupPicker.show();
    });
    Event.observe(window, 'load', function() {
        userPicker.show();
    });
    </script>

</cti:standardPage>