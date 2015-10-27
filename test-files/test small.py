from BatchRemote import BatchRemote

service = BatchRemote()
mybatches ROOT in service :
	for files in ROOT.getFileNames(ROOT.getcwd()) :
	  print files