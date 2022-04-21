import ReactDOM from 'react-dom';
import Table from '../../../components/controls/Table';
import { useState } from 'react';

jest.mock('react-redux', () => ({
    useSelector: jest.fn(fn => fn()),
  }));

const tableHeaders = [
    { label: 'Name'},
    { label: 'Type'}
]

const tableRows = [];

const handlePageChange = (event, newPage) => {
    setPage(newPage);
};

const handleRowChange = (event) => {
    setRowsPerPage(event.target.value);
    setPage(0);
};

it("render Table without crashing", ()=>{
    const div = document.createElement("div");
    ReactDOM.render(
        <Table  
            headers={tableHeaders} 
            rows={tableRows} 
            rowsPerPage={25}  
            page={1}
            totalCount={50}
            onPageChange={handlePageChange} 
            onRowChange={handleRowChange}>
        </Table>
    , div)
})