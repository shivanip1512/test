import React, { useCallback } from "react";
import { useTranslation } from "react-i18next";
import { useSelector, useDispatch } from "react-redux";
import { useHistory } from "react-router-dom";

import { Drawer, DrawerHeader, DrawerBody, DrawerNavGroup, DrawerNavItem, DrawerFooter } from "@brightlayer-ui/react-components";
import Divider from "@material-ui/core/Divider";

/*Icons*/
import MenuIcon from "@material-ui/icons/Menu";
import DashboardIcon from "@material-ui/icons/Dashboard";
import PowerIcon from "@material-ui/icons/Power";
import UtilityIcon from "@brightlayer-ui/icons-mui/Utility";
import RepeatIcon from "@material-ui/icons/Repeat";
import DeviceIcon from "@brightlayer-ui/icons-mui/Device";
import SupportIcon from "@brightlayer-ui/icons-mui/Support";
import AccountTreeIcon from "@material-ui/icons/AccountTree";
import BuildIcon from "@material-ui/icons/Build";
import VerifiedUserIcon from "@material-ui/icons/VerifiedUser";
import DevModeIcon from "@material-ui/icons/DeveloperMode";
import NewIcon from "@material-ui/icons/FiberNew";

import EatonLogo from "../../assets/icons/EatonLogo";
import { useTheme, makeStyles } from "@material-ui/core/styles";

import * as actions from "../../redux/actions/index";

import * as menuItems from "../Navigation/menuRoutes";

const useStyles = makeStyles((theme) => ({
    root: {
        height: "auto !important",
    },
    divider: {
        borderTop: "1px solid #e0e0e0",
    },
}));

const NavigationDrawer = (props) => {
    const open = useSelector((store) => store.app.drawerOpen);
    const yukonTheme = useSelector((store) => store.app.theme);

    const theme = useTheme();
    const classes = useStyles();
    const dispatch = useDispatch();
    const history = useHistory();
    const { t } = useTranslation();

    const onToggleMenu = () => dispatch(actions.toggleDrawer());

    // This code is needed to control switching between old vs new pages
    // All old pages will need to do a complete reload
    // Any React pages will also need to do a complete reload if the previous page was not in React
    // If the selected item is a React page and the user is currently on a React page we can just push the new url to history so we don't do a full reload
    const onNavItemClick = useCallback(
        (url) => {
            const reactPage = url.startsWith("/yukon-ui");
            const currentUrlReact = window.location.href.includes("/yukon-ui");
            if (reactPage) {
                if (history && currentUrlReact) {
                    history.push(url);
                } else {
                    window.location.href = props.reactPath + url;
                }
            } else {
                window.location.href = props.yukonPath + url;
            }
        },
        [history, props.reactPath, props.yukonPath]
    );

    const renderMenuItems = (menuItems) => {
        let renderedMenu = [];
        if (menuItems) {
            menuItems.forEach((menuItem) => {
                renderedMenu.push(renderMenuItem(menuItem.titleKey, t(menuItem.titleKey), menuItem.link, menuItem.dividerBefore, null, null));
            });
        }
        return renderedMenu;
    };

    const renderMenuItem = (id, title, link, dividerBefore, icon, menuItems) => {
        return (
            <DrawerNavItem
                key={id}
                itemID={id}
                InfoListItemProps={{ "data-url": link }}
                classes={dividerBefore ? { root: classes.divider } : {}}
                icon={icon != null ? icon : null}
                onClick={() => (link != null ? onNavItemClick(link) : null)}
                title={title}
            >
                {menuItems != null ? renderMenuItems(menuItems) : null}
            </DrawerNavItem>
        );
    };

    return (
        <Drawer open={open}>
            <DrawerHeader
                icon={<MenuIcon classes={{ root: classes.root }}></MenuIcon>}
                onIconClick={onToggleMenu}
                title={t("menu.yukon")}
                subtitle={t("menu.brightlayer")}
                style={{ backgroundColor: yukonTheme ? yukonTheme.properties.PAGE_BACKGROUND : "" }}
            ></DrawerHeader>
            <DrawerBody>
                <DrawerNavGroup>
                    {renderMenuItem("1", t("menu.dashboard"), "/dashboard", false, <DashboardIcon />)};
                    {renderMenuItem("2", t("menu.AMI"), "/meter/start", false, <PowerIcon />, menuItems.AMI_MENU)};
                    {renderMenuItem("3", t("menu.DR"), "/dr/home", false, <RepeatIcon />, menuItems.DR_MENU)};
                    {renderMenuItem("4", t("menu.VOLTVAR"), "/capcontrol/tier/areas", false, <UtilityIcon />, menuItems.VOLT_VAR_MENU)};
                    {renderMenuItem("5", t("menu.ASSETS"), "/stars/operator/home", false, <DeviceIcon />, menuItems.ASSETS_MENU)};
                    {renderMenuItem("6", t("menu.TOOLS"), null, false, <BuildIcon />, menuItems.TOOLS_MENU)};
                    {renderMenuItem("7", t("menu.ADMIN"), null, false, <VerifiedUserIcon />, menuItems.ADMIN_MENU)};
                </DrawerNavGroup>
                <Divider />
                <DrawerNavGroup
                    titleContent={
                        <div style={{ display: "flex", justifyContent: "space-between", fontWeight: 600 }}>
                            <div>{t("menu.yukon")}</div>
                            <div>v9.3.0</div>
                        </div>
                    }
                >
                    {renderMenuItem("8", t("menu.support"), "/support", false, <SupportIcon />)};
                    {renderMenuItem("9", t("menu.sitemap"), "/sitemap", false, <AccountTreeIcon />)};
                    {renderMenuItem("10", "Dev Pages", "/dev", false, <DevModeIcon />)};
                    <DrawerNavItem itemID="11" title="React Test Pages" icon={<NewIcon />}>
                        {renderMenuItem("11-1", "Dashboard - React", "/yukon-ui/dashboard")};
                        {renderMenuItem("11-2", "DR Setup - React", "/yukon-ui/dr/setup/list")};
                        {renderMenuItem("11-3", "Comm Channel - React", "/yukon-ui/stars/device/commChannel/create")};
                        {renderMenuItem("11-4", "Test Page - React", "/yukon-ui/dr/setup/test")};
                    </DrawerNavItem>
                </DrawerNavGroup>
            </DrawerBody>
            <DrawerFooter>
                <div style={{ display: "flex", justifyContent: "center", padding: theme.spacing(1) }}>
                    <EatonLogo width={"auto"} height={50} style={{ margin: theme.spacing(1) }} />
                </div>
            </DrawerFooter>
        </Drawer>
    );
};

export default NavigationDrawer;
