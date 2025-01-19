FROM python:3.10-slim
LABEL authors="Eric"

WORKDIR /app
COPY requirements.txt .

# Install Python dependencies
RUN pip install --no-cache-dir -r requirements.txt

COPY . .

# Expose the gRPC port
EXPOSE 50051

# Run the application
CMD ["python", "main.py"]