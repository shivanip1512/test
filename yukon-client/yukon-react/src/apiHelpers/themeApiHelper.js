import axios from '../axiosConfig';

export const fetchTheme = async () => {
    let themeResp;
    await  axios.get('/api/admin/config/currentTheme')
            .then(themeJson => {
                themeResp = themeJson.data;
            });
        return themeResp;
    };
