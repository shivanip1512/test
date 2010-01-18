<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>


<%-- give "dialogQuestion" style a more generic name --%>
<h1 class="dialogQuestion"><cti:msg key="yukon.web.modules.stars.dr.admin.applianceCategory.editAssignedPrograms.title"
    argument="WH LITE-S"/></h1>

<form:form>
    <tags:nameValueContainer>
        <cti:msg var="fieldName" key="yukon.web.modules.stars.dr.admin.applianceCategory.editAssignedPrograms.displayName"/>
        <tags:nameValue name="${fieldName}" nameColumnWidth="150px">
            <input type="text" size="30"/>
            <input id="sameAsProgramName" type="checkbox"/>
            <label for="sameAsProgramName"><cti:msg key="yukon.web.modules.stars.dr.admin.applianceCategory.editAssignedPrograms.sameAsProgramName"/></label>
        </tags:nameValue>

        <cti:msg var="fieldName" key="yukon.web.modules.stars.dr.admin.applianceCategory.editAssignedPrograms.shortName"/>
        <tags:nameValue name="${fieldName}">
            <input type="text" size="30"/>
            <input id="sameAsDisplayName" type="checkbox"/>
            <label for="sameAsDisplayName"><cti:msg key="yukon.web.modules.stars.dr.admin.applianceCategory.editAssignedPrograms.sameAsDisplayName"/></label>
        </tags:nameValue>

        <cti:msg var="fieldName" key="yukon.web.modules.stars.dr.admin.applianceCategory.editAssignedPrograms.description"/>
        <tags:nameValue name="${fieldName}">
            <textarea cols="40" rows="5"></textarea>
        </tags:nameValue>

        <cti:msg var="fieldName" key="yukon.web.modules.stars.dr.admin.applianceCategory.editAssignedPrograms.chanceOfControl"/>
        <tags:nameValue name="${fieldName}">
            <select>
                <option value="0">Not Specified</option>
                <option value="1">Unlikely</option>
                <option value="2">Likely</option>
            </select>
        </tags:nameValue>

        <cti:msg var="fieldName" key="yukon.web.modules.stars.dr.admin.applianceCategory.editAssignedPrograms.programDescriptionIcons"/>
        <tags:nameValue name="${fieldName}" isSection="true">
            <cti:msg var="nestedFieldName" key="yukon.web.modules.stars.dr.admin.applianceCategory.editAssignedPrograms.savings"/>
            <tags:nameValue name="${nestedFieldName}">
                <table cellpadding="0" cellspacing="0">
                    <tr>
                       <td>
                           <select>
                               <option>No Image</option>
                               <option>$</option>
                               <option>$$</option>
                               <option>$$$</option>
                               <option>Other Image</option>
                           </select>
                       </td>
                       <td align="right">
                           <img src="<cti:url value="/WebConfig/yukon/Icons/$$Sm.gif"/>"/>
                       </td>
                    </tr>
                    <tr>
                       <td colspan="2">
                           <input type="text" size="50"/>
                       </td>
                    </tr>
                </table>
            </tags:nameValue>
            <cti:msg var="nestedFieldName" key="yukon.web.modules.stars.dr.admin.applianceCategory.editAssignedPrograms.controlPercent"/>
            <tags:nameValue name="${nestedFieldName}">
                <table cellpadding="0" cellspacing="0">
                    <tr>
                       <td>
                           <select>
                               <option>No Image</option>
                               <option>Sixth</option>
                               <option>Quarter</option>
                               <option>Third</option>
                               <option>Half</option>
                               <option>Other Image</option>
                           </select>
                       </td>
                       <td align="right">
                           <img src="<cti:url value="/WebConfig/yukon/Icons/QuarterSm.gif"/>"/>
                       </td>
                    </tr>
                    <tr>
                       <td colspan="2">
                           <input type="text" size="50"/>
                       </td>
                    </tr>
                </table>
            </tags:nameValue>
            <cti:msg var="nestedFieldName" key="yukon.web.modules.stars.dr.admin.applianceCategory.editAssignedPrograms.environment"/>
            <tags:nameValue name="${nestedFieldName}">
                <table cellpadding="0" cellspacing="0">
                    <tr>
                       <td>
                           <select>
                               <option>No Image</option>
                               <option>1 Tree</option>
                               <option>2 Trees</option>
                               <option>3 Trees</option>
                               <option>Other Image</option>
                           </select>
                       </td>
                       <td align="right">
                           <img src="<cti:url value="/WebConfig/yukon/Icons/Tree2Sm.gif"/>"/>
                       </td>
                    </tr>
                    <tr>
                       <td colspan="2">
                           <input type="text" size="50"/>
                       </td>
                    </tr>
                </table>
            </tags:nameValue>
        </tags:nameValue>

    </tags:nameValueContainer>

    <div class="actionArea">
        <input type="button" value="<cti:msg key="yukon.web.modules.stars.dr.admin.applianceCategory.editAssignedPrograms.ok"/>"/>
        <input type="button" value="<cti:msg key="yukon.web.modules.stars.dr.admin.applianceCategory.editAssignedPrograms.cancel"/>"
            onclick="parent.$('acDialog').hide()"/>
    </div>

</form:form>
