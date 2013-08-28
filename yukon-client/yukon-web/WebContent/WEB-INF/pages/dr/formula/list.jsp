<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="dr" page="formulaList">
    <cti:includeScript link="/JavaScript/drFormula.js"/>
<%--     <tags:sectionContainer2 nameKey="weatherInputs"> --%>
<%--         Name: <input type='text'/><br> --%>
<%--         Lat: <input type='text'/><br> --%>
<%--         Lon: <input type='text'/><br> --%>
<!--         <div class="actionArea"> -->
<%--            <cti:button icon="icon-plus-green" nameKey="newFormula" href="create"/> --%>
<!--         </div> -->
<%--     </tags:sectionContainer2> --%>
    <tags:sectionContainer2 nameKey="formulas">
         <div class="f-replaceViaAjax">
            <%@ include file="_formulasTable.jsp" %>
         </div>
    </tags:sectionContainer2>
        <div class="actionArea">
           <cti:button icon="icon-plus-green" nameKey="newFormula" href="create"/>
        </div>
</cti:standardPage>