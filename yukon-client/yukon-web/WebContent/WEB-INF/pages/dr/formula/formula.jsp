<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="dialog" tagdir="/WEB-INF/tags/dialog"%>

<cti:standardPage module="dr" page="formula.${mode}">
    <style>
        h4 {margin: .4em 0;}
        .functions tr .f-show-on-hover {visibility: hidden;}
        .functions tr:hover .f-show-on-hover {visibility: visible;}
        .f-hover-target .f-show-on-hover {visibility: hidden;}
        .f-hover-target:hover .f-show-on-hover {visibility: visible;}
    </style>

    <tags:setFormEditMode mode="${mode}"/>

    <cti:includeScript link="/JavaScript/drFormula.js"/>

    <cti:displayForPageEditModes modes="CREATE">
        <%@ include file="formulaCreate.jspf" %>
    </cti:displayForPageEditModes>

    <cti:displayForPageEditModes modes="EDIT,VIEW">
        <c:if test="${formulaBean.calculationType eq 'FUNCTION'}">
            <%@ include file="formulaForFunction.jspf" %>
        </c:if>
        <c:if test="${formulaBean.calculationType eq 'LOOKUP'}">
            <%@ include file="formulaForTable.jspf" %>
        </c:if>
    </cti:displayForPageEditModes>

</cti:standardPage>
