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
            var submitNormal = $('#commentForm').data('submitNormal');
            if (submitNormal) {
                $('#commentForm').submit();
            } else {
                $.ajax({
                    url: $("#commentForm").attr("action"),
                    data: $("#commentForm").serialize(),
                    type: "POST"
                }).done(function (data) {
                    $("#contentPopup").html(data);
                });
            }
        };

    mod = {
        addComment : function () {
            var newComment = $('#newCommentInput').val(),
                commentsUrl,
                escapedText;
            if ($.trim(newComment).length !== 0) {
                commentsUrl = $('#commentForm').data('commentsUrl');
                $('#commentForm').attr('action', commentsUrl + 'add');
                escapedText = $('<div />').text(newComment).html();
                $('#comment').val(escapedText);
                _submitForm();
                $('#newCommentInput').prop('disabled', true);
            }
        },

        hideNewRow : function () {
            $('#newCommentInput').val('');
            $('#newRow').hide();
        },

        showNewRow : function () {
            $("input[name='editCommentInput']").each(function (index, elem) {
                var elemsCommentId = elem.id.split('_')[1];
                $('#editCommentSpan_' + elemsCommentId).hide();
                $('#comment_' + elemsCommentId).show();
            });
            $('#newRow').show();
            $('#newCommentInput').prop('disabled', false);
            $('#newCommentInput').focus();
            flashYellow($('#newRow')[0]);
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
            $('#comment_' + commentId).show();
            $('#editCommentSpan_' + commentId).hide();
            $('#editComment_' + commentId).val($('#comment_' + commentId).html());
        },
    
        updateComment : function (commentId) {
            var newComment = $('#editComment_' + commentId).val(),
                commentsUrl,
                escapedText;
            if ($.trim(newComment).length !== 0) {
                commentsUrl = $('#commentForm').data('commentsUrl');
                escapedText = $('<div />').text(newComment).html();
                $('#comment').val(escapedText);
                $('#commentForm').attr('action', commentsUrl + 'update');
                $('#commentId').val(commentId);
                _submitForm();
            }
        },

        editComment : function (commentId) {
            mod.hideNewRow();
            $("input[name='editCommentInput']").each(function (index, elem) {
                var elemsCommentId;
                if (elem.id !== 'editComment_' + commentId) {
                    elemsCommentId = elem.id.split('_')[1];
                    $('#editCommentSpan_' + elemsCommentId).hide();
                    $('#comment_' + elemsCommentId).show();
                }
            });
            $('#comment_' + commentId).hide();
            $('#editCommentSpan_' + commentId).show();
            $('#editComment_' + commentId).focus();
        },
    
        deleteComment : function (commentId) {
            var commentsUrl = $('#commentForm').data('commentsUrl');
            $('#commentId').val(commentId);
            $('#commentForm').attr('action', commentsUrl + 'remove');
            _submitForm();
        }
    };
    return mod;
})();