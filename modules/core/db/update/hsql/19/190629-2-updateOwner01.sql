alter table DEMO_OWNER add constraint FK_DEMO_OWNER_ON_ADDRESS_CITY foreign key (ADDRESS_CITY_ID) references DEMO_CITY(ID);
