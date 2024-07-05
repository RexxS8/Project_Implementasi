// Function to get CSRF token
function getCookie(name) {
    let cookieValue = null;
    if (document.cookie && document.cookie !== '') {
        const cookies = document.cookie.split(';');
        for (let i = 0; i < cookies.length; i++) {
            const cookie = cookies[i].trim();
            if (cookie.startsWith(name + '=')) {
                cookieValue = decodeURIComponent(cookie.substring(name.length + 1));
                break;
            }
        }
    }
    return cookieValue;
}

// Login form submission
document.getElementById('loginForm').addEventListener('submit', function(event) {
    event.preventDefault();

    const formData = new FormData(this);

    fetch('/login/', {
        method: 'POST',
        body: formData,
        headers: {
            'X-CSRFToken': getCookie('csrftoken')
        }
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            window.location.href = '/homepage/';
        } else {
            showPopup(data.error_message);
        }
    })
    .catch(error => {
        console.error('Error:', error);
    });
});

// Show popup message
function showPopup(message) {
    const errorPopup = document.getElementById('errorPopup');
    const errorMessage = document.getElementById('errorMessage');
    errorMessage.innerText = message;
    errorPopup.style.display = 'block';
}

// Close popup message
function closePopup() {
    document.getElementById('errorPopup').style.display = 'none';
}

// Filter projects by name
function filterProjects() {
    const searchBox = document.getElementById('searchBox');
    const filter = searchBox.value.toLowerCase();
    const projectTableBody = document.getElementById('projectTableBody');
    const rows = projectTableBody.getElementsByTagName('tr');

    for (let i = 0; i < rows.length; i++) {
        const projectNameCell = rows[i].getElementsByTagName('td')[1];
        if (projectNameCell) {
            const projectName = projectNameCell.textContent || projectNameCell.innerText;
            if (projectName.toLowerCase().indexOf(filter) > -1) {
                rows[i].style.display = '';
            } else {
                rows[i].style.display = 'none';
            }
        }
    }
}

// Toggle select all checkboxes
function toggleSelectAll() {
    const selectAll = document.getElementById('selectAll');
    const checkboxes = document.getElementsByClassName('selectProject');

    for (let i = 0; i < checkboxes.length; i++) {
        checkboxes[i].checked = selectAll.checked;
    }
}

// Get IDs of selected projects
function getSelectedProjectIds() {
    const selectedProjects = [];
    const checkboxes = document.getElementsByClassName('selectProject');

    for (let i = 0; i < checkboxes.length; i++) {
        if (checkboxes[i].checked) {
            selectedProjects.push(checkboxes[i].dataset.projectId);
        }
    }
    return selectedProjects;
}

// Delete selected projects
document.getElementById('deleteSelected').addEventListener('click', function() {
    const selectedProjects = getSelectedProjectIds();

    if (selectedProjects.length > 0) {
        fetch('/api/projects/delete/', {
            method: 'POST',
            body: JSON.stringify({ projects: selectedProjects }),
            headers: {
                'Content-Type': 'application/json',
                'X-CSRFToken': getCookie('csrftoken')
            }
        })
        .then(response => {
            if (response.ok) {
                window.location.reload();
            } else {
                console.error('Delete failed.');
            }
        })
        .catch(error => {
            console.error('Error:', error);
        });
    } else {
        alert('Please select at least one project to delete.');
    }
});

// Download selected projects
document.getElementById('downloadSelected').addEventListener('click', function() {
    const selectedProjects = getSelectedProjectIds();

    if (selectedProjects.length > 0) {
        fetch(`/api/projects/download/?projects=${selectedProjects.join(',')}`, {
            method: 'GET',
            headers: {
                'X-CSRFToken': getCookie('csrftoken')
            }
        })
        .then(response => {
            if (response.ok) {
                return response.blob();
            } else {
                console.error('Download failed.');
            }
        })
        .then(blob => {
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = 'projects.zip';
            document.body.appendChild(a);
            a.click();
            a.remove();
        })
        .catch(error => {
            console.error('Error:', error);
        });
    } else {
        alert('Please select at least one project to download.');
    }
});

// Function to handle form submission
document.getElementById('updateForm').addEventListener('submit', function(event) {
    event.preventDefault();
    var formData = new FormData(this);
    fetch("{% url 'project_update' project.id %}", {
        method: 'POST',
        headers: {
            'X-CSRFToken': '{{ csrf_token }}'
        },
        body: formData
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            document.getElementById('notificationMessage').innerText = data.message;
            document.getElementById('notificationPopup').style.display = 'block';
        }
    })
    .catch(error => {
        console.error('Error:', error);
    });
});

// Function to close notification popup
function closeNotification() {
    document.getElementById('notificationPopup').style.display = 'none';
    window.location.href = "{% url 'project_list' %}";
}