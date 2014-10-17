<%@ page trimDirectiveWhitespaces="true"%>

<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<cti:standardPage module="dev" page="dbChange">
   <script>
    yukon.namespace('yukon.dev.dbChange');

    yukon.dev.dbChange = (function() {
        var _initialized = false,
        _shortenString = function(string) {
            if (string.length > 20) {
                return string.substring(0, 20) + '...';
            }
            return string;
        },
        
        _createPreviouslySentDiv = function(previouslySent) {
            var resultClass = previouslySent.error ? 'error' : 'success';
            var newRow = $('#previouslySentTemplate').clone().removeAttr("id");
            newRow.find('[data-result]').text(previouslySent.resultMessage).addClass(resultClass);
            newRow.find('[data-num-times]').text(previouslySent.numTimes);
            newRow.find('[data-itemId]').html(previouslySent.itemId);
            newRow.find('[data-database]').text(_shortenString(previouslySent.database));
            newRow.find('[data-category]').text(_shortenString(previouslySent.category));
            newRow.find('[data-type]').text(previouslySent.type);
            newRow.find('[data-resubmit-button]').data({"itemId" : previouslySent.itemId,
                                                        "database" : previouslySent.database,
                                                        "category" : previouslySent.category,
                                                        "type" : previouslySent.type});
            newRow.show();
            return newRow;
        },
        
        _updatePreviouslySent = function() {
            var previouslySentDiv = $('#previouslySent').empty();
            var previouslySent = JSON.parse(localStorage['previouslySent']);
            var numberPreviouslySent = previouslySent.length;
            for (var i = 0; i < numberPreviouslySent; i++) {
                previouslySentDiv.append(_createPreviouslySentDiv(previouslySent[i]));
            }
        },
        
        _showTempError = function(message, type) {
            var newMsgDiv = $('<div/>').addMessage({message: message, messageClass:'error'}).show();
            $('#errorMessage').html(newMsgDiv);
            setTimeout(function() {
                newMsgDiv.remove();
            }, 10000);
        };

        _submitButtonClick = function() {
            var form = $('#dbChangeForm');
            var sendData = {
                    itemId : form.find('[name="itemId"]').val(),
                    database : form.find('[name="databaseField"]').val(),
                    type : form.find('[name="type"]').val(),
                    category : form.find('[name="categoryField"]').val()
            };
            _submitDbChangeMessage(sendData);
        },

        _isEqual = function(sendDataA, sendDataB) {
            return sendDataA.resultMessage == sendDataB.resultMessage &&
            sendDataA.itemId == sendDataB.itemId &&
            sendDataA.database == sendDataB.database &&
            sendDataA.category == sendDataB.category &&
            sendDataA.type == sendDataB.type;
        },

        _submitDbChangeMessage = function(sendData) {
            $.ajax({
                url: yukon.url('do-db-change'),
                type: 'post',
                data: sendData
            }).done(function(data) {
                if (data.error) {
                    sendData.error = true;
                    sendData.resultMessage = data.errorMessage;
                } else {
                    sendData.resultMessage = 'Success';
                }
            }).fail(function(data) {
                sendData.error = true;
                sendData.resultMessage = data.statusText;
                _showTempError('Failed to submit DbChangeMessage. Due to ' + data.statusText + '.');
            }).always(function() {
                var previouslySent = JSON.parse(localStorage['previouslySent']);
                if (previouslySent[0] && _isEqual(sendData, previouslySent[0])) {
                    previouslySent[0].numTimes++;
                } else {
                    sendData.numTimes = 1;
                    previouslySent.unshift(sendData);
                    previouslySent = previouslySent.slice(0, 10);
                }
                localStorage.setItem('previouslySent', JSON.stringify(previouslySent));
                _updatePreviouslySent();
            });
        },
        
        mod = {
            init : function() {
                if (_initialized) return;

                if (localStorage.getItem('previouslySent') == null) {
                    localStorage.setItem('previouslySent', "[]");
                }

                $('#submit-button').click(_submitButtonClick);
                $('#dbChangeForm').change(function(event) {
                    var input = $(event.target);
                    yukon.cookie.set('devDbChange', input.attr('name'), input.val());
                });
                $(document).on('click', '.js-re-submit', function() {
                    var sendData = {
                            itemId : $(this).data('itemId'),
                            database : $(this).data('database'),
                            type : $(this).data('type'),
                            category : $(this).data('category')
                    };
                    _submitDbChangeMessage(sendData);
                });
                _updatePreviouslySent();
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
    
    <div id="errorMessage"></div>
    <tags:sectionContainer title="Submit DbChange Message">
        <form id='dbChangeForm'>
            <tags:nameValueContainer2>
                <tags:nameValue2 argument="DbChangeType" label="modules.dev.setupDatabase.setupDevDatabase.generic">
                    <select name="type">
                        <c:forEach var="entry" items="${dbChangeTypes}">
                            <option value="${entry}" ${type eq entry ? 'selected' : ''}>${entry}</option>
                        </c:forEach>
                    </select>
                </tags:nameValue2>
                <tags:nameValue2 argument="Item Id" label="modules.dev.setupDatabase.setupDevDatabase.generic">
                    <input type="text" size="6" name="itemId" value="${itemId}" />
                </tags:nameValue2>
                <tags:nameValue2 argument="Database" label="modules.dev.setupDatabase.setupDevDatabase.generic">
                    <select name="databaseField">
                        <c:forEach var="entry" items="${databaseFields}">
                            <option value="${entry.key}" ${database eq entry.key ? 'selected' : ''}>${fn:replace(entry.key, "CHANGE_", "")}</option>
                        </c:forEach>
                    </select>
                </tags:nameValue2>
                <tags:nameValue2 argument="Category" label="modules.dev.setupDatabase.setupDevDatabase.generic">
                    <select name="categoryField">
                        <c:forEach var="entry" items="${categoryFields}">
                            <option value="${entry.key}" ${category eq entry.key ? 'selected' : ''}>${fn:replace(entry.key, "CAT_", "")}</option>
                        </c:forEach>
                    </select>
                </tags:nameValue2>
            </tags:nameValueContainer2>
        </form>
        <div class="page-action-area">
            <cti:button id="submit-button" label="Submit" classes="primary action"/>
         </div>
     </tags:sectionContainer>
    <tags:sectionContainer title="Previously Sent">
        <div id="previouslySent" class="scroll-lg">
            
        </div>
    </tags:sectionContainer>
    <div id="previouslySentTemplate" class="column-8-16 dn">
        <div class="column one">
            <div class="column-12-12">
                <div class="column one">
                    Type: <span data-type>ADD</span>
                </div>
                <div class="column two nogutter">
                    Item: <span data-itemId>1232</span>
                </div>
            </div>
        </div>
        <div class="column two nogutter">
            <div class="column-12-12">
                <div class="column one">
                    Database: <span data-database>CHANGE_U_USER_ID</span>
                </div>
                <div class="column two nogutter">
                    Category: <span data-category>CHANGE_U_USER_ID</span>
                </div>
            </div>
        </div>
        <div class="stacked clearfix">
            <span class="fl">Result: <span data-result>Success</Span> x<span data-num-times></span></span>
            <i data-resubmit-button class="icon icon-arrow-rotate-clockwise js-re-submit"></i>
        </div>
    </div>
</cti:standardPage>