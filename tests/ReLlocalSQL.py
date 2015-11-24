from NeatPrint import neatPrintTable

connectTo 'jdbc:oracle:thin:@rising-sun.microlab.cs.utexas.edu:1521:orcl' 'CS347_prof' 'orcl_prof' 'local';
SQL """
create table dummy(
attr1 varchar(10),
attr2 varchar(10),
attr3 varchar(10),
attr4 varchar(10)
)
"""
SQL """
insert into dummy(attr1, attr2, attr3, attr4) values ('one', 'two', 'three', 'four')
"""
print SQL """
select dummy.attr1 from dummy
"""
print SQL """
select * from dummy
"""
