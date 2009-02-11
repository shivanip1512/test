<%@ taglib tagdir="/WEB-INF/tags" prefix="ct"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<c:choose>
    <c:when test="${empty errors and empty errorMessage}">

        <c:choose>
            <c:when test="${scheduled}">
                <span class="internalSectionHeader"> A meter reading for ${newMeter.name} is scheduled to be retrieved for ${beginDate}.
                </span><br /><br />

                <c:if test="${prevMeter.name != newMeter.name}">
                    <span class="internalSectionHeader"> Device
                        Name ${prevMeter.name} is scheduled to
                        be ${newMeter.name} </span>
                    <br />
                </c:if>

                <c:if
                    test="${prevMeter.meterNumber != newMeter.meterNumber}">
                    <span class="internalSectionHeader"> Meter
                        Number ${prevMeter.meterNumber} is scheduled to
                        be ${newMeter.meterNumber}</span>
                    <br />
                    <br />
                </c:if>

            </c:when>

            <c:otherwise>

                <c:if test="${not scheduled}">
                    <span class="internalSectionHeader">Move in
                        request for ${newMeter.name} is complete.</span>
                </c:if>

                <br />
                <br />
                <span class="internalSectionHeader">Move In
                    Reading: </span>
                <b> <cti:pointValueFormatter format="FULL"
                        value="${previousReadingValue}" /> </b>
                <br />
                <br />


                <c:if test="${null != currentReading.value}">
                    <span class="internalSectionHeader">Usage
                        Reading: </span>
                    <b> <cti:pointValueFormatter format="FULL"
                            value="${currentReading}" /> </b>
                    <br />
                </c:if>

                <span class="internalSectionHeader">Calculated
                    Usage for ${beginDate} through ${endDate}:</span>
                <b> <cti:pointValueFormatter format="SHORT"
                        value="${calculatedDifference}" /> </b>
                <br />

                <span class="internalSectionHeader">Calculated
                    Beginning Usage Read:</span>
                <b> <cti:pointValueFormatter format="FULL"
                        value="${previousReadingValue}" /> </b>
                <c:if test="not ${autoArchivingEnabled}">
                    <a href="archive.jsp"> archive </a>
                </c:if>
                <br />
                <br />
                
                <c:if test="${prevMeter.name != newMeter.name}">
                    <span class="internalSectionHeader"> Device
                        Name ${prevMeter.name} was updated to
                        ${newMeter.name} </span>
                    <br />
                </c:if>
                
                <c:if
                    test="${prevMeter.meterNumber != newMeter.meterNumber}">
                    <span class="internalSectionHeader"> Meter
                        Number ${prevMeter.meterNumber} was updated to
                        ${newMeter.meterNumber}</span>
                    <br />
                    <br />
                </c:if>

                <c:forEach items="${deviceGroups}" var="deviceGroup">
                    <span class="internalSectionHeader">Meter was
                        removed from the ${deviceGroup.name} Group.</span>
                    <br />
                </c:forEach>

            </c:otherwise>
        </c:choose>
    </c:when>
    <c:otherwise>
        <span class="internalSectionHeader">The system was not
            able to process the move in request for ${prevMeter.name}
            for the following reasons.</span>
        <br /><br />
        <c:if test="${not empty errorMessage}">
            <span class="internalSectionHeader">${errorMessage}</span>
            <br /><br />
        </c:if>
        <c:forEach items="${errors}" var="error">
            <ct:hideReveal title="<span class=\"internalSectionHeader\">${error.description} (${error.errorCode})</span>"
                showInitially="false">
                        <span class="internalSectionHeader">${error.porter}</span>
                <br>
                <span class="internalSectionHeader">${error.troubleshooting}</span>
                <br>
            </ct:hideReveal>
            <br>
        </c:forEach>
        <a href="#" onclick="window.location.reload();"> Click here to retry your entry </a>     
    </c:otherwise>
</c:choose>
