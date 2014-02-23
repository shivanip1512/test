/**
 * Singleton that manages the comments for capcontrol objects
 * 
 * @requires jQuery 1.8.3+
 * @requires jQuery UI 1.9.2+
 */

yukon.namespace('yukon.da');
yukon.namespace('yukon.da.comments');

yukon.da.comments = (function () {
    
    var mod,
        _submitForm = function () {
            var submitNormal = jQuery('#commentForm').data('submitNormal');
            if (submitNormal) {
                jQuery('#commentForm').submit();
            } else {
                jQuery.ajax({
                    url: jQuery("#commentForm").attr("action"),
                    data: jQuery("#commentForm").serialize(),
                    type: "POST"
                }).done(function (data) {
                    jQuery("#contentPopup").html(data);
                });
            }
        };

    mod = {
        addComment : function () {
            var newComment = jQuery('#newCommentInput').val(),
                commentsUrl,
                escapedText;
            if (jQuery.trim(newComment).length !== 0) {
                commentsUrl = jQuery('#commentForm').data('commentsUrl');
                jQuery('#commentForm').attr('action', commentsUrl + 'add');
                escapedText = jQuery('<div />').text(newComment).html();
                jQuery('#comment').val(escapedText);
                _submitForm();
                jQuery('#newCommentInput').prop('disabled', true);
            }
        },

        hideNewRow : function () {
            jQuery('#newCommentInput').val('');
            jQuery('#newRow').hide();
        },

        showNewRow : function () {
            jQuery("input[name='editCommentInput']").each(function (index, elem) {
                var elemsCommentId = elem.id.split('_')[1];
                jQuery('#editCommentSpan_' + elemsCommentId).hide();
                jQuery('#comment_' + elemsCommentId).show();
            });
            jQuery('#newRow').show();
            jQuery('#newCommentInput').prop('disabled', false);
            jQuery('#newCommentInput').focus();
            flashYellow(jQuery('#newRow')[0]);
        },

        addOrCancel : function (event) {
            var key = event.keyCode;
            if (key === 27) {
                /* Escape Key */
                mod.hideNewRow();
            } else if (key === 13) {
                /* Enter Key */
                mod.addComment();
            }
            return (key !== 13);
        },

        updateOrCancel : function (event, commentId) {
            var key = event.keyCode;
            if (key === 27) {
                /* Escape Key */
                mod.cancelUpdate(commentId);
            } else if (key === 13) {
                /* Enter Key */
                mod.updateComment(commentId);
            }
            return (key !== 13);
        },

        cancelUpdate : function (commentId) {
            jQuery('#comment_' + commentId).show();
            jQuery('#editCommentSpan_' + commentId).hide();
            jQuery('#editComment_' + commentId).val(jQuery('#comment_' + commentId).html());
        },
    
        updateComment : function (commentId) {
            var newComment = jQuery('#editComment_' + commentId).val(),
                commentsUrl,
                escapedText;
            if (jQuery.trim(newComment).length !== 0) {
                commentsUrl = jQuery('#commentForm').data('commentsUrl');
                escapedText = jQuery('<div />').text(newComment).html();
                jQuery('#comment').val(escapedText);
                jQuery('#commentForm').attr('action', commentsUrl + 'update');
                jQuery('#commentId').val(commentId);
                _submitForm();
            }
        },

        editComment : function (commentId) {
            mod.hideNewRow();
            jQuery("input[name='editCommentInput']").each(function (index, elem) {
                var elemsCommentId;
                if (elem.id !== 'editComment_' + commentId) {
                    elemsCommentId = elem.id.split('_')[1];
                    jQuery('#editCommentSpan_' + elemsCommentId).hide();
                    jQuery('#comment_' + elemsCommentId).show();
                }
            });
            jQuery('#comment_' + commentId).hide();
            jQuery('#editCommentSpan_' + commentId).show();
            jQuery('#editComment_' + commentId).focus();
        },
    
        deleteComment : function (commentId) {
            var commentsUrl = jQuery('#commentForm').data('commentsUrl');
            jQuery('#commentId').val(commentId);
            jQuery('#commentForm').attr('action', commentsUrl + 'remove');
            _submitForm();
        }
    };
    return mod;
})();