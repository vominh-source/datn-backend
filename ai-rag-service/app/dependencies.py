from chromadb import Client
from chromadb.config import Settings
from sentence_transformers import SentenceTransformer
from app.config import settings

# Vector DB
chroma_client = Client(Settings(
    chroma_db_impl="duckdb+parquet",
    persist_directory=settings.chroma_dir
))

patient_collection = chroma_client.get_or_create_collection("patients")

# Embedding model
embedding_model = SentenceTransformer("all-MiniLM-L6-v2")
