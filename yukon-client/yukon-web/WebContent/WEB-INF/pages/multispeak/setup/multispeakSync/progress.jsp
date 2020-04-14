<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="adminSetup" page="multispeakSyncProgress">

    <cti:includeScript link="/resources/js/common/yukon.ui.progressbar.js"/>

    <tags:sectionContainer2 nameKey="progressDetailContainer">

        <%-- INFO --%>
        <tags:nameValueContainer2>

            <%-- type --%>
            <tags:nameValue2 nameKey=".infoLabel.type">
                <cti:msg key="${progress.type}"/>
            </tags:nameValue2>

            <c:choose>
                <c:when test="${!isEnrollmentSelected}">
                    <%-- status --%>
                    <tags:nameValue2 nameKey=".infoLabel.status">
                        <cti:classUpdater type="MSP_DEVICE_GROUP_SYNC" identifier="STATUS_CLASS">
                            <cti:dataUpdaterValue type="MSP_DEVICE_GROUP_SYNC" identifier="STATUS_TEXT"/>
                        </cti:classUpdater>
                    </tags:nameValue2>

                    <%-- progress bar --%>
                    <tags:nameValue2 nameKey=".infoLabel.progress">
                        <tags:updateableProgressBar totalCount="${totalCount}" 
                                                    countKey="MSP_DEVICE_GROUP_SYNC/METERS_PROCESSED_COUNT"
                                                    isAbortedKey="MSP_DEVICE_GROUP_SYNC/IS_ABORTED"
                                                    hideCount="true"/>
                    </tags:nameValue2>

                    <%-- stats --%>
                    <c:set var="processorTypes" value="${progress.type.processorTypes}"/>
                    <c:forEach var="processorType" items="${processorTypes}">
                        <tags:nameValue2 nameKey=".infoLabel.statsLabel.${processorType}">
                            <cti:dataUpdaterValue type="MSP_DEVICE_GROUP_SYNC" identifier="${processorType}_CHANGE_COUNT"/>
                            <i:inline key=".infoValue.metersAdded"/>
                            <br>
                            <cti:dataUpdaterValue type="MSP_DEVICE_GROUP_SYNC" identifier="${processorType}_NO_CHANGE_COUNT"/>
                            <i:inline key=".infoValue.noChange"/>
                        </tags:nameValue2>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <%-- status --%>
                    <tags:nameValue2 nameKey=".infoLabel.status">
                        <cti:classUpdater type="MSP_ENROLLMENT_SYNC" identifier="STATUS_CLASS">
                            <cti:dataUpdaterValue type="MSP_ENROLLMENT_SYNC" identifier="STATUS_TEXT"/>
                        </cti:classUpdater>
                    </tags:nameValue2>

                    <%-- progress bar --%>
                    <tags:nameValue2 nameKey=".infoLabel.progress">
                        <tags:updateableProgressBar totalCount="${totalCount}" 
                                                    countKey="MSP_ENROLLMENT_SYNC/ENROLLMENT_MESSAGE_SENT_COUNT"
                                                    isAbortedKey="MSP_ENROLLMENT_SYNC/IS_ABORTED"
                                                    hideCount="false"/>
                    </tags:nameValue2>

                </c:otherwise>
            </c:choose>

        </tags:nameValueContainer2>
        <br>

        <%-- BACK BUTTON --%>
        <cti:url var="backUrl" value="/multispeak/setup/multispeakSync/done"/>
        <form id="backToHomeForm" action="${backUrl}" method="post">
            <cti:csrfToken/>
            <button name="backToHome" class="button">
                <i:inline key=".backToHomeButton"/>
            </button>
        </form>

        <%-- CANCEL BUTTON --%>
        <cti:url var="cancelUrl" value="/multispeak/setup/multispeakSync/cancel"/>
        <form action="${cancelUrl}" method="post">
            <cti:csrfToken/>
            <input type="hidden" name="multispeakSyncType" value="${progress.type}">
            <button name="cancel" class="button" id="cancelButton">
                <i:inline key=".cancelButton"/>
            </button>
        </form>

    </tags:sectionContainer2>

<c:choose>
    <c:when test="${!isEnrollmentSelected}">
        <cti:dataUpdaterCallback function="yukon.ui.progressbar.toggleElementsWhenTrue(['cancelButton'], true)" initialize="true" value="MSP_DEVICE_GROUP_SYNC/IS_RUNNING" />
        <cti:dataUpdaterCallback function="yukon.ui.progressbar.toggleElementsWhenTrue(['cancelButton'], false)" initialize="true" value="MSP_DEVICE_GROUP_SYNC/IS_NOT_RUNNING" />
    </c:when>
    <c:otherwise>
        <cti:dataUpdaterCallback function="yukon.ui.progressbar.toggleElementsWhenTrue(['cancelButton'], true)" initialize="true" value="MSP_ENROLLMENT_SYNC/IS_RUNNING" />
        <cti:dataUpdaterCallback function="yukon.ui.progressbar.toggleElementsWhenTrue(['cancelButton'], false)" initialize="true" value="MSP_ENROLLMENT_SYNC/IS_NOT_RUNNING" />
    </c:otherwise>
</c:choose>

</cti:standardPage>