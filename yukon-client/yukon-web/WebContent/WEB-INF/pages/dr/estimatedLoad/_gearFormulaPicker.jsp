<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<cti:url value="gearAssignmentsPage" var="baseUrl"/>
<cti:msgScope paths="modules.dr.estimatedLoad">
<tags:pickerDialog
    id="gearFormulaPicker_${gearAssignment.gear.yukonID}"
    type="gearFormulaPicker"
    linkType="selection"
    selectionProperty="name"
    nameKey="noAssignment"
    icon="icon-plus-green"
    endAction="yukon.dr.formula.gearFormulaPickerClose"
    initialId="${gearAssignment.formulaId}"
    allowEmptySelection="true"/>
    <c:if test="${gearAssignment.formulaId != null}">
        (<a href="formula/view?formulaId=${gearAssignment.formulaId}"><cti:msg2 key="yukon.common.view"/></a>)
    </c:if>
</cti:msgScope>