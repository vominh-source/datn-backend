from fastapi import FastAPI
from app.api.router import api_router

app = FastAPI(title="AI Medical RAG Service")

app.include_router(api_router)

@app.get("/")
def root():
    return {"service": "AI Medical RAG", "status": "running"}
