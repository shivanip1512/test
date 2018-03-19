<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="tools" page="bulk.progressReport">

    <table class="stacked">
        <tr>
            <td class="strong-label-small"><i:inline key=".note.label" /></td>
            <cti:url var="resultsUrl" value="/bulk/recentResults"/>
            <td class="detail"><i:inline key=".note.text" arguments="${resultsUrl}" /></td>
        </tr>
    </table>

    <cti:toJson id="resultsjson" object="${result}"/>

    <input type="hidden" id="key" value="${result.cacheKey}"/>
    <c:forEach var="detail" items="${details}">
        <cti:msg2 var="detailText" key="${detail.formatKey}"/>
        <input type="hidden" id="detail-${detail}" value="${detailText}"/>
        <input type="hidden" id="color-${detail}" value="${detail.color}"/>
    </c:forEach>
    <c:forEach var="executionStatus" items="${status}">
        <cti:msg2 var="statusText" key="${executionStatus.formatKey}"/>
        <input type="hidden" id="status-${executionStatus}" value="${statusText}"/>
    </c:forEach>
    
    <tags:sectionContainer2 nameKey="inputs" hideEnabled="true">
        <tags:nameValueContainer2>
            <tags:nameValue2 nameKey=".action">
                <i:inline key="${result.action.formatKey}"/>
            </tags:nameValue2>
            <c:forEach var="input" items="${result.inputs.inputs}">
                <tr>
                    <td class="name">${input.key}:</td>
                    <td class="value">${input.value}</td>
                </tr>
            </c:forEach>
            <tags:nameValue2 nameKey=".devices">
                ${result.inputs.collection.deviceCount}
                <cm:dropdown icon="icon-cog" triggerClasses="js-cog-menu" style="padding-left:10px;">
                    <cti:url var="collectionActionsUrl" value="/bulk/collectionActions">
                        <c:forEach items="${result.inputs.collection.collectionParameters}" var="cp">
                            <cti:param name="${cp.key}" value="${cp.value}"/>
                        </c:forEach>
                    </cti:url>
                    <cm:dropdownOption key=".collectionActions" icon="icon-cog-go" href="${collectionActionsUrl}"/> 
                    <cti:url var="deviceReportUrl" value="/bulk/deviceCollectionReport">
                        <c:forEach items="${result.inputs.collection.collectionParameters}" var="cp">
                            <cti:param name="${cp.key}" value="${cp.value}"/>
                        </c:forEach>
                    </cti:url>
                    <cm:dropdownOption key=".deviceReport" icon="icon-report" href="${deviceReportUrl}"/>
                    <cti:url var="mapDevicesUrl" value="/tools/map">
                        <c:forEach items="${result.inputs.collection.collectionParameters}" var="cp">
                            <cti:param name="${cp.key}" value="${cp.value}"/>
                        </c:forEach>
                    </cti:url>
                    <cm:dropdownOption icon="icon-map-sat" key=".mapDevices" href="${mapDevicesUrl}"/>
                    <cti:url var="readAttributeUrl" value="/group/groupMeterRead/homeCollection">
                        <c:forEach items="${result.inputs.collection.collectionParameters}" var="cp">
                            <cti:param name="${cp.key}" value="${cp.value}"/>
                        </c:forEach>
                    </cti:url>
                    <cm:dropdownOption icon="icon-read" key=".readAttribute" href="${readAttributeUrl}"/>
                    <cti:url var="sendCommandUrl" value="/group/commander/collectionProcessing">
                        <c:forEach items="${result.inputs.collection.collectionParameters}" var="cp">
                            <cti:param name="${cp.key}" value="${cp.value}"/>
                        </c:forEach>
                    </cti:url>
                    <cm:dropdownOption icon="icon-ping" key=".sendCommand" href="${sendCommandUrl}"/>
                </cm:dropdown>
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".startDateTime">
                <cti:formatDate type="BOTH" value="${result.startTime}"/>
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".stopDateTime" valueClass="js-stop-time">
                <c:if test="${result.stopTime != null}">
                    <cti:formatDate type="BOTH" value="${result.stopTime}"/>
                </c:if>
            </tags:nameValue2>
        </tags:nameValueContainer2>
    </tags:sectionContainer2>
    
    <c:set var="controls">
        <c:if test="${result.execution != null}">
            <cti:msg2 var="creText" key=".creText"/>
            <cti:url var="creLink" value="/common/commandRequestExecutionResults/detail?commandRequestExecutionId=${result.execution.id}"/>
            <cti:button renderMode="image" icon="icon-report" title="${creText}" href="${creLink}"/>
        </c:if>
        <c:set var="logClass" value="${isLogAvailable ? '' : 'dn'}"/>
        <cti:msg2 var="logText" key=".logText"/>
        <cti:url var="logLink" value="/bulk/progressReport/log?key=${result.cacheKey}"/>
        <cti:button renderMode="image" icon="icon-script" title="${logText}" href="${logLink}" classes="js-log-icon ${logClass}"/>
    </c:set>
    
    <tags:sectionContainer2 nameKey="results" controls="${controls}">
        <div class="buffered progress-lg">
            <span class="name"><i:inline key="yukon.common.progress"/>:&nbsp;</span>
            <div class="progress dib vam js-progress">
                <div class="progress-bar progress-bar-info progress-bar-striped active"
                     role="progressbar" aria-valuenow="60" aria-valuemin="0" aria-valuemax="100"></div>
            </div>
            <span class="js-percent-text"></span>
            <span class="js-completed-count" style="padding-left:10px;"></span>
            <span class="js-status" style="padding-left:10px"></span>
            <c:if test="${result.isCancelable()}">
                <cti:button nameKey="cancel" classes="js-cancel fn" busy="true"/>
            </c:if>
        </div>
        <div style="max-height:220px;max-width:500px;" class="js-pie-chart js-initialize"></div>
    </tags:sectionContainer2>

    <cti:includeScript link="HIGH_STOCK"/>
    <cti:includeScript link="/resources/js/pages/yukon.bulk.progressReport.js"/>
    <cti:includeScript link="/resources/js/common/yukon.ui.progressbar.js"/>
      
</cti:standardPage>