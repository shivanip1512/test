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

<%@ attribute name="classes" description="CSS class names applied to the outer wrapper element." %>
<%@ attribute name="id" description="The html id attribute of the input." %>

<cti:msgScope paths=",yukon.web.common">
<cti:default var="classes" value=""/>
<cti:uniqueIdentifier prefix="stepper-" var="thisId"/>
<cti:default var="id" value="${thisId}"/>
<cti:default var="noneKey" value="defaults.none"/>

<cti:displayForPageEditModes modes="CREATE,EDIT">

    <label class="stepper ${classes}" >
        <cti:button classes="stepper-prev left" renderMode="buttonImage" icon="icon-resultset-previous-gray"
                data-toggle-group="${toggleGroup}"
        /><form:select cssClass="stepper-select middle" path="${path}" id="${id}" data-toggle-group="${toggleGroup}">
        
            <c:forEach var="interval" items="${intervals}">
                <form:option value="${interval.seconds}">
                    <c:if test="${interval.seconds == 0}">
                        <cti:msg2 key="${noneKey}"/>
                    </c:if>
                    <c:if test="${interval.seconds != 0}">
                        <cti:formatDuration type="DHMS_REDUCED" value="${interval.duration}"/>
                    </c:if>
                </form:option>
            </c:forEach>
        
        </form:select><cti:button classes="stepper-next right" renderMode="buttonImage" icon="icon-resultset-next-gray" 
                data-toggle-group="${toggleGroup}"/>
    </label>

</cti:displayForPageEditModes>

<cti:displayForPageEditModes modes="VIEW">
    <spring:bind path="${path}">
        <c:if test="${status.value == 0}">
            <cti:msg2 key="${noneKey}"/>
        </c:if>
        <c:if test="${status.value != 0}">
            <cti:formatDuration type="DHMS_REDUCED" value="${status.value * 1000}"/>
        </c:if>
    </spring:bind>
</cti:displayForPageEditModes>

</cti:msgScope>