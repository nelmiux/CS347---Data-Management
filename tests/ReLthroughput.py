connectTo 'jdbc:oracle:thin:@ib-perfdb.us.oracle.com:1525:perfdb' 'cannata' 'orcl' 'remote';

s = []
for i in range(0,20) :
   s.append(0)
print s
for e in [7456,7457,7458,7459,7460,7461,7462,7463,7464,7465,7466,7467,7468,7469,7470,7471,7472,7473,7474,7475,7476,7477,7478,7479,7480,7481,7482,7483,7484,7485,7486, 7487,7488,7489,7490,7491,7492,7493,7494,7495,7496,7497,7498,7499,7500,7501,7502,7503,7504,7505,7506,7507,7508,7509,7510,7511,7512,7513,7514,7515,7516,7517, 7518,7519 ] :
   r = SQL """
select display_value message_size, return_value "1 Stream"
from table(apex_analysis.get_throughput(""" e """, 1, 1, 20))
"""
   print 'Exp ' + str(e) + ' ' + str(r)
   for i in range(0,20) :
      s[i] += r[i+1][1]
      
for i in range(0,20) :
   print s[i]
   

