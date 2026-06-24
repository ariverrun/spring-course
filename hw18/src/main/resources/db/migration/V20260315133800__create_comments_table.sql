create table if not exists comments (
    id bigserial,
    book_id bigint references books(id) on delete cascade,
    text varchar(1024),
    primary key (id)
);