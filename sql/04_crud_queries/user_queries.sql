USE supermarket_db;

-- QUERY: LOGIN_USER
SELECT
    user_id,
    username,
    password_hash,
    full_name,
    role,
    active
FROM app_users
WHERE username = ?
  AND password_hash = ?
  AND active = TRUE;