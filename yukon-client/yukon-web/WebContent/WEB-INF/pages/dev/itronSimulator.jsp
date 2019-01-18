<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="dev" page="itronSimulator">
        
    <form:form action="saveSettings" method="post" modelAttribute="settings">
    <cti:csrfToken/>

        <div class="column-12-12">
            <div class="column one">
                <tags:sectionContainer title="Settings">
                    <tags:nameValueContainer>
                        <tags:nameValue name="AddHANDevice">
                            <tags:input path="addHANDeviceError"/>
                        </tags:nameValue>
                        <tags:nameValue name="EditHANDevice">
                            <form:select path="editHANDeviceError">
                            <option label="SUCCESS"></option>
                                  <c:forEach var="errorType" items="${editHANDeviceErrorTypes}">
                                    <form:option value="${errorType}"></form:option>
                                  </c:forEach>
                            </form:select>
                        </tags:nameValue>
                        <tags:nameValue name="AddServicePoint">
                            <form:select path="addServicePointError">
                            <option label="SUCCESS"></option>
                                  <c:forEach var="errorType" items="${basicErrorTypes}">
                                    <form:option value="${errorType}"></form:option>
                                  </c:forEach>
                            </form:select>
                        </tags:nameValue>
                        <tags:nameValue name="EditServicePoint">
                            <form:select path="editServicePointError">
                            <option label="SUCCESS"></option>
                                  <c:forEach var="errorType" items="${basicErrorTypes}">
                                    <form:option value="${errorType}"></form:option>
                                  </c:forEach>
                            </form:select>
                        </tags:nameValue>
                        <tags:nameValue name="AddESIGroup">
                            <form:select path="addESIGroupError">
                            <option label="SUCCESS"></option>
                                  <c:forEach var="errorType" items="${esiGroupErrorTypes}">
                                    <form:option value="${errorType}"></form:option>
                                  </c:forEach>
                            </form:select>
                        </tags:nameValue>
                        <tags:nameValue name="EditESIGroup">
                            <form:select path="editESIGroupError">
                            <option label="SUCCESS"></option>
                                  <c:forEach var="errorType" items="${esiGroupErrorTypes}">
                                    <form:option value="${errorType}"></form:option>
                                  </c:forEach>
                            </form:select>
                        </tags:nameValue>
                        <tags:nameValue name="AddProgram">
                            <form:select path="addProgramError">
                            <option label="SUCCESS"></option>
                                  <c:forEach var="errorType" items="${addProgramErrorTypes}">
                                    <form:option value="${errorType}"></form:option>
                                  </c:forEach>
                            </form:select>
                        </tags:nameValue>
                        <tags:nameValue name="SetServicePointEnrollment">
                            <form:select path="servicePointEnrollmentSetError">
                            <option label="SUCCESS"></option>
                                  <c:forEach var="errorType" items="${basicErrorTypes}">
                                    <form:option value="${errorType}"></form:option>
                                  </c:forEach>
                            </form:select>
                        </tags:nameValue>
                        <tags:nameValue name="GetServicePointEnrollment">
                            <form:select path="servicePointEnrollmentGetError">
                            <option label="SUCCESS"></option>
                                  <c:forEach var="errorType" items="${basicErrorTypes}">
                                    <form:option value="${errorType}"></form:option>
                                  </c:forEach>
                            </form:select>
                        </tags:nameValue>
                        <tags:nameValue name="AddHANLoadControlProgramEvent">
                            <form:select path="addHANLoadControlProgramEventError">
                            <option label="SUCCESS"></option>
                                  <c:forEach var="errorType" items="${basicErrorTypes}">
                                    <form:option value="${errorType}"></form:option>
                                  </c:forEach>
                            </form:select>
                        </tags:nameValue>
                    </tags:nameValueContainer>
                </tags:sectionContainer>
            </div>
        </div>
    
    
    
        <div class="page-action-area">
            <cti:button label="Save Settings" type="submit" busy="true"/>
            <c:choose>
                <c:when test="${simulatorRunning}">
                    <cti:button label="Stop" type="button" href="stop" busy="true"/>
                </c:when>
                <c:otherwise>
                    <cti:button label="Start" type="button" href="start" busy="true"/>
                </c:otherwise>
            </c:choose>
            <cti:button label="Test" type="button" href="test" busy="true"/>
        </div>
        
    </form:form>

</cti:standardPage>