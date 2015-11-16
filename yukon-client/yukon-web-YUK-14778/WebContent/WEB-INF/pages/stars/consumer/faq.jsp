<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

<cti:standardPage module="consumer" page="faq">
    <cti:standardMenu />
    
    
    <h3><cti:msg key="yukon.dr.consumer.faq.header" /></h3>
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
                
</cti:standardPage>    