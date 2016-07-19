<%@ tag description="A component that looks like a select or textfield with a button on each side to switch
                     between options or change content." %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<%@ attribute name="intervals" required="true" type="java.lang.Object" description="Collection of TimeIntervals"%>
<%@ attribute name="path" description="The path for the select input." %>
<%@ attribute name="noneKey" description="i18n key for an option with 0 duration" %>
<%@ attribute name="toggleGroup" description="Used to setup a toggle group driven by a checkbox." %>
<%@ attribute name="format" description="DurationFormat to use for display. 
    Options: YMODHMS, DHMS, DHMS_REDUCED, DHMS_SHORT_REDUCED, DH, DH_ABBR, HMS, HM, H, M, S, S_ABBR, S_SHORT, HM_ABBR,
        HM_SHORT, MS_ABBR
    Default: DHMS_REDUCED" %>

<%@ attribute name="classes" description="CSS class names applied to the outer wrapper element." %>
<%@ attribute name="id" description="The html id attribute of the input." %>

<cti:msgScope paths=",yukon.web.common">
<cti:default var="classes" value=""/>
<cti:default var="id" value="${thisId}"/>
<cti:default var="noneKey" value="yukon.common.none"/>
<cti:default var="format" value="DHMS_REDUCED"/>

<cti:displayForPageEditModes modes="CREATE,EDIT">
<spring:bind path="${path}">
<c:if test="${status.error}">
    <c:set var="theInputClass" value="${pageScope.inputClass} error"/>
</c:if>
<form:select path="${path}" id="${id}" data-toggle-group="${toggleGroup}">
        
            <c:forEach var="interval" items="${intervals}">
                <form:option value="${interval.seconds}">
                    <c:if test="${interval.seconds == 0}">
                        <cti:msg2 key="${noneKey}"/>
                    </c:if>
                    <c:if test="${interval.seconds != 0}">
                        <cti:formatDuration type="${format}" value="${interval.duration}"/>
                    </c:if>
                </form:option>
            </c:forEach>
        
        </form:select>
        <c:if test="${status.error}"><br><form:errors path="${path}" cssClass="error"/></c:if>
    </label>
</spring:bind>
</cti:displayForPageEditModes>

<cti:displayForPageEditModes modes="VIEW">
    <span class="${classes}">
        <spring:bind path="${path}">
            <c:if test="${status.value == 0}">
                <cti:msg2 key="${noneKey}"/>
            </c:if>
            <c:if test="${status.value != 0}">
                <cti:formatDuration type="${format}" value="${status.value * 1000}"/>
            </c:if>
        </spring:bind>
    </span>
</cti:displayForPageEditModes>

</cti:msgScope>