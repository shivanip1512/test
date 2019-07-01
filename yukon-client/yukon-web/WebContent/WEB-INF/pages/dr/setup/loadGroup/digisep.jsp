<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ page trimDirectiveWhitespaces="true" %>

<tags:sectionContainer2 nameKey="digisepDeviceClass">
    <tags:nameValueContainer2>
        <tags:nameValue2 nameKey=".digisepDeviceClass.title">
            <cti:displayForPageEditModes modes="EDIT,CREATE">
                <cti:msg2 var="deviceClass" key=".digisepDeviceClass.title"/>
                <tags:selectWithItems items="${deviceClassList}" path="deviceClassSet" dataPlaceholder="${deviceClass}" inputClass="js-chosen"/>
            </cti:displayForPageEditModes>
            <cti:displayForPageEditModes modes="VIEW">
                <c:forEach items="${loadGroup.deviceClassSet}" var="deviceClass" varStatus="status">
                    <i:inline key="${deviceClass}"/>
                    <c:if test="${not status.last}">
                        <i:inline key="yukon.common.comma"/>&nbsp
                    </c:if>
                </c:forEach>
            </cti:displayForPageEditModes>
        </tags:nameValue2>
    </tags:nameValueContainer2>
</tags:sectionContainer2>

<tags:sectionContainer2 nameKey="digisepEnrollment">
    <tags:nameValueContainer2>
        <tags:nameValue2 nameKey=".digisep.utilityEnrollmentGroup">
            <tags:input path="utilityEnrollmentGroup"/>
        </tags:nameValue2>
    </tags:nameValueContainer2>
</tags:sectionContainer2>

<tags:sectionContainer2 nameKey="digisepTiming">
    <tags:nameValueContainer2>
        <tags:nameValue2 nameKey=".digisep.rampIn">
            <tags:input path="rampInMinutes"/>
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".digisep.rampOut">
            <tags:input path="rampOutMinutes"/>
        </tags:nameValue2>
    </tags:nameValueContainer2>
</tags:sectionContainer2>

<script>
    $(".js-chosen").chosen({width: "350px"});
</script>
