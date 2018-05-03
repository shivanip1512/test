<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<cti:msgScope paths="yukon.web.modules.tools.bulk.disconnect">

    <tags:bulkActionContainer key="yukon.common.device.bulk.disconnect" deviceCollection="${deviceCollection}">
    
        <cti:url value="/bulk/disconnect/start" var="commandUrl">
            <cti:mapParam value="${deviceCollection.collectionParameters}"/>
        </cti:url>
        <form:form action="${commandUrl}" method="post" >
            <cti:csrfToken/>
            
            <tags:nameValueContainer2 tableClass="name-collapse">
                <tags:nameValue2 nameKey=".command">
                    <div class="button-group stacked">
                        <tags:radio name="command" value="CONNECT" key=".command.CONNECT" checked="true" buttonTextClass="green"/>
                        <tags:radio name="command" value="DISCONNECT" key=".command.DISCONNECT" buttonTextClass="red"/>
                        <c:if test="${displayArmLink}">
                            <tags:radio name="command" value="ARM" key=".command.ARM"/>
                        </c:if>
                    </div>
                </tags:nameValue2>
            </tags:nameValueContainer2>
        
            <div class="page-action-area">
                <cti:button classes="primary action js-action-submit" nameKey="start" busy="true"/>
            </div>
        
        </form:form>
    </tags:bulkActionContainer>
    
</cti:msgScope>