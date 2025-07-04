-- Insert core roles
INSERT INTO roles (name, description) VALUES
  ('ROLE_USER', 'Default role for all new users'),
  ('ROLE_ADMIN', 'Administrator with full access');

-- Insert permissions
INSERT INTO permissions (name, description) VALUES
  ('READ_TASK', 'Permission to read tasks'),
  ('WRITE_TASK', 'Permission to create tasks'),
  ('UPDATE_TASK', 'Permission to update tasks'),
  ('DELETE_TASK', 'Permission to delete tasks'),
  ('MANAGE_USERS', 'Permission to manage users');

-- Assign permissions to ROLE_USER
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'ROLE_USER'
  AND p.name IN ('READ_TASK');

-- Assign all permissions to ROLE_ADMIN
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'ROLE_ADMIN';