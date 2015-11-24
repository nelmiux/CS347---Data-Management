connectTo 'jdbc:oracle:thin:@rising-sun.microlab.cs.utexas.edu:1521:orcl' 'CS347_prof' 'orcl_prof' 'remote'
persist class test(object): 
    a = "a"
    b = 1
    c = None
    d = False
    def func(self):
        pass
        #SQL "select * from test"

t = test()
t.b = "changed"
relInsert t

relCommit
