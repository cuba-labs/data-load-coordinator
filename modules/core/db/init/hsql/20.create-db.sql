-- begin DEMO_PET
alter table DEMO_PET add constraint FK_DEMO_PET_ON_OWNER foreign key (OWNER_ID) references DEMO_OWNER(ID)^
create index IDX_DEMO_PET_ON_OWNER on DEMO_PET (OWNER_ID)^
-- end DEMO_PET
-- begin DEMO_VISIT
alter table DEMO_VISIT add constraint FK_DEMO_VISIT_ON_PET foreign key (PET_ID) references DEMO_PET(ID)^
alter table DEMO_VISIT add constraint FK_DEMO_VISIT_ON_VET foreign key (VET_ID) references DEMO_VET(ID)^
create index IDX_DEMO_VISIT_ON_PET on DEMO_VISIT (PET_ID)^
create index IDX_DEMO_VISIT_ON_VET on DEMO_VISIT (VET_ID)^
-- end DEMO_VISIT
-- begin DEMO_OWNER
alter table DEMO_OWNER add constraint FK_DEMO_OWNER_ON_CATEGORY foreign key (CATEGORY_ID) references DEMO_OWNER_CATEGORY(ID)^
alter table DEMO_OWNER add constraint FK_DEMO_OWNER_ON_ADDRESS_COUNTRY foreign key (ADDRESS_COUNTRY_ID) references DEMO_COUNTRY(ID)^
alter table DEMO_OWNER add constraint FK_DEMO_OWNER_ON_ADDRESS_CITY foreign key (ADDRESS_CITY_ID) references DEMO_CITY(ID)^
create index IDX_DEMO_OWNER_ON_CATEGORY on DEMO_OWNER (CATEGORY_ID)^
create index IDX_DEMO_OWNER_ON_ADDRESS_COUNTRY on DEMO_OWNER (ADDRESS_COUNTRY_ID)^
create index IDX_DEMO_OWNER_ON_ADDRESS_CITY on DEMO_OWNER (ADDRESS_CITY_ID)^
-- end DEMO_OWNER
-- begin DEMO_CITY
alter table DEMO_CITY add constraint FK_DEMO_CITY_ON_COUNTRY foreign key (COUNTRY_ID) references DEMO_COUNTRY(ID)^
create index IDX_DEMO_CITY_ON_COUNTRY on DEMO_CITY (COUNTRY_ID)^
-- end DEMO_CITY
