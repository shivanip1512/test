
yukon.namespace('yukon.ami');
yukon.namespace('yukon.ami.advanced.processing');

yukon.ami.advanced.processing = (function () {
    $(function () {
        //accept all
        $(document).on('click','#accept-all', function() {
            //busy all buttons
            yukon.ui.busy('#delete-all');
            yukon.ui.busy('#reset-12-months');
            yukon.ui.busy('#reset-all');
            var acceptUrl = yukon.url('/amr/veeReview/advancedProcessing/acceptAll');
            $.post(acceptUrl, $('#accept-form').serialize()).done(function(result) {
                $('#accept-form input:checkbox').prop('checked', false);
                yukon.ui.unbusy($('#accept-all'));
                yukon.ui.unbusy('#delete-all');
                yukon.ui.unbusy('#reset-12-months');
                yukon.ui.unbusy('#reset-all');
                if(result === true) {
                    $('#tags-accepted').show();
                }
            });
            return false;
        });
        //delete all
        $(document).on('click','#delete-all', function() {
            yukon.ui.busy('#accept-all');
            yukon.ui.busy('#reset-12-months');
            yukon.ui.busy('#reset-all');
            var deleteUrl = yukon.url('/amr/veeReview/advancedProcessing/deleteAll');
            $.post(deleteUrl, $('#delete-form').serialize()).done(function(result) {
                $('#delete-form input:checkbox').prop('checked', false);
                yukon.ui.unbusy('#delete-all');
                yukon.ui.unbusy('#accept-all');
                yukon.ui.unbusy('#reset-12-months');
                yukon.ui.unbusy('#reset-all');
                if(result === true) {
                    $('#tags-deleted').show();
                }
            });
            return false;
        });
        //reset one year
        $(document).on('click','#reset-12-months', function(e) {
            yukon.ui.busy('#accept-all');
            yukon.ui.busy('#delete-all');
            yukon.ui.busy('#reset-all');
            var resetOneYearUrl = yukon.url('/amr/veeReview/advancedProcessing/resetOneYear');
            $.post(resetOneYearUrl).done(function(result) {
                yukon.ui.unbusy($('#reset-12-months'));
                yukon.ui.unbusy('#accept-all');
                yukon.ui.unbusy('#delete-all');
                yukon.ui.unbusy('#reset-all');
                if(result === true) {
                    $('#engine-reset').show();
                }
            });
            return false;
        });
        //delete all
        $(document).on('click','#reset-all', function(e) {
            yukon.ui.busy('#accept-all');
            yukon.ui.busy('#delete-all');
            yukon.ui.busy('#reset-12-months');
            var resetUrl = yukon.url('/amr/veeReview/advancedProcessing/resetAll');
            $.post(resetUrl).done(function(result) {
                yukon.ui.unbusy($('#reset-all'));
                yukon.ui.unbusy('#accept-all');
                yukon.ui.unbusy('#delete-all');
                yukon.ui.unbusy('#reset-12-months');
                if(result === true) {
                    $('#engine-reset').show();
                }
            });
            return false;
        });
    });
})();