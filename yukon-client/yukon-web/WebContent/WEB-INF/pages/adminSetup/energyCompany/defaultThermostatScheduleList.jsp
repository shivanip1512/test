`<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:url var="delete" value="/WebConfig/yukon/Icons/delete.gif"/>
<cti:url var="deleteOver" value="/WebConfig/yukon/Icons/delete_over.gif"/>

<cti:standardPage module="adminSetup" page="schedules.${mode}">

    <cti:includeCss link="/WebConfig/yukon/styles/admin/energyCompany.css"/>
    <tags:setFormEditMode mode="${mode}"/>
    
    <c:forEach var="schedulableThermostatType" items="${schedulableThermostatTypes}">
        <a href="editDefaultThermostatSchedule?type=${schedulableThermostatType}&ecId=${ecId}"><i:inline key="${schedulableThermostatType.hardwareType.displayKey}" /></a>
        <br>
    </c:forEach>
    
</cti:standardPage>