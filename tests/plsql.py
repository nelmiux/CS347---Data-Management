connectTo 'jdbc:oracle:thin:@rising-sun.microlab.cs.utexas.edu:1521:orcl' 'CS347_prof' 'orcl_prof' 'remote';
# connectTo 'jdbc:oracle:thin:@ib-perfdb.us.oracle.com:1525:perfdb' 'cannata' 'orcl' 'remote';
for t in SQL """ select table_name from user_tables """ :
   print t[0]
   if t[0] == 'EMP' :
      sql = "select * from " + t[0]
      print SQL "" sql
