# Generated by Django 5.0.6 on 2024-06-28 05:20

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('implementasi', '0002_project_activity_project_upload_file'),
    ]

    operations = [
        migrations.RenameField(
            model_name='project',
            old_name='end_date',
            new_name='project_management_end_date',
        ),
        migrations.RenameField(
            model_name='project',
            old_name='start_date',
            new_name='project_management_start_date',
        ),
        migrations.RenameField(
            model_name='project',
            old_name='status',
            new_name='project_management_status',
        ),
        migrations.RemoveField(
            model_name='project',
            name='activity',
        ),
        migrations.AddField(
            model_name='project',
            name='implementation_end_date',
            field=models.DateField(blank=True, null=True),
        ),
        migrations.AddField(
            model_name='project',
            name='implementation_start_date',
            field=models.DateField(blank=True, null=True),
        ),
        migrations.AddField(
            model_name='project',
            name='implementation_status',
            field=models.CharField(blank=True, choices=[('Berlangsung', 'Berlangsung'), ('Selesai', 'Selesai'), ('Gagal', 'Gagal')], max_length=20, null=True),
        ),
    ]
