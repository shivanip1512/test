<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"%>

<cti:standardPage title="Search Results" module="capcontrol">
    <%@include file="/capcontrol/capcontrolHeader.jspf"%>
    
    <jsp:setProperty name="CtiNavObject" property="moduleExitPage" value=""/>
    
    <cti:standardMenu menuSelection="orphans"/>
    
    <cti:breadCrumbs>
      <cti:crumbLink url="/spring/capcontrol/tier/areas" title="Home" />
      <cti:crumbLink title="Results"/>
    </cti:breadCrumbs>
    
    <c:set var="baseUrl" value="/spring/capcontrol/search/searchResults" />
    
    <form id="parentForm" action="feeders.jsp" method="post">
        <input type="hidden" name="${lastAreaKey}" />
        <input type="hidden" name="${lastSubKey}" />
    
        <tags:pagedBox title="Search Results For: ${label}  (${resultsFound} found)" searchResult="${searchResult}" baseUrl="${baseUrl}">
            <c:choose>
            
                <c:when test="${searchResult.hitCount == 0}">
                    <cti:msg key="yukon.web.modules.dr.searchResults.noResults" />
                </c:when>
            
                <c:otherwise>
                
                    <table id="resTable" class="compactResultsTable rowHighlighting" align="center">
                        <tr>
                            <th>Name</th>
                            <th nowrap="nowrap">Item Type</th>
                            <th>Description</th>
                            <th>Parent</th>
                        </tr>
                        <c:forEach items="${searchResult.resultList}" var="row">
                            <tr class="<tags:alternateRow odd="tableCell" even="altTableCell"/>">
            	                <td nowrap="nowrap">
                                    <c:choose>
                                        <c:when test="${row.paobject}">
                                            <cti:checkProperty property="CBCSettingsRole.CBC_DATABASE_EDIT">
                                                <a href="/editor/cbcBase.jsf?type=2&itemid=${row.itemId}" class="tierIconLink">
                                                    <img class="tierImg" src="/WebConfig/yukon/Icons/pencil.gif"/>
                                                </a>
                                                <a href="/editor/deleteBasePAO.jsf?value=${row.itemId}" class="tierIconLink">
                                                    <img class="tierImg" src="/WebConfig/yukon/Icons/delete.gif" />
                                                </a>
                                            </cti:checkProperty>
                                        </c:when>
                                        <c:otherwise>
                                            <cti:checkProperty property="CBCSettingsRole.CBC_DATABASE_EDIT">
                                                <a href="/editor/pointBase.jsf?parentId=${row.parentId}&itemid=${row.itemId}" class="tierIconLink">
                                                    <img class="tierImg" src="/WebConfig/yukon/Icons/pencil.gif"/>
                                                </a>
                                                <a href="/editor/deleteBasePoint.jsf?value=${row.itemId}" class="tierIconLink">
                                                    <img class="tierImg" src="/WebConfig/yukon/Icons/delete.gif" height="16" width="16"/>
                                                </a>
                                            </cti:checkProperty>
                                        </c:otherwise>
                                    </c:choose>
                                    <c:if test="${row.controller}">
                                        <cti:checkProperty property="CBCSettingsRole.CBC_DATABASE_EDIT">
                                            <a class="tierIconLink" href="/editor/copyBase.jsf?itemid=${row.itemId}&type=1>"><img src="/WebConfig/yukon/Icons/copy.gif" class="tierImg"/></a>
                                        </cti:checkProperty>
                                    </c:if>
                                    ${row.name}
                                </td>
            	                <td nowrap="nowrap">${row.itemType}</td>
            	                <td nowrap="nowrap">${row.itemDescription}</td>
            	                <td>${row.parentString}</td>
            	            </tr>
            	        </c:forEach>
            	    </table>
                    
            	</c:otherwise>
            </c:choose>
            
        </tags:pagedBox>
        
    </form>
    
</cti:standardPage>