CREATE TABLE users(
    id BIGSERIAL NOT NULL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(10) NOT NULL DEFAULT 'USER',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CHECK ( role IN ('ADMIN', 'USER'))
);

CREATE INDEX idx_user_name ON users(username);
CREATE INDEX idx_user_role ON users(role);

CREATE TABLE project(
    id BIGSERIAL NOT NULL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255),
    status VARCHAR(10) NOT NULL DEFAULT 'ACTIVE',
    owner_id BIGINT NOT NULL REFERENCES users(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CHECK ( status IN ('ACTIVE', 'ARCHIVED'))
);

CREATE INDEX idx_project_name ON project(name);
CREATE INDEX idx_project_status ON project(status);

CREATE TABLE project_member(
    id BIGSERIAL NOT NULL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    project_id BIGINT NOT NULL REFERENCES project(id),
    joined_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_project_member_user ON project_member(user_id);
CREATE INDEX idx_project_member_project ON project_member(project_id);

CREATE TABLE task(
    id BIGSERIAL NOT NULL PRIMARY KEY,
    project_id BIGINT NOT NULL REFERENCES project(id),
    responsible_user_id BIGINT NOT NULL REFERENCES users(id),
    title VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    prio VARCHAR(10) NOT NULL,
    status VARCHAR(15) NOT NULL DEFAULT 'TO_DO',
    due_date DATE NOT NULL CHECK ( due_date > CURRENT_TIMESTAMP ),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    CHECK ( prio IN ('LOW', 'MEDIUM', 'HIGH')),
    CHECK ( status IN ('TO_DO', 'IN_PROGRESS', 'DONE', 'CANCELED'))
);

CREATE INDEX idx_task_project ON task(project_id);
CREATE INDEX idx_task_responsible ON task(responsible_user_id);
CREATE INDEX idx_task_prio ON task(prio);
CREATE INDEX idx_task_status ON task(status);