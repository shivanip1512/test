<%@ attribute name="id" required="true" %>
<%@ attribute name="path" required="true" %>
<%@ attribute name="icons" required="true" type="java.lang.Object" %>
<%@ attribute name="selectedIcon" required="true" type="com.cannontech.stars.dr.appliance.model.IconEnum" %>
<%@ attribute name="applianceCategoryIconMode" type="java.lang.Boolean" %>
<%@ attribute name="value" %>
<%@ tag body-content="empty" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<c:set var="previewAtts" value=" align=\"right\""/>
<c:set var="iconInputAtts" value=" colspan=\"2\""/>
<c:set var="baseDir" value="/WebConfig/yukon/Icons/"/>
<c:if test="${pageScope.applianceCategoryIconMode}">
    <c:set var="previewAtts" value=" rowspan=\"2\""/>
    <c:set var="iconInputAtts" value=""/>
    <c:set var="baseDir" value="/WebConfig/"/>
</c:if>

<cti:displayForPageEditModes modes="VIEW">
    <c:if test="${empty pageScope.value}">
        <i:inline key=".noIcon"/>
    </c:if>
    <c:if test="${!empty pageScope.value}">
        <img src="${pageScope.baseDir}${pageScope.value}"/>
    </c:if>
</cti:displayForPageEditModes>

<cti:displayForPageEditModes modes="EDIT,CREATE">
<spring:bind path="${pageScope.path}">

<table cellpadding="0" cellspacing="0">
    <tr>
        <td>
            <form:hidden id="${pageScope.id}HiddenIconInput" path="${pageScope.path}"/>
            <select id="${pageScope.id}IconSelect"
                onchange="${pageScope.id}.iconSelected()">
                <c:forEach var="icon" items="${pageScope.icons}">
                    <c:set var="selected" value=""/>
                    <c:if test="${icon == pageScope.selectedIcon}">
                        <c:set var="selected" value=" selected=\"true\""/>
                    </c:if>
                    <option value="${icon}"${selected}>
                        <cti:msg key="${icon}"/>
                    </option>
                </c:forEach>
            </select>
        </td>
        <td${pageScope.previewAtts}>
            <img id="${pageScope.id}IconPreviewImg"/>
        </td>
    </tr>
    <tr>
        <td${pageScope.iconInputAtts}>
            <input id="${pageScope.id}IconInput" type="text" size="50"
                onkeyup="${pageScope.id}.iconInputChanged()"
                onblur="${pageScope.id}.iconInputChanged()"/>
        </td>
    </tr>
</table>
<c:if test="${status.error}">
    <form:errors path="${path}" cssClass="errorMessage"/>
</c:if>

<script type="text/javascript">
${pageScope.id} = new IconChooser('${pageScope.id}', '${pageScope.baseDir}');
${pageScope.id}.iconFilenames = {
    <c:forEach var="icon" varStatus="status" items="${pageScope.icons}">
        '${icon}' : '${icon.filename}'<c:if test="${!status.last}">,</c:if>
    </c:forEach>
};

$('${pageScope.id}IconInput').value = $('${pageScope.id}HiddenIconInput').value;
${pageScope.id}.iconSelected();
</script>

</spring:bind>
</cti:displayForPageEditModes>