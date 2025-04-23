document.addEventListener("DOMContentLoaded", () => {
    const chatContainer = document.getElementById("chatContainer");
    const messageForm = document.getElementById("messageForm");
    const messageInput = document.getElementById("messageInput");
    const fileForm = document.getElementById("fileForm");
    const fileInput = document.getElementById("fileInput");

    const senderId = localStorage.getItem("sender_id"); // Assume sender ID is stored in localStorage
    const recipientId = localStorage.getItem("recipient_id"); // Assume recipient ID is stored in localStorage

    // Fetch and display messages
    async function fetchMessages() {
        try {
            const response = await fetch("/backend/chat/fetch_messages.php", {
                method: "GET",
            });
            const messages = await response.json();

            if (response.ok) {
                displayMessages(messages);
            } else {
                console.error("Failed to fetch messages:", messages.message);
            }
        } catch (error) {
            console.error("Error fetching messages:", error);
        }
    }

    // Display messages in the chat container
    function displayMessages(messages) {
        chatContainer.innerHTML = ""; // Clear existing messages
        messages.forEach((message) => {
            const messageElement = document.createElement("div");
            messageElement.classList.add("message");
            messageElement.innerHTML = `
                <p><strong>${message.sender}</strong> <span class="timestamp">${message.timestamp}</span></p>
                <p>${message.content}</p>
            `;
            chatContainer.appendChild(messageElement);
        });
        chatContainer.scrollTop = chatContainer.scrollHeight; // Scroll to bottom
    }

    // Handle message form submission
    messageForm.addEventListener("submit", async (e) => {
        e.preventDefault();
        const messageText = messageInput.value.trim();

        if (!messageText) {
            alert("Message cannot be empty.");
            return;
        }

        try {
            const response = await fetch("/backend/chat/send_message.php", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    sender_id: senderId,
                    recipient_id: recipientId,
                    message_text: messageText,
                }),
            });

            const result = await response.json();
            if (response.ok) {
                messageInput.value = ""; // Clear input field
                fetchMessages(); // Refresh messages
            } else {
                alert(result.message || "Failed to send message.");
            }
        } catch (error) {
            console.error("Error sending message:", error);
            alert("An error occurred. Please try again.");
        }
    });

    // Handle file upload form submission
    fileForm.addEventListener("submit", async (e) => {
        e.preventDefault();
        const file = fileInput.files[0];

        if (!file) {
            alert("Please select a file to upload.");
            return;
        }

        const allowedExtensions = [".pdf", ".jpg", ".jpeg", ".png", ".doc", ".docx"];
        const fileSizeLimit = 50 * 1024 * 1024; // 50MB
        const fileExtension = file.name.substring(file.name.lastIndexOf(".")).toLowerCase();

        if (!allowedExtensions.includes(fileExtension)) {
            alert("Invalid file type. Allowed types: " + allowedExtensions.join(", "));
            return;
        }

        if (file.size > fileSizeLimit) {
            alert("File size exceeds the 50MB limit.");
            return;
        }

        const formData = new FormData();
        formData.append("file", file);
        formData.append("sender_id", senderId);
        formData.append("recipient_id", recipientId);

        try {
            const response = await fetch("/backend/chat/upload_file.php", {
                method: "POST",
                body: formData,
            });

            const result = await response.json();
            if (response.ok) {
                alert("File uploaded successfully!");
                fileInput.value = ""; // Clear file input
                fetchMessages(); // Refresh messages
            } else {
                alert(result.message || "Failed to upload file.");
            }
        } catch (error) {
            console.error("Error uploading file:", error);
            alert("An error occurred. Please try again.");
        }
    });

    // Fetch messages every 3 seconds
    setInterval(fetchMessages, 3000);

    // Initial fetch of messages
    fetchMessages();
});