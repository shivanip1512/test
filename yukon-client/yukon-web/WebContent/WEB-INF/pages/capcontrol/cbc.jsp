<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:standardPage module="capcontrol" page="cbc.${mode}">
	<cti:msgScope paths="yukon.web.modules.capcontrol.cbc">

		<tags:setFormEditMode mode="${mode}" />

		<cti:url var="action" value="/capcontrol/cbc" />
		<form:form id="cbcEditor-form" commandName="capControlCBC"
			action="${action}" method="POST">
			<cti:csrfToken />
			<form:hidden path="yukonPAObject.paObjectID" />
			<form:hidden path="yukonPAObject.paoType" />
			<cti:tabs containerName="yukon:capcontrol:cbcEditor:tab">
            <cti:msg2 var="generalTab" key=".tab.general" />
            <cti:tab title="${generalTab}">
                
						<tags:nameValueContainer2 tableClass="natural-width">
							<%-- <tags:inputNameValue nameKey=".type" path="type"/> --%>
							<tags:nameValue2 nameKey=".type">${capControlCBC.yukonPAObject.paoType.dbString}</tags:nameValue2>
							<tags:nameValue2 nameKey=".class">${capControlCBC.yukonPAObject.paoClass}</tags:nameValue2>
							<tags:nameValue2 nameKey=".parent">${capControlCBC.cbcParent}</tags:nameValue2>
							<tags:nameValue2 nameKey=".name" valueClass="tar">
								<tags:input path="yukonPAObject.paoName" size="25" />
							</tags:nameValue2>

							<tags:checkboxNameValue id="" path="disableFlag"
								nameKey=".disable" excludeColon="true" />

						</tags:nameValueContainer2>
				</cti:tab>
				<cti:msg2 var="setupTab" key=".tab.setup" />
            <cti:tab title="${setupTab}">
				
					<div class="column-12-12 clearfix">
						<div class="column one">
							<tags:sectionContainer2 nameKey="config" styleClass="stacked-lg">
							
								<tags:nameValueContainer2 tableClass="natural-width">
								<c:choose>
									 <c:when test="${capControlCBC.twoWay}">
											
										<tags:nameValue2 nameKey=".serialNumber" valueClass="tar">
											<tags:input path="deviceCBC.serialNumber" size="25" id="serialNum" />
										</tags:nameValue2>
										<tags:nameValue2 nameKey=".masterAddr" valueClass="tar">
											<tags:input path="deviceAddress.masterAddress" size="25" />
										</tags:nameValue2>
										<tags:nameValue2 nameKey=".slaveAddr" valueClass="tar">
											<tags:input path="deviceAddress.slaveAddress" size="25" />
										</tags:nameValue2>
										
										<tags:nameValue2 nameKey=".commChannel">
													<tags:selectWithItems path="deviceDirectCommSettings.portID"
													items="${selectionLists.commChannels}" itemValue="value"
													itemLabel="label"
													inputClass="with-option-hiding" />
												</tags:nameValue2>
										
											<tags:nameValue2 nameKey=".postCommWait" valueClass="tar">
											<tags:input path="deviceAddress.postCommWait" size="25" />
										</tags:nameValue2>
										
										<div class="column-6-6 clearfix">
											<div class="column one">
												<tags:checkboxNameValue id="" path="editingIntegrity"
												nameKey=".integrityScanRate" excludeColon="true" />
												<tags:nameValue2 nameKey=".interval">
													<tags:intervalStepper path="deviceScanRateMap['Integrity'].intervalRate"
															intervals="${timeIntervals}" />
												</tags:nameValue2>
												<tags:nameValue2 nameKey=".altInterval">
													<tags:intervalStepper path="deviceScanRateMap['Integrity'].alternateRate"
															intervals="${timeIntervals}" />
												</tags:nameValue2>
													<tags:nameValue2 nameKey=".scanGroup">
													<tags:selectWithItems path="deviceScanRateMap['Integrity'].scanGroup"
													items="${scanGroups}" itemValue="value"
													itemLabel="label"
													inputClass="with-option-hiding" />
												</tags:nameValue2>
											</div>
											<div class="column two nogutter">
												<tags:checkboxNameValue id="" path="editingException"
												nameKey=".exceptionScanRate" excludeColon="true" />
												<tags:nameValue2 nameKey=".interval">
													<tags:intervalStepper path="deviceScanRateMap['Exception'].intervalRate"
															intervals="${timeIntervals}" />
												</tags:nameValue2>
												<tags:nameValue2 nameKey=".altInterval">
													<tags:intervalStepper path="deviceScanRateMap['Exception'].alternateRate"
															intervals="${timeIntervals}" />
												</tags:nameValue2>
												<tags:nameValue2 nameKey=".scanGroup">
													<tags:selectWithItems path="deviceScanRateMap['Exception'].scanGroup"
													items="${scanGroups}" itemValue="value"
													itemLabel="label"
													inputClass="with-option-hiding" />
												</tags:nameValue2>
											</div>
										</div>
										</c:when>
									<c:otherwise>
										<tags:nameValue2 nameKey=".serialNumber" valueClass="tar">
											<tags:input path="deviceCBC.serialNumber" size="25" />
										</tags:nameValue2>
											
											<tags:nameValue2 nameKey=".controlRoute">
													<tags:selectWithItems path="deviceCBC.routeID"
													items="${selectionLists.routes}" itemValue="value"
													itemLabel="label"
													inputClass="with-option-hiding" />
												</tags:nameValue2>
										</c:otherwise>
									</c:choose>
								</tags:nameValueContainer2>
							</tags:sectionContainer2>
						</div>
						<div class="column two nogutter">
					<tags:sectionContainer2 nameKey="points" styleClass="stacked-lg">
						<tags:nameValueContainer2 tableClass="natural-width">
							<div id="CBCCtlEditorScrollDiv" class="scroll-md">
								<c:if test="${not empty statusPoints}">
									<li>Status
										<ul>
											<c:forEach var="statusPoint" items="${statusPoints}">
												<input type="hidden" name="statusPoints"
													value="${statusPoint.name}" />

												<li><cti:url var="editUrl"
														value="/tools/points/${statusPoint.pointId}" /> <a
													href="${editUrl}">${fn:escapeXml(statusPoint.name)}</a></li>
											</c:forEach>
										</ul>
									</li>
								</c:if>
								<c:if test="${not empty analogPoints}">
									<li>Analog
										<ul>
											<c:forEach var="analogPoint" items="${analogPoints}">
												<input type="hidden" name="analogPoints"
													value="${analogPoint}" />
												<li><cti:url var="editUrl"
														value="/tools/points/${analogPoint.pointId}" /> <a
													href="${editUrl}">${fn:escapeXml(analogPoint.name)}</a></li>
											</c:forEach>
										</ul>
									</li>
								</c:if>
								<c:if test="${not empty pulseAccumulatorPoints}">
									<li>Accumulator
										<ul>
											<c:forEach var="pulseAccumulatorPoint"
												items="${pulseAccumulatorPoints}">
												<input type="hidden" name="pulseAccumulatorPoints"
													value="${pulseAccumulatorPoint}" />
												<li><cti:url var="editUrl"
														value="/tools/points/${pulseAccumulatorPoint.pointId}" />
													<a href="${editUrl}">${fn:escapeXml(pulseAccumulatorPoint.name)}</a></li>
											</c:forEach>
										</ul>
									</li>
								</c:if>
								<c:if test="${not empty calcAnalogPoints}">
									<li>Calc Analog
										<ul>
											<c:forEach var="calcAnalogPoint" items="${calcAnalogPoints}">
												<input type="hidden" name="calcAnalogPoints"
													value="${caclAnalogPoint}" />
												<li><cti:url var="editUrl"
														value="/tools/points/${calcAnalogPoint.pointId}" />
													<a href="${editUrl}">${fn:escapeXml(calcAnalogPoint.name)}</a></li>
											</c:forEach>
										</ul>
									</li>
								</c:if>
								<c:if test="${not empty calcStatusPoints}">
									<li>Calc Status
										<ul>
											<c:forEach var="calcStatusPoint" items="${calcStatusPoints}">
												<input type="hidden" name="calcStatusPoints"
													value="${calcStatusPoint}" />
												<li><cti:url var="editUrl"
														value="/tools/points/${calcStatusPoint.pointId}" />
													<a href="${editUrl}">${fn:escapeXml(calcStatusPoint.name)}</a></li>
											</c:forEach>
										</ul>
									</li>
								</c:if>
								<cti:checkRolesAndProperties value="CBC_DATABASE_EDIT">
									<div class="action-area">
										<tags:pointCreation paoId="${capControlCBC.yukonPAObject.paObjectID}" />
									</div>
								</cti:checkRolesAndProperties>

							</div>
						</tags:nameValueContainer2>
						</tags:sectionContainer2>
 							
 							<c:if test="${capControlCBC.twoWay}">
							<tags:sectionContainer2 nameKey="dnpConfiguration"
								styleClass="stacked-lg">
								<tags:nameValueContainer2 tableClass="natural-width">
									<tags:nameValue2 nameKey=".dnpConfig">${dnpConfiguration.name}</tags:nameValue2>
								</tags:nameValueContainer2>
								<tags:nameValueContainer2 tableClass="natural-width">
									<tags:nameValue2 nameKey=".internalRetries">${dnpConfiguration.internalRetries}</tags:nameValue2>
								</tags:nameValueContainer2>
								<tags:nameValueContainer2 tableClass="natural-width">
									<tags:nameValue2 nameKey=".useLocal">${dnpConfiguration.localTime}</tags:nameValue2>
								</tags:nameValueContainer2>
								<tags:nameValueContainer2 tableClass="natural-width">
									<tags:nameValue2 nameKey=".enableTimeSync">${dnpConfiguration.enableDnpTimesyncs}</tags:nameValue2>
								</tags:nameValueContainer2>
								<tags:nameValueContainer2 tableClass="natural-width">
									<tags:nameValue2 nameKey=".omitTimeReq">${dnpConfiguration.omitTimeRequest}</tags:nameValue2>
								</tags:nameValueContainer2>
								<tags:nameValueContainer2 tableClass="natural-width">
									<tags:nameValue2 nameKey=".unsolicit1">${dnpConfiguration.enableUnsolicitedMessageClass1}</tags:nameValue2>
								</tags:nameValueContainer2>
								<tags:nameValueContainer2 tableClass="natural-width">
									<tags:nameValue2 nameKey=".unsolicit2">${dnpConfiguration.enableUnsolicitedMessageClass2}</tags:nameValue2>
								</tags:nameValueContainer2>
								<tags:nameValueContainer2 tableClass="natural-width">
									<tags:nameValue2 nameKey=".unsolicit3">${dnpConfiguration.enableUnsolicitedMessageClass3}</tags:nameValue2>
								</tags:nameValueContainer2>
							</tags:sectionContainer2>
							</c:if>

						</div>
						</div>
						</cti:tab>
						</cti:tabs>
					
		 <div class="page-action-area">
		<cti:button nameKey="save" type="submit" classes="primary action"/>
			<cti:msgScope paths="capcontrol.cbcBase">
				<cti:url var="deleteUrl" value="/editor/deleteBasePAO.jsf">
					<cti:param name="value" value="${capControlCBC.yukonPAObject.paObjectID}" />
				</cti:url>
				<cti:button nameKey="delete" href="${deleteUrl}" />
				
				<%-- Copy CBC Button --%>
				 <cti:url var="copyUrl" value="/editor/copyBase.jsf">
                                    <cti:param name="itemid" value="${capControlCBC.yukonPAObject.paObjectID}"/>
                                    <cti:param name="type" value="1"/>
                                </cti:url>
                                <cti:button nameKey="copy" href="${copyUrl}"/>
                                
				<cti:url var="returnUrl" value="" />
                <cti:button label="Return" href="javascript:window.history.back()"/>

			</cti:msgScope>
		</div>
		</form:form>


	</cti:msgScope>
</cti:standardPage>