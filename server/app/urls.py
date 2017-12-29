from django.conf.urls import include, url
from django.contrib import admin
from . import views
app_name = "app"
urlpatterns = [
    url(r'^found', views.insert_found),
    url(r'^lost', views.insert_lost),
    url(r'^query/?',views.query),
    url(r'^match/?',views.match),
    url(r'^profile/update',views.update_prpfile),
    url(r'^reported/?',views.get_reported),
]
