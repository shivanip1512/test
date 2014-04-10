<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="dr" page="ecobee.details">

    <div class="column-12-12">
        <div class="column one">
            <tags:sectionContainer2 nameKey="queryStats" styleClass="stacked">
                <tags:nameValueContainer2 naturalWidth="false">
                    <c:forEach items="${months}" var="month">
                        <tr>
                            <td class="name">${month}:</td>
                            <td class="value full-width">
                                <div class="progress" style="width: 80px;float:left;">
                                    <div class="progress-bar progress-bar-success" role="progressbar" aria-valuenow="0.0%" aria-valuemin="0" aria-valuemax="100" style="width: 27.0%"></div>
                                    <div class="progress-bar" role="progressbar" aria-valuenow="0.0%" aria-valuemin="0" aria-valuemax="100" style="width: 50.0%"></div>
                                    <div class="progress-bar progress-bar-default" role="progressbar" aria-valuenow="0.0%" aria-valuemin="0" aria-valuemax="100" style="width: 23.0%"></div>
                                </div>
                                <div class="fl" style="margin-left: 10px;" title="data collection / reports / reads">
                                    <span style="margin-right: 10px;width:48px;display: inline-block;">15200</span>
                                    <span class="label label-success">4104</span>
                                    <span class="label label-info">7600</span>
                                    <span class="label label-default">3496</span>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>
                </tags:nameValueContainer2>
            </tags:sectionContainer2>
            <tags:sectionContainer2 nameKey="dataDownloads">
                <table class="compact-results-table dashed">
                    <thead>
                        <tr><th>Started</th><th colspan="2"></th></tr>
                    </thead>
                    <tfoot></tfoot>
                    <tbody>
                        <tr>
                            <td>2014-04-09 11:32 AM</td>
                            <td class="half-width">
                                <div class="progress"><div class="progress-bar" role="progressbar" aria-valuenow="0.0%" aria-valuemin="0" aria-valuemax="100" style="width: 60.0%"></div></div>
                            </td>
                            <td style="width:54px;">60.0%</td>
                        </tr>
                        <tr>
                            <td>2014-04-06 10:02 AM</td>
                            <td colspan="2">
                                <span class="success">Finished</span><span>&nbsp;(2014-04-06 10:02 AM)</span>
                            </td>
                        </tr>
                    </tbody>
                </table>
                <div class="action-area">
                    <cti:button nameKey="download" icon="icon-page-white-excel"/>
                </div>
            </tags:sectionContainer2>
        </div>
        <div class="column two nogutter">
            <tags:sectionContainer2 nameKey="issues" arguments="4">
                <table class="compact-results-table dashed with-form-controls">
                    <thead>
                        <tr><th>Type</th><th></th></tr>
                    </thead>
                    <tfoot></tfoot>
                    <tbody>
                        <c:forEach items="${issues}" var="issue">
                            <tr>
                                <td>${issue.type}</td>
                                <td>
                                    <c:if test="${issue.type.deviceIssue}">SN: ${issue.serialNumber}</c:if>
                                    <c:if test="${!issue.type.deviceIssue}">Group: ${issue.loadGroupName}</c:if>
                                    <c:if test="${issue.type.fixable}"><cti:button renderMode="buttonImage" classes="fr" icon="icon-wrench"/></c:if>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
                <div class="action-area">
                    <cti:button label="Fix All" icon="icon-wrench"/>
                </div>
            </tags:sectionContainer2>
        </div>
    </div>
    
</cti:standardPage>