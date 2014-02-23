<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags/jsTree" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>

<cti:standardPage page="meterEventsReport.report" module="amr">

    <cti:includeScript link="/JavaScript/yukon.field.helper.js"/>

    <script type="text/javascript">
        function openSchedulePopup() {
            jQuery("#onlyLatestEvent").prop("checked", jQuery("#filter_onlyLatestEvent").prop("checked"));
            jQuery("#onlyAbnormalEvents").prop("checked", jQuery("#filter_onlyAbnormalEvents").prop("checked"));
            jQuery("#includeDisabledDevices").prop("checked", jQuery("#filter_includeDisabledPaos").prop("checked"));
            open_schedulePopup();
        };    
    
        jQuery(function() {
            jQuery(document).on('click', '#b-export', function (e) {
                document.getElementById('export-form').submit();
            });
            jQuery("#resetButton").click(function () {
                jQuery("#resetForm").submit();
            });

            jQuery("#scheduleButton").click(openSchedulePopup);
            
            submitSchedule = function() {
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
                }).appendTo('#scheduleForm');

                jQuery('#scheduleForm').submit();
            };
            
            jQuery("#eventTypesListOk").click(function() {
                jQuery('#eventTypesList').hide();
            });
            
            jQuery('.eventForm').submit(function() {
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
                   }).appendTo('.eventForm');
                   return true;
            });

            
            if(${not empty jobId} || ${not empty scheduleError}) {
                open_schedulePopup();
            }
        });
        
        function updateEventTypesNum() {
            var numSelected = 0;
            jQuery("#eventTree").dynatree("getSelectedNodes").each(function(node) {
                if (ignoreTitle(node.data.title) === false) {
                    numSelected++;
                }
            });
            jQuery('.numEventTypes').html(numSelected);
            jQuery('#filterPopupEventTypes').dialog('close');
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

            var treeChildren = [{title: allTitle, isFolder: true, expand: true, children: treeChildrenGroups}];
            
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
                };
            }
            return false;
        }
    </script>

    <form:form id="export-form" action="csv" commandName="backingBean" method="get">
        <cti:deviceCollection deviceCollection="${backingBean.deviceCollection}" />
        <tags:sortFields backingBean="${backingBean}" />
        <form:hidden path="toInstant" id="toInstant_csv"/>
        <form:hidden path="fromInstant" id="fromInstant_csv"/>
        <form:hidden path="onlyAbnormalEvents" id="onlyAbnormalEvents_csv"/>
        <form:hidden path="onlyLatestEvent" id="onlyLatestEvent_csv"/>
        <form:hidden path="includeDisabledPaos" id="includeDisabledPaos_csv"/>
    </form:form>
    
    <c:set var="popupTitleArgs" value=""/>
    <c:if test="${not empty exportData.scheduleName}">
        <c:set var="popupTitleArgs" value="\"${fn:escapeXml(exportData.scheduleName)}\""/>
    </c:if>
    
    <cti:msg2 var="scheduleButton" key=".scheduleButton"/>
    <cti:msg2 var="updateButton" key=".updateButton"/>
    <cti:msg2 var="cancelButton" key="yukon.web.components.button.cancel.label"/>
    <d:inline nameKey="schedulePopup" arguments="${popupTitleArgs}" id="schedulePopup" okEvent="submitSchedule"
        options="{'modal': true, 
                  'buttons': 
                    [{
                         text: '${cancelButton}', 
                         click: function() {jQuery(this).dialog('close')}
                    },
                    {
                         text: '${empty jobId ? scheduleButton : updateButton}', 
                         click: function() {submitSchedule();},
                         'class': 'primary action'
                    }]
                 }">
        <form:form id="scheduleForm" action="schedule" method="post" commandName="exportData">
            <cti:deviceCollection deviceCollection="${backingBean.deviceCollection}" />
            <tags:nameValueContainer2>
                <tags:nameValue2 nameKey=".report.selectedDevices">
                    <c:set var="isDeviceGroup" value="${backingBean.deviceCollection.collectionParameters['collectionType'] == 'group'}"/>
                    <c:if test="${backingBean.deviceCollection.deviceCount > 0}">
                        <c:if test="${isDeviceGroup}">
                            <span class="viewGroupLink fr">
                                <cti:url var="deviceGroupUrl" value="/group/editor/home">
                                    <cti:param name="groupName">${backingBean.deviceCollection.collectionParameters['group.name']}</cti:param>
                                </cti:url>
                                (<a href="${deviceGroupUrl}"><i:inline key=".filter.viewDeviceGroup"/></a>)
                            </span>
                        </c:if>
                        <span class="fr">
                            <tags:selectedDevicesPopup deviceCollection="${backingBean.deviceCollection}"/>
                        </span>
                    </c:if>
                    <span>
                        <c:choose>
                            <c:when test="${isDeviceGroup}">
                                <i:inline key=".filter.deviceGroup" arguments="${backingBean.deviceCollection.collectionParameters['group.name']}"/>
                            </c:when>
                            <c:otherwise>
                                <i:inline key="${backingBean.deviceCollection.description}" />
                            </c:otherwise>
                        </c:choose>
                    </span>
                </tags:nameValue2>
                <tags:inputNameValue nameKey=".daysPrevious"
                    path="daysPrevious"
                    size="3"/>
                <tags:nameValue2 nameKey=".filter.onlyLatestEvent" excludeColon="true">
                     <c:if test="${backingBean.onlyLatestEvent}">
                        <c:set var="latestChecked" value=" checked=\"true\""/>
                    </c:if>
                    <input type="checkbox" name="onlyLatestEvent" id="onlyLatestEvent"${latestChecked}>
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".filter.onlyAbnormalEvents" excludeColon="true">
                    <c:if test="${backingBean.onlyAbnormalEvents}">
                        <c:set var="abnormalChecked" value=" checked=\"true\""/>
                    </c:if>
                    <input type="checkbox" name="onlyAbnormalEvents" id="onlyAbnormalEvents"${abnormalChecked}>
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".filter.includeDisabledDevices" excludeColon="true">
                    <c:if test="${backingBean.includeDisabledPaos}">
                        <c:set var="disabledChecked" value=" checked=\"true\""/>
                    </c:if>
                    <input type="checkbox" name="includeDisabledDevices" id="includeDisabledDevices"${disabledChecked}>
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".filter.eventTypesRow">
                    <cti:msg2 key=".filter.cog.title" var="cogTitle"/>
                    <a href="javascript:void(0);" id="eventTypesCogSchedule" title="${cogTitle}">
                        <i class="icon icon-filter"></i>
                        <span class="numEventTypes">
                            <span id="numEventTypes">${backingBean.numSelectedEventTypes}</span>
                            <i:inline key=".filter.selected"/>
                        </span>
                    </a>
                </tags:nameValue2>
                <tags:scheduledFileExportInputs cronExpressionTagState="${cronExpressionTagState}" exportData="${exportData}" />
            </tags:nameValueContainer2>
            <c:if test="${not empty jobId}">
                <input type="hidden" name="jobId" value="${jobId}">
            </c:if>
        </form:form>
    </d:inline>
    
    <form:form id="eventsFilterForm" action="report" method="get" commandName="backingBean" cssClass="eventForm stacked">
        <div class="column-12-12 clearfix">
            
            <%-- LEFT SIDE COLUMN --%>
            <div class="column one">
                <tags:sectionContainer2 nameKey="filterSectionHeader">
                    <tags:nameValueContainer2>
                        <tags:nameValue2 nameKey=".filter.dateFrom">
                            <dt:date path="fromInstant" value="${backingBean.fromInstant}"/>
                        </tags:nameValue2>
            
                        <tags:nameValue2 nameKey=".filter.dateTo">
                            <dt:date path="toInstant" value="${backingBean.toInstantDisplayable}"/>
                        </tags:nameValue2>
            
                        <tags:nameValue2 nameKey=".filter.onlyLatestEvent">
                            <form:checkbox path="onlyLatestEvent" cssClass="fl" id="filter_onlyLatestEvent"/>
                            <span class="focusableFieldHolder">
                                <a id="latestEventsHelp"><i class="icon icon-help"></i></a>
                            </span>
                            <span class="focused-field-description"><i:inline key=".filter.onlyLatestEvents.help.text"/></span>
                        </tags:nameValue2>
            
                        <tags:nameValue2 nameKey=".filter.onlyAbnormalEvents">
                            <form:checkbox path="onlyAbnormalEvents" cssClass="fl" id="filter_onlyAbnormalEvents"/>
                            <span class="focusableFieldHolder">
                                <a id="activeEventsHelp"><i class="icon icon-help"></i></a>
                            </span>
                            <span class="focused-field-description"><i:inline key=".filter.onlyAbnormalEvents.help.text"/></span>
                        </tags:nameValue2>
            
                        <tags:nameValue2 nameKey=".filter.includeDisabledDevices">
                            <form:checkbox path="includeDisabledPaos" id="filter_includeDisabledPaos"/>
                        </tags:nameValue2>
            
                        <tags:nameValue2 nameKey=".filter.eventTypesRow">
                            <cti:msg2 key=".filter.cog.title" var="cogTitle"/>
                            <a href="javascript:void(0);" id="eventTypesCogFilter" title="${cogTitle}">
                                <i class="icon icon-filter"></i>
                                <span class="numEventTypes">
                                    <span id="numEventTypes">${backingBean.numSelectedEventTypes}</span>
                                    <i:inline key=".filter.selected"/>
                                </span>
                            </a>
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                </tags:sectionContainer2>
                
                <i:simplePopup titleKey=".filter.eventTypes" id="filterPopupEventTypes" onClose="updateEventTypesNum()" on="#eventTypesCogFilter, #eventTypesCogSchedule">
        
                    <t:inlineTree id="eventTree"
                        treeCss="/WebConfig/yukon/styles/lib/dynatree/deviceGroup.css"
                        treeParameters="{checkbox: true, selectMode: 3, onPostInit: treeInit()}"
                        includeControlBar="true" />
        
                    <div class="action-area">
                        <cti:button nameKey="ok" onclick="updateEventTypesNum()" classes="primary action"/>
                    </div>
                </i:simplePopup>
                
                <div class="page-action-area">
                    <cti:button nameKey="update" type="submit" classes="primary action"/>
                    <cti:button nameKey="reset" id="resetButton"/>
                    <cti:button nameKey="schedule" id="scheduleButton" icon="icon-calendar-view-day"/>
                    <c:if test="${filterResult.hitCount > 0}">
                        <cti:button nameKey="csv" icon="icon-page-white-excel" id="b-export"/>
                    </c:if>
                </div>
            </div>

            <%-- RIGHT SIDE COLUMN --%>
            <div class="column two nogutter">
                <tags:sortFields backingBean="${backingBean}" />
                <cti:deviceCollection deviceCollection="${backingBean.deviceCollection}" />
                <tags:sectionContainer2 nameKey="deviceSectionHeader">
                    <tags:nameValueContainer2>
                        <tags:nameValue2 nameKey=".report.selectedDevices">
                            <c:set var="isDeviceGroup" value="${backingBean.deviceCollection.collectionParameters['collectionType'] == 'group'}"/>
                            <c:if test="${backingBean.deviceCollection.deviceCount > 0}">
                                <c:if test="${isDeviceGroup}">
                                    <span class="viewGroupLink fr">
                                        <cti:url var="deviceGroupUrl" value="/group/editor/home">
                                            <cti:param name="groupName">${backingBean.deviceCollection.collectionParameters['group.name']}</cti:param>
                                        </cti:url>
                                        (<a href="${deviceGroupUrl}"><i:inline key=".filter.viewDeviceGroup"/></a>)
                                    </span>
                                </c:if>
                                <span class="fr">
                                    <tags:selectedDevicesPopup deviceCollection="${backingBean.deviceCollection}"/>
                                </span>
                            </c:if>
                            <span>
                                <c:choose>
                                    <c:when test="${isDeviceGroup}">
                                        <i:inline key=".filter.deviceGroup" arguments="${backingBean.deviceCollection.collectionParameters['group.name']}"/>
                                    </c:when>
                                    <c:otherwise>
                                        <cti:msg key="${backingBean.deviceCollection.description}" />
                                    </c:otherwise>
                                </c:choose>
                            </span>
                        </tags:nameValue2>
            
                        <tags:nameValue2 nameKey=".report.selectedDevicesCount">${backingBean.deviceCollection.deviceCount}</tags:nameValue2>
                    </tags:nameValueContainer2>
                </tags:sectionContainer2>
            </div>
        </div>
    </form:form>
    
    <form:form id="resetForm" action="reset" method="get">
       <cti:deviceCollection deviceCollection="${backingBean.deviceCollection}" />
    </form:form>

    <c:if test="${collectionFromReportResults != null && filterResult.hitCount > 0}">
        <c:set var="linkHeaderHtml">
            <span class="navLink fr">
                <cm:deviceCollectionMenu deviceCollection="${collectionFromReportResults}"
                    key="yukon.web.modules.common.contextualMenu.actions"/>
            </span>
        </c:set>
    </c:if>

    <cti:url var="filteredUrl" value="report">
        <c:if test="${not empty param.fromInstant}">
            <cti:param name="fromInstant" value="${param.fromInstant}"/>
        </c:if>
        <c:if test="${not empty param.toInstant}">
            <cti:param name="toInstant" value="${param.toInstant}"/>
        </c:if>
        <c:if test="${not empty param.onlyLatestEvent}">
            <cti:param name="onlyLatestEvent" value="${param.onlyLatestEvent}"/>
        </c:if>
        <c:if test="${not empty param._onlyLatestEvent}">
            <cti:param name="_onlyLatestEvent" value="${param._onlyLatestEvent}"/>
        </c:if>
        <c:if test="${not empty param.onlyAbnormalEvents}">
            <cti:param name="onlyAbnormalEvents" value="${param.onlyAbnormalEvents}"/>
        </c:if>
        <c:if test="${not empty param._onlyAbnormalEvents}">
            <cti:param name="_onlyAbnormalEvents" value="${param._onlyAbnormalEvents}"/>
        </c:if>
        <c:if test="${not empty param.includeDisabledPaos}">
            <cti:param name="includeDisabledPaos" value="${param.includeDisabledPaos}"/>
        </c:if>
        <c:if test="${not empty param._includeDisabledPaos}">
            <cti:param name="_includeDisabledPaos" value="${param._includeDisabledPaos}"/>
        </c:if>
        <c:if test="${not empty param.attrNames}">
            <cti:param name="attrNames" value="${param.attrNames}"/>
        </c:if>
        <c:if test="${param.collectionType eq 'group'}">
            <cti:param name="collectionType" value="group"/>
            <cti:param name="group.name" value="${param['group.name']}"/>
        </c:if>
        <c:if test="${param.collectionType eq 'idList'}">
            <cti:param name="collectionType" value="idList"/>
            <cti:param name="idList.ids" value="${param['idList.ids']}"/>
        </c:if>
        <c:if test="${param.collectionType eq 'addressRange'}">
            <cti:param name="collectionType" value="addressRange"/>
            <cti:param name="addressRange.start" value="${param['addressRange.start']}"/>
            <cti:param name="addressRange.end" value="${param['addressRange.end']}"/>
        </c:if>
        <%--File Uploads are given a temp group. Reference this group after the file has been uploaded --%>
        <c:if test="${param.collectionType eq 'fileUpload'}">
            <cti:param name="collectionType" value="group"/>
            <cti:param name="group.name" value="${backingBean.deviceCollection.collectionParameters['group.name']}"/>
        </c:if>
    </cti:url>
    <cti:url var="sortedUrl" value="${filteredUrl}">
        <c:if test="${not empty param.sort}">
            <cti:param name="sort" value="${param.sort}" />
        </c:if>
        <c:if test="${not empty param.descending}">
            <cti:param name="descending" value="${param.descending}" />
        </c:if>
    </cti:url>

    <tags:pagedBox2 nameKey="tableTitle" searchResult="${filterResult}" baseUrl="${sortedUrl}" titleLinkHtml="${linkHeaderHtml}" overrideParams="true">
        <c:choose>
            <c:when test="${fn:length(filterResult.resultList) == 0}">
                <span class="empty-list"><i:inline key=".noEvents" /></span>
            </c:when>
            <c:otherwise>
                <table id="eventsTable" class="compact-results-table f-traversable has-actions sortable-table">
                    <thead>
                        <tr>
                            <th><tags:sortLink nameKey="tableHeader.deviceName" baseUrl="${filteredUrl}" fieldName="NAME" isDefault="false" overrideParams="true"/></th>
                            <th><tags:sortLink nameKey="tableHeader.meterNumber" baseUrl="${filteredUrl}" fieldName="METER_NUMBER" isDefault="false" overrideParams="true"/></th>
                            <th><tags:sortLink nameKey="tableHeader.deviceType" baseUrl="${filteredUrl}" fieldName="TYPE" overrideParams="true"/></th>
                            <th><tags:sortLink nameKey="tableHeader.date" baseUrl="${filteredUrl}" fieldName="DATE" isDefault="true" overrideParams="true"/></th>
                            <th><tags:sortLink nameKey="tableHeader.event" baseUrl="${filteredUrl}" fieldName="EVENT" overrideParams="true"/></th>
                            <th><tags:sortLink nameKey="tableHeader.value" baseUrl="${filteredUrl}" fieldName="VALUE" overrideParams="true"/></th>
                            <th></th>
                        </tr>
                    </thead>
                    <tfoot></tfoot>
                    <tbody>
                        <c:forEach var="event" items="${filterResult.resultList}">
                            <tr>
                                <td>
                                    <cti:paoDetailUrl  yukonPao="${event.meter}" >
                                        ${fn:escapeXml(event.meter.name)}
                                    </cti:paoDetailUrl>
                                </td>
                                <td>${event.meter.meterNumber}</td>
                                <td><tags:paoType yukonPao="${event.meter}"/></td>
                                <td><cti:formatDate type="BOTH" value="${event.pointValueHolder.pointDataTimeStamp}"/></td>
                                <td>${fn:escapeXml(event.pointName)}</td>
                                <cti:pointColor pointId="${event.pointValueHolder.id}" rawState="${event.pointValueHolder.value}" var="color"/>
                                <td class="${color}">
                                    <cti:pointValueFormatter format="VALUE" value="${event.pointValueHolder}" />
                                </td>
                                <td class="contextual-menu">
                                    <cm:singleDeviceMenu deviceId="${event.meter.paoIdentifier.paoId}" containerCssClass="fr"/>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:otherwise>
        </c:choose>
    </tags:pagedBox2>
</cti:standardPage>