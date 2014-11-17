<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="amr" page="validation.review">
    <cti:includeScript link="/JavaScript/yukon.ami.validation.js"/>

    <cti:msg2 var="reloading" key=".reloading"/>
    
    <c:choose>
        <c:when test="${fn:length(groupedExtendedReviewPoints) == 0}">
            <span class="empty-list"><i:inline key=".emptyList" /></span>
        </c:when>
        <c:otherwise>
            <cti:url var="saveUrl" value="/amr/veeReview/save"/>
            <form id="review-form" action="${saveUrl}" method="post">
                <input type="hidden" id="checkAllState" value="">
                 <c:if test="${!noPoints}">
                    <div class="column-12-12 stacked clearfix">
                        <div class="column one">
                            <tags:sectionContainer2 nameKey="displayTypes">
                                <div class="column-12-12 clearfix">
                                    <div class="column one">
                                        <ul class="simple-list">
                                            <c:forEach items="${displayTypes}" var="displayType" begin="0" step="2" varStatus="index">
                                                <li>
                                                    <label class="notes">
                                                        <input type="checkbox" id="display-type-checkbox" name="${displayType.rphTag}" <c:if test="${displayType.checked}">checked</c:if>>
                                                        <cti:icon icon="${displayType.rphTag.iconClass}" classes="fn"/>
                                                        <cti:msg key="${displayType.rphTag.formatKey}"/>
                                                        <cti:dataUpdaterValue type="VALIDATION_PROCESSING" identifier="${displayType.rphTag}_VIOLATIONS" styleClass="badge"/>
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
                                                        <input type="checkbox" id="display-type-checkbox" name="${displayType.rphTag}" <c:if test="${displayType.checked}">checked</c:if>>
                                                        <cti:icon icon="${displayType.rphTag.iconClass}" classes="fn"/>
                                                        <cti:msg key="${displayType.rphTag.formatKey}"/>
                                                        <cti:dataUpdaterValue type="VALIDATION_PROCESSING" identifier="${displayType.rphTag}_VIOLATIONS" styleClass="badge"/>
                                                    </label>
                                                </li>
                                            </c:forEach>
                                        </ul>
                                    </div>
                                </div>
                            </tags:sectionContainer2>
                        </div>
                        <div class="column two nogutter">
                            <cti:url var="reviewUrl" value="/amr/veeReview/advancedProcessing"/>
                            <span class="fr"><a href="${reviewUrl}"><i:inline key=".advancedProcessing"/></a></span>
                        </div>
                    </div>
                </c:if>
                    
                <%-- REVIEW TABLE --%>
                <div id="reviewTable"  data-base-url="reviewTable" data-url="reviewTable">
                    <%@ include file="reviewTable.jsp" %>
                </div>
            </form>
        </c:otherwise>
    </c:choose>
</cti:standardPage>