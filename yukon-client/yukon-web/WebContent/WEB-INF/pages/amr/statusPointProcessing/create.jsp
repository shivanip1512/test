<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="ext" tagdir="/WEB-INF/tags/ext" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="amr" page="statusPointMonitorCreator">

    <cti:standardMenu menuSelection="" />
    
    <cti:breadCrumbs>
        <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home" />
        <cti:crumbLink url="/spring/meter/start" title="Metering" />
        <cti:crumbLink><cti:msg2 key=".title" /></cti:crumbLink>
    </cti:breadCrumbs>
    
    <h2><cti:msg2 key=".title" /></h2>
    <br>
    
    <c:set var="statusPointMonitorId" value="${statusPointMonitorDto.statusPointMonitorId}"/>
    <c:if test="${not empty editError}">
        <div class="errorRed">${editError}</div>
    </c:if>

    <%-- MISC FORMS --%>
    <form id="cancelForm" action="/spring/meter/start" method="get"></form>
    
    <%-- UPDATE FORM --%>
    <form:form commandName="statusPointMonitorDto" id="basicInfoForm" action="/spring/amr/statusPointProcessing/doCreate" method="post">
    
        <form:hidden path="statusPointMonitorId"/>
                    
        <tags:sectionContainer2 nameKey=".setup">
            <tags:nameValueContainer2 style="border-collapse:separate;border-spacing:5px;">
            
                <%-- name --%>
                <tags:inputNameValue nameKey=".name" path="statusPointMonitorName" 
                                    size="50" 
                                    maxlength="50">
                </tags:inputNameValue>
                
                <%-- state group --%>
                <tags:nameValue2 nameKey=".selectStateGroup">
                    <select name="stateGroup">
                        <c:forEach items="${stateGroups}" var="stateGroupVar">
                            <option value="${stateGroupVar.stateGroupName}">${stateGroupVar.stateGroupName}</option>
                        </c:forEach>
                    </select>
                </tags:nameValue2>
                
            </tags:nameValueContainer2>
        </tags:sectionContainer2>
            
        <%-- create / cancel --%>
        <br>
        <tags:slowInput2 formId="basicInfoForm" key="create"/>
        <tags:slowInput2 formId="cancelForm" key="cancel"/>
        
    </form:form>
</cti:standardPage>
