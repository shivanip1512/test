<%@ tag body-content="empty" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="path" description="A base for the spring binding path." %>
<cti:default var="path" value=""/>

<tags:sectionContainer2 nameKey="slotAddresses">
    <table>
        <c:forEach var="slotNum" begin="0" end="5">
            <tr>
                <td><tags:input path="${path}addressingInfo.slots[${slotNum}]" size="15" maxlength="15"/></td>
            </tr>
        </c:forEach>
    </table>
</tags:sectionContainer2>
