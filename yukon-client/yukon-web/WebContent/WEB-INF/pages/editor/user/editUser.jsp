<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="adminSetup" page="userPermissions">

  <tags:widgetContainer userId="${param.userId}" identify="true">
  
    <cti:dataGrid cols="2" tableClasses="twoColumnLayout">
            
            <cti:dataGridCell>
                <div>
                    <span class="smallBoldLabel"><i:inline key=".instructions"/></span>
                    <span class="notes"><i:inline key=".lm.notes"/></span>
                </div>
                <tags:widget bean="userPermissionEditor" pickerType="lmDevicePicker"
                    permission="LM_VISIBLE" allow="true"/>
            </cti:dataGridCell>
            
            <cti:dataGridCell>
                <div>
                    <span class="smallBoldLabel"><i:inline key=".instructions"/></span>
                    <span class="notes"><i:inline key=".cc.notes"/></span>
                </div>
                <tags:widget bean="userPermissionEditor" pickerType="capControlAreaPicker"
                    permission="PAO_VISIBLE" allow="false" permissionDescription="Cap Control Object Visibility"/>
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