<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<table>
    <c:forEach var="rate" items="${field.inputTypes}" varStatus="loopStatus">
        <c:set var="rowClass" value=""/>
        <c:set var="isMidnight" value="${loopStatus.index == 0}"/>

        <%-- Hide TOU entries whose values are midnight (but whose times are not!) --%>
        <c:if test="${categoryEditBean.isScheduleRateHidden(field.fieldName, rate.field) && !isMidnight}">
            <c:set var="rowClass" value="class='dn'"/>
        </c:if>

        <tr ${rowClass} data-schedule-name="${field.fieldName}">
            <td>
                <i:inline key=".${categoryTemplate.categoryType}.${rate.field}"/>
            </td>
            <td id="${field.fieldName}_${rate.field}" data-field-name="${field.fieldName}" data-rate="${rate.field}">
                <c:choose>
                    <c:when test="${!isMidnight}">
                        <cti:displayForPageEditModes modes="VIEW">
                            ${categoryEditBean.scheduleInputs[(field.fieldName)].rateInputs[(rate.field)].time}
                        </cti:displayForPageEditModes>
                        <cti:displayForPageEditModes modes="CREATE,EDIT">
                            <tags:simpleInputType input="${rate.timeType}" path="scheduleInputs[${field.fieldName}].rateInputs[${rate.field}].time"/>
                            <spring:bind path="scheduleInputs[${field.fieldName}].rateInputs[${rate.field}].time">
                                <c:if test="${status.error}">
                                    <form:errors path="scheduleInputs[${field.fieldName}].rateInputs[${rate.field}].time" cssClass="error"/>
                                </c:if>
                            </spring:bind>
                        </cti:displayForPageEditModes>
                    </c:when>
                    <c:otherwise>
                        <cti:displayForPageEditModes modes="VIEW">
                            <i:inline key=".${categoryTemplate.categoryType}.midnightTime"/>
                        </cti:displayForPageEditModes>
                        <cti:displayForPageEditModes modes="CREATE,EDIT">
                            <select disabled>
                                <option><i:inline key=".${categoryTemplate.categoryType}.midnightTime"/></option>
                            </select>
                        </cti:displayForPageEditModes>
                    </c:otherwise>
                </c:choose>
            </td>
            <td>
                <i:inline key="yukon.web.modules.tools.configs.rate"/>
            </td>
            <td>
                <cti:displayForPageEditModes modes="VIEW">
                    ${categoryEditBean.scheduleInputs[(field.fieldName)].rateInputs[(rate.field)].rate}
                </cti:displayForPageEditModes>
                <cti:displayForPageEditModes modes="CREATE,EDIT">
                    <tags:simpleInputType input="${rate.rateType}" path="scheduleInputs[${field.fieldName}].rateInputs[${rate.field}].rate"/>
                </cti:displayForPageEditModes>
            </td>
            <td>
                <%-- This td intentionally left blank to store the Add Rate button in! --%>
            </td>
        </tr>
    </c:forEach>
</table>