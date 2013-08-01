<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="dialog" tagdir="/WEB-INF/tags/dialog"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<dialog:ajaxPage nameKey="removeType" module="tools" page="configs.config" okEvent="yukonDialogSubmit" id="supportedTypePopup">
    <form action="removeSupportedType">
        <input type="hidden" name="configId" value="${configId}"/>
        <input type="hidden" name="paoType" value="${paoType}"/>
        <i:inline key=".confirmSupportedTypeRemove" arguments="${paoType.dbString}"/>
        <c:if test="${! empty removedTypes}">
            <br><br>
            <i:inline key=".removedCategoryTypes" arguments="${removedTypes}"/>
            <ul>
                <c:forEach var="type" items="${removedTypes}">
                    <li><i:inline key="${type.formatKey}"/></li>
                </c:forEach>
            </ul>
        </c:if>
    </form>
</dialog:ajaxPage>
