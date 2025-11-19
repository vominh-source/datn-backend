from fastapi import APIRouter
from app.models.patient_models import PatientSyncRequest
from app.services.patient_service import sync_patient

router = APIRouter()

@router.post("/sync")
async def sync(req: PatientSyncRequest):
    return await sync_patient(req)
