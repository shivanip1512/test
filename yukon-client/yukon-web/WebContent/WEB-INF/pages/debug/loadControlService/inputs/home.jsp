<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage page="webServices.loadControl" module="support">

    <style type="text/css">
      table.resultsTable th.functionality {width:200px;}
      table.resultsTable td.functionality {vertical-align: top;text-align:center;}
      table.resultsTable td.xmlTest {}
      table.resultsTable td.inputTest {vertical-align: top;}
      textarea.xml {
            width: 600px;
            height: 150px;
            border: 3px solid #cccccc;
            padding: 2px;
        }
    </style>
    
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
            <li><div style="color:#151B8D">${result}</div></li>
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
                <form action="/spring/debug/loadControlService/inputs/getProgramStatusByProgramName" method="post">
                    <tags:nameValueContainer>
                        <tags:nameValue name="Program Name" nameColumnWidth="200px">
                            <input type="text" name="programName" value="${programName}">
                        </tags:nameValue>
                        
                        <tags:nameValue name="RUN">
                            <input type="submit" value="TEST">
                        </tags:nameValue>
                    </tags:nameValueContainer>
                </form>
            </td>
        </tr>
        
        
        <%-- CURRENTLY ACTIVE PROGRAMS --%>
        <tr>
            <td class="functionality"><i:inline key=".programStatusAllActive"/></td>
            <td class="inputTest">
                <form action="/spring/debug/loadControlService/inputs/getAllCurrentlyActivePrograms" method="post">
                    <tags:nameValueContainer>
                        <tags:nameValue name="RUN" nameColumnWidth="200px">
                            <input type="submit" value="TEST">
                        </tags:nameValue>
                    </tags:nameValueContainer>
                </form>
            </td>
        </tr>
        
        
        <%-- START CONTROL BY SCENARIO NAME.--%>
        <tr>
            <td class="functionality"><i:inline key=".startByScenarioName"/></td>
            <td class="inputTest">
                <form action="/spring/debug/loadControlService/inputs/startControlByScenarioName" method="post">
                    <tags:nameValueContainer>
                        <tags:nameValue name="Scenario Name" nameColumnWidth="200px">
                            <input type="text" name="scenarioName" value="${scenarioName}">
                        </tags:nameValue>
                        
                        <tags:nameValue name="Start Time">
                            <tags:dateInputCalendar fieldName="startDate" fieldValue="${startDate}"/>
                            <input type="text" name="startTime" value="${startTime}">(HH:MM)
                        </tags:nameValue>
                        
                        <tags:nameValue name="Stop Time">
                            <tags:dateInputCalendar fieldName="stopDate" fieldValue="${stopDate}"/>
                            <input type="text" name="stopTime" value="${stopTime}">(HH:MM)
                        </tags:nameValue>

                        <tags:nameValue name="Force Execute">
                            <input type="checkbox" name="force" onclick="toggleObserveConstraintsAndExecute(this, 'observeConstraintsAndExecute24');">
                        </tags:nameValue>
                        
                        <tags:nameValue name="Observe Constraints And Execute">
                            <input type="checkbox" id="observeConstraintsAndExecute24" name="observeConstraintsAndExecute" checked>
                        </tags:nameValue>
                        
                        <tags:nameValue name="RUN">
                            <input type="submit" value="TEST">
                        </tags:nameValue>
                    </tags:nameValueContainer>
                </form>
            </td>
        </tr>
        
        <%-- START CONTROL BY PROGRAM NAME. --%>
        <tr>
            <td class="functionality"><i:inline key=".startByProgramName"/></td>
            <td class="inputTest">
                <form action="/spring/debug/loadControlService/inputs/startControlByProgramName" method="post">
                    <tags:nameValueContainer>
                        <tags:nameValue name="Program Name" nameColumnWidth="200px">
                            <input type="text" name="programName" value="${programName}">
                        </tags:nameValue>
                        
                        <tags:nameValue name="Start Time">
                            <tags:dateInputCalendar fieldName="startDate" fieldValue="${startDate}"/>
                            <input type="text" name="startTime" value="${startTime}">(HH:MM)
                        </tags:nameValue>
                        
                        <tags:nameValue name="Stop Time">
                            <tags:dateInputCalendar fieldName="stopDate" fieldValue="${stopDate}"/>
                            <input type="text" name="stopTime" value="${stopTime}">(HH:MM)
                        </tags:nameValue>
                        
                        <tags:nameValue name="Gear Name">
                            <input type="text" name="gearName" value="${gearName}" size="2">
                        </tags:nameValue>
                        
                        <tags:nameValue name="Force Execute">
                            <input type="checkbox" name="force" onclick="toggleObserveConstraintsAndExecute(this, 'observeConstraintsAndExecute25');">
                        </tags:nameValue>
                        
                        <tags:nameValue name="Observe Constraints And Execute">
                            <input type="checkbox" id="observeConstraintsAndExecute25" name="observeConstraintsAndExecute" checked>
                        </tags:nameValue>
                        
                        <tags:nameValue name="RUN">
                            <input type="submit" value="TEST">
                        </tags:nameValue>
                    </tags:nameValueContainer>
                </form>
            </td>
        </tr>
        
        <%-- STOP CONTROL BY SCENARIO NAME.--%>
        <tr>
            <td class="functionality"><i:inline key=".stopByScenarioName"/></td>
            <td class="inputTest">
                <form action="/spring/debug/loadControlService/inputs/stopControlByScenarioName" method="post">
                    <tags:nameValueContainer>
                        <tags:nameValue name="Scenario Name" nameColumnWidth="200px">
                            <input type="text" name="scenarioName" value="${scenarioName}">
                        </tags:nameValue>
                        
                        <tags:nameValue name="Stop Time">
                            <tags:dateInputCalendar fieldName="stopDate" fieldValue="${stopDate}"/>
                            <input type="text" name="stopTime" value="${stopTime}">(HH:MM)
                        </tags:nameValue>
                        
                        <tags:nameValue name="Force Execute">
                            <input type="checkbox" name="force" onclick="toggleObserveConstraintsAndExecute(this, 'observeConstraintsAndExecute26');">
                        </tags:nameValue>
                        
                        <tags:nameValue name="Observe Constraints And Execute">
                            <input type="checkbox" id="observeConstraintsAndExecute26" name="observeConstraintsAndExecute" checked>
                        </tags:nameValue>
                        
                        <tags:nameValue name="RUN">
                            <input type="submit" value="TEST">
                        </tags:nameValue>
                    </tags:nameValueContainer>
                </form>
            </td>
        </tr>
        
        <%-- STOP CONTROL BY PROGRAM NAME.--%>
        <tr>
            <td class="functionality"><i:inline key=".stopByProgramName"/></td>
            <td class="inputTest">
                <form action="/spring/debug/loadControlService/inputs/stopControlByProgramName" method="post">
                    <tags:nameValueContainer>
                        <tags:nameValue name="Program Name" nameColumnWidth="200px">
                            <input type="text" name="programName" value="${programName}">
                        </tags:nameValue>
                        
                        <tags:nameValue name="Stop Time">
                            <tags:dateInputCalendar fieldName="stopDate" fieldValue="${startDate}"/>
                            <input type="text" name="stopTime" value="${startTime}">(HH:MM)
                        </tags:nameValue>
                        
                        <tags:nameValue name="Force Execute">
                            <input type="checkbox" name="force" onclick="toggleObserveConstraintsAndExecute(this, 'observeConstraintsAndExecute27');">
                        </tags:nameValue>
                        
                        <tags:nameValue name="Observe Constraints And Execute">
                            <input type="checkbox" id="observeConstraintsAndExecute27" name="observeConstraintsAndExecute" checked>
                        </tags:nameValue>
                        
                        <tags:nameValue name="RUN">
                            <input type="submit" value="TEST">
                        </tags:nameValue>
                    </tags:nameValueContainer>
                </form>
            </td>
        </tr>
        
        <%-- SCENARIOS LIST OF PROGRAMS --%>
        <tr>
            <td class="functionality"><i:inline key=".scenarioProgramList"/></td>
            <td class="inputTest">
                <form action="/spring/debug/loadControlService/inputs/getScenarioProgramStartGears" method="post">
                    <tags:nameValueContainer>
                        <tags:nameValue name="Scenario Name" nameColumnWidth="200px">
                            <input type="text" name="scenarioName" value="${scenarioName}">
                        </tags:nameValue>
                        
                        <tags:nameValue name="RUN">
                            <input type="submit" value="TEST">
                        </tags:nameValue>
                    </tags:nameValueContainer>
                </form>
            </td>
        </tr>
        
        <%-- ENROLLING A DEVICE IN A PROGRAM.--%>
        <tr>
            <td class="functionality"><i:inline key=".enrollingInAProgram"/></td>
            <td class="inputTest">
                <form action="/spring/debug/loadControlService/inputs/enrollingADeviceInAProgram" method="post">
                    <tags:nameValueContainer>
                    
                        <tags:nameValue name="Account Number" nameColumnWidth="200px">
                            <input type="text" name="accountNumber" value="${accountNumber}">
                        </tags:nameValue>
                        <tags:nameValue name="Program Name" nameColumnWidth="200px">
                            <input type="text" name="programName" value="${programName}">
                        </tags:nameValue>
                        <tags:nameValue name="Serial Number" nameColumnWidth="200px">
                            <input type="text" name="serialNumber" value="${serialNumber}">
                        </tags:nameValue>
                        <tags:nameValue name="Appliance Category Name" nameColumnWidth="200px">
                            <input type="text" name="applianceCategoryName" value="${applianceCategoryName}">
                        </tags:nameValue>
                        <tags:nameValue name="Load Group Name" nameColumnWidth="200px">
                            <input type="text" name="loadGroupName" value="${loadGroupName}">
                        </tags:nameValue>
                        <tags:nameValue name="applianceKW" nameColumnWidth="200px">
                            <input type="text" name="applianceKW" value="${applianceKW}">
                        </tags:nameValue>
                        <tags:nameValue name="Relay" nameColumnWidth="200px">
                            <input type="text" name="relay" value="${relay}">
                        </tags:nameValue>
                        <tags:nameValue name="SeasonalLoad" nameColumnWidth="200px">
                            <input type="checkbox" name="seasonalLoad" value="true">
                        </tags:nameValue>
                        
                        <tags:nameValue name="RUN">
                            <input type="submit" value="TEST">
                        </tags:nameValue>
                    </tags:nameValueContainer>
                </form>
            </td>
        </tr>
    
    
        <%-- UNENROLLING A DEVICE IN A PROGRAM.--%>
        <tr>
            <td class="functionality"><i:inline key=".unenrollingInAProgram"/></td>
            <td class="inputTest">
                <form action="/spring/debug/loadControlService/inputs/unenrollingADeviceInAProgram" method="post">
                    <tags:nameValueContainer>
                    
                        <tags:nameValue name="Account Number" nameColumnWidth="200px">
                            <input type="text" name="accountNumber" value="${accountNumber}">
                        </tags:nameValue>
                        <tags:nameValue name="Program Name" nameColumnWidth="200px">
                            <input type="text" name="programName" value="${programName}">
                        </tags:nameValue>
                        <tags:nameValue name="Serial Number" nameColumnWidth="200px">
                            <input type="text" name="serialNumber" value="${serialNumber}">
                        </tags:nameValue>
                        <tags:nameValue name="Appliance Category Name" nameColumnWidth="200px">
                            <input type="text" name="applianceCategoryName" value="${applianceCategoryName}">
                        </tags:nameValue>
                        <tags:nameValue name="Load Group Name" nameColumnWidth="200px">
                            <input type="text" name="loadGroupName" value="${loadGroupName}">
                        </tags:nameValue>
                        <tags:nameValue name="applianceKW" nameColumnWidth="200px">
                            <input type="text" name="applianceKW" value="${applianceKW}">
                        </tags:nameValue>
                        <tags:nameValue name="Relay" nameColumnWidth="200px">
                            <input type="text" name="relay" value="${relay}">
                        </tags:nameValue>
                        
                        <tags:nameValue name="RUN">
                            <input type="submit" value="TEST">
                        </tags:nameValue>
                    </tags:nameValueContainer>
                </form>
            </td>
        </tr>

        <%-- ADD A DEVICE TO AN ACCOUNT--%>
        <tr>
            <td class="functionality"><i:inline key=".addDevice"/></td>
            <td class="inputTest">
                <form action="/spring/debug/loadControlService/inputs/addDeviceToAccount" method="post">
                    <tags:nameValueContainer>
                    
                        <tags:nameValue name="Account Number" nameColumnWidth="200px">
                            <input type="text" name="accountNumber" value="${accountNumber}">
                        </tags:nameValue>
                        <tags:nameValue name="Serial Number" nameColumnWidth="200px">
                            <input type="text" name="serialNumber" value="${serialNumber}">
                        </tags:nameValue>                        
                        <tags:nameValue name="Device Type" nameColumnWidth="200px">
                            <input type="text" name="deviceType" value="${deviceType}">
                        </tags:nameValue>
                        <tags:nameValue name="Field Install Date">
                            <tags:dateInputCalendar fieldName="fieldInstallDate" fieldValue="${fieldInstallDate}"/>
                        </tags:nameValue>                        
                        <tags:nameValue name="Service Company Name" nameColumnWidth="200px">
                            <input type="text" name="serviceCompanyName" value="${serviceCompanyName}">
                        </tags:nameValue>
                        <tags:nameValue name="Device Label" nameColumnWidth="200px">
                            <input type="text" name="deviceLabel" value="${deviceLabel}">
                        </tags:nameValue>
                        
                        <tags:nameValue name="RUN">
                            <input type="submit" value="TEST">
                        </tags:nameValue>
                    </tags:nameValueContainer>
                </form>
            </td>
        </tr>
        
        <%-- UPDATE A DEVICE ON AN ACCOUNT--%>
        <tr>
            <td class="functionality"><i:inline key=".updateDevice"/></td>
            <td class="inputTest">
                <form action="/spring/debug/loadControlService/inputs/updateDeviceOnAccount" method="post">
                    <tags:nameValueContainer>
                    
                        <tags:nameValue name="Account Number" nameColumnWidth="200px">
                            <input type="text" name="accountNumber" value="${accountNumber}">
                        </tags:nameValue>
                        <tags:nameValue name="Serial Number" nameColumnWidth="200px">
                            <input type="text" name="serialNumber" value="${serialNumber}">
                        </tags:nameValue>                        
                        <tags:nameValue name="Device Type" nameColumnWidth="200px">
                            <input type="text" name="deviceType" value="${deviceType}">
                        </tags:nameValue>
                        <tags:nameValue name="Field Install Date">
                            <tags:dateInputCalendar fieldName="fieldInstallDate" fieldValue="${fieldInstallDate}"/>
                        </tags:nameValue>                        
                        <tags:nameValue name="Service Company Name" nameColumnWidth="200px">
                            <input type="text" name="serviceCompanyName" value="${serviceCompanyName}">
                        </tags:nameValue>
                        <tags:nameValue name="Device Label" nameColumnWidth="200px">
                            <input type="text" name="deviceLabel" value="${deviceLabel}">
                        </tags:nameValue>
                        
                        <tags:nameValue name="RUN">
                            <input type="submit" value="TEST">
                        </tags:nameValue>
                    </tags:nameValueContainer>
                </form>
            </td>
        </tr>
        
        <%-- REMOVE A DEVICE FROM AN ACCOUNT--%>
        <tr>
            <td class="functionality"><i:inline key=".removeDevice"/></td>
            <td class="inputTest">
                <form action="/spring/debug/loadControlService/inputs/removeDeviceFromAccount" method="post">
                    <tags:nameValueContainer>
                    
                        <tags:nameValue name="Account Number" nameColumnWidth="200px">
                            <input type="text" name="accountNumber" value="${accountNumber}">
                        </tags:nameValue>
                        <tags:nameValue name="Serial Number" nameColumnWidth="200px">
                            <input type="text" name="serialNumber" value="${serialNumber}">
                        </tags:nameValue>                        
                        <tags:nameValue name="Device Type" nameColumnWidth="200px">
                            <input type="text" name="deviceType" value="${deviceType}">
                        </tags:nameValue>
                        <tags:nameValue name="Field Remove Date">
                            <tags:dateInputCalendar fieldName="fieldRemoveDate" fieldValue="${fieldRemoveDate}"/>
                        </tags:nameValue>                        
                        
                        <tags:nameValue name="RUN">
                            <input type="submit" value="TEST">
                        </tags:nameValue>
                    </tags:nameValueContainer>
                </form>
            </td>
        </tr>                

    </table>
    
</cti:standardPage>