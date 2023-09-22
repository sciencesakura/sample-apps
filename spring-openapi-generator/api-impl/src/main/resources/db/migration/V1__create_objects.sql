create table item
(
  id          uuid   default random_uuid() not null,
  code        char(8)                      not null,
  name        varchar(200)                 not null,
  description varchar(1000),
  price       decimal(10, 2)               not null,
  currency    char(3)                      not null,
  version     bigint default 0             not null,
  primary key (id),
  unique (code)
);

create table item_stock
(
  item_id  uuid             not null,
  quantity decimal(10, 2)   not null,
  version  bigint default 0 not null,
  primary key (item_id),
  foreign key (item_id) references item (id) on delete cascade
);
