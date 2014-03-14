<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:msgScope paths="modules.dev.uiDemos">
<h1>Icons and their associated CSS classes</h1>
<tags:sectionContainer title="16px icons">
    <c:forEach items="${sprites16Array}" var="spriteProperty" varStatus="status">
        <c:choose>
            <c:when test="${status.index % 4 == 0}">
                <div class="column-6-6-6-6 clearfix">
                <c:set var="classes" value="one"/>
            </c:when>
            <c:when test="${status.index % 4 == 1}">
                <c:set var="classes" value="two"/>
            </c:when>
            <c:when test="${status.index % 4 == 2}">
                <c:set var="classes" value="three"/>
            </c:when>
            <c:when test="${status.index % 4 == 3}">
                <c:set var="classes" value="four nogutter"/>
            </c:when>
        </c:choose>
        
            <div class="column ${classes}">
                <div class="well">                
                    <div class="clearfix" style="text-align:center;">
                        <cti:icon icon="${spriteProperty}" classes="fn"/>
                    </div>
                    <div style="text-align:center">
                        <span ><c:out value="${spriteProperty}" /></span>
                    </div>
                </div>
            </div>
            
        <c:if test="${status.index % 4 == 3 or status.last}"></div></c:if>
    </c:forEach>
</tags:sectionContainer>

<tags:sectionContainer title="32px icons">
    <c:forEach items="${sprites32Array}" var="sprite32Property" varStatus="status">
        <c:choose>
            <c:when test="${status.index % 4 == 0}">
                <div class="column-6-6-6-6 clearfix">
                <c:set var="classes" value="one"/>
            </c:when>
            <c:when test="${status.index % 4 == 1}">
                <c:set var="classes" value="two"/>
            </c:when>
            <c:when test="${status.index % 4 == 2}">
                <c:set var="classes" value="three"/>
            </c:when>
            <c:when test="${status.index % 4 == 3}">
                <c:set var="classes" value="four nogutter"/>
            </c:when>
        </c:choose>
        
            <div class="column ${classes}">
                <div class="well">                
                    <div class="clearfix" style="text-align:center;">
                        <cti:icon icon="${sprite32Property}" classes="icon-32 fn"/>
                    </div>
                    <div style="text-align:center">
                        <span ><c:out value="${sprite32Property}" /></span>
                    </div>
                </div>
            </div>
            
        <c:if test="${status.index % 4 == 3 or status.last}"></div></c:if>
    </c:forEach>
</tags:sectionContainer>
</cti:msgScope>