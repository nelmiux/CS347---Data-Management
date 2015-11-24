from NeatPrint import neatPrintTable
from BatchRemote import BatchRemote

connectTo 'jdbc:oracle:thin:@rising-sun.microlab.cs.utexas.edu:1521:orcl' 'CS347_prof' 'orcl_prof' 'native_mode';
SQL """
DROP TABLE EMP~
DROP TABLE DEPT~

CREATE TABLE EMP
       (EMPNO NUMBER(7) NOT NULL,
        CONSTRAINT EMP_pkey PRIMARY KEY (EMPno),
        ENAME VARCHAR2(10),
        JOB VARCHAR2(9),
        MGR NUMBER(4),
        HIREDATE DATE,
        SAL NUMBER(7, 2),
        COMM NUMBER(7, 2),
        DEPTNO NUMBER(2))~

-- create sequence EMP_EMPno
-- start with 1 
-- increment by 1 
-- nomaxvalue~

-- create or replace trigger EMP_EMPno_trigger
-- before insert on EMP
-- for each row
-- begin
-- select EMP_EMPno.nextval into :new.EMPno from dual~
-- end
-- /~

INSERT INTO EMP VALUES
        (7369, 'SMITH',  'CLERK',     7902,
        TO_DATE('17-DEC-1980', 'DD-MON-YYYY'),  800, NULL, 20)~
INSERT INTO EMP VALUES
        (7499, 'ALLEN',  'SALESMAN',  7698,
        TO_DATE('20-FEB-1981', 'DD-MON-YYYY'), 1600,  300, 30)~
INSERT INTO EMP VALUES
        (7521, 'WARD',   'SALESMAN',  7698,
        TO_DATE('22-FEB-1981', 'DD-MON-YYYY'), 1250,  500, 30)~
INSERT INTO EMP VALUES
        (7566, 'JONES',  'MANAGER',   7839,
        TO_DATE('2-APR-1981', 'DD-MON-YYYY'),  2975, NULL, 20)~
INSERT INTO EMP VALUES
        (7654, 'MARTIN', 'SALESMAN',  7698,
        TO_DATE('28-SEP-1981', 'DD-MON-YYYY'), 1250, 1400, 30)~
INSERT INTO EMP VALUES
        (7698, 'BLAKE',  'MANAGER',   7839,
        TO_DATE('1-MAY-1981', 'DD-MON-YYYY'),  2850, NULL, 30)~
INSERT INTO EMP VALUES
        (7782, 'CLARK',  'MANAGER',   7839,
        TO_DATE('9-JUN-1981', 'DD-MON-YYYY'),  2450, NULL, 10)~
INSERT INTO EMP VALUES
        (7788, 'SCOTT',  'ANALYST',   7566,
        TO_DATE('09-DEC-1982', 'DD-MON-YYYY'), 3000, NULL, 20)~
INSERT INTO EMP VALUES
        (7839, 'KING',   'PRESIDENT', NULL,
        TO_DATE('17-NOV-1981', 'DD-MON-YYYY'), 5000, NULL, 10)~
INSERT INTO EMP VALUES
        (7844, 'TURNER', 'SALESMAN',  7698,
        TO_DATE('8-SEP-1981', 'DD-MON-YYYY'),  1500, NULL, 30)~
INSERT INTO EMP VALUES
        (7876, 'ADAMS',  'CLERK',     7788,
        TO_DATE('12-JAN-1983', 'DD-MON-YYYY'), 1100, NULL, 20)~
INSERT INTO EMP VALUES
        (7900, 'JAMES',  'CLERK',     7698,
        TO_DATE('3-DEC-1981', 'DD-MON-YYYY'),   950, NULL, 30)~
INSERT INTO EMP VALUES
        (7902, 'FORD',   'ANALYST',   7566,
        TO_DATE('3-DEC-1981', 'DD-MON-YYYY'),  3000, NULL, 20)~
INSERT INTO EMP VALUES
        (7934, 'MILLER', 'CLERK',     7782,
        TO_DATE('23-JAN-1982', 'DD-MON-YYYY'), 1300, NULL, 10)~

CREATE TABLE DEPT
       (DEPTNO NUMBER(2),
        DNAME VARCHAR2(14),
        LOC VARCHAR2(13) )~

INSERT INTO DEPT VALUES (10, 'ACCOUNTING', 'NEW YORK')~
INSERT INTO DEPT VALUES (20, 'RESEARCH',   'DALLAS')~
INSERT INTO DEPT VALUES (30, 'SALES',      'CHICAGO')~
INSERT INTO DEPT VALUES (40, 'OPERATIONS', 'BOSTON') -- This is a comment~
"""
EMPs = SQL """ select * from EMP"""
DEPTs = SQL """ select * from DEPT"""
print DEPTs

model = 'RELEMPJ_CS347_PROF'
m = SQL """ SELECT TABLE_NAME FROM MDSYS.SEM_MODEL$ WHERE MODEL_NAME =  """ model
if m != (('TABLE_NAME',),) : SQL """ truncate table """  "\"{0}\"".format(m[1][0])

SQL """ ALTER SYSTEM SET open_cursors = 2000 SCOPE=BOTH; """

connectTo 'jdbc:oracle:thin:@rising-sun.microlab.cs.utexas.edu:1521:orcl' 'CS347_prof' 'orcl_prof' 'rdf_mode' 'ReLEMPJ';

for i in range(1, len(EMPs)) :
   SQL """ INSERT INTO EMP """  str(EMPs[0]).replace("'", "") """  VALUES """  EMPs[i]
t = SQL "select EMP.EMPNO, EMP.ENAME from EMP"
neatPrintTable(t)

for i in range(1, len(DEPTs)) :
   SQL """ INSERT INTO DEPT """  str(DEPTs[0]).replace("'", "") """  VALUES """  DEPTs[i]
t = SQL "select DEPT.DEPTNO, DEPT.DNAME from DEPT"
print t
neatPrintTable(t)

# Ask Cannata on ReL database syntax
#batch db:
#        for i in range(1, len(EMPs)) :
#           db.employees.insert(
#           SQL """ INSERT INTO EMP """  str(EMPs[0]).replace("'", "") """  VALUES """  EMPs[i]
#        t = SQL "select EMP.EMPNO, EMP.ENAME from EMP"
#        neatPrintTable(t)

t = SQL """ select * from EMP, DEPT where EMP.DEPTNO = DEPT.DEPTNO and EMP.EMPNO = 7876 """
neatPrintTable(t)

persist class EMP(object):
	COMM = 0
	JOB = ""
	SAL = 0
	DEPTNO = 0
	ENAME = ""
	EMPNO = 0
	MGR = 0
	HIREDATE = ""
	
t = SQL """ select * from """ EMP
for i in range(0,len(t)) :
   print t[i].EMPNO

# Python batch example
#mybatch ROOT from service:
#   for emp in ROOT.employees
#      print emp.empno


persist class DEPT(object):
	DEPTNO = 0
	DNAME = ""
	LOC = 0
	
t = SQL "select * from  " EMP ", " DEPT " where EMP.DEPTNO = DEPT.DEPTNO and EMP.EMPNO = 7876 "
print "----JOIN select----"
print t
print "Name = %s " % (t[0].ENAME)
print "Name = %s : DEPT = %s" % (t[0].ENAME, t[0].DEPT.DNAME)


#Call on BatchRemote.py and create a Remote Service via batch-python-runtime, might need to update core batches
service = BatchRemote()
mybatches ROOT in service:
    for emp in ROOT.employees:
        if emp.empno == 7876:
            print "Name = %s : DEPT = %s" % (emp.ENAME, emp.DEPT.DNAME)



for x in t:
   print " EMP name = %s : department = %s " % (x.ENAME, x.DEPT.DNAME)




