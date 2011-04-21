<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr"%>

<cti:standardPage module="dr" page="searchResults">

	<tags:simpleDialog id="drDialog" />
	<cti:includeCss link="/WebConfig/yukon/styles/calendarControl.css" />
	<cti:includeScript link="/JavaScript/calendarControl.js" />
	<cti:includeScript link="/JavaScript/calendarTagFuncs.js" />
	<dr:favoriteIconSetup />

	<c:set var="baseUrl" value="/spring/dr/search" />
	<cti:msg var="submitButtonSrc"
		key="yukon.web.modules.dr.searchResults.searchBoxSubmitImg" />
	<cti:msg var="submitButtonAlt"
		key="yukon.web.modules.dr.searchResults.searchBoxSubmitAlt" />
        
	<div id="findForm" class="box">
	   <form accept-charset="ISO-8859-1" method="get" action="/spring/dr/search" enctype="application/x-www-form-urlencoded">
            <label class="box fl">
                <cti:msg key="yukon.web.modules.dr.searchResults.searchBoxLabel" />
                <input type="text" id="textinput" class="search_text" value="${quickSearchBean.name}" name="name" />
            </label>
            <input type="submit" class="icon search_button" alt="${submitButtonAlt}" src="${submitButtonSrc}" />
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
				<table class="compactResultsTable rowHighlighting">
					<tr>
						<th></th>
						<th><tags:sortLink
							key="yukon.web.modules.dr.searchResults.nameHeader"
							baseUrl="${baseUrl}" fieldName="NAME" /></th>
						<th><tags:sortLink
							key="yukon.web.modules.dr.searchResults.typeHeader"
							baseUrl="${baseUrl}" fieldName="TYPE" /></th>
						<th><tags:sortLink
							key="yukon.web.modules.dr.searchResults.stateHeader"
							baseUrl="${baseUrl}" fieldName="STATE" /></th>
						<th><cti:msg
							key="yukon.web.modules.dr.searchResults.actionsHeader" /></th>
					</tr>
					<c:forEach var="pao" items="${searchResult.resultList}">
						<tr class="<tags:alternateRow odd="" even="altRow"/>">
							<td><dr:favoriteIcon paoId="${pao.paoIdentifier.paoId}"
								isFavorite="${favoritesByPaoId[pao.paoIdentifier.paoId]}" /></td>
							<td><cti:paoDetailUrl yukonPao="${pao}">
								<spring:escapeBody htmlEscape="true">${pao.name}</spring:escapeBody>
							</cti:paoDetailUrl></td>
							<td><cti:msg
								key="yukon.web.modules.dr.paoType.${pao.paoIdentifier.paoType}" />
							</td>
							<td><dr:stateText pao="${pao}" /></td>
							<td class="nonwrapping"><dr:listActions pao="${pao}" /></td>
						</tr>
					</c:forEach>
				</table>
			</c:otherwise>
		</c:choose>
	</tags:pagedBox>
</cti:standardPage>
