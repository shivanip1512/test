import React from 'react';
import PropTypes from 'prop-types';

import { Dialog as MuiDialog, DialogTitle, DialogContent, DialogActions, IconButton } from '@material-ui/core';
import CloseIcon from '@material-ui/icons/Close';
import { useTheme } from '@material-ui/core/styles';

import Button from '../controls/Button';

const Dialog = (props) => {

    const { title, content, open, closeEvent, buttons } = props;

    Dialog.propTypes = {
        title: PropTypes.string.isRequired,             //title for the dialog
        content: PropTypes.object.isRequired,           //content for the dialog
        open: PropTypes.bool.isRequired,                //boolean to check when dialog should be opened
        closeEvent: PropTypes.func.isRequired,          //event to call on close of dialog
        buttons: PropTypes.array.isRequired             //array of buttons, each should have label and onClick
    }

    const theme = useTheme();

    return (
        <MuiDialog open={open} onClose={closeEvent}>
            <DialogTitle style={{margin: 0, padding: theme.spacing(2)}}>
                {title}
                <IconButton onClick={closeEvent} style={{position: 'absolute', right: theme.spacing(1), top: theme.spacing(1)}}>
                    <CloseIcon/>
                </IconButton>
            </DialogTitle>
            <DialogContent dividers>{content}</DialogContent>
            <DialogActions>
                {buttons.map(function (button, index) {
                    return <Button key={index} label={button.label} onClick={button.onClick} style={button.style}/>
                })}
            </DialogActions>
        </MuiDialog>
    )

}

export default Dialog;