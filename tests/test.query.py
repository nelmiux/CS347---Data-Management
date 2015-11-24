

#def func(my_arg):
#    print my_arg


# This is a comment 
#connectTo 'jdbc:oracle:thin:@rising-sun.microlab.cs.utexas.edu:1521:orcl' 'CS347_prof' 'orcl_prof' 'local' 'try_store'
#connectTo 'jdbc:oracle:thin:@rising-sun.microlab.cs.utexas.edu:1521:orcl' 'CS347_prof' 'orcl_prof' 'local' 'other_store'
connectTo 'jdbc:oracle:thin:@rising-sun.microlab.cs.utexas.edu:1521:orcl' 'CS347_prof' 'orcl_prof' 'local' 
persist class test(object): 
    a = "a"
    b = 1
    c = None
    d = False
    def func(self):
        pass

# x = SQL "select * from " test
# x = SQL "select * from test"
x = SQL "select test.dbuniqueid, test.a from test"
print "x is: " 
print x

