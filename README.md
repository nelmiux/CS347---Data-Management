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
- visitSelect_buildSPARQL returns the SPARQL statement to SQLVisitor.getSelect with the help of jsqlparser that convert SQL statements grammar to SPARQL grammar ( we don't need to write code to check for key words or parse the statement. That work is done by the jsqlparser)
- SQLVisitor.getSelect returns the SPARQL to PyTuple
- PyTuple.doRDF then calls runAndOutputTuples, which calls conn.executeQuery(ReLstmt) and the results from this are processed and stored in the results field of PyTuple

