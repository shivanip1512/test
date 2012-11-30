<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<cti:standardPage module="operator" page="accountImport">

    <cti:checkRolesAndProperties value="OPERATOR_IMPORT_CUSTOMER_ACCOUNT" />
    
    <cti:url var="importImg" value="/WebConfig/yukon/Icons/import_by_DEVICE_TYPE.gif"/>
    <cti:msg var="instructionsText" key="yukon.web.modules.operator.accountImport.instructionsText" />
    
    <tags:boxContainer2 nameKey="fileUploadContainer">
        <cti:dataGrid cols="2" tableClasses="twoColumnLayout">
    
            <cti:dataGridCell>
    
                <form:form id="importForm" commandName="accountImportData" action="/stars/operator/account/uploadImportFiles" enctype="multipart/form-data">
                
                    <%-- note --%>
                    <table>
                        <tr valign="top">
                            <td class="fwb"><i:inline key=".noteLabel"/></td>
                            <td style="font-size:11px;"><i:inline key=".noteText"/></td>
                        </tr>
                    </table>
                    
                    <br>
                    
                    <tags:nameValueContainer2>
                        <tags:nameValue2 nameKey=".accountImportFile" ><input type="file" name="accountImportFile" size="35"></tags:nameValue2>
                        <tags:nameValue2 nameKey=".hardwareImportFile"><input type="file" name="hardwareImportFile" size="35"></tags:nameValue2>
                        <tags:inputNameValue nameKey=".email" path="email" size="35"/>
                    </tags:nameValueContainer2>
                    
                    <div class="actionArea box stacked">
                        <div class="fl"><cti:button nameKey="prescan" type="submit" styleClass="f_blocker"/></div>
                    </div>
                </form:form>
                
            </cti:dataGridCell>
            
            <cti:dataGridCell>
                
                <%-- instructions --%>
                <table>
                    <tr>
                        <td class="fwb"><i:inline key=".instructionsLabel"/></td>
                        <td>${instructionsText}</td>
                    </tr>
                </table>
                
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".sampleFiles">
                        <a href="<cti:url value="/WebConfig/custom/sample_bulk_files/Sample_Account_Import_File.csv"/>"><i:inline key=".sampleAccountFile"/></a>, 
                        <a href="<cti:url value="/WebConfig/custom/sample_bulk_files/Sample_Hardware_Import_File.csv"/>"><i:inline key=".sampleHardwareFile"/></a>,
                        <a href="<cti:url value="/WebConfig/custom/sample_bulk_files/Sample_Account_And_Hardware_Import_File.csv"/>"><i:inline key=".sampleAccountAndHardwareFile"/></a> 
                    </tags:nameValue2>
                </tags:nameValueContainer2>
                
            </cti:dataGridCell>
            
            <cti:dataGridCell>
            
                <div class="box" style="background-color:#CDCDCD;padding:10px;">
                    <div class="fl"><img src="${importImg}"></div>
                    <div class="fl" style="margin-left:20px;">
                        <h2><i:inline key=".importAccountTableHeader"/></h2>
                        <span class="detail"><i:inline key=".importAccountTableDescription"/></span>
                    </div>
                </div>
                <table class="resultsTable bulkImport">
                    <thead>
                        <tr>
                            <th><i:inline key=".accountColumnHeader"/></th>
                            <th><i:inline key=".accountColumnDescription"/></th>
                        </tr>
                    </thead>
                    <tfoot></tfoot>
                    <tbody>
                        <c:forEach var="field" items="${accountFields}">
                            <tr valign="top">
                                <td class="fwb">${field.name} <sup>${field.legendKey}</sup></td>
                                <td><i:inline key=".accountField.description.${field}"/></td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
                <div class="box detail" style="background-color:#CDCDCD;padding:10px;">
                    <table>
                        <tr>
                            <td><i:inline key=".accountFieldLegend.a.label"/></td>
                            <td><i:inline key=".accountFieldLegend.a.text"/></td>
                        </tr>
                        <tr>
                            <td><i:inline key=".accountFieldLegend.b.label"/></td>
                            <td><i:inline key=".accountFieldLegend.b.text"/></td>
                        </tr>
                        <tr>
                            <td><i:inline key=".accountFieldLegend.c.label"/></td>
                            <td><i:inline key=".accountFieldLegend.c.text"/></td>
                        </tr>
                    </table>
                </div>
                
            </cti:dataGridCell>
            
            <cti:dataGridCell>
                    
                <div class="box" style="background-color:#CDCDCD;padding:10px;">
                    <div class="fl"><img src="${importImg}"></div>
                    <div class="fl" style="margin-left:20px;">
                        <h2><i:inline key=".importHardwareTableHeader"/></h2>
                        <span class="detail"><i:inline key=".importHardwareTableDescription"/></span>
                    </div>
                </div>
                <table class="resultsTable bulkImport">
                    <thead>
                        <tr>
                            <th><i:inline key=".hardwareColumnHeader"/></th>
                            <th><i:inline key=".hardwareColumnDescription"/></th>
                        </tr>
                    </thead>
                    <tfoot></tfoot>
                    <tbody>
                    <c:forEach var="field" items="${hardwareFields}">
                        <tr>
                            <td class="fwb">${field.name} <sup>${field.legendKey}</sup></td>
                            <td><i:inline key=".hardwareField.description.${field}"/></td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
                <div class="box detail" style="background-color:#CDCDCD;padding:10px;">
                    <table>
                        <tr>
                            <td><i:inline key=".hardwareFieldLegend.a.label"/></td>
                            <td><i:inline key=".hardwareFieldLegend.a.text"/></td>
                        </tr>
                        <tr>
                            <td><i:inline key=".hardwareFieldLegend.b.label"/></td>
                            <td><i:inline key=".hardwareFieldLegend.b.text"/></td>
                        </tr>
                        <tr>
                            <td><i:inline key=".hardwareFieldLegend.c.label"/></td>
                            <td><i:inline key=".hardwareFieldLegend.c.text"/></td>
                        </tr>
                        <tr>
                            <td><i:inline key=".hardwareFieldLegend.d.label"/></td>
                            <td><i:inline key=".hardwareFieldLegend.d.text"/></td>
                        </tr>
                        <tr>
                            <td><i:inline key=".hardwareFieldLegend.e.label"/></td>
                            <td><i:inline key=".hardwareFieldLegend.e.text"/></td>
                        </tr>
                    </table>
                </div>
            </cti:dataGridCell>
        </cti:dataGrid>
    
    </tags:boxContainer2>

</cti:standardPage>