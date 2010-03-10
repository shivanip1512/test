<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<%@ taglib tagdir="/WEB-INF/tags/i18n" prefix="i"  %>

<cti:standardPage module="operator" page="faq">
    <cti:standardMenu />

    <c:forEach var="subject" items="${questions}">
        <tags:sectionContainer title="${subject.key}">
            <c:forEach var="question" items="${subject.value}">
                <tags:hideReveal title="${question.key}">
                    <spring:escapeBody htmlEscape="true">${question.value}</spring:escapeBody>
                </tags:hideReveal>
            </c:forEach>
        </tags:sectionContainer>
        <br>
    </c:forEach>

</cti:standardPage>