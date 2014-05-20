yukon.namespace('yukon.ami.meterEventsReport');

yukon.ami.meterEventsReport = (function () {

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
                nodes.push({title: eventNode.attributes[i], select: selected, attribute: attribute});
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
        $("#eventTree").dynatree({
            checkbox: true,
            selectMode: 3,
            children: treeChildren
        });

        eventTypesTreeInitialized = true;
    }

    function _updateEventTypes() {
        var numSelected = 0,
            selectedNodes;
        $("#eventTypeInputs").empty();
        selectedNodes = $("#eventTree").dynatree("getSelectedNodes");
        if ('undefined' !== typeof selectedNodes) {
            $(selectedNodes).each(function(index, node) {
                if (_ignoreTitle(node.data.title) === false) {
                    $('<input>').attr({
                        type: 'hidden',
                        name: 'attributes',
                        value: node.data.attribute
                    }).appendTo('#eventTypeInputs');
                    numSelected++;
                }
            });
        }
        $('#numEventTypes').html(numSelected);
        $('#filterPopupEventTypes').dialog('close');
    }
    
    function _submitSchedule() {
        var formSerialized = $("#filterForm").serialize();
        formSerialized += "&" + $("#scheduleForm").serialize();
        $.post("saveScheduledMeterEventJob?" + formSerialized, function (data) {
            var response = $(data),
                scheduleModelData = yukon.fromJson(response.filter("#scheduleModelData"));
            if (scheduleModelData.success) {
                $("#scheduleDialog").dialog('close');
                _reloadScheduledJobsTable(response.filter("#flashScopeMsg"));
            } else {
                $("#scheduleDialog").html(response);
                yukon.tag.scheduledFileExportInputs.initializeFields();
            }
        });
    }

    function _reloadScheduledJobsTable(flashScope) {
        $("#scheduledJobsTable").load("scheduledJobsTable", function () {
            $("#scheduledJobsTableFlashScope").hide().html(flashScope).fadeIn(150);
        });
    }

    function _deleteJob(jobId) {
        var data = {jobId : jobId};

        $.post("delete", data, 'json').done(function (data) {
            var flashScopeEl = $("<div>").attr('class','user-message success').text(data.successMsg);
            _reloadScheduledJobsTable(flashScopeEl);
            $("#scheduledJobsTableFlashScope").html(flashScopeEl);
        });
    }
    
    function _createScheduleReportDialog(title) {
        $("#scheduleDialog").dialog({'title' : title, 'modal' : true, 'height' : 'auto',
            'width' : 'auto'});
    }
    
    function _updateMeterEventsTable() {
        var formSerialized = $("#filterForm").serialize(),
        formUrl = $("#filterForm").attr('action');
        $.post(formUrl, formSerialized, function (data) {
            $("#meterEventsTable").html(data);
            yukon.ui.unbusy($("#updateMeterEventsBtn"));
        });
    }

    mod = {
        updateEventTypes: function() {
            _updateEventTypes();
            _updateMeterEventsTable();
        },

        init: function() {
            _modelData = yukon.fromJson("#modelData");
            $("#updateMeterEventsBtn").click(_updateMeterEventsTable);

            $("#scheduleMeterEventsBtn").click(function() {
                var formSerialized = $("#filterForm").serialize();
                $.post("scheduledMeterEventsDialog", formSerialized, function (data) {
                    $("#scheduleDialog").html(data);
                    yukon.tag.scheduledFileExportInputs.initializeFields();
                });
                if ($("scheduleDialog").hasClass("ui-dialog-content")) {
                    $("#scheduleDialog").dialog('destroy');
                }
                _createScheduleReportDialog(_modelData.newSchedulePopupTitle);
            });

            $("#eventTypesFilterOkBtn").click(function () {
                _updateEventTypes();
                _updateMeterEventsTable();
            });

            $("#exportCsvBtn").click(function () {
                var _meterEventsTableModel = yukon.fromJson("#meterEventsTableModelData"),
                    downloadForm = $("#downloadFormContents").empty();
                $("#filterForm").find(":input[name]").each(function() {
                    var self = $(this);
                    downloadForm.append($("<input>")
                                .attr("name", self.attr("name"))
                                .attr("type", self.attr("type"))
                                .val(self.val()));
                });
                downloadForm.append($("<input>").attr("name", "sort").val(_meterEventsTableModel.sort));
                downloadForm.append($("<input>").attr("name", "descending").val(_meterEventsTableModel.descending));
                $("#downloadForm").submit();
            });

            $(document).on('click', '#scheduledJobDialogSchedule', _submitSchedule);

            $(document).on('click', '#scheduledJobDialogCancel', function() {
                $('#scheduleDialog').dialog('close');
            });

            $(document).on('click', '.f-delete-schedule-item', function (event) {
                event.preventDefault();
                var jobId = $(this).closest("[data-job-id]").data("job-id");
                $("#scheduledJobsConfirmDeleteDialog").dialog({'title' : _modelData.confirmScheduleDeletion,
                    'modal': true, 'height' : 'auto', 'width' : 'auto',
                    'buttons': 
                        [{
                             text: _modelData.cancelBtnLbl, 
                             click: function() {$(this).dialog('close');}
                        }, {
                             text: _modelData.okBtnLbl, 
                             click: function() {
                                 $(this).dialog('close');
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

$(function () {
    yukon.ami.meterEventsReport.init();
});