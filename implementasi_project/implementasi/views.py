from django.shortcuts import get_object_or_404, redirect, render
from django.http import JsonResponse
from django.contrib.auth import logout as auth_logout
from .models import Project, ProjectFile, ProjectPhoto
from implementasi.serializers import ProjectSerializer
from reportlab.pdfgen import canvas
from django.middleware.csrf import get_token
import io
from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework import status
from django.http import FileResponse

def login_redirect_view(request):
    return redirect('login')

def logout(request):
    auth_logout(request)
    return redirect('login')

def login_view(request):
    if request.method == 'POST':
        username = request.POST.get('username')
        password = request.POST.get('password')

        if username == 'admin' and password == '123456':
            request.session['is_logged_in'] = True
            request.session['username'] = username
            return JsonResponse({'success': True}, status=200)
        else:
            return JsonResponse({'success': False, 'error_message': 'Invalid username or password.'}, status=401)
    else:
        return render(request, 'login.html')

def homepage_view(request):
    if not request.session.get('is_logged_in'):
        return redirect('login')

    total_projects = Project.objects.count()
    completed_projects = Project.objects.filter(implementation_status='Selesai').count()
    ongoing_projects = Project.objects.filter(implementation_status='Berlangsung').count()
    failed_projects = Project.objects.filter(implementation_status='Gagal').count()

    context = {
        'total_projects': total_projects,
        'completed_projects': completed_projects,
        'ongoing_projects': ongoing_projects,
        'failed_projects': failed_projects
    }

    return render(request, 'homepage.html', context)

def project_list_view(request):
    if not request.session.get('is_logged_in'):
        return redirect('login')
    
    projects = Project.objects.all()
    return render(request, 'project_list.html', {'projects': projects})

def project_detail_view(request, pk):
    if not request.session.get('is_logged_in'):
        return redirect('login')

    project = get_object_or_404(Project, pk=pk)
    
    # Assuming 'files' and 'photos' are related fields to the 'Project' model
    files = project.files.all()  # Adjust this based on your model's related name
    photos = project.photos.all()  # Adjust this based on your model's related name
    
    context = {
        'project': project,
        'files': files,
        'photos': photos
    }
    
    return render(request, 'project_detail.html', context)

def project_update_view(request, pk):
    if not request.session.get('is_logged_in'):
        return redirect('login')
    
    project = get_object_or_404(Project, pk=pk)
    
    if request.method == 'POST':
        form_data = request.POST
        project.implementation_start_date = form_data.get('implementation_start_date')
        project.implementation_end_date = form_data.get('implementation_end_date')
        project.implementation_status = form_data.get('implementation_status')
        project.activity = form_data.get('activity')
        
        project.save()
        
        if 'file' in request.FILES:
            for file in request.FILES.getlist('file'):
                ProjectFile.objects.create(project=project, file=file)
        
        if 'photo' in request.FILES:
            for photo in request.FILES.getlist('photo'):
                ProjectPhoto.objects.create(project=project, photo=photo)
        
        return JsonResponse({'success': True, 'message': 'Project updated successfully.'}, status=200)
    
    return render(request, 'project_update.html', {'project': project})

class ProjectListCreateAPIView(APIView):
    def get(self, request):
        projects = Project.objects.all()
        serializer = ProjectSerializer(projects, many=True)
        return Response(serializer.data)

    def post(self, request):
        serializer = ProjectSerializer(data=request.data)
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data, status=status.HTTP_201_CREATED)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

class ProjectDeleteAPIView(APIView):
    def post(self, request):
        project_ids = request.data.get('projects', [])
        projects = Project.objects.filter(id__in=project_ids)
        
        if projects.exists():
            projects.delete()
            return Response({'success': True}, status=status.HTTP_204_NO_CONTENT)
        else:
            return Response({'success': False, 'error_message': 'Projects not found.'}, status=status.HTTP_404_NOT_FOUND)
        
class ProjectDownloadAPIView(APIView):
    def get(self, request):
        project_ids = request.GET.get('projects', '').split(',')
        projects = Project.objects.filter(id__in=project_ids)
        
        if not projects.exists():
            return Response({'success': False, 'error_message': 'Projects not found.'}, status=status.HTTP_404_NOT_FOUND)
        
        buffer = io.BytesIO()
        pdf = canvas.Canvas(buffer)
        
        y = 750
        
        for project in projects:
            pdf.drawString(100, y, f"Project Name: {project.name}")
            y -= 20
            pdf.drawString(100, y, f"Tujuan: {project.tujuan}")
            y -= 20
            pdf.drawString(100, y, f"Aktivitas: {project.activity if project.activity else 'None'}")
            y -= 20
            pdf.drawString(100, y, f"Project Management Start Date: {project.project_management_start_date.strftime('%B %d, %Y') if project.project_management_start_date else 'None'}")
            y -= 20
            pdf.drawString(100, y, f"Project Management End Date: {project.project_management_end_date.strftime('%B %d, %Y') if project.project_management_end_date else 'None'}")
            y -= 20
            pdf.drawString(100, y, f"Project Management Status: {project.project_management_status}")
            y -= 20
            pdf.drawString(100, y, f"Implementation Start Date: {project.implementation_start_date.strftime('%B %d, %Y') if project.implementation_start_date else 'None'}")
            y -= 20
            pdf.drawString(100, y, f"Implementation End Date: {project.implementation_end_date.strftime('%B %d, %Y') if project.implementation_end_date else 'None'}")
            y -= 20
            pdf.drawString(100, y, f"Implementation Status: {project.implementation_status}")
            y -= 20
            pdf.drawString(100, y, f"Accuracy: {project.accuracy}%")
            y -= 40

            # Include files related to the project
            pdf.drawString(100, y, "Files:")
            y -= 20
            for file_obj in project.files.all():
                pdf.drawString(120, y, file_obj.file.name)
                y -= 20
            
            # Include photos related to the project
            pdf.drawString(100, y, "Photos:")
            y -= 20
            for photo_obj in project.photos.all():
                pdf.drawString(120, y, photo_obj.photo.name)
                y -= 20
            
            y -= 20  # Additional space between projects
        
        pdf.showPage()
        pdf.save()
        
        buffer.seek(0)
        
        return FileResponse(buffer, as_attachment=True, filename='projects.pdf')