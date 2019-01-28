-- 更新创建人
update sys_menu set created_by = 1000, last_updated_by = 1000;
update sys_code set created_by = 1000, last_updated_by = 1000;
update sys_role set created_by = 1000, last_updated_by = 1000;
update sys_user set created_by = 1000, last_updated_by = 1000;

-- 删除用户数据
delete from sys_account where user_id not in (2000, 1000);
delete from sys_userrole where user_id not in (2000, 1000);
delete from sys_user where objectid not in (2000, 1000);

delete from sed_accendant;
delete from sed_company;