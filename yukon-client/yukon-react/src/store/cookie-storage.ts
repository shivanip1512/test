import { LOCAL_USER_DATA, REMEMBER_ME_DATA } from "../constants";
import Cookies from "universal-cookie";

type AuthData = {
    userId: string | undefined;
    email: string | undefined;
    rememberMeData: { user: string; rememberMe: boolean };
};

async function readAuthData(): Promise<AuthData> {
    const cookies = new Cookies();
    const jsonUserData = cookies.get(LOCAL_USER_DATA) || "{}";
    const jsonRememberMeData = cookies.get(REMEMBER_ME_DATA) || "{}";
    return {
        userId: jsonUserData.userId,
        email: jsonUserData.user,
        rememberMeData: jsonRememberMeData,
    };
}

function saveAuthCredentials(user: string, userId: string): void {
    const userData = {
        user: user,
        userId: userId,
    };
    const authCookie = new Cookies();
    authCookie.set(LOCAL_USER_DATA, JSON.stringify(userData), { path: "/" });
    console.log(authCookie.get(LOCAL_USER_DATA));
}

function saveRememberMeData(user: string, rememberMe: boolean): void {
    const RememberMeData = {
        user: rememberMe ? user : "",
        rememberMe: rememberMe,
    };
    const rememberMeCookie = new Cookies();
    rememberMeCookie.set(REMEMBER_ME_DATA, JSON.stringify(RememberMeData), { path: "/" });
    console.log(rememberMeCookie.get(REMEMBER_ME_DATA));
}
function clearAuthCredentials(): void {
    const cookies = new Cookies();
    cookies.remove(LOCAL_USER_DATA, { path: "/" });
    console.log(cookies.getAll());
}
function clearRememberMeData(): void {
    const cookies = new Cookies();
    cookies.remove(REMEMBER_ME_DATA, { path: "/" });
    console.log(cookies.getAll());
}
export const CookieStorage = {
    readAuthData,
    saveAuthCredentials,
    saveRememberMeData,
    clearAuthCredentials,
    clearRememberMeData,
};
