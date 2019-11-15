<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<cti:msgScope paths="yukon.web.modules.tools.collectionActions.progressReport,yukon.web.modules.tools.collectionActions">

    <table class="stacked">
        <tr>
            <td class="strong-label-small"><i:inline key=".note.label" /></td>
            <cti:url var="resultsUrl" value="/collectionActions/recentResults"/>
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
                    <cti:msg2 var="inputNameKey" key=".collectionActionInput.${input.key}" blankIfMissing="true"/>
                    <c:set var="inputName" value="${!empty inputNameKey ?  inputNameKey : input.key}"/>
                    <td class="name">${inputName}:</td>
                    <td class="value">${fn:escapeXml(input.value)}</td>
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
                    <cm:dropdownOption key=".deviceReport" icon="icon-report" href="${deviceReportUrl}" newTab="true"/>
                    <cti:url var="mapDevicesUrl" value="/tools/map">
                        <c:forEach items="${result.inputs.collection.collectionParameters}" var="cp">
                            <cti:param name="${cp.key}" value="${cp.value}"/>
                        </c:forEach>
                    </cti:url>
                    <cm:dropdownOption icon="icon-map-sat" key=".mapDevices" href="${mapDevicesUrl}" newTab="true"/>
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
            <cti:button renderMode="image" icon="icon-report" title="${creText}" data-url="${creLink}" classes="js-cre"/>
        </c:if>
        <c:if test="${result.verificationExecution != null}">
            <cti:msg2 var="verificationCreText" key=".verificationCreText"/>
            <cti:url var="verificationCreLink" value="/common/commandRequestExecutionResults/detail?commandRequestExecutionId=${result.verificationExecution.id}"/>
            <cti:button renderMode="image" icon="icon-report-edit" title="${verificationCreText}" data-url="${verificationCreLink}" classes="js-cre"/>
        </c:if>
        <c:set var="logClass" value="${isLogAvailable ? '' : 'dn'}"/>
        <cti:msg2 var="logText" key=".logText"/>
        <cti:url var="logLink" value="/collectionActions/progressReport/log?key=${result.cacheKey}"/>
        <cti:button renderMode="image" icon="icon-script" title="${logText}" href="${logLink}" classes="js-log-icon ${logClass}"/>
    </c:set>
    
    <cti:msg2 var="helpText" key=".results.helpText"/>
    <tags:sectionContainer2 nameKey="results" helpText="${helpText}" controls="${controls}">
        <div class="buffered progress-lg">
            <span class="name"><i:inline key="yukon.common.progress"/>:&nbsp;</span>
            <div class="progress dib vam js-progress">
                <div class="progress-bar progress-bar-info progress-bar-striped active"
                     role="progressbar" aria-valuenow="60" aria-valuemin="0" aria-valuemax="100"></div>
            </div>
            <span class="js-percent-text"></span>
            <span class="js-completed-count" style="padding-left:10px;"></span>
            <span class="js-status" style="padding-left:10px"></span>
            <span class="user-message error js-progress-error dn"></span>
            <span class="user-message info js-progress-info dn"></span>
            <c:if test="${result.displayCancelButton()}">
                <cti:button nameKey="cancel" classes="js-cancel fn" busy="true"/>
            </c:if>
            <c:if test="${result.action == 'LOCATE_ROUTE'}">
                <cti:button nameKey="setRoutes" classes="js-set-routes fn"/>
            </c:if>
        </div>
        <div style="max-height:220px;max-width:500px;" class="js-pie-chart js-initialize"></div>
    </tags:sectionContainer2>

</cti:msgScope>