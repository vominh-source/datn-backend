from app.dependencies import embedding_model
from app.repository.vector_repository import query_patient
from app.services.gemini_service import ask_gemini
from app.models.request_models import AskRequest

async def answer_question(req: AskRequest):
    query_emb = embedding_model.encode(req.question).tolist()

    results = query_patient(req.patient_id, query_emb)

    if not results["documents"]:
        return {"answer": "No data found for this patient."}

    context = results["documents"][0][0]

    answer = ask_gemini(context, req.question)

    return {"answer": answer}
