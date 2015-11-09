import flask
from flask import Flask, jsonify
from flask import render_template
from RestfulRel.methods import *
import json
import time

app = Flask(__name__)

@app.route('/')
@app.route('/index')
def index():

    return 'Send rest calls to <url>/rest/native?query=<query>'


@app.route('/rest/<action>/', methods=['GET'], defaults={'path': ''})
@app.route('/rest/<action>/<path:path>', methods=['GET'])
def rest_call(action, path):
    internal = InternalMethods()

    try:
        db = ("DB", flask.request.headers['DB'], flask.request)
        user = ("USER", flask.request.headers['USER'], flask.request)
        pass_word = ("PASS", flask.request.headers['PASS'], flask.request)
        mode = ("MODE", flask.request.headers['MODE'], flask.request)
    except KeyError as e:
            return(error(
                   403,
                   "authentication headers not provided",
                   validheaders=['DB', 'USER', 'PASS', 'MODE']))
    print mode[0]
    if mode[1] == 'rdf_mode':
        try:
            model = ("MODEL", flask.request.headers['MODEL'], flask.request)
        except KeyError as e:
            return(error(402,'please provide model for rdf_mode'))
        print model
        user_conn = internal.db_connect(db, user, pass_word, mode, model=model)

    else:
        user_conn = internal.db_connect(db, user, pass_word, mode)
    #print flask.request.headers
    json_response = internal.rest_call(user_conn, action, path, flask.request.headers)
    return json_response

