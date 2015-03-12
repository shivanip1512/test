<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="operator" page="inventory.config">

<div class="stacked-md"><tags:selectedInventory inventoryCollection="${inventoryCollection}" id="inventoryCollection"/></div>
<div class="stacked-md">
    <span class="label label-info"><i:inline key="yukon.common.note"/></span>&nbsp;
    <i:inline key=".instructions"/>
</div>

<div class="column-12-12 clearfix">
    <div class="column one">
        <tags:sectionContainer2 nameKey="currentConfig">
            <table class="link-table">
                <tr>
                    <td>
                        <cti:url var="url" value="deviceReconfig/setup">
                            <c:forEach var="entry" items="${inventoryCollection.collectionParameters}">
                                <cti:param name="${entry.key}" value="${entry.value}"/>
                            </c:forEach>
                        </cti:url>
                        <a href="${url}"><i:inline key=".scheduleConfig.label"/></a>
                    </td>
                    <td><i:inline key=".scheduleConfig.description"/></td>
                </tr>
                <tr>
                    <td>
                        <cti:url var="url" value="resendConfig/view">
                            <c:forEach var="entry" items="${inventoryCollection.collectionParameters}">
                                <cti:param name="${entry.key}" value="${entry.value}"/>
                            </c:forEach>
                        </cti:url>
                        <a href="${url}"><i:inline key=".sendConfig.label"/></a>
                    </td>
                    <td><i:inline key=".sendConfig.description"/></td>
                </tr>
            </table>
        </tags:sectionContainer2>
    </div>
    <div class="column two nogutter">
        <c:if test="${showNewConfig}">
            <tags:sectionContainer2 nameKey="newConfig">
                <table class="link-table">
                    <tr>
                        <td class="wsnw">
                            <cti:url var="url" value="actions/config/new">
                                <c:forEach var="entry" items="${inventoryCollection.collectionParameters}">
                                    <cti:param name="${entry.key}" value="${entry.value}"/>
                                </c:forEach>
                            </cti:url>
                            <a href="${url}"><i:inline key=".newConfig.label"/></a>
                        </td>
                        <td><i:inline key=".newConfig.description"/></td>
                    </tr>
                </table>
            </tags:sectionContainer2>
        </c:if>
    </div>
</div>

<div class="page-action-area">
    <cti:url value="/stars/operator/inventory/inventoryActions?" var="cancelUrl"/>
    <cti:button href="${cancelUrl}${pageContext.request.queryString}" nameKey="cancel"/>
</div>

</cti:standardPage>