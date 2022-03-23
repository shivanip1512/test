import React from 'react';
import { useTranslation } from 'react-i18next';
import { useSelector } from 'react-redux';

import { useSecurityActions } from '@brightlayer-ui/react-auth-shared';
import { LocalStorage } from '../../store/local-storage';

import { UserMenu, Spacer } from '@brightlayer-ui/react-components';
import { useTheme } from '@material-ui/core/styles';
import AppBar from '@material-ui/core/AppBar';
import Toolbar from '@material-ui/core/Toolbar';
import Typography from '@material-ui/core/Typography';
import InputBase from '@material-ui/core/InputBase';
import Badge from '@material-ui/core/Badge';
import { alpha, makeStyles } from '@material-ui/core/styles';

import SearchIcon from '@material-ui/icons/Search';
import ExitIcon from '@material-ui/icons/ExitToApp';
import EmailIcon from '@material-ui/icons/Email';
import PhoneIcon from '@material-ui/icons/Phone';
import NotificationsIcon from '@material-ui/icons/Notifications';
import PersonIcon from '@material-ui/icons/Person';
import KeyIcon from '@material-ui/icons/VpnKey';
import {useIdleTimer} from 'react-idle-timer'
import Avatar from '@material-ui/core/Avatar';
import axios from '../../axiosConfig';

const useStyles = makeStyles(theme => ({
    search: {
        position: 'relative',
        borderRadius: theme.shape.borderRadius,
        backgroundColor: alpha(theme.palette.common.white, 0.15),
        '&:hover': {
        backgroundColor: alpha(theme.palette.common.white, 0.25),
        },
        marginLeft: 0,
        width: '100%',
        [theme.breakpoints.up('sm')]: {
        marginLeft: theme.spacing(1),
        width: 'auto',
        },
    },
    searchIcon: {
        width: theme.spacing(7),
        height: '100%',
        position: 'absolute',
        pointerEvents: 'none',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
    },
    inputRoot: {
        color: 'inherit',
    },
    inputInput: {
        padding: theme.spacing(1, 1, 1, 7),
        transition: theme.transitions.create('width'),
        width: '100%',
        [theme.breakpoints.up('sm')]: {
        width: 120,
        '&:focus': {
            width: 200,
        },
        },
    },
}));



const NavigationMenu = (props) => {
    const theme = useTheme();
    const classes = useStyles();
    const yukonTheme = useSelector(store => store.app.theme);
    const securityHelper = useSecurityActions();
    const { t } = useTranslation();

    
    function onIdle(event) {
        logOut();
    };
    // 2hr is default user idle time in yukon, which can be customizable as well
    // when that api will be created then timeout value can be used from that api
    useIdleTimer({ onIdle, timeout: 1000 * 60 * 60 * 2})

    const logOut = () => {
        axios.post('/api/logout', {
        }).then((response) => {
            LocalStorage.clearAuthCredentials();
            securityHelper.onUserNotAuthenticated();
        }).catch((error) => {
            // uncomment this line to check custom error from API
            throw new Error (error.response.data.detail) 
        })
    };

    const changePassword = () => {
        securityHelper.showChangePassword();
    };

    return (
        <AppBar position={'sticky'} color={'primary'} 
            style={{backgroundColor: yukonTheme ? yukonTheme.properties.PAGE_BACKGROUND : ''}}>
            <Toolbar> 
                { yukonTheme ? 
                    <img src={'data:image/png;base64,' + yukonTheme.properties.LOGO_IMAGE} alt="customerLogo" style={{paddingRight: theme.spacing(1)}}/>
                    : <div></div>
                }
                <Typography variant="h6" color="inherit">Yukon</Typography>
                <Spacer flex={1} />
                <form acceptCharset="ISO-8859-1" encType="application/x-www-form-urlencoded" method="get"
                    action={props.yukonPath + '/search'} className="yukon-search-form">
                    <div className={classes.search}>
                        <div className={classes.searchIcon}>
                            <SearchIcon />
                        </div>
                        <InputBase
                            name="q"
                            type="search"
                            placeholder={t('menu.search')}
                            classes = {{
                                root: classes.inputRoot,
                                input: classes.inputInput
                            }}
                            inputProps={{ 'aria-label': 'search', className: 'search-field' }}
                        />
                    </div>
                </form>
                <Spacer width={theme.spacing(1)} flex={0} />
                <Badge badgeContent={5} color="error">
                    <NotificationsIcon />
                </Badge>
                <Spacer width={theme.spacing(2)} flex={0} />
                <UserMenu
                    avatar={<Avatar>YU</Avatar>}
                    menuTitle={'Yukon User'}
                    menuSubtitle={'yukon@yukon.com'}
                    menuGroups={[
                        {
                            items: [
                                {
                                    title: t('menu.userProfile'),
                                    icon: <PersonIcon />,
                                    onClick: () => window.location.href = props.yukonPath + "/user/profile",
                                    divider: true,
                                },
                                {
                                    title: t('menu.changePassword'),
                                    icon: <KeyIcon />,
                                    onClick: () => {changePassword()},
                                },
                                {
                                    title: t('menu.logOut'),
                                    icon: <ExitIcon />,
                                    onClick: () => {logOut()},
                                },
                            ],
                        },
                        {
                            title: t('menu.contactUs'),
                            items: [
                                {
                                    title: 'EAS-Support@Eaton.com',
                                    icon: <EmailIcon />,
                                },
                                {
                                    title: '1-800-815-2258',
                                    icon: <PhoneIcon />,
                                },
                            ],
                        },
                    ]}
                />
            </Toolbar>
    </AppBar>
    );
};

export default NavigationMenu;

