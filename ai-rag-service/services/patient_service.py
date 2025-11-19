from app.dependencies import embedding_model
from app.repository.vector_repository import delete_by_patient, add_patient_vector
from app.models.patient_models import PatientSyncRequest

async def sync_patient(req: PatientSyncRequest):
    summary = req.summary_text
    emb = embedding_model.encode(summary).tolist()

    delete_by_patient(req.patient_id)
    add_patient_vector(req.patient_id, summary, emb)

    return {"status": "ok", "patient_id": req.patient_id}
