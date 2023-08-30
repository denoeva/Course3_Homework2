CREATE TABLE driver
(
	id serial PRIMARY KEY,
	car_id INT REFERENCES car (id) NOT NULL,
	name CHAR(50) NOT NULL,
	age INTEGER NOT NULL CHECK (age > 0),
	driver_license boolean
);

CREATE TABLE car
(
	id serial PRIMARY KEY,
	mark CHAR(30) NOT NULL,
	model CHAR(50) NOT NULL,
	price NUMERIC(40,4)NOT NULL
);