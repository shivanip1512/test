<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage title="Load Control Service Test Page" module="blank">
    <cti:standardMenu menuSelection=""/>
    
    <style type="text/css">
      table.resultsTable th.functionality {width:200px;}
      table.resultsTable td.functionality {vertical-align: top;text-align:center;}
      table.resultsTable th.doc {width:200px;}
      table.resultsTable td.doc {vertical-align: top;text-align:center;}
      table.resultsTable td.xmlTest {}
      table.resultsTable td.inputTest {vertical-align: top;}
      textarea.xml {
            width: 600px;
            height: 150px;
            border: 3px solid #cccccc;
            padding: 2px;
        }
    </style>
    
    
    <h2>Load Control Service Test Page</h2>
    <br>
    
    <%-- RESULT AREA --%>
    <c:if test="${not empty results || not empty errorReasons}">
    
        <tags:nameValueContainer>
            
            <c:forEach items="${errorReasons}" var="errorReason">
                <tags:nameValue name="Error">
                    <div class="errorRed">${errorReason}</div>
                </tags:nameValue>
            </c:forEach>
            
            <c:forEach items="${results}" var="result">
                <tags:nameValue name="Result">
                    <div style="color:#151B8D">${result}</div>
                </tags:nameValue>
            </c:forEach>
        
        </tags:nameValueContainer>
    
    <br><br>
    </c:if>
    
    <table class="resultsTable">
    
        <tr>
            <th class="functionality">Functionality</th>
            <th class="doc">BGE Integration<br>Requirements Doc Ref</th>
            <th>XML Test</th>
            <th>Input Test</th>
        </tr>
        
        <%-- PROGRAM STATUS BY PROGRAM NAME --%>
        <tr>
            <td class="functionality">Program Status By Program Name</td>
            <td class="doc">Section 2, #1</td>
            <td class="xmlTest">
                <form action="/spring/loadControlServiceTest/getProgramStatusByProgramName_xml" method="post">
                <textarea name="xml" class="xml">
<programStatusRequest xmlns="http://yukon.cannontech.com/api" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://yukon.cannontech.com/api ../ProgramStatusRequest.xsd">
  <programName>AC</programName> 
</programStatusRequest>
                </textarea>
                <input type="submit" value="TEST">
                </form>
            </td>
            <td class="inputTest">
                <form action="/spring/loadControlServiceTest/getProgramStatusByProgramName" method="post">
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
            <td class="doc">Section 2, #2</td>
            <td class="xmlTest">
                <form action="/spring/loadControlServiceTest/getAllCurrentlyActivePrograms_xml" method="post">
                <textarea name="xml" class="xml">
<currentlyActiveProgramsRequest xmlns="http://yukon.cannontech.com/api" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://yukon.cannontech.com/api ../CurrentlyActiveProgramsRequest.xsd" /> 
                </textarea>
                <input type="submit" value="TEST">
                </form>
            </td>
            <td class="inputTest">
                <form action="/spring/loadControlServiceTest/getAllCurrentlyActivePrograms" method="post">
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
            <td class="doc">Section 2, #3</td>
            <td class="xmlTest">
                <form action="/spring/loadControlServiceTest/startControlByScenarioName_xml" method="post">
                <textarea name="xml" class="xml">
<scenarioStartRequest xmlns="http://yukon.cannontech.com/api" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://yukon.cannontech.com/api ../ScenarioStartRequest.xsd">
  <scenarioName>AC</scenarioName> 
  <startDateTime>2008-10-28T15:07:40</startDateTime> 
  <stopDateTime>2008-10-28T17:07:40</stopDateTime> 
</scenarioStartRequest>
                </textarea>
                <input type="submit" value="TEST">
                </form>
            </td>
            <td class="inputTest">
                <form action="/spring/loadControlServiceTest/startControlByScenarioName" method="post">
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

                        <tags:nameValue name="Force Start">
                            <input type="checkbox" name="force">
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
            <td class="doc">Section 2, #4</td>
            <td class="xmlTest">
                <form action="/spring/loadControlServiceTest/startControlByProgramName_xml" method="post">
                <textarea name="xml" class="xml">
NOT IMPLEMENTED
                </textarea>
                <input type="submit" value="TEST">
                </form>
            </td>
            <td class="inputTest">
                <form action="/spring/loadControlServiceTest/startControlByProgramName" method="post">
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
                        
                        <tags:nameValue name="Force Start">
                            <input type="checkbox" name="force">
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
            <td class="doc">Section 2, #5</td>
            <td class="xmlTest">
                <form action="/spring/loadControlServiceTest/stopControlByScenarioName_xml" method="post">
                <textarea name="xml" class="xml">
NOT IMPLEMENTED
                </textarea>
                <input type="submit" value="TEST">
                </form>
            </td>
            <td class="inputTest">
                <form action="/spring/loadControlServiceTest/stopControlByScenarioName" method="post">
                    <tags:nameValueContainer>
                        <tags:nameValue name="Scenario Name" nameColumnWidth="200px">
                            <input type="text" name="scenarioName" value="${scenarioName}">
                        </tags:nameValue>
                        
                        <tags:nameValue name="Stop Time">
                            <tags:dateInputCalendar fieldName="stopDate" fieldValue="${stopDate}"/>
                            <input type="text" name="stopTime" value="${stopTime}">(HH:MM)
                        </tags:nameValue>
                        
                        <tags:nameValue name="Force Stop">
                            <input type="checkbox" name="force">
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
            <td class="doc">Section 2, #6</td>
            <td class="xmlTest">
                <form action="/spring/loadControlServiceTest/stopControlByProgramName_xml" method="post">
                <textarea name="xml" class="xml">
NOT IMPLEMENTED
                </textarea>
                <input type="submit" value="TEST">
                </form>
            </td>
            <td class="inputTest">
                <form action="/spring/loadControlServiceTest/stopControlByProgramName" method="post">
                    <tags:nameValueContainer>
                        <tags:nameValue name="Program Name" nameColumnWidth="200px">
                            <input type="text" name="programName" value="${programName}">
                        </tags:nameValue>
                        
                        <tags:nameValue name="Stop Time">
                            <tags:dateInputCalendar fieldName="stopDate" fieldValue="${startDate}"/>
                            <input type="text" name="stopTime" value="${startTime}">(HH:MM)
                        </tags:nameValue>
                        
                        <tags:nameValue name="Force Stop">
                            <input type="checkbox" name="force">
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
            <td class="doc">Section 2, #7</td>
            <td class="xmlTest">
                <form action="/spring/loadControlServiceTest/getScenarioProgramStartGears_xml" method="post">
                <textarea name="xml" class="xml">
NOT IMPLEMENTED
                </textarea>
                <input type="submit" value="TEST">
                </form>
            </td>
            <td class="inputTest">
                <form action="/spring/loadControlServiceTest/getScenarioProgramStartGears" method="post">
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
    
    </table>
    
</cti:standardPage>