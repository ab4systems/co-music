-- :name create-user! :! :n
-- :doc creates a new user record
INSERT INTO users
(id, first_name, last_name, email, pass)
VALUES (:id, :first_name, :last_name, :email, :pass)

-- :name update-user! :! :n
-- :doc update an existing user record
UPDATE users
SET first_name = :first_name, last_name = :last_name, email = :email
WHERE id = :id

-- :name get-user :? :1
-- :doc retrieve a user given the id.
SELECT * FROM users
WHERE id = :id

-- :name delete-user! :! :n
-- :doc delete a user given the id
DELETE FROM users
WHERE id = :id;

-- :name save-harmblock! :! :n
-- :doc creates a new harmonic block
INSERT INTO harmblock
(harmblock_block)
VALUES (:harmblock_block)

-- :name get-harmblocks :? :*
-- :doc get all harmonic blocks
SELECT * FROM harmblock

-- :name get-harmblock-block :? :1
-- :doc retrieve a harmonic block's block given the id
SELECT harmblock_block
FROM harmblock
WHERE harmblock_id = :mihai

-- :name delete-harmblock! :! :n
-- :doc delete a harmblock given the id
DELETE FROM harmblock
WHERE harmblock_id = :harmblock_id;

-- :name save-absrhythmblock! :! :n
-- :doc creates a new abstract rhythm block
INSERT INTO absrhythmblock
(absrhythmblock_block)
VALUES (:absrhythmblock_block)

-- :name get-absrhythmblocks :? :*
-- :doc get all abstract rhythm blocks
SELECT * FROM absrhythmblock

-- :name get-absrhythmblock-block :? :1
-- :doc retrieve an abstract rhythm block's block given the id
SELECT absrhythmblock_block
FROM absrhythmblock
WHERE absrhythmblock_id = :irina

-- :name delete-absrhythmblock! :! :n
-- :doc delete an absrhythmblock given the id
DELETE FROM absrhythmblock
WHERE absrhythmblock_id = :absrhythmblock_id

-- :name save-musicblock! :! :n
-- :doc creates a new music block from a harmonic block and an abstract rhythm block
INSERT INTO musicblock
(harmblock_id, absrhythmblock_id, musicblock_block)
VALUES (:harmblock_id, :absrhythmblock_id, :musicblock_block)

-- :name get-musicblocks :? :*
-- :doc retrieve all music blocks
SELECT * FROM musicblock

-- :name get-musicblock-block :? :1
-- :doc retrieve a music block's block given the id
SELECT musicblock_block
FROM musicblock
WHERE harmblock_id = :harmblock_id AND absrhythmblock_id = :absrhythmblock_id

-- :name delete-musicblock! :! :n
-- :doc delete a musicblock given the id (harmblock id and absrhythmblock id)
DELETE FROM musicblock
WHERE harmblock_id = :harmblock_id AND absrhythmblock_id = :absrhythmblock_id
