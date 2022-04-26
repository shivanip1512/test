import React, { Suspense } from "react";

import NavigationMenu from "./NavigationMenu";
import NavigationDrawer from "./NavigationDrawer";
import { ThemeProvider, createTheme } from "@material-ui/core/styles";
import * as PXBThemes from "@brightlayer-ui/react-themes";
import { DrawerLayout } from "@brightlayer-ui/react-components";
import { Provider } from "react-redux";

import { I18nextProvider } from "react-i18next";
import { SecurityContextProvider, AuthNavigationContainer } from "@brightlayer-ui/react-auth-workflow";
import AuthUIConfiguration from "../security/AuthUIConfiguration";
import { routes } from "../../constants/routing";
import yukoni18n from "../I18n/i18nConfig";

import { makeStyles, useTheme } from "@material-ui/core/styles";

import { store } from "../../redux/store";

import i18n from "i18next";

const useStyles = makeStyles((theme) => ({
    drawer: {
        top: 0,
    },
}));

const NavigationContainer = (props) => {
    const theme = useTheme();
    const classes = useStyles();

    return (
        <Provider store={store}>
            <I18nextProvider i18n={i18n}>
                <ThemeProvider theme={createTheme(PXBThemes.blue)}>
                    <Suspense fallback={<div></div>}>
                        <SecurityContextProvider>
                            <AuthUIConfiguration>
                                <AuthNavigationContainer routeConfig={routes}>
                                    <I18nextProvider i18n={yukoni18n}>
                                        <DrawerLayout
                                            drawer={<NavigationDrawer yukonPath={props.path} reactPath={props.reactPath} />}
                                            classes={{ drawer: classes.drawer }}
                                        >
                                            <NavigationMenu yukonPath={props.path} reactPath={props.reactPath} />
                                            <div id="page-contents"></div>
                                        </DrawerLayout>
                                    </I18nextProvider>
                                </AuthNavigationContainer>
                            </AuthUIConfiguration>
                        </SecurityContextProvider>
                    </Suspense>
                </ThemeProvider>
            </I18nextProvider>
        </Provider>
    );
};

$(document).ready(function () {
    //move contents from yukon page to inside drawer
    const header = document.getElementsByClassName("yukon-header")[0];
    const pageContent = document.getElementsByClassName("yukon-content")[0];
    const drawerLayout = document.getElementById("page-contents");
    drawerLayout.appendChild(header);
    drawerLayout.appendChild(pageContent);
});

import(/* webpackChunkName: "react-dom" */ "react-dom").then((ReactDom) => {
    const navigationElement = document.getElementById("navigation");
    const path = navigationElement.getAttribute("data-path");
    const reactPath = navigationElement.getAttribute("data-react-path");
    ReactDom.render(<NavigationContainer path={path} reactPath={reactPath} />, navigationElement);
});
