<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="amr" page="meterDetailProfile">
    <tags:widgetContainer deviceId="${deviceId}">
        <div class="column-12-12">
            <div class="column one">
                <c:if test="${profileSupported}">
                    <tags:widget bean="profileWidget" />
                </c:if>
                <c:if test="${peakReportSupported}">
                    <tags:widget bean="peakReportWidget" />
                </c:if>
            </div>
            <div class="column two nogutter">
                <tags:widget bean="meterInformationWidget" />

                <c:if test="${profileSupported && profileCollection}">
                    <tags:widget bean="pendingProfilesWidget" />
                </c:if>
            </div>
        </div>
    </tags:widgetContainer>
</cti:standardPage>
