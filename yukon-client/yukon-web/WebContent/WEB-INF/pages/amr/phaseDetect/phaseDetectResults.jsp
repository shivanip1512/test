<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="amr" page="phaseDetect.results">
    <cti:msg2 key="yukon.web.modules.amr.phaseDetect.step6.sectionTitle" var="sectionTitle"/>
    <tags:sectionContainer title="${sectionTitle}">
        <table width="100%">
            <c:if test="${not empty cacheKey}">
                <tr>
                    <td style="padding-bottom: 10px;">
                        <cti:simpleReportLinkFromNameTag definitionName="phaseDetectDefinition" viewType="csvView" cacheKey="${cacheKey}">
                        <i:inline key=".exportCsv"/>
                        </cti:simpleReportLinkFromNameTag>
                    </td>
                </tr>
            </c:if>
            <tr>
                <td width="50%" valign="top">
                    <div>
                        <div style="padding-bottom: 5px;">
		                    <c:set var="showA" value="${phaseAMetersSize > 0}" />
					        <tags:boxContainer2 nameKey="metersDetectedA" arguments="${phaseAMetersSize}" hideEnabled="true" showInitially="${showA}">
					            <div style="max-height: 300px;overflow: auto;">
						            <table>
                                        <tr>
                                            <th style="border-bottom: 1px solid #ccc;padding-right: 10px;"><b><i:inline key=".meterName"/></b></th>
                                            <c:if test="${!data.readAfterAll}"><th style="border-bottom: 1px solid #ccc;"><b><i:inline key=".voltageReadings"/></b></th></c:if>
                                        </tr>
							            <c:forEach var="meter" items="${phaseAMeters}">
                                            <c:set var="phaseToReadingMap" value="${result.deviceReadingsMap[meter.deviceId]}"/>
							                <tr>
							                    <td style="padding-right: 10px;">${meter.name}</td>
							                    <c:if test="${!data.readAfterAll}">
                                                    <c:set var="phaseAReading" value="${phaseToReadingMap['A']}"/>
                                                    <td><i:inline key=".initial"/>
                                                        <span style="font-weight: bold;">${phaseAReading.initial}</span> <i:inline key=".last"/>
                                                        <span style="font-weight: bold;">${phaseAReading.last}</span> <i:inline key=".delta"/>
                                                        <c:choose >
                                                            <c:when test="${phaseAReading.delta gt 0}">
                                                                <c:set var="spanClass" value="successMessage"/>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <c:set var="spanClass" value="errorMessage"/>
                                                            </c:otherwise>
                                                        </c:choose>
                                                        <span style="font-weight: bold;" class="${spanClass}">${phaseAReading.delta}</span>
                                                    </td>
                                                </c:if>
							                </tr>
							            </c:forEach>
						            </table>
					            </div>
					        </tags:boxContainer2>
                            <c:if test="${phaseACollection.deviceCount > 0}">
                                <div id="phaseAActionsDiv" style="padding:5px;">
                                    <cti:link href="/spring/bulk/collectionActions" key="yukon.web.modules.amr.phaseDetect.step6.phaseA.collectionActionLabel" class="small">
                                        <cti:mapParam value="${phaseACollection.collectionParameters}"/>
                                    </cti:link>
                                    <tags:selectedDevicesPopup deviceCollection="${phaseACollection}" />
                                </div>
                            </c:if>
				        </div>
				        <div style="padding-bottom: 5px;">
					        <c:set var="showB" value="${phaseBMetersSize > 0}" />
                            <tags:boxContainer2 nameKey="metersDetectedB" arguments="${phaseBMetersSize}" hideEnabled="true" showInitially="false">
		                        <div style="max-height: 300px;overflow: auto;">
			                        <table>
                                        <tr>
                                            <th style="border-bottom: 1px solid #ccc;padding-right: 10px;"><b><i:inline key=".meterName"/></b></th>
                                            <c:if test="${!data.readAfterAll}"><th style="border-bottom: 1px solid #ccc;"><b><i:inline key=".voltageReadings"/></b></th></c:if>
                                        </tr>
			                            <c:forEach var="meter" items="${phaseBMeters}">
                                            <c:set var="phaseToReadingMap" value="${result.deviceReadingsMap[meter.deviceId]}"/>
			                                <tr>
			                                    <td style="padding-right: 10px;">${meter.name}</td>
			                                    <c:if test="${!data.readAfterAll}">
                                                    <c:set var="phaseBReading" value="${phaseToReadingMap['B']}"/>
                                                    <td><i:inline key=".initial"/>
                                                        <span style="font-weight: bold;">${phaseBReading.initial}</span> <i:inline key=".last"/>
                                                        <span style="font-weight: bold;">${phaseBReading.last}</span> <i:inline key=".delta"/>
                                                        <c:choose >
                                                            <c:when test="${phaseBReading.delta gt 0}">
                                                                <c:set var="spanClass" value="successMessage"/>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <c:set var="spanClass" value="errorMessage"/>
                                                            </c:otherwise>
                                                        </c:choose> 
                                                        <span style="font-weight: bold;" class="${spanClass}">${phaseBReading.delta}</span>
                                                    </td>
                                                </c:if>
			                                </tr>
			                            </c:forEach>
			                        </table>
		                        </div>
		                    </tags:boxContainer2>
                            <c:if test="${phaseBCollection.deviceCount > 0}">
                                <div id="phaseBActionsDiv" style="padding:5px;">
                                    <cti:link href="/spring/bulk/collectionActions" key="yukon.web.modules.amr.phaseDetect.step6.phaseB.collectionActionLabel" class="small">
                                        <cti:mapParam value="${phaseBCollection.collectionParameters}"/>
                                    </cti:link>
                                    <tags:selectedDevicesPopup deviceCollection="${phaseBCollection}" />
                                </div>
                            </c:if>
	                    </div>
	                    <div style="padding-bottom: 5px;">
		                    <c:set var="showC" value="${phaseCMetersSize > 0}" />
		                    <tags:boxContainer2 nameKey="metersDetectedC" arguments="${phaseCMetersSize}" hideEnabled="true" showInitially="false">
		                        <div style="max-height: 300px;overflow: auto;">
			                        <table>
                                        <tr>
                                            <th style="border-bottom: 1px solid #ccc;padding-right: 10px;"><b><i:inline key=".meterName"/></b></th>
                                            <c:if test="${!data.readAfterAll}"><th style="border-bottom: 1px solid #ccc;"><b><i:inline key=".voltageReadings"/></b></th></c:if>
                                        </tr>
			                            <c:forEach var="meter" items="${phaseCMeters}">
                                            <c:set var="phaseToReadingMap" value="${result.deviceReadingsMap[meter.deviceId]}"/>
			                                <tr>
			                                    <td style="padding-right: 10px;">${meter.name}</td>
			                                    <c:if test="${!data.readAfterAll}">
                                                    <c:set var="phaseCReading" value="${phaseToReadingMap['C']}"/>
                                                    <td><i:inline key=".initial"/> 
                                                        <span style="font-weight: bold;">${phaseCReading.initial}</span> <i:inline key=".last"/>
                                                        <span style="font-weight: bold;">${phaseCReading.last}</span> <i:inline key=".delta"/>
                                                        <c:choose >
                                                            <c:when test="${phaseCReading.delta gt 0}">
                                                                <c:set var="spanClass" value="successMessage"/>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <c:set var="spanClass" value="errorMessage"/>
                                                            </c:otherwise>
                                                        </c:choose> 
                                                        <span style="font-weight: bold;" class="${spanClass}">${phaseCReading.delta}</span>
                                                    </td>
                                                </c:if>
			                                </tr>
			                            </c:forEach>
			                        </table>
		                        </div>
		                    </tags:boxContainer2>
                            <c:if test="${phaseCCollection.deviceCount > 0}">
                                <div id="phaseCActionsDiv" style="padding:5px;">
                                    <cti:link href="/spring/bulk/collectionActions" key="yukon.web.modules.amr.phaseDetect.step6.phaseC.collectionActionLabel" class="small">
                                        <cti:mapParam value="${phaseCCollection.collectionParameters}"/>
                                    </cti:link>
                                    <tags:selectedDevicesPopup deviceCollection="${phaseCCollection}" />
                                </div>
                            </c:if>
    	                </div>
    	                <c:if test="${!data.readAfterAll}">
	    	                <div style="padding-bottom: 5px;">
	                            <c:set var="showAB" value="${phaseABMetersSize > 0}" />
<%-- 	                            <cti:msg2 key=".metersDetectedAB" var="metersDetectedAB"/> --%>
                                <tags:boxContainer2 nameKey="metersDetectedAB" arguments="${phaseABMetersSize}" hideEnabled="true" showInitially="${showAB}">
	                                <div style="max-height: 300px;overflow: auto;">
	                                    <table>
                                            <tr>
	                                            <th style="border-bottom: 1px solid #ccc;padding-right: 10px;"><b><i:inline key=".meterName"/></b></th>
	                                            <th style="border-bottom: 1px solid #ccc;"><b><i:inline key=".voltageReadings"/></b></th>
	                                        </tr>
	                                        <c:forEach var="meter" items="${phaseABMeters}">
                                                <c:set var="phaseToReadingMap" value="${result.deviceReadingsMap[meter.deviceId]}"/>
	                                            <tr>
	                                                <td style="padding-right: 10px;">${meter.name}</td>
                                                    <c:set var="phaseAReading" value="${phaseToReadingMap['A']}"/>
                                                    <c:set var="phaseBReading" value="${phaseToReadingMap['B']}"/>
                                                    <td><i:inline key=".phaseAInit"/>
                                                        <span style="font-weight: bold;">${phaseAReading.initial}</span> <i:inline key=".last"/>
                                                        <span style="font-weight: bold;">${phaseAReading.last}</span> <i:inline key=".delta"/>
                                                        <c:choose >
                                                            <c:when test="${phaseAReading.delta gt 0}">
                                                                <c:set var="spanClass" value="successMessage"/>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <c:set var="spanClass" value="errorMessage"/>
                                                            </c:otherwise>
                                                        </c:choose> 
                                                        <span style="font-weight: bold;" class="${spanClass}">${phaseAReading.delta}</span><br>
                                                        <i:inline key=".phaseBInit"/> 
                                                        <span style="font-weight: bold;">${phaseBReading.initial}</span> <i:inline key=".last"/>
                                                        <span style="font-weight: bold;">${phaseBReading.last}</span> <i:inline key=".delta"/>
                                                        <c:choose >
                                                            <c:when test="${phaseBReading.delta gt 0}">
                                                                <c:set var="spanClass" value="successMessage"/>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <c:set var="spanClass" value="errorMessage"/>
                                                            </c:otherwise>
                                                        </c:choose> 
                                                        <span style="font-weight: bold;" class="${spanClass}">${phaseBReading.delta}</span>
                                                    </td>
	                                            </tr>
	                                        </c:forEach>
	                                    </table>
	                                </div>
	                            </tags:boxContainer2>
                                <c:if test="${phaseABCollection.deviceCount > 0}">
                                    <div id="phaseABActionsDiv" style="padding:5px;">
                                        <cti:link href="/spring/bulk/collectionActions" key="yukon.web.modules.amr.phaseDetect.step6.phaseAB.collectionActionLabel" class="small">
                                            <cti:mapParam value="${phaseABCollection.collectionParameters}"/>
                                        </cti:link>
                                        <tags:selectedDevicesPopup deviceCollection="${phaseABCollection}" />
                                    </div>
                                </c:if>
	                        </div>
	                        <div style="padding-bottom: 5px;">
	                            <c:set var="showAC" value="${phaseACMetersSize > 0}" />
                                <tags:boxContainer2 nameKey="metersDetectedAC" arguments="${phaseACMetersSize}" hideEnabled="true" showInitially="${showAC}">
	                                <div style="max-height: 300px;overflow: auto;">
	                                    <table>
                                            <tr>
                                                <th style="border-bottom: 1px solid #ccc;padding-right: 10px;"><b><i:inline key=".meterName"/></b></th>
                                                <th style="border-bottom: 1px solid #ccc;"><b><i:inline key=".voltageReadings"/></b></th>
                                            </tr>
	                                        <c:forEach var="meter" items="${phaseACMeters}">
                                                <c:set var="phaseToReadingMap" value="${result.deviceReadingsMap[meter.deviceId]}"/>
	                                            <tr>
	                                                <td style="padding-right: 10px;">${meter.name}</td>
	                                                <c:set var="phaseAReading" value="${phaseToReadingMap['A']}"/>
                                                    <c:set var="phaseCReading" value="${phaseToReadingMap['C']}"/>
                                                    <td><i:inline key=".phaseAInit"/> 
                                                        <span style="font-weight: bold;">${phaseAReading.initial}</span> <i:inline key=".last"/>
                                                        <span style="font-weight: bold;">${phaseAReading.last}</span> <i:inline key=".delta"/>
                                                        <c:choose >
                                                            <c:when test="${phaseAReading.delta gt 0}">
                                                                <c:set var="spanClass" value="successMessage"/>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <c:set var="spanClass" value="errorMessage"/>
                                                            </c:otherwise>
                                                        </c:choose> 
                                                        <span style="font-weight: bold;" class="${spanClass}">${phaseAReading.delta}</span><br>
                                                        <i:inline key=".phaseCInit"/> 
                                                        <span style="font-weight: bold;">${phaseCReading.initial}</span> <i:inline key=".last"/>
                                                        <span style="font-weight: bold;">${phaseCReading.last}</span> <i:inline key=".delta"/>
                                                        <c:choose >
                                                            <c:when test="${phaseCReading.delta gt 0}">
                                                                <c:set var="spanClass" value="successMessage"/>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <c:set var="spanClass" value="errorMessage"/>
                                                            </c:otherwise>
                                                        </c:choose> 
                                                        <span style="font-weight: bold;" class="${spanClass}">${phaseCReading.delta}</span>
                                                    </td>
	                                            </tr>
	                                        </c:forEach>
	                                    </table>
	                                </div>
	                            </tags:boxContainer2>
                                <c:if test="${phaseACCollection.deviceCount > 0}">
                                    <div id="phaseACActionsDiv" style="padding:5px;">
                                        <cti:link href="/spring/bulk/collectionActions" key="yukon.web.modules.amr.phaseDetect.step6.phaseAC.collectionActionLabel" class="small">
                                            <cti:mapParam value="${phaseACCollection.collectionParameters}"/>
                                        </cti:link>
                                        <tags:selectedDevicesPopup deviceCollection="${phaseACCollection}" />
                                    </div>
                                </c:if>
	                        </div>
	                        <div style="padding-bottom: 5px;">
	                            <c:set var="showBC" value="${phaseBCMetersSize > 0}" />
                                <tags:boxContainer2 nameKey="metersDetectedBC" arguments="${phaseBCMetersSize}" hideEnabled="true" showInitially="${showBC}">
	                                <div style="max-height: 300px;overflow: auto;">
	                                    <table>
                                            <tr>
                                                <th style="border-bottom: 1px solid #ccc;padding-right: 10px;"><b><i:inline key=".meterName"/></b></th>
                                                <th style="border-bottom: 1px solid #ccc;"><b><i:inline key=".voltageReadings"/></b></th>
                                            </tr>
	                                        <c:forEach var="meter" items="${phaseBCMeters}">
                                                <c:set var="phaseToReadingMap" value="${result.deviceReadingsMap[meter.deviceId]}"/>
	                                            <tr>
	                                                <td style="padding-right: 10px;">${meter.name}</td>
                                                    <c:set var="phaseBReading" value="${phaseToReadingMap['B']}"/>
                                                    <c:set var="phaseCReading" value="${phaseToReadingMap['C']}"/>
	                                                <td><i:inline key=".phaseBInit"/> 
                                                        <span style="font-weight: bold;">${phaseBReading.initial}</span> <i:inline key=".last"/>
                                                        <span style="font-weight: bold;">${phaseBReading.last}</span> <i:inline key=".delta"/>
                                                        <c:choose >
                                                            <c:when test="${phaseBReading.delta gt 0}">
                                                                <c:set var="spanClass" value="successMessage"/>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <c:set var="spanClass" value="errorMessage"/>
                                                            </c:otherwise>
                                                        </c:choose> 
                                                        <span style="font-weight: bold;" class="${spanClass}">${phaseBReading.delta}</span><br>
                                                        <i:inline key=".phaseCInit"/>
                                                        <span style="font-weight: bold;">${phaseCReading.initial}</span> <i:inline key=".last"/>
                                                        <span style="font-weight: bold;">${phaseCReading.last}</span> <i:inline key=".delta"/>
                                                        <c:choose >
                                                            <c:when test="${phaseCReading.delta gt 0}">
                                                                <c:set var="spanClass" value="successMessage"/>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <c:set var="spanClass" value="errorMessage"/>
                                                            </c:otherwise>
                                                        </c:choose> 
                                                        <span style="font-weight: bold;" class="${spanClass}">${phaseCReading.delta}</span>
                                                    </td>
	                                            </tr>
	                                        </c:forEach>
	                                    </table>
	                                </div>
	                            </tags:boxContainer2>
                                <c:if test="${phaseBCCollection.deviceCount > 0}">
                                    <div id="phaseBCActionsDiv" style="padding:5px;">
                                        <cti:link href="/spring/bulk/collectionActions" key="yukon.web.modules.amr.phaseDetect.step6.phaseBC.collectionActionLabel" class="small">
                                            <cti:mapParam value="${phaseBCCollection.collectionParameters}"/>
                                        </cti:link>
                                        <tags:selectedDevicesPopup deviceCollection="${phaseBCCollection}" />
                                    </div>
                                </c:if>
	                        </div>
	    	                <div style="padding-bottom: 5px;">
	                            <c:set var="showABC" value="${phaseABCMetersSize > 0}" />
	                            <tags:boxContainer2 nameKey="metersDetectedABC" arguments="${phaseABCMetersSize}" hideEnabled="true" showInitially="${showABC}">
	                                <div style="max-height: 300px;overflow: auto;">
	                                    <table>
                                            <tr>
                                                <th style="border-bottom: 1px solid #ccc;padding-right: 10px;"><b><i:inline key=".meterName"/></b></th>
                                                <th style="border-bottom: 1px solid #ccc;"><b><i:inline key=".voltageReadings"/></b></th>
                                            </tr>
	                                        <c:forEach var="meter" items="${phaseABCDevices}">
                                                <c:set var="phaseToReadingMap" value="${result.deviceReadingsMap[meter.deviceId]}"/>
	                                            <tr>
	                                                <td style="padding-right: 10px;">${meter.name}</td>
	                                                <c:set var="phaseAReading" value="${phaseToReadingMap['A']}"/>
	                                                <c:set var="phaseBReading" value="${phaseToReadingMap['B']}"/>
                                                    <c:set var="phaseCReading" value="${phaseToReadingMap['C']}"/>
                                                    <td><i:inline key=".phaseAInit"/> 
                                                        <span style="font-weight: bold;">${phaseAReading.initial}</span> <i:inline key=".last"/>
                                                        <span style="font-weight: bold;">${phaseAReading.last}</span> <i:inline key=".delta"/>
                                                        <c:choose >
                                                            <c:when test="${phaseAReading.delta gt 0}">
                                                                <c:set var="spanClass" value="successMessage"/>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <c:set var="spanClass" value="errorMessage"/>
                                                            </c:otherwise>
                                                        </c:choose> 
                                                        <span style="font-weight: bold;" class="${spanClass}">${phaseAReading.delta}</span><br>
                                                        <i:inline key=".phaseBInit"/> 
                                                        <span style="font-weight: bold;">${phaseBReading.initial}</span> <i:inline key=".last"/>
                                                        <span style="font-weight: bold;">${phaseBReading.last}</span> <i:inline key=".delta"/>
                                                        <c:choose >
                                                            <c:when test="${phaseBReading.delta gt 0}">
                                                                <c:set var="spanClass" value="successMessage"/>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <c:set var="spanClass" value="errorMessage"/>
                                                            </c:otherwise>
                                                        </c:choose> 
                                                        <span style="font-weight: bold;" class="${spanClass}">${phaseBReading.delta}</span><br>
                                                        <i:inline key=".phaseCInit"/> 
                                                        <span style="font-weight: bold;">${phaseCReading.initial}</span> <i:inline key=".last"/>
                                                        <span style="font-weight: bold;">${phaseCReading.last}</span> <i:inline key=".delta"/>
                                                        <c:choose >
                                                            <c:when test="${phaseCReading.delta gt 0}">
                                                                <c:set var="spanClass" value="successMessage"/>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <c:set var="spanClass" value="errorMessage"/>
                                                            </c:otherwise>
                                                        </c:choose> 
                                                        <span style="font-weight: bold;" class="${spanClass}">${phaseCReading.delta}</span>
                                                    </td>
	                                            </tr>
	                                        </c:forEach>
	                                    </table>
	                                </div>
	                            </tags:boxContainer2>
                                <c:if test="${phaseABCCollection.deviceCount > 0}">
                                    <div id="phaseABCActionsDiv" style="padding:5px;">
                                        <cti:link href="/spring/bulk/collectionActions" key="yukon.web.modules.amr.phaseDetect.step6.phaseABC.collectionActionLabel" class="small">
                                            <cti:mapParam value="${phaseABCCollection.collectionParameters}"/>
                                        </cti:link>
                                        <tags:selectedDevicesPopup deviceCollection="${phaseABCCollection}" />
                                    </div>
                                </c:if>
	                        </div>
                        </c:if>
    	                <div style="padding-bottom: 5px;">
	    	                <c:set var="showUndefined" value="${undefinedMetersSize > 0}" />
		                    <tags:boxContainer2 nameKey="metersUndefined" arguments="${undefinedMetersSize}" hideEnabled="true" showInitially="${showUndefined}">
		                        <div style="max-height: 300px;overflow: auto;">
			                        <table>
			                            <c:forEach var="meter" items="${undefinedMeters}">
			                                <tr>
			                                    <td>${meter.name}</td>
			                                </tr>
			                            </c:forEach>
			                        </table>
		                        </div>
		                    </tags:boxContainer2>
                            <c:if test="${undefinedCollection.deviceCount > 0}">
                                <div id="undefinedActionsDiv" style="padding:5px;">
                                    <cti:link href="/spring/bulk/collectionActions" key="yukon.web.modules.amr.phaseDetect.step6.undefined.collectionActionLabel" class="small">
                                        <cti:mapParam value="${undefinedCollection.collectionParameters}"/>
                                    </cti:link>
                                    <tags:selectedDevicesPopup deviceCollection="${undefinedCollection}" />
                                </div>
                            </c:if>
	                    </div>
	                    <div>
                            <c:set var="showFailure" value="${failureMetersSize > 0}" />
                            <tags:boxContainer2 nameKey="failureGroup" arguments="${failureMetersSize}" hideEnabled="true" showInitially="${showFailure}">
                                <div style="max-height: 300px;overflow: auto;">
                                    <table>
                                        <tr>
                                            <th style="border-bottom: 1px solid #ccc;padding-right: 10px;"><b><i:inline key=".meterName"/></b></th>
                                            <th style="border-bottom: 1px solid #ccc;"><b><i:inline key=".errorMsg"/></b></th>
                                        </tr>
                                        <c:forEach var="meter" items="${failureMeters}">
                                            <tr>
                                                <td style="padding-right: 10px;" nowrap="nowrap">${meter.name}</td>
                                                <td><font color="red">${failureMetersMap[meter]}</font></td>
                                            </tr>
                                        </c:forEach>
                                    </table>
                                </div>
                            </tags:boxContainer2>
                            <c:if test="${failureCollection.deviceCount > 0}">
                                <div id="failureActionsDiv" style="padding:5px;">
                                    <cti:link href="/spring/bulk/collectionActions" key="yukon.web.modules.amr.phaseDetect.step6.failure.collectionActionLabel" class="small">
                                        <cti:mapParam value="${failureCollection.collectionParameters}"/>
                                    </cti:link>
                                    <tags:selectedDevicesPopup deviceCollection="${failureCollection}" />
                                </div>
                            </c:if>
	                    </div>
			        </div>
                </td>
                <td height="100%" width="50%" valign="top">
                    <!-- Pie Chart -->
			        <c:set var="amChartsProduct" value="ampie"/>
			        <c:url var="amDataFile" scope="page" value="/spring/amr/phaseDetect/chartData"/>
			        <c:url var="amSettingsFile" scope="page" value="/spring/amr/phaseDetect/chartSettings"/>
			        <c:url var="amSrc" scope="page" value="/JavaScript/amChart/${amChartsProduct}.swf">
			            <c:param name="${amChartsProduct}_path" value="/JavaScript/amChart/" />
			            <c:param name="${amChartsProduct}_flashWidth" value="100%" />
			            <c:param name="${amChartsProduct}_flashHeight" value="100%" />
			            <c:param name="${amChartsProduct}_preloaderColor" value="#000000" />
			            <c:param name="${amChartsProduct}_settingsFile" value="${amSettingsFile}" />
			            <c:param name="${amChartsProduct}_dataFile" value="${amDataFile}" />
			        </c:url>
			        
			        <c:url var="expressInstallSrc" scope="page" value="/JavaScript/expressinstall.swf" />
			        <cti:includeScript link="/JavaScript/swfobject.js"/>
			
			        <cti:uniqueIdentifier var="uniqueId" prefix="flashDiv_"/>
			        <div id="${uniqueId}">
			            <div style="width:90%;text-align:center;">
			                <br>
			                <br>
			                <h4><i:inline key=".adobeRequired"/></h4>
			                <br>
			                <i:inline key=".adobePleaseDL"/>
			                <br>
			                <br>
			                <a href="http://www.adobe.com" target="_blank"><img border="0" src="<c:url value="/WebConfig/yukon/Icons/visitadobe.gif"/>" /></a>
			                <br>
			            </div>
			        </div>
			        
			        <c:set var="swfWidth" value="100%"/>
			        
			        <script type="text/javascript">
			           var so = new SWFObject("${amSrc}", "dataGraph", "${swfWidth}", "500", "8", "#FFFFFF");
			           so.useExpressInstall('${expressInstallSrc}');
			           so.write("${uniqueId}");
			        </script>
                </td>
            </tr>
        </table>
    </tags:sectionContainer>
</cti:standardPage>