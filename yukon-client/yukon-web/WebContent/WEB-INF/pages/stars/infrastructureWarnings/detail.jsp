<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="operator" page="infrastructureWarnings" smartNotificationsEvent="INFRASTRUCTURE_WARNING">

    <cti:msgScope paths="widgets.infrastructureWarnings">
    
        <div class="column-12-12">
            <div class="column one">
                <table class="infrastructure-warnings-summary">
                    <tr>
                        <td colspan="5" style="padding-left:100px;">
                            <span class="fl"><i:inline key=".totalWarnings"/>:&nbsp;&nbsp;
                            <c:set var="totalSuccess" value="${summary.totalGateways - summary.warningGateways + summary.totalRelays - summary.warningRelays + 
                                summary.totalCcus - summary.warningCcus + summary.totalRepeaters - summary.warningRepeaters}"/>
                            <span class="label bg-color-pie-green">${totalSuccess}</span>
                            <c:set var="totalWarnings" value="${summary.warningGateways + summary.warningRelays + summary.warningCcus + summary.warningRepeaters}"/>
                            <span class="label bg-color-orange">${totalWarnings}</span></span>
                            <cti:url var="downloadAll" value="/stars/infrastructureWarnings/downloadAll"/>
                            <cti:icon icon="icon-csv" nameKey="downloadAll" href="${downloadAll}" style="margin-top:2px;"/>
                        </td>
                    </tr>
                    <c:if test="${summary.totalGateways != 0 || summary.totalRelays != 0}">
                        <tr>
                            <c:if test="${summary.totalGateways != 0}">
                                <td>                
                                    <span><i:inline key=".gateways"/>:
                                </td>
                                <td>
                                    <span class="label bg-color-pie-green">${summary.totalGateways - summary.warningGateways}</span></span>
                                    <span class="label bg-color-orange">${summary.warningGateways}</span></span>
                                </td>
                            </c:if>
                            <c:if test="${summary.totalRelays != 0}">
                                <td width="10%">
                                </td>
                                <td>
                                    <span><i:inline key=".relays"/>:
                                </td>
                                <td>
                                    <span class="label bg-color-pie-green">${summary.totalRelays - summary.warningRelays}</span></span>
                                    <span class="label bg-color-orange">${summary.warningRelays}</span></span>
                                </td>
                            </c:if>
                        </tr>
                    </c:if>
                    <c:if test="${summary.totalCcus != 0 || summary.totalRepeaters != 0}">
                        <tr>
                            <c:if test="${summary.totalCcus != 0}">     
                                <td>
                                    <span><i:inline key=".CCUs"/>:
                                </td>
                                <td>
                                    <span class="label bg-color-pie-green">${summary.totalCcus - summary.warningCcus}</span></span>
                                    <span class="label bg-color-orange">${summary.warningCcus}</span></span>
                                </td>
                            </c:if>
                            <c:if test="${summary.totalRepeaters != 0}">
                                <td width="10%">
                                </td>
                                <td>
                                    <span><i:inline key=".repeaters"/>:
                                </td>
                                <td>
                                    <span class="label bg-color-pie-green">${summary.totalRepeaters - summary.warningRepeaters}</span></span>
                                    <span class="label bg-color-orange">${summary.warningRepeaters}</span></span>
                                </td>
                            </c:if>
                        </tr>
                    </c:if>
                </table>

            </div>
            <div class="column two nogutter filter-container">
                <cti:url var="action" value="/stars/infrastructureWarnings/filteredResults" />
                <form id="warnings-form" action="${action}" method="GET">
                    <span class="fr cp"><cti:icon icon="icon-help" data-popup="#results-help"/></span>
                    <cti:msg2 var="helpTitle" key=".detail.helpTitle"/>
                    <div id="results-help" class="dn" data-width="600" data-height="400" data-title="${helpTitle}"><cti:msg2 key=".detail.helpText"/></div><br/>
                    <tags:nameValueContainer2>
                        <tags:nameValue2 nameKey=".deviceTypes">
                            <div class="button-group stacked">
                                <c:forEach var="type" items="${deviceTypes}">
                                    <c:set var="selected" value="${false}"/>
                                    <c:if test="${fn:contains(selectedTypes, type)}">
                                        <c:set var="selected" value="${true}"/>
                                    </c:if>
                                    <cti:msg2 var="deviceType" key=".category.${type}"/>
                                    <tags:check name="types" classes="M0" value="${type}" label="${deviceType}" checked="${selected}"></tags:check>
                                </c:forEach>
                            </div>
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                
                    <div class="fr">
                        <cti:button nameKey="filter" classes="js-filter-results primary action"/>
                    </div>
                </form>
            </div>
        </div>
        
        <div id="results-table">
        </div>

        <div class="dn" id="js-pao-notes-popup"></div>
        <cti:includeScript link="/resources/js/pages/yukon.tools.paonotespopup.js"/>
        <cti:includeScript link="/resources/js/pages/yukon.infrastructurewarnings.detail.js"/>
    
    </cti:msgScope>
    
</cti:standardPage>