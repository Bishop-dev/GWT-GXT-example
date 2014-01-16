create table user (
user_id integer not null,
user_login varchar(50) not null unique,
user_password varchar(50) not null unique,
user_email varchar(50) not null ,
user_firstname varchar(50) not null,
user_lastname varchar(50) not null,
user_birthday date not null,
primary key(user_id)
)

CREATE SEQUENCE HUBACHOV.USER_IDENTITY AS INTEGER
    START WITH 1
    INCREMENT BY 1
    MAXVALUE 2147483647
    MINVALUE 1
    NO CYCLE
    NO CACHE
    NO ORDER
	
create table role(
role_id integer not null unique,
role_name varchar(50) not null unique,
primary key(role_id)
)

create sequence role_identity as integer
start with 1
increment by 1
no maxvalue
no minvalue
no cycle
no cache
no order

create table user_role(
user_role_id integer not null unique,
user_id integer not null,
role_id integer not null,
foreign key (user_id) references user(user_id) on delete cascade,
foreign key (role_id) references role(role_id) on delete cascade,
primary key(user_role_id)
)

create sequence user_role_identity as integer
start with 1
increment by 1
no minvalue
no maxvalue
no cache
no cycle
no order

create function countrole(r_id integer)
returns integer
language sql
return select count(*) from user_role ur where ur.role_id=r_id group by ur.role_id

create procedure statistic()
result set 1
language sql
begin
declare c1 cursor with return for
select r.role_name, countrole(r.role_id) from role r inner join user_role ur on r.role_id=ur.role_id group by r.role_name, r.role_id;
open c1;
end

create procedure addrole(in r_name varchar(50))
language sql
insert into role(role_id,role_name) values(next value for role_identity, r_name)

create procedure adduserfast(in u_login varchar(50))
language sql
insert into user(user_id, user_login, user_password, user_email, user_firstname,
user_lastname, user_birthday) values 
(next value for user_identity, u_login, u_login, u_login||'@mail.com', u_login, 
u_login, '1992-02-05')

create procedure adduserrole(in u_id integer, in r_id integer)
language sql
insert into user_role(user_role_id, user_id, role_id) values
(next value for user_role_identity, u_id, r_id)

create procedure enrichuser(in u_id integer)
result set 1
language sql
begin
declare c1 cursor with return for
select r.role_id, r.role_name from role r inner join user_role ur on r.role_id=ur.role_id and ur.user_id=u_id;
open c1;
end

create procedure saveUserRoles(in u_id integer, in r_id integer)
language sql
insert into user_role(user_role_id, user_id,role_id)values(next value for user_role_identity, u_id, r_id)

create procedure createUser(in u_login varchar(50),in u_password varchar(50),in u_email varchar(50),in u_firstname varchar(50),in u_lastname varchar(50),in u_birthday date,out id integer)
language sql
select user_id into id from final table(insert into user (user_id, user_login,user_password,user_email,user_firstname,user_lastname,user_birthday) values
(next value for user_identity, u_login, u_password, u_email,u_firstname,u_lastname,u_birthday))