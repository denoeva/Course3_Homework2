-- liquibase formatted sql

-- changeset e.denoeva:1
CREATE index ind_students ON student(name);
CREATE index ind_faculties ON faculty(name,color);