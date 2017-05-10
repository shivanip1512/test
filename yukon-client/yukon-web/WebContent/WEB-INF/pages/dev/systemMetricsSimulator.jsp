<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<cti:standardPage module="dev" page="systemMetricsSimulator">
    <form:form modelAttribute="metricSimulatorValues" action="setValues">
        <cti:csrfToken/>
        
        <tags:checkbox path="active" descriptionNameKey=".active"/><br/><br/>
        <c:forEach items="${metricSimulatorValues.metricValues}" var="metrics">
            <div class="stacked-lg">
                <h3><i:inline key="yukon.web.modules.support.systemHealth.metric.${metrics.key.keySuffix}"/></h3>
                <tags:nameValueContainer2>
                    <c:forEach items="${metrics.value}" var="metricValues" varStatus="status">
                        <tags:nameValue2 nameKey=".metrics.${metricValues.key}">
                            <form:input path="metricValues['${metrics.key}']['${metricValues.key}']"/>
                        </tags:nameValue2>
                    </c:forEach>
                </tags:nameValueContainer2>
            </div>
        </c:forEach>
        
        <cti:button type="submit" label="Submit"/>
    </form:form>
</cti:standardPage>