<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<%@ taglib tagdir="/WEB-INF/tags/i18n" prefix="i" %>

<cti:standardPage module="operator" page="changeLogin">
<tags:setFormEditMode mode="${mode}"/>

    <script type="text/javascript">

        function generatePassword() {
            var generatedPasswordCheckBox = $('generatePasswordCheckBox');

            new Ajax.Request('/spring/stars/operator/login/generatePassword', 
                {onSuccess: function(response) {
                     var generatedPassword = response.responseText;

                     var password1 = $('password1');
                     password1.value = generatedPassword;

                     var password2 = $('password2');
                     password2.value = generatedPassword;
                    }
                });

            // Check and show the password fields
            var showPasswordCheckbox = $('showPasswordCheckbox');
            showPasswordCheckbox.checked = true;
            showPassword();
        }

        /**
         * This function takes in an inputElement and an inputType and changes the
         * current node over to the desired type.
         *
         * String inputElement - The id of the element.
         * String inputType - The desired input type.
         */ 
        function changeInputType(inputElement, inputType){
            var input = document.getElementById(inputElement);
            var input2 = document.createElement('input');
            with (input2){
                id = input.id;
                name = input.name;
                value = input.value;
                type = inputType;
            }
            input.parentNode.replaceChild(input2,input);
        }

        function showPassword(){
        	var showPasswordCheckbox = $('showPasswordCheckbox');
            if (showPasswordCheckbox.checked) {
                changeInputType('password1', 'text');
                changeInputType('password2', 'text');
            } else {
                changeInputType('password1', 'password');
                changeInputType('password2', 'password');
            }
        } 

    </script>
    <div>
	    <tags:formElementContainer nameKey=".changeLogin" >
	
	        <form id="deleteForm" action="/spring/stars/operator/login/deleteLogin" method="post">
	            <input type="hidden" name="accountId" value="${accountId}">
	            <input type="hidden" name="loginMode" value="${loginMode}">
	        </form>
	
	        <form:form id="updateForm" action="/spring/stars/operator/login/updateLogin" commandName="changeLoginBackingBean">
	            <input type="hidden" name="accountId" value="${accountId}">
	            <input type="hidden" name="loginMode" value="${loginMode}">
	            
	            <tags:nameValueContainer2>
            		<tags:selectNameValue nameKey=".customerGroup" path="loginGroupName" items="${ecResidentialGroups}" itemValue="groupName" itemLabel="groupName"/>
	                <tags:nameValue2 nameKey=".loginEnabled"><tags:checkbox path="loginEnabled"/></tags:nameValue2>
	                
	                <!-- Username Field -->
	                <cti:checkRolesAndProperties value="OPERATOR_CONSUMER_INFO_ADMIN_CHANGE_LOGIN_USERNAME">
	                    <tags:nameValue2 nameKey=".userName"><tags:input path="username" autocomplete="false"/></tags:nameValue2>
	                </cti:checkRolesAndProperties>
	                <cti:checkRolesAndProperties value="!OPERATOR_CONSUMER_INFO_ADMIN_CHANGE_LOGIN_USERNAME">
	                    <tags:hidden path="username"/>
                        <tags:nameValue2 nameKey=".userName">
                            <spring:htmlEscape defaultHtmlEscape="true">
                                ${changeLoginBackingBean.username}
                            </spring:htmlEscape>
                        </tags:nameValue2>
	                </cti:checkRolesAndProperties>
	                
	                <!-- Password Fields -->
	                <cti:displayForPageEditModes modes="EDIT,CREATE">
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
	            
	            <cti:displayForPageEditModes modes="CREATE">
	            	<br>
	                <tags:slowInput2 formId="updateForm" key="create"/>
	            </cti:displayForPageEditModes>

                <cti:displayForPageEditModes modes="EDIT">
                    <br>
                    <tags:slowInput2 formId="updateForm" key="save"/>
                    <tags:slowInput2 formId="deleteForm" key="delete"/>
                </cti:displayForPageEditModes>

	            
	        </form:form>
	    </tags:formElementContainer>
    </div>

</cti:standardPage>