<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="tools" page="bulk.sendCommand">
    
    <tags:bulkActionContainer key="yukon.web.modules.tools.bulk.sendCommand" deviceCollection="${deviceCollection}">
        
        <%-- ERROR MSG --%>
        <c:if test="${not empty param.errorMsg}">
            <div class="error stacked">${param.errorMsg}</div>
            <c:set var="errorMsg" value="" scope="request"/>
        </c:if>
        
        <form id="collectionProcessingForm" action="<cti:url value="/group/commander/executeCollectionCommand"/>" method="post">
            <cti:csrfToken />
            <cti:deviceCollection deviceCollection="${deviceCollection}"/>
            
            <%-- SELECT COMMAND --%>
            <div class="stacked">
                <cti:msg var="selectCommandLabel" key="yukon.common.device.commander.commandSelector.selectCommand"/>
                <h4>${selectCommandLabel}:</h4>
                <amr:commandSelector selectName="commandSelectValue" fieldName="commandString" commands="${commands}" 
                    selectedCommandString="${param.commandString}"
                    selectedSelectValue="${param.commandSelectValue}"/>
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
                    <cti:button nameKey="execute" type="submit" classes="primary action" busy="true"/>
                </div>
            </c:if>
        </form>
        
    </tags:bulkActionContainer>
    
</cti:standardPage>