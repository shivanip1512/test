<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:msgScope paths="widgets.infrastructureWarnings">
    <table width="70%">
        <tr>
            <c:if test="${summary.totalGateways != 0}">
                <cti:msg2 var="deviceLabel" key=".gateways"/>
                <tags:infrastructureWarningsCount deviceTotalCount="${summary.totalGateways}" 
                                                  deviceWarningsCount="${summary.warningGateways}" 
                                                  deviceLabel="${deviceLabel}" 
                                                  fromDetailPage="${fromDetailPage}"/>
            </c:if>
        </tr>
    </table>
    <%@ include file="infrastructureWarningsDetails.jsp" %>
    <cti:url var="allWarningsUrl" value="/stars/infrastructureWarnings/detail"/>
    <span class="fr"><a href="${allWarningsUrl}?types=GATEWAY" target="_blank"><i:inline key="yukon.common.viewDetails"/></a></span>
</cti:msgScope>