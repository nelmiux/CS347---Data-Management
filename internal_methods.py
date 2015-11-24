import time
import flask
import json

class InternalMethods():
    def __init__(self):
        """
        Instantiate
        """
        pass

    def db_connect(self, db, user, pass_word, mode, model=False):
        (db_version, db_value, db_request) = db
        (user_type, user_value, user_request) = user
        (pass_type, pass_value, pass_request) = pass_word
        (mode_type, mode_value, mode_request) = mode

        if model:
            (model_type, model_value, model_request) = model
            conect_address = "connectTo '" + db_value.strip() + "' '" + user_value.strip() + "' '" + pass_value.strip() + "' '" + mode_value + "' '" + model_value + "'"
        else:
            conect_address = "connectTo '" + db_value.strip() + "' '" + user_value.strip() + "' '" + pass_value.strip() + "' '" + mode_value + "'"

        user_conn = eval(conect_address)
        return user_conn

    def rest_call(self, connection, action, path, headers):
        """
        This function provides REST call functionality and provides JSON object for curl/browser requests.
        """
        approved_actions = {'native': True}
        action = action.lower()

        if action not in approved_actions:
            return 'Invalid DB operation'
        print headers
        params = dict()
        for (k, v) in flask.request.args.iteritems():
            params[k.lower()] = v
            exec('%s = v' % k)
        
        restricted_keys = ['mode', 'connection', 'accept-encoding', 'pass', 'content-type', 'cache-control', 'user-agent', 'host', 'db', 'user', 'accept', 'accept-language', 'content-length']
        for (k, v) in headers.iteritems():
            key = str(k).lower()
            if key not in restricted_keys:
                # print key, v
                if key != 'x-forwarded-for' :
                   exec('%s = v' % key)
        
        statement = "SQL on connection %s" % params['query']
        statement = "Neo4j on connection %s" % params['query']
        # print statment
        print params
        results = eval(statement)
        # print results
        print results[0:3], "\n"
        
        # returnFor = 'R'
        if returnfor == 'R' :
           h = results[0]
           if returndimensions == 'False' :
              l = tuple([ tuple([ i[j] for i in results[1:len(results)]]) for j in range(0, len(results[0])) ])
           else :
              l = tuple([ tuple([ i[j] if i[j] != 'null' else 0 for i in results[1:len(results)]]) for j in range(0, len(results[0])) ])
              dimensions = []
              measures = []
              n = -1
              for i in l :
                 n += 1
                 try:
                    reduce(lambda x, y: x + y, (1, ) + i)
                    measures.append(h[n])
                 except Exception, e:
                    dimensions.append(h[n])
                    continue
              # print "dimensions are: ", dimensions, "\n"
              # print "measures are: ", measures, "\n"
              s = "list(list" + str(tuple(dimensions)) + ", list" + str(tuple(measures)) + ")"
              # print s
              return(s)
           # s = "list(c" + str(h) + ", list("
           s = "list("
           n = 0
           for i in l :
               s = s + h[n] + '=c' + str(i) + ',' 
               n = n + 1
           s = s[:-1]
           s = s + ')'
           # print s
           print s[0:2], "\n"
           return s
           
        # returnFor = 'JSON'
        if returnfor == 'JSON' :
           h = results[0]
           if returndimensions == 'False' :
              l = tuple([ tuple([ i[j] for i in results[1:len(results)]]) for j in range(0, len(results[0])) ])
           else :
              l = tuple([ tuple([ i[j] if i[j] != 'null' else 0 for i in results[1:len(results)]]) for j in range(0, len(results[0])) ])
              dimensions = []
              measures = []
              n = -1
              for i in l :
                 n += 1
                 try:
                    reduce(lambda x, y: x + y, (1, ) + i)
                    measures.append(h[n])
                 except Exception, e:
                    dimensions.append(h[n])
                    continue
              # print "dimensions are: ", dimensions, "\n"
              # print "measures are: ", measures, "\n"
              s = "list(list" + str(tuple(dimensions)) + ", list" + str(tuple(measures)) + ")"
              # print s
              return(s)
           s = "{"
           n = 0
           for i in l :
               s = s + '"' + h[n] + '":' + str(i) + ',' 
               n = n + 1
           s = s[:-1]
           s = s + '}'
           import re
           a = re.sub('\'', '"', re.sub('\)}', ']}', re.sub('\),', '],',re.sub(':\(', ':[', s))))
           return a
           
        # returnFor = 'APEX_JSON'
        if returnfor == 'APEX_JSON' :
           h = results[0]
           if returndimensions == 'False' :
              l = tuple([ tuple([ i[j] for i in results[1:len(results)]]) for j in range(0, len(results[0])) ])
           else :
              l = tuple([ tuple([ i[j] if i[j] != 'null' else 0 for i in results[1:len(results)]]) for j in range(0, len(results[0])) ])
              dimensions = []
              measures = []
              n = -1
              for i in l :
                 n += 1
                 try:
                    reduce(lambda x, y: x + y, (1, ) + i)
                    measures.append(h[n])
                 except Exception, e:
                    dimensions.append(h[n])
                    continue
              # print "dimensions are: ", dimensions, "\n"
              # print "measures are: ", measures, "\n"
              s = "list(list" + str(tuple(dimensions)) + ", list" + str(tuple(measures)) + ")"
              # print s
              return(s)
           s = "[{"
           n = 0
           for i in l :
               s = s + '"' + h[n] + '":' + str(i) + ',' 
               n = n + 1
           s = s[:-1]
           s = s + '}]'
           import re
           a = re.sub('\'', '"', re.sub('\)}', ']}', re.sub('\),', '],',re.sub(':\(', ':[', s))))
           return a

        json_results = json.dumps(results)
        return json_results
