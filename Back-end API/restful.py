from flask import Flask, jsonify, request
import mysql.connector
import requests
import config

app=Flask(__name__)

# cnx = mysql.connector.connect(user='bsoares', password='GOmanny1436',
#                               host='rds-mysql-fanfootball.cwmwr07yxatm.us-east-1.rds.amazonaws.com',
#                               database='FanFootball')
# cursor = cnx.cursor()
# query = ("SELECT * FROM RBTable")
         


@app.route('/', methods=['GET'])
def test():
	# cursor.execute(query)
	# rows = cursor.fetchall()
	temp="ask"
	# cnx.close()
	#return jsonify({'message':rows})
	r=requests.get("https://na1.api.riotgames.com/lol/summoner/v3/summoners/by-name/gladdyy?api_key=RGAPI-513c5309-2e32-4bd2-802a-ceace452264b")
	json=r.json()
	return jsonify(json)

@app.route('/lang', methods=['GET'])
def returnLangs():
	return jsonify({'languages':languages})

@app.route('/lang/<string:name>', methods=['GET'])
def returnSingle(name):
	langs = [language for language in languages if language['name']==name]
	return jsonify({'language': langs[0]})


if __name__ =='__main__':
	app.run(debug=True, port=8080)