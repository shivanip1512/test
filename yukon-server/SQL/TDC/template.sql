create table Template (
                TemplateNum     INTEGER NOT NULL,
                Name            VARCHAR2(40) NOT NULL,
                Description     VARCHAR2(200),
                PRIMARY KEY (TemplateNum) )
/

insert into template values( 1, 'Standard', 'First Standard Cannon Template');
insert into template values( 2, 'Standard - No PtName', 'First Standard Cannon Template');
insert into template values( 3, 'Standard - No DevName', 'First Standard Cannon Template');