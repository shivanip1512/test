<%@ page import="com.cannontech.billing.FileFormatTypes"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="jsTree" tagdir="/WEB-INF/tags/jsTree" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<input id="in_formatType_itronEvent" type="hidden" value="${TYPE_CURTAILMENT_EVENTS_ITRON}" />

<span id="txt_selectGroup" class="error dn fl"><cti:msg key="yukon.web.billing.mustSelectGroup"/></span>
<span id="txt_invalidDemandDaysPrevious" class="error dn"><cti:msg key="yukon.web.billing.invalidDemandDaysPrevious"/></span> 
<span id="txt_invalidEnergyDaysPrevious" class="error dn"><cti:msg key="yukon.web.billing.invalidEnergyDaysPrevious"/></span>

    <c:set var="origEndDate" value="${BILLING_BEAN.endDate}"></c:set>
    <c:set var="systemTimezone" value="${tzFormat.format(origEndDate)}"></c:set>

    <cti:msg key="yukon.common.settings" var="settingsLabel"/>
    <tags:sectionContainer title="${settingsLabel}" id="billingContainer">

        <form id="MForm" name="MForm" action="<cti:url value="/servlet/BillingServlet" />" method="post">
            <cti:csrfToken/>
            <c:if test="${BILLING_BEAN.errorMsg != null}">
                <div class="error">${BILLING_BEAN.errorMsg}</div>
                <br>
            </c:if>
    
            <tags:nameValueContainer>

                <cti:msg key="yukon.web.billing.fileFormat" var="fileFormat" />
                <tags:nameValue name="${fileFormat}" nameColumnWidth="250px">
                    <select id="fileFormat" name="fileFormat">
                        <c:forEach var="format" items="${formatMap}">
                            <option value="${format.value}" ${(format.value == BILLING_BEAN.fileFormat)?'selected':''}>
                            ${fn:escapeXml(format.key)}</option>
                        </c:forEach>
                    </select>
                </tags:nameValue>
                
                <cti:msg key="yukon.web.billing.billingEndDate" var="billingEndDate" />
                <tags:nameValue name="${billingEndDate}">
                    <dt:date name="endDate" value="${origEndDate}" />
                </tags:nameValue>
                
                <cti:msg key="yukon.web.billing.demandDaysPrevious" var="demandDaysPrevious" />
                <tags:nameValue name="${demandDaysPrevious}" id="row-demand-days-previous">
                 <input type="text" name="demandDays" value="${BILLING_BEAN.demandDaysPrev}" size = "8">
                </tags:nameValue>
                
                <cti:msg key="yukon.web.billing.energyDaysPrevious" var="energyDaysPrevious" />
                <tags:nameValue name="${energyDaysPrevious}" id="row-energy-days-previous">
                    <input type="text" name="energyDays" value="${BILLING_BEAN.energyDaysPrev}" size = "8">
                </tags:nameValue>
                
                <cti:msg key="yukon.web.billing.removeMultiplier" var="removeMultiplier" />
                <tags:nameValue  name="${removeMultiplier}">
                    <input type="checkbox" name="removeMultiplier" ${(BILLING_BEAN.removeMult)? 'checked':''} >
                </tags:nameValue>

                <cti:msg key="yukon.web.billing.billingGroup" var="billingGroup" />
                <tags:nameValue name="${billingGroup}" id="row-billing-group" nameClass="vat">
                    <tags:deviceGroupPicker inputName="billGroup" multi="true" inputValue="${billingBean.billGroup}"/>
                </tags:nameValue>

            </tags:nameValueContainer>
            <div class="page-action-area">
                <cti:button nameKey="generate" name="generate" icon="icon-page-white-excel"/>
                <cti:button nameKey="schedule" name="schedule" icon="icon-calendar-view-day"/>
            </div>
        </form>
    </tags:sectionContainer>
