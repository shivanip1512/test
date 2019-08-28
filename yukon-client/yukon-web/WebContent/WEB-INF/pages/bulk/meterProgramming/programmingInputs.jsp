<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="yukon.web.modules.tools.bulk.meterProgramming">

    <tags:bulkActionContainer key="yukon.web.modules.tools.bulk.meterProgramming" deviceCollection="${deviceCollection}">
        
        <%-- ERROR MSG --%>
        <c:if test="${not empty errorMsg}">
            <div class="user-message error stacked">${errorMsg}</div>
        </c:if>
        
        <cti:url var="programUrl" value="/bulk/config/meterProgramming" />
        <form:form method="post" action="${programUrl}" modelAttribute="programmingConfiguration">
            <cti:csrfToken/>
            <cti:deviceCollection deviceCollection="${deviceCollection}" />
            
            <tags:nameValueContainer2>
                <cti:msg2 var="pleaseSelect" key="yukon.common.pleaseSelect"/>
                <tags:nameValue2 nameKey=".configurationType">
                     <tags:switchButton path="newConfiguration" offNameKey=".existingConfiguration" onNameKey=".newConfiguration" classes="js-programming-type" color="false"
                        toggleGroup="existingConfiguration" toggleAction="hide" toggleInverse="true" checked="${programmingConfiguration.newConfiguration}"/>
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".configuration" data-toggle-group="existingConfiguration">
                    <tags:selectWithItems id="selectedConfiguration" path="existingConfigurationGuid" items="${existingConfigurations}" itemValue="guid" itemLabel="name"
                        defaultItemLabel="${pleaseSelect}"/>
                </tags:nameValue2>
                <c:set var="newConfigurationDisplay" value="${programmingConfiguration.newConfiguration ? '' : 'dn'}"/>
                <tags:nameValue2 nameKey=".deviceType" rowClass="js-new-configuration ${newConfigurationDisplay}">
                    <tags:selectWithItems path="paoType" items="${availableTypes}" defaultItemLabel="${pleaseSelect}"/>
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".name" rowClass="js-new-configuration ${newConfigurationDisplay}">
                    <tags:input path="name"/>
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".configuration" rowClass="js-new-configuration ${newConfigurationDisplay}">
                    <tags:file buttonKey="yukon.common.upload"/>
                </tags:nameValue2>
            </tags:nameValueContainer2>
            
            <div class="page-action-area">
                <cti:button nameKey="send" classes="primary action js-action-submit" busy="true"/>
            </div>
            
        </form:form>

    </tags:bulkActionContainer>
       
</cti:msgScope>