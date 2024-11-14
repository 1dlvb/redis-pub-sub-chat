let ws;

function connect() {
    const userId = document.getElementById("userId").value;
    ws = new WebSocket(`ws://localhost:8080/chat/${userId}`);

    ws.onmessage = function (message) {
        displayMessage(message.data, "received");
    };

    ws.onopen = function () {
        document.getElementById("status").textContent = "Connected";
    };

    ws.onclose = function () {
        document.getElementById("status").textContent = "Disconnected";
    };
}

function sendMessage() {
    const targetUserId = document.getElementById("targetUserId").value;
    const message = document.getElementById("message").value;
    ws.send(`${targetUserId} -> ${message}`);
    displayMessage(`Sent to ${targetUserId}: ${message}`, "sent");
    document.getElementById("message").value = "";
}

function displayMessage(text, type) {
    const chatBox = document.getElementById("chatBox");
    const messageElement = document.createElement("div");
    messageElement.classList.add("chat-message", type === "received" ? "received" : "sent");

    const colonIndex = text.indexOf(":");
    if (colonIndex !== -1 && text[colonIndex + 1] !== " ") {
        text = text.slice(0, colonIndex + 1) + " " + text.slice(colonIndex + 1);
    }

    messageElement.textContent = text;
    chatBox.appendChild(messageElement);
    chatBox.scrollTop = chatBox.scrollHeight;
}

function disconnect() {
    if (ws) {
        ws.close();
        document.getElementById("status").textContent = "Disconnected";
    }
}
