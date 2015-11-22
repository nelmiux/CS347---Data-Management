# Simple test that runs a join of three class types.
# We expect ReL to return an instance of the first Type in the select query, 
# and then contain pointers to other instance's that were types during the join. 
# See example near end of file. 

conn = connectTo 'jdbc:oracle:thin:@rising-sun.microlab.cs.utexas.edu:1521:orcl' 'CS347_prof' 'orcl_prof' 'rdf_mode' 'ReLjoin';

persist on conn class A(object):
        JOINKEY = 0
        N = ""
        def __init__(self, n, k):
            self.JOINKEY = k
            self.N = n
        def __str__(self):
            return " A instance: has N value = " + str(self.N)

persist on conn class B(object):
        JOINKEY = 0
        M = ""
        def __init__(self, m, k):
            self.JOINKEY = k
            self.M = m
        def __str__(self):
            return " B instance: has M value = " + str(self.M)

persist on conn class C(object):
        JOINKEY = 0
        Z = ""
        def __init__(self, z, k):
            self.Z = z
            self.JOINKEY = k
        def __str__(self):
            return " C instance : has Z value = " + str(self.Z)

i1 = A("someValueForN", 1)
i2 = B("testMValue", 1)
i3 = C("Zzzzz", 1)

relInsert on conn i1
relInsert on conn i2
relInsert on conn i3
relCommit on conn 

# We select from A, B, and C
# this should return instances of A with pointers to instances of B and C
instances = SQL on conn "select * from " A ", " B ", " C " as beta where A.JOINKEY = B.JOINKEY and C.JOINKEY = A.JOINKEY"
print instances
for x in instances:
    print x
    print x.B
    print x.C

        
