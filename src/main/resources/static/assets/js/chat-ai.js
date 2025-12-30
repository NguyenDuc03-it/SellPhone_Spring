const chatToggle = document.getElementById("chatToggle");
const chatWindow = document.getElementById("chatWindow");
const closeChat = document.getElementById("closeChat");
const chatBody = document.getElementById("chatBody");
const chatInput = document.getElementById("chatInput");
const sendBtn = document.getElementById("sendBtn");

let chatHistory = JSON.parse(sessionStorage.getItem("chatHistory") || "[]");
if(chatHistory.length === 0) {
    const welcomeMsg = "Xin chào 👋<br>Tôi là Hiki, chatbot AI của cửa hàng TĐ Mobile.<br> Tôi có thể cung cấp cho bạn thông tin về sản phẩm của cửa hàng.<br> Cứ hỏi tôi nếu cần giúp đỡ. Tôi sẵn sàng trả lời các câu hỏi của bạn 😊";
    appendMessage("bot", welcomeMsg);
}

function renderChatHistory() {
    chatBody.innerHTML = ""; // Xóa sạch chat cũ
    chatHistory.forEach(msg => {
        const div = document.createElement("div");
        div.className = "chat-message " + (msg.role === "user" ? "user" : "bot");

        // Nếu content có ảnh (markdown chuyển thành HTML), thêm class chat-image cho ảnh
        let html = marked.parse(msg.content);
        html = html.replace(/<img /g, '<img class="chat-image" ');
        if(msg.role === "user"){
            div.innerHTML = `<div class="chat-bubble">${html}</div>`;
        }
        else div.innerHTML = `<div class="chat-bubble bg-white">${html}</div>`;
        chatBody.appendChild(div);
    });
    chatBody.scrollTop = chatBody.scrollHeight;
}

renderChatHistory();

// Mở chat (hiệu ứng bật ra)
chatToggle.addEventListener("click", () => {
    chatWindow.classList.add("active");
    chatToggle.style.opacity = "0";
    chatToggle.style.pointerEvents = "none";
});

// Đóng chat (thu về)
closeChat.addEventListener("click", () => {
    chatWindow.classList.remove("active");
    chatToggle.style.opacity = "1";
    chatToggle.style.pointerEvents = "auto";
});

// Gửi tin nhắn
sendBtn.addEventListener("click", sendMessage);
chatInput.addEventListener("keypress", (e) => {
    if (e.key === "Enter") sendMessage();
});

function saveChatHistory() {
    sessionStorage.setItem("chatHistory", JSON.stringify(chatHistory));
}

function appendMessage(role, content) {
    chatHistory.push({ role, content });
    if (chatHistory.length > 80) chatHistory.shift(); // chỉ giữ 100 message gần nhất
    saveChatHistory();
}

function sendMessage() {
    const text = chatInput.value.trim();
    if (!text) return;

    // Tạo session chat
    let sessionId = sessionStorage.getItem("chatSessionId");
    if (!sessionId) {
        sessionId = crypto.randomUUID();
        sessionStorage.setItem("chatSessionId", sessionId);
    }

    // Thêm tin nhắn user vào chatHistory và UI
    appendMessage("user", text);
    const userMsg = document.createElement("div");
    userMsg.className = "chat-message user";
    userMsg.innerHTML = `<div class="chat-bubble">${text}</div>`;
    chatBody.appendChild(userMsg);
    chatInput.value = "";

    chatBody.scrollTop = chatBody.scrollHeight;

    // Hiển thị hiệu ứng "bot đang nhập..."
    const typing = document.createElement("div");
    typing.className = "chat-message bot";
    typing.innerHTML = `
                <div class="typing-indicator d-flex align-items-center bg-white justify-content-between">
                <span></span><span></span><span></span>
                </div>`;
    chatBody.appendChild(typing);
    chatBody.scrollTop = chatBody.scrollHeight;

    // Giả lập bot phản hồi
    // setTimeout(() => {
    //     const botMsg = document.createElement("div");
    //     botMsg.className = "chat-message bot";
    //     botMsg.innerHTML = `<div class="chat-bubble">Bạn vừa nói: "${text}" 😊</div>`;
    //     typing.remove();
    //     chatBody.appendChild(botMsg);
    //     chatBody.scrollTop = chatBody.scrollHeight;
    // }, 700);

    fetch("/chat", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            question: text,
            sessionId: sessionId,
        })
    })
    .then(response => {
        if (!response.ok) {
            const botMsg = document.createElement("div");
            botMsg.className = "chat-message bot";
            botMsg.innerHTML = `<div class="chat-bubble">Lỗi! Máy chủ không phản hồi.</div>`;
            typing.remove();
            chatBody.appendChild(botMsg);
            chatBody.scrollTop = chatBody.scrollHeight;
            appendMessage("bot", "Lỗi! Máy chủ không phản hồi.");
        }
        return response.json();
    })
    .then(data => {
        const botMsg = document.createElement("div");
        botMsg.className = "chat-message bot";
        let html = marked.parse(data.answer); // chuyển markdown → html
        html = html.replace(/<img /g, '<img class="chat-image" ');
        botMsg.innerHTML = `<div class="chat-bubble bg-white">${html}</div>`;
        typing.remove();
        chatBody.appendChild(botMsg);
        chatBody.scrollTop = chatBody.scrollHeight;
        appendMessage("bot", data.answer);
    })
    .catch(error => {
        const botMsg = document.createElement("div");
        botMsg.className = "chat-message bot";
        botMsg.innerHTML = `<div class="chat-bubble">Lỗi! Máy chủ không phản hồi. (${error})</div>`;
        typing.remove();
        chatBody.appendChild(botMsg);
        chatBody.scrollTop = chatBody.scrollHeight;
        appendMessage("bot", "Lỗi! Máy chủ không phản hồi. (" + error + ")");
    });
}

// Phóng to ảnh trong chat box start
document.addEventListener("click", function(e) {
    if (e.target.classList.contains("chat-image")) {
        const modal = document.getElementById("imageModal");
        const img = document.getElementById("imageModalContent");
        modal.style.display = "flex";
        img.src = e.target.src;
    }
});

// Đóng modal phóng to ảnh
document.getElementById("imageModalClose").onclick = () => {
    document.getElementById("imageModal").style.display = "none";
};
// Phóng to ảnh trong chat box end