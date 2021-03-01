<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="amr" page="phaseDetect.results">
    <c:if test="${not empty cacheKey}">
        <div id="page-buttons">
            <cti:simpleReportLinkFromNameTag definitionName="phaseDetectDefinition" viewType="csvView" cacheKey="${cacheKey}" var="exportUrl"/>
            <cti:msg2 key=".exportCsv" var="exportLabel"/>
            <cti:button label="${exportLabel}" href="${exportUrl}" icon="icon-page-white-excel"/>
        </div>
    </c:if>
    <tags:sectionContainer2 nameKey="detectionResults">
        <div class="column-12-12">
            <div class="column one">
            
                <tags:boxContainer2 nameKey="metersDetectedA" arguments="${phaseAMetersSize}" hideEnabled="true" showInitially="${phaseAMetersSize > 0}">
                    <div class="scroll-lg">
                        <table class="compact-results-table clearfix">
                            <thead>
                                <tr>
                                    <th><i:inline key=".meterName"/></th>
                                    <c:if test="${!data.readAfterAll}"><th><i:inline key=".voltageReadings"/></th></c:if>
                                </tr>
                            </thead>
                            <tfoot></tfoot>
                            <tbody>
                                <c:forEach var="meter" items="${phaseAMeters}">
                                    <c:set var="phaseToReadingMap" value="${result.deviceReadingsMap[meter.deviceId]}"/>
                                    <tr>
                                        <td>${fn:escapeXml(meter.name)}</td>
                                        <c:if test="${!data.readAfterAll}">
                                            <c:set var="phaseAReading" value="${phaseToReadingMap['A']}"/>
                                            <td>
                                                <i:inline key=".initial"/>&nbsp;<strong>${phaseAReading.initial}</strong>&nbsp;
                                                <i:inline key=".last"/>&nbsp;<strong>${phaseAReading.last}</strong>&nbsp;
                                                <i:inline key=".delta"/>&nbsp;
                                                <c:choose>
                                                    <c:when test="${phaseAReading.delta gt 0}">
                                                        <c:set var="resultClass" value="success"/>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <c:set var="resultClass" value="error"/>
                                                    </c:otherwise>
                                                </c:choose>
                                                <strong class="${resultClass}">${phaseAReading.delta}</strong>
                                            </td>
                                        </c:if>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                    <c:if test="${phaseACollection.deviceCount > 0}">
                        <div class="action-area">
                            <cti:url value="/bulk/collectionActions" var="actionUrl">
                                <cti:mapParam value="${phaseACollection.collectionParameters}"/>
                            </cti:url>
                            <cti:button nameKey="collectionAction" href="${actionUrl}" icon="icon-cog-go"/>
                            <tags:selectedDevicesPopup deviceCollection="${phaseACollection}" type="button"/>
                        </div>
                    </c:if>
                </tags:boxContainer2>
                
                <tags:boxContainer2 nameKey="metersDetectedB" arguments="${phaseBMetersSize}" hideEnabled="true" showInitially="${phaseBMetersSize > 0}">
                    <div class="scroll-lg">
                        <table class="compact-results-table clearfix">
                            <thead>
                                <tr>
                                    <th><i:inline key=".meterName"/></th>
                                    <c:if test="${!data.readAfterAll}"><th><i:inline key=".voltageReadings"/></th></c:if>
                                </tr>
                            </thead>
                            <tfoot></tfoot>
                            <tbody>
                                <c:forEach var="meter" items="${phaseBMeters}">
                                    <c:set var="phaseToReadingMap" value="${result.deviceReadingsMap[meter.deviceId]}"/>
                                    <tr>
                                        <td>${fn:escapeXml(meter.name)}</td>
                                        <c:if test="${!data.readAfterAll}">
                                            <c:set var="phaseBReading" value="${phaseToReadingMap['B']}"/>
                                            <td>
                                                <i:inline key=".initial"/>&nbsp;<strong>${phaseBReading.initial}</strong>&nbsp;
                                                <i:inline key=".last"/>&nbsp;<strong>${phaseBReading.last}</strong>&nbsp;
                                                <i:inline key=".delta"/>&nbsp;
                                                <c:choose>
                                                    <c:when test="${phaseBReading.delta gt 0}">
                                                        <c:set var="resultClass" value="success"/>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <c:set var="resultClass" value="error"/>
                                                    </c:otherwise>
                                                </c:choose>
                                                <strong class="${resultClass}">${phaseBReading.delta}</strong>
                                            </td>
                                        </c:if>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                    <c:if test="${phaseBCollection.deviceCount > 0}">
                        <div class="action-area">
                            <cti:url value="/bulk/collectionActions" var="actionUrl">
                                <cti:mapParam value="${phaseBCollection.collectionParameters}"/>
                            </cti:url>
                            <cti:button nameKey="collectionAction" href="${actionUrl}" icon="icon-cog-go"/>
                            <tags:selectedDevicesPopup deviceCollection="${phaseBCollection}" type="button"/>
                        </div>
                    </c:if>
                </tags:boxContainer2>
                    
                <tags:boxContainer2 nameKey="metersDetectedC" arguments="${phaseCMetersSize}" hideEnabled="true" showInitially="${phaseCMetersSize > 0}">
                    <div class="scroll-lg">
                        <table class="compact-results-table clearfix">
                            <thead>
                                <tr>
                                    <th><i:inline key=".meterName"/></th>
                                    <c:if test="${!data.readAfterAll}"><th><i:inline key=".voltageReadings"/></th></c:if>
                                </tr>
                            </thead>
                            <tfoot></tfoot>
                            <tbody>
                                <c:forEach var="meter" items="${phaseCMeters}">
                                    <c:set var="phaseToReadingMap" value="${result.deviceReadingsMap[meter.deviceId]}"/>
                                    <tr>
                                        <td>${fn:escapeXml(meter.name)}</td>
                                        <c:if test="${!data.readAfterAll}">
                                            <c:set var="phaseCReading" value="${phaseToReadingMap['C']}"/>
                                            <td>
                                                <i:inline key=".initial"/>&nbsp;<strong>${phaseCReading.initial}</strong>&nbsp;
                                                <i:inline key=".last"/>&nbsp;<strong>${phaseCReading.last}</strong>&nbsp;
                                                <i:inline key=".delta"/>&nbsp;
                                                <c:choose>
                                                    <c:when test="${phaseCReading.delta gt 0}">
                                                        <c:set var="resultClass" value="success"/>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <c:set var="resultClass" value="error"/>
                                                    </c:otherwise>
                                                </c:choose>
                                                <strong class="${resultClass}">${phaseCReading.delta}</strong>
                                            </td>
                                        </c:if>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                    <c:if test="${phaseCCollection.deviceCount > 0}">
                        <div class="action-area">
                            <cti:url value="/bulk/collectionActions" var="actionUrl">
                                <cti:mapParam value="${phaseCCollection.collectionParameters}"/>
                            </cti:url>
                            <cti:button nameKey="collectionAction" href="${actionUrl}" icon="icon-cog-go"/>
                            <tags:selectedDevicesPopup deviceCollection="${phaseCCollection}" type="button"/>
                        </div>
                    </c:if>
                </tags:boxContainer2>
                
                <c:if test="${!data.readAfterAll}">
                    <tags:boxContainer2 nameKey="metersDetectedAB" arguments="${phaseABMetersSize}" hideEnabled="true" showInitially="${phaseABMetersSize > 0}">
                        <div class="scroll-lg">
                            <table class="compact-results-table clearfix">
                                <thead>
                                    <tr>
                                        <th><i:inline key=".meterName" /></th>
                                        <th><i:inline key=".voltageReadings" /></th>
                                    </tr>
                                </thead>
                                <tfoot></tfoot>
                                <tbody>
                                    <c:forEach var="meter" items="${phaseABMeters}">
                                        <c:set var="phaseToReadingMap" value="${result.deviceReadingsMap[meter.deviceId]}" />
                                        <tr>
                                            <td>${fn:escapeXml(meter.name)}</td>
                                            <c:set var="phaseAReading" value="${phaseToReadingMap['A']}" />
                                            <c:set var="phaseBReading" value="${phaseToReadingMap['B']}" />
                                            <td>
                                                <div>
                                                    <i:inline key=".phaseAInit" />
                                                    &nbsp;<strong>${phaseAReading.initial}</strong>&nbsp;
                                                    <i:inline key=".last" />
                                                    &nbsp;<strong>${phaseAReading.last}</strong>&nbsp;
                                                    <i:inline key=".delta" />
                                                    &nbsp;
                                                    <c:choose>
                                                        <c:when test="${phaseAReading.delta gt 0}">
                                                            <c:set var="resultClass" value="success" />
                                                        </c:when>
                                                        <c:otherwise>
                                                            <c:set var="resultClass" value="error" />
                                                        </c:otherwise>
                                                    </c:choose>
                                                    <strong class="${resultClass}">${phaseAReading.delta}</strong>
                                                </div>
                                                <div>
                                                    <i:inline key=".phaseBInit" />
                                                    &nbsp;<strong>${phaseBReading.initial}</strong>&nbsp;
                                                    <i:inline key=".last" />
                                                    &nbsp;<strong>${phaseBReading.last}</strong>&nbsp;
                                                    <i:inline key=".delta" />
                                                    &nbsp;
                                                    <c:choose>
                                                        <c:when test="${phaseBReading.delta gt 0}">
                                                            <c:set var="resultClass" value="success" />
                                                        </c:when>
                                                        <c:otherwise>
                                                            <c:set var="resultClass" value="error" />
                                                        </c:otherwise>
                                                    </c:choose>
                                                    <strong class="${resultClass}">${phaseBReading.delta}</strong>
                                                </div>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                        <c:if test="${phaseABCollection.deviceCount > 0}">
                            <div class="action-area">
                                <cti:url value="/bulk/collectionActions" var="actionUrl">
                                    <cti:mapParam value="${phaseABCollection.collectionParameters}"/>
                                </cti:url>
                                <cti:button nameKey="collectionAction" href="${actionUrl}" icon="icon-cog-go"/>
                                <tags:selectedDevicesPopup deviceCollection="${phaseABCollection}" type="button"/>
                            </div>
                        </c:if>
                    </tags:boxContainer2>
                    
                    <%-- AC --%>
                    <tags:boxContainer2 nameKey="metersDetectedAC" arguments="${phaseACMetersSize}" hideEnabled="true" showInitially="${phaseACMetersSize > 0}">
                        <div class="scroll-lg">
                            <table class="compact-results-table clearfix">
                                <thead>
                                    <tr>
                                        <th><i:inline key=".meterName"/></th>
                                        <th><i:inline key=".voltageReadings"/></th>
                                    </tr>
                                </thead>
                                <tfoot></tfoot>
                                <tbody>
                                    <c:forEach var="meter" items="${phaseACMeters}">
                                        <c:set var="phaseToReadingMap" value="${result.deviceReadingsMap[meter.deviceId]}"/>
                                        <tr>
                                            <td>${fn:escapeXml(meter.name)}</td>
                                            <c:set var="phaseAReading" value="${phaseToReadingMap['A']}"/>
                                            <c:set var="phaseCReading" value="${phaseToReadingMap['C']}"/>
                                            <td>
                                                <div>
                                                    <i:inline key=".phaseAInit"/>&nbsp;<strong>${phaseAReading.initial}</strong>&nbsp;
                                                    <i:inline key=".last"/>&nbsp;<strong>${phaseAReading.last}</strong>&nbsp;
                                                    <i:inline key=".delta"/>&nbsp;
                                                    <c:choose>
                                                        <c:when test="${phaseAReading.delta gt 0}">
                                                            <c:set var="resultClass" value="success"/>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <c:set var="resultClass" value="error"/>
                                                        </c:otherwise>
                                                    </c:choose> 
                                                    <strong class="${resultClass}">${phaseAReading.delta}</strong>
                                                </div>
                                                <div>
                                                    <i:inline key=".phaseCInit"/>&nbsp;<strong>${phaseCReading.initial}</strong>&nbsp;
                                                    <i:inline key=".last"/>&nbsp;<strong>${phaseCReading.last}</strong>&nbsp;
                                                    <i:inline key=".delta"/>&nbsp;
                                                    <c:choose>
                                                        <c:when test="${phaseCReading.delta gt 0}">
                                                            <c:set var="resultClass" value="success"/>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <c:set var="resultClass" value="error"/>
                                                        </c:otherwise>
                                                    </c:choose> 
                                                    <strong class="${resultClass}">${phaseCReading.delta}</strong>
                                                </div>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                        <c:if test="${phaseACCollection.deviceCount > 0}">
                            <div class="action-area">
                                <cti:url value="/bulk/collectionActions" var="actionUrl">
                                    <cti:mapParam value="${phaseACCollection.collectionParameters}"/>
                                </cti:url>
                                <cti:button nameKey="collectionAction" href="${actionUrl}" icon="icon-cog-go"/>
                                <tags:selectedDevicesPopup deviceCollection="${phaseACCollection}" type="button"/>
                            </div>
                        </c:if>
                    </tags:boxContainer2>
                
                    <%-- BC --%>
                    <tags:boxContainer2 nameKey="metersDetectedBC" arguments="${phaseBCMetersSize}" hideEnabled="true" showInitially="${phaseBCMetersSize > 0}">
                        <div class="scroll-lg">
                            <table class="compact-results-table clearfix">
                                <thead>
                                    <tr>
                                        <th><i:inline key=".meterName"/></th>
                                        <th><i:inline key=".voltageReadings"/></th>
                                    </tr>
                                </thead>
                                <tfoot></tfoot>
                                <tbody>
                                    <c:forEach var="meter" items="${phaseBCMeters}">
                                        <c:set var="phaseToReadingMap" value="${result.deviceReadingsMap[meter.deviceId]}"/>
                                        <tr>
                                            <td>${fn:escapeXml(meter.name)}</td>
                                            <c:set var="phaseBReading" value="${phaseToReadingMap['B']}"/>
                                            <c:set var="phaseCReading" value="${phaseToReadingMap['C']}"/>
                                            <td>
                                                <div>
                                                    <i:inline key=".phaseBInit"/>&nbsp;<strong>${phaseBReading.initial}</strong>&nbsp;
                                                    <i:inline key=".last"/>&nbsp;<strong>${phaseBReading.last}</strong>&nbsp;
                                                    <i:inline key=".delta"/>&nbsp;
                                                    <c:choose>
                                                        <c:when test="${phaseBReading.delta gt 0}">
                                                            <c:set var="resultClass" value="success"/>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <c:set var="resultClass" value="error"/>
                                                        </c:otherwise>
                                                    </c:choose> 
                                                    <strong class="${resultClass}">${phaseBReading.delta}</strong>
                                                </div>
                                                <div>
                                                    <i:inline key=".phaseCInit"/>&nbsp;<strong>${phaseCReading.initial}</strong>&nbsp;
                                                    <i:inline key=".last"/>&nbsp;<strong>${phaseCReading.last}</strong>&nbsp;
                                                    <i:inline key=".delta"/>&nbsp;
                                                    <c:choose>
                                                        <c:when test="${phaseCReading.delta gt 0}">
                                                            <c:set var="resultClass" value="success"/>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <c:set var="resultClass" value="error"/>
                                                        </c:otherwise>
                                                    </c:choose> 
                                                    <strong class="${resultClass}">${phaseCReading.delta}</strong>
                                                </div>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                        <c:if test="${phaseBCCollection.deviceCount > 0}">
                            <div class="action-area">
                                <cti:url value="/bulk/collectionActions" var="actionUrl">
                                    <cti:mapParam value="${phaseBCCollection.collectionParameters}"/>
                                </cti:url>
                                <cti:button nameKey="collectionAction" href="${actionUrl}" icon="icon-cog-go"/>
                                <tags:selectedDevicesPopup deviceCollection="${phaseBCCollection}" type="button"/>
                            </div>
                        </c:if>
                    </tags:boxContainer2>
                    
                    <%-- ABC --%>
                    <tags:boxContainer2 nameKey="metersDetectedABC" arguments="${phaseBCMetersSize}" hideEnabled="true" showInitially="${phaseABCMetersSize > 0}">
                        <div class="scroll-lg">
                            <table class="compact-results-table clearfix">
                                <thead>
                                    <tr>
                                        <th><i:inline key=".meterName"/></th>
                                        <th><i:inline key=".voltageReadings"/></th>
                                    </tr>
                                </thead>
                                <tfoot></tfoot>
                                <tbody>
                                    <c:forEach var="meter" items="${phaseABCMeters}">
                                        <c:set var="phaseToReadingMap" value="${result.deviceReadingsMap[meter.deviceId]}"/>
                                        <tr>
                                            <td>${fn:escapeXml(meter.name)}</td>
                                            <c:set var="phaseAReading" value="${phaseToReadingMap['A']}"/>
                                            <c:set var="phaseBReading" value="${phaseToReadingMap['B']}"/>
                                            <c:set var="phaseCReading" value="${phaseToReadingMap['C']}"/>
                                            <td>
                                                <div>
                                                    <i:inline key=".phaseAInit"/>&nbsp;<strong>${phaseAReading.initial}</strong>&nbsp;
                                                    <i:inline key=".last"/>&nbsp;<strong>${phaseAReading.last}</strong>&nbsp;
                                                    <i:inline key=".delta"/>&nbsp;
                                                    <c:choose>
                                                        <c:when test="${phaseAReading.delta gt 0}">
                                                            <c:set var="resultClass" value="success"/>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <c:set var="resultClass" value="error"/>
                                                        </c:otherwise>
                                                    </c:choose> 
                                                    <strong class="${resultClass}">${phaseAReading.delta}</strong>
                                                </div>
                                                <div>
                                                    <i:inline key=".phaseBInit"/>&nbsp;<strong>${phaseBReading.initial}</strong>&nbsp;
                                                    <i:inline key=".last"/>&nbsp;<strong>${phaseBReading.last}</strong>&nbsp;
                                                    <i:inline key=".delta"/>&nbsp;
                                                    <c:choose>
                                                        <c:when test="${phaseBReading.delta gt 0}">
                                                            <c:set var="resultClass" value="success"/>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <c:set var="resultClass" value="error"/>
                                                        </c:otherwise>
                                                    </c:choose> 
                                                    <strong class="${resultClass}">${phaseBReading.delta}</strong>
                                                </div>
                                                <div>
                                                    <i:inline key=".phaseCInit"/>&nbsp;<strong>${phaseCReading.initial}</strong>&nbsp;
                                                    <i:inline key=".last"/>&nbsp;<strong>${phaseCReading.last}</strong>&nbsp;
                                                    <i:inline key=".delta"/>&nbsp;
                                                    <c:choose>
                                                        <c:when test="${phaseCReading.delta gt 0}">
                                                            <c:set var="resultClass" value="success"/>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <c:set var="resultClass" value="error"/>
                                                        </c:otherwise>
                                                    </c:choose> 
                                                    <strong class="${resultClass}">${phaseCReading.delta}</strong>
                                                </div>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                        <c:if test="${phaseABCCollection.deviceCount > 0}">
                            <div class="action-area">
                                <cti:url value="/bulk/collectionActions" var="actionUrl">
                                    <cti:mapParam value="${phaseABCCollection.collectionParameters}"/>
                                </cti:url>
                                <cti:button nameKey="collectionAction" href="${actionUrl}" icon="icon-cog-go"/>
                                <tags:selectedDevicesPopup deviceCollection="${phaseABCCollection}" type="button"/>
                            </div>
                        </c:if>
                    </tags:boxContainer2>
                    
                </c:if>

                <tags:boxContainer2 nameKey="metersUndefined" arguments="${undefinedMetersSize}" hideEnabled="true" showInitially="${undefinedMetersSize > 0}">
                    <div class="scroll-lg">
                        <ul class="simple-list striped-list">
                            <c:forEach var="meter" items="${undefinedMeters}">
                                <li>${fn:escapeXml(meter.name)}</li>
                            </c:forEach>
                        </ul>
                    </div>
                    <c:if test="${undefinedCollection.deviceCount > 0}">
                        <div class="action-area">
                            <cti:url value="/bulk/collectionActions" var="actionUrl">
                                <cti:mapParam value="${undefinedCollection.collectionParameters}"/>
                            </cti:url>
                            <cti:button nameKey="collectionAction" href="${actionUrl}" icon="icon-cog-go"/>
                            <tags:selectedDevicesPopup deviceCollection="${undefinedCollection}" type="button"/>
                        </div>
                    </c:if>
                </tags:boxContainer2>
                
                <tags:boxContainer2 nameKey="failureGroup" arguments="${failureMetersSize}" hideEnabled="true" showInitially="${failureMetersSize > 0}">
                    <div class="scroll-lg">
                        <table class="compact-results-table clearfix">
                            <thead>
                                <tr>
                                    <th><i:inline key=".meterName"/></th>
                                    <th><i:inline key=".errorMsg"/></th>
                                </tr>
                            </thead>
                            <tfoot></tfoot>
                            <tbody>
                                <c:forEach var="meter" items="${failureMeters}">
                                    <tr>
                                        <td>${fn:escapeXml(meter.name)}</td>
                                        <td><span class="error">${failureMetersMap[meter]}</span></td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                    <c:if test="${failureCollection.deviceCount > 0}">
                        <div class="action-area">
                            <cti:url value="/bulk/collectionActions" var="actionUrl">
                                <cti:mapParam value="${failureCollection.collectionParameters}"/>
                            </cti:url>
                            <cti:button nameKey="collectionAction" href="${actionUrl}" icon="icon-cog-go"/>
                            <tags:selectedDevicesPopup deviceCollection="${failureCollection}" type="button"/>
                        </div>
                    </c:if>
                </tags:boxContainer2>
            </div>
            
            <input type="hidden" class="js-cache-key" value="${cacheKey}"/>
            <div class="column two nogutter">
                <!-- Pie Chart -->
                <div id="js-pie-chart-container"></div>
            </div>
        </div>
    </tags:sectionContainer2>
    <cti:includeScript link="/resources/js/pages/yukon.ami.phaseDetectResults.js" />
</cti:standardPage>