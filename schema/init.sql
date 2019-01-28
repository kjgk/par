INSERT INTO sys_user (
  objectid,
  created_date,
  last_update_date,
  deleted,
  description,
  name,
  created_by,
  last_updated_by
)
VALUES
  (1000, NOW(), NOW(), 0, NULL, '系统', 1000, 1000),
  (2000, NOW(), NOW(), 0, NULL, '管理员', 1000, 1000)
  ;

INSERT INTO sys_account (
  objectid,
  created_date,
  last_update_date,
  deleted,
  description,
  name,
  password,
  status,
  created_by,
  last_updated_by,
  user_id
)
VALUES
  (537639598403092500, now(), now(), 0, null, 'admin', '$2a$10$G9o4sIwbl2O7wkIsI1Bx0e42wTC7MRDIIjsmkV/C4ykznlVy7OBQW', null, 1000, 1000, 2000)
  ;

INSERT INTO sys_authority (
  objectid,
  created_date,
  last_update_date,
  deleted,
  description,
  name,
  tag,
  created_by,
  last_updated_by
)
VALUES
  (537638442280943617, now(), now(), 0, null, 'ADMIN', 'ROLE_ADMIN', 1000, 1000),
  (537638442280943618, now(), now(), 0, null, 'USER', 'ROLE_USER', 1000, 1000)
   ;

INSERT INTO sys_role (
  objectid,
  created_date,
  last_update_date,
  deleted,
  description,
  name,
  tag,
  created_by,
  last_updated_by
)
VALUES
  (537638442285137921, now(), now(), 0, null, '管理员', 'Admin', 1000, 1000),
  (537638442285137922, now(), now(), 0, null, '普通用户', 'Normal', 1000, 1000)
   ;

INSERT INTO sys_menu (
  objectid,
  created_date,
  last_update_date,
  deleted,
  description,
  name,
  order_no,
  enabled,
  hidden,
  icon,
  tag,
  url,
  created_by,
  last_updated_by,
  parent_id
)
VALUES
  (537638442289332224, now(), now(), 0, null, '菜单', 1, 1, 0, null, 'Root', null, 1000, 1000, null),
  (537638442289332225, now(), now(), 0, null, '系统管理', 1, 1, 0, 'setting', null, '/system', 1000, 1000, 537638442289332224),
  (537638442289332226, now(), now(), 0, null, '用户', 1, 1, 0, 'user', null, '/system/user', 1000, 1000, 537638442289332225),
  (537638442289332227, now(), now(), 0, null, '角色', 2, 1, 0, 'trademark', null, '/system/role', 1000, 1000, 537638442289332225),
  (537638442289332228, now(), now(), 0, null, '字典', 3, 1, 0, 'profile', null, '/system/code', 1000, 1000, 537638442289332225),
  (537638442289332229, now(), now(), 0, null, '菜单', 4, 1, 0, 'link', null, '/system/menu', 1000, 1000, 537638442289332225),
  (537638442289332230, now(), now(), 0, null, '权限', 5, 1, 0, 'rocket', null, '/system/authority', 1000, 1000, 537638442289332225)
   ;


INSERT INTO sys_code (
  objectid,
  created_date,
  last_update_date,
  deleted,
  description,
  name,
  order_no,
  tag,
  created_by,
  last_updated_by,
  parent_id
)
VALUES
  (537638442293526528, now(), now(), 0, null, '字典', 1, 'Root', 1000, 1000, null)
  ;


INSERT INTO sys_roleauthority (
  objectid,
  authority_id,
  role_id
)
VALUES
  (537639598394703872, 537638442280943617, 537638442285137921),
  (537639598394703873, 537638442280943618, 537638442285137921),
  (537639598394703874, 537638442280943618, 537638442285137922)
  ;

INSERT INTO sys_rolemenu (objectid, menu_id, role_id)
VALUES
  (537639598398898176, 537638442289332225, 537638442285137921),
  (537639598398898177, 537638442289332226, 537638442285137921),
  (537639598398898178, 537638442289332227, 537638442285137921),
  (537639598398898179, 537638442289332228, 537638442285137921),
  (537639598398898180, 537638442289332229, 537638442285137921),
  (537639598398898181, 537638442289332230, 537638442285137921)
   ;


INSERT INTO sys_userrole (objectid, role_id, user_id)
VALUES
  (537639598403092480, 537638442285137921, 2000)
  ;