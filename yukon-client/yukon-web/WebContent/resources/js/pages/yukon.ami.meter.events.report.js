yukon.namespace('yukon.ami.meterEventsReport');

/**
 * Handles Meter Events Reports. 
 * 
 * @module yukon.ami.meterEventsReport
 * @requires JQUERY
 * @requires JQUERYUI
 * @requires jquery.dynatree.js
 * @requires yukon
 * @requires yukon.ui.util
 */
yukon.ami.meterEventsReport = (function () {

    var 
    /** @type {boolean} - This flag is set to true when the event types tree is initialized. */
    _eventTypesTreeInitialized = false,
    
    /** @type {Array.<string>} - Array of all the titles of event types that need to be ignored. */
    _titlesToIgnore = [],
    
    /** @type {Object} - Model Data. */
    _modelData = {},
    
    /** Populates the tree with attributes for all the event Types.
     * @param {Object} allAttributesMap - all events and attribute Map.
     * @param {Object} eventNode - Event Type.
     */ 
    _populateTreeNodes = function (allAttributesMap, eventNode) {
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
    },

    /** Ignores the event type title from the event Types Nodes.
     *  @param {string} title - node name of events Map.
     */
    _ignoreTitle = function (title) {
        for (var i = 0; i < _titlesToIgnore.length; i++) {
            if (title === _titlesToIgnore[i]) {
                return true;
            }
        }
        return false;
    },

    /** Initializes the Event types tree. */ 
    _initEventTypesTree = function () {
        if (_eventTypesTreeInitialized === true) {
            return;
        }

        var allEventsMap = _modelData.meterEventTypesMap,
            generalNodes = _populateTreeNodes(allEventsMap, _modelData.generalEvents),
            hardwareNodes = _populateTreeNodes(allEventsMap, _modelData.hardwareEvents),
            tamperNodes = _populateTreeNodes(allEventsMap, _modelData.tamperEvents),
            outageNodes = _populateTreeNodes(allEventsMap, _modelData.outageEvents),
            meteringNodes = _populateTreeNodes(allEventsMap, _modelData.meteringEvents);

        _titlesToIgnore.push(_modelData.allTitle);
        _titlesToIgnore.push(_modelData.generalTitle);
        _titlesToIgnore.push(_modelData.hardwareTitle);
        _titlesToIgnore.push(_modelData.tamperTitle);
        _titlesToIgnore.push(_modelData.outageTitle);
        _titlesToIgnore.push(_modelData.meteringTitle);

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
        $('#eventTree').dynatree({
            checkbox: true,
            selectMode: 3,
            children: treeChildren
        });

        _eventTypesTreeInitialized = true;
    },

    /** Updates the event types. */
    _updateEventTypes = function () {
        var numSelected = 0,
            selectedNodes;
        $('#eventTypeInputs').empty();
        selectedNodes = $('#eventTree').dynatree('getSelectedNodes');
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
        $('#download-meter-events-btn, #schedule-meter-events-btn').prop('disabled', numSelected == 0);
    },
    
    /** Schedules a meter event report. */
    _submitSchedule = function () {
        var formSerialized = $('#filter-form').serialize();
        formSerialized += '&' + $('#schedule-form').serialize();
        $.post('saveScheduledMeterEventJob?' + formSerialized, function (data) {
            var response = $(data),
                scheduleModelData = yukon.fromJson(response.filter('#scheduleModelData'));
            if (scheduleModelData.success) {
                $('#scheduleDialog').dialog('close');
                _reloadScheduledJobsTable(response.filter('#flashScopeMsg'));
            } else {
                $('#scheduleDialog').html(response);
                yukon.tag.scheduledFileExportInputs.initializeFields();
            }
        });
    },

    /** Refreshes the schedule table. 
     *  @param {string} flashScope - inner html content.  */
    _reloadScheduledJobsTable = function (flashScope) {
        $('#scheduledJobsTable').load('scheduledJobsTable', function () {
            $('#scheduledJobsTableFlashScope').hide().html(flashScope).fadeIn(150);
        });
    },

    /** Deletes the schedule with give Id. 
     *  @param {string} jobId - Schedule Id.  */
    _deleteJob = function (jobId) {
        var data = {jobId : jobId};

        $.post('delete', data, 'json').done(function (data) {
            var flashScopeEl = $('<div>').attr('class','user-message success').text(data.successMsg);
            _reloadScheduledJobsTable(flashScopeEl);
        });
    },
    
    /**  Opens the schedule dialog with given name.
     * @param {string} title - Dialog box title.  */
    _createScheduleReportDialog = function (title) {
        $('#scheduleDialog').dialog({
            'title' : title, 
            'modal' : true, 
            width: 'auto', 
            height: 'auto',
            show: { complete: function () { $('#scheduleDialog :text').focus(); } },
        });
    },
    
    /**  Updates the meter events table. */
    _updateMeterEventsTable = function () {
        var 
        params = $('#filter-form').serialize(),
        url = $('#filter-form').attr('action'),
        sorting = $('.sortable.desc, .sortable.asc'),
        paging = $('.paging-area'),
        extras = {};
        
        if (sorting.length > 0) {
            extras.sort = sorting.data('sort'),
            extras.dir = sorting.is('.desc') ? 'desc' : 'asc';
        }
        extras.itemsPerPage = paging.length > 0 ? paging.data('pageSize') : 10;
        
        $.post(url, params + '&' + $.param(extras), function (data) {
            $('#meterEventsTable').html(data);
            yukon.ui.unbusy($('#update-meter-events-btn'));
            $('#meterEventsTable').data('url', url + '?' + params);
        });
    },

    _mod = {
            
        updateEventTypes: function() {
            _updateEventTypes();
            _updateMeterEventsTable();
        },

        init: function() {
            
            $(document).on('click', '.js-delete-schedule-item', function (event) {
                event.preventDefault();
                var jobId = $(this).closest('[data-job-id]').data('job-id');
                $('#scheduledJobsConfirmDeleteDialog').dialog({
                    'modal': true, 
                    'height' : 'auto', 
                    'width' : 'auto',
                    'buttons': 
                        [{
                             text: yg.text.cancel, 
                             click: function() {$(this).dialog('close');}
                        }, {
                             text: yg.text.ok, 
                             click: function() {
                                 $(this).dialog('close');
                                 _deleteJob(jobId);
                             },
                             'class': 'primary action'
                        }]
                     });
            });
            
            if ($('#modelData').length) {
                _modelData = yukon.fromJson('#modelData');
                $('#update-meter-events-btn').click(_updateMeterEventsTable);

                $('#schedule-meter-events-btn').click(function() {
                    var formSerialized = $('#filter-form').serialize();
                    $.post('scheduledMeterEventsDialog', formSerialized, function (data) {
                        $('#scheduleDialog').html(data);
                        yukon.tag.scheduledFileExportInputs.initializeFields();
                    });
                    if ($('#scheduleDialog').hasClass('ui-dialog-content')) {
                        $('#scheduleDialog').dialog('destroy');
                    }
                    _createScheduleReportDialog(_modelData.newSchedulePopupTitle);
                });

                $(document).on('yukon.ami.meter.events.filter', function () {
                    $('#filter-events-popup').dialog('close');
                    _updateEventTypes();
                    _updateMeterEventsTable();
                });

                $('#download-meter-events-btn').click(function () {
                    var _meterEventsTableModel = yukon.fromJson('#meterEventsTableModelData'),
                        downloadForm = $('#downloadFormContents').empty();
                    $('#filter-form').find(':input[name]:not(:checkbox)').each(function() {
                        var self = $(this);
                        downloadForm.append($('<input>')
                                    .attr('name', self.attr('name'))
                                    .attr('type', self.attr('type'))
                                    .val(self.val()));
                    });

                    $('#filter-form').find(':input[type = checkbox]').each(function() {
                        var self = $(this);
                        if (self.is(":checked")) {
                            downloadForm.append($('<input>')
                                    .attr('name', self.attr('name'))
                                    .attr('type', self.attr('type'))
                                    .prop("checked", true));
                        }
                    });

                    downloadForm.append($('<input>').attr('name', 'sort').val(_meterEventsTableModel.sort));
                    downloadForm.append($('<input>').attr('name', 'descending').val(_meterEventsTableModel.descending));
                    $('#downloadForm').submit();
                });

                $(document).on('click', '#scheduledJobDialogSchedule', _submitSchedule);

                $(document).on('click', '#scheduledJobDialogCancel', function() {
                    $('#scheduleDialog').dialog('close');
                });

                if (_modelData.openScheduleDialog) {
                    _createScheduleReportDialog(_modelData.schedulePopupTitle);
                }

                _initEventTypesTree();
                _updateEventTypes();
            }
            
        }
    };
    
    return _mod;
}());

$(function () { yukon.ami.meterEventsReport.init(); });