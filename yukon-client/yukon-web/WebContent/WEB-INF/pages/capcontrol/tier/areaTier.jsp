<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="capTags" tagdir="/WEB-INF/tags/capcontrol"%>

<cti:standardPage module="capcontrol" page="areas">
    <%@include file="/capcontrol/capcontrolHeader.jspf"%>

    <cti:checkRolesAndProperties value="AREA_COMMANDS_AND_ACTIONS" level="ALL_DEVICE_COMMANDS_WITH_YUKON_ACTIONS,
        ALL_DEVICE_COMMANDS_WITHOUT_YUKON_ACTIONS,NONOPERATIONAL_COMMANDS_WITH_YUKON_ACTIONS,
        NONOPERATIONAL_COMMANDS_WITHOUT_YUKON_ACTIONS,YUKON_ACTIONS_ONLY">
        <script type="text/javascript">
            addCommandMenuBehavior('a[id^="areaState_"]');
        </script>
    </cti:checkRolesAndProperties>

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