conn = connectTo 'jdbc:oracle:thin:@128.83.138.158:1521:orcl' 'C##cs347_prof' 'orcl_prof' 'rdf_mode' 'FIBO1';
conn1 = connectTo 'jdbc:oracle:thin:@128.83.138.158:1521:orcl' 'C##cs347_prof' 'orcl_prof' 'native_mode' 'FIBO1';

SQL on conn """ create table Thing(thing_id integer, name varchar(255)) """
SQL on conn """ create table EquityOwner(equity_owner_id integer, name varchar(255)) """
SQL on conn """ create table Role(role_id integer, name varchar(255)) """

SQL on conn """ create table Equity(equity_id integer, name varchar(255)) """
SQL on conn """ create table StockholderEquity(sh_equity_id integer, name varchar(255)) """

SQL on conn """ create table FormallyConstitutedOrganizaiton(fco_id integer, name varchar(255)) """
SQL on conn """ create table BodyCorporation(bc_id integer, name varchar(255)) """
SQL on conn """ create table BodyCorporationWithEquity(bcwe_id integer, name varchar(255)) """
SQL on conn """ create table IncorporatedCompany(ic_id integer, name varchar(255)) """
SQL on conn1 """ INSERT INTO FIBO1_C##CS347_PROF_DATA VALUES ( FIBO1_C##CS347_PROF_SQNC.nextval, SDO_RDF_TRIPLE_S('FIBO1_C##CS347_PROF:<http://www.example.org/people.owl>', 'http://www.example.org/people.owl#BodyCorporation', 'http://www.w3.org/2000/01/rdf-schema#subClassOf', 'http://www.example.org/people.owl#FormallyConstitutedOrganizaiton')); """
SQL on conn1 """ INSERT INTO FIBO1_C##CS347_PROF_DATA VALUES ( FIBO1_C##CS347_PROF_SQNC.nextval, SDO_RDF_TRIPLE_S('FIBO1_C##CS347_PROF:<http://www.example.org/people.owl>', 'http://www.example.org/people.owl#BodyCorporationWithEquity', 'http://www.w3.org/2000/01/rdf-schema#subClassOf', 'http://www.example.org/people.owl#BodyCorporation')); """
SQL on conn1 """ INSERT INTO FIBO1_C##CS347_PROF_DATA VALUES ( FIBO1_C##CS347_PROF_SQNC.nextval, SDO_RDF_TRIPLE_S('FIBO1_C##CS347_PROF:<http://www.example.org/people.owl>', 'http://www.example.org/people.owl#IncorporatedCompany', 'http://www.w3.org/2000/01/rdf-schema#subClassOf', 'http://www.example.org/people.owl#BodyCorporationWithEquity')); """

SQL on conn """ create table fibo_be_oac_cown_02(fibo2_id integer, name varchar(255)) """
SQL on conn """ create table ConstitutionalOwner(co_id integer) """
SQL on conn """ create table TransferableContractHolder(tch_id integer, name varchar(255)) """
SQL on conn """ create table Shareholder(shareholder_id integer, name varchar(255)) """
SQL on conn """ create table PublicShareholder(psh_id integer, name varchar(255)) """
SQL on conn """ create table RegisteredShareholder(rsh_id integer, name varchar(255)) """
SQL on conn """ create table BeneficialOwner(bo_id integer, name varchar(255)) """

SQL on conn """ create table fibo_be_oac_cown_01(fibo1_id integer, name varchar(255)) """
SQL on conn """ create table FinancialAsset(fa_id integer, name varchar(255)) """
SQL on conn """ create table Sharholding(shing_id integer, name varchar(255)) """

SQL on conn """ create table Zipcode(zipcode_id integer, name varchar(255)) """

SQL on conn """ insert into FormallyConstitutedOrganizaiton(zipcode) values (rel_zipcode) """
SQL on conn """ insert into Zipcode(zipcode) values (rel_zipcode) """

connr = connectTo 'jdbc:oracle:thin:@128.83.138.158:1521:orcl' 'C##cs347_prof' 'orcl_prof' 'rdf_mode' 'FIBOR';

SQL on connr """ insert into BodyCorporation(x, zip_code) values (1, 78733) """
SQL on conn1 """ INSERT INTO FIBOR_C##CS347_PROF_DATA VALUES ( FIBOR_C##CS347_PROF_SQNC.nextval, SDO_RDF_TRIPLE_S('FIBOR_C##CS347_PROF:<http://www.example.org/people.owl>', 'http://www.example.org/people.owl#zip_code', 'rdf:type', 'owl:DatatypeProperty')) """
SQL on conn1 """ INSERT INTO FIBOR_C##CS347_PROF_DATA VALUES ( FIBOR_C##CS347_PROF_SQNC.nextval, SDO_RDF_TRIPLE_S('FIBOR_C##CS347_PROF:<http://www.example.org/people.owl>', 'http://www.example.org/people.owl#zip_code', 'rdfs:domain', 'http://www.example.org/people.owl#BodyCorporation')) """
SQL on conn1 """ INSERT INTO FIBOR_C##CS347_PROF_DATA VALUES ( FIBOR_C##CS347_PROF_SQNC.nextval, SDO_RDF_TRIPLE_S('FIBOR_C##CS347_PROF:<http://www.example.org/people.owl>', 'http://www.example.org/people.owl#zip_code', 'rdf:range', 'rdfs:xsd:integer')) """
SQL on conn1 """ INSERT INTO FIBOR_C##CS347_PROF_DATA VALUES ( FIBOR_C##CS347_PROF_SQNC.nextval, SDO_RDF_TRIPLE_S('FIBOR_C##CS347_PROF:<http://www.example.org/people.owl>', 'http://www.example.org/people.owl#zip_code', 'rdf:type', 'owl:FunctionalProperty')) """

'''
SQL on connr """ insert into BodyCorporation(x, zip_code) values (2, 78734) """
SQL on connr """ insert into BodyCorporation(x, zip_code) values (3, 78735) """
SQL on connr """ insert into BodyCorporation(x, zip_code) values (4, 78736) """
SQL on connr """ insert into BodyCorporation(x, zip_code) values (5, 78737) """

SQL on connr """ insert into BodyCorporationWithEquity(x, zip_code) values (1, 78733) """
SQL on connr """ insert into BodyCorporationWithEquity(x, zip_code) values (2, 78734) """
SQL on connr """ insert into BodyCorporationWithEquity(x, zip_code) values (3, 78735) """
SQL on connr """ insert into BodyCorporationWithEquity(x, zip_code) values (4, 78736) """
SQL on connr """ insert into BodyCorporationWithEquity(x, zip_code) values (5, 78737) """

SQL on connr """ insert into IncorporatedCompany(x, zip_code) values (1, 78733) """
SQL on connr """ insert into IncorporatedCompany(x, zip_code) values (2, 78734) """
SQL on connr """ insert into IncorporatedCompany(x, zip_code) values (3, 78735) """
SQL on connr """ insert into IncorporatedCompany(x, zip_code) values (4, 78736) """
SQL on connr """ insert into IncorporatedCompany(x, zip_code) values (5, 78737) """

SQL on connr """ insert into PublicShareholder(y, zip_code) values (4, 78733) """
SQL on connr """ insert into PublicShareholder(y, zip_code) values (8, 78734) """
SQL on connr """ insert into PublicShareholder(y, zip_code) values (12, 78735) """
SQL on connr """ insert into PublicShareholder(y, zip_code) values (16, 78736) """
SQL on connr """ insert into PublicShareholder(y, zip_code) values (20, 78737) """
'''
SQL on connr """ insert into Zipcode(y, zip_code) values (4, 78733) """
SQL on connr """ insert into Zipcode(y, zip_code) values (8, 78734) """
SQL on connr """ insert into Zipcode(y, zip_code) values (12, 78735) """
SQL on connr """ insert into Zipcode(y, zip_code) values (16, 78736) """
SQL on connr """ insert into Zipcode(y, zip_code) values (20, 78737) """

r1 = SQL on connr """ select * from BodyCorporation """

r2 = SQL on connr """ select * from PublicShareholder """
r3 = SQL on connr """ select * from Zipcode """
r4 = SQL on connr """ select x, y from BodyCorporation f join Zipcode z on (f.zip_code = z.zip_code) order by 1 """

print r1
print
print r2
print
print r3
print
print r4

"""
-- truncate table FIBO1_C##CS347_PROF_DATA; truncate table FIBOR_C##CS347_PROF_DATA

SELECT a.triple.GET_SUBJECT() as subject,  
      a.triple.GET_PROPERTY() as property,
      a.triple.GET_OBJECT() as object
      from FIBO1_C##CS347_PROF_DATA a order by subject, property;
      select x, y from FormallyConstitutedOrganizaiton f join Zipcode z on (f.zipcode = z.zipcode) order by 1
      
df <- data.frame(fromJSON(getURL(URLencode(gsub("\n", " ", '129.152.144.84:5001/rest/native/?query=
"select x, y from FormallyConstitutedOrganizaiton f join Zipcode z on (f.zip_code = z.zip_code) order by 1"
')),httpheader=c(DB='jdbc:oracle:thin:@128.83.138.158:1521:orcl', USER='C##cs347_prof', PASS='orcl_prof', MODE='rdf_mode', MODEL='FIBOR', returnDimensions = 'False', returnFor = 'JSON'), verbose = TRUE))); tbl_df(df)

"""
