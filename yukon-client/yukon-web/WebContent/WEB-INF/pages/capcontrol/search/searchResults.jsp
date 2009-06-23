<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<cti:includeCss link="/WebConfig/yukon/styles/YukonGeneralStyles.css"/>

<cti:standardPage title="Search Results" module="capcontrol">

<jsp:setProperty name="CtiNavObject" property="moduleExitPage" value=""/>

<cti:standardMenu menuSelection="orphans"/>

<cti:breadCrumbs>
  <cti:crumbLink url="/spring/capcontrol/tier/areas" title="Home" />
  <cti:crumbLink title="Results"/>
</cti:breadCrumbs>

<form id="parentForm" action="feeders.jsp" method="post">
    <input type="hidden" name="${lastAreaKey}" />
    <input type="hidden" name="${lastSubKey}" />

    <div class="scrollLarge">
        <h4>Search Results For: ${label}  (${resultsFound} found)</h4>
        <table id="resTable" class="resultsTable activeResultsTable" align="center">
            <tr>
                <th/>
                <th>Name</th>
                <th nowrap="nowrap">Item Type</th>
                <th>Description</th>
                <th>Parent</th>
            </tr>
            <c:forEach items="${rows}" var="row">
                <tr height="18px" valign="middle" style="vertical-align : middle;">
                    <td width="80px" nowrap="nowrap">
                        <c:choose>
                            <c:when test="${row.paobject}">
                                <input type="checkbox" name="cti_chkbxSubBuses" value="${row.itemId}"/>
                                <cti:checkProperty property="CBCSettingsRole.CBC_DATABASE_EDIT">
                                    <a href="/editor/cbcBase.jsf?type=2&itemid=${row.itemId}" class="editImg">
                                        <img class="rAlign editImg" src="/editor/images/edit_item.gif"/>
                                    </a>
                                    <a href="/editor/deleteBasePAO.jsf?value=${row.itemId}" class="editImg">
                                        <img class="rAlign editImg" src="/editor/images/delete_item.gif" height="16" width="16"/>
                                    </a>
                                </cti:checkProperty>
                            </c:when>
	                        <c:otherwise>
	                            <cti:checkProperty property="CBCSettingsRole.CBC_DATABASE_EDIT">
	                                <a href="/editor/pointBase.jsf?parentId=${row.parentId}&itemid=${row.itemId}" class="editImg">
	                                    <img class="rAlign editImg" src="/editor/images/edit_item.gif"/>
	                                </a>
	                                <a href="/editor/deleteBasePoint.jsf?value=${row.itemId}" class="editImg">
	                                    <img class="rAlign editImg" src="/editor/images/delete_item.gif" height="16" width="16"/>
	                                </a>
	                            </cti:checkProperty>
	                        </c:otherwise>
	                    </c:choose>
	                    <c:if test="${row.controller}">
	                        <cti:checkProperty property="CBCSettingsRole.CBC_DATABASE_EDIT">
	                            <a href="/editor/copyBase.jsf?itemid=${row.itemId}&type=1>"><img src="/editor/images/page_copy.gif" border="0" height="16" width="16"/></a>
	                        </cti:checkProperty>
	                    </c:if>
	                </td>
	                <td nowrap="nowrap">${row.name}</td>
	                <td nowrap="nowrap">${row.itemType}</td>
	                <td nowrap="nowrap">${row.itemDescription}</td>
	                <td>${row.parentString}</td>
	            </tr>
	        </c:forEach>
	    </table>
	</div>
</form>
</cti:standardPage>