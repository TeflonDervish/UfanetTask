create table client (
    id serial primary key,
    name varchar(255) not null,
    phone varchar(20),
    email varchar(255) not null
);

create table timetable (
    id serial primary key,
	client_id int references client(id),
	dateTime timestamp not null,
	check (
		(EXTRACT(hour from dateTime) between 6 and 23)
	)
);