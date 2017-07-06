CREATE TABLE musicblock
 (harmblock_id integer REFERENCES harmblock ON DELETE RESTRICT,
  absrhythmblock_id integer REFERENCES absrhythmblock ON DELETE RESTRICT,
  musicblock_block varchar(50),
  PRIMARY KEY (harmblock_id, absrhythmblock_id));