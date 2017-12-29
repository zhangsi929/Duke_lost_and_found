from django.test import TestCase, RequestFactory
from .models import  LostItem, FoundItem, Reporter
from django.contrib.auth.models import User
from django.utils import timezone
from .views import *
import json
import ast

# test for reporter table
class ReporterTestCase(TestCase):
    def setUp(self):
        user1 = User.objects.create_user("name_test1", password="name_test1")
        reporter1 = Reporter.create(user1)
        reporter1.email = "test@email.com"
        reporter1.phone = 9999900000
        reporter1.name = "tester1"
        reporter1.save()
        user2 = User.objects.create_user("name_test2", password="passw0rd")
        reporter2 = Reporter.create(user2)
        reporter2.email = "test2@email.edu"
        reporter2.phone = 1232323
        reporter2.name = "tester2"
        reporter2.save()

    def test_reporter_table_set_up(self):
        """Reporter created success identified"""
        user1 = User.objects.get(username="name_test1")
        reporter1 = user1.reporter
        self.assertEqual(reporter1.phone, 9999900000)
        self.assertEqual(reporter1.email, 'test@email.com')
        self.assertEqual(reporter1.name, 'tester1')
        user2 = User.objects.get(username="name_test2")
        reporter2 = user2.reporter
        self.assertEqual(reporter2.phone, 1232323)
        self.assertEqual(reporter2.email, 'test2@email.edu')
        self.assertEqual(reporter2.name, 'tester2')

# test for LostItem table
class LostItemTestCase(TestCase):
    def setUp(self):
        user1 = User.objects.create_user("name_test1", password="name_test1")
        reporter1 = Reporter.create(user1)
        reporter1.email = "test@email.com"
        reporter1.phone = 9999900000
        reporter1.name = "tester1"
        reporter1.save()
        lost = LostItem("lost_test")
        lost.lost_location = "duke univeristy hudson hall"
        lost.lost_desc = "some description"
        lost.lost_category = 2
        lost.image = "image_encode_string"
        lost.posted_time = timezone.localtime(timezone.now())
        lost.reporter = reporter1
        lost.save()
    def test_LostItem_table_set_up(self):
        lost = LostItem.objects.get(name="lost_test")
        self.assertEqual(lost.image, "image_encode_string")
        self.assertEqual(lost.lost_category, 2)
        self.assertEqual(lost.lost_desc, "some description")
        self.assertEqual(lost.lost_location, "duke univeristy hudson hall")

# test for FoundItem table
class FoundItemTestCase(TestCase):
    def setUp(self):
        user1 = User.objects.create_user("name_test1", password="name_test1")
        reporter1 = Reporter.create(user1)
        reporter1.email = "test@email.com"
        reporter1.phone = 9999900000
        reporter1.name = "tester1"
        reporter1.save()
        found = FoundItem("found_test")
        found.found_location = "duke univeristy hudson hall"
        found.found_desc = "some description"
        found.found_category = 2
        found.image = "image_encode_string"
        found.posted_time = timezone.localtime(timezone.now())
        found.reporter = reporter1
        found.save()
    def test_LostItem_table_set_up(self):
        found = FoundItem.objects.get(name="found_test")
        self.assertEqual(found.image, "image_encode_string")
        self.assertEqual(found.found_category, 2)
        self.assertEqual(found.found_desc, "some description")
        self.assertEqual(found.found_location, "duke univeristy hudson hall")

class GoogleAPITestCase(TestCase):
    def test_distance(self):
        self.assertEqual(distanceMatch("Duke University Hudson Hall", "Duke Hospital"), False)
        self.assertEqual(distanceMatch("Duke University Hudson Hall", "Duke University perkins library"), False)
        self.assertEqual(distanceMatch("Duke University Ciemas", "Duke University perkins library"), False)
        self.assertEqual(distanceMatch("Duke University West Bus Stop", "Duke University West Union"), True)
        self.assertEqual(distanceMatch("Duke University Pratt", "Duke University Hudson Hall"), True)
        self.assertEqual(distanceMatch("Duke University Teer Building", "Duke University Hudson Hall 207"), True)

#make sure the query function work before running the server
class QueryTestCase(TestCase):
    def setUp(self):
        self.factory = RequestFactory()
        user1 = User.objects.create_user("name_test1", password="name_test1")
        reporter1 = Reporter.create(user1)
        reporter1.email = "test@email.com"
        reporter1.phone = 9999900000
        reporter1.name = "tester1"
        reporter1.save()
        user2 = User.objects.create_user("name_test2", password="name_test1")
        reporter2 = Reporter.create(user2)
        reporter2.email = "test2@email.com"
        reporter2.phone = 111222
        reporter2.name = "tester2"
        reporter2.save()
        found1 = FoundItem("iphone7")
        found1.found_location = "duke univeristy hudson hall"
        found1.found_desc = "it's a black iphone7 with black protector case"
        found1.found_category = 2
        found1.image = "image_encode_string"
        found1.posted_time = timezone.localtime(timezone.now())
        found1.reporter = reporter1
        found1.save()
        found2 = FoundItem("jacket")
        found2.found_location = "duke univeristy Hospital"
        found2.found_desc = "this is a samll size black jacket"
        found2.found_category = 1
        found2.image = "image_encode_string"
        found2.posted_time = timezone.localtime(timezone.now())
        found2.reporter = reporter2
        found2.save()
        lost1 = LostItem("umbrella")
        lost1.lost_location = "duke univeristy hudson hall"
        lost1.lost_desc = "it's a blue umbrella found in hudson hall. I lost in ECE 651 calss at Tuesday"
        lost1.lost_category = 2
        lost1.image = "image_encode_string"
        lost1.posted_time = timezone.localtime(timezone.now())
        lost1.reporter = reporter1
        lost1.save()
        lost2 = LostItem("mac book pro")
        lost2.lost_location = "duke univeristy West Union"
        lost2.lost_desc = "it's the latest 15 inch mac book pro with touch bar"
        lost2.lost_category = 1
        lost2.image = "image_encode_string"
        lost2.posted_time = timezone.localtime(timezone.now())
        lost2.reporter = reporter2
        lost2.save()

    def test_query(self):
        request1 = self.factory.get('/app/query/?name=&category=&found=true&lost=true&description=&location=')
        response1 = query(request1)
        print("response:")
        json_string = (response1.content)
        self.assertEqual(response1.status_code, 200)
        print("json")
        jsonData = json.loads(json_string)
        print(type(jsonData))
        self.assertEqual(len(jsonData["found_items"]), 2)
        self.assertEqual(len(jsonData["lost_items"]), 2)
