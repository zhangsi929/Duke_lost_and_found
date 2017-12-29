from django.shortcuts import render
from django.http import HttpResponse
from django.views.decorators.csrf import csrf_exempt
from django.http import JsonResponse
from django.core import serializers
from django.utils import timezone
from django.contrib.auth.decorators import login_required
from django.contrib.auth.models import User
from PIL import Image
from base64 import decodestring
from base64 import b64decode
import dateutil
import dateutil.parser
import json
from django.core.files.base import ContentFile
from urlparse import urlparse,parse_qs #for python2
#from urllib.parse import urlparse, parse_qs #for python3
import requests
import pytz
from .models import LostItem
from .models import FoundItem
import datetime
import urllib
from django.utils.html import escape
from lru import LRU
# from urllib.parse   import quote #for python3

TZINFOS = {
        'EST': pytz.timezone('America/New_York'),
        # ... add more to handle other timezones
        # (I wish pytz had a list of common abbreviations)
    }

dis_cache = LRU(1000)

@csrf_exempt
def insert_found(request):
    if request.method == "POST":
        body_unicode = request.body.decode('utf-8')
        body = json.loads(body_unicode)
        if ("username" not in body) or (not User.objects.filter(username=body["username"]).exists()):
            return JsonResponse({'status': 'fail: The login information is wrong'})

        #create object and save it in db
        if "item_name" in body:
            found = FoundItem(body["item_name"])
            if "found_time" in body:
                found.found_time = datetime.datetime.strptime(body["found_time"], "%Y-%m-%dT%H:%M:%SZ" )
                #found.found_time = dateutil.parser.parse(body["found_time"])
            if "found_location" in body:
                found.found_location = body["found_location"]
            if "found_desc" in body:
                found.found_desc = body["found_desc"]
            if "found_category" in body:
                found.found_category = body["found_category"]
            if "image" in body:
                found.image = body["image"]
            found.reporter = User.objects.get(username=body["username"]).reporter
            found.posted_time = timezone.localtime(timezone.now())
            found.save()
            #return the response
            return JsonResponse({'status': 'success'})
        else:
            return JsonResponse({'status': 'fail'})
    else:
        return JsonResponse({'status': 'fail'})


@csrf_exempt
def insert_lost(request):
    if request.method == "POST":
        body_unicode = request.body.decode('utf-8')
        body = json.loads(body_unicode)
        #error check
        if ("username" not in body) or (not User.objects.filter(username=body["username"]).exists()):
            return JsonResponse({'status': 'fail: The login information is wrong'})

        #create object and save it in db
        if "item_name" in body:
            lost = LostItem(body["item_name"])
            if "lost_time" in body:
                lost.lost_time = datetime.datetime.strptime(body["lost_time"], "%Y-%m-%dT%H:%M:%SZ" )
               # lost.lost_time = dateutil.parser.parse(body["lost_time"])
            if "lost_location" in body:
                lost.lost_location = body["lost_location"]
            if "lost_desc" in body:
                lost.lost_desc = body["lost_desc"]
            if "lost_category" in body:
                lost.lost_category = body["lost_category"]
            if "image" in body:
                lost.image = body["image"]
            lost.reporter = User.objects.get(username=body["username"]).reporter
            lost.posted_time = timezone.localtime(timezone.now())
            lost.save()
            #return the response
            return JsonResponse({'status': 'success'})
        else:
            return JsonResponse({'status': 'fail'})
    else:
        return JsonResponse({'status': 'fail'})

def get_all_data(request):
    lost_results = [ob.as_json() for ob in list(LostItem.objects.all())]
    found_results = [ob.as_json() for ob in list(FoundItem.objects.all())]
    jason_result = {"lost_items" : lost_results, "found_items" : found_results}
    return HttpResponse(json.dumps(jason_result), content_type="application/json")

def getfounddata(request):
    found_results = [ob.as_json() for ob in list(FoundItem.objects.all())]
    jason_result = {"found_items" : found_results}
    return HttpResponse(json.dumps(jason_result), content_type="application/json")

def getlostdata(request):
    lost_results = [ob.as_json() for ob in list(LostItem.objects.all())]
    jason_result = {"lost_items" : lost_results}
    return HttpResponse(json.dumps(jason_result), content_type="application/json")

def lost_getdata(request, keyword):
    tempList = LostItem.objects.filter(name__icontains=keyword);
    lost_results = [ob.as_json() for ob in list(tempList)]
    jason_result = {"lost_items" : lost_results}
    return HttpResponse(json.dumps(jason_result), content_type="application/json")

def found_getdata(request, keyword):
    found_results = [ob.as_json() for ob in list(FoundItem.objects.filter(name__icontains=keyword))]
    jason_result = {"found_items" : found_results}
    return HttpResponse(json.dumps(jason_result), content_type="application/json")

def all_getdata(request, keyword):
    lost_results = [ob.as_json() for ob in list(LostItem.objects.filter(name__icontains=keyword))]
    found_results = [ob.as_json() for ob in list(FoundItem.objects.filter(name__icontains=keyword))]
    jason_result = {"lost_items" : lost_results, "found_items" : found_results}
    return HttpResponse(json.dumps(jason_result), content_type="application/json")

def query(request):
    print(request)
    full_url = (request.build_absolute_uri())
    urlparse(full_url).query
    parameters = parse_qs(urlparse(full_url).query)
    # print(parameters)
    lost_results = []
    found_results = []
    if ('lost' in parameters) and parameters['lost'][0] == "true":
        #print("we need lost")
        lost_results = getLostresults(parameters)
    if ('found' in parameters) and parameters['found'][0] == "true":
        #print("we need found")
        found_results = getFoundresults(parameters)
    #combine results and send them back
    jason_result = {"lost_items" : lost_results, "found_items" : found_results}
    return HttpResponse(json.dumps(jason_result), content_type="application/json")

def getLostresults(parameters):
    lost_results = LostItem.objects
    queryflag = False
    # check the json data & construct object
    if 'name' in parameters:
        queryflag = True
        keyword = parameters['name'][0]
        lost_results = lost_results.filter(name__icontains=keyword)
    if 'category' in parameters:
        queryflag = True
        category = parameters['category'][0]
        lost_results = lost_results.filter(lost_category=category)
    if 'description' in parameters:
        queryflag = True
        description = parameters['description'][0]
        lost_results = lost_results.filter(lost_desc__icontains=description)
    if 'location' in parameters:
        target_location = parameters['location'][0]
        result = []
        temp = []
        #call the Google Map API
        if queryflag:
            temp = [ob.as_json() for ob in list(lost_results)]
        else:
            temp = [ob.as_json() for ob in list(lost_results.all())]
        for item in temp:
            if distanceMatch(item["lost_location"], target_location):
                result.append(item)
        return result
    result = []
    # save data return the result
    if queryflag:
        result = [ob.as_json() for ob in list(lost_results)]
    else:
        result = [ob.as_json() for ob in list(lost_results.all())]
    # print (result)
    return result

def getFoundresults(parameters):
    found_results = FoundItem.objects
    queryflag = False
    #check json data and construct object
    if 'name' in parameters:
        queryflag = True
        keyword = parameters['name'][0]
        found_results = found_results.filter(name__icontains=keyword)
    if 'category' in parameters:
        queryflag = True
        category = parameters['category'][0]
        found_results = found_results.filter(found_category=category)
    if 'description' in parameters:
        queryflag = True
        description = parameters['description'][0]
        found_results = found_results.filter(found_desc__icontains=description)
    # print(found_results.all())
    if 'location' in parameters:
        target_location = parameters['location'][0]
        result = []
        temp = []
        if queryflag:
            temp = [ob.as_json() for ob in list(found_results)]
        else:
            temp = [ob.as_json() for ob in list(found_results.all())]
        #call the Google Map API
        for item in temp:
            if distanceMatch(item["found_location"], target_location):
                result.append(item)
        return result
    result = []
    if queryflag:
        result = [ob.as_json() for ob in list(found_results)]
    else:
        result = [ob.as_json() for ob in list(found_results.all())]
    # print (result)
    return result

def distanceMatch(location1, location2):
    print(dis_cache)
    matach_key = location1 + "to" + location2
    if matach_key in dis_cache:
        return dis_cache[(location1 + "to" + location2)]
    #for python2
    url = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + urllib.quote_plus(location1) + "&destinations=" + urllib.quote_plus(location2) + "&key=AIzaSyDmQU640wZsR7H67t2Oc1x44OPIRGOnT4Q&language=EN"

    #for python3
    # url = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + quote(location1) +"&destinations=" + quote(location2) + "&key=AIzaSyDmQU640wZsR7H67t2Oc1x44OPIRGOnT4Q&language=EN"
    print(url)
    req = requests.get(url)
    response = req.json()
    # print(response)
    #check status
    if ('status' in response) and (response['status'] == 'OK'):
        print(response['rows'][0])
        if response['rows'][0]['elements'][0]['status'] == 'OK':
            #if everything is OK, we will check the distance
            if response['rows'][0]['elements'][0]['distance']['value'] < 1300: #less than 1.3km
                dis_cache[(matach_key)] = True
                return True
            else:
                dis_cache[(matach_key)] = False
                return False
    dis_cache[(matach_key)] = False
    return False

def match(request):
    full_url = (request.build_absolute_uri())
    urlparse(full_url).query
    parameters = parse_qs(urlparse(full_url).query)
    print(parameters)
    print(parameters["username"][0])
    if ("username" not in parameters) or (not User.objects.filter(username=parameters["username"][0]).exists()):
        return JsonResponse({'status': 'fail: The login information is wrong'})
    username = parameters["username"][0]
    print(username)
    #for lost matching
    #1.Get all items the user reported:
    reporter = User.objects.get(username=username).reporter
    lost_items = reporter.lostitems.all()
    #2.Find matching results:
    lost_match_results = set()
    for lost_item in lost_items:
        lost_match_results |= queryFoundByNameAndLoc(lost_item)
    # print(lost_match_results)
    temp_lost = [ob.as_json() for ob in list(lost_match_results)]

    #for found matching
    #1.Get all items the user reported:
    found_items = reporter.founditems.all()
    #2.Find matching results:
    found_match_results = set()
    for found_item in found_items:
        found_match_results |= queryLostByNameAndLoc(found_item)
    # print(lost_match_results)
    temp_found = [ob.as_json() for ob in list(found_match_results)]

    #3.return results
    jason_result = {"lost_matching" : temp_lost, "found_matching" : temp_found, "status": "success"}
    return HttpResponse(json.dumps(jason_result), content_type="application/json")


def queryFoundByNameAndLoc(lost_item):
    #filter by name
    temp_results = FoundItem.objects.filter(name__icontains=lost_item.name)
    results = set()
    #filter by location
    for temp_result in temp_results:
        if distanceMatch(temp_result.found_location, lost_item.lost_location):
            results.add(temp_result)
    # print(set(results))
    return set(results)

def queryLostByNameAndLoc(found_item):
    #filter by name
    temp_results = LostItem.objects.filter(name__icontains=found_item.name)
    # print(set(results))
    results = set()
    #filter by location
    for temp_result in temp_results:
        if distanceMatch(temp_result.lost_location, found_item.found_location):
            results.add(temp_result)
    return set(results)

@csrf_exempt
def update_prpfile(request):
    if request.method == "POST":
        body_unicode = request.body.decode('utf-8')
        body = json.loads(body_unicode)
        #construct object
        if ("username" not in body) or (not User.objects.filter(username=body["username"]).exists()):
            return JsonResponse({'status': 'fail: The login information is wrong'})
        reporter = User.objects.get(username=body["username"]).reporter
        if "phone" in body:
            reporter.phone = body["phone"]
        if "email" in body:
            reporter.email = body["email"]
        if ("password" in body) and (len(body["password"]) >= 6):
            print("change password")
            reporter.user.set_password(body["password"])
            reporter.user.save()
        reporter.save()
        jason_result = {"status": "success"}
        return HttpResponse(json.dumps(jason_result), content_type="application/json")
    #send response
    jason_result = {"status": "failure:please check the network or login information"}
    return HttpResponse(json.dumps(jason_result), content_type="application/json")

def get_reported(request):
    full_url = (request.build_absolute_uri())
    urlparse(full_url).query
    parameters = parse_qs(urlparse(full_url).query)
    print(parameters)
    print(parameters["username"][0])
    #query items by user
    if ("username" not in parameters) or (not User.objects.filter(username=parameters["username"][0]).exists()):
        return JsonResponse({'status': 'fail: The login information is wrong'})
    reporter = User.objects.get(username=parameters["username"][0]).reporter
    lost_results = [ob.as_json() for ob in list(reporter.lostitems.all())]
    found_results = [ob.as_json() for ob in list(reporter.founditems.all())]
    #send response
    jason_result = {"lost_items" : lost_results, "found_items" : found_results}
    return HttpResponse(json.dumps(jason_result), content_type="application/json")
