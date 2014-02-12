<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog"%>

<cti:standardPage module="adminSetup" page="config">

<div class="clear dashboard">

    <c:forEach items="${categories}" var="category">
        <tags:sectionContainer2 nameKey="${category.first}">
            <div class="leftCol">
                <c:forEach items="${category.second}" begin="0" step="2" end="${fn:length(category.second)}" var="subcategory" varStatus="status">
                    <div class="category">
                        <cti:url value="edit" var="category_url"><cti:param name="category" value="${subcategory.first}"/></cti:url>
                        <a href="${category_url}" class="icon icon-32 fl ${subcategory.second}"></a>
                        <div class="box fl meta">
                            <div><a class="title" href="${category_url}"><i:inline key="yukon.common.setting.subcategory.${subcategory.first}"/></a></div>
                            <div class="detail"><i:inline key="yukon.common.setting.subcategory.${subcategory.first}.description"/></div>
                        </div>
                    </div>
                </c:forEach>
            </div>
            <div class="rightCol">
                <c:forEach items="${category.second}" begin="1" step="2" end="${fn:length(category.second)}" var="subcategory" varStatus="status">
                    <div class="category">
                        <cti:url value="edit" var="category_url"><cti:param name="category" value="${subcategory.first}"/></cti:url>
                        <a href="${category_url}" class="icon icon-32 fl ${subcategory.second}"></a>
                        <div class="box fl meta">
                            <div><a class="title" href="${category_url}"><i:inline key="yukon.common.setting.subcategory.${subcategory.first}"/></a></div>
                            <div class="detail"><i:inline key="yukon.common.setting.subcategory.${subcategory.first}.description"/></div>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </tags:sectionContainer2>
    </c:forEach>
    
</div>

</cti:standardPage>