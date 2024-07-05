# implementasi/templatetags/custom_filters.py

from django import template

register = template.Library()

@register.filter
def ends_with_image_extension(value):
    if value and isinstance(value, str):
        image_extensions = ['.jpg', '.jpeg', '.png', '.gif']
        return any(value.lower().endswith(ext) for ext in image_extensions)
    return False
