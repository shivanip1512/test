<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="wp" tagdir="/WEB-INF/tags/widgetParameters" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<%@ attribute name="widget" required="true" type="java.lang.Object"%>
<%@ attribute name="path" %>

<cti:msg2 var="containerTitle" key="${widget.type.formatKey}"/>

<tags:boxContainer title="${containerTitle}" showArrows="true" hideEnabled="false" styleClass="cm">    
    <form:hidden path="${path}.id"/>
    <form:hidden path="${path}.dashboardId"/>
    <form:hidden path="${path}.type"/>

    <c:forEach var="parameter" items="${widget.type.parameters}">
        <c:set var="filePath" value="/WEB-INF/pages/dashboards/widgetParameters/${parameter.inputType.jspName}.jsp"/>
            <jsp:include flush="false" page="${filePath}">
                <jsp:param name="widgetId" value="${widget.id}"/>
                <jsp:param name="parameterName" value="${parameter.name}"/>
                <jsp:param name="parameterKey" value="${parameter.key}"/>
                <jsp:param name="path" value="${path}"/>
            </jsp:include>
    </c:forEach>

</tags:boxContainer>
