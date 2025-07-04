-- V3__Seed_admin_user.sql

-- Insert the default admin user
INSERT INTO users (
  first_name, last_name, email,
  password_hash, status, created_at, updated_at
) VALUES (
  'System', 'Administrator', 'admin@securetasker.com',
  '$2a$10$BOVBS/AYgCh0n.zFMdLY1.QjYfLFqkm94pJPMh49wNtTSgbDlylzu',                   -- e.g. $2a$10$EixZaYVK1fsbw1ZfbX3OXe...
  'ACTIVE',
  NOW(),
  NOW()
);

-- Grant ROLE_ADMIN to the new user
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
JOIN roles r ON r.name = 'ROLE_ADMIN'
WHERE u.email = 'admin@securetasker.com'
  AND NOT EXISTS (
    SELECT 1 FROM user_roles ur
    WHERE ur.user_id = u.id
      AND ur.role_id = r.id
  );
