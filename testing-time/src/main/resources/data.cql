USE mykeyspace;
CREATE TABLE IF NOT EXISTS person(person_id TEXT PRIMARY KEY, name TEXT, age INT);
INSERT INTO person(person_id, name, age) VALUES('id-1', 'test', 29);
