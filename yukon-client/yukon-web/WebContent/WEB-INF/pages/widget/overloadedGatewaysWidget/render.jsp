<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<script>
    /** User clicked the streaming capacity pill.  Redirect to Data Streaming Summary Page with gateway selected */
    $(document).on('click', '.js-streaming-capacity', function (ev) {
        var gatewayId = $(this).closest('tr').data('gateway');
        window.location.href = yukon.url('/tools/dataStreaming/summary?gatewaysSelect=' + gatewayId);
    });
</script>

<table class="compact-results-table" style="width:auto;">

    <c:forEach var="gateway" items="${overloadedGateways}">
        <tr data-gateway="${gateway.paoIdentifier.paoId}">
            <td>        
                <cti:url var="gatewayUrl" value="/stars/gateways/${gateway.paoIdentifier.paoId}"/>
                <a href="${gatewayUrl}">${fn:escapeXml(gateway.name)}</a>
            </td>
            <td>
                <c:set var="linkClasses" value=""/>
                <cti:checkRolesAndProperties value="RF_DATA_STREAMING_ENABLED">
                    <c:set var="linkClasses" value="cp js-streaming-capacity"/>
                </cti:checkRolesAndProperties>
                <span class="badge badge-error ${linkClasses}" title="<cti:msg2 key=".streamingDetail"/>"><fmt:formatNumber pattern="###.##%" value="${gateway.data.dataStreamingLoadingPercent / 100}"/></span>
            </td>
        </tr>
    </c:forEach>
    
</table>