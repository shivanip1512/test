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
    
    
    <h2>Account Service Test Page</h2>
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
        
        <%-- DELTE ACCOUNT --%>
        <tr>
            <td class="functionality">Delete Account</td>
            <td class="doc">Section 3, #3</td>
            <td class="xmlTest">
                <form action="/spring/accountServiceTest/deleteAccount_xml" method="post">
                <textarea name="xml" class="xml">
                    <accountActionResultRequest xmlns="http://yukon.cannontech.com/api" 
                        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
                        xsi:schemaLocation="http://yukon.cannontech.com/api ../AccountActionResultRequestRequest.xsd">
                        <accountNumber>42424241</accountNumber> 
                    </accountActionResultRequest>
                </textarea>
                <input type="submit" value="TEST">
                </form>
            </td>
            <td class="inputTest">
                <form action="/spring/accountServiceTest/deleteAccount" method="post">
                    <tags:nameValueContainer>
                        <tags:nameValue name="Account Number" nameColumnWidth="200px">
                            <input type="text" name="accountNumber" value="${accountNumber}">
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