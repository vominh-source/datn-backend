from app.dependencies import patient_collection

def delete_by_patient(patient_id: str):
    patient_collection.delete(where={"patient_id": patient_id})

def add_patient_vector(patient_id: str, text: str, emb: list):
    patient_collection.add(
        documents=[text],
        embeddings=[emb],
        ids=[f"patient-{patient_id}"],
        metadatas=[{"patient_id": patient_id}]
    )

def query_patient(patient_id: str, query_emb: list):
    return patient_collection.query(
        query_embeddings=[query_emb],
        n_results=1,
        where={"patient_id": patient_id}
    )
