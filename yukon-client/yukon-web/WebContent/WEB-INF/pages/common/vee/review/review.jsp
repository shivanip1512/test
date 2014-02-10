<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="amr" page="validation.review">
    <cti:includeScript link="/JavaScript/veeReview.js"/>

    <%--<c:set var="pageMe" value="${fn:length(allReviewPoints) > 0}"/>  --%>

    <cti:msg2 var="reloading" key=".reloading"/>
    <form id="reloadForm" action="/common/veeReview/home" method="get" data-reloadmsg="${reloading}">
           <c:if test="${!noPoints}">
               <input type="hidden" name="afterPaoId" value="${afterPaoId}">
           </c:if>
    </form>
    <c:choose>

    <c:when test="${fn:length(groupedExtendedReviewPoints) == 0}">
        <span class="empty-list"><i:inline key=".emptyList" /></span>
    </c:when>

    <c:otherwise>

    <form id="review-form" action="/common/veeReview/save" method="post">
        <cti:csrfToken/>
        <input type="hidden" name="afterPaoId" value="${nextPaoId}">
        <input type="hidden" id="checkAllState" value="">

         <c:if test="${!noPoints}">
            <div class="column-10-14 stacked clearfix">
                <div class="column one">
                    <tags:sectionContainer2 nameKey="displayTypes">
                        <div class="column-12-12 clearfix">
                            <div class="column one">
                                <ul class="simple-list">
                                    <c:forEach items="${displayTypes}" var="displayType" begin="0" step="2" varStatus="index">
                                        <li>
                                            <label class="notes">
                                                <input type="checkbox" name="${displayType.rphTag}" <c:if test="${displayType.checked}">checked</c:if>>
                                                <cti:logo key="${displayType.rphTag.logoKey}"/>
                                                <cti:msg key="${displayType.rphTag.formatKey}"/>
                                                (${displayType.count})
                                            </label>
                                        </li>
                                    </c:forEach>
                                </ul>
                            </div>
                            <div class="column two nogutter">
                                <ul class="simple-list">
                                    <c:forEach items="${displayTypes}" var="displayType" begin="1" step="2" varStatus="index">
                                        <li>
                                            <label class="notes">
                                                <input type="checkbox" name="${displayType.rphTag}" <c:if test="${displayType.checked}">checked</c:if>>
                                                <cti:logo key="${displayType.rphTag.logoKey}"/>
                                                <cti:msg key="${displayType.rphTag.formatKey}"/>
                                                (${displayType.count})
                                            </label>
                                        </li>
                                    </c:forEach>
                                </ul>
                            </div>
                        </div>
                        <div class="action-area">
                            <cti:button id="reloadButton" nameKey="reload" type="button" onclick="Yukon.VeeReview.reloadForm();" busy="true" icon="icon-arrow-refresh"/>
                        </div>
                    </tags:sectionContainer2>
                </div>
                <div class="column two nogutter">
                    <tags:sectionContainer2 nameKey="instructions">
                        <div class="column-12-12">
                            <div class="column one">
                                <h4>
                                    <i:inline key=".delete"/>
                                </h4>
                                <i:inline key=".deleteInfo"/>
                            </div>
                            <div class="column two nogutter">
                                <h4>
                                    <i:inline key=".accept"/>
                                </h4>
                                <i:inline key=".acceptInfo"/>
                            </div>
                        </div>
                    </tags:sectionContainer2>
                </div>
            </div>
        </c:if>

        <%-- REVIEW TABLE --%>
        <table class="compact-results-table row-highlighting stacked has-alerts">
            <thead>
                <th></th>
                <th><i:inline key=".device"/></th>
                <th><i:inline key=".previous"/></th>
                <th><i:inline key=".flagged"/></th>
                <th><i:inline key=".next"/></th>
                <th>
                    <div class="fr">
                        <cti:button id="delete-all" icon="icon-cross" renderMode="buttonImage" classes="left" onclick="Yukon.VeeReview.checkUncheckAll('DELETE');"/>
                        <cti:button id="accept-all" icon="icon-tick" renderMode="buttonImage" classes="middle" onclick="Yukon.VeeReview.checkUncheckAll('ACCEPT');"/>
                        <cti:button nameKey="ignore" id="ignore-all" classes="right on" onclick="Yukon.VeeReview.checkUncheckAll('IGNORE');"/>
                    </div>
                </th>
            </thead>
            <tbody>
            <c:forEach var="entry" items="${groupedExtendedReviewPoints}">
                <c:set var="pList" value="${entry.value}"/>
                <c:forEach var="p" items="${pList}" varStatus="status">
                    <c:set var="changeId" value="${p.reviewPoint.changeId}"/>
                    <tr>
                        <td>
                            <c:forEach var="otherTag" items="${p.otherTags}">
                                <cti:logo key="${otherTag.logoKey}"/>
                            </c:forEach>
                            <cti:logo key="${p.reviewPoint.rphTag.logoKey}"/>
                        </td>
                        <td>
                            <cti:paoDetailUrl yukonPao="${p.reviewPoint.displayablePao}">
                                ${fn:escapeXml(entry.key)}
                            </cti:paoDetailUrl>
                        </td>
                        <td>
                            <div>
                                <cti:pointValueFormatter value="${p.prevPointValue}" format="VALUE_UNIT"/>
                            </div>
                            <div>
                                <cti:pointValueFormatter value="${p.prevPointValue}" format="DATE_QUALITY"/>
                            </div>
                        </td>
                        <td>
                            <div class="error">
                                <cti:pointValueFormatter value="${p.reviewPoint.pointValue}" format="VALUE_UNIT"/> 
                            </div>
                            <div class="error">
                                <cti:pointValueFormatter value="${p.reviewPoint.pointValue}" format="DATE_QUALITY"/> 
                            </div>
                        </td>
                        <td>
                            <div>
                                <cti:pointValueFormatter value="${p.nextPointValue}" format="VALUE_UNIT"/> 
                            </div>
                            <div>
                                <cti:pointValueFormatter value="${p.nextPointValue}" format="DATE_QUALITY"/> 
                            </div>
                        </td>

                        <td class="ACTION_TD pointer">
                            <div class="fr">
                                <cti:button id="ACTION_DELETE_${changeId}" icon="icon-cross" renderMode="buttonImage" classes=" ACTION_BTN left"/>
                                <cti:button id="ACTION_ACCEPT_${changeId}" icon="icon-tick" renderMode="buttonImage" classes="ACTION_BTN middle"/>
                                <cti:button nameKey="ignore" id="ACTION_IGNORE_${changeId}" classes="ACTION_BTN right on"/>
                                <input id="ACTION_${changeId}" name="ACTION_${changeId}" type="hidden" value="">
                            </div>
                    </tr>
                </c:forEach>
            </c:forEach>
            </tbody>
            <tfoot></tfoot>
        </table>

        <%-- <c:if test="${pageMe}">
            <cti:url value="page" var="baseUrl">
            </cti:url>
            <tags:pagingResultsControls baseUrl="${baseUrl}" result="${result}"/>
            
        </c:if>--%>
        <div class="action-area">
            <cti:button type="submit" nameKey="saveAndContinue" classes="f-disable-after-click primary action" busy="true"/>
        </div>
    </form>
    </c:otherwise>
    </c:choose>
</cti:standardPage>