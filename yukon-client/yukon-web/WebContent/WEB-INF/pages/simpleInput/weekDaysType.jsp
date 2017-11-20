<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

 <cti:includeScript link="/resources/js/pages/yukon.admin.weekdays.js"/>
<c:set var="noDaysSelected" value="${true}"/>
<c:set var="daysString" value=""/>
<div class="button-group stacked">
    <c:forEach var="dayOfWeek" items="${inputType.optionList}">
        <c:set var="selected" value="${false}"/>
        <c:if test="${dayOfWeek.enabled}">
            <c:set var="selected" value="${true}"/>
        </c:if>
        <cti:msg2 var="weekDay" key="yukon.common.day.${dayOfWeek.value}.short"/> 
        <tags:check onclick="yukon.admin.weekdays.updateDays(this.id,this.name);" path="${path}" name="${param.id}" id="${dayOfWeek.value}" classes="js-week-day M0" value="${type}" label="${weekDay}" checked="${selected}"></tags:check>
    </c:forEach>
    <input <tags:attributeHelper name="id" value="${param.id}"/> type="hidden" size="30" name="${status.expression}" value="${status.value}" class="${inputClass}">
</div>
