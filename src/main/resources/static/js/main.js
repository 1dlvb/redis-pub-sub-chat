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

    if (typeof text === "string") {
        const messages = text.split("\n");

        messages.forEach(message => {
            if (message.trim() !== "") {
                const messageElement = document.createElement("div");
                const userId = document.getElementById("userId").value;

                if (type === "sent" || message.startsWith(userId + ": ") || message.startsWith("Sent to ")) {
                    messageElement.classList.add("chat-message", "sent");
                } else {
                    messageElement.classList.add("chat-message", "received");
                }

                const colonIndex = message.indexOf(":");
                if (colonIndex !== -1 && message[colonIndex + 1] !== " ") {
                    message = message.slice(0, colonIndex + 1) + " " + message.slice(colonIndex + 1);
                }

                messageElement.textContent = message.trim();
                chatBox.appendChild(messageElement);
            }
        });

        chatBox.scrollTop = chatBox.scrollHeight;
    } else {
        console.error("Received data is not a string:", text);
    }
}



function disconnect() {
    if (ws) {
        ws.close();
        document.getElementById("status").textContent = "Disconnected";
    }
}
