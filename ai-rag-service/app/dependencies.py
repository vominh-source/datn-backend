import chromadb
from sentence_transformers import SentenceTransformer
import os

# Embedding model
embedding_model = SentenceTransformer("all-MiniLM-L6-v2")

# Vector DB - ChromaDB PersistentClient
PERSIST_PATH = os.getenv("CHROMA_DIR", "./vectordb")
os.makedirs(PERSIST_PATH, exist_ok=True)

chroma_client = chromadb.PersistentClient(path=PERSIST_PATH)
collection = chroma_client.get_or_create_collection(name="medical_chat_memory")
