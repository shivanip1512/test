<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu"%>

<cti:standardPage module="adminSetup" page="config">

<div id="page-actions" class="dn">
    <cm:dropdownOption key="yukon.web.modules.adminSetup.config.testEmail" data-popup=".js-test-email-dialog" icon= "icon-email"/>
</div>

<div class= "dn js-test-email-dialog" id = "adminSetup-testEmail-popup" data-dialog
    data-title="<cti:msg2 key="yukon.web.modules.adminSetup.config.testEmail"/>"
    data-ok-text="<cti:msg2 key="yukon.web.modules.adminSetup.config.Send"/>"
    data-url="<cti:url value="/admin/config/emailTestPopup"/>"
    data-event="yukon:adminSetup:config:sendTestEmail">
</div>

<div class="clear dashboard">
    <c:forEach items="${categories}" var="category">
        <tags:sectionContainer2 nameKey="${category.first}">
            <div class="column-12-12">
                <div class="column one">
                    <c:forEach items="${category.second}" var="subcategory" varStatus="status">
                        <fmt:formatNumber var="halfSubCategories" value="${fn:length(category.second) / 2}" pattern="#"/>
                        <c:if test="${status.index == halfSubCategories}">
                            </div>
                            <div class="column two nogutter">
                        </c:if>
                        <c:set var="displaySubcategory" value="false"/>
                        <c:choose>
                            <c:when test="${subcategory.first.roleProperty == null}">                        
                                <c:set var="displaySubcategory" value="true"/>
                            </c:when>
                            <c:otherwise>
                                <cti:checkRolesAndProperties value="${subcategory.first.roleProperty}">
                                    <c:set var="displaySubcategory" value="true"/>
                                </cti:checkRolesAndProperties>
                            </c:otherwise>
                        </c:choose>
                        <c:if test="${displaySubcategory}">
                             <div class="category">
                                <cti:url value="edit" var="category_url"><cti:param name="category" value="${subcategory.first}"/></cti:url>
                                <cti:button renderMode="appButton" icon="icon-32 ${subcategory.second}" href="${category_url}"/>
                                <div class="box meta">
                                    <div><a class="title" href="${category_url}"><i:inline key="yukon.common.setting.subcategory.${subcategory.first}"/></a></div>
                                    <div class="detail"><i:inline key="yukon.common.setting.subcategory.${subcategory.first}.description"/></div>
                                </div>
                            </div>
                        </c:if>
                    </c:forEach>
                </div>
            </div>
        </tags:sectionContainer2>
    </c:forEach>
</div>

<cti:includeScript link="/resources/js/pages/yukon.adminSetup.config.js"/>
</cti:standardPage>