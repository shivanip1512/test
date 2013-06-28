<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>

<cti:standardPage page="webServices.loadControl" module="support">

    <script>
        
        function toggleObserveConstraintsAndExecute(forceEl, observeAndExecuteId) {
            if (forceEl.checked) {
                $(observeAndExecuteId).checked = false;
                $(observeAndExecuteId).disabled = true;
            } else {
                $(observeAndExecuteId).disabled = false;
            }
        }
    </script>

    <%-- RESULT AREA --%>
    <c:if test="${not empty results}">
        <ul>
        <c:forEach items="${results}" var="result">
            <li><div class="successMessage">${result}</div></li>
        </c:forEach>
        </ul>
    <br><br>
    </c:if>
    
    <table class="resultsTable">
    
        <tr>
            <th class="functionality"><i:inline key=".functionality"/></th>
            <th><i:inline key=".inputTest"/></th>
        </tr>
        
        <%-- PROGRAM STATUS BY PROGRAM NAME --%>
        <tr>
            <td class="functionality"><i:inline key=".programStatusByName"/></td>
            <td class="inputTest">
                <form action="/debug/loadControlService/inputs/getProgramStatusByProgramName" method="post">
                    <tags:nameValueContainer2>
                        <tags:nameValue2 nameKey=".programName" >
                            <input type="text" name="programName" value="${programName}">
                        </tags:nameValue2>
                        
                        <tags:nameValue2 nameKey=".run">
                            <cti:button nameKey="testBtn" type="submit" classes="f-blocker"/>
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                </form>
            </td>
        </tr>
        
        
        <%-- CURRENTLY ACTIVE PROGRAMS --%>
        <tr>
            <td class="functionality"><i:inline key=".programStatusAllActive"/></td>
            <td class="inputTest">
                <form action="/debug/loadControlService/inputs/getAllCurrentlyActivePrograms" method="post">
                    <tags:nameValueContainer2>
                        <tags:nameValue2 nameKey=".run" >
                            <cti:button nameKey="testBtn" type="submit" classes="f-blocker"/>
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                </form>
            </td>
        </tr>
        
        
        <%-- START CONTROL BY SCENARIO NAME.--%>
        <tr>
            <td class="functionality"><i:inline key=".startByScenarioName"/></td>
            <td class="inputTest">
                <form action="/debug/loadControlService/inputs/startControlByScenarioName" method="post">
                    <tags:nameValueContainer2>
                        <tags:nameValue2 nameKey=".scenarioName" >
                            <input type="text" name="scenarioName" value="${scenarioName}">
                        </tags:nameValue2>
                        
                        <tags:nameValue2 nameKey=".startTime">
							<dt:date name="startDate" value="${startDate}" />
                            <input type="text" name="startTime" value="${startTime}"><i:inline key=".hhMM"/>
                        </tags:nameValue2>
                        
                        <tags:nameValue2 nameKey=".stopTime">
                            <dt:date name="stopDate" value="${stopDate}"/>
                            <input type="text" name="stopTime" value="${stopTime}"><i:inline key=".hhMM"/>
                        </tags:nameValue2>

                        <tags:nameValue2 nameKey=".forceExecute">
                            <input type="checkbox" name="force" onclick="toggleObserveConstraintsAndExecute(this, 'observeConstraintsAndExecute24');">
                        </tags:nameValue2>
                        
                        <tags:nameValue2 nameKey=".observeAndExecute">
                            <input type="checkbox" id="observeConstraintsAndExecute24" name="observeConstraintsAndExecute" checked>
                        </tags:nameValue2>
                        
                        <tags:nameValue2 nameKey=".run">
                            <cti:button nameKey="testBtn" type="submit" classes="f-blocker"/>
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                </form>
            </td>
        </tr>
        
        <%-- START CONTROL BY PROGRAM NAME. --%>
        <tr>
            <td class="functionality"><i:inline key=".startByProgramName"/></td>
            <td class="inputTest">
                <form action="/debug/loadControlService/inputs/startControlByProgramName" method="post">
                    <tags:nameValueContainer2>
                        <tags:nameValue2 nameKey=".programName" >
                            <input type="text" name="programName" value="${programName}">
                        </tags:nameValue2>
                        
                        <tags:nameValue2 nameKey=".startTime">
                            <dt:date name="startDate" value="${startDate}"/>
                            <input type="text" name="startTime" value="${startTime}"><i:inline key=".hhMM"/>
                        </tags:nameValue2>
                        
                        <tags:nameValue2 nameKey=".stopTime">
                            <dt:date name="stopDate" value="${stopDate}"/>
                            <input type="text" name="stopTime" value="${stopTime}"><i:inline key=".hhMM"/>
                        </tags:nameValue2>
                        
                        <tags:nameValue2 nameKey=".gearName">
                            <input type="text" name="gearName" value="${gearName}" size="2">
                        </tags:nameValue2>
                        
                        <tags:nameValue2 nameKey=".forceExecute">
                            <input type="checkbox" name="force" onclick="toggleObserveConstraintsAndExecute(this, 'observeConstraintsAndExecute25');">
                        </tags:nameValue2>
                        
                        <tags:nameValue2 nameKey=".observeAndExecute">
                            <input type="checkbox" id="observeConstraintsAndExecute25" name="observeConstraintsAndExecute" checked>
                        </tags:nameValue2>
                        
                        <tags:nameValue2 nameKey=".run">
                            <cti:button nameKey="testBtn" type="submit" classes="f-blocker"/>
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                </form>
            </td>
        </tr>
        
        <%-- STOP CONTROL BY SCENARIO NAME.--%>
        <tr>
            <td class="functionality"><i:inline key=".stopByScenarioName"/></td>
            <td class="inputTest">
                <form action="/debug/loadControlService/inputs/stopControlByScenarioName" method="post">
                    <tags:nameValueContainer2>
                        <tags:nameValue2 nameKey=".scenarioName" >
                            <input type="text" name="scenarioName" value="${scenarioName}">
                        </tags:nameValue2>
                        
                        <tags:nameValue2 nameKey=".stopTime">
                            <dt:date name="stopDate" value="${stopDate}"/>
                            <input type="text" name="stopTime" value="${stopTime}"><i:inline key=".hhMM"/>
                        </tags:nameValue2>
                        
                        <tags:nameValue2 nameKey=".forceExecute">
                            <input type="checkbox" name="force" onclick="toggleObserveConstraintsAndExecute(this, 'observeConstraintsAndExecute26');">
                        </tags:nameValue2>
                        
                        <tags:nameValue2 nameKey=".observeAndExecute">
                            <input type="checkbox" id="observeConstraintsAndExecute26" name="observeConstraintsAndExecute" checked>
                        </tags:nameValue2>
                        
                        <tags:nameValue2 nameKey=".run">
                            <cti:button nameKey="testBtn" type="submit" classes="f-blocker"/>
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                </form>
            </td>
        </tr>
        
        <%-- STOP CONTROL BY PROGRAM NAME.--%>
        <tr>
            <td class="functionality"><i:inline key=".stopByProgramName"/></td>
            <td class="inputTest">
                <form action="/debug/loadControlService/inputs/stopControlByProgramName" method="post">
                    <tags:nameValueContainer2>
                        <tags:nameValue2 nameKey=".programName" >
                            <input type="text" name="programName" value="${programName}">
                        </tags:nameValue2>
                        
                        <tags:nameValue2 nameKey=".stopTime">
                            <dt:date name="stopDate" value="${startDate}"/>
                            <input type="text" name="stopTime" value="${startTime}"><i:inline key=".hhMM"/>
                        </tags:nameValue2>
                        
                        <tags:nameValue2 nameKey=".forceExecute">
                            <input type="checkbox" name="force" onclick="toggleObserveConstraintsAndExecute(this, 'observeConstraintsAndExecute27');">
                        </tags:nameValue2>
                        
                        <tags:nameValue2 nameKey=".observeAndExecute">
                            <input type="checkbox" id="observeConstraintsAndExecute27" name="observeConstraintsAndExecute" checked>
                        </tags:nameValue2>
                        
                        <tags:nameValue2 nameKey=".run">
                            <cti:button nameKey="testBtn" type="submit" classes="f-blocker"/>
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                </form>
            </td>
        </tr>
        
        <%-- SCENARIOS LIST OF PROGRAMS --%>
        <tr>
            <td class="functionality"><i:inline key=".scenarioProgramList"/></td>
            <td class="inputTest">
                <form action="/debug/loadControlService/inputs/getScenarioProgramStartGears" method="post">
                    <tags:nameValueContainer2>
                        <tags:nameValue2 nameKey=".scenarioName" >
                            <input type="text" name="scenarioName" value="${scenarioName}">
                        </tags:nameValue2>
                        
                        <tags:nameValue2 nameKey=".run">
                            <cti:button nameKey="testBtn" type="submit" classes="f-blocker"/>
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                </form>
            </td>
        </tr>
        
        <%-- ENROLLING A DEVICE IN A PROGRAM.--%>
        <tr>
            <td class="functionality"><i:inline key=".enrollingInAProgram"/></td>
            <td class="inputTest">
                <form action="/debug/loadControlService/inputs/enrollingADeviceInAProgram" method="post">
                    <tags:nameValueContainer2>
                    
                        <tags:nameValue2 nameKey=".accountNumber" >
                            <input type="text" name="accountNumber" value="${accountNumber}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".programName" >
                            <input type="text" name="programName" value="${programName}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".serialNumber" >
                            <input type="text" name="serialNumber" value="${serialNumber}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".applianceCategoryName" >
                            <input type="text" name="applianceCategoryName" value="${applianceCategoryName}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".loadGroupName" >
                            <input type="text" name="loadGroupName" value="${loadGroupName}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".applianceKW" >
                            <input type="text" name="applianceKW" value="${applianceKW}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".relay" >
                            <input type="text" name="relay" value="${relay}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".seasonalLoad" >
                            <input type="checkbox" name="seasonalLoad" value="true">
                        </tags:nameValue2>
                        
                        <tags:nameValue2 nameKey=".run">
                            <cti:button nameKey="testBtn" type="submit" classes="f-blocker"/>
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                </form>
            </td>
        </tr>
    
    
        <%-- UNENROLLING A DEVICE IN A PROGRAM.--%>
        <tr>
            <td class="functionality"><i:inline key=".unenrollingInAProgram"/></td>
            <td class="inputTest">
                <form action="/debug/loadControlService/inputs/unenrollingADeviceInAProgram" method="post">
                    <tags:nameValueContainer2>
                    
                        <tags:nameValue2 nameKey=".accountNumber" >
                            <input type="text" name="accountNumber" value="${accountNumber}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".programName" >
                            <input type="text" name="programName" value="${programName}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".serialNumber" >
                            <input type="text" name="serialNumber" value="${serialNumber}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".applianceCategoryName" >
                            <input type="text" name="applianceCategoryName" value="${applianceCategoryName}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".loadGroupName" >
                            <input type="text" name="loadGroupName" value="${loadGroupName}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".applianceKW" >
                            <input type="text" name="applianceKW" value="${applianceKW}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".relay" >
                            <input type="text" name="relay" value="${relay}">
                        </tags:nameValue2>
                        
                        <tags:nameValue2 nameKey=".run">
                            <cti:button nameKey="testBtn" type="submit" classes="f-blocker"/>
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                </form>
            </td>
        </tr>

        <%-- ADD A DEVICE TO AN ACCOUNT--%>
        <tr>
            <td class="functionality"><i:inline key=".addDevice"/></td>
            <td class="inputTest">
                <form action="/debug/loadControlService/inputs/addDeviceToAccount" method="post">
                    <tags:nameValueContainer2>
                    
                        <tags:nameValue2 nameKey=".accountNumber" >
                            <input type="text" name="accountNumber" value="${accountNumber}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".serialNumber" >
                            <input type="text" name="serialNumber" value="${serialNumber}">
                        </tags:nameValue2>                        
                        <tags:nameValue2 nameKey=".deviceType" >
                            <input type="text" name="deviceType" value="${deviceType}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".fieldInstallDate">
                            <dt:date name="fieldInstallDate" value="${fieldInstallDate}"/>
                        </tags:nameValue2>                        
                        <tags:nameValue2 nameKey=".serviceCompanyName" >
                            <input type="text" name="serviceCompanyName" value="${serviceCompanyName}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".deviceLabel" >
                            <input type="text" name="deviceLabel" value="${deviceLabel}">
                        </tags:nameValue2>
                        
                        <tags:nameValue2 nameKey=".run">
                            <cti:button nameKey="testBtn" type="submit" classes="f-blocker"/>
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                </form>
            </td>
        </tr>
        
        <%-- UPDATE A DEVICE ON AN ACCOUNT--%>
        <tr>
            <td class="functionality"><i:inline key=".updateDevice"/></td>
            <td class="inputTest">
                <form action="/debug/loadControlService/inputs/updateDeviceOnAccount" method="post">
                    <tags:nameValueContainer2>
                    
                        <tags:nameValue2 nameKey=".accountNumber" >
                            <input type="text" name="accountNumber" value="${accountNumber}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".serialNumber" >
                            <input type="text" name="serialNumber" value="${serialNumber}">
                        </tags:nameValue2>                        
                        <tags:nameValue2 nameKey=".deviceType" >
                            <input type="text" name="deviceType" value="${deviceType}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".fieldInstallDate">
                            <dt:date name="fieldInstallDate" value="${fieldInstallDate}"/>
                        </tags:nameValue2>                        
                        <tags:nameValue2 nameKey=".serviceCompanyName" >
                            <input type="text" name="serviceCompanyName" value="${serviceCompanyName}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".deviceLabel" >
                            <input type="text" name="deviceLabel" value="${deviceLabel}">
                        </tags:nameValue2>
                        
                        <tags:nameValue2 nameKey=".run">
                            <cti:button nameKey="testBtn" type="submit" classes="f-blocker"/>
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                </form>
            </td>
        </tr>
        
        <%-- REMOVE A DEVICE FROM AN ACCOUNT--%>
        <tr>
            <td class="functionality"><i:inline key=".removeDevice"/></td>
            <td class="inputTest">
                <form action="/debug/loadControlService/inputs/removeDeviceFromAccount" method="post">
                    <tags:nameValueContainer2>
                    
                        <tags:nameValue2 nameKey=".accountNumber" >
                            <input type="text" name="accountNumber" value="${accountNumber}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".serialNumber" >
                            <input type="text" name="serialNumber" value="${serialNumber}">
                        </tags:nameValue2>                        
                        <tags:nameValue2 nameKey=".deviceType" >
                            <input type="text" name="deviceType" value="${deviceType}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".fieldRemoveDate">
                            <dt:date name="fieldRemoveDate" value="${fieldRemoveDate}"/>
                        </tags:nameValue2>                        
                        
                        <tags:nameValue2 nameKey=".run">
                            <cti:button nameKey="testBtn" type="submit" classes="f-blocker"/>
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                </form>
            </td>
        </tr>                

    </table>
    
</cti:standardPage>