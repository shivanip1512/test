<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="jsTree" tagdir="/WEB-INF/tags/jsTree" %>

<cti:standardPage page="rfnEventsReport.report" module="amr">

    <script type="text/javascript">
	    jQuery(document).ready(function() {
	        if ('${hasFilterErrors}' === 'true') {
	            adjustDialogSizeAndPosition('filterPopup');
	            jQuery('#filterPopup').show();
	        }
	        
	        jQuery("#eventTypesCog").click(function() {
	        	showSimplePopup($('filterPopupEventTypes'));
	        });
	        
	        jQuery("#eventTypesMag").click(function() {
	        	showSimplePopup($('eventTypesList'));
	        });
	        
	        jQuery("#eventTypesListOk").click(function() {
	        	jQuery('#eventTypesList').hide();
	        });
	        
	        jQuery('#filterForm, #csvForm').submit(function() {
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
	   			}).appendTo('#filterForm');
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
	    	
	    	var map = ${rfnEventTypesMap};
	    	var generalEvents = ${generalEvents};
	    	
	    	var generalNodes = [];
	    	for (var i = 0; i < generalEvents.length; i++) {
	    		var selected = map[generalEvents[i]];
	    		generalNodes.push({title: generalEvents[i], select: selected});
	    	}
	    	
	    	var hardwareEvents = ${hardwareEvents};
	    	var hardwareNodes = [];
	    	for (var i = 0; i < hardwareEvents.length; i++) {
	    		var selected = map[hardwareEvents[i]];
	    		hardwareNodes.push({title: hardwareEvents[i], select: selected});
	    	}
	    	
	    	var tamperEvents = ${tamperEvents};
	    	var tamperNodes = [];
	    	for (var i = 0; i < tamperEvents.length; i++) {
	    		var selected = map[tamperEvents[i]];
	    		tamperNodes.push({title: tamperEvents[i], select: selected});
	    	}
	    	
	    	var outageEvents = ${outageEvents};
	    	var outageNodes = [];
	    	for (var i = 0; i < outageEvents.length; i++) {
	    		var selected = map[outageEvents[i]];
	    		outageNodes.push({title: outageEvents[i], select: selected});
	    	}
	    	
	    	var meteringEvents = ${meteringEvents};
	    	var meteringNodes = [];
	    	for (var i = 0; i < meteringEvents.length; i++) {
	    		var selected = map[meteringEvents[i]];
	    		meteringNodes.push({title: meteringEvents[i], select: selected});
	    	}

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

	    	var treeChildren = [{title: allTitle, isFolder: true, children: [
		    	                    {title: generalTitle, isFolder: true, children: generalNodes},
		    	                    {title: hardwareTitle, isFolder: true, children: hardwareNodes},
		    	                    {title: tamperTitle, isFolder: true, children: tamperNodes},
		    	                    {title: outageTitle, isFolder: true, children: outageNodes},
		    	                    {title: meteringTitle, isFolder: true, children: meteringNodes}]}];
	    	
	    	jQuery("#eventTree").dynatree({
	    		checkbox: true,
	    		selectMode: 3,
	    		children: treeChildren
	    	});
	    	
		    initialized = true;
	    };
	    
        function ignoreTitle(title) {
        	for (var i = 0; i < titlesToIgnore.length; i++) {
        		if (title === titlesToIgnore[i]) {
        			return true;
        		}
        	}
        	return false;
        }
    </script>
    
	<i:simplePopup titleKey=".filter.eventTypes" id="eventTypesList" styleClass="smallSimplePopup">
		<div class="dialogScrollArea">
		<table class="compactResultsTable">
			<tr>
				<th>Event Type</th>
			</tr>
			<c:forEach items="${backingBean.enabledEventTypeStrings}" var="eventType">
				<tr>
					<td>${eventType}</td>
				</tr>
			</c:forEach>
		</table>
		</div>
		<div class="actionArea">
			<cti:button nameKey="ok" id="eventTypesListOk"/>
		</div>
	</i:simplePopup>

	<tags:nameValueContainer2>
		<tags:nameValue2 nameKey="yukon.common.device.bulk.selectedDevicesPopup.linkLabel">
			<c:choose>
				<c:when
					test="${backingBean.deviceCollection.collectionParameters['collectionType'] == 'group'}">
					<cti:url var="deviceGroupUrl" value="/spring/group/editor/home">
						<cti:param name="groupName">${backingBean.deviceCollection.collectionParameters['group.name']}</cti:param>
					</cti:url>
					<i:inline key=".filter.deviceGroup" />
					<a href="${deviceGroupUrl}">${backingBean.deviceCollection.collectionParameters['group.name']}</a>
				</c:when>
				<c:otherwise>
					<cti:msg key="${backingBean.deviceCollection.description}" />
				</c:otherwise>
			</c:choose>
			<c:if test="${backingBean.deviceCollection.deviceCount > 0}">
				<tags:selectedDevicesPopup
					deviceCollection="${backingBean.deviceCollection}" />
			</c:if>
		</tags:nameValue2>

		<tags:nameValue2 nameKey="yukon.common.device.bulk.selectedDevicesPopup.deviceCount">
			${backingBean.deviceCollection.deviceCount}
		</tags:nameValue2>

		<tags:nameValue2 nameKey=".filter.dateFrom">
			<c:choose>
				<c:when test="${backingBean.fromDate != null}">
					<cti:formatDate type="BOTH" value="${backingBean.fromDate}" />
				</c:when>
				<c:otherwise>
					<i:inline key="yukon.web.defaults.dashes"/>
				</c:otherwise>
			</c:choose>
		</tags:nameValue2>

		<tags:nameValue2 nameKey=".filter.dateTo">
			<c:choose>
				<c:when test="${backingBean.toDate != null}">
					<cti:formatDate type="BOTH" value="${backingBean.toDate}" />
				</c:when>
				<c:otherwise>
					<i:inline key="yukon.web.defaults.dashes"/>
				</c:otherwise>
			</c:choose>
		</tags:nameValue2>

		<tags:nameValue2 nameKey=".filter.onlyActiveEvents">
			<i:inline key=".${backingBean.onlyActiveEvents}" />
		</tags:nameValue2>

		<tags:nameValue2 nameKey=".filter.onlyLatestEvent">
			<i:inline key=".${backingBean.onlyLatestEvent}" />
		</tags:nameValue2>

		<tags:nameValue2 nameKey=".filter.includeDisabledDevices">
			<i:inline key=".${backingBean.includeDisabledPaos}" />
		</tags:nameValue2>

		<tags:nameValue2 nameKey=".filter.eventTypesRow">
                	${backingBean.numSelectedEventTypes}
                	<i:inline key=".filter.selected" />
			<cti:button nameKey="eventTypesMag" renderMode="image"
				id="eventTypesMag" />
		</tags:nameValue2>

	</tags:nameValueContainer2>

	<%-- FILTER POPUP --%>
	<i:simplePopup titleKey=".filter.section" id="filterPopup" styleClass="smallSimplePopup">
        <cti:flashScopeMessages/>
        <form:form id="filterForm" action="report" method="get" commandName="backingBean">
            <tags:sortFields backingBean="${backingBean}" />
            <cti:deviceCollection deviceCollection="${backingBean.deviceCollection}" />

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
                    <form:checkbox path="onlyLatestEvent" id="onlyLatestEvent"/>
                </tags:nameValue2>

                <tags:nameValue2 nameKey=".filter.includeDisabledDevices">
                    <form:checkbox path="includeDisabledPaos" />
                </tags:nameValue2>

                <tags:nameValue2 nameKey=".filter.eventTypesRow">
                    <span id="numEventTypes">
                    	${backingBean.numSelectedEventTypes}
                   	</span>
                   	<i:inline key=".filter.selected"/>
					<cti:button nameKey="filter.cog" renderMode="image" id="eventTypesCog" styleClass="filterCog"/>
                </tags:nameValue2>
                
            </tags:nameValueContainer2>

			<i:simplePopup titleKey=".filter.eventTypes" id="filterPopupEventTypes" styleClass="smallSimplePopup" onClose="updateEventTypesNum()">
				
				<jsTree:inlineTree id="eventTree"
	                treeCss="/WebConfig/yukon/styles/lib/dynatree/deviceGroup.css"
	                treeParameters="{checkbox: true, selectMode: 3, onPostInit: treeInit()}"
	                width="500"
	                height="300"
	                includeControlBar="true" />
				
				<div class="actionArea">
					<cti:button nameKey="ok" onclick="updateEventTypesNum()"/>
				</div>
			</i:simplePopup>
            <div class="actionArea">
                <cti:button nameKey="filter" type="submit" styleClass="f_blocker" />
                <cti:button nameKey="clear" type="submit" name="clear" styleClass="f_blocker" />
            </div>
        </form:form>
    </i:simplePopup>

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

	<tags:pagedBox2 nameKey="tableTitle" searchResult="${filterResult}" baseUrl="report" filterDialog="filterPopup">
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