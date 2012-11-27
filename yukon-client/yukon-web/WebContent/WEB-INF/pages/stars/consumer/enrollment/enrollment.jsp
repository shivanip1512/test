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

    <c:forEach var="enrollment" items="${enrollments}">
        <c:if test="${enrollment.applianceCategory.consumerSelectable || enrollment.enrolled}">
            <c:set var="enrollmentPrograms" value="${enrollment.enrollmentPrograms}"/>
            <c:set var="applianceCategoryId"
                value="${enrollment.applianceCategory.applianceCategoryId}"/>
            <div class="applianceCategory">
                <table class="applianceCategoryHeading">
                    <tr>
                        <c:if test="${!empty enrollment.applianceLogo}">
                            <td rowspan="2" class="applianceCategoryIcon">
                                <img src="../../../WebConfig/${enrollment.applianceLogo}">
                            </td>
                        </c:if>
                        <td class="applianceCategoryName">
                            <spring:escapeBody htmlEscape="true">
                                ${enrollment.applianceCategory.categoryLabel}
                            </spring:escapeBody>
                        </td>
                    </tr>
                    <tr>
                        <td class="applianceCategoryDescription">
                            <c:if test="${!empty enrollment.applianceCategory.description}">
                                <spring:escapeBody htmlEscape="true">
                                    ${enrollment.applianceCategory.description}
                                </spring:escapeBody>
                            </c:if>
                        </td>
                    </tr>
                </table>

                <div class="programList">
                    <c:forEach var="enrollmentProgram" items="${enrollmentPrograms}">
                        <c:if test="${enrollment.applianceCategory.consumerSelectable || enrollmentProgram.enrolled}">
                            <c:set var="assignedProgramId" value="${enrollmentProgram.program.programId}"/>
                            <c:set var="enrollmentClass" value="notEnrolled"/>
                            <c:if test="${enrollmentProgram.enrolled}">
                                <c:set var="enrollmentClass" value="enrolled"/>
                            </c:if>

                            <div class="${enrollmentClass} program">
                            <table cellpadding="0" cellspacing="0">
                                <tr>
                                    <td class="programName">
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
                                    <td class="enrollLink">
                                        <c:if test="${enrollment.applianceCategory.consumerSelectable}">
                                            <c:if test="${!enrollmentProgram.enrolled}">
                                                <c:set var="enrollType" value="enroll"/>
                                            </c:if>
                                            <c:if test="${enrollmentProgram.enrolled}">
                                                <c:set var="enrollType" value="unenroll"/>
                                            </c:if>
                                            <cti:url var="enrollUrl" value="/stars/consumer/enrollment/${enrollType}">
                                                <cti:param name="assignedProgramId" value="${assignedProgramId}"/>
                                            </cti:url>
                                            <a href="${enrollUrl}"><cti:msg key="yukon.dr.consumer.enrollment.${enrollType}"/></a>
                                        </c:if>
                                        <c:if test="${!enrollment.applianceCategory.consumerSelectable}">
                                            &nbsp;
                                        </c:if>
                                    </td>
                                </tr>
                            </table>
                            <c:if test="${!empty fn:trim(enrollmentProgram.program.description)}">
                                <div class="${enrollmentClass} programDescription">
                                    <spring:escapeBody htmlEscape="true">${enrollmentProgram.program.description}</spring:escapeBody>
                                </div>
                            </c:if>
                            </div>
                        </c:if>
                    </c:forEach>
                </div>
            </div>
        </c:if>
    </c:forEach>

    <c:if test="${savingsDescriptionIconDisplayed || controlPercentDescriptionIconDisplayed || environmentDescriptionIconDisplayed}">
        <cti:msg key="yukon.dr.consumer.enrollment.iconKeyTitle" var="iconKeyTitle"/>
        <br>
        <div class="legend">
            <h1>${iconKeyTitle}</h1>
            <table>
            <c:if test="${savingsDescriptionIconDisplayed}">
                <tr><td><img src="<cti:msg key="yukon.dr.consumer.enrollment.savingsIconUrl"/>"></td>
                <td><cti:msg key="yukon.dr.consumer.enrollment.savingsIconDescription"/></td></tr>
            </c:if>
            <c:if test="${controlPercentDescriptionIconDisplayed}">
                <tr><td><img src="<cti:msg key="yukon.dr.consumer.enrollment.controlPercentIconUrl"/>"></td>
                <td><cti:msg key="yukon.dr.consumer.enrollment.controlPercentIconDescription"/></td></tr>
            </c:if>
            <c:if test="${environmentDescriptionIconDisplayed}">
                <tr><td><img src="<cti:msg key="yukon.dr.consumer.enrollment.environmentIconUrl"/>"></td>
                <td><cti:msg key="yukon.dr.consumer.enrollment.environmentIconDescription"/></td></tr>
            </c:if>
            </table>
        </div>
    </c:if>
</cti:standardPage>
