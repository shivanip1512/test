<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage page="webServices.account" module="support">
    
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
            <th class="functionality"><i:inline key=".functionality"/></th>
            <th class="doc"><i:inline key=".bgeIntegrationReqDocRef"/></th>
            <th><i:inline key=".inputTest"/></th>
        </tr>
        
        <%-- DELETE ACCOUNT --%>
        <tr>
            <td class="functionality"><i:inline key=".deleteAccount"/></td>
            <td class="doc"><i:inline key=".section3"/></td>
            <td class="inputTest">
                <form action="/spring/debug/accountService/inputs/deleteAccount" method="post">
                    <tags:nameValueContainer>
                        <tags:nameValue name="Account Number" nameColumnWidth="200px">
                            <input type="text" name="delete_accountNumber" value="${delete_accountNumber}">
                        </tags:nameValue>
                        
                        <tags:nameValue name="Operator (yukon user)" nameColumnWidth="200px">
                            <input type="text" name="delete_user" value="${delete_user}">
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
            <td class="functionality"><i:inline key=".addAccount"/></td>
            <td class="doc"></td>
            <td class="inputTest">
                <form action="/spring/debug/accountService/inputs/addAccount" method="post">
                    <tags:nameValueContainer>
                        <tags:nameValue name="Operator (yukon user)" nameColumnWidth="200px">
                            <input type="text" name="add_user" value="${add_user}">
                        </tags:nameValue>
                        <tags:nameValue name="Account Number" nameColumnWidth="200px">
                            <input type="text" name="add_accountNumber" value="${add_accountNumber}">
                        </tags:nameValue>
                        <tags:nameValue name="Last Name" nameColumnWidth="200px">
                            <input type="text" name="add_lastName" value="${add_lastName}">
                        </tags:nameValue>
                        <tags:nameValue name="First Name" nameColumnWidth="200px">
                            <input type="text" name="add_firstName" value="${add_firstName}">
                        </tags:nameValue>
                        <tags:nameValue name="Home Phone" nameColumnWidth="200px">
                            <input type="text" name="add_homePhone" value="${add_homePhone}">
                        </tags:nameValue>
                        <tags:nameValue name="Work Phone" nameColumnWidth="200px">
                            <input type="text" name="add_workPhone" value="${add_workPhone}">
                        </tags:nameValue>
                        <tags:nameValue name="Email Address" nameColumnWidth="200px">
                            <input type="text" name="add_emailAddress" value="${add_emailAddress}">
                        </tags:nameValue>
                        <tags:nameValue name="Alt Tracking #" nameColumnWidth="200px">
                            <input type="text" name="add_altTrackingNum" value="${add_altTrackingNum}">
                        </tags:nameValue>
                        <tags:nameValue name="Map #" nameColumnWidth="200px">
                            <input type="text" name="add_mapNum" value="${add_mapNum}">
                        </tags:nameValue>
                        <tags:nameValue name="commercial? (true-false)" nameColumnWidth="200px">
                            <input type="text" name="add_isCommercial" value="${add_isCommercial}">
                        </tags:nameValue>
                        <tags:nameValue name="Company Name (commercial only)" nameColumnWidth="200px">
                            <input type="text" name="add_companyName" value="${add_companyName}">
                        </tags:nameValue>
                        <tags:nameValue name="Address 1" nameColumnWidth="200px">
                            <input type="text" name="add_streetAddress1" value="${add_streetAddress1}">
                        </tags:nameValue>
                        <tags:nameValue name="Address 2" nameColumnWidth="200px">
                            <input type="text" name="add_streetAddress2" value="${add_streetAddress2}">
                        </tags:nameValue>
                        <tags:nameValue name="City" nameColumnWidth="200px">
                            <input type="text" name="add_city" value="${add_city}">
                        </tags:nameValue>
                        <tags:nameValue name="State" nameColumnWidth="200px">
                            <input type="text" name="add_state" value="${add_state}">
                        </tags:nameValue>
                        <tags:nameValue name="Zip" nameColumnWidth="200px">
                            <input type="text" name="add_zip" value="${add_zip}">
                        </tags:nameValue>
                        <tags:nameValue name="County" nameColumnWidth="200px">
                            <input type="text" name="add_county" value="${add_county}">
                        </tags:nameValue>
                        
                        <tags:nameValue name="Billing Address 1" nameColumnWidth="200px">
                            <input type="text" name="add_billingAddress1" value="${add_billingAddress1}">
                        </tags:nameValue>
                        <tags:nameValue name="Billing Address 2" nameColumnWidth="200px">
                            <input type="text" name="add_billingAddress2" value="${add_billingAddress2}">
                        </tags:nameValue>
                        <tags:nameValue name="Billing City" nameColumnWidth="200px">
                            <input type="text" name="add_billingCity" value="${add_billingCity}">
                        </tags:nameValue>
                        <tags:nameValue name="Billing State" nameColumnWidth="200px">
                            <input type="text" name="add_billingState" value="${add_billingState}">
                        </tags:nameValue>
                        <tags:nameValue name="Billing Zip" nameColumnWidth="200px">
                            <input type="text" name="add_billingZip" value="${add_billingZip}">
                        </tags:nameValue>
                        <tags:nameValue name="Billing County" nameColumnWidth="200px">
                            <input type="text" name="add_billingCounty" value="${add_billingCounty}">
                        </tags:nameValue>
                        
                        <tags:nameValue name="Substation Name" nameColumnWidth="200px">
                            <input type="text" name="add_substation" value="${add_substation}">
                        </tags:nameValue>
                        <tags:nameValue name="Feeder" nameColumnWidth="200px">
                            <input type="text" name="add_feeder" value="${add_feeder}">
                        </tags:nameValue>
                        <tags:nameValue name="Pole" nameColumnWidth="200px">
                            <input type="text" name="add_pole" value="${add_pole}">
                        </tags:nameValue>
                        <tags:nameValue name="Transformer Size" nameColumnWidth="200px">
                            <input type="text" name="add_transformerSize" value="${add_transformerSize}">
                        </tags:nameValue>
                        <tags:nameValue name="Service Voltage" nameColumnWidth="200px">
                            <input type="text" name="add_serviceVoltage" value="${add_serviceVoltage}">
                        </tags:nameValue>
                        
                        <tags:nameValue name="Username" nameColumnWidth="200px">
                            <input type="text" name="add_username" value="${add_username}">
                        </tags:nameValue>
                        <tags:nameValue name="Password" nameColumnWidth="200px">
                            <input type="text" name="add_password" value="${add_password}">
                        </tags:nameValue>
                        <tags:nameValue name="Login Group" nameColumnWidth="200px">
                            <input type="text" name="add_loginGroup" value="${add_loginGroup}">
                        </tags:nameValue>
                        
                        <tags:nameValue name="RUN">
                            <input type="submit" value="TEST">
                        </tags:nameValue>
                    </tags:nameValueContainer>
                </form>
            </td>
        </tr>
        
        <%-- UPDATE ACCOUNT --%>
        <tr>
            <td class="functionality"><i:inline key=".updateAccount"/></td>
            <td class="doc"></td>
            <td class="inputTest">
                <form action="/spring/debug/accountService/inputs/updateAccount" method="post">
                    <tags:nameValueContainer>
                        <tags:nameValue name="Operator (yukon user)" nameColumnWidth="200px">
                            <input type="text" name="update_user" value="${update_user}">
                        </tags:nameValue>
                        <tags:nameValue name="Account Number" nameColumnWidth="200px">
                            <input type="text" name="update_accountNumber" value="${update_accountNumber}">
                        </tags:nameValue>
                        <tags:nameValue name="Last Name" nameColumnWidth="200px">
                            <input type="text" name="update_lastName" value="${update_lastName}">
                        </tags:nameValue>
                        <tags:nameValue name="First Name" nameColumnWidth="200px">
                            <input type="text" name="update_firstName" value="${update_firstName}">
                        </tags:nameValue>
                        <tags:nameValue name="Home Phone" nameColumnWidth="200px">
                            <input type="text" name="update_homePhone" value="${update_homePhone}">
                        </tags:nameValue>
                        <tags:nameValue name="Work Phone" nameColumnWidth="200px">
                            <input type="text" name="update_workPhone" value="${update_workPhone}">
                        </tags:nameValue>
                        <tags:nameValue name="Email Address" nameColumnWidth="200px">
                            <input type="text" name="update_emailAddress" value="${update_emailAddress}">
                        </tags:nameValue>
                        <tags:nameValue name="Alt Tracking #" nameColumnWidth="200px">
                            <input type="text" name="update_altTrackingNum" value="${update_altTrackingNum}">
                        </tags:nameValue>
                        <tags:nameValue name="Map #" nameColumnWidth="200px">
                            <input type="text" name="update_mapNum" value="${update_mapNum}">
                        </tags:nameValue>
                        <tags:nameValue name="commercial? (true-false)" nameColumnWidth="200px">
                            <input type="text" name="update_isCommercial" value="${update_isCommercial}">
                        </tags:nameValue>
                        <tags:nameValue name="Company Name (commercial only)" nameColumnWidth="200px">
                            <input type="text" name="update_companyName" value="${update_companyName}">
                        </tags:nameValue>
                        <tags:nameValue name="Address 1" nameColumnWidth="200px">
                            <input type="text" name="update_streetAddress1" value="${update_streetAddress1}">
                        </tags:nameValue>
                        <tags:nameValue name="Address 2" nameColumnWidth="200px">
                            <input type="text" name="update_streetAddress2" value="${update_streetAddress2}">
                        </tags:nameValue>
                        <tags:nameValue name="City" nameColumnWidth="200px">
                            <input type="text" name="update_city" value="${update_city}">
                        </tags:nameValue>
                        <tags:nameValue name="State" nameColumnWidth="200px">
                            <input type="text" name="update_state" value="${update_state}">
                        </tags:nameValue>
                        <tags:nameValue name="Zip" nameColumnWidth="200px">
                            <input type="text" name="update_zip" value="${update_zip}">
                        </tags:nameValue>
                        <tags:nameValue name="County" nameColumnWidth="200px">
                            <input type="text" name="update_county" value="${update_county}">
                        </tags:nameValue>
                        
                        <tags:nameValue name="Billing Address 1" nameColumnWidth="200px">
                            <input type="text" name="update_billingAddress1" value="${update_billingAddress1}">
                        </tags:nameValue>
                        <tags:nameValue name="Billing Address 2" nameColumnWidth="200px">
                            <input type="text" name="update_billingAddress2" value="${update_billingAddress2}">
                        </tags:nameValue>
                        <tags:nameValue name="Billing City" nameColumnWidth="200px">
                            <input type="text" name="update_billingCity" value="${update_billingCity}">
                        </tags:nameValue>
                        <tags:nameValue name="Billing State" nameColumnWidth="200px">
                            <input type="text" name="update_billingState" value="${update_billingState}">
                        </tags:nameValue>
                        <tags:nameValue name="Billing Zip" nameColumnWidth="200px">
                            <input type="text" name="update_billingZip" value="${update_billingZip}">
                        </tags:nameValue>
                        <tags:nameValue name="Billing County" nameColumnWidth="200px">
                            <input type="text" name="update_billingCounty" value="${update_billingCounty}">
                        </tags:nameValue>
                        
                        <tags:nameValue name="Substation Name" nameColumnWidth="200px">
                            <input type="text" name="update_substation" value="${update_substation}">
                        </tags:nameValue>
                        <tags:nameValue name="Feeder" nameColumnWidth="200px">
                            <input type="text" name="update_feeder" value="${update_feeder}">
                        </tags:nameValue>
                        <tags:nameValue name="Pole" nameColumnWidth="200px">
                            <input type="text" name="update_pole" value="${update_pole}">
                        </tags:nameValue>
                        <tags:nameValue name="Transformer Size" nameColumnWidth="200px">
                            <input type="text" name="update_transformerSize" value="${update_transformerSize}">
                        </tags:nameValue>
                        <tags:nameValue name="Service Voltage" nameColumnWidth="200px">
                            <input type="text" name="update_serviceVoltage" value="${update_serviceVoltage}">
                        </tags:nameValue>
                        
                        <tags:nameValue name="Username" nameColumnWidth="200px">
                            <input type="text" name="update_username" value="${update_username}">
                        </tags:nameValue>
                        <tags:nameValue name="Password" nameColumnWidth="200px">
                            <input type="text" name="update_password" value="${update_password}">
                        </tags:nameValue>
                        <tags:nameValue name="Login Group" nameColumnWidth="200px">
                            <input type="text" name="update_loginGroup" value="${update_loginGroup}">
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