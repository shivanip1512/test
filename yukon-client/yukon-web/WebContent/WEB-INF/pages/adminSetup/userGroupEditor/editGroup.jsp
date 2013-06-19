<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="adminSetup" page="groupPermissions">
    <tags:widgetContainer userGroupId="${userGroupId}" identify="true">
        
        <div class="column_12_12">
            
            <div class="column one">
                <div>
                    <span class="smallBoldLabel"><i:inline key=".instructions"/></span>
                    <span class="notes"><i:inline key=".lm.notes"/></span>
                </div>
        		<tags:widget bean="groupPermissionEditorWidget" pickerType="lmDevicePicker" permission="LM_VISIBLE" allow="true"/>
    		</div>
    		
            <div class="column two nogutter">
                <div>
                    <span class="smallBoldLabel"><i:inline key=".instructions"/></span>
                    <span class="notes"><i:inline key=".cc.notes"/></span>
                </div>
        		<tags:widget bean="groupPermissionEditorWidget" pickerType="capControlAreaPicker"
                    permission="PAO_VISIBLE" allow="false" permissionDescription="Cap Control Object Visibility"/>
            </div>
            
        </div>
    </tags:widgetContainer>

</cti:standardPage>