from pydantic import BaseSettings

class Settings(BaseSettings):
    chroma_dir: str = "./vectordb"
    gemini_key: str = "YOUR_GEMINI_API_KEY"

settings = Settings()
