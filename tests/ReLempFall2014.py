# conn = connectTo 'jdbc:oracle:thin:@rising-sun.microlab.cs.utexas.edu:1521:orcl' 'CS347_prof' 'orcl_prof' 'rdf_mode' 'Fall2014';
# conn = connectTo 'jdbc:oracle:thin:@128.83.138.158:1521:orcl' 'C##cs347_prof' 'orcl_prof' 'rdf_mode' 'F2014';
# conn = connectTo 'jdbc:oracle:thin:@zenji.microlab.cs.utexas.edu:1521:orcl' 'C##cs347_prof' 'orcl_prof' 'rdf_mode' 'Fall2014';
conn = connectTo 'jdbc:oracle:thin:@128.83.138.158:1521:orcl' 'C##cs347_prof' 'orcl_prof' 'rdf_mode' 'Gaar15';

SQL on conn """DROP TABLE EMP"""
SQL on conn """DROP TABLE DEPT"""

SQL on conn """INSERT INTO EMP (EMPNO, ENAME, JOB, MGR, HIREDATE, SAL, COMM, DEPTNO) VALUES
        (7369, 'SMITH',  'CLERK', 7902, '17-DEC-1980',  800, NULL, 20)"""
SQL on conn """INSERT INTO EMP (EMPNO, ENAME, JOB, MGR, HIREDATE, SAL, COMM, DEPTNO) VALUES
        (7499, 'ALLEN',  'SALESMAN',  7698, '20-FEB-1981', 1600,  300, 30)"""
SQL on conn """INSERT INTO EMP (EMPNO, ENAME, JOB, MGR, HIREDATE, SAL, COMM, DEPTNO) VALUES
        (7521, 'WARD',   'SALESMAN',  7698, '22-FEB-1981', 1250,  500, 30)"""
SQL on conn """INSERT INTO EMP (EMPNO, ENAME, JOB, MGR, HIREDATE, SAL, COMM, DEPTNO) VALUES
        (7566, 'JONES',  'MANAGER',   7839, '2-APR-1981',  2975, NULL, 20)"""
SQL on conn """INSERT INTO EMP (EMPNO, ENAME, JOB, MGR, HIREDATE, SAL, COMM, DEPTNO) VALUES
        (7654, 'MARTIN', 'SALESMAN',  7698, '28-SEP-1981', 1250, 1400, 30)"""
SQL on conn """INSERT INTO EMP (EMPNO, ENAME, JOB, MGR, HIREDATE, SAL, COMM, DEPTNO) VALUES
        (7698, 'BLAKE',  'MANAGER',   7839, '1-MAY-1981',  2850, NULL, 30)"""
SQL on conn """INSERT INTO EMP (EMPNO, ENAME, JOB, MGR, HIREDATE, SAL, COMM, DEPTNO) VALUES
        (7782, 'CLARK',  'MANAGER',   7839, '9-JUN-1981',  2450, NULL, 10)"""
SQL on conn """INSERT INTO EMP (EMPNO, ENAME, JOB, MGR, HIREDATE, SAL, COMM, DEPTNO) VALUES
        (7788, 'SCOTT',  'ANALYST',   7566, '09-DEC-1982', 3000, NULL, 20)"""
SQL on conn """INSERT INTO EMP (EMPNO, ENAME, JOB, MGR, HIREDATE, SAL, COMM, DEPTNO) VALUES
        (7839, 'KING',   'PRESIDENT', NULL, '17-NOV-1981', 5000, NULL, 10)"""
SQL on conn """INSERT INTO EMP (EMPNO, ENAME, JOB, MGR, HIREDATE, SAL, COMM, DEPTNO) VALUES
        (7844, 'TURNER', 'SALESMAN',  7698, '8-SEP-1981',  1500, NULL, 30)"""
SQL on conn """INSERT INTO EMP (EMPNO, ENAME, JOB, MGR, HIREDATE, SAL, COMM, DEPTNO) VALUES
        (7876, 'ADAMS',  'CLERK',     7788, '12-JAN-1983', 1100, NULL, 20)"""
SQL on conn """INSERT INTO EMP (EMPNO, ENAME, JOB, MGR, HIREDATE, SAL, COMM, DEPTNO) VALUES
        (7900, 'JAMES',  'CLERK',     7698, '3-DEC-1981',   950, NULL, 30)"""
SQL on conn """INSERT INTO EMP (EMPNO, ENAME, JOB, MGR, HIREDATE, SAL, COMM, DEPTNO) VALUES
        (7902, 'FORD',   'ANALYST',   7566, '3-DEC-1981',  3000, NULL, 20)"""
SQL on conn """INSERT INTO EMP (EMPNO, ENAME, JOB, MGR, HIREDATE, SAL, COMM, DEPTNO) VALUES (7934, 'MILLER', 'CLERK',     7782, '23-JAN-1982', 1300, NULL, 10)"""

SQL on conn """INSERT INTO DEPT (DEPTNO, DNAME, LOC) VALUES (10, 'ACCOUNTING', 'NEW_YORK')"""
SQL on conn """INSERT INTO DEPT (DEPTNO, DNAME, LOC) VALUES (20, 'RESEARCH',   'DALLAS')"""
SQL on conn """INSERT INTO DEPT (DEPTNO, DNAME, LOC) VALUES (30, 'SALES',      'CHICAGO')"""
SQL on conn """INSERT INTO DEPT (DEPTNO, DNAME, LOC) VALUES (40, 'OPERATIONS', 'BOSTON')"""

EMPs = SQL on conn """ select * from EMP"""
print EMPs
DEPTs = SQL on conn """ select * from DEPT"""
print DEPTs
emp = SQL on conn """ select ename from emp """
print emp
c = SQL on conn """select dname, avg(sal) from emp e, dept d
where e.deptno = d.deptno
group by deptno 
having avg(sal) > 1000 
order by avg(sal) """
print c

