<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="dashboard" page="edit">

<div class="page-actions fr stacked-md"><cti:button icon="icon-plus-green" nameKey="addWidgets" data-popup=".js-add-widgets-popup"/></div>
<br/><br/>

    <div class="column-12-12">
        <div class="column one">
            <!-- TODO: Loop through and display column one widgets and parameters (maybe add an Edit mode to the widget tag) -->
            <c:forEach var="widget" items="${dashboard.column1Widgets}">
                <tags:widget bean="${widget.type.beanName}"/>
                <c:forEach var="param" items="${widget.parameters}">
                </c:forEach>
            </c:forEach>
        </div>
        <div class="column two nogutter">
            <!-- TODO: Loop through and display column two widgets and parameters (maybe add an Edit mode to the widget tag) -->
            <c:forEach var="widget" items="${dashboard.column2Widgets}">
                <tags:widget bean="${widget.type.beanName}"/>
                <c:forEach var="param" items="${widget.parameters}">
                </c:forEach>
            </c:forEach>
        </div>
    </div>
    
    <%-- ADD WIDGETS POPUP --%>
<div class="dn js-add-widgets-popup" data-dialog
    data-title="<cti:msg2 key=".addWidgets.label"/>"
    data-url="<cti:url value="/dashboards/${dashboard.id}/addWidgets"/>"></div>
    
</cti:standardPage>