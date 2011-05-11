<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="adminSetup" page="operatorLogin.${mode}">

    <script>
    function generatePassword() {
        new Ajax.Request('/spring/stars/operator/account/generatePassword', {
            onSuccess: function(response) {
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

    function showPassword() {
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
    
     <tags:setFormEditMode mode="${mode}"/>
     
    <cti:url value="${baseUrl}/update" var="action">
        <cti:param name="ecId" value="${ecId}"/>
    </cti:url>
    
     <form:form commandName="operatorLogin" action="${action}" name="operatorLoginForm">
            
        <tags:nameValueContainer2>
            <tags:hidden path="userId"/>
            <tags:selectNameValue nameKey=".operatorGroup" items="${assignableGroups}" path="loginGroupName"/>
            <tags:checkboxNameValue nameKey=".loginEnabled" path="loginEnabled"></tags:checkboxNameValue>
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
                        <input id="showPasswordCheckbox" type="checkbox" onclick="showPassword()" />
                        <i:inline key=".showPassword" />
                    </tags:nameValue2>
                </c:if>
           </cti:displayForPageEditModes>

        </tags:nameValueContainer2>
        
        <div class="pageActionArea">
            <!-- Save/Update -->
            <cti:displayForPageEditModes modes="CREATE">
                <cti:button key="save" name="create" type="submit"/>
            </cti:displayForPageEditModes>
            
            <cti:displayForPageEditModes modes="EDIT">
                <cti:button key="save" name="update" type="submit"/>
            </cti:displayForPageEditModes>
            
            <cti:displayForPageEditModes modes="VIEW">
                <cti:url var="operatorLoginEditUrl" value="${baseUrl}/edit">
                    <cti:param name="ecId" value="${ecId}"/>
                    <cti:param name="operatorLoginId" value="${operatorLogin.userId}"/>
                </cti:url>
                <cti:button key="edit" href="${operatorLoginEditUrl}"/>
            </cti:displayForPageEditModes>

            <cti:displayForPageEditModes modes="EDIT">
                <cti:button key="delete" name="delete" styleClass="delete"/>
                <tags:confirmDialog nameKey="confirmDelete" id="delete" submitName="delete" on="button.delete" />
            </cti:displayForPageEditModes>

            <!-- Cancel -->
            <cti:url var="operatorLoginHomeUrl" value="${baseUrl}/home">
                <cti:param name="ecId" value="${ecId}"/>
            </cti:url>
            <cti:button key="cancel" href="${operatorLoginHomeUrl}"/>
        </div>
     </form:form>
  
</cti:standardPage>