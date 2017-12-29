from django.contrib.auth.models import User
from django.contrib.auth import authenticate, login, logout
from django.shortcuts import render
from django.http import HttpResponse
from django.views.decorators.csrf import csrf_exempt
from django.http import JsonResponse
from django.core import serializers
from django.utils import timezone
from django.contrib.auth.decorators import login_required
from django.contrib.auth.models import User
from app.models import Reporter
import dateutil
import dateutil.parser
import json

@csrf_exempt
def signup(request):
    if request.method == "POST":
        body_unicode = request.body.decode('utf-8')
        body = json.loads(body_unicode)
        if ("name" in body) and ("username" in body) and ("password" in body) and ("email" in body) and ("phone" in body):
            try:
                User.objects.get(username = body["username"])
                return JsonResponse({'status': 'fail: the username has been taken'})
            except User.DoesNotExist:
                user = User.objects.create_user(body["username"], password=body["password"])
                reporter = Reporter.create(user)
                reporter.email = body["email"]
                reporter.phone = body["phone"]
                reporter.name = body["name"]
                reporter.save()
                jason_result = {'status': 'success'}
                return HttpResponse(json.dumps(jason_result), content_type="application/json")
        else:
            jason_result = {'status': 'fail: you need post more field'}
            return HttpResponse(json.dumps(jason_result), content_type="application/json")

@csrf_exempt
def Login(request):
    if request.method == "POST":
        body_unicode = request.body.decode('utf-8')
        body = json.loads(body_unicode)
        user = authenticate(username=body["username"], password=body["password"])
        if user is not None:
            login(request, user)
            jason_result = {'status': 'success'}
            return HttpResponse(json.dumps(jason_result), content_type="application/json")
        else:
            jason_result = {'status': 'fail:the user name and password did not match'}
            return HttpResponse(json.dumps(jason_result), content_type="application/json")

@csrf_exempt
def Logout(request):
    if request.method == "POST":
        logout(request)
        jason_result = {'status': 'success'}
        return HttpResponse(json.dumps(jason_result), content_type="application/json")
