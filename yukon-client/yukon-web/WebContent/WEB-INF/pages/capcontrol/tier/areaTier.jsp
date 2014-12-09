<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="capTags" tagdir="/WEB-INF/tags/capcontrol"%>

<cti:standardPage module="capcontrol" page="areas">
    <%@include file="/capcontrol/capcontrolHeader.jspf"%>

    <c:if test="${hasAreaControl}">
        <script type="text/javascript">
            addCommandMenuBehavior('a[id^="areaState_"]');
        </script>
    </c:if>

    <c:forEach var="item" items="${areasMap}">
        <c:set var="areas" value="${item.value}" />
        <c:set var="areaType" value="${item.key}" />
        <tags:sectionContainer2 nameKey="areasContainer.${areaType.type}" styleClass="stacked-md">
            <div class="scroll-xl">
                <%@ include file="areaTierTable.jsp" %>
            </div>
        </tags:sectionContainer2>
    </c:forEach>
</cti:standardPage>