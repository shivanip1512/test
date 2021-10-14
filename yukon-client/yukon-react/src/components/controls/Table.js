import React from 'react';
import PropTypes from 'prop-types';

import { Table as MuiTable, TableHead, TableSortLabel, TableBody, TableRow, TableCell, TableFooter, TablePagination } from '@material-ui/core';

const Table = (props) => {

    const { headers, rows, totalCount, rowsPerPage, page, onPageChange, onRowChange } = props;
    
    Table.propTypes = {
        headers: PropTypes.array,       //array of headers with label, active, direction and onClick defined
        rows: PropTypes.array,          //array of rows with id and array of column data
        totalCount: PropTypes.number,   //total count for results
        rowsPerPage: PropTypes.number,  //rows to display per page
        page: PropTypes.number,         //page number to display
        onPageChange: PropTypes.func,   //function to call when page changes
        onRowChange: PropTypes.func     //function to call when rows per page changes
    }

    return (
        <MuiTable size="small">
            <TableHead>
                <TableRow>
                    {headers.map((header, index) => {
                        return (
                            <TableCell key={index}>
                                <TableSortLabel 
                                            active={header.active} 
                                            direction={header.direction} 
                                            onClick={header.onClick}>{header.label}</TableSortLabel>
                            </TableCell>
                        )
                    })}
                </TableRow>
            </TableHead>
            <TableBody>
                {rows.map((row) => {
                    return <TableRow hover={true} key={row.id}>
                        {row.columns.map((column, index) => {
                            return <TableCell key={index}>{column}</TableCell>
                        })}
                    </TableRow>
                })}
            </TableBody>
            <TableFooter>
                <TableRow>
                    <TablePagination
                        rowsPerPageOptions={[10, 25, 50, 100, 250, 500]}
                        colSpan={rows.length > 0 ? rows[0].columns.length : 0}
                        count={totalCount}
                        rowsPerPage={rowsPerPage}
                        page={page}
                        onPageChange={onPageChange}
                        onRowsPerPageChange={onRowChange}
                    />
                </TableRow>
            </TableFooter>
        </MuiTable>
    );
};

export default Table;