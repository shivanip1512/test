<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

<cti:standardPage module="consumer" title="Consumer Energy Services">
    <cti:standardMenu />
    
    
    <table class="contentTable">
        <tr>
            <td class="leftColumn">
                <h3><cti:msg key="yukon.dr.consumer.faq.header" /></h3>
                <div style="border: 1px solid #ccc;"></div>
                <br>
                <c:set var="index" value="0"/>
                <c:forEach var="subject" items="${questions}">
                    <b>${subject.key}</b>
                    <br>
                    <ul>
                        <c:forEach var="question" items="${subject.value}">
                            <c:set var="index" value="${index + 1}"/>
                            <c:set var="link" value="#faq${index}"/>
                            <li><a href="${link}">${question.key}</a></li>
                        </c:forEach>
                    </ul>
                </c:forEach>
                
                <br>
                <br>
                <div style="border: 1px solid #ccc;"></div>
                
                <c:set var="index" value="0"/>
                <c:forEach var="subject" items="${questions}">
                    <br>
                    <b>${subject.key}</b>
                    <br>
                    <br>
                    
                    <c:forEach var="question" items="${subject.value}">
                        <c:set var="index" value="${index + 1}"/>
                        <c:set var="name" value="faq${index}"/>
                        
                        <a name="${name}"></a>
                        <b><i>${question.key}</i></b>
                        <br>
                        <br>
                        ${question.value}
                        <br>
                        <a href="#" name="top"><cti:msg key="yukon.dr.consumer.faq.backtotop"/></a>
                        <br>
                        <br>
                    </c:forEach>
                
                </c:forEach>
                
            </td>
            
            <td class="rightColumn">
                <cti:customerAccountInfoTag accountNumber="${customerAccount.accountNumber}" />
                <br>
                <img src="<cti:theme key="yukon.dr.consumer.general.rightColumnLogo" default="/WebConfig/yukon/Family.jpg" url="true"/>"></img>
            </td>
        </tr>
    </table>
    
</cti:standardPage>    