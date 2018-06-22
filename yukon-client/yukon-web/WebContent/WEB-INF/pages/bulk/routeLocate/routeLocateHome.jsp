<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<cti:msgScope paths="yukon.web.modules.tools.bulk.routeLocateHome">

    <tags:bulkActionContainer key="yukon.web.modules.tools.bulk.routeLocateHome" deviceCollection="${deviceCollection}">
        
        <%-- ERROR MSG --%>
        <c:if test="${not empty errorMsg}">
            <div class="user-message error stacked">${errorMsg}</div>
        </c:if>
        
        <form id="executeLocateForm" action="<cti:url value="/bulk/routeLocate/executeRouteLocation"/>" method = "POST">
            <cti:csrfToken/>
        
            <%-- DEVICE COLLECTION --%>
            <cti:deviceCollection deviceCollection="${deviceCollection}"/>
            
            <%-- AUTO UPDATE OPTION --%>
            <label><input type="checkbox" name="autoUpdateRoute" <c:if test="${autoUpdateRoute}">checked</c:if>> <i:inline key=".autoUpdateRouteText"/></label><br>
            <br>
                    
            <%-- ROUTE OPTIONS --%>
            <select multiple name="routesSelect" id="routesSelect" size="12" style="min-width:400px;">
                <c:forEach var="routeOption" items="${routeOptions}">
                    <option value="${routeOption.key}">${fn:escapeXml(routeOption.value)}</option>
                </c:forEach>
            </select>
            
            <%-- LOCATE BUTTON --%>
            <cti:checkRolesAndProperties value="DEVICE_ACTIONS">
            <cti:checkRolesAndProperties value="LOCATE_ROUTE">
                <%-- SELECT COMMAND --%>
                <div class="stacked">
                    <cti:msg var="selectCommandLabel" key="yukon.common.device.commander.commandSelector.selectCommand"/>
                    <h4>${selectCommandLabel}:</h4>
                    <amr:commandSelector id="commandSelectId" selectName="commandSelectValue" fieldName="commandString" commands="${commands}" 
                        selectedCommandString="${fn:escapeXml(commandString)}"
                        selectedSelectValue="${commandSelectValue}"/>
                    <input type="hidden" id="commandFromDropdown" name="commandFromDropdown"/>
                </div>
            
                <div class="page-action-area">
                    <cti:button nameKey="locateRoute" classes="primary action js-locate-route" busy="true"/>
                </div>
            </cti:checkRolesAndProperties>
            </cti:checkRolesAndProperties>
            
        </form>
    
    </tags:bulkActionContainer>
        
</cti:msgScope>