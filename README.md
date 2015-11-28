# SQL to SparQL

## Continue the Development of SQL to SPARQL translation on Jyphon

### Building Instructions

* ant clean
* move dist to the trash
* ant
* ant
* sudo dist/bin/jython >> conn = connectTo 'jdbc:oracle:thin:@129.152.144.84/PDB1.usuniversi01134.oraclecloud.internal''cs370_ontologies' 'orcl' 'rdf_mode' 'emp'; >> SQL on conn 'select * from emp'

### SparQL Executing Instructions

* Login to a CS machine
* wget https://www.dropbox.com/s/c0gshqi3s1fhx9n/neo4j_emp_db.txt?dl=1 -O emp_dept.neo4j.formatted.txt
* Change the login credentials  conn = connectTo 'jdbc:oracle:thin:@sayonara.microlab.cs.utexas.edu:1521:orcl''C##cs347_UTEID''orcl_UTEID''rdf_mode''E5';
* /u/cannata/ReL/dist/bin/jython emp_dept.neo4j.formatted.txt
* Now open SQL Developer and you should see a table named "E5_C##cs347_UTEID"
* Now you can run the SparQL queries.  

### Implement the conversion of SQL subqueries to SPARQL subqueries in ReL (ReL Source Code).

* FROM
* WHERE

### Examples

#### Subquery in WHERE

SQL: SELECT empno FROM emp WHERE empno=(SELECT epmno FROM emp WHERE empno=7369);

SPARQL:   SELECT v1 "empno"
          FROM TABLE(SEM_MATCH('SELECT * WHERE {
                GRAPH <emp_SCHEMA> { ?s1 rdf:type :emp }
                OPTIONAL { ?s1 :empno ?v1 }
                {
                  SELECT * WHERE {
                      GRAPH <emp_SCHEMA> { ?s1 rdf:type :emp }
                      OPTIONAL { ?s1 :empno ?v1 }
               	   ?s1 :empno ?f1 .
        	        FILTER(?f1 = :7369)}
                }
        }
        
#### Subquery in FROM

SQL: SELECT empno, sal FROM (SELECT empno, deptno, sal FROM emp);

SPARQL: SELECT v1 "empno", v2 "sal"
        FROM TABLE(SEM_MATCH('SELECT ?v1 ?v2 ?v3
        WHERE {
            GRAPH <emp_SCHEMA> { ?s1 rdf:type :emp }
            OPTIONAL { ?s1 :empno ?v1 }
            OPTIONAL { ?s1 :sal ?v2 }
            OPTIONAL { ?s1 :deptno ?v3 }
        }

#### Subquery in FROM with * on right

SQL: SELECT empno, sal FROM (SELECT * FROM emp);

SPARQL: SELECT v1 "empno", v8 "sal"
        FROM TABLE(SEM_MATCH('SELECT ?v1 ?v2 ?v3 ?v4 ?v5 ?v6 ?v7 ?v8 ?v9
        WHERE {
                GRAPH <emp_SCHEMA> { ?s1 rdf:type :emp }
                OPTIONAL { ?s1 :empno ?v1 }
                OPTIONAL { ?s1 :deptno ?v2 }
                OPTIONAL { ?s1 :comm ?v3 }
                OPTIONAL { ?s1 :ename ?v4 }
                OPTIONAL { ?s1 :job ?v5 }
                OPTIONAL { ?s1 :mgr ?v6 }
                OPTIONAL { ?s1 :hiredate ?v7 }
                OPTIONAL { ?s1 :sal ?v8 }
        	  OPTIONAL { ?s1 :dept ?v9 }
        }
        
  #### Subquery in FROM with * on left
  
  SQL: select * from (select empno, sal, deptno from emp);
  
  SPARQL: SELECT v1 "empno", v2 "sal", v3 "deptno"
          FROM TABLE(SEM_MATCH('SELECT ?v1 ?v2 ?v3
          WHERE {
              GRAPH <emp_SCHEMA> { ?s1 rdf:type :emp }
              OPTIONAL { ?s1 :empno ?v1 }
              OPTIONAL { ?s1 :sal ?v2 }
              OPTIONAL { ?s1 :deptno ?v3 }
          }
