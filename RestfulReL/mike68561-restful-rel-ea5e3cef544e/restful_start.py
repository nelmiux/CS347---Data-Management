from RestfulRel import app


#def run_server():
   
    # Set the configuration of the web server
#    server = Rocket(interfaces=('0.0.0.0', 80), method='wsgi',
#                    app_info={"wsgi_app": app})

    # Start the Rocket web server
   # server.start()

if __name__ == "__main__":
    app.run(host='0.0.0.0', port=5001)
