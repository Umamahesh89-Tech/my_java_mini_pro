INSERT INTO users (username, password, full_name, role)
VALUES ('admin', '$2a$10$vxQbSxHqgbcAXybtDmXPY.K5myxlcYNnJZfgDeYQe4ndwXEW6xq2m', 'BankSphere Admin', 'ADMIN')
ON DUPLICATE KEY UPDATE
	password = '$2a$10$vxQbSxHqgbcAXybtDmXPY.K5myxlcYNnJZfgDeYQe4ndwXEW6xq2m',
	full_name = 'BankSphere Admin',
	role = 'ADMIN';
