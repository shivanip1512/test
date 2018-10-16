<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="operator" page="optOut.confirm">

<form:form modelAttribute="optOutBackingBean" method="POST"
    action="/stars/operator/program/optOut/optOutQuestions">
    <cti:csrfToken/>
    <form:hidden path="durationInDays" />
    <form:hidden path="startDate" />

    <input type="hidden" name="accountId" value="${accountId}"/>
    <tags:hiddenArray name="inventoryIds" items="${optOutBackingBean.inventoryIds}"/>

    <tags:sectionContainer2 nameKey="optOuts">

        <table id="questionTable">
            <c:forEach var="question" varStatus="status" items="${questions}">
                <tr>
                    <td>
                        <div class="optOutQuestion">${question}</div>
                        <input type="hidden"
                            name="legacyQuestions[${status.index}].index"
                            value="${status.count}"/>
                        <input type="hidden"
                            name="legacyQuestions[${status.index}].question"
                            value="${question}"/>
                        <tags:input path="legacyQuestions[${status.index}].answer" size="80"/>
                    </td>
                </tr>
            </c:forEach>
        </table>

     <div class="page-action-area">
         <cti:button nameKey="save" type="submit" classes="js-blocker"/>
         <cti:button type="submit" name="cancel" nameKey="cancel"/>
     </div>

    </tags:sectionContainer2>
</form:form>

</cti:standardPage>
