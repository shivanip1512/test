<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:url var="optOutUrl" value="/spring/stars/consumer/optout" />
<cti:url var="actionUrl" value="${optOutUrl}/optOutQuestions" />

<cti:standardPage module="consumer" page="optoutconfirm">
    <cti:standardMenu/>

    <h3><cti:msg key="yukon.dr.consumer.optoutconfirm.header"/></h3>

    <div align="center">
        <cti:msg key="yukon.dr.consumer.optoutconfirm.description"/>

        <br>
        <br>

        <form:form id="form" commandName="optOutBackingBean" method="POST"
             action="${actionUrl}">

            <form:hidden path="durationInDays" />
            <form:hidden path="startDate" />

            <tags:hiddenArray name="inventoryIds" items="${optOutBackingBean.inventoryIds}"/>

            <table id="questionTable">
                <c:forEach var="question" varStatus="status" items="${questions}">
                    <tr>
                        <td>
                            <div style="padding-bottom: 0.3em; text-align: left;">${question}</div>
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

            <div>
                <br>
                <span style="padding-right: 0.5em;">
                    <input type="submit" value="<cti:msg key='yukon.dr.consumer.optoutconfirm.save'/>"></input>
                </span>
                <input type="button" value="<cti:msg key='yukon.dr.consumer.optoutconfirm.cancel'/>"
                       onclick="javascript:location.href='${optOutUrl}';"></input>
            </div>
        </form:form>
    </div>

</cti:standardPage>
