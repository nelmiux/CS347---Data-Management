connectTo 'jdbc:oracle:thin:@ib-perfdb.us.oracle.com:1525:perfdb' 'cannata' 'orcl' 'native_mode';

data = SQL """ select display_value label, return_value value from table(apex_analysis.get_throughput(7774, 64, 1, 20)) """
l=[]
v=[]
for d in data[1:]:
   l.append(d[0])
   v.append(d[1])
l = tuple(l)
v = tuple(v)
#print l, v
   
print [j for i in data[1:] for j in i[0:][0::2]]
print [j for i in data[1:] for j in i[0:][1::2]]

'''
R commands
RReLtest1 <- function() {rJython <- rJython()
jython.exec( rJython, "connectTo 'jdbc:oracle:thin:@ib-perfdb.us.oracle.com:1525:perfdb' 'cannata' 'orcl' 'native_mode';")
jython.exec( rJython, 'data = SQL """ select display_value label, return_value value from table(apex_analysis.get_throughput(7774, 64, 1, 20)) """')
x = jython.get( rJython, "[j for i in data[1:] for j in i[0:][0::2]]")
y = jython.get( rJython, "[j for i in data[1:] for j in i[0:][1::2]]")
df <- data.frame(x, y)
print(df)
plot(df)
}
RReLtest1()
'''
