from django.conf.urls import url
from . import views
app_name = "accounts"
urlpatterns = [
    url(r'^signup', views.signup, name='SignUp'),
    url(r'^login', views.Login, name='Login'),
    url(r'^logout', views.Logout, name='Logout'),
]
