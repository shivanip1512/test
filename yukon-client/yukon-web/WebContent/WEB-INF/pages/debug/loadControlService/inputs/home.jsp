<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage title="Load Control Service Inputs Test Page" module="debug">
    <cti:standardMenu menuSelection="loadControlService|inputs" />
    
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
    
    <h2>Load Control Service Inputs Test Page</h2>
    <br>
    
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
            <th class="functionality">Functionality</th>
            <th>Input Test</th>
        </tr>
        
        <%-- PROGRAM STATUS BY PROGRAM NAME --%>
        <tr>
            <td class="functionality">Program Status By Program Name</td>
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
            <td class="functionality">All Currently Active Programs</td>
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
            <td class="functionality">Start By Scenario Name</td>
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
            <td class="functionality">Start By Program Name</td>
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
                        
                        <tags:nameValue name="Gear Number">
                            <input type="text" name="gearNumber" value="${gearNumber}" size="2">
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
            <td class="functionality">Stop By Scenario Name</td>
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
            <td class="functionality">Stop By Program Name</td>
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
            <td class="functionality">Scenario List O' Programs (Starting Gears)</td>
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
            <td class="functionality">Enrolling a device in a program</td>
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
                        
                        <tags:nameValue name="RUN">
                            <input type="submit" value="TEST">
                        </tags:nameValue>
                    </tags:nameValueContainer>
                </form>
            </td>
        </tr>
    
    
        <%-- UNENROLLING A DEVICE IN A PROGRAM.--%>
        <tr>
            <td class="functionality">Unenrolling a device in a program</td>
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

    </table>
    
</cti:standardPage>