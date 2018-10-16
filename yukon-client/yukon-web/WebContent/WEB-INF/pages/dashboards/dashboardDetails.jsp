<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:msgScope paths="modules.dashboard">

    <tags:setFormEditMode mode="${mode}"/>
    
    <cti:url var="action" value="/dashboards/saveDetails"/>
    <cti:displayForPageEditModes modes="CREATE">
        <cti:url value="/dashboards/create" var="action"/>
    </cti:displayForPageEditModes>
    
    <form:form id="dashboard-details" modelAttribute="dashboard" action="${action}" method="POST">
     <cti:csrfToken/>
     
       <cti:displayForPageEditModes modes="EDIT">
            <form:hidden path="dashboardId"/>
       </cti:displayForPageEditModes>
              
        <tags:nameValueContainer2>
            <tags:nameValue2 nameKey=".name">
                <tags:input path="name" size="30" maxlength="100"/>
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".description">
                    <tags:textarea path="description" cols="31" rows="5"/>
            </tags:nameValue2>
            <cti:displayForPageEditModes modes="CREATE">
                <tags:nameValue2 nameKey=".template">
                    <cti:msg2 key=".blankDashboard" var="blankDashboard"/>
                    <tags:selectWithItems path="dashboardId" items="${dashboards}" itemValue="dashboardId" itemLabel="name" defaultItemValue="0" defaultItemLabel="${blankDashboard}"/>
                </tags:nameValue2>
            </cti:displayForPageEditModes>
            <tags:nameValue2 nameKey=".visibility">
                <form:select path="visibility">
                    <c:forEach var="visOption" items="${visibilityOptions}">
                        <c:if test="${visOption != 'SYSTEM'}">
                            <form:option value="${visOption}"><cti:msg2 key=".visibility.${visOption}"/></form:option>
                        </c:if>
                    </c:forEach>
                </form:select>
            </tags:nameValue2>
        </tags:nameValueContainer2>
    
    </form:form>

</cti:msgScope>