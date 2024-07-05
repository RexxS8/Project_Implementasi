# implementasi/urls.py

from django.urls import path
from django.conf import settings
from django.conf.urls.static import static
from implementasi import views

urlpatterns = [
    path('', views.login_redirect_view, name='login_redirect'),
    path('login/', views.login_view, name='login'),
    path('logout/', views.logout, name='logout'),
    path('homepage/', views.homepage_view, name='homepage'),
    path('projects/', views.project_list_view, name='project_list'),
    path('projects/<int:pk>/', views.project_detail_view, name='project_detail'),
    path('projects/update/<int:pk>/', views.project_update_view, name='project_update'),
    path('api/projects/', views.ProjectListCreateAPIView.as_view(), name='project_list_create_api'),
    path('api/projects/delete/', views.ProjectDeleteAPIView.as_view(), name='project_delete_api'),
    path('api/projects/download/', views.ProjectDownloadAPIView.as_view(), name='project_download_api'),
]

# Serve media files during development
if settings.DEBUG:
    urlpatterns += static(settings.MEDIA_URL, document_root=settings.MEDIA_ROOT)
