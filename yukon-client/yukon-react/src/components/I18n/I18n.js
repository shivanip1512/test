import React from 'react';
import { useTranslation } from 'react-i18next';

const I18n = (props) => {
    const { t } = useTranslation();

    return (
        <div>{t(props.i18nKey, props.args)}</div>
    );
}

export default I18n;