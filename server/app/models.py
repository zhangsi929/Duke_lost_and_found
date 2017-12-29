from django.db import models
from django.contrib.auth.models import User
from django.utils import timezone
import base64
import os


class Reporter(models.Model):
    user = models.OneToOneField(User, on_delete=models.CASCADE)
    name = models.CharField(max_length=300, default="anonymous")
    email = models.CharField(max_length=100, default=0)
    phone = models.IntegerField(default=0)
    def __str__(self):
        return self.name

    @classmethod
    def create(cls, user):
        reporter = cls(user=user)
        # do something with the book
        return reporter


class LostItem(models.Model):
    name = models.CharField(max_length=50)
    lost_time = models.DateTimeField('time lost', default=timezone.now)
    lost_desc = models.CharField(max_length=500, default=0)
    item_id = models.AutoField(primary_key=True) #customed primary key
    lost_location = models.CharField(max_length=300, default="Duke Campus")
    posted_time = models.DateTimeField('date published', default=timezone.now)
    lost_category = models.IntegerField(default=0)
    image = models.CharField(max_length=5000000, default="")
    reporter = models.ForeignKey(Reporter, on_delete=models.CASCADE, related_name="lostitems")
    def __str__(self):
        return self.name

    def as_json(self):
        return dict(
            isLost=True,
            name=self.name,
            lost_time=self.lost_time.isoformat(),
            lost_desc=self.lost_desc,
            lost_location=self.lost_location,
            lost_category = self.lost_category,
            reporter_email=self.reporter.email,
            reporter_phone=self.reporter.phone,
            posted_time=self.posted_time.isoformat(),
            reporter_name=self.reporter.name,
            image=self.image
            # lost_location=self.input_date.isoformat(),
            )

class FoundItem(models.Model):
    name = models.CharField(max_length=50)
    found_time = models.DateTimeField('time found', default=timezone.now)
    found_desc = models.CharField(max_length=500, default=0)
    item_id = models.AutoField(primary_key=True) #customed primary key
    found_location = models.CharField(max_length=300, default="Duke Campus")
    posted_time = models.DateTimeField('time published', default=timezone.now)
    found_category = models.IntegerField(default=0)
    image = models.CharField(max_length=5000000, default="")
    reporter = models.ForeignKey(Reporter, on_delete=models.CASCADE, related_name="founditems")
    def __str__(self):
        return self.name

    def as_json(self):
        return dict(
            isLost=False,
            name=self.name,
            found_time=self.found_time.isoformat(),
            found_desc=self.found_desc,
            found_location=self.found_location,
            found_category = self.found_category,
            reporter_email=self.reporter.email,
            reporter_phone=self.reporter.phone,
            posted_time=self.posted_time.isoformat(),
            reporter_name=self.reporter.name,
            image=self.image
            # lost_location=self.input_date.isoformat(),
            )
