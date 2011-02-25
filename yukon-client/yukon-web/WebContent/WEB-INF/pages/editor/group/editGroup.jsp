<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="adminSetup" page="groupPermissions">

    <tags:widgetContainer groupId="${param.groupId}" identify="true">
        
        <cti:dataGrid cols="2" tableClasses="twoColumnLayout">
            
            <cti:dataGridCell>
                <table>
                    <tr>
                        <td class="instructionsLabel">
                            <span class="smallBoldLabel"><i:inline key=".instructions"/></span>
                        </td>
                        <td>
                    	  	<span class="smallBoldLabel notes">
                    	  		Use this permission for Load Management.<br>Select the objects to ALLOW access to for the group.
                    	  	</span>
                        </td>
                    </tr>
                </table>
        		<tags:widget bean="groupPermissionEditorWidget" pickerType="lmDevicePicker" permission="LM_VISIBLE" allow="true"/>
    		</cti:dataGridCell>
    		
            <cti:dataGridCell>
                <table>
                    <tr>
                        <td class="instructionsLabel">
                            <span class="smallBoldLabel"><i:inline key=".instructions"/></span>
                        </td>
                        <td>
                            <span class="smallBoldLabel notes">
                                Use this permission for Cap Control.<br>Select the objects to DENY access to for the group.
                            </span>
                        </td>
                    </tr>
                </table>
        		<tags:widget bean="groupPermissionEditorWidget" pickerType="capControlAreaPicker" permission="PAO_VISIBLE" allow="false"/>
            </cti:dataGridCell>
            
        </cti:dataGrid>
    </tags:widgetContainer>

</cti:standardPage>