`<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:standardPage module="adminSetup" page="schedules.${mode}">

    <tags:setFormEditMode mode="${mode}"/>
    
    <div class="listContainer">
        <tags:boxContainer2 nameKey="defaultThermostatSchedules">
        
            <c:forEach var="schedulableThermostatType" items="${schedulableThermostatTypes}">
                <a href="editDefaultThermostatSchedule?type=${schedulableThermostatType}&ecId=${ecId}"><i:inline key="${schedulableThermostatType.hardwareType}" /></a>
                <br>
            </c:forEach>

        </tags:boxContainer2>
    </div>
    
</cti:standardPage>