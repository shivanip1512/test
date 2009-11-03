<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msg var="pageTitle" key="yukon.web.modules.common.vee.review.home.pageTitle"/>
<cti:msg var="displayTypes" key="yukon.web.modules.common.vee.review.home.displayTypes"/>
<cti:msg var="displayTypesPopupInfo" key="yukon.web.modules.common.vee.review.home.displayTypes.popupInfo"/>
<cti:msg var="device" key="yukon.web.modules.common.vee.review.home.header.device"/>
<cti:msg var="previous" key="yukon.web.modules.common.vee.review.home.header.previous"/>
<cti:msg var="flagged" key="yukon.web.modules.common.vee.review.home.header.flagged"/>
<cti:msg var="next" key="yukon.web.modules.common.vee.review.home.header.next"/>
<cti:msg var="delete" key="yukon.web.modules.common.vee.review.home.header.delete"/>
<cti:msg var="deletePopupInfo" key="yukon.web.modules.common.vee.review.home.header.delete.popupInfo"/>
<cti:msg var="accept" key="yukon.web.modules.common.vee.review.home.header.accept"/>
<cti:msg var="acceptPopupInfo" key="yukon.web.modules.common.vee.review.home.header.accept.popupInfo"/>
<cti:msg var="saveAndContinue" key="yukon.web.modules.common.vee.review.home.saveAndContinue"/>
<cti:msg var="save" key="yukon.web.modules.common.vee.review.home.save"/>

<cti:url var="deleteDisabledImg" value="/WebConfig/yukon/Icons/delete_disabled_gray.gif"/>
<cti:url var="acceptDisabledImg" value="/WebConfig/yukon/Icons/tick_disabled_gray.gif"/>
	
<cti:standardPage title="${pageTitle}" module="blank">

    <cti:breadCrumbs>
	    <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home"  />
	    <cti:crumbLink title="${pageTitle}"/>
	</cti:breadCrumbs>
	
	<cti:standardMenu menuSelection=""/>
	<cti:includeScript link="/JavaScript/veeReview.js"/>

    <h2>${pageTitle}</h2>
    <br>
    
    <form id="saveForm" action="/spring/common/veeReview/save" method="post">
    
    	<input type="hidden" name="prevPaoId" value="${prevPaoId}">
    	<input type="hidden" id="checkAllState" value="">
    	
	    <%-- DISPLAY TYPES --%>
	    <table class="compactResultsTable" style="width:40%;">
	    	<tr>
				<th colspan="4">
					${displayTypes}
					<tags:helpInfoPopup title="${displayTypes}">
			    		${displayTypesPopupInfo}
			    	</tags:helpInfoPopup>
				</th>
			</tr>
	    	<tr>
	    		<td><label><input type="checkbox" name="PU" <c:if test="${PU}">checked</c:if>> <tags:rphTagIcon tag="${allTagsMap['PU']}"/> <cti:msg key="${allTagsMap['PU'].key}"/></label></td>
	    		<td><label><input type="checkbox" name="PD" <c:if test="${PD}">checked</c:if>> <tags:rphTagIcon tag="${allTagsMap['PD']}"/> <cti:msg key="${allTagsMap['PD'].key}"/></label></td>
	    	</tr>
	    	<tr>
	    		<td><label><input type="checkbox" name="UU" <c:if test="${UU}">checked</c:if>> <tags:rphTagIcon tag="${allTagsMap['UU']}"/> <cti:msg key="${allTagsMap['UU'].key}"/></label></td>
	    		<td><label><input type="checkbox" name="UD" <c:if test="${UD}">checked</c:if>> <tags:rphTagIcon tag="${allTagsMap['UD']}"/> <cti:msg key="${allTagsMap['UD'].key}"/></label></td>
	    	</tr>
	    	<tr>
	    		<td colspan="2"><label><input type="checkbox" name="UDC" <c:if test="${UDC}">checked</c:if>> <tags:rphTagIcon tag="${allTagsMap['UDC']}"/> <cti:msg key="${allTagsMap['UDC'].key}"/></label></td>
	    	</tr>
	    </table>
	    
	    <br>
	    
	    <%-- REVIEW TABLE --%>
	    <table class="resultsTable rowHighlighting">
	    
	    	<tr>
	    		<th>${device}</th>
	    		<th>${previous}</th>
	    		<th>${flagged}</th>
	    		<th>${next}</th>
	    		<th align="center">
	    			<a href="javascript:void(0);" title="Check/Uncheck All" onclick="checkUncheckAll('DELETE');">${delete}</a>
	    			<tags:helpInfoPopup title="${delete}">
			    		${deletePopupInfo}
			    	</tags:helpInfoPopup>
	    		</th>
	    		<th align="center">
	    			<a href="javascript:void(0);" title="Check/Uncheck All" onclick="checkUncheckAll('ACCEPT');">${accept}</a>
	    			<tags:helpInfoPopup title="${accept}">
			    		${acceptPopupInfo}
			    	</tags:helpInfoPopup>
	    		</th>
	    	</tr>
	    	
	    	<c:forEach var="entry" items="${groupedExtendedReviewPoints}">
	    	
	    		<c:set var="pList" value="${entry.value}"/>
	    	
	    		<c:forEach var="p" items="${pList}" varStatus="status">
	    	
		    		<tr>
		    		
		    			<c:if test="${status.count == 1}">
			    			<td rowspan="${fn:length(pList)}" style="vertical-align:top;">
			    				<cti:paoDetailUrl yukonPao="${p.reviewPoint.displayablePao}">
			    					${entry.key}
			    				</cti:paoDetailUrl>
			    			</td>
			    		</c:if>
			    	
			    		<td><cti:pointValueFormatter value="${p.prevPointValue}" format="FULL"/></td>
			    		<td title="${p.reviewPoint.changeId}">
			    			<cti:pointValueFormatter value="${p.reviewPoint.pointValue}" format="FULL"/> 
			    			
			    			<div style="float:right;padding-right:10px;">
			    				<c:forEach var="otherTag" items="${p.otherTags}">
			    					<tags:rphTagIcon tag="${otherTag}"/>
			    				</c:forEach> 
			    				<tags:rphTagIcon tag="${p.reviewPoint.rphTag}"/>
			    			</div>
			    		</td>
			    		<td><cti:pointValueFormatter value="${p.nextPointValue}" format="FULL"/></td>
			    		<td align="center" class="ACTION_TD"><img id="ACTION_DELETE_IMG_${p.reviewPoint.changeId}" src="${deleteDisabledImg}"></td>
			    		<td align="center" class="ACTION_TD"><img id="ACTION_ACCEPT_IMG_${p.reviewPoint.changeId}" src="${acceptDisabledImg}"><input id="ACTION_${p.reviewPoint.changeId}" name="ACTION_${p.reviewPoint.changeId}" type="hidden" value=""></td>
			    	</tr>
		    	
		    	</c:forEach>
	    	
	    	</c:forEach>
	    	
	    </table>
	    
	    <br>
	    <c:set var="saveButtonText" value="${saveAndContinue}"/>
	    <c:if test="${!hasMore}">
	    	<c:set var="saveButtonText" value="${save}"/>
	    </c:if>
	    
	    <tags:slowInput myFormId="saveForm" labelBusy="${saveButtonText}" label="${saveButtonText}"/>
    
    </form>
        
</cti:standardPage>