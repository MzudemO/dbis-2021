create table estate_agent ( Login varchar(255),
Name varchar(255),
Password varchar(255),
Address varchar(255),
AgentID serial primary key )

insert into estate_agent values
 ( 1, 'my_login', 'John Doe', '123456', 'Sydney 2000 Wallaby Way 10' )

create table house ( ID int,
Managed_by int,
Square_area real,
Address varchar(255),
Floors int,
Price money,
Garden bool,
primary key (ID),
foreign key (Managed_by) references estate_agent(AgentID) )

insert into house values
	( 1, 1, 142.42, 'New York 10001 Park Ave 24', 3, 25000.50, false)

create table apartment ( ID int,
Managed_by int,
Square_area int,
Address varchar(255),
Floor int,
Rent money,
Balcony bool,
Kitchen bool,
primary key (ID),
foreign key (Managed_by) references estate_agent(AgentID) )

insert into apartment values
	( 1, 1, 65, 'San Francisco 94016 Bay Road 104', 9, 400.99, false, true)

create table person ( PersonID int,
First_name varchar(255),
Name varchar(255),
Address varchar(255),
primary key (PersonID) )

insert into person values
	( 1, 'Hans', 'Hans Zimmer', 'Santa Monica 90404 14th Street 1547')

create table purchase_contract ( ContractNr int,
Installments int,
Interest real,
Date date,
SettlementPlace varchar(255),
HouseID int,
BuyerID int,
primary key (ContractNr),
foreign key (HouseID) references house(ID),
foreign key (BuyerID) references person(PersonID) )

insert into purchase_contract values
	( 1, 12, 0.901, '2004-04-14', 'Some Settlement Place', 1, 1)

create table tenancy_contract ( ContractNr int,
Start_date date,
Duration interval,
Additional_costs money,
Date date,
SettlementPlace varchar(255),
ApartmentID int,
RenterID int,
primary key (ContractNr),
foreign key (ApartmentID) references apartment(ID),
foreign key (RenterID) references person(PersonID) )

insert into tenancy_contract values
	( 1, '2020-12-01', '12 months', 200.00, '2020-10-15', 'Some Settlement Place', 1, 1)