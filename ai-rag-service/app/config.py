from pydantic_settings import BaseSettings
import os

class Settings(BaseSettings):
    chroma_dir: str = os.getenv("CHROMA_DIR", "./vectordb")
    gemini_key: str = os.getenv("GEMINI_API_KEY", "")
    model_name: str = "gemini-1.5-flash"
    embedding_model: str = "all-MiniLM-L6-v2"

    class Config:
        env_file = ".env"
        extra = "allow"

settings = Settings()
