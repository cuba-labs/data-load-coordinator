alter table DEMO_OWNER add constraint FK_DEMO_OWNER_ON_ADDRESS_COUNTRY foreign key (ADDRESS_COUNTRY_ID) references DEMO_COUNTRY(ID);
