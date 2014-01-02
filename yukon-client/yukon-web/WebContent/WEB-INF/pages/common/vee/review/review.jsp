<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msg var="pageTitle" key="yukon.web.modules.common.vee.review.home.pageTitle"/>
<cti:msg var="displayTypesSectionHeader" key="yukon.web.modules.common.vee.review.home.displayTypes"/>
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

<cti:standardPage title="${pageTitle}" module="monitors">
    <cti:standardMenu menuSelection="validation|review"/>

    <cti:breadCrumbs>
        <cti:crumbLink url="/dashboard" title="Operations Home"  />
        <cti:crumbLink url="/common/veeReview/home" title="${pageTitle}" />
    </cti:breadCrumbs>
    
    <cti:includeScript link="/JavaScript/veeReview.js"/>

    <h2>${pageTitle}</h2>
    <br>

    <form id="reloadForm" action="/common/veeReview/home" method="get" data-reloadmsg="${reloading}">
           <c:if test="${!noPoints}">
               <input type="hidden" name="afterPaoId" value="${afterPaoId}">
           </c:if>
    </form>

    <c:choose>

    <c:when test="${fn:length(groupedExtendedReviewPoints) == 0}">
        <table class="results-table">
        <tr><td style="text-align:center;font-style:italic;" class="subtle">
            <br>
            <b>No More Points to Review<b>
            <br><br>
            <a href="/common/veeReview/home">Reload to check for unreviewed points.</a> 
            <br><br>
        </td></tr>
        </table>
    </c:when>

    <c:otherwise>
    
    <form id="saveForm" action="/common/veeReview/save" method="post">
    
        <input type="hidden" name="afterPaoId" value="${nextPaoId}">
        <input type="hidden" id="checkAllState" value="">
        
        <c:if test="${!noPoints}">
        <table cellpadding="0" cellspacing="0" style="width:95%;">
            <tr>

                <%-- DISPLAY TYPES --%>
                <td style="width:35%;vertical-align:top;padding-right:40px;">
                
                    <tags:sectionContainer title="${displayTypesSectionHeader}">
                    
                        <cti:dataGrid cols="2" tableClasses="compact-results-table" tableStyle="width:90%;">
                        
                            <c:forEach var="displayType" items="${displayTypes}">
    
                                <cti:dataGridCell>
                                    <label>
                                        <input type="checkbox" name="${displayType.rphTag}" <c:if test="${displayType.checked}">checked</c:if>>
                                        <cti:logo key="${displayType.rphTag.logoKey}"/>
                                        <cti:msg key="${displayType.rphTag.formatKey}"/>
                                        (${displayType.count})
                                    </label>
                                </cti:dataGridCell>
                            
                            </c:forEach>
                            
                            <cti:dataGridCell>
                                <input id="reloadButton" type="button" onclick="Yukon.ui.veeReview.reloadForm();" value="${reload}"> <img id="reloadSpinner" style="display:none;" src="/WebConfig/yukon/Icons/spinner.gif">
                            </cti:dataGridCell>
                        
                        </cti:dataGrid>
                     
                    </tags:sectionContainer>
                </td>
                
                <%-- INSTRUCTION --%>
                <td style="vertical-align:top;">
                    <tags:sectionContainer title="${instructionsHeader}">
                    <table class="compact-results-table" style="width:100%;">
                        
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
        <table class="results-table row-highlighting">
        
            <tr>
                <th>${device}</th>
                <th>${previous}</th>
                <th>${flagged}</th>
                <th>${next}</th>
                <th align="center">
                    ${delete}
                    <cti:icon icon="icon-cross" title="Check/Uncheck All" onclick="Yukon.ui.veeReview.checkUncheckAll('DELETE');" classes="pointer"/>
                </th>
                <th align="center" >
                    ${accept}
                    <cti:icon icon="icon-tick" title="Check/Uncheck All" onclick="Yukon.ui.veeReview.checkUncheckAll('ACCEPT');" classes="pointer"/>
                </th>
            </tr>
            
            
            
            <c:forEach var="entry" items="${groupedExtendedReviewPoints}">
            
                <c:set var="pList" value="${entry.value}"/>
            
                <c:forEach var="p" items="${pList}" varStatus="status">
                
                    <c:set var="changeId" value="${p.reviewPoint.changeId}"/>
            
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
                                    <cti:logo key="${otherTag.logoKey}"/>
                                </c:forEach> 
                                <cti:logo key="${p.reviewPoint.rphTag.logoKey}"/>
                            </div>
                        </td>
                        
                        <td><cti:pointValueFormatter value="${p.nextPointValue}" format="FULL"/></td>
                        
                        <td align="center" class="ACTION_TD pointer">
                            <cti:icon icon="icon-cross" disabled="disabled" id="ACTION_DELETE_IMG_${changeId}"/>
                        </td>
                        
                        <td align="center" class="ACTION_TD pointer">
                            <cti:icon icon="icon-tick" disabled="disabled" id="ACTION_ACCEPT_IMG_${changeId}"/>
                            <input id="ACTION_${changeId}" name="ACTION_${changeId}" type="hidden" value="">
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