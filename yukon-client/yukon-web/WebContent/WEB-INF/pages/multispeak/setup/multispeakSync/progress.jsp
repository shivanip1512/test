<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

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
                        <!-- TO DO - Replace it with updaters -->
                        <i:inline key="yukon.web.modules.adminSetup.deviceGroupSync.multispeakDeviceGroupSyncProgressStatus.RUNNING"/>
                    </tags:nameValue2>

                    <%-- progress bar --%>
                    <tags:nameValue2 nameKey=".infoLabel.progress">
                        <!-- TO DO - Replace it with enrollment updaters -->
                        <tags:updateableProgressBar totalCount="${totalCount}" 
                                                    countKey="MSP_DEVICE_GROUP_SYNC/METERS_PROCESSED_COUNT"
                                                    isAbortedKey="MSP_DEVICE_GROUP_SYNC/IS_ABORTED"
                                                    hideCount="false"/>
                    </tags:nameValue2>

                </c:otherwise>
            </c:choose>

        </tags:nameValueContainer2>
        <br>

        <%-- BACK/CANCEL BUTTONS --%>
        <cti:url var="backUrl" value="/multispeak/setup/multispeakSync/done"/>
        <form id="backToHomeForm" action="${backUrl}" method="post">
            <cti:csrfToken/>
            <button name="backToHome" class="button">
                <i:inline key=".backToHomeButton"/>
            </button>
            
            <button name="cancel" class="button" id="cancelButton">
                <i:inline key=".cancelButton"/>
            </button>
            
           </form>
    
    </tags:sectionContainer2>
    
    <cti:dataUpdaterCallback function="yukon.ui.progressbar.toggleElementsWhenTrue(['cancelButton'], true)" initialize="true" value="MSP_DEVICE_GROUP_SYNC/IS_RUNNING" />
    <cti:dataUpdaterCallback function="yukon.ui.progressbar.toggleElementsWhenTrue(['cancelButton'], false)" initialize="true" value="MSP_DEVICE_GROUP_SYNC/IS_NOT_RUNNING" />

</cti:standardPage>