alter table Invoice add AuthorizedNumber varchar2(60);
update Invoice set AuthorizedNumber = '';
alter table Invoice modify AuthorizedNumber varchar2(60) not null;