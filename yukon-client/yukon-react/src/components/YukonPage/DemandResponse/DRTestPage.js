import React from 'react';

import PageHeader from '../../PageContents/PageHeader';
import PageContents from '../../PageContents/PageContents';
import PageButtons from '../../PageContents/PageButtons';

const DRTestPage = () => {

    const breadcrumbs = [
        { link: '/', title: 'Home' },
        { link: '/dr/home', title: 'Demand Response' }
    ];

    const pageButtons = [
        { label: 'Save' },
        { label: 'Cancel' }
    ]

    return (
        <div>
            <PageHeader breadcrumbs={breadcrumbs} pageTitle="Test Page"/>
            <PageContents>
                This is just a test page using React.
                <PageButtons buttons={pageButtons}/>
            </PageContents>
        </div>
    )
};

export default DRTestPage;