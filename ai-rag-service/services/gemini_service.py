from google import genai
from app.config import settings

client = genai.Client(api_key=settings.gemini_key)

def ask_gemini(context: str, question: str) -> str:
    prompt = f"""
Dưới đây là toàn bộ hồ sơ bệnh nhân:

{context}

Câu hỏi của bác sĩ: {question}

Hãy trả lời chính xác, an toàn, rõ ràng.
"""

    res = client.models.generate_content(
        model="gemini-2.0-flash",
        prompt=prompt
    )

    return res.text
