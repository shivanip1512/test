<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>

<%@ attribute name="enabled" required="false" type="java.lang.Boolean"%>
<%@ attribute name="href" required="false" type="java.lang.String"%>
<%@ attribute name="textKey" required="false" type="java.lang.String"%>
<%@ attribute name="disabledMessageKey" required="false" type="java.lang.String"%>

<div class="linkBorder">
    <c:choose>
        <c:when test="${enabled == null}">
            <div class="boxedLink">
                <jsp:doBody />
            </div>
        </c:when>
        <c:otherwise>
            <c:choose>
                <c:when test="${enabled}">
                    <div class="boxedLink">
                        <a href="${href}"><cti:msg key="${textKey}" /></a>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="boxedLink disabled">
                        <a class="disabled" title="<cti:msg key="${disabledMessageKey}"/>"><cti:msg key="${textKey}" /></a>
                    </div>
                </c:otherwise>
            </c:choose>
        </c:otherwise>
    </c:choose>
</div>
