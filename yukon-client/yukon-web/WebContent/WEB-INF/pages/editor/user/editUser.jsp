<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="adminSetup" page="userPermissions">

  <tags:widgetContainer userId="${param.userId}" identify="true">
  
    <cti:dataGrid cols="2" tableClasses="twoColumnLayout">
            
            <cti:dataGridCell>
                <table>
                    <tr>
                        <td class="instructionsLabel">
                            <span class="smallBoldLabel"><i:inline key=".instructions"/></span>
                        </td>
                        <td>
                            <span class="smallBoldLabel notes">
                                Use this permission for Load Management.<br>Select the objects to ALLOW access to for the user.
                            </span>
                        </td>
                    </tr>
                </table>
                <tags:widget bean="userPermissionEditorWidget" pickerType="lmDevicePicker" permission="LM_VISIBLE" allow="true"/>
            </cti:dataGridCell>
            
            <cti:dataGridCell>
                <table>
                    <tr>
                        <td class="instructionsLabel">
                            <span class="smallBoldLabel"><i:inline key=".instructions"/></span>
                        </td>
                        <td>
                            <span class="smallBoldLabel notes">
                                Use this permission for Cap Control.<br>Select the objects to DENY access to for the user.
                            </span>
                        </td>
                    </tr>
                </table>
                <tags:widget bean="userPermissionEditorWidget" pickerType="capControlAreaPicker" permission="PAO_VISIBLE" allow="false"/>
            </cti:dataGridCell>
            
        </cti:dataGrid>
    </tags:widgetContainer>
  
  <c:if test="${!empty authThrottleDto}">
    <br/>
    <div style="width: 600px;">
      <cti:url var="removeLoginWaitUrl" value="/spring/editor/user/removeLoginWait"/>
      <tags:boxContainer title="Failed Login Information for: ${user.username}" hideEnabled="false">
	    <form action="${removeLoginWaitUrl}" method="POST">
	    <table>
	        <tr>
	            <td align="right">
	                Last failed login time: 
	            </td>
	            <td align="left">
	                <cti:formatDate type="DATEHM" value="${authThrottleDto.lastFailedLoginTime}"/>
	            </td>
	        </tr>
            <tr>
                <td align="right">
                    Number of failed login attempts: 
                </td>
                <td align="left">
                    ${authThrottleDto.retryCount}
                </td>
            </tr>
            <tr>
                <td align="right">
                    Retry Wait End time: 
                </td>
                <td align="left">
                    <cti:formatDate type="DATEHM" value="${authThrottleDto.throttleEndtime}"/>
                </td>
            </tr>
            <tr>
                <td align="right">
                    Wait duration before next Login retry: 
                </td>
                <td align="left">
                    <cti:formatDuration type="DHMS" value="${(authThrottleDto.throttleDurationSeconds)*1000}"/>
                </td>
            </tr>
            <tr>
                <td align="center" colspan="2">
                    <br>
                    <input type="submit" value="Remove Login Wait" onclick="return confirmRemoveLoginWait()">
                </td>
            </tr>            
        </table>
        <input type="hidden" name="userId" value="${user.userID}"></input>
        </form>
	  </tags:boxContainer>
	</div>
  </c:if>
  
<script language="JavaScript">
    function confirmRemoveLoginWait() {
      return confirm("Do you wish to proceed with Remove Login Wait?");
    }
</script>

</cti:standardPage>