/* eslint-disable @typescript-eslint/no-unused-vars */
import { AuthUIActions, SecurityContextActions } from '@brightlayer-ui/react-auth-workflow';
import { LocalStorage } from '../store/local-storage';
import axios from 'axios';

const sleep = (ms: number): Promise<void> => new Promise((resolve) => setTimeout(resolve, ms));

function getRandomInt(max: number): number {
    return Math.floor(Math.random() * Math.floor(max));
}

function isRandomFailure(): boolean {
    const randomResponseNumber = getRandomInt(100);
    return false; // randomResponseNumber < 10;
}

type AuthUIActionsFunction = () => AuthUIActions;
type AuthUIActionsWithSecurity = (securityHelper: SecurityContextActions) => AuthUIActionsFunction;

/**
 * Example implementation of [[AuthUIActions]] to start with during development.
 *
 * Authentication Actions to be performed based on the user's UI actions. The application will create
 * appropriate actions (often api calls, local network storage, credential updates, etc) and update
 * the global security state based on the actionable needs of the user.
 */
export const ProjectAuthUIActions: AuthUIActionsWithSecurity = (securityHelper) => (): AuthUIActions => ({
    /**
     * Initialize the application security state. This will involve reading any local storage,
     * validating existing credentials (token expiration, for example). At the end of validation,
     * the [[SecurityContextActions]] should be called with either:
     * [[onUserAuthenticated]] (which will present the application), or
     * [[onUserNotAuthenticated]] (which will present the Auth UI).
     *
     * Note: Until this method returns, the applications Splash screen will be presented.
     *
     * @returns Should always resolve. Never throw.
     */
    initiateSecurity: async (): Promise<void> => {
        let authData;

        try {
            authData = await LocalStorage.readAuthData();
        } catch (e) {
            // Restoring token failed
        }

        // After restoring token, we may need to validate it in production apps
        // This will switch to the App screen or Auth screen and this loading
        // screen will be unmounted and thrown away.
        // securityHelper.onUserAuthenticated()
        if (authData?.email !== undefined) {
            securityHelper.onUserAuthenticated({
                email: authData?.email,
                userId: authData.userId ?? '',
                rememberMe: authData?.rememberMeData.rememberMe,
            });
        } else {
            const rememberMeEmail = authData?.rememberMeData.rememberMe ? authData?.rememberMeData.user : undefined;
            securityHelper.onUserNotAuthenticated(false, rememberMeEmail);
        }
    },
    /**
     * The user wants to log into the application. Perform a login with the user's credentials.
     * The application should provide the user's email and password to the authentication server.
     *
     * In the case of valid credentials, the applications code should store the returned data
     * (such as token, user information, etc.). Then the [[onUserAuthenticated]] function should
     * be called on the [[SecurityContextActions]] object.
     *
     * For example:
     * ```
     * LocalStorage.saveAuthCredentials(email, email);
     * LocalStorage.saveRememberMeData(email, rememberMe);
     *
     * securityHelper.onUserAuthenticated({ email: email, userId: email, rememberMe: rememberMe });
     * ```
     *
     * In the case of invalid credentials, an error should be thrown.
     *
     * @param email Email address the user entered into the UI.
     * @param password Password the user entered into the UI.
     * @param rememberMe Indicates whether the user's email should be remembered on success.
     *
     * @returns Resolve if code is credentials are valid, otherwise reject.
     */
    logIn: async (email: string, password: string, rememberMe: boolean): Promise<void> => {
        
        axios.post('/api/token', {
            username: email,
            password: password
        }).then((response:any) => {
            LocalStorage.saveAuthCredentials(email, email);
            LocalStorage.saveRememberMeData(email, rememberMe);
        })
        .catch(function (error) {
            console.log("error", error);
            //handle error here
            // reject(new Error('LOGIN.GENERIC_ERROR'));
                throw new Error('ERRORS.LOGIN.INVALID_CREDENTIALS');
        });

        

        //dont want to set username /pwd in local storage
        //LocalStorage.saveAuthCredentials(email, email);
        //LocalStorage.saveRememberMeData(email, rememberMe);

        //get the theme and store in browser local storage
        //storing in react store gets cleared after every old yukon page since it's counted as a refresh
/*      axios.get('/api/theme')
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

        securityHelper.onUserAuthenticated({ email: email, userId: email, rememberMe: rememberMe });

        //it looks like we'll need to reload the page if it's not a react page - I could not figure out how to get the URL it should go to after log in
    },
    /**
     * The user has forgotten their password and wants help.
     * The application generally should call an API which will then send a password reset
     * link to the user's email.
     *
     * @param email Email address the user entered into the UI.
     *
     * @returns Resolve if email sending was successful, otherwise reject.
     */
    forgotPassword: async (email: string): Promise<void> => {
        await sleep(500);
        if (isRandomFailure()) {
            throw new Error('Sorry, there was a problem sending your request.');
        }

        return;
    },
    /**
     * The user has tapped on an email with a password reset link, which they received after
     * requesting help for forgetting their password.
     * The application should take the password reset code and then verify that it is still
     * valid.
     *
     * @param code Password reset code from a reset password link.
     * @param email Email if it was passed from the reset link
     *
     * @returns Resolve if code is valid, otherwise reject.
     */
    verifyResetCode: async (code: string, email?: string): Promise<void> => {
        await sleep(500);
        if (isRandomFailure()) {
            throw new Error('Sorry, there was a problem sending your request.');
        }

        return;
    },
    /**
     * A user who has previously used "forgotPassword" now has a valid password reset code
     * and has entered a new password.
     * The application should take the user's password reset code and the newly entered
     * password and then reset the user's password.
     *
     * Note: Upon success, the user will be taken to the Login screen.
     *
     * @param code Password reset code from a link
     * @param password New Password the user entered into the UI
     * @param email Email if it was passed from the reset link
     *
     * @returns Resolve if successful, otherwise reject with an error message.
     */
    setPassword: async (code: string, password: string, email?: string): Promise<void> => {
        await sleep(500);
        if (isRandomFailure()) {
            throw new Error('Sorry, there was a problem sending your request.');
        }

        return;
    },
    /**
     * An authenticated user wants to change their password.
     * The application should try to change the user's password. Upon completion,
     * the user will be logged out of the application. Upon cancellation, the user
     * will be taken back to the application's home screen.
     *
     * @param oldPassword The user's current password as entered into the UI.
     * @param newPassword The user's new password as entered into the UI.
     *
     * @returns Resolve if successful, otherwise reject with an error message.
     */
    changePassword: async (oldPassword: string, newPassword: string): Promise<void> => {
        await sleep(1000);

        if (isRandomFailure()) {
            throw new Error('Sorry, there was a problem sending your request.');
        }

        return;
    },
});
