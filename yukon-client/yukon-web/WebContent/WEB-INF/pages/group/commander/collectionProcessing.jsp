<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="yukon.web.modules.tools.bulk.sendCommand,yukon.common">
    
    <tags:bulkActionContainer key="yukon.web.modules.tools.bulk.sendCommand" deviceCollection="${deviceCollection}">
    
        <%-- ERROR MSG --%>
        <c:if test="${not empty errorMsg}">
            <div class="user-message error stacked">${errorMsg}</div>
        </c:if>
        
        <form id="collectionProcessingForm" action="<cti:url value="/group/commander/executeCollectionCommand"/>" method="post">
            <cti:csrfToken />
            <cti:deviceCollection deviceCollection="${deviceCollection}"/>
            
            <%-- SELECT COMMAND --%>
            <div class="stacked">
                <cti:msg var="selectCommandLabel" key="yukon.common.device.commander.commandSelector.selectCommand"/>
                <h4>${selectCommandLabel}:</h4>
                <amr:commandSelector id="commandSelectId" selectName="commandSelectValue" fieldName="commandString" commands="${commands}" 
                    selectedCommandString="${param.commandString}"
                    selectedSelectValue="${param.commandSelectValue}"/>
                <input type="hidden" id="commandFromDropdown" name="commandFromDropdown"/>
            </div>
            <c:set var="manualCommands" value="false" />
            <cti:checkRolesAndProperties value="EXECUTE_MANUAL_COMMAND">
                <c:set var="manualCommands" value="true"/>
            </cti:checkRolesAndProperties>

            <c:if test="${not empty commands or manualCommands}">
                <c:if test="${!isSmtpConfigured}">
                    <tags:nameValueContainer2 tableClass="with-form-controls name-collapse">
                        <tags:nameValue2 nameKey="yukon.common.email.send">
                            <tags:switchButton offNameKey=".no.label" onNameKey=".yes.label" name="sendEmail" classes="fn"
                                toggleGroup="email-address"/>
                            <input type="text" name="emailAddress" value="${email}" size="40" disabled
                                data-toggle-group="email-address">
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                </c:if>
                <div class="page-action-area">
                    <cti:button nameKey="execute" classes="primary action js-execute-command" busy="true"/>
                </div>
            </c:if>
        </form>
        
    </tags:bulkActionContainer>
        
</cti:msgScope>