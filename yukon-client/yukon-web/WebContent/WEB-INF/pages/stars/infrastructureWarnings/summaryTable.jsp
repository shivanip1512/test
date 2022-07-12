<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:msgScope paths="widgets.infrastructureWarnings">

    <c:set var="divClass" value="${fromDetailPage ? 'separated-columns' : ''}"/>
    <div class="${!fromDetailPage ? 'column-12-12 clearfix' : ''}">
        <c:set var="column" value="1"/>
        <c:if test="${summary.totalGateways != 0}">
            <div class="${column % 2 == 0 ? 'column two nogutter' : 'column one'} PB10 ${divClass}">
                <cti:msg2 var="deviceLabel" key=".gateways"/>
                <tags:infrastructureWarningsCount deviceTotalCount="${summary.totalGateways}"
                                                  deviceWarningsCount="${summary.warningGateways}"
                                                  deviceLabel="${deviceLabel}"
                                                  fromDetailPage="${fromDetailPage}"
                                                  deviceType="GATEWAY" />
                <c:set var="column" value="${column + 1}"/>
            </div>
        </c:if>
        <c:if test="${summary.totalRelays != 0}">
            <div class="${column % 2 == 0 ? 'column two nogutter' : 'column one'} PB10 ${divClass}">
                <cti:msg2 var="deviceLabel" key=".relays"/>
                <tags:infrastructureWarningsCount deviceTotalCount="${summary.totalRelays}"
                                                  deviceWarningsCount="${summary.warningRelays}"
                                                  deviceLabel="${deviceLabel}"
                                                  fromDetailPage="${fromDetailPage}"
                                                  deviceType="RELAY" />
                <c:set var="column" value="${column + 1}"/>
            </div>
        </c:if>
        
        <c:if test="${summary.totalMeters != 0}">
            <div class="${column % 2 == 0 ? 'column two nogutter' : 'column one'} PB10 ${divClass}">
                <cti:msg2 var="deviceLabel" key=".meters"/>
                <tags:infrastructureWarningsCount deviceTotalCount="${summary.totalMeters}"
                                                  deviceWarningsCount="${summary.warningMeters}"
                                                  deviceLabel="${deviceLabel}"
                                                  fromDetailPage="${fromDetailPage}"
                                                  deviceType="IPLINK_METER" />
                <c:set var="column" value="${column + 1}"/>
            </div>
        </c:if>
        
        <c:if test="${summary.totalCcus != 0}">
            <div class="${column % 2 == 0 ? 'column two nogutter' : 'column one'} PB10 ${divClass}">
                <cti:msg2 var="deviceLabel" key=".CCUs"/>
                <tags:infrastructureWarningsCount deviceTotalCount="${summary.totalCcus}"
                                                  deviceWarningsCount="${summary.warningCcus}"
                                                  deviceLabel="${deviceLabel}" 
                                                  fromDetailPage="${fromDetailPage}"
                                                  deviceType="CCU" />
                 <c:set var="column" value="${column + 1}"/>
             </div>
        </c:if>
        
        <c:if test="${summary.totalRepeaters != 0}">
            <div class="${column % 2 == 0 ? 'column two nogutter' : 'column one'} PB10 ${divClass}">
                <cti:msg2 var="deviceLabel" key=".repeaters"/>
                <tags:infrastructureWarningsCount deviceTotalCount="${summary.totalRepeaters}"
                                                  deviceWarningsCount="${summary.warningRepeaters}"
                                                  deviceLabel="${deviceLabel}" 
                                                  fromDetailPage="${fromDetailPage}"
                                                  deviceType="REPEATER" />
                <c:set var="column" value="${column + 1}"/>
             </div>
        </c:if>
    
    </div>

</cti:msgScope>