<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<script>
    /** User clicked the streaming capacity pill.  Redirect to Data Streaming Summary Page with gateway selected */
    $(document).on('click', '.js-streaming-capacity', function (ev) {
        var gatewayId = $(this).closest('tr').data('gateway');
        window.location.href = yukon.url('/tools/dataStreaming/summary?selectedGatewayIds=' + gatewayId);
    });
</script>

<table class="compact-results-table" style="width:auto;">

    <c:choose>
        <c:when test="${overloadedGateways.size() > 0}">     
            <c:forEach var="gateway" items="${overloadedGateways}">
                <tr data-gateway="${gateway.paoIdentifier.paoId}">
                    <td>        
                        <cti:url var="gatewayUrl" value="/stars/gateways/${gateway.paoIdentifier.paoId}"/>
                        <a href="${gatewayUrl}">${fn:escapeXml(gateway.name)}</a>
                    </td>
                    <c:if test="${gateway.data.dataStreamingLoadingPercent > 100}">
                    <td>
                        <span class="badge badge-${gateway.data.dataStreamingLoadingPercent > 120? 'error': 'warning' } cp js-streaming-capacity" title="<cti:msg2 key=".streamingDetail"/>"><fmt:formatNumber pattern="###.##%" value="${gateway.data.dataStreamingLoadingPercent / 100}"/></span>
                    </td>
                    </c:if>
                </tr>
            </c:forEach>
        </c:when>
        <c:otherwise>
            <cti:icon icon="icon-tick"/><cti:msg2 key=".noOverloadedGateways"/>
        </c:otherwise>
    </c:choose>

    
</table>