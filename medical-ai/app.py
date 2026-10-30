
from fastapi import FastAPI, UploadFile, File, HTTPException
from fastapi.responses import JSONResponse
from PIL import Image, UnidentifiedImageError
import easyocr
import io
import numpy as np

app = FastAPI(title="ELMS Medical Certificate Screening API")

# Load the pretrained OCR model when the service starts.
reader = easyocr.Reader(["en"], gpu=False)


@app.get("/health")
def health():
    return {
        "status": "running",
        "service": "ELMS Medical Certificate Screening"
    }


@app.post("/screen")
async def screen_certificate(file: UploadFile = File(...)):
    allowed_types = {"image/jpeg", "image/png", "image/webp"}

    if file.content_type not in allowed_types:
        raise HTTPException(
            status_code=400,
            detail="Upload a JPG, PNG, or WEBP image."
        )

    image_bytes = await file.read()

    if not image_bytes:
        raise HTTPException(status_code=400, detail="The uploaded file is empty.")

    if len(image_bytes) > 8 * 1024 * 1024:
        raise HTTPException(
            status_code=413,
            detail="The image must be 8 MB or smaller."
        )

    try:
        image = Image.open(io.BytesIO(image_bytes)).convert("RGB")
    except (UnidentifiedImageError, OSError):
        raise HTTPException(
            status_code=400,
            detail="The uploaded file is not a valid supported image."
        )

    # Deep-learning OCR extracts visible text from the image.
    image_array = np.array(image)
    extracted_lines = reader.readtext(
        image_array,
        detail=0,
        paragraph=False
    )
    extracted_text = "\n".join(extracted_lines)

    # Basic screening checks: these do not establish authenticity.
    text_lower = extracted_text.lower()
    checks = {
        "doctor_or_hospital_reference": any(
            term in text_lower
            for term in ["doctor", "dr.", "hospital", "clinic"]
        ),
        "date_reference": any(
            term in text_lower
            for term in ["date", "dated", "/", "-", "2025", "2026"]
        ),
        "medical_reference": any(
            term in text_lower
            for term in [
                "medical", "patient", "diagnosis", "rest",
                "unfit", "fit", "treatment", "sick"
            ]
        ),
    }

    missing_checks = [
        name for name, passed in checks.items() if not passed
    ]

    return JSONResponse({
        "filename": file.filename,
        "extracted_text": extracted_text,
        "checks": checks,
        "manual_review_recommended": True,
        "screening_message": (
            "Text extracted. Review the certificate manually."
            if not missing_checks
            else "Some expected information was not detected. "
                 "Please review the certificate manually."
        ),
        "checks_not_detected": missing_checks,
        "authenticity_verified": False
    })
