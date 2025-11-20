from fastapi import APIRouter
from app.models.request_models import (
    AskRequest,
    TreatmentAdviceRequest,
    RetrieveContextRequest
)
from app.services.rag_service import (
    generate_treatment_advice,
    retrieve_context,
    save_conversation
)
from app.services.gemini_service import ask_gemini

router = APIRouter()

@router.post("/treatment-advice")
async def get_treatment_advice(req: TreatmentAdviceRequest):
    """Sinh gợi ý chữa trị từ cuộc hội thoại bệnh nhân"""
    suggestion = generate_treatment_advice(req.conversation_text)
    
    # Lưu vào vector DB
    save_result = save_conversation(
        req.patient_id,
        req.conversation_text,
        suggestion
    )
    
    return {
        "patient_id": req.patient_id,
        "suggestion": suggestion,
        "saved": save_result
    }

@router.post("/retrieve-context")
async def get_context(req: RetrieveContextRequest):
    """Truy xuất context lịch sử từ vector DB"""
    context = retrieve_context(req.patient_id, req.query, req.top_k)
    return {
        "patient_id": req.patient_id,
        "query": req.query,
        "context": context
    }

@router.post("/ask")
async def ask_question(req: AskRequest):
    """Hỏi câu hỏi với context từ lịch sử bệnh nhân"""
    context = retrieve_context(req.patient_id, req.question)
    answer = ask_gemini(context, req.question)
    return {
        "patient_id": req.patient_id,
        "question": req.question,
        "context_retrieved": len(context) > 0,
        "answer": answer
    }

