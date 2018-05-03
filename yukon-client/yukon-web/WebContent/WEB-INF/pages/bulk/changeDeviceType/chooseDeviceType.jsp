<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="yukon.web.modules.tools.bulk.changeDeviceTypeChoose">

    <tags:bulkActionContainer key="yukon.common.device.bulk.changeDeviceTypeChoose" deviceCollection="${deviceCollection}">

        <cti:url var="changeTypeUrl" value="/bulk/changeDeviceType/changeDeviceType" />
        <form method="post" action="${changeTypeUrl}">
            <cti:csrfToken />
            <%-- DEVICE COLLECTION --%>
            <cti:deviceCollection deviceCollection="${deviceCollection}" />

            <%-- AVAILABLE TYPES --%>
            <c:choose>
                <c:when test="${!empty deviceTypes}">
                    <c:set var="disabled" value="false" />
                    <tags:nameValueContainer2 tableClass="name-collapse">
                        <tags:nameValue2 nameKey=".deviceType">
                            <select name="deviceTypes">
                                <c:forEach var="deviceType" items="${deviceTypes}">
                                    <option value="${deviceType.value}">${deviceType.key}</option>
                                </c:forEach>
                            </select>
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                </c:when>
                <c:otherwise>
                    <c:set var="disabled" value="true" />
                    <span class="user-message error"><cti:msg2 key=".noDeviceTypes" /></span>
                </c:otherwise>
            </c:choose>
            <div class="page-action-area">
                <cti:button nameKey="change" classes="js-action-submit primary action" disabled="${disabled}" />
            </div>
        </form>

    </tags:bulkActionContainer>

</cti:msgScope>