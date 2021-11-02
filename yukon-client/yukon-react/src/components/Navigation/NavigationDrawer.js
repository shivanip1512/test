import React, { useCallback, useEffect, useState } from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { useHistory } from 'react-router-dom';

import { Drawer, DrawerHeader, DrawerBody, DrawerNavGroup, DrawerNavItem, DrawerFooter } from '@pxblue/react-components';
import Divider from '@material-ui/core/Divider';

/*Icons*/
import MenuIcon from '@material-ui/icons/Menu';
import DashboardIcon from '@material-ui/icons/Dashboard';
import PowerIcon from '@material-ui/icons/Power';
import UtilityIcon from '@pxblue/icons-mui/Utility';
import RepeatIcon from '@material-ui/icons/Repeat';
import DeviceIcon from '@pxblue/icons-mui/Device';
import SupportIcon from '@pxblue/icons-mui/Support';
import AccountTreeIcon from '@material-ui/icons/AccountTree';
import ExitIcon from '@material-ui/icons/ExitToApp';
import BuildIcon from '@material-ui/icons/Build';
import VerifiedUserIcon from '@material-ui/icons/VerifiedUser';
import DevModeIcon from '@material-ui/icons/DeveloperMode';

import EatonLogo from '../../assets/icons/EatonLogo';
import { useTheme, makeStyles } from '@material-ui/core/styles';

import axios from '../../axiosConfig';
import * as actions from '../../redux/actions/index';

const useStyles = makeStyles(theme => ({
    root: {
        height: 'auto !important'
    }
}));

const NavigationDrawer = (props) => {

    const open = useSelector(store => store.app.drawerOpen);
    const yukonTheme = useSelector(store => store.app.theme);
    const renderDrawer = useSelector(store => store.app.renderDrawer);
    const theme = useTheme();
    const classes = useStyles();
    const dispatch = useDispatch();
    const history = useHistory();

    const onToggleMenu = () => dispatch(actions.toggleDrawer());

    if (process.env.NODE_ENV !== 'development') {
        axios.defaults.baseURL = props.yukonPath;
    }

    useEffect(() => {
        if (!renderDrawer) {
            dispatch(actions.renderDrawer());
        }
            //this will need to be moved to after login - and stored in browser local storage
            //storing in react store gets cleared after every old yukon page since it's counted as a refresh
/*             axios.get('/api/theme')
            .then(themeJson => {
                if (themeJson) {
                    //don't change theme if default theme is used
                    if (themeJson.data.themeId > 0) {
                        //get theme image
                        axios.get('/api/theme/image/' + themeJson.data.properties.LOGO)
                        .then(themeImage => {
                            themeJson.data.properties.LOGO_IMAGE = themeImage.data;
                            dispatch(actions.setTheme(themeJson.data));
                            dispatch(actions.renderDrawer());
                            //Example if we want to change an entire piece of the pxblue theme
                            //theme.palette.primary.main = themeJson.data.properties.PRIMARY_COLOR;
                        });
                    } else {
                        dispatch(actions.renderDrawer());
                    }
                }
            });
        }*/
    }, [renderDrawer]);
    
    const onNavItemClick = useCallback(
        (url) => {
            const reactPage = url.startsWith("/yukon-ui");
            const currentUrlReact = window.location.href.includes('/yukon-ui');
            if (reactPage) {
                if (history && currentUrlReact) {
                    history.push(url);
                } else {
                    window.location.href = props.reactPath + url;
                }
            } else {
                window.location.href = props.yukonPath + url;
            }

        }, [history, props.reactPath, props.yukonPath]
    );

    return (
        renderDrawer ? 
            <Drawer open={open}>
                <DrawerHeader icon={<MenuIcon classes={{root: classes.root}}></MenuIcon>}
                    onIconClick={onToggleMenu} title="Yukon" subtitle="Powered by Brightlayer"
                    style={{backgroundColor: yukonTheme ? yukonTheme.properties.PAGE_BACKGROUND : ""}}>
                </DrawerHeader>
                <DrawerBody>
                    <DrawerNavGroup>
                        <DrawerNavItem
                            itemID="1"
                            title="Dashboard"
                            onClick={() => {onNavItemClick("/dashboard")}}
                            icon={<DashboardIcon/>} />
                        <DrawerNavItem
                            itemID="1-1"
                            title="Dashboard - React"
                            onClick={() => {onNavItemClick("/yukon-ui/dashboard")}}
                            icon={<DashboardIcon/>} />
                        <DrawerNavItem
                            itemID="2"
                            title="AMI" 
                            onClick={() => {onNavItemClick("/meter/start")}}
                            icon={<PowerIcon/>}>
                            <DrawerNavItem itemID="3" title="Dashboard"/>
                            <DrawerNavItem itemID="4" title="Billing"/>
                            <DrawerNavItem itemID="5" title="Meter Programming"/>
                            <DrawerNavItem itemID="5-1" title="AMI Tools">
                                <DrawerNavItem itemID="6" title="Bulk Import"/>
                                <DrawerNavItem itemID="7" title="Bulk Update"/>
                                <DrawerNavItem itemID="8" title="Legacy Importer"/>
                                <DrawerNavItem itemID="9" title="Point Import"/>
                                <DrawerNavItem itemID="10" title="Reports"/>
                            </DrawerNavItem>
                        </DrawerNavItem>
                        <DrawerNavItem 
                            itemID="11" 
                            title="Demand Response" 
                            onClick={() => {onNavItemClick("/dr/home")}}
                            icon={<RepeatIcon/>}>
                            <DrawerNavItem itemID="12" title="Dashboard"/>
                            <DrawerNavItem itemID="13" title="Scenarios"/>
                            <DrawerNavItem itemID="14" title="Control Areas"/>
                            <DrawerNavItem itemID="15" title="Programs"/>
                            <DrawerNavItem itemID="15-1-1" title="Setup" onClick={() => {onNavItemClick("/dr/setup/list")}}/>
                            <DrawerNavItem itemID="15-1" title="Setup - React" onClick={() => {onNavItemClick("/yukon-ui/dr/setup/list")}}/>
                            <DrawerNavItem itemID="15-22" title="Test Page - React" onClick={() => {onNavItemClick("/yukon-ui/dr/setup/test")}}/>
                        </DrawerNavItem>
                        <DrawerNavItem 
                            itemID="16" 
                            title="Volt/Var" 
                            onClick={() => {onNavItemClick("/capcontrol/tier/areas")}}
                            icon={<UtilityIcon/>}/>
                        <DrawerNavItem 
                            itemID="17" 
                            title="Assets" 
                            onClick={() => {onNavItemClick("/stars/operator/inventory/home")}}
                            icon={<DeviceIcon/>}>
                            <DrawerNavItem
                                itemID="17-33"
                                title="Dashboard"
                                onClick={() => {onNavItemClick("/stars/operator/inventory/home")}}/>
                            <DrawerNavItem
                                itemID="17-34"
                                title="Gateways"
                                onClick={() => {onNavItemClick("/stars/gateways")}}/>
                            <DrawerNavItem
                                itemID="17-35"
                                title="Comm Channel - React"
                                onClick={() => {onNavItemClick("/yukon-ui/stars/device/commChannel/create")}}/>
                        </DrawerNavItem>
                        <DrawerNavItem 
                            itemID="17-1" 
                            title="Tools" 
                            onClick={() => {onNavItemClick("/collectionActions/home")}}
                            icon={<BuildIcon/>}/>    
                        <DrawerNavItem 
                            itemID="17-2" 
                            title="Admin" 
                            onClick={() => {onNavItemClick("/admin/config/view")}}
                            icon={<VerifiedUserIcon/>}/>                 
                    </DrawerNavGroup>
                    <Divider/>
                    <DrawerNavGroup titleContent={
                        <div style={{ display: 'flex', justifyContent: 'space-between', fontWeight: 600 }}>
                            <div>Yukon</div>
                            <div>v9.2.0</div>
                        </div>
                    }>                
                        <DrawerNavItem 
                            itemID="19" 
                            title="Support"
                            onClick={() => {onNavItemClick("/support")}} 
                            icon={<SupportIcon/>}/>                    
                        <DrawerNavItem 
                            itemID="20" 
                            title="Site Map"
                            onClick={() => {onNavItemClick("/sitemap")}}
                            icon={<AccountTreeIcon/>}/>                    
                        <DrawerNavItem 
                            itemID="21" 
                            title="Logout"
                            onClick={() => {onNavItemClick("/servlet/LoginController/logout")}} 
                            icon={<ExitIcon/>}/>
                        <DrawerNavItem 
                            itemID="22" 
                            title="Dev Pages"
                            onClick={() => {onNavItemClick("/dev")}} 
                            icon={<DevModeIcon/>}/>                   
                    </DrawerNavGroup>
                </DrawerBody>
                <DrawerFooter>
                    <div style={{ display: 'flex', justifyContent: 'center', padding: theme.spacing(1) }}>
                        <EatonLogo width={'auto'} height={50} style={{ margin: theme.spacing(1) }}/>
                    </div>
                </DrawerFooter>
            </Drawer>
        : <div></div>
    );
}

export default NavigationDrawer;