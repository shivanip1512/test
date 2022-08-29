yukon.namespace('yukon.admin.security');

/**
 * Module to handle the behavior on the Admin Security page.
 * @module yukon.admin.security
 * @requires JQUERY
 * @requires yukon
 */
yukon.admin.security = (function () {
    
    'use strict';
    
    var
    _initialized = false,
    
    _loadPublicKey = function(generateNewKey) {
        $('#honeywellPublicKeyDownloadStatus').hide();
        yukon.ui.blockPage();
        $.ajax({ 
            url: "getPublicKey", 
            type: "POST",
            data: {"generateNewKey": generateNewKey},
            cache: false,
            dataType: "json" 
        }).done(function(publicKeyObj) {
            $('.js-no-key-exists').hide();
            $('.js-key-expired').hide();
            $('.js-key-expires').hide();
            $('.js-key-generation-failed').hide();
            if(!publicKeyObj.doesExist) {
                // No public Key exists
                $("#publicKeyText").hide();
                $('.js-no-key-exists').show();
                $("#publicKeyStatus").show('fade',{},200);
            } else if (publicKeyObj.isExpired) {
                // A key exists but it's expired
                $("#publicKeyText").hide();
                $('.js-key-expired').show();
                $("#publicKeyStatus").show('fade',{},200);
            } else {
                // A valid key is found
                $('.js-expiration').html(" " + publicKeyObj.expiration);
                $('.js-key-expires').show();
                $("#publicKeyTextArea").val(publicKeyObj.publicKey);
                $("#publicKeyText").show('fade',{},200);
            }
            yukon.ui.unblockPage();
        }).fail(function() {
            $("#publicKeyText").hide();
            $('.js-key-generation-failed').show();
            $("#publicKeyStatus").show('fade',{},200);
            yukon.ui.unblockPage();
        });
    },
    
    _submitForm = function(formId) {
        yukon.ui.blockPage();
        $("#" + formId).submit();
    },
    
    mod = {
        
        /** Initialize this module. */
        init : function () {
            
            if (_initialized) return;
            
            var showDialog = $('.js-show-dialog').val();
            
            if (showDialog === "addKey") {
                $('#honeywellPublicKeyDownloadStatus').hide();
                $('#addNewKeyBtn').trigger($.Event("click")); // Opens up addKey Dialog
            } else if (showDialog === "importKey") {
                $('#importKeyFileBtn').trigger($.Event("click")); // Opens up importKey Dialog
            }
            
            $("#viewPublicKeyBtn").click(function() {
                $('#honeywellPublicKeyDownloadStatus').hide();
                _loadPublicKey(false);
            });
            
            $(document).on("table.routesBoxTable button", "click", function () {
                $('#honeywellPublicKeyDownloadStatus').hide();
            });
            
            $(document).on('generatePublicKey', function(event) {
                _loadPublicKey(true);
            });
            
            $(document).on("yukon.dialog.confirm.ok", function(event) {
                $('#honeywellPublicKeyDownloadStatus').hide();
                $.ajax({ 
                    url: "getHoneywellPublicKey", 
                    type: "GET",
                }).done(function(data) {
                    if ($("#honeywellPublicKeyTextArea").length) {
                        $("#honeywellPublicKeyTextArea").val(data.honeywellPublicKey);
                    } else {
                        location.reload(true);
                    }
                }).fail(function(data) {
                    $('#honeywellPublicKeyStatus').show();
                });
            });
            
            $(document).on('addKeyFormSubmit', function(event) {
                yukon.ui.blockPage();
                $('#encryptionKey').submit();
            });
            
            $(document).on('importKeyFileFormSubmit', function(event) {
                $('#fileImportBindingBean').ajaxSubmit({
                    error: function (xhr, status, error, $form) {
                        $('#importKeyDialog').html(xhr.responseText);
                    }
                });
            });
            
            $(document).on('importHoneywellKeyFileFormSubmit', function(event) {
                yukon.ui.blockPage();
                $('#honeywellFileImportBindingBean').submit();
            });
            
            $(document).on('yukon:admin:security:submitForm', function(event) {
                var formId = $(event.target).data('form-id');
                _submitForm(formId);
            });
            $(document).on('yukon:admin:security:generateEcobeeZeusKey', function(event) {
                yukon.ui.busy($('#generateEcobeeZeusKey'));
                $('#generateEcobeeZeusKeyDialog').dialog('close');
                $.ajax({
                    url : "generateEcobeeZeusKey",
                    type : "GET",
                }).done(function(data) {
                    $('.js-ecobee-zeus-key-generated').hide();
                    $('.js-ecobee-zeus-key-not-generated').hide();
                    if (data.ecobeeKeyZeusGeneratedDateTime) {
                        $('.js-ecobee-zeus-key-date-time').html(data.ecobeeKeyZeusGeneratedDateTime);
                        $('.js-ecobee-zeus-key-generated').show();
                        $('#viewEcobeeZeusKey').prop('disabled', false);
                        $('#registerConfigurationEcobeeZeusKey').prop('disabled', false);
                        $('#checkRegistrationEcobeeZeusKey').prop('disabled', false);
                        $('#registerEcobeeZeusKey').prop('disabled', false);
                    }
                    yukon.ui.unbusy($('#generateEcobeeZeusKey'));
                });
            });
            
            $(document).on('yukon:admin:security:registerEcobeeZeusKey', function(event) {
                yukon.ui.busy($('#registerConfigurationEcobeeZeusKey'));
                $('#registerEcobeeZeusDialog').dialog('close');
                $.ajax({
                    url : "registerEcobeeZeusKey",
                    type : "POST",
                    dataType : "json"
                }).done(function(data) {
                    
                    var messageClass = data.success ? 'success' : 'error';
                    $('.js-ecobee-zeus-key-register-date-time').html(data.ecobeeZeusRegisteredDateTime);
                    $('.js-ecobee-zeus-key-registered').show();
                    $('#ecobee-zeus-js-message').addMessage({
                        message : data.ecobeeZeusRegisteredDateTimeMsg,
                        messageClass : messageClass
                    });
                    
                    yukon.ui.unbusy($('#registerConfigurationEcobeeZeusKey'));
                });
            });
            
            $(document).on('yukon:admin:security:viewEcobeeZeusPublicKey', function(event) {
                $.ajax({
                    url : "viewEcobeeZeusPublicKey",
                    type : "GET",
                    dataType : "json"
                }).done(function(data) {
                    if (data.success) {
                        $('#ecobeeZeuPublicKeyArea').html(data.publicKey);
                    } else {
                        $('#ecobeeZeusErrorMessage').addMessage({
                            message : data.errorMessage,
                            messageClass : 'error'
                        });
                    }
                    
                });
            });
            
            $(document).on('click', '#registerEcobeeZeusKey', function() {
                yukon.ui.busy($('#registerEcobeeZeusKey'));
                $('#js-url-message').hide();
                $('#ecobee-zeus-js-message').addMessage({
                    message : $('.js-ecobee-url').val(),
                    messageClass : 'error'
                });
                
                yukon.ui.unbusy($('#registerEcobeeZeusKey'));
            });
            
            $(document).on('click', '#checkRegistrationEcobeeZeusKey', function() {
                yukon.ui.busy($('#checkRegistrationEcobeeZeusKey'));
                $.ajax({
                    url : "checkRegistrationEcobeeZeusKey",
                    type : "GET",
                }).done(function(data) {
                    if (data.success) {
                        $('#ecobee-zeus-js-message').addMessage({
                            message : data.checkRegistrationMsg,
                            messageClass : 'success'
                        });
                    } else {
                        $('#ecobee-zeus-js-message').addMessage({
                            message : data.checkRegistrationMsg,
                            messageClass : 'error'
                        });
                    }
                    yukon.ui.unbusy($('#checkRegistrationEcobeeZeusKey'));
                    
                });
            });
          
            $(document).on('yukon:admin:security:generateItronKey', function(otherEvent) {
                $.ajax({ 
                    url: "generateItronKey", 
                    type: "GET",
                }).done(function(data) {
                    $('.js-itron-key-generated').hide();
                    $('.js-itron-key-not-generated').hide();
                    if (data.itronKeyGeneratedDateTime) {
                        $('.js-itron-key-date-time').html(data.itronKeyGeneratedDateTime);
                        $('.js-itron-key-generated').show();
                    }
                });
            });
            
            $(document).on('click', '.js-refresh-secret', function() {
				var secretButtons = $('.js-refresh-secret');
				secretButtons.prop('disabled', true);
            	var secretNumber = $(this).data('secretNumber'),
            		form = $('#refreshSecretForm');
            	form.find('#secretNumber').val(secretNumber);
            	form.submit();
            });
            
            _initialized = true;
        }
        
    };
    
    return mod;
})();

$(function () { yukon.admin.security.init(); });