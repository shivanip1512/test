<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>

<cti:standardPage module="dashboard" page="view">

    <div id="page-actions" class="dn">
        <cti:url var="editUrl" value="/dashboards/${dashboard.id}/edit"/>
        <cm:dropdownOption key=".edit" icon="icon-pencil" href="${editUrl}"/>
    </div>

    <div class="column-12-12">
        <div class="column one">
            <c:forEach var="widget" items="${dashboard.column1Widgets}">
                <tags:widget bean="${widget.type.beanName}" paramMap="${widget.parameters}"/>
            </c:forEach>
        </div>
        <div class="column two nogutter">
            <c:forEach var="widget" items="${dashboard.column2Widgets}">
                <tags:widget bean="${widget.type.beanName}" paramMap="${widget.parameters}"/>
            </c:forEach>        
        </div>
    </div>
    
</cti:standardPage>