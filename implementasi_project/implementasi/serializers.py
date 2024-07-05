# implementasi/serializers.py

from rest_framework import serializers
from .models import Project, ProjectFile, ProjectPhoto

class ProjectFileSerializer(serializers.ModelSerializer):
    class Meta:
        model = ProjectFile
        fields = '__all__'

class ProjectPhotoSerializer(serializers.ModelSerializer):
    class Meta:
        model = ProjectPhoto
        fields = '__all__'

class ProjectSerializer(serializers.ModelSerializer):
    files = ProjectFileSerializer(many=True, read_only=True)
    photos = ProjectPhotoSerializer(many=True, read_only=True)
    
    class Meta:
        model = Project
        fields = '__all__'