alter table Invoice add AuthorizedNumber varchar(60);
update Invoice set AuthorizedNumber = '';
alter table Invoice modify AuthorizedNumber varchar(60) not null;