from fastapi import APIRouter
from app.models.request_models import AskRequest
from app.services.rag_service import answer_question

router = APIRouter()

@router.post("/")
async def ask(req: AskRequest):
    return await answer_question(req)
