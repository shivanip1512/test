<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"%>

<cti:standardPage module="consumer" page="enrollment">
    <cti:includeCss link="/WebConfig/yukon/styles/consumer/enrollment.css"/>
    <cti:standardMenu/>
    
    <c:set var="savingsDescriptionIconDisplayed" value="false"/>
    <c:set var="controlPercentDescriptionIconDisplayed" value="false"/>
    <c:set var="environmentDescriptionIconDisplayed" value="false"/>

    <h3><cti:msg key="yukon.dr.consumer.enrollment.header" /></h3>
    <br>

    <div id="enrollmentNote">
        <cti:msg key="yukon.dr.consumer.enrollment.enrolledProgramsMsg"/>
    </div>

    <table id="enrollmentTable" cellspacing="0" cellpadding="0">
    <c:forEach var="enrollment" items="${enrollments}">
        <c:if test="${enrollment.applianceCategory.consumerSelectable}">
            <c:set var="enrollmentPrograms" value="${enrollment.enrollmentPrograms}"/>
            <c:set var="applianceCategoryId"
                value="${enrollment.applianceCategory.applianceCategoryId}"/>
            <c:set var="numRows" value="${fn:length(enrollmentPrograms) + 1}"/>
            <c:if test="${!empty enrollment.applianceCategory.description}">
                <c:set var="numRows" value="${numRows + 1}"/>
            </c:if>
            <c:forEach var="enrollmentProgram" items="${enrollmentPrograms}">
                <c:if test="${!empty fn:trim(enrollmentProgram.program.description)}">
                    <c:set var="numRows" value="${numRows + 1}"/>
                </c:if>
            </c:forEach>
            <tr class="applianceCategory">
                <td class="applianceCategoryIcon" rowspan="${numRows}">
                    <img src="../../../WebConfig/${enrollment.applianceLogo}">
                </td>
                <td colspan="5">
                    <spring:escapeBody htmlEscape="true">${enrollment.applianceCategory.categoryLabel}</spring:escapeBody>
                </td>
            </tr>
            <c:if test="${!empty enrollment.applianceCategory.description}">
                <tr class="applianceCategoryDescription">
                    <td colspan="5">
                        <spring:escapeBody htmlEscape="true">
                            ${enrollment.applianceCategory.description}
                        </spring:escapeBody>
                    </td>
                </tr>
            </c:if>
            <c:forEach var="enrollmentProgram" items="${enrollmentPrograms}">
                <c:set var="assignedProgramId" value="${enrollmentProgram.program.programId}"/>
                <c:set var="rowClass" value="notEnrolled"/>
                <c:if test="${enrollmentProgram.enrolled}">
                    <c:set var="rowClass" value="enrolled"/>
                </c:if>
                <c:set var="extraClass" value=""/>
                <c:if test="${empty fn:trim(enrollmentProgram.program.description)}">
                    <c:set var="extraClass" value=" lastProgramRow"/>
                </c:if>
                <tr class="${rowClass} firstProgramRow${extraClass}">
                    <td class="firstColumn">
                        <cti:msg2 key="${enrollmentProgram.program.displayName}"/>
                    </td>
                    <td class="programDescriptionIcon">
                        <c:if test="${!empty enrollmentProgram.program.savingsDescriptionIcon}">
                            <img src="../../../WebConfig/yukon/Icons/${enrollmentProgram.program.savingsDescriptionIcon}">
                            <c:set var="savingsDescriptionIconDisplayed" value="true"/>
                        </c:if>
                        <c:if test="${empty enrollmentProgram.program.savingsDescriptionIcon}">
                            &nbsp;
                        </c:if>
                    </td>
                    <td class="programDescriptionIcon">
                        <c:if test="${!empty enrollmentProgram.program.controlPercentDescriptionIcon}">
                            <img src="../../../WebConfig/yukon/Icons/${enrollmentProgram.program.controlPercentDescriptionIcon}">
                            <c:set var="controlPercentDescriptionIconDisplayed" value="true"/>
                        </c:if>
                        <c:if test="${empty enrollmentProgram.program.controlPercentDescriptionIcon}">
                            &nbsp;
                        </c:if>
                    </td>
                    <td class="programDescriptionIcon">
                        <c:if test="${!empty enrollmentProgram.program.environmentDescriptionIcon}">
                            <img src="../../../WebConfig/yukon/Icons/${enrollmentProgram.program.environmentDescriptionIcon}">
                            <c:set var="environmentDescriptionIconDisplayed" value="true"/>
                        </c:if>
                        <c:if test="${empty enrollmentProgram.program.environmentDescriptionIcon}">
                            &nbsp;
                        </c:if>
                    </td>
                    <td class="enrollLink lastColumn">
                        <c:if test="${!enrollmentProgram.enrolled}">
                            <c:set var="enrollType" value="enroll"/>
                        </c:if>
                        <c:if test="${enrollmentProgram.enrolled}">
                            <c:set var="enrollType" value="unenroll"/>
                        </c:if>
                        <cti:url var="enrollUrl" value="/spring/stars/consumer/enrollment/${enrollType}">
                            <cti:param name="assignedProgramId" value="${assignedProgramId}"/>
                        </cti:url>
                        <a href="${enrollUrl}"><cti:msg
                            key="yukon.dr.consumer.enrollment.${enrollType}"/></a>
                    </td>
                </tr>
                <c:if test="${!empty fn:trim(enrollmentProgram.program.description)}">
                    <tr class="${rowClass} programDescription lastProgramRow">
                        <td colspan="5" class="firstColumn lastColumn">
                            <spring:escapeBody htmlEscape="true">${enrollmentProgram.program.description}</spring:escapeBody>
                        </td>
                    </tr>
                </c:if>
            </c:forEach>
        </c:if>
    </c:forEach>
    </table>

    <c:if test="${savingsDescriptionIconDisplayed || controlPercentDescriptionIconDisplayed || environmentDescriptionIconDisplayed}">
        <cti:msg key="yukon.dr.consumer.enrollment.iconKeyTitle" var="iconKeyTitle"/>
        <br>
        <div class="legend">
            <h1>${iconKeyTitle}</h1>
            <table>
            <c:if test="${savingsDescriptionIconDisplayed}">
                <tr><td><img src="../../../WebConfig/yukon/Icons/$$Sm.gif"></td>
                <td><cti:msg key="yukon.dr.consumer.enrollment.savingsIconDescription"/></td></tr>
            </c:if>
            <c:if test="${controlPercentDescriptionIconDisplayed}">
                <tr><td><img src="../../../WebConfig/yukon/Icons/ThirdSm.gif"></td>
                <td><cti:msg key="yukon.dr.consumer.enrollment.controlPercentIconDescription"/></td></tr>
            </c:if>
            <c:if test="${environmentDescriptionIconDisplayed}">
                <tr><td><img src="../../../WebConfig/yukon/Icons/Tree2Sm.gif"></td>
                <td><cti:msg key="yukon.dr.consumer.enrollment.environmentIconDescription"/></td></tr>
            </c:if>
            </table>
        </div>
    </c:if>
</cti:standardPage>
