<%@ tag trimDirectiveWhitespaces="true" description="This tag is used to generate a contextual menu based on the passed in menuBeanId" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="icon" description="The css class name of the icon to use. Default: 'icon-cog'" %>
<%@ attribute name="id" %>
<%@ attribute name="key" %>
<%@ attribute name="menuBeanId" required="true" description="The bean id of the menu you want to render" %>
<%@ attribute name="menuClasses" %>
<%@ attribute name="triggerClasses" %>
<%@ attribute name="showArrow" type="java.lang.Boolean" description="Show the down arrow? Default: 'true'" %>
<%@ attribute name="showIcon" type="java.lang.Boolean" description="Ignored when type = 'icon'. Default: 'true'" %>
<%@ attribute name="type" description="The type of this element. Either 'button', 'link', or 'icon'. Default: 'icon'. 'link' and 'button' require a 'key'" %>

<div class="params dn">
    <input type="hidden" name="menuId" value="${pageScope.menuBeanId}"/>
    <jsp:doBody/>
</div>
<cm:dropdown triggerClasses="ajax-menu ${pageScope.triggerClasses}"
    icon="${pageScope.icon}"
    id="${pageScope.id}"
    key="${pageScope.key}"
    menuClasses="long-menu-list"
    showArrow="${pageScope.showArrow}"
    showIcon="${pageScope.showIcon}"
    type="${pageScope.type}"/>