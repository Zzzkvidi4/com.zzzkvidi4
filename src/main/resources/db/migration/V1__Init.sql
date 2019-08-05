create table item (
  item_id varchar(36)  not null,
  name    varchar(128) not null,
  code    varchar(128) not null,
  constraint item_pk primary key (item_id),
  constraint item_uq unique (code)
);

create table organization (
  organization_id varchar(36)  not null,
  name            varchar(128) not null,
  itn             varchar(11)  not null,
  account         varchar(64)  not null,
  constraint organization_pk primary key (organization_id),
  constraint organization_name_uq unique (name),
  constraint organization_itn_uq  unique (itn)
);

create table invoice (
  invoice_id      varchar(36) not null,
  date            timestamp   not null,
  organization_id varchar(36) not null,
  constraint invoice_pk primary key (invoice_id),
  constraint invoice_organization_fk foreign key (organization_id) references organization (organization_id)
);

create table invoice_item (
  invoice_item_id varchar(36)    not null,
  item_id         varchar(36)    not null,
  invoice_id      varchar(36)    not null,
  price           integer        not null,
  volume          numeric(20, 3) not null,
  constraint invoice_item_pk primary key (invoice_item_id),
  constraint invoice_item_uq unique (item_id, invoice_id),
  constraint invoice_item_item_fk foreign key (item_id) references item (item_id),
  constraint invoice_item_invoice_fk foreign key (invoice_id) references invoice (invoice_id)
);