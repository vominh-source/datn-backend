from fastapi import APIRouter

router = APIRouter()

@router.get("/status")
def get_status():
    """Kiểm tra trạng thái hệ thống"""
    return {
        "status": "ok",
        "service": "Patient Management",
        "version": "1.0.0"
    }

