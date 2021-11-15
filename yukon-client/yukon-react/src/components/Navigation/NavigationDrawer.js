import React, { useCallback } from 'react';
import { useTranslation } from 'react-i18next';
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
import BuildIcon from '@material-ui/icons/Build';
import VerifiedUserIcon from '@material-ui/icons/VerifiedUser';
import DevModeIcon from '@material-ui/icons/DeveloperMode';
import NewIcon from '@material-ui/icons/FiberNew';

import EatonLogo from '../../assets/icons/EatonLogo';
import { useTheme, makeStyles } from '@material-ui/core/styles';

import axios from '../../axiosConfig';
import * as actions from '../../redux/actions/index';

import * as menuItems from '../Navigation/menuRoutes';

const useStyles = makeStyles(theme => ({
    root: {
        height: 'auto !important'
    },
    divider: {
        borderTop: '1px solid #e0e0e0'
    }
}));

const NavigationDrawer = (props) => {

    const open = useSelector(store => store.app.drawerOpen);
    const yukonTheme = useSelector(store => store.app.theme);
    const theme = useTheme();
    const classes = useStyles();
    const dispatch = useDispatch();
    const history = useHistory();
    const { t } = useTranslation();

    const onToggleMenu = () => dispatch(actions.toggleDrawer());

    if (process.env.NODE_ENV !== 'development') {
        axios.defaults.baseURL = props.yukonPath;
    }
    
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

    const renderMenuItems = (menuItems) => {
        let renderedMenu = [];
        menuItems.map(menuItem => {
            renderedMenu.push(
                <DrawerNavItem 
                    key={menuItem.titleKey}
                    itemID={menuItem.titleKey}
                    classes={menuItem.dividerBefore ? {root: classes.divider} : {}}
                    title={t(menuItem.titleKey)} 
                    onClick={() => {onNavItemClick(menuItem.link)}}/>);
        })
        return renderedMenu;
    };

    return (
        <Drawer open={open}>
            <DrawerHeader icon={<MenuIcon classes={{root: classes.root}}></MenuIcon>}
                onIconClick={onToggleMenu} title={t('menu.yukon')} subtitle={t('menu.brightlayer')}
                style={{backgroundColor: yukonTheme ? yukonTheme.properties.PAGE_BACKGROUND : ""}}>
            </DrawerHeader>
            <DrawerBody>
                <DrawerNavGroup>
                    <DrawerNavItem
                        itemID="1"
                        title={t('menu.dashboard')}
                        onClick={() => {onNavItemClick("/dashboard")}}
                        icon={<DashboardIcon/>} />
                    <DrawerNavItem
                        itemID="2"
                        title={t('menu.AMI')}
                        onClick={() => {onNavItemClick("/meter/start")}}
                        icon={<PowerIcon/>}>
                            {renderMenuItems(menuItems.AMI_MENU)};
                    </DrawerNavItem>
                    <DrawerNavItem 
                        itemID="3" 
                        title={t('menu.DR')}
                        onClick={() => {onNavItemClick("/dr/home")}}
                        icon={<RepeatIcon/>}>
                            {renderMenuItems(menuItems.DR_MENU)};
                    </DrawerNavItem>
                    <DrawerNavItem 
                        itemID="4" 
                        title={t('menu.VOLTVAR')}
                        onClick={() => {onNavItemClick("/capcontrol/tier/areas")}}
                        icon={<UtilityIcon/>}>
                            {renderMenuItems(menuItems.VOLT_VAR_MENU)};
                    </DrawerNavItem>
                    <DrawerNavItem 
                        itemID="5" 
                        title={t('menu.ASSETS')} 
                        onClick={() => {onNavItemClick("/stars/operator/inventory/home")}}
                        icon={<DeviceIcon/>}>
                            {renderMenuItems(menuItems.ASSETS_MENU)};
                    </DrawerNavItem>
                    <DrawerNavItem 
                        itemID="6" 
                        title={t('menu.TOOLS')}
                        icon={<BuildIcon/>}>
                            {renderMenuItems(menuItems.TOOLS_MENU)};
                    </DrawerNavItem>
                    <DrawerNavItem 
                        itemID="7" 
                        title={t('menu.ADMIN')}
                        icon={<VerifiedUserIcon/>}>
                            {renderMenuItems(menuItems.ADMIN_MENU)};
                    </DrawerNavItem>         
                </DrawerNavGroup>
                <Divider/>
                <DrawerNavGroup titleContent={
                    <div style={{ display: 'flex', justifyContent: 'space-between', fontWeight: 600 }}>
                        <div>{t('menu.yukon')}</div>
                        <div>v9.3.0</div>
                    </div>
                }>                
                    <DrawerNavItem 
                        itemID="8" 
                        title={t('menu.support')}
                        onClick={() => {onNavItemClick("/support")}} 
                        icon={<SupportIcon/>}/>                    
                    <DrawerNavItem 
                        itemID="9" 
                        title={t('menu.sitemap')}
                        onClick={() => {onNavItemClick("/sitemap")}}
                        icon={<AccountTreeIcon/>}/>                    
                    <DrawerNavItem 
                        itemID="10" 
                        title="Dev Pages"
                        onClick={() => {onNavItemClick("/dev")}} 
                        icon={<DevModeIcon/>}/>
                    <DrawerNavItem 
                        itemID="11" 
                        title="React Test Pages"
                        icon={<NewIcon/>}>
                        <DrawerNavItem itemID="1-1" title="Dashboard - React" onClick={() => {onNavItemClick("/yukon-ui/dashboard")}}/>
                        <DrawerNavItem itemID="15-1" title="DR Setup - React" onClick={() => {onNavItemClick("/yukon-ui/dr/setup/list")}}/>
                        <DrawerNavItem itemID="17-35" title="Comm Channel - React" onClick={() => {onNavItemClick("/yukon-ui/stars/device/commChannel/create")}}/>
                        <DrawerNavItem itemID="15-22" title="Test Page - React" onClick={() => {onNavItemClick("/yukon-ui/dr/setup/test")}}/>
                    </DrawerNavItem>
                </DrawerNavGroup>
            </DrawerBody>
            <DrawerFooter>
                <div style={{ display: 'flex', justifyContent: 'center', padding: theme.spacing(1) }}>
                    <EatonLogo width={'auto'} height={50} style={{ margin: theme.spacing(1) }}/>
                </div>
            </DrawerFooter>
        </Drawer>
    );
}

export default NavigationDrawer;