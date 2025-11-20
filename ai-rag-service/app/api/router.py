from fastapi import APIRouter
from app.api import patient_routes, ask_routes

api_router = APIRouter()

api_router.include_router(patient_routes.router, prefix="/patient", tags=["patient"])
api_router.include_router(ask_routes.router, prefix="/ask", tags=["ask"])
