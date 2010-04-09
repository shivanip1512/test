<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<%@ taglib tagdir="/WEB-INF/tags/i18n" prefix="i" %>

<cti:standardPage module="operator" page="changeLogin" mode="${mode}">
    
    <script type="text/javascript">

        function generatePassword() {
            var generatedPasswordCheckBox = $('generatePasswordCheckBox');

                new Ajax.Request('/spring/stars/operator/login/generatePassword', 
                    {onSuccess: function(response) 
                        {
                         var generatedPassword = response.responseText;

                         var password1 = $('password1');
                         password1.value = generatedPassword;

                         var password2 = $('password2');
                         password2.value = generatedPassword;
                        }
                    });
                
        }

        function showPassword() {
            var showPasswordCheckbox = $('showPasswordCheckbox');
            if (showPasswordCheckbox.checked) {
            	$('password1').type = 'text';
            	$('password2').type = 'text';
            } else {
            	$('password1').type = 'password';
            	$('password2').type = 'password';
            }
        }
        
    </script>
    <div style="width: 50%;">
	    <tags:sectionContainer2 key=".changeLogin" >
	
	        <form id="deleteForm" action="/spring/stars/operator/login/deleteLogin" method="post">
	            <input type="hidden" name="accountId" value="${accountId}">
	            <input type="hidden" name="energyCompanyId" value="${energyCompanyId}">
	            <input type="hidden" name="loginMode" value="${loginMode}">
	        </form>
	
	        <form:form id="updateForm" action="/spring/stars/operator/login/updateLogin" commandName="changeLoginBackingBean">
	            <input type="hidden" name="accountId" value="${accountId}">
	            <input type="hidden" name="energyCompanyId" value="${energyCompanyId}">
	            <input type="hidden" name="loginMode" value="${loginMode}">
	            
	            <tags:nameValueContainer2>
            		<tags:selectNameValue nameKey=".customerGroup" path="customerLoginGroupName" items="${ecResidentialGroups}" itemValue="groupName" itemLabel="groupName"/>
	                <tags:nameValue2 nameKey=".loginEnabled"><tags:checkbox path="loginEnabled"/><i:inline key=".allowUserLogin"/></tags:nameValue2>
	                
	                <!-- Username Field -->
	                <cti:checkRolesAndProperties value="OPERATOR_CONSUMER_INFO_ADMIN_CHANGE_LOGIN_USERNAME">
	                    <tags:nameValue2 nameKey=".userName"><tags:input path="username" autocomplete="false"/></tags:nameValue2>
	                </cti:checkRolesAndProperties>
	                <cti:checkRolesAndProperties value="!OPERATOR_CONSUMER_INFO_ADMIN_CHANGE_LOGIN_USERNAME">
	                    <tags:nameValue2 nameKey=".userName"><tags:input readonly="true" path="username"/></tags:nameValue2>
	                </cti:checkRolesAndProperties>
	                
	                <!-- Password Fields -->
	                <cti:displayForPageEditModes modes="EDIT">
	                <cti:checkRolesAndProperties value="OPERATOR_CONSUMER_INFO_ADMIN_CHANGE_LOGIN_PASSWORD">
	                    <c:if test="${supportsPasswordSet}">
	                        <tags:nameValue2 nameKey=".newPassword">
	                            <tags:password path="password1" autocomplete="false"/>
	                            <input type="button" onclick="generatePassword();" value="Generate Password" />
	                        </tags:nameValue2>
	                        <tags:nameValue2 nameKey=".confirmPassword">
	                            <tags:password path="password2" autocomplete="false"/>
	                            <input id="showPasswordCheckbox" type="checkbox" onclick="showPassword()" /> <i:inline key=".showPassword"/>
	                        </tags:nameValue2>
	                    </c:if>
	                </cti:checkRolesAndProperties>
	                </cti:displayForPageEditModes>
	                
	            </tags:nameValueContainer2>
	            
	            <cti:displayForPageEditModes modes="EDIT">
	            	<br>
	                <tags:slowInput2 myFormId="updateForm" key="save" width="80px"/>
	            	<tags:reset/>
	                <tags:slowInput2 myFormId="deleteForm" key="delete" width="80px"/>
	            </cti:displayForPageEditModes>
	            
	        </form:form>
	    </tags:sectionContainer2>
    </div>

</cti:standardPage>