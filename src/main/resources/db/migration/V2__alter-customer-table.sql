ALTER TABLE customer
ADD COLUMN genderId INT REFERENCES gender(id);