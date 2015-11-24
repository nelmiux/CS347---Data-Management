# Connect to the database.
connectTo 'jdbc:oracle:thin:@rising-sun.microlab.cs.utexas.edu:1521:orcl' 'CS347_prof' 'orcl_prof' 'rdf_mode' 't1'

persist class TEST(object): 
    A = "a"
    B = 1
    C = "was never set"
    D = False
    def __str__(self):
        return " -> a: %s %s \t b: %s %s \t c: %s %s \t d: %s %s" % (type(self.A), str(self.A), type(self.B), str(self.B), type(self.C), str(self.C), type(self.D), str(self.D))
t = TEST()
t2 = TEST()
t3 = TEST()

l = list()
l.append(t)
l.append(t2)
l.append(t3)

for inst in l:
    relInsert inst

SQL "insert into TEST (A, B, C, D) values (10, TRUE, 13.2, somestring)"

# SQL "select * from " TEST
# insert the instance of TEST. 
t.B = "changed"
relInsert t

print "t's dictionary %s " % str(t.__dict__)
print "unique id = " + str(t.DBUNIQUEID)

relCommit

instances = SQL "select TEST.B, TEST.A, TEST.C, TEST.DBUNIQUEID from " TEST

select_star_instances = SQL "select * from " TEST

#for i in instances:
#    print str(i.__dict__)
#    print " ... " + str(i.B) + " dbuniqueid: " + str(i.DBUNIQUEID) + " c: " + str(i.C)

for i in select_star_instances:
    print i
