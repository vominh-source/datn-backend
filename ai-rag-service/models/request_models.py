from pydantic import BaseModel

class AskRequest(BaseModel):
    patient_id: str
    question: str
