import google.generativeai as genai
from app.config import settings

genai.configure(api_key=settings.gemini_key)

def ask_gemini(context: str, question: str) -> str:
    """Hỏi Gemini với context từ vector DB"""
    prompt = f"""
Dựa trên hồ sơ bệnh nhân sau:

{context}

Câu hỏi của bác sĩ: {question}

Hãy trả lời chính xác, an toàn, rõ ràng.
"""
    try:
        model = genai.GenerativeModel(settings.model_name)
        response = model.generate_content(prompt)
        return response.text or "⚠️ Không có phản hồi."
    except Exception as e:
        return f"⚠️ Lỗi: {str(e)}"

