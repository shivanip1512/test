<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>

<form:form>
    <tags:nameValueContainer>
        <cti:msg var="fieldName" key="yukon.web.modules.stars.dr.admin.applianceCategory.edit.categoryName"/>
        <tags:nameValue name="${fieldName}" nameColumnWidth="150px">
            <input type="text" size="30"/>
        </tags:nameValue>

        <cti:msg var="fieldName" key="yukon.web.modules.stars.dr.admin.applianceCategory.edit.displayName"/>
        <tags:nameValue name="${fieldName}">
            <input type="text" size="30"/>
            <input id="sameAsCategoryName" type="checkbox"/>
            <label for="sameAsCategoryName"><cti:msg key="yukon.web.modules.stars.dr.admin.applianceCategory.edit.sameAsCategoryName"/></label>
        </tags:nameValue>

        <cti:msg var="fieldName" key="yukon.web.modules.stars.dr.admin.applianceCategory.edit.type"/>
        <tags:nameValue name="${fieldName}">
            <select>
                <option value="0">Default</option>
                <option value="1">Air Conditioner</option>
                <option value="2">Chiller</option>
                <option value="3">Dual Fuel</option>
                <option value="4">Dual Storage</option>
                <option value="5">Generator</option>
                <option value="6">Grain Dryer</option>
                <option value="7">Heat Pump</option>
                <option value="8">Irrigation</option>
                <option value="9">Storage Heat</option>
                <option value="10">Water Heater</option>
            </select>
        </tags:nameValue>

        <cti:msg var="fieldName" key="yukon.web.modules.stars.dr.admin.applianceCategory.edit.icon"/>
        <tags:nameValue name="${fieldName}">
            <table cellpadding="0" cellspacing="0">
                <tr>
                   <td>
            <select>
                <c:forEach var="iconOption" items="${iconOptions}">
                    <option value="${iconOption}">
                        <cti:msg key="yukon.web.modules.stars.dr.admin.applianceCategory.edit.icon.${iconOption}"/>
                    </option>
                </c:forEach>
            </select>
                   </td>
                   <td rowspan="2">
                   <img src="<cti:url value="/WebConfig/yukon/Icons/Load.gif"/>"/>
                   </td>
                </tr>
                <tr>
                   <td>
            <input type="text" size="30"/>
                   </td>
                </tr>
            </table>
        </tags:nameValue>

        <cti:msg var="fieldName" key="yukon.web.modules.stars.dr.admin.applianceCategory.edit.description"/>
        <tags:nameValue name="${fieldName}">
            <textarea cols="40" rows="5"></textarea>
        </tags:nameValue>

        <c:set var="fieldName">
            <label for="customerSelectableCheckbox"><cti:msg key="yukon.web.modules.stars.dr.admin.applianceCategory.edit.customerSelectable"/></label>
        </c:set>
        <tags:nameValue name="${fieldName}">
            <input id="customerSelectableCheckbox" type="checkbox"/>
            <label for="customerSelectableCheckbox"><cti:msg key="yukon.web.modules.stars.dr.admin.applianceCategory.edit.customerSelectableDescription"/></label>
        </tags:nameValue>

    </tags:nameValueContainer>

    <div class="actionArea">
        <input type="button" value="<cti:msg key="yukon.web.modules.stars.dr.admin.applianceCategory.edit.ok"/>"/>
        <input type="button" value="<cti:msg key="yukon.web.modules.stars.dr.admin.applianceCategory.edit.cancel"/>"
            onclick="parent.$('acDialog').hide()"/>
    </div>

</form:form>
