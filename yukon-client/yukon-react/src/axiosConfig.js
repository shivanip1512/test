import axios from "axios";

import { store } from "./redux/store";

import * as actions from "./redux/actions/index";

let axiosInstance = axios.create();

// apply interceptor on response for error handling
axiosInstance.interceptors.response.use(
    (response) => {
        const { dispatch } = store;
        dispatch(actions.clearAllAlerts());
        return response;
    },
    (error) => {
        const { dispatch } = store;
        let state = store.getState();
        dispatch(actions.clearAllAlerts());
            if (error.response.data.code === 101000) {
            dispatch(actions.setValidationErrors(error.response.data.errors));
            } else {
            dispatch(actions.setFlashErrors(error.response.data.detail));
            }
        return Promise.reject(error);
    }
);

export default axiosInstance;
