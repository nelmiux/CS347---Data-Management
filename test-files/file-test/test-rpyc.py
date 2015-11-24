import sys

sys.path.insert(0, '../rpyc-3.2.3')
import rpyc

from time import clock

conn = rpyc.connect("smarmy-pirate.cs.utexas.edu", 9981)
"""
log = open('rpyc.log', 'w')
times = []
for i in range(100) :
    local = []
    begin = clock()
    for files in conn.root.getFiles(conn.root.getcwd()) :
        for file in files :
            local.append(file)
    end = clock()
    times.append(end - begin)
    log.write(str(end - begin) + "\n")
log.write("Average: " + str(reduce(lambda x, y: x+y, times)/len(times)))
"""
begin = clock()
for files in conn.root.getFiles(conn.root.getcwd()) :
    for file in files :
        log = open('r' + file, 'w')
        log.write(conn.root.getFile(file))
end = clock()
print str(end - begin)

