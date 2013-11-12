<%@ page import="com.cannontech.billing.FileFormatTypes"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="jsTree" tagdir="/WEB-INF/tags/jsTree" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<input id="in_formatType_itronEvent" type="hidden" value="${TYPE_CURTAILMENT_EVENTS_ITRON}" />

<span id="txt_selectGroup" class="error dn fl"><cti:msg key="yukon.web.billing.mustSelectGroup"/></span>

    <c:set var="origEndDate" value="${BILLING_BEAN.endDate}"></c:set>
    <c:set var="systemTimezone" value="${tzFormat.format(origEndDate)}"></c:set>

    <cti:msg key="yukon.web.settings" var="settingsLabel"/>
    <tags:sectionContainer title="${settingsLabel}" id="billingContainer">

        <form id="MForm" name="MForm" action="<cti:url value="/servlet/BillingServlet" />" method="post">
    
            <c:if test="${BILLING_BEAN.errorMsg != null}">
                <div class="error">${BILLING_BEAN.errorMsg}</div>
                <br>
            </c:if>
    
            <tags:nameValueContainer>

                <cti:msg key="yukon.web.billing.fileFormat" var="fileFormat" />
                <tags:nameValue name="${fileFormat}" nameColumnWidth="250px">
                    <select id="fileFormat" name="fileFormat">
                        <c:forEach var="format" items="${formatMap}">
                            <option value="${format.value}" ${(format.value == BILLING_BEAN.fileFormat)?'selected':''}>${format.key}</option>
                        </c:forEach>
                    </select>
                </tags:nameValue>
                <cti:msg key="yukon.web.billing.billingEndDate" var="billingEndDate" />
                <tags:nameValue name="${billingEndDate}">
                    <dt:date name="endDate" value="${origEndDate}" />
                </tags:nameValue>
                <cti:msg key="yukon.web.billing.demandDaysPrevious" var="demandDaysPrevious" />
                <tags:nameValue name="${demandDaysPrevious}">
                    <input type="text" name="demandDays" value="${BILLING_BEAN.demandDaysPrev}" size = "8">
                </tags:nameValue>
                <cti:msg key="yukon.web.billing.energyDaysPrevious" var="energyDaysPrevious" />
                <tags:nameValue name="${energyDaysPrevious}">
                    <input type="text" name="energyDays" value="${BILLING_BEAN.energyDaysPrev}" size = "8">
                </tags:nameValue>
                <cti:msg key="yukon.web.billing.removeMultiplier" var="removeMultiplier" />
                <tags:nameValue  name="${removeMultiplier}">
                    <input type="checkbox" name="removeMultiplier" ${(BILLING_BEAN.removeMult)? 'checked':''} >
                </tags:nameValue>
                <cti:msg key="yukon.web.billing.billingGroup" var="billingGroup" />
                <tags:nameValue name="${billingGroup}" id="row_billing_group" nameClass="vat">

                    <cti:deviceGroupHierarchyJson predicates="NON_HIDDEN" var="dataJson" />
                    <tags:deviceGroupNameSelector fieldName="billGroup" fieldValue="${billGroup}" dataJson="${dataJson}" classes="fl"/>

                </tags:nameValue>

            </tags:nameValueContainer>
            <div class="page-action-area">
                <cti:msg key="yukon.web.generate" var="generateLabel" />
                <cti:msg key="yukon.web.billing.schedule" var="scheduleLabel" />
                <cti:button label="${generateLabel}" name="generate" icon="icon-page-white-excel"/>
                <cti:button label="${scheduleLabel}" name="schedule" icon="icon-calendar-view-day"/>
            </div>
        </form>
    </tags:sectionContainer>
