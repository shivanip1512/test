<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="adminSetup" page="operatorLogin.${mode}">

    <cti:url var="generatedPasswordUrl" value="/stars/operator/account/generatePassword" />
    
    <script>
        function generatePassword() {
            var dataHash = {userGroupName : $('userGroupName').value};
            var userId = $('userId');
            if (userId != null && userId.value != 0) {
                dataHash[ 'userId'] = userId.value;
            }

            jQuery.ajax({
              url: '${generatedPasswordUrl}',
              data: dataHash,
              success: function(data){
                   jQuery('#password1').val(data);
                   jQuery('#password2').val(data);
                   jQuery("#showPasswordCheckbox").attr('checked', true);
                   showPassword();
              }
           });
        };

        function showPassword() {
            if(jQuery("#showPasswordCheckbox").is(":checked")) {
                changeInputType('password1', 'text');
                changeInputType('password2', 'text');
            }else{
                changeInputType('password1', 'password');
                changeInputType('password2', 'password');
            }
    	}
    </script>
    
     <tags:setFormEditMode mode="${mode}"/>
     
    <cti:url value="${baseUrl}/update" var="action">
        <cti:param name="ecId" value="${ecId}"/>
    </cti:url>
    
     <form:form commandName="operatorLogin" action="${action}" name="operatorLoginForm">
            
        <tags:nameValueContainer2>
            <tags:hidden path="userId"/>
            <c:choose>
                <c:when test="${isOperatorInOperatorUserGroup}">
                    <tags:selectNameValue nameKey=".operatorGroup" items="${assignableGroups}" path="userGroupName"/>
                </c:when>
                <c:otherwise>
                    <tags:hidden path="userGroupName"/>
                    <tags:nameValue2 nameKey=".operatorGroup">
                        <spring:escapeBody htmlEscape="true">${operatorLogin.userGroupName}</spring:escapeBody>
                    </tags:nameValue2>
                </c:otherwise>
            </c:choose>
            <c:if test="${currentUserId != operatorLogin.userId}">
                <tags:checkboxNameValue  checkBoxDescriptionNameKey=".loginEnabled.label" nameKey=".loginEnabled" path="loginEnabled"></tags:checkboxNameValue>
            </c:if>

            <c:if test="${currentUserId == operatorLogin.userId}">
                <form:hidden path="loginEnabled" value="true"/>
                <tags:nameValue2 nameKey=".loginEnabled">
                    <input id="enableCheckbox" type="checkbox" disabled="disabled" checked="checked"/>
                    <label for="enableCheckbox"> <i:inline key=".loginEnabled.label"/> </label>
                </tags:nameValue2>
            </c:if>

            <tags:inputNameValue nameKey=".username" path="username" size="50" maxlength="60"/>
            
            <!-- Password Fields -->
            <cti:displayForPageEditModes modes="CREATE,EDIT">
                <c:if test="${supportsPasswordSet}">
                    <tags:nameValue2 nameKey=".password">
                        <tags:password path="password1" autocomplete="false" />
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".confirmPassword"> 
                        <tags:password path="password2" autocomplete="false" />
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey="defaults.blank" excludeColon="true">
                        <button type="button" onclick="generatePassword();"><i:inline key=".generatePassword" /></button>
                        <br>
                        <label>
                            <input id="showPasswordCheckbox" type="checkbox" onclick="showPassword()"/>
                            <i:inline key=".showPassword"/>
                        </label>
                    </tags:nameValue2>
                </c:if>
           </cti:displayForPageEditModes>

        </tags:nameValueContainer2>
        
        <div class="pageActionArea">
            <!-- Save/Update -->
            <cti:displayForPageEditModes modes="CREATE">
                <cti:button nameKey="save" name="save" type="submit"/>
            </cti:displayForPageEditModes>
            
            <cti:displayForPageEditModes modes="EDIT">
                <cti:button nameKey="save" name="update" type="submit"/>
            </cti:displayForPageEditModes>
            
            <cti:displayForPageEditModes modes="VIEW">
                <cti:url var="operatorLoginEditUrl" value="${baseUrl}/edit">
                    <cti:param name="ecId" value="${ecId}"/>
                    <cti:param name="operatorLoginId" value="${operatorLogin.userId}"/>
                </cti:url>
                <cti:button nameKey="edit" href="${operatorLoginEditUrl}"/>
            </cti:displayForPageEditModes>

            <cti:displayForPageEditModes modes="EDIT">
                <c:if test="${!isPrimaryOperator}">
                    <cti:button nameKey="delete" name="delete" styleClass="delete"/>
                    <tags:confirmDialog nameKey="confirmDelete" id="delete" submitName="delete" on="button.delete" />
                </c:if>
            </cti:displayForPageEditModes>

            <!-- Cancel -->
            <cti:url var="operatorLoginHomeUrl" value="${baseUrl}/home">
                <cti:param name="ecId" value="${ecId}"/>
            </cti:url>
            <cti:button nameKey="cancel" name="cancel" href="${operatorLoginHomeUrl}"/>
        </div>
     </form:form>
  
</cti:standardPage>