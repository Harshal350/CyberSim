from fastapi import FastAPI, WebSocket, WebSocketDisconnect
from fastapi.middleware.cors import CORSMiddleware
import asyncio
import json
import random

app = FastAPI(title="CyberSim Backend")

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

class ConnectionManager:
    def __init__(self):
        self.active_connections: list[WebSocket] = []

    async def connect(self, websocket: WebSocket):
        await websocket.accept()
        self.active_connections.append(websocket)

    def disconnect(self, websocket: WebSocket):
        self.active_connections.remove(websocket)

    async def broadcast(self, message: str):
        for connection in self.active_connections:
            await connection.send_text(message)

manager = ConnectionManager()

@app.get("/")
def read_root():
    return {"status": "CyberSim Backend Running"}

@app.websocket("/ws/dashboard")
async def websocket_dashboard(websocket: WebSocket):
    await manager.connect(websocket)
    try:
        while True:
            # Simulate real-time cyber data
            data = {
                "active_connections": random.randint(1000, 5000),
                "packets_sniffed": round(random.uniform(10.0, 100.0), 1),
                "threat_level": random.choice(["LOW", "MEDIUM", "HIGH", "CRITICAL"])
            }
            await websocket.send_text(json.dumps(data))
            await asyncio.sleep(2) # Send update every 2 seconds
    except WebSocketDisconnect:
        manager.disconnect(websocket)
