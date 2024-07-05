# implementasi/forms.py

from django import forms
from .models import Project

class ProjectForm(forms.ModelForm):
    class Meta:
        model = Project
        fields = ['name', 'activity', 'implementation_start_date', 'implementation_end_date', 'implementation_status', 'upload_file']
