from pydantic import BaseModel

class AskRequest(BaseModel):
    patient_id: str
    question: str

class ConversationSaveRequest(BaseModel):
    patient_id: str
    conversation_text: str

class TreatmentAdviceRequest(BaseModel):
    patient_id: str
    conversation_text: str

class RetrieveContextRequest(BaseModel):
    patient_id: str
    query: str
    top_k: int = 3

