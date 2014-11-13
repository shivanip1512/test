<%@ page trimDirectiveWhitespaces="true"%>

<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<cti:standardPage module="dev" page="createImportFiles">
   <script>
    yukon.namespace('yukon.dev.createImportFiles');

    yukon.dev.createImportFiles = (function() {
        var _initialized = false,
        energyCompanyData,
        _energyCompanyChanged = function() {
            var energyCompany =  energyCompanyData[$('#energyCompanySelector').val()];
            if (energyCompany.userGroups.length == 0) {
                $('#accountsSection').find('input, select').prop('disabled', true);
            } else {
                $('#accountsSection').find('input, select').prop('disabled', false);
                var numItems = energyCompany.userGroups.length;
                var usergroupSelector = $('#usergroupSelector').empty();
                for (var i=0; i<numItems; i++) {
                    var userGroup = energyCompany.userGroups[i];
                    usergroupSelector.append('<option value="' + userGroup+ '">'+userGroup+'</option>');
                }
            }
            if ($.isEmptyObject(energyCompany.applianceCategories)) {
                $('#hardwareSection').find('input, select').prop('disabled', true);
            } else {
                $('#hardwareSection').find('input, select').prop('disabled', false);

                var appCatSelector = $('#appCatSelector').empty();
                for (var appCatId in energyCompany.applianceCategories) {
                    var applianceCategory = energyCompany.applianceCategories[appCatId];
                    appCatSelector.append('<option value="' + appCatId+ '">'+applianceCategory.name+'</option>');
                }
            }
            var deviceTypeSelector = $('#deviceTypeSelector').empty();
            var numDeviceTypes = energyCompany.deviceTypes.length;
            for (var i=0; i<numDeviceTypes; i++) {
                var deviceType = energyCompany.deviceTypes[i];
                deviceTypeSelector.append('<option value="' + deviceType+ '">'+deviceType+'</option>');
            }
            _applianceCategoryChanged();
        },
        _applianceCategoryChanged = function() {
            var energyCompany =  energyCompanyData[$('#energyCompanySelector').val()];
            var applianceCategory = energyCompany.applianceCategories[$('#appCatSelector').val()];
            
            if (!applianceCategory || $.isEmptyObject(applianceCategory.programs)) {
                $('#programSelector').prop('disabled', true);
            } else {
                $('#programSelector').prop('disabled', false);
                var programSelector = $('#programSelector').empty();
                for (var programId in applianceCategory.programs) {
                    var program = applianceCategory.programs[programId];
                    programSelector.append('<option value="' + programId+ '">'+program.name+'</option>');
                }
            }
            _programChanged();
        },
        _programChanged = function() {
            var energyCompany =  energyCompanyData[$('#energyCompanySelector').val()];
            var applianceCategory = energyCompany.applianceCategories[$('#appCatSelector').val()];
            var program = applianceCategory ? applianceCategory.programs[$('#programSelector').val()] : undefined;

            if (!program || $.isEmptyObject(program.loadGroups)) {
                $('#loadGroupSelector, #deviceTypeSelector').prop('disabled', true);
            } else {
                $('#loadGroupSelector, #deviceTypeSelector').prop('disabled', false);
                var loadGroupSelector = $('#loadGroupSelector').empty();
                for (var loadGroupIndex in program.loadGroups) {
                    var loadGroup = program.loadGroups[loadGroupIndex];
                    loadGroupSelector.append('<option value="' + loadGroup+ '">'+loadGroup+'</option>');
                }
            }
            _deviceTypeChanged();
        },
        _deviceTypeChanged = function() {
            if (!$('#deviceTypeSelector').val()) {
                $('#download-button').prop('disabled', true);
            } else {
                $('#download-button').prop('disabled', false);
            }
        },
        _download = function() {
            var createFilesForm = $('#createFilesForm').serialize();
            window.location = 'download-hardware-file?' + createFilesForm;
            setTimeout(function() {
                window.location = 'download-account-file?' + createFilesForm;
            }, 500);
        },
        mod = {
            init : function() {
                if (_initialized) return;
                energyCompanyData = yukon.fromJson('#energyCompanyData');

                $('#energyCompanySelector').change(_energyCompanyChanged);
                $('#appCatSelector').change(_applianceCategoryChanged);
                $('#programSelector').change(_programChanged);
                $('#deviceTypeSelector').change(_deviceTypeChanged);
                $('#download-button').click(_download);

                _energyCompanyChanged();
                _initialized = true;
            }
        };
        return mod;
    }());

    $(function() {
        yukon.dev.createImportFiles.init();
    });
    </script>
    <cti:toJson id="energyCompanyData" object="${energyCompanyData}"/>

    <tags:sectionContainer title="Create Import Files">
        <form id='createFilesForm'>
            <tags:nameValueContainer2>
                 <tags:nameValue2 argument="Energy Company" label="modules.dev.setupDatabase.setupDevDatabase.generic">
                    <select id="energyCompanySelector" name="ecId">
                        <c:forEach var="energyCompany" items="${energyCompanies}">
                            <option value="${energyCompany.id}">${energyCompany.name}</option>
                        </c:forEach>
                    </select>
                </tags:nameValue2>
            </tags:nameValueContainer2>

            <tags:nameValueContainer2 id="accountsSection">
                <h3>Accounts</h3>
                <tags:nameValue2 argument="Number of Accounts" label="modules.dev.setupDatabase.setupDevDatabase.generic">
                    <input type="text" name="numberOfAccounts" value="1000" />
                </tags:nameValue2>
                 <tags:nameValue2 argument="Account Number Start" label="modules.dev.setupDatabase.setupDevDatabase.generic">
                    <input type="text" name="accountNumberStart" value="${nextAccountNumber}" />
                </tags:nameValue2>
               <tags:nameValue2 argument="Usergroup" label="modules.dev.setupDatabase.setupDevDatabase.generic">
                    <select id="usergroupSelector" name="usergroup"><option>No Residential User Groups</option></select>
                </tags:nameValue2>
            </tags:nameValueContainer2>

            <tags:nameValueContainer2 id="hardwareSection">
                <h3>Hardware</h3>
                <tags:nameValue2 argument="Number of Hardware" label="modules.dev.setupDatabase.setupDevDatabase.generic">
                    <input type="text" name="numberOfHardware" value="5" />
                </tags:nameValue2>
                 <tags:nameValue2 argument="Serial Number Start" label="modules.dev.setupDatabase.setupDevDatabase.generic">
                    <input type="text" name="serialNumberStart" value="${nextSerialNumber}" />
                </tags:nameValue2>
                <tags:nameValue2 argument="Appliance Category" label="modules.dev.setupDatabase.setupDevDatabase.generic">
                    <select id="appCatSelector" name="appCatId"><option>No Appliance Categories</option></select>
                </tags:nameValue2>
                <tags:nameValue2 argument="Program" label="modules.dev.setupDatabase.setupDevDatabase.generic">
                    <select id="programSelector" name="programId"><option>No Programs</option></select>
                </tags:nameValue2>
                <tags:nameValue2 argument="Load Group" label="modules.dev.setupDatabase.setupDevDatabase.generic">
                    <select id="loadGroupSelector" name="loadGroup"><option>No Load Groups</option></select>
                </tags:nameValue2>
                <tags:nameValue2 argument="Device Type" label="modules.dev.setupDatabase.setupDevDatabase.generic">
                    <select size="8" multiple id="deviceTypeSelector" name="deviceTypes"><option>No Device Types</option></select>
                </tags:nameValue2>
            </tags:nameValueContainer2>
            <div class="page-action-area">
                <cti:button id="download-button" label="Download" classes="primary action"/>
            </div>
        </form>
     </tags:sectionContainer>
</cti:standardPage>