from pydantic import BaseModel

class PatientSyncRequest(BaseModel):
    patient_id: str
    summary_text: str
