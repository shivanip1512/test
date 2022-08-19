yukon.namespace('yukon.da.comments');

/**
 * Singleton that manages the comments for capcontrol objects
 * 
 * @requires JQUERY
 * @requires JQUERYUI 
 */
yukon.da.comments = (function () {
    
    var mod,
        _submitForm = function () {
            var submitNormal = $('#commentForm').data('submitNormal'),
                form = $('#commentForm'),
                dialog = form.closest('.ui-dialog-content');
            if (submitNormal) {
                form.submit();
            } else {
                $.ajax({
                    url: form.attr("action"),
                    data: form.serialize(),
                    type: "POST"
                }).done(function (data) {
                    dialog.html(data);
                });
            }
        };

    mod = {
            
        /** Adds a new comment to the Cap Bank*/  
        addComment : function () {
            var newComment = $('#newCommentInput').val(),
                commentsUrl;
            if ($.trim(newComment).length !== 0) {
                commentsUrl = $('#commentForm').data('commentsUrl');
                $('#commentForm').attr('action', commentsUrl + 'add');
                $('#comment').val(newComment);
                _submitForm();
                $('#newCommentInput').prop('disabled', true);
            } else {
                yukon.ui.unbusy('.js-save-comment');
            }
        },

        /** Hides a Comment row on click of Cancel.*/
        hideNewRow : function () {
            $('#newCommentInput').val('');
            $('#newRow').hide();
        },

        /** Saves a new comment or cancels the operation.
         *  @param {Object} event - key stroke object.
         */
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
        
        /** Edits a comment or cancels the operation.
         *  @param {Object} event - key stroke object.
         *  @param {number} commentId - Id of comment on which the operation is performed.
         */
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

        /** Cancels the edit operation of the comment.
         *  @param {number} commentId - Id of comment on which the operation is performed.
         */
        cancelUpdate : function (commentId) {
            $('#comment_' + commentId).show();
            $('#editCommentSpan_' + commentId).hide();
            $('#editComment_' + commentId).val($('#comment_' + commentId).html().trim());
        },
    
        /** Updates the comment.
         *  @param {number} commentId - Id of comment on which the operation is performed.
         */
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
        
        /** Edits the comment.
         *  @param {number} commentId - Id of comment which is being edited.
         */
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
    
        /** Deletes a comment.
         *  @param {number} commentId - Id of comment which is being deleted.
         */
        deleteComment : function (commentId) {
            var commentsUrl = $('#commentForm').data('commentsUrl');
            $('#commentId').val(commentId);
            $('#commentForm').attr('action', commentsUrl + 'remove');
            _submitForm();
        }
    };
    return mod;
})();