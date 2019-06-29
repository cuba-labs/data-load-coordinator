alter table DEMO_CITY add constraint FK_DEMO_CITY_ON_COUNTRY foreign key (COUNTRY_ID) references DEMO_COUNTRY(ID);
create index IDX_DEMO_CITY_ON_COUNTRY on DEMO_CITY (COUNTRY_ID);
