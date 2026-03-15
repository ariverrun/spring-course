create table if not exists comments (
    id bigserial,
    book_id bigint,
    text varchar(1024),
    primary key (id)
);