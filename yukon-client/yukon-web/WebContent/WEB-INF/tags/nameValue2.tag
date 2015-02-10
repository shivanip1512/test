<%@ tag trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="argument" type="java.lang.Object" %>

<%@ attribute name="excludeColon" %>

<%@ attribute name="label" type="java.lang.Object" %>
<%@ attribute name="labelForId" %>

<%@ attribute name="nameKey" 
              description="The key to use for the label.
                           This key with .title appended will be used for the title of the key column.
                           NOTE:  This nameKey does NOT work like nameKey attributes of other tags." %>
                           
<%@ attribute name="nameClass" %>
<%@ attribute name="nameColumnWidth" %>

<%@ attribute name="rowClass" %>
<%@ attribute name="rowId" %>

<%@ attribute name="valueClass" %>

    <c:choose>
        <c:when test="${nameValueContainer2}">
            <c:set var="colonSuffix" value=":"/>
            <c:if test="${excludeColon == true}">
                <c:set var="colonSuffix" value=""/>
            </c:if>
            <tr <c:if test="${!empty rowId}"> id="${rowId}"</c:if>
                <c:if test="${!empty rowClass}"> class="${rowClass}"</c:if>>
                <td class="name <c:if test="${!empty nameClass}">${nameClass}</c:if>" 
                    style="<c:if test="${not empty pageScope.nameColumnWidth}">width:${pageScope.nameColumnWidth};</c:if>" 
                    title="<cti:msg2 key="${nameKey}.title" blankIfMissing="true"/>">
                    <c:choose>
                        <c:when test="${not empty pageScope.labelForId}">
                            <c:choose>
                                <c:when test="${not empty pageScope.argument}">
                                    <label for="${pageScope.labelForId}"><i:inline key="${label != null ? label : nameKey}" arguments="${argument}"/>${colonSuffix}</label>
                                </c:when>
                                <c:otherwise>
                                    <label for="${pageScope.labelForId}"><i:inline key="${label != null ? label : nameKey}"/>${colonSuffix}</label>
                                </c:otherwise>
                            </c:choose>
                        </c:when>
                        <c:otherwise>
                            <c:choose>
                                <c:when test="${not empty pageScope.argument}">
                                    <i:inline key="${label != null ? label : nameKey}" arguments="${argument}"/>${colonSuffix}
                                </c:when>
                                <c:otherwise>
                                    <i:inline key="${label != null ? label : nameKey}"/>${colonSuffix}
                                </c:otherwise>    
                            </c:choose>
                        </c:otherwise>
                    </c:choose>
                </td>
                
                <td class="value <c:if test="${!empty pageScope.valueClass}"> ${pageScope.valueClass}</c:if>"><jsp:doBody/></td>
            </tr>
        </c:when>
        <c:otherwise>
            <div class="error">ERROR: The &lt;nameValue2&gt; tag must be enclosed in a &lt;nameValueContainer2&gt; tag</div>
        </c:otherwise>
    </c:choose>
