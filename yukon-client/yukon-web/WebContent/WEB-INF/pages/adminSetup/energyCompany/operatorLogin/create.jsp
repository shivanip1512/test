<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="adminSetup" page="operatorLogin.${mode}">

    <cti:url var="generatedPasswordUrl" value="/login/generate-password" />
    
    <script>
        
        function generatePassword() {
            var dataHash = {userGroupName : $('#userGroupName').val()};
            var userId = $('#userId');
            if (userId.length && userId.val() != 0) {
                dataHash[ 'userId'] = userId.val();
            }
            
            $.ajax({
                url: '${generatedPasswordUrl}',
                data: dataHash
            }).done(function (data, textStatus, jqXHR) {
                 $('.js-password-1').val(data);
                 $('.js-password-2').val(data);
                 $(".js-show-password-checkbox").prop('checked', true);
                 showPassword();
            });
        };
        
        function showPassword() {
            if ($(".js-show-password-checkbox").is(":checked")) {
                $('.js-password-1, .js-password-2').attr('type', 'text');
            } else {
                $('.js-password-1, .js-password-2').attr('type', 'password');
            }
    	}
    </script>
    
     <tags:setFormEditMode mode="${mode}"/>
     
    <cti:url value="${baseUrl}/update" var="action">
        <cti:param name="ecId" value="${ecId}"/>
    </cti:url>
    
     <form:form modelAttribute="operatorLogin" action="${action}" name="operatorLoginForm">
        <cti:csrfToken/>
        <tags:nameValueContainer2 tableClass="with-form-controls">
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
                        <tags:password path="password1" cssClass="js-password-1" autocomplete="off" />
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".confirmPassword"> 
                        <tags:password path="password2" cssClass="js-password-2" autocomplete="off" />
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey="yukon.common.blank" excludeColon="true">
                        <cti:msg2 key=".generatePassword" var="pwordButton"/>
                        <cti:button label="${pwordButton}" onclick="generatePassword();"/>
                        <label class="fl">
                            <input class="js-show-password-checkbox" type="checkbox" onclick="showPassword()"/>
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