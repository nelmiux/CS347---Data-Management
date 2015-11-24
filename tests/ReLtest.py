from NeatPrint import neatPrintTable

n=1600
s='Python'
connectTo 'jdbc:oracle:thin:@rising-sun.microlab.cs.utexas.edu:1521:orcl' 'CS347_prof' 'orcl_prof' 'remote';
for i in [1,2,3]:
   print SQL "insert into emp (empno, ename) values (" i ", 'phil');"
   print SQL "select * from emp"
   # output = SQL "select * from emp"
   # neatPrintTable(output)
   print SQL "select ename as \"the name\" from emp where sal = " n
   print SQL "select ename as \"the name\" from emp where " (lambda x: x**2)(i*4) " > 10 \
              and ename = 'phil' or mgr = " i 
   # print SQL "select ename as \"the name\" from emp where " (lambda x: x**2)(4) "< 10 \
              # and ename = 'Phil' or ename = " "Python" 'or 3==3'           # This doesn't get parsed correctly!!!!!
   print SQL "select ename as \"the name\" from emp where " (lambda x: x**2)(i/3) " > 10 \
              and ename = 'Phil' or ename = " s 'or 3=3'

print SQL """
drop table dummy~
create table dummy(
attr1 varchar(10),
attr2 varchar(2),
attr3 varchar(2),
attr4 varchar(2)
)~
"""
