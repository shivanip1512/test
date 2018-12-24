<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>

<cti:standardPage module="operator" page="accountImport" smartNotificationsEvent="ASSET_IMPORT">

    <cti:checkEnergyCompanyOperator showError="true">
        <cti:checkRolesAndProperties value="OPERATOR_IMPORT_CUSTOMER_ACCOUNT"/>

        <div class="column-12-12">
            <div class="column one">
                <cti:url var="importUrl" value="/stars/operator/account/uploadImportFiles"/>
                <form:form id="importForm" modelAttribute="accountImportData" action="${importUrl}" enctype="multipart/form-data">
                    <cti:csrfToken/>
                    <%-- note --%>
                    <div class="stacked">
                        <h3><i:inline key=".noteLabel"/></h3>
                        <span class="notes"><i:inline key=".noteText"/></span>
                    </div>
                    
                    <tags:nameValueContainer2>
                        <tags:nameValue2 nameKey=".accountImportFile" >
                            <tags:file name="accountImportFile"/>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".hardwareImportFile">
                      		<tags:file name="hardwareImportFile"/>
                        </tags:nameValue2>
                        <tags:inputNameValue nameKey=".email" path="email" size="35"/>
                    </tags:nameValueContainer2>
                    
                    <div class="action-area stacked">
                        <cti:button nameKey="prescan" type="submit" classes="js-blocker action primary"/>
                    </div>
                </form:form>
                
            </div>
            
            <div class="column two nogutter">
                
                <%-- instructions --%>
                <h3><i:inline key=".instructionsLabel"/></h3>
                <ul class="disc stacked">
                    <li><i:inline key=".instructionsText1"/></li>
                    <li><i:inline key=".instructionsText2"/></li>
                    <li><i:inline key=".instructionsText3"/></li>
                </ul>
                
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".sampleFiles" nameClass="wsnw">
                        <a href="<cti:url value="/WebConfig/custom/sample_bulk_files/Sample_Account_Import_File.csv"/>"><i:inline key=".sampleAccountFile"/></a>, 
                        <a href="<cti:url value="/WebConfig/custom/sample_bulk_files/Sample_Hardware_Import_File.csv"/>"><i:inline key=".sampleHardwareFile"/></a>,
                        <a href="<cti:url value="/WebConfig/custom/sample_bulk_files/Sample_Account_And_Hardware_Import_File.csv"/>"><i:inline key=".sampleAccountAndHardwareFile"/></a> 
                    </tags:nameValue2>
                </tags:nameValueContainer2>
                
            </div>
        </div>
            
        <div class="column-12-12">
            <div class="column one">
                <div class="box" style="background-color:#CDCDCD;padding:10px;">
                    <div class="fl"><cti:icon icon="icon-shape-handles"/></div>
                    <div class="fl" style="margin-left:20px;">
                        <h2><i:inline key=".importAccountTableHeader"/></h2>
                        <span class="detail"><i:inline key=".importAccountTableDescription"/></span>
                    </div>
                </div>
                <table class="results-table bulkImport">
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
                
            </div>
            
            <div class="column two nogutter">
                <div class="box" style="background-color:#CDCDCD;padding:10px;">
                    <div class="fl"><cti:icon icon="icon-shape-handles"/></div>
                    <div class="fl" style="margin-left:20px;">
                        <h2><i:inline key=".importHardwareTableHeader"/></h2>
                        <span class="detail"><i:inline key=".importHardwareTableDescription"/></span>
                    </div>
                </div>
                <table class="results-table bulkImport">
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
            </div>
        </div>

        <div id="page-actions" class="dn">
            <!-- Create -->
            <cti:url var="createUrl" value="/stars/scheduledDataImport/create"/>
            <cm:dropdownOption key=".createScheduledImport" 
                               id="create-option" href="${createUrl}"/>
            <li class="divider"></li>

            <!--  View -->
            <cti:url var="viewUrl" value="/stars/scheduledDataImport/list"/>
            <cm:dropdownOption key=".viewScheduledImport" 
                               id="view-option" href="${viewUrl}"/>
            <li class="divider"></li>

            <!--  Recent Manual Imports -->
            <cm:dropdownOption key=".recentManualImport" 
                               id="recent-manual-imports" href="imports"/>

            <!--  Recent Manual Imports Results -->
            <cti:url var="recentManualResultsLink" value="/bulk/recentResults"/>
            <cm:dropdownOption key=".recentManualImportResults" 
                               id="recent-manual-results" href="${recentManualResultsLink}"/>
        </div>

  </cti:checkEnergyCompanyOperator>
</cti:standardPage>