<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="tools" page="bulk.demandReset.results">
    <cti:includeScript link="/resources/js/common/yukon.ui.progressbar.js"/>
    <cti:url var="recentResultsUrl" value="/common/commandRequestExecutionResults/list"/>
    <cti:list var="arguments">
        <cti:item value="${recentResultsUrl}" />
    </cti:list>
    <table class="stacked">
        <tr>
            <td class="strong-label-small"><i:inline key=".bulk.demandReset.results.note.label" /></td>
            <td class="detail"><i:inline key=".bulk.demandReset.results.note.text" arguments="${arguments}" /></td>
        </tr>
    </table>
    
    <c:if test="${empty result}">
        <cti:url value="/bulk/demand-reset/start" var="commandUrl">
            <cti:mapParam value="${deviceCollection.collectionParameters}" />
        </cti:url>
        <cti:button id="start-btn" nameKey="start" href="${commandUrl}" />
    </c:if>
    
     <c:if test="${not empty result}">
        <%-- PROGRESS --%>
        <div class="stacked clearfix form-control js-progress">
            <div class="clearfix">
                <tags:resultProgressBar totalCount="${result.allDevicesCollection.deviceCount}"
                    countKey="DEMAND_RESET/${result.key}/COMPLETED_ITEMS"
                    progressLabelTextKey=".bulk.demandReset.results.progressLabel"
                    statusTextKey="DEMAND_RESET/${result.key}/STATUS_TEXT"
                    statusClassKey="DEMAND_RESET/${result.key}/STATUS_CLASS" 
                    classes="dib fl" barClasses="push-down-3"/>
                    
                <c:if test="${not result.isCanceled()}">
                    <cti:button id="cancel-btn" nameKey="cancel" data-key="${result.key}"/>
                </c:if>
            </div>
            <%-- device collection action --%>
            <div class="dn js-action">
                <cti:url var="bulkAllUrl" value="/bulk/collectionActions">
                    <cti:mapParam value="${result.allDevicesCollection.collectionParameters}" />
                </cti:url>
                <a href="${bulkAllUrl}"><i:inline key=".bulk.demandReset.results.newOperationAll" /></a>
                <tags:selectedDevicesPopup deviceCollection="${result.allDevicesCollection}" />
            </div>
            <%-- cre action --%>
            <div class="dn js-action">
                <cti:url var="initiatedResultsUrl" value="/common/commandRequestExecutionResults/detail">
                    <cti:param name="commandRequestExecutionId" value="${result.initiatedExecution.id}" />
                </cti:url>
                <a href="${initiatedResultsUrl}"><i:inline key=".bulk.demandReset.results.initiatedResults" /></a>
                <br>
                <c:if test="${result.verificationExecution != null}">
                    <cti:url var="verificationResultsUrl" value="/common/commandRequestExecutionResults/detail">
                        <cti:param name="commandRequestExecutionId" value="${result.verificationExecution.id}" />
                    </cti:url>
                    <a href="${verificationResultsUrl}"><i:inline key=".bulk.demandReset.results.verificationResults" /></a>
                </c:if>
                <div>
                    <i:inline key=".bulk.demandReset.results.view" />
                    <cti:simpleReportLinkFromNameTag definitionName="demandResetAllResultDefinition" viewType="extView"
                        resultKey="${result.key}" module="amr" showMenu="true">
                        <i:inline key="yukon.common.html" />
                    </cti:simpleReportLinkFromNameTag>
                    |
                    <cti:simpleReportLinkFromNameTag definitionName="demandResetAllResultDefinition" resultKey="${result.key}"
                        viewType="csvView">
                        <i:inline key="yukon.common.csv" />
                    </cti:simpleReportLinkFromNameTag>
                    |
                    <cti:simpleReportLinkFromNameTag definitionName="demandResetAllResultDefinition" resultKey="${result.key}"
                        viewType="pdfView">
                        <i:inline key="yukon.common.pdf" />
                    </cti:simpleReportLinkFromNameTag>
                </div>
            </div>
         </div>
         
          
        <%-- CONFIRMED --%>
        <div class="stacked js-result">
            <div class="fwb">
                <i:inline key=".bulk.demandReset.CONFIRMED" />
                : <span class="success"><cti:dataUpdaterValue type="DEMAND_RESET" styleClass="js-count"
                        identifier="${result.key}/CONFIRMED_COUNT" /></span>
            </div>
            <div class="dn js-action">
                <div>
                    <cti:url var="confirmedUrl" value="/bulk/collectionActions">
                        <cti:mapParam value="${result.confirmedCollection.collectionParameters}" />
                    </cti:url>
                    <a href="${confirmedUrl}"><i:inline key=".bulk.demandReset.results.newOperation" /></a>
                    <tags:selectedDevicesPopup deviceCollection="${result.confirmedCollection}" />
                </div>
                <div>
                    <i:inline key=".bulk.demandReset.results.view" />
                    <cti:simpleReportLinkFromNameTag state="CONFIRMED"
                        definitionName="demandResetSuccessResultDefinition" viewType="extView"
                        resultKey="${result.key}" module="amr" showMenu="true">
                        <i:inline key="yukon.common.html" />
                    </cti:simpleReportLinkFromNameTag>
                    |
                    <cti:simpleReportLinkFromNameTag state="CONFIRMED"
                        definitionName="demandResetSuccessResultDefinition" resultKey="${result.key}"
                        viewType="csvView">
                        <i:inline key="yukon.common.csv" />
                    </cti:simpleReportLinkFromNameTag>
                    |
                    <cti:simpleReportLinkFromNameTag state="CONFIRMED"
                        definitionName="demandResetSuccessResultDefinition" resultKey="${result.key}"
                        viewType="pdfView">
                        <i:inline key="yukon.common.pdf" />
                    </cti:simpleReportLinkFromNameTag>
                </div>
            </div>
        </div>
        
        <%-- UNCONFIRMED --%>
        <div class="stacked js-result">
            <div class="fwb">
                <i:inline key=".bulk.demandReset.UNCONFIRMED" />
                : <span class="success"><cti:dataUpdaterValue type="DEMAND_RESET" styleClass="js-count"
                        identifier="${result.key}/UNCONFIRMED_COUNT" /></span>
            </div>
            <div class="dn js-action">
                <div>
                    <cti:url var="unconfirmedUrl" value="/bulk/collectionActions">
                        <cti:mapParam value="${result.confirmedCollection.collectionParameters}" />
                    </cti:url>
                    <a href="${unconfirmedUrl}"><i:inline key=".bulk.demandReset.results.newOperation" /></a>
                    <tags:selectedDevicesPopup deviceCollection="${result.unconfirmedCollection}" />
                </div>
                <div>
                    <i:inline key=".bulk.demandReset.results.view" />
                    <cti:simpleReportLinkFromNameTag state="UNCONFIRMED"
                        definitionName="demandResetFailureResultDefinition" viewType="extView"
                        resultKey="${result.key}" module="amr" showMenu="true">
                        <i:inline key="yukon.common.html" />
                    </cti:simpleReportLinkFromNameTag>
                    |
                    <cti:simpleReportLinkFromNameTag state="UNCONFIRMED"
                        definitionName="demandResetFailureResultDefinition" resultKey="${result.key}"
                        viewType="csvView">
                        <i:inline key="yukon.common.csv" />
                    </cti:simpleReportLinkFromNameTag>
                    |
                    <cti:simpleReportLinkFromNameTag state="UNCONFIRMED"
                        definitionName="demandResetFailureResultDefinition" resultKey="${result.key}"
                        viewType="pdfView">
                        <i:inline key="yukon.common.pdf" />
                    </cti:simpleReportLinkFromNameTag>
                </div>
            </div>
        </div>
        
        <%-- FAILED --%>
        <div class="stacked js-result">
            <div class="fwb">
                <i:inline key=".bulk.demandReset.FAILED" />
                : <span class="error"><cti:dataUpdaterValue type="DEMAND_RESET" styleClass="js-count"
                        identifier="${result.key}/FAILED_COUNT" /></span>
            </div>
            <div class="dn js-action">
                <div>
                    <cti:url var="failedUrl" value="/bulk/collectionActions">
                        <cti:mapParam value="${result.failedCollection.collectionParameters}" />
                    </cti:url>
                    <a href="${failedUrl}"><i:inline key=".bulk.demandReset.results.newOperation" /></a>
                    <tags:selectedDevicesPopup deviceCollection="${result.failedCollection}" />
                </div>
                <div>
                    <i:inline key=".bulk.demandReset.results.view" />
                    <cti:simpleReportLinkFromNameTag state="FAILED"
                        definitionName="demandResetFailureResultDefinition" viewType="extView" module="amr"
                        showMenu="true" resultKey="${result.key}">
                        <i:inline key="yukon.common.html" />
                    </cti:simpleReportLinkFromNameTag>
                    |
                    <cti:simpleReportLinkFromNameTag state="FAILED"
                        definitionName="demandResetFailureResultDefinition" viewType="csvView"
                        resultKey="${result.key}">
                        <i:inline key="yukon.common.csv" />
                    </cti:simpleReportLinkFromNameTag>
                    |
                    <cti:simpleReportLinkFromNameTag state="FAILED"
                        definitionName="demandResetFailureResultDefinition" viewType="pdfView"
                        resultKey="${result.key}">
                        <i:inline key="yukon.common.pdf" />
                    </cti:simpleReportLinkFromNameTag>
                </div>
            </div>
        </div>
        
        <%-- UNSUPPORTED --%>
        <div class="stacked js-result">
            <div class="fwb">
                <i:inline key=".bulk.demandReset.UNSUPPORTED" />
                : <span class="error"><cti:dataUpdaterValue type="DEMAND_RESET" styleClass="js-count"
                        identifier="${result.key}/UNSUPPORTED_COUNT" /></span>
            </div>
            <div class="dn js-action">
                <div>
                    <cti:url var="unsupportedUrl" value="/bulk/collectionActions">
                        <cti:mapParam value="${result.unsupportedCollection.collectionParameters}" />
                    </cti:url>
                    <a href="${unsupportedUrl}"><i:inline key=".bulk.demandReset.results.newOperation" /></a>
                    <tags:selectedDevicesPopup deviceCollection="${result.unsupportedCollection}" />
                </div>
                <div>
                    <i:inline key=".bulk.demandReset.results.view" />
                    <cti:simpleReportLinkFromNameTag state="UNSUPPORTED"
                        definitionName="demandResetSuccessResultDefinition" viewType="extView"
                        resultKey="${result.key}" module="amr" showMenu="true">
                        <i:inline key="yukon.common.html" />
                    </cti:simpleReportLinkFromNameTag>
                    |
                    <cti:simpleReportLinkFromNameTag state="UNSUPPORTED"
                        definitionName="demandResetSuccessResultDefinition" resultKey="${result.key}"
                        viewType="csvView">
                        <i:inline key="yukon.common.csv" />
                    </cti:simpleReportLinkFromNameTag>
                    |
                    <cti:simpleReportLinkFromNameTag state="UNSUPPORTED"
                        definitionName="demandResetSuccessResultDefinition" resultKey="${result.key}"
                        viewType="pdfView">
                        <i:inline key="yukon.common.pdf" />
                    </cti:simpleReportLinkFromNameTag>
                </div>
            </div>
        </div>
        
        <%-- CANCELED --%>
        <div class="stacked js-result">
            <div class="fwb">
                <i:inline key=".bulk.demandReset.CANCELED" />
                : <span class="error"><cti:dataUpdaterValue type="DEMAND_RESET" styleClass="js-count"
                        identifier="${result.key}/CANCELED_COUNT" /></span>
            </div>
            <div class="dn js-action">
                <div>
                    <cti:url var="canceledUrl" value="/bulk/collectionActions">
                        <cti:mapParam value="${result.canceledCollection.collectionParameters}" />
                    </cti:url>
                    <a href="${unsupportedUrl}"><i:inline key=".bulk.demandReset.results.newOperation" /></a>
                    <tags:selectedDevicesPopup deviceCollection="${result.canceledCollection}" />
                </div>
                <div>
                    <i:inline key=".bulk.demandReset.results.view" />
                    <cti:simpleReportLinkFromNameTag state="CANCELED"
                        definitionName="demandResetSuccessResultDefinition" viewType="extView"
                        resultKey="${result.key}" module="amr" showMenu="true">
                        <i:inline key="yukon.common.html" />
                    </cti:simpleReportLinkFromNameTag>
                    |
                    <cti:simpleReportLinkFromNameTag state="CANCELED"
                        definitionName="demandResetSuccessResultDefinition" resultKey="${result.key}"
                        viewType="csvView">
                        <i:inline key="yukon.common.csv" />
                    </cti:simpleReportLinkFromNameTag>
                    |
                    <cti:simpleReportLinkFromNameTag state="CANCELED"
                        definitionName="demandResetSuccessResultDefinition" resultKey="${result.key}"
                        viewType="pdfView">
                        <i:inline key="yukon.common.pdf" />
                    </cti:simpleReportLinkFromNameTag>
                </div>
            </div>
        </div>

        <cti:dataUpdaterCallback function="yukon.tools.demand.reset.progress" initialize="true"
            value="DEMAND_RESET/${result.key}/IS_COMPLETE" />
        <cti:dataUpdaterCallback function="yukon.tools.demand.reset.cancellable" initialize="true"
            value="DEMAND_RESET/${result.key}/CANCELLABLE" />
    </c:if>
    
    <cti:includeScript link="/resources/js/pages/yukon.tools.demand.reset.js" />
    
</cti:standardPage>