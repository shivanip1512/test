<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:standardPage module="capcontrol" page="search.${pageName}">
    <%@include file="/capcontrol/capcontrolHeader.jspf"%>
    
    <jsp:setProperty name="CtiNavObject" property="moduleExitPage" value=""/>
    
    <c:set var="baseUrl" value="/spring/capcontrol/search/searchResults"/>
    
    <form id="parentForm" action="feeders.jsp" method="post">
        <input type="hidden" name="${lastAreaKey}" />
        <input type="hidden" name="${lastSubKey}" />
    
        <tags:pagedBox2 nameKey="searchContainer"
                arguments="${label},${resultsFound}" 
                argumentSeparator=","
                searchResult="${searchResult}" 
                baseUrl="${baseUrl}"
                styleClass="padBottom">
            <c:choose>
            
                <c:when test="${searchResult.hitCount == 0}">
                    <cti:msg key="yukon.web.modules.dr.searchResults.noResults" />
                </c:when>
            
                <c:otherwise>
                
                    <table id="resTable" class="compactResultsTable rowHighlighting" align="center">
                        <tr>
                            <th><i:inline key=".name"/></th>
                            <th nowrap="nowrap"><i:inline key=".itemType"/></th>
                            <th><i:inline key=".description"/></th>
                            <th><i:inline key=".parent"/></th>
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
            
        </tags:pagedBox2>
        
    </form>
    
</cti:standardPage>