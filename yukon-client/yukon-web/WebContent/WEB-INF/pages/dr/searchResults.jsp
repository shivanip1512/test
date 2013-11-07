<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr"%>

<cti:standardPage module="dr" page="searchResults">

	<tags:simpleDialog id="drDialog" />

	<c:set var="baseUrl" value="/dr/search" />
        
	<div id="findForm" class="box action-area">
	   <form accept-charset="ISO-8859-1" method="get" action="/dr/search" enctype="application/x-www-form-urlencoded">
            <label class="box fl">
                <cti:msg key="yukon.web.modules.dr.searchResults.searchBoxLabel" />
                <input type="text" id="textinput" class="search_text" value="${quickSearchBean.name}" name="name" />
            </label>
            <cti:button nameKey="search" type="submit" icon="icon-magnifier" classes="fl"/>
	   </form>
    </div>
	<br />


	<cti:msg var="searchTitle"
		key="yukon.web.modules.dr.searchResults.searchResult"
		argument="${quickSearchBean.name}" />
	<tags:pagedBox title="${searchTitle}" searchResult="${searchResult}"
		baseUrl="${baseUrl}">
		<c:choose>
			<c:when test="${searchResult.hitCount == 0}">
				<cti:msg key="yukon.web.modules.dr.searchResults.noResults" />
			</c:when>
			<c:otherwise>
				<table class="compact-results-table row-highlighting">
					<tr>
						<th><tags:sortLink nameKey="nameHeader" baseUrl="${baseUrl}" fieldName="NAME"/></th>
						<th><tags:sortLink nameKey="typeHeader" baseUrl="${baseUrl}" fieldName="TYPE"/></th>
						<th><tags:sortLink nameKey="stateHeader" baseUrl="${baseUrl}" fieldName="STATE"/></th>
						<th><cti:msg
							key="yukon.web.modules.dr.searchResults.actionsHeader" /></th>
					</tr>
					<c:forEach var="pao" items="${searchResult.resultList}">
						<tr>
							<td><cti:paoDetailUrl yukonPao="${pao}">
								<spring:escapeBody htmlEscape="true">${pao.name}</spring:escapeBody>
							</cti:paoDetailUrl></td>
							<td><cti:msg
								key="yukon.web.modules.dr.paoType.${pao.paoIdentifier.paoType}" />
							</td>
							<td><dr:stateText pao="${pao}" /></td>
							<td><dr:listActions pao="${pao}" /></td>
						</tr>
					</c:forEach>
				</table>
			</c:otherwise>
		</c:choose>
	</tags:pagedBox>
</cti:standardPage>
