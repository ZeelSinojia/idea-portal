create database ideaportal;
CREATE TABLE IF NOT EXISTS roles (
	roleId ENUM('001','002','003') NOT NULL,
    rolename ENUM('CP','PM','Participant') NOT NULL,
    PRIMARY KEY (roleid)
);
CREATE TABLE IF NOT EXISTS users (
	user_id integer NOT NULL auto_increment,
    userpassword varchar(255) NOT NULL,
    roleId ENUM('001','002','003') NOT NULL,
    userName varchar(255) NOT NULL,
    userEmail varchar(255) ,
    userCompany varchar(255) ,
    primary key(user_id),
    foreign key (roleId) references roles(roleId)
);
CREATE TABLE IF NOT EXISTS participant_responses (
	response_id integer NOT NULL auto_increment,
    user_id integer NOT NULL,
    theme_id integer NOT NULL ,
    idea_id integer NOT NULL ,
    primary key(response_id),
    foreign key(user_id) references users(user_id),
    foreign key(idea_id) references ideas(idea_id),
    foreign key(theme_id) references themes(theme_id)
);
CREATE TABLE IF NOT EXISTS themes (
	theme_id integer NOT NULL auto_increment,
    themename varchar(255),
    themedescription text,
    t_upload_id integer NOT NULL,
    user_id integer NOT NULL ,
    primary key(theme_id),
    foreign key(user_id) references users(user_id),
    foreign key(t_upload_id) references theme_upload(t_upload_id)
);
CREATE TABLE IF NOT EXISTS theme_upload (
	t_upload_id integer NOT NULL auto_increment,
    theme_URL text,
    primary key(t_upload_id)
);
CREATE TABLE IF NOT EXISTS ideas (
	idea_id integer NOT NULL auto_increment,
    ideadescription text,
    user_id integer NOT NULL ,
    theme_id integer NOT NULL,
    i_upload_id integer NOT NULL,
    primary key(idea_id),
    foreign key(user_id) references users(user_id),
    foreign key(i_upload_id) references idea_upload(i_upload_id),
    foreign key(theme_id) references  themes(theme_id)
);
CREATE TABLE IF NOT EXISTS idea_upload (
	i_upload_id integer NOT NULL auto_increment,
    idea_URL text,
    primary key(i_upload_id)
);
CREATE TABLE IF NOT EXISTS comments (
	comment_id integer NOT NULL auto_increment,
    commentvalue text,
    idea_id integer NOT NULL,
    user_id integer NOT NULL,
    primary key(comment_id),
    foreign key(user_id) references users(user_id),
    foreign key(idea_id) references ideas(idea_id)
);
CREATE TABLE IF NOT EXISTS likes (
	like_id integer NOT NULL auto_increment,
    idea_id integer NOT NULL,
    user_id integer NOT NULL,
    primary key (like_id),
    foreign key(user_id) references users(user_id),
    foreign key(idea_id) references ideas(idea_id)
);


insert into roles values('001','CP');
insert into roles values('002','PM');
insert into roles values('003','Participant');


insert into users value(1,'1234','01','IDFC','idfc@gmail.com','IDFC Pvt Ltd');
insert into users value(2,'qwerty','02','Partha','partha@gmail.com','Persistent');
insert into users value(3,'pass1','03','Prajakta','prajakta@gmail.com','Persistent');

insert into theme_upload values(100,'https://teamcodered.sharepoint.com/:w:/r/sites/SEProductions/_layouts/15/Doc.aspx?sourcedoc=%7B5FA2E2B5-63C7-4837-8529-2AE8AB16DAB9%7D&file=theme_description.docx&action=default&mobileredirect=true');
insert into theme_upload values(101,'https://teamcodered.sharepoint.com/sites/SEProductions/trial_1/Forms/AllItems.aspx?id=%2Fsites%2FSEProductions%2Ftrial%5F1%2FfolderA%2Fempty%2Etxt&parent=%2Fsites%2FSEProductions%2Ftrial%5F1%2FfolderA');

insert into themes values(200,'Banking portal','We want to develop a baking portal',100,1);
insert into themes values(201,'Employee management portal','We want to develop a employee mng  portal. it should be a web based portal',101,1);

insert into idea_upload values(500,'https://teamcodered.sharepoint.com/:w:/r/sites/SEProductions/_layouts/15/Doc.aspx?sourcedoc=%7B5FA2E2B5-63C7-4837-8529-2AE8AB16DAB9%7D&file=theme_description.docx&action=default&mobileredirect=true');
insert into idea_upload values(501,'https://teamcodered.sharepoint.com/sites/SEProductions/trial_1/Forms/AllItems.aspx?id=%2Fsites%2FSEProductions%2Ftrial%5F1%2FfolderA%2Fempty%2Etxt&parent=%2Fsites%2FSEProductions%2Ftrial%5F1%2FfolderA');

insert into ideas values(700,'we will develop a banking portal with java and spring boot',2,200,500);

insert into participant_responses values(800,3,200,700);
insert into participant_responses values(801,3,201,700);

insert into comments values(11,'innovative way',700,3);
insert into comments values(12,'scope for improvement',700,2);

insert into likes values(300,700,1);
insert into likes values(301,700,2);
insert into likes values(302,700,3);