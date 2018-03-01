<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="tools" page="bulk.progressReport">

    <input type="hidden" id="key" value="${result.cacheKey}"/>
    <c:forEach var="detail" items="${details}">
        <cti:msg2 var="detailText" key="${detail.formatKey}"/>
        <input type="hidden" id="detail-${detail}" value="${detailText}"/>
        <input type="hidden" id="color-${detail}" value="${detail.color}"/>
    </c:forEach>

    <tags:sectionContainer2 nameKey="inputs" hideEnabled="true">
        <tags:nameValueContainer2>
            <tags:nameValue2 nameKey=".action">
                <i:inline key="${result.action.formatKey}"/>
            </tags:nameValue2>
            <c:forEach var="input" items="${result.inputs.inputs}">
                <tr>
                    <td class="name">${input.key}</td>
                    <td class="value">${input.value}</td>
                </tr>
            </c:forEach>
            <tags:nameValue2 nameKey=".devices">
                ${result.inputs.collection.deviceCount}
                <tags:selectedDevicesPopup deviceCollection="${result.inputs.collection}"/>
                <cti:url var="collectionActionsUrl" value="/bulk/collectionActions">
                    <c:forEach items="${result.inputs.collection.collectionParameters}" var="cp">
                        <cti:param name="${cp.key}" value="${cp.value}"/>
                    </c:forEach>
                </cti:url>
                <cti:msg2 var="titleText" key=".newOperationText"/>
                <input type="hidden" id="newOperationText" value="${titleText}"/>
                <cti:button renderMode="image" href="${collectionActionsUrl}" icon="icon-cog-go" classes="fn" title="${titleText}"/>
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".startDateTime">
                <cti:formatDate type="BOTH" value="${result.startTime}"/>
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".stopDateTime" valueClass="js-stop-time">
                <cti:formatDate type="BOTH" value="${result.stopTime}"/>
            </tags:nameValue2>
        </tags:nameValueContainer2>
    </tags:sectionContainer2>
    
    <c:set var="controls">
        <c:if test="${result.execution}">
            <cti:msg2 var="creText" key=".creText"/>
            <cti:url var="creLink" value="/common/commandRequestExecutionResults/detail?commandRequestExecutionId=${result.execution.id}"/>
            <cti:button renderMode="image" icon="icon-report" title="${creText}" href="${creLink}"/>
        </c:if>
        <cti:msg2 var="logText" key=".logText"/>
        <cti:url var="logLink" value="/bulk/progressReport/log?key=${result.cacheKey}"/>
        <cti:button renderMode="image" icon="icon-script" title="${logText}" href="${logLink}"/>
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
            <c:if test="${result.action.cancelable}">
                <cti:button nameKey="cancel" classes="js-cancel fn"/>
            </c:if>
        </div>
        <div style="max-height:220px;max-width:500px;" class="js-pie-chart js-initialize"></div>
    </tags:sectionContainer2>

    <cti:includeScript link="HIGH_STOCK"/>
    <cti:includeScript link="/resources/js/pages/yukon.bulk.progressReport.js"/>
    <cti:includeScript link="/resources/js/common/yukon.ui.progressbar.js"/>
      
</cti:standardPage>