CREATE TABLE customers (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    phone_number VARCHAR(15),
    email VARCHAR(100)
);

-- Create the Order table
CREATE TABLE orders (
    order_number SERIAL PRIMARY KEY,
    customer_id INT REFERENCES customers(id),
    item VARCHAR(50) NOT NULL
);