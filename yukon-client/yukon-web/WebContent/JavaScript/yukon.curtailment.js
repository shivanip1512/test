/**
 * Singleton to manage the commercial curtailment pages.
 * 
 * @requires jQuery 1.9+
 */

yukon.namespace('yukon.curtailment');

yukon.curtailment = (function () {
    var mod,
        _doCalcLoadReduction = function () {
            var custTableCells = $('#customerTableDiv tbody > tr'),
                i,
                curLoadA,
                fslA,
                loadReductCellA,
                curLoadAval,
                fslAval,
                loadReduction;

            for (i = 0; i < custTableCells.length; i += 1) {
                curLoadA = $(custTableCells[i]).find('.curLoad span');
                fslA = $(custTableCells[i]).find('.fsl span');
                loadReductCellA = $(custTableCells[i]).find('.loadReduct');
                if (curLoadA.length > 0 && fslA.length > 0) {
                    curLoadAval = parseInt($(curLoadA[0]).html().replace(/,/g,""), 10);
                    fslAval = parseInt($(fslA[0]).html().replace(/,/g,""), 10);
                    loadReduction = curLoadAval - fslAval;
                    if (loadReduction !== loadReduction) {
                        // meaning loadReduction is NaN, which happens when either curLoadA[0] or fslA[0] are set to ellipses
                        loadReduction = 'n/a';
                    }
                    $(loadReductCellA[0]).html(loadReduction);
                } else {
                    $(loadReductCellA[0]).html('n/a');
                }
            }
        },

        _doFormatLoadValues = function () {
            var custTableCells = $('#customerTableDiv tbody > tr'),
                i,
                loadReductCellA;
        
            for (i = 0; i < custTableCells.length; i += 1) {
                loadReductCellA = $(custTableCells[i]).find('.loadReduct');
                $(loadReductCellA[0]).html(_commaFormat($(loadReductCellA[0]).html()));
            }
        },

        _commaFormat = function (amount) {
            var delimiter = ",",
                minus,
                i,
                n,
                a,
                nn;

            if (isNaN(amount)) {
                // just return the string if it's not a number
                return amount;
            }
        
            i = parseInt(amount, 10);
            minus = '';
            if (i < 0) { 
                //record the negative sign
                minus = '-';
            }
        
            i = parseInt(amount, 10);
            i = Math.abs(i);    //remove negative sign
            n = i.toString();
            a = [];
            while (n.length > 3) {
                nn = n.substr(n.length-3);
                a.unshift(nn);
                n = n.substr(0,n.length-3);
            }
            if (n.length > 0) { a.unshift(n); }
            n = a.join(delimiter);
            amount = n;
            amount = minus + amount;
            return amount;
        },
        
        _groupMove = function (opts) {
            var programId = $('#program').data('programId'),
                groupId = $(opts.target).data(opts.data),
                rowToMove = $(opts.target).closest('tr'),
                moveTarget = $(opts.moveTarget),
                unassigning = opts.urlEndpoint.indexOf('/un') === 0 ? true : false;
            $.ajax({
                url: yukon.url(opts.baseUrl + programId + opts.urlEndpoint + groupId),
                type: 'post'
            }).done(function (jqXHR, textStatus, errorThrown) {
                var icon = rowToMove.find('i');
                debug.log('endpoint: ' + opts.urlEndpoint + ' for groupId ' + groupId + ' successfully processed');
                rowToMove.appendTo(moveTarget);
                icon.removeClass(unassigning ? 'icon-delete' : 'icon-add');
                icon.addClass(unassigning ? 'icon-add' : 'icon-delete');
            }).fail(function (jqXHR, textStatus, errorThrown) {
                debug.log('endpoint: ' + opts.urlEndpoint + ' for groupId ' + groupId + ' failed: ' + textStatus);
            });
        };

    mod = {
        doCalcSelectedLoad: function () {
            _doCalcLoadReduction();
            var custTableCells = $('#customerTableDiv tbody > tr'),
                loadReduct = 0,
                i,
                checkedCells,
                loadReductA,
                tableCellsLength = custTableCells.length,
                summary,
                loadReductSummaryA;
        
            for (i = 0; i < tableCellsLength; i += 1) {
                checkedCells = $(custTableCells[i]).find('input[type=checkbox]');
                if ($(checkedCells[0]).is(':checked')) {
                    loadReductA = custTableCells.find('.loadReduct');
                    loadReduct += parseFloat($(loadReductA[0]).html());
                }
            }
        
            summary = $('#customerTableDiv tfoot > tr');
            if (summary.length > 0) {
                loadReductSummaryA = $(summary).find('.loadReductFoot');
                $(loadReductSummaryA[0]).html(_commaFormat(loadReduct));
            }
            _doFormatLoadValues();
        },
        
        init: function () {
            $(document).on('click', '#create-program', function (ev) {
                var programTypeId,
                    programName,
                    url;
                programTypeId = $('#program-type :selected').val();
                programName = $('#program-name').val();
                url = $(this).data('url');
                debug.log('programTypeId=' + programTypeId + ' programName=' + programName + ' url=' + url);
                $.ajax({
                    url: url + '/' + programTypeId + '/' + programName,
                    type: 'post'
                }).done(function (data, textStatus, jqXHR) {
                    debug.log('textStatus=' + textStatus);
                    var newDoc = document.open("text/html", "replace");
                    newDoc.write(data);
                    newDoc.close();
                });
            });
            $(function () {
                $(document).on('click', '#assigned-groups', function (ev) {
                    var button = $(ev.target).closest('button');
                    debug.log('moving assigned group to unassigned');
                    _groupMove({target: button, baseUrl: '/dr/cc/program/', urlEndpoint: '/unassignGroup/', data: 'group', moveTarget: '#unassigned-groups tbody'});
                });
                $(document).on('click', '#unassigned-groups', function (ev) {
                    var button = $(ev.target).closest('button');
                    debug.log('moving unassigned group to assigned');
                    _groupMove({target: button, baseUrl: '/dr/cc/program/', urlEndpoint: '/assignGroup/', data: 'group', moveTarget: '#assigned-groups tbody'});
                });
                $(document).on('click', '#assigned-notification-groups', function (ev) {
                    var button = $(ev.target).closest('button');
                    debug.log('moving assigned notification group to unassigned');
                    _groupMove({target: button, baseUrl: '/dr/cc/program/', urlEndpoint: '/unassignNotificationGroup/', data: 'notifGroup', moveTarget: '#unassigned-notification-groups tbody'});
                });
                $(document).on('click', '#unassigned-notification-groups', function (ev) {
                    var button = $(ev.target).closest('button');
                    debug.log('moving unassigned notification group to assigned');
                    _groupMove({target: button, baseUrl: '/dr/cc/program/', urlEndpoint: '/assignNotificationGroup/', data: 'notifGroup', moveTarget: '#assigned-notification-groups tbody'});
                });
            });
        }
        
    };
    
    return mod;
})();
$(function () { yukon.curtailment.init(); });
