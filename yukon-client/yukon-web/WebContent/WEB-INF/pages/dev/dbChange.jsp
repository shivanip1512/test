<%@ page trimDirectiveWhitespaces="true"%>

<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<cti:standardPage module="dev" page="dbChange">
   <script>
    yukon.namespace('yukon.dev.dbChange');

    yukon.dev.dbChange = (function() {
        var _initialized = false,
        _showTempMessage = function(message, type) {
            $('#submitStatus').addMessage({message: message, messageClass:type}).show();
            setTimeout(function() {
                $('#submitStatus').slideUp(150);
            }, 3000);
        }
        _submitButtonClick = function() {
            var formData = $('#dbChangeForm').serialize();
            $.ajax({
                url: yukon.url('do-db-change'),
                type: 'post',
                data: formData
            }).done(function(data) {
                _showTempMessage('Successfully submitted DbChangeMsg', 'success');
            }).fail(function(data) {
                _showTempMessage('Failed to submit. DbChangeMessage', 'error');
            });
        },
        
        mod = {
            init : function() {
                if (_initialized) return;
                
                $('#submit-button').click(_submitButtonClick);
                $('#dbChangeForm').change(function(event) {
                    var input = $(event.target);
                    yukon.cookie.set('devDbChange', input.attr('name'), input.val());
                });
                _initialized = true;
            }
        };
        return mod;
    }());

    $(function() {
        yukon.dev.dbChange.init();
    });
    </script>
    <cti:yukonCookie var="itemId" scope="devDbChange" id="itemId" defaultValue="0"/>
    <cti:yukonCookie var="database" scope="devDbChange" id="databaseField"/>
    <cti:yukonCookie var="category" scope="devDbChange" id="categoryField"/>
    <cti:yukonCookie var="type" scope="devDbChange" id="type"/>
    <div id="submitStatus"></div>
    <form id='dbChangeForm'>
        <tags:nameValueContainer2>
            <tags:nameValue2 argument="Item Id" label="modules.dev.setupDatabase.setupDevDatabase.generic">
                <input type="text" size="6" name="itemId" value="${itemId}" />
            </tags:nameValue2>
            <tags:nameValue2 argument="Database" label="modules.dev.setupDatabase.setupDevDatabase.generic">
                <select name="databaseField">
                    <c:forEach var="entry" items="${databaseFields}">
                        <option value="${entry.key}" ${database eq entry.key ? 'selected' : ''}>${entry.key}</option>
                    </c:forEach>
                </select>
            </tags:nameValue2>
            <tags:nameValue2 argument="Category" label="modules.dev.setupDatabase.setupDevDatabase.generic">
                <select name="categoryField">
                    <c:forEach var="entry" items="${categoryFields}">
                        <option value="${entry.key}" ${category eq entry.key ? 'selected' : ''}>${entry.key}</option>
                    </c:forEach>
                </select>
            </tags:nameValue2>
            <tags:nameValue2 argument="DbChangeType" label="modules.dev.setupDatabase.setupDevDatabase.generic">
                <select name="type">
                    <c:forEach var="entry" items="${dbChangeTypes}">
                        <option value="${entry}" ${type eq entry ? 'selected' : ''}>${entry}</option>
                    </c:forEach>
                </select>
            </tags:nameValue2>
        </tags:nameValueContainer2>
    </form>
    <div class="page-action-area">
        <cti:button id="submit-button" label="Submit" classes="primary action"/>
    </div>
</cti:standardPage>