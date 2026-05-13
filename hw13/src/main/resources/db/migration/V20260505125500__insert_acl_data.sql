INSERT INTO acl_sid (principal, sid) VALUES 
(TRUE, 'user1'),
(TRUE, 'user2'),
(TRUE, 'user3');

INSERT INTO acl_class (class) VALUES 
('ru.otus.hw.models.Author'),
('ru.otus.hw.models.Genre'),
('ru.otus.hw.models.Book'),
('ru.otus.hw.models.Comment');

INSERT INTO acl_object_identity (object_id_class, object_id_identity, parent_object, owner_sid, entries_inheriting) 
SELECT c.id, 1, NULL, s.id, FALSE 
FROM acl_class c, acl_sid s 
WHERE c.class = 'ru.otus.hw.models.Author' AND s.sid = 'user1' AND s.principal = TRUE;

INSERT INTO acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
SELECT oi.id, 1, s.id, 1, TRUE, FALSE, FALSE FROM acl_object_identity oi, acl_sid s
WHERE oi.object_id_identity = 1 AND s.sid = 'user1' AND s.principal = TRUE;
INSERT INTO acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
SELECT oi.id, 2, s.id, 2, TRUE, FALSE, FALSE FROM acl_object_identity oi, acl_sid s
WHERE oi.object_id_identity = 1 AND s.sid = 'user1' AND s.principal = TRUE;
INSERT INTO acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
SELECT oi.id, 3, s.id, 8, TRUE, FALSE, FALSE FROM acl_object_identity oi, acl_sid s
WHERE oi.object_id_identity = 1 AND s.sid = 'user1' AND s.principal = TRUE;

INSERT INTO acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
SELECT oi.id, 4, s.id, 1, TRUE, FALSE, FALSE FROM acl_object_identity oi, acl_sid s
WHERE oi.object_id_identity = 1 AND s.sid = 'user2' AND s.principal = TRUE;

INSERT INTO acl_object_identity (object_id_class, object_id_identity, parent_object, owner_sid, entries_inheriting) 
SELECT c.id, 2, NULL, s.id, FALSE 
FROM acl_class c, acl_sid s 
WHERE c.class = 'ru.otus.hw.models.Author' AND s.sid = 'user1' AND s.principal = TRUE;

INSERT INTO acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
SELECT oi.id, 1, s.id, 1, TRUE, FALSE, FALSE FROM acl_object_identity oi, acl_sid s
WHERE oi.object_id_identity = 2 AND s.sid = 'user1' AND s.principal = TRUE;
INSERT INTO acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
SELECT oi.id, 2, s.id, 2, TRUE, FALSE, FALSE FROM acl_object_identity oi, acl_sid s
WHERE oi.object_id_identity = 2 AND s.sid = 'user1' AND s.principal = TRUE;
INSERT INTO acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
SELECT oi.id, 3, s.id, 8, TRUE, FALSE, FALSE FROM acl_object_identity oi, acl_sid s
WHERE oi.object_id_identity = 2 AND s.sid = 'user1' AND s.principal = TRUE;

INSERT INTO acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
SELECT oi.id, 4, s.id, 1, TRUE, FALSE, FALSE FROM acl_object_identity oi, acl_sid s
WHERE oi.object_id_identity = 2 AND s.sid = 'user2' AND s.principal = TRUE;

INSERT INTO acl_object_identity (object_id_class, object_id_identity, parent_object, owner_sid, entries_inheriting) 
SELECT c.id, 3, NULL, s.id, FALSE 
FROM acl_class c, acl_sid s 
WHERE c.class = 'ru.otus.hw.models.Author' AND s.sid = 'user1' AND s.principal = TRUE;

INSERT INTO acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
SELECT oi.id, 1, s.id, 1, TRUE, FALSE, FALSE FROM acl_object_identity oi, acl_sid s
WHERE oi.object_id_identity = 3 AND s.sid = 'user1' AND s.principal = TRUE;
INSERT INTO acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
SELECT oi.id, 2, s.id, 2, TRUE, FALSE, FALSE FROM acl_object_identity oi, acl_sid s
WHERE oi.object_id_identity = 3 AND s.sid = 'user1' AND s.principal = TRUE;
INSERT INTO acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
SELECT oi.id, 3, s.id, 8, TRUE, FALSE, FALSE FROM acl_object_identity oi, acl_sid s
WHERE oi.object_id_identity = 3 AND s.sid = 'user1' AND s.principal = TRUE;

INSERT INTO acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
SELECT oi.id, 4, s.id, 1, TRUE, FALSE, FALSE FROM acl_object_identity oi, acl_sid s
WHERE oi.object_id_identity = 3 AND s.sid = 'user2' AND s.principal = TRUE;

INSERT INTO acl_object_identity (object_id_class, object_id_identity, parent_object, owner_sid, entries_inheriting) 
SELECT c.id, g.id, NULL, s.id, FALSE 
FROM acl_class c, genres g, acl_sid s 
WHERE c.class = 'ru.otus.hw.models.Genre' AND s.sid = 'user1' AND s.principal = TRUE;

INSERT INTO acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
SELECT oi.id, 1, s.id, 1, TRUE, FALSE, FALSE FROM acl_object_identity oi, acl_sid s
WHERE oi.object_id_class = (SELECT id FROM acl_class WHERE class = 'ru.otus.hw.models.Genre')
AND s.sid = 'user1' AND s.principal = TRUE;

INSERT INTO acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
SELECT oi.id, 2, s.id, 2, TRUE, FALSE, FALSE FROM acl_object_identity oi, acl_sid s
WHERE oi.object_id_class = (SELECT id FROM acl_class WHERE class = 'ru.otus.hw.models.Genre')
AND s.sid = 'user1' AND s.principal = TRUE;

INSERT INTO acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
SELECT oi.id, 3, s.id, 8, TRUE, FALSE, FALSE FROM acl_object_identity oi, acl_sid s
WHERE oi.object_id_class = (SELECT id FROM acl_class WHERE class = 'ru.otus.hw.models.Genre')
AND s.sid = 'user1' AND s.principal = TRUE;

INSERT INTO acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
SELECT oi.id, 4, s.id, 1, TRUE, FALSE, FALSE FROM acl_object_identity oi, acl_sid s
WHERE oi.object_id_class = (SELECT id FROM acl_class WHERE class = 'ru.otus.hw.models.Genre')
AND s.sid = 'user2' AND s.principal = TRUE;

INSERT INTO acl_object_identity (object_id_class, object_id_identity, parent_object, owner_sid, entries_inheriting) 
SELECT c.id, b.id, NULL, s.id, FALSE 
FROM acl_class c, books b, acl_sid s 
WHERE c.class = 'ru.otus.hw.models.Book' AND s.sid = 'user1' AND s.principal = TRUE;

INSERT INTO acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
SELECT oi.id, 1, s.id, 1, TRUE, FALSE, FALSE FROM acl_object_identity oi, acl_sid s
WHERE oi.object_id_class = (SELECT id FROM acl_class WHERE class = 'ru.otus.hw.models.Book')
AND s.sid = 'user1' AND s.principal = TRUE;

INSERT INTO acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
SELECT oi.id, 2, s.id, 2, TRUE, FALSE, FALSE FROM acl_object_identity oi, acl_sid s
WHERE oi.object_id_class = (SELECT id FROM acl_class WHERE class = 'ru.otus.hw.models.Book')
AND s.sid = 'user1' AND s.principal = TRUE;

INSERT INTO acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
SELECT oi.id, 3, s.id, 8, TRUE, FALSE, FALSE FROM acl_object_identity oi, acl_sid s
WHERE oi.object_id_class = (SELECT id FROM acl_class WHERE class = 'ru.otus.hw.models.Book')
AND s.sid = 'user1' AND s.principal = TRUE;

INSERT INTO acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
SELECT oi.id, 4, s.id, 1, TRUE, FALSE, FALSE FROM acl_object_identity oi, acl_sid s
WHERE oi.object_id_class = (SELECT id FROM acl_class WHERE class = 'ru.otus.hw.models.Book')
AND s.sid = 'user2' AND s.principal = TRUE;

INSERT INTO acl_object_identity (object_id_class, object_id_identity, parent_object, owner_sid, entries_inheriting) 
SELECT c.id, cm.id, NULL, s.id, FALSE 
FROM acl_class c, comments cm, acl_sid s 
WHERE c.class = 'ru.otus.hw.models.Comment' AND s.sid = 'user1' AND s.principal = TRUE;

INSERT INTO acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
SELECT oi.id, 1, s.id, 1, TRUE, FALSE, FALSE FROM acl_object_identity oi, acl_sid s
WHERE oi.object_id_class = (SELECT id FROM acl_class WHERE class = 'ru.otus.hw.models.Comment')
AND s.sid = 'user1' AND s.principal = TRUE;

INSERT INTO acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
SELECT oi.id, 2, s.id, 2, TRUE, FALSE, FALSE FROM acl_object_identity oi, acl_sid s
WHERE oi.object_id_class = (SELECT id FROM acl_class WHERE class = 'ru.otus.hw.models.Comment')
AND s.sid = 'user1' AND s.principal = TRUE;

INSERT INTO acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
SELECT oi.id, 3, s.id, 8, TRUE, FALSE, FALSE FROM acl_object_identity oi, acl_sid s
WHERE oi.object_id_class = (SELECT id FROM acl_class WHERE class = 'ru.otus.hw.models.Comment')
AND s.sid = 'user1' AND s.principal = TRUE;

INSERT INTO acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
SELECT oi.id, 4, s.id, 1, TRUE, FALSE, FALSE FROM acl_object_identity oi, acl_sid s
WHERE oi.object_id_class = (SELECT id FROM acl_class WHERE class = 'ru.otus.hw.models.Comment')
AND s.sid = 'user2' AND s.principal = TRUE;

