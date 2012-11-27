<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:checkRole role="operator.DeviceActionsRole.ROLEID">
<cti:checkProperty property="operator.DeviceActionsRole.DEVICE_GROUP_MODIFY">
    <c:set var="hasModifyRoleProperty" value="true"/>
</cti:checkProperty>
</cti:checkRole>

<script>
    //reload the tree on ajax refresh
    jQuery(function(){
    	//if you can come up with a better way to determine if the tree has been intialzed, fix this!
    	var root = jQuery("#deviceGroupWidgetPopupTree").dynatree('getRoot');
    	if(typeof(root.getChildren) != 'undefined'){
	        jQuery("#deviceGroupWidgetPopupTree").dynatree({children: ${groupDataJson}});
	        jQuery("#deviceGroupWidgetPopupTree").dynatree('getTree').reload();
    	}
    });
</script>
    
<div class="widgetInternalSectionHeader"><i:inline key=".currentGroup"/></div>
    <c:choose>
        <c:when test="${not empty currentGroups}">

            <div class="scrollingContainer scrollingContainer_small">
	            <div style="zoom: 1;">
		            <table width="100%">
		                <c:forEach var="group" items="${currentGroups}">
		                
		                    <c:if test="${not group.hidden}">
		                
		                    <tr class="<tags:alternateRow odd="" even="altRow"/>">
		                        <td style="border: none">
		                           <cti:url value="/group/editor/home" var="groupEditorUrl">
		                             <cti:param name="groupName" value="${group.fullName}"/>
		                           </cti:url>
		            
		                            <a href="${groupEditorUrl}">${fn:escapeXml(group.fullName)}</a>
		                        </td>
		                        
		                        <%-- remove device from this group --%>
		                        <c:if test="${hasModifyRoleProperty}">
		                            <td style="border: none; width: 15px; text-align: center;">
		                                <c:choose>
		                                    <c:when test="${group.modifiable}">
                                            <cti:msg2 var="remove" key=".remove"/>
                                            <cti:msg2 var="removing" key=".removing"/>
		                                    <tags:widgetLink method="remove" title="${remove}" labelBusy="${removing}" groupId="${group.id}" container="currentGroups">
		                                        <img class="cssicon" src="<cti:url value="/WebConfig/yukon/Icons/clearbits/close.gif"/>">
		                                    </tags:widgetLink>
		                                    </c:when>

		                                    <c:otherwise>
                                                <cti:msg2 var="cantRemove" key=".cantRemove"/>
		                                        <img class="graycssicon" title="${cantRemove}" src="<cti:url value="/WebConfig/yukon/Icons/clearbits/close.gif"/>">
		                                    </c:otherwise>
		                                </c:choose>
		                            </td>
		                        </c:if>
		                        
		                    </tr>
		                    
		                    </c:if>
		                </c:forEach>
    	            </table>
	            </div>
            </div>
        </c:when>
        <c:otherwise>
            <i:inline key=".noGroups"/>
        </c:otherwise>
    </c:choose>