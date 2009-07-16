<%@ taglib tagdir="/WEB-INF/tags" prefix="ct" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:choose>
    <c:when test="${verifyResult != null}">
        <ct:nameValueContainer>
            <ct:nameValue name="Verify Result">
                <c:choose>
                    <c:when test="${verifyResult.synced}">
                        <span style="font-weight:bold;color:#006633;"><c:out value="In Sync"/></span>
                    </c:when>
                    <c:otherwise>
                        <span style="font-weight:bold;color:#AA0033;"><c:out value="${verifyResult.discrepancies}"/></span>
                    </c:otherwise>
                </c:choose>
            </ct:nameValue>
        </ct:nameValueContainer>
    </c:when>
    <c:when test="${pushResult != null}">
        <ct:nameValueContainer>
            <ct:nameValue name="Push Result">
                <c:choose>
                    <c:when test="${!pushResult.errorsExist}">
                        <span style="font-weight:bold;color:#006633;"><c:out value="Push Sent Successfully"/></span>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="error" items="${pushResult.errors}">
                            <span style="font-weight:bold;color:#AA0033;"><c:out value="${error.description}"/></span>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </ct:nameValue>
        </ct:nameValueContainer>
    </c:when>
    <c:when test="${readResult != null}">
        <ct:nameValueContainer>
            <ct:nameValue name="Read Result">
                <c:choose>
                    <c:when test="${!readResult.errorsExist}">
                        <span style="font-weight:bold;color:#006633;"><c:out value="Read Sent Successfully"/></span>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="error" items="${pushResult.errors}">
                            <span style="font-weight:bold;color:#AA0033;"><c:out value="${error.description}"/></span>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </ct:nameValue>
        </ct:nameValueContainer>
    </c:when>
</c:choose>