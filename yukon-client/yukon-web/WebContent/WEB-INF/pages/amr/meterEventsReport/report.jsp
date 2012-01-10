<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="jsTree" tagdir="/WEB-INF/tags/jsTree" %>

<cti:standardPage page="meterEventsReport.report" module="amr">

    <script type="text/javascript">
	    jQuery(document).ready(function() {
	        if ('${hasFilterErrors}' === 'true') {
	            adjustDialogSizeAndPosition('filterPopup');
	            jQuery('#filterPopup').show();
	        }
	        
	        jQuery(".selectedDevicesLink").hover(function() {
	        	jQuery(".deviceMagIcon .magnifier").toggleClass("magnifier_hovered");
	        });
	        
	        jQuery(".deviceMagIcon .magnifier").hover(function() {
	        	jQuery(".selectedDevicesLink.anchorUnderlineHover").toggleClass("anchorUnderlineHover_hovered");
	        });
	        
	        jQuery(".selectedDevicesLink").click(function() {
	        	jQuery(".f_showSelectedDevices").trigger("click");
	        });
	        
	        jQuery("#eventTypesCog, .eventTypesLink").click(function() {
	        	showSimplePopup('filterPopupEventTypes');
	        });
	        
	        jQuery("#eventTypesListOk").click(function() {
	        	jQuery('#eventTypesList').hide();
	        });
	        
	        jQuery('#eventsFilterForm, #csvForm').submit(function() {
	        	var attrNames = '';
		    	jQuery("#eventTree").dynatree("getSelectedNodes").each(function(node) {
		    		if (ignoreTitle(node.data.title) === false) {
		    			attrNames += node.data.title + ",";
		    		}
		    	});
	        	
	   			jQuery('<input>').attr({
	   			    type: 'hidden',
	   			    name: 'attrNames',
	   			    value: attrNames
	   			}).appendTo('#eventsFilterForm');
	   			return true;
	        });
	        
	    });
	    
	    function updateEventTypesNum() {
	    	var numSelected = 0;
	    	jQuery("#eventTree").dynatree("getSelectedNodes").each(function(node) {
	    		if (ignoreTitle(node.data.title) === false) {
	    			numSelected++;
	    		}
	    	});
	    	jQuery('#numEventTypes').html(numSelected);
	    	jQuery('#filterPopupEventTypes').hide();
	    }
	    
	    var initialized = false;
	    var titlesToIgnore = [];
	    var treeChildren = [];
	    treeInit = function() {
	    	if (initialized === true) {
	    		return;
	    	}
	    	
	    	var map = ${meterEventTypesMap}; // {"LINE_FREQUENCY_WARNING":true,"TIME_ADJUSTMENT":true,...}

	    	var generalNodes = populateTreeNodes({allEventsMap: map, events: ${generalEvents}});
	    	var hardwareNodes = populateTreeNodes({allEventsMap: map, events: ${hardwareEvents}});
	    	var tamperNodes = populateTreeNodes({allEventsMap: map, events: ${tamperEvents}});
	    	var outageNodes = populateTreeNodes({allEventsMap: map, events: ${outageEvents}});
	    	var meteringNodes = populateTreeNodes({allEventsMap: map, events: ${meteringEvents}});

	    	var allTitle = '<cti:msg2 key=".filter.tree.all" javaScriptEscape="true"/>';
	    	titlesToIgnore.push(allTitle);
	    	var generalTitle = '<cti:msg2 key=".filter.tree.general" javaScriptEscape="true"/>';
	    	titlesToIgnore.push(generalTitle);
	    	var hardwareTitle = '<cti:msg2 key=".filter.tree.hardware" javaScriptEscape="true"/>';
	    	titlesToIgnore.push(hardwareTitle);
	    	var tamperTitle = '<cti:msg2 key=".filter.tree.tamper" javaScriptEscape="true"/>';
	    	titlesToIgnore.push(tamperTitle);
	    	var outageTitle = '<cti:msg2 key=".filter.tree.outage" javaScriptEscape="true"/>';
	    	titlesToIgnore.push(outageTitle);
	    	var meteringTitle = '<cti:msg2 key=".filter.tree.metering" javaScriptEscape="true"/>';
	    	titlesToIgnore.push(meteringTitle);
	    	
	    	var treeChildrenGroups = [];
	    	if (generalNodes.length > 0) {
	    		treeChildrenGroups.push({title: generalTitle, isFolder: true, children: generalNodes});
	    	}
	    	if (hardwareNodes.length > 0) {
	    		treeChildrenGroups.push({title: hardwareTitle, isFolder: true, children: hardwareNodes});
	    	}
	    	if (tamperNodes.length > 0) {
	    		treeChildrenGroups.push({title: tamperTitle, isFolder: true, children: tamperNodes});
	    	}
	    	if (outageNodes.length > 0) {
	    		treeChildrenGroups.push({title: outageTitle, isFolder: true, children: outageNodes});
	    	}
	    	if (meteringNodes.length > 0) {
	    		treeChildrenGroups.push({title: meteringTitle, isFolder: true, children: meteringNodes});
	    	}

	    	var treeChildren = [{title: allTitle, isFolder: true, children: treeChildrenGroups}];
	    	
	    	jQuery("#eventTree").dynatree({
	    		checkbox: true,
	    		selectMode: 3,
	    		children: treeChildren
	    	});
	    	
		    initialized = true;
	    };
	    
	    function populateTreeNodes(params) {
	    	var nodes = [];
	    	for (var i = 0; i < params.events.length; i++) {
	    		var selected = params.allEventsMap[params.events[i]];
	    		if (typeof(selected) != 'undefined') {
	    			nodes.push({title: params.events[i], select: selected});
	    		}
	    	}
	    	return nodes;
	    }
	    
        function ignoreTitle(title) {
        	for (var i = 0; i < titlesToIgnore.length; i++) {
        		if (title === titlesToIgnore[i]) {
        			return true;
        		}
        	}
        	return false;
        }
    </script>
	
	<form:form id="eventsFilterForm" action="report" method="get"commandName="backingBean">
        <cti:dataGrid cols="2" tableClasses="twoColumnLayout">

            <%-- LEFT SIDE COLUMN --%>
            <cti:dataGridCell>
				<tags:formElementContainer nameKey="filterSectionHeader">
					<tags:nameValueContainer2>
						<tags:nameValue2 nameKey=".filter.dateFrom">
							<tags:dateInputCalendar fieldName="fromDate" springInput="true" />
						</tags:nameValue2>
			
						<tags:nameValue2 nameKey=".filter.dateTo">
							<tags:dateInputCalendar fieldName="toDate" springInput="true" />
						</tags:nameValue2>
			
						<tags:nameValue2 nameKey=".filter.onlyActiveEvents">
							<form:checkbox path="onlyActiveEvents" />
						</tags:nameValue2>
			
						<tags:nameValue2 nameKey=".filter.onlyLatestEvent">
							<form:checkbox path="onlyLatestEvent" id="onlyLatestEvent" />
						</tags:nameValue2>
			
						<tags:nameValue2 nameKey=".filter.includeDisabledDevices">
							<form:checkbox path="includeDisabledPaos" />
						</tags:nameValue2>
			
						<tags:nameValue2 nameKey=".filter.eventTypesRow">
							<cti:msg2 key=".filter.cog.title" var="cogTitle"/>
							<span id="eventTypesCog" title="${cogTitle}" class="cog anchorUnderlineHover labeled_icon_right">
								<span id="numEventTypes">
									${backingBean.numSelectedEventTypes}
								</span>
								<i:inline key=".filter.selected"/>
							</span>
						</tags:nameValue2>
					</tags:nameValueContainer2>
				</tags:formElementContainer>
	
				<i:simplePopup titleKey=".filter.eventTypes"
					id="filterPopupEventTypes" styleClass="smallSimplePopup"
					onClose="updateEventTypesNum()">
		
					<jsTree:inlineTree id="eventTree"
						treeCss="/WebConfig/yukon/styles/lib/dynatree/deviceGroup.css"
						treeParameters="{checkbox: true, selectMode: 3, onPostInit: treeInit()}"
						width="500" height="300" includeControlBar="true" />
		
					<div class="actionArea">
						<cti:button nameKey="ok" onclick="updateEventTypesNum()" />
					</div>
				</i:simplePopup>
				<div class="pageActionArea">
					<cti:button nameKey="update" type="submit" styleClass="f_blocker" />
					<cti:button nameKey="reset" type="submit" name="clear"
						styleClass="f_blocker" />
				</div>
			</cti:dataGridCell>

			<%-- RIGHT SIDE COLUMN --%>
            <cti:dataGridCell>
				<tags:sortFields backingBean="${backingBean}" />
				<cti:deviceCollection deviceCollection="${backingBean.deviceCollection}" />
			    <tags:formElementContainer nameKey="deviceSectionHeader">
					<tags:nameValueContainer2>
						<tags:nameValue2 nameKey="yukon.web.modules.amr.meterEventsReport.report.selectedDevices">
							<c:set var="isDeviceGroup" value="${backingBean.deviceCollection.collectionParameters['collectionType'] == 'group'}"/>
							<span class="selectedDevicesLink anchorUnderlineHover">
								<c:choose>
									<c:when test="${isDeviceGroup}">
										<i:inline key=".filter.deviceGroup" arguments="${backingBean.deviceCollection.collectionParameters['group.name']}"/>
									</c:when>
									<c:otherwise>
										<cti:msg key="${backingBean.deviceCollection.description}" />
									</c:otherwise>
								</c:choose>
							</span>
							<c:if test="${backingBean.deviceCollection.deviceCount > 0}">
								<span class="fr deviceMagIcon">
									<tags:selectedDevicesPopup deviceCollection="${backingBean.deviceCollection}"/>
								</span>
							</c:if>
							<c:if test="${isDeviceGroup}">
								<cti:url var="deviceGroupUrl" value="/spring/group/editor/home">
									<cti:param name="groupName">${backingBean.deviceCollection.collectionParameters['group.name']}</cti:param>
								</cti:url>
								(<a href="${deviceGroupUrl}"><i:inline key=".filter.viewDeviceGroup"/></a>)
							</c:if>
						</tags:nameValue2>
			
						<tags:nameValue2 nameKey="yukon.web.modules.amr.meterEventsReport.report.selectedDevicesCount">
							${backingBean.deviceCollection.deviceCount}
						</tags:nameValue2>
					</tags:nameValueContainer2>
				</tags:formElementContainer>
			</cti:dataGridCell>
		</cti:dataGrid>
	</form:form>

	<form:form id="csvForm" action="csv" method="post" commandName="backingBean" cssClass="fr">
		<cti:deviceCollection deviceCollection="${backingBean.deviceCollection}" />
		<tags:sortFields backingBean="${backingBean}" />
		<form:hidden path="toDate" id="toDate_csv"/>
		<form:hidden path="fromDate" id="fromDate_csv"/>
		<form:hidden path="onlyActiveEvents" id="onlyActiveEvents_csv"/>
		<form:hidden path="onlyLatestEvent" id="onlyLatestEvent_csv"/>
		<form:hidden path="includeDisabledPaos" id="includeDisabledPaos_csv"/>
		<cti:button nameKey="csv" renderMode="labeledImage" type="submit" />
	</form:form>
	<div class="cr"></div>

	<tags:pagedBox2 nameKey="tableTitle" searchResult="${filterResult}" baseUrl="report">
		<table id="eventsTable" class="compactResultsTable">
			<tr>
				<th><tags:sortLink nameKey="tableHeader.deviceName" baseUrl="report" fieldName="NAME" isDefault="false" /></th>
				<th><tags:sortLink nameKey="tableHeader.deviceType" baseUrl="report" fieldName="TYPE" /></th>
				<th><tags:sortLink nameKey="tableHeader.date" baseUrl="report" fieldName="DATE" isDefault="true" /></th>
				<th><tags:sortLink nameKey="tableHeader.attribute" baseUrl="report" fieldName="ATTR" /></th>
				<th><tags:sortLink nameKey="tableHeader.value" baseUrl="report" fieldName="VALUE" /></th>
			</tr>
			<c:forEach var="event" items="${filterResult.resultList}">
		    	<c:set var="trClass" value=""/>
				<c:if test="${event.meter.disabled}">
					<c:set var="trClass" value="subtleGray"/>
				</c:if>
				<tr class="<tags:alternateRow odd="" even="altRow"/> ${trClass}">
					<td>
						<cti:paoDetailUrl  yukonPao="${event.meter}" >
						    <c:choose>
						        <c:when test="${empty event.meter.name}"></c:when>
						        <c:otherwise><spring:escapeBody>${event.meter.name}</spring:escapeBody></c:otherwise>
						    </c:choose>
						</cti:paoDetailUrl>
					</td>
					<td>
					    <cti:paoTypeIcon yukonPao="${event.meter}" /> &nbsp
					    <c:choose>
					        <c:when test="${empty event.meter.paoType}"></c:when>
					        <c:otherwise>${event.meter.paoType.dbString}</c:otherwise>
					    </c:choose>
					</td>
					<td><cti:formatDate type="BOTH" value="${event.pointValueHolder.pointDataTimeStamp}"/></td>
	                <td>${event.attribute}</td>
					<td class="eventStatus${event.formattedValue}">${event.formattedValue}</td>
				</tr>
			</c:forEach>
			
			<c:if test="${fn:length(filterResult.resultList) == 0}">
				<tr>
					<td class="noResults subtleGray" colspan="3">
						<i:inline key=".noEvents"/>
					</td>
				</tr>
			</c:if>
		</table>
	</tags:pagedBox2>
</cti:standardPage>