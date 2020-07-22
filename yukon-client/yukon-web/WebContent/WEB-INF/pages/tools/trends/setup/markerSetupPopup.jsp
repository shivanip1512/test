<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:msgScope paths="yukon.common,modules.tools.trend">
    <c:forEach items="${colors}" var="color">
        <input type="hidden" class="js-color-item" value="${color}" />
    </c:forEach>

    <cti:url var="addMarkerUrl" value="/tools/trend/addPointOrMarker" />
    <form:form id="js-marker-setup-form" modelAttribute="trendSeries" method="POST" action="${addMarkerUrl}">
        <cti:csrfToken />
        <tags:hidden path="pointId"/>
        <tags:hidden path="type"/>
        <tags:hidden path="style"/>
        <tags:nameValueContainer2>
            <tags:nameValue2 nameKey=".valueTxt" nameClass="vam">
                <tags:input path="multiplier" />
                <cti:icon icon="icon-help" data-popup=".js-marker-value-help-dialog" classes="fn vatb cp" />
                <cti:msg2 var="markerValueHelpTitle" key=".markerValueHelp.title" />
                <div data-title="${markerValueHelpTitle}" class="dn js-marker-value-help-dialog" data-width="400" data-height="200">
                    <i:inline key=".markerValueHelpTxt" />
                </div>
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".label" nameClass="vam">
                <tags:input path="label" maxlength="40" />
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".color" nameClass="vam">
                <input type="hidden" class="js-color-input" name="color" value="${trendSeries.color.hexValue}" />
                <input type="text" class="js-color-picker" value="${trendSeries.color.hexValue}" />
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".axis" nameClass="vam">
                <tags:radioButtonGroup items="${axes}" path="axis" />
            </tags:nameValue2>
        </tags:nameValueContainer2>
    </form:form>
</cti:msgScope>
<cti:includeScript link="/resources/js/pages/yukon.tools.trend.setup.js" />