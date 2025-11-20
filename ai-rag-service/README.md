# 🧠 AI Medical RAG Service - Doctor Assistant

Ứng dụng RAG (Retrieval-Augmented Generation) sử dụng ChromaDB, Sentence Transformers, và Google Gemini API để tạo một trợ lý bác sĩ thông minh có khả năng nhớ lịch sử hội thoại.

## 🚀 Quick Start

### Yêu cầu

- Python 3.9+
- Google Gemini API Key (lấy từ [Google AI Studio](https://aistudio.google.com/app/apikey))

### 1️⃣ Setup Environment

```bash
# Tạo virtual environment
python -m venv venv

# Kích hoạt (Windows)
venv\Scripts\activate

# Hoặc kích hoạt (Linux/Mac)
source venv/bin/activate
```

### 2️⃣ Cài đặt Dependencies

```bash
cd c:\datn-backend\ai-rag-service
pip install -r requirements.txt
```

### 3️⃣ Cấu hình API Key

Sửa file `.env` và thay thế bằng API key của bạn:

```env
GEMINI_API_KEY=your_actual_api_key_here
CHROMA_DIR=./vectordb
```

### 4️⃣ Chạy Server

```bash
python run.py
```

Hoặc chạy trực tiếp với uvicorn:

```bash
python -m uvicorn app.main:app --host 0.0.0.0 --port 8000
```

Server sẽ chạy trên: **`http://localhost:8000`**

---

## 📚 API Documentation

Sau khi server chạy, bạn có thể truy cập:

- **Swagger UI**: http://localhost:8000/docs
- **ReDoc**: http://localhost:8000/redoc

---

## 🦾 Test API bằng Postman

### Endpoint 1: Health Check

```
GET http://localhost:8000/
```

**Response:**

```json
{
  "service": "AI Medical RAG Service",
  "status": "running",
  "version": "1.0.0",
  "docs": "/docs",
  "redoc": "/redoc"
}
```

### Endpoint 2: Sinh Gợi ý Điều Trị

```
POST http://localhost:8000/ask/treatment-advice
```

**Headers:**

```
Content-Type: application/json
```

**Body (raw JSON):**

```json
{
  "patient_id": "patient_123",
  "conversation_text": "Patient: Doctor, I've been having headaches and blurry vision for the past 2 weeks.\nDoctor: How often do you experience these headaches?\nPatient: Almost every day, especially in the morning."
}
```

**Response:**

```json
{
  "patient_id": "patient_123",
  "suggestion": "Based on the patient's symptoms of headaches and blurry vision...",
  "saved": {
    "status": "ok",
    "patient_id": "patient_123",
    "saved_items": 2
  }
}
```

### Endpoint 3: Truy Xuất Context Lịch Sử

```
POST http://localhost:8000/ask/retrieve-context
```

**Body (raw JSON):**

```json
{
  "patient_id": "patient_123",
  "query": "headaches and blood pressure",
  "top_k": 3
}
```

**Response:**

```json
{
  "patient_id": "patient_123",
  "query": "headaches and blood pressure",
  "context": "Doctor, I've been having headaches...\nI was diagnosed with hypertension last year..."
}
```

### Endpoint 4: Hỏi Câu Hỏi Với Context

```
POST http://localhost:8000/ask/ask
```

**Body (raw JSON):**

```json
{
  "patient_id": "patient_123",
  "question": "What treatment do you recommend for this patient?"
}
```

**Response:**

```json
{
  "patient_id": "patient_123",
  "question": "What treatment do you recommend for this patient?",
  "context_retrieved": true,
  "answer": "Based on the patient's history and symptoms..."
}
```

### Endpoint 5: Kiểm Tra Trạng Thái Bệnh Nhân

```
GET http://localhost:8000/patient/status
```

**Response:**

```json
{
  "status": "ok",
  "service": "Patient Management",
  "version": "1.0.0"
}
```

---

## 📋 Hướng Dẫn Postman Chi Tiết

### Bước 1: Import Collection

1. Mở Postman
2. Click **"File" → "New" → "Collection"**
3. Đặt tên: `RAG Doctor Assistant`

### Bước 2: Thêm Request

1. Click **"+" → "Add request"**
2. Chọn method (GET/POST)
3. Nhập URL endpoint
4. Thêm headers (nếu cần): `Content-Type: application/json`
5. Thêm body (nếu POST): chọn **raw → JSON**
6. Click **Send**

### Bước 3: Xem Response

Kết quả sẽ hiển thị dưới tab **Body**

---

## 🏗️ Cấu Trúc Dự Án

```
ai-rag-service/
├── app/
│   ├── main.py              # FastAPI entry point + CORS config
│   ├── config.py            # Configuration (API keys, paths)
│   ├── dependencies.py      # Shared dependencies (ChromaDB, Embedding)
│   ├── api/
│   │   ├── router.py        # Main router
│   │   ├── ask_routes.py    # RAG & question endpoints
│   │   └── patient_routes.py# Patient management
│   ├── models/
│   │   ├── request_models.py# Pydantic request schemas
│   │   └── patient_models.py# Patient data models
│   └── services/
│       ├── rag_service.py   # RAG logic (embed, retrieve, save)
│       └── gemini_service.py# Gemini API calls
├── vectordb/                # ChromaDB persistent storage
├── .env                     # Environment variables (API keys)
├── requirements.txt         # Python dependencies
├── run.py                   # Entry point script
└── README.md               # This file
```

---

## ⚙️ Cấu Hình

### File `.env`

```env
# Google Gemini API Key (bắt buộc)
GEMINI_API_KEY=your_api_key_here

# ChromaDB vector store location
CHROMA_DIR=./vectordb
```

---

## 🔧 Troubleshooting

### ❌ "ModuleNotFoundError: No module named 'chromadb'"

```bash
# Đảm bảo virtual environment được kích hoạt
venv\Scripts\activate

# Cài lại dependencies
pip install -r requirements.txt
```

### ❌ "CORS error" khi gọi từ Frontend

CORS đã được enable trong `main.py`. Nếu vẫn có lỗi:

- Kiểm tra server có đang chạy không
- Kiểm tra URL có đúng không
- Kiểm tra request headers

### ❌ "GEMINI_API_KEY is empty"

1. Lấy API key từ [Google AI Studio](https://aistudio.google.com/app/apikey)
2. Cập nhật file `.env`
3. Restart server

### ❌ "Connection refused to localhost:8000"

```bash
# Chắc chắn server đang chạy
python run.py
```

---

## 💡 Ví Dụ Sử Dụng

### Flow 1: Lần Khám Đầu Tiên

1. Bác sĩ nhập thông tin bệnh nhân qua `/ask/treatment-advice`
2. Gemini sinh gợi ý điều trị
3. Dữ liệu được lưu vào ChromaDB

### Flow 2: Lần Khám Thứ 2 (Bệnh nhân cũ)

1. Truy xuất lịch sử qua `/ask/retrieve-context`
2. Hỏi câu hỏi mới qua `/ask/ask`
3. Gemini sử dụng lịch sử + câu hỏi để trả lời

---

## 📦 Dependencies

- **fastapi**: Web framework
- **uvicorn**: ASGI server
- **chromadb**: Vector database
- **sentence-transformers**: Text embedding
- **google-generativeai**: Gemini API
- **pydantic**: Data validation
- **python-dotenv**: Environment variables

---
