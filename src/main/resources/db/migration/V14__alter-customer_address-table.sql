ALTER TABLE customer_address
DROP CONSTRAINT customer_address_addressid_fkey,
ADD CONSTRAINT customer_address_addressid_fkey FOREIGN KEY (address_id) REFERENCES address (id) ON DELETE CASCADE;