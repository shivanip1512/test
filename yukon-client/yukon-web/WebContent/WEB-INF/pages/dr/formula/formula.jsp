<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="dialog" tagdir="/WEB-INF/tags/dialog"%>

<cti:standardPage module="dr" page="formula.${mode}">

    <tags:setFormEditMode mode="${mode}"/>

    <cti:includeScript link="/JavaScript/drFormula.js"/>

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
