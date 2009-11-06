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
<cti:msg var="reload" key="yukon.web.modules.common.vee.review.home.reload"/>
<cti:msg var="reloading" key="yukon.web.modules.common.vee.review.home.reloading"/>
<cti:msg var="instructionsHeader" key="yukon.web.modules.common.vee.review.home.instructions.header"/>

<cti:url var="deleteImg" value="/WebConfig/yukon/Icons/delete.gif"/>
<cti:url var="acceptImg" value="/WebConfig/yukon/Icons/tick.gif"/>
<cti:url var="deleteDisabledImg" value="/WebConfig/yukon/Icons/delete_disabled_gray.gif"/>
<cti:url var="acceptDisabledImg" value="/WebConfig/yukon/Icons/tick_disabled_gray.gif"/>
	
<cti:standardPage title="${pageTitle}" module="blank">

    <cti:breadCrumbs>
	    <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home"  />
	    <cti:crumbLink url="/spring/common/veeReview/home" title="${pageTitle}" />
	</cti:breadCrumbs>
	
	<cti:standardMenu menuSelection=""/>
	<cti:includeScript link="/JavaScript/veeReview.js"/>

    <h2>${pageTitle}</h2>
    <br>
    
    <script type="text/javascript">
    
    	function reloadForm() {
        	
        	$('saveButton').disable();
        	$('reloadButton').value = '${reloading}';
        	$('reloadButton').disable();
        	$('reloadSpinner').show();

    		$$('input.TYPE_CHECKBOX').each(function(el) {
        		
           		var h = document.createElement('input');
           		h.setAttribute('type', 'hidden');
           		h.setAttribute('name', el.getAttribute('name'));
           		h.setAttribute('value', el.checked);
    			$('reloadForm').appendChild(h);

    			el.disable();
    		});

			$('reloadForm').submit();
    	}
    
    </script>
    
    <cti:verifyRolesAndProperties value="VALIDATION_ENGINE"/>
    
    <form id="reloadForm" action="/spring/common/veeReview/home" method="get">
   		<c:if test="${!noPoints}">
   			<input type="hidden" name="afterPaoId" value="${afterPaoId}">
   		</c:if>
    </form>
    
    <c:choose>
    
    <c:when test="${fn:length(groupedExtendedReviewPoints) == 0}">
    	<table class="resultsTable">
    	<tr><td style="text-align:center;font-style:italic;" class="subtleGray">
			<br>
			<b>No More Points to Review<b>
			<br><br>
			<a href="/spring/common/veeReview/home">Reload to check for unreviewed points.</a> 
			<br><br>
		</td></tr>
		</table>
    </c:when>

	<c:otherwise>
	
    <form id="saveForm" action="/spring/common/veeReview/save" method="post">
    
    	<input type="hidden" name="afterPaoId" value="${nextPaoId}">
    	<input type="hidden" id="checkAllState" value="">
    	
    	<c:if test="${!noPoints}">
    	<table cellpadding="0" cellspacing="0" style="width:95%;">
    		<tr>

				<%-- DISPLAY TYPES --%>
				<td style="width:35%;vertical-align:top;padding-right:40px;">
				
					<tags:sectionContainer title="${displayTypes}">
					<table class="compactResultsTable" style="width:90%;">
				    	<tr>
				    		<td>
				    			<label>
				    				<input type="checkbox" name="PU" class="TYPE_CHECKBOX" <c:if test="${PU}">checked</c:if>>
				    				<tags:rphTagIcon tag="${allTagsMap['PU']}"/> 
				    				<cti:msg key="${allTagsMap['PU'].key}"/>
				    			</label>
				    		</td>
				    		<td>
				    			<label>
				    				<input type="checkbox" name="PD" class="TYPE_CHECKBOX" <c:if test="${PD}">checked</c:if>>
				    				<tags:rphTagIcon tag="${allTagsMap['PD']}"/>
				    				<cti:msg key="${allTagsMap['PD'].key}"/>
				    			</label>
				    		</td>
				    	</tr>
				    	<tr>
				    		<td>
				    			<label>
					    			<input type="checkbox" name="UU" class="TYPE_CHECKBOX" <c:if test="${UU}">checked</c:if>>
					    			<tags:rphTagIcon tag="${allTagsMap['UU']}"/>
					    			<cti:msg key="${allTagsMap['UU'].key}"/>
				    			</label>
				    		</td>
				    		<td>
				    			<label>
					    			<input type="checkbox" name="UD" class="TYPE_CHECKBOX" <c:if test="${UD}">checked</c:if>>
					    			<tags:rphTagIcon tag="${allTagsMap['UD']}"/>
					    			<cti:msg key="${allTagsMap['UD'].key}"/>
				    			</label>
				    		</td>
				    	</tr>
				    	<tr>
				    		<td>
				    			<label>
					    			<input type="checkbox" name="UDC" class="TYPE_CHECKBOX" <c:if test="${UDC}">checked</c:if>>
					    			<tags:rphTagIcon tag="${allTagsMap['UDC']}"/>
					    			<cti:msg key="${allTagsMap['UDC'].key}"/>
				    			</label>
				    		</td>
				    		<td>
				    			<input id="reloadButton" type="button" onclick="reloadForm();" value="${reload}"> <img id="reloadSpinner" style="display:none;" src="/WebConfig/yukon/Icons/indicator_arrows.gif">
				    		</td>
				    	</tr>
				    </table>
				    </tags:sectionContainer>
				</td>
				
				<%-- INSTRUCTION --%>
				<td style="vertical-align:top;">
					<tags:sectionContainer title="${instructionsHeader}">
					<table class="compactResultsTable" style="width:100%;">
				    	
				    	<tr>
				    		<td><b>${delete}</b></td>
				    		<td><b>${accept}</b></td>
				    	</tr>
				    	<tr>
				    		<td style="padding-left:10px;">${deletePopupInfo}</td>
				    		<td style="padding-left:10px;">${acceptPopupInfo}</td>
				    	</tr>
				    </table>
				    </tags:sectionContainer>
				</td>
    		
    		</tr>
    	</table>
	    <br>
	    </c:if>
	    
	    <%-- REVIEW TABLE --%>
	    <table class="resultsTable rowHighlighting">
	    
	    	<tr>
	    		<th>${device}</th>
	    		<th>${previous}</th>
	    		<th>${flagged}</th>
	    		<th>${next}</th>
	    		<th align="center">
	    			${delete}
	    			<img src="${deleteImg}" title="Check/Uncheck All" onclick="checkUncheckAll('DELETE');" class="pointer">
	    		</th>
	    		<th align="center">
	    			${accept}
	    			<img src="${acceptImg}" title="Check/Uncheck All" onclick="checkUncheckAll('ACCEPT');" class="pointer">
	    		</th>
	    	</tr>
	    	
	    	
	    	
	    	<c:forEach var="entry" items="${groupedExtendedReviewPoints}">
	    	
	    		<c:set var="pList" value="${entry.value}"/>
	    	
	    		<c:forEach var="p" items="${pList}" varStatus="status">
	    		
	    			<c:set var="changeId" value="${p.reviewPoint.changeId}"/>
	    			<c:set var="pointId" value="${p.reviewPoint.pointValue.id}"/>
	    	
		    		<tr>
		    		
		    			<c:if test="${status.count == 1}">
			    			<td rowspan="${fn:length(pList)}" style="vertical-align:top;">
			    				<cti:paoDetailUrl yukonPao="${p.reviewPoint.displayablePao}">
			    					${entry.key}
			    				</cti:paoDetailUrl>
			    			</td>
			    		</c:if>
			    	
			    		<td>
			    			<cti:pointValueFormatter value="${p.prevPointValue}" format="FULL"/>
			    		</td>
			    		
			    		<td title="${p.reviewPoint.changeId}">
			    		
			    			<div style="float:left;">
			    				<cti:pointValueFormatter value="${p.reviewPoint.pointValue}" format="FULL"/> 
			    			</div>
			    			
			    			<div style="float:right;padding-right:10px;">
			    				<c:forEach var="otherTag" items="${p.otherTags}">
			    					<tags:rphTagIcon tag="${otherTag}"/>
			    				</c:forEach> 
			    				<tags:rphTagIcon tag="${p.reviewPoint.rphTag}"/>
			    			</div>
			    		</td>
			    		
			    		<td><cti:pointValueFormatter value="${p.nextPointValue}" format="FULL"/></td>
			    		
			    		<td align="center" class="ACTION_TD pointer">
			    			<img id="ACTION_DELETE_IMG_${changeId}_${pointId}" src="${deleteDisabledImg}">
			    		</td>
			    		
			    		<td align="center" class="ACTION_TD pointer">
			    			<img id="ACTION_ACCEPT_IMG_${changeId}_${pointId}" src="${acceptDisabledImg}">
			    			<input id="ACTION_${changeId}_${pointId}" name="ACTION_${changeId}_${pointId}" type="hidden" value="">
			    		</td>
			    	</tr>
		    	
		    	</c:forEach>
	    	
	    	</c:forEach>
	    	
	    </table>
	    
	    <br>
	    <c:if test="${fn:length(groupedExtendedReviewPoints) > 0}">
	    	<tags:slowInput id="saveButton" myFormId="saveForm" labelBusy="${saveAndContinue}" label="${saveAndContinue}"/>
	    </c:if>
    
    </form>
    
    </c:otherwise>
    </c:choose>
        
</cti:standardPage>