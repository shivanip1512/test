<%@ tag body-content="empty"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr"%>

<tags:sectionContainer2 nameKey="slotAddresses">
    <table>
        <c:forEach var="slotNum" begin="0" end="5">
            <tr>
                <td><tags:input path="addressingInfo.slots[${slotNum}]" size="15" maxlength="15"/></td>
            </tr>
        </c:forEach>
    </table>
</tags:sectionContainer2>
