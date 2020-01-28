CREATE TABLE user (
	_id INTEGER PRIMARY KEY AUTOINCREMENT,
	name VARCHAR(255),
	email VARCHAR(255),
	pass VARCHAR(64),
	type INTEGER,
	order_number INTEGER,
	price_sum FLOAT,
	distance_sum FLOAT, -- km
	status INTEGER, -- 0. rest 1. delivering
	cur_order_id INTEGER
);

CREATE TABLE orders (
	_id INTEGER PRIMARY KEY AUTOINCREMENT,
	title VARCHAR(128),
	detail VARCHAR(500),
	pickup_latitude FLOAT,
	pickup_longitude FLOAT,
	delivery_latitude FLOAT,
	delivery_longitude FLOAT,
	status INTEGER, -- 0. haven't be chosen 1. price successfully set 2. pending pickup 3. picked-up 4. delivered
	deliveryman_id INTEGER,
	deliveryman_name VARCHAR(255),
	price FLOAT,
	distance FLOAT -- km
);