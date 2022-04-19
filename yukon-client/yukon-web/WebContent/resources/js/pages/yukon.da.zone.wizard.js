yukon.namespace('yukon.da.zone.wizard');

/**
 * Module for the zone wizard/
 * @module yukon.da.zone.wizard
 * @requires JQUERY
 * @requires yukon
 */
yukon.da.zone.wizard = (function () {

    'use strict';
    
    var _initialized = false,
    
    _zoneDetailButtons = [
         { 
             text: yg.text.back, 
             click: function () { 
                 submitFormViaAjax('zoneWizardPopup', 'zoneDetailsForm', yukon.url('/capcontrol/ivvc/wizard/wizardParentSelected'));
                 $('#zoneWizardPopup').dialog({
                     width: 'auto',
                     height: 'auto',
                     buttons: _zoneTypeButtons
                 });
             }
         },
         {
             text: yg.text.create,
             'class': "primary",
             click: function () {
                 submitFormViaAjax('zoneWizardPopup', 'zoneDetailsForm', null);
             }
         }
     ],
    
    _zoneTypeButtons = [
         { 
             text: yg.text.back, 
             click: function () { 
                 submitFormViaAjax('zoneWizardPopup', 'zoneTypeForm', yukon.url('/capcontrol/ivvc/wizard/wizardSelectParent'));
                 $('#zoneWizardPopup').dialog({
                     width: 'auto',
                     height: 'auto',
                     buttons: _zoneParentButtons
                 });
             }
         },
         {
             text: yg.text.next,
             'class': "primary",
             click: function () {
                 submitFormViaAjax('zoneWizardPopup', 'zoneTypeForm', null);
                 $('#zoneWizardPopup').dialog({
                     width: '700',
                     height: '700',
                     buttons: _zoneDetailButtons
                 });
             }
         }
     ],
    
    _zoneParentButtons = [
          { 
              text: yg.text.cancel, 
              click: function () { 
                  $(this).dialog("close"); 
              }
          },
          {
              text: yg.text.next,
              'class': "primary",
              click: function () {
                  submitFormViaAjax('zoneWizardPopup', 'zoneParentForm');
                  $('#zoneWizardPopup').dialog({
                      width: 'auto',
                      height: 'auto',
                      buttons: _zoneTypeButtons
                  });
              }
          }
      ],
    
    _zoneEditorButtons = [
          {
              text: yg.text.deleteButton,
              'class': "delete",
               click: function () {
                   var popup = $('#zoneWizardPopup'),
                       confirmText = popup.find('.js-confirm-delete')[0].value;
                   yukon.ui.confirm({
                       confirmText: confirmText,
                       dialog: popup,
                       event: 'yukon:da:zone:delete',
                   });   
              }
          },
         {
             text: yg.text.cancel,
             click: function () {
                 $("#zoneWizardPopup").dialog("close");
             }
         },
         { 
             text: yg.text.save, 
             'class': "primary",
             click: function () { 
                 submitFormViaAjax('zoneWizardPopup', 'zoneDetailsForm', null);
             }
         }

     ];

   var mod = {
              
        showZoneCreationWizard : function (url, title) {
            openSimpleDialog('zoneWizardPopup', url, title, null, 'get', { height: "auto", width: "auto" });
            $('#zoneWizardPopup').dialog({
                buttons: _zoneParentButtons
            });

        },
        
        zoneTypeChange : function(zoneType) {
            if (zoneType == 'SINGLE_PHASE') {
                $('#phaseSelector').removeClass('dn');
            } else {
                $('#phaseSelector').addClass('dn');
            }
        },
        
        buildArgs : function(url) {
            return { 'url' : url, 'requests' : [] };
        },
        
        buildRequest : function(id) {
            return { 'extraParameters' : { 'id' : id } };
        },
        
        addBankHandler : function (selectedPaoInfo, picker) {
            var args = mod.buildArgs(yukon.url('/capcontrol/ivvc/wizard/addCapBank'));
            
            for(var i = 0; i < selectedPaoInfo.length; i++) {
                var request = mod.buildRequest(selectedPaoInfo[i].paoId);
                picker.excludeIds.push(selectedPaoInfo[i].paoId);
                args.requests[i] = request;
            }
            bankTable.addItems(args);
            picker.clearEntireSelection.call(picker);
        },
        
        addPointHandler : function(selectedPointInfo, picker) {
            var args = mod.buildArgs(yukon.url('/capcontrol/ivvc/wizard/addVoltagePoint')),
                zoneType = $('#zoneType').val(),
                regulatorPhase = $('#regulatorPhase').val(),
                subBusId = $('#selectedBusId').val();
            for(var i = 0; i < selectedPointInfo.length; i++) {
                var request = mod.buildRequest(selectedPointInfo[i].pointId);
                request.extraParameters.subBusId = subBusId;
                if (zoneType != 'THREE_PHASE' && regulatorPhase != null) {
                    request.extraParameters.phase = regulatorPhase;
                }
                picker.excludeIds.push(selectedPointInfo[i].pointId);
                args.requests[i] = request;
            }
            pointTable.addItems(args);
            picker.clearEntireSelection.call(picker);
        },

        updateRegPickerExcludes : function(selectedItems, picker) {
            var zoneId = $('#zoneId').val();
            var phaseAPicker = yukon.pickers['voltageThreePhaseRegulatorPicker' + zoneId + 'A'];
            var phaseBPicker = yukon.pickers['voltageThreePhaseRegulatorPicker' + zoneId + 'B'];
            var phaseCPicker = yukon.pickers['voltageThreePhaseRegulatorPicker' + zoneId + 'C'];
            if (picker != phaseAPicker) {
                if (phaseAPicker.getSelected.call(phaseAPicker) == selectedItems[0].paoId) {
                    phaseAPicker.clearSelected.call(phaseAPicker);
                }
            }
            if (picker != phaseBPicker) {
                if (phaseBPicker.getSelected.call(phaseBPicker) == selectedItems[0].paoId) {
                    phaseBPicker.clearSelected.call(phaseBPicker);
                }
            }
            if (picker != phaseCPicker) {
                if (phaseCPicker.getSelected.call(phaseCPicker) == selectedItems[0].paoId) {
                    phaseCPicker.clearSelected.call(phaseCPicker);
                }
            }
        },

        /** Initialize this module. */
        init : function () {

            if (_initialized)
                return;
        
            $(document).on('click', '.js-zone-editor', function () {
                var info = $('#zone-editor-info'),
                    url = info.data('editorUrl'),
                    title = info.data('editorTitle');
                openSimpleDialog('zoneWizardPopup', url, title, null, 'get');
                $('#zoneWizardPopup').dialog({
                    height: 'auto',
                    width: 'auto',
                    buttons: _zoneEditorButtons
                });
            });

            _initialized = true;
            
            $(document).on('yukon:da:zone:delete', function (ev) {
                var zoneId = $('#zoneId').val();
                window.location.href = yukon.url('/capcontrol/ivvc/wizard/deleteZone?zoneId=' + zoneId);
            });
        }

    };

    return mod;
})();

$(function () {
    yukon.da.zone.wizard.init();
});