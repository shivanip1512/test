yukon.namespace('yukon.ami');
yukon.namespace('yukon.ami.meter');
yukon.namespace('yukon.ami.meter.events');
yukon.namespace('yukon.ami.meter.events.report');

yukon.ami.meter.events.report = (function () {

    var mod,
        eventTypesTreeInitialized = false,
        titlesToIgnore = [],
        _modelData;

    function _populateTreeNodes(allAttributesMap, eventNode) {
        var nodes = [],
            attributeMap = eventNode.attributeMap;
        for (var i = 0; i < eventNode.attributes.length; i++) {
            var attribute = attributeMap[eventNode.attributes[i]]; // matches java enum
            var selected = allAttributesMap[attribute];
            if (typeof(selected) != 'undefined') {
                nodes.push({title: eventNode.attributes[i], select: selected, a : 1, attribute: attribute});
            }
        }
        return nodes;
    }

    function _ignoreTitle(title) {
        for (var i = 0; i < titlesToIgnore.length; i++) {
            if (title === titlesToIgnore[i]) {
                return true;
            };
        }
        return false;
    }

    function _initEventTypesTree() {
        if (eventTypesTreeInitialized === true) {
            return;
        }

        var allEventsMap = _modelData.meterEventTypesMap,
            generalNodes = _populateTreeNodes(allEventsMap, _modelData.generalEvents),
            hardwareNodes = _populateTreeNodes(allEventsMap, _modelData.hardwareEvents),
            tamperNodes = _populateTreeNodes(allEventsMap, _modelData.tamperEvents),
            outageNodes = _populateTreeNodes(allEventsMap, _modelData.outageEvents),
            meteringNodes = _populateTreeNodes(allEventsMap, _modelData.meteringEvents);

        titlesToIgnore.push(_modelData.allTitle);
        titlesToIgnore.push(_modelData.generalTitle);
        titlesToIgnore.push(_modelData.hardwareTitle);
        titlesToIgnore.push(_modelData.tamperTitle);
        titlesToIgnore.push(_modelData.outageTitle);
        titlesToIgnore.push(_modelData.meteringTitle);

        var treeChildrenGroups = [];
        if (generalNodes.length > 0) {
            treeChildrenGroups.push({title: _modelData.generalTitle, isFolder: true, children: generalNodes});
        }
        if (hardwareNodes.length > 0) {
            treeChildrenGroups.push({title: _modelData.hardwareTitle, isFolder: true, children: hardwareNodes});
        }
        if (tamperNodes.length > 0) {
            treeChildrenGroups.push({title: _modelData.tamperTitle, isFolder: true, children: tamperNodes});
        }
        if (outageNodes.length > 0) {
            treeChildrenGroups.push({title: _modelData.outageTitle, isFolder: true, children: outageNodes});
        }
        if (meteringNodes.length > 0) {
            treeChildrenGroups.push({title: _modelData.meteringTitle, isFolder: true, children: meteringNodes});
        }

        var treeChildren = [{title: _modelData.allTitle, isFolder: true, expand: true, children: treeChildrenGroups}];
        jQuery("#eventTree").dynatree({
            checkbox: true,
            selectMode: 3,
            children: treeChildren
        });

        eventTypesTreeInitialized = true;
    }

    function _updateEventTypes() {
        var numSelected = 0;
        jQuery("#eventTypeInputs").empty();
        jQuery("#eventTree").dynatree("getSelectedNodes").each(function(node) {
            if (_ignoreTitle(node.data.title) === false) {
                jQuery('<input>').attr({
                    type: 'hidden',
                    name: 'attributes',
                    value: node.data.attribute
                }).appendTo('#eventTypeInputs');
                numSelected++;
            }
        });
        jQuery('#numEventTypes').html(numSelected);
        jQuery('#filterPopupEventTypes').dialog('close');
    }
    
    function _submitSchedule() {
        var formSerialized = jQuery("#filterForm").serialize();
        formSerialized += "&" + jQuery("#scheduleForm").serialize();
        jQuery.post("saveScheduledMeterEventJob?" + formSerialized, function (data) {
            var response = jQuery(data),
                scheduleModelData = JSON.parse(response.filter("#scheduleModelData").html());
            if (scheduleModelData.success) {
                jQuery("#scheduleDialog").dialog('close');
                _reloadScheduledJobsTable(response.filter("#flashScopeMsg"));
            } else {
                jQuery("#scheduleDialog").html(response);
                yukon.tag.scheduledFileExportInputs.initializeFields();
            }
        });
    }

    function _reloadScheduledJobsTable(flashScope) {
        jQuery("#scheduledJobsTable").load("scheduledJobsTable", function () {
            jQuery("#scheduledJobsTableFlashScope").hide().html(flashScope).fadeIn(150);
        });
    }

    function _deleteJob(jobId) {
        var data = {jobId : jobId};

        jQuery.post("delete", data, 'json').done(function (data) {
            var flashScope = jQuery("<div>").attr('class','user-message success').html(data.successMsg);
            _reloadScheduledJobsTable(flashScope);
            jQuery("#scheduledJobsTableFlashScope").html(flashScope);
        });
    }
    
    function _createScheduleReportDialog(titleHtml) {
        jQuery("#scheduleDialog").dialog({'modal': true, 'position' : 'center', 'height' : 'auto',
            'width' : 'auto', 
            create: function () {
                jQuery(this).siblings().find(".ui-dialog-title").html(titleHtml); 
        }});
    }
    
    function _updateMeterEventsTable() {
        var formSerialized = jQuery("#filterForm").serialize(),
        formUrl = jQuery("#filterForm").attr('action');
        jQuery.post(formUrl, formSerialized, function (data) {
            jQuery("#meterEventsTable").html(data);
            yukon.ui.unbusy(jQuery("#updateMeterEventsBtn"));
        });
    }

    mod = {
        updateEventTypes: function() {
            _updateEventTypes();
            _updateMeterEventsTable();
        },

        init: function() {
            _modelData = JSON.parse(jQuery("#modelData").html());
            jQuery("#updateMeterEventsBtn").click(_updateMeterEventsTable);

            jQuery("#scheduleMeterEventsBtn").click(function() {
                var formSerialized = jQuery("#filterForm").serialize();
                jQuery.post("scheduledMeterEventsDialog", formSerialized, function (data) {
                    jQuery("#scheduleDialog").html(data);
                    yukon.tag.scheduledFileExportInputs.initializeFields();
                });
                if (jQuery("scheduleDialog").hasClass("ui-dialog-content")) {
                    jQuery("#scheduleDialog").dialog('destroy');
                }
                _createScheduleReportDialog(_modelData.newSchedulePopupTitle);
            });

            jQuery("#eventTypesFilterOkBtn").click(function () {
                _updateEventTypes();
                _updateMeterEventsTable();
            });

            jQuery("#exportCsvBtn").click(function () {
                var _meterEventsTableModel = JSON.parse(jQuery("#meterEventsTableModelData").html()),
                    downloadForm = jQuery("#downloadFormContents").empty();
                jQuery("#filterForm").find(":input[name]").each(function() {
                    var self = jQuery(this);
                    downloadForm.append(jQuery("<input>")
                                .attr("name", self.attr("name"))
                                .attr("type", self.attr("type"))
                                .val(self.val()));
                });
                downloadForm.append(jQuery("<input>").attr("name", "sort").val(_meterEventsTableModel.sort));
                downloadForm.append(jQuery("<input>").attr("name", "descending").val(_meterEventsTableModel.descending));
                jQuery("#downloadForm").submit();
            });

            jQuery(document).on('click', '#scheduledJobDialogSchedule', _submitSchedule);

            jQuery(document).on('click', '#scheduledJobDialogCancel', function() {
                jQuery('#scheduleDialog').dialog('close');
            });

            jQuery(document).on('click', '.f-delete-schedule-item', function (event) {
                event.preventDefault();
                var jobId = jQuery(this).closest("[data-job-id]").data("job-id");
                jQuery("#scheduledJobsConfirmDeleteDialog").dialog({'title' : _modelData.confirmScheduleDeletion,
                    'modal': true, 'position' : 'center', 'height' : 'auto', 'width' : 'auto',
                    'buttons': 
                        [{
                             text: _modelData.cancelBtnLbl, 
                             click: function() {jQuery(this).dialog('close');}
                        }, {
                             text: _modelData.okBtnLbl, 
                             click: function() {
                                 jQuery(this).dialog('close');
                                 _deleteJob(jobId);
                             },
                             'class': 'primary action'
                        }]
                     });
            });

            if (_modelData.openScheduleDialog) {
                _createScheduleReportDialog(_modelData.schedulePopupTitle);
            }

            _initEventTypesTree();
            _updateEventTypes();
        }
    };
    return mod;
}());

jQuery(function () {
    yukon.ami.meter.events.report.init();
});