<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="dr" page="formula.${mode}">

<style>
    .input-output-arrow {margin-top:.35em;}
    .output-column-container {position:relative;}
    .output-column {position:absolute;top:1.65em;left:-2em;}
    .output-column-remove-btn {position:absolute; top:1.65em; right:0em;}
    .input-select {width:150px}
</style>

    <tags:setFormEditMode mode="${mode}"/>

    <cti:includeScript link="/JavaScript/yukon.dr.formula.js"/>

    <cti:displayForPageEditModes modes="CREATE">
        <%@ include file="formulaCreate.jspf" %>
    </cti:displayForPageEditModes>

    <cti:displayForPageEditModes modes="EDIT,VIEW">
        <c:if test="${formulaBean.functionCalculation}">
            <%@ include file="formulaForFunction.jspf" %>
        </c:if>
        <c:if test="${formulaBean.lookupTableCalculation}">
            <%@ include file="formulaForTable.jspf" %>
        </c:if>
    </cti:displayForPageEditModes>

</cti:standardPage>
