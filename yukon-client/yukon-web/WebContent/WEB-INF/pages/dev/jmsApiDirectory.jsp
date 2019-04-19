<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="dev" page="jmsApis">
    <tags:sectionContainer title="Filters">
        <form:form id="filterForm" action="jmsApiDirectory" modelAttribute="apiFilters">
            <cti:csrfToken/>
            <tags:nameValueContainer>
                <tags:nameValue name="Category">
                    <form:select path="category">
                        <form:option value="" label="Any"/>
                        <c:forEach var="category" items="${categories}">
                            <form:option value="${category}" label="${category.toString()}"/>
                        </c:forEach>
                    </form:select>
                </tags:nameValue>
                <tags:nameValue name="Communication Pattern">
                    <form:select path="communicationPattern">
                        <form:option value="" label="Any"/>
                        <c:forEach var="commPattern" items="${commPatterns}">
                            <form:option value="${commPattern}" label="${commPattern.toString()}"/>
                        </c:forEach>
                    </form:select>
                </tags:nameValue>
                <tags:nameValue name="Sender">
                    <form:select path="sender">
                        <form:option value="" label="Any"/>
                        <c:forEach var="service" items="${services}">
                            <form:option value="${service}" label="${service.toString()}"/>
                        </c:forEach>
                    </form:select>
                </tags:nameValue>
                <tags:nameValue name="Receiver">
                    <form:select path="receiver">
                        <form:option value="" label="Any"/>
                        <c:forEach var="service" items="${services}">
                            <form:option value="${service}" label="${service.toString()}"/>
                        </c:forEach>
                    </form:select>
                </tags:nameValue>
                <tags:nameValue name="Search in Queues">
                    <form:input path="queueSearch"/>
                </tags:nameValue>
            </tags:nameValueContainer>
            <div class="page-action-area stacked">
                <cti:button label="Filter" name="filter" type="submit" classes="primary action" busy="true"/>
            </div>
        </form:form>
    </tags:sectionContainer>
    
    <c:forEach var="categoryEntry" items="${jmsApis.entrySet()}">
        <c:if test="${categoryEntry.value.size() gt 0}">
        <tags:sectionContainer title="${categoryEntry.key.toString()}">
            <c:forEach var="api" items="${categoryEntry.value}">
                <tags:boxContainer title="${api.name}">
                    <tags:nameValueContainer>
                        <div class="stacked-md">
                            ${api.description}
                        </div>
                        <tags:nameValue name="Communication Pattern">
                            ${api.pattern.toString()}
                        </tags:nameValue>
                        <tags:nameValue name="Senders">
                            ${api.sendersString}
                        </tags:nameValue>
                        <tags:nameValue name="Receivers">
                            ${api.receiversString}
                        </tags:nameValue>
                        <tags:nameValue name="Request Queue">
                            ${api.queue.name}
                        </tags:nameValue>
                        <c:if test="${api.ackQueue.present}">
                            <tags:nameValue name="Ack Queue">
                                ${api.ackQueue.get().name}
                            </tags:nameValue>
                        </c:if>
                        <c:if test="${api.responseQueue.present}">
                            <tags:nameValue name="Response Queue">
                                ${api.responseQueue.get().name}
                            </tags:nameValue>
                        </c:if>
                        <tags:nameValue name="Request Message">
                            ${api.requestMessage.simpleName}
                        </tags:nameValue>
                        <c:if test="${api.ackMessage.present}">
                            <tags:nameValue name="Ack Message">
                                ${api.ackMessage.get().simpleName}
                            </tags:nameValue>
                        </c:if>
                        <c:if test="${api.responseMessage.present}">
                            <tags:nameValue name="Response Message">
                                ${api.responseMessage.get().simpleName}
                            </tags:nameValue>
                        </c:if>
                    </tags:nameValueContainer>
                </tags:boxContainer>
            </c:forEach>
        </tags:sectionContainer>
        </c:if>
    </c:forEach>
    
</cti:standardPage>