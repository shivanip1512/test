-- Invoice is missing a column
alter table Invoice add AuthorizedNumber     varchar(60)          not null default '';