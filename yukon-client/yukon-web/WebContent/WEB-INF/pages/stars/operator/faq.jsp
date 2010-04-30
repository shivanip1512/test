<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<%@ taglib tagdir="/WEB-INF/tags/i18n" prefix="i"  %>

<cti:standardPage module="operator" page="faq">

    <c:forEach var="subject" items="${questions}">
        <tags:sectionContainer title="${subject.key}" escapeTitle="true">
            <c:forEach var="question" items="${subject.value}">
                <tags:hideReveal title="${question.key}" escapeTitle="true">
                    ${question.value}
                </tags:hideReveal>
            </c:forEach>
        </tags:sectionContainer>
        <br>
    </c:forEach>

</cti:standardPage>