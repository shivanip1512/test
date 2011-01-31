<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:standardPage module="adminSetup" page="userGroupEditor">

<style>
    span.pickerLabel {
        vertical-align: middle;
        padding-right: 5px;
    }
    
    span.radio {
        vertical-align: middle;
    }
</style>

<script type="text/javascript">

YEvent.observeSelectorClick('input[value=group]', function(event) {
    $('groupPickerContainer').show();
    $('userPickerContainer').hide();
});

YEvent.observeSelectorClick('input[value=user]', function(event) {
    $('userPickerContainer').show();
    $('groupPickerContainer').hide();
});

</script>

    <form action="/spring/adminSetup/userGroupEditor/edit">
        
        <tags:formElementContainer nameKey="selectUserOrGroup">
            
            <div>
                <span class="radio"><input type="radio" name="typeToEdit" value="group" checked="checked"></span>
                <input type="hidden" id="groupId" name="groupId">
                <span class="pickerLabel"><i:inline key=".group"/></span>
                <span id="groupPickerContainer">
                    <tags:pickerDialog type="loginGroupPicker" id="groupPicker" selectionProperty="groupName"
                        destinationFieldId="groupId" linkType="selection"/>
                </span>
            </div>
            <div>
                <span class="radio"><input type="radio" name="typeToEdit" value="user"></span>
                <input type="hidden" id="userId" name="userId">
                <span class="pickerLabel"><i:inline key=".user"/></span>
                <span id="userPickerContainer" style="display: none;">
                    <tags:pickerDialog type="userPicker" id="userPicker" selectionProperty="userName"
                        destinationFieldId="userId" linkType="selection"/>
                </span>
            </div>
            
        </tags:formElementContainer>
        
        <div class="pageActionArea">
            <cti:button key="submit" type="submit"/>
        </div>
    </form>

</cti:standardPage>