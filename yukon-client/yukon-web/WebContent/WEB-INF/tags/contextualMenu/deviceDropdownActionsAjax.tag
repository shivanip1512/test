<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:includeScript link="/JavaScript/dropdown_actions.js"/>

<%@ tag trimDirectiveWhitespaces="true" description="This tag is used to generate a contextual menu based on the passed in menuBeanId"%>

<%@ attribute name="menuBeanId" required="true" description="The bean id of the menu you want to render"%>
<%@ attribute name="key" %>

<div class="params dn">
    <input type="hidden" name="menuId" value="${pageScope.menuBeanId}"/>
    <jsp:doBody/>
</div>
<cm:dropdownActions containerCssClass="ajax-menu" menuCssClass="long-menu-list" key="${pageScope.key}"/>