WITH new_project AS (
INSERT INTO projects (name)
VALUES ('Practice 4')
    RETURNING id
    )
INSERT INTO tasks (
    title,
    description,
    status,
    project_id
)
SELECT
    sample.title,
    sample.description,
    sample.status,
    new_project.id
FROM new_project
         CROSS JOIN (
    VALUES
        (
            'Learn Java',
            'Review Java basics',
            'TODO'
        ),
        (
            'Learn JPA',
            'Connect entities to PostgreSQL',
            'IN_PROGRESS'
        ),
        (
            'Test CRUD',
            'Check endpoints in Postman',
            'DONE'
        )
) AS sample(title, description, status);

SELECT version, description, type, success
FROM public.flyway_schema_history
ORDER BY installed_rank;


SELECT
    installed_rank,
    version,
    description,
    type,
    script,
    checksum,
    installed_on,
    success
FROM public.flyway_schema_history
ORDER BY installed_rank;

