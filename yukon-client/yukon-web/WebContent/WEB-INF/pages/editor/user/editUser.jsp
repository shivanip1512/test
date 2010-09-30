<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage title="User Assignment for Load Management Visibility" module="userlm">
<cti:standardMenu/>
<cti:breadCrumbs>
    <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home"  />
    <cti:crumbLink url="/spring/editor/userGroupSelector" title="User/Group Editor"  />
	&gt; User Editor
</cti:breadCrumbs>


  <h2>${user.username}</h2>
  <tags:widgetContainer userId="${param.userId}" width="600px" identify="true">
  
    <br><br>
  	<div style="width: 600px;">
  		Use this permission for Load Management.  Select the objects to ALLOW access to for the user.
  	</div>
    <tags:widget bean="userPermissionEditorWidget" pickerType="lmDevicePicker"
        permission="LM_VISIBLE" allow="true"/>
    
    <br><br>
  	<div style="width: 600px;">
	    Use this permission for Cap Control.  Select the objects to DENY access to for the user.
  	</div>
    <tags:widget bean="userPermissionEditorWidget" pickerType="capControlAreaPicker"
        permission="PAO_VISIBLE" allow="false"/>

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