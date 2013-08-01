<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="dialog" tagdir="/WEB-INF/tags/dialog"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<dialog:ajaxPage nameKey="addType" module="tools" page="configs.config" okEvent="yukonDialogSubmit" id="supportedTypePopup">
    <form action="addSupportedType">
        <input type="hidden" name="configId" value="${configId}"/>
        <input type="hidden" name="paoType" value="${paoType}"/>
        <i:inline key=".confirmSupportedTypeAdd" arguments="${paoType.dbString}"/>
        <c:if test="${! empty requiredTypes}">
            <br><br>
            <i:inline key=".requiredCategoryTypes" arguments="${requiredTypes}"/>
            <ul>
                <c:forEach var="type" items="${requiredTypes}">
                    <li><i:inline key="${type.formatKey}"/></li>
                </c:forEach>
            </ul>
        </c:if>
    </form>
</dialog:ajaxPage>
