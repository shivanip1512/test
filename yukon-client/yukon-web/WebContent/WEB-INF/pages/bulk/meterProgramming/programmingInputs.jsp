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
        
        <cti:url var="programUrl" value="/bulk/meterProgramming/send" />
        <form:form method="post" action="${programUrl}" modelAttribute="programModel">
            <cti:csrfToken/>
            <cti:deviceCollection deviceCollection="${deviceCollection}" />
            
            <tags:nameValueContainer2>
                <cti:msg2 var="pleaseSelect" key="yukon.common.pleaseSelect"/>
                <tags:nameValue2 nameKey=".meterProgramType">
                     <tags:switchButton path="newProgram" offNameKey=".existingMeterProgram" onNameKey=".newMeterProgram" classes="js-programming-type" color="false"
                        toggleGroup="existingMeterProgram" toggleAction="hide" toggleInverse="true" checked="${programModel.newProgram}"/>
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".meterProgram" data-toggle-group="existingMeterProgram">
                    <tags:selectWithItems id="selectedProgram" path="existingProgramGuid" items="${existingConfigurations}" itemValue="guid" itemLabel="name"
                        defaultItemLabel="${pleaseSelect}"/>
                </tags:nameValue2>
                <c:set var="newProgramDisplay" value="${programModel.newProgram ? '' : 'dn'}"/>
                <tags:nameValue2 nameKey=".deviceType" rowClass="js-new-program ${newProgramDisplay}">
                    <tags:selectWithItems path="paoType" items="${availableTypes}" defaultItemLabel="${pleaseSelect}"/>
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".name" rowClass="js-new-program ${newProgramDisplay}">
                    <tags:input path="name"/>
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".password" rowClass="js-new-program ${newProgramDisplay}">
                    <tags:password path="password" showPassword="true" includeShowHideButton="true"/>
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".meterProgram" rowClass="js-new-program ${newProgramDisplay}">
                    <tags:file buttonKey="yukon.common.upload"/>
                </tags:nameValue2>
            </tags:nameValueContainer2>
            
            <div class="page-action-area">
                <cti:button nameKey="send" classes="primary action js-action-submit" busy="true"/>
            </div>
            
        </form:form>

    </tags:bulkActionContainer>
       
</cti:msgScope>