<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<cti:url value="appCatAssignmentsPage" var="baseUrl"/>
<cti:msgScope paths="modules.dr.estimatedLoad">
<tags:pickerDialog
    id="appCatFormulaPicker_${appCatAssignment.applianceCategory.applianceCategoryId}"
    type="appCatFormulaPicker"
    linkType="selection"
    selectionProperty="name"
    nameKey="noAssignment"
    icon="icon-plus-green"
    endAction="yukon.dr.formula.appCatFormulaPickerClose"
    initialId="${appCatAssignment.formulaId}"
    allowEmptySelection="true"/>
    <c:if test="${appCatAssignment.formulaId != null}">
        (<a href="formula/view?formulaId=${appCatAssignment.formulaId}"><cti:msg2 key="yukon.common.view"/></a>)
    </c:if>
</cti:msgScope>