ALTER TABLE customer_address
DROP CONSTRAINT customer_address_customerid_fkey,
ADD CONSTRAINT customer_address_customerid_fkey FOREIGN KEY (customer_id) REFERENCES customer (id) ON DELETE CASCADE;