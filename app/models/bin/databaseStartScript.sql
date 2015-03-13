BEGIN TRANSACTION;

drop table if exists public.photo;
drop table if exists public.album_user_link;
drop table if exists public.album;
drop table if exists public.user;

create table public.user (
    id serial,
    facebook_id varchar(255) NOT NULL,
    fullname varchar(255) NOT NULL,

    created_at timestamp without time zone not null,
    updated_at timestamp without time zone,
 
    primary key (id)
);

create table public.album (
       id serial,
       user_id int,
       name varchar(128),

       created_at timestamp without time zone not null,
       updated_at timestamp without time zone,

       primary key(id),
       foreign key (user_id) references public.user(id) on delete cascade
);

create table public.photo (
       id serial,
       album_id int,
       blob bytea,
       
       created_at timestamp without time zone not null,
       updated_at timestamp without time zone,
       
       primary key (id),       
       foreign key (album_id) references album(id) on delete cascade
);

create table public.album_user_link (
       id serial,
       album_id int,
       user_id int,

       created_at timestamp without time zone not null,
       updated_at timestamp without time zone,

       primary key (id),
       foreign key (album_id) references album(id) on delete cascade,
       foreign key (user_id) references public.user(id) on delete cascade
);       


END;
