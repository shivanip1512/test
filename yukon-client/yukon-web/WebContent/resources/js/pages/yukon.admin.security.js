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
            
            $(document).on('yukon:admin:security:generateEcobeeKey', function(event) {
                $.ajax({ 
                    url: "generateEcobeeKey", 
                    type: "GET",
                }).done(function(data) {
                    $('.js-ecobee-key-generated').hide();
                    $('.js-ecobee-key-not-generated').hide();
                    if (data.ecobeeKeyGeneratedDateTime) {
                        $('.js-ecobee-key-date-time').html(data.ecobeeKeyGeneratedDateTime);
                        $('.js-ecobee-key-generated').show();
                    }
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
            
            _initialized = true;
        }
        
    };
    
    return mod;
})();

$(function () { yukon.admin.security.init(); });