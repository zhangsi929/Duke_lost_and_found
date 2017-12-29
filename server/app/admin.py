from django.contrib import admin

# Register your models here.
from .models import  LostItem, FoundItem, Reporter
admin.site.register(LostItem)
admin.site.register(FoundItem)
admin.site.register(Reporter)
