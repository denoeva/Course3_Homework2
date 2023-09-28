SELECT s.name, s.age, f.name
FROM student AS s
JOIN faculty AS f ON s.faculty_id = f.id;

SELECT s.name
FROM student AS s
JOIN avatar AS a ON a.student_id = s.id;