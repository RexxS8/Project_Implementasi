# implementasi/models.py

from django.db import models

class Project(models.Model):
    name = models.CharField(max_length=100)
    tujuan = models.TextField()
    project_management_start_date = models.DateField()
    project_management_end_date = models.DateField()
    project_management_status = models.CharField(max_length=20, choices=[
        ('Berlangsung', 'Berlangsung'),
        ('Selesai', 'Selesai'),
        ('Gagal', 'Gagal'),
    ])
    implementation_start_date = models.DateField(blank=True, null=True)
    implementation_end_date = models.DateField(blank=True, null=True)
    implementation_status = models.CharField(max_length=20, choices=[
        ('Berlangsung', 'Berlangsung'),
        ('Selesai', 'Selesai'),
        ('Gagal', 'Gagal'),
    ], blank=True, null=True)
    activity = models.CharField(max_length=255, blank=True, null=True)
    supervisor = models.CharField(max_length=100)
    anggota = models.JSONField(default=list)
    notes = models.TextField(blank=True)
    accuracy = models.DecimalField(max_digits=5, decimal_places=2, default=0.00)

    def __str__(self):
        return self.name

class ProjectFile(models.Model):
    project = models.ForeignKey(Project, related_name='files', on_delete=models.CASCADE)
    file = models.FileField(upload_to='uploads/')

    def __str__(self):
        return f"{self.project.name} - {self.file.name}"

class ProjectPhoto(models.Model):
    project = models.ForeignKey(Project, related_name='photos', on_delete=models.CASCADE)
    photo = models.ImageField(upload_to='photos/')

    def __str__(self):
        return f"{self.project.name} - {self.photo.name}"
