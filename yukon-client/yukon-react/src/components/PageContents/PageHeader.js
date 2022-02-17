import React, { useState } from 'react';
import { useSelector, useDispatch } from 'react-redux';
import PropTypes from 'prop-types';

import { Link, Breadcrumbs, Typography, IconButton, Menu, MenuItem, ListItemIcon, ListItemText } from '@material-ui/core';
import { Alert } from '@material-ui/lab';
import { useTheme } from '@material-ui/core/styles';
import { Spacer } from '@brightlayer-ui/react-components';

import StarBorderIcon from '@material-ui/icons/StarBorder';
import MoreVertIcon from '@material-ui/icons/MoreVert';

import Button from '../controls/Button';

import { CLEAR_FLASH_ERRORS, CLEAR_FLASH_SUCCESS } from '../../redux/actions/actionTypes';

const PageHeader = (props) => {

    const { pageTitle, breadcrumbs, actionButtons, actionMenuOptions } = props;

    PageHeader.propTypes = {
        pageTitle: PropTypes.string.isRequired,         //i18n function to get title for the page
        breadcrumbs: PropTypes.array.isRequired,        //array of breadcrumbs, each should have link and title defined
        actionButtons: PropTypes.array,                 //array of action buttons, each should have label, clickEvent and optional icon defined
        actionMenuOptions: PropTypes.array              //array of action menu items, each should have title and optional icon defined
    }

    const dispatch = useDispatch();
    const theme = useTheme();

    const [anchorEl, setAnchorEl] = useState(null);

    const handleClick = (event) => {setAnchorEl(event.currentTarget);};
    const handleClose = () => {setAnchorEl(null);};

    const flashErrors = useSelector(store => store.app.flashErrors);
    const flashSuccess = useSelector(store => store.app.flashSuccess);

    const handleClearFlashErrors = () => dispatch({type: CLEAR_FLASH_ERRORS});
    const handleClearFlashSuccess = () => dispatch({type: CLEAR_FLASH_SUCCESS});

    let actionMenu = <div></div>;
    let actionButtonContent = <div></div>;

    if (actionButtons != null) {
        actionButtonContent = 
        <div style={{float: 'right'}}>
            {actionButtons.map((button) => 
                <Button key={button.label} label={button.label} onClick={button.clickEvent} 
                    variant="outlined" color="primary" 
                    icon={button.icon} style={{marginRight: theme.spacing(2)}}/>
            )}
        </div>
    }

    if (actionMenuOptions != null) {
        actionMenu =
            <div>
                <IconButton 
                    aria-controls="customized-menu"
                    aria-haspopup="true"
                    variant="contained"
                    onClick={handleClick}>
                    <MoreVertIcon/>
                </IconButton>
                <Menu
                    id="customized-menu"
                    anchorEl={anchorEl}
                    keepMounted
                    open={Boolean(anchorEl)}
                    onClose={handleClose}
                    getContentAnchorEl={null}
                    anchorOrigin={{
                        vertical: 'bottom',
                        horizontal: 'center',
                    }}
                    transformOrigin={{
                        vertical: 'top',
                        horizontal: 'center',
                    }}
                >
                    {actionMenuOptions.map((menuItem) => 
                        <MenuItem key={menuItem.title} dense={true} style={{paddingTop: theme.spacing(0), paddingBottom: theme.spacing(0)}}>
                            <ListItemIcon>{menuItem.icon}</ListItemIcon>
                            <ListItemText>{menuItem.title}</ListItemText>
                        </MenuItem>
                    )}
                </Menu>
            </div>
    }

    return (
        <div style={{margin: theme.spacing(2)}}>
            <Breadcrumbs>
                {breadcrumbs.map((breadcrumb, index) =>
                    <Link key={index} href={breadcrumb.link}>{breadcrumb.title}</Link>
                )};
                <Typography>{pageTitle}</Typography>
            </Breadcrumbs>
            <div style={{display: 'flex', marginTop: theme.spacing(2), alignItems: 'center'}}>
                <StarBorderIcon/><Typography variant="h6" style={{paddingLeft: theme.spacing(2)}}>{pageTitle}</Typography>
                <Spacer flex={1} />
                {actionButtonContent}
                {actionMenu}
            </div>
            { flashErrors 
                ? <Alert severity="error" onClose={handleClearFlashErrors}>{flashErrors}</Alert> 
                : <div></div> }
            { flashSuccess
                ? <Alert severity="success" onClose={handleClearFlashSuccess}>{flashSuccess}</Alert> 
                : <div></div> }
        </div>
    )
}

export default PageHeader;