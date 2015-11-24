import unittest

"""
Define acceptance tests for ReL's features. 
"""

# Notice that this connection gets stored to the global_conn node. 
global_conn = connectTo 'jdbc:oracle:thin:@rising-sun.microlab.cs.utexas.edu:1521:orcl' 'CS347_prof' 'orcl_prof' 'rdf_mode' 'b5'

# All database statements must occure "on" a connection node. 
persist on global_conn class TEST(object):
    A = 0
    B = 0
    def __init__(self):
        self.A = "" 
        self.B = 0

    

""" 
Here's the OORel acceptance tests. 
"""
class OORelTests(unittest.TestCase):
    """
    === Test One === 
    This test will drop the TEST table, assert the table returns zero results when queried,
    then insert an instance of TEST using relInsert, assert that the table then returns only the 1 instance
    during a query, then try to delete all entries from TEST, and assert that the table is then empty. 

    \TODO The DELETE * from TEST does not seem to work in this test near the end. 
    """
    def testOne(self):


        # Drop the TEST table, assert that we are starting with a clean slate. 
        SQL on global_conn """ DROP TABLE TEST """
        
        results = SQL on global_conn """ SELECT TEST.A, TEST.B FROM """ TEST
        self.failUnless(len(results) == 0)
        
        # Use relInsert to try to insert an instance of TEST.
        previous_len = len(results)
        item = TEST()
        item.A = "samplestring"
        item.B = 540
        relInsert on global_conn item
        relCommit on global_conn
        
        # Make sure we can query the instance 
        results = SQL on global_conn "select * from " TEST
        print str(results[0].A) + " " + str(results[0].B) 
        self.failUnless(len(results) == 1 + previous_len)
        self.failUnless(results[0].A == item.A)
        self.failUnless(results[0].B == item.B)

        # Try to delete all entries in the TEST table, and assert it worked. 
        SQL on global_conn """ DELETE * FROM TEST"""   
        results = SQL on global_conn """ SELECT TEST.A, TEST.B FROM TEST """  
        print results
        # Notice the length should be 1 because we suspect the first item in the tuple to be the column headers ('A', 'B')
        self.failUnless(len(results) == 1)
    
        """ TODO the following only inserted 1 item. 
        for i in range(10):
            item = TEST()
            item.B = delete_key
            print "inserting..."
            print item
            relInsert item"""

    def testConnectScope(self):
        local_conn = connectTo 'jdbc:oracle:thin:@rising-sun.microlab.cs.utexas.edu:1521:orcl' 'CS347_prof' 'orcl_prof' 'rdf_mode' 'b5'
        print local_conn









print "Running all tests..."
unittest.main()
