import json,urllib,urllib2,subprocess,time,csv,sys,random

url="http://localhost:9000/data/upload/wavesegment"
#url="http://128.97.93.30:9000/data/upload/wavesegment"
#url="http://localhost:8080/ws/upload"
#url="http://sensoract-vpds.appspot.com/ws/upload"
#url="http://192.168.17.11:9000/data/upload/wavesegment"

obj={}
obj["secretkey"]="93cf82461c324858961b6cc70fc5033d"
obj["data"]={}
obj["data"]["dname"]="Test_Device1"
obj["data"]["sname"]="Temperature"
#obj["data"]["sname"]="Temperature"
#obj["data"]["sid"]=sys.argv[1]
obj["data"]["sid"]="1"
obj["data"]["loc"]="Room_B306"
obj["data"]["sinterval"]="1"
obj["data"]["channels"]=[]
obj["data"]["channels"].append({})

obj["data"]["channels"][0]["cname"]="channel1"
obj["data"]["channels"][0]["unit"]="Celcious"
obj["data"]["channels"][0]["type"]="Double"

obj["data"]["channels"].append({})
obj["data"]["channels"][1]["cname"]="channel2"
obj["data"]["channels"][1]["unit"]="Celcious"
obj["data"]["channels"][1]["type"]="Double"

count = 1;
while 1:
	readings_core0=[]
	readings_core1=[]
	obj["data"]["timestamp"]=time.time()
	for x in range(0,0):
		# readings_core0.append(random.randint(20,30))
		readings_core0.append(1)
		readings_core1.append(2)
		# time.sleep(1)

	readings_core0.append(1)
	readings_core1.append(2)
	
	obj["data"]["channels"][0]["readings"]=readings_core0
	obj["data"]["channels"][1]["readings"]=readings_core1

	json_data=json.dumps(obj)
	post_data = json_data.encode('utf-8')
	print json_data
	
	try:
		req = urllib2.Request(url, post_data, {'Content-Type': 'application/json'})
		f = urllib2.urlopen(req)
		response = f.read()
		print response
		f.close()
		count = count + 1
	except Exception, e:
    		print "Couldn't do it: %s" % e	

	if (count == 12):
		count = 1;
		
	time.sleep(2)	

