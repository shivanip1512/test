<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage title="Account Service Test Page" module="blank">
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
            <th>Input Test</th>
        </tr>
        
        <%-- DELETE ACCOUNT --%>
        <tr>
            <td class="functionality">Delete Account</td>
            <td class="doc">Section 3, #3</td>
            <td class="inputTest">
                <form action="/spring/debug/accountService/inputs/deleteAccount" method="post">
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
        
        <%-- ADD ACCOUNT --%>
        <tr>
            <td class="functionality">Add Account</td>
            <td class="doc"></td>
            <td class="inputTest">
                <form action="/spring/debug/accountService/inputs/addAccount" method="post">
                    <tags:nameValueContainer>
                        <tags:nameValue name="Account Number" nameColumnWidth="200px">
                            <input type="text" name="accountNumber" value="${accountNumber}">
                        </tags:nameValue>
                        <tags:nameValue name="Last Name" nameColumnWidth="200px">
                            <input type="text" name="lastName" value="${lastName}">
                        </tags:nameValue>
                        <tags:nameValue name="First Name" nameColumnWidth="200px">
                            <input type="text" name="firstName" value="${firstName}">
                        </tags:nameValue>
                        <tags:nameValue name="Home Phone" nameColumnWidth="200px">
                            <input type="text" name="homePhone" value="${homePhone}">
                        </tags:nameValue>
                        <tags:nameValue name="Work Phone" nameColumnWidth="200px">
                            <input type="text" name="workPhone" value="${workPhone}">
                        </tags:nameValue>
                        <tags:nameValue name="Email Address" nameColumnWidth="200px">
                            <input type="text" name="emailAddress" value="${emailAddress}">
                        </tags:nameValue>
                        <tags:nameValue name="Alt Tracking #" nameColumnWidth="200px">
                            <input type="text" name="altTrackingNum" value="${altTrackingNum}">
                        </tags:nameValue>
                        <tags:nameValue name="Map #" nameColumnWidth="200px">
                            <input type="text" name="mapNum" value="${mapNum}">
                        </tags:nameValue>
                        <tags:nameValue name="commercial? (true-false)" nameColumnWidth="200px">
                            <input type="text" name="isCommercial" value="${isCommercial}">
                        </tags:nameValue>
                        <tags:nameValue name="Company Name (commercial only)" nameColumnWidth="200px">
                            <input type="text" name="companyName" value="${companyName}">
                        </tags:nameValue>
                        <tags:nameValue name="Address 1" nameColumnWidth="200px">
                            <input type="text" name="streetAddress1" value="${streetAddress1}">
                        </tags:nameValue>
                        <tags:nameValue name="Address 2" nameColumnWidth="200px">
                            <input type="text" name="streetAddress2" value="${streetAddress2}">
                        </tags:nameValue>
                        <tags:nameValue name="City" nameColumnWidth="200px">
                            <input type="text" name="city" value="${city}">
                        </tags:nameValue>
                        <tags:nameValue name="State" nameColumnWidth="200px">
                            <input type="text" name="state" value="${state}">
                        </tags:nameValue>
                        <tags:nameValue name="Zip" nameColumnWidth="200px">
                            <input type="text" name="zip" value="${zip}">
                        </tags:nameValue>
                        <tags:nameValue name="County" nameColumnWidth="200px">
                            <input type="text" name="county" value="${county}">
                        </tags:nameValue>
                        
                        <tags:nameValue name="Billing Address 1" nameColumnWidth="200px">
                            <input type="text" name="billingAddress1" value="${billingAddress1}">
                        </tags:nameValue>
                        <tags:nameValue name="Billing Address 2" nameColumnWidth="200px">
                            <input type="text" name="billingAddress2" value="${billingAddress2}">
                        </tags:nameValue>
                        <tags:nameValue name="Billing City" nameColumnWidth="200px">
                            <input type="text" name="billingCity" value="${billingCity}">
                        </tags:nameValue>
                        <tags:nameValue name="Billing State" nameColumnWidth="200px">
                            <input type="text" name="billingState" value="${billingState}">
                        </tags:nameValue>
                        <tags:nameValue name="Billing Zip" nameColumnWidth="200px">
                            <input type="text" name="billingZip" value="${billingZip}">
                        </tags:nameValue>
                        <tags:nameValue name="Billing County" nameColumnWidth="200px">
                            <input type="text" name="billingCounty" value="${billingCounty}">
                        </tags:nameValue>
                        
                        <tags:nameValue name="Substation Name" nameColumnWidth="200px">
                            <input type="text" name="substation" value="${substation}">
                        </tags:nameValue>
                        <tags:nameValue name="Feeder" nameColumnWidth="200px">
                            <input type="text" name="feeder" value="${feeder}">
                        </tags:nameValue>
                        <tags:nameValue name="Pole" nameColumnWidth="200px">
                            <input type="text" name="pole" value="${pole}">
                        </tags:nameValue>
                        <tags:nameValue name="Transformer Size" nameColumnWidth="200px">
                            <input type="text" name="transformerSize" value="${transformerSize}">
                        </tags:nameValue>
                        <tags:nameValue name="Service Voltage" nameColumnWidth="200px">
                            <input type="text" name="serviceVoltage" value="${serviceVoltage}">
                        </tags:nameValue>
                        
                        <tags:nameValue name="User Name" nameColumnWidth="200px">
                            <input type="text" name="username" value="${username}">
                        </tags:nameValue>
                        <tags:nameValue name="Password" nameColumnWidth="200px">
                            <input type="text" name="password" value="${password}">
                        </tags:nameValue>
                        <tags:nameValue name="Login Group" nameColumnWidth="200px">
                            <input type="text" name="loginGroup" value="${loginGroup}">
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