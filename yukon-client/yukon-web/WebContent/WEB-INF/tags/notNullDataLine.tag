<%@ tag trimDirectiveWhitespaces="true" body-content="empty"%>

<%@ attribute name="value" required="true" %>
<%@ attribute name="inLine" description="If true no line break will be added." type="java.lang.Boolean" %>
<%@ attribute name="ignore" description="Treat this value as null" type="java.lang.String" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<c:if test='${not empty value && value != pageScope.ignore}'>${fn:escapeXml(value)}<c:if test="${empty pageScope.inLine || not pageScope.inLine}"><br></c:if></c:if>