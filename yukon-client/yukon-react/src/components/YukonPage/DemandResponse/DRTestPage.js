import React from 'react';

import PageHeader from '../../PageContents/PageHeader';

const DRTestPage = () => {

    const breadcrumbs = [
        { link: '/', title: 'Home' },
        { link: '/dr/home', title: 'Demand Response' }
    ];

    return (
        <PageHeader breadcrumbs={breadcrumbs} pageTitle="Test Page"/>
    )
};

export default DRTestPage;