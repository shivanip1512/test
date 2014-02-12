<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog"%>

<cti:standardPage module="adminSetup" page="operatorLogin.${mode}">

    <cti:url var="generatedPasswordUrl" value="/stars/operator/account/generatePassword" />
    
    <script>
        function generatePassword() {
            var dataHash = {userGroupName : jQuery('#userGroupName').val()};
            var userId = jQuery('#userId');
            if (userId.length && userId.val() != 0) {
                dataHash[ 'userId'] = userId.val();
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
            var yukUiAux = yukon.ui.aux;
            if(jQuery("#showPasswordCheckbox").is(":checked")) {
                yukUiAux.changeInputType('password1', 'text');
                yukUiAux.changeInputType('password2', 'text');
            }else{
                yukUiAux.changeInputType('password1', 'password');
                yukUiAux.changeInputType('password2', 'password');
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
            <cti:displayForPageEditModes modes="EDIT,CREATE">
                <tags:selectNameValue nameKey=".operatorGroup" items="${assignableGroups}" path="userGroupName" id="userGroupName"/>
            </cti:displayForPageEditModes>
            <cti:displayForPageEditModes modes="EDIT">
                <c:if test="${!isOperatorInOperatorUserGroup}">
                    <tags:nameValue2 nameKey=".currentGroup">
                        ${fn:escapeXml(operatorLogin.userGroupName)}
                    </tags:nameValue2>
                </c:if>
            </cti:displayForPageEditModes>
            <cti:displayForPageEditModes modes="VIEW">
                    <tags:nameValue2 nameKey=".operatorGroup">
                        ${fn:escapeXml(operatorLogin.userGroupName)}
                    </tags:nameValue2>
            </cti:displayForPageEditModes>
            <c:if test="${!add && currentUserId != operatorLogin.userId}">
                <tags:checkboxNameValue  checkBoxDescriptionNameKey=".loginEnabled.label" nameKey=".loginEnabled" path="loginEnabled"></tags:checkboxNameValue>
            </c:if>
            <c:if test="${currentUserId == operatorLogin.userId}">
                <form:hidden path="loginEnabled" value="true"/>
                <tags:nameValue2 nameKey=".loginEnabled">
                    <input id="enableCheckbox" type="checkbox" disabled="disabled" checked="checked"/>
                    <label for="enableCheckbox"> <i:inline key=".loginEnabled.label"/> </label>
                </tags:nameValue2>
            </c:if>
            <c:choose>
                <c:when test="${add}">
                    <form:hidden path="username" value="${username}"/>
                    <tags:nameValue2 nameKey=".username">
                        ${fn:escapeXml(operatorLogin.username)}
                    </tags:nameValue2>
                </c:when>
                <c:otherwise>
                   <tags:inputNameValue nameKey=".username" path="username" size="50" maxlength="60"/>
                </c:otherwise>
            </c:choose>
            
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
        
        <div class="page-action-area">
            <!-- Save/Update -->
            <cti:displayForPageEditModes modes="CREATE">
                <c:choose>
                    <c:when test="${add}">
                        <c:set var="name" value="add" />
                    </c:when>
                    <c:otherwise>
                        <c:set var="name" value="save" />
                    </c:otherwise>
                </c:choose>
                <cti:button nameKey="save" name="${name}" type="submit" classes="primary action"/>
            </cti:displayForPageEditModes>
            
            <cti:displayForPageEditModes modes="EDIT">
                <cti:button nameKey="save" name="update" type="submit" classes="primary action"/>
                <c:if test="${!isPrimaryOperator}">
                    <cti:button id="deleteButton" nameKey="delete" name="delete" type="submit" classes="delete"/>
                    <d:confirm on="#deleteButton" nameKey="confirmDelete"/>
                </c:if>
            </cti:displayForPageEditModes>
            
            <cti:displayForPageEditModes modes="VIEW">
                <cti:url var="operatorLoginEditUrl" value="${baseUrl}/edit">
                    <cti:param name="ecId" value="${ecId}"/>
                    <cti:param name="operatorLoginId" value="${operatorLogin.userId}"/>
                </cti:url>
                <cti:button nameKey="edit" icon="icon-pencil" href="${operatorLoginEditUrl}"/>
            </cti:displayForPageEditModes>

            <!-- Cancel -->
            <cti:url var="operatorLoginHomeUrl" value="${baseUrl}/home">
                <cti:param name="ecId" value="${ecId}"/>
            </cti:url>
            <cti:button nameKey="cancel" name="cancel" href="${operatorLoginHomeUrl}"/>
        </div>
     </form:form>
  
</cti:standardPage>