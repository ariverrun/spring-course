SET REFERENTIAL_INTEGRITY FALSE;

TRUNCATE TABLE comments;
TRUNCATE TABLE books_genres;
TRUNCATE TABLE books;
TRUNCATE TABLE authors;
TRUNCATE TABLE genres;

ALTER TABLE authors ALTER COLUMN id RESTART WITH 1;
ALTER TABLE genres ALTER COLUMN id RESTART WITH 1;
ALTER TABLE books ALTER COLUMN id RESTART WITH 1;
ALTER TABLE comments ALTER COLUMN id RESTART WITH 1;

SET REFERENTIAL_INTEGRITY TRUE;

insert into authors(full_name)
values ('Author_1'), ('Author_2'), ('Author_3');

insert into genres(name)
values ('Genre_1'), ('Genre_2'), ('Genre_3'),
       ('Genre_4'), ('Genre_5'), ('Genre_6');

insert into books(title, author_id)
values ('BookTitle_1', 1), ('BookTitle_2', 2), ('BookTitle_3', 3);

insert into books_genres(book_id, genre_id)
values (1, 1), (1, 2),
       (2, 3), (2, 4),
       (3, 5), (3, 6);

insert into comments(book_id, text)
values (1, 'Comment_1'), (1, 'Comment_2'),
       (2, 'Comment_3'), (3, 'Comment_4');