<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:url var="routeUrl"
    value="/spring/multispeak/setup/routemapping/route" />
<c:url var="subUrl"
    value="/spring/multispeak/setup/routemapping/substation" />

<h2>
    Substation Name
</h2>

<form name="subform" action="${subUrl}" method="post">
    <div>
        <select id="subSelectList" name="selection_list" size="10"
            style="width:185px"
            onclick='javascript:SubstationToRouteMappings_SelectListener("${routeUrl}")'>
            <c:forEach var="substation" items="${list}">
                <option value="${substation.id}">
                    ${substation.name}
                </option>
            </c:forEach>
        </select>
        <input type="submit" value="Delete" size="5" name="remove" />
    </div>

    <div style="padding-top: 10px">
        <input type="text" value="" name="name" size="20" />
        <input type="submit" value="Add" size="5" name="add" />
        <input type="button" value="MSP" size="5" />
    </div>
</form>


