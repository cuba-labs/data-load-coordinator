alter table DEMO_OWNER add constraint FK_DEMO_OWNER_ON_CATEGORY foreign key (CATEGORY_ID) references DEMO_OWNER_CATEGORY(ID);
create index IDX_DEMO_OWNER_ON_CATEGORY on DEMO_OWNER (CATEGORY_ID);