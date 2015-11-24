connectTo 'jdbc:oracle:thin:@ib-perfdb.us.oracle.com:1525:perfdb' 'cannata' 'orcl' 'remote';
for i in range(1,65) :
   SQL """ insert into ibexperiment(name, exp_date, tool, protocol, published) values ('BM  on cloud-mwm-15 -> cloud-mwm-16 S12b23 1 stream only, multi-parts """ i """/ 64', to_char(sysdate), 'rds-stress', 'IPoIB', 'Yes');"""
