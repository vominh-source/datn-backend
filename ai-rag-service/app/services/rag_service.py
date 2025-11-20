from app.dependencies import embedding_model, collection
from app.config import settings
import google.generativeai as genai

# Configure Gemini
genai.configure(api_key=settings.gemini_key)

def embed_texts(texts):
    """Embed danh sách text thành vectors"""
    return embedding_model.encode(texts).tolist()

def save_conversation(patient_id: str, text: str, suggestion: str):
    """Lưu cuộc hội thoại và gợi ý chữa trị vào vector DB"""
    chunks = [text, suggestion]
    embeddings = embed_texts(chunks)
    ids = [f"{patient_id}_conv_{i}" for i in range(len(chunks))]
    
    collection.add(
        ids=ids,
        documents=chunks,
        metadatas=[{"patient_id": patient_id, "type": t} for t in ["conversation", "treatment"]],
        embeddings=embeddings
    )
    return {"status": "ok", "patient_id": patient_id, "saved_items": len(chunks)}

def retrieve_context(patient_id: str, query: str, top_k: int = 3):
    """Truy xuất context liên quan từ vector DB"""
    try:
        query_emb = embed_texts([query])[0]
        results = collection.query(
            query_embeddings=[query_emb],
            n_results=top_k,
            where={"patient_id": patient_id}
        )
        context = "\n".join(results["documents"][0]) if results["documents"] else ""
        return context
    except Exception as e:
        return f"Lỗi khi truy xuất context: {str(e)}"

def generate_treatment_advice(conversation_text: str) -> str:
    """Sinh gợi ý chữa trị bằng Gemini"""
    prompt = f"""
Bạn là một bác sĩ chuyên nghiệp. Dựa trên cuộc hội thoại giữa bác sĩ và bệnh nhân dưới đây,
hãy đề xuất hướng điều trị rõ ràng, an toàn (không chứa nội dung nguy hiểm hoặc phi pháp).

Cuộc hội thoại:
{conversation_text}

Hãy tóm tắt tình trạng bệnh và đưa ra khuyến nghị điều trị tiếp theo.
"""
    try:
        model = genai.GenerativeModel(settings.model_name)
        response = model.generate_content(prompt)
        return response.text or "⚠️ Không có phản hồi từ AI."
    except Exception as e:
        return f"⚠️ Lỗi khi tạo gợi ý: {str(e)}"
