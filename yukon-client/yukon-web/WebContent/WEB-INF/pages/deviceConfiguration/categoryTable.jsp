<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<table id="categoryList" class="compact-results-table dashed">
    <thead>
        <tr>
            <c:forEach var="column" items="${catColumns}">
                <tags:sort column="${column}" />
            </c:forEach>
        </tr>
    </thead>
    <tfoot></tfoot>
    <tbody>
        <c:forEach var="category" items="${categories}">
            <tr>
                <td><cti:url var="url" value="category/view">
                        <cti:param name="categoryId" value="${category.categoryId}" />
                    </cti:url> <a href="${url}">${fn:escapeXml(category.categoryName)}</a>
                </td>
                <td><cti:msg2 var="catType" key="yukon.web.modules.tools.configs.category.${category.categoryType}.title" />
                    ${fn:escapeXml(catType)}</td>
                <td>
                    <div id="category-assignments-${category.categoryId}" class="dn">
                        <c:forEach var="configName" items="${category.configNames}">
                            <div class="detail">${fn:escapeXml(configName)}</div>
                        </c:forEach>
                    </div>
                    <div data-tooltip="#category-assignments-${category.categoryId}">
                        ${fn:length(category.configNames)}</div>
                </td>
            </tr>
        </c:forEach>
    </tbody>
</table>
