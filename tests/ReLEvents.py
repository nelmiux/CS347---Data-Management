global_conn = connectTo 'jdbc:oracle:thin:@rising-sun.microlab.cs.utexas.edu:1521:orcl' 'CS347_prof' 'orcl_prof' 'rdf_mode' 'EVENTS'
results = SQL on global_conn """select * from EVENTS where EventType='AUTHENTICATION_FAILED_EVENT'"""
for i in results :
   print "RESULTS: ", i
