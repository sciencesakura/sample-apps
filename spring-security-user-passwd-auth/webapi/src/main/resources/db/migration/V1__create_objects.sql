create table user_info
(
  id            uuid         default random_uuid()     not null,
  email_address varchar(255)                           not null,
  password      char(68)                               not null,
  name          varchar(300)                           not null,
  status        integer      default 0                 not null,
  description   varchar(1000),
  created_at    timestamp    default current_timestamp not null,
  created_by    varchar(255) default 'system'          not null,
  updated_at    timestamp    default current_timestamp not null,
  updated_by    varchar(255) default 'system'          not null,
  version       bigint       default 0                 not null,
  primary key (id),
  unique (email_address)
);

create table user_role
(
  user_id    uuid                                   not null,
  role       varchar(50)                            not null,
  created_at timestamp    default current_timestamp not null,
  created_by varchar(255) default 'system'          not null,
  primary key (user_id, role),
  foreign key (user_id) references user_info (id) on delete cascade
);

create table task
(
  id          uuid         default random_uuid()     not null,
  title       varchar(300)                           not null,
  assignee_id uuid                                   not null,
  due_date    date                                   not null,
  priority    integer      default 0                 not null,
  status      integer      default 0                 not null,
  description varchar(1000),
  created_at  timestamp    default current_timestamp not null,
  created_by  varchar(255) default 'system'          not null,
  updated_at  timestamp    default current_timestamp not null,
  updated_by  varchar(255) default 'system'          not null,
  version     bigint       default 0                 not null,
  primary key (id),
  foreign key (assignee_id) references user_info (id) on delete cascade
);
