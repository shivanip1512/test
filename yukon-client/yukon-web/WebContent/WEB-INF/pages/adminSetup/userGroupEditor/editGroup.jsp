<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="adminSetup" page="groupPermissions">
    <tags:widgetContainer userGroupId="${userGroupId}" identify="true">
        
        <div class="column-12-12">
            
            <div class="column one">
                <div class="stacked">
                    <span class="strong-label-small"><i:inline key=".instructions"/></span>
                    <span class="notes"><i:inline key=".lm.notes"/></span>
                </div>
                <tags:widget bean="groupPermissionEditorWidget" pickerType="lmDevicePicker" permission="LM_VISIBLE" allow="true" container="section"/>
            </div>
            
            <div class="column two nogutter">
                <div class="stacked">
                    <span class="strong-label-small"><i:inline key=".instructions"/></span>
                    <span class="notes"><i:inline key=".cc.notes"/></span>
                </div>
                <tags:widget bean="groupPermissionEditorWidget" pickerType="capControlAreaPicker"
                    permission="PAO_VISIBLE" allow="false" permissionDescription="Cap Control Object Visibility" container="section"/>
            </div>
            
        </div>
    </tags:widgetContainer>

</cti:standardPage>