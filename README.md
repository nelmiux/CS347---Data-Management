# CS347 - Data Management Project

The code given implements the translation from SQL to SPARQL without subqueries fruntionability.

## What we implemented?

- Subquery functionability translation from SQL to SPARQL

## Setting the enviroment and using the Application over Linux Terminal

### Steps:
ant clean
move dist to the trash
ant
ant
sudo pip3 install --target=<yourpath>/MyReL_F15/dist/Lib/ Flask
dist/bin/jython 
>> conn = connectTo 'jdbc:oracle:thin:@129.152.144.84/PDB1.usuniversi01134.oraclecloud.internal' 'cs370_ontologies' 'orcl' 'rdf_mode' 'emp';
>> SQL on conn 'select * from emp'

## How it works?

- After the SQL query is wrote, it is evaluated and send it to the SQL Server hosting the database,
- In PyTuple.doRDF: if (statement instanceof Select), call SQLVisitor.getSelect
- In SQLVisitor.getSelect, somehow gets to visit, which calls visitSelect_buildSPARQL
- visitSelect_buildSPARQL returns the SPARQL statement to SQLVisitor.getSelect with the help of jsqlparser that convert SQL statements grammar to SPARQL grammar
- SQLVisitor.getSelect returns the SPARQL to PyTuple
- PyTuple.doRDF then calls runAndOutputTuples, which calls conn.executeQuery(ReLstmt) and the results from this are processed and stored in the results field of PyTuple

Example without subqueries:

  SQL                                                     SPARQL
    SELECT * FROM emp;  --- Translation Process ---       SELECT v1 "empno", v2 "deptno", v3 "comm", v4                                                                                  "job", v5 "mgr", v6 "ename", v7 "hiredate", v8 "sal", v9 "dept"
                                                          FROM TABLE(SEM_MATCH('SELECT * WHERE {
	                                                            GRAPH <emp_SCHEMA> { ?s1 rdf:type :emp }
	                                                            OPTIONAL { ?s1 :empno ?v1 }
	                                                            OPTIONAL { ?s1 :deptno ?v2 }
	                                                            OPTIONAL { ?s1 :comm ?v3 }
	                                                            OPTIONAL { ?s1 :job ?v4 }
	                                                            OPTIONAL { ?s1 :mgr ?v5 }
	                                                            OPTIONAL { ?s1 :ename ?v6 }
	                                                            OPTIONAL { ?s1 :hiredate ?v7 }
	                                                            OPTIONAL { ?s1 :sal ?v8 }
	                                                            OPTIONAL { ?s1 :dept ?v9 }
                                                            }


