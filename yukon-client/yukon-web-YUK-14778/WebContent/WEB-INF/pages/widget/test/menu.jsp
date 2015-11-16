<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<cti:standardPage module="blank" title="Widget Test">

    <h2>Please select a widget</h2>
    
    <c:forEach items="${widgets}" var="widget">
        <form action="create" method="get">
            <tags:boxContainer title="${widget.title}">
                <table>
                    <c:forEach items="${widget.inputs}" var="input">
                        <tr>
                            <td>${input.description}</td>
                            <td><input type="text" name="${input.name}"></td>
                        </tr>
                    </c:forEach>
                </table>
                <input type="hidden" name="type" value="${widget.shortName}">
                <cti:button type="submit" name="create" label="Show Widget"/>
            </tags:boxContainer>
        </form>
    </c:forEach>

</cti:standardPage>