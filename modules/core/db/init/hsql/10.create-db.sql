-- begin DEMO_PET
create table DEMO_PET (
    ID varchar(36) not null,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    NAME varchar(255),
    OWNER_ID varchar(36),
    --
    primary key (ID)
)^
-- end DEMO_PET
-- begin DEMO_VET
create table DEMO_VET (
    ID varchar(36) not null,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    NAME varchar(255),
    --
    primary key (ID)
)^
-- end DEMO_VET
-- begin DEMO_OWNER
create table DEMO_OWNER (
    ID varchar(36) not null,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    ADDRESS_COUNTRY_ID varchar(36),
    ADDRESS_CITY_ID varchar(36),
    ADDRESS_POSTCODE varchar(20),
    ADDRESS_LINE1 varchar(255),
    ADDRESS_LINE2 varchar(255),
    --
    NAME varchar(255),
    CATEGORY_ID varchar(36),
    EMAIL varchar(255),
    --
    primary key (ID)
)^
-- end DEMO_OWNER
-- begin DEMO_VISIT
create table DEMO_VISIT (
    ID varchar(36) not null,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    PET_ID varchar(36) not null,
    VET_ID varchar(36) not null,
    DATE_ date,
    --
    primary key (ID)
)^
-- end DEMO_VISIT
-- begin DEMO_OWNER_CATEGORY
create table DEMO_OWNER_CATEGORY (
    ID varchar(36) not null,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    NAME varchar(255),
    --
    primary key (ID)
)^
-- end DEMO_OWNER_CATEGORY
-- begin DEMO_COUNTRY
create table DEMO_COUNTRY (
    ID varchar(36) not null,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    NAME varchar(255),
    CODE varchar(2),
    --
    primary key (ID)
)^
-- end DEMO_COUNTRY
-- begin DEMO_CITY
create table DEMO_CITY (
    ID varchar(36) not null,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    NAME varchar(255),
    COUNTRY_ID varchar(36),
    --
    primary key (ID)
)^
-- end DEMO_CITY
