<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<%@ taglib tagdir="/WEB-INF/tags/i18n" prefix="i" %>

<cti:standardPage module="operator" page="changeLogin">
    
    <script type="text/javascript">

        function generatePassword() {
            var generatedPasswordCheckBox = $('generatePasswordCheckBox');

                new Ajax.Request('/spring/stars/operator/login/generatePassword', 
                    {onSuccess: function(response) 
                        {
                         var json = response.responseText.evalJSON();

                         var password1 = $('password1');
                         password1.value = json.generatedPassword;

                         var password2 = $('password2');
                         password2.value = json.generatedPassword;
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
    
    <tags:boxContainer2 key=".changeLogin">

        <form id="deleteForm" action="/spring/stars/operator/login/deleteLogin" method="post">
            <input type="hidden" name="accountId" value="${accountId}">
            <input type="hidden" name="energyCompanyId" value="${energyCompanyId}">
        </form>

        <form:form id="updateForm" action="/spring/stars/operator/login/updateLogin" commandName="changeLoginBackingBean">
            <input type="hidden" name="accountId" value="${accountId}">
            <input type="hidden" name="energyCompanyId" value="${energyCompanyId}">
            
            <tags:nameValueContainer2 nameColumnWidth="200px">
                <tags:nameValue2 nameKey=".customerGroup">
                    <form:select path="customerLoginGroupName">
                        <form:options items="${ecResidentialGroupNames}" />
                    </form:select>
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".loginEnabled"><tags:checkbox path="loginEnabled"/><i:inline key=".allowUserLogin"/></tags:nameValue2>
                
                <!-- Username Field -->
                <cti:checkRolesAndProperties value="OPERATOR_CONSUMER_INFO_ADMIN_CHANGE_LOGIN_USERNAME">
                    <tags:nameValue2 nameKey=".userName"><tags:input path="username"/></tags:nameValue2>
                </cti:checkRolesAndProperties>
                <cti:checkRolesAndProperties value="!OPERATOR_CONSUMER_INFO_ADMIN_CHANGE_LOGIN_USERNAME">
                    <tags:nameValue2 nameKey=".userName"><tags:input disabled="true" path="username"/></tags:nameValue2>
                </cti:checkRolesAndProperties>
                
                <!-- Password Fields -->
                <cti:checkRolesAndProperties value="OPERATOR_CONSUMER_INFO_ADMIN_CHANGE_LOGIN_PASSWORD">
                    <c:if test="${empty changeLoginBackingBean.username and supportsPasswordChange or
                             not empty changeLoginBackingBean.username and supportsPasswordSet}">
                        <tags:nameValue2 nameKey=".newPassword">
                            <tags:password path="password1"/>
                            <input type="button" onclick="generatePassword();" value="Generate Password" />
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".confirmPassword">
                            <tags:password path="password2"/>
                            <input id="showPasswordCheckbox" type="checkbox" onclick="showPassword()" /> Show Password
                        </tags:nameValue2>
                    </c:if>
                </cti:checkRolesAndProperties>
            </tags:nameValueContainer2>

            <br>
            <cti:checkRolesAndProperties value="OPERATOR_ALLOW_ACCOUNT_EDITING">
                <tags:slowInput2 myFormId="updateForm" key="save" width="80px"/>
            </cti:checkRolesAndProperties>
            <tags:reset/>
            <cti:checkRolesAndProperties value="OPERATOR_ALLOW_ACCOUNT_EDITING">
                <tags:slowInput2 myFormId="deleteForm" key="delete" width="80px"/>
            </cti:checkRolesAndProperties>
            
        </form:form>
    </tags:boxContainer2>

</cti:standardPage>