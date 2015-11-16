<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="tools" page="bulk.${command.toString().toLowerCase()}.results">
    <cti:includeScript link="/resources/js/common/yukon.ui.progressbar.js"/>
    <cti:url var="recentResultsUrl" value="/bulk/disconnect/recentResults"/>
    <cti:msg2 var="help" key=".bulk.disconnect.results.${command.toString().toLowerCase()}.help"/>
    <cti:list var="arguments">
        <cti:item value="${recentResultsUrl}" />
        <cti:item value="${help}" />
    </cti:list>
    <table class="stacked">
        <tr>
            <td class="strong-label-small"><i:inline key=".bulk.disconnect.results.note.label" /></td>
            <td class="detail"><i:inline key=".bulk.disconnect.results.note.text"
                    arguments="${arguments}" /></td>
        </tr>
    </table>

    <c:if test="${empty result}">
        <cti:url value="/bulk/disconnect/start" var="commandUrl">
            <cti:mapParam value="${deviceCollection.collectionParameters}"/>
            <cti:param name="command" value="${command}"/>
        </cti:url>
        <cti:button id="start-btn" nameKey="start" href="${commandUrl}"/>
    </c:if>

    <c:if test="${not empty result}">
        <%-- PROGRESS --%>
        <div class="stacked clearfix form-control js-progress">
            <div class="clearfix">
                <tags:resultProgressBar totalCount="${result.allDevicesCollection.deviceCount}"
                    countKey="DISCONNECT/${result.key}/COMPLETED_ITEMS"
                    progressLabelTextKey=".bulk.disconnect.results.progressLabel"
                    statusTextKey="DISCONNECT/${result.key}/STATUS_TEXT"
                    statusClassKey="DISCONNECT/${result.key}/STATUS_CLASS" classes="dib fl" />
                    
                <c:if test="${not result.isCanceled()}">
                    <cti:button id="cancel-btn" nameKey="cancel" data-key="${result.key}" data-command="${result.command}"/>
                </c:if>
            </div>
            <%-- device collection action --%>
            <div class="dn js-action">
                <cti:url var="bulkAllUrl" value="/bulk/collectionActions">
                    <cti:mapParam value="${result.allDevicesCollection.collectionParameters}" />
                </cti:url>
                <a href="${bulkAllUrl}"><i:inline key=".bulk.disconnect.results.newOperationAll" /></a>
                <tags:selectedDevicesPopup deviceCollection="${result.allDevicesCollection}" />
            </div>
            <%-- cre action --%>
            <div class="dn js-action">
                <cti:url var="creResultsUrl" value="/common/commandRequestExecutionResults/detail">
                    <cti:param name="commandRequestExecutionId" value="${result.commandRequestExecution.id}" />
                </cti:url>
                <a href="${creResultsUrl}"><i:inline key=".bulk.disconnect.results.creResults" /></a>
                <div>
                    <i:inline key=".bulk.disconnect.results.view" />
                    <cti:simpleReportLinkFromNameTag definitionName="disconnectAllResultDefinition" viewType="extView"
                        resultKey="${result.key}" module="amr" showMenu="true">
                        <i:inline key="yukon.common.html" />
                    </cti:simpleReportLinkFromNameTag>
                    |
                    <cti:simpleReportLinkFromNameTag definitionName="disconnectAllResultDefinition" resultKey="${result.key}"
                        viewType="csvView">
                        <i:inline key="yukon.common.csv" />
                    </cti:simpleReportLinkFromNameTag>
                    |
                    <cti:simpleReportLinkFromNameTag definitionName="disconnectAllResultDefinition" resultKey="${result.key}"
                        viewType="pdfView">
                        <i:inline key="yukon.common.pdf" />
                    </cti:simpleReportLinkFromNameTag>
                </div>
            </div>
        </div>
        
        <%-- CONNECTED --%>
        <div class="stacked js-result">
            <div class="fwb">
                <i:inline key=".bulk.disconnect.CONNECTED" />
                : <span class="success"><cti:dataUpdaterValue type="DISCONNECT" styleClass="js-count"
                        identifier="${result.key}/CONNECTED_COUNT" /></span>
            </div>
            <div class="dn js-action">
                <div>
                    <cti:url var="connectedUrl" value="/bulk/collectionActions">
                        <cti:mapParam value="${result.connectedCollection.collectionParameters}" />
                    </cti:url>
                    <a href="${connectedUrl}"><i:inline key=".bulk.disconnect.results.newOperation" /></a>
                    <tags:selectedDevicesPopup deviceCollection="${result.connectedCollection}" />
                </div>
                <div>
                    <i:inline key=".bulk.disconnect.results.view" />
                    <cti:simpleReportLinkFromNameTag state="CONNECTED"
                        definitionName="disconnectSuccessResultDefinition" viewType="extView"
                        resultKey="${result.key}" module="amr" showMenu="true">
                        <i:inline key="yukon.common.html" />
                    </cti:simpleReportLinkFromNameTag>
                    |
                    <cti:simpleReportLinkFromNameTag state="CONNECTED"
                        definitionName="disconnectSuccessResultDefinition" resultKey="${result.key}"
                        viewType="csvView">
                        <i:inline key="yukon.common.csv" />
                    </cti:simpleReportLinkFromNameTag>
                    |
                    <cti:simpleReportLinkFromNameTag state="CONNECTED"
                        definitionName="disconnectSuccessResultDefinition" resultKey="${result.key}"
                        viewType="pdfView">
                        <i:inline key="yukon.common.pdf" />
                    </cti:simpleReportLinkFromNameTag>
                </div>
            </div>
        </div>
        
        <%-- ARMED --%>
        <div class="stacked js-result">
            <div class="fwb">
                <i:inline key=".bulk.disconnect.ARMED" />
                &nbsp;:&nbsp; <span class="success"> <cti:dataUpdaterValue type="DISCONNECT"
                        styleClass="js-count" identifier="${result.key}/ARMED_COUNT" />
                </span>
            </div>
            <div class="dn js-action">
                <div>
                    <cti:url var="armedUrl" value="/bulk/collectionActions">
                        <cti:mapParam value="${result.armedCollection.collectionParameters}" />
                    </cti:url>
                    <a href="${armedUrl}"><i:inline key=".bulk.disconnect.results.newOperation" /></a>
                    <tags:selectedDevicesPopup deviceCollection="${result.armedCollection}" />
                </div>
                <div>
                    <i:inline key=".bulk.disconnect.results.view" />
                    <cti:simpleReportLinkFromNameTag state="ARMED"
                        definitionName="disconnectSuccessResultDefinition" viewType="extView" module="amr"
                        showMenu="true" resultKey="${result.key}">
                        <i:inline key="yukon.common.html" />
                    </cti:simpleReportLinkFromNameTag>
                    |
                    <cti:simpleReportLinkFromNameTag state="ARMED"
                        definitionName="disconnectSuccessResultDefinition" viewType="csvView"
                        resultKey="${result.key}">
                        <i:inline key="yukon.common.csv" />
                    </cti:simpleReportLinkFromNameTag>
                    |
                    <cti:simpleReportLinkFromNameTag state="ARMED"
                        definitionName="disconnectSuccessResultDefinition" viewType="pdfView"
                        resultKey="${result.key}">
                        <i:inline key="yukon.common.pdf" />
                    </cti:simpleReportLinkFromNameTag>
                </div>
            </div>
        </div>
        
        <%-- DISCONNECTED --%>
        <div class="stacked js-result">
            <div class="fwb">
                <i:inline key=".bulk.disconnect.DISCONNECTED" />
                : <span class="success"><cti:dataUpdaterValue type="DISCONNECT" styleClass="js-count"
                        identifier="${result.key}/DISCONNECTED_COUNT" /></span>
            </div>
            <div class="dn js-action">
                <div>
                    <cti:url var="disconnectedUrl" value="/bulk/collectionActions">
                        <cti:mapParam value="${result.disconnectedCollection.collectionParameters}" />
                    </cti:url>
                    <a href="${disconnectedUrl}"><i:inline key=".bulk.disconnect.results.newOperation" /></a>
                    <tags:selectedDevicesPopup deviceCollection="${result.disconnectedCollection}" />
                </div>
                <div>
                    <i:inline key=".bulk.disconnect.results.view" />
                    <cti:simpleReportLinkFromNameTag state="DISCONNECTED"
                        definitionName="disconnectSuccessResultDefinition" viewType="extView" module="amr"
                        showMenu="true" resultKey="${result.key}">
                        <i:inline key="yukon.common.html" />
                    </cti:simpleReportLinkFromNameTag>
                    |
                    <cti:simpleReportLinkFromNameTag state="DISCONNECTED"
                        definitionName="disconnectSuccessResultDefinition" viewType="csvView"
                        resultKey="${result.key}">
                        <i:inline key="yukon.common.csv" />
                    </cti:simpleReportLinkFromNameTag>
                    |
                    <cti:simpleReportLinkFromNameTag state="DISCONNECTED"
                        definitionName="disconnectSuccessResultDefinition" viewType="pdfView"
                        resultKey="${result.key}">
                        <i:inline key="yukon.common.pdf" />
                    </cti:simpleReportLinkFromNameTag>
                </div>
            </div>
        </div>
                
        <%-- FAILED --%>
        <div class="stacked js-result">
            <div class="fwb">
                <i:inline key=".bulk.disconnect.FAILED" />
                : <span class="error"><cti:dataUpdaterValue type="DISCONNECT" styleClass="js-count"
                        identifier="${result.key}/FAILED_COUNT" /></span>
            </div>
            <div class="dn js-action">
                <div>
                    <cti:url var="failedUrl" value="/bulk/collectionActions">
                        <cti:mapParam value="${result.failedCollection.collectionParameters}" />
                    </cti:url>
                    <a href="${failedUrl}"><i:inline key=".bulk.disconnect.results.newOperation" /></a>
                    <tags:selectedDevicesPopup deviceCollection="${result.failedCollection}" />
                </div>
                <div>
                    <i:inline key=".bulk.disconnect.results.view" />
                    <cti:simpleReportLinkFromNameTag state="FAILED"
                        definitionName="disconnectFailureResultDefinition" viewType="extView" module="amr"
                        showMenu="true" resultKey="${result.key}">
                        <i:inline key="yukon.common.html" />
                    </cti:simpleReportLinkFromNameTag>
                    |
                    <cti:simpleReportLinkFromNameTag state="FAILED"
                        definitionName="disconnectFailureResultDefinition" viewType="csvView"
                        resultKey="${result.key}">
                        <i:inline key="yukon.common.csv" />
                    </cti:simpleReportLinkFromNameTag>
                    |
                    <cti:simpleReportLinkFromNameTag state="FAILED"
                        definitionName="disconnectFailureResultDefinition" viewType="pdfView"
                        resultKey="${result.key}">
                        <i:inline key="yukon.common.pdf" />
                    </cti:simpleReportLinkFromNameTag>
                </div>
            </div>
        </div>
        
        <%-- NOT CONFIGURED --%>
        <div class="stacked js-result">
            <div class="fwb">
                <i:inline key=".bulk.disconnect.NOT_CONFIGURED" />
                : <span class="error"><cti:dataUpdaterValue type="DISCONNECT" styleClass="js-count"
                        identifier="${result.key}/NOT_CONFIGURED_COUNT" /></span>
            </div>
            <div class="dn js-action">
                <div>
                    <cti:url var="armedUrl" value="/bulk/collectionActions">
                        <cti:mapParam value="${result.notConfiguredCollection.collectionParameters}" />
                    </cti:url>
                    <a href="${armedUrl}"><i:inline key=".bulk.disconnect.results.newOperation" /></a>
                    <tags:selectedDevicesPopup deviceCollection="${result.notConfiguredCollection}" />
                </div>
                <div>
                    <i:inline key=".bulk.disconnect.results.view" />
                    <cti:simpleReportLinkFromNameTag state="NOT_CONFIGURED"
                        definitionName="disconnectSuccessResultDefinition" viewType="extView" module="amr"
                        showMenu="true" resultKey="${result.key}">
                        <i:inline key="yukon.common.html" />
                    </cti:simpleReportLinkFromNameTag>
                    |
                    <cti:simpleReportLinkFromNameTag state="NOT_CONFIGURED"
                        definitionName="disconnectSuccessResultDefinition" viewType="csvView"
                        resultKey="${result.key}">
                        <i:inline key="yukon.common.csv" />
                    </cti:simpleReportLinkFromNameTag>
                    |
                    <cti:simpleReportLinkFromNameTag state="NOT_CONFIGURED"
                        definitionName="disconnectSuccessResultDefinition" viewType="pdfView"
                        resultKey="${result.key}">
                        <i:inline key="yukon.common.pdf" />
                    </cti:simpleReportLinkFromNameTag>
                </div>
            </div>
        </div>
        
        <%-- UNSUPPORTED --%>
        <div class="stacked js-result">
            <div class="fwb">
                <i:inline key=".bulk.disconnect.UNSUPPORTED" />
                : <span class="error"><cti:dataUpdaterValue type="DISCONNECT"
                        identifier="${result.key}/UNSUPPORTED_COUNT" styleClass="js-count" /></span>
            </div>
            <div class="dn js-action">
                <div>
                    <cti:url var="unsupportedUrl" value="/bulk/collectionActions">
                        <cti:mapParam value="${result.unsupportedCollection.collectionParameters}" />
                    </cti:url>
                    <a href="${unsupportedUrl}"><i:inline key=".bulk.disconnect.results.newOperation" /></a>
                    <tags:selectedDevicesPopup deviceCollection="${result.unsupportedCollection}" />
                </div>
                <div>
                    <i:inline key=".bulk.disconnect.results.view" />
                    <cti:simpleReportLinkFromNameTag state="UNSUPPORTED"
                        definitionName="disconnectSuccessResultDefinition" viewType="extView" module="amr"
                        showMenu="true" resultKey="${result.key}">
                        <i:inline key="yukon.common.html" />
                    </cti:simpleReportLinkFromNameTag>
                    |
                    <cti:simpleReportLinkFromNameTag state="UNSUPPORTED"
                        definitionName="disconnectSuccessResultDefinition" viewType="csvView"
                        resultKey="${result.key}">
                        <i:inline key="yukon.common.csv" />
                    </cti:simpleReportLinkFromNameTag>
                    |
                    <cti:simpleReportLinkFromNameTag state="UNSUPPORTED"
                        definitionName="disconnectSuccessResultDefinition" viewType="pdfView"
                        resultKey="${result.key}">
                        <i:inline key="yukon.common.pdf" />
                    </cti:simpleReportLinkFromNameTag>
                </div>
            </div>
        </div>
        
        <%-- CANCELED --%>
        <div class="stacked js-result">
            <div class="fwb">
                <i:inline key=".bulk.disconnect.CANCELED" />
                : <span class="error"><cti:dataUpdaterValue type="DISCONNECT" styleClass="js-count"
                        identifier="${result.key}/CANCELED_COUNT" /></span>
            </div>
            <div class="dn js-action">
                <div>
                    <cti:url var="canceledUrl" value="/bulk/collectionActions">
                        <cti:mapParam value="${result.canceledCollection.collectionParameters}" />
                    </cti:url>
                    <a href="${canceledUrl}"><i:inline key=".bulk.disconnect.results.newOperation" /></a>
                    <tags:selectedDevicesPopup deviceCollection="${result.canceledCollection}" />
                </div>
                <div>
                    <i:inline key=".bulk.disconnect.results.view" />
                    <cti:simpleReportLinkFromNameTag state="CANCELED"
                        definitionName="disconnectSuccessResultDefinition" viewType="extView" module="amr"
                        showMenu="true" resultKey="${result.key}">
                        <i:inline key="yukon.common.html" />
                    </cti:simpleReportLinkFromNameTag>
                    |
                    <cti:simpleReportLinkFromNameTag state="CANCELED"
                        definitionName="disconnectSuccessResultDefinition" viewType="csvView"
                        resultKey="${result.key}">
                        <i:inline key="yukon.common.csv" />
                    </cti:simpleReportLinkFromNameTag>
                    |
                    <cti:simpleReportLinkFromNameTag state="CANCELED"
                        definitionName="disconnectSuccessResultDefinition" viewType="pdfView"
                        resultKey="${result.key}">
                        <i:inline key="yukon.common.pdf" />
                    </cti:simpleReportLinkFromNameTag>
                </div>
            </div>
        </div>
        
        <cti:dataUpdaterCallback function="yukon.tools.disconnect.progress" initialize="true"
            value="DISCONNECT/${result.key}/IS_COMPLETE" />
    </c:if>
    
    <cti:includeScript link="/resources/js/pages/yukon.tools.disconnect.js" />
    
</cti:standardPage>